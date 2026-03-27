package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JornadaDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Jornada> query;

    @InjectMocks
    private JornadaDAO dao;

    @BeforeEach
    void setUp() {
    }

    @Test
    void debeRetornarLista_buscarPorNombre() {

        List<Jornada> lista = List.of(new Jornada());

        when(em.createNamedQuery(anyString(), eq(Jornada.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<Jornada> result = dao.buscarPorNombre("mañana", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());

        // 🔥 Validar que convierte a MAYÚSCULA y usa LIKE
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(query).setParameter(eq("nombre"), captor.capture());

        assertEquals("%MAÑANA%", captor.getValue());
    }

    @Test
    void retornaVacio_siNombreNull() {
        assertTrue(dao.buscarPorNombre(null, 0, 10).isEmpty());
    }

    @Test
    void retornaVacio_siNombreVacio() {
        assertTrue(dao.buscarPorNombre("   ", 0, 10).isEmpty());
    }

    @Test
    void retornaVacio_siFirstNegativo_buscarPorNombre() {
        assertTrue(dao.buscarPorNombre("test", -1, 10).isEmpty());
    }

    @Test
    void retornaVacio_siMaxInvalido_buscarPorNombre() {
        assertTrue(dao.buscarPorNombre("test", 0, 0).isEmpty());
    }

    @Test
    void lanzaIllegalState_siFallaCreateNamedQuery_buscarPorNombre() {

        when(em.createNamedQuery(anyString(), eq(Jornada.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorNombre("test", 0, 10));
    }

    @Test
    void lanzaIllegalState_siFallaGetResultList_buscarPorNombre() {

        when(em.createNamedQuery(anyString(), eq(Jornada.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorNombre("test", 0, 10));
    }

    @Test
    void debeRetornarLista_buscarPorActivo() {

        List<Jornada> lista = List.of(new Jornada());

        when(em.createNamedQuery(anyString(), eq(Jornada.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<Jornada> result = dao.buscarPorActivo(true, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void retornaVacio_siActivoNull() {
        assertTrue(dao.buscarPorActivo(null, 0, 10).isEmpty());
    }

    @Test
    void retornaVacio_siFirstNegativo_buscarPorActivo() {
        assertTrue(dao.buscarPorActivo(true, -1, 10).isEmpty());
    }

    @Test
    void retornaVacio_siMaxInvalido_buscarPorActivo() {
        assertTrue(dao.buscarPorActivo(true, 0, 0).isEmpty());
    }

    @Test
    void lanzaIllegalState_siFallaCreateNamedQuery_buscarPorActivo() {

        when(em.createNamedQuery(anyString(), eq(Jornada.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorActivo(true, 0, 10));
    }

    @Test
    void lanzaIllegalState_siFallaGetResultList_buscarPorActivo() {

        when(em.createNamedQuery(anyString(), eq(Jornada.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorActivo(true, 0, 10));
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