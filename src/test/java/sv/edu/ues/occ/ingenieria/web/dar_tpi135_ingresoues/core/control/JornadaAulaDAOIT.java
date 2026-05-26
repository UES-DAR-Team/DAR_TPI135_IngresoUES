package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAula;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class JornadaAulaDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private JornadaAulaDAO cut;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new JornadaAulaDAO();
        cut.em = em;
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    private JornadaAula getSample() {
        List<JornadaAula> data = cut.findRange(0, 1);
        assertFalse(data.isEmpty(), "Debe existir data en la BD");
        return data.get(0);
    }

    /**
     * JornadaAula usa GenerationType.IDENTITY.
     * Sin flush(), EclipseLink difiere el INSERT — con rollback()
     * nunca llega a la BD, evitando restricciones de clave.
     */
    @Test
    @Order(1)
    public void testCreate() {
        JornadaAula ref = getSample();

        JornadaAula nueva = new JornadaAula();
        nueva.setIdJornada(ref.getIdJornada());
        nueva.setIdAula(ref.getIdAula());

        em.getTransaction().begin();
        assertDoesNotThrow(() -> cut.create(nueva));
        em.getTransaction().rollback();
    }

    @Test
    @Order(2)
    public void testFindByJornada() {
        UUID id = UUID.randomUUID();
        List<JornadaAula> result = cut.findByJornada(id, 0, 10);
        assertNotNull(result);
    }

    @Test
    @Order(3)
    public void testFindByAula() {
        UUID id = UUID.randomUUID();
        List<JornadaAula> result = cut.findByAula(id, 0, 10);
        assertNotNull(result);
    }

    @Test
    @Order(4)
    public void testCountByJornada() {
        UUID id = UUID.randomUUID();
        Long result = cut.countByJornada(id);
        assertNotNull(result);
        assertTrue(result >= 0);
    }

    @Test
    @Order(5)
    public void testCountByAula() {
        UUID id = UUID.randomUUID();
        Long result = cut.countByAula(id);
        assertNotNull(result);
        assertTrue(result >= 0);
    }

    @Test
    @Order(6)
    public void testFindByJornadaNull() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByJornada(null, 0, 10));
    }

    @Test
    @Order(7)
    public void testFindByAulaNull() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByAula(null, 0, 10));
    }

    @Test
    @Order(8)
    public void testCountByJornadaNull() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.countByJornada(null));
    }

    @Test
    @Order(9)
    public void testCountByAulaNull() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.countByAula(null));
    }

    @Test
    @Order(10)
    public void testInvalidPaginationJornada() {
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByJornada(id, -1, 10));
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByJornada(id, 0, 0));
    }

    @Test
    @Order(11)
    public void testInvalidPaginationAula() {
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByAula(id, -1, 10));
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByAula(id, 0, 0));
    }


    @Test
    @Order(12)
    public void testGetEntityManagerOK() {
        assertNotNull(cut.getEntityManager());
    }

    @Test
    @Order(13)
    public void testGetEntityManagerNull() {
        cut.em = null;
        assertThrows(IllegalStateException.class,
                () -> cut.getEntityManager());
    }

    @Test
    @Order(14)
    public void testGetEntityClass() {
        assertEquals(JornadaAula.class, cut.getEntityClass());
    }

    @Test
    @Order(15)
    public void testFindByJornadaException() {
        em.close();
        assertThrows(IllegalStateException.class,
                () -> cut.findByJornada(UUID.randomUUID(), 0, 10));
    }

    @Test
    @Order(16)
    public void testFindByAulaException() {
        em.close();
        assertThrows(IllegalStateException.class,
                () -> cut.findByAula(UUID.randomUUID(), 0, 10));
    }

    @Test
    @Order(17)
    public void testCountByJornadaException() {
        em.close();
        assertThrows(IllegalStateException.class,
                () -> cut.countByJornada(UUID.randomUUID()));
    }

    @Test
    @Order(18)
    public void testCountByAulaException() {
        em.close();
        assertThrows(IllegalStateException.class,
                () -> cut.countByAula(UUID.randomUUID()));
    }
}