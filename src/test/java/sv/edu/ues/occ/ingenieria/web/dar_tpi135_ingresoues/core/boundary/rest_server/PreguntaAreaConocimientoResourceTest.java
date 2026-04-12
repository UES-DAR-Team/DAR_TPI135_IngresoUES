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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PreguntaAreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PreguntaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PreguntaAreaConocimiento;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreguntaAreaConocimientoResourceTest {

    @Mock
    PreguntaAreaConocimientoDAO preguntaAreaConocimientoDAO;

    @Mock
    PreguntaDAO preguntaDAO;

    @Mock
    AreaConocimientoDAO areaConocimientoDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    PreguntaAreaConocimientoResource resource;

    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final int EXCEEDMAX = 11;

    private UUID idAreaConocimiento;
    private UUID idPregunta;
    private Pregunta pregunta;
    private AreaConocimiento areaConocimiento;
    private PreguntaAreaConocimiento pac;
    private PreguntaAreaConocimiento entity;

    @BeforeEach
    void setUp() {
        idAreaConocimiento = UUID.randomUUID();
        idPregunta = UUID.randomUUID();

        pregunta = new Pregunta();
        pregunta.setId(idPregunta);

        areaConocimiento = new AreaConocimiento();
        areaConocimiento.setId(idAreaConocimiento);

        pac = new PreguntaAreaConocimiento();
        pac.setId(1);
        pac.setIdPregunta(pregunta);
        pac.setIdAreaConocimiento(areaConocimiento);
        pac.setFechaAsignacion(OffsetDateTime.now());

        entity = new PreguntaAreaConocimiento();
    }

    @Nested
    class FindRange {
        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos() {
            when(areaConocimientoDAO.findById(idAreaConocimiento)).thenReturn(areaConocimiento);
            when(preguntaAreaConocimientoDAO.findPreguntaByIdAreaConocimiento(idAreaConocimiento, FIRST, MAX)).thenReturn(List.of(pac));
            when(preguntaAreaConocimientoDAO.count()).thenReturn(1);

            Response resp = resource.findRange(idAreaConocimiento, FIRST, MAX);

            assertEquals(200, resp.getStatus());
            List<?> entidad = (List<?>) resp.getEntity();
            assertEquals(1, entidad.size());
            assertEquals("1", resp.getHeaderString("X-Total-Count"));
            verify(areaConocimientoDAO).findById(idAreaConocimiento);
            verify(preguntaAreaConocimientoDAO).findPreguntaByIdAreaConocimiento(idAreaConocimiento, FIRST, MAX);
            verify(preguntaAreaConocimientoDAO).count();
        }

        @Test
        void retorna422_cuandoIdAreaEsNulo() {
            Response resp = resource.findRange(null, FIRST, MAX);
            assertEquals(422, resp.getStatus());
            assertEquals("idAreaConocimiento", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO, preguntaAreaConocimientoDAO);
        }

        @Test
        void retorna422_cuandoFirstInvalido(){
                Response resp = resource.findRange(idAreaConocimiento, INVALIDFIRST, MAX);
                assertEquals(422, resp.getStatus());
                assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
                verifyNoInteractions(areaConocimientoDAO, preguntaAreaConocimientoDAO);
        }
        @Test
        void retorna422_cuandoMaxEsInvalido(){
            Response resp = resource.findRange(idAreaConocimiento, FIRST, INVALIDMAX);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO, preguntaAreaConocimientoDAO);
        }
        @Test
        void retorna422_cuandoMaxEsInvalidoPorMas(){
            Response resp = resource.findRange(idAreaConocimiento, FIRST, EXCEEDMAX);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO, preguntaAreaConocimientoDAO);
        }

        @Test
        void retorna404_cuandoAreaNoExiste() {
            when(areaConocimientoDAO.findById(idAreaConocimiento)).thenReturn(null);
            Response resp = resource.findRange(idAreaConocimiento, FIRST, MAX);
            assertEquals(404, resp.getStatus());
            assertEquals("AreaConocimiento with id " + idAreaConocimiento + " not found", resp.getHeaderString("Not-found"));
            verify(areaConocimientoDAO).findById(idAreaConocimiento);
            verifyNoMoreInteractions(preguntaAreaConocimientoDAO);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(areaConocimientoDAO.findById(idAreaConocimiento)).thenReturn(areaConocimiento);
            when(preguntaAreaConocimientoDAO.findPreguntaByIdAreaConocimiento(idAreaConocimiento, FIRST, MAX)).thenThrow(new RuntimeException("DB"));
            Response resp = resource.findRange(idAreaConocimiento, FIRST, MAX);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(areaConocimientoDAO).findById(idAreaConocimiento);
            verify(preguntaAreaConocimientoDAO).findPreguntaByIdAreaConocimiento(idAreaConocimiento, FIRST, MAX);
        }
    }

    @Nested
    class FindOne {
        @Test
        void retorna200ConEntidad_cuandoExiste() {
            when(preguntaAreaConocimientoDAO.findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE)).thenReturn(List.of(pac));
            Response resp = resource.findOne(idAreaConocimiento, idPregunta);
            assertEquals(200, resp.getStatus());
            assertEquals(pac, resp.getEntity());
            verify(preguntaAreaConocimientoDAO).findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna422_cuandoParametrosNulos() {
            Response resp = resource.findOne(null, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idAreaConocimiento,idPregunta", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaAreaConocimientoDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra() {
            when(preguntaAreaConocimientoDAO.findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.findOne(idAreaConocimiento, idPregunta);
            assertEquals(404, resp.getStatus());
            assertEquals("Record linking area " + idAreaConocimiento + " and pregunta " + idPregunta + " not found",
                    resp.getHeaderString("Not-found-id"));
            verify(preguntaAreaConocimientoDAO).findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(preguntaAreaConocimientoDAO.findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE)).thenThrow(new RuntimeException("DB"));
            Response resp = resource.findOne(idAreaConocimiento, idPregunta);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(preguntaAreaConocimientoDAO).findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);
        }
    }

    @Nested
    class Create {
        @Test
        void retorna201_cuandoEntidadValida() {
            entity.setId(null);
            Pregunta bodyPreg = new Pregunta();
            bodyPreg.setId(idPregunta);
            entity.setIdPregunta(bodyPreg);

            when(areaConocimientoDAO.findById(idAreaConocimiento)).thenReturn(areaConocimiento);
            when(preguntaDAO.findById(idPregunta)).thenReturn(pregunta);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/areaConocimiento/" + idAreaConocimiento + "/pregunta/" + idPregunta));

            doAnswer(inv -> {
                entity.setId(42);
                return null;
            }).when(preguntaAreaConocimientoDAO).create(entity);

            Response resp = resource.create(idAreaConocimiento, entity, uriInfo);
            assertEquals(201, resp.getStatus());
            assertEquals(entity, resp.getEntity());
            verify(areaConocimientoDAO).findById(idAreaConocimiento);
            verify(preguntaDAO).findById(idPregunta);
            verify(preguntaAreaConocimientoDAO).create(entity);
        }

        @Test
        void retorna422_cuandoIdAreaNulo() {
            Response resp = resource.create(null, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idAreaConocimiento", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO, preguntaDAO, preguntaAreaConocimientoDAO);
        }

        @Test
        void retorna422_cuandoEntidadNula() {
            Response resp = resource.create(idAreaConocimiento, null, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO, preguntaDAO, preguntaAreaConocimientoDAO);
        }

        @Test
        void retorna422_cuandoEntityTieneId() {
            entity.setId(5);
            Response resp = resource.create(idAreaConocimiento, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.id must be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO, preguntaDAO, preguntaAreaConocimientoDAO);
        }

        @Test
        void retorna422_cuandoIdPreguntaNoProvistoEnBody() {
            entity.setId(null);
            entity.setIdPregunta(null);
            Response resp = resource.create(idAreaConocimiento, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idPregunta must be provided in body", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO, preguntaDAO, preguntaAreaConocimientoDAO);
        }

        @Test
        void retorna404_cuandoAreaNoExiste() {
            entity.setId(null);
            Pregunta bodyPreg = new Pregunta();
            bodyPreg.setId(idPregunta);
            entity.setIdPregunta(bodyPreg);

            when(areaConocimientoDAO.findById(idAreaConocimiento)).thenReturn(null);
            Response resp = resource.create(idAreaConocimiento, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("AreaConocimiento with id " + idAreaConocimiento + " not found", resp.getHeaderString("Not-found"));
            verify(areaConocimientoDAO).findById(idAreaConocimiento);
            verifyNoMoreInteractions(preguntaDAO, preguntaAreaConocimientoDAO);
        }

        @Test
        void retorna404_cuandoPreguntaNoExiste() {
            entity.setId(null);
            Pregunta bodyPreg = new Pregunta();
            bodyPreg.setId(idPregunta);
            entity.setIdPregunta(bodyPreg);

            when(areaConocimientoDAO.findById(idAreaConocimiento)).thenReturn(areaConocimiento);
            when(preguntaDAO.findById(idPregunta)).thenReturn(null);

            Response resp = resource.create(idAreaConocimiento, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("Pregunta with id " + idPregunta + " not found", resp.getHeaderString("Not-found"));
            verify(areaConocimientoDAO).findById(idAreaConocimiento);
            verify(preguntaDAO).findById(idPregunta);
            verifyNoMoreInteractions(preguntaAreaConocimientoDAO);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            entity.setId(null);
            Pregunta bodyPreg = new Pregunta();
            bodyPreg.setId(idPregunta);
            entity.setIdPregunta(bodyPreg);

            when(areaConocimientoDAO.findById(idAreaConocimiento)).thenReturn(areaConocimiento);
            when(preguntaDAO.findById(idPregunta)).thenReturn(pregunta);
            doThrow(new RuntimeException("DB")).when(preguntaAreaConocimientoDAO).create(entity);

            Response resp = resource.create(idAreaConocimiento, entity, uriInfo);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(preguntaAreaConocimientoDAO).create(entity);
        }
    }

    @Nested
    class Delete {
        @Test
        void retorna204_cuandoValido() {
            when(preguntaAreaConocimientoDAO.findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE)).thenReturn(List.of(pac));
            Response resp = resource.delete(idAreaConocimiento, idPregunta);
            assertEquals(204, resp.getStatus());
            verify(preguntaAreaConocimientoDAO).findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);
            verify(preguntaAreaConocimientoDAO).delete(pac);
        }

        @Test
        void retorna422_cuandoParametrosNulos() {
            Response resp = resource.delete(null, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idAreaConocimiento,idPregunta", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(preguntaAreaConocimientoDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra() {
            when(preguntaAreaConocimientoDAO.findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.delete(idAreaConocimiento, idPregunta);
            assertEquals(404, resp.getStatus());
            assertEquals("Record linking area " + idAreaConocimiento + " and pregunta " + idPregunta + " not found",
                    resp.getHeaderString("Not-found-id"));
            verify(preguntaAreaConocimientoDAO).findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(preguntaAreaConocimientoDAO.findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE)).thenThrow(new RuntimeException("DB"));
            Response resp = resource.delete(idAreaConocimiento, idPregunta);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(preguntaAreaConocimientoDAO).findPreguntaByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);
        }
    }
}

