package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspiranteOpcione;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.time.OffsetDateTime;
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

    // -------------------------------------------------------------------------
    // HAPPY PATH
    // -------------------------------------------------------------------------

    /**
     * Búsqueda por aspirante válida.
     * Resultado esperado: lista no nula.
     */
    @Test
    @Order(1)
    public void testFindByAspirante() {
        AspiranteOpcione ref = cut.findRange(0, 1).getFirst();
        UUID id = ref.getIdAspirante().getId();

        List<AspiranteOpcione> resultado = cut.findByAspirante(id, 0, 10);

        assertNotNull(resultado);
    }

    /**
     * Búsqueda por código de programa válida.
     * Resultado esperado: lista no nula.
     */
    @Test
    @Order(2)
    public void testFindByCodigoPrograma() {
        List<AspiranteOpcione> resultado = cut.findByCodigoPrograma("ING", 0, 10);
        assertNotNull(resultado);
    }

    /**
     * Búsqueda por nombre de programa válida.
     * Resultado esperado: lista no nula.
     */
    @Test
    @Order(3)
    public void testFindByNombrePrograma() {
        List<AspiranteOpcione> resultado = cut.findByNombrePrograma("ING", 0, 10);
        assertNotNull(resultado);
    }

    /**
     * Conteo por aspirante válido.
     * Resultado esperado: valor no nulo >= 0.
     */
    @Test
    @Order(4)
    public void testCountByAspirante() {
        AspiranteOpcione ref = cut.findRange(0, 1).getFirst();

        Long total = cut.countByAspirante(ref.getIdAspirante().getId());

        assertNotNull(total);
        assertTrue(total >= 0);
    }

    /**
     * existePreferencia con combinación que SÍ existe en la BD.
     * Resultado esperado: true.
     */
    @Test
    @Order(5)
    public void testExistePreferencia_True() {
        AspiranteOpcione ref = cut.findRange(0, 1).getFirst();
        UUID idAspirante = ref.getIdAspirante().getId();
        Short preferencia = ref.getPreferencia();

        boolean resultado = cut.existePreferencia(idAspirante, preferencia);

        assertTrue(resultado);
    }

    /**
     * existePreferencia con preferencia que NO existe para ese aspirante.
     * Resultado esperado: false.
     */
    @Test
    @Order(6)
    public void testExistePreferencia_False() {
        AspiranteOpcione ref = cut.findRange(0, 1).getFirst();
        UUID idAspirante = ref.getIdAspirante().getId();

        boolean resultado = cut.existePreferencia(idAspirante, (short) 999);

        assertFalse(resultado);
    }

    /**
     * Crear una nueva opción de carrera válida.
     * Resultado esperado: id asignado automáticamente.
     */
    @Test
    @Order(7)
    public void testCreate() {
        AspiranteOpcione ref = cut.findRange(0, 1).getFirst();
        Aspirante aspirante = em.merge(ref.getIdAspirante());

        AspiranteOpcione nueva = new AspiranteOpcione();
        nueva.setIdAspirante(aspirante);
        nueva.setCodigoPrograma("TEST01");
        nueva.setNombrePrograma("Programa de Prueba");
        nueva.setFechaSeleccion(OffsetDateTime.now());
        nueva.setPreferencia((short) 98);

        em.getTransaction().begin();
        cut.create(nueva);
        em.getTransaction().commit();

        assertNotNull(nueva.getId());
    }

    // -------------------------------------------------------------------------
    // VALIDACIONES DE PARÁMETROS INVÁLIDOS
    // -------------------------------------------------------------------------

    /**
     * findByAspirante con id null.
     * Resultado esperado: IllegalArgumentException.
     */
    @Test
    @Order(8)
    public void testFindByAspiranteNull() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByAspirante(null, 0, 10));
    }

    /**
     * findByCodigoPrograma con parámetros inválidos.
     * Resultado esperado: IllegalArgumentException.
     */
    @Test
    @Order(9)
    public void testFindByCodigoProgramaInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByCodigoPrograma(null, 0, 10));
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByCodigoPrograma(" ", 0, 10));
    }

    /**
     * findByNombrePrograma con parámetros inválidos.
     * Resultado esperado: IllegalArgumentException.
     */
    @Test
    @Order(10)
    public void testFindByNombreProgramaInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByNombrePrograma(null, 0, 10));
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByNombrePrograma(" ", 0, 10));
    }

    /**
     * Paginación inválida en todos los métodos.
     * Resultado esperado: IllegalArgumentException.
     */
    @Test
    @Order(11)
    public void testInvalidPagination() {
        UUID id = UUID.randomUUID();

        // findByAspirante
        assertThrows(IllegalArgumentException.class, () -> cut.findByAspirante(id, -1, 10));
        assertThrows(IllegalArgumentException.class, () -> cut.findByAspirante(id, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> cut.findByAspirante(id, 0, -1));

        // findByCodigoPrograma
        assertThrows(IllegalArgumentException.class, () -> cut.findByCodigoPrograma("ING", -1, 10));
        assertThrows(IllegalArgumentException.class, () -> cut.findByCodigoPrograma("ING", 0, 0));
        assertThrows(IllegalArgumentException.class, () -> cut.findByCodigoPrograma("ING", 0, -1));

        // findByNombrePrograma
        assertThrows(IllegalArgumentException.class, () -> cut.findByNombrePrograma("ING", -1, 10));
        assertThrows(IllegalArgumentException.class, () -> cut.findByNombrePrograma("ING", 0, 0));
        assertThrows(IllegalArgumentException.class, () -> cut.findByNombrePrograma("ING", 0, -1));
    }

    /**
     * countByAspirante con id null.
     * Resultado esperado: IllegalArgumentException.
     */
    @Test
    @Order(12)
    public void testCountByAspiranteInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.countByAspirante(null));
    }

    /**
     * existePreferencia con parámetros null.
     * Resultado esperado: IllegalArgumentException en ambos casos.
     */
    @Test
    @Order(13)
    public void testExistePreferencia_NullParams() {
        assertThrows(IllegalArgumentException.class,
                () -> cut.existePreferencia(null, (short) 1));
        assertThrows(IllegalArgumentException.class,
                () -> cut.existePreferencia(UUID.randomUUID(), null));
    }

    // -------------------------------------------------------------------------
    // MANEJO DE EXCEPCIONES (em cerrado)
    // -------------------------------------------------------------------------

    /**
     * Manejo de error en consultas de lista con em cerrado.
     * Resultado esperado: lista vacía (no excepción propagada).
     */
    @Test
    @Order(14)
    public void testExceptionHandlingList() {
        em.close();

        assertTrue(cut.findByAspirante(UUID.randomUUID(), 0, 10).isEmpty());
        assertTrue(cut.findByCodigoPrograma("ING", 0, 10).isEmpty());
        assertTrue(cut.findByNombrePrograma("ING", 0, 10).isEmpty());
    }

    /**
     * Manejo de error en conteo con em cerrado.
     * Resultado esperado: 0L (no excepción propagada).
     */
    @Test
    @Order(15)
    public void testCountByAspiranteException() {
        em.close();

        Long total = cut.countByAspirante(UUID.randomUUID());

        assertEquals(0L, total);
    }

    /**
     * Manejo de error en existePreferencia con em cerrado.
     * Resultado esperado: false (no excepción propagada).
     */
    @Test
    @Order(16)
    public void testExistePreferencia_Exception() {
        em.close();

        boolean resultado = cut.existePreferencia(UUID.randomUUID(), (short) 1);

        assertFalse(resultado);
    }
}