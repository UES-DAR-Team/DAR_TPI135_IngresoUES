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

class JornadaAulaAspiranteResourceTest {

    private JornadaAulaAspiranteResource resource;
    private JornadaAulaAspiranteDAO jaaDAO;
    private JornadaAulaDAO jaDAO;
    private AspirantePruebaDAO apDAO;

    @BeforeEach
    void setUp() {
        resource = new JornadaAulaAspiranteResource();
        jaaDAO = mock(JornadaAulaAspiranteDAO.class);
        jaDAO = mock(JornadaAulaDAO.class);
        apDAO = mock(AspirantePruebaDAO.class);

        resource.jaaDAO = jaaDAO;
        resource.jornadaAulaDAO = jaDAO;
        resource.aspirantePruebaDAO = apDAO;
    }

    @Test
    void findById_success() {
        when(jaaDAO.findById(1)).thenReturn(new JornadaAulaAspirante());

        Response r = resource.findById(1);

        assertEquals(200, r.getStatus());
    }

    @Test
    void findById_notFound() {
        when(jaaDAO.findById(1)).thenReturn(null);

        Response r = resource.findById(1);

        assertEquals(404, r.getStatus());
    }

    @Test
    void findById_null() {
        Response r = resource.findById(null);

        assertEquals(422, r.getStatus());
    }

    @Test
    void findById_exception() {
        when(jaaDAO.findById(1)).thenThrow(new RuntimeException());

        Response r = resource.findById(1);

        assertEquals(500, r.getStatus());
    }

    @Test
    void create_success() {
        UUID idJornadaAula = UUID.randomUUID();
        Integer idAspirantePrueba = 1;

        JornadaAula ja = new JornadaAula();
        ja.setId(idJornadaAula);

        AspirantePrueba ap = new AspirantePrueba();
        ap.setId(idAspirantePrueba);

        JornadaAulaAspirante entity = new JornadaAulaAspirante();

        when(jaDAO.findById(idJornadaAula)).thenReturn(ja);
        when(apDAO.findById(idAspirantePrueba)).thenReturn(ap);

        doAnswer(inv -> {
            entity.setId(1);
            return null;
        }).when(jaaDAO).create(entity);

        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getAbsolutePathBuilder())
                .thenReturn(UriBuilder.fromUri("http://localhost"));

        Response r = resource.create(idJornadaAula, idAspirantePrueba, entity, uriInfo);

        assertEquals(201, r.getStatus());
    }

    @Test
    void create_invalidParams() {
        Response r = resource.create(null, null, null, mock(UriInfo.class));

        assertEquals(422, r.getStatus());
    }

    @Test
    void create_entityWithId() {
        JornadaAulaAspirante entity = new JornadaAulaAspirante();
        entity.setId(1);

        Response r = resource.create(UUID.randomUUID(), 1, entity, mock(UriInfo.class));

        assertEquals(422, r.getStatus());
    }

    @Test
    void create_notFound() {
        when(jaDAO.findById(any())).thenReturn(null);

        Response r = resource.create(UUID.randomUUID(), 1, new JornadaAulaAspirante(), mock(UriInfo.class));

        assertEquals(404, r.getStatus());
    }

    @Test
    void create_exception() {
        when(jaDAO.findById(any())).thenThrow(new RuntimeException());

        Response r = resource.create(UUID.randomUUID(), 1, new JornadaAulaAspirante(), mock(UriInfo.class));

        assertEquals(500, r.getStatus());
    }

    @Test
    void update_success() {
        UUID idJornadaAula = UUID.randomUUID();
        Integer idAspirantePrueba = 1;
        Integer id = 1;

        when(jaaDAO.findById(id)).thenReturn(new JornadaAulaAspirante());
        when(jaDAO.findById(idJornadaAula)).thenReturn(new JornadaAula());
        when(apDAO.findById(idAspirantePrueba)).thenReturn(new AspirantePrueba());
        when(jaaDAO.update(any())).thenReturn(new JornadaAulaAspirante());

        Response r = resource.update(id, idJornadaAula, idAspirantePrueba, new JornadaAulaAspirante());

        assertEquals(200, r.getStatus());
    }

    @Test
    void update_invalid() {
        Response r = resource.update(null, null, null, null);

        assertEquals(422, r.getStatus());
    }

    @Test
    void update_notFound() {
        when(jaaDAO.findById(1)).thenReturn(null);

        Response r = resource.update(1, UUID.randomUUID(), 1, new JornadaAulaAspirante());

        assertEquals(404, r.getStatus());
    }

    @Test
    void update_relationsNotFound() {
        UUID idJornadaAula = UUID.randomUUID();

        when(jaaDAO.findById(1)).thenReturn(new JornadaAulaAspirante());
        when(jaDAO.findById(idJornadaAula)).thenReturn(null);

        Response r = resource.update(1, idJornadaAula, 1, new JornadaAulaAspirante());

        assertEquals(404, r.getStatus());
    }

    @Test
    void update_exception() {
        when(jaaDAO.findById(1)).thenThrow(new RuntimeException());

        Response r = resource.update(1, UUID.randomUUID(), 1, new JornadaAulaAspirante());

        assertEquals(500, r.getStatus());
    }
}