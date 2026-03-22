package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aula;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//IAE = IllegalArgumentException cuando el parametro es invalido
//ISE = IllegalStateException cuando hay error interno o EntityManager nulo
//Lista vacia = no hay resultados pero tampoco se lanza excepcion

@ExtendWith(MockitoExtension.class)
class AulaDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<Aula> query;

    AulaDAO dao;

    @BeforeEach
    void setUp() {
        dao = new AulaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return em;
            }
        };
    }

    // Retorna un DAO cuyo EntityManager es nulo, haciendo simular un fallo de inyeccion
    private AulaDAO daoConEmNulo() {
        return new AulaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };
    }

    // Construye una Aula con todos los campos validos para los tests con resultados esperados
    private Aula aulaValida() {
        Aula a = new Aula();
        a.setId(1);
        a.setNombreAula("Aula 1");
        a.setCapacidad(30);
        a.setFechaCreacion(OffsetDateTime.now());
        a.setActivo(true);
        return a;
    }

    // findByCapacidadMin ----------------------------------


    // La capacidad es nula, el DAO debe rechazarla antes de consultar la BD
    @Test
    void findByCapacidadMin_cuandoCapacidadNula_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByCapacidadMin(null, 0, 10));
    }

    // El parametro first es negativo, se prueba solo para saber exactamente que condicion falla
    @Test
    void findByCapacidadMin_cuandoFirstNegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByCapacidadMin(10, -1, 10));
    }

    // El parametro max es cero, se prueba solo separado del anterior para saber exactamente que condicion falla
    @Test
    void findByCapacidadMin_cuandoMaxCeroONegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByCapacidadMin(10, 0, 0));
    }

    // Todos los parametros son validos, se verifica que la query se ejecute correctamente
    @Test
    void findByCapacidadMin_cuandoParametrosValidos_deberiaRetornarLista() {
        when(em.createNamedQuery("Aula.findByCapacidadMin", Aula.class)).thenReturn(query);
        when(query.setParameter("capacidad", 10)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(aulaValida()));

        List<Aula> resultado = dao.findByCapacidadMin(10, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("Aula.findByCapacidadMin", Aula.class);
        verify(query).setParameter("capacidad", 10);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    // No hay aulas con esa capacidad, el DAO devuelve lista vacia sin lanzar excepcion
    @Test
    void findByCapacidadMin_cuandoNoHayResultados_deberiaRetornarListaVacia() {
        when(em.createNamedQuery("Aula.findByCapacidadMin", Aula.class)).thenReturn(query);
        when(query.setParameter("capacidad", 99)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Aula> resultado = dao.findByCapacidadMin(99, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // La BD falla, el DAO debe envolver el error en ISE
    @Test
    void findByCapacidadMin_cuandoErrorInterno_deberiaLanzarISE() {
        when(em.createNamedQuery("Aula.findByCapacidadMin", Aula.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByCapacidadMin(10, 0, 10));
    }

    // El EntityManager es nulo, el DAO debe capturar el error y lanzar ISE
    @Test
    void findByCapacidadMin_cuandoEmNulo_deberiaLanzarISE() {
        assertThrows(IllegalStateException.class,
                () -> daoConEmNulo().findByCapacidadMin(10, 0, 10));
    }

    // findByNombre -------------------------------------


    // El nombre es nulo el DAO debe rechazarlo antes de consultar
    @Test
    void findByNombre_cuandoNombreNulo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNombre(null, 0, 10));
    }

    // El nombre tiene solo espacios, isBlank lo detecta como invalido igual que nulo
    @Test
    void findByNombre_cuandoNombreBlank_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNombre("   ", 0, 10));
    }

    // El parametro first es negativo con nombre valido
    @Test
    void findByNombre_cuandoFirstNegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNombre("Aula1", -1, 10));
    }

    @Test
    void findByNombre_cuandoMaxCeroONegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNombre("Aula1", 0, 0));
    }

    // Todos los parametros son validos, el mock recibe el nombre sin porcentajes
    @Test
    void findByNombre_cuandoParametrosValidos_deberiaRetornarLista() {
        when(em.createNamedQuery("Aula.findByNombre", Aula.class)).thenReturn(query);
        when(query.setParameter("nombre", "Aula1")).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(aulaValida()));

        List<Aula> resultado = dao.findByNombre("Aula1", 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("Aula.findByNombre", Aula.class);
        verify(query).setParameter("nombre", "Aula1");
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    // El nombre entra con espacios y el verify confirma que llego limpio al mock
    @Test
    void findByNombre_cuandoNombreConEspacios_deberiaAplicarTrim() {
        when(em.createNamedQuery("Aula.findByNombre", Aula.class)).thenReturn(query);
        when(query.setParameter("nombre", "Aula1")).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        dao.findByNombre("  Aula1  ", 0, 10);

        verify(query).setParameter("nombre", "Aula1");
    }

    // No hay aulas con ese nombre, se devuelve lista vacia sin excepcion
    @Test
    void findByNombre_cuandoNoHayResultados_deberiaRetornarListaVacia() {
        when(em.createNamedQuery("Aula.findByNombre", Aula.class)).thenReturn(query);
        when(query.setParameter("nombre", "XYZ")).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Aula> resultado = dao.findByNombre("XYZ", 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // La base de datos falla, el DAO envuelve el error en ISE
    @Test
    void findByNombre_cuandoErrorInterno_deberiaLanzarISE() {
        when(em.createNamedQuery("Aula.findByNombre", Aula.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByNombre("Aula1", 0, 10));
    }

    // El EntityManager es nulo, el DAO captura el error y lanza ISE
    @Test
    void findByNombre_cuandoEmNulo_deberiaLanzarISE() {
        assertThrows(IllegalStateException.class,
                () -> daoConEmNulo().findByNombre("Aula1", 0, 10));
    }

    // findActivos ----------------------------

    @Test
    void findActivos_cuandoFirstNegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findActivos(-1, 10));
    }

    @Test
    void findActivos_cuandoMaxCeroONegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findActivos(0, 0));
    }

    // Paginacion valida, se verifica que la query se ejecute correctamente
    @Test
    void findActivos_cuandoParametrosValidos_deberiaRetornarLista() {
        when(em.createNamedQuery("Aula.findActivos", Aula.class)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(aulaValida()));

        List<Aula> resultado = dao.findActivos(0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("Aula.findActivos", Aula.class);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    // No hay aulas activas en el sistema, se devuelve lista vacia sin excepcion
    @Test
    void findActivos_cuandoNoHayActivos_deberiaRetornarListaVacia() {
        when(em.createNamedQuery("Aula.findActivos", Aula.class)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Aula> resultado = dao.findActivos(0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // cuando la BD falla, el DAO envuelve el error en ISE
    @Test
    void findActivos_cuandoErrorInterno_deberiaLanzarISE() {
        when(em.createNamedQuery("Aula.findActivos", Aula.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findActivos(0, 10));
    }

    // El EntityManager es nulo, el DAO captura el error y lanza ISE
    @Test
    void findActivos_cuandoEmNulo_deberiaLanzarISE() {
        assertThrows(IllegalStateException.class,
                () -> daoConEmNulo().findActivos(0, 10));
    }
}