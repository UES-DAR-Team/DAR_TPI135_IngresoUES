package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.bdd.aspirante.crear.stepsefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.validation.constraints.AssertTrue;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import org.junit.jupiter.api.Assertions;
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

public class CrearStepDefitions {

    static Client cliente;
    static WebTarget target;

    static Network red= Network.newNetwork();
    static MountableFile getWarFile() {
        return MountableFile.forHostPath(Paths.get("target/DAR_TPI135_IngresoUES-1.0-SNAPSHOT.war").toAbsolutePath());
    }
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5-alpine")
            .withDatabaseName("ingreso_ues_db")
            .withPassword("postgresmy")
            .withUsername("postgres")
            .withExposedPorts(5432)
            .withNetwork(red)
            .withNetworkAliases("db");

    static final GenericContainer<?> openliberty = new GenericContainer<>(
            new ImageFromDockerfile()
                    .withDockerfile(Paths.get("src/test/resources/liberty/Dockerfile")))
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

    @Given("se tiene un servidor contenido con la aplicacion desplegada")
    public void se_tiene_un_servidor_contenido_con_la_aplicacion_desplegada() {
        //verificar que el servidor esta corriendo y la aplicacion esta desplegada
        Startables.deepStart(List.of(postgres, openliberty)).join();
        Assertions.assertTrue(postgres.isRunning());
        cliente = ClientBuilder.newClient();
        target = cliente.target(String.format("http://%s:%d/PupaSV-1.0-SNAPSHOT/v1/aspirante", openliberty.getHost(), openliberty.getMappedPort(9080)));

    }

    @When("puedo crear un aspirante")
    public void puedo_crear_un_aspirante() {
        throw new io.cucumber.java.PendingException();
    }
    @When("puedo asociarle a una opcion de carrera, por ejemplo I30515")
    public void puedo_asociarle_a_una_opcion_de_carrera_por_ejemplo_I30515() {
        System.out.println("and running");
    }

    @Then("puedo consultar el perfil del aspirante recien creado")
    public void puedo_consultar_el_perfil_del_aspirante_recien_creado() {
        System.out.println("with finish");
    }

    @Then("verificar la opcion de carrera a la que fue asociado")
    public void verificar_la_opcion_de_carrera_a_la_que_fue_asociado() {
        System.out.println("with finish");
    }

}
