package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.bdd;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PreguntaDistractor;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class pregunta_crearYAsociarAreaYDistractorStepDefinitions {

    static Client cliente;
    static WebTarget target;
    static PreguntaDistractor preguntaDistractor;
    static Pregunta pregunta;
    static Distractor distractor;
    static AreaConocimiento areaConocimiento;
    static UUID areaConocimientoId;


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

    @Given("existe un area de conocimiento llamada {string}")
    public void existe_un_area_de_conocimiento_llamada(String area) {
        Startables.deepStart(List.of(postgres, openliberty)).join();
        Assertions.assertTrue(postgres.isRunning());
        cliente = ClientBuilder.newClient();
        //target = cliente.target(String.format("http://%s:%d/DAR_TPI135_IngresoUES-1.0-SNAPSHOT/v1/", openliberty.getHost(), openliberty.getMappedPort(9080)));

        String base = String.format("http://%s:%d/DAR_TPI135_IngresoUES-1.0-SNAPSHOT/v1",
                openliberty.getHost(), openliberty.getMappedPort(9080));
        WebTarget areaTarget = cliente.target(base).path("areaConocimiento");

        Response r = areaTarget
                .queryParam("first", 0)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertEquals(200, r.getStatus(), "Error al listar áreas de conocimiento");

        List<Map<String, Object>> lista = r.readEntity(new GenericType<List<Map<String, Object>>>() {});
        boolean encontrado = false;
        for (Map<String, Object> item : lista) {
            Object nombreObj = item.get("nombre");
            if (nombreObj != null && nombreObj.toString().equals(area)) {
                Object idObj = item.get("id");
                Assertions.assertNotNull(idObj, "El registro encontrado no contiene id");
                areaConocimientoId = UUID.fromString(idObj.toString());
                encontrado = true;
                break;
            }
        }
        Assertions.assertTrue(encontrado, "No se encontró el área de conocimiento: " + area);

    }

    @And("existe un primer distractor activo con valor {string}")
    public void existe_un_primer_distractor_activo_con_valor(String distractor) {
        throw new io.cucumber.java.PendingException();

    }

    @And("existe un segundo distractor activo con valor {string}")
    public void existe_un_segundo_distractor_activo_con_valor(String distractor) {
        throw new io.cucumber.java.PendingException();

    }

    @When("creo una pregunta con el texto {string}")
    public void creo_una_pregunta_con_el_texto(String texto) {
        throw new io.cucumber.java.PendingException();

    }

    @And("asocio la pregunta al area de conocimiento {string}")
    public void asocio_la_pregunta_al_area_de_conocimiento(String area){
        throw new io.cucumber.java.PendingException();

    }

    @And("asocio el primer distractor a la pregunta marcando esCorrecto como {string}")
    public void asocio_el_primer_distractor_a_la_pregunta_marcando_esCorrecto_como_(String esCorrecto){
        throw new io.cucumber.java.PendingException();

    }

    @And("asocio el segundo distractor a la pregunta marcando esCorrecto como {string}")
    public void asocio_el_segundo_distractor_a_la_pregunta_marcando_esCorrecto_como_(String esCorrecto){
        throw new io.cucumber.java.PendingException();

    }

    @Then("puedo consultar la pregunta recien creada con el texto {string}")
    public void puedo_consultar_la_pregunta_recien_creada_con_el_texto(String texto){
        throw new io.cucumber.java.PendingException();

    }

    @And("verifico que la pregunta pertenece al area {string}")
    public void verifico_que_la_pregunta_pertenece_al_area_(String area){
        throw new io.cucumber.java.PendingException();

    }
    @And("verifico que la pregunta se asocio al primer distractor")
    public void verifico_que_la_pregunta_se_asocio_al_primer_distractor(){
        throw new io.cucumber.java.PendingException();

    }

    @And("verifico que la pregunta se asocio al segundo distractor")
    public void verifico_que_la_pregunta_se_asocio_al_segundo_distractor(){
        throw new io.cucumber.java.PendingException();

    }


}
