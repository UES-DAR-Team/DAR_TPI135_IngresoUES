package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.bdd;

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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspiranteOpcione;

import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class CrearStepDefitions {

    static Client cliente;
    static WebTarget target;
    static Aspirante nuevoAspirante;

    static Network red = Network.newNetwork();

    static MountableFile getWarFile() {
        return MountableFile.forHostPath(
                Paths.get("target/DAR_TPI135_IngresoUES-1.0-SNAPSHOT.war").toAbsolutePath());
    }

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5-alpine")
            .withDatabaseName("ingreso_ues_db")
            .withPassword("postgresmy")
            .withUsername("postgres")
            .withExposedPorts(5432)
            .withNetwork(red)
            .withNetworkAliases("db")
            .withInitScript("ingreso_ues_db.sql");

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

    // -------------------------------------------------------------------------
    // GIVEN
    // -------------------------------------------------------------------------

    @Given("se tiene un servidor contenido con la aplicacion desplegada")
    public void se_tiene_un_servidor_contenido_con_la_aplicacion_desplegada() {
        Startables.deepStart(List.of(postgres, openliberty)).join();
        Assertions.assertTrue(postgres.isRunning(), "El contenedor de postgres debe estar corriendo");
        Assertions.assertTrue(openliberty.isRunning(), "El contenedor de openliberty debe estar corriendo");

        cliente = ClientBuilder.newClient();
        target = cliente.target(
                String.format("http://%s:%d/DAR_TPI135_IngresoUES-1.0-SNAPSHOT/v1/aspirante",
                        openliberty.getHost(),
                        openliberty.getMappedPort(9080)));
    }

    // -------------------------------------------------------------------------
    // WHEN
    // -------------------------------------------------------------------------

    @When("puedo crear un aspirante")
    public void puedo_crear_un_aspirante() {
        // Construir aspirante con todos los campos obligatorios
        nuevoAspirante = new Aspirante();
        nuevoAspirante.setNombreAspirante("Chepe");
        nuevoAspirante.setApellidoAspirante("Funes");
        //nuevoAspirante.setFechaRegistro(OffsetDateTime.now());
        //nuevoAspirante.setActivo(true);

        Response response = target
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(nuevoAspirante));

        // Verificar que el servidor respondió 201 Created
        Assertions.assertEquals(201, response.getStatus(),
                "Se esperaba status 201 al crear el aspirante");

        // Verificar que viene el header Location con la URL del nuevo recurso
        Assertions.assertTrue(response.getHeaders().containsKey("Location"),
                "La respuesta debe incluir el header Location");

        // Extraer el UUID desde el final de la URL del Location
        // Ejemplo: http://host:port/.../aspirante/550e8400-e29b-41d4-a716-446655440000
        String location = response.getHeaderString("Location");
        String idStr = location.substring(location.lastIndexOf("/") + 1);
        UUID id = UUID.fromString(idStr);

        Assertions.assertNotNull(id, "El id del aspirante creado no debe ser null");
        nuevoAspirante.setId(id);

        System.out.println("Aspirante creado con id: " + nuevoAspirante.getId());
    }

    @When("puedo asociarle a una opcion de carrera, por ejemplo {word}")
    public void puedo_asociarle_a_una_opcion_de_carrera_por_ejemplo(String codigoPrograma) {
        Assertions.assertNotNull(codigoPrograma, "El código de programa no debe ser null");
        Assertions.assertNotNull(nuevoAspirante, "El aspirante debe haberse creado antes");
        Assertions.assertNotNull(nuevoAspirante.getId(), "El aspirante debe tener id");

        // Construir la opción con todos los campos obligatorios
        // NOTA: NO se setea idAspirante aquí; el recurso lo toma del path param {idAspirante}
        AspiranteOpcione opcion = new AspiranteOpcione();
        opcion.setCodigoPrograma(codigoPrograma);
        opcion.setNombrePrograma("Ingeniería en Sistemas Informáticos");
       // opcion.setFechaSeleccion(OffsetDateTime.now());
        opcion.setPreferencia((short) 1);   // primera opción de preferencia

        Response response = target
                .path("{idAspirante}/opciones")
                .resolveTemplate("idAspirante", nuevoAspirante.getId())
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(opcion));

        // Verificar 201 Created
        Assertions.assertEquals(201, response.getStatus(),
                "Se esperaba status 201 al asociar la opción de carrera. Body: "
                        + response.readEntity(String.class));

        // Verificar header Location
        Assertions.assertTrue(response.getHeaders().containsKey("Location"),
                "La respuesta debe incluir el header Location");

        // El id de AspiranteOpcione es Integer; se extrae del final del Location
        // Ejemplo: http://host:port/.../aspirante/{uuid}/opciones/42
        String location = response.getHeaderString("Location");
        String idStr = location.substring(location.lastIndexOf("/") + 1);
        Integer opcionId = Integer.parseInt(idStr);

        Assertions.assertNotNull(opcionId, "El id de la opción creada no debe ser null");
        System.out.println("Opción de carrera '" + codigoPrograma + "' creada con id: " + opcionId);
    }

    // -------------------------------------------------------------------------
    // THEN
    // -------------------------------------------------------------------------

    @Then("puedo consultar el perfil del aspirante recien creado")
    public void puedo_consultar_el_perfil_del_aspirante_recien_creado() {
        Assertions.assertNotNull(nuevoAspirante, "El aspirante debe haberse creado");
        Assertions.assertNotNull(nuevoAspirante.getId(), "El aspirante debe tener id");

        Response response = target
                .path("{idAspirante}")
                .resolveTemplate("idAspirante", nuevoAspirante.getId())
                .request(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertNotNull(response, "La respuesta no debe ser null");
        Assertions.assertEquals(200, response.getStatus(),
                "Se esperaba status 200 al consultar el aspirante");

        Aspirante aspiranteResponse = response.readEntity(Aspirante.class);
        Assertions.assertNotNull(aspiranteResponse, "El cuerpo de la respuesta no debe ser null");
        Assertions.assertEquals(nuevoAspirante.getId(), aspiranteResponse.getId(),
                "El id del aspirante consultado debe coincidir con el creado");

        System.out.println("Aspirante consultado: " + aspiranteResponse.getId());
    }

    @Then("verificar la opcion de carrera a la que fue asociado")
    public void verificar_la_opcion_de_carrera_a_la_que_fue_asociado() {
        Assertions.assertNotNull(nuevoAspirante, "El aspirante debe haberse creado");
        Assertions.assertNotNull(nuevoAspirante.getId(), "El aspirante debe tener id");

        int first = 0;
        int max = 10;

        Response response = target
                .path("{idAspirante}/opciones")
                .resolveTemplate("idAspirante", nuevoAspirante.getId())
                .queryParam("first", first)
                .queryParam("max", max)
                .request(MediaType.APPLICATION_JSON)
                .get();

        Assertions.assertNotNull(response, "La respuesta no debe ser null");
        Assertions.assertEquals(200, response.getStatus(),
                "Se esperaba status 200 al consultar las opciones del aspirante");

        // Verificar el header Total-records que devuelve el recurso
        Assertions.assertTrue(response.getHeaders().containsKey("Total-records"),
                "La respuesta debe incluir el header Total-records");

        long totalRegistros = Long.parseLong(response.getHeaderString("Total-records"));
        Assertions.assertTrue(totalRegistros >= 1,
                "Debe existir al menos una opción de carrera registrada");

        // Leer y verificar la lista de opciones
        List<AspiranteOpcione> registros = response.readEntity(
                new GenericType<List<AspiranteOpcione>>() {});

        Assertions.assertNotNull(registros, "La lista de opciones no debe ser null");
        Assertions.assertFalse(registros.isEmpty(), "La lista de opciones no debe estar vacía");

        System.out.println("Total opciones registradas: " + totalRegistros);
        registros.forEach(r ->
                System.out.println("  - Opción: " + r.getCodigoPrograma()
                        + " | Preferencia: " + r.getPreferencia()
                        + " | Nombre: " + r.getNombrePrograma()));
    }
}