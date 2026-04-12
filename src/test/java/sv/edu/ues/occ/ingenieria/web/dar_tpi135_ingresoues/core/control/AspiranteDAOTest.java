package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AspiranteDAOTest {

    private AspiranteDAO dao;
    private EntityManager em;
    private TypedQuery<Aspirante> query;
    private TypedQuery<Long> queryCount;

    @BeforeEach
    void setUp() {
        dao = new AspiranteDAO();
        em = mock(EntityManager.class);
        query = mock(TypedQuery.class);
        queryCount = mock(TypedQuery.class);

        dao = spy(dao);
        doReturn(em).when(dao).getEntityManager();
    }

    @Test
    void testFindByNombreSuccess() {
        when(em.createNamedQuery(anyString(), eq(Aspirante.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Aspirante()));

        List<Aspirante> result = dao.findByNombre("Juan", 0, 10);

        assertNotNull(result);
    }

    @Test
    void testFindByNombreInvalidNombre() {
        assertThrows(IllegalArgumentException.class, () -> {
            dao.findByNombre(null, 0, 10);
        });
    }

    @Test
    void testFindByNombreInvalidPagination() {
        assertThrows(IllegalArgumentException.class, () -> {
            dao.findByNombre("Juan", -1, 10);
        });
    }

    @Test
    void testFindByNombreException() {
        when(em.createNamedQuery(anyString(), eq(Aspirante.class))).thenThrow(RuntimeException.class);

        assertThrows(IllegalStateException.class, () -> {
            dao.findByNombre("Juan", 0, 10);
        });
    }

    @Test
    void testFindActivosSuccess() {
        when(em.createNamedQuery(anyString(), eq(Aspirante.class))).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Aspirante()));

        List<Aspirante> result = dao.findActivos(0, 10);

        assertNotNull(result);
    }

    @Test
    void testFindByDocumentoSuccess() {
        when(em.createNamedQuery(anyString(), eq(Aspirante.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Aspirante()));

        List<Aspirante> result = dao.findByDocumento("123", 0, 10);

        assertNotNull(result);
    }

    @Test
    void testFindByEstadoSuccess() {
        when(em.createNamedQuery(anyString(), eq(Aspirante.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Aspirante()));

        List<Aspirante> result = dao.findByEstado(true, 0, 10);

        assertNotNull(result);
    }

    @Test
    void testCountByNombreSuccess() {
        when(em.createNamedQuery(anyString(), eq(Long.class))).thenReturn(queryCount);
        when(queryCount.setParameter(anyString(), any())).thenReturn(queryCount);
        when(queryCount.getSingleResult()).thenReturn(1L);

        Long result = dao.countByNombre("Juan");

        assertEquals(1L, result);
    }

    @Test
    void testCountByNombreInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            dao.countByNombre("");
        });
    }

    @Test
    void testCountByNombreException() {
        when(em.createNamedQuery(anyString(), eq(Long.class))).thenThrow(RuntimeException.class);

        assertThrows(IllegalStateException.class, () -> {
            dao.countByNombre("Juan");
        });
    }
}