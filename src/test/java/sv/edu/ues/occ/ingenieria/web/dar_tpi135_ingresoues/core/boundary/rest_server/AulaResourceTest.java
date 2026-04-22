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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aula;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AulaResourceTest {

    @Mock
    AulaDAO aulaDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    AulaResource aulaResource;

    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final List<Aula> LISTA = List.of(
            new Aula(), new Aula()
    );
    private UUID id;
    private Aula aula;
    private Aula entity;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        aula = new Aula();
        aula.setId(id);
        entity = new Aula();
    }

    @Nested
    class FindRange {

        @Test
        void retorna200ConListaYHeaderParametrosValidos() {
            when(aulaDAO.findRange(FIRST, MAX)).thenReturn(LISTA);
            when(aulaDAO.count()).thenReturn(2);

            Response response = aulaResource.findRange(FIRST, MAX);

            assertEquals(200, response.getStatus());
            List<?> entidad = (List<?>) response.getEntity();
            assertEquals(2, entidad.size());
            assertEquals("2", response.getHeaderString("Total-records"));
            verify(aulaDAO).findRange(FIRST, MAX);
            verify(aulaDAO).count();
        }

        @Test
        void retorna422ParametrosInvalidos() {
            Response response = aulaResource.findRange(INVALIDFIRST, INVALIDMAX);

            assertEquals(422, response.getStatus());
            assertEquals("first,max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aulaDAO);
        }

        @Test
        void retorna422CuandoMaxExcedeLimite() {
            Response response = aulaResource.findRange(0, 11);

            assertEquals(422, response.getStatus());
            assertEquals("first,max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aulaDAO);
        }

        @Test
        void retorna422CuandoMaxEsCero() {
            Response response = aulaResource.findRange(0, 0);

            assertEquals(422, response.getStatus());
            assertEquals("first,max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aulaDAO);
        }

        @Test
        void retorna500CuandoDAOLanzaExcepcion() {
            when(aulaDAO.findRange(FIRST, MAX)).thenThrow(new RuntimeException("Error en la BD"));

            Response response = aulaResource.findRange(FIRST, MAX);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(aulaDAO).findRange(FIRST, MAX);
        }
    }

    @Nested
    class FindById {

        @Test
        void retorna200ConEntidadCuandoIdEsValido() {
            when(aulaDAO.findById(id)).thenReturn(aula);

            Response response = aulaResource.findById(id);

            assertEquals(200, response.getStatus());
            assertEquals(aula, response.getEntity());
            verify(aulaDAO).findById(id);
        }

        @Test
        void retorna422IdEsNulo() {
            Response response = aulaResource.findById(null);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aulaDAO);
        }

        @Test
        void retorna404NoSeEncuentraRegistro() {
            when(aulaDAO.findById(id)).thenReturn(null);

            Response response = aulaResource.findById(id);

            assertEquals(404, response.getStatus());
            assertEquals("Record with id " + id + " not found", response.getHeaderString("Not-found-id"));
            verify(aulaDAO).findById(id);
        }

        @Test
        void retorna500DAOlanzaExcepcion() {
            when(aulaDAO.findById(id)).thenThrow(new RuntimeException("Error en la BD"));

            Response response = aulaResource.findById(id);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(aulaDAO).findById(id);
        }
    }

    @Nested
    class Delete {

        @Test
        void retorna204IdEsValido() {
            when(aulaDAO.findById(id)).thenReturn(aula);

            Response response = aulaResource.delete(id);

            assertEquals(204, response.getStatus());
            verify(aulaDAO).findById(id);
            verify(aulaDAO).delete(aula);
        }

        @Test
        void retorna422IdEsNulo() {
            Response response = aulaResource.delete(null);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aulaDAO);
        }

        @Test
        void retorna404NoSeEncuentraRegistro() {
            when(aulaDAO.findById(id)).thenReturn(null);

            Response response = aulaResource.delete(id);

            assertEquals(404, response.getStatus());
            assertEquals("Record with id " + id + " not found", response.getHeaderString("Not-found-id"));
            verify(aulaDAO).findById(id);
        }

        @Test
        void retorna500DAOlanzaExcepcion() {
            when(aulaDAO.findById(id)).thenThrow(new RuntimeException("Error en la BD"));

            Response response = aulaResource.delete(id);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(aulaDAO).findById(id);
        }
    }

    @Nested
    class Create {

        @Test
        void retorna201EntidadEsValida() {
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/aula"));
            doAnswer(invocationOnMock -> {
                entity.setId(id);
                return null;
            }).when(aulaDAO).create(entity);

            Response response = aulaResource.create(entity, uriInfo);

            assertEquals(201, response.getStatus());
            assertEquals(entity, response.getEntity());
            verify(aulaDAO).create(entity);
        }

        @Test
        void retorna422EntidadEsNula() {
            Response response = aulaResource.create(null, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aulaDAO);
        }

        @Test
        void retorna422EntidadTieneId() {
            entity.setId(id);

            Response response = aulaResource.create(entity, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("entity.id must be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aulaDAO);
        }

        @Test
        void retorna500DAOlanzaExcepcion() {
            doThrow(new RuntimeException("Error en la BD")).when(aulaDAO).create(entity);

            Response response = aulaResource.create(entity, uriInfo);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(aulaDAO).create(entity);
        }
    }

    @Nested
    class Update {

        @Test
        void retorna200EntidadEsValida() {
            Aula existing = new Aula();
            existing.setId(id);
            Aula updated = new Aula();
            updated.setId(id);
            when(aulaDAO.findById(id)).thenReturn(existing);
            when(aulaDAO.update(updated)).thenReturn(updated);

            Response response = aulaResource.update(id, updated);

            assertEquals(200, response.getStatus());
            assertEquals(updated, response.getEntity());
            verify(aulaDAO).findById(id);
            verify(aulaDAO).update(updated);
        }

        @Test
        void retorna422IdEsNulo() {
            Response response = aulaResource.update(null, entity);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aulaDAO);
        }

        @Test
        void retorna422EntidadEsNula() {
            Response response = aulaResource.update(id, null);

            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aulaDAO);
        }

        @Test
        void retorna404NoSeEncuentraRegistro() {
            when(aulaDAO.findById(id)).thenReturn(null);

            Response response = aulaResource.update(id, entity);

            assertEquals(404, response.getStatus());
            assertEquals("Record with id " + id + " not found", response.getHeaderString("Not-found-id"));
            verify(aulaDAO).findById(id);
        }

        @Test
        void retorna500DAOlanzaExcepcion() {
            when(aulaDAO.findById(id)).thenThrow(new RuntimeException("Error en la BD"));

            Response response = aulaResource.update(id, entity);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(aulaDAO).findById(id);
        }
    }
}