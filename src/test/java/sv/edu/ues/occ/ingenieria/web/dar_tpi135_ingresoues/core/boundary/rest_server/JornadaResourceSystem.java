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
 * Pruebas de sistema para {@link JornadaResource}.
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
 *   <li>Body JSON vacío o malformado → fallo de deserialización → {@code 400} o {@code 500}.</li>
 *   <li>UUID asignado por el DAO tras {@code em.persist()} + {@code em.flush()} → disponible
 *       en el body JSON del {@code 201}.</li>
 *   <li>{@code fechaCreacion} es {@code @NotNull} — Bean Validation se ejecuta en
 *       {@code prePersist} antes del INSERT, por lo que debe incluirse en el body JSON
 *       aunque la BD tenga {@code DEFAULT now()}.</li>
 *   <li>Todos los headers de error usan {@code Not-found} (minúscula f) en todos los verbos.</li>
 * </ul>
 *
 * @see JornadaResource
 * @see BaseIntegrationAbstract
 */
@SystemTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ContainerExtension.class)
public class JornadaResourceSystem extends BaseIntegrationAbstract {

    // --- Arrange General ---

    /** Segmento de ruta que mapea a {@code @Path("jornada")} en el resource. */
    private static final String PATH = "jornada";

    /**
     * <li>UUID del registro creado en {@code Create.responde201_cuandoEntidadValida}.
     * Se comparte con {@code FindById}, {@code Update} y {@code DeleteById}.</li>
     * <li>UUID inexistente para verificar respuestas {@code 404}.</li>
     * <li>Parámetros de paginación válidos e inválidos para {@code FindRange}.</li>
     */
    private static UUID IDCREADO;
    private static final UUID IDINEXISTENTE = UUID.randomUUID();
    private static final int FIRST        = 0;
    private static final int MAX          = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX   = 101;

    /**
     * Pruebas del endpoint {@code GET /jornada}.
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
         * <p>Liberty intercepta la violación de {@code @Min(0)} con Bean Validation.
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
         * Verifica {@code 400} cuando {@code max} supera el límite de 100.
         *
         * <p>Liberty intercepta la violación de {@code @Max(100)} con Bean Validation.
         */
        @Order(3)
        @Test
        void responde400_cuandoMaxExcedeLimite() {
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
    }

    /**
     * Pruebas del endpoint {@code POST /jornada}.
     *
     * <p>El UUID es asignado por el DAO tras {@code em.persist()} + {@code em.flush()}.
     * El resource rechaza con {@code 422} si {@code entity == null} o
     * {@code entity.getId() != null}.
     *
     * <p>Importante: {@code fechaCreacion} tiene {@code @NotNull} en la entidad —
     * Bean Validation se ejecuta en {@code prePersist} antes del INSERT, por lo que
     * debe incluirse siempre en el body JSON aunque la BD tenga {@code DEFAULT now()}.
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
            // Arrange
            String bodyJson = """
                    {
                        "nombreJornada": "Jornada de prueba de sistema",
                        "fecha": "2025-12-01",
                        "horaInicio": "07:00:00",
                        "horaFin": "11:00:00",
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
                    "Debe retornar 201 Created al crear una Jornada válida");
            assertNotNull(response.getHeaderString("Location"),
                    "El header Location debe contener la URI del nuevo recurso");

            // Extraer el UUID del body JSON
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
         * <p>Liberty falla en deserialización antes del resource
         * ({@code 400} o {@code 500}).
         */
        @Order(2)
        @Test
        void respondeFallo_cuandoBodyJsonMalformado() {
            // Act
            Response response = target
                    .path(PATH)
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
         * Verifica {@code 422} cuando la entidad enviada trae un {@code id} pre-asignado.
         *
         * <p>El resource rechaza con {@code 422} y header {@code Missing-parameter}
         * si el cliente manda un {@code id}.
         */
        @Order(3)
        @Test
        void responde422_cuandoEntidadTieneIdPreasignado() {
            // Arrange
            String bodyConId = """
                    {
                        "id": "00000000-0000-0000-0000-000000000001",
                        "nombreJornada": "No debe crearse",
                        "fecha": "2025-12-01",
                        "horaInicio": "07:00:00",
                        "horaFin": "11:00:00",
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
     * Pruebas del endpoint {@code GET /jornada/{id}}.
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
                    "La respuesta debe contener la Jornada encontrada");
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

    /**
     * Pruebas del endpoint {@code PUT /jornada/{id}}.
     *
     * <p>Opera sobre el registro creado en {@code Create}.
     * El body siempre incluye {@code fechaCreacion} por el {@code @NotNull}
     * de la entidad.
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
                        "nombreJornada": "Jornada actualizada por prueba de sistema",
                        "fecha": "2025-12-15",
                        "horaInicio": "08:00:00",
                        "horaFin": "12:00:00",
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
                    "La respuesta debe contener la Jornada actualizada");
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
                        "nombreJornada": "No debe actualizarse",
                        "fecha": "2025-12-01",
                        "horaInicio": "07:00:00",
                        "horaFin": "11:00:00",
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
            // Arrange
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            // Act
            Response response = target
                    .path(PATH)
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

    /**
     * Pruebas del endpoint {@code DELETE /jornada/{id}}.
     *
     * <p>Se ejecuta al final porque elimina el registro identificado por
     * {@code IDCREADO}, dejando la BD en estado limpio.
     * Todos los headers de no encontrado usan {@code Not-found} (minúscula f).
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
            // Act
            Response response = target
                    .path(PATH)
                    .path(IDINEXISTENTE.toString())
                    .request()
                    .delete();

            // Assert
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