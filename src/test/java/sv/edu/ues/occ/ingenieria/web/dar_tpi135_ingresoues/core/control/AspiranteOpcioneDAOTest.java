package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspiranteOpcione;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AspiranteOpcioneDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<AspiranteOpcione> query;

    @Mock
    private TypedQuery<Long> queryLong;

    @InjectMocks
    private AspiranteOpcioneDAO dao;

    @Test
    void debeRetornarLista_findByAspirante() {

        List<AspiranteOpcione> lista = List.of(new AspiranteOpcione());

        when(em.createNamedQuery(anyString(), eq(AspiranteOpcione.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<AspiranteOpcione> result =
                dao.findByAspirante(UUID.randomUUID(), 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void lanzaException_idNull_findByAspirante() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByAspirante(null, 0, 10));
    }

    @Test
    void debeRetornarLista_findByCodigoPrograma() {

        List<AspiranteOpcione> lista = List.of(new AspiranteOpcione());

        when(em.createNamedQuery(anyString(), eq(AspiranteOpcione.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<AspiranteOpcione> result =
                dao.findByCodigoPrograma("INF01", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void lanzaException_codigoNull_findByCodigoPrograma() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByCodigoPrograma(null, 0, 10));
    }

    @Test
    void debeRetornarLista_findByNombrePrograma() {

        List<AspiranteOpcione> lista = List.of(new AspiranteOpcione());

        when(em.createNamedQuery(anyString(), eq(AspiranteOpcione.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(lista);

        List<AspiranteOpcione> result =
                dao.findByNombrePrograma("ingenieria", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(query).setParameter(eq("nombrePrograma"), captor.capture());

        assertEquals("%INGENIERIA%", captor.getValue());
    }

    @Test
    void lanzaException_nombreNull_findByNombrePrograma() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.findByNombrePrograma(null, 0, 10));
    }

    @Test
    void debeContarPorAspirante() {

        when(em.createNamedQuery(anyString(), eq(Long.class))).thenReturn(queryLong);
        when(queryLong.setParameter(anyString(), any())).thenReturn(queryLong);
        when(queryLong.getSingleResult()).thenReturn(4L);

        Long result = dao.countByAspirante(UUID.randomUUID());

        assertEquals(4L, result);
    }

    @Test
    void lanzaException_null_countByAspirante() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.countByAspirante(null));
    }

    @Test
    void getEntityManager_noDebeSerNull() {
        assertNotNull(dao.getEntityManager());
    }

    @Test
    void getEntityClass_debeSerCorrecta() {
        assertEquals(AspiranteOpcione.class, dao.getEntityClass());
    }
}