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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PreguntaAreaConocimiento;

import java.util.List;
import java.util.UUID;

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

    private UUID idAreaConocimiento;
    private UUID idPregunta;
    private PreguntaAreaConocimiento pac;

    @BeforeEach
    void setUp() {
        idAreaConocimiento = UUID.randomUUID();
        idPregunta = UUID.randomUUID();
        pac = new PreguntaAreaConocimiento();
    }

    @Nested
    class FindByIdAreaConocimiento {

        @Test
        void testFindByIdAreaConocimientoParametrosValido() {
            when(em.createNamedQuery("PreguntaAreaConocimiento.findByIdAreaConocimiento", PreguntaAreaConocimiento.class))
                    .thenReturn(query);
            when(query.setParameter("idAreaConocimiento", idAreaConocimiento)).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(pac));

            List<PreguntaAreaConocimiento> resultado = dao.findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(pac, resultado.getFirst());
            assertTrue(resultado.contains(pac));
            verify(em).createNamedQuery("PreguntaAreaConocimiento.findByIdAreaConocimiento", PreguntaAreaConocimiento.class);
            verify(query).setParameter("idAreaConocimiento", idAreaConocimiento);
            verify(query).setFirstResult(0);
            verify(query).setMaxResults(10);
            verify(query).getResultList();
        }

        @Test
        void lanzaIllegalArgumentException_cuandoIdAreaConocimientoEsNulo() {
            assertThrows(IllegalArgumentException.class, () -> dao.findPreguntaByIdAreaConocimiento(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstNegativo() {
            assertThrows(IllegalArgumentException.class, () -> dao.findPreguntaByIdAreaConocimiento(idAreaConocimiento, -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxNegativoOCero() {
            assertThrows(IllegalArgumentException.class, () -> dao.findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, -1));
            assertThrows(IllegalArgumentException.class, () -> dao.findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, 0));
        }

        @Test
        void lanzaIllegalStateException_cuandoJpaFalla() {
            when(em.createNamedQuery("PreguntaAreaConocimiento.findByIdAreaConocimiento", PreguntaAreaConocimiento.class))
                    .thenThrow(new RuntimeException("Fallo de base de datos"));
            assertThrows(IllegalStateException.class, () -> dao.findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, 10));
            verify(em).createNamedQuery("PreguntaAreaConocimiento.findByIdAreaConocimiento", PreguntaAreaConocimiento.class);
        }

    }

    @Nested
    class FindByIdPregunta {
        @Test
        void testFindByIdPreguntaParametrosValido() {
            when(em.createNamedQuery("PreguntaAreaConocimiento.findByIdPregunta", PreguntaAreaConocimiento.class))
                    .thenReturn(query);
            when(query.setParameter("idPregunta", idPregunta)).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(pac));

            List<PreguntaAreaConocimiento> resultado = dao.findByIdPregunta(idPregunta, 0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(pac, resultado.getFirst());
            assertTrue(resultado.contains(pac));
            verify(em).createNamedQuery("PreguntaAreaConocimiento.findByIdPregunta", PreguntaAreaConocimiento.class);
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
        void lanzaIllegalStateException_cuandoJpaFalla() {
            when(em.createNamedQuery("PreguntaAreaConocimiento.findByIdPregunta", PreguntaAreaConocimiento.class))
                    .thenThrow(new RuntimeException("Fallo de base de datos"));
            assertThrows(IllegalStateException.class, () -> dao.findByIdPregunta(idPregunta, 0, 10));
            verify(em).createNamedQuery("PreguntaAreaConocimiento.findByIdPregunta", PreguntaAreaConocimiento.class);
        }

    }

}