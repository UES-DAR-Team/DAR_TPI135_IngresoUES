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
     * Prueba: búsqueda por aspirante válida.
     * Resultado esperado: lista no nula.
     */
    @Test
    @Order(1)
    public void testFindByAspirante(){
        AspiranteOpcione ref = cut.findRange(0,1).getFirst();
        UUID id = ref.getIdAspirante().getId();

        List<AspiranteOpcione> resultado = cut.findByAspirante(id, 0, 10);

        assertNotNull(resultado);
    }

    /**
     * Prueba: búsqueda por código de programa válida.
     * Resultado esperado: lista no nula.
     */
    @Test
    @Order(2)
    public void testFindByCodigoPrograma(){
        List<AspiranteOpcione> resultado = cut.findByCodigoPrograma("ING",0,10);
        assertNotNull(resultado);
    }

    /**
     * Prueba: búsqueda por nombre de programa válida.
     * Resultado esperado: lista no nula.
     */
    @Test
    @Order(3)
    public void testFindByNombrePrograma(){
        List<AspiranteOpcione> resultado = cut.findByNombrePrograma("ING",0,10);
        assertNotNull(resultado);
    }

    /**
     * Prueba: conteo por aspirante válido.
     * Resultado esperado: >= 0.
     */
    @Test
    @Order(4)
    public void testCountByAspirante(){
        AspiranteOpcione ref = cut.findRange(0,1).getFirst();

        Long total = cut.countByAspirante(ref.getIdAspirante().getId());

        assertNotNull(total);
        assertTrue(total >= 0);
    }

    /**
     * Prueba: id aspirante nulo.
     * Resultado esperado: IllegalArgumentException.
     */
    @Test
    @Order(5)
    public void testFindByAspiranteNull(){
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByAspirante(null,0,10));
    }

    /**
     * Prueba: código programa inválido.
     */
    @Test
    @Order(6)
    public void testFindByCodigoProgramaInvalid(){
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByCodigoPrograma(null,0,10));

        assertThrows(IllegalArgumentException.class,
                () -> cut.findByCodigoPrograma(" ",0,10));
    }

    /**
     * Prueba: nombre programa inválido.
     */
    @Test
    @Order(7)
    public void testFindByNombreProgramaInvalid(){
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByNombrePrograma(null,0,10));

        assertThrows(IllegalArgumentException.class,
                () -> cut.findByNombrePrograma(" ",0,10));
    }

    /**
     * Prueba: paginación inválida en todos los métodos.
     */
    @Test
    @Order(8)
    public void testInvalidPagination(){

        UUID id = UUID.randomUUID();

        // findByAspirante
        assertThrows(IllegalArgumentException.class, () -> cut.findByAspirante(id,-1,10));
        assertThrows(IllegalArgumentException.class, () -> cut.findByAspirante(id,0,0));
        assertThrows(IllegalArgumentException.class, () -> cut.findByAspirante(id,0,-1));

        // findByCodigoPrograma
        assertThrows(IllegalArgumentException.class, () -> cut.findByCodigoPrograma("ING",-1,10));
        assertThrows(IllegalArgumentException.class, () -> cut.findByCodigoPrograma("ING",0,0));

        // findByNombrePrograma
        assertThrows(IllegalArgumentException.class, () -> cut.findByNombrePrograma("ING",-1,10));
        assertThrows(IllegalArgumentException.class, () -> cut.findByNombrePrograma("ING",0,0));

        assertThrows(IllegalArgumentException.class,
                () -> cut.findByCodigoPrograma("ING",0,-1));

        assertThrows(IllegalArgumentException.class,
                () -> cut.findByNombrePrograma("ING",0,-1));
    }

    /**
     * Prueba: manejo de errores en consultas (lista vacía).
     */
    @Test
    @Order(9)
    public void testExceptionHandlingList(){

        em.close();

        assertTrue(cut.findByAspirante(UUID.randomUUID(),0,10).isEmpty());
        assertTrue(cut.findByCodigoPrograma("ING",0,10).isEmpty());
        assertTrue(cut.findByNombrePrograma("ING",0,10).isEmpty());
    }

    /**
     * Prueba: manejo de errores en conteo.
     * Resultado esperado: 0L.
     */
    @Test
    @Order(10)
    public void testCountByAspiranteException(){

        em.close();

        Long total = cut.countByAspirante(UUID.randomUUID());

        assertEquals(0L, total);
    }

    /**
     * Prueba: id aspirante nulo en conteo.
     * Resultado esperado: IllegalArgumentException.
     */
    @Test
    @Order(11)
    public void testCountByAspiranteInvalid(){
        assertThrows(IllegalArgumentException.class,
                () -> cut.countByAspirante(null));
    }
}