package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspiranteResultado;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JornadaAulaAspiranteResultadoDAOTest {

    private JornadaAulaAspiranteResultadoDAO dao;
    private EntityManager em;

    @SuppressWarnings("unchecked")
    private TypedQuery<JornadaAulaAspiranteResultado> query = mock(TypedQuery.class);

    @SuppressWarnings("unchecked")
    private TypedQuery<Long> queryLong = mock(TypedQuery.class);

    @BeforeEach
    void setUp() throws Exception {
        dao = new JornadaAulaAspiranteResultadoDAO();
        em = mock(EntityManager.class);

        var field = JornadaAulaAspiranteResultadoDAO.class.getDeclaredField("em");
        field.setAccessible(true);
        field.set(dao, em);
    }

    @Nested
    class GetEntityManager {

        @Test
        void retornaEntityManager_cuandoEstaInyectado() {
            assertNotNull(dao.getEntityManager());
        }
    }

    @Nested
    class GetEntityClass {

        @Test
        void retornaClaseJornadaAulaAspiranteResultado() {
            assertEquals(JornadaAulaAspiranteResultado.class, dao.getEntityClass());
        }
    }

    @Nested
    class Create {

        @Test
        void invocaPersist_cuandoEntityEsValida() {
            JornadaAulaAspiranteResultado entity = new JornadaAulaAspiranteResultado();
            dao.create(entity);
            verify(em).persist(entity);
        }
    }

    @Nested
    class FindByJornadaAulaAspirante {

        @Test
        void lanzaIllegalArgumentException_cuandoIdEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByJornadaAulaAspirante(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByJornadaAulaAspirante(1, -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCero() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByJornadaAulaAspirante(1, 0, 0));
        }

        @Test
        void retornaLista_cuandoParametrosSonValidos() {
            when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class)))
                    .thenReturn(query);
            when(query.setParameter(anyString(), any())).thenReturn(query);
            when(query.setFirstResult(anyInt())).thenReturn(query);
            when(query.setMaxResults(anyInt())).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(new JornadaAulaAspiranteResultado()));

            List<JornadaAulaAspiranteResultado> result =
                    dao.findByJornadaAulaAspirante(1, 0, 10);

            assertNotNull(result);
            assertFalse(result.isEmpty());
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.findByJornadaAulaAspirante(1, 0, 10));
        }
    }

    @Nested
    class FindByAprobado {

        @Test
        void lanzaIllegalArgumentException_cuandoAprobadoEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByAprobado(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByAprobado(true, -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCero() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByAprobado(true, 0, 0));
        }

        @Test
        void retornaLista_cuandoParametrosSonValidos() {
            when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class)))
                    .thenReturn(query);
            when(query.setParameter(anyString(), any())).thenReturn(query);
            when(query.setFirstResult(anyInt())).thenReturn(query);
            when(query.setMaxResults(anyInt())).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(new JornadaAulaAspiranteResultado()));

            List<JornadaAulaAspiranteResultado> result = dao.findByAprobado(true, 0, 10);

            assertNotNull(result);
            assertFalse(result.isEmpty());
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.findByAprobado(true, 0, 10));
        }
    }

    @Nested
    class FindByRangoPuntaje {

        @Test
        void lanzaIllegalArgumentException_cuandoMinEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByRangoPuntaje(null, new BigDecimal("20"), 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByRangoPuntaje(new BigDecimal("10"), null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMinEsMayorQueMax() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByRangoPuntaje(
                            new BigDecimal("50"),
                            new BigDecimal("10"),
                            0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByRangoPuntaje(
                            new BigDecimal("10"),
                            new BigDecimal("20"),
                            -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxResultsEsCero() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByRangoPuntaje(
                            new BigDecimal("10"),
                            new BigDecimal("20"),
                            0, 0));
        }

        @Test
        void retornaLista_cuandoParametrosSonValidos() {
            when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class)))
                    .thenReturn(query);
            when(query.setParameter(anyString(), any())).thenReturn(query);
            when(query.setFirstResult(anyInt())).thenReturn(query);
            when(query.setMaxResults(anyInt())).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(new JornadaAulaAspiranteResultado()));

            List<JornadaAulaAspiranteResultado> result = dao.findByRangoPuntaje(
                    new BigDecimal("10"),
                    new BigDecimal("20"),
                    0, 10);

            assertNotNull(result);
            assertFalse(result.isEmpty());
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(JornadaAulaAspiranteResultado.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.findByRangoPuntaje(
                            new BigDecimal("10"),
                            new BigDecimal("20"),
                            0, 10));
        }
    }

    @Nested
    class CountByAprobado {

        @Test
        void lanzaIllegalArgumentException_cuandoAprobadoEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.countByAprobado(null));
        }

        @Test
        void retornaConteo_cuandoAprobadoEsValido() {
            when(em.createNamedQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
            when(queryLong.setParameter(anyString(), any())).thenReturn(queryLong);
            when(queryLong.getSingleResult()).thenReturn(4L);

            Long result = dao.countByAprobado(true);

            assertEquals(4L, result);
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(Long.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.countByAprobado(true));
        }
    }
}