package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;
import testing.SystemTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SystemTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ContainerExtension.class)
public class PruebaJornadaResourceSystem extends BaseIntegrationAbstract {

    private static final String PATH = "prueba";

    private static final UUID ID_PRUEBA = UUID.fromString("07000000-0000-0000-0000-000000000001");
    private static final UUID ID_JORNADA = UUID.fromString("e5000000-0000-0000-0000-000000000001");
    private static final UUID ID_PRUEBA_INEXISTENTE = UUID.fromString("99999999-9999-9999-9999-999999999999");
    private static final UUID ID_JORNADA_INEXISTENTE = UUID.fromString("99999999-9999-9999-9999-999999999998");
    private static final int ID_EXISTENTE = 1;
    private static final int ID_INEXISTENTE = 99999;

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindById {

        @Test
        @Order(1)
        void responde200CuandoExiste() {
            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("jornada")
                    .path(ID_JORNADA.toString())
                    .path(String.valueOf(ID_EXISTENTE))
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus());
            assertTrue(response.hasEntity());
        }

        @Test
        @Order(2)
        void responde404CuandoNoExiste() {
            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("jornada")
                    .path(ID_JORNADA.toString())
                    .path(String.valueOf(ID_INEXISTENTE))
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus());
        }

        @Test
        @Order(3)
        void responde404CuandoIdPruebaNoCoincide() {
            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA_INEXISTENTE.toString())
                    .path("jornada")
                    .path(ID_JORNADA.toString())
                    .path(String.valueOf(ID_EXISTENTE))
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus());
        }

        @Test
        @Order(4)
        void responde404CuandoIdJornadaNoCoincide() {
            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("jornada")
                    .path(ID_JORNADA_INEXISTENTE.toString())
                    .path(String.valueOf(ID_EXISTENTE))
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus());
        }
    }

    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Create {

        @Test
        @Order(1)
        void responde404CuandoPruebaNoExiste() {
            String body = """
                    { "fechaAsignacion": "2025-06-01T08:00:00Z" }
                    """;

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA_INEXISTENTE.toString())
                    .path("jornada")
                    .path(ID_JORNADA.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(404, response.getStatus());
            assertNotNull(response.getHeaderString("Not-found"));
        }

        @Test
        @Order(2)
        void responde404CuandoJornadaNoExiste() {
            String body = """
                    { "fechaAsignacion": "2025-06-01T08:00:00Z" }
                    """;

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("jornada")
                    .path(ID_JORNADA_INEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(404, response.getStatus());
            assertNotNull(response.getHeaderString("Not-found"));
        }

        @Test
        @Order(3)
        void responde422CuandoEntityTieneId() {
            String body = """
                    { "id": 1, "fechaAsignacion": "2025-06-01T08:00:00Z" }
                    """;

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("jornada")
                    .path(ID_JORNADA.toString())
                    .request()
                    .post(Entity.json(body));

            assertEquals(422, response.getStatus());
        }

        @Test
        @Order(4)
        void respondeErrorCuandoBodyVacio() {
            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("jornada")
                    .path(ID_JORNADA.toString())
                    .request()
                    .post(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(response.getStatus() == 400 || response.getStatus() == 500);
        }

        @Test
        @Order(5)
        void responde500OErrorCuandoRelacionYaExiste() {
            String body = """
                    { "fechaAsignacion": "2025-06-01T08:00:00Z" }
                    """;

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("jornada")
                    .path(ID_JORNADA.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertTrue(response.getStatus() == 500 || response.getStatus() == 409);
        }

        @Test
        @Order(6)
        void responde201CuandoCombinacionNueva() {
            UUID idPrueba2 = UUID.fromString("07000000-0000-0000-0000-000000000002");
            String body = """
                    { "fechaAsignacion": "2025-06-01T08:00:00Z" }
                    """;

            Response response = target
                    .path(PATH)
                    .path(idPrueba2.toString())
                    .path("jornada")
                    .path(ID_JORNADA.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(201, response.getStatus());
            assertNotNull(response.getHeaderString("Location"));
            assertTrue(response.hasEntity());
        }

    }

    @Nested
    @Order(3)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Update {

        @Test
        @Order(1)
        void responde200CuandoExiste() {
            String body = """
                    { "fechaAsignacion": "2025-07-15T10:00:00Z" }
                    """;

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("jornada")
                    .path(ID_JORNADA.toString())
                    .path(String.valueOf(ID_EXISTENTE))
                    .request()
                    .put(Entity.json(body));

            assertEquals(200, response.getStatus());
        }

        @Test
        @Order(2)
        void responde404CuandoNoExiste() {
            String body = """
                    { "fechaAsignacion": "2025-07-15T10:00:00Z" }
                    """;

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("jornada")
                    .path(ID_JORNADA.toString())
                    .path(String.valueOf(ID_INEXISTENTE))
                    .request()
                    .put(Entity.json(body));

            assertEquals(404, response.getStatus());
        }

        @Test
        @Order(3)
        void responde404CuandoIdPruebaNoCoincide() {
            String body = """
                    { "fechaAsignacion": "2025-07-15T10:00:00Z" }
                    """;

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA_INEXISTENTE.toString())
                    .path("jornada")
                    .path(ID_JORNADA.toString())
                    .path(String.valueOf(ID_EXISTENTE))
                    .request()
                    .put(Entity.json(body));

            assertEquals(404, response.getStatus());
        }

        @Test
        @Order(4)
        void responde404CuandoIdJornadaNoCoincide() {
            String body = """
                    { "fechaAsignacion": "2025-07-15T10:00:00Z" }
                    """;

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("jornada")
                    .path(ID_JORNADA_INEXISTENTE.toString())
                    .path(String.valueOf(ID_EXISTENTE))
                    .request()
                    .put(Entity.json(body));

            assertEquals(404, response.getStatus());
        }

        @Test
        @Order(5)
        void respondeErrorCuandoBodyVacio() {
            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("jornada")
                    .path(ID_JORNADA.toString())
                    .path(String.valueOf(ID_EXISTENTE))
                    .request()
                    .put(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(response.getStatus() == 400 || response.getStatus() == 500);
        }
    }
}