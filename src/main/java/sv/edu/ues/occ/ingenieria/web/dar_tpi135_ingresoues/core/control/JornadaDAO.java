package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Jornada;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class JornadaDAO extends  IngresoDefaultDataAcces<Jornada, Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public JornadaDAO() {
        super(Jornada.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Jornada> getEntityClass() {
        return Jornada.class;
    }

    public List<Jornada> buscarPorNombre(String nombre, int first, int max) {
        try {
            if (nombre != null && !nombre.isBlank() && first >= 0 && max > 0) {

                TypedQuery<Jornada> q = em.createNamedQuery(
                        "Jornada.buscarPorNombre",
                        Jornada.class
                );

                q.setParameter("nombre", "%" + nombre.toUpperCase() + "%");
                q.setFirstResult(first);
                q.setMaxResults(max);

                return q.getResultList();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar jornadas por nombre", ex);
        }

        return List.of();
    }

    public List<Jornada> buscarPorActivo(Boolean activo, int first, int max) {
        try {
            if (activo != null && first >= 0 && max > 0) {

                TypedQuery<Jornada> q = em.createNamedQuery(
                        "Jornada.buscarPorActivo",
                        Jornada.class
                );

                q.setParameter("activo", activo);
                q.setFirstResult(first);
                q.setMaxResults(max);

                return q.getResultList();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar jornadas por estado", ex);
        }

        return List.of();
    }
}
