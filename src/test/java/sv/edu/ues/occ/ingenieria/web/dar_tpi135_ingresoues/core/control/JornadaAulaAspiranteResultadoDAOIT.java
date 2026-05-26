package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspirante;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspiranteResultado;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class JornadaAulaAspiranteResultadoDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private JornadaAulaAspiranteResultadoDAO cut;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new JornadaAulaAspiranteResultadoDAO();
        cut.em = em;
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    /**
     * JornadaAulaAspiranteResultado usa GenerationType.IDENTITY.
     * Sin flush(), EclipseLink difiere el INSERT — rollback no llega a la BD.
     */
    @Test
    @Order(1)
    public void testCreate() {
        // Obtenemos un JornadaAulaAspirante existente para la FK
        JornadaAulaAspiranteDAO jaaDAO = new JornadaAulaAspiranteDAO();
        jaaDAO.em = em;
        List<JornadaAulaAspirante> jaaData = jaaDAO.findRange(0, 1);
        assertFalse(jaaData.isEmpty(), "Debe existir data en la BD");

        JornadaAulaAspiranteResultado nuevo = new JornadaAulaAspiranteResultado();
        nuevo.setIdJornadaAulaAspirante(jaaData.get(0));
        nuevo.setPuntajeObtenido(new BigDecimal("75.50"));
        nuevo.setAprobado(true);

        em.getTransaction().begin();
        assertDoesNotThrow(() -> cut.create(nuevo));
        em.getTransaction().rollback();
    }

    @Test
    @Order(2)
    public void testFinds_OK() {
        assertDoesNotThrow(() -> {
            List<JornadaAulaAspiranteResultado> r1 =
                    cut.findByJornadaAulaAspirante(1, 0, 10);

            List<JornadaAulaAspiranteResultado> r2 =
                    cut.findByAprobado(true, 0, 10);

            List<JornadaAulaAspiranteResultado> r3 =
                    cut.findByRangoPuntaje(
                            BigDecimal.ZERO,
                            new BigDecimal("100"),
                            0, 10
                    );

            assertNotNull(r1);
            assertNotNull(r2);
            assertNotNull(r3);
        });
    }

    @Test
    @Order(3)
    public void testCount_OK() {
        Long total = cut.countByAprobado(true);
        assertNotNull(total);
        assertTrue(total >= 0);
    }

    @Test
    @Order(4)
    public void testGetters() {
        assertEquals(JornadaAulaAspiranteResultado.class, cut.getEntityClass());
        assertNotNull(cut.getEntityManager());
    }

    @Test
    @Order(5)
    public void testNullValidations() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> cut.findByJornadaAulaAspirante(null, 0, 10)),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> cut.findByAprobado(null, 0, 10)),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> cut.countByAprobado(null)),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> cut.findByRangoPuntaje(null, null, 0, 10)),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> cut.findByRangoPuntaje(null, new BigDecimal("10"), 0, 10)),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> cut.findByRangoPuntaje(new BigDecimal("10"), null, 0, 10))
        );
    }

    @Test
    @Order(6)
    public void testInvalidRanges() {
        assertThrows(IllegalArgumentException.class, () ->
                cut.findByRangoPuntaje(
                        new BigDecimal("10"),
                        new BigDecimal("5"),
                        0, 10
                )
        );
    }

    @Test
    @Order(7)
    public void testInvalidPagination() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> cut.findByJornadaAulaAspirante(1, -1, 10)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> cut.findByJornadaAulaAspirante(1, 0, 0)),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> cut.findByAprobado(true, -1, 10)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> cut.findByAprobado(true, 0, 0)),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> cut.findByRangoPuntaje(BigDecimal.ZERO, new BigDecimal("10"), -1, 10)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> cut.findByRangoPuntaje(BigDecimal.ZERO, new BigDecimal("10"), 0, 0))
        );
    }

    @Test
    @Order(8)
    public void testExceptions_entityManagerClosed() {
        em.close();

        assertAll(
                () -> assertThrows(IllegalStateException.class,
                        () -> cut.findByJornadaAulaAspirante(1, 0, 10)),
                () -> assertThrows(IllegalStateException.class,
                        () -> cut.findByAprobado(true, 0, 10)),
                () -> assertThrows(IllegalStateException.class,
                        () -> cut.findByRangoPuntaje(BigDecimal.ZERO, new BigDecimal("10"), 0, 10)),
                () -> assertThrows(IllegalStateException.class,
                        () -> cut.countByAprobado(true))
        );
    }

    @Test
    @Order(9)
    public void testExceptions_entityManagerNull() {
        cut.em = null;

        assertAll(
                () -> assertThrows(IllegalStateException.class,
                        () -> cut.findByJornadaAulaAspirante(1, 0, 10)),

                // findByAprobado también debe cubrirse con em null
                () -> assertThrows(IllegalStateException.class,
                        () -> cut.findByAprobado(true, 0, 10)),

                () -> assertThrows(IllegalStateException.class,
                        () -> cut.findByRangoPuntaje(BigDecimal.ZERO, new BigDecimal("10"), 0, 10)),

                () -> assertThrows(IllegalStateException.class,
                        () -> cut.countByAprobado(true))
        );
    }
}