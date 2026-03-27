package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Stateless
@LocalBean
public class AspirantePruebaDAO extends IngresoDefaultDataAcces<AspirantePrueba, Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public AspirantePruebaDAO() {
        super(AspirantePrueba.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<AspirantePrueba> getEntityClass() {
        return AspirantePrueba.class;
    }

    public List<AspirantePrueba> buscarPorAspirante(UUID idAspirante, int first, int max) {
        try {
            if (idAspirante != null && first >= 0 && max > 0) {

                TypedQuery<AspirantePrueba> q = em.createNamedQuery(
                        "AspirantePrueba.buscarPorAspirante",
                        AspirantePrueba.class
                );

                q.setParameter("idAspirante", idAspirante);
                q.setFirstResult(first);
                q.setMaxResults(max);

                return q.getResultList();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar pruebas por aspirante", ex);
        }

        return List.of();
    }

    public List<AspirantePrueba> buscarPorPrueba(Integer idPrueba, int first, int max) {
        try {
            if (idPrueba != null && first >= 0 && max > 0) {

                TypedQuery<AspirantePrueba> q = em.createNamedQuery(
                        "AspirantePrueba.buscarPorPrueba",
                        AspirantePrueba.class
                );

                q.setParameter("idPrueba", idPrueba);
                q.setFirstResult(first);
                q.setMaxResults(max);

                return q.getResultList();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar aspirantes por prueba", ex);
        }

        return List.of();
    }

}
