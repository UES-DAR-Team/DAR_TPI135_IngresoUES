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
 *   <li>{@code IDCREADO} se puebla en {@code Create} y se reutiliza en
 *       {@code FindById} y {@code Update}.</li>
 * </ul>
 *
 * <p>Particularidades de este resource:
 * <ul>
 *   <li>Es un sub-recurso bajo {@code /aspirante/{idAspirante}/pruebas}.</li>
 *   <li>El {@code id} es {@code Integer} asignado por PostgreSQL via {@code serial}
 *       ({@code @GeneratedValue(IDENTITY)}) — se extrae del body del {@code 201}.</li>
 *   <li>El POST requiere un {@code idPrueba} válido dentro del body JSON.</li>
 *   <li>Los 404 y 422 del {@code update} NO incluyen headers — el resource
 *       solo devuelve el status sin headers adicionales.</li>
 *   <li>No tiene endpoints {@code FindRange} ni {@code DeleteById}.</li>
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
     * Carlos Ernesto Martínez López.
     */
    private static final UUID ID_ASPIRANTE_EXISTENTE =
            UUID.fromString("b2000000-0000-0000-0000-000000000003");

    /** UUID de aspirante inexistente para verificar respuestas {@code 404}. */
    private static final UUID ID_ASPIRANTE_INEXISTENTE = UUID.randomUUID();

    /**
     * UUID de prueba existente en la BD (insertado por {@code ingreso_ues_db.sql}).
     * Se envía dentro del body JSON del POST.
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
     * <p>Requiere aspirante y prueba existentes en BD.
     * El resource rechaza con {@code 422} sin header si {@code entity.getId() != null}.
     * Devuelve {@code 404} con header {@code Not-found} si aspirante o prueba no existen.
     */
    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Create {

        /**
         * Verifica {@code 201 Created} con aspirante y prueba válidos.
         *
         * <p>El id generado se extrae del campo {@code id} del body JSON
         * y se almacena en {@code IDCREADO} para los {@code @Nested} posteriores.
         *
         * <p>Nota: se usa el aspirante {@code b2000000-...-000000000003} (José Alejandro)
         * que en el SQL solo tiene asignada la prueba {@code 07000000-...-000000000002},
         * por lo que asignarle la prueba {@code 07000000-...-000000000003} no viola
         * la restricción UNIQUE de la tabla.
         */
        @Order(1)
        @Test
        void responde201_cuandoEntidadValida() {
            // Arrange
            String bodyJson = """
                    {
                        "idPrueba": {
                            "id": "%s"
                        },
                        "fechaAsignacion": "2025-01-01T00:00:00Z"
                    }
                    """.formatted(ID_PRUEBA_EXISTENTE);

            // Act
            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_PRUEBAS)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyJson));

            // Assert
            assertEquals(201, response.getStatus(),
                    "Debe retornar 201 Created al crear una AspirantePrueba válida");
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
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_PRUEBAS)
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
         * <p>Nota: el resource devuelve {@code 422} sin header {@code Missing-parameter}
         * en este caso — es el comportamiento real del resource.
         */
        @Order(3)
        @Test
        void responde422_cuandoEntidadTieneIdPreasignado() {
            // Arrange
            String bodyConId = """
                    {
                        "id": 99999,
                        "idPrueba": { "id": "%s" },
                        "fechaAsignacion": "2025-01-01T00:00:00Z"
                    }
                    """.formatted(ID_PRUEBA_EXISTENTE);

            // Act
            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_PRUEBAS)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyConId));

            // Assert
            assertEquals(422, response.getStatus(),
                    "Debe retornar 422 cuando la entidad trae un id pre-asignado");
        }

        /**
         * Verifica {@code 404} con header {@code Not-found} cuando el aspirante
         * del path no existe en BD.
         */
        @Order(4)
        @Test
        void responde404_cuandoAspiranteNoExiste() {
            // Arrange
            String body = """
                    {
                        "idPrueba": { "id": "%s" },
                        "fechaAsignacion": "2025-01-01T00:00:00Z"
                    }
                    """.formatted(ID_PRUEBA_EXISTENTE);

            // Act
            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_INEXISTENTE.toString())
                    .path(PATH_PRUEBAS)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            // Assert
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
            // Arrange
            String body = """
                    {
                        "idPrueba": { "id": "%s" },
                        "fechaAsignacion": "2025-01-01T00:00:00Z"
                    }
                    """.formatted(ID_PRUEBA_INEXISTENTE);

            // Act
            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_PRUEBAS)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            // Assert
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
         * Verifica {@code 200 OK} y cuerpo JSON cuando el {@code id} existe
         * y pertenece al aspirante del path.
         */
        @Order(1)
        @Test
        void responde200_cuandoIdExiste() {
            // Arrange
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            // Act
            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_PRUEBAS)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            // Assert
            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando el id existe");
            assertTrue(response.hasEntity(),
                    "La respuesta debe contener la AspirantePrueba encontrada");
        }

        /**
         * Verifica {@code 404} con header {@code Not-found} cuando el {@code id}
         * no corresponde a ningún registro.
         */
        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            // Act
            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_PRUEBAS)
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
     * Pruebas del endpoint {@code PUT /aspirante/{idAspirante}/pruebas/{idPrueba}}.
     *
     * <p>Nota: el resource devuelve {@code 404} y {@code 422} sin headers
     * en los casos de error — es el comportamiento real del resource.
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
                        "idPrueba": { "id": "%s" },
                        "fechaAsignacion": "2025-06-01T00:00:00Z"
                    }
                    """.formatted(ID_PRUEBA_EXISTENTE);

            // Act
            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_PRUEBAS)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(bodyActualizado));

            // Assert
            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando el id existe y la entidad es válida");
            assertTrue(response.hasEntity(),
                    "La respuesta debe contener la AspirantePrueba actualizada");
        }

        /**
         * Verifica {@code 404} cuando el {@code idPrueba} del path no existe.
         *
         * <p>Nota: el resource devuelve {@code 404} sin header en este caso.
         */
        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            // Arrange
            String body = """
                    {
                        "idPrueba": { "id": "%s" },
                        "fechaAsignacion": "2025-01-01T00:00:00Z"
                    }
                    """.formatted(ID_PRUEBA_EXISTENTE);

            // Act
            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_PRUEBAS)
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
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_PRUEBAS)
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