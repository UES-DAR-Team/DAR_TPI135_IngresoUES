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
 * Pruebas de sistema para {@link DistractorResource}.
 *
 * <p>Levantan un entorno real con Docker (PostgreSQL + OpenLiberty) y verifican
 * el comportamiento del API REST de extremo a extremo.
 *
 * <p>Convenciones:
 * <ul>
 *   <li>Un {@code @Nested} por endpoint, ordenado con {@code @TestClassOrder}.</li>
 *   <li>Nombre de metodo: {@code respondeXXX_cuandoCondicion}.</li>
 *   <li>Patrón AAA (Arrange / Act / Assert) en cada test.</li>
 *   <li>{@code idCreado} se puebla en {@code Create} y se reutiliza en
 *       {@code FindById}, {@code Update} y {@code DeleteById}.</li>
 * </ul>
 *
 * <p>Comportamientos reales verificados contra Liberty 25:
 * <ul>
 *   <li>Bean Validation ({@code @Min}/{@code @Max}) interceptada por Liberty → {@code 400}.</li>
 *   <li>Body JSON vacío o malformado → fallo de deserialización en Liberty → {@code 400} o {@code 500}
 *       (no llega al metodo del resource).</li>
 *   <li>UUID asignado por el DAO tras {@code em.persist()} + {@code em.flush()} → disponible
 *       en el body JSON del {@code 201}.</li>
 * </ul>
 *
 * @see DistractorResource
 * @see BaseIntegrationAbstract
 */
@SystemTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ContainerExtension.class)
public class DistractorResourceSystem extends BaseIntegrationAbstract {

    //Arrange General
    /** Segmento de ruta que mapea a {@code @Path("distractor")} en el resource. */
    private static final String PATH = "distractor";

    //se modifico la entidad para que el id se genere con @GeneratedValue(strategy = GenerationType.UUID)
    //y se asigna en el DAO tras em.persist() + em.flush(), por lo que el UUID del registro creado se obtiene
    // del body JSON de la respuesta al crear, no es un valor fijo predefinido. Se declara como variable de clase para compartirlo entre los tests anidados.

    /**
     * <li>UUID del registro creado en {@code Create.responde201_cuandoEntidadValida}.
     * Se comparte con {@code FindById}, {@code Update} y {@code DeleteById}.</li>
     * <li>UUID del id inexistente utilizado en {@code DeleteById}, {@code Update} y {@code FindById},
     * para la comprobacion del registro inexistente para la devolucion del {@code 404}</li>
     * <li>int del First, parametro de paginacion valido para la busqueda en {@code FindRange}</li>
     * <li>int del Max, parametro de paginacion valido para la busqueda en {@code FindRange}</li>
     * <li>int del INVALIDFIRST, parametro de paginacion Invalido para la busqueda en {@code FindRange}</li>
     * <li>int del INVALIDMAX, parametro de paginacion Invalido para la busqueda en {@code FindRange}</li>
     * <li>int del EXCEEDMAX, parametro de paginacion Excedido para la busqueda en {@code FindRange}</li>
     */
    private static UUID IDCREADO;
    private static final UUID IDINEXISTENTE= UUID.randomUUID();
    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final int EXCEEDMAX = 11;

