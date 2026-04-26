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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    private Integer id;
    private PruebaAreaPregunta pruebaAreaPregunta;
    private Distractor distractor;
    private PruebaAreaPreguntaDistractor entity;

    @BeforeEach
    void setUp() {
        idPruebaAreaPregunta = 1;
        idDistractor = UUID.randomUUID();
        id = 100;

        pruebaAreaPregunta = new PruebaAreaPregunta();
        pruebaAreaPregunta.setId(idPruebaAreaPregunta);

        distractor = new Distractor();
        distractor.setId(idDistractor);

        entity = new PruebaAreaPreguntaDistractor();
        entity.setId(id);
        entity.setIdPruebaAreaPregunta(pruebaAreaPregunta);
        entity.setIdDistractor(distractor);
    }

    @Nested
    class FindById {

        @Test
        void retorna200_cuandoParametrosValidos() {
            when(pruebaAreaPreguntaDistractorDAO.findById(id)).thenReturn(entity);

            Response response = resource.findById(idPruebaAreaPregunta, idDistractor, id);

            assertEquals(200, response.getStatus());
            assertEquals(entity, response.getEntity());
            verify(pruebaAreaPreguntaDistractorDAO).findById(id);
        }

        @Test
        void retorna422_cuandoIdPruebaAreaPreguntaEsNull() {
            Response response = resource.findById(null, idDistractor, id);

            assertEquals(422, response.getStatus());
            assertEquals("idPruebaAreaPregunta,idDistractor,id",
                    response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoIdDistractorEsNull() {
            Response response = resource.findById(idPruebaAreaPregunta, null, id);

            assertEquals(422, response.getStatus());
            assertEquals("idPruebaAreaPregunta,idDistractor,id",
                    response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoIdEsNull() {
            Response response = resource.findById(idPruebaAreaPregunta, idDistractor, null);

            assertEquals(422, response.getStatus());
            assertEquals("idPruebaAreaPregunta,idDistractor,id",
                    response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna404_cuandoNoExisteRegistro() {
            when(pruebaAreaPreguntaDistractorDAO.findById(id)).thenReturn(null);

            Response response = resource.findById(idPruebaAreaPregunta, idDistractor, id);

            assertEquals(404, response.getStatus());
            assertEquals("Record not found", response.getHeaderString("Not-found"));
            verify(pruebaAreaPreguntaDistractorDAO).findById(id);
        }

        @Test
        void retorna404_cuandoPruebaAreaPreguntaNoCoincide() {
            PruebaAreaPregunta otraPAP = new PruebaAreaPregunta();
            otraPAP.setId(999);
            entity.setIdPruebaAreaPregunta(otraPAP);
            when(pruebaAreaPreguntaDistractorDAO.findById(id)).thenReturn(entity);

            Response response = resource.findById(idPruebaAreaPregunta, idDistractor, id);

            assertEquals(404, response.getStatus());
            assertEquals("Record not found", response.getHeaderString("Not-found"));
        }

        @Test
        void retorna404_cuandoDistractorNoCoincide() {
            Distractor otroDistractor = new Distractor();
            otroDistractor.setId(UUID.randomUUID());
            entity.setIdDistractor(otroDistractor);
            when(pruebaAreaPreguntaDistractorDAO.findById(id)).thenReturn(entity);

            Response response = resource.findById(idPruebaAreaPregunta, idDistractor, id);

            assertEquals(404, response.getStatus());
            assertEquals("Record not found", response.getHeaderString("Not-found"));
        }

        @Test
        void retorna404_cuandoPruebaAreaPreguntaEsNullEnEntity() {
            entity.setIdPruebaAreaPregunta(null);
            when(pruebaAreaPreguntaDistractorDAO.findById(id)).thenReturn(entity);

            Response response = resource.findById(idPruebaAreaPregunta, idDistractor, id);

            assertEquals(404, response.getStatus());
        }

        @Test
        void retorna404_cuandoDistractorEsNullEnEntity() {
            entity.setIdDistractor(null);
            when(pruebaAreaPreguntaDistractorDAO.findById(id)).thenReturn(entity);

            Response response = resource.findById(idPruebaAreaPregunta, idDistractor, id);

            assertEquals(404, response.getStatus());
        }

        @Test
        void retorna500_cuandoDAOExcepcion() {
            when(pruebaAreaPreguntaDistractorDAO.findById(id)).thenThrow(new RuntimeException("DB error"));

            Response response = resource.findById(idPruebaAreaPregunta, idDistractor, id);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
        }
    }

    @Nested
    class Create {

        @Test
        void retorna201_cuandoEntidadValida() {
            PruebaAreaPreguntaDistractor newEntity = new PruebaAreaPreguntaDistractor();
            newEntity.setId(null);

            when(pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta)).thenReturn(pruebaAreaPregunta);
            when(distractorDAO.findById(idDistractor)).thenReturn(distractor);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("http://test/" + id));

            doAnswer(inv -> {
                PruebaAreaPreguntaDistractor entityToSave = inv.getArgument(0);
                entityToSave.setId(id);
                return null;
            }).when(pruebaAreaPreguntaDistractorDAO).create(any(PruebaAreaPreguntaDistractor.class));

            Response response = resource.create(idPruebaAreaPregunta, idDistractor, newEntity, uriInfo);

            assertEquals(201, response.getStatus());
            assertNotNull(response.getEntity());
            assertNotNull(response.getHeaderString("Location"));
            verify(pruebaAreaPreguntaDAO).findById(idPruebaAreaPregunta);
            verify(distractorDAO).findById(idDistractor);
            verify(pruebaAreaPreguntaDistractorDAO).create(any(PruebaAreaPreguntaDistractor.class));
        }

        @Test
        void retorna422_cuandoIdPruebaAreaPreguntaEsNull() {
            Response response = resource.create(null, idDistractor, entity, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("idPruebaAreaPregunta,idDistractor,entity",
                    response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO, distractorDAO, pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoIdDistractorEsNull() {
            Response response = resource.create(idPruebaAreaPregunta, null, entity, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("idPruebaAreaPregunta,idDistractor,entity",
                    response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO, distractorDAO, pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoEntityEsNull() {
            Response response = resource.create(idPruebaAreaPregunta, idDistractor, null, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("idPruebaAreaPregunta,idDistractor,entity",
                    response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO, distractorDAO, pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoEntityTieneId() {
            Response response = resource.create(idPruebaAreaPregunta, idDistractor, entity, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("idPruebaAreaPregunta,idDistractor,entity",
                    response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDAO, distractorDAO, pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna404_cuandoPruebaAreaPreguntaNoExiste() {
            PruebaAreaPreguntaDistractor newEntity = new PruebaAreaPreguntaDistractor();
            newEntity.setId(null);

            when(pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta)).thenReturn(null);

            Response response = resource.create(idPruebaAreaPregunta, idDistractor, newEntity, uriInfo);

            assertEquals(404, response.getStatus());
            assertEquals("PruebaAreaPregunta not found", response.getHeaderString("Not-found"));
            verify(pruebaAreaPreguntaDAO).findById(idPruebaAreaPregunta);
            verifyNoInteractions(distractorDAO, pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna404_cuandoDistractorNoExiste() {
            PruebaAreaPreguntaDistractor newEntity = new PruebaAreaPreguntaDistractor();
            newEntity.setId(null);

            when(pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta)).thenReturn(pruebaAreaPregunta);
            when(distractorDAO.findById(idDistractor)).thenReturn(null);

            Response response = resource.create(idPruebaAreaPregunta, idDistractor, newEntity, uriInfo);

            assertEquals(404, response.getStatus());
            assertEquals("Distractor not found", response.getHeaderString("Not-found"));
            verify(pruebaAreaPreguntaDAO).findById(idPruebaAreaPregunta);
            verify(distractorDAO).findById(idDistractor);
            verifyNoInteractions(pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna500_cuandoDAOExcepcion() {
            PruebaAreaPreguntaDistractor newEntity = new PruebaAreaPreguntaDistractor();
            newEntity.setId(null);

            when(pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta)).thenReturn(pruebaAreaPregunta);
            when(distractorDAO.findById(idDistractor)).thenReturn(distractor);
            doThrow(new RuntimeException("DB error")).when(pruebaAreaPreguntaDistractorDAO)
                    .create(any(PruebaAreaPreguntaDistractor.class));

            Response response = resource.create(idPruebaAreaPregunta, idDistractor, newEntity, uriInfo);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
        }
    }

    @Nested
    class Update {

        @Test
        void retorna200_cuandoEntidadValida() {
            when(pruebaAreaPreguntaDistractorDAO.findById(id)).thenReturn(entity);
            when(pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta)).thenReturn(pruebaAreaPregunta);
            when(distractorDAO.findById(idDistractor)).thenReturn(distractor);
            when(pruebaAreaPreguntaDistractorDAO.update(any(PruebaAreaPreguntaDistractor.class)))
                    .thenReturn(entity);

            Response response = resource.update(idPruebaAreaPregunta, idDistractor, id, entity);

            assertEquals(200, response.getStatus());
            assertEquals(entity, response.getEntity());
            verify(pruebaAreaPreguntaDistractorDAO).findById(id);
            verify(pruebaAreaPreguntaDAO).findById(idPruebaAreaPregunta);
            verify(distractorDAO).findById(idDistractor);
            verify(pruebaAreaPreguntaDistractorDAO).update(any(PruebaAreaPreguntaDistractor.class));
        }

        @Test
        void retorna422_cuandoIdPruebaAreaPreguntaEsNull() {
            Response response = resource.update(null, idDistractor, id, entity);

            assertEquals(422, response.getStatus());
            assertEquals("idPruebaAreaPregunta,idDistractor,id,entity",
                    response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoIdDistractorEsNull() {
            Response response = resource.update(idPruebaAreaPregunta, null, id, entity);

            assertEquals(422, response.getStatus());
            assertEquals("idPruebaAreaPregunta,idDistractor,id,entity",
                    response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoIdEsNull() {
            Response response = resource.update(idPruebaAreaPregunta, idDistractor, null, entity);

            assertEquals(422, response.getStatus());
            assertEquals("idPruebaAreaPregunta,idDistractor,id,entity",
                    response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna422_cuandoEntityEsNull() {
            Response response = resource.update(idPruebaAreaPregunta, idDistractor, id, null);

            assertEquals(422, response.getStatus());
            assertEquals("idPruebaAreaPregunta,idDistractor,id,entity",
                    response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna404_cuandoNoExisteRegistro() {
            when(pruebaAreaPreguntaDistractorDAO.findById(id)).thenReturn(null);

            Response response = resource.update(idPruebaAreaPregunta, idDistractor, id, entity);

            assertEquals(404, response.getStatus());
            assertEquals("Record not found", response.getHeaderString("Not-found"));
            verify(pruebaAreaPreguntaDistractorDAO).findById(id);
        }

        @Test
        void retorna404_cuandoPruebaAreaPreguntaNoCoincide() {
            PruebaAreaPregunta otraPAP = new PruebaAreaPregunta();
            otraPAP.setId(999);
            entity.setIdPruebaAreaPregunta(otraPAP);
            when(pruebaAreaPreguntaDistractorDAO.findById(id)).thenReturn(entity);

            Response response = resource.update(idPruebaAreaPregunta, idDistractor, id, entity);

            assertEquals(404, response.getStatus());
            assertEquals("Record not found", response.getHeaderString("Not-found"));
        }

        @Test
        void retorna404_cuandoDistractorNoCoincide() {
            Distractor otroDistractor = new Distractor();
            otroDistractor.setId(UUID.randomUUID());
            entity.setIdDistractor(otroDistractor);
            when(pruebaAreaPreguntaDistractorDAO.findById(id)).thenReturn(entity);

            Response response = resource.update(idPruebaAreaPregunta, idDistractor, id, entity);

            assertEquals(404, response.getStatus());
            assertEquals("Record not found", response.getHeaderString("Not-found"));
        }

        @Test
        void retorna404_cuandoPruebaAreaPreguntaNoExisteEnUpdate() {
            when(pruebaAreaPreguntaDistractorDAO.findById(id)).thenReturn(entity);
            when(pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta)).thenReturn(null);

            Response response = resource.update(idPruebaAreaPregunta, idDistractor, id, entity);

            assertEquals(404, response.getStatus());
            assertEquals("PruebaAreaPregunta or Distractor not found",
                    response.getHeaderString("Not-found"));
        }

        @Test
        void retorna404_cuandoDistractorNoExisteEnUpdate() {
            when(pruebaAreaPreguntaDistractorDAO.findById(id)).thenReturn(entity);
            when(pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta)).thenReturn(pruebaAreaPregunta);
            when(distractorDAO.findById(idDistractor)).thenReturn(null);

            Response response = resource.update(idPruebaAreaPregunta, idDistractor, id, entity);

            assertEquals(404, response.getStatus());
            assertEquals("PruebaAreaPregunta or Distractor not found",
                    response.getHeaderString("Not-found"));
        }

        @Test
        void retorna500_cuandoDAOFindByIdExcepcion() {
            when(pruebaAreaPreguntaDistractorDAO.findById(id)).thenThrow(new RuntimeException("DB error"));

            Response response = resource.update(idPruebaAreaPregunta, idDistractor, id, entity);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
        }

        @Test
        void retorna500_cuandoDAOUpdateExcepcion() {
            when(pruebaAreaPreguntaDistractorDAO.findById(id)).thenReturn(entity);
            when(pruebaAreaPreguntaDAO.findById(idPruebaAreaPregunta)).thenReturn(pruebaAreaPregunta);
            when(distractorDAO.findById(idDistractor)).thenReturn(distractor);
            when(pruebaAreaPreguntaDistractorDAO.update(any(PruebaAreaPreguntaDistractor.class)))
                    .thenThrow(new RuntimeException("DB error"));

            Response response = resource.update(idPruebaAreaPregunta, idDistractor, id, entity);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
        }

        @Test
        void retorna404_cuandoPruebaAreaPreguntaEsNullEnExisting_Update() {
            entity.setIdPruebaAreaPregunta(null);
            when(pruebaAreaPreguntaDistractorDAO.findById(id)).thenReturn(entity);

            Response response = resource.update(idPruebaAreaPregunta, idDistractor, id, entity);

            assertEquals(404, response.getStatus());
            assertEquals("Record not found", response.getHeaderString("Not-found"));
            verify(pruebaAreaPreguntaDistractorDAO).findById(id);
            verifyNoMoreInteractions(pruebaAreaPreguntaDAO, distractorDAO, pruebaAreaPreguntaDistractorDAO);
        }

        @Test
        void retorna404_cuandoDistractorEsNullEnExisting_Update() {
            entity.setIdDistractor(null);
            when(pruebaAreaPreguntaDistractorDAO.findById(id)).thenReturn(entity);

            Response response = resource.update(idPruebaAreaPregunta, idDistractor, id, entity);

            assertEquals(404, response.getStatus());
            assertEquals("Record not found", response.getHeaderString("Not-found"));
            verify(pruebaAreaPreguntaDistractorDAO).findById(id);
            verifyNoMoreInteractions(pruebaAreaPreguntaDAO, distractorDAO, pruebaAreaPreguntaDistractorDAO);
        }
    }
}