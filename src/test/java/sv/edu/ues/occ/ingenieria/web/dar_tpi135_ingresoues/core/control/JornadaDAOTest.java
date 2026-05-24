package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JornadaDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<Jornada> query;

    @Mock
    TypedQuery<Long> queryLong;

    @InjectMocks
    JornadaDAO dao;

    // ─────────────────────────────────────────────
    // getEntityManager
    // ─────────────────────────────────────────────
    @Nested
    class GetEntityManager {

        @Test
        void retornaEntityManager_cuandoEstaInyectado() {
            assertNotNull(dao.getEntityManager());
        }
    }

    // ─────────────────────────────────────────────
    // getEntityClass
    // ─────────────────────────────────────────────
    @Nested
    class GetEntityClass {

        @Test
        void retornaClaseJornada() {
            assertEquals(Jornada.class, dao.getEntityClass());
        }
    }

    // ─────────────────────────────────────────────
    // create
    // ─────────────────────────────────────────────
    @Nested
    class Create {

        @Test
        void invocaPersist_cuandoEntityEsValida() {
            Jornada entity = new Jornada();
            dao.create(entity);
            verify(em).persist(entity);
        }
    }

    // ─────────────────────────────────────────────
    // findByNombre
    // ─────────────────────────────────────────────
    @Nested
    class FindByNombre {

        @Test
        void lanzaIllegalArgumentException_cuandoNombreEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByNombre(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoNombreEsBlanco() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByNombre("   ", 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByNombre("test", -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCero() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByNombre("test", 0, 0));
        }

        @Test
        void retornaLista_cuandoParametrosSonValidos() {
            when(em.createNamedQuery(anyString(), eq(Jornada.class))).thenReturn(query);
            when(query.setParameter(anyString(), any())).thenReturn(query);
            when(query.setFirstResult(anyInt())).thenReturn(query);
            when(query.setMaxResults(anyInt())).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(new Jornada()));

            List<Jornada> result = dao.findByNombre("mañana", 0, 10);

            assertNotNull(result);
            assertEquals(1, result.size());

            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            verify(query).setParameter(eq("nombre"), captor.capture());
            assertEquals("%MAÑANA%", captor.getValue());
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(Jornada.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.findByNombre("test", 0, 10));
        }
    }

    // ─────────────────────────────────────────────
    // findByActivo
    // ─────────────────────────────────────────────
    @Nested
    class FindByActivo {

        @Test
        void lanzaIllegalArgumentException_cuandoActivoEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByActivo(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByActivo(true, -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCero() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByActivo(true, 0, 0));
        }

        @Test
        void retornaLista_cuandoParametrosSonValidos() {
            when(em.createNamedQuery(anyString(), eq(Jornada.class))).thenReturn(query);
            when(query.setParameter(anyString(), any())).thenReturn(query);
            when(query.setFirstResult(anyInt())).thenReturn(query);
            when(query.setMaxResults(anyInt())).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(new Jornada()));

            List<Jornada> result = dao.findByActivo(true, 0, 10);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(Jornada.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.findByActivo(true, 0, 10));
        }
    }

    // ─────────────────────────────────────────────
    // countByNombre
    // ─────────────────────────────────────────────
    @Nested
    class CountByNombre {

        @Test
        void lanzaIllegalArgumentException_cuandoNombreEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.countByNombre(null));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoNombreEsBlanco() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.countByNombre("   "));
        }

        @Test
        void retornaConteo_cuandoNombreEsValido() {
            when(em.createNamedQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
            when(queryLong.setParameter(anyString(), any())).thenReturn(queryLong);
            when(queryLong.getSingleResult()).thenReturn(5L);

            Long result = dao.countByNombre("test");

            assertEquals(5L, result);
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(Long.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.countByNombre("test"));
        }
    }

    // ─────────────────────────────────────────────
    // countByActivo
    // ─────────────────────────────────────────────
    @Nested
    class CountByActivo {

        @Test
        void lanzaIllegalArgumentException_cuandoActivoEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.countByActivo(null));
        }

        @Test
        void retornaConteo_cuandoActivoEsValido() {
            when(em.createNamedQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
            when(queryLong.setParameter(anyString(), any())).thenReturn(queryLong);
            when(queryLong.getSingleResult()).thenReturn(3L);

            Long result = dao.countByActivo(true);

            assertEquals(3L, result);
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(Long.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.countByActivo(true));
        }
    }
}