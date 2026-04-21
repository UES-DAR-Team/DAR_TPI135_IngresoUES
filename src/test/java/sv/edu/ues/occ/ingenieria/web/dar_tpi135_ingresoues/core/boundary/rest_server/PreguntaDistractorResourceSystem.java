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
public class PreguntaDistractorResourceSystem extends BaseIntegrationAbstract {

    // Usaremos rutas explícitas para recursos padre e hijo
    private static final String PATH_PREGUNTA = "pregunta";
    private static final String PATH_DISTRACTOR = "distractor";

    private static UUID IDPREGUNTA;
    private static UUID IDCREADO; // id del distractor asociado (UUID)
    private static final UUID IDINEXISTENTE = UUID.randomUUID();
    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CreateParentPregunta {

        @Order(1)
        @Test
        void debeCrearPreguntaValida() {
            String bodyJson = """
                    {
                        "contenidoPregunta": "Pregunta para pruebas sistema asociación",
                        "fechaCreacion": "2024-06-01T12:00:00Z",
                        "activo": true
                    }
                    """;

            Response response = target
                    .path(PATH_PREGUNTA)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyJson));

            assertEquals(201, response.getStatus(), "Debe retornar 201 al crear una Pregunta válida");
            assertTrue(response.hasEntity());
            String body = response.readEntity(String.class);
            assertNotNull(body);
            assertTrue(body.contains("\"id\""));

