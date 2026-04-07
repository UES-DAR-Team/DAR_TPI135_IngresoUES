package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AspiranteDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AspiranteResourceTest {

    private AspiranteResource resource;
    private AspiranteDAO dao;

    @BeforeEach
    void setUp() {
        resource = new AspiranteResource();
        dao = mock(AspiranteDAO.class);

        resource.aspiranteDAO = dao;
    }

    @Test
    void findRange_success() {
        when(dao.count()).thenReturn(1);
        when(dao.findRange(0, 10)).thenReturn(Collections.emptyList());

        Response response = resource.findRange(0, 10);

        assertEquals(200, response.getStatus());
        assertEquals("1", response.getHeaderString("Total-records"));
    }

    @Test
    void findRange_invalidParams() {
        Response response = resource.findRange(-1, 200);

        assertEquals(422, response.getStatus());
        assertEquals("first,max", response.getHeaderString("Missing-parameter"));
    }

    @Test
    void findRange_exception() {
        when(dao.count()).thenThrow(new RuntimeException());

        Response response = resource.findRange(0, 10);

        assertEquals(500, response.getStatus());
        assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
    }

    @Test
    void findById_success() {
        UUID id = UUID.randomUUID();
        Aspirante a = new Aspirante();
        a.setId(id);

        when(dao.findById(id)).thenReturn(a);

        Response response = resource.findById(id);

        assertEquals(200, response.getStatus());
    }

    @Test
    void findById_notFound() {
        UUID id = UUID.randomUUID();

        when(dao.findById(id)).thenReturn(null);

        Response response = resource.findById(id);

        assertEquals(404, response.getStatus());
    }

    @Test
    void findById_null() {
        Response response = resource.findById(null);

        assertEquals(422, response.getStatus());
    }

    @Test
    void findById_exception() {
        UUID id = UUID.randomUUID();

        when(dao.findById(id)).thenThrow(new RuntimeException());

        Response response = resource.findById(id);

        assertEquals(500, response.getStatus());
    }

    @Test
    void delete_success() {
        UUID id = UUID.randomUUID();
        Aspirante a = new Aspirante();
        a.setId(id);

        when(dao.findById(id)).thenReturn(a);

        Response response = resource.delete(id);

        assertEquals(204, response.getStatus());
        verify(dao).delete(a);
    }

    @Test
    void delete_notFound() {
        UUID id = UUID.randomUUID();

        when(dao.findById(id)).thenReturn(null);

        Response response = resource.delete(id);

        assertEquals(404, response.getStatus());
    }

    @Test
    void delete_null() {
        Response response = resource.delete(null);

        assertEquals(422, response.getStatus());
    }

    @Test
    void delete_exception() {
        UUID id = UUID.randomUUID();

        when(dao.findById(id)).thenThrow(new RuntimeException());

        Response response = resource.delete(id);

        assertEquals(500, response.getStatus());
    }

    @Test
    void create_success() {
        Aspirante a = new Aspirante();

        UriInfo uriInfo = mock(UriInfo.class);
        UriBuilder builder = UriBuilder.fromUri("http://localhost/aspirante");

        when(uriInfo.getAbsolutePathBuilder()).thenReturn(builder);

        doAnswer(invocation -> {
            a.setId(UUID.randomUUID());
            return null;
        }).when(dao).create(a);

        Response response = resource.create(a, uriInfo);

        assertEquals(201, response.getStatus());
    }

    @Test
    void create_invalid_nullEntity() {
        Response response = resource.create(null, mock(UriInfo.class));

        assertEquals(422, response.getStatus());
    }

    @Test
    void create_invalid_idNotNull() {
        Aspirante a = new Aspirante();
        a.setId(UUID.randomUUID());

        Response response = resource.create(a, mock(UriInfo.class));

        assertEquals(422, response.getStatus());
    }

    @Test
    void create_exception() {
        Aspirante a = new Aspirante();

        doThrow(new RuntimeException()).when(dao).create(a);

        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getAbsolutePathBuilder())
                .thenReturn(UriBuilder.fromUri("http://localhost"));

        Response response = resource.create(a, uriInfo);

        assertEquals(500, response.getStatus());
    }

    @Test
    void update_success() {
        UUID id = UUID.randomUUID();
        Aspirante existing = new Aspirante();
        Aspirante updated = new Aspirante();

        when(dao.findById(id)).thenReturn(existing);
        when(dao.update(any())).thenReturn(updated);

        Response response = resource.update(id, new Aspirante());

        assertEquals(200, response.getStatus());
    }

    @Test
    void update_notFound() {
        UUID id = UUID.randomUUID();

        when(dao.findById(id)).thenReturn(null);

        Response response = resource.update(id, new Aspirante());

        assertEquals(404, response.getStatus());
    }

    @Test
    void update_invalid() {
        Response response = resource.update(null, null);

        assertEquals(422, response.getStatus());
    }

    @Test
    void update_exception() {
        UUID id = UUID.randomUUID();

        when(dao.findById(id)).thenThrow(new RuntimeException("boom"));

        Response response = resource.update(id, new Aspirante());

        assertEquals(500, response.getStatus());
    }
}