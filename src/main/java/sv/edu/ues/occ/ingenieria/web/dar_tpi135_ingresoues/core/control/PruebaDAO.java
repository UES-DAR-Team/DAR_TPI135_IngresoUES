package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class PruebaDAO extends IngresoDefaultDataAcces<Prueba,Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    private EntityManager em;

    public PruebaDAO() {
        super(Prueba.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Prueba> getEntityClass() {
        return Prueba.class;
    }

    public List<Prueba> findActivas(final int first, final int max)
            throws IllegalArgumentException, IllegalStateException {
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<Prueba> q = getEntityManager().createNamedQuery(
                    "Prueba.findActivas", Prueba.class);
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        }
        catch (Exception ex) {
            Logger.getLogger(PruebaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();

    }

    public List<Prueba> findByNombre(final String nombre, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Nombre inválido");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<Prueba> q = getEntityManager().createNamedQuery(
                    "Prueba.findByNombre", Prueba.class);
            q.setParameter("nombre", nombre.trim());
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();
        }
        catch (Exception ex) {
            Logger.getLogger(PruebaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();

    }
}