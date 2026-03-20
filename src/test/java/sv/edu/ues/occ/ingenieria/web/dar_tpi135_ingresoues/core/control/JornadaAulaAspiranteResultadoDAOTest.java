package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspiranteResultado;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JornadaAulaAspiranteResultadoDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<JornadaAulaAspiranteResultado> query;

    @InjectMocks
    private JornadaAulaAspiranteResultadoDAO dao;

    @Test
    void debeRetornarLista_buscarPorJornadaAulaAspirante() {

        List<JornadaAulaAspiranteResultado> lista = List.of(new JornadaAulaAspiranteResultado());

        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<JornadaAulaAspiranteResultado> result =
                dao.buscarPorJornadaAulaAspirante(1, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void retornaVacio_siIdNull_buscarPorJornadaAulaAspirante() {
        assertTrue(dao.buscarPorJornadaAulaAspirante(null, 0, 10).isEmpty());
    }

    @Test
    void retornaVacio_siFirstNegativo_buscarPorJornadaAulaAspirante() {
        assertTrue(dao.buscarPorJornadaAulaAspirante(1, -1, 10).isEmpty());
    }

    @Test
    void retornaVacio_siMaxInvalido_buscarPorJornadaAulaAspirante() {
        assertTrue(dao.buscarPorJornadaAulaAspirante(1, 0, 0).isEmpty());
    }

    @Test
    void lanzaIllegalState_siFallaCreateNamedQuery_buscarPorJornadaAulaAspirante() {

        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorJornadaAulaAspirante(1, 0, 10));
    }

    @Test
    void lanzaIllegalState_siFallaGetResultList_buscarPorJornadaAulaAspirante() {

        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorJornadaAulaAspirante(1, 0, 10));
    }

    @Test
    void debeRetornarLista_buscarPorAprobado() {

        List<JornadaAulaAspiranteResultado> lista = List.of(new JornadaAulaAspiranteResultado());

        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<JornadaAulaAspiranteResultado> result =
                dao.buscarPorAprobado(true, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void retornaVacio_siAprobadoNull() {
        assertTrue(dao.buscarPorAprobado(null, 0, 10).isEmpty());
    }

    @Test
    void retornaVacio_siFirstNegativo_buscarPorAprobado() {
        assertTrue(dao.buscarPorAprobado(true, -1, 10).isEmpty());
    }

    @Test
    void retornaVacio_siMaxInvalido_buscarPorAprobado() {
        assertTrue(dao.buscarPorAprobado(true, 0, 0).isEmpty());
    }

    @Test
    void lanzaIllegalState_siFallaCreateNamedQuery_buscarPorAprobado() {

        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorAprobado(true, 0, 10));
    }

    @Test
    void lanzaIllegalState_siFallaGetResultList_buscarPorAprobado() {

        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorAprobado(true, 0, 10));
    }

    @Test
    void debeRetornarLista_buscarPorRangoPuntaje() {

        List<JornadaAulaAspiranteResultado> lista = List.of(new JornadaAulaAspiranteResultado());

        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<JornadaAulaAspiranteResultado> result =
                dao.buscarPorRangoPuntaje(BigDecimal.ZERO, BigDecimal.TEN);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getEntityManager_noDebeSerNull() {
        assertNotNull(dao.getEntityManager());
    }

    @Test
    void getEntityClass_debeSerCorrecta() {
        assertEquals(JornadaAulaAspiranteResultado.class, dao.getEntityClass());
    }
}