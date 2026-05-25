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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Turno;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnoDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Turno> query;

    @InjectMocks
    private TurnoDAO dao;

    private Turno turno;

    @BeforeEach
    void setUp() {
        turno = new Turno();
        turno.setId(UUID.randomUUID());
    }

    @Nested
    class FindByNameLike{

        @Test
        void retornaResultados_cuandoParametrosSonValidos() {
            when(em.createNamedQuery("Turno.findByNameLike", Turno.class))
                    .thenReturn(query);
            when(query.setParameter("name", "%TURNO%")).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(turno));

            List<Turno> resultado = dao.findByNameLike("turno", 0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(turno, resultado.getFirst());
            verify(em).createNamedQuery("Turno.findByNameLike", Turno.class);
            verify(query).setParameter("name", "%TURNO%");
            verify(query).setFirstResult(0);
            verify(query).setMaxResults(10);
            verify(query).getResultList();
        }

        @Test
        void lanzaIllegalArgumentException_cuandoNameEsNulo(){
            assertThrows(IllegalArgumentException.class, () -> dao.findByNameLike(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoNameEsBlancoOVacio(){
            assertThrows(IllegalArgumentException.class, () -> dao.findByNameLike("", 0, 10));
            assertThrows(IllegalArgumentException.class, () -> dao.findByNameLike("   ", 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstEsNegativo(){
            assertThrows(IllegalArgumentException.class, () -> dao.findByNameLike("turno", -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxEsNegativoOCero(){
            assertThrows(IllegalArgumentException.class, () -> dao.findByNameLike("turno", 0, -1));
            assertThrows(IllegalArgumentException.class, () -> dao.findByNameLike("turno", 0, 0));
        }

        @Test
        void lanzaIllegalStateException_cuandoJpaFalla(){
            when(em.createNamedQuery("Turno.findByNameLike", Turno.class))
                    .thenThrow(new RuntimeException("Error de sistema"));
            assertThrows(IllegalStateException.class, () -> dao.findByNameLike("turno", 0, 10));
            verify(em).createNamedQuery("Turno.findByNameLike", Turno.class);
        }


    }

}