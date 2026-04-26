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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaArea;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPregunta;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Pregunta;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PruebaAreaPreguntaResourceTest {

    @Mock
    PruebaAreaPreguntaDAO pruebaAreaPreguntaDAO;

    @Mock
    PruebaAreaDAO pruebaAreaDAO;

    @Mock
    PreguntaDAO preguntaDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    PruebaAreaPreguntaResource resource;

    private static final int FIRST = 0;
    private static final int MAX = 10;

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
        pruebaArea.setId(1);

        pregunta = new Pregunta();
        pregunta.setId(idPregunta);

        pap = new PruebaAreaPregunta();
        pap.setId(1);
        pap.setIdPruebaArea(pruebaArea);
        pap.setIdPregunta(pregunta);
        pap.setFechaAsignacion(OffsetDateTime.now());
        pap.setOrden((short)2);

        entity = new PruebaAreaPregunta();
    }

    @Nested
    class FindRange {
        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos() {
            when(pruebaAreaDAO.findById(idPruebaArea)).thenReturn(pruebaArea);
            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, FIRST, MAX)).thenReturn(List.of(pap));
            when(pruebaAreaPreguntaDAO.count()).thenReturn(1);

            Response resp = resource.findRange(idPruebaArea, FIRST, MAX);

            assertEquals(200, resp.getStatus());
            List<?> entidad = (List<?>) resp.getEntity();
            assertEquals(1, entidad.size());
            assertEquals("1", resp.getHeaderString("X-Total-Count"));
            verify(pruebaAreaDAO).findById(idPruebaArea);
            verify(pruebaAreaPreguntaDAO).findByPruebaArea(idPruebaArea, FIRST, MAX);
            verify(pruebaAreaPreguntaDAO).count();
        }

        @Test
        void retorna422_cuandoIdPruebaAreaEsNulo() {
            Response resp = resource.findRange(null, FIRST, MAX);
            assertEquals(422, resp.getStatus());
            assertEquals("idPruebaArea", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO, pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoParametrosInvalidos() {
            Response resp = resource.findRange(idPruebaArea, -1, 0);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO, pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna404_cuandoPruebaAreaNoExiste() {
            when(pruebaAreaDAO.findById(idPruebaArea)).thenReturn(null);
            Response resp = resource.findRange(idPruebaArea, FIRST, MAX);
            assertEquals(404, resp.getStatus());
            assertEquals("PruebaArea with id " + idPruebaArea + " not found", resp.getHeaderString("Not-found"));
            verify(pruebaAreaDAO).findById(idPruebaArea);
            verifyNoMoreInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(pruebaAreaDAO.findById(idPruebaArea)).thenReturn(pruebaArea);
            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, FIRST, MAX)).thenThrow(new RuntimeException("DB"));
            Response resp = resource.findRange(idPruebaArea, FIRST, MAX);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(pruebaAreaDAO).findById(idPruebaArea);
            verify(pruebaAreaPreguntaDAO).findByPruebaArea(idPruebaArea, FIRST, MAX);
        }

        @Test
        void retorna422_cuandoMaxExcedeLimite() {
            Response resp = resource.findRange(idPruebaArea, 0, 11);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO, pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoMaxEsCero() {
            Response resp = resource.findRange(idPruebaArea, 0, 0);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO, pruebaAreaPreguntaDAO);
        }
    }

    @Nested
    class FindOne {
        @Test
        void retorna200ConEntidad_cuandoExiste() {
            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of(pap));
            Response resp = resource.findOne(idPruebaArea, idPregunta);
            assertEquals(200, resp.getStatus());
            assertEquals(pap, resp.getEntity());
            verify(pruebaAreaPreguntaDAO).findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna422_cuandoParametrosNulos() {
            Response resp = resource.findOne(null, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idPruebaArea,idPregunta", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra() {
            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.findOne(idPruebaArea, idPregunta);
            assertEquals(404, resp.getStatus());
            assertEquals("Record linking pruebaArea " + idPruebaArea + " and pregunta " + idPregunta + " not found",
                    resp.getHeaderString("Not-found-id"));
            verify(pruebaAreaPreguntaDAO).findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenThrow(new RuntimeException("DB"));
            Response resp = resource.findOne(idPruebaArea, idPregunta);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(pruebaAreaPreguntaDAO).findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna404_cuandoPreguntaDelRegistroEsNull() {
            PruebaAreaPregunta sinPregunta = new PruebaAreaPregunta();
            sinPregunta.setId(2);
            sinPregunta.setIdPregunta(null);

            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE))
                    .thenReturn(List.of(sinPregunta));

            Response resp = resource.findOne(idPruebaArea, idPregunta);
            assertEquals(404, resp.getStatus());
            verify(pruebaAreaPreguntaDAO).findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna404_cuandoIdPreguntaNoCoincide() {
            PruebaAreaPregunta otraPregunta = new PruebaAreaPregunta();
            Pregunta preguntaDiferente = new Pregunta();
            preguntaDiferente.setId(UUID.randomUUID());
            otraPregunta.setIdPregunta(preguntaDiferente);

            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE))
                    .thenReturn(List.of(otraPregunta));

            Response resp = resource.findOne(idPruebaArea, idPregunta);
            assertEquals(404, resp.getStatus());
            verify(pruebaAreaPreguntaDAO).findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna422_cuandoSoloIdPruebaAreaEsNulo() {
            Response resp = resource.findOne(null, idPregunta);
            assertEquals(422, resp.getStatus());
            assertEquals("idPruebaArea,idPregunta", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoSoloIdPreguntaEsNulo() {
            Response resp = resource.findOne(idPruebaArea, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idPruebaArea,idPregunta", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
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

            when(pruebaAreaDAO.findById(idPruebaArea)).thenReturn(pruebaArea);
            when(preguntaDAO.findById(idPregunta)).thenReturn(pregunta);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/pruebaArea/" + idPruebaArea + "/pregunta/" + idPregunta));

            doAnswer(inv -> {
                entity.setId(42);
                return null;
            }).when(pruebaAreaPreguntaDAO).create(entity);

            Response resp = resource.create(idPruebaArea, entity, uriInfo);
            assertEquals(201, resp.getStatus());
            assertEquals(entity, resp.getEntity());
            verify(pruebaAreaDAO).findById(idPruebaArea);
            verify(preguntaDAO).findById(idPregunta);
            verify(pruebaAreaPreguntaDAO).create(entity);
        }

        @Test
        void retorna422_cuandoIdPruebaAreaNulo() {
            Response resp = resource.create(null, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idPruebaArea", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO, preguntaDAO, pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoEntidadNula() {
            Response resp = resource.create(idPruebaArea, null, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO, preguntaDAO, pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoEntityTieneId() {
            entity.setId(5);
            Response resp = resource.create(idPruebaArea, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.id must be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO, preguntaDAO, pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoIdPreguntaNoProvistoEnBody() {
            entity.setId(null);
            entity.setIdPregunta(null);
            Response resp = resource.create(idPruebaArea, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idPregunta must be provided in body", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO, preguntaDAO, pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna404_cuandoPruebaAreaNoExiste() {
            entity.setId(null);
            Pregunta bodyPreg = new Pregunta();
            bodyPreg.setId(idPregunta);
            entity.setIdPregunta(bodyPreg);

            when(pruebaAreaDAO.findById(idPruebaArea)).thenReturn(null);
            Response resp = resource.create(idPruebaArea, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("PruebaArea with id " + idPruebaArea + " not found", resp.getHeaderString("Not-found"));
            verify(pruebaAreaDAO).findById(idPruebaArea);
            verifyNoMoreInteractions(preguntaDAO, pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna404_cuandoPreguntaNoExiste() {
            entity.setId(null);
            Pregunta bodyPreg = new Pregunta();
            bodyPreg.setId(idPregunta);
            entity.setIdPregunta(bodyPreg);

            when(pruebaAreaDAO.findById(idPruebaArea)).thenReturn(pruebaArea);
            when(preguntaDAO.findById(idPregunta)).thenReturn(null);

            Response resp = resource.create(idPruebaArea, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("Pregunta with id " + idPregunta + " not found", resp.getHeaderString("Not-found"));
            verify(pruebaAreaDAO).findById(idPruebaArea);
            verify(preguntaDAO).findById(idPregunta);
            verifyNoMoreInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            entity.setId(null);
            Pregunta bodyPreg = new Pregunta();
            bodyPreg.setId(idPregunta);
            entity.setIdPregunta(bodyPreg);

            when(pruebaAreaDAO.findById(idPruebaArea)).thenReturn(pruebaArea);
            when(preguntaDAO.findById(idPregunta)).thenReturn(pregunta);
            doThrow(new RuntimeException("DB")).when(pruebaAreaPreguntaDAO).create(entity);

            Response resp = resource.create(idPruebaArea, entity, uriInfo);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(pruebaAreaPreguntaDAO).create(entity);
        }

        @Test
        void retorna422_cuandoIdPreguntaEnBodyEsNull() {
            entity.setId(null);
            Pregunta bodyPreg = new Pregunta();
            bodyPreg.setId(null);
            entity.setIdPregunta(bodyPreg);

            Response resp = resource.create(idPruebaArea, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idPregunta must be provided in body", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaDAO, preguntaDAO, pruebaAreaPreguntaDAO);
        }
    }

    @Nested
    class Update {
        @Test
        void retorna200_cuandoActualizaOrden() {
            PruebaAreaPregunta updateBody = new PruebaAreaPregunta();
            updateBody.setOrden((short)4);

            PruebaAreaPregunta expected = new PruebaAreaPregunta();
            expected.setId(pap.getId());
            expected.setIdPruebaArea(pruebaArea);
            expected.setIdPregunta(pregunta);
            expected.setOrden((short)4);
            expected.setFechaAsignacion(pap.getFechaAsignacion());

            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of(pap));
            when(pruebaAreaPreguntaDAO.update(any(PruebaAreaPregunta.class))).thenReturn(expected);

            Response resp = resource.update(idPruebaArea, idPregunta, updateBody);
            assertEquals(200, resp.getStatus());
            assertEquals(expected, resp.getEntity());
            verify(pruebaAreaPreguntaDAO).findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);
            verify(pruebaAreaPreguntaDAO).update(any(PruebaAreaPregunta.class));
        }

        @Test
        void retorna422_cuandoParametrosNulos() {
            Response resp = resource.update(null, null, new PruebaAreaPregunta());
            assertEquals(422, resp.getStatus());
            assertEquals("idPruebaArea,idPregunta", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoEntidadNula() {
            Response resp = resource.update(idPruebaArea, idPregunta, null);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra() {
            PruebaAreaPregunta updateBody = new PruebaAreaPregunta();
            updateBody.setOrden((short)4);

            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.update(idPruebaArea, idPregunta, updateBody);
            assertEquals(404, resp.getStatus());
            assertEquals("Record linking pruebaArea " + idPruebaArea + " and pregunta " + idPregunta + " not found",
                    resp.getHeaderString("Not-found-id"));
            verify(pruebaAreaPreguntaDAO).findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            PruebaAreaPregunta updateBody = new PruebaAreaPregunta();
            updateBody.setOrden((short)4);

            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenThrow(new RuntimeException("DB"));
            Response resp = resource.update(idPruebaArea, idPregunta, updateBody);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(pruebaAreaPreguntaDAO).findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna404_cuandoPreguntaDelRegistroEsNull() {
            PruebaAreaPregunta sinPregunta = new PruebaAreaPregunta();
            sinPregunta.setId(2);
            sinPregunta.setIdPregunta(null);

            PruebaAreaPregunta updateBody = new PruebaAreaPregunta();
            updateBody.setOrden((short) 3);

            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE))
                    .thenReturn(List.of(sinPregunta));

            Response resp = resource.update(idPruebaArea, idPregunta, updateBody);
            assertEquals(404, resp.getStatus());
            verify(pruebaAreaPreguntaDAO).findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna404_cuandoIdPreguntaNoCoincide() {
            PruebaAreaPregunta otraPregunta = new PruebaAreaPregunta();
            Pregunta preguntaDiferente = new Pregunta();
            preguntaDiferente.setId(UUID.randomUUID());
            otraPregunta.setIdPregunta(preguntaDiferente);

            PruebaAreaPregunta updateBody = new PruebaAreaPregunta();
            updateBody.setOrden((short) 3);

            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE))
                    .thenReturn(List.of(otraPregunta));

            Response resp = resource.update(idPruebaArea, idPregunta, updateBody);
            assertEquals(404, resp.getStatus());
            verify(pruebaAreaPreguntaDAO).findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna422_cuandoSoloIdPruebaAreaEsNulo() {
            Response resp = resource.update(null, idPregunta, new PruebaAreaPregunta());
            assertEquals(422, resp.getStatus());
            assertEquals("idPruebaArea,idPregunta", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoSoloIdPreguntaEsNulo() {
            Response resp = resource.update(idPruebaArea, null, new PruebaAreaPregunta());
            assertEquals(422, resp.getStatus());
            assertEquals("idPruebaArea,idPregunta", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }
    }

    @Nested
    class Delete {
        @Test
        void retorna204_cuandoValido() {
            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of(pap));
            Response resp = resource.delete(idPruebaArea, idPregunta);
            assertEquals(204, resp.getStatus());
            verify(pruebaAreaPreguntaDAO).findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);
            verify(pruebaAreaPreguntaDAO).delete(pap);
        }

        @Test
        void retorna422_cuandoParametrosNulos() {
            Response resp = resource.delete(null, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idPruebaArea,idPregunta", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra() {
            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.delete(idPruebaArea, idPregunta);
            assertEquals(404, resp.getStatus());
            assertEquals("Record linking pruebaArea " + idPruebaArea + " and pregunta " + idPregunta + " not found",
                    resp.getHeaderString("Not-found-id"));
            verify(pruebaAreaPreguntaDAO).findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE)).thenThrow(new RuntimeException("DB"));
            Response resp = resource.delete(idPruebaArea, idPregunta);
            assertEquals(500, resp.getStatus());
            assertEquals("Cannot access db", resp.getHeaderString("Server-exception"));
            verify(pruebaAreaPreguntaDAO).findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna404_cuandoPreguntaDelRegistroEsNull() {
            PruebaAreaPregunta sinPregunta = new PruebaAreaPregunta();
            sinPregunta.setId(2);
            sinPregunta.setIdPregunta(null);

            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE))
                    .thenReturn(List.of(sinPregunta));

            Response resp = resource.delete(idPruebaArea, idPregunta);
            assertEquals(404, resp.getStatus());
            verify(pruebaAreaPreguntaDAO).findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna404_cuandoIdPreguntaNoCoincide() {
            PruebaAreaPregunta otraPregunta = new PruebaAreaPregunta();
            Pregunta preguntaDiferente = new Pregunta();
            preguntaDiferente.setId(UUID.randomUUID());
            otraPregunta.setIdPregunta(preguntaDiferente);

            when(pruebaAreaPreguntaDAO.findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE))
                    .thenReturn(List.of(otraPregunta));

            Response resp = resource.delete(idPruebaArea, idPregunta);
            assertEquals(404, resp.getStatus());
            verify(pruebaAreaPreguntaDAO).findByPruebaArea(idPruebaArea, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna422_cuandoSoloIdPruebaAreaEsNulo() {
            Response resp = resource.delete(null, idPregunta);
            assertEquals(422, resp.getStatus());
            assertEquals("idPruebaArea,idPregunta", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }

        @Test
        void retorna422_cuandoSoloIdPreguntaEsNulo() {
            Response resp = resource.delete(idPruebaArea, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idPruebaArea,idPregunta", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO);
        }
    }
}

