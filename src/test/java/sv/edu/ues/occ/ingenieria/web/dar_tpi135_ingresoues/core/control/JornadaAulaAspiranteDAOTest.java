package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspirante;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JornadaAulaAspiranteDAOTest {

    private JornadaAulaAspiranteDAO dao;
    private EntityManager em;
    private TypedQuery<JornadaAulaAspirante> query;

    @BeforeEach
    void setUp() {
        dao = new JornadaAulaAspiranteDAO();
        em = mock(EntityManager.class);
        query = mock(TypedQuery.class);

        dao = spy(dao);
        doReturn(em).when(dao).getEntityManager();
    }

    @Test
    void testFindByJornadaAulaSuccess() {
        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new JornadaAulaAspirante()));

        List<JornadaAulaAspirante> result = dao.findByJornadaAula(1, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testFindByJornadaAulaInvalidId() {
        assertThrows(IllegalArgumentException.class, () -> {
            dao.findByJornadaAula(null, 0, 10);
        });
    }

    @Test
    void testFindByJornadaAulaInvalidPagination() {
        assertThrows(IllegalArgumentException.class, () -> {
            dao.findByJornadaAula(1, -1, 10);
        });
    }

    @Test
    void testFindByJornadaAulaException() {
        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class)))
                .thenThrow(RuntimeException.class);

        assertThrows(IllegalStateException.class, () -> {
            dao.findByJornadaAula(1, 0, 10);
        });
    }

    @Test
    void testFindByAspirantePruebaSuccess() {
        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new JornadaAulaAspirante()));

        List<JornadaAulaAspirante> result = dao.findByAspirantePrueba(1, 0, 10);

        assertNotNull(result);
    }

    @Test
    void testFindByAspirantePruebaInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            dao.findByAspirantePrueba(null, 0, 10);
        });
    }

    @Test
    void testFindByAspirantePruebaException() {
        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class)))
                .thenThrow(RuntimeException.class);

        assertThrows(IllegalStateException.class, () -> {
            dao.findByAspirantePrueba(1, 0, 10);
        });
    }

    @Test
    void testFindByAsistenciaSuccess() {
        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new JornadaAulaAspirante()));

        List<JornadaAulaAspirante> result = dao.findByAsistencia(true, 0, 10);

        assertNotNull(result);
    }

    @Test
    void testFindByAsistenciaInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            dao.findByAsistencia(null, 0, 10);
        });
    }

    @Test
    void testFindByAsistenciaException() {
        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class)))
                .thenThrow(RuntimeException.class);

        assertThrows(IllegalStateException.class, () -> {
            dao.findByAsistencia(true, 0, 10);
        });
    }
}