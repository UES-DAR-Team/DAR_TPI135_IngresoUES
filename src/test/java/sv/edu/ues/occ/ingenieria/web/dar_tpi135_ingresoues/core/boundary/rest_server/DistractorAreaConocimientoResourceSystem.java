package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.boundary.rest_server;

import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.AreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.DistractorAreaConocimientoDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control.DistractorDAO;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AreaConocimiento;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Distractor;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.DistractorAreaConocimiento;
import testing.BaseIntegrationAbstract;
import testing.ContainerExtension;
import testing.NeedsLiberty;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ContainerExtension.class)
@NeedsLiberty
public class DistractorAreaConocimientoResourceSystem extends BaseIntegrationAbstract {
}