package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AspiranteDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AspiranteOpcioneDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspiranteOpcione;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AspiranteOpcioneResourceTest {

    private AspiranteOpcioneResource resource;
    private AspiranteOpcioneDAO opcioneDAO;
    private AspiranteDAO aspiranteDAO;

    @BeforeEach
    void setUp() {
        resource = new AspiranteOpcioneResource();
        opcioneDAO = mock(AspiranteOpcioneDAO.class);
        aspiranteDAO = mock(AspiranteDAO.class);

        resource.aspiranteOpcioneDAO = opcioneDAO;
        resource.aspiranteDAO = aspiranteDAO;
    }

    @Test
    void findById_success() {
        UUID idAspirante = UUID.randomUUID();
        Integer id = 1;

        Aspirante aspirante = new Aspirante();
        aspirante.setId(idAspirante);

        AspiranteOpcione op = new AspiranteOpcione();
        op.setId(id);
        op.setIdAspirante(aspirante);

        when(opcioneDAO.findById(id)).thenReturn(op);

        Response response = resource.findById(idAspirante, id);

        assertEquals(200, response.getStatus());
    }

    @Test
    void findById_notFound() {
        UUID idAspirante = UUID.randomUUID();
        Integer id = 1;

        when(opcioneDAO.findById(id)).thenReturn(null);

        Response response = resource.findById(idAspirante, id);

        assertEquals(404, response.getStatus());
    }

    @Test
    void findById_mismatchAspirante() {
        UUID idAspirante = UUID.randomUUID();
        UUID otro = UUID.randomUUID();
        Integer id = 1;

        Aspirante aspirante = new Aspirante();
        aspirante.setId(otro);

        AspiranteOpcione op = new AspiranteOpcione();
        op.setId(id);
        op.setIdAspirante(aspirante);

        when(opcioneDAO.findById(id)).thenReturn(op);

        Response response = resource.findById(idAspirante, id);

        assertEquals(404, response.getStatus());
    }

    @Test
    void findById_invalidParams() {
        Response response = resource.findById(null, null);

        assertEquals(422, response.getStatus());
    }

    @Test
    void findById_exception() {
        Integer id = 1;

        when(opcioneDAO.findById(id)).thenThrow(new RuntimeException());

        Response response = resource.findById(UUID.randomUUID(), id);

        assertEquals(500, response.getStatus());
    }

    @Test
    void create_success() {
        UUID idAspirante = UUID.randomUUID();

        AspiranteOpcione entity = new AspiranteOpcione();

        Aspirante aspirante = new Aspirante();
        aspirante.setId(idAspirante);

        when(aspiranteDAO.findById(idAspirante)).thenReturn(aspirante);

        // Simular generación de ID
        doAnswer(inv -> {
            entity.setId(1);
            return null;
        }).when(opcioneDAO).create(entity);

        UriInfo uriInfo = mock(UriInfo.class);
        UriBuilder builder = UriBuilder.fromUri("http://localhost/opciones");

        when(uriInfo.getAbsolutePathBuilder()).thenReturn(builder);

        Response response = resource.create(idAspirante, entity, uriInfo);

        assertEquals(201, response.getStatus());
    }

    @Test
    void create_invalidParams() {
        Response response = resource.create(null, null, mock(UriInfo.class));

        assertEquals(422, response.getStatus());
    }

    @Test
    void create_entityWithId() {
        AspiranteOpcione entity = new AspiranteOpcione();
        entity.setId(1);

        Response response = resource.create(UUID.randomUUID(), entity, mock(UriInfo.class));

        assertEquals(422, response.getStatus());
    }

    @Test
    void create_aspiranteNotFound() {
        UUID idAspirante = UUID.randomUUID();

        when(aspiranteDAO.findById(idAspirante)).thenReturn(null);

        Response response = resource.create(idAspirante, new AspiranteOpcione(), mock(UriInfo.class));

        assertEquals(404, response.getStatus());
    }

    @Test
    void create_exception() {
        UUID idAspirante = UUID.randomUUID();

        when(aspiranteDAO.findById(idAspirante)).thenThrow(new RuntimeException());

        Response response = resource.create(idAspirante, new AspiranteOpcione(), mock(UriInfo.class));

        assertEquals(500, response.getStatus());
    }

    @Test
    void update_success() {
        UUID idAspirante = UUID.randomUUID();
        Integer id = 1;

        AspiranteOpcione existing = new AspiranteOpcione();
        AspiranteOpcione updated = new AspiranteOpcione();

        Aspirante aspirante = new Aspirante();
        aspirante.setId(idAspirante);

        when(opcioneDAO.findById(id)).thenReturn(existing);
        when(aspiranteDAO.findById(idAspirante)).thenReturn(aspirante);
        when(opcioneDAO.update(any())).thenReturn(updated);

        Response response = resource.update(idAspirante, id, new AspiranteOpcione());

        assertEquals(200, response.getStatus());
    }

    @Test
    void update_invalidParams() {
        Response response = resource.update(null, null, null);

        assertEquals(422, response.getStatus());
    }

    @Test
    void update_notFoundOpcion() {
        when(opcioneDAO.findById(1)).thenReturn(null);

        Response response = resource.update(UUID.randomUUID(), 1, new AspiranteOpcione());

        assertEquals(404, response.getStatus());
    }

    @Test
    void update_aspiranteNotFound() {
        Integer id = 1;
        UUID idAspirante = UUID.randomUUID();

        when(opcioneDAO.findById(id)).thenReturn(new AspiranteOpcione());
        when(aspiranteDAO.findById(idAspirante)).thenReturn(null);

        Response response = resource.update(idAspirante, id, new AspiranteOpcione());

        assertEquals(404, response.getStatus());
    }

    @Test
    void update_exception() {
        when(opcioneDAO.findById(1)).thenThrow(new RuntimeException("boom"));

        Response response = resource.update(UUID.randomUUID(), 1, new AspiranteOpcione());

        assertEquals(500, response.getStatus());
    }
}