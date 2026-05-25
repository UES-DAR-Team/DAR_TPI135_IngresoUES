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
 * Pruebas de sistema para {@link AspirantePruebaResource}.
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
 *   <li>Es un sub-recurso bajo {@code /aspirante/{idAspirante}/pruebas}.</li>
 *   <li>El {@code id} es {@code Integer} asignado por PostgreSQL via {@code serial}
 *       ({@code @GeneratedValue(IDENTITY)}) — se extrae del body del {@code 201}.</li>
 *   <li>El POST recibe un {@link AspirantePruebaResource.AspirantePruebaInput} con
 *       solo el UUID de la prueba: {@code { "idPrueba": "uuid" }}.</li>
 *   <li>No tiene endpoint {@code DeleteById} ni {@code FindRange} propios en este test.</li>
 * </ul>
 *
 * @see AspirantePruebaResource
 * @see BaseIntegrationAbstract
 */
@SystemTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ContainerExtension.class)
public class AspirantePruebaResourceSystem extends BaseIntegrationAbstract {

    // --- Arrange General ---

    /**
     * UUID de aspirante existente en la BD (insertado por {@code ingreso_ues_db.sql}).
     * José Alejandro Hernández Cruz — solo tiene asignada la prueba {@code 000000000002},
     * por lo que asignarle la {@code 000000000003} no viola la restricción UNIQUE.
     */
    private static final UUID ID_ASPIRANTE_EXISTENTE =
            UUID.fromString("b2000000-0000-0000-0000-000000000003");

    /** UUID de aspirante inexistente para verificar respuestas {@code 404}. */
    private static final UUID ID_ASPIRANTE_INEXISTENTE = UUID.randomUUID();

    /**
     * UUID de prueba existente en la BD (insertado por {@code ingreso_ues_db.sql}).
     * Se envía como valor plano en el body JSON del POST/PUT:
     * {@code { "idPrueba": "07000000-0000-0000-0000-000000000003" }}.
     */
    private static final UUID ID_PRUEBA_EXISTENTE =
            UUID.fromString("07000000-0000-0000-0000-000000000003");

    /** UUID de prueba inexistente para verificar respuesta {@code 404} en create. */
    private static final UUID ID_PRUEBA_INEXISTENTE = UUID.randomUUID();

    /**
     * Id ({@code Integer}) de la AspirantePrueba creada en
     * {@code Create.responde201_cuandoEntidadValida}.
     * Se extrae del body JSON del {@code 201} y se comparte con
     * {@code FindById} y {@code Update}.
     */
    private static Integer IDCREADO;

    /** Id inexistente para verificar respuestas {@code 404}. */
    private static final Integer ID_INEXISTENTE = Integer.MAX_VALUE;

    private static final String PATH_BASE    = "aspirante";
    private static final String PATH_PRUEBAS = "pruebas";

