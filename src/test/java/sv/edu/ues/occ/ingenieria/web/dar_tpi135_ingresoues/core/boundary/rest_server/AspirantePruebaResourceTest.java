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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AspiranteDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AspirantePruebaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspirantePrueba;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AspirantePruebaResourceTest {

    @Mock
    AspirantePruebaDAO aspirantePruebaDAO;

    @Mock
    AspiranteDAO aspiranteDAO;

    @Mock
    PruebaDAO pruebaDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    AspirantePruebaResource resource;

    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final int EXCEEDMAX = 11;

    private UUID idAspirante;
    private UUID idPrueba;
    private Aspirante aspirante;
    private Prueba prueba;
    private AspirantePrueba ap;
    private AspirantePrueba entity;

    @BeforeEach
    void setUp(){
        idAspirante = UUID.randomUUID();
        idPrueba = UUID.randomUUID();

        aspirante = new Aspirante();
        aspirante.setId(idAspirante);

        prueba = new Prueba();
        prueba.setId(idPrueba);

        ap = new AspirantePrueba();
        ap.setId(1);
        ap.setIdAspirante(aspirante);
        ap.setIdPrueba(prueba);

        entity = new AspirantePrueba();
    }

    @Nested
    class FindRange{
        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos(){
            when(aspiranteDAO.findById(idAspirante)).thenReturn(aspirante);
            when(aspirantePruebaDAO.findByAspirante(idAspirante, FIRST, MAX)).thenReturn(List.of(ap));
            when(aspirantePruebaDAO.count()).thenReturn(1);

            Response resp = resource.findRange(idAspirante, FIRST, MAX);

            assertEquals(200, resp.getStatus());
            List<?> entidad = (List<?>) resp.getEntity();
            assertEquals(1, entidad.size());
            assertEquals("1", resp.getHeaderString("X-Total-Count"));
            verify(aspiranteDAO).findById(idAspirante);
            verify(aspirantePruebaDAO).findByAspirante(idAspirante, FIRST, MAX);
            verify(aspirantePruebaDAO).count();
        }

        @Test
        void retorna422_cuandoIdAspiranteNulo(){
            Response resp = resource.findRange(null, FIRST, MAX);
            assertEquals(422, resp.getStatus());
            assertEquals("idAspirante", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteDAO, aspirantePruebaDAO);
        }

        @Test
        void retorna422_cuandoFirstInvalido(){
            Response resp = resource.findRange(idAspirante, INVALIDFIRST, MAX);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteDAO, aspirantePruebaDAO);
        }

        @Test
        void retorna422_cuandoMaxInvalido(){
            Response resp = resource.findRange(idAspirante, FIRST, INVALIDMAX);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteDAO, aspirantePruebaDAO);
        }

        @Test
        void retorna422_cuandoMaxExcedido(){
            Response resp = resource.findRange(idAspirante, FIRST, EXCEEDMAX);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteDAO, aspirantePruebaDAO);
        }

        @Test
        void retorna404_cuandoAspiranteNoExiste(){
            when(aspiranteDAO.findById(idAspirante)).thenReturn(null);
            Response resp = resource.findRange(idAspirante, FIRST, MAX);
            assertEquals(404, resp.getStatus());
            assertEquals("Aspirante with id " + idAspirante + " not found", resp.getHeaderString("Not-found-id"));
            verify(aspiranteDAO).findById(idAspirante);
            verifyNoMoreInteractions(aspirantePruebaDAO);
        }
    }

    @Nested
    class FindOne{
        @Test
        void retorna200ConEntidad_cuandoExiste(){
            when(aspirantePruebaDAO.findByAspirante(idAspirante, 0, Integer.MAX_VALUE)).thenReturn(List.of(ap));
            Response resp = resource.findOne(idAspirante, idPrueba);
            assertEquals(200, resp.getStatus());
            assertEquals(ap, resp.getEntity());
            verify(aspirantePruebaDAO).findByAspirante(idAspirante, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna422_cuandoIdAspiranteNulo(){
            Response resp = resource.findOne(null, idPrueba);
            assertEquals(422, resp.getStatus());
            assertEquals("idAspirante", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspirantePruebaDAO);
        }

        @Test
        void retorna422_cuandoIdPruebaNulo(){
            Response resp = resource.findOne(idAspirante, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idPrueba", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspirantePruebaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra(){
            when(aspirantePruebaDAO.findByAspirante(idAspirante, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.findOne(idAspirante, idPrueba);
            assertEquals(404, resp.getStatus());
            assertEquals("AspirantePrueba with id prueba= " + idPrueba + ", aspirante=" + idAspirante + " not found",
                    resp.getHeaderString("Not-found-id"));
            verify(aspirantePruebaDAO).findByAspirante(idAspirante, 0, Integer.MAX_VALUE);
        }
    }

    @Nested
    class Create{
        @Test
        void retorna201_cuandoValido(){
            entity.setId(null);
            Prueba body = new Prueba();
            body.setId(idPrueba);
            entity.setIdPrueba(body);

            when(aspiranteDAO.findById(idAspirante)).thenReturn(aspirante);
            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/aspirante/" + idAspirante + "/prueba/" + idPrueba));

            doAnswer(inv -> {
                entity.setId(99);
                return null;
            }).when(aspirantePruebaDAO).create(entity);

            Response resp = resource.create(idAspirante, entity, uriInfo);
            assertEquals(201, resp.getStatus());
            verify(aspiranteDAO).findById(idAspirante);
            verify(pruebaDAO).findById(idPrueba);
            verify(aspirantePruebaDAO).create(entity);
        }

        @Test
        void retorna422_cuandoIdAspiranteNulo(){
            Response resp = resource.create(null, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idAspirante", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteDAO, pruebaDAO, aspirantePruebaDAO);
        }

        @Test
        void retorna422_cuandoEntidadNula(){
            Response resp = resource.create(idAspirante, null, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteDAO, pruebaDAO, aspirantePruebaDAO);
        }

        @Test
        void retorna422_cuandoEntityTieneId(){
            entity.setId(5);
            Response resp = resource.create(idAspirante, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.id must be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteDAO, pruebaDAO, aspirantePruebaDAO);
        }

        @Test
        void retorna422_cuandoIdPruebaNoProvistoEnBody(){
            entity.setId(null);
            entity.setIdPrueba(null);
            Response resp = resource.create(idAspirante, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.idPrueba must be provided in body", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteDAO, pruebaDAO, aspirantePruebaDAO);
        }

        @Test
        void retorna404_cuandoAspiranteNoExiste(){
            entity.setId(null);
            Prueba body = new Prueba();
            body.setId(idPrueba);
            entity.setIdPrueba(body);

            when(aspiranteDAO.findById(idAspirante)).thenReturn(null);
            Response resp = resource.create(idAspirante, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("Aspirante with id " + idAspirante + " not found", resp.getHeaderString("Not-found-id"));
            verify(aspiranteDAO).findById(idAspirante);
            verifyNoMoreInteractions(pruebaDAO, aspirantePruebaDAO);
        }

        @Test
        void retorna404_cuandoPruebaNoExiste(){
            entity.setId(null);
            Prueba body = new Prueba();
            body.setId(idPrueba);
            entity.setIdPrueba(body);

            when(aspiranteDAO.findById(idAspirante)).thenReturn(aspirante);
            when(pruebaDAO.findById(idPrueba)).thenReturn(null);

            Response resp = resource.create(idAspirante, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("Prueba with id " + idPrueba + " not found", resp.getHeaderString("Not-found-id"));
            verify(aspiranteDAO).findById(idAspirante);
            verify(pruebaDAO).findById(idPrueba);
            verifyNoMoreInteractions(aspirantePruebaDAO);
        }
    }

    @Nested
    class Delete{
        @Test
        void retorna204_cuandoValido(){
            when(aspirantePruebaDAO.findByAspirante(idAspirante, 0, Integer.MAX_VALUE)).thenReturn(List.of(ap));
            Response resp = resource.delete(idAspirante, idPrueba);
            assertEquals(204, resp.getStatus());
            verify(aspirantePruebaDAO).findByAspirante(idAspirante, 0, Integer.MAX_VALUE);
            verify(aspirantePruebaDAO).delete(ap);
        }

        @Test
        void retorna422_cuandoIdAspiranteNulo(){
            Response resp = resource.delete(null, idPrueba);
            assertEquals(422, resp.getStatus());
            assertEquals("idAspirante", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspirantePruebaDAO);
        }

        @Test
        void retorna422_cuandoIdPruebaNulo(){
            Response resp = resource.delete(idAspirante, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idPrueba", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspirantePruebaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra(){
            when(aspirantePruebaDAO.findByAspirante(idAspirante, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.delete(idAspirante, idPrueba);
            assertEquals(404, resp.getStatus());
            assertEquals("AspirantePrueba with id prueba= " + idPrueba + ", aspirante=" + idAspirante + " not found",
                    resp.getHeaderString("Not-found-id"));
            verify(aspirantePruebaDAO).findByAspirante(idAspirante, 0, Integer.MAX_VALUE);
        }
    }
}

