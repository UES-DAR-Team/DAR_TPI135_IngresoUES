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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DistractorAreaConocimientoDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<DistractorAreaConocimiento> query;

    @InjectMocks
    private DistractorAreaConocimientoDAO dao;

    private DistractorAreaConocimiento dac;

    @BeforeEach
    void setUp() {
        dac = new DistractorAreaConocimiento();
        dac.setId(1);
    }

    //===============pruebas para buscar por idDistractor===========================
    @Test
    void testFindByIdDistractorParametrosValido() {
        when(em.createNamedQuery("DistractorAreaConocimiento.findByIdDistractor", DistractorAreaConocimiento.class))
                .thenReturn(query);
        when(query.setParameter("idDistractor", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(java.util.List.of(dac));

        var resultado = dao.findByIdDistractor(1, 0, 10);

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.contains(dac));
        verify(em).createNamedQuery("DistractorAreaConocimiento.findByIdDistractor", DistractorAreaConocimiento.class);
        verify(query).setParameter("idDistractor", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindBiIdDistractorIdNulo() {
        var resultado = dao.findByIdDistractor(null, 0, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindBiIdDistractorIdNegativo() {
        var resultado = dao.findByIdDistractor(-1, 0, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdFirstNegativo(){
        List<DistractorAreaConocimiento> resultado = dao.findByIdDistractor(1, -1, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdMaxNegativo(){
        List<DistractorAreaConocimiento> resultado = dao.findByIdDistractor(1, 0, -1);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdDistractorException() {
        when(em.createNamedQuery("DistractorAreaConocimiento.findByIdDistractor", DistractorAreaConocimiento.class))
                .thenThrow(new RuntimeException("DB Error"));
        List<DistractorAreaConocimiento> resultado = dao.findByIdDistractor(1, 0, 10);
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em).createNamedQuery("DistractorAreaConocimiento.findByIdDistractor", DistractorAreaConocimiento.class);
    }

    //===============pruebas para buscar por idAreaConocimiento=====================
    @Test
    void testFindByIdAreaConocimientoParametrosValido() {
        when(em.createNamedQuery("DistractorAreaConocimiento.findByIdAreaConocimiento", DistractorAreaConocimiento.class))
                .thenReturn(query);
        when(query.setParameter("idAreaConocimiento", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(java.util.List.of(dac));

        var resultado = dao.findByIdAreaConocimiento(1, 0, 10);

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.contains(dac));
        verify(em).createNamedQuery("DistractorAreaConocimiento.findByIdAreaConocimiento", DistractorAreaConocimiento.class);
        verify(query).setParameter("idAreaConocimiento", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindByIdAreaConocimientoIdNulo() {
        var resultado = dao.findByIdAreaConocimiento(null, 0, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdAreaConocimientoIdNegativo() {
        var resultado = dao.findByIdAreaConocimiento(-1, 0, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdAreaConocimientoFirstNegativo() {
        List<DistractorAreaConocimiento> resultado = dao.findByIdAreaConocimiento(1, -1, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdAreaConocimientoMaxNegativo() {
        List<DistractorAreaConocimiento> resultado = dao.findByIdAreaConocimiento(1, 0, -1);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdAreaConocimientoException() {
        when(em.createNamedQuery("DistractorAreaConocimiento.findByIdAreaConocimiento", DistractorAreaConocimiento.class))
                .thenThrow(new RuntimeException("DB Error"));
        List<DistractorAreaConocimiento> resultado = dao.findByIdAreaConocimiento(1, 0, 10);
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em).createNamedQuery("DistractorAreaConocimiento.findByIdAreaConocimiento", DistractorAreaConocimiento.class);
    }

}