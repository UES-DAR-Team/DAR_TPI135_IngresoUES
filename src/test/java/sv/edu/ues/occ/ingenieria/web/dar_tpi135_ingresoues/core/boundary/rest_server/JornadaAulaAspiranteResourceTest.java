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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JornadaAulaAspiranteResourceTest {

    private JornadaAulaAspiranteResource resource;
    private JornadaAulaAspiranteDAO jaaDAO;
    private JornadaAulaDAO jornadaAulaDAO;
    private AspirantePruebaDAO aspirantePruebaDAO;

    @BeforeEach
    void setUp() {
        resource = new JornadaAulaAspiranteResource();
        jaaDAO = mock(JornadaAulaAspiranteDAO.class);
        jornadaAulaDAO = mock(JornadaAulaDAO.class);
        aspirantePruebaDAO = mock(AspirantePruebaDAO.class);

        resource.jaaDAO = jaaDAO;
        resource.jornadaAulaDAO = jornadaAulaDAO;
        resource.aspirantePruebaDAO = aspirantePruebaDAO;
    }

    /** Construye un JornadaAulaAspirante con idJornadaAula dado para verificar match. */
    private JornadaAulaAspirante buildExisting(Integer idJornadaAula) {
        JornadaAula ja = new JornadaAula();
        ja.setId(idJornadaAula);
        JornadaAulaAspirante jaa = new JornadaAulaAspirante();
        jaa.setIdJornadaAula(ja);
        return jaa;
    }

    /** Construye un AspirantePrueba con id dado para usar en create. */
    private JornadaAulaAspirante buildEntityWithAspirantePrueba(Integer idAspirantePrueba) {
        AspirantePrueba ap = new AspirantePrueba();
        ap.setId(idAspirantePrueba);
        JornadaAulaAspirante entity = new JornadaAulaAspirante();
        entity.setIdAspirantePrueba(ap);
        return entity;
    }

    @Nested
    class FindRange {

        @Test
        void retorna200_cuandoParametrosSonValidos() {
            when(jornadaAulaDAO.findById(1)).thenReturn(new JornadaAula());
            when(jaaDAO.countByJornadaAula(1)).thenReturn(1L);
            when(jaaDAO.findByJornadaAula(1, 0, 10)).thenReturn(Collections.emptyList());

            Response r = resource.findRange(1, 0, 10);

            assertEquals(200, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJornadaAulaEsNulo() {
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
        void retorna404_cuandoJornadaAulaNoExiste() {
            when(jornadaAulaDAO.findById(1)).thenReturn(null);

            Response r = resource.findRange(1, 0, 10);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(jornadaAulaDAO.findById(1)).thenThrow(new RuntimeException());

            Response r = resource.findRange(1, 0, 10);

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class FindById {

        @Test
        void retorna200_cuandoExiste() {
            JornadaAulaAspirante existing = buildExisting(1);
            when(jaaDAO.findById(1)).thenReturn(existing);

            Response r = resource.findById(1, 1);

            assertEquals(200, r.getStatus());
        }

        @Test
        void retorna404_cuandoNoExiste() {
            when(jaaDAO.findById(1)).thenReturn(null);

            Response r = resource.findById(1, 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoJornadaAulaMismatch() {
            JornadaAulaAspirante existing = buildExisting(99);
            when(jaaDAO.findById(1)).thenReturn(existing);

            Response r = resource.findById(1, 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJornadaAulaEsNulo() {
            Response r = resource.findById(null, 1);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdAspirantePruebaEsNulo() {
            Response r = resource.findById(1, null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(jaaDAO.findById(1)).thenThrow(new RuntimeException());

            Response r = resource.findById(1, 1);

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class Create {

        @Test
        void retorna201_cuandoCreacionExitosa() {
            JornadaAulaAspirante entity = buildEntityWithAspirantePrueba(1);
            UriInfo uriInfo = mock(UriInfo.class);
            when(uriInfo.getAbsolutePathBuilder())
                    .thenReturn(UriBuilder.fromUri("http://localhost"));
            when(jornadaAulaDAO.findById(1)).thenReturn(new JornadaAula());
            when(aspirantePruebaDAO.findById(1)).thenReturn(new AspirantePrueba());

            Response r = resource.create(1, entity, uriInfo);

            assertEquals(201, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJornadaAulaEsNulo() {
            Response r = resource.create(null, new JornadaAulaAspirante(), mock(UriInfo.class));
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoEntityEsNula() {
            Response r = resource.create(1, null, mock(UriInfo.class));
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoEntityTieneId() {
            JornadaAulaAspirante entity = new JornadaAulaAspirante();
            entity.setId(1);

            Response r = resource.create(1, entity, mock(UriInfo.class));

            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdAspirantePruebaEsNulo() {
            Response r = resource.create(1, new JornadaAulaAspirante(), mock(UriInfo.class));
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna404_cuandoJornadaAulaNoExiste() {
            JornadaAulaAspirante entity = buildEntityWithAspirantePrueba(1);
            when(jornadaAulaDAO.findById(1)).thenReturn(null);

            Response r = resource.create(1, entity, mock(UriInfo.class));

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoAspirantePruebaNoExiste() {
            JornadaAulaAspirante entity = buildEntityWithAspirantePrueba(1);
            when(jornadaAulaDAO.findById(1)).thenReturn(new JornadaAula());
            when(aspirantePruebaDAO.findById(1)).thenReturn(null);

            Response r = resource.create(1, entity, mock(UriInfo.class));

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna409_cuandoHayDuplicateKey() {
            JornadaAulaAspirante entity = buildEntityWithAspirantePrueba(1);

            UriInfo uriInfo = mock(UriInfo.class);

            when(jornadaAulaDAO.findById(1)).thenReturn(new JornadaAula());
            when(aspirantePruebaDAO.findById(1)).thenReturn(new AspirantePrueba());

            RuntimeException cause =
                    new RuntimeException("duplicate key value violates unique constraint");

            doThrow(new RuntimeException(cause))
                    .when(jaaDAO).create(any(JornadaAulaAspirante.class));

            Response r = resource.create(1, entity, uriInfo);

            assertEquals(409, r.getStatus());
        }

        @Test
        void retorna500_cuandoExcepcionSinCause() {
            JornadaAulaAspirante entity = buildEntityWithAspirantePrueba(1);
            when(jornadaAulaDAO.findById(1)).thenReturn(new JornadaAula());
            when(aspirantePruebaDAO.findById(1)).thenReturn(new AspirantePrueba());
            doThrow(new RuntimeException("error")).when(jaaDAO).create(entity);

            Response r = resource.create(1, entity, mock(UriInfo.class));

            assertEquals(500, r.getStatus());
        }

        @Test
        void retorna500_cuandoExcepcionSinMensaje() {
            JornadaAulaAspirante entity = buildEntityWithAspirantePrueba(1);
            when(jornadaAulaDAO.findById(1)).thenReturn(new JornadaAula());
            when(aspirantePruebaDAO.findById(1)).thenReturn(new AspirantePrueba());
            doThrow(new RuntimeException(new RuntimeException((String) null))).when(jaaDAO).create(entity);

            Response r = resource.create(1, entity, mock(UriInfo.class));

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class Update {

        @Test
        void retorna200_cuandoActualizacionExitosa() {
            JornadaAulaAspirante existing = buildExisting(1);
            JornadaAulaAspirante entity = new JornadaAulaAspirante();
            when(jaaDAO.findById(1)).thenReturn(existing);
            when(jaaDAO.update(existing)).thenReturn(existing);

            Response r = resource.update(1, 1, entity);

            assertEquals(200, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJornadaAulaEsNulo() {
            Response r = resource.update(null, 1, new JornadaAulaAspirante());
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdAspirantePruebaEsNulo() {
            Response r = resource.update(1, null, new JornadaAulaAspirante());
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoEntityEsNula() {
            Response r = resource.update(1, 1, null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna404_cuandoExistingEsNulo() {
            when(jaaDAO.findById(1)).thenReturn(null);

            Response r = resource.update(1, 1, new JornadaAulaAspirante());

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoJornadaAulaMismatch() {
            JornadaAulaAspirante existing = buildExisting(99);
            when(jaaDAO.findById(1)).thenReturn(existing);

            Response r = resource.update(1, 1, new JornadaAulaAspirante());

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(jaaDAO.findById(1)).thenThrow(new RuntimeException());

            Response r = resource.update(1, 1, new JornadaAulaAspirante());

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class Delete {

        @Test
        void retorna204_cuandoEliminacionExitosa() {
            JornadaAulaAspirante existing = buildExisting(1);
            when(jaaDAO.findById(1)).thenReturn(existing);

            Response r = resource.delete(1, 1);

            assertEquals(204, r.getStatus());
            verify(jaaDAO).delete(existing);
        }

        @Test
        void retorna404_cuandoNoExiste() {
            when(jaaDAO.findById(1)).thenReturn(null);

            Response r = resource.delete(1, 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoJornadaAulaMismatch() {
            JornadaAulaAspirante existing = buildExisting(99);
            when(jaaDAO.findById(1)).thenReturn(existing);

            Response r = resource.delete(1, 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdJornadaAulaEsNulo() {
            Response r = resource.delete(null, 1);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdAspirantePruebaEsNulo() {
            Response r = resource.delete(1, null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(jaaDAO.findById(1)).thenThrow(new RuntimeException());

            Response r = resource.delete(1, 1);

            assertEquals(500, r.getStatus());
        }
    }
}