package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PreguntaAreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PreguntaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PreguntaAreaConocimiento;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;
import testing.NeedsLiberty;

import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
@NeedsLiberty
public class PreguntaAreaConocimientoResourceIT extends BaseIntegrationAbstract {

    private PreguntaAreaConocimientoResource cut;
    private PreguntaAreaConocimientoDAO dao;
    private PreguntaAreaConocimientoDAO daoNoEm;
    private AreaConocimientoDAO areaDao;
    private PreguntaDAO preguntaDao;
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();

        // DAO en memoria para PreguntaAreaConocimiento
        dao = new PreguntaAreaConocimientoDAO() {
            private int seq = 1;
            private final java.util.List<PreguntaAreaConocimiento> storage = new java.util.ArrayList<>();

            @Override
            public void create(PreguntaAreaConocimiento obj) {
                if (obj.getId() == null) {
                    obj.setId(seq++);
                }
                if (obj.getFechaAsignacion() == null) {
                    obj.setFechaAsignacion(OffsetDateTime.now());
                }
                storage.add(obj);
            }

            @Override
            public java.util.List<PreguntaAreaConocimiento> findPreguntaByIdAreaConocimiento(final UUID idAreaConocimiento, int first, int max) {
                java.util.List<PreguntaAreaConocimiento> result = new java.util.ArrayList<>();
                for (PreguntaAreaConocimiento pac : storage) {
                    if (pac.getIdAreaConocimiento() != null && idAreaConocimiento.equals(pac.getIdAreaConocimiento().getId())) {
                        result.add(pac);
                    }
                }
                int toIndex = Math.min(first + max, result.size());
                if (first >= result.size()) return java.util.Collections.emptyList();
                return result.subList(first, toIndex);
            }

            @Override
            public int count() {
                return storage.size();
            }

            @Override
            public void delete(PreguntaAreaConocimiento obj) {
                storage.removeIf(p -> p.getId() != null && p.getId().equals(obj.getId()));
            }
        };

        // mantener daoNoEm como instancia real (sin em) para simular excepciones del DAO
        daoNoEm = new PreguntaAreaConocimientoDAO();

        // DAO en memoria para AreaConocimiento
        areaDao = new AreaConocimientoDAO() {
            private final java.util.Map<UUID, AreaConocimiento> map = new java.util.HashMap<>();

            @Override
            public void create(AreaConocimiento obj) {
                if (obj.getId() == null) {
                    obj.setId(UUID.randomUUID());
                }
                if (obj.getFechaCreacion() == null) {
                    obj.setFechaCreacion(OffsetDateTime.now());
                }
                map.put(obj.getId(), obj);
            }

            @Override
            public AreaConocimiento findById(Object id) {
                if (id == null) return null;
                return map.get((UUID) id);
            }
        };

        // DAO en memoria para Pregunta
        preguntaDao = new PreguntaDAO() {
            private final java.util.Map<UUID, Pregunta> map = new java.util.HashMap<>();

            @Override
            public void create(Pregunta obj) {
                if (obj.getId() == null) {
                    obj.setId(UUID.randomUUID());
                }
                if (obj.getFechaCreacion() == null) {
                    obj.setFechaCreacion(OffsetDateTime.now());
                }
                if (obj.getActivo() == null) {
                    obj.setActivo(Boolean.TRUE);
                }
                map.put(obj.getId(), obj);
            }

            @Override
            public Pregunta findById(Object id) {
                if (id == null) return null;
                return map.get((UUID) id);
            }
        };

        cut = new PreguntaAreaConocimientoResource();
        cut.preguntaAreaConocimientoDAO = dao;
        cut.areaConocimientoDAO = areaDao;
        cut.preguntaDAO = preguntaDao;
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    @Nested
    class FindRange {

