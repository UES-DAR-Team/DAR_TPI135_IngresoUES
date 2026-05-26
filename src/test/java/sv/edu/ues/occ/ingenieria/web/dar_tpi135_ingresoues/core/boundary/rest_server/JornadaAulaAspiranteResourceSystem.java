package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;
import testing.SystemTest;

import static org.junit.jupiter.api.Assertions.*;

@SystemTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ContainerExtension.class)
public class JornadaAulaAspiranteResourceSystem extends BaseIntegrationAbstract {

    private static final Integer ID_JORNADA_AULA_EXISTENTE     = 3;
    private static final Integer ID_ASPIRANTE_PRUEBA_EXISTENTE = 1;
    private static final Integer ID_JORNADA_AULA_INEXISTENTE   = Integer.MAX_VALUE;

    private static Integer IDCREADO;
    private static final Integer ID_INEXISTENTE = Integer.MAX_VALUE;

    private static final String PATH_JORNADA_AULA     = "jornadaAula";
    private static final String PATH_ASPIRANTE_PRUEBA = "aspirantePrueba";

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindRange {

        @Order(1)
        @Test
        void responde200_cuandoJornadaAulaExiste() {
            Response response = target
                    .path(PATH_JORNADA_AULA)
                    .path(ID_JORNADA_AULA_EXISTENTE.toString())
                    .path(PATH_ASPIRANTE_PRUEBA)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando la jornadaAula existe");

            assertNotNull(response.getHeaderString("Total-records"),
                    "El header Total-records debe estar presente");

            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.startsWith("["),
                    "El body debe ser un array JSON");
        }

        @Order(2)
        @Test
        void responde404_cuandoJornadaAulaNoExiste() {
            Response response = target
                    .path(PATH_JORNADA_AULA)
                    .path(ID_JORNADA_AULA_INEXISTENTE.toString())
                    .path(PATH_ASPIRANTE_PRUEBA)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando la jornadaAula no existe");

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
                        "idAspirantePrueba": { "id": %d },
                        "horaLlegada": "06:45:00",
                        "asistio": true,
                        "fechaAsignacion": "2025-05-10T00:00:00Z"
                    }
                    """.formatted(ID_ASPIRANTE_PRUEBA_EXISTENTE);

            Response response = target
                    .path(PATH_JORNADA_AULA)
                    .path(ID_JORNADA_AULA_EXISTENTE.toString())
                    .path(PATH_ASPIRANTE_PRUEBA)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyJson));

            assertEquals(201, response.getStatus(),
                    "Debe retornar 201 Created al crear un JornadaAulaAspirante válido");

            // Assert — headers
            assertNotNull(response.getHeaderString("Location"),
                    "El header Location debe estar presente");

            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.contains("\"id\""),
                    "El body debe contener el campo id generado por el servidor");
            assertTrue(body.contains("true"),
                    "El body debe contener el valor asistio enviado");

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
                    .path(PATH_JORNADA_AULA)
                    .path(ID_JORNADA_AULA_EXISTENTE.toString())
                    .path(PATH_ASPIRANTE_PRUEBA)
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
                        "idAspirantePrueba": { "id": %d },
                        "asistio": false,
                        "fechaAsignacion": "2025-05-10T00:00:00Z"
                    }
                    """.formatted(ID_ASPIRANTE_PRUEBA_EXISTENTE);

            Response response = target
                    .path(PATH_JORNADA_AULA)
                    .path(ID_JORNADA_AULA_EXISTENTE.toString())
                    .path(PATH_ASPIRANTE_PRUEBA)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyConId));

            assertEquals(422, response.getStatus(),
                    "Debe retornar 422 cuando la entidad trae un id pre-asignado");

            assertNotNull(response.getHeaderString("Missing-parameter"),
                    "El header Missing-parameter debe estar presente");
        }

        @Order(4)
        @Test
        void responde404_cuandoJornadaAulaNoExiste() {
            String body = """
                    {
                        "idAspirantePrueba": { "id": %d },
                        "asistio": false,
                        "fechaAsignacion": "2025-05-10T00:00:00Z"
                    }
                    """.formatted(ID_ASPIRANTE_PRUEBA_EXISTENTE);

            Response response = target
                    .path(PATH_JORNADA_AULA)
                    .path(ID_JORNADA_AULA_INEXISTENTE.toString())
                    .path(PATH_ASPIRANTE_PRUEBA)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando la JornadaAula no existe");

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
                    .path(PATH_JORNADA_AULA)
                    .path(ID_JORNADA_AULA_EXISTENTE.toString())
                    .path(PATH_ASPIRANTE_PRUEBA)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando el id existe");

            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.contains(String.valueOf(IDCREADO)),
                    "El body debe contener el id consultado");
        }

        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            Response response = target
                    .path(PATH_JORNADA_AULA)
                    .path(ID_JORNADA_AULA_EXISTENTE.toString())
                    .path(PATH_ASPIRANTE_PRUEBA)
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
                        "horaLlegada": "07:00:00",
                        "asistio": true,
                        "fechaAsignacion": "2025-05-10T00:00:00Z"
                    }
                    """;

            Response response = target
                    .path(PATH_JORNADA_AULA)
                    .path(ID_JORNADA_AULA_EXISTENTE.toString())
                    .path(PATH_ASPIRANTE_PRUEBA)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(bodyActualizado));

            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando el id existe y la entidad es válida");

            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.contains(String.valueOf(IDCREADO)),
                    "El body debe contener el id del registro actualizado");
            assertTrue(body.contains("true"),
                    "El body debe reflejar el campo asistio actualizado");
        }

        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            String body = """
                    {
                        "asistio": false,
                        "fechaAsignacion": "2025-05-10T00:00:00Z"
                    }
                    """;

            Response response = target
                    .path(PATH_JORNADA_AULA)
                    .path(ID_JORNADA_AULA_EXISTENTE.toString())
                    .path(PATH_ASPIRANTE_PRUEBA)
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
                    .path(PATH_JORNADA_AULA)
                    .path(ID_JORNADA_AULA_EXISTENTE.toString())
                    .path(PATH_ASPIRANTE_PRUEBA)
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