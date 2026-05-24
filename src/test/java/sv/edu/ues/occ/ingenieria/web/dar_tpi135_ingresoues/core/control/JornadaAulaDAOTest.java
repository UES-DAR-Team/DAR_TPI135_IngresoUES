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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAula;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

    @Nested
    class GetEntityManager {

        @Test
        void retornaEntityManager_cuandoEstaInyectado() {
            assertNotNull(dao.getEntityManager());
        }

        @Test
        void lanzaIllegalStateException_cuandoEmEsNulo() {
            // DAO sin inyección para que em quede null
            JornadaAulaDAO daoSinEm = new JornadaAulaDAO();
            assertThrows(IllegalStateException.class, daoSinEm::getEntityManager);
        }
    }

    @Nested
    class GetEntityClass {

        @Test
        void retornaClaseJornadaAula() {
            assertEquals(JornadaAula.class, dao.getEntityClass());
        }
    }

    @Nested
    class Create {

        @Test
        void invocaPersist_cuandoEntityEsValida() {
            JornadaAula entity = new JornadaAula();
            dao.create(entity);
            verify(em).persist(entity);
        }
    }

    @Nested
    class FindByJornada {

        @Test
        void lanzaIllegalArgumentException_cuandoIdEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByJornada(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByJornada(UUID.randomUUID(), -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCero() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByJornada(UUID.randomUUID(), 0, 0));
        }

        @Test
        void retornaLista_cuandoParametrosSonValidos() {
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
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(JornadaAula.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.findByJornada(UUID.randomUUID(), 0, 10));
        }
    }

    @Nested
    class FindByAula {

        @Test
        void lanzaIllegalArgumentException_cuandoIdEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByAula(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByAula(UUID.randomUUID(), -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCero() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByAula(UUID.randomUUID(), 0, 0));
        }

        @Test
        void retornaLista_cuandoParametrosSonValidos() {
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
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(JornadaAula.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.findByAula(UUID.randomUUID(), 0, 10));
        }
    }

    @Nested
    class CountByJornada {

        @Test
        void lanzaIllegalArgumentException_cuandoIdEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.countByJornada(null));
        }

        @Test
        void retornaConteo_cuandoIdEsValido() {
            UUID id = UUID.randomUUID();
            when(em.createNamedQuery("JornadaAula.countByJornada", Long.class)).thenReturn(queryLong);
            when(queryLong.setParameter("idJornada", id)).thenReturn(queryLong);
            when(queryLong.getSingleResult()).thenReturn(5L);

            Long result = dao.countByJornada(id);

            assertEquals(5L, result);
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(Long.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.countByJornada(UUID.randomUUID()));
        }
    }

    @Nested
    class CountByAula {

        @Test
        void lanzaIllegalArgumentException_cuandoIdEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.countByAula(null));
        }

        @Test
        void retornaConteo_cuandoIdEsValido() {
            UUID id = UUID.randomUUID();
            when(em.createNamedQuery("JornadaAula.countByAula", Long.class)).thenReturn(queryLong);
            when(queryLong.setParameter("idAula", id)).thenReturn(queryLong);
            when(queryLong.getSingleResult()).thenReturn(3L);

            Long result = dao.countByAula(id);

            assertEquals(3L, result);
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(Long.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.countByAula(UUID.randomUUID()));
        }
    }
}