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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JornadaResourceTest {

    private JornadaResource resource;
    private JornadaDAO dao;

    @BeforeEach
    void setUp() {
        resource = new JornadaResource();
        dao = mock(JornadaDAO.class);
        resource.jornadaDAO = dao;
    }

    @Nested
    class FindRange {

        @Test
        void retorna200_cuandoParametrosSonValidos() {
            when(dao.count()).thenReturn(1);
            when(dao.findRange(0, 10)).thenReturn(Collections.emptyList());

            Response r = resource.findRange(0, 10);

            assertEquals(200, r.getStatus());
            assertEquals("1", r.getHeaderString("Total-records"));
        }

        @Test
        void retorna422_cuandoFirstEsNegativo() {
            Response r = resource.findRange(-1, 10);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoMaxEsCero() {
            Response r = resource.findRange(0, 0);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoMaxExcedeLimite() {
            Response r = resource.findRange(0, 101);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(dao.count()).thenThrow(new RuntimeException());

            Response r = resource.findRange(0, 10);

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class FindById {

        @Test
        void retorna200_cuandoExiste() {
            UUID id = UUID.randomUUID();
            when(dao.findById(id)).thenReturn(new Jornada());

            Response r = resource.findById(id);

            assertEquals(200, r.getStatus());
        }

        @Test
        void retorna404_cuandoNoExiste() {
            UUID id = UUID.randomUUID();
            when(dao.findById(id)).thenReturn(null);

            Response r = resource.findById(id);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response r = resource.findById(null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            UUID id = UUID.randomUUID();
            when(dao.findById(id)).thenThrow(new RuntimeException());

            Response r = resource.findById(id);

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class Create {

        @Test
        void retorna201_cuandoCreacionExitosa() {
            Jornada entity = new Jornada();
            UriInfo uriInfo = mock(UriInfo.class);
            when(uriInfo.getAbsolutePathBuilder())
                    .thenReturn(UriBuilder.fromUri("http://localhost"));

            doAnswer(inv -> {
                entity.setId(UUID.randomUUID());
                return null;
            }).when(dao).create(entity);

            Response r = resource.create(entity, uriInfo);

            assertEquals(201, r.getStatus());
        }

        @Test
        void retorna422_cuandoEntityEsNula() {
            Response r = resource.create(null, mock(UriInfo.class));
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdNoEsNulo() {
            Jornada entity = new Jornada();
            entity.setId(UUID.randomUUID());

            Response r = resource.create(entity, mock(UriInfo.class));

            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna409_cuandoHayDuplicateKey() {
            Jornada entity = new Jornada();
            UriInfo uriInfo = mock(UriInfo.class);
            when(uriInfo.getAbsolutePathBuilder())
                    .thenReturn(UriBuilder.fromUri("http://localhost"));

            RuntimeException cause = new RuntimeException("duplicate key value violates unique constraint");
            doThrow(new RuntimeException(cause)).when(dao).create(entity);

            Response r = resource.create(entity, uriInfo);

            assertEquals(409, r.getStatus());
            assertEquals("Ya existe una jornada con esos datos",
                    r.getHeaderString("Conflict"));
        }

        @Test
        void retorna500_cuandoExcepcionSinCause() {
            // e.getCause() == null → usa e directamente
            Jornada entity = new Jornada();
            doThrow(new RuntimeException("error generico")).when(dao).create(entity);

            Response r = resource.create(entity, mock(UriInfo.class));

            assertEquals(500, r.getStatus());
        }

        @Test
        void retorna500_cuandoExcepcionSinMensaje() {
            // cause.getMessage() == null → msg queda como ""
            Jornada entity = new Jornada();
            doThrow(new RuntimeException(new RuntimeException((String) null)))
                    .when(dao).create(entity);

            Response r = resource.create(entity, mock(UriInfo.class));

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class Update {

        @Test
        void retorna200_cuandoActualizacionExitosa() {
            UUID id = UUID.randomUUID();
            when(dao.findById(id)).thenReturn(new Jornada());
            when(dao.update(any())).thenReturn(new Jornada());

            Response r = resource.update(id, new Jornada());

            assertEquals(200, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            // id == null (cortocircuito)
            Response r = resource.update(null, new Jornada());
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoEntityEsNula() {
            // id != null pero entity == null
            Response r = resource.update(UUID.randomUUID(), null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna404_cuandoNoExiste() {
            UUID id = UUID.randomUUID();
            when(dao.findById(id)).thenReturn(null);

            Response r = resource.update(id, new Jornada());

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            UUID id = UUID.randomUUID();
            when(dao.findById(id)).thenThrow(new RuntimeException("boom"));

            Response r = resource.update(id, new Jornada());

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class Delete {

        @Test
        void retorna204_cuandoEliminacionExitosa() {
            UUID id = UUID.randomUUID();
            Jornada j = new Jornada();
            when(dao.findById(id)).thenReturn(j);

            Response r = resource.delete(id);

            assertEquals(204, r.getStatus());
            verify(dao).delete(j);
        }

        @Test
        void retorna404_cuandoNoExiste() {
            UUID id = UUID.randomUUID();
            when(dao.findById(id)).thenReturn(null);

            Response r = resource.delete(id);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response r = resource.delete(null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            UUID id = UUID.randomUUID();
            when(dao.findById(id)).thenThrow(new RuntimeException());

            Response r = resource.delete(id);

            assertEquals(500, r.getStatus());
        }
    }
}