package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.bdd.aspirante.crear.stepsefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import testing.CucumberContainerExtension;
import testing.CucumberTest;

import org.testcontainers.containers.GenericContainer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@CucumberTest
@ExtendWith(CucumberContainerExtension.class)
public class CrearStepDefitions {


    @SuppressWarnings("resource")
    @Given("se tiene un servidor contenido con la aplicacion desplegada")
    public void se_tiene_un_servidor_contenido_con_la_aplicacion_desplegada() {
        //verificar que el servidor esta corriendo y la aplicacion esta desplegada
        // Obtener el contenedor de OpenLiberty desde la extensión de test
        GenericContainer<?> openLiberty = CucumberContainerExtension.getOpenLiberty();

        // Verificaciones básicas del contenedor
        Assertions.assertNotNull(openLiberty, "El contenedor de OpenLiberty no está inicializado");
        Assertions.assertTrue(openLiberty.isRunning(), "El contenedor de OpenLiberty no está corriendo");

        // Intentar consultar la aplicación desplegada mediante HTTP
        String host = openLiberty.getHost();
        int port = openLiberty.getMappedPort(9080);

        // Asumimos que la aplicación está desplegada bajo el contexto del WAR; ajusta si usas otro contexto
        String context = "DAR_TPI135_IngresoUES-1.0-SNAPSHOT";
        String urlStr = String.format("http://%s:%d/%s/index.jsp", host, port, context);

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlStr))
                    .GET()
                    .timeout(Duration.ofSeconds(5))
                    .build();

            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            int code = response.statusCode();

            Assertions.assertTrue(code >= 200 && code < 400,
                    "La aplicación no respondió con estado exitoso. HTTP: " + code + " al consultar: " + urlStr);
        } catch (Exception e) {
            Assertions.fail("No se pudo conectar a la aplicación desplegada en " + urlStr + ": " + e.getMessage());
        }

        System.out.println("as intended");
    }

    @When("puedo crear un aspirante")
    public void puedo_crear_un_aspirante() {
        System.out.println("and running");
    }
    @When("puedo asociarle a una opcion de carrera, por ejemplo I30515")
    public void puedo_asociarle_a_una_opcion_de_carrera_por_ejemplo_I30515() {
        System.out.println("and running");
    }

    @Then("puedo consultar el perfil del aspirante recien creado")
    public void puedo_consultar_el_perfil_del_aspirante_recien_creado() {
        System.out.println("with finish");
    }

    @Then("verificar la opcion de carrera a la que fue asociado")
    public void verificar_la_opcion_de_carrera_a_la_que_fue_asociado() {
        System.out.println("with finish");
    }

}
