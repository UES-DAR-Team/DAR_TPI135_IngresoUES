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


    @Test
    @Order(1)
    public void testFindByJornada() {
        UUID id = UUID.randomUUID();

        List<JornadaAula> result = cut.findByJornada(id, 0, 10);

        assertNotNull(result);
    }

    @Test
    @Order(2)
    public void testFindByAula() {
        UUID id = UUID.randomUUID();

        List<JornadaAula> result = cut.findByAula(id, 0, 10);

        assertNotNull(result);
    }

    @Test
    @Order(3)
    public void testCountByJornada() {
        UUID id = UUID.randomUUID();

        Long result = cut.countByJornada(id);

        assertNotNull(result);
        assertTrue(result >= 0);
    }

    @Test
    @Order(4)
    public void testCountByAula() {
        UUID id = UUID.randomUUID();

        Long result = cut.countByAula(id);

        assertNotNull(result);
        assertTrue(result >= 0);
    }


    @Test
    @Order(5)
    public void testFindByJornadaNull() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByJornada(null, 0, 10));
    }

    @Test
    @Order(6)
    public void testFindByAulaNull() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByAula(null, 0, 10));
    }

    @Test
    @Order(7)
    public void testCountByJornadaNull() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.countByJornada(null));
    }

    @Test
    @Order(8)
    public void testCountByAulaNull() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.countByAula(null));
    }

    @Test
    @Order(9)
    public void testInvalidPaginationJornada() {
        UUID id = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> cut.findByJornada(id, -1, 10));

        assertThrows(IllegalArgumentException.class,
                () -> cut.findByJornada(id, 0, 0));
    }

    @Test
    @Order(10)
    public void testInvalidPaginationAula() {
        UUID id = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> cut.findByAula(id, -1, 10));

        assertThrows(IllegalArgumentException.class,
                () -> cut.findByAula(id, 0, 0));
    }


    @Test
    @Order(11)
    public void testGetEntityManagerOK() {
        assertNotNull(cut.getEntityManager());
    }

    @Test
    @Order(12)
    public void testGetEntityManagerNull() {
        cut.em = null;

        assertThrows(IllegalStateException.class,
                () -> cut.getEntityManager());
    }

    @Test
    @Order(13)
    public void testGetEntityClass() {
        assertEquals(JornadaAula.class, cut.getEntityClass());
    }
}