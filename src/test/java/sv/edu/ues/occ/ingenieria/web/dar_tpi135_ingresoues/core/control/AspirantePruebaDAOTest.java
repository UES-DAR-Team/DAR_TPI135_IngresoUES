package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspirantePrueba;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AspirantePruebaDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<AspirantePrueba> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private AspirantePruebaDAO dao;

    @Test
    void debeRetornarLista_findByAspirante() {
        UUID id = UUID.randomUUID();
        List<AspirantePrueba> lista = List.of(new AspirantePrueba());

        when(em.createNamedQuery(anyString(), eq(AspirantePrueba.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<AspirantePrueba> result = dao.findByAspirante(id, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void lanzaException_siIdNull_findByAspirante() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByAspirante(null, 0, 10));
    }

    @Test
    void lanzaException_siPaginacionInvalida_findByAspirante() {
        UUID id = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> dao.findByAspirante(id, -1, 10));

        assertThrows(IllegalArgumentException.class,
                () -> dao.findByAspirante(id, 0, 0));
    }

    @Test
    void lanzaIllegalState_siFallaQuery_findByAspirante() {
        UUID id = UUID.randomUUID();

        when(em.createNamedQuery(anyString(), eq(AspirantePrueba.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.findByAspirante(id, 0, 10));
    }

    @Test
    void debeRetornarLista_findByPrueba() {
        List<AspirantePrueba> lista = List.of(new AspirantePrueba());

        when(em.createNamedQuery(anyString(), eq(AspirantePrueba.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<AspirantePrueba> result = dao.findByPrueba(1, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void lanzaException_siIdNull_findByPrueba() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPrueba(null, 0, 10));
    }

    @Test
    void lanzaException_siPaginacionInvalida_findByPrueba() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPrueba(1, -1, 10));

        assertThrows(IllegalArgumentException.class,
                () -> dao.findByPrueba(1, 0, 0));
    }

    @Test
    void lanzaIllegalState_siFallaQuery_findByPrueba() {

        when(em.createNamedQuery(anyString(), eq(AspirantePrueba.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.findByPrueba(1, 0, 10));
    }



    @Test
    void lanzaException_siIdNull_countByAspirante() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.countByAspirante(null));
    }



    @Test
    void getEntityManager_noDebeSerNull() {
        assertNotNull(dao.getEntityManager());
    }

    @Test
    void getEntityClass_debeSerCorrecta() {
        assertEquals(AspirantePrueba.class, dao.getEntityClass());
    }
}