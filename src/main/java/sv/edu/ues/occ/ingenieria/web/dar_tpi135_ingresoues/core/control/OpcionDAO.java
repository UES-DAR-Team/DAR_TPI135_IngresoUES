package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Opcion;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class OpcionDAO extends IngresoDefaultDataAcces<Opcion> implements Serializable {
    @PersistenceContext(unitName = "IngresoPU")
    EntityManager em;

    public OpcionDAO() {
        super(Opcion.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Opcion> getEntityClass() {
        return Opcion.class;
    }

    public List<Opcion> findByNameLike(final String name, int first, int max)
    throws IllegalArgumentException, IllegalStateException {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("parametro invalido: name");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parametros invalidos: first, max");
        }
        try {
            TypedQuery<Opcion> q = em.createNamedQuery("Opcion.findByNameLike", Opcion.class)
                    .setParameter("name", "%" + name.trim().toUpperCase() + "%")
                    .setFirstResult(first)
                    .setMaxResults(max);
            return q.getResultList();

        } catch (RuntimeException e) {
            throw new IllegalStateException("Error de sistema en la ejecucion de query", e);
        }

    }

    public List<Opcion> findByCodigoLike(final String codigo, int first, int max)
    throws IllegalArgumentException, IllegalStateException {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("parametro invalido: codigo");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parametros invalidos: first, max");
        }
        try {
            TypedQuery<Opcion> q = em.createNamedQuery("Opcion.findByCodigoLike", Opcion.class)
                    .setParameter("codigo", "%" + codigo.trim().toUpperCase() + "%")
                    .setFirstResult(first)
                    .setMaxResults(max);
            return q.getResultList();

        } catch (RuntimeException e) {
            throw new IllegalStateException("Error de sistema en la ejecucion de query", e);
        }

    }


}
