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

    private PreguntaDistractor pd;
    private UUID idPregunta;
    private UUID idDistractor;

    @BeforeEach
    void setUp() {
        pd = new PreguntaDistractor();
        pd.setId(1);
        idPregunta = UUID.randomUUID();
        idDistractor = UUID.randomUUID();
    }

    @Nested
    class FindByIdPregunta {
        @Test
        void testFindByIdPreguntaParametrosValido() {
            when(em.createNamedQuery("PreguntaDistractor.findByIdPregunta", PreguntaDistractor.class))
                    .thenReturn(query);
            when(query.setParameter("idPregunta", idPregunta)).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(pd));

            List<PreguntaDistractor> resultado = dao.findByIdPregunta(idPregunta, 0, 10);

            assertFalse(resultado.isEmpty());
            assertTrue(resultado.contains(pd));
            verify(em).createNamedQuery("PreguntaDistractor.findByIdPregunta", PreguntaDistractor.class);
            verify(query).setParameter("idPregunta", idPregunta);
            verify(query).setFirstResult(0);
            verify(query).setMaxResults(10);
            verify(query).getResultList();
        }

        @Test
        void lanzaIllegalArgumentExceptionCuandoIdPreguntaEsNulo() {
            assertThrows(IllegalArgumentException.class, () -> dao.findByIdPregunta(null, 0, 10));
        }


        @Test
        void lanzaIllegalArgumentExceptionCuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class, () -> dao.findByIdPregunta(idPregunta, -1, 10));
        }

        @Test
        void lanzaIllegalArgumentExceptionCuandoMaxEsNegativoOCero() {
            assertThrows(IllegalArgumentException.class, () -> dao.findByIdPregunta(idPregunta, 0, -1));
            assertThrows(IllegalArgumentException.class, () -> dao.findByIdPregunta(idPregunta, 0, 0));
        }

        @Test
        void lanzaIllegalStateExceptionCuandoQueryFalla() {
            when(em.createNamedQuery("PreguntaDistractor.findByIdPregunta", PreguntaDistractor.class))
                    .thenThrow(new RuntimeException("DB Error"));
            assertThrows(IllegalStateException.class, () -> dao.findByIdPregunta(idPregunta, 0, 10));
            verify(em).createNamedQuery("PreguntaDistractor.findByIdPregunta", PreguntaDistractor.class);
        }
    }

    @Nested
    class FindByIdDistractor {
        @Test
        void testFindByIdDistractorParametrosValido() {
            when(em.createNamedQuery("PreguntaDistractor.findByIdDistractor", PreguntaDistractor.class))
                    .thenReturn(query);
            when(query.setParameter("idDistractor", idDistractor)).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(pd));

            List<PreguntaDistractor> resultado = dao.findByIdDistractor(idDistractor, 0, 10);

            assertFalse(resultado.isEmpty());
            assertTrue(resultado.contains(pd));
            verify(em).createNamedQuery("PreguntaDistractor.findByIdDistractor", PreguntaDistractor.class);
            verify(query).setParameter("idDistractor", idDistractor);
            verify(query).setFirstResult(0);
            verify(query).setMaxResults(10);
            verify(query).getResultList();
        }

        @Test
        void lanzaIllegalArgumentExceptionCuandoIdDistractorEsNulo() {
            assertThrows(IllegalArgumentException.class, () -> dao.findByIdDistractor(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentExceptionCuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class, () -> dao.findByIdDistractor(idDistractor, -1, 10));
        }

        @Test
        void lanzaIllegalArgumentExceptionCuandoMaxEsNegativoOCero() {
            assertThrows(IllegalArgumentException.class, () -> dao.findByIdDistractor(idDistractor, 0, -1));
            assertThrows(IllegalArgumentException.class, () -> dao.findByIdDistractor(idDistractor, 0, 0));
        }

        @Test
        void lanzaIllegalStateExceptionCuandoQueryFalla() {
            when(em.createNamedQuery("PreguntaDistractor.findByIdDistractor", PreguntaDistractor.class))
                    .thenThrow(new RuntimeException("DB Error"));
            assertThrows(IllegalStateException.class, () -> dao.findByIdDistractor(idDistractor, 0, 10));
            verify(em).createNamedQuery("PreguntaDistractor.findByIdDistractor", PreguntaDistractor.class);
        }
    }

}