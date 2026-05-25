package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.OpcionDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Opcion;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpcionResourceTest {

    @Mock
    OpcionDAO opcionDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    OpcionResource opcionResource;

    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final int EXCEEDMAX = 11;
    private static final List<Opcion> LISTA = List.of(
            new Opcion(),
            new Opcion());
    private UUID idOpcion = UUID.randomUUID();
    private Opcion opcion;
    private Opcion entity;

    @BeforeEach
    void setUp() {
        idOpcion = UUID.randomUUID();
        opcion = new Opcion();
        opcion.setId(idOpcion);
        entity = new Opcion();
    }

    @Nested
    class FindRange {

        @Test
        void retorna200conListaYHeader_cuandoPaarametrosSonValidos() {
            when(opcionDAO.findRange(FIRST, MAX)).thenReturn(LISTA);
            when(opcionDAO.count()).thenReturn(2);

            Response response = opcionResource.findRange(FIRST,MAX);

            assertEquals(200, response.getStatus());
            List<?> entidad = (List<?>) response.getEntity();
            assertEquals(2, entidad.size());
            assertEquals("2", response.getHeaderString("X-Total-Count"));
            verify(opcionDAO).findRange(FIRST, MAX);
            verify(opcionDAO).count();
        }

        @Test
        void retorna422_cuandoMaxEsInvalido() {
            Response response = opcionResource.findRange(FIRST,INVALIDMAX);

            assertEquals(422,response.getStatus());
            assertEquals("first,max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(opcionDAO);
        }

        @Test
        void retorna422_cuandoMaxEsExcedido() {
            Response response = opcionResource.findRange(FIRST,EXCEEDMAX);

            assertEquals(422,response.getStatus());
            assertEquals("first,max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(opcionDAO);
        }

        @Test
        void retorna422_cuandoFirstEsInvalido() {
            Response response = opcionResource.findRange(INVALIDFIRST,MAX);

            assertEquals(422,response.getStatus());
            assertEquals("first,max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(opcionDAO);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(opcionDAO.findRange(FIRST, MAX)).thenThrow(new RuntimeException("DB error"));

            Response response = opcionResource.findRange(FIRST,MAX);

            assertEquals(500,response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(opcionDAO).findRange(FIRST, MAX);
        }

    }

    @Nested
    class FindById{
        @Test
        void retorna200ConEntidad_cuandoIdEsValido(){
            when(opcionDAO.findById(idOpcion)).thenReturn(opcion);

            Response response = opcionResource.findById(idOpcion);

            assertEquals(200, response.getStatus());
            assertEquals(opcion, response.getEntity());
            verify(opcionDAO).findById(idOpcion);
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response response = opcionResource.findById(null);

            assertEquals(422,response.getStatus());
            assertEquals("id",response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(opcionDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro(){
            when(opcionDAO.findById(idOpcion)).thenReturn(null);

            Response response = opcionResource.findById(idOpcion);

            assertEquals(404,response.getStatus());
            assertEquals("Record with id " + idOpcion + " not found",response.getHeaderString("Not-found-id"));
            verify(opcionDAO).findById(idOpcion);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion(){
            when(opcionDAO.findById(idOpcion)).thenThrow(new RuntimeException("DB error"));

            Response response = opcionResource.findById(idOpcion);

            assertEquals(500,response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(opcionDAO).findById(idOpcion);
        }
    }

    @Nested
    class Delete{
        @Test
        void retorna204_cuandoIdEsValido(){
            when(opcionDAO.findById(idOpcion)).thenReturn(opcion);

            Response response = opcionResource.deleteById(idOpcion);

            assertEquals(204, response.getStatus());
            verify(opcionDAO).findById(idOpcion);
            verify(opcionDAO).delete(opcion);
        }

        @Test
        void retorna422_cuandoIdEsNulo(){
            Response response = opcionResource.deleteById(null);

            assertEquals(422,response.getStatus());
            assertEquals("id",response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(opcionDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro(){
            when(opcionDAO.findById(idOpcion)).thenReturn(null);

            Response response = opcionResource.deleteById(idOpcion);

            assertEquals(404,response.getStatus());
            assertEquals("Record with id " + idOpcion + " not found", response.getHeaderString("Not-found-id"));
            verify(opcionDAO).findById(idOpcion);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion(){
            when(opcionDAO.findById(idOpcion)).thenThrow(new RuntimeException("DB error"));

            Response response = opcionResource.deleteById(idOpcion);

            assertEquals(500,response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(opcionDAO).findById(idOpcion);
        }
    }


    @Nested
    class Create{

        @Test
        void retorna201_cuandoEntidadEsValida(){
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("v1/opcion/") );
            doAnswer(inv -> {
                entity.setId(idOpcion);
                return null;
            }).when(opcionDAO).create(entity);

            Response response = opcionResource.create(entity, uriInfo);

            assertEquals(201, response.getStatus());
            assertEquals(entity, response.getEntity());
            verify(opcionDAO).create(entity);
        }

        @Test
        void retorna422_cuandoEntidadEsNula(){
            Response response = opcionResource.create(null, uriInfo);

            assertEquals(422,response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(opcionDAO);
        }

        @Test
        void retorna422_cuandoEntidadTieneId(){
            entity.setId(idOpcion);

            Response response = opcionResource.create(entity, uriInfo);

            assertEquals(422,response.getStatus());
            assertEquals("entity.id must be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(opcionDAO);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion(){
            doThrow(new RuntimeException("DB error")).when(opcionDAO).create(entity);

            Response response = opcionResource.create(entity, uriInfo);

            assertEquals(500,response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(opcionDAO).create(entity);
        }
    }

    @Nested
    class Update{
        @Test
        void retorna200_cuandoEntidadEsvalida(){
            Opcion existing = new Opcion();
            existing.setId(idOpcion);
            Opcion update = new Opcion();
            update.setId(idOpcion);
            when(opcionDAO.findById(idOpcion)).thenReturn(existing);
            when(opcionDAO.update(update)).thenReturn(update);

            Response response = opcionResource.update(idOpcion, update);

            assertEquals(200, response.getStatus());
            assertEquals(update, response.getEntity());
            verify(opcionDAO).findById(idOpcion);
            verify(opcionDAO).update(update);
        }

        @Test
        void retorna422_cuandoIdEsNulo(){
            Response response = opcionResource.update(null, entity);

            assertEquals(422,response.getStatus());
            assertEquals("id",response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(opcionDAO);
        }
        @Test
        void retorna422_cuandoEntidadEsNula(){
            Response  response = opcionResource.update(idOpcion, null);

            assertEquals(422,response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(opcionDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro(){
            when(opcionDAO.findById(idOpcion)).thenReturn(null);

            Response response = opcionResource.update(idOpcion, entity);

            assertEquals(404,response.getStatus());
            assertEquals("Record with id " + idOpcion + " not found", response.getHeaderString("Not-found-id"));
            verify(opcionDAO).findById(idOpcion);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion(){
            when(opcionDAO.findById(idOpcion)).thenThrow(new RuntimeException("DB error"));

            Response response = opcionResource.update(idOpcion, entity);

            assertEquals(500,response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(opcionDAO).findById(idOpcion);
        }

    }

}