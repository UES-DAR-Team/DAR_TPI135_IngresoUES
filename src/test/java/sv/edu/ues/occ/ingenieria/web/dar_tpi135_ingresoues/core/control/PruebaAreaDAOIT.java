package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaArea;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class PruebaAreaDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private PruebaAreaDAO cut;
    private PruebaAreaDAO daoNoEm;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new PruebaAreaDAO();
        cut.em = em;
        daoNoEm = new PruebaAreaDAO();
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    // =========================
    // FIND BY PRUEBA
    // =========================
    @Nested
    class FindByPrueba {

        @Order(1)
        @Test
        public void findByPruebaRetornaResultados() {
            List<PruebaArea> lista = cut.findRange(0, 1);
            assertFalse(lista.isEmpty());
            UUID idPrueba = lista.getFirst().getIdPrueba().getId();

            List<PruebaArea> resultado = cut.findByPrueba(idPrueba, 0, 10);

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
        }

        @Order(2)
        @Test
        public void findByPruebaParametroNullInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPrueba(null, 0, 10));
        }

        @Order(3)
        @Test
        public void findByPruebaPaginacionInvalida() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPrueba(UUID.randomUUID(), -1, 10));
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPrueba(UUID.randomUUID(), 0, 0));
        }

        @Order(4)
        @Test
        public void findByPruebaEntityManagerNullErrorSistema() {
            assertThrows(IllegalStateException.class, () ->
                    daoNoEm.findByPrueba(UUID.randomUUID(), 0, 10));
        }

        @Order(5)
        @Test
        public void findByPruebaFalloEnBaseErrorSistema() {
            em.close();
            assertThrows(IllegalStateException.class, () ->
                    cut.findByPrueba(UUID.randomUUID(), 0, 10));
        }

        @Order(6)
        @Test
        public void findByPruebaSinResultadosRetornaListaVacia() {
            List<PruebaArea> resultado = cut.findByPrueba(UUID.randomUUID(), 0, 10);
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    class FindByAreaConocimiento {

        @Order(7)
        @Test
        public void findByAreaConocimientoRetornaResultados() {
            List<PruebaArea> lista = cut.findRange(0, 1);
            assertFalse(lista.isEmpty());
            UUID idArea = lista.getFirst().getIdAreaConocimiento().getId();

            List<PruebaArea> resultado = cut.findByAreaConocimiento(idArea, 0, 10);

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
        }

        @Order(8)
        @Test
        public void findByAreaConocimientoParametroNullInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByAreaConocimiento(null, 0, 10));
        }

        @Order(9)
        @Test
        public void findByAreaConocimientoPaginacionInvalida() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByAreaConocimiento(UUID.randomUUID(), -1, 10));
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByAreaConocimiento(UUID.randomUUID(), 0, 0));
        }

        @Order(10)
        @Test
        public void findByAreaConocimientoEntityManagerNullErrorSistema() {
            assertThrows(IllegalStateException.class, () ->
                    daoNoEm.findByAreaConocimiento(UUID.randomUUID(), 0, 10));
        }

        @Order(11)
        @Test
        public void findByAreaConocimientoFalloEnBaseErrorSistema() {
            em.close();
            assertThrows(IllegalStateException.class, () ->
                    cut.findByAreaConocimiento(UUID.randomUUID(), 0, 10));
        }

        @Order(12)
        @Test
        public void findByAreaConocimientoSinResultadosRetornaListaVacia() {
            List<PruebaArea> resultado = cut.findByAreaConocimiento(UUID.randomUUID(), 0, 10);
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    class FindByNumPreguntasMin {

        @Order(13)
        @Test
        public void findByNumPreguntasMinRetornaResultados() {
            List<PruebaArea> lista = cut.findRange(0, 1);
            assertFalse(lista.isEmpty());
            Short num = lista.getFirst().getNumPreguntas();

            List<PruebaArea> resultado = cut.findByNumPreguntasMin(num, 0, 10);

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
        }

        @Order(14)
        @Test
        public void findByNumPreguntasMinParametroNullInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByNumPreguntasMin(null, 0, 10));
        }

        @Order(15)
        @Test
        public void findByNumPreguntasMinPaginacionInvalida() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByNumPreguntasMin((short) 5, -1, 10));
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByNumPreguntasMin((short) 5, 0, 0));
        }

        @Order(16)
        @Test
        public void findByNumPreguntasMinEntityManagerNullErrorSistema() {
            assertThrows(IllegalStateException.class, () ->
                    daoNoEm.findByNumPreguntasMin((short) 5, 0, 10));
        }

        @Order(17)
        @Test
        public void findByNumPreguntasMinFalloEnBaseErrorSistema() {
            em.close();
            assertThrows(IllegalStateException.class, () ->
                    cut.findByNumPreguntasMin((short) 5, 0, 10));
        }

        @Order(18)
        @Test
        public void findByNumPreguntasMinSinResultadosRetornaListaVacia() {
            List<PruebaArea> resultado = cut.findByNumPreguntasMin((short) 9999, 0, 10);
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Test
    public void getEntityClassRetornaClasePruebaArea() {
        Class<?> cls = cut.getEntityClass();
        assertNotNull(cls);
        assertEquals(PruebaArea.class, cls);
    }
}