package testing;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ContainerExtension.class)
public abstract class BaseIntegrationAbstract {


    // variables para E2A
 protected Client cliente;
 protected WebTarget target;

 //EntityManagerFactory con el patron SINGLETON
    protected static EntityManagerFactory emf;

    @BeforeAll
    public void initializeClient() {

        PostgreSQLContainer<?> postgres = ContainerExtension.getPostgres();
        //Verificar que el contenedor está corriendo
        assertTrue(postgres.isRunning());

        //(solo si son necesarios)
        // System.out.println("=== PostgreSQL Logs ===");
        // System.out.println(postgres.getLogs());

        //OPENLIBERTY (logica con el servidro)

        if(this.getClass().isAnnotationPresent(SystemTest.class)){
            GenericContainer<?> openliberty = ContainerExtension.getOpenLiberty();
            assertTrue(openliberty.isRunning());

            cliente = ClientBuilder.newClient();
            target = cliente.target(getBaseUrl());
            System.out.println("Testing URL: " + getBaseUrl());

            //si falla la construccion del cliente, se puede intentar con estas opciones para evitar redirecciones automáticas
//            cliente = ClientBuilder.newClient()
//                    .property("jersey.config.client.followRedirects", false)
//                    // Resteasy:
//                    .property("resteasy.disable.follow.redirect", true);

//            cliente = ClientBuilder.newBuilder()
//                    .register(new jakarta.ws.rs.ext.ExceptionMapper<Exception>() {
//                        @Override
//                        public Response toResponse(Exception exception) {
//                            return null;
//                        }
//                    })
//                    .build();

//            target = cliente.target(getBaseUrl());
//            System.out.println("Testing URL: " + getBaseUrl());

        }


        // Configuracion de la BD
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

        //Crear Entity Manager Factory solo una vez
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("IngresoPUIT", propiedades);
        }
        System.out.println("URL PostgreSQL: " + url);
    }

    // Metodo para las pruebas de sistemas
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