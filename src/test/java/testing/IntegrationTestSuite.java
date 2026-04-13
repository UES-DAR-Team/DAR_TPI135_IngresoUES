package testing;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server.*;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.*;

@Suite
@SelectClasses({
        AreaConocimientoDAOIT.class,
        DistractorAreaConocimientoDAOIT.class,
        DistractorDAOIT.class,
        PreguntaAreaDAOIT.class,
        PreguntaDAOIT.class,
        PreguntaDistractorDAOIT.class,
        AreaConocimientoResourceSystem.class,
        DistractorAreaConocimientoResourceIT.class,
        DistractorResourceIT.class,
        PreguntaAreaConocimientoResourceIT.class,
        PreguntaDistractorResourceIT.class,
        PreguntaResourceIT.class

})
public class IntegrationTestSuite {
}
