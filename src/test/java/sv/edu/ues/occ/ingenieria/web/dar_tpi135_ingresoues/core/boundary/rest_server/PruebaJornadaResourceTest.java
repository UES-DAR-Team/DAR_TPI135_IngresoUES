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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaJornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaJornada;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PruebaJornadaResourceTest {

    @Mock
    PruebaJornadaDAO pruebaJornadaDAO;

    @Mock
    PruebaDAO pruebaDAO;

    @Mock
    JornadaDAO jornadaDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    PruebaJornadaResource resource;

    private UUID idPrueba;
    private UUID idJornada;
    private Prueba prueba;
    private Jornada jornada;
    private PruebaJornada pj;
    private PruebaJornada entity;

    @BeforeEach
    void setUp(){
        idPrueba = UUID.randomUUID();
        idJornada = UUID.randomUUID();

        prueba = new Prueba();
        prueba.setId(idPrueba);

        jornada = new Jornada();
        jornada.setId(idJornada);

        pj = new PruebaJornada();
        pj.setId(1);
        pj.setIdPrueba(prueba);
        pj.setIdJornada(jornada);

        entity = new PruebaJornada();
    }

    @Nested
    class FindById{
        @Test
        void retorna200ConEntidad_cuandoExiste(){
            when(pruebaJornadaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of(pj));
            Response resp = resource.findById(idPrueba, idJornada);
            assertEquals(200, resp.getStatus());
            assertEquals(pj, resp.getEntity());
            verify(pruebaJornadaDAO).findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna422_cuandoIdPruebaNulo(){
            Response resp = resource.findById(null, idJornada);
            assertEquals(422, resp.getStatus());
            assertEquals("idPrueba", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna422_cuandoIdJornadaNulo(){
            Response resp = resource.findById(idPrueba, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornada", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra(){
            when(pruebaJornadaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.findById(idPrueba, idJornada);
            assertEquals(404, resp.getStatus());
            assertEquals("PruebaJornada with id prueba="+idPrueba+", jornada="+idJornada+" not found", resp.getHeaderString("Not-found-id"));
            verify(pruebaJornadaDAO).findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
        }
    }

    @Nested
    class Create{
        @Test
        void retorna201_cuandoValido(){
            entity.setId(null);
            Jornada body = new Jornada();
            body.setId(idJornada);
            entity.setIdJornada(body);

            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/prueba/" + idPrueba + "/jornada/" + idJornada));

            doAnswer(inv -> {
                entity.setId(99);
                return null;
            }).when(pruebaJornadaDAO).create(entity);

            Response resp = resource.create(idPrueba, entity, uriInfo);
            assertEquals(201, resp.getStatus());
            verify(pruebaDAO).findById(idPrueba);
            verify(jornadaDAO).findById(idJornada);
            verify(pruebaJornadaDAO).create(entity);
        }

        @Test
        void retorna422_cuandoIdPruebaNulo(){
            Response resp = resource.create(null, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idPrueba", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO, jornadaDAO, pruebaJornadaDAO);
        }

        @Test
        void retorna422_cuandoEntidadNula(){
            Response resp = resource.create(idPrueba, null, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO, jornadaDAO, pruebaJornadaDAO);
        }

        @Test
        void retorna422_cuandoEntityTieneId(){
            entity.setId(5);
            Response resp = resource.create(idPrueba, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.id must be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO, jornadaDAO, pruebaJornadaDAO);
        }

        @Test
        void retorna422_cuandoIdJornadaNoProvistoEnBody(){
            entity.setId(null);
            entity.setIdJornada(null);
            Response resp = resource.create(idPrueba, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.idJornada must be provided in body", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO, jornadaDAO, pruebaJornadaDAO);
        }

        @Test
        void retorna404_cuandoPruebaNoExiste(){
            entity.setId(null);
            Jornada body = new Jornada();
            body.setId(idJornada);
            entity.setIdJornada(body);

            when(pruebaDAO.findById(idPrueba)).thenReturn(null);
            Response resp = resource.create(idPrueba, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("Prueba with id " + idPrueba + " not found", resp.getHeaderString("Not-found-id"));
            verify(pruebaDAO).findById(idPrueba);
            verifyNoMoreInteractions(jornadaDAO, pruebaJornadaDAO);
        }

        @Test
        void retorna404_cuandoJornadaNoExiste(){
            entity.setId(null);
            Jornada body = new Jornada();
            body.setId(idJornada);
            entity.setIdJornada(body);

            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(jornadaDAO.findById(idJornada)).thenReturn(null);

            Response resp = resource.create(idPrueba, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("Jornada with id " + idJornada + " not found", resp.getHeaderString("Not-found-id"));
            verify(pruebaDAO).findById(idPrueba);
            verify(jornadaDAO).findById(idJornada);
            verifyNoMoreInteractions(pruebaJornadaDAO);
        }
    }

    @Nested
    class Update{
        @Test
        void retorna200_cuandoValido(){
            PruebaJornada existing = new PruebaJornada();
            existing.setId(1);
            existing.setIdJornada(jornada);
            when(pruebaJornadaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of(existing));

            PruebaJornada update = new PruebaJornada();
            when(pruebaJornadaDAO.update(any())).thenReturn(update);

            Response resp = resource.update(idPrueba, idJornada, update);
            assertEquals(200, resp.getStatus());
            verify(pruebaJornadaDAO).findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
            verify(pruebaJornadaDAO).update(any());
        }

        @Test
        void retorna422_cuandoIdPruebaNulo(){
            Response resp = resource.update(null, idJornada, entity);
            assertEquals(422, resp.getStatus());
            assertEquals("idPrueba", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna422_cuandoIdJornadaNulo(){
            Response resp = resource.update(idPrueba, null, entity);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornada", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna422_cuandoEntidadNula(){
            Response resp = resource.update(idPrueba, idJornada, null);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra(){
            when(pruebaJornadaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.update(idPrueba, idJornada, entity);
            assertEquals(404, resp.getStatus());
            assertEquals("PruebaJornada with id prueba="+idPrueba+", jornada="+idJornada+" not found", resp.getHeaderString("Not-found-id"));
            verify(pruebaJornadaDAO).findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
        }
    }

    @Nested
    class Delete{
        @Test
        void retorna204_cuandoValido(){
            when(pruebaJornadaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of(pj));
            Response resp = resource.update(idPrueba, idJornada);
            assertEquals(204, resp.getStatus());
            verify(pruebaJornadaDAO).findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
            verify(pruebaJornadaDAO).delete(pj);
        }

        @Test
        void retorna422_cuandoIdPruebaNulo(){
            Response resp = resource.update(null, idJornada);
            assertEquals(422, resp.getStatus());
            assertEquals("idPrueba", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna422_cuandoIdJornadaNulo(){
            Response resp = resource.update(idPrueba, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornada", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra(){
            when(pruebaJornadaDAO.findByPrueba(idPrueba, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.update(idPrueba, idJornada);
            assertEquals(404, resp.getStatus());
            assertEquals("PruebaJornada with id prueba="+idPrueba+", jornada="+idJornada+" not found", resp.getHeaderString("Not-found-id"));
            verify(pruebaJornadaDAO).findByPrueba(idPrueba, 0, Integer.MAX_VALUE);
        }
    }
}
