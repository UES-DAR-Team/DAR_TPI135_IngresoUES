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
 *   <li>Los 404 y 422 del {@code update} NO incluyen headers.</li>
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
     * El resource rechaza con {@code 422} sin header si {@code entity.getId() != null}.
     * Devuelve {@code 404} con header {@code Not-found} si jornada o aula no existen.
     */
    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Create {

        /**
         * Verifica {@code 201 Created} con jornada y aula válidas.
         *
         * <p>El id generado se extrae del campo {@code id} del body JSON
         * y se almacena en {@code IDCREADO} para los {@code @Nested} posteriores.
         */
        @Order(1)
        @Test
        void responde201_cuandoEntidadValida() {
            // Arrange
            String bodyJson = """
                    {
                        "fechaAsignacion": "2025-01-01T00:00:00Z"
                    }
                    """;

            // Act
            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_AULA_EXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyJson));

            // Assert
            assertEquals(201, response.getStatus(),
                    "Debe retornar 201 Created al crear un JornadaAula válido");
            assertNotNull(response.getHeaderString("Location"),
                    "El header Location debe contener la URI del nuevo recurso");

            // Extraer el id Integer del body JSON
            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.contains("\"id\""), "El body debe contener el campo id");

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
            // Act
            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_AULA_EXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity("", MediaType.APPLICATION_JSON));

            // Assert
            assertTrue(
                    response.getStatus() == 400 || response.getStatus() == 500,
                    "Debe retornar 400 o 500 cuando el body JSON está vacío, " +
                            "fue: " + response.getStatus()
            );
        }

        /**
         * Verifica {@code 422} cuando la entidad trae un {@code id} pre-asignado.
         *
         * <p>Nota: el resource devuelve {@code 422} sin header en este caso.
         */
        @Order(3)
        @Test
        void responde422_cuandoEntidadTieneIdPreasignado() {
            // Arrange
            String bodyConId = """
                    {
                        "id": 99999,
                        "fechaAsignacion": "2025-01-01T00:00:00Z"
                    }
                    """;

            // Act
            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_AULA_EXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyConId));

            // Assert
            assertEquals(422, response.getStatus(),
                    "Debe retornar 422 cuando la entidad trae un id pre-asignado");
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

            // Act
            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_INEXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_AULA_EXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            // Assert
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

            // Act
            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_AULA_INEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            // Assert
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
         * Verifica {@code 200 OK} y cuerpo JSON cuando el {@code id} existe.
         */
        @Order(1)
        @Test
        void responde200_cuandoIdExiste() {
            // Arrange
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            // Act
            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_AULA_EXISTENTE.toString())
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            // Assert
            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando el id existe");
            assertTrue(response.hasEntity(),
                    "La respuesta debe contener el JornadaAula encontrado");
        }

        /**
         * Verifica {@code 404} y header {@code Not-found} cuando el {@code id}
         * no corresponde a ningún registro.
         */
        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            // Act
            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_AULA_EXISTENTE.toString())
                    .path(ID_INEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            // Assert
            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando el id no existe");
            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
        }
    }

    /**
     * Pruebas del endpoint {@code PUT /jornada/{idJornada}/aula/{idAula}/{id}}.
     *
     * <p>Nota: el resource devuelve {@code 404} y {@code 422} sin headers
     * en los casos de error.
     */
    @Nested
    @Order(3)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Update {

        /**
         * Verifica {@code 200 OK} y cuerpo actualizado cuando el {@code id} existe
         * y la entidad es válida.
         */
        @Order(1)
        @Test
        void responde200_cuandoIdExisteYEntidadValida() {
            // Arrange
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            String bodyActualizado = """
                    {
                        "fechaAsignacion": "2025-06-01T00:00:00Z"
                    }
                    """;

            // Act
            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_AULA_EXISTENTE.toString())
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(bodyActualizado));

            // Assert
            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando el id existe y la entidad es válida");
            assertTrue(response.hasEntity(),
                    "La respuesta debe contener el JornadaAula actualizado");
        }

        /**
         * Verifica {@code 404} cuando el {@code id} del path no existe.
         *
         * <p>Nota: el resource devuelve {@code 404} sin header en este caso.
         */
        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            // Arrange
            String body = """
                    {
                        "fechaAsignacion": "2025-01-01T00:00:00Z"
                    }
                    """;

            // Act
            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_AULA_EXISTENTE.toString())
                    .path(ID_INEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(body));

            // Assert
            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando el id no existe");
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
            // Arrange
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            // Act
            Response response = target
                    .path(PATH_JORNADA)
                    .path(ID_JORNADA_EXISTENTE.toString())
                    .path(PATH_AULA)
                    .path(ID_AULA_EXISTENTE.toString())
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity("", MediaType.APPLICATION_JSON));

            // Assert
            assertTrue(
                    response.getStatus() == 400 || response.getStatus() == 500,
                    "Debe retornar 400 o 500 cuando el body JSON está vacío, " +
                            "fue: " + response.getStatus()
            );
        }
    }
}