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
     * Prueba: conteo de registros existentes.
     * Propósito: verificar que el metodo count() devuelve un número mayor que cero.
     * Precondiciones: la base de datos contiene registros iniciales.
     * Resultado esperado: count() > 0.
     */
    @Order(1)
    @Test
    public void testCount(){
        int total = cut.count();
        assertTrue(total > 0);
    }

    /**
     * Prueba: búsqueda de aspirantes por nombre.
     * Propósito: verificar que findByNombre retorna resultados válidos.
     * Precondiciones: existe al menos un aspirante en la base.
     * Resultado esperado: lista no nula y no vacía.
     */
    @Order(2)
    @Test
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
     * Propósito: verificar que findActivos retorna resultados.
     * Precondiciones: existen aspirantes activos en la base.
     * Resultado esperado: lista no nula y no vacía.
     */
    @Order(3)
    @Test
    public void testFindActivos(){
        List<Aspirante> resultado = cut.findActivos(0,10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    /**
     * Prueba: búsqueda por documento.
     * Propósito: verificar que findByDocumento retorna resultados válidos.
     * Precondiciones: existe un aspirante con identificación registrada.
     * Resultado esperado: lista no nula y no vacía.
     */
    @Order(4)
    @Test
    public void testFindByDocumento(){
        Aspirante ref = cut.findRange(0,1).getFirst();

        List<Aspirante> resultado = cut.findByDocumento(
                ref.getIdentificacion(), 0, 10
        );

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    /**
     * Prueba: búsqueda por estado (activo).
     * Propósito: verificar que findByEstado retorna resultados válidos.
     * Precondiciones: existe al menos un aspirante con estado definido.
     * Resultado esperado: lista no nula y no vacía.
     */
    @Order(5)
    @Test
    public void testFindByEstado(){
        Aspirante ref = cut.findRange(0,1).getFirst();

        List<Aspirante> resultado = cut.findByEstado(
                ref.getActivo(), 0, 10
        );

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    /**
     * Prueba: conteo por nombre.
     * Propósito: verificar que countByNombre retorna un valor mayor que cero.
     * Precondiciones: existen registros con nombres coincidentes.
     * Resultado esperado: valor mayor que cero.
     */
    @Order(6)
    @Test
    public void testCountByNombre(){
        Aspirante ref = cut.findRange(0,1).getFirst();

        Long total = cut.countByNombre(ref.getNombreAspirante());

        assertNotNull(total);
        assertTrue(total > 0);
    }

    /**
     * Prueba: validación de nombre inválido.
     * Resultado esperado: IllegalArgumentException.
     */
    @Order(7)
    @Test
    public void testFindByNombreInvalid(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByNombre(" ",0,10);
        });
    }

    /**
     * Prueba: validación de documento inválido.
     * Resultado esperado: IllegalArgumentException.
     */
    @Order(8)
    @Test
    public void testFindByDocumentoInvalid(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByDocumento(null,0,10);
        });
    }

    /**
     * Prueba: validación de estado nulo.
     * Resultado esperado: IllegalArgumentException.
     */
    @Order(9)
    @Test
    public void testFindByEstadoInvalid(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByEstado(null,0,10);
        });
    }

    /**
     * Prueba: validación de paginación inválida.
     * Resultado esperado: IllegalArgumentException.
     */
    @Order(10)
    @Test
    public void testInvalidPagination(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByNombre("JUAN",-1,10);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            cut.findActivos(0,0);
        });
    }

    /**
     * Prueba: manejo de excepciones internas.
     * Propósito: verificar que se lanza IllegalStateException si ocurre un error interno.
     * Resultado esperado: IllegalStateException.
     */
    @Order(11)
    @Test
    public void testExceptionHandling(){
        em.close();

        assertThrows(IllegalStateException.class, () -> {
            cut.findByNombre("JUAN",0,10);
        });

        assertThrows(IllegalStateException.class, () -> {
            cut.findActivos(0,10);
        });

        assertThrows(IllegalStateException.class, () -> {
            cut.findByDocumento("123",0,10);
        });

        assertThrows(IllegalStateException.class, () -> {
            cut.findByEstado(true,0,10);
        });

        assertThrows(IllegalStateException.class, () -> {
            cut.countByNombre("JUAN");
        });
    }
}