        @Order(1)
        @Test
        public void findRange_happy() {
            // crear area, pregunta y asociacion
            em.getTransaction().begin();
            AreaConocimiento area = new AreaConocimiento();
            area.setId(UUID.randomUUID());
            area.setNombre("Area PAC");
            area.setActivo(true);
            area.setFechaCreacion(OffsetDateTime.now());
            areaDao.create(area);

            Pregunta p = new Pregunta();
            p.setContenidoPregunta("Contenido");
            p.setFechaCreacion(OffsetDateTime.now());
            p.setActivo(true);
            preguntaDao.create(p);

            PreguntaAreaConocimiento pac = new PreguntaAreaConocimiento();
            pac.setIdPregunta(p);
            pac.setIdAreaConocimiento(area);
            pac.setFechaAsignacion(OffsetDateTime.now());
            dao.create(pac);
            em.getTransaction().commit();

            Response response = cut.findRange(area.getId(), 0, 10);
            assertEquals(200, response.getStatus());
            assertNotNull(response.getEntity());
            List<?> lista = (List<?>) response.getEntity();
            assertFalse(lista.isEmpty());
            assertNotNull(response.getHeaderString("X-Total-Count"));
        }

        @Order(2)
        @Test
        public void findRange_invalidParams_returns422() {
            Response response = cut.findRange(UUID.randomUUID(), -1, 0);
            assertEquals(422, response.getStatus());
            assertEquals("first,max", response.getHeaderString("Missing-parameter"));
        }

        @Order(3)
        @Test
        public void findRange_areaNotFound_returns404() {
            // area inexistente
            Response response = cut.findRange(UUID.randomUUID(), 0, 10);
            assertEquals(404, response.getStatus());
            assertTrue(response.getHeaderString("Not-found").contains("AreaConocimiento with id"));
        }

        @Order(4)
        @Test
        public void findRange_daoThrows_returns500() {
            cut.preguntaAreaConocimientoDAO = daoNoEm; // dao sin em lanzará IllegalStateException
            // crear area válido para pasar la validación previa
            em.getTransaction().begin();
            AreaConocimiento area = new AreaConocimiento();
            area.setId(UUID.randomUUID());
            area.setNombre("Area PAC 2");
            area.setActivo(true);
            area.setFechaCreacion(OffsetDateTime.now());
            areaDao.create(area);
            em.getTransaction().commit();

            Response response = cut.findRange(area.getId(), 0, 10);
            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
        }
    }

    @Nested
    class FindOne {

        @Order(5)
        @Test
        public void findOne_happy() {
            em.getTransaction().begin();
            AreaConocimiento area = new AreaConocimiento();
            UUID areaId = UUID.randomUUID();
            area.setId(areaId);
            area.setNombre("Area FindOne");
            area.setActivo(true);
            area.setFechaCreacion(OffsetDateTime.now());
            areaDao.create(area);

            Pregunta p = new Pregunta();
            UUID pid = UUID.randomUUID();
            p.setId(pid);
            p.setContenidoPregunta("Pregunta FindOne");
            p.setFechaCreacion(OffsetDateTime.now());
            p.setActivo(true);
            preguntaDao.create(p);

            PreguntaAreaConocimiento pac = new PreguntaAreaConocimiento();
            pac.setIdPregunta(p);
            pac.setIdAreaConocimiento(area);
            pac.setFechaAsignacion(OffsetDateTime.now());
            dao.create(pac);
            em.getTransaction().commit();

            Response response = cut.findOne(areaId, pid);
            assertEquals(200, response.getStatus());
            assertNotNull(response.getEntity());
            assertInstanceOf(PreguntaAreaConocimiento.class, response.getEntity());
            PreguntaAreaConocimiento entidad = (PreguntaAreaConocimiento) response.getEntity();
            assertEquals(pid, entidad.getIdPregunta().getId());
        }

        @Order(6)
        @Test
        public void findOne_nullIds_returns422() {
            Response response = cut.findOne(null, null);
            assertEquals(422, response.getStatus());
            assertEquals("idAreaConocimiento,idPregunta", response.getHeaderString("Missing-parameter"));
        }

        @Order(7)
        @Test
        public void findOne_notFound_returns404() {
            UUID a = UUID.randomUUID();
            UUID p = UUID.randomUUID();
            Response response = cut.findOne(a, p);
            assertEquals(404, response.getStatus());
            assertEquals("Record linking area " + a + " and pregunta " + p + " not found", response.getHeaderString("Not-found-id"));
        }

