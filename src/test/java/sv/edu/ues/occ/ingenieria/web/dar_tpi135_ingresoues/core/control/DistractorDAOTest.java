package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DistractorDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    TypedQuery<Distractor> query;

    @InjectMocks
    DistractorDAO dao;

    private Distractor distractor;

    @BeforeEach
    void setUp() {
        distractor = new Distractor();
        distractor.setId(1);
    }

    //=======================================pruebas de coincidencia de texto=========================================

    //prueba de coincidencia de texto valido
    @Test
    void findByCoincidenciaTexto(){
        when(em.createNamedQuery("Distractor.findByCoincidenciaTexto", Distractor.class))
                .thenReturn(query);
        when(query.setParameter("text", "%TEXT%")).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(distractor));

        List<Distractor> resultado = dao.findByCoincidenciaTexto("text", 0, 10);

        assertTrue(resultado.contains(distractor));
        verify(em).createNamedQuery("Distractor.findByCoincidenciaTexto", Distractor.class);
        verify(query).setParameter("text", "%TEXT%");
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    //validacion de parametros
    //nulo
    @Test
    void findByCoincidenciaTextoNulo(){
        List<Distractor> resultado = dao.findByCoincidenciaTexto(null, 0, 10);
        assertTrue(resultado.isEmpty());
    }

    //vacio
    @Test
    void findByCoincidenciaTextVacio(){
        List<Distractor> resutlado = dao.findByCoincidenciaTexto(" ", 0, 10);
        assertTrue(resutlado.isEmpty());
    }

    //first negativo
    @Test
    void findByCoincidenciaTextoFirstNegativo(){
        List<Distractor> resultado = dao.findByCoincidenciaTexto("text", -1, 10);
        assertTrue(resultado.isEmpty());
    }

    //max negativo
    @Test
    void findByCoincidenciaTextoMaxNegativo(){
        List<Distractor> resultado =dao.findByCoincidenciaTexto("text", 10, -1);
        assertTrue(resultado.isEmpty());
    }

    //prueba de captura de excepcion
    @Test
    void findByCoincidenciaTextoExcepcion(){
        when(em.createNamedQuery("Distractor.findByCoincidenciaTexto", Distractor.class))
                .thenThrow(new RuntimeException("DB Error"));
        List<Distractor> resultado = dao.findByCoincidenciaTexto("text", 0, 10);
        assertTrue(resultado.isEmpty());
        verify(em).createNamedQuery("Distractor.findByCoincidenciaTexto", Distractor.class);
    }



















}