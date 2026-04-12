package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PreguntaDistractor;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class PreguntaDistractorDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private PreguntaDistractorDAO cut;
    private PreguntaDistractorDAO daoNoEm;
    private UUID idPregunta;
    private UUID idDistractor;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new PreguntaDistractorDAO();
        cut.em = em;
        daoNoEm = new PreguntaDistractorDAO();
        idPregunta = UUID.fromString("c3000000-0000-0000-0000-000000000001");
        idDistractor = UUID.fromString("d4000000-0000-0000-0000-000000000001");
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
    class FindByIdPregunta {
        //happy path
        @Order(4)
        @Test
        public void findByIdPreguntaHappy() {
            List<PreguntaDistractor> registro = cut.findByIdPregunta(idPregunta, 0, 10);

            assertNotNull(registro);
            assertFalse(registro.isEmpty());
        }

        //id prgunta null devuelve IllegalArgumentException
        @Order(5)
        @Test
        public void findByIdPregunta_idPreguntaNullDevuelveIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> cut.findByIdPregunta(null, 0, 10));
        }

        //first o max invalidos devuelve IllegalArgumentException
        @Order(6)
        @Test
        public void findByIdPregunta_FirstAndMaxInvalidosDevuelveIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> cut.findByIdPregunta(idPregunta, -1, 10));
            assertThrows(IllegalArgumentException.class, () -> cut.findByIdPregunta(idPregunta, 0, -5));
        }

        //fallo DB devuelve IllegalStateException
        @Order(7)
        @Test
        public void findByIdPregunta_falloDBDevuelveIllegalStateException() {
            em.close();
            assertThrows(IllegalStateException.class, () -> cut.findByIdPregunta(idPregunta, 0, 10));
        }

    }

    @Nested
    class FindByIdDistractor {

        //happy path
        @Order(8)
        @Test
        public void findByIdDistractorHappy() {
            List<PreguntaDistractor> registro = cut.findByIdDistractor(idDistractor, 0, 10);

            assertNotNull(registro);
            assertFalse(registro.isEmpty());
        }

        //id prgunta null devuelve IllegalArgumentException
        @Order(9)
        @Test
        public void findByIdDistractor_idDistractorNullDevuelveIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> cut.findByIdDistractor(null, 0, 10));
        }

        //first o max invalidos devuelve IllegalArgumentException
        @Order(10)
        @Test
        public void findByIdDistractor_FirstAndMaxInvalidosDevuelveIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> cut.findByIdDistractor(idDistractor, -1, 10));
            assertThrows(IllegalArgumentException.class, () -> cut.findByIdDistractor(idDistractor, 0, -5));
        }

        //fallo DB devuelve IllegalStateException
        @Order(11)
        @Test
        public void findByIdDistractor_falloDBDevuelveIllegalStateException() {
            em.close();
            assertThrows(IllegalStateException.class, () -> cut.findByIdDistractor(idDistractor, 0, 10));
        }
    }


}
