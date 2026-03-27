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
public class AspiranteOpcioneDAO extends IngresoDefaultDataAcces<AspiranteOpcione, Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public AspiranteOpcioneDAO() {
        super(AspiranteOpcione.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<AspiranteOpcione> getEntityClass() {
        return AspiranteOpcione.class;
    }

    public List<AspiranteOpcione> buscarPorAspirante(UUID idAspirante, int first, int max) {
        try {
            if (idAspirante != null && first >= 0 && max > 0) {

                TypedQuery<AspiranteOpcione> q = em.createNamedQuery(
                        "AspiranteOpcione.buscarPorAspirante",
                        AspiranteOpcione.class
                );

                q.setParameter("idAspirante", idAspirante);
                q.setFirstResult(first);
                q.setMaxResults(max);

                return q.getResultList();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar opciones del aspirante", ex);
        }

        return List.of();
    }
}
