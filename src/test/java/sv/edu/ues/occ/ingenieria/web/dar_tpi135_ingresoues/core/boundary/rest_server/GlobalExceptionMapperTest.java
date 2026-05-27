package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.persistence.*;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionMapperTest {

    private GlobalExceptionMapper exceptionMapper;

    @BeforeEach
    void setUp() {
        exceptionMapper = new GlobalExceptionMapper();
    }

    @Nested
    class ManejoDeExcepcionesJPA {

        @Test
        void mapeaEntityExistsException() {
            EntityExistsException exception = new EntityExistsException("Ya existe");

            Response response = exceptionMapper.toResponse(exception);

            assertEquals(500, response.getStatus());
            assertEquals("DB-Error: Entity already exists", response.getHeaderString("Server-exception"));
        }

        @Test
        void mapeaOptimisticLockException() {
            OptimisticLockException exception = new OptimisticLockException("Conflicto de versión");

            Response response = exceptionMapper.toResponse(exception);

            assertEquals(500, response.getStatus());
            assertEquals("DB-Error: Concurrent modification detected", response.getHeaderString("Server-exception"));
        }

        @Test
        void mapeaTransactionRequiredException() {
            TransactionRequiredException exception = new TransactionRequiredException("No hay transacción");

            Response response = exceptionMapper.toResponse(exception);

            assertEquals(500, response.getStatus());
            assertEquals("DB-Error: No active transaction", response.getHeaderString("Server-exception"));
        }

        @Test
        void mapeaNoResultException() {
            NoResultException exception = new NoResultException("No se encontraron registros");

            Response response = exceptionMapper.toResponse(exception);

            assertEquals(500, response.getStatus());
            assertEquals("DB-Error: No result found", response.getHeaderString("Server-exception"));
        }

        @Test
        void mapeaPersistenceExceptionGenerica() {
            PersistenceException exception = new PersistenceException("Error raro de persistencia");

            Response response = exceptionMapper.toResponse(exception);

            assertEquals(500, response.getStatus());
            assertEquals("DB-Error: Error raro de persistencia", response.getHeaderString("Server-exception"));
        }
    }

    @Nested
    class ManejoDeExcepcionesDeBaseDeDatosYRootCause {

        @Test
        void extraeSQLExceptionComoCausaRaiz() {
            SQLException sqlException = new SQLException("Violacion de constraint", "23505");
            PersistenceException persistenceException = new PersistenceException(sqlException);
            RuntimeException runtimeException = new RuntimeException("Excepción de Jakarta", persistenceException);

            Response response = exceptionMapper.toResponse(runtimeException);

            assertEquals(500, response.getStatus());
            assertEquals("DB-Error: SQLState=23505 - Violacion de constraint", response.getHeaderString("Server-exception"));
        }
    }

    @Nested
    class ManejoDeExcepcionesGenericasYVacias {

        @Test
        void mapeaNullPointerExceptionOInesperado() {
            NullPointerException exception = new NullPointerException("Valor nulo inesperado");

            Response response = exceptionMapper.toResponse(exception);

            assertEquals(500, response.getStatus());
            assertEquals("Unexpected error: NullPointerException - Valor nulo inesperado", response.getHeaderString("Server-exception"));
        }

        @Test
        void manejaExcepcionSinMensajeParaEvitarNulos() {
            NullPointerException exception = new NullPointerException();

            Response response = exceptionMapper.toResponse(exception);

            assertEquals(500, response.getStatus());
            assertEquals("Unexpected error: NullPointerException - null", response.getHeaderString("Server-exception"));
        }
    }

    @Nested
    class SanitizacionDeHeaders {

        @Test
        void eliminaSaltosDeLineaYTabs() {
            RuntimeException exception = new RuntimeException("Error malicioso \r\nHeader-falso: inyectado \t");

            Response response = exceptionMapper.toResponse(exception);

            String headerValue = response.getHeaderString("Server-exception");
            assertFalse(headerValue.contains("\n"));
            assertFalse(headerValue.contains("\r"));
            assertFalse(headerValue.contains("\t"));

            assertEquals("Unexpected error: RuntimeException - Error malicioso   Header-falso: inyectado  ", headerValue);
        }

        @Test
        void eliminaCaracteresNoAscii() {
            RuntimeException exception = new RuntimeException("Error con ñ y á");

            Response response = exceptionMapper.toResponse(exception);

            assertEquals("Unexpected error: RuntimeException - Error con  y ", response.getHeaderString("Server-exception"));
        }

        @Test
        void recortaElMensajeA150CaracteresMaximo() {
            String mensajeLargo = "a".repeat(300);
            RuntimeException exception = new RuntimeException(mensajeLargo);

            Response response = exceptionMapper.toResponse(exception);

            String headerValue = response.getHeaderString("Server-exception");

            assertEquals(150, headerValue.length());
            assertTrue(headerValue.startsWith("Unexpected error: RuntimeException - aaaaa"));
        }
    }
}