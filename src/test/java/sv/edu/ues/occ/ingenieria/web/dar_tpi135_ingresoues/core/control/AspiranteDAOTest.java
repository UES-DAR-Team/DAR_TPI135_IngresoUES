package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AspiranteDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<Aspirante> query;

    AspiranteDAO dao;

    @BeforeEach
    void setup() throws Exception {
        dao = new AspiranteDAO();

        // 🔥 inyección REAL del EntityManager
        Field field = AspiranteDAO.class.getDeclaredField("em");
        field.setAccessible(true);
        field.set(dao, em);
    }

    @Test
    void debeRetornarLista_siParametrosValidos() {

        when(em.createNamedQuery(anyString(), eq(Aspirante.class)))
                .thenReturn(query);

        when(query.setParameter(anyString(), any()))
                .thenReturn(query);

        when(query.setFirstResult(anyInt()))
                .thenReturn(query);

        when(query.setMaxResults(anyInt()))
                .thenReturn(query);

        when(query.getResultList())
                .thenReturn(List.of(new Aspirante()));

        List<Aspirante> result = dao.buscarAspirantePorNombre("juan", 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void retornaVacio_siNombreNull() {
        assertTrue(dao.buscarAspirantePorNombre(null, 0, 10).isEmpty());
    }

    @Test
    void retornaVacio_siNombreBlank() {
        assertTrue(dao.buscarAspirantePorNombre("   ", 0, 10).isEmpty());
    }

    @Test
    void retornaVacio_siFirstNegativo() {
        assertTrue(dao.buscarAspirantePorNombre("juan", -1, 10).isEmpty());
    }

    @Test
    void retornaVacio_siMaxCero() {
        assertTrue(dao.buscarAspirantePorNombre("juan", 0, 0).isEmpty());
    }

    @Test
    void lanzaIllegalState_siFallaCreateNamedQuery() {

        when(em.createNamedQuery(anyString(), eq(Aspirante.class)))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarAspirantePorNombre("juan", 0, 10));
    }

    @Test
    void lanzaIllegalState_siFallaSetParameter() {

        when(em.createNamedQuery(anyString(), eq(Aspirante.class)))
                .thenReturn(query);

        when(query.setParameter(anyString(), any()))
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarAspirantePorNombre("juan", 0, 10));
    }

    @Test
    void lanzaIllegalState_siFallaGetResultList() {

        when(em.createNamedQuery(anyString(), eq(Aspirante.class)))
                .thenReturn(query);

        when(query.setParameter(anyString(), any()))
                .thenReturn(query);

        when(query.setFirstResult(anyInt()))
                .thenReturn(query);

        when(query.setMaxResults(anyInt()))
                .thenReturn(query);

        when(query.getResultList())
                .thenThrow(new RuntimeException());

        assertThrows(IllegalStateException.class,
                () -> dao.buscarAspirantePorNombre("juan", 0, 10));
    }
}