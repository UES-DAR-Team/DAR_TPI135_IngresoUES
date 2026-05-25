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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspiranteOpcion;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AspiranteOpcionDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<AspiranteOpcion> query;

    @InjectMocks
    private AspiranteOpcionDAO dao;

    private UUID idAspirante;
    private UUID idOpcion;
    private AspiranteOpcion aspiranteOpcion;

    @BeforeEach
    void setUp() {
        idAspirante = UUID.randomUUID();
        idOpcion = UUID.randomUUID();
        aspiranteOpcion = new AspiranteOpcion();
    }


    @Nested
    class FindOpcionByIdAspirante {

        @Test
        void retornaResultados_cuandoParametrosSonValidos() {
            when(em.createNamedQuery("AspiranteOpcion.findByIdAspirante", AspiranteOpcion.class))
                    .thenReturn(query);
            when(query.setParameter("idAspirante", idAspirante)).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(aspiranteOpcion));

            List<AspiranteOpcion> resultado = dao.findOpcionByIdAspirante(idAspirante, 0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(aspiranteOpcion, resultado.getFirst());
            assertTrue(resultado.contains(aspiranteOpcion));
            verify(em).createNamedQuery("AspiranteOpcion.findByIdAspirante", AspiranteOpcion.class);
            verify(query).setParameter("idAspirante", idAspirante);
            verify(query).setFirstResult(0);
            verify(query).setMaxResults(10);
            verify(query).getResultList();
        }

        @Test
        void lanzaIllegalArgumentException_cuandoIdAspiranteEsNulo() {
            assertThrows(IllegalArgumentException.class, () -> dao.findOpcionByIdAspirante(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstNegativo() {
            assertThrows(IllegalArgumentException.class, () -> dao.findOpcionByIdAspirante(idAspirante, -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxNegativoOCero() {
            assertThrows(IllegalArgumentException.class, () -> dao.findOpcionByIdAspirante(idAspirante, 0, -1));
            assertThrows(IllegalArgumentException.class, () -> dao.findOpcionByIdAspirante(idAspirante, 0, 0));
        }

        @Test
        void lanzaIllegalStateException_cuandoJpaFalla(){
            when(em.createNamedQuery("AspiranteOpcion.findByIdAspirante", AspiranteOpcion.class))
                    .thenThrow(new RuntimeException("Fallo de base de datos"));
            assertThrows(IllegalStateException.class, () -> dao.findOpcionByIdAspirante(idAspirante, 0, 10));
            verify(em).createNamedQuery("AspiranteOpcion.findByIdAspirante", AspiranteOpcion.class);
        }

    }

    @Nested
    class FindAspiranteByIdOpcion {
        @Test
        void retornaResultados_cuandoParametrosSonValidos() {
            when(em.createNamedQuery("AspiranteOpcion.findByIdOpcion", AspiranteOpcion.class))
                    .thenReturn(query);
            when(query.setParameter("idOpcion", idOpcion)).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(aspiranteOpcion));

            List<AspiranteOpcion> resultado = dao.findAspiranteByIdOpcion(idOpcion, 0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(aspiranteOpcion, resultado.getFirst());
            assertTrue(resultado.contains(aspiranteOpcion));
            verify(em).createNamedQuery("AspiranteOpcion.findByIdOpcion", AspiranteOpcion.class);
            verify(query).setParameter("idOpcion", idOpcion);
            verify(query).setFirstResult(0);
            verify(query).setMaxResults(10);
            verify(query).getResultList();
        }

         @Test
        void lanzaIllegalArgumentException_cuandoIdOpcionEsNulo() {
            assertThrows(IllegalArgumentException.class, () -> dao.findAspiranteByIdOpcion(null, 0, 10));
         }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstNegativo() {
            assertThrows(IllegalArgumentException.class, () -> dao.findAspiranteByIdOpcion(idOpcion, -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxNegativoOCero() {
            assertThrows(IllegalArgumentException.class, () -> dao.findAspiranteByIdOpcion(idOpcion, 0, -1));
            assertThrows(IllegalArgumentException.class, () -> dao.findAspiranteByIdOpcion(idOpcion, 0, 0));
        }

        @Test
        void lanzaIllegalStateException_cuandoJpaFalla(){
            when(em.createNamedQuery("AspiranteOpcion.findByIdOpcion", AspiranteOpcion.class))
                    .thenThrow(new RuntimeException("Fallo de base de datos"));
            assertThrows(IllegalStateException.class, () -> dao.findAspiranteByIdOpcion(idOpcion, 0, 10));
            verify(em).createNamedQuery("AspiranteOpcion.findByIdOpcion", AspiranteOpcion.class);
        }

    }

}