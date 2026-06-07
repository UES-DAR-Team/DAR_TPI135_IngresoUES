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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PruebaAreaPreguntaDistractorResourceTest {

    @Mock
    PruebaAreaPreguntaDistractorDAO pruebaAreaPreguntaDistractorDAO;

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

    private Integer idPruebaAreaPregunta;
    private UUID idDistractor;
    private PruebaAreaPregunta pap;
    private Distractor distractor;
    private PruebaAreaPreguntaDistractor papd;
    private PruebaAreaPreguntaDistractor entity;

    @BeforeEach
    void setUp(){
        idPruebaAreaPregunta = 33;
        idDistractor = UUID.randomUUID();

        pap = new PruebaAreaPregunta();
        pap.setId(idPruebaAreaPregunta);

        distractor = new Distractor();
        distractor.setId(idDistractor);

        papd = new PruebaAreaPreguntaDistractor();
        papd.setId(1);
        papd.setIdPruebaAreaPregunta(pap);
        papd.setIdDistractor(distractor);
        papd.setEsRespuestaCorrecta(Boolean.TRUE);

        entity = new PruebaAreaPreguntaDistractor();
    }

    @Nested
    class FindById{
        @Test
        void retorna200ConEntidad_cuandoExiste(){
            when(pruebaAreaPreguntaDistractorDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE)).thenReturn(List.of(papd));
            Response resp = resource.findById(idPruebaAreaPregunta, idDistractor);
            assertEquals(200, resp.getStatus());
            assertEquals(papd, resp.getEntity());
            verify(pruebaAreaPreguntaDistractorDAO).findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna422_cuandoIdPruebaAreaPreguntaNulo(){
            Response resp = resource.findById(null, idDistractor);
            assertEquals(422, resp.getStatus());
            assertEquals("idPruebaAreaPregunta", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoIdDistractorNulo(){
            Response resp = resource.findById(idPruebaAreaPregunta, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idDistractor", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra(){
            when(pruebaAreaPreguntaDistractorDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.findById(idPruebaAreaPregunta, idDistractor);
            assertEquals(404, resp.getStatus());
            assertEquals("PuebaAreaPreguntaDistractor with id pruebaAreaPregunta="+idPruebaAreaPregunta+", distractor="+idDistractor+" not found",
                    resp.getHeaderString("Not-found-id"));
            verify(pruebaAreaPreguntaDistractorDAO).findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE);
        }
    }

    @Nested
    class Create{
        @Test
        void retorna201_cuandoValido(){
            entity.setId(null);
            Distractor body = new Distractor();
            body.setId(idDistractor);
            entity.setIdDistractor(body);

            when(pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta)).thenReturn(pap);
            when(distractorDAO.findById(idDistractor)).thenReturn(distractor);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/pruebaAreaPregunta/" + idPruebaAreaPregunta + "/distractor/" + idDistractor));

            doAnswer(inv -> {
                entity.setId(99);
                return null;
            }).when(pruebaAreaPreguntaDistractorDAO).create(entity);

            Response resp = resource.create(idPruebaAreaPregunta, entity, uriInfo);
            assertEquals(201, resp.getStatus());
            verify(pruebaAreaPreguntaDAO).findById(idPruebaAreaPregunta);
            verify(distractorDAO).findById(idDistractor);
            verify(pruebaAreaPreguntaDistractorDAO).create(entity);
        }

        @Test
        void retorna422_cuandoIdPruebaAreaPreguntaNulo(){
            Response resp = resource.create(null, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idPruebaAreaPregunta", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO, distractorDAO, pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoEntidadNula(){
            Response resp = resource.create(idPruebaAreaPregunta, null, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO, distractorDAO, pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoEntityTieneId(){
            entity.setId(5);
            Response resp = resource.create(idPruebaAreaPregunta, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.id must be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO, distractorDAO, pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoIdDistractorNoProvistoEnBody(){
            entity.setId(null);
            entity.setIdDistractor(null);
            Response resp = resource.create(idPruebaAreaPregunta, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.idDistractor.id must be provided in body", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO, distractorDAO, pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna404_cuandoPruebaAreaPreguntaNoExiste(){
            entity.setId(null);
            Distractor body = new Distractor();
            body.setId(idDistractor);
            entity.setIdDistractor(body);

            when(pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta)).thenReturn(null);
            Response resp = resource.create(idPruebaAreaPregunta, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("PruebaAreaPreguntaDistractor with id " + idPruebaAreaPregunta + " not found", resp.getHeaderString("Not-found-id"));
            verify(pruebaAreaPreguntaDAO).findById(idPruebaAreaPregunta);
            verifyNoMoreInteractions(distractorDAO, pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna404_cuandoDistractorNoExiste(){
            entity.setId(null);
            Distractor body = new Distractor();
            body.setId(idDistractor);
            entity.setIdDistractor(body);

            when(pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta)).thenReturn(pap);
            when(distractorDAO.findById(idDistractor)).thenReturn(null);

            Response resp = resource.create(idPruebaAreaPregunta, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("Distractor with id " + idDistractor + " not found", resp.getHeaderString("Not-found-id"));
            verify(pruebaAreaPreguntaDAO).findById(idPruebaAreaPregunta);
            verify(distractorDAO).findById(idDistractor);
            verifyNoMoreInteractions(pruebaAreaPreguntaDistractorDAO);
        }
    }

    @Nested
    class Update{
        @Test
        void retorna200_cuandoValido(){
            PruebaAreaPreguntaDistractor existing = new PruebaAreaPreguntaDistractor();
            existing.setId(1);
            existing.setIdDistractor(distractor);
            when(pruebaAreaPreguntaDistractorDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE)).thenReturn(List.of(existing));

            PruebaAreaPreguntaDistractor update = new PruebaAreaPreguntaDistractor();
            update.setEsRespuestaCorrecta(Boolean.FALSE);

            when(pruebaAreaPreguntaDistractorDAO.update(any())).thenReturn(update);

            Response resp = resource.update(idPruebaAreaPregunta, idDistractor, update);
            assertEquals(200, resp.getStatus());
            verify(pruebaAreaPreguntaDistractorDAO).findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE);
            verify(pruebaAreaPreguntaDistractorDAO).update(any());
        }

        @Test
        void retorna422_cuandoIdPruebaAreaPreguntaNulo(){
            Response resp = resource.update(null, idDistractor, entity);
            assertEquals(422, resp.getStatus());
            assertEquals("idPruebaAreaPregunta", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoIdDistractorNulo(){
            Response resp = resource.update(idPruebaAreaPregunta, null, entity);
            assertEquals(422, resp.getStatus());
            assertEquals("idDistractor", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoEntidadNula(){
            Response resp = resource.update(idPruebaAreaPregunta, idDistractor, null);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra(){
            when(pruebaAreaPreguntaDistractorDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.update(idPruebaAreaPregunta, idDistractor, entity);
            assertEquals(404, resp.getStatus());
            assertEquals("PuebaAreaPreguntaDistractor with id pruebaAreaPregunta="+idPruebaAreaPregunta+", distractor="+idDistractor+" not found",
                    resp.getHeaderString("Not-found-id"));
            verify(pruebaAreaPreguntaDistractorDAO).findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE);
        }
    }

    @Nested
    class Delete{
        @Test
        void retorna204_cuandoValido(){
            when(pruebaAreaPreguntaDistractorDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE)).thenReturn(List.of(papd));
            Response resp = resource.delete(idPruebaAreaPregunta, idDistractor);
            assertEquals(204, resp.getStatus());
            verify(pruebaAreaPreguntaDistractorDAO).findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE);
            verify(pruebaAreaPreguntaDistractorDAO).delete(papd);
        }

        @Test
        void retorna422_cuandoIdPruebaAreaPreguntaNulo(){
            Response resp = resource.delete(null, idDistractor);
            assertEquals(422, resp.getStatus());
            assertEquals("idPruebaAreaPregunta", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoIdDistractorNulo(){
            Response resp = resource.delete(idPruebaAreaPregunta, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idDistractor", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra(){
            when(pruebaAreaPreguntaDistractorDAO.findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.delete(idPruebaAreaPregunta, idDistractor);
            assertEquals(404, resp.getStatus());
            assertEquals("PuebaAreaPreguntaDistractor with id pruebaAreaPregunta="+idPruebaAreaPregunta+", distractor="+idDistractor+" not found",
                    resp.getHeaderString("Not-found-id"));
            verify(pruebaAreaPreguntaDistractorDAO).findByPruebaAreaPregunta(idPruebaAreaPregunta, 0, Integer.MAX_VALUE);
        }
    }
}

