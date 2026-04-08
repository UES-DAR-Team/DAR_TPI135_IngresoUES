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

import java.util.List;
import java.util.UUID;

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
        distractor.setId(UUID.randomUUID());
    }


    @Test
    void retornaResulatados_cuandoParametrosSonValidos(){
        when(em.createNamedQuery("Distractor.findByCoincidenciaTexto", Distractor.class))
                .thenReturn(query);
        when(query.setParameter("text", "%TEXT%")).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(distractor));

        List<Distractor> resultado = dao.findByCoincidenciaTexto("text", 0, 10);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertSame(distractor, resultado.getFirst());
        assertTrue(resultado.contains(distractor));
        verify(em).createNamedQuery("Distractor.findByCoincidenciaTexto", Distractor.class);
        verify(query).setParameter("text", "%TEXT%");
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
        verify(query).getResultList();
    }

    @Test
    void lanzaIllegalArgumentException_cuandoTextoEsNulo() {
        assertThrows(IllegalArgumentException.class,()-> dao.findByCoincidenciaTexto(null, 0, 10));
    }

    @Test
    void lanzaIllegalArgumentException_cuandoTextoEsBlancoOVacio(){
        assertThrows(IllegalArgumentException.class,()-> dao.findByCoincidenciaTexto(" ", 0, 10));
        assertThrows(IllegalArgumentException.class, ()-> dao.findByCoincidenciaTexto("", 0, 10));
    }

    @Test
    void lanzaIllegalArgumentException_cuandoFirstEsNegativo(){
        assertThrows(IllegalArgumentException.class, ()-> dao.findByCoincidenciaTexto("text", -1, 10));
    }

    @Test
    void lanzaIllegalArgumentException_cuandoMaxEsCeroONegativo(){
        assertThrows(IllegalArgumentException.class, ()-> dao.findByCoincidenciaTexto("text", 0, 0));
        assertThrows(IllegalArgumentException.class, ()-> dao.findByCoincidenciaTexto("text", 0, -1));
    }

    @Test
    void lanzaIllegalStateException_cuandoJpaFalla(){
        when(em.createNamedQuery("Distractor.findByCoincidenciaTexto", Distractor.class))
                .thenThrow(new RuntimeException("fallo de base de datos"));
        assertThrows(IllegalStateException.class,()-> dao.findByCoincidenciaTexto("text", 0, 10));
        verify(em).createNamedQuery("Distractor.findByCoincidenciaTexto", Distractor.class);
    }

}