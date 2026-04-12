package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.Aspirante;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class AspiranteDAO extends IngresoDefaultDataAcces<Aspirante, Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    EntityManager em;

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

    public List<Aspirante> findByNombre(String nombre, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Nombre inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<Aspirante> q = getEntityManager().createNamedQuery(
                    "Aspirante.buscarAspirantePorNombre",
                    Aspirante.class
            );

            q.setParameter("nombre", "%" + nombre.trim().toUpperCase() + "%");
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar aspirantes por nombre", ex);
        }
    }

    public List<Aspirante> findActivos(int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<Aspirante> q = getEntityManager().createNamedQuery(
                    "Aspirante.findActivos",
                    Aspirante.class
            );

            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar aspirantes activos", ex);
        }
    }

    public List<Aspirante> findByDocumento(String documento, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("Documento inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<Aspirante> q = getEntityManager().createNamedQuery(
                    "Aspirante.findByDocumento",
                    Aspirante.class
            );

            q.setParameter("documento", documento.trim());
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar aspirantes por documento", ex);
        }
    }

    public List<Aspirante> findByEstado(Boolean estado, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (estado == null) {
            throw new IllegalArgumentException("Estado inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<Aspirante> q = getEntityManager().createNamedQuery(
                    "Aspirante.findByEstado",
                    Aspirante.class
            );

            q.setParameter("estado", estado);
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar aspirantes por estado", ex);
        }
    }

    public Long countByNombre(String nombre)
            throws IllegalArgumentException, IllegalStateException {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Nombre inválido");
        }

        try {
            TypedQuery<Long> q = getEntityManager().createNamedQuery(
                    "Aspirante.countByNombre",
                    Long.class
            );

            q.setParameter("nombre", "%" + nombre.trim().toUpperCase() + "%");

            return q.getSingleResult();

        } catch (Exception ex) {
            throw new IllegalStateException("Error al contar aspirantes por nombre", ex);
        }
    }
}