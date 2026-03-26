package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAula;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JornadaAulaDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<JornadaAula> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private JornadaAulaDAO dao;

    @Test
    void debeRetornarLista_findByJornada() {

        List<JornadaAula> lista = List.of(new JornadaAula());

        when(em.createNamedQuery(anyString(), eq(JornadaAula.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<JornadaAula> result = dao.findByJornada(1, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void lanzaException_idNull_findByJornada() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByJornada(null, 0, 10));
    }

    @Test
    void lanzaException_paginacionInvalida_findByJornada() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByJornada(1, -1, 10));
    }

    @Test
    void lanzaIllegalState_findByJornada() {

        when(em.createNamedQuery(anyString(), eq(JornadaAula.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.findByJornada(1, 0, 10));
    }

    @Test
    void debeRetornarLista_findByAula() {

        List<JornadaAula> lista = List.of(new JornadaAula());

        when(em.createNamedQuery(anyString(), eq(JornadaAula.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<JornadaAula> result = dao.findByAula(1, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void lanzaException_idNull_findByAula() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByAula(null, 0, 10));
    }

    @Test
    void lanzaException_paginacionInvalida_findByAula() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByAula(1, -1, 10));
    }

    @Test
    void lanzaIllegalState_findByAula() {

        when(em.createNamedQuery(anyString(), eq(JornadaAula.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.findByAula(1, 0, 10));
    }

    @Test
    void debeContarPorJornada() {

        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.setParameter(anyString(), any())).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(5L);

        Long result = dao.countByJornada(1);

        assertEquals(5L, result);
    }

    @Test
    void lanzaException_idNull_countByJornada() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.countByJornada(null));
    }

    @Test
    void lanzaIllegalState_countByJornada() {

        when(em.createQuery(anyString(), eq(Long.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.countByJornada(1));
    }

    @Test
    void debeContarPorAula() {

        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.setParameter(anyString(), any())).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(3L);

        Long result = dao.countByAula(1);

        assertEquals(3L, result);
    }

    @Test
    void lanzaException_idNull_countByAula() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.countByAula(null));
    }

    @Test
    void lanzaIllegalState_countByAula() {

        when(em.createQuery(anyString(), eq(Long.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.countByAula(1));
    }

    @Test
    void getEntityManager_noDebeSerNull() {
        assertNotNull(dao.getEntityManager());
    }

    @Test
    void getEntityClass_debeSerCorrecta() {
        assertEquals(JornadaAula.class, dao.getEntityClass());
    }
}