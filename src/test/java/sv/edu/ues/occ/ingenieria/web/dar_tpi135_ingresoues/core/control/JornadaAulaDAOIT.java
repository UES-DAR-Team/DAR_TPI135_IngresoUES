package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAula;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class JornadaAulaDAOIT extends BaseIntegrationAbstract {

    private EntityManager em;
    private JornadaAulaDAO cut;

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new JornadaAulaDAO();
        cut.em = em;
    }

    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    /**
     * Prueba: búsqueda por jornada.
     * Propósito: verificar que findByJornada retorna resultados válidos.
     * Resultado esperado: lista no nula.
     */
    @Order(1)
    @Test
    public void testFindByJornada(){
        UUID id = UUID.randomUUID();

        List<JornadaAula> resultado = cut.findByJornada(id, 0, 10);

        assertNotNull(resultado);
    }

    /**
     * Prueba: búsqueda por aula.
     * Propósito: verificar que findByAula retorna resultados válidos.
     * Resultado esperado: lista no nula.
     */
    @Order(2)
    @Test
    public void testFindByAula(){
        UUID id = UUID.randomUUID();

        List<JornadaAula> resultado = cut.findByAula(id, 0, 10);

        assertNotNull(resultado);
    }

    /**
     * Prueba: conteo por jornada.
     * Propósito: verificar que countByJornada retorna un valor válido.
     * Resultado esperado: valor mayor o igual a cero.
     */
    @Order(3)
    @Test
    public void testCountByJornada(){
        UUID id = UUID.randomUUID();

        Long total = cut.countByJornada(id);

        assertNotNull(total);
        assertTrue(total >= 0);
    }

    /**
     * Prueba: conteo por aula.
     * Propósito: verificar que countByAula retorna un valor válido.
     * Resultado esperado: valor mayor o igual a cero.
     */
    @Order(4)
    @Test
    public void testCountByAula(){
        UUID id = UUID.randomUUID();

        Long total = cut.countByAula(id);

        assertNotNull(total);
        assertTrue(total >= 0);
    }

    /**
     * Prueba: validación de id jornada nulo.
     * Resultado esperado: IllegalArgumentException.
     */
    @Order(5)
    @Test
    public void testFindByJornadaNull(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByJornada(null,0,10);
        });
    }

    /**
     * Prueba: validación de id aula nulo.
     * Resultado esperado: IllegalArgumentException.
     */
    @Order(6)
    @Test
    public void testFindByAulaNull(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByAula(null,0,10);
        });
    }

    /**
     * Prueba: validación de paginación inválida.
     * Resultado esperado: IllegalArgumentException.
     */
    @Order(7)
    @Test
    public void testInvalidPagination(){
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findByJornada(UUID.randomUUID(),-1,10);
        });
    }

    /**
     * Prueba: manejo de errores en findByJornada.
     * Propósito: verificar que retorna lista vacía.
     */
    @Order(8)
    @Test
    public void testFindByJornadaException(){
        em.close();

        List<JornadaAula> resultado = cut.findByJornada(UUID.randomUUID(),0,10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    /**
     * Prueba: manejo de errores en findByAula.
     */
    @Order(9)
    @Test
    public void testFindByAulaException(){
        em.close();

        List<JornadaAula> resultado = cut.findByAula(UUID.randomUUID(),0,10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    /**
     * Prueba: manejo de errores en countByJornada.
     * Resultado esperado: 0L.
     */
    @Order(10)
    @Test
    public void testCountByJornadaException(){
        em.close();

        Long total = cut.countByJornada(UUID.randomUUID());

        assertEquals(0L, total);
    }

    /**
     * Prueba: manejo de errores en countByAula.
     * Resultado esperado: 0L.
     */
    @Order(11)
    @Test
    public void testCountByAulaException(){
        em.close();

        Long total = cut.countByAula(UUID.randomUUID());

        assertEquals(0L, total);
    }
}