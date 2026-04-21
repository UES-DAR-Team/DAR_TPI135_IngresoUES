package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.bdd.aspirante.crear.stepsefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.mail.Header;
import jakarta.validation.constraints.AssertTrue;
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
        Startables.deepStart(List.of(postgres, openliberty)).join();
        Assertions.assertTrue(postgres.isRunning());
        cliente = ClientBuilder.newClient();
        // El WAR desplegado se llama DAR_TPI135_IngresoUES-1.0-SNAPSHOT.war, el contexto por defecto será ese nombre.
        target = cliente.target(String.format("http://%s:%d/DAR_TPI135_IngresoUES-1.0-SNAPSHOT/v1/aspirante", openliberty.getHost(), openliberty.getMappedPort(9080)));
    }

    @When("puedo crear un aspirante")
    public void puedo_crear_un_aspirante() {
        System.out.println("crear aspirante");
        nuevoAspirante = new Aspirante();
        nuevoAspirante.setNombreAspirante("chepe");
        nuevoAspirante.setApellidoAspirante("Funes");
        nuevoAspirante.setFechaRegistro(OffsetDateTime.now());
        nuevoAspirante.setActivo(true);
        int esperado = 201;
        Response response = target
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(nuevoAspirante));
        Assertions.assertEquals(esperado, response.getStatus());
        Assertions.assertTrue(response.getHeaders().containsKey("Location"));
        UUID id = UUID.fromString(response.getHeaderString("Location").split("aspirante/")[1]);
        Assertions.assertNotNull(id);
        nuevoAspirante.setId(id);
    }

    @When("puedo asociarle a una opcion de carrera, por ejemplo {word}")
    public void puedo_asociarle_a_una_opcion_de_carrera_por_ejemplo_I30515(String codigoPrograma) {
        System.out.println("asociar opcion de carrera");
        Assertions.assertNotNull(codigoPrograma);
        AspiranteOpcione aspiranteOpcione = new AspiranteOpcione();
        aspiranteOpcione.setCodigoPrograma(codigoPrograma);
        aspiranteOpcione.setFechaSeleccion(OffsetDateTime.now());
        aspiranteOpcione.setIdAspirante(nuevoAspirante);
        //debemos modificar toda la linea de aspiranteOpcione para asignaerle preferencias
        //aspiranteOpcione.setPreferenca(1);

        // corregir path: usar {idAspirante} para que resolveTemplate funcione
        Response response = target
                .path("{idAspirante}/opciones")
                .resolveTemplate("idAspirante", nuevoAspirante.getId())
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(aspiranteOpcione));
        Assertions.assertEquals(201, response.getStatus());
        Assertions.assertTrue(response.getHeaders().containsKey("Location"));
        UUID id = UUID.fromString(response.getHeaderString("Location").split("opcion/")[1]);
        Assertions.assertNotNull(id);

    }

    @Then("puedo consultar el perfil del aspirante recien creado")
    public void puedo_consultar_el_perfil_del_aspirante_recien_creado() {
        System.out.println("consultando aspirante");
            Response response = target
                    .path("{idAspirante}")
                    .resolveTemplate("idAspirante", nuevoAspirante.getId())
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            Assertions.assertNotNull(response);
            Assertions.assertEquals(200, response.getStatus());
            Aspirante aspiranteResponse = response.readEntity(Aspirante.class);
            Assertions.assertTrue(nuevoAspirante.getId().compareTo(aspiranteResponse.getId()) == 0);

    }

    @Then("verificar la opcion de carrera a la que fue asociado")
    public void verificar_la_opcion_de_carrera_a_la_que_fue_asociado() {
        System.out.println("consultando opciones de carrera");
        int first = 0;
        int max = 10;
        int esperado = 200;
        int total_esperado = 1;
        Response response = target
                .path("{idAspirante}/opciones")
                .resolveTemplate("idAspirante", nuevoAspirante.getId())
                .queryParam("first", first)
                .queryParam("max", max)
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(esperado, response.getStatus());
        //implementar bien la verificacion la cabecera de total count
        //Assertions.assertTrue(response.getHeaders().containsKey("X-Total-Count"));
        //implementar bien la verificacion de total count
        //Assertions.assertEquals(total_esperado, Integer.parseInt(response.));
        List<AspiranteOpcione> registros = response.readEntity(new GenericType<List<AspiranteOpcione>>() {});
        for(AspiranteOpcione registro : registros) {
            System.out.println(registro.getCodigoPrograma());
        }
    }

}
