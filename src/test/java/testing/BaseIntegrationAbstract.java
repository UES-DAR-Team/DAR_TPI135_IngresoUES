package testing;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Clase base abstracta para pruebas de integración y de sistema (E2E).
 *
 * <p>Proporciona la infraestructura común para todas las pruebas que necesitan
 * interactuar con la base de datos PostgreSQL y, opcionalmente, con el servidor
 * OpenLiberty desplegado en un contenedor Docker.</p>
 *
 * <p><b>Características principales:</b></p>
 * <ul>
 *   <li>Configuración automática del cliente HTTP para pruebas de sistema</li>
 *   <li>Inicialización del EntityManagerFactory (patrón Singleton) para pruebas de integración</li>
 *   <li>Verificación del estado de los contenedores Docker</li>
 *   <li>Generación de la URL base del API REST para pruebas E2E</li>
 * </ul>
 *
 */

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ContainerExtension.class)
public abstract class BaseIntegrationAbstract {

    // VARIABLES DE INSTANCIA

    /**
     * Cliente HTTP para realizar peticiones a los endpoints REST.
     * Solo se inicializa para pruebas de sistema (anotadas con {@code @SystemTest}).
     */

    protected Client cliente;

    /**
     * Punto de entrada para construir peticiones HTTP a la API REST.
     * Solo se inicializa para pruebas de sistema (anotadas con {@code @SystemTest}).
     */

    protected WebTarget target;

    /**
     * Fábrica de EntityManagers para interactuar directamente con la base de datos.
     * Implementa el patrón Singleton para ser compartida entre todas las clases de prueba.
     *
     * <p>Se inicializa con la configuración de la base de datos PostgreSQL del contenedor.</p>
     */

    protected static EntityManagerFactory emf;

    // CONFIGURACIÓN INICIAL
    /**
     * Configura el entorno de prueba antes de ejecutar cualquier test.
     *
     * <p><b>Comportamiento:</b></p>
     * <ol>
     *   <li>Obtiene el contenedor de PostgreSQL desde {@link ContainerExtension}</li>
     *   <li>Verifica que el contenedor esté corriendo</li>
     *   <li>Si la clase está anotada con {@code @SystemTest}, inicializa el cliente HTTP</li>
     *   <li>Configura el EntityManagerFactory con la URL de la base de datos</li>
     * </ol>
     *
     * @throws AssertionError si el contenedor de PostgreSQL no está corriendo
     */

    @BeforeAll
    public void initializeClient() {

        // 1. OBTENER Y VERIFICAR CONTENEDOR POSTGRESQL

        PostgreSQLContainer<?> postgres = ContainerExtension.getPostgres();
        //Verificar que el contenedor está corriendo
        assertTrue(postgres.isRunning());

        // 2. INICIALIZAR CLIENTE HTTP PARA PRUEBAS DE SISTEMA

        if(this.getClass().isAnnotationPresent(SystemTest.class)){
            GenericContainer<?> openliberty = ContainerExtension.getOpenLiberty();
            assertTrue(openliberty.isRunning());

            //logs para verificar el despliegue
            System.out.println("=== LIBERTY LOGS ===");
            System.out.println(openliberty.getLogs());
            System.out.println("=== BASE URL: " + getBaseUrl());

            cliente = ClientBuilder.newClient();
            target = cliente.target(getBaseUrl());
            System.out.println("Testing URL: " + getBaseUrl());
        }

        // 3. CONFIGURAR ENTITYMANAGERFactory

        String url = String.format(
                "jdbc:postgresql://%s:%d/ingreso_ues_db",
                postgres.getHost(),
                postgres.getMappedPort(5432)
        );

        Map<String, Object> propiedades = new HashMap<>();
        propiedades.put("jakarta.persistence.jdbc.url", url);
        propiedades.put("jakarta.persistence.jdbc.user", postgres.getUsername());
        propiedades.put("jakarta.persistence.jdbc.password", postgres.getPassword());
        propiedades.put("jakarta.persistence.jdbc.driver", postgres.getDriverClassName());

        // Crear EntityManagerFactory solo una vez (patron Singleton)

        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("IngresoPUIT", propiedades);
        }
        System.out.println("URL PostgreSQL: " + url);
    }

      // METODOS AUXILIARES


    /**
     * Construye la URL base del API REST para las pruebas de sistema.
     *
     * @return URL base del API REST, o {@code null} si la clase no es de sistema
     */
    protected String getBaseUrl() {
        if (this.getClass().isAnnotationPresent(SystemTest.class)) {
            String hostliberty = ContainerExtension.getOpenLiberty().getHost();
            return String.format("http://%s:%d/DAR_TPI135_IngresoUES-1.0-SNAPSHOT/v1/",
                    hostliberty,
                    ContainerExtension.getOpenLiberty().getMappedPort(9080));
        }
        return null;
    }

}