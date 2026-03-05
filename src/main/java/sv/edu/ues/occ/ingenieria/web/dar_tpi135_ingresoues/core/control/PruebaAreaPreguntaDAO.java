package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PruebaAreaPregunta;

import java.io.Serializable;

@Stateless
@LocalBean
public class PruebaAreaPreguntaDAO extends IngresoDefaultDataAcces<PruebaAreaPregunta, Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public PruebaAreaPreguntaDAO() {
        super(PruebaAreaPregunta.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<PruebaAreaPregunta> getEntityClass() {
        return PruebaAreaPregunta.class;
    }
}
