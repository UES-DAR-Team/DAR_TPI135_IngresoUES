package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspirantePrueba;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AspirantePruebaDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<AspirantePrueba> query;

    @Mock
    TypedQuery<Long> queryLong;

    @InjectMocks
    AspirantePruebaDAO dao;

    @Nested
    class GetEntityManager {

        @Test
        void retornaEntityManager_cuandoEstaInyectado() {
            assertNotNull(dao.getEntityManager());
        }

        @Test
        void lanzaIllegalStateException_cuandoEmEsNulo() {
            // Creamos el DAO sin @InjectMocks para que em quede null
            AspirantePruebaDAO daoSinEm = new AspirantePruebaDAO();
            assertThrows(IllegalStateException.class, daoSinEm::getEntityManager);
        }
    }

    @Nested
    class GetEntityClass {

        @Test
        void retornaClaseAspirantePrueba() {
            assertEquals(AspirantePrueba.class, dao.getEntityClass());
        }
    }

    @Nested
    class Create {

        @Test
        void invocaPersist_cuandoEntityEsValida() {
            AspirantePrueba entity = new AspirantePrueba();
            dao.create(entity);
            verify(em).persist(entity);
        }
    }

    @Nested
    class FindByAspirante {

        @Test
        void lanzaIllegalArgumentException_cuandoIdEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByAspirante(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByAspirante(UUID.randomUUID(), -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCero() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByAspirante(UUID.randomUUID(), 0, 0));
        }

        @Test
        void retornaLista_cuandoParametrosSonValidos() {
            UUID id = UUID.randomUUID();
            when(em.createNamedQuery(anyString(), eq(AspirantePrueba.class))).thenReturn(query);
            when(query.setParameter(anyString(), any())).thenReturn(query);
            when(query.setFirstResult(anyInt())).thenReturn(query);
            when(query.setMaxResults(anyInt())).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(new AspirantePrueba()));

            List<AspirantePrueba> result = dao.findByAspirante(id, 0, 10);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(AspirantePrueba.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.findByAspirante(UUID.randomUUID(), 0, 10));
        }
    }

    @Nested
    class FindByPrueba {

        @Test
        void lanzaIllegalArgumentException_cuandoIdEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByPrueba(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByPrueba(UUID.randomUUID(), -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCero() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByPrueba(UUID.randomUUID(), 0, 0));
        }

        @Test
        void retornaLista_cuandoParametrosSonValidos() {
            UUID id = UUID.randomUUID();
            when(em.createNamedQuery(anyString(), eq(AspirantePrueba.class))).thenReturn(query);
            when(query.setParameter(anyString(), any())).thenReturn(query);
            when(query.setFirstResult(anyInt())).thenReturn(query);
            when(query.setMaxResults(anyInt())).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(new AspirantePrueba()));

            List<AspirantePrueba> result = dao.findByPrueba(id, 0, 10);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(AspirantePrueba.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.findByPrueba(UUID.randomUUID(), 0, 10));
        }
    }

    @Nested
    class CountByAspirante {

        @Test
        void lanzaIllegalArgumentException_cuandoIdEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.countByAspirante(null));
        }

        @Test
        void retornaConteo_cuandoIdEsValido() {
            UUID id = UUID.randomUUID();
            when(em.createNamedQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
            when(queryLong.setParameter(anyString(), any())).thenReturn(queryLong);
            when(queryLong.getSingleResult()).thenReturn(3L);

            Long result = dao.countByAspirante(id);

            assertEquals(3L, result);
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(Long.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.countByAspirante(UUID.randomUUID()));
        }
    }
}