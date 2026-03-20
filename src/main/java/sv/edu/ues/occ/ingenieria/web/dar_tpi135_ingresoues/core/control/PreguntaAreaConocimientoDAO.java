package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.PreguntaAreaConocimiento;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class PreguntaAreaConocimientoDAO extends IngresoDefaultDataAcces<PreguntaAreaConocimiento, Object> implements Serializable {
    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public PreguntaAreaConocimientoDAO() {
        super(PreguntaAreaConocimiento.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<PreguntaAreaConocimiento> getEntityClass() {
        return PreguntaAreaConocimiento.class;
    }

    //buscar pregunta por  idAreaConocimiento
    public List<PreguntaAreaConocimiento> findPreguntaByIdAreaConocimiento(final Integer idAreaConocimiento, int first, int max) {
        //capturar excepciones
        try {
            //validacion de parametros
            if (idAreaConocimiento != null) {
                TypedQuery<PreguntaAreaConocimiento> q = em.createNamedQuery("PreguntaAreaConocimiento.findByIdAreaConocimiento", PreguntaAreaConocimiento.class)
                        .setParameter("idAreaConocimiento", idAreaConocimiento)
                        .setFirstResult(first)
                        .setMaxResults(max);
                return q.getResultList();
            }
        }catch (Exception ex){
            Logger.getLogger(PreguntaAreaConocimientoDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }






}
