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
public class AulaResourceSystem extends BaseIntegrationAbstract {

    private static final String PATH = "aula";

    private static final UUID ID_EXISTENTE = UUID.fromString("f6000000-0000-0000-0000-000000000001");
    private static final UUID ID_INEXISTENTE = UUID.fromString("99999999-9999-9999-9999-999999999999");

    private static UUID ID_CREADO;

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindRange {

        @Test
        @Order(1)
        void responde200ParametrosValidos() {
            Response response = target
                    .path(PATH)
                    .queryParam("first", 0)
                    .queryParam("max", 10)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus());
            assertNotNull(response.getHeaderString("Total-records"));
            assertTrue(response.hasEntity());
        }

        @Test
        @Order(2)
        void responde400FirstInvalido() {
            Response response = target
                    .path(PATH)
                    .queryParam("first", -1)
                    .queryParam("max", 10)
                    .request()
                    .get();

            assertEquals(400, response.getStatus());
        }

        @Test
        @Order(3)
        void responde400MaxExcedeLimite() {
            Response response = target
                    .path(PATH)
                    .queryParam("first", 0)
                    .queryParam("max", 11)
                    .request()
                    .get();

            assertEquals(400, response.getStatus());
        }

        @Test
        @Order(4)
        void responde400MaxEsCero() {
            Response response = target
                    .path(PATH)
                    .queryParam("first", 0)
                    .queryParam("max", 0)
                    .request()
                    .get();

            assertEquals(400, response.getStatus());
        }
    }

    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Create {

        @Test
        @Order(1)
        void responde422CuandoEntityTieneId() {
            String body = """
                    { "id": "%s", "nombreAula": "Aula Test", "capacidad": 30, "activo": true }
                    """.formatted(ID_EXISTENTE);

            Response response = target
                    .path(PATH)
                    .request()
                    .post(Entity.json(body));

            assertEquals(422, response.getStatus());
        }

        @Test
        @Order(2)
        void respondeErrorCuandoBodyVacio() {
            Response response = target
                    .path(PATH)
                    .request()
                    .post(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(response.getStatus() == 400 || response.getStatus() == 500);
        }

        @Test
        @Order(3)
        void responde201CuandoEntidadValida() {
            String body = """
                    { "nombreAula": "Aula Test Sistema", "capacidad": 25,
                      "fechaCreacion": "2025-06-01T08:00:00Z", "activo": true }
                    """;

            Response response = target
                    .path(PATH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(201, response.getStatus());
            assertNotNull(response.getHeaderString("Location"));
            assertTrue(response.hasEntity());

            String responseBody = response.readEntity(String.class);
            int idStart = responseBody.indexOf("\"id\":\"") + 6;
            int idEnd = responseBody.indexOf("\"", idStart);
            ID_CREADO = UUID.fromString(responseBody.substring(idStart, idEnd));
        }
    }

    @Nested
    @Order(3)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindById {

        @Test
        @Order(1)
        void responde200CuandoExiste() {
            Response response = target
                    .path(PATH)
                    .path(ID_EXISTENTE.toString())
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
                    .path(ID_INEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus());
            assertNotNull(response.getHeaderString("Not-found-id"));
        }
    }

    @Nested
    @Order(4)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Update {

        @Test
        @Order(1)
        void responde200CuandoExiste() {
            String body = """
                    { "nombreAula": "Aula Test Actualizada", "capacidad": 35,
                      "fechaCreacion": "2025-06-01T08:00:00Z", "activo": true }
                    """;

            Response response = target
                    .path(PATH)
                    .path(ID_EXISTENTE.toString())
                    .request()
                    .put(Entity.json(body));

            assertEquals(200, response.getStatus());
        }

        @Test
        @Order(2)
        void responde404CuandoNoExiste() {
            String body = """
                    { "nombreAula": "Aula Test", "capacidad": 25,
                      "fechaCreacion": "2025-06-01T08:00:00Z", "activo": true }
                    """;

            Response response = target
                    .path(PATH)
                    .path(ID_INEXISTENTE.toString())
                    .request()
                    .put(Entity.json(body));

            assertEquals(404, response.getStatus());
        }

        @Test
        @Order(3)
        void respondeErrorCuandoBodyVacio() {
            Response response = target
                    .path(PATH)
                    .path(ID_EXISTENTE.toString())
                    .request()
                    .put(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(response.getStatus() == 400 || response.getStatus() == 500);
        }
    }

    @Nested
    @Order(5)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class DeleteById {

        @Test
        @Order(1)
        void responde404CuandoNoExiste() {
            Response response = target
                    .path(PATH)
                    .path(ID_INEXISTENTE.toString())
                    .request()
                    .delete();

            assertEquals(404, response.getStatus());
            assertNotNull(response.getHeaderString("Not-found-id"));
        }

        @Test
        @Order(2)
        void responde204CuandoExiste() {
            Response response = target
                    .path(PATH)
                    .path(ID_CREADO.toString())
                    .request()
                    .delete();

            assertEquals(204, response.getStatus());
            assertFalse(response.hasEntity());
        }

        @Test
        @Order(3)
        void responde404CuandoSeIntentaAccederAlRegistroEliminado() {
            Response response = target
                    .path(PATH)
                    .path(ID_CREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus());
        }
    }
}