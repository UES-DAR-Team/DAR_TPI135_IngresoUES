package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    private PruebaJornadaDAO daoConEntityNulo() {
        return new PruebaJornadaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };
    }

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

    @Test
    void testFindByJornadaIdNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByJornada(null, 0, 10));
    }

    @Test
    void testFindByJornadaFirstNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByJornada(1, -1, 10));
    }

    @Test
    void testFindByJornadaMaxCeroNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByJornada(1, 0, 0));
    }

    @Test
    void testFindByJornadaParametrosValidosRetornaLista() {
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

    @Test
    void testFindByJornadaRetornaListaVacia() {
        when(em.createNamedQuery("PruebaJornada.findByJornada", PruebaJornada.class)).thenReturn(query);
        when(query.setParameter("idJornada", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaJornada> resultado = dao.findByJornada(1, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByJornadaErrorInterno() {
        when(em.createNamedQuery("PruebaJornada.findByJornada", PruebaJornada.class))
                .thenThrow(new RuntimeException("Fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByJornada(1, 0, 10));
    }

    @Test
    void testFindByJornadaEntityManagerNulo() {
        assertThrows(IllegalStateException.class,
                () -> daoConEntityNulo().findByJornada(1, 0, 10));
    }

    @Test
    void testFindByPruebaParametroNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPrueba(null, 0, 10));
    }

    @Test
    void testFindByPruebaFirstNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPrueba(1, -1, 10));
    }

    @Test
    void testFindByPruebaMaxCeroNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPrueba(1, 0, 0));
    }

    @Test
    void testFindByPruebaParametrosValidosRetornaLista() {
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

    @Test
    void testFindByPruebaRetornaListaVacia() {
        when(em.createNamedQuery("PruebaJornada.findByPrueba", PruebaJornada.class)).thenReturn(query);
        when(query.setParameter("idPrueba", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaJornada> resultado = dao.findByPrueba(1, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByPruebaErrorInterno() {
        when(em.createNamedQuery("PruebaJornada.findByPrueba", PruebaJornada.class))
                .thenThrow(new RuntimeException("Fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByPrueba(1, 0, 10));
    }

    @Test
    void testFindByPruebaEntityManagerNulo() {
        assertThrows(IllegalStateException.class,
                () -> daoConEntityNulo().findByPrueba(1, 0, 10));
    }

    /**
     * Verifica el comportamiento del metodo al filtrar por prueba y jornada
     * asegurando que ambos parametros se envían correctamente a la consulta
     * y que la paginación se aplica adecuadamente.
     */

    @Test
    void testFindByPruebaAndJornadaIdPruebaNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaAndJornada(null, 1, 0, 10));
    }

    @Test
    void testFindByPruebaAndJornadaIdJornadaNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaAndJornada(1, null, 0, 10));
    }

    @Test
    void testFindByPruebaAndJornadaFirstNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaAndJornada(1, 1, -1, 10));
    }

    @Test
    void testfFindByPruebaAndJornadaMaxCeroNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaAndJornada(1, 1, 0, 0));
    }

    @Test
    void testFindByPruebaAndJornadaParametrosValidosRetornaLista() {
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

    @Test
    void testFindByPruebaAndJornadaRetornaListaVacia() {
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

    @Test
    void testFindByPruebaAndJornadaErrorInterno() {
        when(em.createNamedQuery("PruebaJornada.findByPruebaAndJornada", PruebaJornada.class))
                .thenThrow(new RuntimeException("Fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByPruebaAndJornada(1, 1, 0, 10));
    }

    @Test
    void testFindByPruebaAndJornadaEntityManagerNulo() {
        assertThrows(IllegalStateException.class,
                () -> daoConEntityNulo().findByPruebaAndJornada(1, 1, 0, 10));
    }
}