package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class JornadaDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private JornadaDAO cut;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new JornadaDAO();
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
     * Propósito: verificar que el metodo count() devuelve un valor mayor que cero.
     * Precondiciones: la base de datos contiene registros de Jornada.
     * Resultado esperado: count() > 0.
     */
    @Order(1)
    @Test
    public void testCount(){
        int total = cut.count();
        assertTrue(total > 0);
    }

    /**
     * Prueba: búsqueda por nombre.
     * Propósito: verificar que findByNombre retorna resultados válidos.
     * Precondiciones: existe al menos una jornada registrada.
     * Resultado esperado: lista no nula y no vacía.
     */
    @Order(2)
    @Test
    public void testFindByNombre(){
        Jornada ref = cut.findRange(0,1).getFirst();

        List<Jornada> resultado = cut.findByNombre(
                ref.getNombreJornada(), 0, 10
        );

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    /**
     * Prueba: búsqueda por estado activo.
     * Propósito: verificar que findByActivo retorna resultados válidos.
     * Precondiciones: existe al menos una jornada con estado definido.
     * Resultado esperado: lista no nula y no vacía.
     */
    @Order(3)
    @Test
    public void testFindByActivo(){
        Jornada ref = cut.findRange(0,1).getFirst();

        List<Jornada> resultado = cut.findByActivo(
                ref.getActivo(), 0, 10
        );

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    /**
     * Prueba: conteo por nombre.
     * Propósito: verificar que countByNombre retorna un valor mayor que cero.
     * Precondiciones: existen jornadas con nombres coincidentes.
     * Resultado esperado: valor mayor que cero.
     */
    @Order(4)
    @Test
    public void testCountByNombre(){
        Jornada ref = cut.findRange(0,1).getFirst();

        Long total = cut.countByNombre(ref.getNombreJornada());

        assertNotNull(total);
        assertTrue(total > 0);
    }

    /**
     * Prueba: conteo por estado.
     * Propósito: verificar que countByActivo retorna un valor válido.
     * Resultado esperado: valor mayor o igual a cero.
     */
    @Order(5)
    @Test
    public void testCountByActivo(){
        Jornada ref = cut.findRange(0,1).getFirst();

        Long total = cut.countByActivo(ref.getActivo());

        assertNotNull(total);
        assertTrue(total >= 0);
    }

    /**
     * Prueba: validación de nombre inválido.
     * Propósito: verificar que se lanza IllegalArgumentException.
     */
    @Order(6)
    @Test
    public void testFindByNombreInvalid(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByNombre(" ",0,10);
        });
    }

    /**
     * Prueba: validación de estado nulo.
     * Propósito: verificar que se lanza IllegalArgumentException.
     */
    @Order(7)
    @Test
    public void testFindByActivoInvalid(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByActivo(null,0,10);
        });
    }

    /**
     * Prueba: validación de paginación inválida.
     * Propósito: verificar que se lanza IllegalArgumentException.
     */
    @Order(8)
    @Test
    public void testInvalidPagination(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByNombre("TEST",-1,10);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByActivo(true,0,0);
        });
    }

    /**
     * Prueba: manejo de excepciones internas en findByNombre.
     * Propósito: verificar que se lanza IllegalStateException.
     */
    @Order(9)
    @Test
    public void testFindByNombreException(){
        em.close();

        assertThrows(IllegalStateException.class, () -> {
            cut.findByNombre("TEST",0,10);
        });
    }

    /**
     * Prueba: manejo de excepciones en findByActivo.
     */
    @Order(10)
    @Test
    public void testFindByActivoException(){
        em.close();

        assertThrows(IllegalStateException.class, () -> {
            cut.findByActivo(true,0,10);
        });
    }

    /**
     * Prueba: manejo de excepciones en countByNombre.
     */
    @Order(11)
    @Test
    public void testCountByNombreException(){
        em.close();

        assertThrows(IllegalStateException.class, () -> {
            cut.countByNombre("TEST");
        });
    }

    /**
     * Prueba: manejo de excepciones en countByActivo.
     */
    @Order(12)
    @Test
    public void testCountByActivoException(){
        em.close();

        assertThrows(IllegalStateException.class, () -> {
            cut.countByActivo(true);
        });
    }

    @Test
    @Order(13)
    public void testFindByNombreInvalidCompleto(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByNombre(null,0,10);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByNombre(" ",0,10);
        });
    }

    @Test
    @Order(14)
    public void testInvalidPaginationCompleto(){

        // findByNombre
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByNombre("TEST",0,-1);
        });

        // findByActivo
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByActivo(true,-1,10);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByActivo(true,0,-1);
        });
    }

    @Test
    @Order(15)
    public void testCountByNombreInvalid(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.countByNombre(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            cut.countByNombre(" ");
        });
    }

    @Test
    @Order(16)
    public void testCountByActivoInvalid(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.countByActivo(null);
        });
    }
}