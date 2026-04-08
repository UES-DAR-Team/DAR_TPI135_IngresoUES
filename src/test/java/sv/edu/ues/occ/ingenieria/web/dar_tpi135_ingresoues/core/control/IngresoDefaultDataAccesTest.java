package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngresoDefaultDataAccesTest {

    static class TestEntity {
        private Integer id;
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
    }

    @Mock
    EntityManager em;

    IngresoDefaultDataAcces<TestEntity, Integer> dao;

    @BeforeEach
    void setUp() {
        dao = daoConEntityManager(em);
    }

    private IngresoDefaultDataAcces<TestEntity, Integer> daoConEntityManager(EntityManager entityManager) {
        return new IngresoDefaultDataAcces<>(TestEntity.class) {
            @Override
            public EntityManager getEntityManager() { return entityManager; }
        };
    }

    @Nested
    class Create {

        @Test
        void lanzaIllegalArgumentException_cuandoObjetoEsNulo() {
            assertThrows(IllegalArgumentException.class, () -> dao.create(null));
        }

        @Test
        void lanzaIllegalStateException_cuandoEntityManagerEsNulo() {
            IngresoDefaultDataAcces<TestEntity, Integer> daoSinEm = daoConEntityManager(null);

            assertThrows(IllegalStateException.class, () -> daoSinEm.create(new TestEntity()));
        }

        @Test
        void invocaPersistYFlush_cuandoObjetoYEntityManagerSonValidos() {
            TestEntity entity = new TestEntity();

            dao.create(entity);

            verify(em).persist(entity);
            verify(em).flush();
        }

        @Test
        void envuelveEnIllegalStateException_cuandoPersistLanzaRuntimeException() {
            TestEntity entity = new TestEntity();
            doThrow(new RuntimeException("fallo de base de datos")).when(em).persist(entity);

            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> dao.create(entity));

            assertEquals("Error al acceder al repositorio", ex.getMessage());
        }

        @Test
        void relanzaIllegalArgumentException_cuandoPersistLaLanza() {
            TestEntity entity = new TestEntity();
            doThrow(new IllegalArgumentException("dato inválido")).when(em).persist(entity);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> dao.create(entity));

            assertEquals("dato inválido", ex.getMessage());
        }
    }

    @Nested
    class Delete {

        @Test
        void lanzaIllegalArgumentException_cuandoObjetoEsNulo() {
            assertThrows(IllegalArgumentException.class, () -> dao.delete(null));
        }

        @Test
        void lanzaIllegalStateException_cuandoEntityManagerEsNulo() {
            IngresoDefaultDataAcces<TestEntity, Integer> daoSinEm = daoConEntityManager(null);

            assertThrows(IllegalStateException.class, () -> daoSinEm.delete(new TestEntity()));
        }

        @Test
        void invocaMergeYRemove_cuandoObjetoYEntityManagerSonValidos() {
            TestEntity entity = new TestEntity();
            when(em.merge(entity)).thenReturn(entity);

            dao.delete(entity);

            verify(em).merge(entity);
            verify(em).remove(entity);
        }

        @Test
        void envuelveEnIllegalStateException_cuandoMergeLanzaRuntimeException() {
            TestEntity entity = new TestEntity();
            doThrow(new RuntimeException("fallo de base de datos")).when(em).merge(entity);

            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> dao.delete(entity));

            assertEquals("Error al acceder al repositorio", ex.getMessage());
        }

        @Test
        void relanzaIllegalArgumentException_cuandoMergeLaLanza() {
            TestEntity entity = new TestEntity();
            doThrow(new IllegalArgumentException("dato inválido")).when(em).merge(entity);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> dao.delete(entity));

            assertEquals("dato inválido", ex.getMessage());
        }
    }

    @Nested
    class Update {

        @Test
        void lanzaIllegalArgumentException_cuandoRegistroEsNulo() {
            assertThrows(IllegalArgumentException.class, () -> dao.update(null));
        }

        @Test
        void lanzaIllegalStateException_cuandoEntityManagerEsNulo() {
            IngresoDefaultDataAcces<TestEntity, Integer> daoSinEm = daoConEntityManager(null);

            assertThrows(IllegalStateException.class, () -> daoSinEm.update(new TestEntity()));
        }

        @Test
        void devuelveEntidadActualizada_cuandoMergeEsExitoso() {
            TestEntity original = new TestEntity();
            TestEntity actualizada = new TestEntity();
            when(em.merge(original)).thenReturn(actualizada);

            TestEntity resultado = dao.update(original);

            assertSame(actualizada, resultado);
            verify(em).merge(original);
        }

        @Test
        void envuelveEnIllegalStateException_cuandoMergeLanzaRuntimeException() {
            TestEntity original = new TestEntity();
            doThrow(new RuntimeException("fallo de base de datos")).when(em).merge(original);

            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> dao.update(original));

            assertEquals("Error al actualizar el registro", ex.getMessage());
        }

        @Test
        void envuelveEnIllegalStateException_cuandoMergeLanzaIllegalArgumentException() {
            TestEntity original = new TestEntity();
            doThrow(new IllegalArgumentException("argumento inválido")).when(em).merge(original);

            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> dao.update(original));

            assertEquals("Error al actualizar el registro", ex.getMessage());
        }
    }

    @Nested
    class FindRange {

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class, () -> dao.findRange(-1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCero() {
            assertThrows(IllegalArgumentException.class, () -> dao.findRange(0, 0));
        }

        @Test
        void lanzaIllegalStateException_cuandoEntityManagerEsNulo() {
            IngresoDefaultDataAcces<TestEntity, Integer> daoSinEm = daoConEntityManager(null);

            assertThrows(IllegalStateException.class, () -> daoSinEm.findRange(0, 10));
        }

        @Test
        void envuelveEnIllegalStateException_cuandoGetResultListFalla() {
            CriteriaBuilder cb = mock(CriteriaBuilder.class);
            CriteriaQuery<TestEntity> cq = mock(CriteriaQuery.class);
            Root<TestEntity> root = mock(Root.class);
            TypedQuery<TestEntity> tq = mock(TypedQuery.class);

            when(em.getCriteriaBuilder()).thenReturn(cb);
            when(cb.createQuery(TestEntity.class)).thenReturn(cq);
            when(cq.from(TestEntity.class)).thenReturn(root);
            when(cq.select(root)).thenReturn(cq);
            when(em.createQuery(cq)).thenReturn(tq);
            when(tq.setFirstResult(0)).thenReturn(tq);
            when(tq.setMaxResults(5)).thenReturn(tq);
            doThrow(new RuntimeException("timeout de BD")).when(tq).getResultList();

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> dao.findRange(0, 5));

            assertEquals("No se pudo acceder al repositorio", ex.getMessage());
        }

        @Test
        void retornaResultados_cuandoParametrosYEntityManagerSonValidos() {
            TestEntity entity = new TestEntity();
            CriteriaBuilder cb = mock(CriteriaBuilder.class);
            CriteriaQuery<TestEntity> cq = mock(CriteriaQuery.class);
            Root<TestEntity> root = mock(Root.class);
            TypedQuery<TestEntity> tq = mock(TypedQuery.class);

            when(em.getCriteriaBuilder()).thenReturn(cb);
            when(cb.createQuery(TestEntity.class)).thenReturn(cq);
            when(cq.from(TestEntity.class)).thenReturn(root);
            when(cq.select(root)).thenReturn(cq);
            when(em.createQuery(cq)).thenReturn(tq);
            when(tq.setFirstResult(5)).thenReturn(tq);
            when(tq.setMaxResults(10)).thenReturn(tq);
            when(tq.getResultList()).thenReturn(List.of(entity));

            List<TestEntity> resultado = dao.findRange(5, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(entity, resultado.get(0));
            verify(tq).setFirstResult(5);
            verify(tq).setMaxResults(10);
            verify(tq).getResultList();
        }
    }

    @Nested
    class Count {

        @Test
        void lanzaIllegalStateException_cuandoEntityManagerEsNulo() {
            IngresoDefaultDataAcces<TestEntity, Integer> daoSinEm = daoConEntityManager(null);

            assertThrows(IllegalStateException.class, daoSinEm::count);
        }

        @Test
        void envuelveEnIllegalStateException_cuandoGetSingleResultFalla() {
            CriteriaBuilder cb = mock(CriteriaBuilder.class);
            CriteriaQuery<Long> cq = mock(CriteriaQuery.class);
            Root<TestEntity> root = mock(Root.class);
            TypedQuery<Long> tq = mock(TypedQuery.class);

            when(em.getCriteriaBuilder()).thenReturn(cb);
            when(cb.createQuery(Long.class)).thenReturn(cq);
            when(cq.from(TestEntity.class)).thenReturn(root);
            when(cb.count(root)).thenReturn(mock(Expression.class));
            when(cq.select(any())).thenReturn(cq);
            when(em.createQuery(cq)).thenReturn(tq);
            doThrow(new RuntimeException("timeout de BD")).when(tq).getSingleResult();

            IllegalStateException ex = assertThrows(IllegalStateException.class, dao::count);

            assertEquals("No se pudo acceder al repositorio", ex.getMessage());
        }

        @Test
        void retornaConteoComoEntero_cuandoConsultaEsExitosa() {
            CriteriaBuilder cb = mock(CriteriaBuilder.class);
            CriteriaQuery<Long> cq = mock(CriteriaQuery.class);
            Root<TestEntity> root = mock(Root.class);
            TypedQuery<Long> tq = mock(TypedQuery.class);

            when(em.getCriteriaBuilder()).thenReturn(cb);
            when(cb.createQuery(Long.class)).thenReturn(cq);
            when(cq.from(TestEntity.class)).thenReturn(root);
            when(cb.count(root)).thenReturn(mock(Expression.class));
            when(cq.select(any())).thenReturn(cq);
            when(em.createQuery(cq)).thenReturn(tq);
            when(tq.getSingleResult()).thenReturn(7L);

            int resultado = dao.count();

            assertEquals(7, resultado);
            verify(tq).getSingleResult();
        }
    }

    @Nested
    class FindById {

        @Test
        void lanzaIllegalArgumentException_cuandoIdEsNulo() {
            assertThrows(IllegalArgumentException.class, () -> dao.findById(null));
        }

        @Test
        void lanzaIllegalStateException_cuandoEntityManagerEsNulo() {
            IngresoDefaultDataAcces<TestEntity, Integer> daoSinEm = daoConEntityManager(null);

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> daoSinEm.findById(1));

            assertEquals("Error al acceder al repositorio", ex.getMessage());
        }

        @Test
        void retornaEntidad_cuandoIdExisteEnRepositorio() {
            TestEntity entity = new TestEntity();
            when(em.find(TestEntity.class, 1)).thenReturn(entity);

            TestEntity resultado = dao.findById(1);

            assertSame(entity, resultado);
            verify(em).find(TestEntity.class, 1);
        }

        @Test
        void retornaNull_cuandoEntidadNoExisteEnRepositorio() {
            when(em.find(TestEntity.class, 99)).thenReturn(null);

            TestEntity resultado = dao.findById(99);

            assertNull(resultado);
            verify(em).find(TestEntity.class, 99);
        }

        @Test
        void envuelveEnIllegalStateException_cuandoFindLanzaRuntimeException() {
            doThrow(new RuntimeException("fallo de base de datos")).when(em).find(TestEntity.class, 1);

            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> dao.findById(1));

            assertEquals("Error al acceder al repositorio", ex.getMessage());
        }
    }
}