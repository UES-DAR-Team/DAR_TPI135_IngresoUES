package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.AspiranteOpcione;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class AspiranteOpcioneDAO extends IngresoDefaultDataAcces<AspiranteOpcione, Object> implements Serializable {

    @PersistenceContext(unitName = "IngresoPU")
    EntityManager em;

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

    public List<AspiranteOpcione> findByAspirante(UUID idAspirante, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (idAspirante == null) {
            throw new IllegalArgumentException("Id de aspirante inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<AspiranteOpcione> q = getEntityManager().createNamedQuery(
                    "AspiranteOpcione.buscarPorAspirante",
                    AspiranteOpcione.class
            );

            q.setParameter("idAspirante", idAspirante);
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            Logger.getLogger(AspiranteOpcioneDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return List.of();
    }

    public List<AspiranteOpcione> findByCodigoPrograma(String codigoPrograma, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (codigoPrograma == null || codigoPrograma.isBlank()) {
            throw new IllegalArgumentException("Código de programa inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<AspiranteOpcione> q = getEntityManager().createNamedQuery(
                    "AspiranteOpcione.findByCodigoPrograma",
                    AspiranteOpcione.class
            );

            q.setParameter("codigoPrograma", codigoPrograma.trim());
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            Logger.getLogger(AspiranteOpcioneDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return List.of();
    }

    public List<AspiranteOpcione> findByNombrePrograma(String nombrePrograma, int first, int max)
            throws IllegalArgumentException, IllegalStateException {

        if (nombrePrograma == null || nombrePrograma.isBlank()) {
            throw new IllegalArgumentException("Nombre de programa inválido");
        }

        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros de paginación inválidos");
        }

        try {
            TypedQuery<AspiranteOpcione> q = getEntityManager().createNamedQuery(
                    "AspiranteOpcione.findByNombrePrograma",
                    AspiranteOpcione.class
            );

            q.setParameter("nombrePrograma", "%" + nombrePrograma.trim().toUpperCase() + "%");
            q.setFirstResult(first);
            q.setMaxResults(max);

            return q.getResultList();

        } catch (Exception ex) {
            Logger.getLogger(AspiranteOpcioneDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return List.of();
    }


    public Long countByAspirante(UUID idAspirante)
            throws IllegalArgumentException, IllegalStateException {

        if (idAspirante == null) {
            throw new IllegalArgumentException("Id de aspirante inválido");
        }

        try {
            TypedQuery<Long> q = getEntityManager().createNamedQuery(
                    "AspiranteOpcione.countByAspirante",
                    Long.class
            );

            q.setParameter("idAspirante", idAspirante);

            return q.getSingleResult();

        } catch (Exception ex) {
            Logger.getLogger(AspiranteOpcioneDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return 0L;
    }
}