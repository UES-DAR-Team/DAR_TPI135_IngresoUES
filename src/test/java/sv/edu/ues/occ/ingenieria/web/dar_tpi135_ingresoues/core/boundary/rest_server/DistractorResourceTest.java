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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.DistractorDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DistractorResourceTest {

    @Mock
    DistractorDAO distractorDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    DistractorResource distractorResource;

    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final int EXCEEDMAX = 11;
    private static final List<Distractor> LISTA = List.of(
            new Distractor(),
            new Distractor()
    );

    private UUID id;
    private Distractor distractor;
    private Distractor entity;

    @BeforeEach
    void setup() {
        id = UUID.randomUUID();
        distractor = new Distractor();
        distractor.setId(id);
        entity = new Distractor();
    }

    @Nested
    class FindRange {

        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos() {
            when(distractorDAO.findRange(FIRST, MAX)).thenReturn(LISTA);
            when(distractorDAO.count()).thenReturn(2);

            Response response = distractorResource.findRange(FIRST, MAX);

            assertEquals(200, response.getStatus());
            List<?> entidad = (List<?>) response.getEntity();
            assertEquals(2, entidad.size());
            assertEquals("2", response.getHeaderString("X-Total-Count"));
            verify(distractorDAO).findRange(FIRST, MAX);
            verify(distractorDAO).count();
        }

        @Test
        void retorna422_cuandoMaxEsInvalido() {
            Response response = distractorResource.findRange(FIRST, INVALIDMAX);

            assertEquals(422, response.getStatus());
            assertEquals("first,max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(distractorDAO);
        }
        @Test
        void rertorna422_cuandoMaxEsExcedido() {
            Response response = distractorResource.findRange(FIRST, EXCEEDMAX);

            assertEquals(422, response.getStatus());
            assertEquals("first,max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(distractorDAO);
        }
        @Test
        void rertorna422_cuandoFirstEsInvalido() {
            Response response = distractorResource.findRange(INVALIDFIRST, MAX);

            assertEquals(422, response.getStatus());
            assertEquals("first,max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(distractorDAO);
        }


        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(distractorDAO.findRange(FIRST, MAX)).thenThrow(new RuntimeException("DB error"));

            Response response = distractorResource.findRange(FIRST, MAX);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(distractorDAO).findRange(FIRST, MAX);
        }
    }

    @Nested
    class FindById {
        @Test
        void retorna200ConEntidad_cuandoIdEsValido() {
            when(distractorDAO.findById(id)).thenReturn(distractor);

            Response response = distractorResource.findById(id);

            assertEquals(200, response.getStatus());
            assertEquals(distractor, response.getEntity());
            verify(distractorDAO).findById(id);
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response response = distractorResource.findById(null);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(distractorDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro() {
            when(distractorDAO.findById(id)).thenReturn(null);

            Response response = distractorResource.findById(id);

            assertEquals(404, response.getStatus());
            assertEquals("Record with id " + id + " not found", response.getHeaderString("Not-found-id"));
            verify(distractorDAO).findById(id);
        }

        @Test
        void retorna500_cuandoDAOlanzaExcepcion() {
            when(distractorDAO.findById(id)).thenThrow(new RuntimeException("DB error"));

            Response response = distractorResource.findById(id);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(distractorDAO).findById(id);
        }
    }

    @Nested
    class Delete {
        @Test
        void retorna204_cuandoIdEsValido() {
            when(distractorDAO.findById(id)).thenReturn(distractor);

            Response response = distractorResource.deleteById(id);

            assertEquals(204, response.getStatus());
            verify(distractorDAO).findById(id);
            verify(distractorDAO).delete(distractor);
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response response = distractorResource.deleteById(null);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(distractorDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro() {
            when(distractorDAO.findById(id)).thenReturn(null);

            Response response = distractorResource.deleteById(id);

            assertEquals(404, response.getStatus());
            assertEquals("Record with id " + id + " not found", response.getHeaderString("Not-found-id"));
            verify(distractorDAO).findById(id);
        }

        @Test
        void retorna500_cuandoDAOlanzaExcepcion() {
            when(distractorDAO.findById(id)).thenThrow(new RuntimeException("DB error"));

            Response response = distractorResource.deleteById(id);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(distractorDAO).findById(id);
        }
    }

    @Nested
    class Create {
        @Test
        void retorna201_cuandoEntidadEsValida() {
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/distractor/"));
            doAnswer(inv -> {
                entity.setId(id);
                return null;
            }).when(distractorDAO).create(entity);

            Response response = distractorResource.create(entity, uriInfo);

            assertEquals(201, response.getStatus());
            assertEquals(entity, response.getEntity());
            verify(distractorDAO).create(entity);
        }

        @Test
        void retorna422_cuandoEntidadEsNula() {
            Response response = distractorResource.create(null, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(distractorDAO);
        }

        @Test
        void retorna422_cuandoEntidadTieneId() {
            entity.setId(id);

            Response response = distractorResource.create(entity, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("entity.id must be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(distractorDAO);
        }

        @Test
        void retorna500_cuandoDAOlanzaExcepcion() {
            doThrow(new RuntimeException("DB error")).when(distractorDAO).create(entity);

            Response response = distractorResource.create(entity, uriInfo);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(distractorDAO).create(entity);
        }
    }

    @Nested
    class Update {
        @Test
        void retorna200_cuandoEntidadEsValida() {
            Distractor existing = new Distractor();
            existing.setId(id);
            Distractor update = new Distractor();
            update.setId(id);
            when(distractorDAO.findById(id)).thenReturn(existing);
            when(distractorDAO.update(update)).thenReturn(update);

            Response response = distractorResource.update(id, update);

            assertEquals(200, response.getStatus());
            assertEquals(update, response.getEntity());
            verify(distractorDAO).findById(id);
            verify(distractorDAO).update(update);
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response response = distractorResource.update(null, entity);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(distractorDAO);
        }

        @Test
        void retorna422_cuandoEntidadEsNula() {
            Response response = distractorResource.update(id, null);

            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(distractorDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro() {
            when(distractorDAO.findById(id)).thenReturn(null);

            Response response = distractorResource.update(id, entity);

            assertEquals(404, response.getStatus());
            assertEquals("Record with id " + id + " not found", response.getHeaderString("Not-found-id"));
            verify(distractorDAO).findById(id);
        }

        @Test
        void retorna500_cuandoDAOlanzaExcepcion() {
            when(distractorDAO.findById(id)).thenThrow( new RuntimeException("DB error"));;

            Response response = distractorResource.update(id, entity);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(distractorDAO).findById(id);
        }
    }
}