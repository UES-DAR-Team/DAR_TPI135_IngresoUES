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

/**
 * Pruebas de sistema para {@link JornadaAulaAspiranteResource}.
 *
 * <p>Levantan un entorno real con Docker (PostgreSQL + OpenLiberty) y verifican
 * el comportamiento del API REST de extremo a extremo.
 *
 * <p>Convenciones:
 * <ul>
 *   <li>Un {@code @Nested} por endpoint, ordenado con {@code @TestClassOrder}.</li>
 *   <li>Nombre de método: {@code respondeXXX_cuandoCondicion}.</li>
 *   <li>Patrón AAA (Arrange / Act / Assert) en cada test.</li>
 *   <li>Cada Assert verifica: status → headers → body.</li>
 *   <li>{@code IDCREADO} se puebla en {@code Create} y se reutiliza en
 *       {@code FindById} y {@code Update}.</li>
 * </ul>
 *
 * <p>Particularidades de este resource:
 * <ul>
 *   <li>Path: {@code jornadaAula/{idJornadaAula}/aspirantePrueba/{idAspirantePrueba}}.</li>
 *   <li>El {@code id} es {@code Integer} asignado por PostgreSQL via {@code serial}
 *       ({@code @GeneratedValue(IDENTITY)}) — se extrae del body del {@code 201}.</li>
 *   <li>{@code fechaAsignacion} es {@code @NotNull} — debe incluirse en el body JSON.</li>
 *   <li>{@code asistio} tiene default {@code false} en Java — puede omitirse.</li>
 *   <li>No tiene endpoints {@code FindRange} ni {@code DeleteById}.</li>
 * </ul>
 *
 * @see JornadaAulaAspiranteResource
 * @see BaseIntegrationAbstract
 */
@SystemTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ContainerExtension.class)
public class JornadaAulaAspiranteResourceSystem extends BaseIntegrationAbstract {

    // --- Arrange General ---

    /**
     * IDs de JornadaAula y AspirantePrueba existentes en BD.
     * La combinación jornadaAula=3 + aspirantePrueba=1 NO viola la restricción UNIQUE
     * si no existe ya en los datos iniciales del script.
     */
    private static final Integer ID_JORNADA_AULA_EXISTENTE     = 3;
    private static final Integer ID_ASPIRANTE_PRUEBA_EXISTENTE = 1;

    /** IDs inexistentes para verificar respuestas {@code 404}. */
    private static final Integer ID_JORNADA_AULA_INEXISTENTE    = Integer.MAX_VALUE;
    private static final Integer ID_ASPIRANTE_PRUEBA_INEXISTENTE = Integer.MAX_VALUE - 1;

    /**
     * Id ({@code Integer}) del JornadaAulaAspirante creado en
     * {@code Create.responde201_cuandoEntidadValida}.
     * Se extrae del body JSON del {@code 201} y se comparte con
     * {@code FindById} y {@code Update}.
     */
    private static Integer IDCREADO;

    /** Id inexistente para verificar respuestas {@code 404}. */
    private static final Integer ID_INEXISTENTE = Integer.MAX_VALUE;

    private static final String PATH_JORNADA_AULA     = "jornadaAula";
    private static final String PATH_ASPIRANTE_PRUEBA = "aspirantePrueba";

