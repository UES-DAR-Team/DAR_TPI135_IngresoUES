package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity.TurnoJornada;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Stateless
@LocalBean
public class TurnoJornadaDAO extends IngresoDefaultDataAcces<TurnoJornada, Object> implements Serializable {
    @PersistenceContext(unitName = "IngresoPU")
    EntityManager em;

    public TurnoJornadaDAO() { super(TurnoJornada.class); }

    @Override
    public EntityManager getEntityManager() { return em; }

    @Override
    protected Class<TurnoJornada> getEntityClass() { return TurnoJornada.class; }

    public List<TurnoJornada> findTurnoByIdJornada(final UUID idJornada, int first, int max)
            throws IllegalArgumentException, IllegalStateException {
        if (idJornada == null) {
            throw new IllegalArgumentException("Parametro invalido: idJornada");
        }
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parametros invalidos: first, max");
        }
        try {
            TypedQuery<TurnoJornada> q = em.createNamedQuery("TurnoJornada.findByIdJornada", TurnoJornada.class)
                    .setParameter("idJornada", idJornada)
                    .setFirstResult(first)
                    .setMaxResults(max);
            return q.getResultList();
        } catch (RuntimeException e) {
            throw new IllegalStateException("Error de sistema en la ejecucion de query", e);
        }
    }


        public List<TurnoJornada> findJornadaByIdTurno(final UUID idTurno, int first, int max)
                throws IllegalArgumentException, IllegalStateException {
            if (idTurno == null) {
                throw new IllegalArgumentException("Parametro invalido: idTurno");
            }
            if (first < 0 || max <= 0) {
                throw new IllegalArgumentException("Parametros invalidos: first, max");
            }
            try {
                 TypedQuery<TurnoJornada> q = em.createNamedQuery("TurnoJornada.findByIdTurno", TurnoJornada.class)
                        .setParameter("idTurno", idTurno)
                        .setFirstResult(first)
                        .setMaxResults(max);
                return q.getResultList();
            } catch (RuntimeException e) {
                throw new IllegalStateException("Error de sistema en la ejecucion de query", e);
            }
        }


}
