package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.bdd;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
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
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class pregunta_crearYAsociarAreaYDistractorStepDefinitions {

    static Client cliente;
    static String baseUrl;
    static PreguntaDistractor preguntaDistractor;
    static Pregunta pregunta;
    static Distractor primerDistractor;
    static Distractor segundoDistractor;
    static AreaConocimiento areaConocimiento;
    static UUID areaConocimientoId;
    static UUID preguntaId;
    static UUID primerDistractorId;
    static UUID segundoDistractorId;

    static Network red = Network.newNetwork();

    static MountableFile getWarFile() {
        return MountableFile.forHostPath(Paths.get("target/DAR_TPI135_IngresoUES-1.0-SNAPSHOT.war").toAbsolutePath());
    }

    // ── FIX PRINCIPAL: se agrega .withInitScript para cargar el SQL con los datos de prueba ──
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5-alpine")
            .withDatabaseName("ingreso_ues_db")
            .withPassword("postgresmy")
            .withUsername("postgres")
            .withExposedPorts(5432)
            .withNetwork(red)
            .withNetworkAliases("db")
            .withInitScript("ingreso_ues_db.sql"); // <-- carga las tablas y datos de prueba al iniciar

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
                    .withStartupTimeout(java.time.Duration.ofSeconds(180)));

    // ── GIVEN: verifica que el área "Matemáticas" exista en la BD ──
    @Given("existe un area de conocimiento llamada {string}")
    public void existe_un_area_de_conocimiento_llamada(String area) {
        Startables.deepStart(List.of(postgres, openliberty)).join();
        Assertions.assertTrue(postgres.isRunning());

        cliente = ClientBuilder.newClient();
        baseUrl = String.format("http://%s:%d/DAR_TPI135_IngresoUES-1.0-SNAPSHOT/v1",
                openliberty.getHost(), openliberty.getMappedPort(9080));

        WebTarget areaTarget = cliente.target(baseUrl).path("areaConocimiento");

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

    // UUIDs fijos de los distractores definidos en ingreso_ues_db.sql
    // "16" -> d4000000-0000-0000-0000-000000000031
    // "8"  -> d4000000-0000-0000-0000-000000000029
    private static final Map<String, UUID> DISTRACTOR_UUID_POR_VALOR = Map.of(
            "16", UUID.fromString("d4000000-0000-0000-0000-000000000031"),
            "8",  UUID.fromString("d4000000-0000-0000-0000-000000000029")
    );

    // ── AND: verifica que el primer distractor exista y esté activo vía GET /distractor/{id} ──
    @And("existe un primer distractor activo con valor {string}")
    public void existe_un_primer_distractor_activo_con_valor(String valor) {
        UUID id = DISTRACTOR_UUID_POR_VALOR.get(valor);
        Assertions.assertNotNull(id,
                "UUID no mapeado para el distractor con valor: " + valor);

        Response r = cliente.target(baseUrl)
                .path("distractor")
                .path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertEquals(200, r.getStatus(),
                "No se encontró el primer distractor con id: " + id);

        Map<String, Object> distractor = r.readEntity(new GenericType<Map<String, Object>>() {});
        Assertions.assertEquals(valor, distractor.get("contenidoDistractor"),
                "El valor del primer distractor no coincide");
        Assertions.assertEquals(Boolean.TRUE, distractor.get("activo"),
                "El primer distractor no está activo");

        primerDistractorId = id;
    }

    // ── AND: verifica que el segundo distractor exista y esté activo vía GET /distractor/{id} ──
    @And("existe un segundo distractor activo con valor {string}")
    public void existe_un_segundo_distractor_activo_con_valor(String valor) {
        UUID id = DISTRACTOR_UUID_POR_VALOR.get(valor);
        Assertions.assertNotNull(id,
                "UUID no mapeado para el distractor con valor: " + valor);

        Response r = cliente.target(baseUrl)
                .path("distractor")
                .path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertEquals(200, r.getStatus(),
                "No se encontró el segundo distractor con id: " + id);

        Map<String, Object> distractor = r.readEntity(new GenericType<Map<String, Object>>() {});
        Assertions.assertEquals(valor, distractor.get("contenidoDistractor"),
                "El valor del segundo distractor no coincide");
        Assertions.assertEquals(Boolean.TRUE, distractor.get("activo"),
                "El segundo distractor no está activo");

        segundoDistractorId = id;
    }

    // ── WHEN: crea la pregunta vía POST /pregunta ──
    @When("creo una pregunta con el texto {string}")
    public void creo_una_pregunta_con_el_texto(String texto) {
        WebTarget preguntaTarget = cliente.target(baseUrl).path("pregunta");

        Map<String, Object> body = new HashMap<>();
        body.put("contenidoPregunta", texto);
        body.put("fechaCreacion", OffsetDateTime.now().toString());
        body.put("activo", true);

        Response r = preguntaTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(body, MediaType.APPLICATION_JSON));

        Assertions.assertEquals(201, r.getStatus(), "Error al crear la pregunta");

        Map<String, Object> creada = r.readEntity(new GenericType<Map<String, Object>>() {});
        Assertions.assertNotNull(creada.get("id"), "La pregunta creada no tiene id");
        preguntaId = UUID.fromString(creada.get("id").toString());
    }

    // ── AND: asocia la pregunta al área via POST /areaConocimiento/{idArea}/pregunta ──
    // @Path real del resource: areaConocimiento/{idAreaConocimiento}/pregunta
    // body: { "idPregunta": { "id": "..." } }
    @And("asocio la pregunta al area de conocimiento {string}")
    public void asocio_la_pregunta_al_area_de_conocimiento(String area) {
        WebTarget asociarTarget = cliente.target(baseUrl)
                .path("areaConocimiento")
                .path(areaConocimientoId.toString())
                .path("pregunta");

        Map<String, Object> preguntaRef = new HashMap<>();
        preguntaRef.put("id", preguntaId.toString());

        Map<String, Object> body = new HashMap<>();
        body.put("idPregunta", preguntaRef);

        Response r = asociarTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(body, MediaType.APPLICATION_JSON));

        Assertions.assertEquals(201, r.getStatus(),
                "Error al asociar la pregunta al área de conocimiento: " + area);
    }

    // ── AND: asocia el primer distractor a la pregunta via POST /pregunta/{id}/distractor ──
    @And("asocio el primer distractor a la pregunta marcando esCorrecto como {string}")
    public void asocio_el_primer_distractor_a_la_pregunta_marcando_esCorrecto_como_(String esCorrecto) {
        WebTarget asociarTarget = cliente.target(baseUrl)
                .path("pregunta")
                .path(preguntaId.toString())
                .path("distractor");

        Map<String, Object> body = new HashMap<>();
        Map<String, Object> distRef = new HashMap<>();
        distRef.put("id", primerDistractorId.toString());
        body.put("idDistractor", distRef);
        body.put("esCorrecto", Boolean.parseBoolean(esCorrecto));

        Response r = asociarTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(body, MediaType.APPLICATION_JSON));

        Assertions.assertEquals(201, r.getStatus(),
                "Error al asociar el primer distractor a la pregunta");
    }

    // ── AND: asocia el segundo distractor a la pregunta via POST /pregunta/{id}/distractor ──
    @And("asocio el segundo distractor a la pregunta marcando esCorrecto como {string}")
    public void asocio_el_segundo_distractor_a_la_pregunta_marcando_esCorrecto_como_(String esCorrecto) {
        WebTarget asociarTarget = cliente.target(baseUrl)
                .path("pregunta")
                .path(preguntaId.toString())
                .path("distractor");

        Map<String, Object> body = new HashMap<>();
        Map<String, Object> distRef = new HashMap<>();
        distRef.put("id", segundoDistractorId.toString());
        body.put("idDistractor", distRef);
        body.put("esCorrecto", Boolean.parseBoolean(esCorrecto));

        Response r = asociarTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(body, MediaType.APPLICATION_JSON));

        Assertions.assertEquals(201, r.getStatus(),
                "Error al asociar el segundo distractor a la pregunta");
    }

    // ── THEN: consulta la pregunta creada via GET /pregunta/{id} ──
    @Then("puedo consultar la pregunta recien creada con el texto {string}")
    public void puedo_consultar_la_pregunta_recien_creada_con_el_texto(String texto) {
        Response r = cliente.target(baseUrl)
                .path("pregunta")
                .path(preguntaId.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertEquals(200, r.getStatus(), "Error al consultar la pregunta recién creada");

        Map<String, Object> preguntaConsultada = r.readEntity(new GenericType<Map<String, Object>>() {});
        Assertions.assertEquals(texto, preguntaConsultada.get("contenidoPregunta"),
                "El texto de la pregunta no coincide");
    }

    // ── AND: verifica que el área tiene asociada la pregunta via GET /areaConocimiento/{idArea}/pregunta ──
    // @Path real del resource: areaConocimiento/{idAreaConocimiento}/pregunta
    @And("verifico que la pregunta pertenece al area {string}")
    public void verifico_que_la_pregunta_pertenece_al_area_(String area) {
        Response r = cliente.target(baseUrl)
                .path("areaConocimiento")
                .path(areaConocimientoId.toString())
                .path("pregunta")
                .queryParam("first", 0)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertEquals(200, r.getStatus(),
                "Error al consultar las preguntas del área");

        List<Map<String, Object>> lista = r.readEntity(new GenericType<List<Map<String, Object>>>() {});
        boolean encontrado = lista.stream().anyMatch(item -> {
            Object preguntaObj = item.get("idPregunta");
            if (preguntaObj instanceof Map) {
                Object idObj = ((Map<?, ?>) preguntaObj).get("id");
                return idObj != null && preguntaId.toString().equals(idObj.toString());
            }
            return false;
        });
        Assertions.assertTrue(encontrado,
                "La pregunta " + preguntaId + " no está asociada al área: " + area);
    }

    // ── AND: verifica que el primer distractor está asociado a la pregunta ──
    @And("verifico que la pregunta se asocio al primer distractor")
    public void verifico_que_la_pregunta_se_asocio_al_primer_distractor() {
        Response r = cliente.target(baseUrl)
                .path("pregunta")
                .path(preguntaId.toString())
                .path("distractor")
                .queryParam("first", 0)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertEquals(200, r.getStatus(),
                "Error al consultar los distractores de la pregunta");

        List<Map<String, Object>> lista = r.readEntity(new GenericType<List<Map<String, Object>>>() {});
        boolean encontrado = lista.stream().anyMatch(item -> {
            Object distObj = item.get("idDistractor");
            if (distObj instanceof Map) {
                Object idObj = ((Map<?, ?>) distObj).get("id");
                return idObj != null && primerDistractorId.toString().equals(idObj.toString());
            }
            return false;
        });
        Assertions.assertTrue(encontrado,
                "La pregunta no tiene asociado el primer distractor con id: " + primerDistractorId);
    }

    // ── AND: verifica que el segundo distractor está asociado a la pregunta ──
    @And("verifico que la pregunta se asocio al segundo distractor")
    public void verifico_que_la_pregunta_se_asocio_al_segundo_distractor() {
        Response r = cliente.target(baseUrl)
                .path("pregunta")
                .path(preguntaId.toString())
                .path("distractor")
                .queryParam("first", 0)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertEquals(200, r.getStatus(),
                "Error al consultar los distractores de la pregunta");

        List<Map<String, Object>> lista = r.readEntity(new GenericType<List<Map<String, Object>>>() {});
        boolean encontrado = lista.stream().anyMatch(item -> {
            Object distObj = item.get("idDistractor");
            if (distObj instanceof Map) {
                Object idObj = ((Map<?, ?>) distObj).get("id");
                return idObj != null && segundoDistractorId.toString().equals(idObj.toString());
            }
            return false;
        });
        Assertions.assertTrue(encontrado,
                "La pregunta no tiene asociado el segundo distractor con id: " + segundoDistractorId);
    }
}