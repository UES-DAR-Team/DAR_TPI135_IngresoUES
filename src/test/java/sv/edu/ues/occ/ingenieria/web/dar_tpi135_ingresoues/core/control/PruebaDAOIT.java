package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class PruebaDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private PruebaDAO cut;
    private PruebaDAO daoNoEm;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new PruebaDAO();
        cut.em = em;
        daoNoEm = new PruebaDAO();
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    @Nested
    class FindByNombre {

        @Order(1)
        @Test
        public void findByNombreRetornaPruebasConNombreExacto() {
            List<Prueba> pruebas = cut.findRange(0, 1);
            assertFalse(pruebas.isEmpty(), "Debe existir al menos un dato");

            String nombre = pruebas.getFirst().getNombrePrueba();

            List<Prueba> resultado = cut.findByNombre(nombre, 0, 10);

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
            for (Prueba p : resultado) {
                assertEquals(nombre, p.getNombrePrueba());
            }
        }

        @Order(2)
        @Test
        public void findByNombreNombreNullParametroInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByNombre(null, 0, 10));
        }

        @Order(3)
        @Test
        public void findByNombreNombreVacioOEspaciosParametroInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByNombre("", 0, 10));
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByNombre("   ", 0, 10));
        }

        @Order(4)
        @Test
        public void findByNombrePaginacionInvalidaParametrosInvalidos() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByNombre("dato", -1, 10));
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByNombre("dato", 0, 0));
        }

        @Order(5)
        @Test
        public void findByNombreEntityManagerNullErrorSistema() {
            assertThrows(IllegalStateException.class, () ->
                    daoNoEm.findByNombre("dato", 0, 10));
        }

        @Order(6)
        @Test
        public void findByNombreFalloEnBaseErrorSistema() {
            em.close();
            assertThrows(IllegalStateException.class, () ->
                    cut.findByNombre("dato", 0, 10));
        }

        @Order(7)
        @Test
        public void findByNombreNombreInexistenteRetornaListaVacia() {
            List<Prueba> resultado = cut.findByNombre("NO_EXISTE_12345", 0, 10);
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }

        @Order(8)
        @Test
        public void findByNombrePaginacionFueraDeRangoRetornaListaVacia() {
            List<Prueba> pruebas = cut.findRange(0, 5);
            assertFalse(pruebas.isEmpty());
            String nombre = pruebas.getFirst().getNombrePrueba();

            List<Prueba> resultado = cut.findByNombre(nombre, 9999, 10);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    class FindActivas {

        @Order(9)
        @Test
        public void findActivasRetornaPruebasActivas() {
            List<Prueba> resultado = cut.findActivas(0, 10);

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());

            for (Prueba p : resultado) {
                assertTrue(p.getActivo());
            }
        }

        @Order(10)
        @Test
        public void findActivasPaginacionInvalidaParametrosInvalidos() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findActivas(-1, 10));
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findActivas(0, 0));
        }

        @Order(11)
        @Test
        public void findActivasEntityManagerNullErrorSistema() {
            assertThrows(IllegalStateException.class, () ->
                    daoNoEm.findActivas(0, 10));
        }

        @Order(12)
        @Test
        public void findActivasFalloEnBaseErrorSistema() {
            em.close();
            assertThrows(IllegalStateException.class, () ->
                    cut.findActivas(0, 10));
        }
    }

    @Test
    public void getEntityClassRetornaClasePrueba() {
        Class<?> cls = cut.getEntityClass();
        assertNotNull(cls);
        assertEquals(Prueba.class, cls);
    }
}