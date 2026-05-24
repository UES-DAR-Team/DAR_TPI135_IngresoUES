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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AspiranteDAOTest {

    @Mock
    EntityManager em;

    @InjectMocks
    AspiranteDAO dao;

    @SuppressWarnings("unchecked")
    private final TypedQuery<Aspirante> query = mock(TypedQuery.class);

    @SuppressWarnings("unchecked")
    private final TypedQuery<Long> queryCount = mock(TypedQuery.class);

    @Nested
    class GetEntityClass {

        @Test
        void retornaClaseAspirante() {
            assertEquals(Aspirante.class, dao.getEntityClass());
        }
    }

    @Nested
    class Create {

        @Test
        void invocaPersist_cuandoEntityEsValida() {
            Aspirante entity = new Aspirante();
            dao.create(entity);
            verify(em).persist(entity);
        }
    }

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
                    () -> dao.findByNombre("Juan", -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCero() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByNombre("Juan", 0, 0));
        }

        @Test
        void retornaLista_cuandoParametrosSonValidos() {
            when(em.createNamedQuery(anyString(), eq(Aspirante.class))).thenReturn(query);
            when(query.setParameter(anyString(), any())).thenReturn(query);
            when(query.setFirstResult(anyInt())).thenReturn(query);
            when(query.setMaxResults(anyInt())).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(new Aspirante()));

            List<Aspirante> result = dao.findByNombre("Juan", 0, 10);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(Aspirante.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.findByNombre("Juan", 0, 10));
        }
    }

    @Nested
    class FindActivos {

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findActivos(-1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCero() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findActivos(0, 0));
        }

        @Test
        void retornaLista_cuandoParametrosSonValidos() {
            when(em.createNamedQuery(anyString(), eq(Aspirante.class))).thenReturn(query);
            when(query.setFirstResult(anyInt())).thenReturn(query);
            when(query.setMaxResults(anyInt())).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(new Aspirante()));

            List<Aspirante> result = dao.findActivos(0, 10);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(Aspirante.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.findActivos(0, 10));
        }
    }

    @Nested
    class FindByDocumento {

        @Test
        void lanzaIllegalArgumentException_cuandoDocumentoEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByDocumento(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoDocumentoEsBlanco() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByDocumento("   ", 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByDocumento("12345678", -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCero() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByDocumento("12345678", 0, 0));
        }

        @Test
        void retornaLista_cuandoParametrosSonValidos() {
            when(em.createNamedQuery(anyString(), eq(Aspirante.class))).thenReturn(query);
            when(query.setParameter(anyString(), any())).thenReturn(query);
            when(query.setFirstResult(anyInt())).thenReturn(query);
            when(query.setMaxResults(anyInt())).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(new Aspirante()));

            List<Aspirante> result = dao.findByDocumento("12345678", 0, 10);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(Aspirante.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.findByDocumento("12345678", 0, 10));
        }
    }

    @Nested
    class FindByEstado {

        @Test
        void lanzaIllegalArgumentException_cuandoEstadoEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByEstado(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByEstado(true, -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCero() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByEstado(true, 0, 0));
        }

        @Test
        void retornaLista_cuandoParametrosSonValidos() {
            when(em.createNamedQuery(anyString(), eq(Aspirante.class))).thenReturn(query);
            when(query.setParameter(anyString(), any())).thenReturn(query);
            when(query.setFirstResult(anyInt())).thenReturn(query);
            when(query.setMaxResults(anyInt())).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(new Aspirante()));

            List<Aspirante> result = dao.findByEstado(true, 0, 10);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(Aspirante.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.findByEstado(true, 0, 10));
        }
    }

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
                    () -> dao.countByNombre(""));
        }

        @Test
        void retornaConteo_cuandoNombreEsValido() {
            when(em.createNamedQuery(anyString(), eq(Long.class))).thenReturn(queryCount);
            when(queryCount.setParameter(anyString(), any())).thenReturn(queryCount);
            when(queryCount.getSingleResult()).thenReturn(1L);

            Long result = dao.countByNombre("Juan");

            assertEquals(1L, result);
        }

        @Test
        void lanzaIllegalStateException_cuandoQueryFalla() {
            when(em.createNamedQuery(anyString(), eq(Long.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(IllegalStateException.class,
                    () -> dao.countByNombre("Juan"));
        }
    }
}