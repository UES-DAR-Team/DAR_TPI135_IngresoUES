package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
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

    @InjectMocks
    private AspirantePruebaDAO dao;

    private UUID idAspirante;

    @BeforeEach
    void setUp() {
        idAspirante = UUID.randomUUID();
    }

    @Test
    void debeRetornarLista_buscarPorAspirante() {

        List<AspirantePrueba> lista = List.of(new AspirantePrueba());

        when(em.createNamedQuery(anyString(), eq(AspirantePrueba.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<AspirantePrueba> result = dao.buscarPorAspirante(idAspirante, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void retornaVacio_siIdAspiranteNull() {
        assertTrue(dao.buscarPorAspirante(null, 0, 10).isEmpty());
    }

    @Test
    void retornaVacio_siFirstNegativo_buscarPorAspirante() {
        assertTrue(dao.buscarPorAspirante(idAspirante, -1, 10).isEmpty());
    }

    @Test
    void retornaVacio_siMaxInvalido_buscarPorAspirante() {
        assertTrue(dao.buscarPorAspirante(idAspirante, 0, 0).isEmpty());
    }

    @Test
    void lanzaIllegalState_siFallaCreateNamedQuery_buscarPorAspirante() {

        when(em.createNamedQuery(anyString(), eq(AspirantePrueba.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorAspirante(idAspirante, 0, 10));
    }

    @Test
    void lanzaIllegalState_siFallaGetResultList_buscarPorAspirante() {

        when(em.createNamedQuery(anyString(), eq(AspirantePrueba.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorAspirante(idAspirante, 0, 10));
    }

    @Test
    void debeRetornarLista_buscarPorPrueba() {

        List<AspirantePrueba> lista = List.of(new AspirantePrueba());

        when(em.createNamedQuery(anyString(), eq(AspirantePrueba.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<AspirantePrueba> result = dao.buscarPorPrueba(1, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void retornaVacio_siIdPruebaNull() {
        assertTrue(dao.buscarPorPrueba(null, 0, 10).isEmpty());
    }

    @Test
    void retornaVacio_siFirstNegativo_buscarPorPrueba() {
        assertTrue(dao.buscarPorPrueba(1, -1, 10).isEmpty());
    }

    @Test
    void retornaVacio_siMaxInvalido_buscarPorPrueba() {
        assertTrue(dao.buscarPorPrueba(1, 0, 0).isEmpty());
    }

    @Test
    void lanzaIllegalState_siFallaCreateNamedQuery_buscarPorPrueba() {

        when(em.createNamedQuery(anyString(), eq(AspirantePrueba.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorPrueba(1, 0, 10));
    }

    @Test
    void lanzaIllegalState_siFallaGetResultList_buscarPorPrueba() {

        when(em.createNamedQuery(anyString(), eq(AspirantePrueba.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorPrueba(1, 0, 10));
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