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
class PreguntaAreaConocimientoDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<PreguntaAreaConocimiento> query;

    @InjectMocks
    private PreguntaAreaConocimientoDAO dao;

    private PreguntaAreaConocimiento pac;

    @BeforeEach
    void setUp() {
        pac = new PreguntaAreaConocimiento();
        pac.setId(1);
    }

    @Test
    void testFindPreguntaByIdAreaConocimientoValido(){
        when(em.createNamedQuery("PreguntaAreaConocimiento.findByIdAreaConocimiento", PreguntaAreaConocimiento.class))
                .thenReturn(query);
        when(query.setParameter("idAreaConocimiento", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pac));

        List<PreguntaAreaConocimiento> resultado = dao.findPreguntaByIdAreaConocimiento(1, 0, 10);

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.contains(pac));
        verify(em).createNamedQuery("PreguntaAreaConocimiento.findByIdAreaConocimiento", PreguntaAreaConocimiento.class);
        verify(query).setParameter("idAreaConocimiento", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testfindPreguntaByIdAreaConocimientoIdNulo() {
        List<PreguntaAreaConocimiento> resultado = dao.findPreguntaByIdAreaConocimiento(null, 0, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindPreguntaByIdAreaConocimientoIdNegativo() {
        List<PreguntaAreaConocimiento> resultado = dao.findPreguntaByIdAreaConocimiento(-1, 0, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindPreguntaByIdAreaConocimientoFirstNegativo() {
        List<PreguntaAreaConocimiento> resultado = dao.findPreguntaByIdAreaConocimiento(1, -1, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindPreguntaByIdAreaConocimientoIdMaxNegativo() {
        List<PreguntaAreaConocimiento> resultado = dao.findPreguntaByIdAreaConocimiento(1, 0, -1);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindPreguntaByIdAreaConocimientoException() {
        when(em.createNamedQuery("PreguntaAreaConocimiento.findByIdAreaConocimiento", PreguntaAreaConocimiento.class))
                .thenThrow(new RuntimeException("DB Error"));
        List<PreguntaAreaConocimiento> resultado = dao.findPreguntaByIdAreaConocimiento(1, 0, 10);
        assertTrue(resultado.isEmpty());
        verify(em).createNamedQuery("PreguntaAreaConocimiento.findByIdAreaConocimiento", PreguntaAreaConocimiento.class);
    }

    @Test
    void testFindByIdPreguntaValido() {
        when(em.createNamedQuery("PreguntaAreaConocimiento.findByIdPregunta", PreguntaAreaConocimiento.class))
                .thenReturn(query);
        when(query.setParameter("idPregunta", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pac));

        List<PreguntaAreaConocimiento> resultado = dao.findByIdPregunta(1, 0, 10);

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.contains(pac));
        verify(em).createNamedQuery("PreguntaAreaConocimiento.findByIdPregunta", PreguntaAreaConocimiento.class);
        verify(query).setParameter("idPregunta", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindByIdPreguntaIdNulo(){
        List<PreguntaAreaConocimiento> resultado = dao.findByIdPregunta(null, 10, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindBiIdPreguntaIdNegativo(){
        List<PreguntaAreaConocimiento> resultado = dao.findByIdPregunta(-1, 10, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdPreguntaFirstNegativo(){
        List<PreguntaAreaConocimiento> resultado = dao.findByIdPregunta(1, -1, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdPreguntaMaxNegativo() {
        List<PreguntaAreaConocimiento> resultado = dao.findByIdPregunta(1, 0, -1);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdPreguntaException() {
        when(em.createNamedQuery("PreguntaAreaConocimiento.findByIdPregunta", PreguntaAreaConocimiento.class))
                .thenThrow(new RuntimeException("DB Error"));
        List<PreguntaAreaConocimiento> resultado = dao.findByIdPregunta(1, 0, 10);
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em).createNamedQuery("PreguntaAreaConocimiento.findByIdPregunta", PreguntaAreaConocimiento.class);
    }


}