    /**
     * Pruebas del endpoint
     * {@code POST /jornadaAula/{idJornadaAula}/aspirantePrueba/{idAspirantePrueba}}.
     *
     * <p>Requiere JornadaAula y AspirantePrueba existentes en BD.
     * El resource rechaza con {@code 422} y header {@code Missing-parameter}
     * si {@code entity.getId() != null}.
     * Devuelve {@code 404} con header {@code Not-found} si JornadaAula no existe.
     */
    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Create {

        /**
         * Verifica {@code 201 Created} con JornadaAula y AspirantePrueba válidos.
         *
         * <p>Comprueba que:
         * <ul>
         *   <li>El status sea {@code 201}.</li>
         *   <li>El header {@code Location} esté presente.</li>
         *   <li>El body contenga el campo {@code id} generado por el servidor.</li>
         *   <li>El body contenga el valor {@code asistio} enviado.</li>
         * </ul>
         */
        @Order(1)
        @Test
        void responde201_cuandoEntidadValida() {
            // Arrange
            String bodyJson = """
                    {
                        "horaLlegada": "06:45:00",
                        "asistio": true,
                        "fechaAsignacion": "2025-05-10T00:00:00Z"
                    }
                    """;

            Response response = target
                    .path(PATH_JORNADA_AULA)
                    .path(ID_JORNADA_AULA_EXISTENTE.toString())
                    .path(PATH_ASPIRANTE_PRUEBA)
                    .path(ID_ASPIRANTE_PRUEBA_EXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyJson));

            assertEquals(201, response.getStatus(),
                    "Debe retornar 201 Created al crear un JornadaAulaAspirante válido");

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

        /**
         * Verifica que un body JSON vacío resulta en error de servidor.
         *
         * <p>Liberty falla en deserialización antes del resource
         * ({@code 400} o {@code 500}).
         */
        @Order(2)
        @Test
        void respondeFallo_cuandoBodyJsonMalformado() {
            Response response = target
                    .path(PATH_JORNADA_AULA)
                    .path(ID_JORNADA_AULA_EXISTENTE.toString())
                    .path(PATH_ASPIRANTE_PRUEBA)
                    .path(ID_ASPIRANTE_PRUEBA_EXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(
                    response.getStatus() == 400 || response.getStatus() == 500,
                    "Debe retornar 400 o 500 cuando el body JSON está vacío, " +
                            "fue: " + response.getStatus()
            );
        }

        /**
         * Verifica {@code 422} cuando la entidad trae un {@code id} pre-asignado.
         *
         * <p>Comprueba que:
         * <ul>
         *   <li>El status sea {@code 422}.</li>
         *   <li>El header {@code Missing-parameter} esté presente.</li>
         * </ul>
         */
        @Order(3)
        @Test
        void responde422_cuandoEntidadTieneIdPreasignado() {
            String bodyConId = """
                    {
                        "id": 99999,
                        "asistio": false,
                        "fechaAsignacion": "2025-05-10T00:00:00Z"
                    }
                    """;

            Response response = target
                    .path(PATH_JORNADA_AULA)
                    .path(ID_JORNADA_AULA_EXISTENTE.toString())
                    .path(PATH_ASPIRANTE_PRUEBA)
                    .path(ID_ASPIRANTE_PRUEBA_EXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyConId));

            assertEquals(422, response.getStatus(),
                    "Debe retornar 422 cuando la entidad trae un id pre-asignado");

            assertNotNull(response.getHeaderString("Missing-parameter"),
                    "El header Missing-parameter debe estar presente");
        }

        /**
         * Verifica {@code 404} con header {@code Not-found} cuando la JornadaAula
         * del path no existe en BD.
         */
        @Order(4)
        @Test
        void responde404_cuandoJornadaAulaNoExiste() {
            String body = """
                    {
                        "asistio": false,
                        "fechaAsignacion": "2025-05-10T00:00:00Z"
                    }
                    """;

            Response response = target
                    .path(PATH_JORNADA_AULA)
                    .path(ID_JORNADA_AULA_INEXISTENTE.toString())
                    .path(PATH_ASPIRANTE_PRUEBA)
                    .path(ID_ASPIRANTE_PRUEBA_EXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando la JornadaAula no existe");

            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
        }
    }

    /**
     * Pruebas del endpoint
     * {@code GET /jornadaAula/{idJornadaAula}/aspirantePrueba/{idAspirantePrueba}/{id}}.
     *
     * <p>Depende de que {@code Create.responde201_cuandoEntidadValida} haya
     * poblado {@code IDCREADO}.
     */
    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindById {

        /**
         * Verifica {@code 200 OK} cuando el {@code id} existe.
         *
         * <p>Comprueba que:
         * <ul>
         *   <li>El status sea {@code 200}.</li>
         *   <li>El body contenga el {@code id} consultado.</li>
         * </ul>
         */
        @Order(1)
        @Test
        void responde200_cuandoIdExiste() {
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            Response response = target
                    .path(PATH_JORNADA_AULA)
                    .path(ID_JORNADA_AULA_EXISTENTE.toString())
                    .path(PATH_ASPIRANTE_PRUEBA)
                    .path(ID_ASPIRANTE_PRUEBA_EXISTENTE.toString())
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

        /**
         * Verifica {@code 404} con header {@code Not-found} cuando el {@code id}
         * no corresponde a ningún registro.
         */
        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            Response response = target
                    .path(PATH_JORNADA_AULA)
                    .path(ID_JORNADA_AULA_EXISTENTE.toString())
                    .path(PATH_ASPIRANTE_PRUEBA)
                    .path(ID_ASPIRANTE_PRUEBA_EXISTENTE.toString())
                    .path(ID_INEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando el id no existe");

            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
        }
    }

    /**
     * Pruebas del endpoint
     * {@code PUT /jornadaAula/{idJornadaAula}/aspirantePrueba/{idAspirantePrueba}/{id}}.
     */
    @Nested
    @Order(3)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Update {

        /**
         * Verifica {@code 200 OK} cuando el {@code id} existe y la entidad es válida.
         *
         * <p>Comprueba que:
         * <ul>
         *   <li>El status sea {@code 200}.</li>
         *   <li>El body contenga el {@code id} del registro actualizado.</li>
         *   <li>El body refleje el campo {@code asistio} actualizado.</li>
         * </ul>
         */
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
                    .path(ID_ASPIRANTE_PRUEBA_EXISTENTE.toString())
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

        /**
         * Verifica {@code 404} con header {@code Not-found} cuando el {@code id}
         * del path no corresponde a ningún registro.
         */
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
                    .path(ID_ASPIRANTE_PRUEBA_EXISTENTE.toString())
                    .path(ID_INEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(body));

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando el id no existe");

            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
        }

        /**
         * Verifica que un body JSON vacío resulta en error de servidor.
         *
         * <p>Liberty falla en deserialización antes del resource
         * ({@code 400} o {@code 500}).
         */
        @Order(3)
        @Test
        void respondeFallo_cuandoBodyJsonMalformado() {
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            Response response = target
                    .path(PATH_JORNADA_AULA)
                    .path(ID_JORNADA_AULA_EXISTENTE.toString())
                    .path(PATH_ASPIRANTE_PRUEBA)
                    .path(ID_ASPIRANTE_PRUEBA_EXISTENTE.toString())
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