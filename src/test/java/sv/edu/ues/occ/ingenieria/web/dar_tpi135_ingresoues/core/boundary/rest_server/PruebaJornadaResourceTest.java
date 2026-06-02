package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.PruebaJornadaDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaJornada;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PruebaJornadaResourceTest {

    @Mock
    PruebaJornadaDAO pruebaJornadaDAO;

    @Mock
    PruebaDAO pruebaDAO;

    @Mock
    JornadaDAO jornadaDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    PruebaJornadaResource pruebaJornadaResource;

    private static final int FIRST = 0;
    private static final int MAX = 10;
    private static final int INVALIDFIRST = -1;
    private static final int INVALIDMAX = 0;
    private static final int EXCEEDMAX = 11;

    private UUID idPrueba;
    private UUID idJornada;
    private Prueba prueba;
    private Jornada jornada;
    private PruebaJornada pruebaJornada;
    private PruebaJornada entity;

    @BeforeEach
    void setUp() {
        idPrueba = UUID.randomUUID();
        idJornada = UUID.randomUUID();

        prueba = new Prueba();
        prueba.setId(idPrueba);

        jornada = new Jornada();
        jornada.setId(idJornada);

        pruebaJornada = new PruebaJornada();
        pruebaJornada.setId(1);
        pruebaJornada.setIdPrueba(prueba);
        pruebaJornada.setIdJornada(jornada);

        entity = new PruebaJornada();
    }

    @Nested
    class findRange {

        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos() {
            List<PruebaJornada> lista = List.of(pruebaJornada);
            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(pruebaJornadaDAO.findByPrueba(idPrueba, FIRST, MAX)).thenReturn(lista);
            when(pruebaJornadaDAO.count()).thenReturn(1);

            Response response = pruebaJornadaResource.findRange(idPrueba, FIRST, MAX);

            assertEquals(200, response.getStatus());
            assertEquals(lista, response.getEntity());
            assertEquals("1", response.getHeaderString("X-Total-Count"));
            verify(pruebaDAO).findById(idPrueba);
            verify(pruebaJornadaDAO).findByPrueba(idPrueba, FIRST, MAX);
        }

        @Test
        void retorna422_cuandoIdPruebaNulo() {
            Response response = pruebaJornadaResource.findRange(null, FIRST, MAX);

            assertEquals(422, response.getStatus());
            assertEquals("idPrueba", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaDAO, pruebaJornadaDAO);
        }

        @Test
        void retorna422_cuandoPaginacionEsInvalida() {
            Response respFirst = pruebaJornadaResource.findRange(idPrueba, INVALIDFIRST, MAX);
            assertEquals(422, respFirst.getStatus());

            Response respMax = pruebaJornadaResource.findRange(idPrueba, FIRST, INVALIDMAX);
            assertEquals(422, respMax.getStatus());

            Response respExceed = pruebaJornadaResource.findRange(idPrueba, FIRST, EXCEEDMAX);
            assertEquals(422, respExceed.getStatus());

            verifyNoInteractions(pruebaDAO, pruebaJornadaDAO);
        }

        @Test
        void retorna404_cuandoPruebaNoExiste() {
            when(pruebaDAO.findById(idPrueba)).thenReturn(null);

            Response response = pruebaJornadaResource.findRange(idPrueba, FIRST, MAX);

            assertEquals(404, response.getStatus());
            assertEquals("Prueba with id " + idPrueba + " not found", response.getHeaderString("Not-found-id"));
            verify(pruebaDAO).findById(idPrueba);
            verifyNoInteractions(pruebaJornadaDAO);
        }
    }

    @Nested
    class findOne {

        @Test
        void retorna200ConEntidad_cuandoParametrosSonValidos() {
            List<PruebaJornada> lista = List.of(pruebaJornada);
            when(pruebaJornadaDAO.findByPruebaAndJornada(idPrueba, idJornada, 0, 1)).thenReturn(lista);

            Response response = pruebaJornadaResource.findOne(idPrueba, idJornada);

            assertEquals(200, response.getStatus());
            assertEquals(pruebaJornada, response.getEntity());
            verify(pruebaJornadaDAO).findByPruebaAndJornada(idPrueba, idJornada, 0, 1);
        }

        @Test
        void retorna422_cuandoIdsNulos() {
            Response response1 = pruebaJornadaResource.findOne(null, idJornada);
            assertEquals(422, response1.getStatus());

            Response response2 = pruebaJornadaResource.findOne(idPrueba, null);
            assertEquals(422, response2.getStatus());

            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentraRegistro() {
            when(pruebaJornadaDAO.findByPruebaAndJornada(idPrueba, idJornada, 0, 1)).thenReturn(List.of());

            Response response = pruebaJornadaResource.findOne(idPrueba, idJornada);

            assertEquals(404, response.getStatus());
            assertEquals("Record with id linking prueba " + idPrueba + " and jornada " + idJornada + " not found", response.getHeaderString("Not-found-id"));
        }
    }

    @Nested
    class Create {
        @Test
        void retorna201_cuandoEntidadEsValidaYAsignaFechaSiEsNula() {
            entity.setIdJornada(jornada);
            entity.setFechaAsignacion(null);

            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);

            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("v1/prueba/jornada/" + idJornada));

            doAnswer(inv -> {
                entity.setId(1);
                return null;
            }).when(pruebaJornadaDAO).create(entity);

            Response response = pruebaJornadaResource.create(idPrueba, entity, uriInfo);

            assertEquals(201, response.getStatus());
            assertNull(response.getEntity());
            assertNotNull(entity.getFechaAsignacion());
            verify(pruebaJornadaDAO).create(entity);
        }

        @Test
        void retorna201_cuandoEntidadEsValidaYRespetaFechaExistente() {
            entity.setIdJornada(jornada);
            OffsetDateTime fechaPrevia = OffsetDateTime.now().minusDays(5);
            entity.setFechaAsignacion(fechaPrevia);

            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);

            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("v1/prueba/jornada/" + idJornada));

            Response response = pruebaJornadaResource.create(idPrueba, entity, uriInfo);

            assertEquals(201, response.getStatus());
            assertEquals(fechaPrevia, entity.getFechaAsignacion());
            verify(pruebaJornadaDAO).create(entity);
        }

        @Test
        void retorna422_cuandoDatosInvalidos() {
            Response resp1 = pruebaJornadaResource.create(null, entity, uriInfo);
            assertEquals(422, resp1.getStatus());

            Response resp2 = pruebaJornadaResource.create(idPrueba, null, uriInfo);
            assertEquals(422, resp2.getStatus());

            entity.setId(1);
            Response resp3 = pruebaJornadaResource.create(idPrueba, entity, uriInfo);
            assertEquals(422, resp3.getStatus());

            entity.setId(null);
            entity.setIdJornada(null);
            Response resp4 = pruebaJornadaResource.create(idPrueba, entity, uriInfo);
            assertEquals(422, resp4.getStatus());

            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna404_cuandoPadresNoExisten() {
            entity.setIdJornada(jornada);

            when(pruebaDAO.findById(idPrueba)).thenReturn(null);
            Response response1 = pruebaJornadaResource.create(idPrueba, entity, uriInfo);
            assertEquals(404, response1.getStatus());

            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(jornadaDAO.findById(idJornada)).thenReturn(null);
            Response response2 = pruebaJornadaResource.create(idPrueba, entity, uriInfo);
            assertEquals(404, response2.getStatus());

            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna422_cuandoJornadaVieneSinId() {
            Jornada jornadaSinId = new Jornada();
            jornadaSinId.setId(null);
            entity.setIdJornada(jornadaSinId);

            Response response = pruebaJornadaResource.create(idPrueba, entity, uriInfo);

            assertEquals(422, response.getStatus());
            verifyNoInteractions(pruebaJornadaDAO);
        }
    }

    @Nested
    class Update {

        @Test
        void retorna200_cuandoEntidadEsValida() {
            List<PruebaJornada> lista = List.of(pruebaJornada);
            entity.setFechaAsignacion(OffsetDateTime.now());

            when(pruebaJornadaDAO.findByPruebaAndJornada(idPrueba, idJornada, 0, 1)).thenReturn(lista);
            when(pruebaJornadaDAO.update(pruebaJornada)).thenReturn(pruebaJornada);

            Response response = pruebaJornadaResource.update(idPrueba, idJornada, entity);

            assertEquals(200, response.getStatus());
            assertEquals(pruebaJornada, response.getEntity());
            verify(pruebaJornadaDAO).update(pruebaJornada);
        }

        @Test
        void retorna422_cuandoFaltanParametros() {
            Response resp1 = pruebaJornadaResource.update(null, idJornada, entity);
            assertEquals(422, resp1.getStatus());

            Response resp2 = pruebaJornadaResource.update(idPrueba, null, entity);
            assertEquals(422, resp2.getStatus());

            Response resp3 = pruebaJornadaResource.update(idPrueba, idJornada, null);
            assertEquals(422, resp3.getStatus());
        }

        @Test
        void retorna404_cuandoRegistroNoExiste() {
            when(pruebaJornadaDAO.findByPruebaAndJornada(idPrueba, idJornada, 0, 1)).thenReturn(List.of());

            Response response = pruebaJornadaResource.update(idPrueba, idJornada, entity);

            assertEquals(404, response.getStatus());
            verify(pruebaJornadaDAO, never()).update(any());
        }

        @Test
        void retorna200_cuandoEntidadEsValidaYNoSeEnviaFecha() {
            List<PruebaJornada> lista = List.of(pruebaJornada);
            entity.setFechaAsignacion(null);

            when(pruebaJornadaDAO.findByPruebaAndJornada(idPrueba, idJornada, 0, 1)).thenReturn(lista);
            when(pruebaJornadaDAO.update(pruebaJornada)).thenReturn(pruebaJornada);

            Response response = pruebaJornadaResource.update(idPrueba, idJornada, entity);

            assertEquals(200, response.getStatus());
            assertEquals(pruebaJornada, response.getEntity());
            verify(pruebaJornadaDAO).update(pruebaJornada);
        }
    }

    @Nested
    class Delete {

        @Test
        void retorna204_cuandoRegistroExiste() {
            List<PruebaJornada> lista = List.of(pruebaJornada);
            when(pruebaJornadaDAO.findByPruebaAndJornada(idPrueba, idJornada, 0, 1)).thenReturn(lista);

            Response response = pruebaJornadaResource.delete(idPrueba, idJornada);

            assertEquals(204, response.getStatus());
            verify(pruebaJornadaDAO).delete(pruebaJornada);
        }

        @Test
        void retorna422_cuandoIdsNulos() {
            Response resp1 = pruebaJornadaResource.delete(null, idJornada);
            assertEquals(422, resp1.getStatus());

            Response resp2 = pruebaJornadaResource.delete(idPrueba, null);
            assertEquals(422, resp2.getStatus());
        }

        @Test
        void retorna404_cuandoRegistroNoExiste() {
            when(pruebaJornadaDAO.findByPruebaAndJornada(idPrueba, idJornada, 0, 1)).thenReturn(List.of());

            Response response = pruebaJornadaResource.delete(idPrueba, idJornada);

            assertEquals(404, response.getStatus());
            verify(pruebaJornadaDAO, never()).delete(any());
        }
    }
}