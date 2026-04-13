package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;
import testing.NeedsLiberty;

import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
@NeedsLiberty
public class AreaConocimientoResourceIT extends BaseIntegrationAbstract {

    private AreaConocimientoResource cut;
    private AreaConocimientoDAO dao;
    private AreaConocimientoDAO daoNoEm;
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        // usar un DAO de prueba que asigna id y fechaCreacion si faltan para evitar violaciones de constraints
        dao = new AreaConocimientoDAO(){
            @Override
            public void create(AreaConocimiento obj) {
                if(obj.getId() == null){
                    obj.setId(UUID.randomUUID());
                }
                if(obj.getFechaCreacion() == null){
                    obj.setFechaCreacion(OffsetDateTime.now());
                }
                super.create(obj);
            }
        };
        // `em` in AreaConocimientoDAO has package visibility; usar reflexión para inyectarlo
        try {
            // buscar el campo `em` en la clase base AreaConocimientoDAO (evita NoSuchField en subclases anónimas)
            Field emField = AreaConocimientoDAO.class.getDeclaredField("em");
            emField.setAccessible(true);
            emField.set(dao, em);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        daoNoEm = new AreaConocimientoDAO();
        cut = new AreaConocimientoResource();
        cut.areaConocimientoDAO = dao;
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
            // asegurar que hay al menos un registro
            em.getTransaction().begin();
            AreaConocimiento a = new AreaConocimiento();
            a.setId(UUID.randomUUID());
            a.setNombre("Area IT");
            a.setActivo(true);
            a.setFechaCreacion(OffsetDateTime.now());
            a.setIdAutoReferenciaArea(dao.findRange(0,1).getFirst());
            dao.create(a);
            em.getTransaction().commit();

            Response response = cut.findRange(0,10);
            assertEquals(200, response.getStatus());
            assertNotNull(response.getEntity());
            List<?> lista = (List<?>) response.getEntity();
            assertTrue(lista.size() >= 1);
            assertNotNull(response.getHeaderString("X-Total-Count"));
        }

        @Order(2)
        @Test
        public void findRange_invalidParams_returns422() {
            Response response = cut.findRange(-1, 0);
            assertEquals(422, response.getStatus());
            assertEquals("first,max", response.getHeaderString("Missing-parameter"));
        }

