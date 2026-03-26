package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JornadaDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Jornada> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private JornadaDAO dao;

    @Test
    void debeRetornarLista_findByNombre() {

        List<Jornada> lista = List.of(new Jornada());

        when(em.createNamedQuery(anyString(), eq(Jornada.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<Jornada> result = dao.findByNombre("mañana", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(query).setParameter(eq("nombre"), captor.capture());

        assertEquals("%MAÑANA%", captor.getValue());
    }

    @Test
    void lanzaException_nombreNull_findByNombre() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNombre(null, 0, 10));
    }

    @Test
    void lanzaException_nombreVacio_findByNombre() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNombre("   ", 0, 10));
    }

    @Test
    void lanzaException_paginacionInvalida_findByNombre() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNombre("test", -1, 10));
    }

    @Test
    void lanzaIllegalState_findByNombre() {

        when(em.createNamedQuery(anyString(), eq(Jornada.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.findByNombre("test", 0, 10));
    }

    @Test
    void debeRetornarLista_findByActivo() {

        List<Jornada> lista = List.of(new Jornada());

        when(em.createNamedQuery(anyString(), eq(Jornada.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<Jornada> result = dao.findByActivo(true, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void lanzaException_activoNull_findByActivo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByActivo(null, 0, 10));
    }

    @Test
    void lanzaException_paginacionInvalida_findByActivo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByActivo(true, -1, 10));
    }

    @Test
    void lanzaIllegalState_findByActivo() {

        when(em.createNamedQuery(anyString(), eq(Jornada.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.findByActivo(true, 0, 10));
    }

    @Test
    void debeContarPorNombre() {

        when(em.createNamedQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.setParameter(anyString(), any())).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(5L);

        Long result = dao.countByNombre("test");

        assertEquals(5L, result);
    }

    @Test
    void lanzaException_nombreNull_countByNombre() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.countByNombre(null));
    }

    @Test
    void lanzaIllegalState_countByNombre() {

        when(em.createNamedQuery(anyString(), eq(Long.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.countByNombre("test"));
    }

    @Test
    void debeContarPorActivo() {

        when(em.createNamedQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.setParameter(anyString(), any())).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(3L);

        Long result = dao.countByActivo(true);

        assertEquals(3L, result);
    }

    @Test
    void lanzaException_activoNull_countByActivo() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.countByActivo(null));
    }

    @Test
    void lanzaIllegalState_countByActivo() {

        when(em.createNamedQuery(anyString(), eq(Long.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.countByActivo(true));
    }

    @Test
    void getEntityManager_noDebeSerNull() {
        assertNotNull(dao.getEntityManager());
    }

    @Test
    void getEntityClass_debeSerCorrecta() {
        assertEquals(Jornada.class, dao.getEntityClass());
    }
}