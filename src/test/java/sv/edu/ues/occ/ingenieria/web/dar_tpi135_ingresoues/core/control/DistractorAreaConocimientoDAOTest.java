package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

    private UUID idDistractor;
    private UUID idAreaConocimiento;
    private DistractorAreaConocimiento dac;

    @BeforeEach
    void setUp() {
        idDistractor = UUID.randomUUID();
        idAreaConocimiento = UUID.randomUUID();
        dac = new DistractorAreaConocimiento();
    }

    @Nested
    class FindByIdDistractor {

        @Test
        void retornaResultados_cuandoParametrosSonValidos() {
            when(em.createNamedQuery("DistractorAreaConocimiento.findByIdDistractor", DistractorAreaConocimiento.class))
                    .thenReturn(query);
            when(query.setParameter("idDistractor", idDistractor)).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(dac));

            List<DistractorAreaConocimiento> resultado = dao.findByIdDistractor(idDistractor, 0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(dac, resultado.getFirst());

            verify(em).createNamedQuery("DistractorAreaConocimiento.findByIdDistractor", DistractorAreaConocimiento.class);
            verify(query).setParameter("idDistractor", idDistractor);
            verify(query).setFirstResult(0);
            verify(query).setMaxResults(10);
            verify(query).getResultList();
        }

        @Test
        void lanzaIllegalArgumentException_cuandoIdDistractorEsNulo(){
            assertThrows(IllegalArgumentException.class, ()->dao.findByIdDistractor(null,0,10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFistNegativo(){
            assertThrows(IllegalArgumentException.class, ()->dao.findByIdDistractor(idDistractor,-1,10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxNegativoOCero(){
            assertThrows(IllegalArgumentException.class, ()->dao.findByIdDistractor(idDistractor,0,0));
            assertThrows(IllegalArgumentException.class, ()->dao.findByIdDistractor(idDistractor,0,-1));
        }

        @Test
        void lanzaIllegalStateException_cuandoJpaFalla(){
            when(em.createNamedQuery("DistractorAreaConocimiento.findByIdDistractor", DistractorAreaConocimiento.class))
                    .thenThrow(new RuntimeException("Fallo de base de datos"));
            assertThrows(IllegalStateException.class, ()->dao.findByIdDistractor(idDistractor,0,10));
            verify(em).createNamedQuery("DistractorAreaConocimiento.findByIdDistractor", DistractorAreaConocimiento.class);
        }
    }

    @Nested
    class FindByIdAreaConocimiento {

        @Test
        void retornaResultados_cuandoParametrosSonValidos() {
            when(em.createNamedQuery("DistractorAreaConocimiento.findByIdAreaConocimiento", DistractorAreaConocimiento.class))
                    .thenReturn(query);
            when(query.setParameter("idAreaConocimiento", idAreaConocimiento)).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(dac));

            List<DistractorAreaConocimiento> resultado = dao.findByIdAreaConocimiento(idAreaConocimiento, 0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(dac, resultado.getFirst());

            verify(em).createNamedQuery("DistractorAreaConocimiento.findByIdAreaConocimiento", DistractorAreaConocimiento.class);
            verify(query).setParameter("idAreaConocimiento", idAreaConocimiento);
            verify(query).setFirstResult(0);
            verify(query).setMaxResults(10);
            verify(query).getResultList();
        }
        @Test
        void lanzaIllegalArgumentException_cuandoIdDistractorEsNulo(){
            assertThrows(IllegalArgumentException.class, ()->dao.findByIdAreaConocimiento(null,0,10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFistNegativo(){
            assertThrows(IllegalArgumentException.class, ()->dao.findByIdAreaConocimiento(idAreaConocimiento,-1,10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxNegativoOCero(){
            assertThrows(IllegalArgumentException.class, ()->dao.findByIdAreaConocimiento(idAreaConocimiento,0,0));
            assertThrows(IllegalArgumentException.class, ()->dao.findByIdAreaConocimiento(idAreaConocimiento,0,-1));
        }

        @Test
        void lanzaIllegalStateException_cuandoJpaFalla(){
            when(em.createNamedQuery("DistractorAreaConocimiento.findByIdAreaConocimiento", DistractorAreaConocimiento.class))
                    .thenThrow(new RuntimeException("Fallo de base de datos"));
            assertThrows(IllegalStateException.class, ()->dao.findByIdAreaConocimiento(idAreaConocimiento,0,10));
            verify(em).createNamedQuery("DistractorAreaConocimiento.findByIdAreaConocimiento", DistractorAreaConocimiento.class);
        }

    }

}