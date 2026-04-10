package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
public class AreaConocimientoDAOIT {

    @Container
    //GenericContainer postgres =  new PostgreSQLContainer("postgres:17.5-alpine")
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17.5-alpine")
            .withDatabaseName("ingreso_ues_bd")
            .withInitScript("ingreso_ues_db.sql")
            .withUsername("postgres")
            .withPassword("postgresmy")
            .withExposedPorts(5432);

    @Order(1)
    @Test
    public void testCount(){
        System.out.println("count");
        assertTrue(postgres.isRunning());
        Integer puertoPostgresql =postgres.getMappedPort(5432);
        Map<String, Object> propiedades = new  HashMap<>();
        propiedades.put("jakarta.persistence.jdbc.url",String.format( "jdbc:postgresql://localhost:%d/ingreso_ues_bd", puertoPostgresql));

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("IngresoPUIT", propiedades);
        EntityManager em = emf.createEntityManager();
        AreaConocimientoDAO cut = new AreaConocimientoDAO();
        cut.em = em;

        int result = cut.count();

        assertTrue(result > 0);
        System.out.println("count: " + result);
    }

//crud completo y pruebas de los metodos de consulta personalizada

    @Order(2)
    @Test
    public void testCreate() {
        System.out.println("create");
        AreaConocimiento nuevo = new AreaConocimiento();
        nuevo.setId(UUID.randomUUID());
        nuevo.setNombre("Prueba 1");
        nuevo.setActivo(true);
        nuevo.setFechaCreacion(OffsetDateTime.now());

        assertTrue(postgres.isRunning());
        Integer puertoPostgresql = postgres.getMappedPort(5432);
        Map<String, Object> propiedades = new HashMap<>();
        propiedades.put("jakarta.persistence.jdbc.url", String.format("jdbc:postgresql://localhost:%d/ingreso_ues_bd", puertoPostgresql));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("IngresoPUIT", propiedades);
        EntityManager em = emf.createEntityManager();

        AreaConocimientoDAO cut = new AreaConocimientoDAO();
        cut.em = em;
        cut.em.getTransaction().begin();
        nuevo.setIdAutoReferenciaArea(cut.findRange(0,1).getFirst());
        cut.create(nuevo);
        cut.em.getTransaction().commit();
        int resultado = cut.count();
        assertEquals(6, resultado);
        System.out.println("Total registros: " + resultado);
    }

    //tengo que pasarle un id de lo que voy a actualizar
    @Order(3)
    @Test
    public void testUpdate() {
        System.out.println("update");
        assertTrue(postgres.isRunning());
        Integer puertoPostgresql = postgres.getMappedPort(5432);
        Map<String, Object> propiedades = new HashMap<>();
        propiedades.put("jakarta.persistence.jdbc.url", String.format("jdbc:postgresql://localhost:%d/ingreso_ues_bd", puertoPostgresql));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("IngresoPUIT", propiedades);
        EntityManager em = emf.createEntityManager();

        AreaConocimientoDAO cut = new AreaConocimientoDAO();
        cut.em = em;
        cut.em.getTransaction().begin();

        //creamos uno para prueba sobre el que vamos a actualizar
        AreaConocimiento nuevo = new AreaConocimiento();
        nuevo.setId(UUID.randomUUID());
        nuevo.setNombre("Prueba 1");
        nuevo.setActivo(true);
        nuevo.setFechaCreacion(OffsetDateTime.now());
        nuevo.setIdAutoReferenciaArea(cut.findRange(0,1).getFirst());
        cut.create(nuevo);
        cut.em.getTransaction().commit();


        //toma el registro creado y actuliza su nombre
        AreaConocimiento actualizacion =cut.findById(nuevo.getId());
        Assertions.assertNotNull(actualizacion);
        actualizacion.setNombre("Prueba 1 Actualizada");

        cut.em.getTransaction().begin();
        cut.update(actualizacion);
        cut.em.getTransaction().commit();

        Assertions.assertEquals("Prueba 1 Actualizada", actualizacion.getNombre());

        System.out.println("Actualizacion : " +  actualizacion.getNombre() );
    }

    //tengo que pasarle un id de lo que voy a actualizar
    @Order(4)
    @Test
    public void testDelete() {
        System.out.println("delete");
        assertTrue(postgres.isRunning());
        Integer puertoPostgresql = postgres.getMappedPort(5432);
        Map<String, Object> propiedades = new HashMap<>();
        propiedades.put("jakarta.persistence.jdbc.url", String.format("jdbc:postgresql://localhost:%d/ingreso_ues_bd", puertoPostgresql));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("IngresoPUIT", propiedades);
        EntityManager em = emf.createEntityManager();

        AreaConocimientoDAO cut = new AreaConocimientoDAO();
        cut.em = em;
        cut.em.getTransaction().begin();

        //creamos un registro sobre el que vamos a eliminar
        AreaConocimiento nuevo = new AreaConocimiento();
        nuevo.setId(UUID.randomUUID());
        nuevo.setNombre("Prueba 1");
        nuevo.setActivo(true);
        nuevo.setFechaCreacion(OffsetDateTime.now());
        nuevo.setIdAutoReferenciaArea(cut.findRange(0,1).getFirst());
        cut.create(nuevo);
        cut.em.getTransaction().commit();
        int registros = cut.count();
        assertEquals(8, registros);
        System.out.println("Total registros antes de eliminar: " + registros);

        //toma el registro creado y actuliza su nombre
        AreaConocimiento eliminacion =cut.findById(nuevo.getId());
        Assertions.assertNotNull(eliminacion);

        cut.em.getTransaction().begin();
        cut.delete(eliminacion);
        cut.em.getTransaction().commit();

        int registrosA = cut.count();
        assertEquals(7, registrosA);
        System.out.println("Total registros despues de eliminar: " + registrosA);

    }

    @Order(5)
    @Test
    public void testFindById(){
        System.out.println("findById");
        assertTrue(postgres.isRunning());
        Integer puertoPostgresql = postgres.getMappedPort(5432);
        Map<String, Object> propiedades = new HashMap<>();
        propiedades.put("jakarta.persistence.jdbc.url", String.format("jdbc:postgresql://localhost:%d/ingreso_ues_bd", puertoPostgresql));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("IngresoPUIT", propiedades);
        EntityManager em = emf.createEntityManager();

        AreaConocimientoDAO cut = new AreaConocimientoDAO();
        cut.em = em;
        cut.em.getTransaction().begin();

        //traer el primer id del stack
        UUID id=UUID.fromString("a1000000-0000-0000-0000-000000000001");
        cut.findById(id);
        cut.em.getTransaction().commit();

//        //buscar en el estac el primero y comparalo con este
//        AreaConocimiento buscado = new AreaConocimiento();
//        //buscado.getId(cut.findRange(0,1).getFirst());
//
//        int registros = cut.count();
//        assertEquals(8, registros);
//        System.out.println("Total registros antes de eliminar: " + registros);

    }



}