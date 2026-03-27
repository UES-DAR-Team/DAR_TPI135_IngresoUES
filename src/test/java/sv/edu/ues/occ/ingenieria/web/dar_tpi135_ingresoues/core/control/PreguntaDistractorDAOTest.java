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
class PreguntaDistractorDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<PreguntaDistractor> query;

    @InjectMocks
    private PreguntaDistractorDAO dao;

    private PreguntaDistractor pd;

    @BeforeEach
    void setUp() {
        pd = new PreguntaDistractor();
        pd.setId(1);
    }

    @Test
    void testFindByIdPreguntaParametrosValido() {
        when(em.createNamedQuery("PreguntaDistractor.findByIdPregunta", PreguntaDistractor.class))
                .thenReturn(query);
        when(query.setParameter("idPregunta", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pd));

        List<PreguntaDistractor> resultado = dao.findByIdPregunta(1, 0, 10);

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.contains(pd));
        verify(em).createNamedQuery("PreguntaDistractor.findByIdPregunta", PreguntaDistractor.class);
        verify(query).setParameter("idPregunta", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindByIdPreguntaIdNulo() {
        List<PreguntaDistractor> resultado = dao.findByIdPregunta(null, 0, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdPreguntaIdNegativo() {
        List<PreguntaDistractor> resultado = dao.findByIdPregunta(-1, 0, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdPreguntaFirstNegativo() {
        List<PreguntaDistractor> resultado = dao.findByIdPregunta(1, -1, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdPreguntaMaxNegativo() {
        List<PreguntaDistractor> resultado = dao.findByIdPregunta(1, 0, -1);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdPreguntaException() {
        when(em.createNamedQuery("PreguntaDistractor.findByIdPregunta", PreguntaDistractor.class))
                .thenThrow(new RuntimeException("DB Error"));
        List<PreguntaDistractor> resultado = dao.findByIdPregunta(1, 0, 10);
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em).createNamedQuery("PreguntaDistractor.findByIdPregunta", PreguntaDistractor.class);
    }

    @Test
    void testFindByIdDistractorParametrosValido() {
        when(em.createNamedQuery("PreguntaDistractor.findByIdDistractor", PreguntaDistractor.class))
                .thenReturn(query);
        when(query.setParameter("idDistractor", 1)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pd));

        List<PreguntaDistractor> resultado = dao.findByIdDistractor(1, 0, 10);

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.contains(pd));
        verify(em).createNamedQuery("PreguntaDistractor.findByIdDistractor", PreguntaDistractor.class);
        verify(query).setParameter("idDistractor", 1);
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindByIdDistractorIdNulo() {
        List<PreguntaDistractor> resultado = dao.findByIdDistractor(null, 0, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdDistractorIdNegativo() {
        List<PreguntaDistractor> resultado = dao.findByIdDistractor(-1, 0, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdDistractorFirstNegativo() {
        List<PreguntaDistractor> resultado = dao.findByIdDistractor(1, -1, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdDistractorMaxNegativo() {
        List<PreguntaDistractor> resultado = dao.findByIdDistractor(1, 0, -1);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByIdDistractorException() {
        when(em.createNamedQuery("PreguntaDistractor.findByIdDistractor", PreguntaDistractor.class))
                .thenThrow(new RuntimeException("DB Error"));
        List<PreguntaDistractor> resultado = dao.findByIdDistractor(1, 0, 10);
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(em).createNamedQuery("PreguntaDistractor.findByIdDistractor", PreguntaDistractor.class);
    }

}