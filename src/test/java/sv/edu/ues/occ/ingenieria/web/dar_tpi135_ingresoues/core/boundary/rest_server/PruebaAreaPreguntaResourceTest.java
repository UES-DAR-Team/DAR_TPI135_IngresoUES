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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaAreaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaAreaPreguntaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaArea;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPregunta;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PruebaAreaPreguntaResourceTest {

    @Mock PruebaAreaPreguntaDAO pruebaAreaPreguntaDAO;
    @Mock PruebaAreaDAO pruebaAreaDAO;
    @Mock PreguntaDAO preguntaDAO;
    @Mock UriInfo uriInfo;
    @Mock UriBuilder uriBuilder;

    @InjectMocks
    PruebaAreaPreguntaResource resource;

    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final int EXCEEDMAX = 11;

    private Integer idPruebaArea;
    private UUID idPregunta;
    private PruebaArea pruebaArea;
    private Pregunta pregunta;
    private PruebaAreaPregunta pap;
    private PruebaAreaPregunta entity;

    @BeforeEach
    void setUp() {
        idPruebaArea = 1;
        idPregunta = UUID.randomUUID();

        pruebaArea = new PruebaArea();
        pruebaArea.setId(idPruebaArea);

        pregunta = new Pregunta();
        pregunta.setId(idPregunta);

        pap = new PruebaAreaPregunta();
        pap.setId(10);
        pap.setIdPruebaArea(pruebaArea);
        pap.setIdPregunta(pregunta);

        entity = new PruebaAreaPregunta();
    }

    @Nested
    class FindRange {

        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos() {
            List<PruebaAreaPregunta> lista = List.of(pap);
            when(pruebaAreaDAO.findById(idPruebaArea)).thenReturn(pruebaArea);
            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, FIRST, MAX)).thenReturn(lista);
            when(pruebaAreaPreguntaDAO.count()).thenReturn(1);

            Response response = resource.findRange(idPruebaArea, FIRST, MAX);

            assertEquals(200, response.getStatus());
            assertEquals(lista, response.getEntity());
            assertEquals("1", response.getHeaderString("X-Total-Count"));
            verify(pruebaAreaDAO).findById(idPruebaArea);
            verify(pruebaAreaPreguntaDAO).findByPruebaArea(idPruebaArea, FIRST, MAX);
            verify(pruebaAreaPreguntaDAO).count();
        }

        @Test
        void retorna422_cuandoIdPruebaAreaNulo() {
            Response response = resource.findRange(null, FIRST, MAX);

            assertEquals(422, response.getStatus());
            assertEquals("idPruebaArea", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO, pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoFirstInvalido() {
            Response response = resource.findRange(idPruebaArea, INVALIDFIRST, MAX);

            assertEquals(422, response.getStatus());
            assertEquals("first", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO, pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoMaxInvalido() {
            Response response = resource.findRange(idPruebaArea, FIRST, INVALIDMAX);

            assertEquals(422, response.getStatus());
            assertEquals("max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO, pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoMaxExcedeLimite() {
            Response response = resource.findRange(idPruebaArea, FIRST, EXCEEDMAX);

            assertEquals(422, response.getStatus());
            assertEquals("max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO, pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna404_cuandoPruebaAreaNoExiste() {
            when(pruebaAreaDAO.findById(idPruebaArea)).thenReturn(null);

            Response response = resource.findRange(idPruebaArea, FIRST, MAX);

            assertEquals(404, response.getStatus());
            assertEquals("PruebaArea with id " + idPruebaArea + " not found", response.getHeaderString("Not-found-id"));
            verify(pruebaAreaDAO).findById(idPruebaArea);
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }
    }

    @Nested
    class FindOne {

        @Test
        void retorna200ConEntidad_cuandoParametrosSonValidos() {
            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of(pap));

            Response response = resource.findOne(idPruebaArea, idPregunta);

            assertEquals(200, response.getStatus());
            assertEquals(pap, response.getEntity());
        }

        @Test
        void retorna422_cuandoIdPruebaAreaNulo() {
            Response response = resource.findOne(null, idPregunta);

            assertEquals(422, response.getStatus());
            assertEquals("idPruebaArea", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoIdPreguntaNulo() {
            Response response = resource.findOne(idPruebaArea, null);

            assertEquals(422, response.getStatus());
            assertEquals("idPregunta", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna404_cuandoListaVacia() {
            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of());

            Response response = resource.findOne(idPruebaArea, idPregunta);

            assertEquals(404, response.getStatus());
            assertEquals("Record with id linking pruebaArea " + idPruebaArea + " and pregunta " + idPregunta + " not found",
                    response.getHeaderString("Not-found-id"));
        }

        @Test
        void retorna404_cuandoPreguntaNoCoincideEnLista() {
            Pregunta otraPregunta = new Pregunta();
            otraPregunta.setId(UUID.randomUUID());
            pap.setIdPregunta(otraPregunta);

            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of(pap));

            Response response = resource.findOne(idPruebaArea, idPregunta);

            assertEquals(404, response.getStatus());
        }

        @Test
        void retorna404_cuandoStreamContieneElementosConPreguntaNula() {
            PruebaAreaPregunta sinPregunta = new PruebaAreaPregunta();
            sinPregunta.setIdPregunta(null);

            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of(sinPregunta));

            Response response = resource.findOne(idPruebaArea, idPregunta);

            assertEquals(404, response.getStatus());
        }
    }

    @Nested
    class Create {

        @Test
        void retorna201_cuandoEntidadEsValida() {
            entity.setIdPregunta(pregunta);

            when(pruebaAreaDAO.findById(idPruebaArea)).thenReturn(pruebaArea);
            when(preguntaDAO.findById(idPregunta)).thenReturn(pregunta);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("test"));

            Response response = resource.create(idPruebaArea, entity, uriInfo);

            assertEquals(201, response.getStatus());
            assertNull(response.getEntity());
            assertNotNull(entity.getFechaAsignacion());
            verify(pruebaAreaPreguntaDAO).create(entity);
        }

        @Test
        void retorna422_cuandoIdPruebaAreaNulo() {
            Response response = resource.create(null, entity, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("idPruebaArea", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoEntityNulo() {
            Response response = resource.create(idPruebaArea, null, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoEntityTieneId() {
            entity.setId(1);
            Response response = resource.create(idPruebaArea, entity, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("entity.id must be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoIdPreguntaNoProporcionado() {
            Response response = resource.create(idPruebaArea, entity, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("idPregunta must be provided in body", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoPreguntaVieneSinIdInterno() {
            Pregunta preguntaSinId = new Pregunta();
            preguntaSinId.setId(null);
            entity.setIdPregunta(preguntaSinId);

            Response response = resource.create(idPruebaArea, entity, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("idPregunta must be provided in body", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna404_cuandoPruebaAreaNoExiste() {
            entity.setIdPregunta(pregunta);
            when(pruebaAreaDAO.findById(idPruebaArea)).thenReturn(null);

            Response response = resource.create(idPruebaArea, entity, uriInfo);

            assertEquals(404, response.getStatus());
            assertEquals("PruebaArea with id " + idPruebaArea + " not found", response.getHeaderString("Not-found-id"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna404_cuandoPreguntaNoExiste() {
            entity.setIdPregunta(pregunta);
            when(pruebaAreaDAO.findById(idPruebaArea)).thenReturn(pruebaArea);
            when(preguntaDAO.findById(idPregunta)).thenReturn(null);

            Response response = resource.create(idPruebaArea, entity, uriInfo);

            assertEquals(404, response.getStatus());
            assertEquals("Pregunta with id " + idPregunta + " not found", response.getHeaderString("Not-found-id"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }
    }

    @Nested
    class Update {

        @Test
        void retorna200_cuandoEntidadEsValida() {
            entity.setOrden((short) 3);
            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of(pap));
            when(pruebaAreaPreguntaDAO.update(pap)).thenReturn(pap);

            Response response = resource.update(idPruebaArea, idPregunta, entity);

            assertEquals(200, response.getStatus());
            assertEquals(pap, response.getEntity());
            assertEquals((short) 3, pap.getOrden());
            verify(pruebaAreaPreguntaDAO).update(pap);
        }

        @Test
        void retorna200_cuandoOrdenEsNulo() {
            entity.setOrden(null);
            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of(pap));
            when(pruebaAreaPreguntaDAO.update(pap)).thenReturn(pap);

            Response response = resource.update(idPruebaArea, idPregunta, entity);

            assertEquals(200, response.getStatus());
        }

        @Test
        void retorna422_cuandoIdPruebaAreaNulo() {
            Response response = resource.update(null, idPregunta, entity);

            assertEquals(422, response.getStatus());
            assertEquals("idPruebaArea", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoIdPreguntaNulo() {
            Response response = resource.update(idPruebaArea, null, entity);

            assertEquals(422, response.getStatus());
            assertEquals("idPregunta", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoEntityNulo() {
            Response response = resource.update(idPruebaArea, idPregunta, null);

            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna404_cuandoListaVacia() {
            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of());

            Response response = resource.update(idPruebaArea, idPregunta, entity);

            assertEquals(404, response.getStatus());
            verify(pruebaAreaPreguntaDAO, never()).update(any());
        }

        @Test
        void retorna404_cuandoPreguntaNoCoincideEnLista() {
            Pregunta otraPregunta = new Pregunta();
            otraPregunta.setId(UUID.randomUUID());
            pap.setIdPregunta(otraPregunta);

            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of(pap));

            Response response = resource.update(idPruebaArea, idPregunta, entity);

            assertEquals(404, response.getStatus());
            verify(pruebaAreaPreguntaDAO, never()).update(any());
        }

        @Test
        void retorna404_cuandoStreamContieneElementosConPreguntaNula() {
            PruebaAreaPregunta sinPregunta = new PruebaAreaPregunta();
            sinPregunta.setIdPregunta(null);

            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of(sinPregunta));

            Response response = resource.update(idPruebaArea, idPregunta, entity);

            assertEquals(404, response.getStatus());
            verify(pruebaAreaPreguntaDAO, never()).update(any());
        }
    }

    @Nested
    class Delete {

        @Test
        void retorna204_cuandoRegistroExiste() {
            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of(pap));

            Response response = resource.delete(idPruebaArea, idPregunta);

            assertEquals(204, response.getStatus());
            verify(pruebaAreaPreguntaDAO).delete(pap);
        }

        @Test
        void retorna422_cuandoIdPruebaAreaNulo() {
            Response response = resource.delete(null, idPregunta);

            assertEquals(422, response.getStatus());
            assertEquals("idPruebaArea", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoIdPreguntaNulo() {
            Response response = resource.delete(idPruebaArea, null);

            assertEquals(422, response.getStatus());
            assertEquals("idPregunta", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna404_cuandoListaVacia() {
            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of());

            Response response = resource.delete(idPruebaArea, idPregunta);

            assertEquals(404, response.getStatus());
            verify(pruebaAreaPreguntaDAO, never()).delete(any());
        }

        @Test
        void retorna404_cuandoPreguntaNoCoincideEnLista() {
            Pregunta otraPregunta = new Pregunta();
            otraPregunta.setId(UUID.randomUUID());
            pap.setIdPregunta(otraPregunta);

            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of(pap));

            Response response = resource.delete(idPruebaArea, idPregunta);

            assertEquals(404, response.getStatus());
            verify(pruebaAreaPreguntaDAO, never()).delete(any());
        }

        @Test
        void retorna404_cuandoStreamContieneElementosConPreguntaNula() {
            PruebaAreaPregunta sinPregunta = new PruebaAreaPregunta();
            sinPregunta.setIdPregunta(null);

            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of(sinPregunta));

            Response response = resource.delete(idPruebaArea, idPregunta);

            assertEquals(404, response.getStatus());
            verify(pruebaAreaPreguntaDAO, never()).delete(any());
        }
    }
}