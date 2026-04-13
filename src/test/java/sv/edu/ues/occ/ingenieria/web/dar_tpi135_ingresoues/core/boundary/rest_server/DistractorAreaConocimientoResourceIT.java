package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.DistractorAreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.DistractorDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.DistractorAreaConocimiento;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;
import testing.NeedsLiberty;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
@NeedsLiberty
public class DistractorAreaConocimientoResourceIT extends BaseIntegrationAbstract {

    private DistractorAreaConocimientoResource cut;
    private DistractorAreaConocimientoDAO dao;
    private DistractorAreaConocimientoDAO daoNoEm;
    private DistractorDAO distractorDAO;
    private AreaConocimientoDAO areaConocimientoDAO;
    private EntityManager em;

    // contador para asignar ids enteros en las entidades DistractorAreaConocimiento de prueba
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(1);

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();

        // DAO de prueba que asigna id entero y fechaAsignacion si faltan
        dao = new DistractorAreaConocimientoDAO() {
            @Override
            public void create(DistractorAreaConocimiento obj) {
                if (obj.getId() == null) {
                    obj.setId(ID_COUNTER.getAndIncrement());
                }
                if (obj.getFechaAsignacion() == null) {
                    obj.setFechaAsignacion(OffsetDateTime.now());
                }
                super.create(obj);
            }
        };

        distractorDAO = new DistractorDAO() {
            @Override
            public void create(Distractor obj) {
                if (obj.getId() == null) {
                    obj.setId(UUID.randomUUID());
                }
                if (obj.getFechaCreacion() == null) {
                    obj.setFechaCreacion(OffsetDateTime.now());
                }
                super.create(obj);
            }
        };

        areaConocimientoDAO = new AreaConocimientoDAO() {
            @Override
            public void create(AreaConocimiento obj) {
                if (obj.getId() == null) {
                    obj.setId(UUID.randomUUID());
                }
                if (obj.getFechaCreacion() == null) {
                    obj.setFechaCreacion(OffsetDateTime.now());
                }
                super.create(obj);
            }
        };

        // inyectar EntityManager por reflexion en los DAOs de prueba
        try {
            Field emFieldDac = DistractorAreaConocimientoDAO.class.getDeclaredField("em");
            emFieldDac.setAccessible(true);
            emFieldDac.set(dao, em);

            Field emFieldDistractor = DistractorDAO.class.getDeclaredField("em");
            emFieldDistractor.setAccessible(true);
            emFieldDistractor.set(distractorDAO, em);

            Field emFieldArea = AreaConocimientoDAO.class.getDeclaredField("em");
            emFieldArea.setAccessible(true);
            emFieldArea.set(areaConocimientoDAO, em);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        // DAOs sin em para forzar errores
        daoNoEm = new DistractorAreaConocimientoDAO();

        cut = new DistractorAreaConocimientoResource();
        cut.distractorAreaConocimientoDAO = dao;
        cut.distractorDAO = distractorDAO;
        cut.areaConocimientoDAO = areaConocimientoDAO;
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
            // crear area, distractor y link
            em.getTransaction().begin();
            AreaConocimiento area = new AreaConocimiento();
            UUID areaId = UUID.randomUUID();
            area.setId(areaId);
            area.setNombre("Area Test");
            area.setActivo(true);
            area.setFechaCreacion(OffsetDateTime.now());
            areaConocimientoDAO.create(area);

            Distractor d = new Distractor();
            UUID dId = UUID.randomUUID();
            d.setId(dId);
            d.setContenidoDistractor("Contenido");
            d.setEsCorrecto(false);
            d.setActivo(true);
            d.setFechaCreacion(OffsetDateTime.now());
            distractorDAO.create(d);

            DistractorAreaConocimiento dac = new DistractorAreaConocimiento();
            dac.setIdDistractor(d);
            dac.setIdAreaConocimiento(area);
            dac.setFechaAsignacion(OffsetDateTime.now());
            dao.create(dac);
            em.getTransaction().commit();

            Response response = cut.findRange(areaId, 0, 10);
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
            UUID randomArea = UUID.randomUUID();
            Response response = cut.findRange(randomArea, 0, 10);
            assertEquals(404, response.getStatus());
            assertEquals("AreaConocimiento with id " + randomArea + " not found", response.getHeaderString("Not-found"));
        }

