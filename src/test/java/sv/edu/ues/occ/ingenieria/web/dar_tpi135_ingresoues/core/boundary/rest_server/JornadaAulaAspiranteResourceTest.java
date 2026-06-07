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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AspirantePruebaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaAulaAspiranteDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaAulaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspirantePrueba;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAula;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspirante;

import java.net.URI;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JornadaAulaAspiranteResourceTest {

    @Mock
    JornadaAulaAspiranteDAO jaaDAO;

    @Mock
    JornadaAulaDAO jornadaAulaDAO;

    @Mock
    AspirantePruebaDAO aspirantePruebaDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    JornadaAulaAspiranteResource resource;

    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final int EXCEEDMAX = 11;

    private Integer idJornadaAula;
    private Integer idAspirantePrueba;
    private JornadaAula jornadaAula;
    private AspirantePrueba aspirantePrueba;
    private JornadaAulaAspirante jaa;
    private JornadaAulaAspirante entity;

    @BeforeEach
    void setUp(){
        idJornadaAula = 42;
        idAspirantePrueba = 7;

        jornadaAula = new JornadaAula();
        jornadaAula.setId(idJornadaAula);

        aspirantePrueba = new AspirantePrueba();
        aspirantePrueba.setId(idAspirantePrueba);

        jaa = new JornadaAulaAspirante();
        jaa.setId(1);
        jaa.setIdJornadaAula(jornadaAula);
        jaa.setIdAspirantePrueba(aspirantePrueba);
        jaa.setHoraLlegada(LocalTime.now());
        jaa.setAsistio(Boolean.TRUE);

        entity = new JornadaAulaAspirante();
    }

    @Nested
    class FindRange{
        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos(){
            when(jornadaAulaDAO.findById(idJornadaAula)).thenReturn(jornadaAula);
            when(jaaDAO.findByJornadaAula(idJornadaAula, FIRST, MAX)).thenReturn(List.of(jaa));
            when(jaaDAO.count()).thenReturn(1);

            Response resp = resource.findRange(idJornadaAula, FIRST, MAX);

            assertEquals(200, resp.getStatus());
            List<?> entidad = (List<?>) resp.getEntity();
            assertEquals(1, entidad.size());
            assertEquals("1", resp.getHeaderString("X-Total-Count"));
            verify(jornadaAulaDAO).findById(idJornadaAula);
            verify(jaaDAO).findByJornadaAula(idJornadaAula, FIRST, MAX);
            verify(jaaDAO).count();
        }

        @Test
        void retorna422_cuandoIdJornadaEsNulo(){
            Response resp = resource.findRange(null, FIRST, MAX);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornadaAula", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaDAO, jaaDAO);
        }

        @Test
        void retorna422_cuandoFirstInvalido(){
            Response resp = resource.findRange(idJornadaAula, INVALIDFIRST, MAX);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaDAO, jaaDAO);
        }

        @Test
        void retorna422_cuandoMaxInvalido(){
            Response resp = resource.findRange(idJornadaAula, FIRST, INVALIDMAX);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaDAO, jaaDAO);
        }

        @Test
        void retorna422_cuandoMaxExcedido(){
            Response resp = resource.findRange(idJornadaAula, FIRST, EXCEEDMAX);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaDAO, jaaDAO);
        }

        @Test
        void retorna404_cuandoNoExisteJornadaAula(){
            when(jornadaAulaDAO.findById(idJornadaAula)).thenReturn(null);
            Response resp = resource.findRange(idJornadaAula, FIRST, MAX);
            assertEquals(404, resp.getStatus());
            assertEquals("JornadaAula with id " + idJornadaAula + " not found", resp.getHeaderString("Not-found-id"));
            verify(jornadaAulaDAO).findById(idJornadaAula);
            verifyNoMoreInteractions(jaaDAO);
        }
    }

    @Nested
    class FindById{
        @Test
        void retorna200ConEntidad_cuandoExiste(){
            when(jaaDAO.findByJornadaAula(idJornadaAula, 0, Integer.MAX_VALUE)).thenReturn(List.of(jaa));
            Response resp = resource.findById(idJornadaAula, idAspirantePrueba);
            assertEquals(200, resp.getStatus());
            verify(jaaDAO).findByJornadaAula(idJornadaAula, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna422_cuandoIdJornadaNulo(){
            Response resp = resource.findById(null, idAspirantePrueba);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornadaAula", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jaaDAO);
        }

        @Test
        void retorna422_cuandoIdAspiranteNulo(){
            Response resp = resource.findById(idJornadaAula, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idAspirantePrueba", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jaaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra(){
            when(jaaDAO.findByJornadaAula(idJornadaAula, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.findById(idJornadaAula, idAspirantePrueba);
            assertEquals(404, resp.getStatus());
            assertEquals("JornadaAula with id jornadaAula="+idJornadaAula+", aspirantePrueba="+idAspirantePrueba+" not found",
                    resp.getHeaderString("Not-found-id"));
            verify(jaaDAO).findByJornadaAula(idJornadaAula, 0, Integer.MAX_VALUE);
        }
    }

    @Nested
    class Create{
        @Test
        void retorna201_cuandoValido(){
            entity.setId(null);
            AspirantePrueba body = new AspirantePrueba();
            body.setId(idAspirantePrueba);
            entity.setIdAspirantePrueba(body);

            when(jornadaAulaDAO.findById(idJornadaAula)).thenReturn(jornadaAula);
            when(aspirantePruebaDAO.findById(idAspirantePrueba)).thenReturn(aspirantePrueba);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/jornadaAula/" + idJornadaAula + "/aspirantePrueba/" + idAspirantePrueba));

            doAnswer(inv -> {
                entity.setId(99);
                return null;
            }).when(jaaDAO).create(entity);

            Response resp = resource.create(idJornadaAula, entity, uriInfo);
            assertEquals(201, resp.getStatus());
            verify(jornadaAulaDAO).findById(idJornadaAula);
            verify(aspirantePruebaDAO).findById(idAspirantePrueba);
            verify(jaaDAO).create(entity);
        }

        @Test
        void retorna422_cuandoIdJornadaNulo(){
            Response resp = resource.create(null, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornadaAula", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaDAO, aspirantePruebaDAO, jaaDAO);
        }

        @Test
        void retorna422_cuandoEntidadNula(){
            Response resp = resource.create(idJornadaAula, null, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaDAO, aspirantePruebaDAO, jaaDAO);
        }

        @Test
        void retorna422_cuandoEntityTieneId(){
            entity.setId(5);
            Response resp = resource.create(idJornadaAula, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.id must be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaDAO, aspirantePruebaDAO, jaaDAO);
        }

        @Test
        void retorna422_cuandoIdAspiranteNoProvistoEnBody(){
            entity.setId(null);
            entity.setIdAspirantePrueba(null);
            Response resp = resource.create(idJornadaAula, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.idAspirantePrueba.id must be provided in body", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaDAO, aspirantePruebaDAO, jaaDAO);
        }

        @Test
        void retorna404_cuandoJornadaAulaNoExiste(){
            entity.setId(null);
            AspirantePrueba body = new AspirantePrueba();
            body.setId(idAspirantePrueba);
            entity.setIdAspirantePrueba(body);

            when(jornadaAulaDAO.findById(idJornadaAula)).thenReturn(null);
            Response resp = resource.create(idJornadaAula, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("JornadaAula with id " + idJornadaAula + " not found", resp.getHeaderString("Not-found-id"));
            verify(jornadaAulaDAO).findById(idJornadaAula);
            verifyNoMoreInteractions(aspirantePruebaDAO, jaaDAO);
        }

        @Test
        void retorna404_cuandoAspiranteNoExiste(){
            entity.setId(null);
            AspirantePrueba body = new AspirantePrueba();
            body.setId(idAspirantePrueba);
            entity.setIdAspirantePrueba(body);

            when(jornadaAulaDAO.findById(idJornadaAula)).thenReturn(jornadaAula);
            when(aspirantePruebaDAO.findById(idAspirantePrueba)).thenReturn(null);

            Response resp = resource.create(idJornadaAula, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("AspirantePrueba with id " + idAspirantePrueba + " not found", resp.getHeaderString("Not-found-id"));
            verify(jornadaAulaDAO).findById(idJornadaAula);
            verify(aspirantePruebaDAO).findById(idAspirantePrueba);
            verifyNoMoreInteractions(jaaDAO);
        }
    }

    @Nested
    class Update{
        @Test
        void retorna200_cuandoValido(){
            JornadaAulaAspirante existing = new JornadaAulaAspirante();
            existing.setId(1);
            existing.setIdAspirantePrueba(aspirantePrueba);
            when(jaaDAO.findByJornadaAula(idJornadaAula, 0, Integer.MAX_VALUE)).thenReturn(List.of(existing));

            JornadaAulaAspirante update = new JornadaAulaAspirante();
            update.setHoraLlegada(LocalTime.now());
            update.setAsistio(Boolean.FALSE);

            when(jaaDAO.update(any())).thenReturn(update);

            Response resp = resource.update(idJornadaAula, idAspirantePrueba, update);
            assertEquals(200, resp.getStatus());
            verify(jaaDAO).findByJornadaAula(idJornadaAula, 0, Integer.MAX_VALUE);
            verify(jaaDAO).update(any());
        }

        @Test
        void retorna422_cuandoIdJornadaNulo(){
            Response resp = resource.update(null, idAspirantePrueba, entity);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornadaAula", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jaaDAO);
        }

        @Test
        void retorna422_cuandoIdAspiranteNulo(){
            Response resp = resource.update(idJornadaAula, null, entity);
            assertEquals(422, resp.getStatus());
            assertEquals("idAspirantePrueba", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jaaDAO);
        }

        @Test
        void retorna422_cuandoEntidadNula(){
            Response resp = resource.update(idJornadaAula, idAspirantePrueba, null);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jaaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra(){
            when(jaaDAO.findByJornadaAula(idJornadaAula, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.update(idJornadaAula, idAspirantePrueba, entity);
            assertEquals(404, resp.getStatus());
            assertEquals("JornadaAulaAspirante with id jornadaAula="+idJornadaAula+", aspirantePrueba="+idAspirantePrueba+" not found",
                    resp.getHeaderString("Not-found-id"));
            verify(jaaDAO).findByJornadaAula(idJornadaAula, 0, Integer.MAX_VALUE);
        }
    }

    @Nested
    class Delete{
        @Test
        void retorna204_cuandoValido(){
            when(jaaDAO.findByJornadaAula(idJornadaAula, 0, Integer.MAX_VALUE)).thenReturn(List.of(jaa));
            Response resp = resource.delete(idJornadaAula, idAspirantePrueba);
            assertEquals(204, resp.getStatus());
            verify(jaaDAO).findByJornadaAula(idJornadaAula, 0, Integer.MAX_VALUE);
            verify(jaaDAO).delete(jaa);
        }

        @Test
        void retorna422_cuandoIdJornadaNulo(){
            Response resp = resource.delete(null, idAspirantePrueba);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornadaAula", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jaaDAO);
        }

        @Test
        void retorna422_cuandoIdAspiranteNulo(){
            Response resp = resource.delete(idJornadaAula, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idAspirantePrueba", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jaaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra(){
            when(jaaDAO.findByJornadaAula(idJornadaAula, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.delete(idJornadaAula, idAspirantePrueba);
            assertEquals(404, resp.getStatus());
            assertEquals("JornadaAulaAspirante with id jornadaAula="+idJornadaAula+", aspirantePrueba="+idAspirantePrueba+" not found",
                    resp.getHeaderString("Not-found-id"));
            verify(jaaDAO).findByJornadaAula(idJornadaAula, 0, Integer.MAX_VALUE);
        }
    }
}
