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
public class PreguntaAreaConocimientoResourceSystem extends BaseIntegrationAbstract {

    private static final String PATH_AREA_CONOCIMIENTO = "areaConocimiento";
    private static final String PATH_PREGUNTA = "pregunta";

    private static final UUID ID_AREA_SEED = UUID.fromString("a1000000-0000-0000-0000-000000000001");

    private static final UUID ID_PREGUNTA_SEED = UUID.fromString("c3000000-0000-0000-0000-000000000009");

    private static final UUID IDINEXISTENTE = UUID.randomUUID();
    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindRange {

        @Order(1)
        @Test
        void responde200_cuandoParametrosValidos() {
            Response response = target
                    .path(PATH_AREA_CONOCIMIENTO)
                    .path(ID_AREA_SEED.toString())
                    .path(PATH_PREGUNTA)
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
        void responde400_cuandoFirstYMaxInvalidos() {
            Response response = target
                    .path(PATH_AREA_CONOCIMIENTO)
                    .path(ID_AREA_SEED.toString())
                    .path(PATH_PREGUNTA)
                    .queryParam("first", INVALIDFIRST)
                    .queryParam("max", INVALIDMAX)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(400, response.getStatus());
        }

        @Order(3)
        @Test
        void responde404_cuandoAreaConocimientoNoExiste() {
            Response response = target
                    .path(PATH_AREA_CONOCIMIENTO)
                    .path(IDINEXISTENTE.toString())
                    .path(PATH_PREGUNTA)
                    .queryParam("first", FIRST)
                    .queryParam("max", MAX)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus(), "Debe retornar 404 cuando el área padre no existe");
            assertNotNull(response.getHeaderString("Not-found"), "El header Not-found debe estar presente");
        }
    }

    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Create {

        @Order(1)
        @Test
        void responde201_cuandoEntidadValida() {
            String bodyAsoc = String.format(
                    "{\n  \"idPregunta\": { \"id\": \"%s\" }\n}",
                    ID_PREGUNTA_SEED
            );

            Response response = target
                    .path(PATH_AREA_CONOCIMIENTO)
                    .path(ID_AREA_SEED.toString())
                    .path(PATH_PREGUNTA)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyAsoc));

            assertEquals(201, response.getStatus(), "Debe retornar 201 Created al crear la asociación");
            assertNotNull(response.getHeaderString("Location"), "El header Location debe contener la URI del nuevo recurso");
        }

        @Order(2)
        @Test
        void respondeFallo_cuandoBodyJsonMalformado() {
            Response response = target
                    .path(PATH_AREA_CONOCIMIENTO)
                    .path(ID_AREA_SEED.toString())
                    .path(PATH_PREGUNTA)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(
                    response.getStatus() == 400 || response.getStatus() == 500,
                    "Debe retornar 400 o 500 cuando el body JSON está vacío o malformado, " +
                            "fue: " + response.getStatus()
            );
        }

        @Order(3)
        @Test
        void responde422_cuandoEntidadTieneIdPreasignado() {
            String bodyConId = String.format(
                    "{\n  \"id\": 999,\n  \"idPregunta\": { \"id\": \"%s\" }\n}",
                    ID_PREGUNTA_SEED
            );

            Response response = target
                    .path(PATH_AREA_CONOCIMIENTO)
                    .path(ID_AREA_SEED.toString())
                    .path(PATH_PREGUNTA)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyConId));

            assertEquals(422, response.getStatus(), "Debe retornar 422 cuando la entidad trae un id pre-asignado");
            assertNotNull(response.getHeaderString("Missing-parameter"));
        }

        @Order(4)
        @Test
        void responde422_cuandoIdPreguntaEsNulo() {
            Response response = target
                    .path(PATH_AREA_CONOCIMIENTO)
                    .path(ID_AREA_SEED.toString())
                    .path(PATH_PREGUNTA)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json("{}"));

            assertEquals(422, response.getStatus(), "Debe retornar 422 cuando idPregunta no se provee en el body");
            assertNotNull(response.getHeaderString("Missing-parameter"));
        }

        @Order(5)
        @Test
        void responde404_cuandoAreaConocimientoNoExiste() {
            String bodyAsoc = String.format(
                    "{\n  \"idPregunta\": { \"id\": \"%s\" }\n}",
                    UUID.randomUUID()
            );

            Response response = target
                    .path(PATH_AREA_CONOCIMIENTO)
                    .path(IDINEXISTENTE.toString())
                    .path(PATH_PREGUNTA)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyAsoc));

            assertEquals(404, response.getStatus(), "Debe retornar 404 cuando el área padre no existe");
            assertNotNull(response.getHeaderString("Not-found"));
        }
    }

    @Nested
    @Order(3)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindById {

        @Order(1)
        @Test
        void responde200_cuandoIdExiste() {
            Response response = target
                    .path(PATH_AREA_CONOCIMIENTO)
                    .path(ID_AREA_SEED.toString())
                    .path(PATH_PREGUNTA)
                    .path(ID_PREGUNTA_SEED.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus(), "Debe retornar 200 cuando el idPregunta existe y está asociado al área");
            assertTrue(response.hasEntity(), "La respuesta debe contener la asociación encontrada");
        }

        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            Response response = target
                    .path(PATH_AREA_CONOCIMIENTO)
                    .path(ID_AREA_SEED.toString())
                    .path(PATH_PREGUNTA)
                    .path(IDINEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus(), "Debe retornar 404 cuando el idPregunta no está asociado al área");
            assertNotNull(response.getHeaderString("Not-found-id"));
        }
    }

    @Nested
    @Order(4)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class DeleteById {

        @Order(1)
        @Test
        void responde404_cuandoIdNoExiste() {
            Response response = target
                    .path(PATH_AREA_CONOCIMIENTO)
                    .path(ID_AREA_SEED.toString())
                    .path(PATH_PREGUNTA)
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
                    .path(PATH_AREA_CONOCIMIENTO)
                    .path(ID_AREA_SEED.toString())
                    .path(PATH_PREGUNTA)
                    .path(ID_PREGUNTA_SEED.toString())
                    .request()
                    .delete();

            assertEquals(204, response.getStatus(), "Debe retornar 204 No Content al eliminar la asociación");
            assertFalse(response.hasEntity(), "La respuesta no debe contener cuerpo tras eliminar");
        }

        @Order(3)
        @Test
        void responde404_cuandoSeIntentaAccederAlRegistroEliminado() {
            Response response = target
                    .path(PATH_AREA_CONOCIMIENTO)
                    .path(ID_AREA_SEED.toString())
                    .path(PATH_PREGUNTA)
                    .path(ID_PREGUNTA_SEED.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus(), "Debe retornar 404 al intentar acceder a un registro ya eliminado");
        }
    }
}