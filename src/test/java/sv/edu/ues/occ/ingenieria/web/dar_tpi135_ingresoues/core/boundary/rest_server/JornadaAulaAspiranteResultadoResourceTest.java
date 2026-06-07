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
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaAulaAspiranteDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.JornadaAulaAspiranteResultadoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspirante;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspiranteResultado;

import java.net.URI;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JornadaAulaAspiranteResultadoResourceTest {

    @Mock
    JornadaAulaAspiranteResultadoDAO jaarDAO;

    @Mock
    JornadaAulaAspiranteDAO jornadaAulaAspiranteDAO;

    @Mock
    UriInfo uriInfo;

    @Mock
    UriBuilder uriBuilder;

    @InjectMocks
    JornadaAulaAspiranteResultadoResource resource;

    private static final int FIRST = 0;
    private static final int MAX = 10;

    private Integer idJaa;
    private Integer idResultado;
    private JornadaAulaAspirante jaa;
    private JornadaAulaAspiranteResultado resultado;
    private JornadaAulaAspiranteResultado entity;

    @BeforeEach
    void setUp(){
        idJaa = 42;
        idResultado = 7;

        jaa = new JornadaAulaAspirante();
        jaa.setId(idJaa);

        resultado = new JornadaAulaAspiranteResultado();
        resultado.setId(idResultado);
        resultado.setIdJornadaAulaAspirante(jaa);
        resultado.setAprobado(Boolean.TRUE);
        resultado.setPuntajeObtenido(BigDecimal.valueOf(85.5));

        entity = new JornadaAulaAspiranteResultado();
    }

    @Nested
    class FindRange{
        @Test
        void retorna200ConListaYHeader_cuandoParametrosSonValidos(){
            when(jornadaAulaAspiranteDAO.findById(idJaa)).thenReturn(jaa);
            when(jaarDAO.findByJornadaAulaAspirante(idJaa, FIRST, MAX)).thenReturn(List.of(resultado));
            when(jaarDAO.count()).thenReturn(1);

            Response resp = resource.findRange(idJaa, FIRST, MAX);

            assertEquals(200, resp.getStatus());
            List<?> entidad = (List<?>) resp.getEntity();
            assertEquals(1, entidad.size());
            assertEquals("1", resp.getHeaderString("X-Total-Count"));
            verify(jornadaAulaAspiranteDAO).findById(idJaa);
            verify(jaarDAO).findByJornadaAulaAspirante(idJaa, FIRST, MAX);
        }

        @Test
        void retorna422_cuandoIdJaaNulo(){
            Response resp = resource.findRange(null, FIRST, MAX);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornadaAulaAspirante", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaAspiranteDAO, jaarDAO);
        }

        @Test
        void retorna422_cuandoFirstInvalido(){
            Response resp = resource.findRange(idJaa, -1, MAX);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaAspiranteDAO, jaarDAO);
        }

        @Test
        void retorna422_cuandoMaxInvalido(){
            Response resp = resource.findRange(idJaa, FIRST, 0);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaAspiranteDAO, jaarDAO);
        }

        @Test
        void retorna422_cuandoMaxExcedido(){
            Response resp = resource.findRange(idJaa, FIRST, 11);
            assertEquals(422, resp.getStatus());
            assertEquals("first,max", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaAspiranteDAO, jaarDAO);
        }

        @Test
        void retorna404_cuandoJaaNoExiste(){
            when(jornadaAulaAspiranteDAO.findById(idJaa)).thenReturn(null);
            Response resp = resource.findRange(idJaa, FIRST, MAX);
            assertEquals(404, resp.getStatus());
            assertEquals("JornadaAulaAspirante with id " + idJaa + " not found", resp.getHeaderString("Not-found-id"));
            verify(jornadaAulaAspiranteDAO).findById(idJaa);
            verifyNoMoreInteractions(jaarDAO);
        }
    }

    @Nested
    class FindById{
        @Test
        void retorna200ConEntidad_cuandoExiste(){
            when(jaarDAO.findByJornadaAulaAspirante(idJaa, 0, Integer.MAX_VALUE)).thenReturn(List.of(resultado));
            Response resp = resource.findById(idJaa, idResultado);
            assertEquals(200, resp.getStatus());
            assertEquals(resultado, resp.getEntity());
            verify(jaarDAO).findByJornadaAulaAspirante(idJaa, 0, Integer.MAX_VALUE);
        }

        @Test
        void retorna422_cuandoIdJaaNulo(){
            Response resp = resource.findById(null, idResultado);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornadaAulaAspirante", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jaarDAO);
        }

        @Test
        void retorna422_cuandoIdNulo(){
            Response resp = resource.findById(idJaa, null);
            assertEquals(422, resp.getStatus());
            assertEquals("id", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jaarDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra(){
            when(jaarDAO.findByJornadaAulaAspirante(idJaa, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.findById(idJaa, idResultado);
            assertEquals(404, resp.getStatus());
            assertEquals("JornadaAulaAspiranteResultado with id Resultado con id " + idResultado + ", jornadaAulaAspirante " + idJaa + " not found",
                    resp.getHeaderString("Not-found-id"));
            verify(jaarDAO).findByJornadaAulaAspirante(idJaa, 0, Integer.MAX_VALUE);
        }
    }

    @Nested
    class Create{
        @Test
        void retorna201_cuandoValido(){
            entity.setId(null);
            JornadaAulaAspirante body = new JornadaAulaAspirante();
            body.setId(idJaa);
            entity.setIdJornadaAulaAspirante(body);

            when(jornadaAulaAspiranteDAO.findById(idJaa)).thenReturn(jaa);
            when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
            when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
            when(uriBuilder.build()).thenReturn(URI.create("resources/v1/jornadaAulaAspirante/" + idJaa + "/resultado/" + 99));

            doAnswer(inv -> {
                entity.setId(99);
                return null;
            }).when(jaarDAO).create(entity);

            Response resp = resource.create(idJaa, entity, uriInfo);
            assertEquals(201, resp.getStatus());
            verify(jornadaAulaAspiranteDAO).findById(idJaa);
            verify(jaarDAO).create(entity);
        }

        @Test
        void retorna422_cuandoIdJaaNulo(){
            Response resp = resource.create(null, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornadaAulaAspirante", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaAspiranteDAO, jaarDAO);
        }

        @Test
        void retorna422_cuandoEntidadNula(){
            Response resp = resource.create(idJaa, null, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaAspiranteDAO, jaarDAO);
        }

        @Test
        void retorna422_cuandoEntityTieneId(){
            entity.setId(5);
            Response resp = resource.create(idJaa, entity, uriInfo);
            assertEquals(422, resp.getStatus());
            assertEquals("entity.id must be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jornadaAulaAspiranteDAO, jaarDAO);
        }

        @Test
        void retorna404_cuandoJaaNoExiste(){
            entity.setId(null);
            JornadaAulaAspirante body = new JornadaAulaAspirante();
            body.setId(idJaa);
            entity.setIdJornadaAulaAspirante(body);

            when(jornadaAulaAspiranteDAO.findById(idJaa)).thenReturn(null);
            Response resp = resource.create(idJaa, entity, uriInfo);
            assertEquals(404, resp.getStatus());
            assertEquals("JornadaAulaAspirante with id " + idJaa + " not found", resp.getHeaderString("Not-found-id"));
            verify(jornadaAulaAspiranteDAO).findById(idJaa);
            verifyNoMoreInteractions(jaarDAO);
        }
    }

    @Nested
    class Update{
        @Test
        void retorna200_cuandoValido(){
            JornadaAulaAspiranteResultado existing = new JornadaAulaAspiranteResultado();
            existing.setId(idResultado);
            existing.setIdJornadaAulaAspirante(jaa);
            when(jaarDAO.findByJornadaAulaAspirante(idJaa, 0, Integer.MAX_VALUE)).thenReturn(List.of(existing));

            JornadaAulaAspiranteResultado update = new JornadaAulaAspiranteResultado();
            update.setAprobado(Boolean.FALSE);
            update.setPuntajeObtenido(BigDecimal.valueOf(50));

            when(jaarDAO.update(any())).thenReturn(update);

            Response resp = resource.update(idJaa, idResultado, update);
            assertEquals(200, resp.getStatus());
            verify(jaarDAO).findByJornadaAulaAspirante(idJaa, 0, Integer.MAX_VALUE);
            verify(jaarDAO).update(any());
        }

        @Test
        void retorna422_cuandoIdJaaNulo(){
            Response resp = resource.update(null, idResultado, entity);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornadaAulaAspirante", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jaarDAO);
        }

        @Test
        void retorna422_cuandoIdNulo(){
            Response resp = resource.update(idJaa, null, entity);
            assertEquals(422, resp.getStatus());
            assertEquals("id", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jaarDAO);
        }

        @Test
        void retorna422_cuandoEntidadNula(){
            Response resp = resource.update(idJaa, idResultado, null);
            assertEquals(422, resp.getStatus());
            assertEquals("entity must not be null", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jaarDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra(){
            when(jaarDAO.findByJornadaAulaAspirante(idJaa, 0, Integer.MAX_VALUE)).thenReturn(List.of());
            Response resp = resource.update(idJaa, idResultado, entity);
            assertEquals(404, resp.getStatus());
            assertEquals("JornadaAulaAspiranteResultado with id resultado="+idResultado+", jornadaAulaAspirante="+idJaa+" not found",
                    resp.getHeaderString("Not-found-id"));
            verify(jaarDAO).findByJornadaAulaAspirante(idJaa, 0, Integer.MAX_VALUE);
        }
    }

    @Nested
    class Delete{
        @Test
        void retorna204_cuandoValido(){
            JornadaAulaAspiranteResultado existing = new JornadaAulaAspiranteResultado();
            existing.setId(idResultado);
            existing.setIdJornadaAulaAspirante(jaa);
            when(jaarDAO.findById(idResultado)).thenReturn(existing);

            Response resp = resource.delete(idJaa, idResultado);
            assertEquals(204, resp.getStatus());
            verify(jaarDAO).findById(idResultado);
            verify(jaarDAO).delete(existing);
        }

        @Test
        void retorna422_cuandoIdJaaNulo(){
            Response resp = resource.delete(null, idResultado);
            assertEquals(422, resp.getStatus());
            assertEquals("idJornadaAulaAspirante", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jaarDAO);
        }

        @Test
        void retorna422_cuandoIdNulo(){
            Response resp = resource.delete(idJaa, null);
            assertEquals(422, resp.getStatus());
            assertEquals("id", resp.getHeaderString("Missing-parameter"));
            verifyNoInteractions(jaarDAO);
        }

        @Test
        void retorna404_cuandoNoSeEncuentra(){
            when(jaarDAO.findById(idResultado)).thenReturn(null);
            Response resp = resource.delete(idJaa, idResultado);
            assertEquals(404, resp.getStatus());
            assertEquals("Resultado no encontrado para el aspirante indicado", resp.getHeaderString("Not-found"));
            verify(jaarDAO).findById(idResultado);
        }
    }
}

