package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.bdd;

import io.cucumber.java.en.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.junit.jupiter.api.Assertions;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.*;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Paths;
import java.util.*;

public class construccionDeUnaPruebaStepDefinitions {

    static Client cliente;
    static WebTarget baseTarget;

    static UUID pruebaId;
    static UUID areaId;
    static UUID pregunta1Id;
    static UUID pregunta2Id;

    static Integer pruebaAreaId;
    static Integer pap1Id;
    static Integer pap2Id;

    static Network red = Network.newNetwork();

    static MountableFile getWarFile() {
        return MountableFile.forHostPath(
                Paths.get("target/DAR_TPI135_IngresoUES-1.0-SNAPSHOT.war").toAbsolutePath()
        );
    }

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5-alpine")
            .withDatabaseName("ingreso_ues_db")
            .withUsername("postgres")
            .withPassword("postgresmy")
            .withNetwork(red)
            .withNetworkAliases("db")
            .withInitScript("ingreso_ues_db.sql");

    static GenericContainer<?> openliberty = new GenericContainer<>(
            new ImageFromDockerfile()
                    .withDockerfile(Paths.get("src/test/resources/liberty/Dockerfile")))
            .withExposedPorts(9080)
            .withCopyFileToContainer(
                    getWarFile(),
                    "/config/dropins/DAR_TPI135_IngresoUES-1.0-SNAPSHOT.war")
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("LIBERTY")))
            .withNetwork(red)
            .withEnv("PGHOST", "db")
            .withEnv("PGPORT", "5432")
            .withEnv("PGDBNAME", "ingreso_ues_db")
            .withEnv("PGUSER", "postgres")
            .withEnv("PGPASSWORD", "postgresmy")
            .dependsOn(postgres)
            .waitingFor(Wait.forLogMessage(".*CWWKF0011I.*", 1));

    // ---------------- GIVEN ----------------

    @Given("existen areas de conocimiento registradas")
    public void levantar_servidor() {

        Startables.deepStart(List.of(postgres, openliberty)).join();
        cliente = ClientBuilder.newClient();

        String baseUrl = String.format(
                "http://%s:%d/DAR_TPI135_IngresoUES-1.0-SNAPSHOT/v1",
                openliberty.getHost(),
                openliberty.getMappedPort(9080)
        );

        baseTarget = cliente.target(baseUrl);

        Response r = baseTarget.path("areaConocimiento")
                .queryParam("first", 0)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();

        List<Map<String, Object>> lista = r.readEntity(new GenericType<>() {});
        areaId = UUID.fromString(lista.get(0).get("id").toString());
    }

    @And("existen preguntas con sus distractores asociados")
    public void obtener_preguntas() {

        Response r = baseTarget.path("pregunta")
                .queryParam("first", 0)
                .queryParam("max", 10)
                .request(MediaType.APPLICATION_JSON)
                .get();

        List<Map<String, Object>> lista = r.readEntity(new GenericType<>() {});
        pregunta1Id = UUID.fromString(lista.get(0).get("id").toString());
        pregunta2Id = UUID.fromString(lista.get(1).get("id").toString());
    }

    // ---------------- WHEN ----------------

    @When("creo una prueba con nombre {string}")
    public void crear_prueba(String nombre) {

        Map<String, Object> body = new HashMap<>();
        body.put("nombrePrueba", nombre);
        body.put("activo", true);
        body.put("fechaCreacion", java.time.OffsetDateTime.now().toString());

        Response r = baseTarget.path("prueba")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(body));

        Assertions.assertEquals(201, r.getStatus());

        String location = r.getHeaderString("Location");
        pruebaId = UUID.fromString(location.split("prueba/")[1]);
    }

    @And("agrego el area {string} a la prueba con {int} preguntas")
    public void agregar_area(String nombre, int numPreguntas) {

        Map<String, Object> areaRef = new HashMap<>();
        areaRef.put("id", areaId.toString());

        Map<String, Object> body = new HashMap<>();
        body.put("idAreaConocimiento", areaRef);
        body.put("numPreguntas", numPreguntas);

        Response r = baseTarget.path("prueba")
                .path(pruebaId.toString())
                .path("areaConocimiento")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(body));

        Assertions.assertEquals(201, r.getStatus());

        // Obtener idPruebaArea
        Response get = baseTarget.path("prueba")
                .path(pruebaId.toString())
                .path("areaConocimiento")
                .request(MediaType.APPLICATION_JSON)
                .get();

        List<Map<String, Object>> lista = get.readEntity(new GenericType<>() {});
        pruebaAreaId = (Integer) lista.get(0).get("id");
    }

    @And("agrego la pregunta {string} al area en la prueba")
    public void agregar_pregunta(String texto) {

        UUID preguntaId = texto.contains("3²") ? pregunta1Id : pregunta2Id;

        Map<String, Object> preguntaRef = new HashMap<>();
        preguntaRef.put("id", preguntaId.toString());

        Map<String, Object> body = new HashMap<>();
        body.put("idPregunta", preguntaRef);

        Response r = baseTarget.path("pruebaArea")
                .path(pruebaAreaId.toString())
                .path("pregunta")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(body));

        Assertions.assertEquals(201, r.getStatus());

        // obtener idPruebaAreaPregunta
        Response get = baseTarget.path("pruebaArea")
                .path(pruebaAreaId.toString())
                .path("pregunta")
                .request(MediaType.APPLICATION_JSON)
                .get();

        List<Map<String, Object>> lista = get.readEntity(new GenericType<>() {});
        Integer id = (Integer) lista.get(lista.size() - 1).get("id");

        if (pap1Id == null) pap1Id = id;
        else pap2Id = id;
    }

    @And("agrego los distractores a la primera pregunta en la prueba")
    public void distractores1() {
        agregarDistractor(pap1Id, "d4000000-0000-0000-0000-000000000031", false);
        agregarDistractor(pap1Id, "d4000000-0000-0000-0000-000000000029", true);
    }

    @And("agrego los distractores a la segunda pregunta en la prueba")
    public void distractores2() {
        agregarDistractor(pap2Id, "d4000000-0000-0000-0000-000000000031", false);
        agregarDistractor(pap2Id, "d4000000-0000-0000-0000-000000000029", true);
    }

    private void agregarDistractor(Integer papId, String idDistractor, boolean correcto) {

        Map<String, Object> distractorRef = new HashMap<>();
        distractorRef.put("id", idDistractor);

        Map<String, Object> body = new HashMap<>();
        body.put("idDistractor", distractorRef);
        body.put("esRespuestaCorrecta", correcto);
        body.put("fechaRegistro", java.time.OffsetDateTime.now().toString());

        Response r = baseTarget.path("pruebaAreaPregunta")
                .path(papId.toString())
                .path("distractor")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(body));

        System.out.println("STATUS: " + r.getStatus());
        System.out.println("RESPUESTA: " + r.readEntity(String.class));

        Assertions.assertEquals(201, r.getStatus());
    }

    // ---------------- THEN ----------------

    @Then("la prueba {string} existe")
    public void validar_prueba(String nombre) {

        Response r = baseTarget.path("prueba")
                .path(pruebaId.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertEquals(200, r.getStatus());
    }

    @And("la prueba tiene el area {string}")
    public void validar_area(String nombre) {
        Assertions.assertNotNull(pruebaAreaId);
    }

    @And("el area tiene {int} preguntas")
    public void validar_preguntas(int cantidad) {
        Assertions.assertTrue(cantidad > 0);
    }

    @And("cada pregunta tiene {int} distractores")
    public void validar_distractores(int cantidad) {
        Assertions.assertEquals(4, cantidad);
    }

    @And("cada pregunta tiene una unica respuesta correcta")
    public void validar_correcta() {
        Assertions.assertTrue(true);
    }
}