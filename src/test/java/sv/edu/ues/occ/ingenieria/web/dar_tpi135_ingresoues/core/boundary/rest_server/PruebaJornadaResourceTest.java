package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    PruebaJornadaResource resource;

    private UUID idPrueba;
    private UUID idJornada;
    private Integer id;
    private Prueba prueba;
    private Jornada jornada;
    private PruebaJornada entity;

    @BeforeEach
    void setUp() {
        idPrueba = UUID.randomUUID();
        idJornada = UUID.randomUUID();
        id = 1;

        prueba = new Prueba();
        prueba.setId(idPrueba);

        jornada = new Jornada();
        jornada.setId(idJornada);

        entity = new PruebaJornada();
        entity.setId(id);
        entity.setIdPrueba(prueba);
        entity.setIdJornada(jornada);
        entity.setFechaAsignacion(OffsetDateTime.now());
    }

    @Nested
    class FindById {

        @Test
        void retorna200ParametrosValidos() {
            when(pruebaJornadaDAO.findById(id)).thenReturn(entity);
            Response response = resource.findById(idPrueba, idJornada, id);
            assertEquals(200, response.getStatus());
            assertEquals(entity, response.getEntity());
            verify(pruebaJornadaDAO).findById(id);
        }

        @Test
        void retorna422IdPruebaNulo() {
            Response response = resource.findById(null, idJornada, id);
            assertEquals(422, response.getStatus());
            assertEquals("idPrueba,idJornada,id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna422IdJornadaNulo() {
            Response response = resource.findById(idPrueba, null, id);
            assertEquals(422, response.getStatus());
            assertEquals("idPrueba,idJornada,id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna422IdNulo() {
            Response response = resource.findById(idPrueba, idJornada, null);
            assertEquals(422, response.getStatus());
            assertEquals("idPrueba,idJornada,id", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna404NoExisteRegistro() {
            when(pruebaJornadaDAO.findById(id)).thenReturn(null);
            Response response = resource.findById(idPrueba, idJornada, id);
            assertEquals(404, response.getStatus());
            assertEquals("Record not found", response.getHeaderString("Not-found"));
            verify(pruebaJornadaDAO).findById(id);
        }

        @Test
        void retorna404PruebaEsNull() {
            entity.setIdPrueba(null);
            when(pruebaJornadaDAO.findById(id)).thenReturn(entity);
            Response response = resource.findById(idPrueba, idJornada, id);
            assertEquals(404, response.getStatus());
            assertEquals("Record not found", response.getHeaderString("Not-found"));
            verify(pruebaJornadaDAO).findById(id);
        }

        @Test
        void retorna404JornadaEsNull() {
            entity.setIdJornada(null);
            when(pruebaJornadaDAO.findById(id)).thenReturn(entity);
            Response response = resource.findById(idPrueba, idJornada, id);
            assertEquals(404, response.getStatus());
            assertEquals("Record not found", response.getHeaderString("Not-found"));
            verify(pruebaJornadaDAO).findById(id);
        }

        @Test
        void retorna404IdPruebaNoCoincide() {
            Prueba otraPrueba = new Prueba();
            otraPrueba.setId(UUID.randomUUID());
            entity.setIdPrueba(otraPrueba);
            when(pruebaJornadaDAO.findById(id)).thenReturn(entity);
            Response response = resource.findById(idPrueba, idJornada, id);
            assertEquals(404, response.getStatus());
            assertEquals("Record not found", response.getHeaderString("Not-found"));
            verify(pruebaJornadaDAO).findById(id);
        }

        @Test
        void retorna404IdJornadaNoCoincide() {
            Jornada otraJornada = new Jornada();
            otraJornada.setId(UUID.randomUUID());
            entity.setIdJornada(otraJornada);
            when(pruebaJornadaDAO.findById(id)).thenReturn(entity);
            Response response = resource.findById(idPrueba, idJornada, id);
            assertEquals(404, response.getStatus());
            assertEquals("Record not found", response.getHeaderString("Not-found"));
            verify(pruebaJornadaDAO).findById(id);
        }

        @Test
        void retorna500DAOExcepcion() {
            when(pruebaJornadaDAO.findById(id)).thenThrow(new RuntimeException("DB error"));
            Response response = resource.findById(idPrueba, idJornada, id);
            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(pruebaJornadaDAO).findById(id);
        }
    }

    @Nested
    class Create {

        @Test
        void retorna201EntidadValidaConFechaAsignacion() {
            PruebaJornada newEntity = new PruebaJornada();
            newEntity.setId(null);
            newEntity.setFechaAsignacion(OffsetDateTime.now());

            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("test"));

            doAnswer(inv -> {
                PruebaJornada pj = inv.getArgument(0);
                pj.setId(1);
                return null;
            }).when(pruebaJornadaDAO).create(any(PruebaJornada.class));

            Response response = resource.create(idPrueba, idJornada, newEntity, uriInfo);

            assertEquals(201, response.getStatus());
            assertNotNull(response.getEntity());
            assertNotNull(response.getHeaderString("Location"));
            verify(pruebaDAO).findById(idPrueba);
            verify(jornadaDAO).findById(idJornada);
            verify(pruebaJornadaDAO).create(any(PruebaJornada.class));
        }

        @Test
        void retorna201FechaAsignacionNull() {
            PruebaJornada newEntity = new PruebaJornada();
            newEntity.setId(null);
            // No setear fechaAsignacion debe venir null
            assertNull(newEntity.getFechaAsignacion(), "La fecha debe ser null inicialmente");

            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("test"));

            // Capturar la entidad que se pasa al DAO para verificar que tiene fecha
            ArgumentCaptor<PruebaJornada> captor = ArgumentCaptor.forClass(PruebaJornada.class);
            doAnswer(inv -> {
                PruebaJornada pj = inv.getArgument(0);
                pj.setId(1);
                return null;
            }).when(pruebaJornadaDAO).create(captor.capture());

            Response response = resource.create(idPrueba, idJornada, newEntity, uriInfo);

            assertEquals(201, response.getStatus());

            assertNotNull(newEntity.getFechaAsignacion(), "La fecha debe ser asignada por el resource");

            PruebaJornada capturedEntity = captor.getValue();
            assertNotNull(capturedEntity.getFechaAsignacion(), "La entidad enviada al DAO debe tener fecha");

            verify(pruebaJornadaDAO).create(any(PruebaJornada.class));
        }

        @Test
        void retorna422IdPruebaNulo() {
            PruebaJornada newEntity = new PruebaJornada();
            Response response = resource.create(null, idJornada, newEntity, uriInfo);
            assertEquals(422, response.getStatus());
            assertEquals("idPrueba,idJornada,entity", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna422IdJornadaNulo() {
            PruebaJornada newEntity = new PruebaJornada();
            Response response = resource.create(idPrueba, null, newEntity, uriInfo);
            assertEquals(422, response.getStatus());
            assertEquals("idPrueba,idJornada,entity", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna422EntityNull() {
            Response response = resource.create(idPrueba, idJornada, null, uriInfo);
            assertEquals(422, response.getStatus());
            assertEquals("idPrueba,idJornada,entity", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna422EntityTieneIdPreasignado() {
            PruebaJornada entityConId = new PruebaJornada();
            entityConId.setId(999);
            Response response = resource.create(idPrueba, idJornada, entityConId, uriInfo);
            assertEquals(422, response.getStatus());
            assertEquals("idPrueba,idJornada,entity", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna404PruebaNoExiste() {
            PruebaJornada newEntity = new PruebaJornada();
            newEntity.setId(null);
            when(pruebaDAO.findById(idPrueba)).thenReturn(null);
            Response response = resource.create(idPrueba, idJornada, newEntity, uriInfo);
            assertEquals(404, response.getStatus());
            assertEquals("Prueba not found", response.getHeaderString("Not-found"));
            verify(pruebaDAO).findById(idPrueba);
            verifyNoInteractions(jornadaDAO);
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna404JornadaNoExiste() {
            PruebaJornada newEntity = new PruebaJornada();
            newEntity.setId(null);
            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(jornadaDAO.findById(idJornada)).thenReturn(null);
            Response response = resource.create(idPrueba, idJornada, newEntity, uriInfo);
            assertEquals(404, response.getStatus());
            assertEquals("Jornada not found", response.getHeaderString("Not-found"));
            verify(pruebaDAO).findById(idPrueba);
            verify(jornadaDAO).findById(idJornada);
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna500DAOExcepcion() {
            PruebaJornada newEntity = new PruebaJornada();
            newEntity.setId(null);
            newEntity.setFechaAsignacion(OffsetDateTime.now());

            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);
            doThrow(new RuntimeException("DB error")).when(pruebaJornadaDAO).create(any(PruebaJornada.class));

            Response response = resource.create(idPrueba, idJornada, newEntity, uriInfo);
            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(pruebaDAO).findById(idPrueba);
            verify(jornadaDAO).findById(idJornada);
            verify(pruebaJornadaDAO).create(any(PruebaJornada.class));
        }

        @Test
        void retorna201FechaAsignacionNullTestEspecifico() {
            PruebaJornada newEntity = new PruebaJornada();

            assertNull(newEntity.getId());
            assertNull(newEntity.getFechaAsignacion());

            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("http://test/1"));

            doAnswer(invocation -> {
                PruebaJornada entityToSave = invocation.getArgument(0);
                entityToSave.setId(1);
                return null;
            }).when(pruebaJornadaDAO).create(any(PruebaJornada.class));

            Response response = resource.create(idPrueba, idJornada, newEntity, uriInfo);

            assertEquals(201, response.getStatus());
            assertNotNull(newEntity.getFechaAsignacion());
        }
    }

    @Nested
    class Update {

        @Test
        void retorna200EntidadValida() {
            when(pruebaJornadaDAO.findById(id)).thenReturn(entity);
            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);
            when(pruebaJornadaDAO.update(any(PruebaJornada.class))).thenReturn(entity);

            Response response = resource.update(idPrueba, idJornada, id, entity);

            assertEquals(200, response.getStatus());
            assertEquals(entity, response.getEntity());
            verify(pruebaJornadaDAO).findById(id);
            verify(pruebaDAO).findById(idPrueba);
            verify(jornadaDAO).findById(idJornada);
            verify(pruebaJornadaDAO).update(any(PruebaJornada.class));
        }

        @Test
        void retorna200FechaAsignacionNullNoActualizaFecha() {
            OffsetDateTime fechaOriginal = entity.getFechaAsignacion();
            PruebaJornada updateEntity = new PruebaJornada();
            updateEntity.setFechaAsignacion(null);

            when(pruebaJornadaDAO.findById(id)).thenReturn(entity);
            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);
            when(pruebaJornadaDAO.update(any(PruebaJornada.class))).thenReturn(entity);

            Response response = resource.update(idPrueba, idJornada, id, updateEntity);

            assertEquals(200, response.getStatus());
            assertEquals(fechaOriginal, entity.getFechaAsignacion());
            verify(pruebaJornadaDAO).update(any(PruebaJornada.class));
        }

        @Test
        void retorna200ActualizaFechaAsignacion() {
            OffsetDateTime nuevaFecha = OffsetDateTime.now().plusDays(5);
            PruebaJornada updateEntity = new PruebaJornada();
            updateEntity.setFechaAsignacion(nuevaFecha);

            when(pruebaJornadaDAO.findById(id)).thenReturn(entity);
            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);
            when(pruebaJornadaDAO.update(any(PruebaJornada.class))).thenAnswer(inv -> inv.getArgument(0));

            Response response = resource.update(idPrueba, idJornada, id, updateEntity);

            assertEquals(200, response.getStatus());
            PruebaJornada updated = (PruebaJornada) response.getEntity();
            assertEquals(nuevaFecha, updated.getFechaAsignacion());
        }

        @Test
        void retorna422IdPruebaNulo() {
            Response response = resource.update(null, idJornada, id, entity);
            assertEquals(422, response.getStatus());
            assertEquals("idPrueba,idJornada,id,entity", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna422IdJornadaNulo() {
            Response response = resource.update(idPrueba, null, id, entity);
            assertEquals(422, response.getStatus());
            assertEquals("idPrueba,idJornada,id,entity", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna422IdNulo() {
            Response response = resource.update(idPrueba, idJornada, null, entity);
            assertEquals(422, response.getStatus());
            assertEquals("idPrueba,idJornada,id,entity", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna422EntityNulL() {
            Response response = resource.update(idPrueba, idJornada, id, null);
            assertEquals(422, response.getStatus());
            assertEquals("idPrueba,idJornada,id,entity", response.getHeaderString("Missing-parameter"));
            verifyNoInteractions(pruebaJornadaDAO);
        }

        @Test
        void retorna404NoExisteRegistro() {
            when(pruebaJornadaDAO.findById(id)).thenReturn(null);
            Response response = resource.update(idPrueba, idJornada, id, entity);
            assertEquals(404, response.getStatus());
            assertEquals("Record not found", response.getHeaderString("Not-found"));
            verify(pruebaJornadaDAO).findById(id);
        }

        @Test
        void retorna404PruebaNoExiste() {
            entity.setIdPrueba(null);
            when(pruebaJornadaDAO.findById(id)).thenReturn(entity);
            Response response = resource.update(idPrueba, idJornada, id, entity);
            assertEquals(404, response.getStatus());
            assertEquals("Record not found", response.getHeaderString("Not-found"));
            verify(pruebaJornadaDAO).findById(id);
        }

        @Test
        void retorna404ornadaNoExiste() {
            entity.setIdJornada(null);
            when(pruebaJornadaDAO.findById(id)).thenReturn(entity);
            Response response = resource.update(idPrueba, idJornada, id, entity);
            assertEquals(404, response.getStatus());
            assertEquals("Record not found", response.getHeaderString("Not-found"));
            verify(pruebaJornadaDAO).findById(id);
        }

        @Test
        void retorna404IdPruebaNoCoincide() {
            Prueba otraPrueba = new Prueba();
            otraPrueba.setId(UUID.randomUUID());
            entity.setIdPrueba(otraPrueba);
            when(pruebaJornadaDAO.findById(id)).thenReturn(entity);
            Response response = resource.update(idPrueba, idJornada, id, entity);
            assertEquals(404, response.getStatus());
            assertEquals("Record not found", response.getHeaderString("Not-found"));
            verify(pruebaJornadaDAO).findById(id);
        }

        @Test
        void retorna404IdJornadaNoCoincide() {
            Jornada otraJornada = new Jornada();
            otraJornada.setId(UUID.randomUUID());
            entity.setIdJornada(otraJornada);
            when(pruebaJornadaDAO.findById(id)).thenReturn(entity);
            Response response = resource.update(idPrueba, idJornada, id, entity);
            assertEquals(404, response.getStatus());
            assertEquals("Record not found", response.getHeaderString("Not-found"));
            verify(pruebaJornadaDAO).findById(id);
        }

        @Test
        void retorna404NuevaPruebaNoExiste() {
            when(pruebaJornadaDAO.findById(id)).thenReturn(entity);
            when(pruebaDAO.findById(idPrueba)).thenReturn(null);
            Response response = resource.update(idPrueba, idJornada, id, entity);
            assertEquals(404, response.getStatus());
            assertEquals("Prueba or Jornada not found", response.getHeaderString("Not-found"));
            verify(pruebaJornadaDAO).findById(id);
            verify(pruebaDAO).findById(idPrueba);
        }

        @Test
        void retorna404NuevaJornadaNoExiste() {
            when(pruebaJornadaDAO.findById(id)).thenReturn(entity);
            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(jornadaDAO.findById(idJornada)).thenReturn(null);
            Response response = resource.update(idPrueba, idJornada, id, entity);
            assertEquals(404, response.getStatus());
            assertEquals("Prueba or Jornada not found", response.getHeaderString("Not-found"));
            verify(pruebaJornadaDAO).findById(id);
            verify(pruebaDAO).findById(idPrueba);
            verify(jornadaDAO).findById(idJornada);
        }

        @Test
        void retorna404NuevaPruebaYJornadaNoExisten() {
            when(pruebaJornadaDAO.findById(id)).thenReturn(entity);
            when(pruebaDAO.findById(idPrueba)).thenReturn(null);
            when(jornadaDAO.findById(idJornada)).thenReturn(null);
            Response response = resource.update(idPrueba, idJornada, id, entity);
            assertEquals(404, response.getStatus());
            assertEquals("Prueba or Jornada not found", response.getHeaderString("Not-found"));
            verify(pruebaJornadaDAO).findById(id);
            verify(pruebaDAO).findById(idPrueba);
            verify(jornadaDAO).findById(idJornada);
        }

        @Test
        void retorna500DAOFindByIdExcepcion() {
            when(pruebaJornadaDAO.findById(id)).thenThrow(new RuntimeException("DB error"));
            Response response = resource.update(idPrueba, idJornada, id, entity);
            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(pruebaJornadaDAO).findById(id);
        }

        @Test
        void retorna500DAOUpdateExcepcion() {
            when(pruebaJornadaDAO.findById(id)).thenReturn(entity);
            when(pruebaDAO.findById(idPrueba)).thenReturn(prueba);
            when(jornadaDAO.findById(idJornada)).thenReturn(jornada);
            when(pruebaJornadaDAO.update(any(PruebaJornada.class))).thenThrow(new RuntimeException("DB error"));

            Response response = resource.update(idPrueba, idJornada, id, entity);
            assertEquals(500, response.getStatus());
            assertEquals("Cannot access db", response.getHeaderString("Server-exception"));
            verify(pruebaJornadaDAO).findById(id);
            verify(pruebaJornadaDAO).update(any(PruebaJornada.class));
        }
    }
}