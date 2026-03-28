package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreguntaDAOTest {
    @Mock
    EntityManager em;

    @Mock
    TypedQuery<Pregunta> query;

    @InjectMocks
    PreguntaDAO dao;

    private Pregunta pregunta;

    @BeforeEach
    void setUp() {
        pregunta = new Pregunta();
        pregunta.setId(UUID.randomUUID());
    }

    @Test
    void testFindByCoincidenciaTextoValido() {
        when(em.createNamedQuery("Pregunta.findByCoincidenciaTexto", Pregunta.class))
                .thenReturn(query);
        when(query.setParameter("texto", "%TEXTO%")).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(pregunta));

        List<Pregunta> resultado = dao.findByCoincidenciaTexto("texto", 0, 10);

        assertTrue(resultado.contains(pregunta));
        verify(em).createNamedQuery("Pregunta.findByCoincidenciaTexto", Pregunta.class);
        verify(query).setParameter("texto", "%TEXTO%");
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void testFindByCoincidenciaTextoNulo(){
        List<Pregunta> resultado = dao.findByCoincidenciaTexto(null, 0, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByCoincidenciaTextoVacio(){
        List<Pregunta> resultado = dao.findByCoincidenciaTexto("", 0, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByCoincidenciaTextoFirstNegativo(){
        List<Pregunta> resultado = dao.findByCoincidenciaTexto("texto", -1, 10);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByCoincidenciaTextoMaxNegativo(){
        List<Pregunta> resultado = dao.findByCoincidenciaTexto("texto", 0, -1);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testFindByCoincidenciaTextoException() {
        when(em.createNamedQuery("Pregunta.findByCoincidenciaTexto", Pregunta.class))
                .thenThrow(new RuntimeException("DB error"));
        List<Pregunta> resultado = dao.findByCoincidenciaTexto("texto",0 , 10);
        assertTrue(resultado.isEmpty());
        verify(em).createNamedQuery("Pregunta.findByCoincidenciaTexto", Pregunta.class);
    }

}