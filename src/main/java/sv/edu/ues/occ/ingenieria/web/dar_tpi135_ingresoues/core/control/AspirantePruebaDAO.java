package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspirantePrueba;

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

    public List<AspirantePrueba> findByAspirante(UUID idAspirante, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (idAspirante == null) {
            throw new IllegalArgumentException("Id de aspirante inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<AspirantePrueba> q = getEntityManager().createNamedQuery(
                    "AspirantePrueba.buscarPorAspirante",
                    AspirantePrueba.class
            );

            q.setParameter("idAspirante", idAspirante);
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar pruebas por aspirante", ex);
        }
    }

    public List<AspirantePrueba> findByPrueba(Integer idPrueba, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (idPrueba == null) {
            throw new IllegalArgumentException("Id de prueba inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<AspirantePrueba> q = getEntityManager().createNamedQuery(
                    "AspirantePrueba.buscarPorPrueba",
                    AspirantePrueba.class
            );

            q.setParameter("idPrueba", idPrueba);
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar aspirantes por prueba", ex);
        }
    }

    public Long countByAspirante(UUID idAspirante)
            throws IllegalArgumentException, IllegalStateException {

        if (idAspirante == null) {
            throw new IllegalArgumentException("Id de aspirante inválido");
        }

        try {
            TypedQuery<Long> q = getEntityManager().createQuery(
                    "SELECT COUNT(ap) FROM AspirantePrueba ap WHERE ap.idAspirante.id = :idAspirante",
                    Long.class
            );

            q.setParameter("idAspirante", idAspirante);

            return q.getSingleResult();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al contar pruebas por aspirante", ex);
        }
    }
}