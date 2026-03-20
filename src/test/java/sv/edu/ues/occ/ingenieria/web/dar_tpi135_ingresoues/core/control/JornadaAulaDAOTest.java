package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAula;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JornadaAulaDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<JornadaAula> query;

    @InjectMocks
    private JornadaAulaDAO dao;

    @BeforeEach
    void setUp() {
    }

    @Test
    void debeRetornarLista_buscarPorJornada() {

        List<JornadaAula> lista = List.of(new JornadaAula());

        when(em.createNamedQuery(anyString(), eq(JornadaAula.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<JornadaAula> result = dao.buscarPorJornada(1, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void retornaVacio_siIdJornadaNull() {
        assertTrue(dao.buscarPorJornada(null, 0, 10).isEmpty());
    }

    @Test
    void retornaVacio_siFirstNegativo_buscarPorJornada() {
        assertTrue(dao.buscarPorJornada(1, -1, 10).isEmpty());
    }

    @Test
    void retornaVacio_siMaxInvalido_buscarPorJornada() {
        assertTrue(dao.buscarPorJornada(1, 0, 0).isEmpty());
    }

    @Test
    void lanzaIllegalState_siFallaCreateNamedQuery_buscarPorJornada() {

        when(em.createNamedQuery(anyString(), eq(JornadaAula.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorJornada(1, 0, 10));
    }

    @Test
    void lanzaIllegalState_siFallaGetResultList_buscarPorJornada() {

        when(em.createNamedQuery(anyString(), eq(JornadaAula.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorJornada(1, 0, 10));
    }

    @Test
    void debeRetornarLista_buscarPorAula() {

        List<JornadaAula> lista = List.of(new JornadaAula());

        when(em.createNamedQuery(anyString(), eq(JornadaAula.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<JornadaAula> result = dao.buscarPorAula(1, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void retornaVacio_siIdAulaNull() {
        assertTrue(dao.buscarPorAula(null, 0, 10).isEmpty());
    }

    @Test
    void retornaVacio_siFirstNegativo_buscarPorAula() {
        assertTrue(dao.buscarPorAula(1, -1, 10).isEmpty());
    }

    @Test
    void retornaVacio_siMaxInvalido_buscarPorAula() {
        assertTrue(dao.buscarPorAula(1, 0, 0).isEmpty());
    }

    @Test
    void lanzaIllegalState_siFallaCreateNamedQuery_buscarPorAula() {

        when(em.createNamedQuery(anyString(), eq(JornadaAula.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorAula(1, 0, 10));
    }

    @Test
    void lanzaIllegalState_siFallaGetResultList_buscarPorAula() {

        when(em.createNamedQuery(anyString(), eq(JornadaAula.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarPorAula(1, 0, 10));
    }

    @Test
    void getEntityManager_noDebeSerNull() {
        assertNotNull(dao.getEntityManager());
    }

    @Test
    void getEntityClass_debeSerCorrecta() {
        assertEquals(JornadaAula.class, dao.getEntityClass());
    }
}