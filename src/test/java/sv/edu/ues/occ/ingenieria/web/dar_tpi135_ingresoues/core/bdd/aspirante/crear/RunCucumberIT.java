package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.bdd.aspirante.crear;

import org.junit.platform.suite.api.*;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;


@Suite
@IncludeEngines("cucumber")
@SelectPackages("bdd.aspirante.crear")
@ConfigurationParameter(key =GLUE_PROPERTY_NAME, value = "sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.bdd.aspirante.crear")
public class RunCucumberIT {
}
