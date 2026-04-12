package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;


import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PreguntaAreaConocimiento;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PreguntaDistractor;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class PreguntaAreaDAOIT extends BaseIntegrationAbstract {


    private EntityManager em;
    private PreguntaAreaConocimientoDAO cut;
    private PreguntaAreaConocimientoDAO daoNoEm;
    private UUID idPregunta;
    private UUID idArea;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new PreguntaAreaConocimientoDAO();
        cut.em = em;
        daoNoEm = new PreguntaAreaConocimientoDAO();
        idPregunta = UUID.fromString("c3000000-0000-0000-0000-000000000001");
        idArea = UUID.fromString("a1000000-0000-0000-0000-000000000001");
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
            List<PreguntaAreaConocimiento> registro = cut.findByIdPregunta(idPregunta, 0, 10);

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
    class FindByIdArea {

        //happy path
        @Order(8)
        @Test
        public void findByIdDistractorHappy() {
            List<PreguntaAreaConocimiento> registro = cut.findPreguntaByIdAreaConocimiento(idArea, 0, 10);

            assertNotNull(registro);
            assertFalse(registro.isEmpty());
        }

        //id prgunta null devuelve IllegalArgumentException
        @Order(9)
        @Test
        public void findByIdDistractor_idDistractorNullDevuelveIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> cut.findPreguntaByIdAreaConocimiento(null, 0, 10));
        }

        //first o max invalidos devuelve IllegalArgumentException
        @Order(10)
        @Test
        public void findByIdDistractor_FirstAndMaxInvalidosDevuelveIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> cut.findPreguntaByIdAreaConocimiento(idArea, -1, 10));
            assertThrows(IllegalArgumentException.class, () -> cut.findPreguntaByIdAreaConocimiento(idArea, 0, -5));
        }

        //fallo DB devuelve IllegalStateException
        @Order(11)
        @Test
        public void findByIdDistractor_falloDBDevuelveIllegalStateException() {
            em.close();
            assertThrows(IllegalStateException.class, () -> cut.findPreguntaByIdAreaConocimiento(idArea, 0, 10));
        }
    }
}
