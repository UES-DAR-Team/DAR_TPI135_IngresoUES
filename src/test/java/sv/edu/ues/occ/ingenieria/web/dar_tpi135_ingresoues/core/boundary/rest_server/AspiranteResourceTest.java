package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AspiranteDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AspiranteResourceTest {

    private AspiranteResource resource;
    private AspiranteDAO dao;

    @BeforeEach
    void setUp() {
        resource = new AspiranteResource();
        dao = mock(AspiranteDAO.class);
        resource.aspiranteDAO = dao;
    }

    @Nested
    class FindRange {

        @Test
        void retorna200_cuandoParametrosSonValidos() {
            when(dao.count()).thenReturn(1);
            when(dao.findRange(0, 10)).thenReturn(Collections.emptyList());

            Response response = resource.findRange(0, 10);

            assertEquals(200, response.getStatus());
            assertEquals("1", response.getHeaderString("Total-records"));
        }

        @Test
        void retorna422_cuandoFirstEsNegativo() {
            Response response = resource.findRange(-1, 10);
            assertEquals(422, response.getStatus());
        }

        @Test
        void retorna422_cuandoMaxEsCero() {
            Response response = resource.findRange(0, 0);
            assertEquals(422, response.getStatus());
        }

        @Test
        void retorna422_cuandoMaxExcedeLimite() {
            Response response = resource.findRange(0, 101);
            assertEquals(422, response.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(dao.count()).thenThrow(new RuntimeException());

            Response response = resource.findRange(0, 10);

            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
        }
    }

    @Nested
    class FindById {

        @Test
        void retorna200_cuandoExiste() {
            UUID id = UUID.randomUUID();
            Aspirante a = new Aspirante();
            a.setId(id);
            when(dao.findById(id)).thenReturn(a);

            Response response = resource.findById(id);

            assertEquals(200, response.getStatus());
        }

        @Test
        void retorna404_cuandoNoExiste() {
            UUID id = UUID.randomUUID();
            when(dao.findById(id)).thenReturn(null);

            Response response = resource.findById(id);

            assertEquals(404, response.getStatus());
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response response = resource.findById(null);
            assertEquals(422, response.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            UUID id = UUID.randomUUID();
            when(dao.findById(id)).thenThrow(new RuntimeException());

            Response response = resource.findById(id);

            assertEquals(500, response.getStatus());
        }
    }

    @Nested
    class Create {

        @Test
        void retorna201_cuandoCreacionExitosa() {
            Aspirante a = new Aspirante();
            UriInfo uriInfo = mock(UriInfo.class);
            when(uriInfo.getAbsolutePathBuilder())
                    .thenReturn(UriBuilder.fromUri("http://localhost/aspirante"));

            doAnswer(invocation -> {
                a.setId(UUID.randomUUID());
                return null;
            }).when(dao).create(a);

            Response response = resource.create(a, uriInfo);

            assertEquals(201, response.getStatus());
        }

        @Test
        void retorna422_cuandoEntityEsNula() {
            Response response = resource.create(null, mock(UriInfo.class));
            assertEquals(422, response.getStatus());
        }

        @Test
        void retorna422_cuandoIdNoEsNulo() {
            Aspirante a = new Aspirante();
            a.setId(UUID.randomUUID());

            Response response = resource.create(a, mock(UriInfo.class));

            assertEquals(422, response.getStatus());
        }

        @Test
        void retorna409_cuandoHayDuplicateKey() {
            Aspirante a = new Aspirante();
            UriInfo uriInfo = mock(UriInfo.class);

            RuntimeException cause = new RuntimeException("duplicate key value violates unique constraint");
            doThrow(new RuntimeException(cause)).when(dao).create(a);

            Response response = resource.create(a, uriInfo);

            assertEquals(409, response.getStatus());
            assertEquals("Ya existe un registro con esos datos",
                    response.getHeaderString("Conflict"));
        }

        @Test
        void retorna500_cuandoExcepcionConMensaje() {
            // e.getCause() != null pero mensaje no contiene duplicate key
            Aspirante a = new Aspirante();
            UriInfo uriInfo = mock(UriInfo.class);

            RuntimeException cause = new RuntimeException("otro error de BD");
            doThrow(new RuntimeException(cause)).when(dao).create(a);

            Response response = resource.create(a, uriInfo);

            assertEquals(500, response.getStatus());
        }

        @Test
        void retorna500_cuandoExcepcionSinCause() {
            Aspirante a = new Aspirante();
            UriInfo uriInfo = mock(UriInfo.class);

            doThrow(new RuntimeException("error generico")).when(dao).create(a);

            Response response = resource.create(a, uriInfo);

            assertEquals(500, response.getStatus());
        }

        @Test
        void retorna500_cuandoExcepcionSinMensaje() {
            // cause.getMessage() == null → msg queda como ""
            Aspirante a = new Aspirante();
            UriInfo uriInfo = mock(UriInfo.class);

            RuntimeException cause = new RuntimeException((String) null);
            doThrow(new RuntimeException(cause)).when(dao).create(a);

            Response response = resource.create(a, uriInfo);

            assertEquals(500, response.getStatus());
        }
    }


    @Nested
    class Update {

        @Test
        void retorna200_cuandoActualizacionExitosa() {
            UUID id = UUID.randomUUID();
            Aspirante existing = new Aspirante();
            Aspirante updated = new Aspirante();

            when(dao.findById(id)).thenReturn(existing);
            when(dao.update(any())).thenReturn(updated);

            Response response = resource.update(id, new Aspirante());

            assertEquals(200, response.getStatus());
        }

        @Test
        void retorna404_cuandoNoExiste() {
            UUID id = UUID.randomUUID();
            when(dao.findById(id)).thenReturn(null);

            Response response = resource.update(id, new Aspirante());

            assertEquals(404, response.getStatus());
        }

        @Test
        void retorna422_cuandoParametrosSonNulos() {
            Response response = resource.update(null, null);
            assertEquals(422, response.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            UUID id = UUID.randomUUID();
            when(dao.findById(id)).thenThrow(new RuntimeException("boom"));

            Response response = resource.update(id, new Aspirante());

            assertEquals(500, response.getStatus());
        }

        @Test
        void retorna422_cuandoEntityEsNula() {
            Response response = resource.update(UUID.randomUUID(), null);
            assertEquals(422, response.getStatus());
        }
    }

    @Nested
    class Delete {

        @Test
        void retorna204_cuandoEliminacionExitosa() {
            UUID id = UUID.randomUUID();
            Aspirante a = new Aspirante();
            a.setId(id);
            when(dao.findById(id)).thenReturn(a);

            Response response = resource.delete(id);

            assertEquals(204, response.getStatus());
            verify(dao).delete(a);
        }

        @Test
        void retorna404_cuandoNoExiste() {
            UUID id = UUID.randomUUID();
            when(dao.findById(id)).thenReturn(null);

            Response response = resource.delete(id);

            assertEquals(404, response.getStatus());
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response response = resource.delete(null);
            assertEquals(422, response.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            UUID id = UUID.randomUUID();
            when(dao.findById(id)).thenThrow(new RuntimeException());

            Response response = resource.delete(id);

            assertEquals(500, response.getStatus());
        }

    }
}