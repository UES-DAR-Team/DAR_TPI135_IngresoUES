package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aula;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class AulaDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private AulaDAO cut;
    private AulaDAO daoNoEm;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new AulaDAO();
        cut.em = em;
        daoNoEm = new AulaDAO();
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    @Nested
    class FindByCapacidadMin {

        @Order(1)
        @Test
        public void findByCapacidadMinRetornaAulasConCapacidadMayorOIgual() {
            List<Aula> aulas = cut.findRange(0, 1);
            assertFalse(aulas.isEmpty(), "Debe existir al menos un aula para la prueba");
            Integer capacidad = aulas.getFirst().getCapacidad();

            List<Aula> resultado = cut.findByCapacidadMin(capacidad, 0, 10);

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
            for (Aula aula : resultado) {
                assertTrue(aula.getCapacidad() >= capacidad);
            }
        }

        @Order(2)
        @Test
        public void findByCapacidadMinCapacidadNullParametroInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByCapacidadMin(null, 0, 10));
        }

        @Order(3)
        @Test
        public void findByCapacidadMinFirstNegativoOMaxCeroPaginacionInvalida() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByCapacidadMin(10, -1, 10));
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByCapacidadMin(10, 0, 0));
        }

        @Order(4)
        @Test
        public void findByCapacidadMinEntityManagerNullErrorSistema() {
            assertThrows(IllegalStateException.class, () ->
                    daoNoEm.findByCapacidadMin(10, 0, 10));
        }

        @Order(5)
        @Test
        public void findByCapacidadMinFalloEnBaseErrorSistema() {
            em.close();
            assertThrows(IllegalStateException.class, () ->
                    cut.findByCapacidadMin(10, 0, 10));
        }

        @Order(6)
        @Test
        public void findByCapacidadMinRetornaListaVaciaCuandoNoHayResultados() {
            List<Aula> resultado = cut.findByCapacidadMin(999999, 0, 10);
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    class FindByNombre {

        @Order(7)
        @Test
        public void findByNombreRetornaAulasConNombreExacto() {
            List<Aula> aulas = cut.findRange(0, 1);
            assertFalse(aulas.isEmpty(), "Debe existir al menos un aula para la prueba");
            String nombre = aulas.getFirst().getNombreAula();

            List<Aula> resultado = cut.findByNombre(nombre, 0, 10);

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
            for (Aula aula : resultado) {
                assertEquals(nombre, aula.getNombreAula());
            }
        }

        @Order(8)
        @Test
        public void findByNombreNombreNullParametroIvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByNombre(null, 0, 10));
        }

        @Order(9)
        @Test
        public void findByNombreNombreVacioOconEspacioParametroInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByNombre("", 0, 10));
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByNombre("   ", 0, 10));
        }

        @Order(10)
        @Test
        public void findByNombreFirstNegativoOMaxCeroParametroInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByNombre("Aula", -1, 10));
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findByNombre("Aula", 0, 0));
        }

        @Order(11)
        @Test
        public void findByNombreEntityManagerNullErrorSistema() {
            assertThrows(IllegalStateException.class, () ->
                    daoNoEm.findByNombre("Aula", 0, 10));
        }

        @Order(12)
        @Test
        public void findByNombreFalloEnBaseErrorSistema() {
            em.close();
            assertThrows(IllegalStateException.class, () ->
                    cut.findByNombre("Aula", 0, 10));
        }

        @Order(13)
        @Test
        public void findByNombreNombreInexistenteRetornaListaVacia() {
            List<Aula> resultado = cut.findByNombre("NO_EXISTE_12345", 0, 10);
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }

        @Order(14)
        @Test
        public void findByNombrePaginacionFueraDeRangoRetornaListaVacia() {
            List<Aula> aulas = cut.findRange(0, 5);
            assertFalse(aulas.isEmpty(), "Debe existir al menos un aula para la prueba");
            String nombre = aulas.getFirst().getNombreAula();

            List<Aula> resultado = cut.findByNombre(nombre, 9999, 10);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    class FindActivos {

        @Order(15)
        @Test
        public void findActivosRetornaAulasConActivoTrue() {
            List<Aula> resultado = cut.findActivos(0, 10);

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
            for (Aula aula : resultado) {
                assertTrue(aula.getActivo());
            }
        }

        @Order(16)
        @Test
        public void findActivosPaginacionInvalidaParametrosInvalidos() {
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findActivos(-1, 10));
            assertThrows(IllegalArgumentException.class, () ->
                    cut.findActivos(0, 0));
        }

        @Order(17)
        @Test
        public void findActivosEntityManagerNullErrorSistema() {
            assertThrows(IllegalStateException.class, () ->
                    daoNoEm.findActivos(0, 10));
        }

        @Order(18)
        @Test
        public void findActivosFalloEnBaseErrorSistema() {
            em.close();
            assertThrows(IllegalStateException.class, () ->
                    cut.findActivos(0, 10));
        }
    }

    @Test
    public void getEntityClassRetornaClaseAula() {
        Class<?> cls = cut.getEntityClass();
        assertNotNull(cls);
        assertEquals(Aula.class, cls);
    }
}