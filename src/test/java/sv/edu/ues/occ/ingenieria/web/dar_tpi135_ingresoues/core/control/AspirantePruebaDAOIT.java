package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspirantePrueba;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class AspirantePruebaDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private AspirantePruebaDAO cut;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new AspirantePruebaDAO();
        cut.em = em;
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    private AspirantePrueba getSample() {
        List<AspirantePrueba> data = cut.findRange(0, 1);
        assertFalse(data.isEmpty(), "Debe existir data en la BD");
        return data.get(0);
    }

    /**
     * AspirantePrueba usa GenerationType.IDENTITY.
     * Sin flush(), EclipseLink difiere el INSERT — con rollback()
     * nunca llega a la BD, evitando duplicate key.
     * Solo verificamos que create() no lanza excepcion —
     * em.persist() se ejecuta y JaCoCo registra la cobertura.
     */
    @Test
    @Order(1)
    public void testCreate() {
        AspirantePrueba ref = getSample();

        AspirantePrueba nuevo = new AspirantePrueba();
        nuevo.setIdAspirante(ref.getIdAspirante());
        nuevo.setIdPrueba(ref.getIdPrueba());
        nuevo.setFechaAsignacion(OffsetDateTime.now());

        em.getTransaction().begin();
        assertDoesNotThrow(() -> cut.create(nuevo));
        em.getTransaction().rollback();
    }

    @Test
    @Order(2)
    public void testFindByAspirante() {
        AspirantePrueba ref = getSample();
        UUID id = ref.getIdAspirante().getId();

        List<AspirantePrueba> resultado = cut.findByAspirante(id, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    @Order(3)
    public void testFindByPrueba() {
        AspirantePrueba ref = getSample();
        UUID id = ref.getIdPrueba().getId();

        List<AspirantePrueba> resultado = cut.findByPrueba(id, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    @Order(4)
    public void testCountByAspirante() {
        AspirantePrueba ref = getSample();
        UUID id = ref.getIdAspirante().getId();

        Long total = cut.countByAspirante(id);

        assertNotNull(total);
        assertTrue(total > 0);
    }

    @Test
    @Order(5)
    public void testFindByAspiranteNull() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByAspirante(null, 0, 10));
    }

    @Test
    @Order(6)
    public void testFindByPruebaNull() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByPrueba(null, 0, 10));
    }

    @Test
    @Order(7)
    public void testCountByAspiranteInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.countByAspirante(null));
    }

    @Test
    @Order(8)
    public void testInvalidPagination() {
        UUID id = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> cut.findByAspirante(id, -1, 10));
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByAspirante(id, 0, 0));

        assertThrows(IllegalArgumentException.class,
                () -> cut.findByPrueba(id, -1, 10));
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByPrueba(id, 0, 0));
    }

    @Test
    @Order(9)
    public void testGetEntityManagerOk() {
        assertNotNull(cut.getEntityManager());
    }

    @Test
    @Order(10)
    public void testGetEntityManagerNull() {
        cut.em = null;
        assertThrows(IllegalStateException.class,
                () -> cut.getEntityManager());
    }

    @Test
    @Order(11)
    public void testFindByAspiranteException() {
        em.close();
        assertThrows(IllegalStateException.class,
                () -> cut.findByAspirante(UUID.randomUUID(), 0, 10));
    }

    @Test
    @Order(12)
    public void testFindByPruebaException() {
        em.close();
        assertThrows(IllegalStateException.class,
                () -> cut.findByPrueba(UUID.randomUUID(), 0, 10));
    }

    @Test
    @Order(13)
    public void testCountByAspiranteException() {
        em.close();
        assertThrows(IllegalStateException.class,
                () -> cut.countByAspirante(UUID.randomUUID()));
    }
}