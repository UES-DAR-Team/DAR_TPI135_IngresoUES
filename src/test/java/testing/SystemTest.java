package testing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación de marcador  para identificar pruebas de sistema
 *
 * <p>Esta anotación se utiliza para distinguir las pruebas que requieren un entorno completo
 * de ejecución, incluyendo el servidor OpenLiberty desplegado en un contenedor Docker,
 * de las pruebas de integración que solo necesitan la base de datos PostgreSQL.</p>
 *
 * <p><b>Características:</b></p>
 * <ul>
 *   <li>Es una anotación de tipo {@code @interface} sin atributos (marker annotation)</li>
 *   <li>Tiene visibilidad en tiempo de ejecución mediante {@code @Retention(RUNTIME)}</li>
 *   <li>Solo puede aplicarse a clases mediante {@code @Target(TYPE)}</li>
 * </ul>
 */

@Retention(RetentionPolicy.RUNTIME)
// Indica que la anotación solo se puede usar sobre clases
@Target(ElementType.TYPE)

public @interface SystemTest {
}
