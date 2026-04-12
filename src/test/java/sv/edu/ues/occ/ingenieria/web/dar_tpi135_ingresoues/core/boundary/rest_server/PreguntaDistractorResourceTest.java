// java
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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PreguntaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PreguntaDistractorDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PreguntaDistractor;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreguntaDistractorResourceTest {

    @Mock
    PreguntaDistractorDAO preguntaDistractorDAO;

    @Mock
    PreguntaDAO preguntaDAO;

    @Mock
    DistractorDAO distractorDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    PreguntaDistractorResource resource;

    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final int EXCEEDMAX = 11;

    private UUID idPregunta;
    private UUID idDistractor;
    private Pregunta pregunta;
    private Distractor distractor;
    private PreguntaDistractor pd;
    private PreguntaDistractor entity;

    @BeforeEach
    void setUp() {
        idPregunta = UUID.randomUUID();
        idDistractor = UUID.randomUUID();

        pregunta = new Pregunta();
        pregunta.setId(idPregunta);

        distractor = new Distractor();
        distractor.setId(idDistractor);

        pd = new PreguntaDistractor();
        pd.setId(1);
        pd.setIdPregunta(pregunta);
        pd.setIdDistractor(distractor);
        pd.setFechaAsignacion(OffsetDateTime.now());
        pd.setOrden((short) 1);

        entity = new PreguntaDistractor();
    }

    @Nested
    class FindByPregunta {
        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos() {
            when(preguntaDAO.findById(idPregunta)).thenReturn(pregunta);
            when(preguntaDistractorDAO.findByIdPregunta(idPregunta, FIRST, MAX)).thenReturn(List.of(pd));
            when(preguntaDistractorDAO.count()).thenReturn(1);

            var resp = resource.findByPregunta(idPregunta, FIRST, MAX);

            assertEquals(200, resp.getStatus());
            List<?> entidad = (List<?>) resp.getEntity();
            assertEquals(1, entidad.size());
            assertEquals("1", resp.getHeaderString("X-Total-Count"));
            verify(preguntaDAO).findById(idPregunta);
            verify(preguntaDistractorDAO).findByIdPregunta(idPregunta, FIRST, MAX);
            verify(preguntaDistractorDAO).count();
        }

        @Test
        void retorna422_cuandoIdPreguntaEsNulo() {
            var resp = resource.findByPregunta(null, FIRST, MAX);
            assertEquals(422, resp.getStatus());
            assertEquals("idPregunta", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDAO, preguntaDistractorDAO);
        }


        @Test
        void retorna422_cuandoFirstInvalido(){
            Response resp = resource.findByPregunta(idPregunta, INVALIDFIRST, MAX);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDAO, preguntaDistractorDAO);
        }
        @Test
        void retorna422_cuandoMaxEsInvalido(){
            Response resp = resource.findByPregunta(idPregunta, FIRST, INVALIDMAX);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDAO, preguntaDistractorDAO);
        }
        @Test
        void retorna422_cuandoMaxEsInvalidoPorMas(){
            Response resp = resource.findByPregunta(idPregunta, FIRST, EXCEEDMAX);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDAO, preguntaDistractorDAO);
        }

        @Test
        void retorna404_cuandoPreguntaNoExiste() {
            when(preguntaDAO.findById(idPregunta)).thenReturn(null);
            var resp = resource.findByPregunta(idPregunta, FIRST, MAX);
            assertEquals(404, resp.getStatus());
            assertEquals("Pregunta with id " + idPregunta + " not found", resp.getHeaderString("Not-found-id"));
            verify(preguntaDAO).findById(idPregunta);
            verifyNoMoreInteractions(preguntaDistractorDAO);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(preguntaDAO.findById(idPregunta)).thenReturn(pregunta);
            when(preguntaDistractorDAO.findByIdPregunta(idPregunta, FIRST, MAX)).thenThrow(new RuntimeException("DB"));
            var resp = resource.findByPregunta(idPregunta, FIRST, MAX);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(preguntaDAO).findById(idPregunta);
            verify(preguntaDistractorDAO).findByIdPregunta(idPregunta, FIRST, MAX);
        }
    }

    @Nested
    class FindOne {
        @Test
        void retorna200ConEntidad_cuandoExiste() {
            when(preguntaDistractorDAO.findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE)).thenReturn(List.of(pd));
            var resp = resource.findOne(idPregunta, idDistractor);
            assertEquals(200, resp.getStatus());
            assertEquals(pd, resp.getEntity());
            verify(preguntaDistractorDAO).findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna422_cuandoIdDistractorNoEsNull() {
            var resp = resource.findOne(null, idDistractor);
            assertEquals(422, resp.getStatus());
            assertEquals("idPregunta,idDistractor", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDistractorDAO);
        }
        @Test
        void retorna422_cuandoIdPreguntaNoEsNull() {
            var resp = resource.findOne(idPregunta, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idPregunta,idDistractor", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDistractorDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra() {
            when(preguntaDistractorDAO.findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            var resp = resource.findOne(idPregunta, idDistractor);
            assertEquals(404, resp.getStatus());
            assertEquals("Record linking pregunta " + idPregunta + " and distractor " + idDistractor + " not found",
                    resp.getHeaderString("Not-found-id"));
            verify(preguntaDistractorDAO).findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(preguntaDistractorDAO.findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE)).thenThrow(new RuntimeException("DB"));
            var resp = resource.findOne(idPregunta, idDistractor);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(preguntaDistractorDAO).findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE);
        }
    }

    @Nested
    class Create {
        @Test
        void retorna201_cuandoEntidadValida() {
            // preparar body con idDistractor dentro
            entity.setId(null);
            Distractor bodyDist = new Distractor();
            bodyDist.setId(idDistractor);
            entity.setIdDistractor(bodyDist);

            when(preguntaDAO.findById(idPregunta)).thenReturn(pregunta);
            when(distractorDAO.findById(idDistractor)).thenReturn(distractor);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/pregunta/" + idPregunta + "/distractor/" + idDistractor));

            doAnswer(inv -> {
                // simular que DAO asigna id
                entity.setId(42);
                return null;
            }).when(preguntaDistractorDAO).create(entity);

            var resp = resource.create(idPregunta, entity, uriInfo);
            assertEquals(201, resp.getStatus());
            assertEquals(entity, resp.getEntity());
            verify(preguntaDAO).findById(idPregunta);
            verify(distractorDAO).findById(idDistractor);
            verify(preguntaDistractorDAO).create(entity);
        }

        @Test
        void retorna422_cuandoIdPreguntaNulo() {
            var resp = resource.create(null, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idPregunta", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDAO, distractorDAO, preguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoEntidadNula() {
            var resp = resource.create(idPregunta, null, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDAO, distractorDAO, preguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoEntityTieneId() {
            entity.setId(5);
            var resp = resource.create(idPregunta, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.id must be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDAO, distractorDAO, preguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoIdDistractorNoProvistoEnBody() {
            entity.setIdDistractor(null);
            var resp = resource.create(idPregunta, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idDistractor must be provided in body", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDAO, distractorDAO, preguntaDistractorDAO);
        }


        @Test
        void retorna404_cuandoPreguntaNoExiste() {
            entity.setId(null);
            Distractor bodyDist = new Distractor();
            bodyDist.setId(idDistractor);
            entity.setIdDistractor(bodyDist);

            when(preguntaDAO.findById(idPregunta)).thenReturn(null);
            var resp = resource.create(idPregunta, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("Pregunta with id " + idPregunta + " not found", resp.getHeaderString("Not-found-id"));
            verify(preguntaDAO).findById(idPregunta);
            verifyNoMoreInteractions(distractorDAO, preguntaDistractorDAO);
        }

        @Test
        void retorna404_cuandoDistractorNoExiste() {
            entity.setId(null);
            Distractor bodyDist = new Distractor();
            bodyDist.setId(idDistractor);
            entity.setIdDistractor(bodyDist);

            when(preguntaDAO.findById(idPregunta)).thenReturn(pregunta);
            when(distractorDAO.findById(idDistractor)).thenReturn(null);

            var resp = resource.create(idPregunta, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("Distractor with id " + idDistractor + " not found", resp.getHeaderString("Not-found-id"));
            verify(preguntaDAO).findById(idPregunta);
            verify(distractorDAO).findById(idDistractor);
            verifyNoMoreInteractions(preguntaDistractorDAO);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            entity.setId(null);
            Distractor bodyDist = new Distractor();
            bodyDist.setId(idDistractor);
            entity.setIdDistractor(bodyDist);

            when(preguntaDAO.findById(idPregunta)).thenReturn(pregunta);
            when(distractorDAO.findById(idDistractor)).thenReturn(distractor);
            doThrow(new RuntimeException("DB")).when(preguntaDistractorDAO).create(entity);

            var resp = resource.create(idPregunta, entity, uriInfo);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(preguntaDistractorDAO).create(entity);
        }
    }

    @Nested
    class Update {
        @Test
        void retorna200_cuandoEntidadEsValida() {
            // existing registro con orden 1
            PreguntaDistractor existing = new PreguntaDistractor();
            existing.setId(1);
            existing.setIdPregunta(pregunta);
            existing.setIdDistractor(distractor);
            existing.setOrden((short) 1);

            // body con nuevo orden
            PreguntaDistractor body = new PreguntaDistractor();
            body.setOrden((short) 5);

            when(preguntaDistractorDAO.findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE)).thenReturn(List.of(existing));
            when(preguntaDistractorDAO.update(existing)).thenAnswer(inv -> existing);

            var resp = resource.update(idPregunta, idDistractor, body);
            assertEquals(200, resp.getStatus());
            PreguntaDistractor updated = (PreguntaDistractor) resp.getEntity();
            assertEquals((short)5, updated.getOrden());
            verify(preguntaDistractorDAO).findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE);
            verify(preguntaDistractorDAO).update(existing);
        }

        @Test
        void retorna422_cuandoIdDistractorNoEsNull() {
            var resp = resource.update(null, idDistractor, entity);
            assertEquals(422, resp.getStatus());
            assertEquals("idPregunta,idDistractor", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDistractorDAO);
        }
        @Test
        void retorna422_cuandoIdPreguntaNoEsNull() {
            var resp = resource.update(idPregunta, null, entity);
            assertEquals(422, resp.getStatus());
            assertEquals("idPregunta,idDistractor", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoEntidadNula() {
            var resp = resource.update(idPregunta, idDistractor, null);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDistractorDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra() {
            when(preguntaDistractorDAO.findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            var resp = resource.update(idPregunta, idDistractor, entity);
            assertEquals(404, resp.getStatus());
            assertEquals("Record linking pregunta " + idPregunta + " and distractor " + idDistractor + " not found",
                    resp.getHeaderString("Not-found-id"));
            verify(preguntaDistractorDAO).findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(preguntaDistractorDAO.findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE)).thenThrow(new RuntimeException("DB"));
            var resp = resource.update(idPregunta, idDistractor, entity);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(preguntaDistractorDAO).findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE);
        }
    }

    @Nested
    class Delete {
        @Test
        void retorna204_cuandoValido() {
            when(preguntaDistractorDAO.findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE)).thenReturn(List.of(pd));
            var resp = resource.delete(idPregunta, idDistractor);
            assertEquals(204, resp.getStatus());
            verify(preguntaDistractorDAO).findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE);
            verify(preguntaDistractorDAO).delete(pd);
        }

        @Test
        void retorna422_cuandoParametrosNulos() {
            var resp = resource.delete(null, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idPregunta,idDistractor", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDistractorDAO);
        }
        @Test
        void retorna422_cuandoIdDistractorNoEsNull() {
            var resp = resource.delete(null, idDistractor);
            assertEquals(422, resp.getStatus());
            assertEquals("idPregunta,idDistractor", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDistractorDAO);
        }
        @Test
        void retorna422_cuandoIdPreguntaNoEsNull() {
            var resp = resource.delete(idPregunta, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idPregunta,idDistractor", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaDistractorDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra() {
            when(preguntaDistractorDAO.findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            var resp = resource.delete(idPregunta, idDistractor);
            assertEquals(404, resp.getStatus());
            assertEquals("Record linking pregunta " + idPregunta + " and distractor " + idDistractor + " not found",
                    resp.getHeaderString("Not-found-id"));
            verify(preguntaDistractorDAO).findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(preguntaDistractorDAO.findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE)).thenThrow(new RuntimeException("DB"));
            var resp = resource.delete(idPregunta, idDistractor);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(preguntaDistractorDAO).findByIdPregunta(idPregunta, 0, Integer.MAX_VALUE);
        }
    }
}
