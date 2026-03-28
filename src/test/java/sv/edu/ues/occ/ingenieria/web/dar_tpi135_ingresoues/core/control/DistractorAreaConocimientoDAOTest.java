package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.DistractorAreaConocimiento;

import java.util.List;
import java.util.UUID;

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
        UUID idDistractor = UUID.fromString("218062e5-d905-47cb-96e9-d427c98dc284");

        when(em.createNamedQuery("DistractorAreaConocimiento.findByIdDistractor", DistractorAreaConocimiento.class))
                .thenReturn(query);
        when(query.setParameter("idDistractor", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(java.util.List.of(dac));

        var resultado = dao.findByIdDistractor(idDistractor, 0, 10);

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.contains(dac));
        verify(em).createNamedQuery("DistractorAreaConocimiento.findByIdDistractor", DistractorAreaConocimiento.class);
        verify(query).setParameter("idDistractor", idDistractor);
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
    void testFindByIdFirstNegativo(){
        UUID idDistractor = UUID.randomUUID();
        List<DistractorAreaConocimiento> resultado = dao.findByIdDistractor(idDistractor, -1, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdMaxNegativo(){
        UUID idDistractor = UUID.randomUUID();
        List<DistractorAreaConocimiento> resultado = dao.findByIdDistractor(idDistractor, 0, -1);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdDistractorException() {
        UUID idDistractor = UUID.randomUUID();

        when(em.createNamedQuery("DistractorAreaConocimiento.findByIdDistractor", DistractorAreaConocimiento.class))
                .thenThrow(new RuntimeException("DB Error"));
        List<DistractorAreaConocimiento> resultado = dao.findByIdDistractor(idDistractor, 0, 10);
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em).createNamedQuery("DistractorAreaConocimiento.findByIdDistractor", DistractorAreaConocimiento.class);
    }

    //===============pruebas para buscar por idAreaConocimiento=====================
    @Test
    void testFindByIdAreaConocimientoParametrosValido() {
        UUID idArea = UUID.randomUUID();

        when(em.createNamedQuery("DistractorAreaConocimiento.findByIdAreaConocimiento", DistractorAreaConocimiento.class))
                .thenReturn(query);
        when(query.setParameter("idAreaConocimiento", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(java.util.List.of(dac));

        var resultado = dao.findByIdAreaConocimiento(idArea, 0, 10);

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.contains(dac));
        verify(em).createNamedQuery("DistractorAreaConocimiento.findByIdAreaConocimiento", DistractorAreaConocimiento.class);
        verify(query).setParameter("idAreaConocimiento", idArea);
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
    void testFindByIdAreaConocimientoFirstNegativo() {
        UUID idArea = UUID.randomUUID();

        List<DistractorAreaConocimiento> resultado = dao.findByIdAreaConocimiento(idArea, -1, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdAreaConocimientoMaxNegativo() {
        UUID idArea = UUID.randomUUID();
        List<DistractorAreaConocimiento> resultado = dao.findByIdAreaConocimiento(idArea, 0, -1);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdAreaConocimientoException() {
        UUID idArea = UUID.randomUUID();
        when(em.createNamedQuery("DistractorAreaConocimiento.findByIdAreaConocimiento", DistractorAreaConocimiento.class))
                .thenThrow(new RuntimeException("DB Error"));
        List<DistractorAreaConocimiento> resultado = dao.findByIdAreaConocimiento(idArea, 0, 10);
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em).createNamedQuery("DistractorAreaConocimiento.findByIdAreaConocimiento", DistractorAreaConocimiento.class);
    }

}