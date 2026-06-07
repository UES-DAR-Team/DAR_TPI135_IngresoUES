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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.TurnoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Turno;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurnoResourceTest {

    @Mock
    TurnoDAO turnoDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    TurnoResource turnoResource;

    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final int EXCEEDMAX = 11;
    private static final List<Turno> LISTA = List.of(
            new Turno(),
            new Turno()
    );

    private UUID idTurno;
    private Turno turno;
    private Turno entity;

    @BeforeEach
    void setUp() {
        idTurno = UUID.randomUUID();
        turno = new Turno();
        turno.setId(idTurno);
        entity = new Turno();
    }

    @Nested
    class FindRange {

        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos() {
            when(turnoDAO.findRange(FIRST, MAX)).thenReturn(LISTA);
            when(turnoDAO.count()).thenReturn(2);

            Response response = turnoResource.findRange(FIRST, MAX);

            assertEquals(200, response.getStatus());
            List<?> entidad = (List<?>) response.getEntity();
            assertEquals(2, entidad.size());
            assertEquals("2", response.getHeaderString("X-Total-Count"));
            verify(turnoDAO).findRange(FIRST, MAX);
            verify(turnoDAO).count();
        }

        @Test
        void retorna422_cuandoMaxEsInvalido() {
            Response response = turnoResource.findRange(FIRST, INVALIDMAX);

            assertEquals(422, response.getStatus());
            assertEquals("first, max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(turnoDAO);
        }

        @Test
        void retorna422_cuandoMaxEsExcedido() {
            Response response = turnoResource.findRange(FIRST, EXCEEDMAX);

            assertEquals(422, response.getStatus());
            assertEquals("first, max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(turnoDAO);
        }

        @Test
        void retorna422_cuandoFirstEsInvalido() {
            Response response = turnoResource.findRange(INVALIDFIRST, MAX);

            assertEquals(422, response.getStatus());
            assertEquals("first, max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(turnoDAO);
        }

    }

    @Nested
    class FindById {

        @Test
        void retorna200ConEntidad_cuandoIdEsValido() {
            when(turnoDAO.findById(idTurno)).thenReturn(turno);

            Response response = turnoResource.findById(idTurno);

            assertEquals(200, response.getStatus());
            assertEquals(turno, response.getEntity());
            verify(turnoDAO).findById(idTurno);
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response response = turnoResource.findById(null);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(turnoDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro() {
            when(turnoDAO.findById(idTurno)).thenReturn(null);

            Response response = turnoResource.findById(idTurno);

            assertEquals(404, response.getStatus());
            assertEquals("Turno with id " + idTurno + " not found", response.getHeaderString("Not-found-id"));
            verify(turnoDAO).findById(idTurno);
        }

    }

    @Nested
    class Delete {

        @Test
        void retorna204_cuandoIdEsValido() {
            when(turnoDAO.findById(idTurno)).thenReturn(turno);

            Response response = turnoResource.deleteById(idTurno);

            assertEquals(204, response.getStatus());
            verify(turnoDAO).findById(idTurno);
            verify(turnoDAO).delete(turno);
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response response = turnoResource.deleteById(null);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(turnoDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro() {
            when(turnoDAO.findById(idTurno)).thenReturn(null);

            Response response = turnoResource.deleteById(idTurno);

            assertEquals(404, response.getStatus());
            assertEquals("Turno with id " + idTurno + " not found", response.getHeaderString("Not-found-id"));
            verify(turnoDAO).findById(idTurno);
        }

    }

    @Nested
    class Create {
        @Test
        void retorna201_cuandoEntidadEsValida() {
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/turno/"));
            doAnswer(inv -> {
                entity.setId(idTurno);
                return null;
            }).when(turnoDAO).create(entity);

            Response response = turnoResource.create(entity, uriInfo);

            assertEquals(201, response.getStatus());

            verify(turnoDAO).create(entity);
        }

        @Test
        void retorna422_cuandoEntidadEsNula() {
            Response response = turnoResource.create(null, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(turnoDAO);
        }

        @Test
        void retorna422_cuandoEntidadTieneId() {
            entity.setId(idTurno);

            Response response = turnoResource.create(entity, uriInfo);
            assertEquals(422, response.getStatus());
            assertEquals("entity.id must be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(turnoDAO);
        }

    }

    @Nested
    class Update {

        @Test
        void retorna200_cuandoEntidadEsValida() {
            Turno existing = new Turno();
            existing.setId(idTurno);
            Turno update = new Turno();
            update.setId(idTurno);
            when(turnoDAO.findById(idTurno)).thenReturn(existing);
            when(turnoDAO.update(update)).thenReturn(update);

            Response response = turnoResource.update(idTurno, update);

            assertEquals(200, response.getStatus());
            assertEquals(update, response.getEntity());
            verify(turnoDAO).findById(idTurno);
            verify(turnoDAO).update(update);
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response response = turnoResource.update(null, entity);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(turnoDAO);
        }

        @Test
        void retorna422_cuandoEntidadEsNula() {
            Response response = turnoResource.update(idTurno, null);

            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(turnoDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro() {
            when(turnoDAO.findById(idTurno)).thenReturn(null);

            Response response = turnoResource.update(idTurno, entity);

            assertEquals(404, response.getStatus());
            assertEquals("Turno with id " + idTurno + " not found", response.getHeaderString("Not-found-id"));
            verify(turnoDAO).findById(idTurno);
        }

    }

}