package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.*;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AspirantePruebaResourceTest {

    private AspirantePruebaResource resource;
    private AspirantePruebaDAO apDAO;
    private AspiranteDAO aspiranteDAO;
    private PruebaDAO pruebaDAO;

    @BeforeEach
    void setUp() {
        resource = new AspirantePruebaResource();
        apDAO = mock(AspirantePruebaDAO.class);
        aspiranteDAO = mock(AspiranteDAO.class);
        pruebaDAO = mock(PruebaDAO.class);

        resource.aspirantePruebaDAO = apDAO;
        resource.aspiranteDAO = aspiranteDAO;
        resource.pruebaDAO = pruebaDAO;
    }

    @Test
    void findById_success() {
        UUID idAspirante = UUID.randomUUID();
        Integer idPrueba = 1;

        Aspirante aspirante = new Aspirante();
        aspirante.setId(idAspirante);

        AspirantePrueba ap = new AspirantePrueba();
        ap.setId(idPrueba);
        ap.setIdAspirante(aspirante);

        when(apDAO.findById(idPrueba)).thenReturn(ap);

        Response r = resource.findById(idAspirante, idPrueba);

        assertEquals(200, r.getStatus());
    }

    @Test
    void findById_notFound() {
        when(apDAO.findById(1)).thenReturn(null);

        Response r = resource.findById(UUID.randomUUID(), 1);

        assertEquals(404, r.getStatus());
    }

    @Test
    void findById_mismatch() {
        UUID idAspirante = UUID.randomUUID();

        Aspirante otro = new Aspirante();
        otro.setId(UUID.randomUUID());

        AspirantePrueba ap = new AspirantePrueba();
        ap.setIdAspirante(otro);

        when(apDAO.findById(1)).thenReturn(ap);

        Response r = resource.findById(idAspirante, 1);

        assertEquals(404, r.getStatus());
    }

    @Test
    void findById_invalid() {
        Response r = resource.findById(null, null);

        assertEquals(422, r.getStatus());
    }

    @Test
    void findById_exception() {
        when(apDAO.findById(1)).thenThrow(new RuntimeException());

        Response r = resource.findById(UUID.randomUUID(), 1);

        assertEquals(500, r.getStatus());
    }

    @Test
    void create_success() {
        UUID idAspirante = UUID.randomUUID();

        Aspirante aspirante = new Aspirante();
        aspirante.setId(idAspirante);

        UUID idPrueba = UUID.randomUUID();

        Prueba prueba = new Prueba();
        prueba.setId(idPrueba);

        AspirantePrueba entity = new AspirantePrueba();
        entity.setIdPrueba(prueba);

        when(aspiranteDAO.findById(idAspirante)).thenReturn(aspirante);
        when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);

        doAnswer(inv -> {
            entity.setId(1);
            return null;
        }).when(apDAO).create(entity);

        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getAbsolutePathBuilder())
                .thenReturn(UriBuilder.fromUri("http://localhost"));

        Response r = resource.create(idAspirante, entity, uriInfo);

        assertEquals(201, r.getStatus());
    }

    @Test
    void create_invalidParams() {
        Response r = resource.create(null, null, mock(UriInfo.class));

        assertEquals(422, r.getStatus());
    }

    @Test
    void create_entityWithId() {
        AspirantePrueba e = new AspirantePrueba();
        e.setId(1);

        Response r = resource.create(UUID.randomUUID(), e, mock(UriInfo.class));

        assertEquals(422, r.getStatus());
    }

    @Test
    void create_aspiranteNotFound() {
        when(aspiranteDAO.findById(any())).thenReturn(null);

        Response r = resource.create(UUID.randomUUID(), new AspirantePrueba(), mock(UriInfo.class));

        assertEquals(404, r.getStatus());
    }

    @Test
    void create_idPruebaNull() {
        AspirantePrueba e = new AspirantePrueba();

        when(aspiranteDAO.findById(any())).thenReturn(new Aspirante());

        Response r = resource.create(UUID.randomUUID(), e, mock(UriInfo.class));

        assertEquals(422, r.getStatus());
    }

    @Test
    void create_pruebaNotFound() {
        UUID idAspirante = UUID.randomUUID();

        Aspirante aspirante = new Aspirante();
        aspirante.setId(idAspirante);

        Prueba p = new Prueba();
        p.setId(UUID.randomUUID());

        AspirantePrueba e = new AspirantePrueba();
        e.setIdPrueba(p);

        when(aspiranteDAO.findById(idAspirante)).thenReturn(aspirante);
        when(pruebaDAO.findById(1)).thenReturn(null);

        Response r = resource.create(idAspirante, e, mock(UriInfo.class));

        assertEquals(404, r.getStatus());
    }

    @Test
    void create_exception() {
        when(aspiranteDAO.findById(any())).thenThrow(new RuntimeException());

        Response r = resource.create(UUID.randomUUID(), new AspirantePrueba(), mock(UriInfo.class));

        assertEquals(500, r.getStatus());
    }

    @Test
    void update_success() {
        UUID idAspirante = UUID.randomUUID();

        when(apDAO.findById(1)).thenReturn(new AspirantePrueba());
        when(aspiranteDAO.findById(idAspirante)).thenReturn(new Aspirante());
        when(apDAO.update(any())).thenReturn(new AspirantePrueba());

        Response r = resource.update(idAspirante, 1, new AspirantePrueba());

        assertEquals(200, r.getStatus());
    }

    @Test
    void update_invalid() {
        Response r = resource.update(null, null, null);

        assertEquals(422, r.getStatus());
    }

    @Test
    void update_notFound() {
        when(apDAO.findById(1)).thenReturn(null);

        Response r = resource.update(UUID.randomUUID(), 1, new AspirantePrueba());

        assertEquals(404, r.getStatus());
    }

    @Test
    void update_aspiranteNotFound() {
        when(apDAO.findById(1)).thenReturn(new AspirantePrueba());
        when(aspiranteDAO.findById(any())).thenReturn(null);

        Response r = resource.update(UUID.randomUUID(), 1, new AspirantePrueba());

        assertEquals(404, r.getStatus());
    }

    @Test
    void update_exception() {
        when(apDAO.findById(1)).thenThrow(new RuntimeException());

        Response r = resource.update(UUID.randomUUID(), 1, new AspirantePrueba());

        assertEquals(500, r.getStatus());
    }
}