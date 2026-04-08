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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AreaConocimientoDAOTest {
    @Mock
    EntityManager em;

    @Mock
    TypedQuery<AreaConocimiento> query;

    @InjectMocks
    AreaConocimientoDAO dao;

    private AreaConocimiento areaConocimiento;

    @BeforeEach
    void setUp() {
        areaConocimiento = new AreaConocimiento();
        areaConocimiento.setId(UUID.randomUUID());
    }


    @Nested
    class FindByNameLike {

        @Test
        void retornaResultados_cuandoParametrosSonValidos() {
            when(em.createNamedQuery("AreaConocimiento.findByNameLike", AreaConocimiento.class))
                    .thenReturn(query);
            when(query.setParameter("name", "%MATEMATICA%")).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(areaConocimiento));

            List<AreaConocimiento> resultado = dao.findByNameLike("matematica", 0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(areaConocimiento, resultado.getFirst());
            verify(em).createNamedQuery("AreaConocimiento.findByNameLike", AreaConocimiento.class);
            verify(query).setParameter("name", "%MATEMATICA%");
            verify(query).setFirstResult(0);
            verify(query).setMaxResults(10);
            verify(query).getResultList();
        }

        @Test
        void lanzaIllegalArgumentException_cuandoNameEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByNameLike(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoNameEsBlancoOVacio() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByNameLike("   ", 0, 10));
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByNameLike("", 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByNameLike("matematica", -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCeroONegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByNameLike("matematica", 0, 0));
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findByNameLike("matematica", 0, -1));
        }

        @Test
        void lanzaIllegalStateException_cuandoJpaFalla() {
            when(em.createNamedQuery("AreaConocimiento.findByNameLike", AreaConocimiento.class))
                    .thenThrow(new RuntimeException("fallo de base de datos"));

            assertThrows(IllegalStateException.class,
                    () -> dao.findByNameLike("matematica", 0, 10));

            verify(em).createNamedQuery("AreaConocimiento.findByNameLike", AreaConocimiento.class);
        }
    }

    @Nested
    class FindByAreaPadre {

        @Test
        void retornaListaDeAreasPadre_cuandoConsultaEsExitosa() {
            when(em.createNamedQuery("AreaConocimiento.findByAreaPadre", AreaConocimiento.class))
                    .thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(areaConocimiento));

            List<AreaConocimiento> resultado = dao.findByAreaPadre();

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(areaConocimiento, resultado.getFirst());
            verify(em).createNamedQuery("AreaConocimiento.findByAreaPadre", AreaConocimiento.class);
            verify(query).getResultList();
        }

        @Test
        void lanzaIllegalStateException_cuandoJpaFalla() {
            when(em.createNamedQuery("AreaConocimiento.findByAreaPadre", AreaConocimiento.class))
                    .thenThrow(new RuntimeException("fallo de base de datos"));

            assertThrows(IllegalStateException.class, () -> dao.findByAreaPadre());

            verify(em).createNamedQuery("AreaConocimiento.findByAreaPadre", AreaConocimiento.class);
        }
    }

    @Nested
    class FindHijosByPadre {

        @Test
        void retornaHijos_cuandoIdPadreEsValido() {
            UUID idPadre = areaConocimiento.getId();
            when(em.createNamedQuery("AreaConocimiento.findHijosByPadre", AreaConocimiento.class))
                    .thenReturn(query);
            when(query.setParameter("idPadre", idPadre)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(areaConocimiento));

            List<AreaConocimiento> resultado = dao.findHijosByPadre(idPadre);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(areaConocimiento, resultado.getFirst());
            verify(em).createNamedQuery("AreaConocimiento.findHijosByPadre", AreaConocimiento.class);
            verify(query).setParameter("idPadre", idPadre);
            verify(query).getResultList();
        }

        @Test
        void lanzaIllegalArgumentException_cuandoIdPadreEsNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> dao.findHijosByPadre(null));
        }

        @Test
        void lanzaIllegalStateException_cuandoJpaFalla() {
            UUID idPadre = areaConocimiento.getId();
            when(em.createNamedQuery("AreaConocimiento.findHijosByPadre", AreaConocimiento.class))
                    .thenThrow(new RuntimeException("Fallo de base de datos"));

            assertThrows(IllegalStateException.class,
                    () -> dao.findHijosByPadre(idPadre));
            verify(em).createNamedQuery("AreaConocimiento.findHijosByPadre", AreaConocimiento.class);
        }
    }

}