    // GET /distractor?first=&max=
    /**
     * Pruebas del endpoint {@code GET /distractor}.
     *
     * <p>Verifica paginación con parámetros válidos e inválidos.
     * Las violaciones de {@code @Min}/{@code @Max} son interceptadas por
     * Bean Validation de Liberty antes de entrar al metodo, devolviendo {@code 400}.
     */
    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindRange {

        /**
         * Verifica {@code 200 OK} con parámetros válidos, cuerpo JSON presente
         * y header {@code X-Total-Count} incluido.
         */
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
            assertNotNull(response.getHeaderString("X-Total-Count"),
                    "El header X-Total-Count debe estar presente");
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
         * Verifica {@code 400} cuando {@code max} es negativo.
         *
         * <p>Liberty intercepta la violación de {@code @Min(1)} con Bean Validation.
         */
        @Order(3)
        @Test
        void responde400_cuandoMaxNegativo() {
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
         * Verifica {@code 400} cuando {@code max} supera el límite de 10.
         *
         * <p>Liberty intercepta la violación de {@code @Max(10)} con Bean Validation.
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

    /**
     * Pruebas del endpoint {@code POST /distractor}.
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
         * y se almacena en {@code idCreado} para los {@code @Nested} posteriores.
         */
        @Order(1)
        @Test
        void responde201_cuandoEntidadValida() {
            // Arrange
            String bodyJson = """
                    {
                        "contenidoDistractor": "Distractor de prueba de sistema",
                        "esCorrecto": false,
                        "fechaCreacion": "2025-01-01T00:00:00Z",
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
                    "Debe retornar 201 Created al crear un Distractor válido");
            assertNotNull(response.getHeaderString("Location"),
                    "El header Location debe contener la URI del nuevo recurso");

            // Extraer el UUID del body JSON y almacenarlo para tests posteriores
            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.contains("\"id\""), "El body debe contener el campo id");

            int idStart = body.indexOf("\"id\":\"") + 6;
            int idEnd = body.indexOf("\"", idStart);
            IDCREADO = UUID.fromString(body.substring(idStart, idEnd));
            assertNotNull(IDCREADO, "El UUID extraído del body no debe ser nulo");
        }

        /**
         * Verifica que un body JSON malformado (vacío) resulta en error de servidor.
         *
         * <p>Liberty no llega a ejecutar el método del resource porque el deserializador
         * JSON falla primero. El código real devuelto es {@code 400} o {@code 500}
         * dependiendo de la versión de Liberty y la implementación JAX-RS.
         */
        @Order(2)
        @Test
        void respondeFallo_cuandoBodyJsonMalformado() {
            // Arrange — string vacío es JSON inválido
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
                        "contenidoDistractor": "No debe crearse",
                        "esCorrecto": false,
                        "fechaCreacion": "2025-01-01T00:00:00Z",
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

    /**
     * Pruebas del endpoint {@code GET /distractor/{id}}.
     *
     * <p>Depende de que {@code Create.responde201_cuandoEntidadValida} haya
     * poblado {@code idCreado}.
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
                    "idCreado debe estar poblado por Create.responde201_cuandoEntidadValida");

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
                    "La respuesta debe contener el Distractor encontrado");
        }

        /**
         * Verifica {@code 404} y header {@code Not-found-id} cuando el {@code id}
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
            assertNotNull(response.getHeaderString("Not-found-id"),
                    "El header Not-found-id debe estar presente");
        }
    }

    /**
     * Pruebas del endpoint {@code PUT /distractor/{id}}.
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
                    "idCreado debe estar poblado por Create.responde201_cuandoEntidadValida");

            String bodyActualizado = """
                    {
                        "contenidoDistractor": "Distractor actualizado por prueba de sistema",
                        "esCorrecto": true,
                        "fechaCreacion": "2025-06-01T00:00:00Z",
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
                    "La respuesta debe contener el Distractor actualizado");
        }

        /**
         * Verifica {@code 404} y header {@code Not-found-id} cuando el {@code id}
         * no corresponde a ningún registro.
         */
        @Order(2)
        @Test
        void responde404_cuandoIdNoExiste() {
            String body = """
                    {
                        "contenidoDistractor": "No debe actualizarse",
                        "esCorrecto": false,
                        "fechaCreacion": "2025-01-01T00:00:00Z",
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
            assertNotNull(response.getHeaderString("Not-found-id"),
                    "El header Not-found-id debe estar presente");
        }

        /**
         * Verifica que un body JSON malformado (vacío) resulta en error de servidor.
         *
         * <p>Liberty no llega a ejecutar el metodo del resource porque el deserializador
         * JSON falla primero. El código real devuelto es {@code 400} o {@code 500}.
         */
        @Order(3)
        @Test
        void respondeFallo_cuandoBodyJsonMalformado() {
            // Arrange
            assertNotNull(IDCREADO,
                    "idCreado debe estar poblado por Create.responde201_cuandoEntidadValida");

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

    /**
     * Pruebas del endpoint {@code DELETE /distractor/{id}}.
     *
     * <p>Se ejecuta al final porque elimina el registro identificado por {@code idCreado},
     * dejando la BD en estado limpio.
     */
    @Nested
    @Order(5)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class DeleteById {

        /**
         * Verifica {@code 404} y header {@code Not-found-id} cuando el {@code id}
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
            assertNotNull(response.getHeaderString("Not-found-id"),
                    "El header Not-found-id debe estar presente");
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
                    "idCreado debe estar poblado por Create.responde201_cuandoEntidadValida");

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
                    "idCreado debe estar poblado por Create.responde201_cuandoEntidadValida");

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