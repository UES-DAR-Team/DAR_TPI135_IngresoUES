package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
public class AreaConocimientoDAOIT extends BaseIntegrationAbstract {

    //ARRANGE general para todas las pruebas de esta clase
    // Campos reutilizados para evitar variables locales redundantes
    private EntityManager em;
    private AreaConocimientoDAO cut;

    //asignamos el entity manager y el cut antes de cada prueba
    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        cut = new AreaConocimientoDAO();
        cut.em = em;
    }

    //cerramos el entity manager despues de cada prueba para liberar recursos
    @AfterEach
    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
    //fin del Arrange general xd

    //probamos el crud general de una sola vez en esta clase, creo que ya con esto lo toma la comvertura
    // por ende ya no va a ser necesario probar el crud en cada una de las demas clases

    /**
     * Prueba: conteo de registros existentes.
     * Propósito: verificar que el metodo count() devuelve un número mayor que cero
     *            usando la base de datos cargada por Testcontainers.
     * Precondiciones: el contenedor de PostgreSQL está levantado y la BD tiene datos de ejemplo.
     * Resultado esperado: count() > 0.
     */
    @Order(1)
    @Test
    public void testCount(){
        //act
        int total = cut.count();

        //assert
        assertTrue(total > 0);
    }

    /**
     * Prueba: creación de una nueva entidad AreaConocimiento.
     * Propósito: comprobar que create() persiste una nueva fila y que el conteo aumenta en 1.
     * Precondiciones: existe al menos una área de referencia (usada como idAutoReferenciaArea).
     * Acciones: crea una entidad con valores válidos, inicia transacción, persiste y commitea.
     * Resultado esperado: confirmacion del id no nulo y el conteo después de crear aumenta en 1.
     */
    @Order(2)
    @Test
    public void testCreate(){
        //Arrange local
        int regBeforCreate = cut.count();
        AreaConocimiento nuevo = new AreaConocimiento();

        cut.em.getTransaction().begin();
        nuevo.setId(UUID.randomUUID());
        nuevo.setNombre("Prueba 1");
        nuevo.setActivo(true);
        nuevo.setFechaCreacion(OffsetDateTime.now());
        nuevo.setIdAutoReferenciaArea(cut.findRange(0,1).getFirst());

        //act
        cut.create(nuevo);
        cut.em.getTransaction().commit();

        //assert
        assertNotNull(nuevo.getId());
        int resultado = cut.count();
        assertEquals((regBeforCreate+1), resultado);
    }

    /**
     * Prueba: actualización de una entidad AreaConocimiento existente.
     * Propósito: asegurar que update() modifica correctamente los campos en BD.
     * Precondiciones: se crea un registro de prueba sobre el que operar.
     * Acciones: crear registro, recuperar por id, modificar el nombre y llamar a update().
     * Resultado esperado: el nombre se actualiza correctamente.
     */
    @Order(3)
    @Test
    public void testUpdate(){

        //Arrange local ---> creamos un registro para prueba sobre el que vamos a actualizar
        cut.em.getTransaction().begin();
        AreaConocimiento nuevoUD = new AreaConocimiento();
        nuevoUD.setId(UUID.randomUUID());
        nuevoUD.setNombre("Prueba Update");
        nuevoUD.setActivo(true);
        nuevoUD.setFechaCreacion(OffsetDateTime.now());
        nuevoUD.setIdAutoReferenciaArea(cut.findRange(0,1).getFirst());
        cut.create(nuevoUD);
        cut.em.getTransaction().commit();
        //toma el registro creado y actuliza su nombre
        AreaConocimiento actualizacion =cut.findById(nuevoUD.getId());
        actualizacion.setNombre("Prueba Actualizada");

        //act
        cut.em.getTransaction().begin();
        cut.update(actualizacion);
        cut.em.getTransaction().commit();

        //assert
        assertEquals("Prueba Actualizada", actualizacion.getNombre());
    }

    /**
     * Prueba: eliminación de una entidad AreaConocimiento.
     * Propósito: verificar que delete() remueve el registro y que el conteo vuelve al valor previo.
     * Precondiciones: se crea un registro que será eliminado en la misma transacción de prueba.
     * Acciones: crear registro, comprobar aumento de conteo, eliminar el registro y comprobar conteo final.
     * Resultado esperado: conteo final == conteo inicial antes de crear el registro de prueba.
     */
    @Order(4)
    @Test
    public void testDelete(){
        //Arrange local
        //la misma logica que create pero en vez de hacer algo con el contenido obtenido del registro creado
        //simplemente eliminamos el registro
        cut.em.getTransaction().begin();
        int regBeforeCreateToDelete = cut.count();

        //creamos un registro sobre el que vamos a eliminar
        AreaConocimiento nuevo = new AreaConocimiento();
        nuevo.setId(UUID.randomUUID());
        nuevo.setNombre("Eliminar esta prueba");
        nuevo.setActivo(true);
        nuevo.setFechaCreacion(OffsetDateTime.now());
        nuevo.setIdAutoReferenciaArea(cut.findRange(0,1).getFirst());
        cut.create(nuevo);
        cut.em.getTransaction().commit();

        int registros = cut.count();

        //toma el registro creado y lo elimina
        AreaConocimiento eliminacion =cut.findById(nuevo.getId());
        Assertions.assertNotNull(eliminacion);

        cut.em.getTransaction().begin();
        cut.delete(eliminacion);
        cut.em.getTransaction().commit();

        //confirmamos que el registro de prueba se creo
        assertEquals((regBeforeCreateToDelete+1), registros);
        //volvemos a contar despues de haber eliminado
        int regAfterDelete = cut.count();
        //compara el conteo de registros antes de crear la prueba para eliminar
        //con el conteo de despues de eliminar el registro  de prueba
        assertEquals(regBeforeCreateToDelete, regAfterDelete);
    }

    /**
     * Prueba: consulta por rango (paginación básica).
     * Propósito: asegurar que findRange devuelve una lista no nula y no vacía para límites razonables.
     * Precondiciones: la BD contiene suficientes registros de AreaConocimiento.
     * Resultado esperado: lista no nula y no vacía.
     */
    @Order(5)
    @Test
    public void testFindRange(){
        //act ---> lista de registros obtenida con el rango del primero al undecimo
        List<AreaConocimiento> registroRango = cut.findRange(0, 10);

        //assert
        assertNotNull(registroRango);
        assertFalse(registroRango.isEmpty());
    }

    /**
     * Prueba: obtención de un registro por id a través de findRange().
     * Propósito: comprobar que se puede buscar un registro por id.
     * Precondiciones: la consulta findRange(0,1) devuelve al menos un elemento para utilizacion de su id.
     * Resultado esperado: el elemento devuelto de la funcion findById no es nulo.
     */
    @Order(6)
    @Test
    public void testFindById(){
        //Arrange local ---> obtenemos el id del primer registro de la consulta findRange(0,1) para usarlo en findById
        AreaConocimiento reg = cut.findRange(0,1).getFirst();
        UUID id = reg.getId();

        //act
        AreaConocimiento registro = cut.findById(id);

        //assert
        assertNotNull(registro);
    }
    //fin de las pruebas del crud general


    //probamos los metodos especificos
    /**
     * Prueba: búsqueda por nombre con patrón (LIKE).
     * Propósito: verificar que findByNameLike devuelve coincidencias cuando se proporciona un término válido.
     * Precondiciones: existen áreas cuyo nombre contiene "Matemáticas" (caso insensible en la consulta).
     * Resultado esperado: lista no nula y no vacía.
     */
    @Order(7)
    @Test
    public void testFindByNameLike(){
        //act
        List<AreaConocimiento> registro = cut.findByNameLike("Matemáticas", 0, 10);

        //assert
        assertNotNull(registro);
        assertFalse(registro.isEmpty());
    }

    /**
     * Prueba: obtención de áreas que son padres (raíz) mediante findByAreaPadre().
     * Propósito: comprobar que la consulta que retorna áreas padre funciona y devuelve resultados.
     * Precondiciones: existen áreas catalogadas como padre en la BD de prueba.
     * Resultado esperado: lista no nula y no vacía.
     */
    @Order(8)
    @Test
    public void testFindByAreaPadre(){
        //act
        List<AreaConocimiento> registro = cut.findByAreaPadre();

        //assert
        assertNotNull(registro);
        assertFalse(registro.isEmpty());
    }

    /**
     * Prueba: obtención de hijos dada un área padre específica.
     * Propósito: asegurar que findHijosByPadre(UUID) retorna las áreas hijas para un id válido.
     * Precondiciones: el UUID especificado tiene hijos en la BD de prueba.
     * Resultado esperado: lista no nula y no vacía.
     */
    @Order(9)
    @Test
    public void testFindHijosByPadre(){
        //Arrange local ---> obtenemos el id del tercer registro de la consulta findRange(0,2) para usarlo en findById
        //ya que conocemos que el tiene hijos
        AreaConocimiento reg = cut.findRange(0,3).getLast();
        UUID id = reg.getId();

        //act
        List<AreaConocimiento> registro = cut.findHijosByPadre(id);

        //assert
        assertNotNull(registro);
        assertFalse(registro.isEmpty());
    }

}