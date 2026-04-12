package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class DistractorDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private DistractorDAO cut;
    private DistractorDAO daoNoEm;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new DistractorDAO();
        cut.em = em;
        daoNoEm = new DistractorDAO();
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    @Order(1)
    @Test
    public void findByCoincidenciaTextoHappy() {
        List<Distractor> registro = cut.findByCoincidenciaTexto("Símil", 0, 10);

        Assertions.assertNotNull(registro);
        Assertions.assertFalse(registro.isEmpty());
    }

    @Order(2)
    @Test
    public void findByCoincidenciaTexto_textoNullDevuelveIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByCoincidenciaTexto(null, 0, 10);
        });
    }

    @Order(3)
    @Test
    public void findByCoincidenciaTexto_FirstAndMaxInvalidosDevuelveIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByCoincidenciaTexto("Símil", -1, 10);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByCoincidenciaTexto("Símil", 0, -5);
        });
    }

    @Order(4)
    @Test
    public void findByCoincidenciaTexto_textoVacioDevuelveListaVacia() {
        assertThrows(IllegalArgumentException.class, ()-> cut.findByCoincidenciaTexto("", 0, 10));

    }

    @Order(5)
    @Test
    public void findByCoincidenciaTexto_falloDBDevuelveIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> {
            daoNoEm.findByCoincidenciaTexto("Símil", 0, 10);
        });
    }

    @Nested
    class Count {
        @Order(6)
        @Test
        public void count_happy() {
            int total = cut.count();
            assertTrue(total > 0);
        }

        @Order(7)
        @Test
        public void count_emNullDevuelveIllegalStateException() {
            assertThrows(IllegalStateException.class, () -> daoNoEm.count());
        }

        @Order(8)
        @Test
        public void count_falloDBDevuelveIllegalSatateException() {
            em.close();
            assertThrows(IllegalStateException.class, () -> cut.count());
        }
    }

}
