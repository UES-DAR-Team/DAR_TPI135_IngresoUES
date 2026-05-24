package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class JornadaDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private JornadaDAO cut;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new JornadaDAO();
        cut.em = em;
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    /**
     * Jornada usa GenerationType.UUID — el ID se asigna en memoria
     * antes del INSERT, así que el rollback no afecta la asignación del ID.
     */
    @Test
    @Order(1)
    public void testCreate() {
        Jornada nueva = new Jornada();
        nueva.setNombreJornada("JornadaIT");

        em.getTransaction().begin();
        cut.create(nueva);
        em.getTransaction().rollback();

        assertNotNull(nueva.getId(),
                "El UUID debe estar asignado en memoria tras persist");
    }

    @Test
    @Order(2)
    public void testCount() {
        int total = cut.count();
        assertTrue(total > 0);
    }

    @Test
    @Order(3)
    public void testFindByNombre() {
        Jornada ref = cut.findRange(0, 1).getFirst();

        List<Jornada> resultado = cut.findByNombre(
                ref.getNombreJornada(), 0, 10
        );

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    @Order(4)
    public void testFindByActivo() {
        Jornada ref = cut.findRange(0, 1).getFirst();

        List<Jornada> resultado = cut.findByActivo(
                ref.getActivo(), 0, 10
        );

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    @Order(5)
    public void testCountByNombre() {
        Jornada ref = cut.findRange(0, 1).getFirst();

        Long total = cut.countByNombre(ref.getNombreJornada());

        assertNotNull(total);
        assertTrue(total > 0);
    }

    @Test
    @Order(6)
    public void testCountByActivo() {
        Jornada ref = cut.findRange(0, 1).getFirst();

        Long total = cut.countByActivo(ref.getActivo());

        assertNotNull(total);
        assertTrue(total >= 0);
    }

    @Test
    @Order(7)
    public void testFindByNombreInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByNombre(null, 0, 10));
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByNombre(" ", 0, 10));
    }

    @Test
    @Order(8)
    public void testFindByActivoInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByActivo(null, 0, 10));
    }

    @Test
    @Order(9)
    public void testCountByNombreInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.countByNombre(null));
        assertThrows(IllegalArgumentException.class,
                () -> cut.countByNombre(" "));
    }

    @Test
    @Order(10)
    public void testCountByActivoInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.countByActivo(null));
    }

    @Test
    @Order(11)
    public void testInvalidPagination() {
        // findByNombre
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByNombre("TEST", -1, 10));
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByNombre("TEST", 0, 0));

        // findByActivo
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByActivo(true, -1, 10));
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByActivo(true, 0, 0));
    }

    @Test
    @Order(12)
    public void testFindByNombreException() {
        em.close();
        assertThrows(IllegalStateException.class,
                () -> cut.findByNombre("TEST", 0, 10));
    }

    @Test
    @Order(13)
    public void testFindByActivoException() {
        em.close();
        assertThrows(IllegalStateException.class,
                () -> cut.findByActivo(true, 0, 10));
    }

    @Test
    @Order(14)
    public void testCountByNombreException() {
        em.close();
        assertThrows(IllegalStateException.class,
                () -> cut.countByNombre("TEST"));
    }

    @Test
    @Order(15)
    public void testCountByActivoException() {
        em.close();
        assertThrows(IllegalStateException.class,
                () -> cut.countByActivo(true));
    }
}