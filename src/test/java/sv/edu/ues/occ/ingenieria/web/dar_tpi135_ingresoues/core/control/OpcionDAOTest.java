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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Opcion;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpcionDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Opcion> query;

    @InjectMocks
    private  OpcionDAO dao;

    private Opcion opcion;

    @BeforeEach
    void setUp() {
        opcion = new Opcion();
        opcion.setId(UUID.randomUUID());
    }

    @Nested
    class FindByNameLike {

        @Test
        void retornaResultados_cuandoParametrosSonValidos() {
            when(em.createNamedQuery("Opcion.findByNameLike", Opcion.class))
                    .thenReturn(query);
            when(query.setParameter("name", "%OPCION%")).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(opcion));

            List<Opcion> resultado = dao.findByNameLike("opcion", 0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(opcion, resultado.getFirst());
            verify(em).createNamedQuery("Opcion.findByNameLike", Opcion.class);
            verify(query).setParameter("name", "%OPCION%");
            verify(query).setFirstResult(0);
            verify(query).setMaxResults(10);
            verify(query).getResultList();
        }

        @Test
        void lanzaIllegalArgumentException_cuandoNameEsNulo() {
            assertThrows(IllegalArgumentException.class,()-> dao.findByNameLike(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoNameEsBlancoOVacio() {
            assertThrows(IllegalArgumentException.class,()-> dao.findByNameLike("", 0, 10));
            assertThrows(IllegalArgumentException.class,()-> dao.findByNameLike("   ", 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class,()-> dao.findByNameLike("opcion", -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCeroONegativo() {
            assertThrows(IllegalArgumentException.class,()-> dao.findByNameLike("opcion", 0, 0));
            assertThrows(IllegalArgumentException.class,()-> dao.findByNameLike("opcion", 0, -1));
        }

        @Test
        void lanzaIllegalStateException_cuandoJpaFalla(){
            when(em.createNamedQuery("Opcion.findByNameLike", Opcion.class)).thenThrow(new RuntimeException("Error de JPA"));

            assertThrows(IllegalStateException.class, () -> dao.findByNameLike("opcion", 0, 10));
            verify(em).createNamedQuery("Opcion.findByNameLike", Opcion.class);
        }

    }

    @Nested
    class FindByCodigoLike {

        @Test
        void retornaResultados_cuandoParametrosSonValidos() {
            when(em.createNamedQuery("Opcion.findByCodigoLike", Opcion.class))
                    .thenReturn(query);
            when(query.setParameter("codigo", "%CODIGO%")).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(opcion));

            List<Opcion> resultado = dao.findByCodigoLike("codigo", 0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(opcion, resultado.getFirst());
            verify(em).createNamedQuery("Opcion.findByCodigoLike", Opcion.class);
            verify(query).setParameter("codigo", "%CODIGO%");
            verify(query).setFirstResult(0);
            verify(query).setMaxResults(10);
            verify(query).getResultList();
        }

        @Test
        void lanzaIllegalArgumentException_cuandoCodigoEsNulo() {
            assertThrows(IllegalArgumentException.class,()-> dao.findByCodigoLike(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoCodigoEsBlancoOVacio() {
            assertThrows(IllegalArgumentException.class,()-> dao.findByCodigoLike("", 0, 10));
            assertThrows(IllegalArgumentException.class,()-> dao.findByCodigoLike("   ", 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo() {
            assertThrows(IllegalArgumentException.class,()-> dao.findByCodigoLike("codigo", -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsCeroONegativo() {
            assertThrows(IllegalArgumentException.class,()-> dao.findByCodigoLike("codigo", 0, 0));
            assertThrows(IllegalArgumentException.class,()-> dao.findByCodigoLike("codigo", 0, -1));
        }

        @Test
        void lanzaIllegalStateException_cuandoJpaFalla() {
            when(em.createNamedQuery("Opcion.findByCodigoLike", Opcion.class)).thenThrow(new RuntimeException("Error de JPA"));

            assertThrows(IllegalStateException.class, () -> dao.findByCodigoLike("codigo", 0, 10));
            verify(em).createNamedQuery("Opcion.findByCodigoLike", Opcion.class);
        }
    }

}