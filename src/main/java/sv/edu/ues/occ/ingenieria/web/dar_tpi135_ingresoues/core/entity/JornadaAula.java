package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "jornada_aula", schema = "public")
@NamedQueries({
        @NamedQuery(
                name = "JornadaAula.buscarPorJornada",
                query = "SELECT ja FROM JornadaAula ja WHERE ja.idJornada.id = :idJornada"
        ),
        @NamedQuery(
                name = "JornadaAula.buscarPorAula",
                query = "SELECT ja FROM JornadaAula ja WHERE ja.idAula.id = :idAula"
        ),
        @NamedQuery(
                name = "JornadaAula.countByJornada",
                query = "SELECT COUNT(ja) FROM JornadaAula ja WHERE ja.idJornada.id = :idJornada"
        ),
        @NamedQuery(
                name = "JornadaAula.countByAula",
                query = "SELECT COUNT(ja) FROM JornadaAula ja WHERE ja.idAula.id = :idAula"
        )
})
public class JornadaAula {
    @Id
    @Column(name = "id_jornada_aula", nullable = false, updatable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_jornada", nullable = false)
    private Jornada idJornada;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_aula", nullable = false)
    private Aula idAula;

    @NotNull
    @Column(name = "fecha_asignacion", nullable = false)
    private OffsetDateTime fechaAsignacion;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Jornada getIdJornada() {
        return idJornada;
    }

    public void setIdJornada(Jornada idJornada) {
        this.idJornada = idJornada;
    }

    public Aula getIdAula() {
        return idAula;
    }

    public void setIdAula(Aula idAula) {
        this.idAula = idAula;
    }

    public OffsetDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(OffsetDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }
}