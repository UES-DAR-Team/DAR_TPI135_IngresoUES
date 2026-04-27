package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.bdd.confCucumber;

import static io.cucumber.junit.platform.engine.Constants.*;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("bdd")
@ConfigurationParameter(key =GLUE_PROPERTY_NAME, value = "sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.bdd")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
public class RunCucumberIT {
}
