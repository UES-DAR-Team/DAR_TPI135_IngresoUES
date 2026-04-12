package testing;

import java.nio.file.Paths;
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

    // Red de Docker para comunicación entre contenedores
    //  protected static final Network red = Network.newNetwork();

    //CONTENEDOR DE POSTGRES
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5-alpine")
            .withDatabaseName("ingreso_ues_db")
            .withPassword("postgresmy")
            .withUsername("postgres")
            .withExposedPorts(5432)
            // .withNetwork(red)
            .withNetworkAliases("db");


    // contenedor de openLiberty
    /*
    protected static final GenericContainer<?> openliberty = new GenericContainer<>("openliberty:latest")
            .withExposedPorts(9080)
            .withCopyFileToContainer(getWarFile(), "RUTA-1.0-SNAPSHOT.war")
            .withNetwork(red)
            .withEnv("PGPASSWORD", "abc123")
            .withEnv("PGUSER", "postgres")
            .withEnv("PGDBNAME", "ingreso_ues_db")
            .withEnv("PGPORT", "5432")
            .withEnv("PGHOST", "db")
            .dependsOn(postgres)
            .waitingFor(Wait.forLogMessage(".*The app server is ready to run a smarter planet.*", 1));
    */


    //CONFIGURACIONES

    /*
    // Configuración para pruebas E2E
    public static void configurarParaE2E(){
        SystemTest = true;
        postgres = postgres.withInitScript("ingreso_ues_db_E2E.sql");
    }
    */

    // Configuración para pruebas de integración
    public static void configurarParaIT(){
        SystemTest = false;
        postgres = postgres.withInitScript("ingreso_ues_db.sql");
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        synchronized (lock) {
            // Configuramos el script ANTES de iniciar el contenedor
            if (!postgresStart) {
                configurarParaIT(); // bd real
            }
            /*
            // Para diferenciar E2E vs IT
            if (context.getTestClass().isPresent()) {
                Class<?> testClass = context.getTestClass().get();
                if (testClass.getName().contains("E2E") || testClass.getName().contains("SystemTest")) {
                    configurarParaE2E();
                } else {
                    configurarParaIT();
                }
            }
            */

            //Detectar si necesita OpenLiberty
            /*
            boolean needLiberty = context.getTestClass()
                    .map(cls -> cls.isAnnotationPresent(NeedsLiberty.class))
                    .orElse(false);
            */
            numClassTest++;
            // Iniciar PostgreSQL una sola vez
            if (!postgresStart) {
                postgres.start();
                postgresStart = true;
            }
            /*
            // Iniciar OpenLiberty solo si se necesita
            if(needLiberty && !libertyStart){
                openliberty.start();
                libertyStart = true;
            }
            */

            //Hook para cerrar contenedores al finalizar la ejecución
            if (numClassTest == 1) {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    synchronized (lock) {
                        /*
                        if(libertyStart){
                            openliberty.stop();
                            libertyStart = false;
                        }
                        */
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

    /*
    //Metodo para OpenLiberty
    public static GenericContainer<?> getOpenLiberty() {
        return openliberty;
    }
    */

    /*
    //Obtener WAR
    private static MountableFile getWarFile() {
        return MountableFile.forHostPath(Paths.get("target/ruta-1.0-SNAPSHOT.war").toAbsolutePath());
    }
    */

    /*
    //Saber si es SystemTest
    public static boolean isSystemTest() {
        return SystemTest;
    }
    */
}