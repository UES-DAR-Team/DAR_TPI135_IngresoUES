package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import com.sun.tools.rngom.util.Uri;
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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;

import java.awt.geom.Area;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AreaConocimientoResourceTest {

    @Mock
    AreaConocimientoDAO areaConocimientoDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    AreaConocimientoResource areaConocimientoResource;

    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final int EXCEEDMAX = 11;
    private static final List<AreaConocimiento> LISTA = List.of(
            new AreaConocimiento(),
            new AreaConocimiento()
    );

    private UUID id;
    private AreaConocimiento areaConocimiento;
    private AreaConocimiento entity;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        areaConocimiento = new AreaConocimiento();
        areaConocimiento.setId(id);
        entity = new AreaConocimiento();
    }

    @Nested
    class findRange {

        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos() {
            when(areaConocimientoDAO.findRange(FIRST, MAX)).thenReturn(LISTA);
            when(areaConocimientoDAO.count()).thenReturn(2);

            Response response = areaConocimientoResource.findRange(FIRST, MAX);

            assertEquals(200, response.getStatus());
            assertEquals(LISTA, response.getEntity());
            List<?> entidad = (List<?>) response.getEntity();
            assertEquals(2, entidad.size());
            assertEquals("2", response.getHeaderString("X-Total-Count"));
            verify(areaConocimientoDAO).findRange(FIRST, MAX);
            verify(areaConocimientoDAO).count();
        }

        @Test
        void retorna422_cuandoFirstInvalido(){
                Response response = areaConocimientoResource.findRange(INVALIDFIRST, MAX);

                assertEquals(422, response.getStatus());
                assertEquals("first,max", response.getHeaderString("Missing-parameter"));
                verifyNoInteractions(areaConocimientoDAO);
        }
        @Test
        void retorna422_cuandoMaxEsInvalido(){
            Response response = areaConocimientoResource.findRange(FIRST, INVALIDMAX);

            assertEquals(422, response.getStatus());
            assertEquals("first,max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO);
        }
        @Test
        void retorna422_cuandoMaxEsInvalidoPorMas(){
            Response response = areaConocimientoResource.findRange(FIRST, EXCEEDMAX);

            assertEquals(422, response.getStatus());
            assertEquals("first,max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(areaConocimientoDAO.findRange(FIRST, MAX)).thenThrow(new RuntimeException("DB error"));

            Response response = areaConocimientoResource.findRange(FIRST, MAX);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(areaConocimientoDAO).findRange(FIRST, MAX);
        }
    }

    @Nested
    class findById {

        @Test
        void retorna200ConEntidad_cuandoParametrosSonValidos(){
            when(areaConocimientoDAO.findById(id)).thenReturn(areaConocimiento);

            Response response = areaConocimientoResource.findById(id);

            assertEquals(200, response.getStatus());
            assertEquals(areaConocimiento, response.getEntity());
            verify(areaConocimientoDAO).findById(id);
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response response = areaConocimientoResource.findById(null);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro() {
            when(areaConocimientoDAO.findById(id)).thenReturn(null);

            Response response = areaConocimientoResource.findById(id);

            assertEquals(404, response.getStatus());
            assertEquals("Record with id " + id + " not found", response.getHeaderString("Not-found-id"));
            verify(areaConocimientoDAO).findById(id);
        }

        @Test
        void retorna500_cuandoDAOlanzaExcepcion() {
            when(areaConocimientoDAO.findById(id)).thenThrow(new RuntimeException("DB error"));

            Response response = areaConocimientoResource.findById(id);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(areaConocimientoDAO).findById(id);
        }
    }

    @Nested
    class Delete {
        @Test
        void retorna204_cuandoIdEsValido() {
            when(areaConocimientoDAO.findById(id)).thenReturn(areaConocimiento);

            Response response = areaConocimientoResource.deleteById(id);

            assertEquals(204, response.getStatus());
            verify(areaConocimientoDAO).findById(id);
            verify(areaConocimientoDAO).delete(areaConocimiento);
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response response = areaConocimientoResource.deleteById(null);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro() {
            when(areaConocimientoDAO.findById(id)).thenReturn(null);

            Response response = areaConocimientoResource.deleteById(id);

            assertEquals(404, response.getStatus());
            assertEquals("Record with id " + id + " not found", response.getHeaderString("Not-found-id"));
            verify(areaConocimientoDAO).findById(id);
        }

        @Test
        void retorna500_cuandoDAOlanzaExcepcion() {
            when(areaConocimientoDAO.findById(id)).thenThrow(new RuntimeException("DB error"));

            Response response = areaConocimientoResource.deleteById(id);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(areaConocimientoDAO).findById(id);
        }

        @Test
        void retorna204_cuandoIdEsValidoYNoTieneHijos() {
            when(areaConocimientoDAO.findById(id)).thenReturn(areaConocimiento);
            when(areaConocimientoDAO.findHijosByPadre(id)).thenReturn(List.of());

            Response response = areaConocimientoResource.deleteById(id);

            assertEquals(204, response.getStatus());
            verify(areaConocimientoDAO).findById(id);
            verify(areaConocimientoDAO).findHijosByPadre(id);
            verify(areaConocimientoDAO).delete(areaConocimiento);
        }

        @Test
        void retorna409_cuandoTieneHijos() {
            when(areaConocimientoDAO.findById(id)).thenReturn(areaConocimiento);
            when(areaConocimientoDAO.findHijosByPadre(id)).thenReturn(List.of(new AreaConocimiento()));

            Response response = areaConocimientoResource.deleteById(id);

            assertEquals(409, response.getStatus());
            assertEquals("Record with id " + id + " has child records and cannot be deleted", response.getHeaderString("Conflict-id"));
            verify(areaConocimientoDAO).findById(id);
            verify(areaConocimientoDAO).findHijosByPadre(id);
            verify(areaConocimientoDAO, never()).delete(any());
        }
    }

    @Nested
    class Create {
        @Test
        void retorna201_cuandoEntidadEsValida() {
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("v1/areaConocimiento/"));
            doAnswer(inv -> {
                entity.setId(id);
                return null;
            }).when(areaConocimientoDAO).create(entity);

            Response response = areaConocimientoResource.create(entity, uriInfo);

            assertEquals(201, response.getStatus());
            assertEquals(entity, response.getEntity());
            verify(areaConocimientoDAO).create(entity);
        }

        @Test
        void retorna422_cuandoEntidadEsNula() {
            Response response = areaConocimientoResource.create(null, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO);
        }

        @Test
        void retorna422_cuandoEntidadTieneId() {
            entity.setId(id);

            Response response = areaConocimientoResource.create(entity, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("entity.id must be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO);
        }

        @Test
        void retorna500_cuandoDAOlanzaExcepcion() {
            doThrow(new RuntimeException("DB error")).when(areaConocimientoDAO).create(entity);

            Response response = areaConocimientoResource.create(entity, uriInfo);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(areaConocimientoDAO).create(entity);
        }

        @Test
        void retorna404_cuandoAreaPadreAsignadaNoExiste() {
            AreaConocimiento padre = new AreaConocimiento();
            UUID padreIdInexistente = UUID.randomUUID();
            padre.setId(padreIdInexistente);
            entity.setIdAutoReferenciaArea(padre);

            when(areaConocimientoDAO.findById(padreIdInexistente)).thenReturn(null);

            Response response = areaConocimientoResource.create(entity, uriInfo);

            assertEquals(404, response.getStatus());
            assertEquals("idAutoReferenciaArea with id " + padreIdInexistente + " not found", response.getHeaderString("Invalid-parameter"));
            verify(areaConocimientoDAO).findById(padreIdInexistente);
            verify(areaConocimientoDAO, never()).create(any());
        }

        @Test
        void retorna201_cuandoEntidadEsValidaYAreaPadreExiste() {
            AreaConocimiento padre = new AreaConocimiento();
            UUID padreId = UUID.randomUUID();
            padre.setId(padreId);
            entity.setIdAutoReferenciaArea(padre);

            when(areaConocimientoDAO.findById(padreId)).thenReturn(padre);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("v1/areaconocimiento/"));
            doAnswer(inv -> {
                entity.setId(id);
                return null;
            }).when(areaConocimientoDAO).create(entity);

            Response response = areaConocimientoResource.create(entity, uriInfo);

            assertEquals(201, response.getStatus());
            verify(areaConocimientoDAO).findById(padreId);
            verify(areaConocimientoDAO).create(entity);
        }

    }

    @Nested
    class Update {
        @Test
        void retorna200_cuandoEntidadEsValida() {
            AreaConocimiento existing = new AreaConocimiento();
            existing.setId(id);
            AreaConocimiento update = new AreaConocimiento();
            update.setId(id);
            when(areaConocimientoDAO.findById(id)).thenReturn(existing);
            when(areaConocimientoDAO.update(update)).thenReturn(update);

            Response response = areaConocimientoResource.update(id, update);

            assertEquals(200, response.getStatus());
            assertEquals(update, response.getEntity());
            verify(areaConocimientoDAO).findById(id);
            verify(areaConocimientoDAO).update(update);
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response response = areaConocimientoResource.update(null, entity);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO);
        }

        @Test
        void retorna422_cuandoEntidadEsNula() {
            Response response = areaConocimientoResource.update(id, null);

            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(areaConocimientoDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro() {
            when(areaConocimientoDAO.findById(id)).thenReturn(null);

            Response response = areaConocimientoResource.update(id, entity);

            assertEquals(404, response.getStatus());
            assertEquals("Record with id " + id + " not found", response.getHeaderString("Not-found-id"));
            verify(areaConocimientoDAO).findById(id);
        }

        @Test
        void retorna500_cuandoDAOlanzaExcepcion() {
            when(areaConocimientoDAO.findById(id)).thenThrow( new RuntimeException("DB error"));;

            Response response = areaConocimientoResource.update(id, entity);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(areaConocimientoDAO).findById(id);
        }
        @Test
        void retorna404_cuandoAreaPadreAsignadaNoExiste() {
            AreaConocimiento existing = new AreaConocimiento();
            existing.setId(id);

            AreaConocimiento padre = new AreaConocimiento();
            UUID padreId = UUID.randomUUID();
            padre.setId(padreId);
            entity.setIdAutoReferenciaArea(padre);

            when(areaConocimientoDAO.findById(id)).thenReturn(existing);
            when(areaConocimientoDAO.findById(padreId)).thenReturn(null);

            Response response = areaConocimientoResource.update(id, entity);

            assertEquals(404, response.getStatus());
            assertEquals("idAutoReferenciaArea with id " + padreId + " not found", response.getHeaderString("Invalid-parameter"));
            verify(areaConocimientoDAO).findById(id);
            verify(areaConocimientoDAO).findById(padreId);
            verify(areaConocimientoDAO, never()).update(any());
        }

        @Test
        void retorna200_cuandoAreaPadreAsignadaExiste() {
            AreaConocimiento existing = new AreaConocimiento();
            existing.setId(id);

            AreaConocimiento padre = new AreaConocimiento();
            UUID padreId = UUID.randomUUID();
            padre.setId(padreId);
            entity.setIdAutoReferenciaArea(padre);

            when(areaConocimientoDAO.findById(id)).thenReturn(existing);
            when(areaConocimientoDAO.findById(padreId)).thenReturn(padre);
            when(areaConocimientoDAO.update(entity)).thenReturn(entity);

            Response response = areaConocimientoResource.update(id, entity);

            assertEquals(200, response.getStatus());
            verify(areaConocimientoDAO).findById(id);
            verify(areaConocimientoDAO).findById(padreId);
            verify(areaConocimientoDAO).update(entity);
        }
    }
}