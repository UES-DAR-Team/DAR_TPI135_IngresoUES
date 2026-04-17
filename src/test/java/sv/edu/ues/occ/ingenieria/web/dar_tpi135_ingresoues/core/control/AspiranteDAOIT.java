package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class AspiranteDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private AspiranteDAO cut;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new AspiranteDAO();
        cut.em = em;
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    /**
     * Prueba: conteo general de registros.
     * Resultado esperado: total > 0.
     */
    @Test
    @Order(1)
    public void testCount(){
        assertTrue(cut.count() > 0);
    }

    /**
     * Prueba: búsqueda por nombre válido.
     * Resultado esperado: lista no vacía.
     */
    @Test
    @Order(2)
    public void testFindByNombre(){
        Aspirante ref = cut.findRange(0,1).getFirst();

        List<Aspirante> resultado = cut.findByNombre(
                ref.getNombreAspirante(), 0, 10
        );

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    /**
     * Prueba: búsqueda de aspirantes activos.
     * Resultado esperado: lista no vacía.
     */
    @Test
    @Order(3)
    public void testFindActivos(){
        List<Aspirante> resultado = cut.findActivos(0,10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    /**
     * Prueba: búsqueda por documento válido.
     * Resultado esperado: lista no vacía.
     */
    @Test
    @Order(4)
    public void testFindByDocumento(){
        Aspirante ref = cut.findRange(0,1).getFirst();

        List<Aspirante> resultado = cut.findByDocumento(
                ref.getIdentificacion(), 0, 10
        );

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    /**
     * Prueba: búsqueda por estado válido.
     * Resultado esperado: lista no vacía.
     */
    @Test
    @Order(5)
    public void testFindByEstado(){
        Aspirante ref = cut.findRange(0,1).getFirst();

        List<Aspirante> resultado = cut.findByEstado(
                ref.getActivo(), 0, 10
        );

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    /**
     * Prueba: conteo por nombre válido.
     * Resultado esperado: total > 0.
     */
    @Test
    @Order(6)
    public void testCountByNombre(){
        Aspirante ref = cut.findRange(0,1).getFirst();

        Long total = cut.countByNombre(ref.getNombreAspirante());

        assertNotNull(total);
        assertTrue(total > 0);
    }

    /**
     * Prueba: nombre inválido (null y blank).
     * Resultado esperado: IllegalArgumentException.
     */
    @Test
    @Order(7)
    public void testFindByNombreInvalid(){
        assertThrows(IllegalArgumentException.class, () -> cut.findByNombre(null,0,10));
        assertThrows(IllegalArgumentException.class, () -> cut.findByNombre(" ",0,10));
    }

    /**
     * Prueba: documento inválido.
     * Resultado esperado: IllegalArgumentException.
     */
    @Test
    @Order(8)
    public void testFindByDocumentoInvalid(){
        assertThrows(IllegalArgumentException.class, () -> cut.findByDocumento(null,0,10));
        assertThrows(IllegalArgumentException.class, () -> cut.findByDocumento(" ",0,10));
    }

    /**
     * Prueba: estado nulo.
     * Resultado esperado: IllegalArgumentException.
     */
    @Test
    @Order(9)
    public void testFindByEstadoInvalid(){
        assertThrows(IllegalArgumentException.class, () -> cut.findByEstado(null,0,10));
    }

    /**
     * Prueba: paginación inválida en todos los métodos.
     * Resultado esperado: IllegalArgumentException.
     */
    @Test
    @Order(10)
    public void testInvalidPagination(){

        assertThrows(IllegalArgumentException.class, () -> cut.findByNombre("JUAN",-1,10));
        assertThrows(IllegalArgumentException.class, () -> cut.findByNombre("JUAN",0,0));
        assertThrows(IllegalArgumentException.class, () -> cut.findByNombre("JUAN",0,-1));

        assertThrows(IllegalArgumentException.class, () -> cut.findActivos(-1,10));
        assertThrows(IllegalArgumentException.class, () -> cut.findActivos(0,0));
        assertThrows(IllegalArgumentException.class, () -> cut.findActivos(0,-1));

        assertThrows(IllegalArgumentException.class, () -> cut.findByDocumento("123",-1,10));
        assertThrows(IllegalArgumentException.class, () -> cut.findByDocumento("123",0,0));

        assertThrows(IllegalArgumentException.class, () -> cut.findByEstado(true,-1,10));
        assertThrows(IllegalArgumentException.class, () -> cut.findByEstado(true,0,0));
    }

    /**
     * Prueba: nombre inválido en conteo.
     * Resultado esperado: IllegalArgumentException.
     */
    @Test
    @Order(11)
    public void testCountByNombreInvalid(){
        assertThrows(IllegalArgumentException.class, () -> cut.countByNombre(null));
        assertThrows(IllegalArgumentException.class, () -> cut.countByNombre(" "));
    }

    /**
     * Prueba: manejo de errores internos.
     * Resultado esperado: IllegalStateException.
     */
    @Test
    @Order(12)
    public void testExceptionHandling(){

        em.close();

        assertThrows(IllegalStateException.class, () -> cut.findByNombre("JUAN",0,10));
        assertThrows(IllegalStateException.class, () -> cut.findActivos(0,10));
        assertThrows(IllegalStateException.class, () -> cut.findByDocumento("123",0,10));
        assertThrows(IllegalStateException.class, () -> cut.findByEstado(true,0,10));
        assertThrows(IllegalStateException.class, () -> cut.countByNombre("JUAN"));
    }
}