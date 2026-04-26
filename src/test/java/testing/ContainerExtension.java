package testing;

import java.nio.file.Paths;
import java.time.Duration;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

//Extensión de JUnit 5 para manejar contenedores Docker.
//por el momento solo se usara PostgreSQL (pruebas de integración con BD) despues OpenLiberty (pruebas E2E)

public class ContainerExtension implements BeforeAllCallback, AfterAllCallback {

    // Lock para evitar que múltiples hilos arranquen contenedores al mismo tiempo
    private static final Object lock = new Object();

    // Control de estado de los contenedores
    private static boolean postgresStart = false;
    //private static boolean libertyStart = false; // (para las otras pruebas)

    // Contador de clases de prueba
    private static int numClassTest = 0;

    // Indica si el contenedor de OpenLiberty ya fue iniciado
    private static boolean libertyStart = false;

    // Red de Docker para comunicación entre contenedores
    protected static final Network red = Network.newNetwork();

    //CONTENEDOR DE POSTGRES
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5-alpine")
            .withDatabaseName("ingreso_ues_db")
            .withPassword("abc123")
            .withUsername("postgres")
            .withExposedPorts(5432)
            .withNetwork(red)
            .withNetworkAliases("db");

    /**
     * Contenedor de OpenLiberty usando la imagen local {@code liberty:latest}.
     *
     * <p><b>Configuración:</b></p>
     * <ul>
     *   <li>Imagen: liberty:latest (descargada localmente)</li>
     *   <li>Puerto expuesto: 9080</li>
     *   <li>Archivos copiados: WAR del proyecto, server.xml, driver PostgreSQL</li>
     *   <li>Variables de entorno para conexión a PostgreSQL</li>
     *   <li>Dependencia: PostgreSQL (espera a que esté listo)</li>
     *   <li>Tiempo de espera: 3 minutos máximo</li>
     * </ul>
     */

    protected static final GenericContainer<?> openliberty = new GenericContainer<>("liberty:latest")
            .withExposedPorts(9080)
            //war del proyecto
            .withCopyFileToContainer(getWarFile(), "/config/apps/DAR_TPI135_IngresoUES-1.0-SNAPSHOT.war")
            .withCopyFileToContainer(
                    MountableFile.forHostPath(Paths.get("src/test/resources/liberty/server.xml").toAbsolutePath()),
                    "/config/server.xml")
            .withCopyFileToContainer(
                    MountableFile.forHostPath(Paths.get("src/test/resources/liberty/postgresql-42.7.7.jar").toAbsolutePath()),
                    "/config/lib/postgresql-42.7.7.jar")
            .withNetwork(red)
            //variables de entorno
            .withEnv("PGHOST", "db")
            .withEnv("PGPORT", "5432")
            .withEnv("PGDBNAME", "ingreso_ues_db")
            .withEnv("PGUSER", "postgres")
            .withEnv("PGPASSWORD", "abc123")
            //orden de arranque
            .dependsOn(postgres)
            .waitingFor(Wait.forLogMessage(".*CWWKF0011I.*", 1)
                    .withStartupTimeout(Duration.ofMinutes(3)));

     // CONFIGURACIÓN DE ENTORNO

    //CONFIGURACIONES
    // Configuración para pruebas E2E
    public static void configurarParaE2E(){
        postgres = postgres.withInitScript("ingreso_ues_db.sql");
    }


    // Configuración para pruebas de integración
    public static void configurarParaIT(){
        postgres = postgres.withInitScript("ingreso_ues_db.sql");
    }

    // METODOS DE CICLO DE VIDA DE JUNIT

    /**
     * Se ejecuta antes de cada clase de prueba.
     *
     * <p><b>Comportamiento:</b></p>
     * <ol>
     *   <li>Detecta si la clase está anotada con {@code @SystemTest}</li>
     *   <li>Configura el entorno según el tipo de prueba (E2E o IT)</li>
     *   <li>Inicia PostgreSQL una sola vez (compartido entre todas las pruebas)</li>
     *   <li>Inicia OpenLiberty solo si es prueba de sistema y aún no fue levantado</li>
     *   <li>Registra un shutdown hook para detener los contenedores al finalizar</li>
     * </ol>
     *
     * @param context contexto de la extensión provisto por JUnit 5
     * @throws Exception si ocurre un error al iniciar los contenedores
     */
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        synchronized (lock) {

            // 1. DETECTAR TIPO DE PRUEBA

            boolean isSystemTest = context.getTestClass()
                    // Obtiene la clase de prueba y verifica si tiene la anotación @SystemTest
                    .map(cls -> cls.isAnnotationPresent(SystemTest.class))
                    // Si no hay clase, por defecto asume que NO es SystemTest
                    .orElse(false);

            // 2. CONFIGURAR ENTORNO

            if (isSystemTest) {
                // Configuración para pruebas de sistema (E2E): Levanta OpenLiberty, usa base de datos preparada para E2E
                configurarParaE2E();
            } else {
                // Configuración para pruebas de integración, solo usa base de datos, No levanta el servidor
                configurarParaIT();
            }
            numClassTest++;

            // 3. INICIAR POSTGRESQL (UNA SOLA VEZ)

            if (!postgresStart) {
                postgres.start();
                postgresStart = true;
            }

            // 4. INICIAR OPENLIBERTY (SOLO PARA PRUEBAS DE SISTEMA)


            if (isSystemTest && !libertyStart) {
                // Arranca el contenedor de OpenLiberty
                openliberty.start();

                System.out.println("=== LIBERTY LOGS ===");
                System.out.println(openliberty.getLogs());

                // Marca que ya fue iniciado para no volver a levantarlo en otras clases
                libertyStart = true;
            }

            // 5. REGISTRAR SHUTDOWN HOOK (UNA SOLA VEZ)

            if (numClassTest == 1) {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    synchronized (lock) {
                        // Si OpenLiberty fue iniciado, lo detenemos
                        if (libertyStart) {
                            openliberty.stop();
                            libertyStart = false;
                        }
                        // Si PostgreSQL fue iniciado, lo detenemos
                        if (postgresStart) {
                            postgres.stop();
                            postgresStart = false;
                        }
                    }
                }));
            }
        }
    }

    /**
     * Se ejecuta después de cada clase de prueba.
     *
     * <p>Decrementa el contador de clases activas.
     * Los contenedores no se detienen aquí porque se reutilizan entre clases de prueba.</p>
     *
     * @param context contexto de la extensión provisto por JUnit 5
     * @throws Exception si ocurre un error inesperado
     */
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        synchronized (ContainerExtension.class) {
            numClassTest--;
        }
    }

    //Obtener PostgreSQL
    public static PostgreSQLContainer<?> getPostgres() {
        return postgres;
    }

    //Metodo para OpenLiberty
    public static GenericContainer<?> getOpenLiberty() {
        return openliberty;
    }

    //Obtener WAR
    private static MountableFile getWarFile() {
        return MountableFile.forHostPath(Paths.get("target/DAR_TPI135_IngresoUES-1.0-SNAPSHOT.war").toAbsolutePath());
    }
}