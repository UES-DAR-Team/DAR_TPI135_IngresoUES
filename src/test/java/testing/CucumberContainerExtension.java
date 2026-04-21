package testing;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.MountableFile;
import java.nio.file.Paths;
import java.util.List;

public class CucumberContainerExtension implements BeforeAllCallback, AfterAllCallback {

        // Lock para evitar que múltiples hilos arranquen contenedores al mismo tiempo
        private static final Object lock = new Object();

        private static boolean postgresStart = false;
        private static int numClassTest = 0;
        private static boolean SystemTest = false;
        private static boolean libertyStart = false;
        protected static final Network red = Network.newNetwork();

        protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5-alpine")
                .withDatabaseName("ingreso_ues_db")
                .withPassword("postgresmy")
                .withUsername("postgres")
                .withExposedPorts(5432)
                .withNetwork(red)
                .withNetworkAliases("db");

        //cuando cambiemos la configuracion de la construccion a partir de dockerfile a la imagen preconstruida
    //hay que modificar estas lineas, osea cuando traigamos los cambios de la main
        protected static final GenericContainer<?> openliberty = new GenericContainer<>(
                new ImageFromDockerfile()
                        .withDockerfile(Paths.get("src/test/resources/liberty/Dockerfile"))
        )
                .withExposedPorts(9080)
                .withCopyFileToContainer(
                        getWarFile(), "/config/dropins/DAR_TPI135_IngresoUES-1.0-SNAPSHOT.war")
                .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("LIBERTY")))
                .withNetwork(red)
                .withEnv("PGHOST", "db")
                .withEnv("PGPORT", "5432")
                .withEnv("PGDBNAME", "ingreso_ues_db")
                .withEnv("PGUSER", "postgres")
                .withEnv("PGPASSWORD", "postgresmy")
                .dependsOn(postgres)
                .waitingFor(Wait.forLogMessage(".*CWWKF0011I.*", 1)
                        .withStartupTimeout(java.time.Duration.ofSeconds(180))
                );


        public static void configurarParaE2E(){
            SystemTest = true;
            postgres = postgres.withInitScript("ingreso_ues_db.sql");
        }

        public static void configurarParaIT(){
            SystemTest = false;
            postgres = postgres.withInitScript("ingreso_ues_db.sql");
        }

        @Override
        public void beforeAll(ExtensionContext context) throws Exception {
            synchronized (lock) {
                boolean isCucumberTest = context.getTestClass()
                        .map(cls -> cls.isAnnotationPresent(CucumberTest.class))
                        .orElse(false);
//solo se configura como E2E por que va a ser necesario levantar los dos conetenedores
                if (isCucumberTest) {
                    configurarParaE2E();
                }

                numClassTest++;

                if (!postgresStart) {
                    postgres.start();
                    postgresStart = true;
                }

                // Iniciar OpenLiberty y postgres (@CucumberTest)
                if (isCucumberTest && !libertyStart) {
                    // Arranca el contenedor de OpenLiberty y postgres
                    //openliberty.start();
                    Startables.deepStart(List.of(openliberty, postgres)).join();

                    // Agrega un shutdown hook para imprimir los logs de OpenLiberty al finalizar las pruebas
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        System.out.println("=== LIBERTY MESSAGES.LOG ===");
                        try {
                            org.testcontainers.containers.Container.ExecResult result =
                                    openliberty.execInContainer("cat", "/logs/messages.log");
                            System.out.println(result.getStdout());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }));

                    // Imprime los logs de OpenLiberty al iniciar las pruebas (para debug)
                    System.out.println("=== LIBERTY LOGS ===");
                    System.out.println(openliberty.getLogs());
                    System.out.println("=== FIN LIBERTY LOGS ===");

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
            synchronized (testing.ContainerExtension.class) {
                numClassTest--;
            }
        }

        public static PostgreSQLContainer<?> getPostgres() {
            return postgres;
        }

        public static GenericContainer<?> getOpenLiberty() {
            return openliberty;
        }

        private static MountableFile getWarFile() {
            return MountableFile.forHostPath(Paths.get("target/DAR_TPI135_IngresoUES-1.0-SNAPSHOT.war").toAbsolutePath());
        }

    }
