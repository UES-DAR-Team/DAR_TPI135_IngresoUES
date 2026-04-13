package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPreguntaDistractor;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class PruebaAreaPreguntaDistractorDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private PruebaAreaPreguntaDistractorDAO cut;
    private PruebaAreaPreguntaDistractorDAO daoNoEm;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new PruebaAreaPreguntaDistractorDAO();
        cut.em = em;
        daoNoEm = new PruebaAreaPreguntaDistractorDAO();
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }


    @Nested
    class FindByPruebaAreaPregunta {

        @Order(1)
        @Test
        public void findByPruebaAreaPreguntaRetornaResultados() {
            List<PruebaAreaPreguntaDistractor> lista = cut.findRange(0, 1);
            assertFalse(lista.isEmpty());

            Integer id = lista.getFirst().getIdPruebaAreaPregunta().getId();

            List<PruebaAreaPreguntaDistractor> resultado =
                    cut.findByPruebaAreaPregunta(id, 0, 10);

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
        }

        @Order(2)
        @Test
        public void findByPruebaAreaPreguntaParametroNullInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPruebaAreaPregunta(null, 0, 10));
        }

        @Order(3)
        @Test
        public void findByPruebaAreaPreguntaPaginacionInvalida() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPruebaAreaPregunta(1, -1, 10));
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPruebaAreaPregunta(1, 0, 0));
        }

        @Order(4)
        @Test
        public void findByPruebaAreaPreguntaEntityManagerNullErrorSistema() {
            assertThrows(IllegalStateException.class, () ->
                    daoNoEm.findByPruebaAreaPregunta(1, 0, 10));
        }

        @Order(5)
        @Test
        public void findByPruebaAreaPreguntaFalloEnBaseErrorSistema() {
            em.close();
            assertThrows(IllegalStateException.class, () ->
                    cut.findByPruebaAreaPregunta(1, 0, 10));
        }

        @Order(6)
        @Test
        public void findByPruebaAreaPreguntaSinResultadosRetornaListaVacia() {
            List<PruebaAreaPreguntaDistractor> resultado =
                    cut.findByPruebaAreaPregunta(999999, 0, 10);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    class FindRespuestaCorrecta {

        @Order(7)
        @Test
        public void findRespuestaCorrectaRetornaResultado() {
            List<PruebaAreaPreguntaDistractor> lista = cut.findRange(0, 1);
            assertFalse(lista.isEmpty());

            Integer id = lista.getFirst().getIdPruebaAreaPregunta().getId();

            PruebaAreaPreguntaDistractor resultado =
                    cut.findRespuestaCorrecta(id);

            // Puede ser null o no, dependiendo de datos
            assertNotNull(resultado); // cuando se sabe que hay datos
        }

        @Order(8)
        @Test
        public void findRespuestaCorrectaParametroNullInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findRespuestaCorrecta(null));
        }

        @Order(9)
        @Test
        public void findRespuestaCorrectaSinResultadoRetornaNull() {
            PruebaAreaPreguntaDistractor resultado =
                    cut.findRespuestaCorrecta(999999);

            assertNull(resultado);
        }

        @Order(10)
        @Test
        public void findRespuestaCorrectaFalloEnBaseErrorSistema() {
            em.close();
            assertThrows(IllegalStateException.class, () ->
                    cut.findRespuestaCorrecta(1));
        }
    }

    @Nested
    class FindByDistractor {

        @Order(11)
        @Test
        public void findByDistractorRetornaResultados() {
            List<PruebaAreaPreguntaDistractor> lista = cut.findRange(0, 1);
            assertFalse(lista.isEmpty());

            UUID idDistractor = lista.getFirst().getIdDistractor().getId();

            List<PruebaAreaPreguntaDistractor> resultado =
                    cut.findByDistractor(idDistractor, 0, 10);

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
        }

        @Order(12)
        @Test
        public void findByDistractorParametroNullInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByDistractor(null, 0, 10));
        }

        @Order(13)
        @Test
        public void findByDistractorPaginacionInvalida() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByDistractor(UUID.randomUUID(), -1, 10));
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByDistractor(UUID.randomUUID(), 0, 0));
        }

        @Order(14)
        @Test
        public void findByDistractorEntityManagerNullErrorSistema() {
            assertThrows(IllegalStateException.class, () ->
                    daoNoEm.findByDistractor(UUID.randomUUID(), 0, 10));
        }

        @Order(15)
        @Test
        public void findByDistractorFalloEnBaseErrorSistema() {
            em.close();
            assertThrows(IllegalStateException.class, () ->
                    cut.findByDistractor(UUID.randomUUID(), 0, 10));
        }

        @Order(16)
        @Test
        public void findByDistractorSinResultadosRetornaListaVacia() {
            List<PruebaAreaPreguntaDistractor> resultado =
                    cut.findByDistractor(UUID.randomUUID(), 0, 10);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }
}