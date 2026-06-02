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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaAreaPreguntaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaAreaPreguntaDistractorDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPregunta;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPreguntaDistractor;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PruebaAreaPreguntaDistractorResourceTest {

    @Mock
    PruebaAreaPreguntaDistractorDAO papdDAO;

    @Mock
    PruebaAreaPreguntaDAO pruebaAreaPreguntaDAO;

    @Mock
    DistractorDAO distractorDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    PruebaAreaPreguntaDistractorResource resource;

    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final int EXCEEDMAX = 11;

    private Integer idPruebaAreaPregunta;
    private UUID idDistractor;
    private PruebaAreaPregunta padre;
    private Distractor distractor;
    private PruebaAreaPreguntaDistractor papd;
    private PruebaAreaPreguntaDistractor entity;

    @BeforeEach
    void setUp() {
        idPruebaAreaPregunta = 1;
        idDistractor = UUID.randomUUID();

        padre = new PruebaAreaPregunta();
        padre.setId(idPruebaAreaPregunta);

        distractor = new Distractor();
        distractor.setId(idDistractor);

        papd = new PruebaAreaPreguntaDistractor();
        papd.setId(100);
        papd.setIdPruebaAreaPregunta(padre);
        papd.setIdDistractor(distractor);

        entity = new PruebaAreaPreguntaDistractor();
    }

    @Nested
    class findRange {

        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos() {
            List<PruebaAreaPreguntaDistractor> lista = List.of(papd);
            when(pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta)).thenReturn(padre);
            when(papdDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, FIRST, MAX)).thenReturn(lista);
            when(papdDAO.count()).thenReturn(1);

            Response response = resource.findRange(idPruebaAreaPregunta, FIRST, MAX);

            assertEquals(200, response.getStatus());
            assertEquals(lista, response.getEntity());
            assertEquals("1", response.getHeaderString("X-Total-Count"));
            verify(pruebaAreaPreguntaDAO).findById(idPruebaAreaPregunta);
            verify(papdDAO).findByPruebaAreaPregunta(idPruebaAreaPregunta, FIRST, MAX);
        }

        @Test
        void retorna422_cuandoIdPadreNulo() {
            Response response = resource.findRange(null, FIRST, MAX);

            assertEquals(422, response.getStatus());
            assertEquals("idPruebaAreaPregunta", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO, papdDAO);
        }

        @Test
        void retorna422_cuandoPaginacionEsInvalida() {
            Response respFirst = resource.findRange(idPruebaAreaPregunta, INVALIDFIRST, MAX);
            assertEquals(422, respFirst.getStatus());

            Response respMax = resource.findRange(idPruebaAreaPregunta, FIRST, INVALIDMAX);
            assertEquals(422, respMax.getStatus());

            Response respExceed = resource.findRange(idPruebaAreaPregunta, FIRST, EXCEEDMAX);
            assertEquals(422, respExceed.getStatus());

            verifyNoInteractions(pruebaAreaPreguntaDAO, papdDAO);
        }

        @Test
        void retorna404_cuandoPadreNoExiste() {
            when(pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta)).thenReturn(null);

            Response response = resource.findRange(idPruebaAreaPregunta, FIRST, MAX);

            assertEquals(404, response.getStatus());
            assertEquals("PruebaAreaPregunta with id " + idPruebaAreaPregunta + " not found", response.getHeaderString("Not-found-id"));
            verifyNoInteractions(papdDAO);
        }
    }

    @Nested
    class findOne {

        @Test
        void retorna200ConEntidad_cuandoParametrosSonValidosYSeEncuentra() {
            List<PruebaAreaPreguntaDistractor> lista = List.of(papd);
            when(papdDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE)).thenReturn(lista);

            Response response = resource.findOne(idPruebaAreaPregunta, idDistractor);

            assertEquals(200, response.getStatus());
            assertEquals(papd, response.getEntity());
        }

        @Test
        void retorna422_cuandoIdsNulos() {
            assertEquals(422, resource.findOne(null, idDistractor).getStatus());
            assertEquals(422, resource.findOne(idPruebaAreaPregunta, null).getStatus());
            verifyNoInteractions(papdDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraEnElStream() {

            when(papdDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE)).thenReturn(List.of());

            Response response = resource.findOne(idPruebaAreaPregunta, idDistractor);

            assertEquals(404, response.getStatus());
            assertTrue(response.getHeaderString("Not-found-id").contains("linking pruebaAreaPregunta"));
        }
    }

    @Nested
    class Create {

        @Test
        void retorna201_cuandoEntidadEsValidaYAsignaFechaNula() {
            entity.setIdDistractor(distractor);
            entity.setFechaRegistro(null);

            when(pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta)).thenReturn(padre);
            when(distractorDAO.findById(idDistractor)).thenReturn(distractor);

            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("v1/distractor/" + idDistractor));

            Response response = resource.create(idPruebaAreaPregunta, entity, uriInfo);

            assertEquals(201, response.getStatus());
            assertNull(response.getEntity());
            assertNotNull(entity.getFechaRegistro());
            verify(papdDAO).create(entity);
        }

        @Test
        void retorna201_cuandoEntidadEsValidaYRespetaFechaExistente() {
            entity.setIdDistractor(distractor);
            OffsetDateTime fechaPrevia = OffsetDateTime.now().minusDays(2);
            entity.setFechaRegistro(fechaPrevia);

            when(pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta)).thenReturn(padre);
            when(distractorDAO.findById(idDistractor)).thenReturn(distractor);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("v1/distractor/"));

            Response response = resource.create(idPruebaAreaPregunta, entity, uriInfo);

            assertEquals(201, response.getStatus());
            assertEquals(fechaPrevia, entity.getFechaRegistro());
            verify(papdDAO).create(entity);
        }

        @Test
        void retorna422_cuandoDatosInvalidos() {
            assertEquals(422, resource.create(null, entity, uriInfo).getStatus());
            assertEquals(422, resource.create(idPruebaAreaPregunta, null, uriInfo).getStatus());

            entity.setId(100);
            assertEquals(422, resource.create(idPruebaAreaPregunta, entity, uriInfo).getStatus());

            entity.setId(null);
            entity.setIdDistractor(null);
            assertEquals(422, resource.create(idPruebaAreaPregunta, entity, uriInfo).getStatus());

            Distractor distSinId = new Distractor();
            distSinId.setId(null);
            entity.setIdDistractor(distSinId);
            assertEquals(422, resource.create(idPruebaAreaPregunta, entity, uriInfo).getStatus());

            verifyNoInteractions(papdDAO);
        }

        @Test
        void retorna404_cuandoPadresNoExisten() {
            entity.setIdDistractor(distractor);

            when(pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta)).thenReturn(null);
            assertEquals(404, resource.create(idPruebaAreaPregunta, entity, uriInfo).getStatus());

            when(pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta)).thenReturn(padre);
            when(distractorDAO.findById(idDistractor)).thenReturn(null);
            assertEquals(404, resource.create(idPruebaAreaPregunta, entity, uriInfo).getStatus());

            verifyNoInteractions(papdDAO);
        }

        @Test
        void retorna404_cuandoStreamContieneElementosQueNoCoinciden() {
            PruebaAreaPreguntaDistractor papdSinDistractor = new PruebaAreaPreguntaDistractor();
            papdSinDistractor.setIdDistractor(null);

            PruebaAreaPreguntaDistractor papdOtroId = new PruebaAreaPreguntaDistractor();
            Distractor otroDistractor = new Distractor();
            otroDistractor.setId(UUID.randomUUID());
            papdOtroId.setIdDistractor(otroDistractor);

            List<PruebaAreaPreguntaDistractor> lista = List.of(papdSinDistractor, papdOtroId);

            when(papdDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE)).thenReturn(lista);

            Response response = resource.findOne(idPruebaAreaPregunta, idDistractor);

            assertEquals(404, response.getStatus());
        }
    }

    @Nested
    class Update {

        @Test
        void retorna200_cuandoEntidadEsValidaYSeEncuentra() {
            List<PruebaAreaPreguntaDistractor> lista = List.of(papd);
            entity.setEsRespuestaCorrecta(true);

            when(papdDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE)).thenReturn(lista);
            when(papdDAO.update(papd)).thenReturn(papd);

            Response response = resource.update(idPruebaAreaPregunta, idDistractor, entity);

            assertEquals(200, response.getStatus());
            assertEquals(papd, response.getEntity());
            assertTrue(papd.getEsRespuestaCorrecta());
            verify(papdDAO).update(papd);
        }

        @Test
        void retorna422_cuandoFaltanParametros() {
            assertEquals(422, resource.update(null, idDistractor, entity).getStatus());
            assertEquals(422, resource.update(idPruebaAreaPregunta, null, entity).getStatus());
            assertEquals(422, resource.update(idPruebaAreaPregunta, idDistractor, null).getStatus());
            verifyNoInteractions(papdDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraParaActualizar() {
            when(papdDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE)).thenReturn(List.of());

            Response response = resource.update(idPruebaAreaPregunta, idDistractor, entity);

            assertEquals(404, response.getStatus());
            verify(papdDAO, never()).update(any());
        }

        @Test
        void retorna404_cuandoDistractorNoCoincideParaActualizar() {
            PruebaAreaPreguntaDistractor papdSinDistractor = new PruebaAreaPreguntaDistractor();
            papdSinDistractor.setIdDistractor(null);

            PruebaAreaPreguntaDistractor papdOtroId = new PruebaAreaPreguntaDistractor();
            Distractor otroDistractor = new Distractor();
            otroDistractor.setId(UUID.randomUUID());
            papdOtroId.setIdDistractor(otroDistractor);

            List<PruebaAreaPreguntaDistractor> lista = List.of(papdSinDistractor, papdOtroId);

            when(papdDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE)).thenReturn(lista);

            Response response = resource.update(idPruebaAreaPregunta, idDistractor, entity);

            assertEquals(404, response.getStatus());
            verify(papdDAO, never()).update(any());
        }
    }

    @Nested
    class Delete {

        @Test
        void retorna204_cuandoRegistroExiste() {
            List<PruebaAreaPreguntaDistractor> lista = List.of(papd);
            when(papdDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE)).thenReturn(lista);

            Response response = resource.delete(idPruebaAreaPregunta, idDistractor);

            assertEquals(204, response.getStatus());
            verify(papdDAO).delete(papd);
        }

        @Test
        void retorna422_cuandoIdsNulos() {
            assertEquals(422, resource.delete(null, idDistractor).getStatus());
            assertEquals(422, resource.delete(idPruebaAreaPregunta, null).getStatus());
            verifyNoInteractions(papdDAO);
        }

        @Test
        void retorna404_cuandoRegistroNoExisteParaBorrar() {
            when(papdDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE)).thenReturn(List.of());

            Response response = resource.delete(idPruebaAreaPregunta, idDistractor);

            assertEquals(404, response.getStatus());
            verify(papdDAO, never()).delete(any());
        }

        @Test
        void retorna404_cuandoDistractorNoCoincideParaBorrar () {
            PruebaAreaPreguntaDistractor papdSinDistractor = new PruebaAreaPreguntaDistractor();
            papdSinDistractor.setIdDistractor(null);

            PruebaAreaPreguntaDistractor papdOtroId = new PruebaAreaPreguntaDistractor();
            Distractor otroDistractor = new Distractor();
            otroDistractor.setId(UUID.randomUUID());
            papdOtroId.setIdDistractor(otroDistractor);

            List<PruebaAreaPreguntaDistractor> lista = List.of(papdSinDistractor, papdOtroId);

            when(papdDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE)).thenReturn(lista);

            Response response = resource.delete(idPruebaAreaPregunta, idDistractor);

            assertEquals(404, response.getStatus());
            verify(papdDAO, never()).delete(any());
        }
    }
}