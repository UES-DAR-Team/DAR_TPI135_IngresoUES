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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

    private JornadaAulaAspiranteResultado buildExisting(Integer idJaa) {
        JornadaAulaAspirante jaa = new JornadaAulaAspirante();
        jaa.setId(idJaa);
        JornadaAulaAspiranteResultado r = new JornadaAulaAspiranteResultado();
        r.setIdJornadaAulaAspirante(jaa);
        return r;
    }

    @Nested
    class FindRange {

        @Test
        void retorna200_cuandoParametrosSonValidos() {
            when(jaaDAO.findById(1)).thenReturn(new JornadaAulaAspirante());
            when(resultadoDAO.findByJornadaAulaAspirante(1, 0, 10))
                    .thenReturn(Collections.emptyList());

            Response r = resource.findRange(1, 0, 10);

            assertEquals(200, r.getStatus());
            assertEquals("0", r.getHeaderString("Total-records"));
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response r = resource.findRange(null, 0, 10);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoFirstEsNegativo() {
            Response r = resource.findRange(1, -1, 10);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoMaxEsCero() {
            Response r = resource.findRange(1, 0, 0);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoMaxExcedeLimite() {
            Response r = resource.findRange(1, 0, 101);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna404_cuandoJaaNoExiste() {
            when(jaaDAO.findById(1)).thenReturn(null);

            Response r = resource.findRange(1, 0, 10);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(jaaDAO.findById(1)).thenThrow(new RuntimeException());

            Response r = resource.findRange(1, 0, 10);

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class FindById {

        @Test
        void retorna200_cuandoExiste() {
            JornadaAulaAspiranteResultado existing = buildExisting(1);
            when(resultadoDAO.findById(1)).thenReturn(existing);

            Response r = resource.findById(1, 1);

            assertEquals(200, r.getStatus());
        }

        @Test
        void retorna404_cuandoNoExiste() {
            when(resultadoDAO.findById(1)).thenReturn(null);

            Response r = resource.findById(1, 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoMismatch() {
            // resp.getIdJornadaAulaAspirante().getId() != idJornadaAulaAspirante
            JornadaAulaAspiranteResultado existing = buildExisting(99);
            when(resultadoDAO.findById(1)).thenReturn(existing);

            Response r = resource.findById(1, 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJaaEsNulo() {
            // idJornadaAulaAspirante == null (cortocircuito)
            Response r = resource.findById(null, 1);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            // idJornadaAulaAspirante != null pero id == null
            Response r = resource.findById(1, null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(resultadoDAO.findById(1)).thenThrow(new RuntimeException());

            Response r = resource.findById(1, 1);

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class Create {

        @Test
        void retorna201_cuandoCreacionExitosa() {
            JornadaAulaAspirante jaa = new JornadaAulaAspirante();
            jaa.setId(1);
            JornadaAulaAspiranteResultado entity = new JornadaAulaAspiranteResultado();

            when(jaaDAO.findById(1)).thenReturn(jaa);

            UriInfo uriInfo = mock(UriInfo.class);
            when(uriInfo.getAbsolutePathBuilder())
                    .thenReturn(UriBuilder.fromUri("http://localhost"));

            Response r = resource.create(1, entity, uriInfo);

            assertEquals(201, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJaaEsNulo() {
            // idJornadaAulaAspirante == null (cortocircuito)
            Response r = resource.create(null, new JornadaAulaAspiranteResultado(), mock(UriInfo.class));
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoEntityEsNula() {
            // idJornadaAulaAspirante != null pero entity == null
            Response r = resource.create(1, null, mock(UriInfo.class));
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoEntityTieneId() {
            JornadaAulaAspiranteResultado entity = new JornadaAulaAspiranteResultado();
            entity.setId(1);

            Response r = resource.create(1, entity, mock(UriInfo.class));

            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna404_cuandoJaaNoExiste() {
            when(jaaDAO.findById(1)).thenReturn(null);

            Response r = resource.create(1, new JornadaAulaAspiranteResultado(), mock(UriInfo.class));

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna409_cuandoHayDuplicateKey() {
            JornadaAulaAspiranteResultado entity = new JornadaAulaAspiranteResultado();
            UriInfo uriInfo = mock(UriInfo.class);
            when(uriInfo.getAbsolutePathBuilder())
                    .thenReturn(UriBuilder.fromUri("http://localhost"));

            when(jaaDAO.findById(1)).thenReturn(new JornadaAulaAspirante());

            RuntimeException cause = new RuntimeException("duplicate key value violates unique constraint");
            doThrow(new RuntimeException(cause)).when(resultadoDAO).create(entity);

            Response r = resource.create(1, entity, uriInfo);

            assertEquals(409, r.getStatus());
        }

        @Test
        void retorna500_cuandoExcepcionSinCause() {
            JornadaAulaAspiranteResultado entity = new JornadaAulaAspiranteResultado();
            when(jaaDAO.findById(1)).thenReturn(new JornadaAulaAspirante());
            doThrow(new RuntimeException("error")).when(resultadoDAO).create(entity);

            Response r = resource.create(1, entity, mock(UriInfo.class));

            assertEquals(500, r.getStatus());
        }

        @Test
        void retorna500_cuandoExcepcionSinMensaje() {
            JornadaAulaAspiranteResultado entity = new JornadaAulaAspiranteResultado();
            when(jaaDAO.findById(1)).thenReturn(new JornadaAulaAspirante());
            doThrow(new RuntimeException(new RuntimeException((String) null)))
                    .when(resultadoDAO).create(entity);

            Response r = resource.create(1, entity, mock(UriInfo.class));

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class Update {

        @Test
        void retorna200_cuandoActualizacionExitosa() {
            JornadaAulaAspiranteResultado existing = buildExisting(1);
            when(resultadoDAO.findById(1)).thenReturn(existing);
            when(jaaDAO.findById(1)).thenReturn(new JornadaAulaAspirante());
            when(resultadoDAO.update(any())).thenReturn(new JornadaAulaAspiranteResultado());

            Response r = resource.update(1, 1, new JornadaAulaAspiranteResultado());

            assertEquals(200, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJaaEsNulo() {
            Response r = resource.update(null, 1, new JornadaAulaAspiranteResultado());
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response r = resource.update(1, null, new JornadaAulaAspiranteResultado());
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoEntityEsNula() {
            Response r = resource.update(1, 1, null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna404_cuandoExistingEsNulo() {
            when(resultadoDAO.findById(1)).thenReturn(null);

            Response r = resource.update(1, 1, new JornadaAulaAspiranteResultado());

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoMismatch() {
            JornadaAulaAspiranteResultado existing = buildExisting(99);
            when(resultadoDAO.findById(1)).thenReturn(existing);

            Response r = resource.update(1, 1, new JornadaAulaAspiranteResultado());

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoJaaNoExiste() {
            JornadaAulaAspiranteResultado existing = buildExisting(1);
            when(resultadoDAO.findById(1)).thenReturn(existing);
            when(jaaDAO.findById(1)).thenReturn(null);

            Response r = resource.update(1, 1, new JornadaAulaAspiranteResultado());

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(resultadoDAO.findById(1)).thenThrow(new RuntimeException());

            Response r = resource.update(1, 1, new JornadaAulaAspiranteResultado());

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class Delete {

        @Test
        void retorna204_cuandoEliminacionExitosa() {
            JornadaAulaAspiranteResultado existing = buildExisting(1);
            when(resultadoDAO.findById(1)).thenReturn(existing);

            Response r = resource.delete(1, 1);

            assertEquals(204, r.getStatus());
            verify(resultadoDAO).delete(existing);
        }

        @Test
        void retorna404_cuandoNoExiste() {
            when(resultadoDAO.findById(1)).thenReturn(null);

            Response r = resource.delete(1, 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoMismatch() {
            JornadaAulaAspiranteResultado existing = buildExisting(99);
            when(resultadoDAO.findById(1)).thenReturn(existing);

            Response r = resource.delete(1, 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJaaEsNulo() {
            Response r = resource.delete(null, 1);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdEsNulo() {
            Response r = resource.delete(1, null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(resultadoDAO.findById(1)).thenThrow(new RuntimeException());

            Response r = resource.delete(1, 1);

            assertEquals(500, r.getStatus());
        }
    }
}