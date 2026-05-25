package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;


import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Turno;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class TurnoDAO extends IngresoDefaultDataAcces<Turno, Object> implements Serializable {
    @PersistenceContext(unitName = "IngresoPU")
    EntityManager em;

    public TurnoDAO() {super(Turno.class);}

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Turno> getEntityClass() {
        return Turno.class;
    }

    public List<Turno> findByNameLike(final String name, int first, int max)
    throws IllegalArgumentException, IllegalStateException {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("parametro invalido: name");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parametros invalidos: first, max");
        }
        try {
            TypedQuery<Turno> q = em.createNamedQuery("Turno.findByNameLike", Turno.class)
                    .setParameter("name", "%" + name.trim().toUpperCase() + "%")
                    .setFirstResult(first)
                    .setMaxResults(max);
            return q.getResultList();

        } catch (RuntimeException e) {
            throw new IllegalStateException("Error de sistema en la ejecucion de query", e);
        }
    }
}
