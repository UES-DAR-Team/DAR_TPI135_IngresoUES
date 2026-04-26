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
 * Pruebas de sistema para {@link AspiranteResource}.
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
 *       {@code FindById}, {@code Update} y {@code DeleteById}.</li>
 * </ul>
 *
 * <p>Comportamientos reales verificados contra Liberty 25:
 * <ul>
 *   <li>Bean Validation ({@code @Min}/{@code @Max}) interceptada por Liberty → {@code 400}.</li>
 *   <li>Body JSON vacío o malformado → fallo de deserialización en Liberty → {@code 400} o {@code 500}
 *       (no llega al método del resource).</li>
 *   <li>UUID asignado por el DAO tras {@code em.persist()} + {@code em.flush()} → disponible
 *       en el body JSON del {@code 201}.</li>
 * </ul>
 *
 * @see AspiranteResource
 * @see BaseIntegrationAbstract
 */
@SystemTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ContainerExtension.class)
public class AspiranteResourceSystem extends BaseIntegrationAbstract {

    // --- Arrange General ---

    /** Segmento de ruta que mapea a {@code @Path("aspirante")} en el resource. */
    private static final String PATH = "aspirante";

    /**
     * <li>UUID del registro creado en {@code Create.responde201_cuandoEntidadValida}.
     * Se comparte con {@code FindById}, {@code Update} y {@code DeleteById}.</li>
     * <li>UUID del id inexistente utilizado en {@code DeleteById}, {@code Update} y {@code FindById},
     * para la comprobación del registro inexistente para la devolución del {@code 404}.</li>
     * <li>int del First, parámetro de paginación válido para la búsqueda en {@code FindRange}.</li>
     * <li>int del Max, parámetro de paginación válido para la búsqueda en {@code FindRange}.</li>
     * <li>int del INVALIDFIRST, parámetro de paginación inválido para la búsqueda en {@code FindRange}.</li>
     * <li>int del INVALIDMAX, parámetro de paginación inválido para la búsqueda en {@code FindRange}.</li>
     * <li>int del EXCEEDMAX, parámetro de paginación excedido para la búsqueda en {@code FindRange}.</li>
     */
    private static UUID IDCREADO;
    private static final UUID IDINEXISTENTE = UUID.randomUUID();
    private static final int FIRST      = 0;
    private static final int MAX        = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX   = 0;
    private static final int EXCEEDMAX    = 101;

    // =========================================================================
    // GET /aspirante?first=&max=
    // =========================================================================

    /**
     * Pruebas del endpoint {@code GET /aspirante}.
     *
     * <p>Verifica paginación con parámetros válidos e inválidos.
     * Las violaciones de {@code @Min}/{@code @Max} son interceptadas por
     * Bean Validation de Liberty antes de entrar al método, devolviendo {@code 400}.
     */
    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindRange {

        /**
         * Verifica {@code 200 OK} con parámetros válidos, cuerpo JSON presente
         * y header {@code Total-records} incluido.
         */

       /** @Order(1)
        @Test
        void responde201_cuandoEntidadValida() {
            String bodyJson = """
            {
                "nombreAspirante": "Aspirante de prueba de sistema",
                "apellidoAspirante": "Apellido prueba",
                "identificacion": "12345678-9",
                "email": "prueba@sistema.test",
                "fechaNacimiento": "2000-05-15",
                "fechaRegistro": "2025-01-01T00:00:00Z",
                "activo": true
            }
            """;

            Response response = target
                    .path(PATH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyJson));

            // ← AGREGAR ESTO TEMPORALMENTE
            System.out.println("STATUS: " + response.getStatus());
            System.out.println("BODY: " + response.readEntity(String.class));
            System.out.println("=== LIBERTY LOGS ===");
            System.out.println(ContainerExtension.getOpenLiberty().getLogs());

            assertEquals(201, response.getStatus());
            // ... resto del test
        }*/
        @Order(1)
        @Test
        void responde200_cuandoParametrosValidos() {
            // Act
            Response response = target
                    .path(PATH)
                    .queryParam("first", FIRST)
                    .queryParam("max", MAX)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            // Assert
            assertEquals(200, response.getStatus());
            assertNotNull(response.getHeaderString("Total-records"),
                    "El header Total-records debe estar presente");
            assertTrue(response.hasEntity(),
                    "La respuesta debe contener un cuerpo JSON");
        }

        /**
         * Verifica {@code 400} cuando {@code first} es negativo.
         *
         * <p>Liberty intercepta la violación de {@code @Min(0)} con Bean Validation
         * y devuelve {@code 400} sin ejecutar el cuerpo del método.
         */
        @Order(2)
        @Test
        void responde400_cuandoFirstNegativo() {
            // Act
            Response response = target
                    .path(PATH)
                    .queryParam("first", INVALIDFIRST)
                    .queryParam("max", MAX)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            // Assert
            assertEquals(400, response.getStatus());
        }

        /**
         * Verifica {@code 400} cuando {@code max} es cero.
         *
         * <p>Liberty intercepta la violación de {@code @Min(1)} con Bean Validation.
         */
        @Order(3)
        @Test
        void responde400_cuandoMaxCero() {
            // Act
            Response response = target
                    .path(PATH)
                    .queryParam("first", FIRST)
                    .queryParam("max", INVALIDMAX)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            // Assert
            assertEquals(400, response.getStatus());
        }

        /**
         * Verifica {@code 400} cuando {@code max} supera el límite de 100.
         *
         * <p>Liberty intercepta la violación de {@code @Max(100)} con Bean Validation.
         */
        @Order(4)
        @Test
        void responde400_cuandoMaxExcedeLimite() {
            // Act
            Response response = target
                    .path(PATH)
                    .queryParam("first", FIRST)
                    .queryParam("max", EXCEEDMAX)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            // Assert
            assertEquals(400, response.getStatus());
        }
    }

