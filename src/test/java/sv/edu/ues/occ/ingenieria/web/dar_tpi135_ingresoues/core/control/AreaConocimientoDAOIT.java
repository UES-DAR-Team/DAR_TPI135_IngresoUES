package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class AreaConocimientoDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private AreaConocimientoDAO cut;
    private AreaConocimientoDAO daoNoEm;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new AreaConocimientoDAO();
        cut.em = em;
        daoNoEm = new AreaConocimientoDAO();
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    @Nested
    class Count {
        @Order(1)
        @Test
        public void count_happy() {
            int total = cut.count();
            assertTrue(total > 0);
        }

        @Order(2)
        @Test
        public void count_emNullDevuelveIllegalStateException() {
            assertThrows(IllegalStateException.class, () -> daoNoEm.count());
        }

        @Order(3)
        @Test
        public void count_falloDBDevuelveIllegalSatateException() {
            em.close();
            assertThrows(IllegalStateException.class, () -> cut.count());
        }
    }

    @Nested
    class Create {
        @Order(4)
        @Test
        public void create_happy() {
            int regBeforCreate = cut.count();
            AreaConocimiento nuevo = new AreaConocimiento();
            cut.em.getTransaction().begin();
            nuevo.setId(UUID.randomUUID());
            nuevo.setNombre("Prueba 1");
            nuevo.setActivo(true);
            nuevo.setFechaCreacion(OffsetDateTime.now());
            nuevo.setIdAutoReferenciaArea(cut.findRange(0, 1).getFirst());
            cut.create(nuevo);
            cut.em.getTransaction().commit();

            assertNotNull(nuevo.getId());
            int resultado = cut.count();
            assertEquals((regBeforCreate + 1), resultado);
        }

        @Order(5)
        @Test
        public void create_emNullDevuelveIllegalStateException() {
            AreaConocimiento aCNull = new AreaConocimiento();
            aCNull.setId(UUID.randomUUID());
            aCNull.setNombre("Prueba create em null");
            aCNull.setActivo(true);
            aCNull.setFechaCreacion(OffsetDateTime.now());
            aCNull.setIdAutoReferenciaArea(cut.findRange(0, 1).getFirst());

            assertThrows(IllegalStateException.class, () -> daoNoEm.create(aCNull));
        }

        @Order(6)
        @Test
        public void create_objNullDevuelveIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> cut.create(null));
        }

        @Order(7)
        @Test
        public void create_falloDBDevuelveIllegalStateException() {
            AreaConocimiento aCNull = new AreaConocimiento();
            aCNull.setId(UUID.randomUUID());
            aCNull.setNombre("Prueba create db fallo");
            aCNull.setActivo(true);
            aCNull.setFechaCreacion(OffsetDateTime.now());

            em.close();
            assertThrows(IllegalStateException.class, () -> cut.create(aCNull));
        }
    }

    @Nested
    class Update {

        @Order(8)
        @Test
        public void updateHappy() {
            cut.em.getTransaction().begin();
            AreaConocimiento nuevoUD = new AreaConocimiento();
            nuevoUD.setId(UUID.randomUUID());
            nuevoUD.setNombre("Prueba Update");
            nuevoUD.setActivo(true);
            nuevoUD.setFechaCreacion(OffsetDateTime.now());
            nuevoUD.setIdAutoReferenciaArea(cut.findRange(0, 1).getFirst());
            cut.create(nuevoUD);
            cut.em.getTransaction().commit();
            AreaConocimiento actualizacion = cut.findById(nuevoUD.getId());
            actualizacion.setNombre("Prueba Actualizada");

            cut.em.getTransaction().begin();
            cut.update(actualizacion);
            cut.em.getTransaction().commit();

            assertEquals("Prueba Actualizada", actualizacion.getNombre());
        }

        @Order(10)
        @Test
        public void update_regNullDevuelveIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> cut.update(null));
        }

        @Order(10)
        @Test
        public void update_emNullDevuelveIllegaStateException() {
            AreaConocimiento aUNull = new AreaConocimiento();
            aUNull.setId(UUID.randomUUID());
            aUNull.setNombre("Prueba update em null");
            aUNull.setActivo(true);
            aUNull.setFechaCreacion(OffsetDateTime.now());
            aUNull.setIdAutoReferenciaArea(cut.findRange(0, 1).getFirst());

            assertThrows(IllegalStateException.class, () -> daoNoEm.update(aUNull));
        }

        @Order(11)
        @Test
        public void update_falloDBDevuelveIllegalStateException() {

            AreaConocimiento aUDB = new AreaConocimiento();
            aUDB.setId(UUID.randomUUID());
            aUDB.setNombre("Prueba update db fallo");
            aUDB.setActivo(true);
            aUDB.setFechaCreacion(OffsetDateTime.now());
            aUDB.setIdAutoReferenciaArea(cut.findRange(0, 1).getFirst());

            em.close();
            assertThrows(IllegalStateException.class, () -> cut.update(aUDB));
        }
    }

    @Nested
    class Delete {
        @Order(12)
        @Test
        public void testDelete() {
            cut.em.getTransaction().begin();
            int regBeforeCreateToDelete = cut.count();

            AreaConocimiento nuevo = new AreaConocimiento();
            nuevo.setId(UUID.randomUUID());
            nuevo.setNombre("Eliminar esta prueba");
            nuevo.setActivo(true);
            nuevo.setFechaCreacion(OffsetDateTime.now());
            nuevo.setIdAutoReferenciaArea(cut.findRange(0, 1).getFirst());
            cut.create(nuevo);
            cut.em.getTransaction().commit();

            int registros = cut.count();

            AreaConocimiento eliminacion = cut.findById(nuevo.getId());
            Assertions.assertNotNull(eliminacion);

            cut.em.getTransaction().begin();
            cut.delete(eliminacion);
            cut.em.getTransaction().commit();

            assertEquals((regBeforeCreateToDelete + 1), registros);
            int regAfterDelete = cut.count();
            assertEquals(regBeforeCreateToDelete, regAfterDelete);
        }

        @Order(13)
        @Test
        public void delete_objNullDevuelveIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> cut.delete(null));
        }

        @Order(14)
        @Test
        public void delete_emNullDevuelveIllegalStateException() {
            AreaConocimiento aDNull = new AreaConocimiento();
            aDNull.setId(UUID.randomUUID());
            aDNull.setNombre("Prueba delete em null");
            aDNull.setActivo(true);
            aDNull.setFechaCreacion(OffsetDateTime.now());
            aDNull.setIdAutoReferenciaArea(cut.findRange(0, 1).getFirst());

            assertThrows(IllegalStateException.class, () -> daoNoEm.delete(aDNull));
        }

        @Order(15)
        @Test
        public void delete_falloDBDevuelveIllegalStateException() {
            AreaConocimiento aDDB = new AreaConocimiento();
            aDDB.setId(UUID.randomUUID());
            aDDB.setNombre("Prueba delete db fallo");
            aDDB.setActivo(true);
            aDDB.setFechaCreacion(OffsetDateTime.now());

            em.close();
            assertThrows(IllegalStateException.class, () -> cut.delete(aDDB));

        }
    }

    @Nested
    class FindRange {

        //el happy path ya se probo implicitamente en otros metodos, aqui se prueban los casos de error
        @Order(16)
        @Test
        public void findRange_FirstAndMaxInvalidDevuelveIllegalArgumentExcepcion() {
            assertThrows(IllegalArgumentException.class, () -> cut.findRange(-1, 10));
        }

        @Order(17)
        @Test
        public void findRange_emNullDevuelveIllegalStateException() {
            assertThrows(IllegalStateException.class, () -> daoNoEm.findRange(0, 10));
        }

        @Order(18)
        @Test
        public void findRange_falloDBDevuelveIllegalStateException() {
            em.close();
            assertThrows(IllegalStateException.class, () -> cut.findRange(0, 10));
        }
    }

    @Nested
    class FindById {

        @Order(19)
        @Test
        public void findById_IdNullDevuelveIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> cut.findById(null));
        }

        @Order(20)
        @Test
        public void findById_emNullDevuelveIllegalStateException() {
            UUID id = UUID.randomUUID();
            assertThrows(IllegalStateException.class, () -> daoNoEm.findById(id));
        }

        @Order(21)
        @Test
        public void findById_falloDBDevuelveIllegalStateException() {
            UUID id = UUID.randomUUID();
            em.close();
            assertThrows(IllegalStateException.class, () -> cut.findById(id));
        }
    }

    //prueba para getEntityClass para llegar al 100% de cobertura
    @Order(22)
    @Test
    public void testGetEntityClass() {
        //logica para probar getEntityClass
        //ya intente por varios test probarlo pero no lo logre cubrir
        //luego verificaremos con que otra logica podemos cubrirlo
        Class<?> cls = cut.getEntityClass();
        assertNotNull(cls);
        assertEquals(AreaConocimiento.class, cls);
    }

    @Nested
    class AreaConocimientoDAOFindByNameLike {

        @Order(23)
        @Test
        public void findByNameLike_NameNullDevuelveIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> cut.findByNameLike(null, 0, 10));
        }

        @Order(24)
        @Test
        public void findByNameLike_NameBlankDevuelveIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> cut.findByNameLike("   ", 0, 10));
        }

        @Order(25)
        @Test
        public void findByNameLike_FirstAndMaxInvalidDevuelveIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> cut.findByNameLike("Nombre", -1, 10));
            assertThrows(IllegalArgumentException.class, () -> cut.findByNameLike("Nombre", 0, 0));
        }

        @Order(26)
        @Test
        public void findByNameLike_emNullDevuelveIllegalStateException() {
            assertThrows(IllegalStateException.class, () -> daoNoEm.findByNameLike("Nombre", 0, 10));
        }

        @Order(27)
        @Test
        public void findByNameLike_falloDBDevuelveIllegalStateException() {
            em.close();
            assertThrows(IllegalStateException.class, () -> cut.findByNameLike("Nombre", 0, 10));
        }

        @Order(28)
        @Test
        public void findByNameLikeHappy() {
            List<AreaConocimiento> registro = cut.findByNameLike("Matemáticas", 0, 10);

            assertNotNull(registro);
            assertFalse(registro.isEmpty());
        }
    }


    @Nested
    class FindByAreaPadre {

        @Order(29)
        @Test
        public void findByAreaPadreHappy(){
            List<AreaConocimiento> registro = cut.findByAreaPadre();

            assertNotNull(registro);
            assertFalse(registro.isEmpty());
        }

        @Order(30)
        @Test
        public void findByAreaPadre_emNullDevuelveIllegalStateException() {
            assertThrows(IllegalStateException.class, () -> daoNoEm.findByAreaPadre());
        }


    }

    @Nested
    class FindHijosByPadre {
        @Order(31)
        @Test
        public void findHijosByPadreHappy() {
            UUID id = UUID.fromString("a1000000-0000-0000-0000-000000000003");
            List<AreaConocimiento> registro = cut.findHijosByPadre(id);

            assertNotNull(registro);
            assertFalse(registro.isEmpty());
        }

        @Order(32)
        @Test
        public void findHijosByPadre_emNullDevuelveIllegalStateException() {
            UUID id = UUID.fromString("a1000000-0000-0000-0000-000000000003");
            assertThrows(IllegalStateException.class, () -> daoNoEm.findHijosByPadre(id));
        }

        @Order(33)
        @Test
        public void findHijosByPadre_IdNullDevuelveIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> cut.findHijosByPadre(null));
        }
    }
}