package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspirantePrueba;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class AspirantePruebaDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private AspirantePruebaDAO cut;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new AspirantePruebaDAO();
        cut.em = em;
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    /**
     * Prueba: búsqueda por aspirante.
     * Propósito: verificar que findByAspirante retorna resultados válidos.
     * Precondiciones: existe al menos un registro en la base.
     * Resultado esperado: lista no nula.
     */
    @Order(1)
    @Test
    public void testFindByAspirante(){
        AspirantePrueba ref = cut.findRange(0,1).getFirst();

        UUID id = ref.getIdAspirante().getId();

        List<AspirantePrueba> resultado = cut.findByAspirante(id, 0, 10);

        assertNotNull(resultado);
    }

    /**
     * Prueba: búsqueda por prueba.
     * Propósito: verificar que findByPrueba retorna resultados válidos.
     * Resultado esperado: lista no nula.
     */
    @Order(2)
    @Test
    public void testFindByPrueba(){
        AspirantePrueba ref = cut.findRange(0,1).getFirst();

        UUID id = ref.getIdPrueba().getId();

        List<AspirantePrueba> resultado = cut.findByPrueba(id, 0, 10);
    }

    /**
     * Prueba: conteo por aspirante.
     * Propósito: verificar que countByAspirante retorna un valor válido.
     * Resultado esperado: valor mayor o igual a cero.
     */
    @Order(3)
    @Test
    public void testCountByAspirante(){
        AspirantePrueba ref = cut.findRange(0,1).getFirst();

        UUID id = ref.getIdAspirante().getId();

        Long total = cut.countByAspirante(id);

        assertNotNull(total);
        assertTrue(total >= 0);
    }

    /**
     * Prueba: validación de id aspirante nulo.
     * Resultado esperado: IllegalArgumentException.
     */
    @Order(4)
    @Test
    public void testFindByAspiranteNull(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByAspirante(null,0,10);
        });
    }

    /**
     * Prueba: validación de id prueba nulo.
     * Resultado esperado: IllegalArgumentException.
     */
    @Order(5)
    @Test
    public void testFindByPruebaNull(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByPrueba(null,0,10);
        });
    }

    /**
     * Prueba: validación de paginación inválida.
     * Resultado esperado: IllegalArgumentException.
     */
    @Order(6)
    @Test
    public void testInvalidPagination(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByAspirante(UUID.randomUUID(),-1,10);
        });
    }

    /**
     * Prueba: manejo de excepciones internas en findByAspirante.
     * Propósito: verificar que se lanza IllegalStateException.
     */
    @Order(7)
    @Test
    public void testFindByAspiranteException(){
        em.close();

        assertThrows(IllegalStateException.class, () -> {
            cut.findByAspirante(UUID.randomUUID(),0,10);
        });
    }

    /**
     * Prueba: manejo de excepciones en findByPrueba.
     */
    @Order(8)
    @Test
    public void testFindByPruebaException(){
        em.close();

        assertThrows(IllegalStateException.class, () -> {
            cut.findByPrueba(UUID.randomUUID(), 0, 10);
        });
    }

    /**
     * Prueba: manejo de excepciones en countByAspirante.
     */
    @Order(9)
    @Test
    public void testCountByAspiranteException(){
        em.close();

        assertThrows(IllegalStateException.class, () -> {
            cut.countByAspirante(UUID.randomUUID());
        });
    }
}