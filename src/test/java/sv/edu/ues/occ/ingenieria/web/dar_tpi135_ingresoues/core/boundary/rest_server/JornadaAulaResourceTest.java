package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.*;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.*;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    /** Construye un JornadaAula con idJornada dado para verificar match. */
    private JornadaAula buildExisting(UUID idJornada) {
        Jornada jornada = new Jornada();
        jornada.setId(idJornada);
        JornadaAula ja = new JornadaAula();
        ja.setIdJornada(jornada);
        return ja;
    }

    /** Construye un JornadaAula con idAula seteado en el body para create. */
    private JornadaAula buildEntityWithAula(UUID idAula) {
        Aula aula = new Aula();
        aula.setId(idAula);
        JornadaAula entity = new JornadaAula();
        entity.setIdAula(aula);
        return entity;
    }

   @Nested
    class FindRange {

        @Test
        void retorna200_cuandoParametrosSonValidos() {
            UUID idJornada = UUID.randomUUID();
            when(jornadaDAO.findById(idJornada)).thenReturn(new Jornada());
            when(jaDAO.countByJornada(idJornada)).thenReturn(1L);
            when(jaDAO.findByJornada(idJornada, 0, 10)).thenReturn(Collections.emptyList());

            Response r = resource.findRange(idJornada, 0, 10);

            assertEquals(200, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJornadaEsNulo() {
            Response r = resource.findRange(null, 0, 10);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoFirstEsNegativo() {
            Response r = resource.findRange(UUID.randomUUID(), -1, 10);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoMaxEsCero() {
            Response r = resource.findRange(UUID.randomUUID(), 0, 0);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoMaxExcedeLimite() {
            Response r = resource.findRange(UUID.randomUUID(), 0, 101);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna404_cuandoJornadaNoExiste() {
            UUID idJornada = UUID.randomUUID();
            when(jornadaDAO.findById(idJornada)).thenReturn(null);

            Response r = resource.findRange(idJornada, 0, 10);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            UUID idJornada = UUID.randomUUID();
            when(jornadaDAO.findById(idJornada)).thenThrow(new RuntimeException());

            Response r = resource.findRange(idJornada, 0, 10);

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class FindById {

        @Test
        void retorna200_cuandoExiste() {
            UUID idJornada = UUID.randomUUID();
            JornadaAula ja = buildExisting(idJornada);
            when(jaDAO.findById(1)).thenReturn(ja);

            Response r = resource.findById(idJornada, 1);

            assertEquals(200, r.getStatus());
        }

        @Test
        void retorna404_cuandoNoExiste() {
            when(jaDAO.findById(1)).thenReturn(null);

            Response r = resource.findById(UUID.randomUUID(), 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoJornadaMismatch() {
            JornadaAula ja = buildExisting(UUID.randomUUID()); // jornada diferente
            when(jaDAO.findById(1)).thenReturn(ja);

            Response r = resource.findById(UUID.randomUUID(), 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJornadaEsNulo() {
            Response r = resource.findById(null, 1);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdAulaEsNulo() {
            Response r = resource.findById(UUID.randomUUID(), null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(jaDAO.findById(1)).thenThrow(new RuntimeException());

            Response r = resource.findById(UUID.randomUUID(), 1);

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class Create {

        @Test
        void retorna201_cuandoCreacionExitosa() {
            UUID idJornada = UUID.randomUUID();
            UUID idAula = UUID.randomUUID();
            JornadaAula entity = buildEntityWithAula(idAula);

            when(jornadaDAO.findById(idJornada)).thenReturn(new Jornada());
            when(aulaDAO.findById(idAula)).thenReturn(new Aula());

            UriInfo uriInfo = mock(UriInfo.class);

            when(uriInfo.getAbsolutePathBuilder())
                    .thenReturn(UriBuilder.fromUri("http://localhost"));

            Response r = resource.create(idJornada, entity, uriInfo);

            assertEquals(201, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJornadaEsNulo() {
            Response r = resource.create(null, new JornadaAula(), mock(UriInfo.class));
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoEntityEsNula() {
            Response r = resource.create(UUID.randomUUID(), null, mock(UriInfo.class));
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoEntityTieneId() {
            JornadaAula entity = buildEntityWithAula(UUID.randomUUID());
            entity.setId(1);

            Response r = resource.create(UUID.randomUUID(), entity, mock(UriInfo.class));

            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdAulaEsNulo() {
            // entity sin idAula seteado
            Response r = resource.create(UUID.randomUUID(), new JornadaAula(), mock(UriInfo.class));
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna404_cuandoJornadaNoExiste() {
            UUID idJornada = UUID.randomUUID();
            JornadaAula entity = buildEntityWithAula(UUID.randomUUID());
            when(jornadaDAO.findById(idJornada)).thenReturn(null);

            Response r = resource.create(idJornada, entity, mock(UriInfo.class));

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoAulaNoExiste() {
            UUID idJornada = UUID.randomUUID();
            UUID idAula = UUID.randomUUID();
            JornadaAula entity = buildEntityWithAula(idAula);

            when(jornadaDAO.findById(idJornada)).thenReturn(new Jornada());
            when(aulaDAO.findById(idAula)).thenReturn(null);

            Response r = resource.create(idJornada, entity, mock(UriInfo.class));

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna409_cuandoHayDuplicateKey() {
            UUID idJornada = UUID.randomUUID();
            UUID idAula = UUID.randomUUID();
            JornadaAula entity = buildEntityWithAula(idAula);

            when(jornadaDAO.findById(idJornada)).thenReturn(new Jornada());
            when(aulaDAO.findById(idAula)).thenReturn(new Aula());


            RuntimeException cause = new RuntimeException("duplicate key value violates unique constraint");
            doThrow(new RuntimeException(cause)).when(jaDAO).create(entity);

            Response r = resource.create(idJornada, entity, mock(UriInfo.class));

            assertEquals(409, r.getStatus());
        }

        @Test
        void retorna500_cuandoExcepcionSinCause() {
            UUID idJornada = UUID.randomUUID();
            UUID idAula = UUID.randomUUID();
            JornadaAula entity = buildEntityWithAula(idAula);

            when(jornadaDAO.findById(idJornada)).thenReturn(new Jornada());
            when(aulaDAO.findById(idAula)).thenReturn(new Aula());
            doThrow(new RuntimeException("error")).when(jaDAO).create(entity);

            Response r = resource.create(idJornada, entity, mock(UriInfo.class));

            assertEquals(500, r.getStatus());
        }

        @Test
        void retorna500_cuandoExcepcionSinMensaje() {
            UUID idJornada = UUID.randomUUID();
            UUID idAula = UUID.randomUUID();
            JornadaAula entity = buildEntityWithAula(idAula);

            when(jornadaDAO.findById(idJornada)).thenReturn(new Jornada());
            when(aulaDAO.findById(idAula)).thenReturn(new Aula());
            doThrow(new RuntimeException(new RuntimeException((String) null))).when(jaDAO).create(entity);

            Response r = resource.create(idJornada, entity, mock(UriInfo.class));

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class Update {

        @Test
        void retorna200_cuandoActualizacionExitosa() {
            UUID idJornada = UUID.randomUUID();
            JornadaAula existing = buildExisting(idJornada);
            when(jaDAO.findById(1)).thenReturn(existing);
            when(jaDAO.update(existing)).thenReturn(existing);

            Response r = resource.update(idJornada, 1, new JornadaAula());

            assertEquals(200, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJornadaEsNulo() {
            Response r = resource.update(null, 1, new JornadaAula());
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdAulaEsNulo() {
            Response r = resource.update(UUID.randomUUID(), null, new JornadaAula());
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoEntityEsNula() {
            Response r = resource.update(UUID.randomUUID(), 1, null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna404_cuandoExistingEsNulo() {
            when(jaDAO.findById(1)).thenReturn(null);

            Response r = resource.update(UUID.randomUUID(), 1, new JornadaAula());

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoJornadaMismatch() {
            JornadaAula existing = buildExisting(UUID.randomUUID()); // jornada diferente
            when(jaDAO.findById(1)).thenReturn(existing);

            Response r = resource.update(UUID.randomUUID(), 1, new JornadaAula());

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(jaDAO.findById(1)).thenThrow(new RuntimeException());

            Response r = resource.update(UUID.randomUUID(), 1, new JornadaAula());

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class Delete {

        @Test
        void retorna204_cuandoEliminacionExitosa() {
            UUID idJornada = UUID.randomUUID();
            JornadaAula existing = buildExisting(idJornada);
            when(jaDAO.findById(1)).thenReturn(existing);

            Response r = resource.delete(idJornada, 1);

            assertEquals(204, r.getStatus());
            verify(jaDAO).delete(existing);
        }

        @Test
        void retorna404_cuandoNoExiste() {
            when(jaDAO.findById(1)).thenReturn(null);

            Response r = resource.delete(UUID.randomUUID(), 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoJornadaMismatch() {
            JornadaAula existing = buildExisting(UUID.randomUUID()); // jornada diferente
            when(jaDAO.findById(1)).thenReturn(existing);

            Response r = resource.delete(UUID.randomUUID(), 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJornadaEsNulo() {
            Response r = resource.delete(null, 1);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdAulaEsNulo() {
            Response r = resource.delete(UUID.randomUUID(), null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(jaDAO.findById(1)).thenThrow(new RuntimeException());

            Response r = resource.delete(UUID.randomUUID(), 1);

            assertEquals(500, r.getStatus());
        }
    }
}