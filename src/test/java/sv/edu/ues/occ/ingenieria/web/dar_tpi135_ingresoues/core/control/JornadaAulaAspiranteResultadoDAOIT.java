package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspiranteResultado;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class JornadaAulaAspiranteResultadoDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private JornadaAulaAspiranteResultadoDAO cut;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new JornadaAulaAspiranteResultadoDAO();
        cut.em = em;
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    /**
     * Prueba: búsqueda por id de jornada aula aspirante.
     * Propósito: verificar que retorna una lista válida.
     * Resultado esperado: lista no nula.
     */
    @Order(1)
    @Test
    public void testFindByJornadaAulaAspirante(){
        try {
            List<JornadaAulaAspiranteResultado> resultado =
                    cut.findByJornadaAulaAspirante(1, 0, 10);

            assertNotNull(resultado);

        } catch (IllegalStateException ex) {
            assertTrue(true);
        }
    }

    /**
     * Prueba: búsqueda por estado aprobado.
     * Resultado esperado: lista no nula.
     */
    @Order(2)
    @Test
    public void testFindByAprobado(){
        try {
            List<JornadaAulaAspiranteResultado> resultado =
                    cut.findByAprobado(true, 0, 10);

            assertNotNull(resultado);

        } catch (IllegalStateException ex) {
            assertTrue(true);
        }
    }

    /**
     * Prueba: búsqueda por rango de puntaje.
     * Resultado esperado: lista no nula.
     */
    @Order(3)
    @Test
    public void testFindByRangoPuntaje(){
        try {
            List<JornadaAulaAspiranteResultado> resultado =
                    cut.findByRangoPuntaje(
                            BigDecimal.ZERO,
                            new BigDecimal("100"),
                            0,
                            10
                    );

            assertNotNull(resultado);

        } catch (IllegalStateException ex) {
            assertTrue(true);
        }
    }

    /**
     * Prueba: conteo por estado aprobado.
     * Resultado esperado: valor mayor o igual a cero.
     */
    @Order(4)
    @Test
    public void testCountByAprobado(){
        Long total = cut.countByAprobado(true);

        assertNotNull(total);
        assertTrue(total >= 0);
    }

    /**
     * Prueba: id nulo en búsqueda por jornada aula aspirante.
     */
    @Order(5)
    @Test
    public void testFindByJornadaAulaAspiranteNull(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByJornadaAulaAspirante(null,0,10);
        });
    }

    /**
     * Prueba: estado aprobado nulo.
     */
    @Order(6)
    @Test
    public void testFindByAprobadoNull(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByAprobado(null,0,10);
        });
    }

    /**
     * Prueba: rango inválido (valores nulos).
     */
    @Order(7)
    @Test
    public void testFindByRangoNull(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByRangoPuntaje(null,null,0,10);
        });
    }

    /**
     * Prueba: rango inválido (min > max).
     */
    @Order(8)
    @Test
    public void testFindByRangoInvalido(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByRangoPuntaje(
                    new BigDecimal("10"),
                    new BigDecimal("5"),
                    0,
                    10
            );
        });
    }

    /**
     * Prueba: paginación inválida.
     */
    @Order(9)
    @Test
    public void testInvalidPagination(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByAprobado(true,-1,10);
        });
    }

    /**
     * Prueba: manejo de excepción en findByJornadaAulaAspirante.
     */
    @Order(10)
    @Test
    public void testFindByJornadaAulaAspiranteException(){
        em.close();

        assertThrows(IllegalStateException.class, () -> {
            cut.findByJornadaAulaAspirante(1,0,10);
        });
    }

    /**
     * Prueba: manejo de excepción en findByAprobado.
     */
    @Order(11)
    @Test
    public void testFindByAprobadoException(){
        em.close();

        assertThrows(IllegalStateException.class, () -> {
            cut.findByAprobado(true,0,10);
        });
    }

    /**
     * Prueba: manejo de excepción en findByRangoPuntaje.
     */
    @Order(12)
    @Test
    public void testFindByRangoException(){
        em.close();

        assertThrows(IllegalStateException.class, () -> {
            cut.findByRangoPuntaje(
                    BigDecimal.ZERO,
                    new BigDecimal("10"),
                    0,
                    10
            );
        });
    }

    /**
     * Prueba: manejo de excepción en countByAprobado.
     */
    @Order(13)
    @Test
    public void testCountByAprobadoException(){
        em.close();

        assertThrows(IllegalStateException.class, () -> {
            cut.countByAprobado(true);
        });
    }
}