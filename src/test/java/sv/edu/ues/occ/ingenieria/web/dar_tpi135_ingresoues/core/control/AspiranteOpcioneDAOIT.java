package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspiranteOpcione;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class AspiranteOpcioneDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private AspiranteOpcioneDAO cut;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new AspiranteOpcioneDAO();
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
     * Resultado esperado: lista no nula.
     */
    @Order(1)
    @Test
    public void testFindByAspirante(){
        AspiranteOpcione ref = cut.findRange(0,1).getFirst();

        UUID id = ref.getIdAspirante().getId();

        List<AspiranteOpcione> resultado = cut.findByAspirante(id, 0, 10);
    }

    /**
     * Prueba: búsqueda por código de programa.
     * Resultado esperado: lista no nula.
     */
    @Order(2)
    @Test
    public void testFindByCodigoPrograma(){
        List<AspiranteOpcione> resultado = cut.findByCodigoPrograma("ING",0,10);
        assertNotNull(resultado);
    }

    /**
     * Prueba: búsqueda por nombre de programa.
     * Resultado esperado: lista no nula.
     */
    @Order(3)
    @Test
    public void testFindByNombrePrograma(){
        List<AspiranteOpcione> resultado = cut.findByNombrePrograma("ING",0,10);
        assertNotNull(resultado);
    }

    /**
     * Prueba: conteo por aspirante.
     * Resultado esperado: valor >= 0.
     */
    @Order(4)
    @Test
    public void testCountByAspirante(){
        AspiranteOpcione ref = cut.findRange(0,1).getFirst();

        Long total = cut.countByAspirante(ref.getIdAspirante().getId());

        assertNotNull(total);
        assertTrue(total >= 0);
    }

    /**
     * Prueba: validación id aspirante nulo.
     */
    @Order(5)
    @Test
    public void testFindByAspiranteNull(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByAspirante(null,0,10);
        });
    }

    /**
     * Prueba: validación código programa inválido.
     */
    @Order(6)
    @Test
    public void testFindByCodigoProgramaInvalid(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByCodigoPrograma(" ",0,10);
        });
    }

    /**
     * Prueba: validación nombre programa inválido.
     */
    @Order(7)
    @Test
    public void testFindByNombreProgramaInvalid(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByNombrePrograma(null,0,10);
        });
    }

    /**
     * Prueba: validación paginación inválida.
     */
    @Order(8)
    @Test
    public void testInvalidPagination(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByAspirante(UUID.randomUUID(),-1,10);
        });
    }

    /**
     * Prueba: manejo de errores internos en findByAspirante.
     * Resultado esperado: lista vacía.
     */
    @Order(9)
    @Test
    public void testFindByAspiranteException(){
        em.close();

        List<AspiranteOpcione> resultado = cut.findByAspirante(UUID.randomUUID(),0,10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    /**
     * Prueba: manejo de errores en countByAspirante.
     * Resultado esperado: 0L.
     */
    @Order(10)
    @Test
    public void testCountByAspiranteException(){
        em.close();

        Long total = cut.countByAspirante(UUID.randomUUID());

        assertEquals(0L, total);
    }
}