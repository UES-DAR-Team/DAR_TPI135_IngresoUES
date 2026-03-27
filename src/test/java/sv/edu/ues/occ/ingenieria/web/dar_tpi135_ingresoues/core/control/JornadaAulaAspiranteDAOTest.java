package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JornadaAulaAspiranteDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<JornadaAulaAspirante> query;

    @InjectMocks
    private JornadaAulaAspiranteDAO dao;

    @BeforeEach
    void setUp() {
    }

    @Test
    void debeRetornarLista_buscarPorJornadaAula() {

        List<JornadaAulaAspirante> lista = List.of(new JornadaAulaAspirante());

        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<JornadaAulaAspirante> result = dao.buscarPorJornadaAula(1, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void retornaVacio_siIdJornadaAulaNull() {
        assertTrue(dao.buscarPorJornadaAula(null, 0, 10).isEmpty());
    }

    @Test
    void retornaVacio_siFirstNegativo_buscarPorJornadaAula() {
        assertTrue(dao.buscarPorJornadaAula(1, -1, 10).isEmpty());
    }

    @Test
    void retornaVacio_siMaxInvalido_buscarPorJornadaAula() {
        assertTrue(dao.buscarPorJornadaAula(1, 0, 0).isEmpty());
    }

    @Test
    void lanzaIllegalState_siFallaCreateNamedQuery_buscarPorJornadaAula() {

        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorJornadaAula(1, 0, 10));
    }

    @Test
    void lanzaIllegalState_siFallaGetResultList_buscarPorJornadaAula() {

        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorJornadaAula(1, 0, 10));
    }

    @Test
    void debeRetornarLista_buscarPorAspirantePrueba() {

        List<JornadaAulaAspirante> lista = List.of(new JornadaAulaAspirante());

        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<JornadaAulaAspirante> result = dao.buscarPorAspirantePrueba(1, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void retornaVacio_siIdAspirantePruebaNull() {
        assertTrue(dao.buscarPorAspirantePrueba(null, 0, 10).isEmpty());
    }

    @Test
    void retornaVacio_siFirstNegativo_buscarPorAspirantePrueba() {
        assertTrue(dao.buscarPorAspirantePrueba(1, -1, 10).isEmpty());
    }

    @Test
    void retornaVacio_siMaxInvalido_buscarPorAspirantePrueba() {
        assertTrue(dao.buscarPorAspirantePrueba(1, 0, 0).isEmpty());
    }

    @Test
    void lanzaIllegalState_siFallaCreateNamedQuery_buscarPorAspirantePrueba() {

        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorAspirantePrueba(1, 0, 10));
    }

    @Test
    void lanzaIllegalState_siFallaGetResultList_buscarPorAspirantePrueba() {

        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorAspirantePrueba(1, 0, 10));
    }

    @Test
    void debeRetornarLista_buscarPorAsistencia() {

        List<JornadaAulaAspirante> lista = List.of(new JornadaAulaAspirante());

        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<JornadaAulaAspirante> result = dao.buscarPorAsistencia(true, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void retornaVacio_siAsistenciaNull() {
        assertTrue(dao.buscarPorAsistencia(null, 0, 10).isEmpty());
    }

    @Test
    void retornaVacio_siFirstNegativo_buscarPorAsistencia() {
        assertTrue(dao.buscarPorAsistencia(true, -1, 10).isEmpty());
    }

    @Test
    void retornaVacio_siMaxInvalido_buscarPorAsistencia() {
        assertTrue(dao.buscarPorAsistencia(true, 0, 0).isEmpty());
    }

    @Test
    void lanzaIllegalState_siFallaCreateNamedQuery_buscarPorAsistencia() {

        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorAsistencia(true, 0, 10));
    }

    @Test
    void lanzaIllegalState_siFallaGetResultList_buscarPorAsistencia() {

        when(em.createNamedQuery(anyString(), eq(JornadaAulaAspirante.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorAsistencia(true, 0, 10));
    }

    @Test
    void getEntityManager_noDebeSerNull() {
        assertNotNull(dao.getEntityManager());
    }

    @Test
    void getEntityClass_debeSerCorrecta() {
        assertEquals(JornadaAulaAspirante.class, dao.getEntityClass());
    }
}