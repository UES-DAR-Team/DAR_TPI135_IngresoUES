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

    //Indica si es prueba de sistema (E2E)
    private static boolean SystemTest = false; // (no se usara en estas pruebas)

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


    // contenedor de openLiberty
    /**CAMBIO: se usa la imagen liberty:latest descargada localmente en lugar de construir
    desde Dockerfile con ImageFromDockerfile,se requiere ejecutar previamente
     */

    protected static final GenericContainer<?> openliberty = new GenericContainer<>("liberty:latest")
            //expone el puerto del contenedor donde corre el api rest
            .withExposedPorts(9080)
            //war del proyecto
            .withCopyFileToContainer(getWarFile(), "/config/apps/DAR_TPI135_IngresoUES-1.0-SNAPSHOT.war")

            /** CAMBIO: se copian manualmente el server.xml y el driver de PostgreSQL al contenedor
            porque la imagen liberty:latest no los incluye por defecto
            el server.xml configura el servidor y el driver permite la conexion a Postgres
             */

            .withCopyFileToContainer(
                    MountableFile.forHostPath(Paths.get("src/test/resources/liberty/server.xml").toAbsolutePath()),
                    "/config/server.xml")
            .withCopyFileToContainer(
                    MountableFile.forHostPath(Paths.get("src/test/resources/liberty/postgresql-42.7.7.jar").toAbsolutePath()),
                    "/config/lib/postgresql-42.7.7.jar")
            //conecta a la red docker
            .withNetwork(red)

            //variables de entorno
            .withEnv("PGHOST", "db")
            .withEnv("PGPORT", "5432")
            .withEnv("PGDBNAME", "ingreso_ues_db")
            .withEnv("PGUSER", "postgres")
            .withEnv("PGPASSWORD", "abc123")

            //orden de arranque
            .dependsOn(postgres)

            /** CAMBIO: se espera el mensaje exacto que imprime la imagen liberty:latest al arrancar
            el servidor se llama defaultServer en esta imagen, no app como en la imagen anterior
            */

            .waitingFor(Wait.forLogMessage(".*The defaultServer server is ready to run a smarter planet.*", 1)
                    .withStartupTimeout(Duration.ofMinutes(3)));


    //CONFIGURACIONES
    // Configuración para pruebas E2E
    public static void configurarParaE2E(){
        SystemTest = true;
        postgres = postgres.withInitScript("ingreso_ues_db.sql");
    }


    // Configuración para pruebas de integración
    public static void configurarParaIT(){
        SystemTest = false;
        postgres = postgres.withInitScript("ingreso_ues_db.sql");
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        synchronized (lock) {

            // Verifica si la clase de prueba actual está anotada con @SystemTest
            // Esto indica que la prueba es de tipo sistema (E2E)
            boolean isSystemTest = context.getTestClass()

                    // Obtiene la clase de prueba y verifica si tiene la anotación @SystemTest
                    .map(cls -> cls.isAnnotationPresent(SystemTest.class))

                    // Si no hay clase (caso raro), por defecto asume que NO es SystemTest
                    .orElse(false);

            // Según el tipo de prueba, configura el entorno adecuado
            if (isSystemTest) {
                // Configuración para pruebas de sistema (E2E):
                // Levanta OpenLiberty
                // Usa base de datos preparada para E2E
                configurarParaE2E();
            } else {
                // Configuración para pruebas de integración:
                // Solo usa base de datos
                // No levanta el servidor completo
                configurarParaIT();
            }

            numClassTest++;

            // Iniciar PostgreSQL una sola vez
            if (!postgresStart) {
                postgres.start();
                postgresStart = true;
            }

            // Iniciar OpenLiberty solo si la prueba lo requiere (es decir, si es @SystemTest)
            // y además evitar iniciarlo múltiples veces
            if (isSystemTest && !libertyStart) {
                // Arranca el contenedor de OpenLiberty
                openliberty.start();

                System.out.println("=== LIBERTY LOGS ===");
                System.out.println(openliberty.getLogs());

                // Marca que ya fue iniciado para no volver a levantarlo en otras clases
                libertyStart = true;
            }

            // Hook para cerrar los contenedores cuando termina toda la ejecución de pruebas
            // Se ejecuta una sola vez (cuando empieza la primera clase de test)
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

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        synchronized (ContainerExtension.class) {
            numClassTest--;

            //No detenemos el contenedor aqqui porque queremos reutilizarlo
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