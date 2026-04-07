package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JornadaResourceTest {

    private JornadaResource resource;
    private JornadaDAO dao;

    @BeforeEach
    void setUp() {
        resource = new JornadaResource();
        dao = mock(JornadaDAO.class);

        resource.jornadaDAO = dao;
    }

    @Test
    void findRange_success() {
        when(dao.count()).thenReturn(1);
        when(dao.findRange(0, 10)).thenReturn(Collections.emptyList());

        Response r = resource.findRange(0, 10);

        assertEquals(200, r.getStatus());
    }

    @Test
    void findRange_invalid() {
        Response r = resource.findRange(-1, 200);

        assertEquals(422, r.getStatus());
    }

    @Test
    void findRange_exception() {
        when(dao.count()).thenThrow(new RuntimeException());

        Response r = resource.findRange(0, 10);

        assertEquals(500, r.getStatus());
    }

    @Test
    void findById_success() {
        UUID id = UUID.randomUUID();

        when(dao.findById(id)).thenReturn(new Jornada());

        Response r = resource.findById(id);

        assertEquals(200, r.getStatus());
    }

    @Test
    void findById_notFound() {
        UUID id = UUID.randomUUID();

        when(dao.findById(id)).thenReturn(null);

        Response r = resource.findById(id);

        assertEquals(404, r.getStatus());
    }

    @Test
    void findById_null() {
        Response r = resource.findById(null);

        assertEquals(422, r.getStatus());
    }

    @Test
    void findById_exception() {
        UUID id = UUID.randomUUID();

        when(dao.findById(id)).thenThrow(new RuntimeException());

        Response r = resource.findById(id);

        assertEquals(500, r.getStatus());
    }

    @Test
    void delete_success() {
        UUID id = UUID.randomUUID();
        Jornada j = new Jornada();

        when(dao.findById(id)).thenReturn(j);

        Response r = resource.delete(id);

        assertEquals(204, r.getStatus());
        verify(dao).delete(j);
    }

    @Test
    void delete_notFound() {
        UUID id = UUID.randomUUID();

        when(dao.findById(id)).thenReturn(null);

        Response r = resource.delete(id);

        assertEquals(404, r.getStatus());
    }

    @Test
    void delete_null() {
        Response r = resource.delete(null);

        assertEquals(422, r.getStatus());
    }

    @Test
    void delete_exception() {
        UUID id = UUID.randomUUID();

        when(dao.findById(id)).thenThrow(new RuntimeException());

        Response r = resource.delete(id);

        assertEquals(500, r.getStatus());
    }

    @Test
    void create_success() {
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
    void create_null() {
        Response r = resource.create(null, mock(UriInfo.class));

        assertEquals(422, r.getStatus());
    }

    @Test
    void create_withId() {
        Jornada entity = new Jornada();
        entity.setId(UUID.randomUUID());

        Response r = resource.create(entity, mock(UriInfo.class));

        assertEquals(422, r.getStatus());
    }

    @Test
    void create_exception() {
        Jornada entity = new Jornada();

        doThrow(new RuntimeException()).when(dao).create(entity);

        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getAbsolutePathBuilder())
                .thenReturn(UriBuilder.fromUri("http://localhost"));

        Response r = resource.create(entity, uriInfo);

        assertEquals(500, r.getStatus());
    }

    @Test
    void update_success() {
        UUID id = UUID.randomUUID();

        when(dao.findById(id)).thenReturn(new Jornada());
        when(dao.update(any())).thenReturn(new Jornada());

        Response r = resource.update(id, new Jornada());

        assertEquals(200, r.getStatus());
    }

    @Test
    void update_invalid() {
        Response r = resource.update(null, null);

        assertEquals(422, r.getStatus());
    }

    @Test
    void update_notFound() {
        UUID id = UUID.randomUUID();

        when(dao.findById(id)).thenReturn(null);

        Response r = resource.update(id, new Jornada());

        assertEquals(404, r.getStatus());
    }

    @Test
    void update_exception() {
        UUID id = UUID.randomUUID();

        when(dao.findById(id)).thenThrow(new RuntimeException("boom"));

        Response r = resource.update(id, new Jornada());

        assertEquals(500, r.getStatus());
    }
}