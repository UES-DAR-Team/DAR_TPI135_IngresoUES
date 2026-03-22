package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PruebaDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<Prueba> query;

    PruebaDAO dao;

    @BeforeEach
    void setUp() {
        dao = new PruebaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return em;
            }
        };
    }

    // devuelve un DAO donde el EntityManager es nulo, simulando un fallo de inyeccion
    private PruebaDAO daoConEmNulo() {
        return new PruebaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };
    }

    // Construye una Prueba con todos los campos validos para los tests con resultados esperados
    private Prueba pruebaValida() {
        Prueba p = new Prueba();
        p.setId(1);
        p.setNombrePrueba("Prueba de Matematica");
        p.setDuracionMin(90);
        p.setFechaCreacion(OffsetDateTime.now());
        p.setActivo(true);
        return p;
    }

    // findActivas ----------------------------------

    // El parametro first es negativo, se prueba solo para saber exactamente que condicion falla
    @Test
    void findActivas_cuandoFirstNegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findActivas(-1, 10));
    }

    // El parametro max es cero, se prueba solo separado para saber en que condicion falla
    @Test
    void findActivas_cuandoMaxCeroONegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findActivas(0, 0));
    }

    // paginacion valida, se verifica que la query se ejecute correctamente
    @Test
    void findActivas_cuandoPaginacionValida_deberiaRetornarLista() {
        when(em.createNamedQuery("Prueba.findActivas", Prueba.class)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaValida()));

        List<Prueba> resultado = dao.findActivas(0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("Prueba.findActivas", Prueba.class);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    // No hay pruebas activas en el sistema, se devuelve lista vacia sin excepcion
    @Test
    void findActivas_cuandoNoHayActivas_deberiaRetornarListaVacia() {
        when(em.createNamedQuery("Prueba.findActivas", Prueba.class)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Prueba> resultado = dao.findActivas(0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // La BD falla, el DAO debe envolver el error en ISE
    @Test
    void findActivas_cuandoErrorInterno_deberiaLanzarISE() {
        when(em.createNamedQuery("Prueba.findActivas", Prueba.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findActivas(0, 10));
    }

    // El EntityManager es nulo, el DAO debe capturar el error y lanzar ISE
    @Test
    void findActivas_cuandoEmNulo_deberiaLanzarISE() {
        assertThrows(IllegalStateException.class,
                () -> daoConEmNulo().findActivas(0, 10));
    }

    // findByNombre ----------------------------------

    // El nombre es nulo, el DAO debe rechazarlo antes de consultar la BD
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
                () -> dao.findByNombre("Matematica", -1, 10));
    }

    // El parametro max es cero con nombre valido
    @Test
    void findByNombre_cuandoMaxCeroONegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNombre("Matematica", 0, 0));
    }

    // Todos los parametros son validos, el mock recibe el nombre sin porcentajes
    @Test
    void findByNombre_cuandoParametrosValidos_deberiaRetornarLista() {
        when(em.createNamedQuery("Prueba.findByNombre", Prueba.class)).thenReturn(query);
        when(query.setParameter("nombre", "Matematica")).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaValida()));

        List<Prueba> resultado = dao.findByNombre("Matematica", 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("Prueba.findByNombre", Prueba.class);
        verify(query).setParameter("nombre", "Matematica");
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }


    //verifica que el DAO limpia los espacios del nombre antes de enviarlo a la base.
    // el nombre entra con espacios y el verify confirma que llego limpio al mock
    @Test
    void findByNombre_cuandoNombreConEspacios_deberiaAplicarTrim() {
        when(em.createNamedQuery("Prueba.findByNombre", Prueba.class)).thenReturn(query);
        when(query.setParameter("nombre", "Matematica")).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        dao.findByNombre("  Matematica  ", 0, 10);

        verify(query).setParameter("nombre", "Matematica");
    }

    // Cuando la base no encuentra ninguna prueba con ese nombre, se devuelve lista vacia sin excepcion
    @Test
    void findByNombre_cuandoNoHayResultados_deberiaRetornarListaVacia() {
        when(em.createNamedQuery("Prueba.findByNombre", Prueba.class)).thenReturn(query);
        when(query.setParameter("nombre", "XYZ")).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<Prueba> resultado = dao.findByNombre("XYZ", 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // La BD falla, el DAO envuelve el error en ISE
    @Test
    void findByNombre_cuandoErrorInterno_deberiaLanzarISE() {
        when(em.createNamedQuery("Prueba.findByNombre", Prueba.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByNombre("Matematica", 0, 10));
    }

    // El EntityManager es nulo, el DAO captura el error y lanza ISE
    @Test
    void findByNombre_cuandoEmNulo_deberiaLanzarISE() {
        assertThrows(IllegalStateException.class,
                () -> daoConEmNulo().findByNombre("Matematica", 0, 10));
    }
}