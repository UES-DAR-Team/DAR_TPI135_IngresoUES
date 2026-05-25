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

/**
 * Pruebas de sistema para {@link JornadaAulaResource}.
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
 *   <li>Path: {@code jornada/{idJornada}/aula/{idAula}} con {@code {id}} adicional
 *       para {@code findById} y {@code update}.</li>
 *   <li>El {@code id} es {@code Integer} asignado por PostgreSQL via {@code serial}
 *       ({@code @GeneratedValue(IDENTITY)}) — se extrae del body del {@code 201}.</li>
 *   <li>{@code fechaAsignacion} es {@code @NotNull} — debe incluirse en el body JSON.</li>
 *   <li>No tiene endpoints {@code FindRange} ni {@code DeleteById}.</li>
 * </ul>
 *
 * @see JornadaAulaResource
 * @see BaseIntegrationAbstract
 */
@SystemTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ContainerExtension.class)
public class JornadaAulaResourceSystem extends BaseIntegrationAbstract {

    // --- Arrange General ---

    /**
     * UUIDs de Jornada y Aula existentes en BD (insertados por ingreso_ues_db.sql).
     * La combinación jornada=001 + aula=003 NO existe en el script, por lo que
     * no viola la restricción UNIQUE (id_jornada, id_aula).
     */
    private static final UUID ID_JORNADA_EXISTENTE =
            UUID.fromString("e5000000-0000-0000-0000-000000000001");
    private static final UUID ID_AULA_EXISTENTE =
            UUID.fromString("f6000000-0000-0000-0000-000000000003");

    /** UUIDs inexistentes para verificar respuestas {@code 404}. */
    private static final UUID ID_JORNADA_INEXISTENTE = UUID.randomUUID();
    private static final UUID ID_AULA_INEXISTENTE    = UUID.randomUUID();

    /**
     * Id ({@code Integer}) del JornadaAula creado en
     * {@code Create.responde201_cuandoEntidadValida}.
     * Se extrae del body JSON del {@code 201} y se comparte con
     * {@code FindById} y {@code Update}.
     */
    private static Integer IDCREADO;

    /** Id inexistente para verificar respuestas {@code 404}. */
    private static final Integer ID_INEXISTENTE = Integer.MAX_VALUE;

    private static final String PATH_JORNADA = "jornada";
    private static final String PATH_AULA    = "aula";

    /**
     * Pruebas del endpoint {@code POST /jornada/{idJornada}/aula/{idAula}}.
     *
     * <p>Requiere jornada y aula existentes en BD.
     * Devuelve {@code 404} con header {@code Not-found} si jornada o aula no existen.
     * Devuelve {@code 422} sin header si {@code entity.getId() != null}.
     */
    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Create {

        /**
         * Verifica {@code 201 Created} con jornada y aula válidas.
         *
         * <p>Comprueba que:
         * <ul>
         *   <li>El status sea {@code 201}.</li>
         *   <li>El header {@code Location} esté presente.</li>
         *   <li>El body contenga el campo {@code id} generado por el servidor.</li>
         *   <li>El body contenga el UUID de la jornada asignada.</li>
         * </ul>
         */
        @Order(1)
        @Test
        void responde201_cuandoEntidadValida() {
            String bodyJson = """
                    {
                        "fechaAsignacion": "2025-01-01T00:00:00Z"
                    }
                    """;

            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_AULA_EXISTENTE.toString())
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
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_AULA_EXISTENTE.toString())
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
                        "fechaAsignacion": "2025-01-01T00:00:00Z"
                    }
                    """;

            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_AULA_EXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyConId));

            assertEquals(422, response.getStatus(),
                    "Debe retornar 422 cuando la entidad trae un id pre-asignado");
            assertNotNull(response.getHeaderString("Missing-parameter"),
                    "El header Missing-parameter debe estar presente");
        }

        /**
         * Verifica {@code 404} con header {@code Not-found} cuando la jornada
         * del path no existe en BD.
         */
        @Order(4)
        @Test
        void responde404_cuandoJornadaNoExiste() {
            // Arrange
            String body = """
                    {
                        "fechaAsignacion": "2025-01-01T00:00:00Z"
                    }
                    """;

            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_INEXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_AULA_EXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando la jornada no existe");
            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
        }

        /**
         * Verifica {@code 404} con header {@code Not-found} cuando el aula
         * del path no existe en BD.
         */
        @Order(5)
        @Test
        void responde404_cuandoAulaNoExiste() {
            // Arrange
            String body = """
                    {
                        "fechaAsignacion": "2025-01-01T00:00:00Z"
                    }
                    """;

            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_AULA_INEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando el aula no existe");
            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
        }
    }

    /**
     * Pruebas del endpoint {@code GET /jornada/{idJornada}/aula/{idAula}/{id}}.
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
         *   <li>El body contenga el UUID de la jornada.</li>
         * </ul>
         */
        @Order(1)
        @Test
        void responde200_cuandoIdExiste() {
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_AULA_EXISTENTE.toString())
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

        /**
         * Verifica {@code 404} y header {@code Not-found} cuando el {@code id}
         * no corresponde a ningún registro.
         */
        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_AULA_EXISTENTE.toString())
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
     * Pruebas del endpoint {@code PUT /jornada/{idJornada}/aula/{idAula}/{id}}.
     *
     * <p>Opera sobre el registro creado en {@code Create}.
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
         *   <li>El body conserve el UUID de la jornada.</li>
         * </ul>
         */
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
                    .path(ID_AULA_EXISTENTE.toString())
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

        /**
         * Verifica {@code 404} con header {@code Not-found} cuando el {@code id}
         * del path no corresponde a ningún registro.
         */
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
                    .path(ID_AULA_EXISTENTE.toString())
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
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_AULA_EXISTENTE.toString())
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