package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PreguntaAreaConocimiento;

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

    //=======================pruebas de busqueda por idAreaConocimiento=======================
    //metodo valido
    @Test
    void findPreguntaByIdAreaConocimientoValido(){
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

    //parametros invalidos/nulos
    @Test
    void findPreguntaByIdAreaConocimientoNulo() {
        List<PreguntaAreaConocimiento> resultado = dao.findPreguntaByIdAreaConocimiento(null, 0, 10);
        assertTrue(resultado.isEmpty());
    }

    //captura de excepciones
    @Test
    void findPreguntaByIdAreaConocimientoException() {
        when(em.createNamedQuery("PreguntaAreaConocimiento.findByIdAreaConocimiento", PreguntaAreaConocimiento.class))
                .thenThrow(new RuntimeException("DB Error"));
        List<PreguntaAreaConocimiento> resultado = dao.findPreguntaByIdAreaConocimiento(1, 0, 10);
        assertTrue(resultado.isEmpty());
        verify(em).createNamedQuery("PreguntaAreaConocimiento.findByIdAreaConocimiento", PreguntaAreaConocimiento.class);
    }

}