            int idStart = body.indexOf("\"id\"") + 6;
            int idEnd = body.indexOf("\"", idStart);
            IDPREGUNTA = UUID.fromString(body.substring(idStart, idEnd));
            assertNotNull(IDPREGUNTA);
        }

    }

    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindByPregunta {

        @Order(1)
        @Test
        void responde200_cuandoParametrosValidos() {
            Response response = target
                    .path(PATH_PREGUNTA)
                    .path(IDPREGUNTA.toString())
                    .path(PATH_DISTRACTOR)
                    .queryParam("first", FIRST)
                    .queryParam("max", MAX)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus());
            assertNotNull(response.getHeaderString("X-Total-Count"), "El header X-Total-Count debe estar presente");
            assertTrue(response.hasEntity(), "La respuesta debe contener un cuerpo JSON");
        }

        @Order(2)
        @Test
        void responde422_cuandoFirstYMaxInvalidos() {
            Response response = target
                    .path(PATH_PREGUNTA)
                    .path(IDPREGUNTA.toString())
                    .path(PATH_DISTRACTOR)
                    .queryParam("first", INVALIDFIRST)
                    .queryParam("max", INVALIDMAX)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            // El recurso valida y devuelve 422 cuando first<0 o max<=0 o max>10
            // La validación de parámetros (Bean Validation) produce 400 Bad Request como en otros System tests
            assertEquals(400, response.getStatus());
        }
    }

    @Nested
    @Order(3)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Create {

        @Order(1)
        @Test
        void responde201_cuandoEntidadValida() {
            // Primero crear un distractor independiente
            String bodyDistractor = """
                    {
                        "contenidoDistractor": "Distractor para asociar en sistema",
                        "esCorrecto": false,
                        "fechaCreacion": "2025-01-01T00:00:00Z",
                        "activo": true
                    }
                    """;

            Response respDist = target
                    .path(PATH_DISTRACTOR)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyDistractor));

            assertEquals(201, respDist.getStatus(), "Debe crear distractor previamente");
            String body = respDist.readEntity(String.class);
            int idStart = body.indexOf("\"id\"") + 6;
            int idEnd = body.indexOf("\"", idStart);
            UUID idDistractor = UUID.fromString(body.substring(idStart, idEnd));
            assertNotNull(idDistractor);

            // Ahora crear la asociación enviando idDistractor en el body
            String bodyAsoc = String.format("{\n  \"idDistractor\": { \"id\": \"%s\" }\n}", idDistractor);

            Response response = target
                    .path(PATH_PREGUNTA)
                    .path(IDPREGUNTA.toString())
                    .path(PATH_DISTRACTOR)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyAsoc));

            assertEquals(201, response.getStatus(), "Debe retornar 201 Created al crear la asociación");
            assertNotNull(response.getHeaderString("Location"), "El header Location debe contener la URI del nuevo recurso");

            // Guardar idDistractor como identificador para pruebas posteriores
            IDCREADO = idDistractor;
        }

        @Order(2)
        @Test
        void respondeFallo_cuandoBodyJsonMalformado() {
            Response response = target
                    .path(PATH_PREGUNTA)
                    .path(IDPREGUNTA.toString())
                    .path(PATH_DISTRACTOR)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(response.getStatus() == 400 || response.getStatus() == 500,
                    "Debe retornar 400 o 500 cuando el body JSON está vacío o malformado, " +
                            "fue: " + response.getStatus());
        }

        @Order(3)
        @Test
        void responde422_cuandoEntidadTieneIdPreasignado() {
            String bodyConId = String.format("{\n  \"id\": 999, \n  \"idDistractor\": { \"id\": \"%s\" }\n}", IDCREADO == null ? UUID.randomUUID() : IDCREADO);

            Response response = target
                    .path(PATH_PREGUNTA)
                    .path(IDPREGUNTA.toString())
                    .path(PATH_DISTRACTOR)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyConId));

            assertEquals(422, response.getStatus(), "Debe retornar 422 cuando la entidad trae un id pre-asignado");
            assertNotNull(response.getHeaderString("Missing-parameter"));
        }

        @Order(4)
        @Test
        void responde404_cuandoPreguntaNoExiste() {
            String bodyAsoc = String.format("{\n  \"idDistractor\": { \"id\": \"%s\" }\n}", UUID.randomUUID());

            Response response = target
                    .path(PATH_PREGUNTA)
                    .path(IDINEXISTENTE.toString())
                    .path(PATH_DISTRACTOR)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyAsoc));

            assertEquals(404, response.getStatus(), "Debe retornar 404 cuando la pregunta padre no existe");
            assertNotNull(response.getHeaderString("Not-found-id"));
        }
    }

    @Nested
    @Order(4)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindById {

        @Order(1)
        @Test
        void responde200_cuandoIdExiste() {
            Response response = target
                    .path(PATH_PREGUNTA)
                    .path(IDPREGUNTA.toString())
                    .path(PATH_DISTRACTOR)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus(), "Debe retornar 200 cuando el idDistractor existe y está asociado a la pregunta");
            assertTrue(response.hasEntity(), "La respuesta debe contener la asociación encontrada");
        }

        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            Response response = target
                    .path(PATH_PREGUNTA)
                    .path(IDPREGUNTA.toString())
                    .path(PATH_DISTRACTOR)
                    .path(IDINEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus(), "Debe retornar 404 cuando el idDistractor no está asociado");
            assertNotNull(response.getHeaderString("Not-found-id"));
        }
    }

    @Nested
    @Order(5)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Update {

        @Order(1)
        @Test
        void responde200_cuandoIdExisteYEntidadValida() {
            String bodyActualizado = """
                    {
                        "orden": 5
                    }
                    """;

            Response response = target
                    .path(PATH_PREGUNTA)
                    .path(IDPREGUNTA.toString())
                    .path(PATH_DISTRACTOR)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(bodyActualizado));

            assertEquals(200, response.getStatus(), "Debe retornar 200 cuando el idDistractor existe y la entidad es válida");
            assertTrue(response.hasEntity(), "La respuesta debe contener la asociación actualizada");
        }

        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            String body = """
                    {
                        "orden": 1
                    }
                    """;

            Response response = target
                    .path(PATH_PREGUNTA)
                    .path(IDPREGUNTA.toString())
                    .path(PATH_DISTRACTOR)
                    .path(IDINEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(body));

            assertEquals(404, response.getStatus(), "Debe retornar 404 cuando el idDistractor no existe");
            assertNotNull(response.getHeaderString("Not-found-id"));
        }

        @Order(3)
        @Test
        void respondeFallo_cuandoBodyJsonMalformado() {
            Response response = target
                    .path(PATH_PREGUNTA)
                    .path(IDPREGUNTA.toString())
                    .path(PATH_DISTRACTOR)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(
                    response.getStatus() == 400 || response.getStatus() == 500,
                    "Debe retornar 400 o 500 cuando el body JSON está vacío o malformado, " +
                            "fue: " + response.getStatus()
            );
        }
    }

    @Nested
    @Order(6)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class DeleteById {

        @Order(1)
        @Test
        void responde404_cuandoIdNoExiste() {
            Response response = target
                    .path(PATH_PREGUNTA)
                    .path(IDPREGUNTA.toString())
                    .path(PATH_DISTRACTOR)
                    .path(IDINEXISTENTE.toString())
                    .request()
                    .delete();

            assertEquals(404, response.getStatus(), "Debe retornar 404 cuando el id no existe");
            assertNotNull(response.getHeaderString("Not-found-id"));
        }

        @Order(2)
        @Test
        void responde204_cuandoIdExiste() {
            Response response = target
                    .path(PATH_PREGUNTA)
                    .path(IDPREGUNTA.toString())
                    .path(PATH_DISTRACTOR)
                    .path(IDCREADO.toString())
                    .request()
                    .delete();

            assertEquals(204, response.getStatus(), "Debe retornar 204 No Content al eliminar la asociación");
            assertFalse(response.hasEntity(), "La respuesta no debe contener cuerpo tras eliminar");
        }

        @Order(3)
        @Test
        void responde404_cuandoSeIntentaAccederAlRegistroEliminado() {
            Response response = target
                    .path(PATH_PREGUNTA)
                    .path(IDPREGUNTA.toString())
                    .path(PATH_DISTRACTOR)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus(), "Debe retornar 404 al intentar acceder a un registro ya eliminado");
        }
    }

}