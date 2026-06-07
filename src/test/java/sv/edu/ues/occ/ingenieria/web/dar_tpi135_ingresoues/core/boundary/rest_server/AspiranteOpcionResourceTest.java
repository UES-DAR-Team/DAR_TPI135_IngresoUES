package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.inject.Inject;
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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AspiranteOpcionDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.OpcionDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspiranteOpcion;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Opcion;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AspiranteOpcionResourceTest {

    @Mock
    AspiranteOpcionDAO aspiranteOpcionDAO;

    @Mock
    AspiranteDAO aspiranteDAO;

    @Mock
    OpcionDAO opcionDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    AspiranteOpcionResource resource;

    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final int EXCEEDMAX = 11;

    private UUID idAspirante;
    private UUID idOpcion;
    private Aspirante aspirante;
    private Opcion opcion;
    private AspiranteOpcion aspiranteOpcion;
    private AspiranteOpcion entity;

    @BeforeEach
    void setUp() {
        idAspirante = UUID.randomUUID();
        idOpcion = UUID.randomUUID();

        aspirante = new Aspirante();
        aspirante.setId(idAspirante);

        opcion = new Opcion();
        opcion.setId(idOpcion);

        aspiranteOpcion = new AspiranteOpcion();
        aspiranteOpcion.setId(1);
        aspiranteOpcion.setIdAspirante(aspirante);
        aspiranteOpcion.setIdOpcion(opcion);

        entity = new AspiranteOpcion();
    }


    @Nested
    class FindRange{

        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos(){
            when(aspiranteDAO.findById(idAspirante)).thenReturn(aspirante);
            when(aspiranteOpcionDAO.findOpcionByIdAspirante(idAspirante, FIRST, MAX)).thenReturn(List.of(aspiranteOpcion));
            when(aspiranteOpcionDAO.count()).thenReturn(1);

            var resp = resource.findRange(idAspirante, FIRST, MAX);

            assertEquals(200, resp.getStatus());
            List<?> list = (List<?>) resp.getEntity();
            assertEquals(1, list.size());
            assertEquals("1", resp.getHeaderString("X-Total-Count"));
            verify(aspiranteDAO).findById(idAspirante);
            verify(aspiranteOpcionDAO).findOpcionByIdAspirante(idAspirante, FIRST, MAX);
            verify(aspiranteOpcionDAO).count();
        }

        @Test
        void retorna422_cuandoIdAspiranteEsNulo(){
                var resp = resource.findRange(null, FIRST, MAX);
                assertEquals(422, resp.getStatus());
                assertEquals("idAspirante", resp.getHeaderString("Missing-parameter"));
                verifyNoInteractions(aspiranteDAO, aspiranteOpcionDAO);
        }

        @Test
        void retorna404_cuandoAspiranteNoExiste(){
            when(aspiranteDAO.findById(idAspirante)).thenReturn(null);

            var resp = resource.findRange(idAspirante, FIRST, MAX);
            assertEquals(404, resp.getStatus());
            assertEquals("Aspirante with id " + idAspirante + " not found", resp.getHeaderString("Not-found-id"));
            verify(aspiranteDAO).findById(idAspirante);
            verifyNoMoreInteractions(aspiranteOpcionDAO);
        }

        @Test
        void retorna422_cuandoMaxEsInvalido(){
            Response resp = resource.findRange(idAspirante, FIRST, INVALIDMAX);
            assertEquals(422, resp.getStatus());
            assertEquals("first, max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteDAO, aspiranteOpcionDAO);
        }

        @Test
        void retorna422_cuandoMaxEsExcedido(){
            Response resp = resource.findRange(idAspirante, FIRST, EXCEEDMAX);
            assertEquals(422, resp.getStatus());
            assertEquals("first, max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteDAO, aspiranteOpcionDAO);
        }

        @Test
        void retorna422_cuandoFirstEsInvalido(){
            Response resp = resource.findRange(idAspirante, INVALIDFIRST, MAX);
            assertEquals(422, resp.getStatus());
            assertEquals("first, max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteDAO, aspiranteOpcionDAO);
        }



    }

    @Nested
    class FindOne{

        @Test
        void retorna200ConEntidad_cuandoIdEsValido(){
            when(aspiranteOpcionDAO.findOpcionByIdAspirante(idAspirante, 0, Integer.MAX_VALUE)).thenReturn(List.of(aspiranteOpcion));

            var resp = resource.findOne(idAspirante, idOpcion);
            assertEquals(200, resp.getStatus());
            assertEquals(aspiranteOpcion, resp.getEntity());
            verify(aspiranteOpcionDAO).findOpcionByIdAspirante(idAspirante, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna422_cuandoIdAspiranteEsNulo(){
            var resp = resource.findOne(null, idOpcion);
            assertEquals(422, resp.getStatus());
            assertEquals("idAspirante", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteOpcionDAO);
        }

        @Test
        void retorna422_cuandoIdOpcionEsNulo(){
            var resp = resource.findOne(idAspirante, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idOpcion", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteOpcionDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro(){
                when(aspiranteOpcionDAO.findOpcionByIdAspirante(idAspirante, 0, Integer.MAX_VALUE)).thenReturn(List.of());

                var resp = resource.findOne(idAspirante, idOpcion);
                assertEquals(404, resp.getStatus());
                assertEquals("AspiranteOpcion with id aspirante=" + idAspirante + ", opcion=" + idOpcion + " not found"
                        , resp.getHeaderString("Not-found-id"));
                verify(aspiranteOpcionDAO).findOpcionByIdAspirante(idAspirante, 0, Integer.MAX_VALUE);
        }



    }

    @Nested
    class Create{
        @Test
        void retorna201_cuandoEntidadEsValida(){
            entity.setId(null);
            Opcion bodyOpcion = new Opcion();
            bodyOpcion.setId(idOpcion);
            entity.setIdOpcion(bodyOpcion);

            when(aspiranteDAO.findById(idAspirante)).thenReturn(aspirante);
            when(opcionDAO.findById(idOpcion)).thenReturn(opcion);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/aspirantes/" + idAspirante + "/opciones/" + idOpcion));

            doAnswer(inv ->{
                entity.setId(7);
                return null;
            }).when(aspiranteOpcionDAO).create(entity);

            var resp = resource.create(idAspirante, entity, uriInfo);
            assertEquals(201, resp.getStatus());
            verify(aspiranteDAO).findById(idAspirante);
            verify(opcionDAO).findById(idOpcion);
            verify(aspiranteOpcionDAO).create(entity);
        }

        @Test
        void retorna422_cuandoIdAspiranteEsNulo(){
            var resp = resource.create(null, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idAspirante", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteDAO, opcionDAO, aspiranteOpcionDAO);
        }

        @Test
        void retorna422_cuandoEntidadEsNula(){
            var resp = resource.create(idAspirante, null, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteDAO, opcionDAO, aspiranteOpcionDAO);
        }

        @Test
        void retorna422_cuandoEntidadTieneId(){
            entity.setId(7);
            var resp = resource.create(idAspirante, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.id must be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteDAO, opcionDAO, aspiranteOpcionDAO);
        }

        @Test
        void retorna422_cuandoIdOpcionNoPrivistoEnBody(){
            entity.setId(null);
            var resp = resource.create(idAspirante, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.idOpcion must be provided in body", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteDAO, opcionDAO, aspiranteOpcionDAO);
        }

        @Test
        void retorna404_cuandoAspiranteNoExiste(){
            entity.setId(null);
            Opcion bodyOpcion = new Opcion();
            bodyOpcion.setId(idOpcion);
            entity.setIdOpcion(bodyOpcion);

            when(aspiranteDAO.findById(idAspirante)).thenReturn(null);

            var resp = resource.create(idAspirante, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("Aspirante with id " + idAspirante + " not found", resp.getHeaderString("Not-found-id"));
            verify(aspiranteDAO).findById(idAspirante);
            verifyNoMoreInteractions(opcionDAO, aspiranteOpcionDAO);
        }

        @Test
        void retorna404_cuandoOpcionNoExiste(){
            entity.setId(null);
            Opcion bodyOpcion = new Opcion();
            bodyOpcion.setId(idOpcion);
            entity.setIdOpcion(bodyOpcion);

            when(aspiranteDAO.findById(idAspirante)).thenReturn(aspirante);
            when(opcionDAO.findById(idOpcion)).thenReturn(null);

            var resp = resource.create(idAspirante, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("Opcion with id " + idOpcion + " not found", resp.getHeaderString("Not-found-id"));
            verify(aspiranteDAO).findById(idAspirante);
            verify(opcionDAO).findById(idOpcion);
            verifyNoMoreInteractions(aspiranteOpcionDAO);
        }



    }

    @Nested
    class Delete{

        @Test
        void retorna204_cuandoIdEsValido(){
            when(aspiranteOpcionDAO.findOpcionByIdAspirante(idAspirante, 0, Integer.MAX_VALUE)).thenReturn(List.of(aspiranteOpcion));
            var resp = resource.delete(idAspirante, idOpcion);
            assertEquals(204, resp.getStatus());
            verify(aspiranteOpcionDAO).findOpcionByIdAspirante(idAspirante, 0, Integer.MAX_VALUE);
            verify(aspiranteOpcionDAO).delete(aspiranteOpcion);
        }

        @Test
        void retorna422_cuandoIdOpcionEsNulo(){
            var resp = resource.delete(idAspirante, null);
            assertEquals(422, resp.getStatus());
            assertEquals("idOpcion", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteOpcionDAO);
        }

        @Test
        void retorna422_cuandoIdAspiranteEsNulo(){
            var resp = resource.delete(null, idOpcion);
            assertEquals(422, resp.getStatus());
            assertEquals("idAspirante", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aspiranteOpcionDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro(){
            when(aspiranteOpcionDAO.findOpcionByIdAspirante(idAspirante, 0, Integer.MAX_VALUE)).thenReturn(List.of());

            var resp = resource.delete(idAspirante, idOpcion);
            assertEquals(404, resp.getStatus());
            assertEquals("AspiranteOpcion with id aspirante=" + idAspirante + ", opcion=" + idOpcion + " not found"
                    , resp.getHeaderString("Not-found-id"));
            verify(aspiranteOpcionDAO).findOpcionByIdAspirante(idAspirante, 0, Integer.MAX_VALUE);
        }


    }
}