        @Order(8)
        @Test
        public void findOne_daoThrows_returns500() {
            cut.preguntaAreaConocimientoDAO = daoNoEm;
            Response response = cut.findOne(UUID.randomUUID(), UUID.randomUUID());
            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
        }
    }

    @Nested
    class Create {

        @Order(9)
        @Test
        public void create_happy() {
            em.getTransaction().begin();
            AreaConocimiento area = new AreaConocimiento();
            area.setId(UUID.randomUUID());
            area.setNombre("Area Create");
            area.setActivo(true);
            area.setFechaCreacion(OffsetDateTime.now());
            areaDao.create(area);

            Pregunta p = new Pregunta();
            p.setId(UUID.randomUUID());
            p.setContenidoPregunta("Pregunta Create");
            p.setFechaCreacion(OffsetDateTime.now());
            p.setActivo(true);
            preguntaDao.create(p);
            em.getTransaction().commit();

            PreguntaAreaConocimiento entity = new PreguntaAreaConocimiento();
            Pregunta idPreg = new Pregunta();
            idPreg.setId(p.getId());
            entity.setIdPregunta(idPreg);

            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(UriBuilder.fromUri("http://localhost/resources/v1/areaConocimiento/"));

            em.getTransaction().begin();
            Response response = cut.create(area.getId(), entity, uriInfo);
            em.getTransaction().commit();

            assertEquals(201, response.getStatus());
            assertNotNull(response.getEntity());
            assertInstanceOf(PreguntaAreaConocimiento.class, response.getEntity());
            PreguntaAreaConocimiento created = (PreguntaAreaConocimiento) response.getEntity();
            assertNotNull(created.getId());
        }

        @Order(10)
        @Test
        public void create_nullArea_returns422() {
            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            Response response = cut.create(null, new PreguntaAreaConocimiento(), uriInfo);
            assertEquals(422, response.getStatus());
            assertEquals("idAreaConocimiento", response.getHeaderString("Missing-parameter"));
        }

        @Order(11)
        @Test
        public void create_nullEntity_returns422() {
            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            Response response = cut.create(UUID.randomUUID(), null, uriInfo);
            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
        }

        @Order(12)
        @Test
        public void create_entityWithId_returns422() {
            PreguntaAreaConocimiento a = new PreguntaAreaConocimiento();
            a.setId(1);
            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            Response response = cut.create(UUID.randomUUID(), a, uriInfo);
            assertEquals(422, response.getStatus());
            assertEquals("entity.id must be null", response.getHeaderString("Missing-parameter"));
        }

        @Order(13)
        @Test
        public void create_missingIdPregunta_returns422() {
            PreguntaAreaConocimiento a = new PreguntaAreaConocimiento();
            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            Response response = cut.create(UUID.randomUUID(), a, uriInfo);
            assertEquals(422, response.getStatus());
            assertEquals("idPregunta must be provided in body", response.getHeaderString("Missing-parameter"));
        }

        @Order(14)
        @Test
        public void create_areaNotFound_returns404() {
            // preparar pregunta existente
            em.getTransaction().begin();
            Pregunta p = new Pregunta();
            p.setId(UUID.randomUUID());
            p.setContenidoPregunta("Pregunta Create2");
            p.setFechaCreacion(OffsetDateTime.now());
            p.setActivo(true);
            preguntaDao.create(p);
            em.getTransaction().commit();

            PreguntaAreaConocimiento entity = new PreguntaAreaConocimiento();
            Pregunta idPreg = new Pregunta();
            idPreg.setId(p.getId());
            entity.setIdPregunta(idPreg);

            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            Response response = cut.create(UUID.randomUUID(), entity, uriInfo);
            assertEquals(404, response.getStatus());
            assertTrue(response.getHeaderString("Not-found").contains("AreaConocimiento with id"));
        }

