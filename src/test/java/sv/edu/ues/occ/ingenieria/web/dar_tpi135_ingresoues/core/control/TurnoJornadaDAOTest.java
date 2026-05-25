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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.TurnoJornada;

import javax.swing.text.html.parser.Entity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnoJornadaDAOTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<TurnoJornada> query;

    @InjectMocks
    private TurnoJornadaDAO dao;

    private UUID idTurno;
    private UUID idJornada;
    private TurnoJornada turnoJornada;

    @BeforeEach
    void setUp() {
        idTurno = UUID.randomUUID();
        idJornada = UUID.randomUUID();
        turnoJornada = new TurnoJornada();
    }


    @Nested
    class FindTurnoByIdJornada {

        @Test
        void retornaResultados_cuandoParametrosSonValidos() {
            when(em.createNamedQuery("TurnoJornada.findByIdJornada", TurnoJornada.class))
                    .thenReturn(query);
            when(query.setParameter("idJornada", idJornada)).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(turnoJornada));

            List<TurnoJornada> resultado = dao.findTurnoByIdJornada(idJornada, 0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(turnoJornada, resultado.getFirst());
            verify(em).createNamedQuery("TurnoJornada.findByIdJornada", TurnoJornada.class);
            verify(query).setParameter("idJornada", idJornada);
            verify(query).setFirstResult(0);
            verify(query).setMaxResults(10);
        }

        @Test
        void lanzaIllegalArgumentException_cuandoIdJornadaEsNulo(){
            assertThrows(IllegalArgumentException.class, () -> dao.findTurnoByIdJornada(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstNegativo(){
            assertThrows(IllegalArgumentException.class, () -> dao.findTurnoByIdJornada(idJornada, -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxNegativoOCero(){
            assertThrows(IllegalArgumentException.class, () -> dao.findTurnoByIdJornada(idJornada, 0, -1));
            assertThrows(IllegalArgumentException.class, () -> dao.findTurnoByIdJornada(idJornada, 0, 0));
        }

        @Test
        void lanzaIllegalStateException_cuandoJpaFalla(){
            when(em.createNamedQuery("TurnoJornada.findByIdJornada", TurnoJornada.class))
                    .thenThrow(new RuntimeException("Error de sistema en la ejecucion de query"));

            assertThrows(IllegalStateException.class, () -> dao.findTurnoByIdJornada(idJornada, 0, 10));
             verify(em).createNamedQuery("TurnoJornada.findByIdJornada", TurnoJornada.class);
        }
    }


    @Nested
    class FindJornadaByIdTurno {

        @Test
        void retornaResultados_cuandoParametrosSonValidos() {
            when(em.createNamedQuery("TurnoJornada.findByIdTurno", TurnoJornada.class))
                    .thenReturn(query);
            when(query.setParameter("idTurno", idTurno)).thenReturn(query);
            when(query.setFirstResult(0)).thenReturn(query);
            when(query.setMaxResults(10)).thenReturn(query);
            when(query.getResultList()).thenReturn(List.of(turnoJornada));

            List<TurnoJornada> resultado = dao.findJornadaByIdTurno(idTurno, 0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertSame(turnoJornada, resultado.getFirst());
            verify(em).createNamedQuery("TurnoJornada.findByIdTurno", TurnoJornada.class);
            verify(query).setParameter("idTurno", idTurno);
            verify(query).setFirstResult(0);
            verify(query).setMaxResults(10);
            verify(query).getResultList();
        }

        @Test
        void lanzaIllegalArgumentException_cuandoIdTurnoEsNulo(){
            assertThrows(IllegalArgumentException.class, () -> dao.findJornadaByIdTurno(null, 0, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoFirstNegativo(){
            assertThrows(IllegalArgumentException.class, () -> dao.findJornadaByIdTurno(idTurno, -1, 10));
        }

        @Test
        void lanzaIllegalArgumentException_cuandoMaxNegativoOCero(){
            assertThrows(IllegalArgumentException.class, () -> dao.findJornadaByIdTurno(idTurno, 0, -1));
            assertThrows(IllegalArgumentException.class, () -> dao.findJornadaByIdTurno(idTurno, 0, 0));
        }

        @Test
        void lanzaIllegalStateException_cuandoJpaFalla(){
            when(em.createNamedQuery("TurnoJornada.findByIdTurno", TurnoJornada.class))
                    .thenThrow(new RuntimeException("Error de sistema en la ejecucion de query"));

            assertThrows(IllegalStateException.class, () -> dao.findJornadaByIdTurno(idTurno, 0, 10));
             verify(em).createNamedQuery("TurnoJornada.findByIdTurno", TurnoJornada.class);
        }

    }





}