package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaArea;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPregunta;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PruebaAreaPreguntaDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<PruebaAreaPregunta> query;

    PruebaAreaPreguntaDAO dao;

    @BeforeEach
    void setUp() {
        dao = new PruebaAreaPreguntaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return em;
            }
        };
    }

    private PruebaAreaPreguntaDAO daoConEntityManagerNulo() {
        return new PruebaAreaPreguntaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };
    }

    private PruebaAreaPregunta pruebaAreaPreguntaValida() {
        PruebaArea pruebaArea = new PruebaArea();
        pruebaArea.setId(1);

        Pregunta pregunta = new Pregunta();
        pregunta.setId(1);

        PruebaAreaPregunta pap = new PruebaAreaPregunta();
        pap.setId(1);
        pap.setIdPruebaArea(pruebaArea);
        pap.setIdPregunta(pregunta);
        pap.setOrden((short) 1);
        pap.setFechaAsignacion(OffsetDateTime.now());
        return pap;
    }

    @Test
    void testFindByPruebaAreaIdNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaArea(null, 0, 10));
    }

    @Test
    void testFindByPruebaAreaFirstNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaArea(1, -1, 10));
    }

    @Test
    void testFindByPruebaAreaMaxCeroNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPruebaArea(1, 0, 0));
    }

    @Test
    void testFindByPruebaAreaParametrosValidosRetornaLista() {
        when(em.createNamedQuery("PruebaAreaPregunta.findByPruebaArea", PruebaAreaPregunta.class)).thenReturn(query);
        when(query.setParameter("idPruebaArea", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaAreaPreguntaValida()));

        List<PruebaAreaPregunta> resultado = dao.findByPruebaArea(1, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("PruebaAreaPregunta.findByPruebaArea", PruebaAreaPregunta.class);
        verify(query).setParameter("idPruebaArea", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindByPruebaAreaRetornaListaVacia() {
        when(em.createNamedQuery("PruebaAreaPregunta.findByPruebaArea", PruebaAreaPregunta.class)).thenReturn(query);
        when(query.setParameter("idPruebaArea", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaAreaPregunta> resultado = dao.findByPruebaArea(1, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByPruebaAreaErrorInterno() {
        when(em.createNamedQuery("PruebaAreaPregunta.findByPruebaArea", PruebaAreaPregunta.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByPruebaArea(1, 0, 10));
    }

    @Test
    void testFindByPruebaAreaEntityManagerNulo() {
        assertThrows(IllegalStateException.class,
                () -> daoConEntityManagerNulo().findByPruebaArea(1, 0, 10));
    }

    @Test
    void testFindByPreguntaIdNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPregunta(null, 0, 10));
    }

    @Test
    void testFindByPreguntaFirstNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPregunta(1, -1, 10));
    }

    @Test
    void testFindByPreguntaMaxCeroNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPregunta(1, 0, 0));
    }

    @Test
    void testFindByPreguntaParametrosValidosRetornaLista() {
        when(em.createNamedQuery("PruebaAreaPregunta.findByPregunta", PruebaAreaPregunta.class)).thenReturn(query);
        when(query.setParameter("idPregunta", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaAreaPreguntaValida()));

        List<PruebaAreaPregunta> resultado = dao.findByPregunta(1, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("PruebaAreaPregunta.findByPregunta", PruebaAreaPregunta.class);
        verify(query).setParameter("idPregunta", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindByPreguntaRetornaListaVacia() {
        when(em.createNamedQuery("PruebaAreaPregunta.findByPregunta", PruebaAreaPregunta.class)).thenReturn(query);
        when(query.setParameter("idPregunta", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaAreaPregunta> resultado = dao.findByPregunta(1, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByPreguntaErrorInterno() {
        when(em.createNamedQuery("PruebaAreaPregunta.findByPregunta", PruebaAreaPregunta.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByPregunta(1, 0, 10));
    }

    @Test
    void testFindByPreguntaEntityManagerNulo() {
        assertThrows(IllegalStateException.class,
                () -> daoConEntityManagerNulo().findByPregunta(1, 0, 10));
    }
}