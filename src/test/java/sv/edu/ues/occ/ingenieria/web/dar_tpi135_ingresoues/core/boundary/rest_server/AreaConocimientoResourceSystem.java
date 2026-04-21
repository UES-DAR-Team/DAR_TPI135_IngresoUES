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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SystemTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ContainerExtension.class)
public class AreaConocimientoResourceSystem extends BaseIntegrationAbstract {

    private static final String PATH = "areaConocimiento";

    private static UUID IDCREADO;
    private static final UUID IDINEXISTENTE= UUID.randomUUID();
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
                    .path(PATH)
                    .queryParam("first", FIRST)
                    .queryParam("max", MAX)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus());
            assertNotNull(response.getHeaderString("X-Total-Count"),
                    "El header X-Total-Count debe estar presente");
            assertTrue(response.hasEntity(),
                    "La respuesta debe contener un cuerpo JSON");
        }

        @Order(2)
        @Test
        void responde400_cuandoFirstYMaxInvalidos() {
            Response response = target
                    .path(PATH)
                    .queryParam("first", INVALIDFIRST)
                    .queryParam("max", INVALIDMAX)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(400, response.getStatus());
        }
    }

    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Create {

        @Order(1)
        @Test
        void responde201_cuandoEntidadValida() {
            String bodyJson = """
                    {
                        "nombre": "Prueba de sistema",
                        "fechaCreacion": "2025-01-01T00:00:00Z",
                        "activo": true
                    }
                    """;

            Response response = target
                    .path(PATH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyJson));

            assertEquals(201, response.getStatus(),
                    "Debe retornar 201 Created al crear un Area Conocimiento válido");
            assertNotNull(response.getHeaderString("Location"),
                    "El header Location debe contener la URI del nuevo recurso");

            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.contains("\"id\""), "El body debe contener el campo id");

            int idStart = body.indexOf("\"id\":\"") + 6;
            int idEnd = body.indexOf("\"", idStart);
            IDCREADO = UUID.fromString(body.substring(idStart, idEnd));
            assertNotNull(IDCREADO, "El UUID extraído del body no debe ser nulo");
        }

        @Order(3)
        @Test
        void respondeFallo_cuandoBodyJsonMalformado() {
            Response response = target
                    .path(PATH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(
                    response.getStatus() == 400 || response.getStatus() == 500,
                    "Debe retornar 400 o 500 cuando el body JSON está vacío o malformado, " +
                            "fue: " + response.getStatus()
            );
        }


        @Order(4)
        @Test
        void responde422_cuandoEntidadTieneIdPreasignado() {
            String bodyConId = """
                    {
                        "id": "00000000-0000-0000-0000-000000000001",
                        "nombre": "Prueba de sistema con padre inexistente",
                        "fechaCreacion": "2025-01-01T00:00:00Z",
                        "activo": true
                    }
                    """;

            Response response = target
                    .path(PATH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyConId));

            assertEquals(422, response.getStatus(),
                    "Debe retornar 422 cuando la entidad trae un id pre-asignado");
            assertNotNull(response.getHeaderString("Missing-parameter"),
                    "El header Missing-parameter debe estar presente");
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
                    .path(PATH)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando el id existe");
            assertTrue(response.hasEntity(),
                    "La respuesta debe contener el Area Conocimiento encontrado");
        }

        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            Response response = target
                    .path(PATH)
                    .path(IDINEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando el id no existe");
            assertNotNull(response.getHeaderString("Not-found-id"),
                    "El header Not-found-id debe estar presente");
        }
    }

    @Nested
    @Order(4)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Update {

        @Order(1)
        @Test
        void responde200_cuandoIdExisteYEntidadValida() {
            String bodyActualizado = """
                    {
                        "nombre": "Prueba de sistema Actualizado",
                        "fechaCreacion": "2025-01-01T00:00:00Z",
                        "activo": true
                    }
                    """;

            Response response = target
                    .path(PATH)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(bodyActualizado));

            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando el id existe y la entidad es válida");
            assertTrue(response.hasEntity(),
                    "La respuesta debe contener el Area Conocimiento actualizado");
        }

        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            String body = """
                    {
                        "nombre": "Prueba de sistema No debe actualizarse",
                        "fechaCreacion": "2025-01-01T00:00:00Z",
                        "activo": true
                    }
                    """;

            Response response = target
                    .path(PATH)
                    .path(IDINEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(body));

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando el id no existe");
            assertNotNull(response.getHeaderString("Not-found-id"),
                    "El header Not-found-id debe estar presente");
        }

        @Order(3)
        @Test
        void respondeFallo_cuandoBodyJsonMalformado() {
            Response response = target
                    .path(PATH)
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
    @Order(5)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class DeleteById {

        @Order(1)
        @Test
        void responde404_cuandoIdNoExiste() {
            Response response = target
                    .path(PATH)
                    .path(IDINEXISTENTE.toString())
                    .request()
                    .delete();

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando el id no existe");
            assertNotNull(response.getHeaderString("Not-found-id"),
                    "El header Not-found-id debe estar presente");
        }

        @Order(2)
        @Test
        void responde204_cuandoIdExiste() {
            Response response = target
                    .path(PATH)
                    .path(IDCREADO.toString())
                    .request()
                    .delete();

            assertEquals(204, response.getStatus(),
                    "Debe retornar 204 No Content al eliminar un registro existente");
            assertFalse(response.hasEntity(),
                    "La respuesta no debe contener cuerpo tras eliminar");
        }

        @Order(3)
        @Test
        void responde404_cuandoSeIntentaAccederAlRegistroEliminado() {

            Response response = target
                    .path(PATH)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 al intentar acceder a un registro ya eliminado");
        }


    }

}