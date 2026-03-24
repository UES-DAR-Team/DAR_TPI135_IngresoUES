package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AreaConocimientoDAOTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<AreaConocimiento> query;

    @InjectMocks
    AreaConocimientoDAO dao;

    private AreaConocimiento area;

    @BeforeEach
    void setUp() {
        area = new AreaConocimiento();
        area.setId(1);
    }

    @Test
    void testFindByNameLikeNombreVacio() {
        List<AreaConocimiento> resultado = dao.findByNameLike("", 0, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByNameLikeNombreNulo() {
        List<AreaConocimiento> resultado = dao.findByNameLike(null, 0, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByNameLikeFirstNegativo() {
        List<AreaConocimiento> resultado = dao.findByNameLike("test", -1, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByNameLikeMaxNegativo() {
        List<AreaConocimiento> resultado = dao.findByNameLike("test", 0, -1);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByNameLikeParametrosValidos() {
        when(em.createNamedQuery("AreaConocimiento.findByNameLike", AreaConocimiento.class))
                .thenReturn(query);
        when(query.setParameter("name", "%TEST%")).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(area));
        List<AreaConocimiento> resultado = dao.findByNameLike("test", 0, 10);
        assertTrue(resultado.contains(area));
        verify(em).createNamedQuery("AreaConocimiento.findByNameLike", AreaConocimiento.class);
        verify(query).setParameter("name", "%TEST%");
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindByNameLikeException(){
        when(em.createNamedQuery("AreaConocimiento.findByNameLike", AreaConocimiento.class))
                .thenThrow(new RuntimeException("DB error"));
        List<AreaConocimiento> resultado = dao.findByNameLike("test", 0,10);
        assertTrue(resultado.isEmpty());
        verify(em).createNamedQuery("AreaConocimiento.findByNameLike", AreaConocimiento.class);
    }

    @Test
    void testFindByAreaPadreOk() {
        when(em.createNamedQuery("AreaConocimiento.findByAreaPadre", AreaConocimiento.class))
                .thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(area));
        List<AreaConocimiento> resultado = dao.findByAreaPadre();
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertSame(area, resultado.getFirst());

        verify(em).createNamedQuery("AreaConocimiento.findByAreaPadre", AreaConocimiento.class);
        verify(query).getResultList();
    }

    @Test
    void testFindByAreaPadreException(){
        when(em.createNamedQuery("AreaConocimiento.findByAreaPadre", AreaConocimiento.class))
                .thenThrow(new RuntimeException("DB error"));
        List<AreaConocimiento> resultado = dao.findByAreaPadre();
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em).createNamedQuery("AreaConocimiento.findByAreaPadre", AreaConocimiento.class);
    }

    @Test
    void testFindHijosByPadreParametrosValidos(){
        when(em.createNamedQuery("AreaConocimiento.findHijosByPadre", AreaConocimiento.class))
                .thenReturn(query);
        when(query.setParameter("idPadre", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(area));
        List<AreaConocimiento> resultado = dao.findHijosByPadre(1);
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertSame(area, resultado.getFirst());
    }

    @Test
    void testFindHijosByPadreParametrosNulos(){
        List<AreaConocimiento> resultado = dao.findHijosByPadre(null);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindHijosByPadreParametroNegativo(){
        List<AreaConocimiento> resultado = dao.findHijosByPadre(-1);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindHijosByPadreException(){
        when(em.createNamedQuery("AreaConocimiento.findHijosByPadre", AreaConocimiento.class))
                .thenThrow(new RuntimeException("DB error"));
        List<AreaConocimiento> resultado = dao.findHijosByPadre(1);
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em).createNamedQuery("AreaConocimiento.findHijosByPadre", AreaConocimiento.class);
    }

}