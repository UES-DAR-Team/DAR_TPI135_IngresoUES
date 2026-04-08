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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaAreaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaArea;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PruebaAreaResourceTest {

    @Mock
    PruebaAreaDAO pruebaAreaDAO;

    @Mock
    PruebaDAO pruebaDAO;

    @Mock
    AreaConocimientoDAO areaConocimientoDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    PruebaAreaResource resource;

    private static final int FIRST = 0;
    private static final int MAX = 10;

    private UUID idPrueba;
    private UUID idAreaConocimiento;
    private Prueba prueba;
    private AreaConocimiento areaConocimiento;
    private PruebaArea pa;
    private PruebaArea entity;

    @BeforeEach
    void setUp() {
        idPrueba = UUID.randomUUID();
        idAreaConocimiento = UUID.randomUUID();

        prueba = new Prueba();
        prueba.setId(idPrueba);

        areaConocimiento = new AreaConocimiento();
        areaConocimiento.setId(idAreaConocimiento);

        pa = new PruebaArea();
        pa.setId(1);
        pa.setIdPrueba(prueba);
        pa.setIdAreaConocimiento(areaConocimiento);
        pa.setFechaAsignacion(OffsetDateTime.now());
        pa.setNumPreguntas((short)5);

        entity = new PruebaArea();
    }

    @Nested
    class FindRange {
        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos() {
            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(pruebaAreaDAO.findByPrueba(idPrueba, FIRST, MAX)).thenReturn(List.of(pa));
            when(pruebaAreaDAO.count()).thenReturn(1);

            Response resp = resource.findRange(idPrueba, FIRST, MAX);

            assertEquals(200, resp.getStatus());
            List<?> entidad = (List<?>) resp.getEntity();
            assertEquals(1, entidad.size());
            assertEquals("1", resp.getHeaderString("X-Total-Count"));
            verify(pruebaDAO).findById(idPrueba);
            verify(pruebaAreaDAO).findByPrueba(idPrueba, FIRST, MAX);
            verify(pruebaAreaDAO).count();
        }

        @Test
        void retorna422_cuandoIdPruebaEsNulo() {
            Response resp = resource.findRange(null, FIRST, MAX);
            assertEquals(422, resp.getStatus());
            assertEquals("idPrueba", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO, pruebaAreaDAO);
        }

        @Test
        void retorna422_cuandoParametrosInvalidos() {
            Response resp = resource.findRange(idPrueba, -1, 0);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO, pruebaAreaDAO);
        }

        @Test
        void retorna404_cuandoPruebaNoExiste() {
            when(pruebaDAO.findById(idPrueba)).thenReturn(null);
            Response resp = resource.findRange(idPrueba, FIRST, MAX);
            assertEquals(404, resp.getStatus());
            assertEquals("Prueba with id " + idPrueba + " not found", resp.getHeaderString("Not-found"));
            verify(pruebaDAO).findById(idPrueba);
            verifyNoMoreInteractions(pruebaAreaDAO);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(pruebaAreaDAO.findByPrueba(idPrueba, FIRST, MAX)).thenThrow(new RuntimeException("DB"));
            Response resp = resource.findRange(idPrueba, FIRST, MAX);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(pruebaDAO).findById(idPrueba);
            verify(pruebaAreaDAO).findByPrueba(idPrueba, FIRST, MAX);
        }
    }

    @Nested
    class FindOne {
        @Test
        void retorna200ConEntidad_cuandoExiste() {
            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of(pa));
            Response resp = resource.findOne(idPrueba, idAreaConocimiento);
            assertEquals(200, resp.getStatus());
            assertEquals(pa, resp.getEntity());
            verify(pruebaAreaDAO).findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna422_cuandoParametrosNulos() {
            Response resp = resource.findOne(null, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idPrueba,idAreaConocimiento", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra() {
            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.findOne(idPrueba, idAreaConocimiento);
            assertEquals(404, resp.getStatus());
            assertEquals("Record linking prueba " + idPrueba + " and area " + idAreaConocimiento + " not found",
                    resp.getHeaderString("Not-found-id"));
            verify(pruebaAreaDAO).findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenThrow(new RuntimeException("DB"));
            Response resp = resource.findOne(idPrueba, idAreaConocimiento);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(pruebaAreaDAO).findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
        }
    }

    @Nested
    class Create {
        @Test
        void retorna201_cuandoEntidadValida() {
            entity.setId(null);
            AreaConocimiento bodyArea = new AreaConocimiento();
            bodyArea.setId(idAreaConocimiento);
            entity.setIdAreaConocimiento(bodyArea);

            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(areaConocimientoDAO.findById(idAreaConocimiento)).thenReturn(areaConocimiento);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/prueba/" + idPrueba + "/areaConocimiento/" + idAreaConocimiento));

            doAnswer(inv -> {
                entity.setId(42);
                return null;
            }).when(pruebaAreaDAO).create(entity);

            Response resp = resource.create(idPrueba, entity, uriInfo);
            assertEquals(201, resp.getStatus());
            assertEquals(entity, resp.getEntity());
            verify(pruebaDAO).findById(idPrueba);
            verify(areaConocimientoDAO).findById(idAreaConocimiento);
            verify(pruebaAreaDAO).create(entity);
        }

        @Test
        void retorna422_cuandoIdPruebaNulo() {
            Response resp = resource.create(null, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idPrueba", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO, areaConocimientoDAO, pruebaAreaDAO);
        }

        @Test
        void retorna422_cuandoEntidadNula() {
            Response resp = resource.create(idPrueba, null, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO, areaConocimientoDAO, pruebaAreaDAO);
        }

        @Test
        void retorna422_cuandoEntityTieneId() {
            entity.setId(5);
            Response resp = resource.create(idPrueba, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.id must be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO, areaConocimientoDAO, pruebaAreaDAO);
        }

        @Test
        void retorna422_cuandoIdAreaNoProvistoEnBody() {
            entity.setId(null);
            entity.setIdAreaConocimiento(null);
            Response resp = resource.create(idPrueba, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idAreaConocimiento must be provided in body", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO, areaConocimientoDAO, pruebaAreaDAO);
        }

        @Test
        void retorna404_cuandoPruebaNoExiste() {
            entity.setId(null);
            AreaConocimiento bodyArea = new AreaConocimiento();
            bodyArea.setId(idAreaConocimiento);
            entity.setIdAreaConocimiento(bodyArea);

            when(pruebaDAO.findById(idPrueba)).thenReturn(null);
            Response resp = resource.create(idPrueba, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("Prueba with id " + idPrueba + " not found", resp.getHeaderString("Not-found"));
            verify(pruebaDAO).findById(idPrueba);
            verifyNoMoreInteractions(areaConocimientoDAO, pruebaAreaDAO);
        }

        @Test
        void retorna404_cuandoAreaNoExiste() {
            entity.setId(null);
            AreaConocimiento bodyArea = new AreaConocimiento();
            bodyArea.setId(idAreaConocimiento);
            entity.setIdAreaConocimiento(bodyArea);

            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(areaConocimientoDAO.findById(idAreaConocimiento)).thenReturn(null);

            Response resp = resource.create(idPrueba, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("AreaConocimiento with id " + idAreaConocimiento + " not found", resp.getHeaderString("Not-found"));
            verify(pruebaDAO).findById(idPrueba);
            verify(areaConocimientoDAO).findById(idAreaConocimiento);
            verifyNoMoreInteractions(pruebaAreaDAO);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            entity.setId(null);
            AreaConocimiento bodyArea = new AreaConocimiento();
            bodyArea.setId(idAreaConocimiento);
            entity.setIdAreaConocimiento(bodyArea);

            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(areaConocimientoDAO.findById(idAreaConocimiento)).thenReturn(areaConocimiento);
            doThrow(new RuntimeException("DB")).when(pruebaAreaDAO).create(entity);

            Response resp = resource.create(idPrueba, entity, uriInfo);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(pruebaAreaDAO).create(entity);
        }
    }

    @Nested
    class Update {
        @Test
        void retorna200_cuandoActualizaNumPreguntas() {
            PruebaArea updateBody = new PruebaArea();
            updateBody.setNumPreguntas((short)7);

            PruebaArea expected = new PruebaArea();
            expected.setId(pa.getId());
            expected.setIdPrueba(prueba);
            expected.setIdAreaConocimiento(areaConocimiento);
            expected.setNumPreguntas((short)7);
            expected.setFechaAsignacion(pa.getFechaAsignacion());

            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of(pa));
            when(pruebaAreaDAO.update(any(PruebaArea.class))).thenReturn(expected);

            Response resp = resource.update(idPrueba, idAreaConocimiento, updateBody);
            assertEquals(200, resp.getStatus());
            assertEquals(expected, resp.getEntity());
            verify(pruebaAreaDAO).findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
            verify(pruebaAreaDAO).update(any(PruebaArea.class));
        }

        @Test
        void retorna422_cuandoParametrosNulos() {
            Response resp = resource.update(null, null, new PruebaArea());
            assertEquals(422, resp.getStatus());
            assertEquals("idPrueba,idAreaConocimiento", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO);
        }

        @Test
        void retorna422_cuandoEntidadNula() {
            Response resp = resource.update(idPrueba, idAreaConocimiento, null);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra() {
            PruebaArea updateBody = new PruebaArea();
            updateBody.setNumPreguntas((short)7);

            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.update(idPrueba, idAreaConocimiento, updateBody);
            assertEquals(404, resp.getStatus());
            assertEquals("Record linking prueba " + idPrueba + " and area " + idAreaConocimiento + " not found",
                    resp.getHeaderString("Not-found-id"));
            verify(pruebaAreaDAO).findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            PruebaArea updateBody = new PruebaArea();
            updateBody.setNumPreguntas((short)7);

            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenThrow(new RuntimeException("DB"));
            Response resp = resource.update(idPrueba, idAreaConocimiento, updateBody);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(pruebaAreaDAO).findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
        }
    }

    @Nested
    class Delete {
        @Test
        void retorna204_cuandoValido() {
            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of(pa));
            Response resp = resource.delete(idPrueba, idAreaConocimiento);
            assertEquals(204, resp.getStatus());
            verify(pruebaAreaDAO).findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
            verify(pruebaAreaDAO).delete(pa);
        }

        @Test
        void retorna422_cuandoParametrosNulos() {
            Response resp = resource.delete(null, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idPrueba,idAreaConocimiento", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra() {
            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.delete(idPrueba, idAreaConocimiento);
            assertEquals(404, resp.getStatus());
            assertEquals("Record linking prueba " + idPrueba + " and area " + idAreaConocimiento + " not found",
                    resp.getHeaderString("Not-found-id"));
            verify(pruebaAreaDAO).findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenThrow(new RuntimeException("DB"));
            Response resp = resource.delete(idPrueba, idAreaConocimiento);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(pruebaAreaDAO).findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
        }
    }
}

