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
 * Pruebas de sistema para {@link AspiranteOpcioneResource}.
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
 *   <li>Es un sub-recurso bajo {@code /aspirante/{idAspirante}/opciones}.</li>
 *   <li>El {@code id} es {@code Integer} asignado por PostgreSQL via {@code serial}
 *       ({@code @GeneratedValue(strategy = IDENTITY)}) — se extrae del body del {@code 201}.</li>
 *   <li>Requiere un {@code idAspirante} válido en el path; si el aspirante no existe
 *       el resource devuelve {@code 404}.</li>
 *   <li>No tiene endpoints {@code FindRange} ni {@code DeleteById}.</li>
 * </ul>
 *
 * @see AspiranteOpcioneResource
 * @see BaseIntegrationAbstract
 */
@SystemTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ContainerExtension.class)
public class AspiranteOpcioneResourceSystem extends BaseIntegrationAbstract {

    // --- Arrange General ---

    /**
     * UUID de aspirante existente en la BD (insertado por {@code ingreso_ues_db.sql}).
     * Se usa como {@code idAspirante} válido en el path.
     */
    private static final UUID ID_ASPIRANTE_EXISTENTE =
            UUID.fromString("b2000000-0000-0000-0000-000000000001");

    /** UUID de aspirante inexistente para verificar respuestas {@code 404}. */
    private static final UUID ID_ASPIRANTE_INEXISTENTE = UUID.randomUUID();

    /**
     * Id ({@code Integer}) de la opción creada en {@code Create.responde201_cuandoEntidadValida}.
     * Se extrae del body JSON del {@code 201} y se comparte con {@code FindById} y {@code Update}.
     */
    private static Integer IDCREADO;

    /** Id inexistente para verificar respuestas {@code 404} en {@code FindById} y {@code Update}. */
    private static final Integer ID_INEXISTENTE = Integer.MAX_VALUE;

    /** Segmento base del path: {@code aspirante/{idAspirante}/opciones}. */
    private static final String PATH_BASE = "aspirante";
    private static final String PATH_OPCIONES = "opciones";

    /**
     * Pruebas del endpoint {@code POST /aspirante/{idAspirante}/opciones}.
     *
     * <p>El {@code id} es asignado por PostgreSQL via {@code serial} +
     * {@code @GeneratedValue(IDENTITY)} + {@code em.flush()}.
     * El resource rechaza con {@code 422} si {@code entity.getId() != null}
     * o si {@code idAspirante} es nulo.
     * Si el aspirante no existe en BD devuelve {@code 404}.
     */
    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Create {

        /**
         * Verifica {@code 201 Created} con entidad válida y aspirante existente.
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
                        "codigoPrograma": "ING-SIS",
                        "nombrePrograma": "Ingeniería en Sistemas Informáticos",
                        "fechaSeleccion": "2025-01-01T00:00:00Z"
                    }
                    """;

            // Act
            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_OPCIONES)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyJson));

            // Assert
            assertEquals(201, response.getStatus(),
                    "Debe retornar 201 Created al crear una opción válida");
            assertNotNull(response.getHeaderString("Location"),
                    "El header Location debe contener la URI del nuevo recurso");

            // Extraer el id Integer del body JSON
            String body = response.readEntity(String.class);
            assertNotNull(body, "El body no debe ser nulo");
            assertTrue(body.contains("\"id\""), "El body debe contener el campo id");

            int idStart = body.indexOf("\"id\":") + 5;
            int idEnd = body.indexOf(",", idStart);
            if (idEnd == -1) idEnd = body.indexOf("}", idStart);
            IDCREADO = Integer.parseInt(body.substring(idStart, idEnd).trim());
            assertNotNull(IDCREADO, "El id extraído del body no debe ser nulo");
        }

        /**
         * Verifica que un body JSON vacío resulta en error de servidor.
         *
         * <p>Liberty no llega al método del resource porque el deserializador
         * JSON falla primero ({@code 400} o {@code 500}).
         */
        @Order(2)
        @Test
        void respondeFallo_cuandoBodyJsonMalformado() {
            // Act
            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_OPCIONES)
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
         * <p>El contrato exige {@code entity.id == null}; si el cliente manda
         * un {@code id}, el resource lo rechaza con {@code 422}.
         */
        @Order(3)
        @Test
        void responde422_cuandoEntidadTieneIdPreasignado() {
            // Arrange
            String bodyConId = """
                    {
                        "id": 99999,
                        "codigoPrograma": "NO-CREAR",
                        "nombrePrograma": "No debe crearse",
                        "fechaSeleccion": "2025-01-01T00:00:00Z"
                    }
                    """;

            // Act
            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_OPCIONES)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(bodyConId));

