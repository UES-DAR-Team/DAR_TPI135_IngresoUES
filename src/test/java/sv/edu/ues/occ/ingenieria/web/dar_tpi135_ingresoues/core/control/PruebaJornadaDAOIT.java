package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaJornada;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class PruebaJornadaDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private PruebaJornadaDAO cut;
    private PruebaJornadaDAO daoNoEm;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new PruebaJornadaDAO();
        cut.em = em;
        daoNoEm = new PruebaJornadaDAO();
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    @Nested
    class FindByJornada {

        @Order(1)
        @Test
        public void findByJornadaRetornaResultadosValidos() {
            List<PruebaJornada> lista = cut.findRange(0, 1);
            assertFalse(lista.isEmpty());

            UUID idJornada = lista.getFirst().getIdJornada().getId();

            List<PruebaJornada> resultado = cut.findByJornada(idJornada, 0, 10);

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
            for (PruebaJornada pj : resultado) {
                assertEquals(idJornada, pj.getIdJornada().getId());
            }
        }

        @Order(2)
        @Test
        public void findByJornadaIdNullParametroInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByJornada(null, 0, 10));
        }

        @Order(3)
        @Test
        public void findByJornadaPaginacionInvalida() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByJornada(UUID.randomUUID(), -1, 10));

            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByJornada(UUID.randomUUID(), 0, 0));
        }

        @Order(4)
        @Test
        public void findByJornadaEntityManagerNullErrorSistema() {
            assertThrows(IllegalStateException.class, () ->
                    daoNoEm.findByJornada(UUID.randomUUID(), 0, 10));
        }

        @Order(5)
        @Test
        public void findByJornadaFalloEnBaseErrorSistema() {
            em.close();
            assertThrows(IllegalStateException.class, () ->
                    cut.findByJornada(UUID.randomUUID(), 0, 10));
        }

        @Order(6)
        @Test
        public void findByJornadaSinResultadosRetornaListaVacia() {
            List<PruebaJornada> resultado = cut.findByJornada(UUID.randomUUID(), 0, 10);
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    class FindByPrueba {

        @Order(7)
        @Test
        public void findByPruebaRetornaResultadosValidos() {
            List<PruebaJornada> lista = cut.findRange(0, 1);
            assertFalse(lista.isEmpty());

            UUID idPrueba = lista.getFirst().getIdPrueba().getId();

            List<PruebaJornada> resultado = cut.findByPrueba(idPrueba, 0, 10);

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
            for (PruebaJornada pj : resultado) {
                assertEquals(idPrueba, pj.getIdPrueba().getId());
            }
        }

        @Order(8)
        @Test
        public void findByPruebaIdNullParametroInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPrueba(null, 0, 10));
        }

        @Order(9)
        @Test
        public void findByPruebaPaginacionInvalida() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPrueba(UUID.randomUUID(), -1, 10));

            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPrueba(UUID.randomUUID(), 0, 0));
        }

        @Order(10)
        @Test
        public void findByPruebaEntityManagerNullErrorSistema() {
            assertThrows(IllegalStateException.class, () ->
                    daoNoEm.findByPrueba(UUID.randomUUID(), 0, 10));
        }

        @Order(11)
        @Test
        public void findByPruebaFalloEnBaseErrorSistema() {
            em.close();
            assertThrows(IllegalStateException.class, () ->
                    cut.findByPrueba(UUID.randomUUID(), 0, 10));
        }

        @Order(12)
        @Test
        public void findByPruebaSinResultadosRetornaListaVacia() {
            List<PruebaJornada> resultado = cut.findByPrueba(UUID.randomUUID(), 0, 10);
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    class FindByPruebaAndJornada {

        @Order(13)
        @Test
        public void findByPruebaAndJornadaRetornaResultados() {
            List<PruebaJornada> lista = cut.findRange(0, 1);
            assertFalse(lista.isEmpty());
            UUID idPrueba = lista.getFirst().getIdPrueba().getId();
            UUID idJornada = lista.getFirst().getIdJornada().getId();

            List<PruebaJornada> resultado =
                    cut.findByPruebaAndJornada(idPrueba, idJornada, 0, 10);

            assertNotNull(resultado);
        }

        @Order(14)
        @Test
        public void findByPruebaAndJornadaParametrosInvalidos() {
            UUID idValido = UUID.randomUUID();

            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPruebaAndJornada(null, idValido, 0, 10));

            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPruebaAndJornada(idValido, null, 0, 10));
        }

        @Order(15)
        @Test
        public void findByPruebaAndJornadaPaginacionInvalida() {
            UUID idPrueba = UUID.randomUUID();
            UUID idJornada = UUID.randomUUID();

            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPruebaAndJornada(idPrueba, idJornada, -1, 10));

            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByPruebaAndJornada(idPrueba, idJornada, 0, 0));
        }

        @Order(16)
        @Test
        public void findByPruebaAndJornadaEntityManagerNullErrorSistema() {
            UUID idPrueba = UUID.randomUUID();
            UUID idJornada = UUID.randomUUID();

            assertThrows(IllegalStateException.class, () ->
                    daoNoEm.findByPruebaAndJornada(idPrueba, idJornada, 0, 10));
        }

        @Order(17)
        @Test
        public void findByPruebaAndJornadaFalloEnBaseErrorSistema() {
            UUID idPrueba = UUID.randomUUID();
            UUID idJornada = UUID.randomUUID();

            em.close();

            assertThrows(IllegalStateException.class, () ->
                    cut.findByPruebaAndJornada(idPrueba, idJornada, 0, 10));
        }

        @Test
        public void getEntityClassRetornaClasePruebaJornada() {
            Class<?> cls = cut.getEntityClass();
            assertNotNull(cls);
            assertEquals(PruebaJornada.class, cls);
        }
    }
}