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
public class PruebaAreaPreguntaResourceSystem extends BaseIntegrationAbstract {

    private static final String PATH = "pruebaArea";

    private static final int ID_PRUEBA_AREA = 1;
    private static final int ID_PRUEBA_AREA_INEXISTENTE = 99999;
    private static final UUID ID_PREGUNTA = UUID.fromString("c3000000-0000-0000-0000-000000000001");
    private static final UUID ID_PREGUNTA_INEXISTENTE = UUID.fromString("99999999-9999-9999-9999-999999999999");
    private static final UUID ID_PREGUNTA_NUEVA = UUID.fromString("c3000000-0000-0000-0000-000000000004");

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindRange {

        @Test
        @Order(1)
        void responde200ParametrosValidos() {
            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA))
                    .path("pregunta")
                    .queryParam("first", 0)
                    .queryParam("max", 10)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus());
            assertNotNull(response.getHeaderString("X-Total-Count"));
            assertTrue(response.hasEntity());
        }

        @Test
        @Order(2)
        void responde404CuandoPruebaAreaNoExiste() {
            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA_INEXISTENTE))
                    .path("pregunta")
                    .queryParam("first", 0)
                    .queryParam("max", 10)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus());
            assertNotNull(response.getHeaderString("Not-found"));
        }

        @Test
        @Order(3)
        void responde400FirstInvalido() {
            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA))
                    .path("pregunta")
                    .queryParam("first", -1)
                    .queryParam("max", 10)
                    .request()
                    .get();

            assertEquals(400, response.getStatus());
        }

        @Test
        @Order(4)
        void responde400MaxExcedeLimite() {
            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA))
                    .path("pregunta")
                    .queryParam("first", 0)
                    .queryParam("max", 11)
                    .request()
                    .get();

            assertEquals(400, response.getStatus());
        }

        @Test
        @Order(5)
        void responde400MaxEsCero() {
            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA))
                    .path("pregunta")
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
    class FindOne {

        @Test
        @Order(1)
        void responde200CuandoExiste() {
            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA))
                    .path("pregunta")
                    .path(ID_PREGUNTA.toString())
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
                    .path(String.valueOf(ID_PRUEBA_AREA))
                    .path("pregunta")
                    .path(ID_PREGUNTA_INEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus());
        }
    }

    @Nested
    @Order(3)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Create {

        @Test
        @Order(1)
        void responde404CuandoPruebaAreaNoExiste() {
            String body = """
                    { "idPregunta": { "id": "%s" } }
                    """.formatted(ID_PREGUNTA_NUEVA);

            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA_INEXISTENTE))
                    .path("pregunta")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(404, response.getStatus());
            assertNotNull(response.getHeaderString("Not-found"));
        }

        @Test
        @Order(2)
        void responde404CuandoPreguntaNoExiste() {
            String body = """
                    { "idPregunta": { "id": "%s" } }
                    """.formatted(ID_PREGUNTA_INEXISTENTE);

            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA))
                    .path("pregunta")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(404, response.getStatus());
            assertNotNull(response.getHeaderString("Not-found"));
        }

        @Test
        @Order(3)
        void responde422CuandoEntityTieneId() {
            String body = """
                    { "id": 1, "idPregunta": { "id": "%s" } }
                    """.formatted(ID_PREGUNTA_NUEVA);

            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA))
                    .path("pregunta")
                    .request()
                    .post(Entity.json(body));

            assertEquals(422, response.getStatus());
        }

        @Test
        @Order(4)
        void respondeErrorCuandoBodyVacio() {
            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA))
                    .path("pregunta")
                    .request()
                    .post(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(response.getStatus() == 400 || response.getStatus() == 500);
        }

        @Test
        @Order(5)
        void responde500OErrorCuandoRelacionYaExiste() {
            String body = """
                    { "idPregunta": { "id": "%s" } }
                    """.formatted(ID_PREGUNTA);

            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA))
                    .path("pregunta")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertTrue(response.getStatus() == 500 || response.getStatus() == 409);
        }

        @Test
        @Order(6)
        void responde201CuandoCombinacionNueva() {
            String body = """
            { "idPregunta": { "id": "%s" }, "orden": 10 }
            """.formatted(ID_PREGUNTA_NUEVA);

            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA))
                    .path("pregunta")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(201, response.getStatus());
            assertNotNull(response.getHeaderString("Location"));
            assertTrue(response.hasEntity());
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
                    { "orden": 5 }
                    """;

            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA))
                    .path("pregunta")
                    .path(ID_PREGUNTA.toString())
                    .request()
                    .put(Entity.json(body));

            assertEquals(200, response.getStatus());
        }

        @Test
        @Order(2)
        void responde404CuandoNoExiste() {
            String body = """
                    { "orden": 5 }
                    """;

            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA))
                    .path("pregunta")
                    .path(ID_PREGUNTA_INEXISTENTE.toString())
                    .request()
                    .put(Entity.json(body));

            assertEquals(404, response.getStatus());
        }

        @Test
        @Order(3)
        void respondeErrorCuandoBodyVacio() {
            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA))
                    .path("pregunta")
                    .path(ID_PREGUNTA.toString())
                    .request()
                    .put(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(response.getStatus() == 400 || response.getStatus() == 500);
        }
    }

    @Nested
    @Order(5)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Delete {

        @Test
        @Order(1)
        void responde404CuandoNoExiste() {
            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA))
                    .path("pregunta")
                    .path(ID_PREGUNTA_INEXISTENTE.toString())
                    .request()
                    .delete();

            assertEquals(404, response.getStatus());
        }

        @Test
        @Order(2)
        void responde204CuandoExiste() {
            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA))
                    .path("pregunta")
                    .path(ID_PREGUNTA_NUEVA.toString())
                    .request()
                    .delete();

            assertEquals(204, response.getStatus());
        }
    }


}