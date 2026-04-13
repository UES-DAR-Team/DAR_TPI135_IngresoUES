package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPregunta;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class PruebaAreaPreguntaDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private PruebaAreaPreguntaDAO cut;
    private PruebaAreaPreguntaDAO daoNoEm;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new PruebaAreaPreguntaDAO();
        cut.em = em;
        daoNoEm = new PruebaAreaPreguntaDAO();
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    @Nested
    class FindByPruebaArea {

        @Order(1)
        @Test
        public void findByPruebaAreaRetornaResultados() {
            List<PruebaAreaPregunta> lista = cut.findRange(0, 1);
            assertFalse(lista.isEmpty());

            Integer idPruebaArea;
            idPruebaArea = lista.getFirst().getIdPruebaArea().getId();

            List<PruebaAreaPregunta> resultado =
                    cut.findByPruebaArea(idPruebaArea, 0, 10);

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
        }

        @Order(2)
        @Test
        public void findByPruebaAreaParametroNullInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPruebaArea(null, 0, 10));
        }

        @Order(3)
        @Test
        public void findByPruebaAreaPaginacionInvalida() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPruebaArea(1, -1, 10));
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPruebaArea(1, 0, 0));
        }

        @Order(4)
        @Test
        public void findByPruebaAreaEntityManagerNullErrorSistema() {
            assertThrows(IllegalStateException.class, () ->
                    daoNoEm.findByPruebaArea(1, 0, 10));
        }

        @Order(5)
        @Test
        public void findByPruebaAreaFalloEnBaseErrorSistema() {
            em.close();
            assertThrows(IllegalStateException.class, () ->
                    cut.findByPruebaArea(1, 0, 10));
        }

        @Order(6)
        @Test
        public void findByPruebaAreaSinResultadosRetornaListaVacia() {
            List<PruebaAreaPregunta> resultado =
                    cut.findByPruebaArea(999999, 0, 10);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    class FindByPregunta {

        @Order(7)
        @Test
        public void findByPreguntaRetornaResultados() {
            List<PruebaAreaPregunta> lista = cut.findRange(0, 1);
            assertFalse(lista.isEmpty());

            UUID idPregunta = lista.getFirst().getIdPregunta().getId();

            List<PruebaAreaPregunta> resultado =
                    cut.findByPregunta(idPregunta, 0, 10);

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
        }

        @Order(8)
        @Test
        public void findByPreguntaParametroNullInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPregunta(null, 0, 10));
        }

        @Order(9)
        @Test
        public void findByPreguntaPaginacionInvalida() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPregunta(UUID.randomUUID(), -1, 10));
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPregunta(UUID.randomUUID(), 0, 0));
        }

        @Order(10)
        @Test
        public void findByPreguntaEntityManagerNullErrorSistema() {
            assertThrows(IllegalStateException.class, () ->
                    daoNoEm.findByPregunta(UUID.randomUUID(), 0, 10));
        }

        @Order(11)
        @Test
        public void findByPreguntaFalloEnBaseErrorSistema() {
            em.close();
            assertThrows(IllegalStateException.class, () ->
                    cut.findByPregunta(UUID.randomUUID(), 0, 10));
        }

        @Order(12)
        @Test
        public void findByPreguntaSinResultadosRetornaListaVacia() {
            List<PruebaAreaPregunta> resultado =
                    cut.findByPregunta(UUID.randomUUID(), 0, 10);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }
}