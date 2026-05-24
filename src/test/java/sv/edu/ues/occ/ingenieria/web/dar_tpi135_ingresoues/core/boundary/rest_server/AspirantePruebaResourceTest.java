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

    @Nested
    class FindRange {

        @Test
        void retorna200_cuandoParametrosSonValidos() {
            UUID idAspirante = UUID.randomUUID();
            when(aspiranteDAO.findById(idAspirante)).thenReturn(new Aspirante());
            when(apDAO.countByAspirante(idAspirante)).thenReturn(1L);
            when(apDAO.findByAspirante(idAspirante, 0, 10)).thenReturn(Collections.emptyList());

            Response r = resource.findRange(idAspirante, 0, 10);

            assertEquals(200, r.getStatus());
            assertEquals("1", r.getHeaderString("Total-records"));
        }

        @Test
        void retorna422_cuandoIdAspiranteEsNulo() {
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
        void retorna404_cuandoAspiranteNoExiste() {
            UUID idAspirante = UUID.randomUUID();
            when(aspiranteDAO.findById(idAspirante)).thenReturn(null);

            Response r = resource.findRange(idAspirante, 0, 10);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            UUID idAspirante = UUID.randomUUID();
            when(aspiranteDAO.findById(idAspirante)).thenThrow(new RuntimeException());

            Response r = resource.findRange(idAspirante, 0, 10);

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class FindById {

        @Test
        void retorna200_cuandoExiste() {
            UUID idAspirante = UUID.randomUUID();
            Aspirante aspirante = new Aspirante();
            aspirante.setId(idAspirante);

            AspirantePrueba ap = new AspirantePrueba();
            ap.setId(1);
            ap.setIdAspirante(aspirante);

            when(apDAO.findById(1)).thenReturn(ap);

            Response r = resource.findById(idAspirante, 1);

            assertEquals(200, r.getStatus());
        }

        @Test
        void retorna404_cuandoNoExiste() {
            when(apDAO.findById(1)).thenReturn(null);

            Response r = resource.findById(UUID.randomUUID(), 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoAspiranteMismatch() {
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
        void retorna422_cuandoAmbosNulos() {
            Response r = resource.findById(null, null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdPruebaEsNulo() {
            Response r = resource.findById(UUID.randomUUID(), null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdAspiranteEsNulo() {
            Response r = resource.findById(null, 1);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(apDAO.findById(1)).thenThrow(new RuntimeException());

            Response r = resource.findById(UUID.randomUUID(), 1);

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class Create {

        @Test
        void retorna201_cuandoCreacionExitosa() {
            UUID idAspirante = UUID.randomUUID();
            UUID idPrueba = UUID.randomUUID();

            Aspirante aspirante = new Aspirante();
            aspirante.setId(idAspirante);

            Prueba prueba = new Prueba();
            prueba.setId(idPrueba);

            AspirantePrueba entity = new AspirantePrueba();
            entity.setIdPrueba(prueba);

            when(aspiranteDAO.findById(idAspirante)).thenReturn(aspirante);
            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);

            UriInfo uriInfo = mock(UriInfo.class);
            when(uriInfo.getAbsolutePathBuilder())
                    .thenReturn(UriBuilder.fromUri("http://localhost"));

            doAnswer(inv -> { entity.setId(1); return null; }).when(apDAO).create(entity);

            Response r = resource.create(idAspirante, entity, uriInfo);

            assertEquals(201, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdAspiranteEsNulo() {
            Response r = resource.create(null, new AspirantePrueba(), mock(UriInfo.class));
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoEntityEsNula() {
            Response r = resource.create(UUID.randomUUID(), null, mock(UriInfo.class));
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoEntityTieneId() {
            AspirantePrueba e = new AspirantePrueba();
            e.setId(1);

            Response r = resource.create(UUID.randomUUID(), e, mock(UriInfo.class));

            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna404_cuandoAspiranteNoExiste() {
            when(aspiranteDAO.findById(any())).thenReturn(null);

            Response r = resource.create(UUID.randomUUID(), new AspirantePrueba(), mock(UriInfo.class));

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdPruebaEsNulo() {
            when(aspiranteDAO.findById(any())).thenReturn(new Aspirante());

            Response r = resource.create(UUID.randomUUID(), new AspirantePrueba(), mock(UriInfo.class));

            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdDePruebaEsNulo() {
            AspirantePrueba e = new AspirantePrueba();
            e.setIdPrueba(new Prueba()); // Prueba sin id

            when(aspiranteDAO.findById(any())).thenReturn(new Aspirante());

            Response r = resource.create(UUID.randomUUID(), e, mock(UriInfo.class));

            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna404_cuandoPruebaNoExiste() {
            UUID idAspirante = UUID.randomUUID();
            UUID idPrueba = UUID.randomUUID();

            Prueba p = new Prueba();
            p.setId(idPrueba);

            AspirantePrueba e = new AspirantePrueba();
            e.setIdPrueba(p);

            when(aspiranteDAO.findById(idAspirante)).thenReturn(new Aspirante());
            when(pruebaDAO.findById(idPrueba)).thenReturn(null);

            Response r = resource.create(idAspirante, e, mock(UriInfo.class));

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna409_cuandoHayDuplicateKey() {
            UUID idAspirante = UUID.randomUUID();
            UUID idPrueba = UUID.randomUUID();

            Prueba p = new Prueba();
            p.setId(idPrueba);

            AspirantePrueba e = new AspirantePrueba();
            e.setIdPrueba(p);

            when(aspiranteDAO.findById(idAspirante)).thenReturn(new Aspirante());
            when(pruebaDAO.findById(idPrueba)).thenReturn(p);

            UriInfo uriInfo = mock(UriInfo.class);
            when(uriInfo.getAbsolutePathBuilder())
                    .thenReturn(UriBuilder.fromUri("http://localhost"));

            RuntimeException cause = new RuntimeException("duplicate key value violates unique constraint");
            doThrow(new RuntimeException(cause)).when(apDAO).create(e);

            Response r = resource.create(idAspirante, e, uriInfo);

            assertEquals(409, r.getStatus());
        }

        @Test
        void retorna500_cuandoExcepcionSinCause() {
            UUID idAspirante = UUID.randomUUID();
            UUID idPrueba = UUID.randomUUID();

            Prueba p = new Prueba();
            p.setId(idPrueba);

            AspirantePrueba e = new AspirantePrueba();
            e.setIdPrueba(p);

            when(aspiranteDAO.findById(idAspirante)).thenReturn(new Aspirante());
            when(pruebaDAO.findById(idPrueba)).thenReturn(p);

            doThrow(new RuntimeException("error generico")).when(apDAO).create(e);

            Response r = resource.create(idAspirante, e, mock(UriInfo.class));

            assertEquals(500, r.getStatus());
        }

        @Test
        void retorna500_cuandoExcepcionSinMensaje() {
            UUID idAspirante = UUID.randomUUID();
            UUID idPrueba = UUID.randomUUID();

            Prueba p = new Prueba();
            p.setId(idPrueba);

            AspirantePrueba e = new AspirantePrueba();
            e.setIdPrueba(p);

            when(aspiranteDAO.findById(idAspirante)).thenReturn(new Aspirante());
            when(pruebaDAO.findById(idPrueba)).thenReturn(p);

            doThrow(new RuntimeException(new RuntimeException((String) null))).when(apDAO).create(e);

            Response r = resource.create(idAspirante, e, mock(UriInfo.class));

            assertEquals(500, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(aspiranteDAO.findById(any())).thenThrow(new RuntimeException());

            Response r = resource.create(UUID.randomUUID(), new AspirantePrueba(), mock(UriInfo.class));

            assertEquals(500, r.getStatus());
        }
    }

    @Nested
    class Update {

        @Test
        void retorna200_conPruebaEnBody() {
            UUID idAspirante = UUID.randomUUID();
            UUID idPrueba = UUID.randomUUID();

            Aspirante aspirante = new Aspirante();
            aspirante.setId(idAspirante);

            Prueba prueba = new Prueba();
            prueba.setId(idPrueba);

            AspirantePrueba existing = new AspirantePrueba();
            existing.setIdAspirante(aspirante);

            AspirantePrueba entity = new AspirantePrueba();
            entity.setIdPrueba(prueba);

            when(apDAO.findById(1)).thenReturn(existing);
            when(aspiranteDAO.findById(idAspirante)).thenReturn(aspirante);
            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(apDAO.update(any())).thenReturn(new AspirantePrueba());

            Response r = resource.update(idAspirante, 1, entity);

            assertEquals(200, r.getStatus());
        }

        @Test
        void retorna200_sinPruebaEnBody() {
            UUID idAspirante = UUID.randomUUID();

            Aspirante aspirante = new Aspirante();
            aspirante.setId(idAspirante);

            AspirantePrueba existing = new AspirantePrueba();
            existing.setIdAspirante(aspirante);
            existing.setIdPrueba(new Prueba());

            when(apDAO.findById(1)).thenReturn(existing);
            when(aspiranteDAO.findById(idAspirante)).thenReturn(aspirante);
            when(apDAO.update(any())).thenReturn(new AspirantePrueba());

            Response r = resource.update(idAspirante, 1, new AspirantePrueba());

            assertEquals(200, r.getStatus());
        }

        @Test
        void retorna404_cuandoPruebaNoExisteEnUpdate() {
            UUID idAspirante = UUID.randomUUID();
            UUID idPrueba = UUID.randomUUID();

            Aspirante aspirante = new Aspirante();
            aspirante.setId(idAspirante);

            Prueba prueba = new Prueba();
            prueba.setId(idPrueba);

            AspirantePrueba existing = new AspirantePrueba();
            existing.setIdAspirante(aspirante);

            AspirantePrueba entity = new AspirantePrueba();
            entity.setIdPrueba(prueba);

            when(apDAO.findById(1)).thenReturn(existing);
            when(aspiranteDAO.findById(idAspirante)).thenReturn(aspirante);
            when(pruebaDAO.findById(idPrueba)).thenReturn(null);

            Response r = resource.update(idAspirante, 1, entity);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna422_cuandoTodosNulos() {
            Response r = resource.update(null, null, null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdPruebaEsNulo() {
            // idAspirante != null, idPrueba == null
            Response r = resource.update(UUID.randomUUID(), null, new AspirantePrueba());
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoEntityEsNula() {
            // idAspirante != null, idPrueba != null, entity == null
            Response r = resource.update(UUID.randomUUID(), 1, null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna404_cuandoExistingEsNulo() {
            when(apDAO.findById(1)).thenReturn(null);

            Response r = resource.update(UUID.randomUUID(), 1, new AspirantePrueba());

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoAspiranteMismatch() {
            UUID idAspirante = UUID.randomUUID();

            Aspirante otro = new Aspirante();
            otro.setId(UUID.randomUUID());

            AspirantePrueba existing = new AspirantePrueba();
            existing.setIdAspirante(otro);

            when(apDAO.findById(1)).thenReturn(existing);

            Response r = resource.update(idAspirante, 1, new AspirantePrueba());

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoAspiranteNoExiste() {
            UUID idAspirante = UUID.randomUUID();

            Aspirante aspirante = new Aspirante();
            aspirante.setId(idAspirante);

            AspirantePrueba existing = new AspirantePrueba();
            existing.setIdAspirante(aspirante);

            when(apDAO.findById(1)).thenReturn(existing);
            when(aspiranteDAO.findById(idAspirante)).thenReturn(null);

            Response r = resource.update(idAspirante, 1, new AspirantePrueba());

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(apDAO.findById(1)).thenThrow(new RuntimeException());

            Response r = resource.update(UUID.randomUUID(), 1, new AspirantePrueba());

            assertEquals(500, r.getStatus());
        }

        @Test
        void retorna200_cuandoPruebaEnBodySinId() {
            // entity.getIdPrueba() != null pero getId() == null → va al else
            UUID idAspirante = UUID.randomUUID();

            Aspirante aspirante = new Aspirante();
            aspirante.setId(idAspirante);

            AspirantePrueba existing = new AspirantePrueba();
            existing.setIdAspirante(aspirante);
            existing.setIdPrueba(new Prueba());

            AspirantePrueba entity = new AspirantePrueba();
            entity.setIdPrueba(new Prueba());

            when(apDAO.findById(1)).thenReturn(existing);
            when(aspiranteDAO.findById(idAspirante)).thenReturn(aspirante);
            when(apDAO.update(any())).thenReturn(new AspirantePrueba());

            Response r = resource.update(idAspirante, 1, entity);

            assertEquals(200, r.getStatus());
        }
    }

    @Nested
    class Delete {

        @Test
        void retorna204_cuandoEliminacionExitosa() {
            UUID idAspirante = UUID.randomUUID();

            Aspirante aspirante = new Aspirante();
            aspirante.setId(idAspirante);

            AspirantePrueba existing = new AspirantePrueba();
            existing.setIdAspirante(aspirante);

            when(apDAO.findById(1)).thenReturn(existing);

            Response r = resource.delete(idAspirante, 1);

            assertEquals(204, r.getStatus());
            verify(apDAO).delete(existing);
        }

        @Test
        void retorna404_cuandoNoExiste() {
            when(apDAO.findById(1)).thenReturn(null);

            Response r = resource.delete(UUID.randomUUID(), 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna404_cuandoAspiranteMismatch() {
            UUID idAspirante = UUID.randomUUID();

            Aspirante otro = new Aspirante();
            otro.setId(UUID.randomUUID());

            AspirantePrueba existing = new AspirantePrueba();
            existing.setIdAspirante(otro);

            when(apDAO.findById(1)).thenReturn(existing);

            Response r = resource.delete(idAspirante, 1);

            assertEquals(404, r.getStatus());
        }

        @Test
        void retorna422_cuandoAmbosNulos() {
            Response r = resource.delete(null, null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdPruebaEsNulo() {
            Response r = resource.delete(UUID.randomUUID(), null);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna422_cuandoIdAspiranteEsNulo() {
            Response r = resource.delete(null, 1);
            assertEquals(422, r.getStatus());
        }

        @Test
        void retorna500_cuandoDAOLanzaExcepcion() {
            when(apDAO.findById(1)).thenThrow(new RuntimeException());

            Response r = resource.delete(UUID.randomUUID(), 1);

            assertEquals(500, r.getStatus());
        }
    }
}