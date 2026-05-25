package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.TurnoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.TurnoJornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Turno;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.TurnoJornada;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurnoJornadaResourceTest {

    @Mock
    TurnoJornadaDAO turnoJornadaDAO;

    @Mock
    TurnoDAO turnoDAO;

    @Mock
    JornadaDAO jornadaDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    TurnoJornadaResource resource;

    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final int EXCEEDMAX = 11;

    private UUID idJornada;
    private UUID idTurno;
    private Jornada jornada;
    private Turno turno;
    private TurnoJornada turnoJornada;
    private TurnoJornada entity;

    @BeforeEach
    void setUp() {
        idJornada = UUID.randomUUID();
        idTurno = UUID.randomUUID();

        jornada = new Jornada();
        jornada.setId(idJornada);

        turno = new Turno();
        turno.setId(idTurno);

        turnoJornada = new TurnoJornada();
        turnoJornada.setId(1);
        turnoJornada.setIdJornada(jornada);
        turnoJornada.setIdTurno(turno);

        entity = new TurnoJornada();
    }


    @Nested
    class FindRange {

        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos() {
            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);
            when(turnoJornadaDAO.findTurnoByIdJornada(idJornada, FIRST, MAX)).thenReturn(List.of(turnoJornada));
            when(turnoJornadaDAO.count()).thenReturn(1);

            var resp = resource.findRange(idJornada, FIRST, MAX);

            assertEquals(200, resp.getStatus());
            List<?> entidad = (List<?>) resp.getEntity();
            assertEquals(1, entidad.size());
            assertEquals("1", resp.getHeaderString("X-Total-Count"));
            verify(jornadaDAO).findById(idJornada);
            verify(turnoJornadaDAO).findTurnoByIdJornada(idJornada, FIRST, MAX);
            verify(turnoJornadaDAO).count();
        }

        @Test
        void retorna422_cuandoIdJornadaEsNulo() {
            var resp = resource.findRange(null, FIRST, MAX);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornada", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaDAO, turnoJornadaDAO);
        }

        @Test
        void retorna404_cuandoJornadaNoExiste() {
            when(jornadaDAO.findById(idJornada)).thenReturn(null);
            var resp = resource.findRange(idJornada, FIRST, MAX);
            assertEquals(404, resp.getStatus());
            assertEquals("Jornada with id " + idJornada + " not found", resp.getHeaderString("Not-found"));
            verify(jornadaDAO).findById(idJornada);
            verifyNoInteractions(turnoJornadaDAO);
        }

        @Test
        void retorna422_cuandoMaxEsInvalido() {
            Response resp = resource.findRange(idJornada, FIRST, INVALIDMAX);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaDAO, turnoJornadaDAO);
        }

        @Test
        void retorna422_cuandoMaxEsExcedido() {
            Response resp = resource.findRange(idJornada, FIRST, EXCEEDMAX);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaDAO, turnoJornadaDAO);
        }

        @Test
        void retorna422_cuandoFirstEsInvalido() {
            Response resp = resource.findRange(idJornada, INVALIDFIRST, MAX);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaDAO, turnoJornadaDAO);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);
            when(turnoJornadaDAO.findTurnoByIdJornada(idJornada, FIRST, MAX)).thenThrow(new RuntimeException("DB error"));
            var resp = resource.findRange(idJornada, FIRST, MAX);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(jornadaDAO).findById(idJornada);
            verify(turnoJornadaDAO).findTurnoByIdJornada(idJornada, FIRST, MAX);
        }
    }

    @Nested
    class FindOne {

        @Test
        void retorna200ConEntidad_cuandoIdEsValido() {
            when(turnoJornadaDAO.findTurnoByIdJornada(idJornada, 0, Integer.MAX_VALUE)).thenReturn(List.of(turnoJornada));
            var resp = resource.findOne(idJornada, idTurno);
            assertEquals(200, resp.getStatus());
            assertEquals(turnoJornada, resp.getEntity());
            verify(turnoJornadaDAO).findTurnoByIdJornada(idJornada, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna422_cuandoIdJornadaEsNulo() {
            var resp = resource.findOne(null, idTurno);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornada,idTurno", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(turnoJornadaDAO);
        }

        @Test
        void retorna422_cuandoIdTurnoEsNulo() {
            var resp = resource.findOne(idJornada, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornada,idTurno", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(turnoJornadaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro() {
            when(turnoJornadaDAO.findTurnoByIdJornada(idJornada, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            var resp = resource.findOne(idJornada, idTurno);
            assertEquals(404, resp.getStatus());
            assertEquals("Record linking jornada " + idJornada + " and turno " + idTurno + "not found"
                    , resp.getHeaderString("Not-found-id"));
            verify(turnoJornadaDAO).findTurnoByIdJornada(idJornada, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(turnoJornadaDAO.findTurnoByIdJornada(idJornada, 0, Integer.MAX_VALUE)).thenThrow(new RuntimeException("DB error"));
            var resp = resource.findOne(idJornada, idTurno);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(turnoJornadaDAO).findTurnoByIdJornada(idJornada, 0, Integer.MAX_VALUE);
        }
    }


    @Nested
    class Create {
        @Test
        void retorna201_cuandoEntidadEsValida() {
            entity.setId(null);
            Turno bodyTurno = new Turno();
            bodyTurno.setId(idTurno);
            entity.setIdTurno(bodyTurno);

            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);
            when(turnoDAO.findById(idTurno)).thenReturn(turno);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/jornada/" + idJornada + "/turno/" + idTurno ));

            doAnswer(inv-> {
                entity.setId(4);
                return null;
            }).when(turnoJornadaDAO).create(entity);

            var resp = resource.create(idJornada, entity, uriInfo);
            assertEquals(201, resp.getStatus());
            assertEquals(entity, resp.getEntity());
            verify(jornadaDAO).findById(idJornada);
            verify(turnoDAO).findById(idTurno);
            verify(turnoJornadaDAO).create(entity);
        }

        @Test
        void retorna422_cuandoIdJornadaEsNulo() {
            var resp = resource.create(null, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornada", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaDAO, turnoDAO, turnoJornadaDAO);
        }

        @Test
        void retorna422_cuandoEntidadEsNula() {
            var resp = resource.create(idJornada, null, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaDAO, turnoDAO, turnoJornadaDAO);
        }

        @Test
        void retorna422_cuandoEntidadTieneId() {
                entity.setId(5);
                var resp = resource.create(idJornada, entity, uriInfo);
                assertEquals(422, resp.getStatus());
                assertEquals("entity.id must be null", resp.getHeaderString("Missing-parameter"));
                verifyNoInteractions(jornadaDAO, turnoDAO, turnoJornadaDAO);
        }

        @Test
        void retorna422_cuandoIdTurnoNoProvistoEnBody() {
            entity.setId(null);
            var resp = resource.create(idJornada, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idTurno must be provider in body", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaDAO, turnoDAO, turnoJornadaDAO);
        }

        @Test
        void retorna404_cuandoJornadaNoExiste() {
            entity.setId(null);
            Turno bodyTurno = new Turno();
            bodyTurno.setId(idTurno);
            entity.setIdTurno(bodyTurno);

            when(jornadaDAO.findById(idJornada)).thenReturn(null);

            var resp = resource.create(idJornada, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("Jornada with id " + idJornada + " not found", resp.getHeaderString("Not-found-id"));
            verify(jornadaDAO).findById(idJornada);
            verifyNoInteractions(turnoDAO, turnoJornadaDAO);
        }

        @Test
        void retorna404_cuandoTurnoNoExiste() {
            entity.setId(null);
            Turno bodyTurno = new Turno();
            bodyTurno.setId(idTurno);
            entity.setIdTurno(bodyTurno);

            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);
            when(turnoDAO.findById(idTurno)).thenReturn(null);

            var resp = resource.create(idJornada, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("Turno with id " + idTurno + " not found", resp.getHeaderString("Not-found-id"));
            verify(jornadaDAO).findById(idJornada);
            verify(turnoDAO).findById(idTurno);
            verifyNoInteractions(turnoJornadaDAO);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            entity.setId(null);
            Turno bodyTurno = new Turno();
            bodyTurno.setId(idTurno);
            entity.setIdTurno(bodyTurno);

            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);
            when(turnoDAO.findById(idTurno)).thenReturn(turno);
            doThrow(new RuntimeException("DB error")).when(turnoJornadaDAO).create(entity);

            var resp = resource.create(idJornada, entity, uriInfo);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(turnoJornadaDAO).create(entity);
        }
    }

    @Nested
    class Delete {

        @Test
        void retorna204_cuandoIdEsValido() {
            when(turnoJornadaDAO.findTurnoByIdJornada(idJornada,0, Integer.MAX_VALUE)).thenReturn(List.of(turnoJornada));
            var resp = resource.delete(idJornada, idTurno);
            assertEquals(204, resp.getStatus());
            verify(turnoJornadaDAO).findTurnoByIdJornada(idJornada,0, Integer.MAX_VALUE);
            verify(turnoJornadaDAO).delete(turnoJornada);
        }

        @Test
        void retorna422_cuandoParametrosNulos(){
            var resp = resource.delete(null, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornada,idTurno", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(turnoJornadaDAO);
        }

        @Test
        void retorna422_cuandoIdTurnoEsNulo() {
            var resp = resource.delete(idJornada, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornada,idTurno", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(turnoJornadaDAO);
        }

        @Test
        void retorna422_cuandoIdJornadaEsNulo() {
            var resp = resource.delete(null, idTurno);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornada,idTurno", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(turnoJornadaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro() {
            when(turnoJornadaDAO.findTurnoByIdJornada(idJornada,0, Integer.MAX_VALUE)).thenReturn(List.of());
            var resp = resource.delete(idJornada, idTurno);
            assertEquals(404, resp.getStatus());
            assertEquals("Record linking jornada " + idJornada + " and turno " + idTurno + "not found"
                    , resp.getHeaderString("Not-found-id"));
            verify(turnoJornadaDAO).findTurnoByIdJornada(idJornada,0, Integer.MAX_VALUE);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(turnoJornadaDAO.findTurnoByIdJornada(idJornada,0, Integer.MAX_VALUE)).thenThrow(new RuntimeException("DB error"));
            var resp = resource.delete(idJornada, idTurno);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(turnoJornadaDAO).findTurnoByIdJornada(idJornada,0, Integer.MAX_VALUE);
        }

    }

}