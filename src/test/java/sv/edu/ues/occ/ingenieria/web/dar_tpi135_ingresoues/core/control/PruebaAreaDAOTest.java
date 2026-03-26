package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaArea;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PruebaAreaDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<PruebaArea> query;

    PruebaAreaDAO dao;

    @BeforeEach
    void setUp() {
        dao = new PruebaAreaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return em;
            }
        };
    }

    private PruebaAreaDAO daoConEntityManagerNulo() {
        return new PruebaAreaDAO() {
            @Override
            public EntityManager getEntityManager() {
                return null;
            }
        };
    }

    private PruebaArea pruebaAreaValida() {
        Prueba prueba = new Prueba();
        prueba.setId(1);

        AreaConocimiento area = new AreaConocimiento();
        area.setId(1);

        PruebaArea pa = new PruebaArea();
        pa.setId(1);
        pa.setIdPrueba(prueba);
        pa.setIdAreaConocimiento(area);
        pa.setNumPreguntas((short) 5);
        pa.setFechaAsignacion(OffsetDateTime.now());
        return pa;
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
        when(em.createNamedQuery("PruebaArea.findByPrueba", PruebaArea.class)).thenReturn(query);
        when(query.setParameter("idPrueba", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaAreaValida()));

        List<PruebaArea> resultado = dao.findByPrueba(1, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("PruebaArea.findByPrueba", PruebaArea.class);
        verify(query).setParameter("idPrueba", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindByPruebaRetornaListaVacia() {
        when(em.createNamedQuery("PruebaArea.findByPrueba", PruebaArea.class)).thenReturn(query);
        when(query.setParameter("idPrueba", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaArea> resultado = dao.findByPrueba(1, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByPruebaErrorInterno() {
        when(em.createNamedQuery("PruebaArea.findByPrueba", PruebaArea.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByPrueba(1, 0, 10));
    }

    @Test
    void testFindByPruebaEntityManagerNulo() {
        assertThrows(IllegalStateException.class,
                () -> daoConEntityManagerNulo().findByPrueba(1, 0, 10));
    }

    @Test
    void testFindByAreaConocimientoParametroNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByAreaConocimiento(null, 0, 10));
    }

    @Test
    void testFindByAreaConocimientoFirstNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByAreaConocimiento(1, -1, 10));
    }

    @Test
    void testFindByAreaConocimientoMaxCeroNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByAreaConocimiento(1, 0, 0));
    }

    @Test
    void testFindByAreaConocimientoParametrosValidosRetornaLista() {
        when(em.createNamedQuery("PruebaArea.findByAreaConocimiento", PruebaArea.class)).thenReturn(query);
        when(query.setParameter("idAreaConocimiento", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaAreaValida()));

        List<PruebaArea> resultado = dao.findByAreaConocimiento(1, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("PruebaArea.findByAreaConocimiento", PruebaArea.class);
        verify(query).setParameter("idAreaConocimiento", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindByAreaConocimientoRetornaListaVacia() {
        when(em.createNamedQuery("PruebaArea.findByAreaConocimiento", PruebaArea.class)).thenReturn(query);
        when(query.setParameter("idAreaConocimiento", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaArea> resultado = dao.findByAreaConocimiento(1, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByAreaConocimientoErrorInterno() {
        when(em.createNamedQuery("PruebaArea.findByAreaConocimiento", PruebaArea.class))
                .thenThrow(new RuntimeException("fallo en la Base de datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByAreaConocimiento(1, 0, 10));
    }

    @Test
    void testFindByAreaConocimientoEntityManagerNulo() {
        assertThrows(IllegalStateException.class,
                () -> daoConEntityManagerNulo().findByAreaConocimiento(1, 0, 10));
    }

    @Test
    void testFindByNumPreguntasMinParametroNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNumPreguntasMin(null, 0, 10));
    }

    @Test
    void testFindByNumPreguntasMinFirstNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNumPreguntasMin((short) 5, -1, 10));
    }

    @Test
    void testFindByNumPreguntasMinMaxCeroNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNumPreguntasMin((short) 5, 0, 0));
    }

    @Test
    void testFindByNumPreguntasMinParametrosValidosRetornaLista() {
        when(em.createNamedQuery("PruebaArea.findByNumPreguntasMin", PruebaArea.class)).thenReturn(query);
        when(query.setParameter("numPreguntas", (short) 5)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pruebaAreaValida()));

        List<PruebaArea> resultado = dao.findByNumPreguntasMin((short) 5, 0, 10);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(em).createNamedQuery("PruebaArea.findByNumPreguntasMin", PruebaArea.class);
        verify(query).setParameter("numPreguntas", (short) 5);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindByNumPreguntasMinRetornaListaVacia() {
        when(em.createNamedQuery("PruebaArea.findByNumPreguntasMin", PruebaArea.class)).thenReturn(query);
        when(query.setParameter("numPreguntas", (short) 99)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<PruebaArea> resultado = dao.findByNumPreguntasMin((short) 99, 0, 10);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByNumPreguntasMinErrorInterno() {
        when(em.createNamedQuery("PruebaArea.findByNumPreguntasMin", PruebaArea.class))
                .thenThrow(new RuntimeException("fallo en la Base de Datos"));

        assertThrows(IllegalStateException.class,
                () -> dao.findByNumPreguntasMin((short) 5, 0, 10));
    }

    @Test
    void testFindByNumPreguntasMinEntityManagerNulo() {
        assertThrows(IllegalStateException.class,
                () -> daoConEntityManagerNulo().findByNumPreguntasMin((short) 5, 0, 10));
    }
}