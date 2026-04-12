package testing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Indica que la anotación estará disponible en tiempo de ejecución
// Esto permite que clases como ContainerExtension puedan detectarla
@Retention(RetentionPolicy.RUNTIME)

// Indica que la anotación solo se puede usar sobre clases
@Target(ElementType.TYPE)

// Define una anotación personalizada llamada SystemTest
// Se usa como una etiqueta para identificar pruebas de sistema (E2E)
public @interface SystemTest {

    // No tiene atributos es una "marker annotation"
    // Solo sirve para marcar clases
}

/*
 * Esta anotación se utiliza para identificar clases que representan
 * pruebas de sistema (End-to-End).
 *
 * Permite que el sistema de pruebas (por ejemplo, ContainerExtension)
 * detecte estas clases y configure el entorno adecuado, como:
 * Levantar OpenLiberty
 * Configurar base de datos E2E
 */