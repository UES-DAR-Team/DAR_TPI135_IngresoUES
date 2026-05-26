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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspirante;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JornadaAulaAspiranteDAOTest {

    private JornadaAulaAspiranteDAO dao;
    private EntityManager em;
    private TypedQuery<JornadaAulaAspirante> query;
    private TypedQuery<Long> queryLong;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() throws Exception {
        dao = new JornadaAulaAspiranteDAO();
        em = mock(EntityManager.class);
        query = mock(TypedQuery.class);
        queryLong = mock(TypedQuery.class);

        // Inyectamos el mock directamente en el campo via reflexión
        // para que create() y getEntityManager() funcionen correctamente
        var field = JornadaAulaAspiranteDAO.class.getDeclaredField("em");
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
        void retornaClaseJornadaAulaAspirante() {
            assertEquals(JornadaAulaAspirante.class, dao.getEntityClass());
        }
    }

    @Nested
    class Create {

        @Test
        void invocaPersist_cuandoEntityEsValida() {
            JornadaAulaAspirante entity = new JornadaAulaAspirante();
            dao.create(entity);
            verify(em).persist(entity);
        }
    }

    @Nested
    class FindByJornadaAula {

        @Test
        void lanzaIllegalArgumentException_cuandoIdEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByJornadaAula(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByJornadaAula(1, -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCero() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByJornadaAula(1, 0, 0));
        }

        @Test
        void retornaLista_cuandoParametrosSonValidos() {
            when(em.createNamedQuery("JornadaAulaAspirante.buscarPorJornadaAula", JornadaAulaAspirante.class))
                    .thenReturn(query);
            when(query.setParameter("idJornadaAula", 1)).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(new JornadaAulaAspirante()));

            List<JornadaAulaAspirante> result = dao.findByJornadaAula(1, 0, 10);

            assertFalse(result.isEmpty());
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.findByJornadaAula(1, 0, 10));
        }
    }

    @Nested
    class FindByAspirantePrueba {

        @Test
        void lanzaIllegalArgumentException_cuandoIdEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByAspirantePrueba(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByAspirantePrueba(1, -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCero() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByAspirantePrueba(1, 0, 0));
        }

        @Test
        void retornaLista_cuandoParametrosSonValidos() {
            when(em.createNamedQuery("JornadaAulaAspirante.buscarPorAspirantePrueba", JornadaAulaAspirante.class))
                    .thenReturn(query);
            when(query.setParameter("idAspirantePrueba", 1)).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(new JornadaAulaAspirante()));

            List<JornadaAulaAspirante> result = dao.findByAspirantePrueba(1, 0, 10);

            assertFalse(result.isEmpty());
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.findByAspirantePrueba(1, 0, 10));
        }
    }

    @Nested
    class FindByAsistencia {

        @Test
        void lanzaIllegalArgumentException_cuandoAsistioEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByAsistencia(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByAsistencia(true, -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCero() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByAsistencia(true, 0, 0));
        }

        @Test
        void retornaLista_cuandoParametrosSonValidos() {
            when(em.createNamedQuery("JornadaAulaAspirante.buscarPorAsistencia", JornadaAulaAspirante.class))
                    .thenReturn(query);
            when(query.setParameter("asistio", true)).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(new JornadaAulaAspirante()));

            List<JornadaAulaAspirante> result = dao.findByAsistencia(true, 0, 10);

            assertFalse(result.isEmpty());
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.findByAsistencia(true, 0, 10));
        }
    }

    @Nested
    class CountByJornadaAula {

        @Test
        void lanzaIllegalArgumentException_cuandoIdEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.countByJornadaAula(null));
        }

        @Test
        void retornaConteo_cuandoIdEsValido() {
            when(em.createNamedQuery("JornadaAulaAspirante.countByJornadaAula", Long.class))
                    .thenReturn(queryLong);
            when(queryLong.setParameter("idJornadaAula", 1)).thenReturn(queryLong);
            when(queryLong.getSingleResult()).thenReturn(5L);

            Long result = dao.countByJornadaAula(1);

            assertEquals(5L, result);
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(Long.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.countByJornadaAula(1));
        }
    }

    @Nested
    class CountByAsistencia {

        @Test
        void lanzaIllegalArgumentException_cuandoAsistioEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.countByAsistencia(null));
        }

        @Test
        void retornaConteo_cuandoAsistioEsValido() {
            when(em.createNamedQuery("JornadaAulaAspirante.countByAsistencia", Long.class))
                    .thenReturn(queryLong);
            when(queryLong.setParameter("asistio", true)).thenReturn(queryLong);
            when(queryLong.getSingleResult()).thenReturn(3L);

            Long result = dao.countByAsistencia(true);

            assertEquals(3L, result);
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(Long.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.countByAsistencia(true));
        }
    }
}