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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PreguntaDistractor;

import java.util.List;
import java.util.UUID;

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

    private UUID idPregunta;
    private UUID idDistractor;
    private PreguntaDistractor pd;

    @BeforeEach
    void setUp() {
        idPregunta = UUID.randomUUID();
        idDistractor = UUID.randomUUID();
        pd = new PreguntaDistractor();
    }

    @Nested
    class FindByIdPregunta {

        @Test
        void retornaResultados_cuandoParametrosSonValidos() {
            when(em.createNamedQuery("PreguntaDistractor.findByIdPregunta", PreguntaDistractor.class))
                    .thenReturn(query);
            when(query.setParameter("idPregunta", idPregunta)).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(pd));

            List<PreguntaDistractor> resultado = dao.findByIdPregunta(idPregunta, 0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(pd, resultado.getFirst());
            assertTrue(resultado.contains(pd));
            verify(em).createNamedQuery("PreguntaDistractor.findByIdPregunta", PreguntaDistractor.class);
            verify(query).setParameter("idPregunta", idPregunta);
            verify(query).setFirstResult(0);
            verify(query).setMaxResults(10);
            verify(query).getResultList();
        }

        @Test
        void lanzaIllegalArgumentException_cuandoIdPreguntaEsNulo() {
            assertThrows(IllegalArgumentException.class, () -> dao.findByIdPregunta(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstNegativo() {
            assertThrows(IllegalArgumentException.class, () -> dao.findByIdPregunta(idPregunta, -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxNegativoOCero() {
            assertThrows(IllegalArgumentException.class, () -> dao.findByIdPregunta(idPregunta, 0, -1));
            assertThrows(IllegalArgumentException.class, () -> dao.findByIdPregunta(idPregunta, 0, 0));
        }

        @Test
        void lanzaIllegalStateException_cuandoJpaFalla(){
            when(em.createNamedQuery("PreguntaDistractor.findByIdPregunta", PreguntaDistractor.class))
                    .thenThrow(new RuntimeException("Fallo de base de datos"));
            assertThrows(IllegalStateException.class, () -> dao.findByIdPregunta(idPregunta, 0, 10));
            verify(em).createNamedQuery("PreguntaDistractor.findByIdPregunta", PreguntaDistractor.class);
        }

    }

    @Nested
    class FindByIdDistractor {

        @Test
        void retornaResultados_cuandoParametrosSonValidos() {
            when(em.createNamedQuery("PreguntaDistractor.findByIdDistractor", PreguntaDistractor.class))
                    .thenReturn(query);
            when(query.setParameter("idDistractor", idDistractor)).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(pd));

            List<PreguntaDistractor> resultado = dao.findByIdDistractor(idDistractor, 0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(pd, resultado.getFirst());
            assertTrue(resultado.contains(pd));
            verify(em).createNamedQuery("PreguntaDistractor.findByIdDistractor", PreguntaDistractor.class);
            verify(query).setParameter("idDistractor", idDistractor);
            verify(query).setFirstResult(0);
            verify(query).setMaxResults(10);
            verify(query).getResultList();
        }

        @Test
        void lanzaIllegalArgumentException_cuandoIdDistractorEsNulo() {
            assertThrows(IllegalArgumentException.class, () -> dao.findByIdDistractor(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstNegativo() {
            assertThrows(IllegalArgumentException.class, () -> dao.findByIdDistractor(idDistractor, -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxNegativoOCero() {
            assertThrows(IllegalArgumentException.class, () -> dao.findByIdDistractor(idDistractor, 0, -1));
            assertThrows(IllegalArgumentException.class, () -> dao.findByIdDistractor(idDistractor, 0, 0));
        }

        @Test
        void lanzaIllegalStateException_cuandoJpaFalla(){
            when(em.createNamedQuery("PreguntaDistractor.findByIdDistractor", PreguntaDistractor.class))
                    .thenThrow(new RuntimeException("Fallo de base de datos"));
            assertThrows(IllegalStateException.class, () -> dao.findByIdDistractor(idDistractor, 0, 10));
            verify(em).createNamedQuery("PreguntaDistractor.findByIdDistractor", PreguntaDistractor.class);
        }
    }

}