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


    private AspirantePrueba getSample() {
        List<AspirantePrueba> data = cut.findRange(0,1);
        assertFalse(data.isEmpty(), "Debe existir data en la BD");
        return data.get(0);
    }

    @Test
    @Order(1)
    public void testFindByAspirante(){
        AspirantePrueba ref = getSample();

        UUID id = ref.getIdAspirante().getId();

        List<AspirantePrueba> resultado = cut.findByAspirante(id, 0, 10);

        assertNotNull(resultado);
    }

    @Test
    @Order(2)
    public void testFindByPrueba(){
        AspirantePrueba ref = getSample();

        UUID id = ref.getIdPrueba().getId();

        List<AspirantePrueba> resultado = cut.findByPrueba(id, 0, 10);

        assertNotNull(resultado);
    }

    @Test
    @Order(3)
    public void testCountByAspirante(){
        AspirantePrueba ref = getSample();

        UUID id = ref.getIdAspirante().getId();

        Long total = cut.countByAspirante(id);

        assertNotNull(total);
        assertTrue(total >= 0);
    }

    @Test
    @Order(4)
    public void testFindByAspiranteNull(){
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByAspirante(null,0,10));
    }

    @Test
    @Order(5)
    public void testFindByPruebaNull(){
        assertThrows(IllegalArgumentException.class,
                () -> cut.findByPrueba(null,0,10));
    }

    @Test
    @Order(6)
    public void testInvalidPagination(){

        UUID id = UUID.randomUUID();

        // findByAspirante
        assertThrows(IllegalArgumentException.class, () -> cut.findByAspirante(id,-1,10));
        assertThrows(IllegalArgumentException.class, () -> cut.findByAspirante(id,0,0));
        assertThrows(IllegalArgumentException.class, () -> cut.findByAspirante(id,0,-1));

        // findByPrueba
        assertThrows(IllegalArgumentException.class, () -> cut.findByPrueba(id,-1,10));
        assertThrows(IllegalArgumentException.class, () -> cut.findByPrueba(id,0,0));
        assertThrows(IllegalArgumentException.class, () -> cut.findByPrueba(id,0,-1));
    }

    @Test
    @Order(7)
    public void testCountByAspiranteInvalid(){
        assertThrows(IllegalArgumentException.class,
                () -> cut.countByAspirante(null));
    }


    @Test
    @Order(8)
    public void testFindByAspiranteException(){
        em.close();

        assertThrows(IllegalStateException.class,
                () -> cut.findByAspirante(UUID.randomUUID(),0,10));
    }

    @Test
    @Order(9)
    public void testFindByPruebaException(){
        em.close();

        assertThrows(IllegalStateException.class,
                () -> cut.findByPrueba(UUID.randomUUID(), 0, 10));
    }

    @Test
    @Order(10)
    public void testCountByAspiranteException(){
        em.close();

        assertThrows(IllegalStateException.class,
                () -> cut.countByAspirante(UUID.randomUUID()));
    }

    @Test
    @Order(11)
    public void testGetEntityManagerOk() {
        EntityManager result = cut.getEntityManager();
        assertNotNull(result);
    }

    @Test
    @Order(12)
    public void testGetEntityManagerNull() {
        cut.em = null;

        assertThrows(IllegalStateException.class,
                () -> cut.getEntityManager());
    }
}