        @Order(15)
        @Test
        public void create_preguntaNotFound_returns404() {
            em.getTransaction().begin();
            AreaConocimiento area = new AreaConocimiento();
            area.setId(UUID.randomUUID());
            area.setNombre("Area Create3");
            area.setActivo(true);
            area.setFechaCreacion(OffsetDateTime.now());
            areaDao.create(area);
            em.getTransaction().commit();

            PreguntaAreaConocimiento entity = new PreguntaAreaConocimiento();
            Pregunta idPreg = new Pregunta();
            idPreg.setId(UUID.randomUUID());
            entity.setIdPregunta(idPreg);

            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            Response response = cut.create(area.getId(), entity, uriInfo);
            assertEquals(404, response.getStatus());
            assertTrue(response.getHeaderString("Not-found").contains("Pregunta with id"));
        }

        @Order(16)
        @Test
        public void create_daoThrows_returns500() {
            // preparar area y pregunta válidos para que la validación previa pase
            em.getTransaction().begin();
            AreaConocimiento area = new AreaConocimiento();
            area.setId(UUID.randomUUID());
            area.setNombre("Area DAO FAIL");
            area.setActivo(true);
            area.setFechaCreacion(OffsetDateTime.now());
            areaDao.create(area);

            Pregunta p = new Pregunta();
            p.setId(UUID.randomUUID());
            p.setContenidoPregunta("Pregunta DAO FAIL");
            p.setFechaCreacion(OffsetDateTime.now());
            p.setActivo(true);
            preguntaDao.create(p);
            em.getTransaction().commit();

            // ahora forzar que el DAO lance (dao sin em)
            cut.preguntaAreaConocimientoDAO = new PreguntaAreaConocimientoDAO() {
                @Override
                public void create(PreguntaAreaConocimiento obj) {
                    throw new RuntimeException("forced failure");
                }
            };
            PreguntaAreaConocimiento entity = new PreguntaAreaConocimiento();
            Pregunta idPreg = new Pregunta();
            idPreg.setId(p.getId());
            entity.setIdPregunta(idPreg);
            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            Response response = cut.create(area.getId(), entity, uriInfo);
            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
        }
    }

    @Nested
    class Delete {

        @Order(17)
        @Test
        public void delete_happy() {
            em.getTransaction().begin();
            AreaConocimiento area = new AreaConocimiento();
            UUID areaId = UUID.randomUUID();
            area.setId(areaId);
            area.setNombre("Area Delete");
            area.setActivo(true);
            area.setFechaCreacion(OffsetDateTime.now());
            areaDao.create(area);

            Pregunta p = new Pregunta();
            UUID pid = UUID.randomUUID();
            p.setId(pid);
            p.setContenidoPregunta("Pregunta Delete");
            p.setFechaCreacion(OffsetDateTime.now());
            p.setActivo(true);
            preguntaDao.create(p);

            PreguntaAreaConocimiento pac = new PreguntaAreaConocimiento();
            pac.setIdPregunta(p);
            pac.setIdAreaConocimiento(area);
            pac.setFechaAsignacion(OffsetDateTime.now());
            dao.create(pac);
            em.getTransaction().commit();

            // eliminar dentro de transaccion
            em.getTransaction().begin();
            Response response = cut.delete(areaId, pid);
            em.getTransaction().commit();
            assertEquals(204, response.getStatus());

            // verificar que ya no existe
            Response response2 = cut.findOne(areaId, pid);
            assertEquals(404, response2.getStatus());
        }

        @Order(18)
        @Test
        public void delete_nullIds_returns422() {
            Response response = cut.delete(null, null);
            assertEquals(422, response.getStatus());
            assertEquals("idAreaConocimiento,idPregunta", response.getHeaderString("Missing-parameter"));
        }

        @Order(19)
        @Test
        public void delete_notFound_returns404() {
            UUID a = UUID.randomUUID();
            UUID p = UUID.randomUUID();
            Response response = cut.delete(a, p);
            assertEquals(404, response.getStatus());
            assertEquals("Record linking area " + a + " and pregunta " + p + " not found", response.getHeaderString("Not-found-id"));
        }

        @Order(20)
        @Test
        public void delete_daoThrows_returns500() {
            cut.preguntaAreaConocimientoDAO = daoNoEm;
            Response response = cut.delete(UUID.randomUUID(), UUID.randomUUID());
            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
        }
    }

}