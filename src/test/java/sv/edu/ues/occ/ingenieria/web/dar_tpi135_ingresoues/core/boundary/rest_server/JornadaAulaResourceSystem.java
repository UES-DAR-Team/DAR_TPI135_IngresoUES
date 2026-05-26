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
public class JornadaAulaResourceSystem extends BaseIntegrationAbstract {

    private static final UUID ID_JORNADA_EXISTENTE =
            UUID.fromString("e5000000-0000-0000-0000-000000000001");
    private static final UUID ID_AULA_EXISTENTE =
            UUID.fromString("f6000000-0000-0000-0000-000000000003");
    private static final UUID ID_JORNADA_INEXISTENTE = UUID.randomUUID();
    private static final UUID ID_AULA_INEXISTENTE    = UUID.randomUUID();

    private static Integer IDCREADO;
    private static final Integer ID_INEXISTENTE = Integer.MAX_VALUE;

    private static final String PATH_JORNADA = "jornada";
    private static final String PATH_AULA    = "aula";

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindRange {

        @Order(1)
        @Test
        void responde200_cuandoJornadaExiste() {
            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando la jornada existe");

            assertNotNull(response.getHeaderString("Total-records"),
                    "El header Total-records debe estar presente");

            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.startsWith("["),
                    "El body debe ser un array JSON");
        }

        @Order(2)
        @Test
        void responde404_cuandoJornadaNoExiste() {
            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_INEXISTENTE.toString())
                    .path(PATH_AULA)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            // Assert — status
            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando la jornada no existe");

            // Assert — headers
            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
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
                        "idAula": { "id": "%s" },
                        "fechaAsignacion": "2025-01-01T00:00:00Z"
                    }
                    """.formatted(ID_AULA_EXISTENTE);

            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyJson));

            assertEquals(201, response.getStatus(),
                    "Debe retornar 201 Created al crear un JornadaAula válido");

            assertNotNull(response.getHeaderString("Location"),
                    "El header Location debe estar presente");

            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.contains("\"id\""),
                    "El body debe contener el campo id generado por el servidor");
            assertTrue(body.contains(ID_JORNADA_EXISTENTE.toString()),
                    "El body debe contener el UUID de la jornada asignada");

            int idStart = body.indexOf("\"id\":") + 5;
            int idEnd   = body.indexOf(",", idStart);
            if (idEnd == -1) idEnd = body.indexOf("}", idStart);
            IDCREADO = Integer.parseInt(body.substring(idStart, idEnd).trim());
            assertNotNull(IDCREADO, "El id extraído del body no debe ser nulo");
        }

        @Order(2)
        @Test
        void respondeFallo_cuandoBodyJsonMalformado() {
            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(
                    response.getStatus() == 400 || response.getStatus() == 500,
                    "Debe retornar 400 o 500 cuando el body JSON está vacío, " +
                            "fue: " + response.getStatus()
            );
        }

        @Order(3)
        @Test
        void responde422_cuandoEntidadTieneIdPreasignado() {
            String bodyConId = """
                    {
                        "id": 99999,
                        "idAula": { "id": "%s" },
                        "fechaAsignacion": "2025-01-01T00:00:00Z"
                    }
                    """.formatted(ID_AULA_EXISTENTE);

            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyConId));

            assertEquals(422, response.getStatus(),
                    "Debe retornar 422 cuando la entidad trae un id pre-asignado");

            assertNotNull(response.getHeaderString("Missing-parameter"),
                    "El header Missing-parameter debe estar presente");
        }

        @Order(4)
        @Test
        void responde404_cuandoJornadaNoExiste() {
            String body = """
                    {
                        "idAula": { "id": "%s" },
                        "fechaAsignacion": "2025-01-01T00:00:00Z"
                    }
                    """.formatted(ID_AULA_EXISTENTE);

            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_INEXISTENTE.toString())
                    .path(PATH_AULA)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando la jornada no existe");

            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
        }

        @Order(5)
        @Test
        void responde404_cuandoAulaNoExiste() {
            String body = """
                    {
                        "idAula": { "id": "%s" },
                        "fechaAsignacion": "2025-01-01T00:00:00Z"
                    }
                    """.formatted(ID_AULA_INEXISTENTE);

            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando el aula no existe");

            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
        }
    }

    @Nested
    @Order(3)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindById {

        @Order(1)
        @Test
        void responde200_cuandoIdExiste() {
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando el id existe");

            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.contains(String.valueOf(IDCREADO)),
                    "El body debe contener el id consultado");
            assertTrue(body.contains(ID_JORNADA_EXISTENTE.toString()),
                    "El body debe contener el UUID de la jornada");
        }

        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_INEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando el id no existe");

            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
        }
    }

    @Nested
    @Order(4)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Update {

        @Order(1)
        @Test
        void responde200_cuandoIdExisteYEntidadValida() {
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            String bodyActualizado = """
                    {
                        "fechaAsignacion": "2025-06-01T00:00:00Z"
                    }
                    """;

            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(bodyActualizado));

            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando el id existe y la entidad es válida");

            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.contains(String.valueOf(IDCREADO)),
                    "El body debe contener el id del registro actualizado");
            assertTrue(body.contains(ID_JORNADA_EXISTENTE.toString()),
                    "El body debe conservar el UUID de la jornada");
        }

        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            String body = """
                    {
                        "fechaAsignacion": "2025-01-01T00:00:00Z"
                    }
                    """;

            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_INEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(body));

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando el id no existe");

            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
        }

        @Order(3)
        @Test
        void respondeFallo_cuandoBodyJsonMalformado() {
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(
                    response.getStatus() == 400 || response.getStatus() == 500,
                    "Debe retornar 400 o 500 cuando el body JSON está vacío, " +
                            "fue: " + response.getStatus()
            );
        }
    }
}