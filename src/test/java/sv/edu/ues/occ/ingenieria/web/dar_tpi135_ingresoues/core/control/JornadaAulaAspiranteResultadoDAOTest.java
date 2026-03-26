package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspiranteResultado;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JornadaAulaAspiranteResultadoDAOTest {

    private JornadaAulaAspiranteResultadoDAO dao;
    private EntityManager em;
    private TypedQuery<JornadaAulaAspiranteResultado> query;

    @BeforeEach
    void setUp() {
        dao = new JornadaAulaAspiranteResultadoDAO();
        em = mock(EntityManager.class);
        query = mock(TypedQuery.class);

        dao = spy(dao);
        doReturn(em).when(dao).getEntityManager();
    }

    @Test
    void testFindByJornadaAulaAspiranteSuccess() {
        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new JornadaAulaAspiranteResultado()));

        List<JornadaAulaAspiranteResultado> result =
                dao.findByJornadaAulaAspirante(1, 0, 10);

        assertNotNull(result);
    }

    @Test
    void testFindByJornadaAulaAspiranteInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            dao.findByJornadaAulaAspirante(null, 0, 10);
        });
    }

    @Test
    void testFindByJornadaAulaAspiranteException() {
        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class)))
                .thenThrow(RuntimeException.class);

        assertThrows(IllegalStateException.class, () -> {
            dao.findByJornadaAulaAspirante(1, 0, 10);
        });
    }

    @Test
    void testFindByAprobadoSuccess() {
        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new JornadaAulaAspiranteResultado()));

        List<JornadaAulaAspiranteResultado> result =
                dao.findByAprobado(true, 0, 10);

        assertNotNull(result);
    }

    @Test
    void testFindByAprobadoInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            dao.findByAprobado(null, 0, 10);
        });
    }

    @Test
    void testFindByAprobadoException() {
        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class)))
                .thenThrow(RuntimeException.class);

        assertThrows(IllegalStateException.class, () -> {
            dao.findByAprobado(true, 0, 10);
        });
    }

    @Test
    void testFindByRangoPuntajeSuccess() {
        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new JornadaAulaAspiranteResultado()));

        List<JornadaAulaAspiranteResultado> result =
                dao.findByRangoPuntaje(
                        new BigDecimal("10"),
                        new BigDecimal("20"),
                        0,
                        10
                );

        assertNotNull(result);
    }

    @Test
    void testFindByRangoPuntajeInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            dao.findByRangoPuntaje(null, new BigDecimal("10"), 0, 10);
        });
    }

    @Test
    void testFindByRangoPuntajeException() {
        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class)))
                .thenThrow(RuntimeException.class);

        assertThrows(IllegalStateException.class, () -> {
            dao.findByRangoPuntaje(
                    new BigDecimal("10"),
                    new BigDecimal("20"),
                    0,
                    10
            );
        });
    }
}