        @Order(3)
        @Test
        public void findRange_daoThrows_returns500() {
            cut.areaConocimientoDAO = daoNoEm; // dao sin em lanzará IllegalStateException
            Response response = cut.findRange(0,10);
            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
        }
    }

    @Nested
    class FindById {

        @Order(4)
        @Test
        public void findById_happy() {
            em.getTransaction().begin();
            AreaConocimiento a = new AreaConocimiento();
            UUID id = UUID.randomUUID();
            a.setId(id);
            a.setNombre("FindById IT");
            a.setActivo(true);
            a.setFechaCreacion(OffsetDateTime.now());
            a.setIdAutoReferenciaArea(dao.findRange(0,1).getFirst());
            dao.create(a);
            em.getTransaction().commit();

            Response response = cut.findById(id);
            assertEquals(200, response.getStatus());
            assertNotNull(response.getEntity());
            assertTrue(response.getEntity() instanceof AreaConocimiento);
            AreaConocimiento entidad = (AreaConocimiento) response.getEntity();
            assertEquals(id, entidad.getId());
        }

        @Order(5)
        @Test
        public void findById_nullId_returns422() {
            Response response = cut.findById(null);
            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
        }

        @Order(6)
        @Test
        public void findById_notFound_returns404() {
            UUID randomId = UUID.randomUUID();
            Response response = cut.findById(randomId);
            assertEquals(404, response.getStatus());
            assertEquals("Record with id " + randomId + " not found", response.getHeaderString("Not-found-id"));
        }

        @Order(7)
        @Test
        public void findById_daoThrows_returns500() {
            cut.areaConocimientoDAO = daoNoEm;
            Response response = cut.findById(UUID.randomUUID());
            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
        }
    }

    @Nested
    class DeleteById {

        @Order(8)
        @Test
        public void deleteById_happy() {
            em.getTransaction().begin();
            AreaConocimiento a = new AreaConocimiento();
            UUID id = UUID.randomUUID();
            a.setId(id);
            a.setNombre("Delete IT");
            a.setActivo(true);
            a.setFechaCreacion(OffsetDateTime.now());
            a.setIdAutoReferenciaArea(dao.findRange(0,1).getFirst());
            dao.create(a);
            em.getTransaction().commit();

            // borrar dentro de transaccion
            em.getTransaction().begin();
            Response response = cut.deleteById(id);
            em.getTransaction().commit();
            assertEquals(204, response.getStatus());

            // verificar que ya no existe
            Response response2 = cut.findById(id);
            assertEquals(404, response2.getStatus());
        }

        @Order(9)
        @Test
        public void deleteById_nullId_returns422() {
            Response response = cut.deleteById(null);
            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
        }

        @Order(10)
        @Test
        public void deleteById_notFound_returns404() {
            UUID randomId = UUID.randomUUID();
            Response response = cut.deleteById(randomId);
            assertEquals(404, response.getStatus());
            assertEquals("Record with id " + randomId + " not found", response.getHeaderString("Not-Found-id"));
        }

        @Order(11)
        @Test
        public void deleteById_daoThrows_returns500() {
            cut.areaConocimientoDAO = daoNoEm;
            Response response = cut.deleteById(UUID.randomUUID());
            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
        }
    }

    @Nested
    class Create {

        @Order(12)
        @Test
        public void create_happy() {
            AreaConocimiento a = new AreaConocimiento();
            a.setNombre("Create IT");
            a.setActivo(true);
            a.setFechaCreacion(OffsetDateTime.now());
            a.setIdAutoReferenciaArea(dao.findRange(0,1).getFirst());

            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(UriBuilder.fromUri("http://localhost/resources/v1/areaConocimiento/"));

            // crear dentro de una transaccion (simular EJB container-managed tx)
            em.getTransaction().begin();
            Response response = cut.create(a, uriInfo);
            em.getTransaction().commit();
            assertEquals(201, response.getStatus());
            assertNotNull(response.getEntity());
            assertTrue(response.getEntity() instanceof AreaConocimiento);
            AreaConocimiento created = (AreaConocimiento) response.getEntity();
            assertNotNull(created.getId());
        }

        @Order(13)
        @Test
        public void create_nullEntity_returns422() {
            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            Response response = cut.create(null, uriInfo);
            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
        }

        @Order(14)
        @Test
        public void create_entityWithId_returns422() {
            AreaConocimiento a = new AreaConocimiento();
            a.setId(UUID.randomUUID());
            a.setNombre("HasId");
            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            Response response = cut.create(a, uriInfo);
            assertEquals(422, response.getStatus());
            assertEquals("entity.id must be null", response.getHeaderString("Missing-parameter"));
        }

        @Order(15)
        @Test
        public void create_daoThrows_returns500() {
            cut.areaConocimientoDAO = daoNoEm;
            AreaConocimiento a = new AreaConocimiento();
            a.setNombre("CreateFail");
            UriInfo uriInfo = Mockito.mock(UriInfo.class);
            Response response = cut.create(a, uriInfo);
            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
        }
    }

    @Nested
    class Update {

        @Order(16)
        @Test
        public void update_happy() {
            em.getTransaction().begin();
            AreaConocimiento a = new AreaConocimiento();
            UUID id = UUID.randomUUID();
            a.setId(id);
            a.setNombre("Update IT");
            a.setActivo(true);
            a.setFechaCreacion(OffsetDateTime.now());
            a.setIdAutoReferenciaArea(dao.findRange(0,1).getFirst());
            dao.create(a);
            em.getTransaction().commit();

            AreaConocimiento update = new AreaConocimiento();
            update.setNombre("Updated Name");
            // preservar campos obligatorios para que la actualización no deje valores nulos
            update.setFechaCreacion(a.getFechaCreacion());
            update.setActivo(a.getActivo());
            update.setIdAutoReferenciaArea(a.getIdAutoReferenciaArea());

            // update debe ejecutarse dentro de una transaccion
            em.getTransaction().begin();
            Response response = cut.update(id, update);
            em.getTransaction().commit();
            assertEquals(200, response.getStatus());
            assertNotNull(response.getEntity());
            AreaConocimiento updated = (AreaConocimiento) response.getEntity();
            assertEquals(id, updated.getId());
            assertEquals("Updated Name", updated.getNombre());
        }

        @Order(17)
        @Test
        public void update_nullId_returns422() {
            Response response = cut.update(null, new AreaConocimiento());
            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
        }

        @Order(18)
        @Test
        public void update_nullEntity_returns422() {
            Response response = cut.update(UUID.randomUUID(), null);
            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
        }

        @Order(19)
        @Test
        public void update_notFound_returns404() {
            UUID randomId = UUID.randomUUID();
            AreaConocimiento upd = new AreaConocimiento();
            Response response = cut.update(randomId, upd);
            assertEquals(404, response.getStatus());
            assertEquals("Record with id " + randomId + " not found", response.getHeaderString("Not-found-id"));
        }

        @Order(20)
        @Test
        public void update_daoThrows_returns500() {
            cut.areaConocimientoDAO = daoNoEm;
            Response response = cut.update(UUID.randomUUID(), new AreaConocimiento());
            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
        }
    }

}