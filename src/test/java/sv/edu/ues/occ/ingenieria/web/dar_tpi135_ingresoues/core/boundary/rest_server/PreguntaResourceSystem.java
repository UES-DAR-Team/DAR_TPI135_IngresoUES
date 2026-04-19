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
public class PreguntaResourceSystem extends BaseIntegrationAbstract {

    private static final String PATH = "pregunta";

    private static UUID IDCREADO;
    private static final UUID IDINEXISTENTE= UUID.randomUUID();
    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final int EXCEEDMAX = 11;

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindRange{


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
        void responde400_cuandoFirstNegativo(){
            Response response = target
                    .path(PATH)
                    .queryParam("first", INVALIDFIRST)
                    .queryParam("max", MAX)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(400, response.getStatus());
        }

        @Order(3)
        @Test
        void responde400_cuandoMaxInvalido(){
            Response response = target
                    .path(PATH)
                    .queryParam("first", FIRST)
                    .queryParam("max", INVALIDMAX)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(400, response.getStatus());
        }
        @Order(4)
        @Test
        void responde400_cuandoMaxExcedeLimite() {
            Response response = target
                    .path(PATH)
                    .queryParam("first", FIRST)
                    .queryParam("max", EXCEEDMAX)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(400, response.getStatus());
        }
    }

    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Create{

        @Order(1)
        @Test
        void responde201_cuandoEntidadValida(){
            String bodyJson = """
                    {
                        "contenidoPregunta": "¿Cuál es la capital de Francia?",
                        "fechaCreacion": "2024-06-01T12:00:00Z",
                        "activo": true
                    }
                    """;
            Response response = target
                    .path(PATH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyJson));

            assertEquals(201, response.getStatus(),
                    "Debe retornar 201 Created para una entidad válida");
            assertTrue(response.hasEntity(),
                    "La respuesta debe contener un cuerpo JSON");

            String body =  response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.contains("\"id\""), "El body debe contener el campo id");

            int idStart = body.indexOf("\"id\"")+6;
            int idEnd = body.indexOf("\"", idStart);
            IDCREADO = UUID.fromString(body.substring(idStart, idEnd));
            assertNotNull(IDCREADO, "El UUID extraido del body no debe ser nulo");
        }

        @Order(2)
        @Test
        void respondeFallo_cuandoBodyJsonMalFormado(){
            Response response = target
                    .path(PATH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity("", MediaType.APPLICATION_JSON));
            assertTrue(
                    response.getStatus()==400 || response.getStatus()==500,
                    "Debe retornar 400 Bad Request o 500 Internal Server Error para un JSON mal formado"+
                            "fue: "+response.getStatus()
            );
        }

    }

    @Nested
    @Order(3)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindById{

        @Order(1)
        @Test
        void responde200_cuandoIdExiste(){
            Response response = target
                    .path(PATH)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 OK para un ID existente");
            assertTrue(response.hasEntity(),
                    "La respuesta debe contener un cuerpo JSON");
        }

        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste(){
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
    class Update{

        @Order(1)
        @Test
        void responde200_cuandoIdExisteYEntidadValida(){
            String bodyActualizado = """
                    {
                        "contenidoPregunta": "¿Cuál es la capital de España?",
                        "fechaCreacion": "2024-06-01T12:00:00Z",
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
                    "La respuesta debe la pregunta actualizado");
        }

        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste(){

            String body = """
                    {
                        "contenidoPregunta": "no debe actualizarse",
                        "fechaCreacion": "2024-06-01T12:00:00Z",
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
        void respondeFallo_cuandoBodyJsonMalFormado(){
            Response response = target
                    .path(PATH)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity("", MediaType.APPLICATION_JSON));
            assertTrue(
                    response.getStatus()==400 || response.getStatus()==500,
                    "Debe retornar 400 o 500 cuando el body JSON está vacío o malformado, "+
                            "fue: "+response.getStatus()
            );
        }

    }

    @Nested
    @Order(5)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class DeleteById{

        @Order(1)
        @Test
        void responde404_cuandoIdNoExiste(){
            Response response = target
                    .path(PATH)
                    .path(IDINEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .delete();

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando el id no existe");
            assertNotNull(response.getHeaderString("Not-found-id"),
                    "El header Not-found-id debe estar presente");

        }

        @Order(2)
        @Test
        void responde204_cuandoIdExiste(){
            Response response = target
                    .path(PATH)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .delete();

            assertEquals(204, response.getStatus(),
                    "Debe retornar 204 No Content al eliminar un registro existente");
            assertFalse(response.hasEntity(),
                    "La respuesta no debe contener un cuerpo tras eliminar");
        }

        @Order(3)
        @Test
        void responde404_cuandoSeIntentaAccederAlRefistroEliminado(){
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