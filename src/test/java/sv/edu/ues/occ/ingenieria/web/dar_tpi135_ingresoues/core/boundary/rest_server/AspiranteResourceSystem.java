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
 *   <li>Cada Assert verifica: status → headers → body.</li>
 *   <li>{@code IDCREADO} se puebla en {@code Create} y se reutiliza en
 *       {@code FindById}, {@code Update} y {@code DeleteById}.</li>
 * </ul>
 *
 * <p>Comportamientos reales verificados contra Liberty 25:
 * <ul>
 *   <li>Bean Validation ({@code @Min}/{@code @Max}) interceptada por Liberty → {@code 400}.</li>
 *   <li>Body JSON vacío o malformado → fallo de deserialización en Liberty → {@code 400} o {@code 500}
 *       (no llega al método del resource).</li>
 *   <li>UUID asignado por el DAO tras {@code em.persist()} → disponible
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

    /** Segmento de ruta que mapea a {@code @Path("aspirante")} en el resource. */
    private static final String PATH = "aspirante";

    /**
     * UUID del registro creado en {@code Create.responde201_cuandoEntidadValida}.
     * Se comparte con {@code FindById}, {@code Update} y {@code DeleteById}.
     */
    private static UUID IDCREADO;

    /**
     * UUID inexistente utilizado en {@code DeleteById}, {@code Update} y {@code FindById}
     * para verificar la devolución del {@code 404}.
     */
    private static final UUID IDINEXISTENTE = UUID.randomUUID();

    /** Parámetro de paginación válido — primera página. */
    private static final int FIRST        = 0;

    /** Parámetro de paginación válido — tamaño de página. */
    private static final int MAX          = 10;

    /** Parámetro inválido — {@code first} negativo, viola {@code @Min(0)}. */
    private static final int INVALIDFIRST = -1;

    /** Parámetro inválido — {@code max} cero, viola {@code @Min(1)}. */
    private static final int INVALIDMAX   = 0;

    /** Parámetro inválido — {@code max} excede el límite, viola {@code @Max(100)}. */
    private static final int EXCEEDMAX    = 101;

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
         * Verifica {@code 200 OK} con parámetros válidos.
         *
         * <p>Comprueba que:
         * <ul>
         *   <li>El status sea {@code 200}.</li>
         *   <li>El header {@code Total-records} esté presente y sea un número {@code >= 0}.</li>
         *   <li>El body sea un array JSON (empieza con {@code [}).</li>
         * </ul>
         */
        @Order(1)
        @Test
        void responde200_cuandoParametrosValidos() {
            Response response = target
                    .path(PATH)
                    .queryParam("first", FIRST)
                    .queryParam("max", MAX)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 con parámetros válidos");

            String totalRecords = response.getHeaderString("Total-records");
            assertNotNull(totalRecords,
                    "El header Total-records debe estar presente");
            assertTrue(Integer.parseInt(totalRecords) >= 0,
                    "Total-records debe ser un número mayor o igual a 0");

            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.startsWith("["),
                    "El body debe ser un array JSON");
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
            Response response = target
                    .path(PATH)
                    .queryParam("first", INVALIDFIRST)
                    .queryParam("max", MAX)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(400, response.getStatus(),
                    "Liberty debe interceptar @Min(0) y retornar 400");
        }

        /**
         * Verifica {@code 400} cuando {@code max} es cero.
         *
         * <p>Liberty intercepta la violación de {@code @Min(1)} con Bean Validation.
         */
        @Order(3)
        @Test
        void responde400_cuandoMaxCero() {
            Response response = target
                    .path(PATH)
                    .queryParam("first", FIRST)
                    .queryParam("max", INVALIDMAX)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(400, response.getStatus(),
                    "Liberty debe interceptar @Min(1) y retornar 400");
        }

        /**
         * Verifica {@code 400} cuando {@code max} supera el límite de 100.
         *
         * <p>Liberty intercepta la violación de {@code @Max(100)} con Bean Validation.
         */
        @Order(4)
        @Test
        void responde400_cuandoMaxExcedeLimite() {
            Response response = target
                    .path(PATH)
                    .queryParam("first", FIRST)
                    .queryParam("max", EXCEEDMAX)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(400, response.getStatus(),
                    "Liberty debe interceptar @Max(100) y retornar 400");
        }
    }

    /**
     * Pruebas del endpoint {@code POST /aspirante}.
     *
     * <p>El resource verifica que {@code entity.getId() == null} antes de persistir;
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
         * <p>Comprueba que:
         * <ul>
         *   <li>El status sea {@code 201}.</li>
         *   <li>El header {@code Location} esté presente.</li>
         *   <li>El body contenga el {@code id} generado por el servidor.</li>
         *   <li>El body contenga el {@code nombreAspirante} y {@code apellidoAspirante} enviados.</li>
         *   <li>El UUID extraído del body sea válido y se almacene en {@code IDCREADO}.</li>
         * </ul>
         */
        @Order(1)
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

            assertEquals(201, response.getStatus(),
                    "Debe retornar 201 Created al crear un Aspirante válido");

            assertNotNull(response.getHeaderString("Location"),
                    "El header Location debe estar presente");

            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.contains("\"id\""),
                    "El body debe contener el campo id generado por el servidor");
            assertTrue(body.contains("Aspirante de prueba de sistema"),
                    "El body debe contener el nombreAspirante enviado");
            assertTrue(body.contains("Apellido prueba"),
                    "El body debe contener el apellidoAspirante enviado");

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
            Response response = target
                    .path(PATH)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(
                    response.getStatus() == 400 || response.getStatus() == 500,
                    "Debe retornar 400 o 500 con body vacío, fue: "
                            + response.getStatus()
            );
        }

        /**
         * Verifica {@code 422} cuando la entidad enviada trae un {@code id} pre-asignado.
         *
         * <p>El contrato del endpoint exige {@code entity.id == null} para que el servidor
         * asigne el UUID. Si el cliente manda un {@code id}, el resource lo rechaza con {@code 422}.
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
                        "id": "00000000-0000-0000-0000-000000000001",
                        "nombreAspirante": "No debe crearse",
                        "apellidoAspirante": "Con id preasignado",
                        "fechaRegistro": "2025-01-01T00:00:00Z",
                        "activo": false
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
         * Verifica {@code 200 OK} cuando el {@code id} existe.
         *
         * <p>Comprueba que:
         * <ul>
         *   <li>El status sea {@code 200}.</li>
         *   <li>El body contenga el {@code id} consultado.</li>
         *   <li>El body contenga el nombre del aspirante creado.</li>
         * </ul>
         */
        @Order(1)
        @Test
        void responde200_cuandoIdExiste() {
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            Response response = target
                    .path(PATH)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando el id existe");

            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.contains(IDCREADO.toString()),
                    "El body debe contener el id consultado");
            assertTrue(body.contains("Aspirante de prueba de sistema"),
                    "El body debe contener el nombre del aspirante creado");
        }

        /**
         * Verifica {@code 404} y header {@code Not-found} cuando el {@code id}
         * no corresponde a ningún registro.
         */
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

            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
        }
    }

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
         * Verifica {@code 200 OK} cuando el {@code id} existe y la entidad es válida.
         *
         * <p>Comprueba que:
         * <ul>
         *   <li>El status sea {@code 200}.</li>
         *   <li>El body contenga el nombre y apellido actualizados.</li>
         *   <li>El body conserve el mismo {@code id} del aspirante.</li>
         * </ul>
         */
        @Order(1)
        @Test
        void responde200_cuandoIdExisteYEntidadValida() {
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

            Response response = target
                    .path(PATH)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(bodyActualizado));

            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando el id existe y la entidad es válida");

            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.contains("Aspirante actualizado por prueba de sistema"),
                    "El body debe contener el nombre actualizado");
            assertTrue(body.contains("Apellido actualizado"),
                    "El body debe contener el apellido actualizado");
            assertTrue(body.contains(IDCREADO.toString()),
                    "El body debe conservar el mismo id del aspirante");
        }

        /**
         * Verifica {@code 404} y header {@code Not-found} cuando el {@code id}
         * no corresponde a ningún registro.
         */
        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            String body = """
                    {
                        "nombreAspirante": "No debe actualizarse",
                        "apellidoAspirante": "Inexistente",
                        "fechaRegistro": "2025-01-01T00:00:00Z",
                        "activo": false
                    }
                    """;

            Response response = target
                    .path(PATH)
                    .path(IDINEXISTENTE.toString())
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
         * <p>Liberty no llega a ejecutar el método del resource porque el
         * deserializador JSON falla primero. El código real devuelto es {@code 400}
         * o {@code 500}.
         */
        @Order(3)
        @Test
        void respondeFallo_cuandoBodyJsonMalformado() {
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            Response response = target
                    .path(PATH)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(
                    response.getStatus() == 400 || response.getStatus() == 500,
                    "Debe retornar 400 o 500 con body vacío, fue: "
                            + response.getStatus()
            );
        }
    }

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
         * Verifica {@code 404} y header {@code Not-found} cuando el {@code id}
         * no corresponde a ningún registro.
         */
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

            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
        }

        /**
         * Verifica {@code 204 No Content} sin cuerpo cuando el {@code id} existe
         * y la eliminación es exitosa.
         *
         * <p>Se ejecuta después del {@code 404} para no destruir el registro
         * que usan {@code FindById} y {@code Update}.
         *
         * <p>Comprueba que:
         * <ul>
         *   <li>El status sea {@code 204}.</li>
         *   <li>La respuesta no tenga cuerpo.</li>
         * </ul>
         */
        @Order(2)
        @Test
        void responde204_cuandoIdExiste() {
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

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

        /**
         * Verifica {@code 404} al intentar acceder al registro ya eliminado,
         * confirmando que la eliminación fue persistida correctamente en BD.
         *
         * <p>Comprueba que:
         * <ul>
         *   <li>El status sea {@code 404}.</li>
         *   <li>El header {@code Not-found} esté presente.</li>
         * </ul>
         */
        @Order(3)
        @Test
        void responde404_cuandoSeIntentaAccederAlRegistroEliminado() {
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            Response response = target
                    .path(PATH)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 al intentar acceder a un registro ya eliminado");

            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente tras eliminar");
        }
    }
}