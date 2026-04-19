package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspirante;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class JornadaAulaAspiranteDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private JornadaAulaAspiranteDAO cut;
    private JornadaAulaAspiranteDAO daoNoEm;

    private Integer idJornadaAula;
    private Integer idAspirantePrueba;
    private Boolean asistencia;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();

        cut = new JornadaAulaAspiranteDAO();
        cut.em = em;

        daoNoEm = new JornadaAulaAspiranteDAO();

        List<JornadaAulaAspirante> data = cut.findRange(0, 1);
        assertFalse(data.isEmpty(), "Debe existir data en la BD");

        JornadaAulaAspirante sample = data.get(0);

        idJornadaAula = sample.getIdJornadaAula().getId();
        idAspirantePrueba = sample.getIdAspirantePrueba().getId();
        asistencia = sample.getAsistio();
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }


    @Test
    @Order(1)
    public void findByJornadaAula_ok() {
        List<JornadaAulaAspirante> result =
                cut.findByJornadaAula(idJornadaAula, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @Order(2)
    public void findByJornadaAula_paramInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByJornadaAula(null, 0, 10));

        assertThrows(IllegalArgumentException.class,
                () -> cut.findByJornadaAula(idJornadaAula, -1, 10));

        assertThrows(IllegalArgumentException.class,
                () -> cut.findByJornadaAula(idJornadaAula, 0, 0));
    }

    @Test
    @Order(3)
    public void findByJornadaAula_dbError() {
        em.close();

        List<JornadaAulaAspirante> result =
                cut.findByJornadaAula(idJornadaAula, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    @Order(4)
    public void findByAspirantePrueba_ok() {
        List<JornadaAulaAspirante> result =
                cut.findByAspirantePrueba(idAspirantePrueba, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @Order(5)
    public void findByAspirantePrueba_paramInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByAspirantePrueba(null, 0, 10));
    }

    @Test
    @Order(6)
    public void findByAspirantePrueba_empty() {
        List<JornadaAulaAspirante> result =
                cut.findByAspirantePrueba(-999, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    @Order(7)
    public void findByAsistencia_ok() {
        List<JornadaAulaAspirante> result =
                cut.findByAsistencia(asistencia, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @Order(8)
    public void findByAsistencia_paramInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByAsistencia(null, 0, 10));
    }


    @Test
    @Order(9)
    public void countByJornadaAula_ok() {
        Long count = cut.countByJornadaAula(idJornadaAula);

        assertNotNull(count);
        assertTrue(count > 0);
    }

    @Test
    @Order(10)
    public void countByJornadaAula_paramInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.countByJornadaAula(null));
    }

    @Test
    @Order(11)
    public void countByJornadaAula_dbError() {
        em.close();

        Long count = cut.countByJornadaAula(idJornadaAula);

        assertEquals(0L, count);
    }

    @Test
    @Order(12)
    public void countByAsistencia_ok() {
        Long count = cut.countByAsistencia(asistencia);

        assertNotNull(count);
        assertTrue(count > 0);
    }

    @Test
    @Order(13)
    public void countByAsistencia_paramInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.countByAsistencia(null));
    }

    @Test
    @Order(14)
    public void countByAsistencia_dbError() {
        em.close();

        Long count = cut.countByAsistencia(asistencia);

        assertEquals(0L, count);
    }

    @Test
    @Order(15)
    public void findByAspirantePrueba_dbError_noEm() {

        List<JornadaAulaAspirante> result =
                daoNoEm.findByAspirantePrueba(idAspirantePrueba, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @Order(16)
    public void findByAsistencia_dbError_noEm() {

        List<JornadaAulaAspirante> result =
                daoNoEm.findByAsistencia(asistencia, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findByAspirantePrueba_invalidPagination() {

        assertThrows(IllegalArgumentException.class,
                () -> cut.findByAspirantePrueba(idAspirantePrueba, -1, 10));

        assertThrows(IllegalArgumentException.class,
                () -> cut.findByAspirantePrueba(idAspirantePrueba, 0, 0));
    }

    @Test
    public void findByAsistencia_invalidPagination() {

        assertThrows(IllegalArgumentException.class,
                () -> cut.findByAsistencia(asistencia, -1, 10));

        assertThrows(IllegalArgumentException.class,
                () -> cut.findByAsistencia(asistencia, 0, 0));
    }

    @Test
    public void findByAspirantePrueba_dbError_real() {

        cut.em = null;

        List<JornadaAulaAspirante> result =
                cut.findByAspirantePrueba(idAspirantePrueba, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findByAsistencia_dbError_real() {

        cut.em = null;

        List<JornadaAulaAspirante> result =
                cut.findByAsistencia(asistencia, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}