    /**
     * Pruebas del endpoint {@code POST /aspirante/{idAspirante}/pruebas}.
     *
     * <p>El body esperado es {@code { "idPrueba": "uuid" }} — solo el UUID de la prueba,
     * no el objeto anidado. Esto es manejado por {@link AspirantePruebaResource.AspirantePruebaInput}.
     *
     * <p>Devuelve {@code 404} con header {@code Not-found} si aspirante o prueba no existen.
     * Devuelve {@code 422} con header {@code Missing-parameter} si {@code idPrueba} está ausente.
     */
    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Create {

        /**
         * Verifica {@code 201 Created} con aspirante y prueba válidos.
         *
         * <p>El body enviado usa el nuevo formato de {@code AspirantePruebaInput}:
         * {@code { "idPrueba": "uuid" }} — solo el UUID, sin objeto anidado.
         *
         * <p>Comprueba que:
         * <ul>
         *   <li>El status sea {@code 201}.</li>
         *   <li>El header {@code Location} esté presente.</li>
         *   <li>El body contenga el campo {@code id} generado por el servidor.</li>
         *   <li>El body contenga el {@code idAspirante} usado en el path.</li>
         * </ul>
         *
         * <p>Nota: se usa el aspirante {@code b2000000-...-000000000003} (José Alejandro)
         * que en el SQL solo tiene asignada la prueba {@code 07000000-...-000000000002},
         * por lo que asignarle la prueba {@code 07000000-...-000000000003} no viola
         * la restricción UNIQUE de la tabla.
         */
        @Order(1)
        @Test
        void responde201_cuandoEntidadValida() {
            String bodyJson = """
                    {
                        "idPrueba": "%s"
                    }
                    """.formatted(ID_PRUEBA_EXISTENTE);

            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_PRUEBAS)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyJson));

            assertEquals(201, response.getStatus(),
                    "Debe retornar 201 Created al crear una AspirantePrueba válida");

            assertNotNull(response.getHeaderString("Location"),
                    "El header Location debe estar presente");

            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.contains("\"id\""),
                    "El body debe contener el campo id generado por el servidor");
            assertTrue(body.contains(ID_ASPIRANTE_EXISTENTE.toString()),
                    "El body debe contener el idAspirante usado en el path");

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
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_PRUEBAS)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(
                    response.getStatus() == 400 || response.getStatus() == 500,
                    "Debe retornar 400 o 500 cuando el body JSON está vacío, " +
                            "fue: " + response.getStatus()
            );
        }

        /**
         * Verifica {@code 422} cuando el body no incluye el campo {@code idPrueba}.
         *
         * <p>El resource valida que {@code input.getIdPrueba() != null} y devuelve
         * {@code 422} con header {@code Missing-parameter} si falta.
         */
        @Order(3)
        @Test
        void responde422_cuandoIdPruebaAusente() {
            String bodySinPrueba = """
                    {
                        "fechaAsignacion": "2025-01-01T00:00:00Z"
                    }
                    """;

            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_PRUEBAS)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodySinPrueba));

            assertEquals(422, response.getStatus(),
                    "Debe retornar 422 cuando idPrueba no está en el body");

            assertNotNull(response.getHeaderString("Missing-parameter"),
                    "El header Missing-parameter debe estar presente");
        }

        /**
         * Verifica {@code 404} con header {@code Not-found} cuando el aspirante
         * del path no existe en BD.
         */
        @Order(4)
        @Test
        void responde404_cuandoAspiranteNoExiste() {
            String body = """
                    {
                        "idPrueba": "%s"
                    }
                    """.formatted(ID_PRUEBA_EXISTENTE);

            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_INEXISTENTE.toString())
                    .path(PATH_PRUEBAS)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando el aspirante no existe");

            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
        }

        /**
         * Verifica {@code 404} con header {@code Not-found} cuando la prueba
         * del body no existe en BD.
         */
        @Order(5)
        @Test
        void responde404_cuandoPruebaNoExiste() {
            String body = """
                    {
                        "idPrueba": "%s"
                    }
                    """.formatted(ID_PRUEBA_INEXISTENTE);

            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_PRUEBAS)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando la prueba no existe");

            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
        }
    }

    /**
     * Pruebas del endpoint {@code GET /aspirante/{idAspirante}/pruebas/{idPrueba}}.
     *
     * <p>Depende de que {@code Create.responde201_cuandoEntidadValida} haya
     * poblado {@code IDCREADO}.
     */
    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindById {

        /**
         * Verifica {@code 200 OK} cuando el {@code id} existe y pertenece
         * al aspirante del path.
         *
         * <p>Comprueba que:
         * <ul>
         *   <li>El status sea {@code 200}.</li>
         *   <li>El body contenga el {@code id} consultado.</li>
         *   <li>El body contenga el {@code idAspirante} del path.</li>
         * </ul>
         */
        @Order(1)
        @Test
        void responde200_cuandoIdExiste() {
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_PRUEBAS)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando el id existe");

            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.contains(String.valueOf(IDCREADO)),
                    "El body debe contener el id consultado");
            assertTrue(body.contains(ID_ASPIRANTE_EXISTENTE.toString()),
                    "El body debe contener el idAspirante del path");
        }

        /**
         * Verifica {@code 404} con header {@code Not-found} cuando el {@code id}
         * no corresponde a ningún registro.
         */
        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_PRUEBAS)
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
     * Pruebas del endpoint {@code PUT /aspirante/{idAspirante}/pruebas/{idPrueba}}.
     *
     * <p>El body esperado es {@code { "idPrueba": "uuid" }} — formato plano del DTO.
     * Si {@code idPrueba} no viene en el body, el resource conserva la prueba existente.
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
         *   <li>El body contenga el {@code idAspirante} del path.</li>
         * </ul>
         */
        @Order(1)
        @Test
        void responde200_cuandoIdExisteYEntidadValida() {
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            String bodyActualizado = """
                    {
                        "idPrueba": "%s"
                    }
                    """.formatted(ID_PRUEBA_EXISTENTE);

            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_PRUEBAS)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(bodyActualizado));

            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando el id existe y la entidad es válida");

            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.contains(String.valueOf(IDCREADO)),
                    "El body debe contener el id del registro actualizado");
            assertTrue(body.contains(ID_ASPIRANTE_EXISTENTE.toString()),
                    "El body debe contener el idAspirante del path");
        }

        /**
         * Verifica {@code 404} con header {@code Not-found} cuando el {@code idPrueba}
         * del path no corresponde a ningún registro.
         */
        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            String body = """
                    {
                        "idPrueba": "%s"
                    }
                    """.formatted(ID_PRUEBA_EXISTENTE);

            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_PRUEBAS)
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
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_PRUEBAS)
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