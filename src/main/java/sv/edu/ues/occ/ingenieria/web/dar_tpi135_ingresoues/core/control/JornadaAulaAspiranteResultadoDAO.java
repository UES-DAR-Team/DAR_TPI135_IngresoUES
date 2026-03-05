package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.JornadaAulaAspiranteResultado;

import java.io.Serializable;

@Stateless
@LocalBean
public class JornadaAulaAspiranteResultadoDAO extends IngresoDefaultDataAcces<JornadaAulaAspiranteResultado,Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public JornadaAulaAspiranteResultadoDAO() {
        super(JornadaAulaAspiranteResultado.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<JornadaAulaAspiranteResultado> getEntityClass() {
        return JornadaAulaAspiranteResultado.class;
    }
}
