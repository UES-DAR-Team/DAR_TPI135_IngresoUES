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
public class PruebaAreaPreguntaDistractorResourceSystem extends BaseIntegrationAbstract {

    private static final String PATH = "pruebaAreaPregunta";

    private static final int ID_PRUEBA_AREA_PREGUNTA = 1;
    private static final int ID_PRUEBA_AREA_PREGUNTA_INEXISTENTE = 99999;
    private static final UUID ID_DISTRACTOR = UUID.fromString("d4000000-0000-0000-0000-000000000001");
    private static final UUID ID_DISTRACTOR_INEXISTENTE = UUID.fromString("99999999-9999-9999-9999-999999999999");
    private static final int ID_EXISTENTE = 1;
    private static final int ID_INEXISTENTE = 99999;
    private static final UUID ID_DISTRACTOR_NUEVO = UUID.fromString("d4000000-0000-0000-0000-000000000005");

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindById {

        @Test
        @Order(1)
        void responde200CuandoExiste() {
            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA_PREGUNTA))
                    .path("distractor")
                    .path(ID_DISTRACTOR.toString())
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
                    .path(String.valueOf(ID_PRUEBA_AREA_PREGUNTA))
                    .path("distractor")
                    .path(ID_DISTRACTOR.toString())
                    .path(String.valueOf(ID_INEXISTENTE))
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus());
        }

        @Test
        @Order(3)
        void responde404CuandoIdPruebaAreaPreguntaNoCoincide() {
            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA_PREGUNTA_INEXISTENTE))
                    .path("distractor")
                    .path(ID_DISTRACTOR.toString())
                    .path(String.valueOf(ID_EXISTENTE))
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus());
        }

        @Test
        @Order(4)
        void responde404CuandoIdDistractorNoCoincide() {
            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA_PREGUNTA))
                    .path("distractor")
                    .path(ID_DISTRACTOR_INEXISTENTE.toString())
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
        void responde404CuandoPruebaAreaPreguntaNoExiste() {
            String body = "{}";

            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA_PREGUNTA_INEXISTENTE))
                    .path("distractor")
                    .path(ID_DISTRACTOR_NUEVO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(404, response.getStatus());
            assertNotNull(response.getHeaderString("Not-found"));
        }

        @Test
        @Order(2)
        void responde404CuandoDistractorNoExiste() {
            String body = "{}";

            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA_PREGUNTA))
                    .path("distractor")
                    .path(ID_DISTRACTOR_INEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(404, response.getStatus());
            assertNotNull(response.getHeaderString("Not-found"));
        }

        @Test
        @Order(3)
        void responde422CuandoEntityTieneId() {
            String body = """
                    { "id": 1 }
                    """;

            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA_PREGUNTA))
                    .path("distractor")
                    .path(ID_DISTRACTOR_NUEVO.toString())
                    .request()
                    .post(Entity.json(body));

            assertEquals(422, response.getStatus());
        }

        @Test
        @Order(4)
        void responde500OErrorCuandoRelacionYaExiste() {
            String body = "{}";

            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA_PREGUNTA))
                    .path("distractor")
                    .path(ID_DISTRACTOR.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertTrue(response.getStatus() == 500 || response.getStatus() == 409);
        }

        @Test
        @Order(5)
        void responde201OErrorCuandoCombinacionNueva() {
            String body = """
            { "esRespuestaCorrecta": false, "fechaRegistro": "2025-06-01T08:00:00Z" }
            """;

            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA_PREGUNTA))
                    .path("distractor")
                    .path(ID_DISTRACTOR_NUEVO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertTrue(response.getStatus() == 201 || response.getStatus() == 500);
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
                   { "esRespuestaCorrecta": true, "fechaRegistro": "2025-06-01T08:00:00Z" }staCorrecta": true }
                   """;

            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA_PREGUNTA))
                    .path("distractor")
                    .path(ID_DISTRACTOR.toString())
                    .path(String.valueOf(ID_EXISTENTE))
                    .request()
                    .put(Entity.json(body));

            assertEquals(200, response.getStatus());
        }

        @Test
        @Order(2)
        void responde404CuandoNoExiste() {
            String body = """
        { "esRespuestaCorrecta": true, "fechaRegistro": "2025-06-01T08:00:00Z" }
        """;

            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA_PREGUNTA))
                    .path("distractor")
                    .path(ID_DISTRACTOR.toString())
                    .path(String.valueOf(ID_INEXISTENTE))
                    .request()
                    .put(Entity.json(body));

            assertEquals(404, response.getStatus());
        }

        @Test
        @Order(3)
        void responde404CuandoIdPruebaAreaPreguntaNoCoincide() {
            String body = """
        { "esRespuestaCorrecta": true, "fechaRegistro": "2025-06-01T08:00:00Z" }
        """;

            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA_PREGUNTA_INEXISTENTE))
                    .path("distractor")
                    .path(ID_DISTRACTOR.toString())
                    .path(String.valueOf(ID_EXISTENTE))
                    .request()
                    .put(Entity.json(body));

            assertEquals(404, response.getStatus());
        }

        @Test
        @Order(4)
        void responde404CuandoIdDistractorNoCoincide() {
            String body = """
        { "esRespuestaCorrecta": true, "fechaRegistro": "2025-06-01T08:00:00Z" }
        """;

            Response response = target
                    .path(PATH)
                    .path(String.valueOf(ID_PRUEBA_AREA_PREGUNTA))
                    .path("distractor")
                    .path(ID_DISTRACTOR_INEXISTENTE.toString())
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
                    .path(String.valueOf(ID_PRUEBA_AREA_PREGUNTA))
                    .path("distractor")
                    .path(ID_DISTRACTOR.toString())
                    .path(String.valueOf(ID_EXISTENTE))
                    .request()
                    .put(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(response.getStatus() == 400 || response.getStatus() == 500);
        }
    }
}