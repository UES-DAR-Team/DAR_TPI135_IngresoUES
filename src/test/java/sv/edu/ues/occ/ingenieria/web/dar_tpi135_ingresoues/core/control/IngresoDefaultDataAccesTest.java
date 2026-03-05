package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngresoDefaultDataAccesTest {

    static class TestEntity {
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    @Mock
    EntityManager em;
    IngresoDefaultDataAcces<TestEntity, Integer> dao;

    //aislamiento de la clase abstracta creando una clase anónima para poder probar el metodo create.
    @BeforeEach
    void setup() {
        dao = new IngresoDefaultDataAcces<TestEntity, Integer>(TestEntity.class) {
            @Override
            public EntityManager getEntityManager() {
                return em;
            }

            @Override
            protected Class<TestEntity> getEntityClass() {
                return TestEntity.class;
            }
        };
    }

    //======================================Tests de create====================================================
    //si el objeto es nulo, se espera que se lance una IllegalArgumentException.
    @Test
    void create_mostrarThrowIllegalArgumentException_siObjetoNulo() {
        assertThrows(IllegalArgumentException.class, () -> dao.create(null));
    }

    //confirmamiento de las llamadas a persist y flush del EntityManager cuando se crea un objeto valido.
    @Test
    void create_mostrarLlamadaPersist_y_Flush_siObjetoValido() {
        TestEntity entity = new TestEntity();
        dao.create(entity);
        //verificar que se llama a persist y flush con el objeto correcto.
        verify(em).persist(entity);
        verify(em).flush();
    }

    @Test
    void create_mostrarThrowException_cuandoEntityManagerEsNulo() {
        IngresoDefaultDataAcces<TestEntity, Integer> daoNull =
                new IngresoDefaultDataAcces<>(TestEntity.class) {
                    @Override
                    public EntityManager getEntityManager() {
                        return null;
                    }

                    @Override
                    protected Class<TestEntity> getEntityClass() {
                        return TestEntity.class;
                    }
                };
        assertThrows(IllegalArgumentException.class, () -> daoNull.create(new TestEntity()));
    }

    //los siguientes tests son para asegurar la covertura en las ramas de excepciones,
    // asegurando que se lanza la excepciones adecuadas.
    @Test
    void create_debeEnvolverExcepcioneEnIllegalStateException_siPersistFalla() {
        TestEntity entity = new TestEntity();
        //doThrow se utiliza para simular que el metodo persist lanza una excepcion,
        // lo que nos permite probar como el metodo create maneja esta situacion.
        doThrow(new RuntimeException("DB error")).when(em).persist(entity);
        IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> dao.create(entity));
        assertEquals("Error al acceder al repositorio", exception.getMessage());
    }

    @Test
    void create_debeEnvolverExcepcioneEnIllegalArgumentException_siPersistLanzaIllegalArgumentException() {
        TestEntity entity = new TestEntity();
        doThrow(new IllegalArgumentException("Dato invalido")).when(em).persist(entity);
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> dao.create(entity));
        assertEquals("Dato invalido", exception.getMessage());
    }

    //=========================================test de delete====================================================
    @Test
    void delete_mostrarThrowIllegalArgumentException_siObjetoNulo() {
        assertThrows(IllegalArgumentException.class, () -> dao.delete(null));
    }

    @Test
    void delete_mostrarLLamadas_Merge_y_Remove() {
        TestEntity entity = new TestEntity();
        //simular el comportamiento del EntityManager para el metodo merge, devolviendo el mismo objeto.
        when(em.merge(entity)).thenReturn(entity);
        dao.delete(entity);
        //verificacion de las llamadas de merge y remove con el objeto correcto
        verify(em).merge(entity);
        verify(em).remove(entity);
    }

    //osea si despues de la asignacion limpia del EntityManager, este es nulo, se espera que se lance una IllegalArgumentException.
    @Test
    void delete_mostrarThrowException_cuandoEntityManagerEsNulo() {
        IngresoDefaultDataAcces<TestEntity, Integer> daoNull =
                new IngresoDefaultDataAcces<>(TestEntity.class) {
                    @Override
                    public EntityManager getEntityManager() {
                        return null;
                    }

                    @Override
                    protected Class<TestEntity> getEntityClass() {
                        return TestEntity.class;
                    }
                };
        assertThrows(IllegalArgumentException.class, () -> daoNull.delete(new TestEntity()));
    }

    // a partir de aqui van los tests para asegurar la cobertura de las ramas de excepciones en el metodo delete,
    // asegurando que se lanza la excepcion adecuada cuando el EntityManager lanza una excepcion.
    @Test
    void delete_debe_EnvolverExcepcionesEnIllegalStateException_siMergeFalla() {
        TestEntity entity = new TestEntity();
        //doThrow se utiliza para simular que el metodo persist lanza una excepcion,
        // lo que nos permite probar como el metodo create maneja esta situacion.
        doThrow(new RuntimeException("DB error")).when(em).merge(entity);
        IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> dao.delete(entity));
        assertEquals("Error al acceder al repositorio", exception.getMessage());
    }

    @Test
    void delete_debe_EnvolverExcepcionesEnIllegalArgumentException_siPersistLanzaIllegalArgumentExcetrion() {
        TestEntity entity = new TestEntity();
        doThrow(new IllegalArgumentException("Dato invalido")).when(em).merge(entity);
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> dao.delete(entity));
        assertEquals("Dato invalido", exception.getMessage());
    }


}