    // =========================================================================
    // POST /aspirante
    // =========================================================================

    /**
     * Pruebas del endpoint {@code POST /aspirante}.
     *
     * <p>El UUID es asignado por el DAO tras {@code em.persist()} + {@code em.flush()}.
     * El resource verifica que {@code entity.getId() == null} antes de persistir;
     * si el cliente manda un {@code id}, el resource devuelve {@code 422}.
     *
     * <p>Un body JSON vacío o malformado falla en la capa de deserialización de Liberty
     * antes de llegar al método del resource, resultando en {@code 400} o {@code 500}.
     */
    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Create {

        /**
         * Verifica {@code 201 Created} con entidad válida sin {@code id}.
         *
         * <p>El UUID generado se extrae del campo {@code id} del body JSON
         * y se almacena en {@code IDCREADO} para los {@code @Nested} posteriores.
         */
        @Order(1)
        @Test
        void responde201_cuandoEntidadValida() {
            // Arrange — sin id, el servidor lo asigna via @GeneratedValue + flush()
            String bodyJson = """
                    {
                        "nombreAspirante": "Aspirante de prueba de sistema",
                        "apellidoAspirante": "Apellido prueba",
                        "identificacion": "12345678-9",
                        "email": "prueba@sistema.test",
                        "fechaNacimiento": "2000-05-15",
                        "fechaRegistro": "2025-01-01T00:00:00Z",
                        "activo": true
                    }
                    """;

            // Act
            Response response = target
                    .path(PATH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyJson));

            // Assert
            assertEquals(201, response.getStatus(),
                    "Debe retornar 201 Created al crear un Aspirante válido");
            assertNotNull(response.getHeaderString("Location"),
                    "El header Location debe contener la URI del nuevo recurso");

            // Extraer el UUID del body JSON y almacenarlo para tests posteriores
            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.contains("\"id\""), "El body debe contener el campo id");

