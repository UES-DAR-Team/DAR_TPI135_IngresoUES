package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class AspiranteDAO extends IngresoDefaultDataAcces<Aspirante, Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public AspiranteDAO() {
        super(Aspirante.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    @Override
    protected Class<Aspirante> getEntityClass() {
        return Aspirante.class;
    }

    public List<Aspirante> buscarAspirantePorNombre(String nombre, int first, int max) {
        try {
            if (nombre != null && !nombre.isBlank() && first >= 0 && max > 0) {

                TypedQuery<Aspirante> q = em.createNamedQuery(
                        "Aspirante.buscarAspirantePorNombre",
                        Aspirante.class
                );

                q.setParameter("nombre", "%" + nombre.toUpperCase() + "%");
                q.setFirstResult(first);
                q.setMaxResults(max);

                return q.getResultList();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar aspirantes por nombre", ex);
        }

        return List.of();
    }
}
