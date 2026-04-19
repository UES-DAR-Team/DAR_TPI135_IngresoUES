package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAula;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JornadaAulaDAOTest {

    private JornadaAulaDAO dao;
    private EntityManager em;
    private TypedQuery<JornadaAula> queryJA;
    private TypedQuery<Long> queryLong;

    @BeforeEach
    void setUp() throws Exception {
        dao = new JornadaAulaDAO();

        em = mock(EntityManager.class);
        queryJA = mock(TypedQuery.class);
        queryLong = mock(TypedQuery.class);

        var field = JornadaAulaDAO.class.getDeclaredField("em");
        field.setAccessible(true);
        field.set(dao, em);
    }


    @Test
    void findByJornada_ok() {
        UUID id = UUID.randomUUID();

        when(em.createNamedQuery("JornadaAula.buscarPorJornada", JornadaAula.class)).thenReturn(queryJA);
        when(queryJA.setParameter("idJornada", id)).thenReturn(queryJA);
        when(queryJA.setFirstResult(0)).thenReturn(queryJA);
        when(queryJA.setMaxResults(10)).thenReturn(queryJA);
        when(queryJA.getResultList()).thenReturn(List.of(new JornadaAula()));

        List<JornadaAula> result = dao.findByJornada(id, 0, 10);

        assertFalse(result.isEmpty());
    }

    @Test
    void findByJornada_idNull() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByJornada(null, 0, 10));
    }

    @Test
    void findByJornada_paramInvalidos() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByJornada(UUID.randomUUID(), -1, 10));

        assertThrows(IllegalArgumentException.class,
                () -> dao.findByJornada(UUID.randomUUID(), 0, 0));
    }

    @Test
    void findByJornada_exception() {
        when(em.createNamedQuery(anyString(), eq(JornadaAula.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.findByJornada(UUID.randomUUID(), 0, 10));
    }


    @Test
    void findByAula_ok() {
        UUID id = UUID.randomUUID();

        when(em.createNamedQuery("JornadaAula.buscarPorAula", JornadaAula.class)).thenReturn(queryJA);
        when(queryJA.setParameter("idAula", id)).thenReturn(queryJA);
        when(queryJA.setFirstResult(0)).thenReturn(queryJA);
        when(queryJA.setMaxResults(10)).thenReturn(queryJA);
        when(queryJA.getResultList()).thenReturn(List.of(new JornadaAula()));

        List<JornadaAula> result = dao.findByAula(id, 0, 10);

        assertFalse(result.isEmpty());
    }

    @Test
    void findByAula_idNull() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByAula(null, 0, 10));
    }

    @Test
    void findByAula_exception() {
        when(em.createNamedQuery(anyString(), eq(JornadaAula.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.findByAula(UUID.randomUUID(), 0, 10));
    }


    @Test
    void countByJornada_ok() {
        UUID id = UUID.randomUUID();

        when(em.createNamedQuery("JornadaAula.countByJornada", Long.class)).thenReturn(queryLong);
        when(queryLong.setParameter("idJornada", id)).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(5L);

        Long result = dao.countByJornada(id);

        assertEquals(5L, result);
    }

    @Test
    void countByJornada_null() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.countByJornada(null));
    }

    @Test
    void countByJornada_exception() {
        when(em.createNamedQuery(anyString(), eq(Long.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.countByJornada(UUID.randomUUID()));
    }


    @Test
    void countByAula_ok() {
        UUID id = UUID.randomUUID();

        when(em.createNamedQuery("JornadaAula.countByAula", Long.class)).thenReturn(queryLong);
        when(queryLong.setParameter("idAula", id)).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(3L);

        Long result = dao.countByAula(id);

        assertEquals(3L, result);
    }

    @Test
    void countByAula_null() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.countByAula(null));
    }

    @Test
    void countByAula_exception() {
        when(em.createNamedQuery(anyString(), eq(Long.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.countByAula(UUID.randomUUID()));
    }
}