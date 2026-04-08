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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.DistractorAreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.DistractorDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.DistractorAreaConocimiento;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DistractorAreaConocimientoResourceTest {

    @Mock
    DistractorAreaConocimientoDAO distractorAreaConocimientoDAO;

    @Mock
    DistractorDAO distractorDAO;

    @Mock
    AreaConocimientoDAO areaConocimientoDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    DistractorAreaConocimientoResource resource;

    private static final int FIRST = 0;
    private static final int MAX = 10;

    private UUID idAreaConocimiento;
    private UUID idDistractor;
    private Distractor distractor;
    private AreaConocimiento areaConocimiento;
    private DistractorAreaConocimiento dac;
    private DistractorAreaConocimiento entity;

    @BeforeEach
    void setUp() {
        idAreaConocimiento = UUID.randomUUID();
        idDistractor = UUID.randomUUID();

        distractor = new Distractor();
        distractor.setId(idDistractor);

        areaConocimiento = new AreaConocimiento();
        areaConocimiento.setId(idAreaConocimiento);

        dac = new DistractorAreaConocimiento();
        dac.setId(1);
        dac.setIdDistractor(distractor);
        dac.setIdAreaConocimiento(areaConocimiento);
        dac.setFechaAsignacion(OffsetDateTime.now());

        entity = new DistractorAreaConocimiento();
    }

    @Nested
    class FindRange {
        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos() {
            when(areaConocimientoDAO.findById(idAreaConocimiento)).thenReturn(areaConocimiento);
            when(distractorAreaConocimientoDAO.findByIdAreaConocimiento(idAreaConocimiento, FIRST, MAX)).thenReturn(List.of(dac));
            when(distractorAreaConocimientoDAO.count()).thenReturn(1);

            Response resp = resource.findRange(idAreaConocimiento, FIRST, MAX);

            assertEquals(200, resp.getStatus());
            List<?> entidad = (List<?>) resp.getEntity();
            assertEquals(1, entidad.size());
            assertEquals("1", resp.getHeaderString("X-Total-Count"));
            verify(areaConocimientoDAO).findById(idAreaConocimiento);
            verify(distractorAreaConocimientoDAO).findByIdAreaConocimiento(idAreaConocimiento, FIRST, MAX);
            verify(distractorAreaConocimientoDAO).count();
        }

        @Test
        void retorna422_cuandoIdAreaEsNulo() {
            Response resp = resource.findRange(null, FIRST, MAX);
            assertEquals(422, resp.getStatus());
            assertEquals("idAreaConocimiento", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO, distractorAreaConocimientoDAO);
        }

        @Test
        void retorna422_cuandoParametrosInvalidos() {
            Response resp = resource.findRange(idAreaConocimiento, -1, 0);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO, distractorAreaConocimientoDAO);
        }

        @Test
        void retorna404_cuandoAreaNoExiste() {
            when(areaConocimientoDAO.findById(idAreaConocimiento)).thenReturn(null);
            Response resp = resource.findRange(idAreaConocimiento, FIRST, MAX);
            assertEquals(404, resp.getStatus());
            assertEquals("AreaConocimiento with id " + idAreaConocimiento + " not found", resp.getHeaderString("Not-found"));
            verify(areaConocimientoDAO).findById(idAreaConocimiento);
            verifyNoMoreInteractions(distractorAreaConocimientoDAO);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(areaConocimientoDAO.findById(idAreaConocimiento)).thenReturn(areaConocimiento);
            when(distractorAreaConocimientoDAO.findByIdAreaConocimiento(idAreaConocimiento, FIRST, MAX)).thenThrow(new RuntimeException("DB"));
            Response resp = resource.findRange(idAreaConocimiento, FIRST, MAX);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(areaConocimientoDAO).findById(idAreaConocimiento);
            verify(distractorAreaConocimientoDAO).findByIdAreaConocimiento(idAreaConocimiento, FIRST, MAX);
        }
    }

    @Nested
    class FindOne {
        @Test
        void retorna200ConEntidad_cuandoExiste() {
            when(distractorAreaConocimientoDAO.findByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE)).thenReturn(List.of(dac));
            Response resp = resource.findOne(idAreaConocimiento, idDistractor);
            assertEquals(200, resp.getStatus());
            assertEquals(dac, resp.getEntity());
            verify(distractorAreaConocimientoDAO).findByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna422_cuandoParametrosNulos() {
            Response resp = resource.findOne(null, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idAreaConocimiento,idDistractor", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(distractorAreaConocimientoDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra() {
            when(distractorAreaConocimientoDAO.findByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.findOne(idAreaConocimiento, idDistractor);
            assertEquals(404, resp.getStatus());
            assertEquals("Record linking area " + idAreaConocimiento + " and distractor " + idDistractor + "not found",
                    resp.getHeaderString("Not-found-id"));
            verify(distractorAreaConocimientoDAO).findByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(distractorAreaConocimientoDAO.findByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE)).thenThrow(new RuntimeException("DB"));
            Response resp = resource.findOne(idAreaConocimiento, idDistractor);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(distractorAreaConocimientoDAO).findByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);
        }
    }

    @Nested
    class Create {
        @Test
        void retorna201_cuandoEntidadValida() {
            entity.setId(null);
            Distractor bodyDist = new Distractor();
            bodyDist.setId(idDistractor);
            entity.setIdDistractor(bodyDist);

            when(areaConocimientoDAO.findById(idAreaConocimiento)).thenReturn(areaConocimiento);
            when(distractorDAO.findById(idDistractor)).thenReturn(distractor);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/areaConocimiento/" + idAreaConocimiento + "/distractor/" + idDistractor));

            doAnswer(inv -> {
                entity.setId(42);
                return null;
            }).when(distractorAreaConocimientoDAO).create(entity);

            Response resp = resource.create(idAreaConocimiento, entity, uriInfo);
            assertEquals(201, resp.getStatus());
            assertEquals(entity, resp.getEntity());
            verify(areaConocimientoDAO).findById(idAreaConocimiento);
            verify(distractorDAO).findById(idDistractor);
            verify(distractorAreaConocimientoDAO).create(entity);
        }

        @Test
        void retorna422_cuandoIdAreaNulo() {
            Response resp = resource.create(null, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idAreaConocimiento", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO, distractorDAO, distractorAreaConocimientoDAO);
        }

        @Test
        void retorna422_cuandoEntidadNula() {
            Response resp = resource.create(idAreaConocimiento, null, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO, distractorDAO, distractorAreaConocimientoDAO);
        }

        @Test
        void retorna422_cuandoEntityTieneId() {
            entity.setId(5);
            Response resp = resource.create(idAreaConocimiento, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.id must be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO, distractorDAO, distractorAreaConocimientoDAO);
        }

        @Test
        void retorna422_cuandoIdDistractorNoProvistoEnBody() {
            entity.setId(null);
            entity.setIdDistractor(null);
            Response resp = resource.create(idAreaConocimiento, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.id must be provider in body", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO, distractorDAO, distractorAreaConocimientoDAO);
        }

        @Test
        void retorna404_cuandoAreaNoExiste() {
            entity.setId(null);
            Distractor bodyDist = new Distractor();
            bodyDist.setId(idDistractor);
            entity.setIdDistractor(bodyDist);

            when(areaConocimientoDAO.findById(idAreaConocimiento)).thenReturn(null);
            Response resp = resource.create(idAreaConocimiento, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("AreaConocimiento with id " + idAreaConocimiento + " not found", resp.getHeaderString("Not-found"));
            verify(areaConocimientoDAO).findById(idAreaConocimiento);
            verifyNoMoreInteractions(distractorDAO, distractorAreaConocimientoDAO);
        }

        @Test
        void retorna404_cuandoDistractorNoExiste() {
            entity.setId(null);
            Distractor bodyDist = new Distractor();
            bodyDist.setId(idDistractor);
            entity.setIdDistractor(bodyDist);

            when(areaConocimientoDAO.findById(idAreaConocimiento)).thenReturn(areaConocimiento);
            when(distractorDAO.findById(idDistractor)).thenReturn(null);

            Response resp = resource.create(idAreaConocimiento, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("Distractor with id " + idDistractor + " not found", resp.getHeaderString("Not-found"));
            verify(areaConocimientoDAO).findById(idAreaConocimiento);
            verify(distractorDAO).findById(idDistractor);
            verifyNoMoreInteractions(distractorAreaConocimientoDAO);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            entity.setId(null);
            Distractor bodyDist = new Distractor();
            bodyDist.setId(idDistractor);
            entity.setIdDistractor(bodyDist);

            when(areaConocimientoDAO.findById(idAreaConocimiento)).thenReturn(areaConocimiento);
            when(distractorDAO.findById(idDistractor)).thenReturn(distractor);
            doThrow(new RuntimeException("DB")).when(distractorAreaConocimientoDAO).create(entity);

            Response resp = resource.create(idAreaConocimiento, entity, uriInfo);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(distractorAreaConocimientoDAO).create(entity);
        }
    }

    @Nested
    class Delete {
        @Test
        void retorna204_cuandoValido() {
            when(distractorAreaConocimientoDAO.findByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE)).thenReturn(List.of(dac));
            Response resp = resource.delete(idAreaConocimiento, idDistractor);
            assertEquals(204, resp.getStatus());
            verify(distractorAreaConocimientoDAO).findByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);
            verify(distractorAreaConocimientoDAO).delete(dac);
        }

        @Test
        void retorna422_cuandoParametrosNulos() {
            Response resp = resource.delete(null, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idAreaConocimiento,idDistractor", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(distractorAreaConocimientoDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra() {
            when(distractorAreaConocimientoDAO.findByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.delete(idAreaConocimiento, idDistractor);
            assertEquals(404, resp.getStatus());
            assertEquals("Record linking area " + idAreaConocimiento + " and distractor " + idDistractor + "not found",
                    resp.getHeaderString("Not-found-id"));
            verify(distractorAreaConocimientoDAO).findByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(distractorAreaConocimientoDAO.findByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE)).thenThrow(new RuntimeException("DB"));
            Response resp = resource.delete(idAreaConocimiento, idDistractor);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(distractorAreaConocimientoDAO).findByIdAreaConocimiento(idAreaConocimiento, 0, Integer.MAX_VALUE);
        }
    }
}