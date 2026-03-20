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
        pregunta.setId(1);
    }

    //======================pruebas de coincidencia de texto========================
    //prueba de coincidencia de texto valido
    @Test
    void findByCoincidenciaTextoValido() {
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

    //validacion de parametro
    //nulo
    @Test
    void findByCoincidenciaTextoNulo(){
        List<Pregunta> resultado = dao.findByCoincidenciaTexto(null, 0, 10);
        assertTrue(resultado.isEmpty());
    }

    //vacio
    @Test
    void findByCoincidenciaTextoVacio(){
        List<Pregunta> resultado = dao.findByCoincidenciaTexto("", 0, 10);
        assertTrue(resultado.isEmpty());
    }

    // first negativo
    @Test
    void findByCoincidenciaTextoFirstNegativo(){
        List<Pregunta> resultado = dao.findByCoincidenciaTexto("texto", -1, 10);
        assertTrue(resultado.isEmpty());
    }

    // max negativo
    @Test
    void findByCoincidenciaTextoMaxNegativo(){
        List<Pregunta> resultado = dao.findByCoincidenciaTexto("texto", 0, -1);
        assertTrue(resultado.isEmpty());
    }

    // prueba de captura de excepcion
    @Test
    void findByCoincidenciaTextoException() {
        when(em.createNamedQuery("Pregunta.findByCoincidenciaTexto", Pregunta.class))
                .thenThrow(new RuntimeException("DB error"));
        List<Pregunta> resultado = dao.findByCoincidenciaTexto("texto",0 , 10);
        assertTrue(resultado.isEmpty());
        verify(em).createNamedQuery("Pregunta.findByCoincidenciaTexto", Pregunta.class);
    }


}