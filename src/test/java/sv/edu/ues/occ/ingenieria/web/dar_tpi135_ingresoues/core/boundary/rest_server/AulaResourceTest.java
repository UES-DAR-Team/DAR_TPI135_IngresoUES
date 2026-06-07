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
    private static final int EXCEEDMAX = 11;
    private static final List<Aula> LISTA = List.of(new Aula(), new Aula());

    private UUID idAula;
    private Aula aula;
    private Aula entity;

    @BeforeEach
    void setUp() {
        idAula = UUID.randomUUID();
        aula = new Aula();
        aula.setId(idAula);
        entity = new Aula();
    }

    @Nested
    class FindRange {

        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos() {
            when(aulaDAO.findRange(FIRST, MAX)).thenReturn(LISTA);
            when(aulaDAO.count()).thenReturn(2);

            Response response = aulaResource.findRange(FIRST, MAX);

            assertEquals(200, response.getStatus());
            List<?> entidad = (List<?>) response.getEntity();
            assertEquals(2, entidad.size());
            assertEquals("2", response.getHeaderString("X-Total-Count"));
            verify(aulaDAO).findRange(FIRST, MAX);
            verify(aulaDAO).count();
        }

        @Test
        void retorna422_cuandoMaxEsInvalido() {
            Response response = aulaResource.findRange(FIRST, INVALIDMAX);

            assertEquals(422, response.getStatus());
            assertEquals("first, max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aulaDAO);
        }

        @Test
        void retorna422_cuandoMaxEsExcedido() {
            Response response = aulaResource.findRange(FIRST, EXCEEDMAX);

            assertEquals(422, response.getStatus());
            assertEquals("first, max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aulaDAO);
        }

        @Test
        void retorna422_cuandoFirstEsInvalido() {
            Response response = aulaResource.findRange(INVALIDFIRST, MAX);

            assertEquals(422, response.getStatus());
            assertEquals("first, max", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aulaDAO);
        }

    }

    @Nested
    class FindById {

        @Test
        void retorna200ConEntidad_cuandoIdEsValido() {
            when(aulaDAO.findById(idAula)).thenReturn(aula);

            Response response = aulaResource.findById(idAula);

            assertEquals(200, response.getStatus());
            assertEquals(aula, response.getEntity());
            verify(aulaDAO).findById(idAula);
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response response = aulaResource.findById(null);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aulaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro() {
            when(aulaDAO.findById(idAula)).thenReturn(null);

            Response response = aulaResource.findById(idAula);

            assertEquals(404, response.getStatus());
            assertEquals("Aula with id " + idAula + " not found", response.getHeaderString("Not-found-id"));
            verify(aulaDAO).findById(idAula);
        }

    }

    @Nested
    class Delete {

        @Test
        void retorna204_cuandoIdEsValido() {
            when(aulaDAO.findById(idAula)).thenReturn(aula);

            Response response = aulaResource.delete(idAula);

            assertEquals(204, response.getStatus());
            verify(aulaDAO).findById(idAula);
            verify(aulaDAO).delete(aula);
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response response = aulaResource.delete(null);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aulaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro() {
            when(aulaDAO.findById(idAula)).thenReturn(null);

            Response response = aulaResource.delete(idAula);

            assertEquals(404, response.getStatus());
            assertEquals("Aula with id " + idAula + " not found", response.getHeaderString("Not-found-id"));
            verify(aulaDAO).findById(idAula);
        }

    }

    @Nested
    class Create {
        @Test
        void retorna201_cuandoEntidadEsValida() {
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/aula/"));
            doAnswer(inv -> {
                entity.setId(idAula);
                return null;
            }).when(aulaDAO).create(entity);

            Response response = aulaResource.create(entity, uriInfo);

            assertEquals(201, response.getStatus());

            verify(aulaDAO).create(entity);
        }

        @Test
        void retorna422_cuandoEntidadEsNula() {
            Response response = aulaResource.create(null, uriInfo);

            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aulaDAO);
        }

        @Test
        void retorna422_cuandoEntidadTieneId() {
            entity.setId(idAula);

            Response response = aulaResource.create(entity, uriInfo);
            assertEquals(422, response.getStatus());
            assertEquals("entity.id must be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aulaDAO);
        }

    }

    @Nested
    class Update {

        @Test
        void retorna200_cuandoEntidadEsValida() {
            Aula existing = new Aula();
            existing.setId(idAula);
            Aula update = new Aula();
            update.setId(idAula);
            when(aulaDAO.findById(idAula)).thenReturn(existing);
            when(aulaDAO.update(update)).thenReturn(update);

            Response response = aulaResource.update(idAula, update);

            assertEquals(200, response.getStatus());
            assertEquals(update, response.getEntity());
            verify(aulaDAO).findById(idAula);
            verify(aulaDAO).update(update);
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response response = aulaResource.update(null, entity);

            assertEquals(422, response.getStatus());
            assertEquals("id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aulaDAO);
        }

        @Test
        void retorna422_cuandoEntidadEsNula() {
            Response response = aulaResource.update(idAula, null);

            assertEquals(422, response.getStatus());
            assertEquals("entity must not be null", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(aulaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro() {
            when(aulaDAO.findById(idAula)).thenReturn(null);

            Response response = aulaResource.update(idAula, entity);

            assertEquals(404, response.getStatus());
            assertEquals("Aula with id " + idAula + " not found", response.getHeaderString("Not-found-id"));
            verify(aulaDAO).findById(idAula);
        }

    }

}

