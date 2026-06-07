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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AulaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaAulaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aula;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAula;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JornadaAulaResourceTest {

    @Mock
    JornadaAulaDAO jornadaAulaDAO;

    @Mock
    JornadaDAO jornadaDAO;

    @Mock
    AulaDAO aulaDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    JornadaAulaResource resource;

    private UUID idJornada;
    private UUID idAula;
    private Jornada jornada;
    private Aula aula;
    private JornadaAula ja;
    private JornadaAula entity;

    @BeforeEach
    void setUp(){
        idJornada = UUID.randomUUID();
        idAula = UUID.randomUUID();

        jornada = new Jornada();
        jornada.setId(idJornada);

        aula = new Aula();
        aula.setId(idAula);

        ja = new JornadaAula();
        ja.setId(1);
        ja.setIdJornada(jornada);
        ja.setIdAula(aula);

        entity = new JornadaAula();
    }

    @Nested
    class FindRange{
        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos(){
            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);
            when(jornadaAulaDAO.findByJornada(idJornada, 0, 100)).thenReturn(List.of(ja));
            when(jornadaAulaDAO.count()).thenReturn(1);

            Response resp = resource.findRange(idJornada, 0, 100);
            assertEquals(200, resp.getStatus());
            List<?> entidad = (List<?>) resp.getEntity();
            assertEquals(1, entidad.size());
            assertEquals("1", resp.getHeaderString("X-Total-Count"));
            verify(jornadaDAO).findById(idJornada);
            verify(jornadaAulaDAO).findByJornada(idJornada, 0, 100);
            verify(jornadaAulaDAO).count();
        }

        @Test
        void retorna422_cuandoIdJornadaNulo(){
            Response resp = resource.findRange(null, 0, 100);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornada", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaDAO, jornadaAulaDAO);
        }

        @Test
        void retorna422_cuandoFirstInvalido(){
            Response resp = resource.findRange(idJornada, -1, 100);
            assertEquals(422, resp.getStatus());
            assertEquals("first, max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaDAO, jornadaAulaDAO);
        }

        @Test
        void retorna422_cuandoMaxInvalido(){
            Response resp = resource.findRange(idJornada, 0, 0);
            assertEquals(422, resp.getStatus());
            assertEquals("first, max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaDAO, jornadaAulaDAO);
        }

        @Test
        void retorna422_cuandoMaxExcedido(){
            Response resp = resource.findRange(idJornada, 0, 101);
            assertEquals(422, resp.getStatus());
            assertEquals("first, max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaDAO, jornadaAulaDAO);
        }

        @Test
        void retorna404_cuandoJornadaNoExiste(){
            when(jornadaDAO.findById(idJornada)).thenReturn(null);
            Response resp = resource.findRange(idJornada, 0, 100);
            assertEquals(404, resp.getStatus());
            assertEquals("Jornada with id " + idJornada + " not found", resp.getHeaderString("Not-found-id"));
            verify(jornadaDAO).findById(idJornada);
            verifyNoMoreInteractions(jornadaAulaDAO);
        }
    }

    @Nested
    class FindOne{
        @Test
        void retorna200ConEntidad_cuandoExiste(){
            when(jornadaAulaDAO.findByJornada(idJornada, 0, Integer.MAX_VALUE)).thenReturn(List.of(ja));
            Response resp = resource.findOne(idJornada, idAula);
            assertEquals(200, resp.getStatus());
            assertEquals(ja, resp.getEntity());
            verify(jornadaAulaDAO).findByJornada(idJornada, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna422_cuandoIdJornadaNulo(){
            Response resp = resource.findOne(null, idAula);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornada", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaDAO);
        }

        @Test
        void retorna422_cuandoIdAulaNulo(){
            Response resp = resource.findOne(idJornada, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idAula", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra(){
            when(jornadaAulaDAO.findByJornada(idJornada, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.findOne(idJornada, idAula);
            assertEquals(404, resp.getStatus());
            assertEquals("JornadaAula with id jornada="+idJornada+", aula="+idAula+" not found", resp.getHeaderString("Not-found-id"));
            verify(jornadaAulaDAO).findByJornada(idJornada, 0, Integer.MAX_VALUE);
        }
    }

    @Nested
    class Create{
        @Test
        void retorna201_cuandoValido(){
            entity.setId(null);
            Aula body = new Aula();
            body.setId(idAula);
            entity.setIdAula(body);

            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);
            when(aulaDAO.findById(idAula)).thenReturn(aula);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/jornada/" + idJornada + "/aula/" + idAula));

            doAnswer(inv -> {
                entity.setId(55);
                return null;
            }).when(jornadaAulaDAO).create(entity);

            Response resp = resource.create(idJornada, entity, uriInfo);
            assertEquals(201, resp.getStatus());
            verify(jornadaDAO).findById(idJornada);
            verify(aulaDAO).findById(idAula);
            verify(jornadaAulaDAO).create(entity);
        }

        @Test
        void retorna422_cuandoIdJornadaNulo(){
            Response resp = resource.create(null, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornada", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaDAO, aulaDAO, jornadaAulaDAO);
        }

        @Test
        void retorna422_cuandoEntidadNula(){
            Response resp = resource.create(idJornada, null, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaDAO, aulaDAO, jornadaAulaDAO);
        }

        @Test
        void retorna422_cuandoEntityTieneId(){
            entity.setId(5);
            Response resp = resource.create(idJornada, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.id must be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaDAO, aulaDAO, jornadaAulaDAO);
        }

        @Test
        void retorna422_cuandoIdAulaNoProvistoEnBody(){
            entity.setId(null);
            entity.setIdAula(null);
            Response resp = resource.create(idJornada, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.idAula must be provided in body", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaDAO, aulaDAO, jornadaAulaDAO);
        }

        @Test
        void retorna404_cuandoJornadaNoExiste(){
            entity.setId(null);
            Aula body = new Aula();
            body.setId(idAula);
            entity.setIdAula(body);

            when(jornadaDAO.findById(idJornada)).thenReturn(null);
            Response resp = resource.create(idJornada, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("Jornada with id " + idJornada + " not found", resp.getHeaderString("Not-found-id"));
            verify(jornadaDAO).findById(idJornada);
            verifyNoMoreInteractions(aulaDAO, jornadaAulaDAO);
        }

        @Test
        void retorna404_cuandoAulaNoExiste(){
            entity.setId(null);
            Aula body = new Aula();
            body.setId(idAula);
            entity.setIdAula(body);

            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);
            when(aulaDAO.findById(idAula)).thenReturn(null);

            Response resp = resource.create(idJornada, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("Aula with id " + idAula + " not found", resp.getHeaderString("Not-found-id"));
            verify(jornadaDAO).findById(idJornada);
            verify(aulaDAO).findById(idAula);
            verifyNoMoreInteractions(jornadaAulaDAO);
        }
    }

    @Nested
    class Delete{
        @Test
        void retorna204_cuandoValido(){
            when(jornadaAulaDAO.findByJornada(idJornada, 0, Integer.MAX_VALUE)).thenReturn(List.of(ja));
            Response resp = resource.delete(idJornada, idAula);
            assertEquals(204, resp.getStatus());
            verify(jornadaAulaDAO).findByJornada(idJornada, 0, Integer.MAX_VALUE);
            verify(jornadaAulaDAO).delete(ja);
        }

        @Test
        void retorna422_cuandoIdJornadaNulo(){
            Response resp = resource.delete(null, idAula);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornada", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaDAO);
        }

        @Test
        void retorna422_cuandoIdAulaNulo(){
            Response resp = resource.delete(idJornada, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idAula", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra(){
            when(jornadaAulaDAO.findByJornada(idJornada, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.delete(idJornada, idAula);
            assertEquals(404, resp.getStatus());
            assertEquals("JornadaAula with id jornada="+idJornada+", aula="+idAula+" not found", resp.getHeaderString("Not-found-id"));
            verify(jornadaAulaDAO).findByJornada(idJornada, 0, Integer.MAX_VALUE);
        }
    }
}
