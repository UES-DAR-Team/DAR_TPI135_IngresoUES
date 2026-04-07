package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.*;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JornadaAulaAspiranteResultadoResourceTest {

    private JornadaAulaAspiranteResultadoResource resource;
    private JornadaAulaAspiranteResultadoDAO resultadoDAO;
    private JornadaAulaAspiranteDAO jaaDAO;

    @BeforeEach
    void setUp() {
        resource = new JornadaAulaAspiranteResultadoResource();
        resultadoDAO = mock(JornadaAulaAspiranteResultadoDAO.class);
        jaaDAO = mock(JornadaAulaAspiranteDAO.class);

        resource.resultadoDAO = resultadoDAO;
        resource.jornadaAulaAspiranteDAO = jaaDAO;
    }

    // =========================
    // FIND BY ID
    // =========================

    @Test
    void findById_success() {
        when(resultadoDAO.findById(1)).thenReturn(new JornadaAulaAspiranteResultado());

        Response r = resource.findById(1);

        assertEquals(200, r.getStatus());
    }

    @Test
    void findById_notFound() {
        when(resultadoDAO.findById(1)).thenReturn(null);

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
        when(resultadoDAO.findById(1)).thenThrow(new RuntimeException());

        Response r = resource.findById(1);

        assertEquals(500, r.getStatus());
    }

    // =========================
    // CREATE
    // =========================

    @Test
    void create_success() {
        Integer idJAA = 1;

        JornadaAulaAspirante jaa = new JornadaAulaAspirante();
        jaa.setId(idJAA);

        JornadaAulaAspiranteResultado entity = new JornadaAulaAspiranteResultado();

        when(jaaDAO.findById(idJAA)).thenReturn(jaa);

        doAnswer(inv -> {
            entity.setId(1);
            return null;
        }).when(resultadoDAO).create(entity);

        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getAbsolutePathBuilder())
                .thenReturn(UriBuilder.fromUri("http://localhost"));

        Response r = resource.create(idJAA, entity, uriInfo);

        assertEquals(201, r.getStatus());
    }

    @Test
    void create_invalidParams() {
        Response r = resource.create(null, null, mock(UriInfo.class));

        assertEquals(422, r.getStatus());
    }

    @Test
    void create_entityWithId() {
        JornadaAulaAspiranteResultado entity = new JornadaAulaAspiranteResultado();
        entity.setId(1);

        Response r = resource.create(1, entity, mock(UriInfo.class));

        assertEquals(422, r.getStatus());
    }

    @Test
    void create_notFound() {
        when(jaaDAO.findById(1)).thenReturn(null);

        Response r = resource.create(1, new JornadaAulaAspiranteResultado(), mock(UriInfo.class));

        assertEquals(404, r.getStatus());
    }

    @Test
    void create_exception() {
        when(jaaDAO.findById(1)).thenThrow(new RuntimeException());

        Response r = resource.create(1, new JornadaAulaAspiranteResultado(), mock(UriInfo.class));

        assertEquals(500, r.getStatus());
    }

    // =========================
    // UPDATE
    // =========================

    @Test
    void update_success() {
        Integer id = 1;
        Integer idJAA = 1;

        when(resultadoDAO.findById(id)).thenReturn(new JornadaAulaAspiranteResultado());
        when(jaaDAO.findById(idJAA)).thenReturn(new JornadaAulaAspirante());
        when(resultadoDAO.update(any())).thenReturn(new JornadaAulaAspiranteResultado());

        Response r = resource.update(id, idJAA, new JornadaAulaAspiranteResultado());

        assertEquals(200, r.getStatus());
    }

    @Test
    void update_invalid() {
        Response r = resource.update(null, null, null);

        assertEquals(422, r.getStatus());
    }

    @Test
    void update_notFound() {
        when(resultadoDAO.findById(1)).thenReturn(null);

        Response r = resource.update(1, 1, new JornadaAulaAspiranteResultado());

        assertEquals(404, r.getStatus());
    }

    @Test
    void update_relationNotFound() {
        when(resultadoDAO.findById(1)).thenReturn(new JornadaAulaAspiranteResultado());
        when(jaaDAO.findById(1)).thenReturn(null);

        Response r = resource.update(1, 1, new JornadaAulaAspiranteResultado());

        assertEquals(404, r.getStatus());
    }

    @Test
    void update_exception() {
        when(resultadoDAO.findById(1)).thenThrow(new RuntimeException());

        Response r = resource.update(1, 1, new JornadaAulaAspiranteResultado());

        assertEquals(500, r.getStatus());
    }
}