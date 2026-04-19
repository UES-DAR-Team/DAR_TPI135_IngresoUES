package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.*;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JornadaAulaAspiranteResourceTest {

    private JornadaAulaAspiranteResource resource;

    private JornadaAulaAspiranteDAO jaaDAO;
    private JornadaAulaDAO jornadaAulaDAO;
    private AspirantePruebaDAO aspirantePruebaDAO;

    private UriInfo uriInfo;

    @BeforeEach
    void setUp() {
        resource = new JornadaAulaAspiranteResource();

        jaaDAO = mock(JornadaAulaAspiranteDAO.class);
        jornadaAulaDAO = mock(JornadaAulaDAO.class);
        aspirantePruebaDAO = mock(AspirantePruebaDAO.class);
        uriInfo = mock(UriInfo.class);

        resource.jaaDAO = jaaDAO;
        resource.jornadaAulaDAO = jornadaAulaDAO;
        resource.aspirantePruebaDAO = aspirantePruebaDAO;
    }

    @Test
    void findById_ok() {
        Integer id = 1;
        JornadaAulaAspirante entity = new JornadaAulaAspirante();

        when(jaaDAO.findById(id)).thenReturn(entity);

        Response r = resource.findById(id);

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
    void create_ok() {
        Integer idJornadaAula = 1;
        Integer idAspirantePrueba = 1;

        JornadaAula ja = new JornadaAula();
        AspirantePrueba ap = new AspirantePrueba();
        JornadaAulaAspirante entity = new JornadaAulaAspirante();

        when(jornadaAulaDAO.findById(idJornadaAula)).thenReturn(ja);
        when(aspirantePruebaDAO.findById(idAspirantePrueba)).thenReturn(ap);

        UriBuilder builder = UriBuilder.fromUri("http://localhost/");
        when(uriInfo.getAbsolutePathBuilder()).thenReturn(builder);

        doAnswer(inv -> {
            entity.setId(1);
            return null;
        }).when(jaaDAO).create(entity);

        Response r = resource.create(idJornadaAula, idAspirantePrueba, entity, uriInfo);

        assertEquals(201, r.getStatus());
    }

    @Test
    void create_paramInvalid() {
        Response r = resource.create(null, null, null, uriInfo);

        assertEquals(422, r.getStatus());
    }

    @Test
    void create_notFound() {
        Integer idJornadaAula = 1;

        when(jornadaAulaDAO.findById(idJornadaAula)).thenReturn(null);

        Response r = resource.create(idJornadaAula, 1, new JornadaAulaAspirante(), uriInfo);

        assertEquals(404, r.getStatus());
    }

    @Test
    void create_exception() {
        Integer idJornadaAula = 1;

        when(jornadaAulaDAO.findById(idJornadaAula)).thenThrow(new RuntimeException());

        Response r = resource.create(idJornadaAula, 1, new JornadaAulaAspirante(), uriInfo);

        assertEquals(500, r.getStatus());
    }


    @Test
    void update_ok() {
        Integer id = 1;
        Integer idJornadaAula = 1;
        Integer idAspirantePrueba = 1;

        JornadaAulaAspirante existing = new JornadaAulaAspirante();
        JornadaAula ja = new JornadaAula();
        AspirantePrueba ap = new AspirantePrueba();
        JornadaAulaAspirante entity = new JornadaAulaAspirante();

        when(jaaDAO.findById(id)).thenReturn(existing);
        when(jornadaAulaDAO.findById(idJornadaAula)).thenReturn(ja);
        when(aspirantePruebaDAO.findById(idAspirantePrueba)).thenReturn(ap);
        when(jaaDAO.update(entity)).thenReturn(entity);

        Response r = resource.update(id, idJornadaAula, idAspirantePrueba, entity);

        assertEquals(200, r.getStatus());
    }

    @Test
    void update_paramInvalid() {
        Response r = resource.update(null, null, null, null);

        assertEquals(422, r.getStatus());
    }

    @Test
    void update_notFound() {
        when(jaaDAO.findById(1)).thenReturn(null);

        Response r = resource.update(1, 1, 1, new JornadaAulaAspirante());

        assertEquals(404, r.getStatus());
    }

    @Test
    void update_fkNotFound() {
        when(jaaDAO.findById(1)).thenReturn(new JornadaAulaAspirante());
        when(jornadaAulaDAO.findById(any())).thenReturn(null);

        Response r = resource.update(1, 1, 1, new JornadaAulaAspirante());

        assertEquals(404, r.getStatus());
    }

    @Test
    void update_exception() {
        when(jaaDAO.findById(1)).thenThrow(new RuntimeException());

        Response r = resource.update(1, 1, 1, new JornadaAulaAspirante());

        assertEquals(500, r.getStatus());
    }
}