            // Assert
            assertEquals(422, response.getStatus(),
                    "Debe retornar 422 cuando la entidad trae un id pre-asignado");
            assertNotNull(response.getHeaderString("Missing-parameter"),
                    "El header Missing-parameter debe estar presente");
        }

        /**
         * Verifica {@code 404} cuando el {@code idAspirante} del path no existe en BD.
         *
         * <p>El resource busca el aspirante antes de persistir la opción;
         * si no lo encuentra devuelve {@code 404}.
         */
        @Order(4)
        @Test
        void responde404_cuandoAspiranteNoExiste() {
            // Arrange
            String body = """
                    {
                        "codigoPrograma": "NO-CREAR",
                        "nombrePrograma": "No debe crearse",
                        "fechaSeleccion": "2025-01-01T00:00:00Z"
                    }
                    """;

            // Act
            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_INEXISTENTE.toString())
                    .path(PATH_OPCIONES)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            // Assert
            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando el aspirante no existe");
            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
        }
    }

    /**
     * Pruebas del endpoint {@code GET /aspirante/{idAspirante}/opciones/{id}}.
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
                    .path(PATH_OPCIONES)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            // Assert
            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando el id existe");
            assertTrue(response.hasEntity(),
                    "La respuesta debe contener la opción encontrada");
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
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_OPCIONES)
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
     * Pruebas del endpoint {@code PUT /aspirante/{idAspirante}/opciones/{id}}.
     *
     * <p>Opera sobre el registro creado en {@code Create}.
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
                        "codigoPrograma": "ING-IND",
                        "nombrePrograma": "Ingeniería Industrial actualizada",
                        "fechaSeleccion": "2025-06-01T00:00:00Z"
                    }
                    """;

            // Act
            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_OPCIONES)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(bodyActualizado));

            // Assert
            assertEquals(200, response.getStatus(),
                    "Debe retornar 200 cuando el id existe y la entidad es válida");
            assertTrue(response.hasEntity(),
                    "La respuesta debe contener la opción actualizada");
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
                        "codigoPrograma": "NO-ACT",
                        "nombrePrograma": "No debe actualizarse",
                        "fechaSeleccion": "2025-01-01T00:00:00Z"
                    }
                    """;

            // Act
            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_OPCIONES)
                    .path(ID_INEXISTENTE.toString())
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
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_EXISTENTE.toString())
                    .path(PATH_OPCIONES)
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

        /**
         * Verifica {@code 404} cuando el {@code idAspirante} del path no existe en BD.
         */
        @Order(4)
        @Test
        void responde404_cuandoAspiranteNoExiste() {
            // Arrange
            assertNotNull(IDCREADO,
                    "IDCREADO debe estar poblado por Create.responde201_cuandoEntidadValida");

            String body = """
                    {
                        "codigoPrograma": "NO-ACT",
                        "nombrePrograma": "No debe actualizarse",
                        "fechaSeleccion": "2025-01-01T00:00:00Z"
                    }
                    """;

            // Act
            Response response = target
                    .path(PATH_BASE)
                    .path(ID_ASPIRANTE_INEXISTENTE.toString())
                    .path(PATH_OPCIONES)
                    .path(IDCREADO.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(body));

            // Assert
            assertEquals(404, response.getStatus(),
                    "Debe retornar 404 cuando el aspirante no existe");
            assertNotNull(response.getHeaderString("Not-found"),
                    "El header Not-found debe estar presente");
        }
    }
}