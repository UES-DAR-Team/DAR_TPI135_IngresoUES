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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.*;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.*;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

    @Nested
    class FindRange {

        @Test
        void retorna200_cuandoParametrosSonValidos() {
            UUID idJornada = UUID.randomUUID();
            UUID idAula = UUID.randomUUID();

            when(jornadaDAO.findById(idJornada)).thenReturn(new Jornada());
            when(aulaDAO.findById(idAula)).thenReturn(new Aula());
            when(jaDAO.countByJornada(idJornada)).thenReturn(1L);
            when(jaDAO.findByJornada(idJornada, 0, 10)).thenReturn(Collections.emptyList());

            Response r = resource.findRange(idJornada, idAula, 0, 10);

            assertEquals(200, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJornadaEsNulo() {
            Response r = resource.findRange(null, UUID.randomUUID(), 0, 10);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdAulaEsNulo() {
            Response r = resource.findRange(UUID.randomUUID(), null, 0, 10);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoFirstEsNegativo() {
            Response r = resource.findRange(UUID.randomUUID(), UUID.randomUUID(), -1, 10);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoMaxEsCero() {
            Response r = resource.findRange(UUID.randomUUID(), UUID.randomUUID(), 0, 0);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoMaxExcedeLimite() {
            Response r = resource.findRange(UUID.randomUUID(), UUID.randomUUID(), 0, 101);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna404_cuandoJornadaNoExiste() {
            UUID idJornada = UUID.randomUUID();
            when(jornadaDAO.findById(idJornada)).thenReturn(null);

            Response r = resource.findRange(idJornada, UUID.randomUUID(), 0, 10);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoAulaNoExiste() {
            UUID idJornada = UUID.randomUUID();
            UUID idAula = UUID.randomUUID();

            when(jornadaDAO.findById(idJornada)).thenReturn(new Jornada());
            when(aulaDAO.findById(idAula)).thenReturn(null);

            Response r = resource.findRange(idJornada, idAula, 0, 10);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            UUID idJornada = UUID.randomUUID();
            when(jornadaDAO.findById(idJornada)).thenThrow(new RuntimeException());

            Response r = resource.findRange(idJornada, UUID.randomUUID(), 0, 10);

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class FindById {

        private JornadaAula buildExisting(UUID idJornada, UUID idAula) {
            Jornada jornada = new Jornada();
            jornada.setId(idJornada);
            Aula aula = new Aula();
            aula.setId(idAula);
            JornadaAula ja = new JornadaAula();
            ja.setIdJornada(jornada);
            ja.setIdAula(aula);
            return ja;
        }

        @Test
        void retorna200_cuandoExiste() {
            UUID idJornada = UUID.randomUUID();
            UUID idAula = UUID.randomUUID();
            JornadaAula ja = buildExisting(idJornada, idAula);

            when(jaDAO.findById(1)).thenReturn(ja);

            Response r = resource.findById(idJornada, idAula, 1);

            assertEquals(200, r.getStatus());
        }

        @Test
        void retorna404_cuandoNoExiste() {
            when(jaDAO.findById(1)).thenReturn(null);

            Response r = resource.findById(UUID.randomUUID(), UUID.randomUUID(), 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoJornadaMismatch() {
            UUID idAula = UUID.randomUUID();
            JornadaAula ja = buildExisting(UUID.randomUUID(), idAula); // jornada diferente

            when(jaDAO.findById(1)).thenReturn(ja);

            Response r = resource.findById(UUID.randomUUID(), idAula, 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoAulaMismatch() {
            UUID idJornada = UUID.randomUUID();
            JornadaAula ja = buildExisting(idJornada, UUID.randomUUID()); // aula diferente

            when(jaDAO.findById(1)).thenReturn(ja);

            Response r = resource.findById(idJornada, UUID.randomUUID(), 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJornadaEsNulo() {
            Response r = resource.findById(null, UUID.randomUUID(), 1);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdAulaEsNulo() {
            Response r = resource.findById(UUID.randomUUID(), null, 1);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response r = resource.findById(UUID.randomUUID(), UUID.randomUUID(), null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(jaDAO.findById(1)).thenThrow(new RuntimeException());

            Response r = resource.findById(UUID.randomUUID(), UUID.randomUUID(), 1);

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class Create {

        @Test
        void retorna201_cuandoCreacionExitosa() {
            UUID idJornada = UUID.randomUUID();
            UUID idAula = UUID.randomUUID();
            JornadaAula entity = new JornadaAula();

            when(jornadaDAO.findById(idJornada)).thenReturn(new Jornada());
            when(aulaDAO.findById(idAula)).thenReturn(new Aula());

            UriInfo uriInfo = mock(UriInfo.class);
            when(uriInfo.getAbsolutePathBuilder())
                    .thenReturn(UriBuilder.fromUri("http://localhost"));

            Response r = resource.create(idJornada, idAula, entity, uriInfo);

            assertEquals(201, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJornadaEsNulo() {
            Response r = resource.create(null, UUID.randomUUID(), new JornadaAula(), mock(UriInfo.class));
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdAulaEsNulo() {
            Response r = resource.create(UUID.randomUUID(), null, new JornadaAula(), mock(UriInfo.class));
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoEntityEsNula() {
            Response r = resource.create(UUID.randomUUID(), UUID.randomUUID(), null, mock(UriInfo.class));
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoEntityTieneId() {
            JornadaAula entity = new JornadaAula();
            entity.setId(1);

            Response r = resource.create(UUID.randomUUID(), UUID.randomUUID(), entity, mock(UriInfo.class));

            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna404_cuandoJornadaNoExiste() {
            when(jornadaDAO.findById(any())).thenReturn(null);

            Response r = resource.create(UUID.randomUUID(), UUID.randomUUID(), new JornadaAula(), mock(UriInfo.class));

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoAulaNoExiste() {
            UUID idJornada = UUID.randomUUID();
            UUID idAula = UUID.randomUUID();

            when(jornadaDAO.findById(idJornada)).thenReturn(new Jornada());
            when(aulaDAO.findById(idAula)).thenReturn(null);

            Response r = resource.create(idJornada, idAula, new JornadaAula(), mock(UriInfo.class));

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna409_cuandoHayDuplicateKey() {
            UUID idJornada = UUID.randomUUID();
            UUID idAula = UUID.randomUUID();
            JornadaAula entity = new JornadaAula();

            when(jornadaDAO.findById(idJornada)).thenReturn(new Jornada());
            when(aulaDAO.findById(idAula)).thenReturn(new Aula());

            UriInfo uriInfo = mock(UriInfo.class);
            when(uriInfo.getAbsolutePathBuilder())
                    .thenReturn(UriBuilder.fromUri("http://localhost"));

            RuntimeException cause = new RuntimeException("duplicate key value violates unique constraint");
            doThrow(new RuntimeException(cause)).when(jaDAO).create(entity);

            Response r = resource.create(idJornada, idAula, entity, uriInfo);

            assertEquals(409, r.getStatus());
        }

        @Test
        void retorna500_cuandoExcepcionSinCause() {
            UUID idJornada = UUID.randomUUID();
            UUID idAula = UUID.randomUUID();
            JornadaAula entity = new JornadaAula();

            when(jornadaDAO.findById(idJornada)).thenReturn(new Jornada());
            when(aulaDAO.findById(idAula)).thenReturn(new Aula());
            doThrow(new RuntimeException("error")).when(jaDAO).create(entity);

            Response r = resource.create(idJornada, idAula, entity, mock(UriInfo.class));

            assertEquals(500, r.getStatus());
        }

        @Test
        void retorna500_cuandoExcepcionSinMensaje() {
            UUID idJornada = UUID.randomUUID();
            UUID idAula = UUID.randomUUID();
            JornadaAula entity = new JornadaAula();

            when(jornadaDAO.findById(idJornada)).thenReturn(new Jornada());
            when(aulaDAO.findById(idAula)).thenReturn(new Aula());
            doThrow(new RuntimeException(new RuntimeException((String) null))).when(jaDAO).create(entity);

            Response r = resource.create(idJornada, idAula, entity, mock(UriInfo.class));

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class Update {

        private JornadaAula buildExisting(UUID idJornada, UUID idAula) {
            Jornada jornada = new Jornada();
            jornada.setId(idJornada);
            Aula aula = new Aula();
            aula.setId(idAula);
            JornadaAula ja = new JornadaAula();
            ja.setIdJornada(jornada);
            ja.setIdAula(aula);
            return ja;
        }

        @Test
        void retorna200_cuandoActualizacionExitosa() {
            UUID idJornada = UUID.randomUUID();
            UUID idAula = UUID.randomUUID();
            JornadaAula existing = buildExisting(idJornada, idAula);

            when(jaDAO.findById(1)).thenReturn(existing);
            when(jornadaDAO.findById(idJornada)).thenReturn(new Jornada());
            when(aulaDAO.findById(idAula)).thenReturn(new Aula());
            when(jaDAO.update(any())).thenReturn(new JornadaAula());

            Response r = resource.update(idJornada, idAula, 1, new JornadaAula());

            assertEquals(200, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJornadaEsNulo() {
            Response r = resource.update(null, UUID.randomUUID(), 1, new JornadaAula());
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdAulaEsNulo() {
            Response r = resource.update(UUID.randomUUID(), null, 1, new JornadaAula());
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response r = resource.update(UUID.randomUUID(), UUID.randomUUID(), null, new JornadaAula());
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoEntityEsNula() {
            Response r = resource.update(UUID.randomUUID(), UUID.randomUUID(), 1, null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna404_cuandoExistingEsNulo() {
            when(jaDAO.findById(1)).thenReturn(null);

            Response r = resource.update(UUID.randomUUID(), UUID.randomUUID(), 1, new JornadaAula());

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoJornadaMismatch() {
            UUID idAula = UUID.randomUUID();
            JornadaAula existing = buildExisting(UUID.randomUUID(), idAula);

            when(jaDAO.findById(1)).thenReturn(existing);

            Response r = resource.update(UUID.randomUUID(), idAula, 1, new JornadaAula());

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoAulaMismatch() {
            UUID idJornada = UUID.randomUUID();
            JornadaAula existing = buildExisting(idJornada, UUID.randomUUID());

            when(jaDAO.findById(1)).thenReturn(existing);

            Response r = resource.update(idJornada, UUID.randomUUID(), 1, new JornadaAula());

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoJornadaNoExisteEnBD() {
            UUID idJornada = UUID.randomUUID();
            UUID idAula = UUID.randomUUID();
            JornadaAula existing = buildExisting(idJornada, idAula);

            when(jaDAO.findById(1)).thenReturn(existing);
            when(jornadaDAO.findById(idJornada)).thenReturn(null);

            Response r = resource.update(idJornada, idAula, 1, new JornadaAula());

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoAulaNoExisteEnBD() {
            UUID idJornada = UUID.randomUUID();
            UUID idAula = UUID.randomUUID();
            JornadaAula existing = buildExisting(idJornada, idAula);

            when(jaDAO.findById(1)).thenReturn(existing);
            when(jornadaDAO.findById(idJornada)).thenReturn(new Jornada());
            when(aulaDAO.findById(idAula)).thenReturn(null);

            Response r = resource.update(idJornada, idAula, 1, new JornadaAula());

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(jaDAO.findById(1)).thenThrow(new RuntimeException());

            Response r = resource.update(UUID.randomUUID(), UUID.randomUUID(), 1, new JornadaAula());

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class Delete {

        private JornadaAula buildExisting(UUID idJornada, UUID idAula) {
            Jornada jornada = new Jornada();
            jornada.setId(idJornada);
            Aula aula = new Aula();
            aula.setId(idAula);
            JornadaAula ja = new JornadaAula();
            ja.setIdJornada(jornada);
            ja.setIdAula(aula);
            return ja;
        }

        @Test
        void retorna204_cuandoEliminacionExitosa() {
            UUID idJornada = UUID.randomUUID();
            UUID idAula = UUID.randomUUID();
            JornadaAula existing = buildExisting(idJornada, idAula);

            when(jaDAO.findById(1)).thenReturn(existing);

            Response r = resource.delete(idJornada, idAula, 1);

            assertEquals(204, r.getStatus());
            verify(jaDAO).delete(existing);
        }

        @Test
        void retorna404_cuandoNoExiste() {
            when(jaDAO.findById(1)).thenReturn(null);

            Response r = resource.delete(UUID.randomUUID(), UUID.randomUUID(), 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoJornadaMismatch() {
            UUID idAula = UUID.randomUUID();
            JornadaAula existing = buildExisting(UUID.randomUUID(), idAula);

            when(jaDAO.findById(1)).thenReturn(existing);

            Response r = resource.delete(UUID.randomUUID(), idAula, 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoAulaMismatch() {
            UUID idJornada = UUID.randomUUID();
            JornadaAula existing = buildExisting(idJornada, UUID.randomUUID());

            when(jaDAO.findById(1)).thenReturn(existing);

            Response r = resource.delete(idJornada, UUID.randomUUID(), 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJornadaEsNulo() {
            Response r = resource.delete(null, UUID.randomUUID(), 1);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdAulaEsNulo() {
            Response r = resource.delete(UUID.randomUUID(), null, 1);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response r = resource.delete(UUID.randomUUID(), UUID.randomUUID(), null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(jaDAO.findById(1)).thenThrow(new RuntimeException());

            Response r = resource.delete(UUID.randomUUID(), UUID.randomUUID(), 1);

            assertEquals(500, r.getStatus());
        }
    }
}