            int idStart = body.indexOf("\"id\":\"") + 6;
            int idEnd   = body.indexOf("\"", idStart);
            IDCREADO = UUID.fromString(body.substring(idStart, idEnd));
            assertNotNull(IDCREADO, "El UUID extraído del body no debe ser nulo");
        }

        /**
         * Verifica que un body JSON vacío resulta en error de servidor.
         *
         * <p>Liberty no llega a ejecutar el método del resource porque el deserializador
         * JSON falla primero. El código real devuelto es {@code 400} o {@code 500}
         * dependiendo de la versión de Liberty y la implementación JAX-RS.
         */
        @Order(2)
        @Test
        void respondeFallo_cuandoBodyJsonMalformado() {
            // Act
            Response response = target
                    .path(PATH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity("", MediaType.APPLICATION_JSON));

            // Assert — Liberty falla en deserialización antes del resource
            assertTrue(
                    response.getStatus() == 400 || response.getStatus() == 500,
                    "Debe retornar 400 o 500 cuando el body JSON está vacío o malformado, " +
                            "fue: " + response.getStatus()
            );
        }

        /**
         * Verifica {@code 422} cuando la entidad enviada trae un {@code id} pre-asignado.
         *
         * <p>El contrato del endpoint exige {@code entity.id == null} para que el servidor
         * asigne el UUID. Si el cliente manda un {@code id}, el resource lo rechaza con {@code 422}.
         */
        @Order(3)
        @Test
        void responde422_cuandoEntidadTieneIdPreasignado() {
            // Arrange
            String bodyConId = """
                    {
                        "id": "00000000-0000-0000-0000-000000000001",
                        "nombreAspirante": "No debe crearse",
                        "apellidoAspirante": "Con id preassignado",
                        "fechaRegistro": "2025-01-01T00:00:00Z",
                        "activo": false
                    }
                    """;

            // Act
            Response response = target
                    .path(PATH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyConId));

            // Assert
            assertEquals(422, response.getStatus(),
                    "Debe retornar 422 cuando la entidad trae un id pre-asignado");
            assertNotNull(response.getHeaderString("Missing-parameter"),
                    "El header Missing-parameter debe estar presente");
        }
    }

    // =========================================================================
    // GET /aspirante/{id}
    // =========================================================================

    /**
     * Pruebas del endpoint {@code GET /aspirante/{id}}.
     *
     * <p>Depende de que {@code Create.responde201_cuandoEntidadValida} haya
     * poblado {@code IDCREADO}.
     */
    @Nested
    @Order(3)
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
                    .path(PATH)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            // Assert
            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando el id existe");
            assertTrue(response.hasEntity(),
                    "La respuesta debe contener el Aspirante encontrado");
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
                    .path(PATH)
                    .path(IDINEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            // Assert
            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando el id no existe");
            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
        }
    }

    // =========================================================================
    // PUT /aspirante/{id}
    // =========================================================================

    /**
     * Pruebas del endpoint {@code PUT /aspirante/{id}}.
     *
     * <p>Opera sobre el registro creado en {@code Create}.
     * Un body vacío falla en deserialización de Liberty antes del resource
     * ({@code 400} o {@code 500}).
     */
    @Nested
    @Order(4)
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
                        "nombreAspirante": "Aspirante actualizado por prueba de sistema",
                        "apellidoAspirante": "Apellido actualizado",
                        "identificacion": "98765432-1",
                        "email": "actualizado@sistema.test",
                        "fechaNacimiento": "1999-03-20",
                        "fechaRegistro": "2025-06-01T00:00:00Z",
                        "activo": true
                    }
                    """;

            // Act
            Response response = target
                    .path(PATH)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(bodyActualizado));

            // Assert
            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando el id existe y la entidad es válida");
            assertTrue(response.hasEntity(),
                    "La respuesta debe contener el Aspirante actualizado");
        }

        /**
         * Verifica {@code 404} y header {@code Not-found} cuando el {@code id}
         * no corresponde a ningún registro.
         */
        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            // Arrange
            String body = """
                    {
                        "nombreAspirante": "No debe actualizarse",
                        "apellidoAspirante": "Inexistente",
                        "fechaRegistro": "2025-01-01T00:00:00Z",
                        "activo": false
                    }
                    """;

            // Act
            Response response = target
                    .path(PATH)
                    .path(IDINEXISTENTE.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(body));

            // Assert
            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando el id no existe");
            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
        }

        /**
         * Verifica que un body JSON vacío resulta en error de servidor.
         *
         * <p>Liberty no llega a ejecutar el método del resource porque el
         * deserializador JSON falla primero. El código real devuelto es {@code 400}
         * o {@code 500}.
         */
        @Order(3)
        @Test
        void respondeFallo_cuandoBodyJsonMalformado() {
            // Arrange
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            // Act
            Response response = target
                    .path(PATH)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity("", MediaType.APPLICATION_JSON));

            // Assert — Liberty falla en deserialización antes del resource
            assertTrue(
                    response.getStatus() == 400 || response.getStatus() == 500,
                    "Debe retornar 400 o 500 cuando el body JSON está vacío o malformado, " +
                            "fue: " + response.getStatus()
            );
        }
    }

    // =========================================================================
    // DELETE /aspirante/{id}
    // =========================================================================

    /**
     * Pruebas del endpoint {@code DELETE /aspirante/{id}}.
     *
     * <p>Se ejecuta al final porque elimina el registro identificado por {@code IDCREADO},
     * dejando la BD en estado limpio.
     */
    @Nested
    @Order(5)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class DeleteById {

        /**
         * Verifica {@code 404} y header {@code Not-Found} cuando el {@code id}
         * no corresponde a ningún registro.
         */
        @Order(1)
        @Test
        void responde404_cuandoIdNoExiste() {
            // Act
            Response response = target
                    .path(PATH)
                    .path(IDINEXISTENTE.toString())
                    .request()
                    .delete();

            // Assert
            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando el id no existe");
            assertNotNull(response.getHeaderString("Not-Found"),
                    "El header Not-Found debe estar presente");
        }

        /**
         * Verifica {@code 204 No Content} sin cuerpo cuando el {@code id} existe
         * y la eliminación es exitosa.
         *
         * <p>Se ejecuta después del {@code 404} para no destruir el registro
         * que usan {@code FindById} y {@code Update}.
         */
        @Order(2)
        @Test
        void responde204_cuandoIdExiste() {
            // Arrange
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            // Act
            Response response = target
                    .path(PATH)
                    .path(IDCREADO.toString())
                    .request()
                    .delete();

            // Assert
            assertEquals(204, response.getStatus(),
                    "Debe retornar 204 No Content al eliminar un registro existente");
            assertFalse(response.hasEntity(),
                    "La respuesta no debe contener cuerpo tras eliminar");
        }

        /**
         * Verifica {@code 404} al intentar acceder al registro ya eliminado,
         * confirmando que la eliminación fue persistida correctamente en BD.
         */
        @Order(3)
        @Test
        void responde404_cuandoSeIntentaAccederAlRegistroEliminado() {
            // Arrange
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            // Act
            Response response = target
                    .path(PATH)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            // Assert
            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 al intentar acceder a un registro ya eliminado");
        }
    }
}