        @Order(4)
        @Test
        public void findRange_daoThrows_returns500() {
            cut.distractorAreaConocimientoDAO = daoNoEm; // dao sin em lanzará IllegalStateException
            Response response = cut.findRange(UUID.randomUUID(), 0, 10);
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
            areaConocimientoDAO.create(area);

            Distractor d = new Distractor();
            UUID dId = UUID.randomUUID();
            d.setId(dId);
            d.setContenidoDistractor("Contenido FindOne");
            d.setEsCorrecto(false);
            d.setActivo(true);
            d.setFechaCreacion(OffsetDateTime.now());
            distractorDAO.create(d);

            DistractorAreaConocimiento dac = new DistractorAreaConocimiento();
            dac.setIdDistractor(d);
            dac.setIdAreaConocimiento(area);
            dac.setFechaAsignacion(OffsetDateTime.now());
            dao.create(dac);
            em.getTransaction().commit();

            Response response = cut.findOne(areaId, dId);
            assertEquals(200, response.getStatus());
            assertNotNull(response.getEntity());
            assertInstanceOf(DistractorAreaConocimiento.class, response.getEntity());
            DistractorAreaConocimiento entidad = (DistractorAreaConocimiento) response.getEntity();
            assertNotNull(entidad.getIdDistractor());
            assertEquals(dId, entidad.getIdDistractor().getId());
        }

        @Order(6)
        @Test
        public void findOne_nullParams_returns422() {
            Response response = cut.findOne(null, null);
            assertEquals(422, response.getStatus());
            assertEquals("idAreaConocimiento,idDistractor", response.getHeaderString("Missing-parameter"));
        }

        @Order(7)
        @Test
        public void findOne_notFound_returns404() {
            UUID areaId = UUID.randomUUID();
            UUID dId = UUID.randomUUID();
            Response response = cut.findOne(areaId, dId);
            assertEquals(404, response.getStatus());
            assertEquals("Record linking area " + areaId + " and distractor " + dId + "not found", response.getHeaderString("Not-found-id"));
        }

        @Order(8)
        @Test
        public void findOne_daoThrows_returns500() {
            cut.distractorAreaConocimientoDAO = daoNoEm;
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
            UUID areaId = UUID.randomUUID();
            area.setId(areaId);
            area.setNombre("Area Create");
            area.setActivo(true);
            area.setFechaCreacion(OffsetDateTime.now());
            areaConocimientoDAO.create(area);

            Distractor d = new Distractor();
            UUID dId = UUID.randomUUID();
            d.setId(dId);
            d.setContenidoDistractor("Contenido Create");
            d.setEsCorrecto(false);
            d.setActivo(true);
            d.setFechaCreacion(OffsetDateTime.now());
            distractorDAO.create(d);

            DistractorAreaConocimiento entity = new DistractorAreaConocimiento();
            Distractor ref = new Distractor();
            ref.setId(dId);
            entity.setIdDistractor(ref);
            entity.setFechaAsignacion(OffsetDateTime.now());

            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(UriBuilder.fromUri("http://localhost/resources/v1/areaConocimiento/"));

            Response response = cut.create(areaId, entity, uriInfo);
            assertEquals(201, response.getStatus());
            assertNotNull(response.getEntity());
            assertInstanceOf(DistractorAreaConocimiento.class, response.getEntity());
            DistractorAreaConocimiento created = (DistractorAreaConocimiento) response.getEntity();
            assertNotNull(created.getId());
            // verificar que quedó asociado al area
            assertNotNull(created.getIdAreaConocimiento());
            assertEquals(areaId, created.getIdAreaConocimiento().getId());
        }

        @Order(10)
        @Test
        public void create_nullAreaId_returns422() {
            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            Response response = cut.create(null, new DistractorAreaConocimiento(), uriInfo);
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
            DistractorAreaConocimiento e = new DistractorAreaConocimiento();
            e.setId(123);
            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            Response response = cut.create(UUID.randomUUID(), e, uriInfo);
            assertEquals(422, response.getStatus());
            assertEquals("entity.id must be null", response.getHeaderString("Missing-parameter"));
        }

