package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaJornada;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PruebaJornadaDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<PruebaJornada> query;

    PruebaJornadaDAO dao;

    @BeforeEach
    void setUp() {
        dao = new PruebaJornadaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return em;
            }
        };
    }

    // Retorna un DAO donde el EntityManager es nulo, simulando un fallo de inyeccion
    private PruebaJornadaDAO daoConEmNulo() {
        return new PruebaJornadaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };
    }

    // Construye una PruebaJornada valida para los tests con resultados esperados
    private PruebaJornada pruebaJornadaValida() {
        Prueba prueba = new Prueba();
        prueba.setId(1);

        Jornada jornada = new Jornada();
        jornada.setId(1);

        PruebaJornada pj = new PruebaJornada();
        pj.setId(1);
        pj.setIdPrueba(prueba);
        pj.setIdJornada(jornada);
        pj.setFechaAsignacion(OffsetDateTime.now());
        return pj;
    }

    // findByJornada ----------------------------------

    // El id de la jornada es nulo, el DAO debe rechazarlo antes de consultar la BD
    @Test
    void findByJornada_cuandoIdNulo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByJornada(null, 0, 10));
    }

    // El parametro first es negativo, cada condicion se prueba por separado
    @Test
    void findByJornada_cuandoFirstNegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByJornada(1, -1, 10));
    }

    // El parametro max es cero, separado del anterior para identificar cual falla
    @Test
    void findByJornada_cuandoMaxCeroONegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByJornada(1, 0, 0));
    }

    // Todos los parametros son validos, se verifica que la query se ejecute correctamente
    @Test
    void findByJornada_cuandoParametrosValidos_deberiaRetornarLista() {
        when(em.createNamedQuery("PruebaJornada.findByJornada", PruebaJornada.class)).thenReturn(query);
        when(query.setParameter("idJornada", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaJornadaValida()));

        List<PruebaJornada> resultado = dao.findByJornada(1, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("PruebaJornada.findByJornada", PruebaJornada.class);
        verify(query).setParameter("idJornada", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    // No hay pruebas asignadas a esa jornada, se devuelve lista vacia sin excepcion
    @Test
    void findByJornada_cuandoNoHayResultados_deberiaRetornarListaVacia() {
        when(em.createNamedQuery("PruebaJornada.findByJornada", PruebaJornada.class)).thenReturn(query);
        when(query.setParameter("idJornada", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaJornada> resultado = dao.findByJornada(1, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // La BD falla, el DAO debe envolver el error en ISE
    @Test
    void findByJornada_cuandoErrorInterno_deberiaLanzarISE() {
        when(em.createNamedQuery("PruebaJornada.findByJornada", PruebaJornada.class))
                .thenThrow(new RuntimeException("Fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByJornada(1, 0, 10));
    }

    // El EntityManager es nulo, el DAO debe capturar el error y lanzar ISE
    @Test
    void findByJornada_cuandoEmNulo_deberiaLanzarISE() {
        assertThrows(IllegalStateException.class,
                () -> daoConEmNulo().findByJornada(1, 0, 10));
    }

    // findByPrueba ----------------------------------

    // El id de la prueba es nulo, el DAO debe rechazarlo antes de consultar la BD
    @Test
    void findByPrueba_cuandoIdNulo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPrueba(null, 0, 10));
    }

    // El parametro first es negativo, cada condicion se prueba por separado
    @Test
    void findByPrueba_cuandoFirstNegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPrueba(1, -1, 10));
    }

    // El parametro max es cero, separado del anterior para identificar cual falla
    @Test
    void findByPrueba_cuandoMaxCeroONegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPrueba(1, 0, 0));
    }

    // Todos los parametros son validos, se verifica que la query se ejecute correctamente
    @Test
    void findByPrueba_cuandoParametrosValidos_deberiaRetornarLista() {
        when(em.createNamedQuery("PruebaJornada.findByPrueba", PruebaJornada.class)).thenReturn(query);
        when(query.setParameter("idPrueba", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaJornadaValida()));

        List<PruebaJornada> resultado = dao.findByPrueba(1, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("PruebaJornada.findByPrueba", PruebaJornada.class);
        verify(query).setParameter("idPrueba", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    // No hay jornadas que contengan esa prueba, se devuelve lista vacia sin excepcion
    @Test
    void findByPrueba_cuandoNoHayResultados_deberiaRetornarListaVacia() {
        when(em.createNamedQuery("PruebaJornada.findByPrueba", PruebaJornada.class)).thenReturn(query);
        when(query.setParameter("idPrueba", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaJornada> resultado = dao.findByPrueba(1, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // La BD falla, el DAO debe envolver el error en ISE
    @Test
    void findByPrueba_cuandoErrorInterno_deberiaLanzarISE() {
        when(em.createNamedQuery("PruebaJornada.findByPrueba", PruebaJornada.class))
                .thenThrow(new RuntimeException("Fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByPrueba(1, 0, 10));
    }

    // El EntityManager es nulo, el DAO debe capturar el error y lanzar ISE
    @Test
    void findByPrueba_cuandoEmNulo_deberiaLanzarISE() {
        assertThrows(IllegalStateException.class,
                () -> daoConEmNulo().findByPrueba(1, 0, 10));
    }

    // findByPruebaAndJornada ----------------------------------
    // tiene dos parametros de filtro

    // El id de la prueba es nulo, se valida antes del id de la jornada
    @Test
    void findByPruebaAndJornada_cuandoIdPruebaNulo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaAndJornada(null, 1, 0, 10));
    }

    // El id de la jornada es nulo con prueba valida
    @Test
    void findByPruebaAndJornada_cuandoIdJornadaNulo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaAndJornada(1, null, 0, 10));
    }

    // El parametro first es negativo, cada condicion se prueba por separado
    @Test
    void findByPruebaAndJornada_cuandoFirstNegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaAndJornada(1, 1, -1, 10));
    }

    // El parametro max es cero, separado del anterior para identificar cual falla
    @Test
    void findByPruebaAndJornada_cuandoMaxCeroONegativo_deberiaLanzarIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaAndJornada(1, 1, 0, 0));
    }

    // Todos los parametros son validos, se verifica que ambos parametros lleguen al mock
    @Test
    void findByPruebaAndJornada_cuandoParametrosValidos_deberiaRetornarLista() {
        when(em.createNamedQuery("PruebaJornada.findByPruebaAndJornada", PruebaJornada.class)).thenReturn(query);
        when(query.setParameter("idPrueba", 1)).thenReturn(query);
        when(query.setParameter("idJornada", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaJornadaValida()));

        List<PruebaJornada> resultado = dao.findByPruebaAndJornada(1, 1, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("PruebaJornada.findByPruebaAndJornada", PruebaJornada.class);
        verify(query).setParameter("idPrueba", 1);
        verify(query).setParameter("idJornada", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    // La combinacion prueba y jornada no existe, se devuelve lista vacia sin excepcion
    @Test
    void findByPruebaAndJornada_cuandoNoHayResultados_deberiaRetornarListaVacia() {
        when(em.createNamedQuery("PruebaJornada.findByPruebaAndJornada", PruebaJornada.class)).thenReturn(query);
        when(query.setParameter("idPrueba", 1)).thenReturn(query);
        when(query.setParameter("idJornada", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaJornada> resultado = dao.findByPruebaAndJornada(1, 1, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // La BD falla, el DAO debe envolver el error en ISE
    @Test
    void findByPruebaAndJornada_cuandoErrorInterno_deberiaLanzarISE() {
        when(em.createNamedQuery("PruebaJornada.findByPruebaAndJornada", PruebaJornada.class))
                .thenThrow(new RuntimeException("Fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByPruebaAndJornada(1, 1, 0, 10));
    }

    // El EntityManager es nulo, el DAO debe capturar el error y lanzar ISE
    @Test
    void findByPruebaAndJornada_cuandoEmNulo_deberiaLanzarISE() {
        assertThrows(IllegalStateException.class,
                () -> daoConEmNulo().findByPruebaAndJornada(1, 1, 0, 10));
    }
}