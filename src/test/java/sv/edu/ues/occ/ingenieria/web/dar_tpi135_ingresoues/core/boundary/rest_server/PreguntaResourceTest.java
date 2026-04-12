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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PreguntaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreguntaResourceTest {

    @Mock
    PreguntaDAO preguntaDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    PreguntaResource preguntaResource;

    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final int EXCEEDMAX = 11;
    private static final List<Pregunta> LISTA = List.of(
            new Pregunta(), new Pregunta(), new Pregunta()
    );


    private UUID id;
    private Pregunta pregunta;
    private Pregunta entity;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        pregunta = new Pregunta();
        pregunta.setId(id);
        entity = new Pregunta();
    }

    @Nested
    class FindRange {
        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos(){
            when(preguntaDAO.findRange(FIRST, MAX)).thenReturn(LISTA);
            when(preguntaDAO.count()).thenReturn(3);

            Response response = preguntaResource.findRange(FIRST, MAX);

            assertEquals(200, response.getStatus());
            List<?> entidad = (List<?>) response.getEntity();
            assertEquals(3, entidad.size());
            assertEquals("3", response.getHeaderString("X-Total-Count"));
            verify(preguntaDAO).findRange(FIRST, MAX);
            verify(preguntaDAO).count();
        }

        @Test
        void retorna422_cuandoFirstInvalido(){
            Response response = preguntaResource.findRange(INVALIDFIRST, MAX);
            assertEquals(422,response.getStatus());
            assertEquals("first,max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDAO);
        }
        @Test
        void retorna422_cuandoMaxEsInvalido(){
            Response response = preguntaResource.findRange(FIRST, INVALIDMAX);
            assertEquals(422,response.getStatus());
            assertEquals("first,max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDAO);
        }
        @Test
        void retorna422_cuandoMaxEsInvalidoPorMas(){
            Response response = preguntaResource.findRange(FIRST, EXCEEDMAX);
            assertEquals(422,response.getStatus());
            assertEquals("first,max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDAO);
        }

        @Test
		void retorna500_cuandoDAOLanzaExcepcion(){
            when(preguntaDAO.findRange(FIRST, MAX)).thenThrow(new RuntimeException("DB error"));

            Response response = preguntaResource.findRange(FIRST, MAX);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(preguntaDAO).findRange(FIRST, MAX);
        }
    }

    @Nested
    class FinById {
        @Test
        void retorna200ConEntidad_cuandoIdEsValido(){
            when(preguntaDAO.findById(id)).thenReturn(pregunta);

            Response response = preguntaResource.findById(id);

            assertEquals(200, response.getStatus());
            assertEquals(pregunta, response.getEntity());
            verify(preguntaDAO).findById(id);
        }

        @Test
		void retorna422_cuandoIdEsNulo(){
            Response response = preguntaResource.findById(null);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDAO);
        }

        @Test
		void retorna404_cuandoNoSeEncuentraRegistro(){
            when(preguntaDAO.findById(id)).thenReturn(null);

            Response response = preguntaResource.findById(id);

            assertEquals(404, response.getStatus());
            assertEquals("Record with id " + id + " not found", response.getHeaderString("Not-found-id"));
            verify(preguntaDAO).findById(id);
        }

        @Test
		void retorna500_cuandoDAOLanzaExcepcion(){
            when(preguntaDAO.findById(id)).thenThrow(new RuntimeException("DB error"));

            Response response = preguntaResource.findById(id);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(preguntaDAO).findById(id);
        }
    }

    @Nested
    class Delete {
        @Test
        void retorna204_cuandoIdEsValido(){
            when(preguntaDAO.findById(id)).thenReturn(pregunta);

            Response response = preguntaResource.deleteById(id);

            assertEquals(204, response.getStatus());
            verify(preguntaDAO).findById(id);
            verify(preguntaDAO).delete(pregunta);
        }

        @Test
		void retorna422_cuandoIdEsNulo(){
            Response response = preguntaResource.deleteById(null);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDAO);
        }

        @Test
		void retorna404_cuandoNoSeEncuentraRegistro(){
            when(preguntaDAO.findById(id)).thenReturn(null);

            Response response = preguntaResource.deleteById(id);

            assertEquals(404, response.getStatus());
            assertEquals("Record with id " + id + " not found", response.getHeaderString("Not-found-id"));
            verify(preguntaDAO).findById(id);
        }

        @Test
		void retorna500_cuandoDAOLanzaExcepcion(){
            when(preguntaDAO.findById(id)).thenThrow(new RuntimeException("DB error"));

            Response response = preguntaResource.deleteById(id);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(preguntaDAO).findById(id);
        }
    }

    @Nested
    class Create {
        @Test
        void retorna201_cuandoEntidadEsValida(){
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/pregunta/" ));
            doAnswer(inv->{
                entity.setId(id);
                return null;
            }).when(preguntaDAO).create(entity);

            Response response = preguntaResource.create(entity, uriInfo);

            assertEquals(201, response.getStatus());
            assertEquals(entity, response.getEntity());
            verify(preguntaDAO).create(entity);
        }

        @Test
		void retorna422_cuandoEntidadEsNula(){
            Response response = preguntaResource.create(null, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDAO);
        }

        @Test
		void retorna422_cuandoEntidadTieneId(){
            entity.setId(id);

            Response response = preguntaResource.create(entity, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("entity.id must be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDAO);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion(){
            doThrow(new RuntimeException("DB error")).when(preguntaDAO).create(entity);

            Response response = preguntaResource.create(entity, uriInfo);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(preguntaDAO).create(entity);
        }
    }

    @Nested
    class Update {
        @Test
        void retorna200_cuandoEntidadEsValida(){
            Pregunta existing = new Pregunta();
            existing.setId(id);
            Pregunta update = new Pregunta();
            update.setId(id);
            when(preguntaDAO.findById(id)).thenReturn(existing);
            when(preguntaDAO.update(update)).thenReturn(update);

            Response response = preguntaResource.update(id, update);

            assertEquals(200, response.getStatus());
            assertEquals(update, response.getEntity());
            verify(preguntaDAO).findById(id);
            verify(preguntaDAO).update(update);
        }

        @Test
		void retorna422_cuandoIdEsNulo(){
            Response response = preguntaResource.update(null, entity);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDAO);
        }

        @Test
		void retorna422_cuandoEntidadEsNula(){
            Response response = preguntaResource.update(id, null);

            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDAO);
        }

        @Test
		void retorna404_cuandoNoSeEncuentraRegistro(){
            when(preguntaDAO.findById(id)).thenReturn(null);

            Response response = preguntaResource.update(id, entity);

            assertEquals(404, response.getStatus());
            assertEquals("Record with id " + id + " not found", response.getHeaderString("Not-found-id"));
            verify(preguntaDAO).findById(id);
        }

        @Test
		void retorna500_cuandoDAOLanzaExcepcion(){
            when(preguntaDAO.findById(id)).thenThrow(new RuntimeException("DB error"));

            Response response = preguntaResource.update(id, entity);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(preguntaDAO).findById(id);
        }
    }

}