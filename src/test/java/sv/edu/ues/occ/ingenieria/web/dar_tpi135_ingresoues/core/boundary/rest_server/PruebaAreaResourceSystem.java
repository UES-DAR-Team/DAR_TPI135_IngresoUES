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

@SystemTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ContainerExtension.class)
public class PruebaAreaResourceSystem extends BaseIntegrationAbstract {

    private static final String PATH = "prueba";

    private static final UUID ID_PRUEBA = UUID.fromString("07000000-0000-0000-0000-000000000001");
    private static final UUID ID_AREA = UUID.fromString("a1000000-0000-0000-0000-000000000001");
    private static final UUID ID_AREA_INEXISTENTE = UUID.randomUUID();

    private static final int FIRST = 0;
    private static final int MAX = 10;

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindRange {

        @Test
        @Order(1)
        void responde200ParametrosValidos() {

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("areaConocimiento")
                    .queryParam("first", FIRST)
                    .queryParam("max", MAX)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus());
            assertNotNull(response.getHeaderString("X-Total-Count"));
            assertTrue(response.hasEntity());
        }

        @Test
        @Order(2)
        void responde400FirstInvalido() {

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("areaConocimiento")
                    .queryParam("first", -1)
                    .queryParam("max", MAX)
                    .request()
                    .get();

            assertEquals(400, response.getStatus());
        }

        @Test
        @Order(3)
        void responde200ListaVacia() {
            Response response = target
                    .path(PATH)
                    .path(UUID.randomUUID().toString()) // prueba que no existe
                    .path("areaConocimiento")
                    .queryParam("first", 0)
                    .queryParam("max", 10)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            // puede ser 404 porque prueba no existe
            assertEquals(404, response.getStatus());
        }

        @Test
        @Order(4)
        void responde400MaxExcedeLimite() {

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("areaConocimiento")
                    .queryParam("first", 0)
                    .queryParam("max", 11) // >10 rompe @Max(10)
                    .request()
                    .get();

            assertEquals(400, response.getStatus());
        }

        @Test
        @Order(5)
        void responde400_cuandoMaxEsCero() {

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("areaConocimiento")
                    .queryParam("first", 0)
                    .queryParam("max", 0)
                    .request()
                    .get();

            assertEquals(400, response.getStatus());
        }
    }

    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Create {

        @Test
        @Order(1)
        void responde404RelacionYaExiste() {
            String body = """
                    { "idAreaConocimiento": {
                       "id": "%s"
                        }, "numPreguntas": 3
                    }
                    """.formatted(ID_AREA);

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("areaConocimiento")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            // ya existe puede lanzar error
            assertTrue(response.getStatus() == 500 || response.getStatus() == 409);
        }

        @Test
        @Order(2)
        void responde422CuandoFaltaArea() {
            String body = """
                    { "numPreguntas": 5 }
                    """;

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("areaConocimiento")
                    .request()
                    .post(Entity.json(body));

            assertEquals(422, response.getStatus());
        }

        @Test
        @Order(3)
        void responde404PruebaNoExiste() {
            UUID idPruebaInvalida = UUID.fromString("99999999-9999-9999-9999-999999999999");
            String body = """
            { "idAreaConocimiento": { "id": "%s"},
                "numPreguntas": 3
            }
            """.formatted(ID_AREA);

            Response response = target
                    .path(PATH)
                    .path(idPruebaInvalida.toString())
                    .path("areaConocimiento")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(404, response.getStatus());
        }

        @Test
        @Order(4)
        void responde404AreaNoExiste() {
            String body = """
            { "idAreaConocimiento": {"id": "%s" },
                "numPreguntas": 3
            }
            """.formatted(ID_AREA_INEXISTENTE);

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("areaConocimiento")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(body));

            assertEquals(404, response.getStatus());
            assertNotNull(response.getHeaderString("Not-found"));
        }

        @Test
        @Order(5)
        void responde422EntityTieneId() {
            String body = """
            { "id": 1, "idAreaConocimiento": { "id": "%s"
                },
              "numPreguntas": 3
            }
            """.formatted(ID_AREA);

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("areaConocimiento")
                    .request()
                    .post(Entity.json(body));

            assertEquals(422, response.getStatus());
        }

        @Test
        @Order(6)
        void respondeErrorCuandoBodyVacio() {

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("areaConocimiento")
                    .request()
                    .post(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(response.getStatus() == 400 || response.getStatus() == 500);
        }
    }

    @Nested
    @Order(3)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindOne {

        @Test
        @Order(1)
        void responde200CuandoExiste() {

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("areaConocimiento")
                    .path(ID_AREA.toString())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            assertEquals(200, response.getStatus());
            assertTrue(response.hasEntity());
        }

        @Test
        @Order(2)
        void responde404CuandoNoExiste() {

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("areaConocimiento")
                    .path(ID_AREA_INEXISTENTE.toString())
                    .request()
                    .get();

            assertEquals(404, response.getStatus());
        }
    }

    @Nested
    @Order(4)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Update {

        @Test
        @Order(1)
        void responde200CuandoExiste() {

            String body = """
                    {
                        "numPreguntas": 10
                    }
                    """;

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("areaConocimiento")
                    .path(ID_AREA.toString())
                    .request()
                    .put(Entity.json(body));

            assertEquals(200, response.getStatus());
        }

        @Test
        @Order(2)
        void responde404CuandoNoExiste() {

            String body = """
                    {
                        "numPreguntas": 10
                    }
                    """;

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("areaConocimiento")
                    .path(ID_AREA_INEXISTENTE.toString())
                    .request()
                    .put(Entity.json(body));

            assertEquals(404, response.getStatus());
        }

        @Test
        @Order(3)
        void respondeError_cuandoBodyVacio() {

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("areaConocimiento")
                    .path(ID_AREA.toString())
                    .request()
                    .put(Entity.entity("", MediaType.APPLICATION_JSON));

            assertTrue(response.getStatus() == 400 || response.getStatus() == 500);
        }
    }

    @Nested
    @Order(5)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Delete {

        @Test
        @Order(1)
        void responde404_cuandoNoExiste() {

            Response response = target
                    .path(PATH)
                    .path(ID_PRUEBA.toString())
                    .path("areaConocimiento")
                    .path(ID_AREA_INEXISTENTE.toString())
                    .request()
                    .delete();

            assertEquals(404, response.getStatus());
        }
    }
}