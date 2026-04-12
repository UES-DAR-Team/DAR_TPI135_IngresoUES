package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspirante;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class JornadaAulaAspiranteDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private JornadaAulaAspiranteDAO cut;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new JornadaAulaAspiranteDAO();
        cut.em = em;
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    /**
     * Prueba: búsqueda por jornada aula.
     * Propósito: verificar que findByJornadaAula retorna una lista válida.
     * Resultado esperado: lista no nula.
     */
    @Order(1)
    @Test
    public void testFindByJornadaAula(){
        List<JornadaAulaAspirante> resultado =
                cut.findByJornadaAula(UUID.randomUUID(), 0, 10);

        assertNotNull(resultado);
    }

    /**
     * Prueba: búsqueda por aspirante prueba.
     * Resultado esperado: lista no nula.
     */
    @Order(2)
    @Test
    public void testFindByAspirantePrueba(){
        List<JornadaAulaAspirante> resultado =
                cut.findByAspirantePrueba(1, 0, 10);

        assertNotNull(resultado);
    }

    /**
     * Prueba: búsqueda por asistencia.
     * Resultado esperado: lista no nula.
     */
    @Order(3)
    @Test
    public void testFindByAsistencia(){
        List<JornadaAulaAspirante> resultado =
                cut.findByAsistencia(true, 0, 10);

        assertNotNull(resultado);
    }

    /**
     * Prueba: conteo por jornada aula.
     * Resultado esperado: valor mayor o igual a cero.
     */
    @Order(4)
    @Test
    public void testCountByJornadaAula(){
        Long total = cut.countByJornadaAula(1);

        assertNotNull(total);
        assertTrue(total >= 0);
    }

    /**
     * Prueba: conteo por asistencia.
     * Resultado esperado: valor mayor o igual a cero.
     */
    @Order(5)
    @Test
    public void testCountByAsistencia(){
        Long total = cut.countByAsistencia(true);

        assertNotNull(total);
        assertTrue(total >= 0);
    }

    /**
     * Prueba: validación de id jornada aula nulo.
     */
    @Order(6)
    @Test
    public void testFindByJornadaAulaNull(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByJornadaAula(null,0,10);
        });
    }

    /**
     * Prueba: validación de id aspirante prueba nulo.
     */
    @Order(7)
    @Test
    public void testFindByAspirantePruebaNull(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByAspirantePrueba(null,0,10);
        });
    }

    /**
     * Prueba: validación de asistencia nula.
     */
    @Order(8)
    @Test
    public void testFindByAsistenciaNull(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByAsistencia(null,0,10);
        });
    }

    /**
     * Prueba: validación de paginación inválida.
     */
    @Order(9)
    @Test
    public void testInvalidPagination(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByJornadaAula(UUID.randomUUID(),-1,10);
        });
    }

    /**
     * Prueba: manejo de errores en findByJornadaAula.
     * Resultado esperado: lista vacía.
     */
    @Order(10)
    @Test
    public void testFindByJornadaAulaException(){
        em.close();

        List<JornadaAulaAspirante> resultado =
                cut.findByJornadaAula(UUID.randomUUID(),0,10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    /**
     * Prueba: manejo de errores en findByAspirantePrueba.
     */
    @Order(11)
    @Test
    public void testFindByAspirantePruebaException(){
        em.close();

        List<JornadaAulaAspirante> resultado =
                cut.findByAspirantePrueba(1,0,10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    /**
     * Prueba: manejo de errores en findByAsistencia.
     */
    @Order(12)
    @Test
    public void testFindByAsistenciaException(){
        em.close();

        List<JornadaAulaAspirante> resultado =
                cut.findByAsistencia(true,0,10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    /**
     * Prueba: manejo de errores en countByJornadaAula.
     * Resultado esperado: 0L.
     */
    @Order(13)
    @Test
    public void testCountByJornadaAulaException(){
        em.close();

        Long total = cut.countByJornadaAula(1);

        assertEquals(0L, total);
    }

    /**
     * Prueba: manejo de errores en countByAsistencia.
     * Resultado esperado: 0L.
     */
    @Order(14)
    @Test
    public void testCountByAsistenciaException(){
        em.close();

        Long total = cut.countByAsistencia(true);

        assertEquals(0L, total);
    }
}