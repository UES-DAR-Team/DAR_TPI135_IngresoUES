package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspiranteOpcion;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Stateless
@LocalBean
public class AspiranteOpcionDAO extends IngresoDefaultDataAcces<AspiranteOpcion> implements Serializable {
    @PersistenceContext(unitName="IngresoPU")
    EntityManager em;
    
    public AspiranteOpcionDAO() {
        super(AspiranteOpcion.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<AspiranteOpcion> getEntityClass() {
        return AspiranteOpcion.class;
    }

    public List<AspiranteOpcion> findOpcionByIdAspirante(final UUID idAspirante, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        if(idAspirante == null){
            throw new IllegalArgumentException("Parametro invalido: idAspirante");
        }
        if (first < 0 || max<=0){
            throw new IllegalArgumentException("Parametros invalidos: first, max");
        }
        try {
            TypedQuery<AspiranteOpcion> q = em.createNamedQuery("AspiranteOpcion.findByIdAspirante", AspiranteOpcion.class)
                    .setParameter("idAspirante", idAspirante)
                    .setFirstResult(first)
                    .setMaxResults(max);
            return q.getResultList();
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Error de sistema en la ejecucion de query", ex);
        }
    }

    public List<AspiranteOpcion> findAspiranteByIdOpcion(final UUID idOpcion, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        if(idOpcion == null){
            throw new IllegalArgumentException("Parametro invalido: idOpcion");
        }
        if (first < 0 || max<=0){
            throw new IllegalArgumentException("Parametros invalidos: first, max");
        }
        try {
            TypedQuery<AspiranteOpcion> q = em.createNamedQuery("AspiranteOpcion.findByIdOpcion", AspiranteOpcion.class)
                    .setParameter("idOpcion", idOpcion)
                    .setFirstResult(first)
                    .setMaxResults(max);
            return q.getResultList();
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Error de sistema en la ejecucion de query", ex);
        }
    }

}