        @Order(13)
        @Test
        public void create_entityMissingDistractor_returns422() {
            DistractorAreaConocimiento e = new DistractorAreaConocimiento();
            e.setFechaAsignacion(OffsetDateTime.now());
            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            Response response = cut.create(UUID.randomUUID(), e, uriInfo);
            assertEquals(422, response.getStatus());
            assertEquals("entity.id must be provider in body", response.getHeaderString("Missing-parameter"));
        }

        @Order(14)
        @Test
        public void create_areaNotFound_returns404() {
            em.getTransaction().begin();
            // crear solo distractor
            Distractor d = new Distractor();
            UUID dId = UUID.randomUUID();
            d.setId(dId);
            d.setContenidoDistractor("Contenido");
            d.setEsCorrecto(false);
            d.setActivo(true);
            d.setFechaCreacion(OffsetDateTime.now());
            distractorDAO.create(d);
            em.getTransaction().commit();

            DistractorAreaConocimiento e = new DistractorAreaConocimiento();
            Distractor ref = new Distractor();
            ref.setId(dId);
            e.setIdDistractor(ref);
            e.setFechaAsignacion(OffsetDateTime.now());

            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            Response response = cut.create(UUID.randomUUID(), e, uriInfo);
            assertEquals(404, response.getStatus());
            assertTrue(response.getHeaderString("Not-found").contains("AreaConocimiento with id"));
        }

        @Order(15)
        @Test
        public void create_distractorNotFound_returns404() {
            em.getTransaction().begin();
            AreaConocimiento area = new AreaConocimiento();
            UUID areaId = UUID.randomUUID();
            area.setId(areaId);
            area.setNombre("Area Create");
            area.setActivo(true);
            area.setFechaCreacion(OffsetDateTime.now());
            areaConocimientoDAO.create(area);
            em.getTransaction().commit();

            DistractorAreaConocimiento e = new DistractorAreaConocimiento();
            Distractor ref = new Distractor();
            ref.setId(UUID.randomUUID()); // no existe
            e.setIdDistractor(ref);
            e.setFechaAsignacion(OffsetDateTime.now());

            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            Response response = cut.create(areaId, e, uriInfo);
            assertEquals(404, response.getStatus());
            assertTrue(response.getHeaderString("Not-found").contains("Distractor with id"));
        }

        @Order(16)
        @Test
        public void create_daoThrows_returns500() {
            cut.distractorAreaConocimientoDAO = daoNoEm;
            DistractorAreaConocimiento e = new DistractorAreaConocimiento();
            e.setIdDistractor(new Distractor());
            e.getIdDistractor().setId(UUID.randomUUID());
            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            Response response = cut.create(UUID.randomUUID(), e, uriInfo);
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
            areaConocimientoDAO.create(area);

            Distractor d = new Distractor();
            UUID dId = UUID.randomUUID();
            d.setId(dId);
            d.setContenidoDistractor("Contenido Delete");
            d.setEsCorrecto(false);
            d.setActivo(true);
            d.setFechaCreacion(OffsetDateTime.now());
            distractorDAO.create(d);

            DistractorAreaConocimiento dac = new DistractorAreaConocimiento();
            dac.setIdDistractor(d);
            dac.setIdAreaConocimiento(area);
            dac.setFechaAsignacion(OffsetDateTime.now());
            dao.create(dac);
            em.getTransaction().commit();

            // borrar dentro de transaccion
            em.getTransaction().begin();
            Response response = cut.delete(areaId, dId);
            em.getTransaction().commit();
            assertEquals(204, response.getStatus());

            // verificar que ya no existe
            Response response2 = cut.findOne(areaId, dId);
            assertEquals(404, response2.getStatus());
        }

        @Order(18)
        @Test
        public void delete_nullParams_returns422() {
            Response response = cut.delete(null, null);
            assertEquals(422, response.getStatus());
            assertEquals("idAreaConocimiento,idDistractor", response.getHeaderString("Missing-parameter"));
        }

        @Order(19)
        @Test
        public void delete_notFound_returns404() {
            Response response = cut.delete(UUID.randomUUID(), UUID.randomUUID());
            assertEquals(404, response.getStatus());
            assertTrue(response.getHeaderString("Not-found-id").contains("Record linking area"));
        }

        @Order(20)
        @Test
        public void delete_daoThrows_returns500() {
            cut.distractorAreaConocimientoDAO = daoNoEm;
            Response response = cut.delete(UUID.randomUUID(), UUID.randomUUID());
            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
        }
    }

}