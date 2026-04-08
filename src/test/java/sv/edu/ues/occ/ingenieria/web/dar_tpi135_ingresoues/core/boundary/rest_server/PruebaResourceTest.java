package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.validation.constraints.Max;
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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PruebaResourceTest {

    @Mock
    PruebaDAO pruebaDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    PruebaResource pruebaResource;

    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final List<Prueba> LISTA = List.of(
            new Prueba(), new Prueba()
    );
    private UUID id;
    private Prueba prueba;
    private Prueba entity;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        prueba = new Prueba();
        prueba.setId(id);
        entity = new Prueba();
    }


    @Nested
    class FindRange {

        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos() {
            when(pruebaDAO.findRange(FIRST, MAX)).thenReturn(LISTA);
            when(pruebaDAO.count()).thenReturn(2);

            Response response = pruebaResource.findRange(FIRST, MAX);

            assertEquals(200, response.getStatus());
            List<?> entidad = (List<?>) response.getEntity();
            assertEquals(2, entidad.size());
            assertEquals("2", response.getHeaderString("X-Total-Count"));
            verify(pruebaDAO).findRange(FIRST, MAX);
            verify(pruebaDAO).count();
        }

        @Test
        void retorna422_cuandoParametrosSonInvalidos() {
            Response response = pruebaResource.findRange(INVALIDFIRST, INVALIDMAX);

            assertEquals(422, response.getStatus());
            assertEquals("first,max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO);
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(pruebaDAO.findRange(FIRST, MAX)).thenThrow(new RuntimeException("DB error"));

            Response response = pruebaResource.findRange(FIRST, MAX);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(pruebaDAO).findRange(FIRST, MAX);
        }
    }

    @Nested
    class FindById {
        @Test
        void retorna200ConEntidad_cuandoIdEsValido() {
            when(pruebaDAO.findById(id)).thenReturn(prueba);

            Response response = pruebaResource.findById(id);

            assertEquals(200, response.getStatus());
            assertEquals(prueba, response.getEntity());
            verify(pruebaDAO).findById(id);
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response response = pruebaResource.findById(null);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro() {
            when(pruebaDAO.findById(id)).thenReturn(null);

            Response response = pruebaResource.findById(id);

            assertEquals(404, response.getStatus());
            assertEquals("Record with id " + id + " not found", response.getHeaderString("Not-found-id"));
            verify(pruebaDAO).findById(id);
        }

        @Test
        void retorna500_cuandoDAOlanzaExcepcion() {
            when(pruebaDAO.findById(id)).thenThrow(new RuntimeException("DB error"));

            Response response = pruebaResource.findById(id);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(pruebaDAO).findById(id);
        }
    }

    @Nested
    class Delete {
        @Test
        void retorna204_cuandoIdEsValido() {
            when(pruebaDAO.findById(id)).thenReturn(prueba);

            Response response = pruebaResource.deleteById(id);

            assertEquals(204, response.getStatus());
            verify(pruebaDAO).findById(id);
            verify(pruebaDAO).delete(prueba);
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response response = pruebaResource.deleteById(null);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro() {
            when(pruebaDAO.findById(id)).thenReturn(null);

            Response response = pruebaResource.deleteById(id);

            assertEquals(404, response.getStatus());
            assertEquals("Record with id " + id + " not found", response.getHeaderString("Not-found-id"));
            verify(pruebaDAO).findById(id);
        }

        @Test
        void retorna500_cuandoDAOlanzaExcepcion() {
            when(pruebaDAO.findById(id)).thenThrow(new RuntimeException("DB error"));

            Response response = pruebaResource.deleteById(id);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(pruebaDAO).findById(id);
        }
    }

    @Nested
    class Create {
        @Test
        void retorna201_cuandoEntidadEsValida() {
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/prueba"));
            doAnswer(invocationOnMock -> {
                entity.setId(id);
                return null;
            }).when(pruebaDAO).create(entity);

            Response response = pruebaResource.create(entity, uriInfo);

            assertEquals(201, response.getStatus());
            assertEquals(entity, response.getEntity());
            verify(pruebaDAO).create(entity);
        }

        @Test
        void retorna422_cuandoEntidadEsNula() {
            Response response = pruebaResource.create(null, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO);
        }

        @Test
        void retorna422_cuandoEntidadTieneId() {
            entity.setId(id);

            Response response = pruebaResource.create(entity, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("entity.id must be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO);
        }

        @Test
        void retorna500_cuandoDAOlanzaExcepcion() {
            doThrow(new RuntimeException("DB error")).when(pruebaDAO).create(entity);

            Response response = pruebaResource.create(entity, uriInfo);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(pruebaDAO).create(entity);
        }
    }

    @Nested
    class Update {
        @Test
        void retorna200_cuandoEntidadEsValida() {
            Prueba existing = new Prueba();
            existing.setId(id);
            Prueba updated = new Prueba();
            updated.setId(id);
            when(pruebaDAO.findById(id)).thenReturn(existing);
            when(pruebaDAO.update(updated)).thenReturn(updated);

            Response response = pruebaResource.update(id, updated);

            assertEquals(200, response.getStatus());
            assertEquals(updated, response.getEntity());
            verify(pruebaDAO).findById(id);
            verify(pruebaDAO).update(updated);
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response response = pruebaResource.update(null, entity);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO);
        }

        @Test
        void retorna422_cuandoEntidadEsNula() {
            Response response = pruebaResource.update(id, null);

            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro() {
            when(pruebaDAO.findById(id)).thenReturn(null);

            Response response = pruebaResource.update(id, entity);

            assertEquals(404, response.getStatus());
            assertEquals("Record with id " + id + " not found", response.getHeaderString("Not-found-id"));
            verify(pruebaDAO).findById(id);
        }

        @Test
        void retorna500_cuandoDAOlanzaExcepcion() {
            when(pruebaDAO.findById(id)).thenThrow( new RuntimeException("DB error"));

            Response response = pruebaResource.update(id, entity);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(pruebaDAO).findById(id);
        }
    }
}