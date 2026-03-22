package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Prueba;

import java.io.Serializable;
import java.util.List;

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

    //devuelve todas las pruebas que estan activas
    public List<Prueba> findActivas(int first, int max) {
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }
        try {
            TypedQuery<Prueba> q = getEntityManager().createNamedQuery(
                    "Prueba.findActivas", Prueba.class);
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar pruebas activas", ex);
        }
    }

    //para buscar por nombres
    public List<Prueba> findByNombre(String nombre, int first, int max) {
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

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar pruebas por nombre", ex);
        }
    }
}