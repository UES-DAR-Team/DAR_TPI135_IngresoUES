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

class JornadaAulaResourceTest {

    private JornadaAulaResource resource;
    private JornadaAulaDAO jaDAO;
    private JornadaDAO jornadaDAO;
    private AulaDAO aulaDAO;

    @BeforeEach
    void setUp() {
        resource = new JornadaAulaResource();
        jaDAO = mock(JornadaAulaDAO.class);
        jornadaDAO = mock(JornadaDAO.class);
        aulaDAO = mock(AulaDAO.class);

        resource.jornadaAulaDAO = jaDAO;
        resource.jornadaDAO = jornadaDAO;
        resource.aulaDAO = aulaDAO;
    }

    @Test
    void findById_success() {
        UUID id = UUID.randomUUID();

        when(jaDAO.findById(id)).thenReturn(new JornadaAula());

        Response r = resource.findById(id);

        assertEquals(200, r.getStatus());
    }

    @Test
    void findById_notFound() {
        UUID id = UUID.randomUUID();

        when(jaDAO.findById(id)).thenReturn(null);

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

        when(jaDAO.findById(id)).thenThrow(new RuntimeException());

        Response r = resource.findById(id);

        assertEquals(500, r.getStatus());
    }

    @Test
    void create_success() {
        UUID idJornada = UUID.randomUUID();
        UUID idAula = UUID.randomUUID();

        Jornada jornada = new Jornada();
        jornada.setId(idJornada);

        Aula aula = new Aula();
        aula.setId(idAula);

        JornadaAula entity = new JornadaAula();

        when(jornadaDAO.findById(idJornada)).thenReturn(jornada);
        when(aulaDAO.findById(idAula)).thenReturn(aula);

        doAnswer(inv -> {
            entity.setId(UUID.randomUUID());
            return null;
        }).when(jaDAO).create(entity);

        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getAbsolutePathBuilder())
                .thenReturn(UriBuilder.fromUri("http://localhost"));

        Response r = resource.create(idJornada, idAula, entity, uriInfo);

        assertEquals(201, r.getStatus());
    }

    @Test
    void create_invalidParams() {
        Response r = resource.create(null, null, null, mock(UriInfo.class));

        assertEquals(422, r.getStatus());
    }

    @Test
    void create_entityWithId() {
        JornadaAula entity = new JornadaAula();
        entity.setId(UUID.randomUUID());

        Response r = resource.create(UUID.randomUUID(), UUID.randomUUID(), entity, mock(UriInfo.class));

        assertEquals(422, r.getStatus());
    }

    @Test
    void create_jornadaNotFound() {
        when(jornadaDAO.findById(any())).thenReturn(null);

        Response r = resource.create(UUID.randomUUID(), UUID.randomUUID(), new JornadaAula(), mock(UriInfo.class));

        assertEquals(404, r.getStatus());
    }

    @Test
    void create_aulaNotFound() {
        UUID idJornada = UUID.randomUUID();
        UUID idAula = UUID.randomUUID();

        when(jornadaDAO.findById(idJornada)).thenReturn(new Jornada());
        when(aulaDAO.findById(idAula)).thenReturn(null);

        Response r = resource.create(idJornada, idAula, new JornadaAula(), mock(UriInfo.class));

        assertEquals(404, r.getStatus());
    }

    @Test
    void create_exception() {
        when(jornadaDAO.findById(any())).thenThrow(new RuntimeException());

        Response r = resource.create(UUID.randomUUID(), UUID.randomUUID(), new JornadaAula(), mock(UriInfo.class));

        assertEquals(500, r.getStatus());
    }

    @Test
    void update_success() {
        UUID id = UUID.randomUUID();
        UUID idJornada = UUID.randomUUID();
        UUID idAula = UUID.randomUUID();

        when(jaDAO.findById(id)).thenReturn(new JornadaAula());
        when(jornadaDAO.findById(idJornada)).thenReturn(new Jornada());
        when(aulaDAO.findById(idAula)).thenReturn(new Aula());
        when(jaDAO.update(any())).thenReturn(new JornadaAula());

        Response r = resource.update(id, idJornada, idAula, new JornadaAula());

        assertEquals(200, r.getStatus());
    }

    @Test
    void update_invalid() {
        Response r = resource.update(null, null, null, null);

        assertEquals(422, r.getStatus());
    }

    @Test
    void update_notFound() {
        when(jaDAO.findById(any())).thenReturn(null);

        Response r = resource.update(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new JornadaAula());

        assertEquals(404, r.getStatus());
    }

    @Test
    void update_jornadaOrAulaNotFound() {
        UUID id = UUID.randomUUID();
        UUID idJornada = UUID.randomUUID();
        UUID idAula = UUID.randomUUID();

        when(jaDAO.findById(id)).thenReturn(new JornadaAula());
        when(jornadaDAO.findById(idJornada)).thenReturn(null);

        Response r = resource.update(id, idJornada, idAula, new JornadaAula());

        assertEquals(404, r.getStatus());
    }

    @Test
    void update_exception() {
        when(jaDAO.findById(any())).thenThrow(new RuntimeException());

        Response r = resource.update(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new JornadaAula());

        assertEquals(500, r.getStatus());
    }
}