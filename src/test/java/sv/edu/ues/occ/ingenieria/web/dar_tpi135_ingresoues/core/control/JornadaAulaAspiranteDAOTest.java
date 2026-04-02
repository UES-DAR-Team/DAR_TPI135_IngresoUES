package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspirante;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JornadaAulaAspiranteDAOTest {

    private JornadaAulaAspiranteDAO dao;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        dao = new JornadaAulaAspiranteDAO();
        em = mock(EntityManager.class);

        dao = Mockito.spy(dao);
        doReturn(em).when(dao).getEntityManager();
    }

    @Test
    void findByJornadaAula_ok() {
        UUID id = UUID.randomUUID();

        TypedQuery<JornadaAulaAspirante> q = mock(TypedQuery.class);

        when(em.createNamedQuery("JornadaAulaAspirante.buscarPorJornadaAula", JornadaAulaAspirante.class))
                .thenReturn(q);
        when(q.setParameter("idJornadaAula", id)).thenReturn(q);
        when(q.setFirstResult(0)).thenReturn(q);
        when(q.setMaxResults(10)).thenReturn(q);
        when(q.getResultList()).thenReturn(List.of(new JornadaAulaAspirante()));

        List<JornadaAulaAspirante> result = dao.findByJornadaAula(id, 0, 10);

        assertFalse(result.isEmpty());
    }

    @Test
    void findByJornadaAula_null() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByJornadaAula(null, 0, 10));
    }

    @Test
    void findByJornadaAula_paramInvalidos() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByJornadaAula(UUID.randomUUID(), -1, 10));

        assertThrows(IllegalArgumentException.class,
                () -> dao.findByJornadaAula(UUID.randomUUID(), 0, 0));
    }

    @Test
    void findByJornadaAula_exception() {
        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class)))
                .thenThrow(new RuntimeException());

        List<JornadaAulaAspirante> result =
                dao.findByJornadaAula(UUID.randomUUID(), 0, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByAspirantePrueba_ok() {
        Integer id = 1;

        TypedQuery<JornadaAulaAspirante> q = mock(TypedQuery.class);

        when(em.createNamedQuery("JornadaAulaAspirante.buscarPorAspirantePrueba", JornadaAulaAspirante.class))
                .thenReturn(q);
        when(q.setParameter("idAspirantePrueba", id)).thenReturn(q);
        when(q.setFirstResult(0)).thenReturn(q);
        when(q.setMaxResults(10)).thenReturn(q);
        when(q.getResultList()).thenReturn(List.of(new JornadaAulaAspirante()));

        List<JornadaAulaAspirante> result = dao.findByAspirantePrueba(id, 0, 10);

        assertFalse(result.isEmpty());
    }

    @Test
    void findByAspirantePrueba_null() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByAspirantePrueba(null, 0, 10));
    }

    @Test
    void findByAspirantePrueba_exception() {
        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class)))
                .thenThrow(new RuntimeException());

        List<JornadaAulaAspirante> result =
                dao.findByAspirantePrueba(1, 0, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByAsistencia_ok() {
        TypedQuery<JornadaAulaAspirante> q = mock(TypedQuery.class);

        when(em.createNamedQuery("JornadaAulaAspirante.buscarPorAsistencia", JornadaAulaAspirante.class))
                .thenReturn(q);
        when(q.setParameter("asistio", true)).thenReturn(q);
        when(q.setFirstResult(0)).thenReturn(q);
        when(q.setMaxResults(10)).thenReturn(q);
        when(q.getResultList()).thenReturn(List.of(new JornadaAulaAspirante()));

        List<JornadaAulaAspirante> result = dao.findByAsistencia(true, 0, 10);

        assertFalse(result.isEmpty());
    }

    @Test
    void findByAsistencia_null() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByAsistencia(null, 0, 10));
    }

    @Test
    void findByAsistencia_exception() {
        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class)))
                .thenThrow(new RuntimeException());

        List<JornadaAulaAspirante> result =
                dao.findByAsistencia(true, 0, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    void countByJornadaAula_ok() {
        TypedQuery<Long> q = mock(TypedQuery.class);

        when(em.createNamedQuery("JornadaAulaAspirante.countByJornadaAula", Long.class))
                .thenReturn(q);
        when(q.setParameter("idJornadaAula", 1)).thenReturn(q);
        when(q.getSingleResult()).thenReturn(5L);

        Long result = dao.countByJornadaAula(1);

        assertEquals(5L, result);
    }

    @Test
    void countByJornadaAula_null() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.countByJornadaAula(null));
    }

    @Test
    void countByJornadaAula_exception() {
        when(em.createNamedQuery(anyString(), eq(Long.class)))
                .thenThrow(new RuntimeException());

        Long result = dao.countByJornadaAula(1);

        assertEquals(0L, result);
    }

    @Test
    void countByAsistencia_ok() {
        TypedQuery<Long> q = mock(TypedQuery.class);

        when(em.createNamedQuery("JornadaAulaAspirante.countByAsistencia", Long.class))
                .thenReturn(q);
        when(q.setParameter("asistio", true)).thenReturn(q);
        when(q.getSingleResult()).thenReturn(3L);

        Long result = dao.countByAsistencia(true);

        assertEquals(3L, result);
    }

    @Test
    void countByAsistencia_null() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.countByAsistencia(null));
    }

    @Test
    void countByAsistencia_exception() {
        when(em.createNamedQuery(anyString(), eq(Long.class)))
                .thenThrow(new RuntimeException());

        Long result = dao.countByAsistencia(true);

        assertEquals(0L, result);
    }
}