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

    @Mock PruebaAreaDAO pruebaAreaDAO;
    @Mock PruebaDAO pruebaDAO;
    @Mock AreaConocimientoDAO areaConocimientoDAO;
    @Mock UriInfo uriInfo;
    @Mock UriBuilder uriBuilder;

    @InjectMocks
    PruebaAreaResource resource;

    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final int EXCEEDMAX = 11;

    private UUID idPrueba;
    private UUID idArea;
    private Prueba prueba;
    private AreaConocimiento area;
    private PruebaArea pruebaArea;
    private PruebaArea entity;

    @BeforeEach
    void setUp() {
        idPrueba = UUID.randomUUID();
        idArea = UUID.randomUUID();

        prueba = new Prueba();
        prueba.setId(idPrueba);

        area = new AreaConocimiento();
        area.setId(idArea);

        pruebaArea = new PruebaArea();
        pruebaArea.setId(1);
        pruebaArea.setIdPrueba(prueba);
        pruebaArea.setIdAreaConocimiento(area);

        entity = new PruebaArea();
    }

    @Nested
    class FindRange {

        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos() {
            List<PruebaArea> lista = List.of(pruebaArea);
            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(pruebaAreaDAO.findByPrueba(idPrueba, FIRST, MAX)).thenReturn(lista);
            when(pruebaAreaDAO.count()).thenReturn(1);

            Response response = resource.findRange(idPrueba, FIRST, MAX);

            assertEquals(200, response.getStatus());
            assertEquals(lista, response.getEntity());
            assertEquals("1", response.getHeaderString("X-Total-Count"));
            verify(pruebaDAO).findById(idPrueba);
            verify(pruebaAreaDAO).findByPrueba(idPrueba, FIRST, MAX);
            verify(pruebaAreaDAO).count();
        }

        @Test
        void retorna422_cuandoIdPruebaNulo() {
            Response response = resource.findRange(null, FIRST, MAX);

            assertEquals(422, response.getStatus());
            assertEquals("idPrueba", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO, pruebaAreaDAO);
        }

        @Test
        void retorna422_cuandoFirstInvalido() {
            Response response = resource.findRange(idPrueba, INVALIDFIRST, MAX);

            assertEquals(422, response.getStatus());
            assertEquals("first", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO, pruebaAreaDAO);
        }

        @Test
        void retorna422_cuandoMaxInvalido() {
            Response response = resource.findRange(idPrueba, FIRST, INVALIDMAX);

            assertEquals(422, response.getStatus());
            assertEquals("max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO, pruebaAreaDAO);
        }

        @Test
        void retorna422_cuandoMaxExcedeLimite() {
            Response response = resource.findRange(idPrueba, FIRST, EXCEEDMAX);

            assertEquals(422, response.getStatus());
            assertEquals("max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO, pruebaAreaDAO);
        }

        @Test
        void retorna404_cuandoPruebaNoExiste() {
            when(pruebaDAO.findById(idPrueba)).thenReturn(null);

            Response response = resource.findRange(idPrueba, FIRST, MAX);

            assertEquals(404, response.getStatus());
            assertEquals("Prueba with id " + idPrueba + " not found", response.getHeaderString("Not-found-id"));
            verify(pruebaDAO).findById(idPrueba);
            verifyNoInteractions(pruebaAreaDAO);
        }
    }

    @Nested
    class FindOne {

        @Test
        void retorna200ConEntidad_cuandoParametrosSonValidos() {
            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of(pruebaArea));

            Response response = resource.findOne(idPrueba, idArea);

            assertEquals(200, response.getStatus());
            assertEquals(pruebaArea, response.getEntity());
        }

        @Test
        void retorna422_cuandoIdPruebaNulo() {
            Response response = resource.findOne(null, idArea);

            assertEquals(422, response.getStatus());
            assertEquals("idPrueba,idAreaConocimiento", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO);
        }

        @Test
        void retorna422_cuandoIdAreaNulo() {
            Response response = resource.findOne(idPrueba, null);

            assertEquals(422, response.getStatus());
            assertEquals("idPrueba,idAreaConocimiento", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO);
        }

        @Test
        void retorna404_cuandoListaVacia() {
            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of());

            Response response = resource.findOne(idPrueba, idArea);

            assertEquals(404, response.getStatus());
            assertEquals("Record with id linking prueba " + idPrueba + " and area " + idArea + " not found", response.getHeaderString("Not-found-id"));
        }

        @Test
        void retorna404_cuandoAreaNoCoincideEnLista() {
            AreaConocimiento otraArea = new AreaConocimiento();
            otraArea.setId(UUID.randomUUID());
            pruebaArea.setIdAreaConocimiento(otraArea);

            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of(pruebaArea));

            Response response = resource.findOne(idPrueba, idArea);

            assertEquals(404, response.getStatus());
        }

        @Test
        void retorna404_cuandoStreamContieneElementosConAreaNula() {
            PruebaArea paConAreaNula = new PruebaArea();
            paConAreaNula.setIdAreaConocimiento(null);

            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of(paConAreaNula));

            Response response = resource.findOne(idPrueba, idArea);

            assertEquals(404, response.getStatus());
        }
    }

    @Nested
    class Create {

        @Test
        void retorna201_cuandoEntidadEsValidaYAsignaFechaAutomatica() {
            entity.setIdAreaConocimiento(area);
            entity.setFechaAsignacion(null);

            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(areaConocimientoDAO.findById(idArea)).thenReturn(area);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("test"));

            Response response = resource.create(idPrueba, entity, uriInfo);

            assertEquals(201, response.getStatus());
            assertNull(response.getEntity());
            assertNotNull(entity.getFechaAsignacion());
            verify(pruebaAreaDAO).create(entity);
        }

        @Test
        void retorna201_cuandoEntidadEsValidaYRespetaFechaExistente() {
            entity.setIdAreaConocimiento(area);
            OffsetDateTime fechaPrevia = OffsetDateTime.now().minusDays(5);
            entity.setFechaAsignacion(fechaPrevia);

            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(areaConocimientoDAO.findById(idArea)).thenReturn(area);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("test"));

            resource.create(idPrueba, entity, uriInfo);

            assertTrue(fechaPrevia.isEqual(entity.getFechaAsignacion()));
            verify(pruebaAreaDAO).create(entity);
        }

        @Test
        void retorna422_cuandoIdPruebaNulo() {
            assertEquals(422, resource.create(null, entity, uriInfo).getStatus());
            verifyNoInteractions(pruebaAreaDAO);
        }

        @Test
        void retorna422_cuandoEntityNulo() {
            assertEquals(422, resource.create(idPrueba, null, uriInfo).getStatus());
            verifyNoInteractions(pruebaAreaDAO);
        }

        @Test
        void retorna422_cuandoEntityTieneId() {
            entity.setId(1);
            assertEquals(422, resource.create(idPrueba, entity, uriInfo).getStatus());
            verifyNoInteractions(pruebaAreaDAO);
        }

        @Test
        void retorna422_cuandoIdAreaConocimientoNoProporcionado() {
            assertEquals(422, resource.create(idPrueba, entity, uriInfo).getStatus());
            verifyNoInteractions(pruebaAreaDAO);
        }

        @Test
        void retorna422_cuandoAreaConocimientoVieneSinIdInterno() {
            AreaConocimiento areaSinId = new AreaConocimiento();
            areaSinId.setId(null);
            entity.setIdAreaConocimiento(areaSinId);

            Response response = resource.create(idPrueba, entity, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("idAreaConocimiento must be provided in body", response.getHeaderString("Missing-parameter"));
        }

        @Test
        void retorna404_cuandoPruebaNoExiste() {
            entity.setIdAreaConocimiento(area);
            when(pruebaDAO.findById(idPrueba)).thenReturn(null);

            assertEquals(404, resource.create(idPrueba, entity, uriInfo).getStatus());
            verifyNoInteractions(pruebaAreaDAO);
        }

        @Test
        void retorna404_cuandoAreaConocimientoNoExiste() {
            entity.setIdAreaConocimiento(area);
            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(areaConocimientoDAO.findById(idArea)).thenReturn(null);

            assertEquals(404, resource.create(idPrueba, entity, uriInfo).getStatus());
            verifyNoInteractions(pruebaAreaDAO);
        }
    }

    @Nested
    class Update {

        @Test
        void retorna200_cuandoEntidadEsValida() {
            entity.setNumPreguntas((short) 5);
            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of(pruebaArea));
            when(pruebaAreaDAO.update(pruebaArea)).thenReturn(pruebaArea);

            Response response = resource.update(idPrueba, idArea, entity);

            assertEquals(200, response.getStatus());
            assertEquals(pruebaArea, response.getEntity());
            assertEquals((short) 5, pruebaArea.getNumPreguntas());
            verify(pruebaAreaDAO).update(pruebaArea);
        }

        @Test
        void retorna200_cuandoActualizaConNumPreguntasNulo() {
            entity.setNumPreguntas(null);
            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of(pruebaArea));
            when(pruebaAreaDAO.update(any())).thenReturn(pruebaArea);

            Response response = resource.update(idPrueba, idArea, entity);

            assertEquals(200, response.getStatus());
        }

        @Test
        void retorna422_cuandoIdPruebaNulo() {
            Response response = resource.update(null, idArea, entity);

            assertEquals(422, response.getStatus());
            assertEquals("idPrueba,idAreaConocimiento", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO);
        }

        @Test
        void retorna422_cuandoIdAreaNulo() {
            Response response = resource.update(idPrueba, null, entity);

            assertEquals(422, response.getStatus());
            assertEquals("idPrueba,idAreaConocimiento", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO);
        }

        @Test
        void retorna422_cuandoEntityNulo() {
            Response response = resource.update(idPrueba, idArea, null);

            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO);
        }

        @Test
        void retorna404_cuandoListaVacia() {
            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of());

            Response response = resource.update(idPrueba, idArea, entity);

            assertEquals(404, response.getStatus());
            verify(pruebaAreaDAO, never()).update(any());
        }

        @Test
        void retorna404_cuandoAreaNoCoincideEnLista() {
            AreaConocimiento otraArea = new AreaConocimiento();
            otraArea.setId(UUID.randomUUID());
            pruebaArea.setIdAreaConocimiento(otraArea);

            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of(pruebaArea));

            Response response = resource.update(idPrueba, idArea, entity);

            assertEquals(404, response.getStatus());
            verify(pruebaAreaDAO, never()).update(any());
        }

        @Test
        void retorna404_cuandoStreamContieneElementosConAreaNula() {
            PruebaArea paNula = new PruebaArea();
            paNula.setIdAreaConocimiento(null);

            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of(paNula));

            Response response = resource.update(idPrueba, idArea, entity);

            assertEquals(404, response.getStatus());
            verify(pruebaAreaDAO, never()).update(any());
        }
    }

    @Nested
    class Delete {

        @Test
        void retorna204_cuandoRegistroExiste() {
            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of(pruebaArea));

            Response response = resource.delete(idPrueba, idArea);

            assertEquals(204, response.getStatus());
            verify(pruebaAreaDAO).delete(pruebaArea);
        }

        @Test
        void retorna422_cuandoIdPruebaNulo() {
            Response response = resource.delete(null, idArea);

            assertEquals(422, response.getStatus());
            assertEquals("idPrueba,idAreaConocimiento", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO);
        }

        @Test
        void retorna422_cuandoIdAreaNulo() {
            Response response = resource.delete(idPrueba, null);

            assertEquals(422, response.getStatus());
            assertEquals("idPrueba,idAreaConocimiento", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO);
        }

        @Test
        void retorna404_cuandoListaVacia() {
            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of());

            Response response = resource.delete(idPrueba, idArea);

            assertEquals(404, response.getStatus());
            verify(pruebaAreaDAO, never()).delete(any());
        }

        @Test
        void retorna404_cuandoAreaNoCoincideEnLista() {
            AreaConocimiento otraArea = new AreaConocimiento();
            otraArea.setId(UUID.randomUUID());
            pruebaArea.setIdAreaConocimiento(otraArea);

            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of(pruebaArea));

            Response response = resource.delete(idPrueba, idArea);

            assertEquals(404, response.getStatus());
            verify(pruebaAreaDAO, never()).delete(any());
        }

        @Test
        void retorna404_cuandoStreamContieneElementosConAreaNula() {
            PruebaArea paNula = new PruebaArea();
            paNula.setIdAreaConocimiento(null);

            when(pruebaAreaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of(paNula));

            Response response = resource.delete(idPrueba, idArea);

            assertEquals(404, response.getStatus());
            verify(pruebaAreaDAO, never()).delete(any());
        }
    }
}