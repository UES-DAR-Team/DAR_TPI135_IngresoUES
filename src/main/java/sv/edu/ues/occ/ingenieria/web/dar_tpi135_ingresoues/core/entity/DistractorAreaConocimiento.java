package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

@Entity
@Table(name = "disctractor_area_conocimiento", schema = "public")
@NamedQueries({
        @NamedQuery(name = "DistractorAreaConocimiento.findByIdDistractor", query = "SELECT dac FROM DistractorAreaConocimiento dac WHERE dac.idDistractor.id = :idDistractor"),
        @NamedQuery(name = "DistractorAreaConocimiento.findByIdAreaConocimiento", query = "SELECT dac FROM DistractorAreaConocimiento dac WHERE dac.idAreaConocimiento.id = :idAreaConocimiento")
})
public class DistractorAreaConocimiento {
    @Id
    @Column(name = "id_distractor_area_conocimiento", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_distractor", nullable = false)
    private Distractor idDistractor;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_area_conocimiento", nullable = false)
    private AreaConocimiento idAreaConocimiento;

    @NotNull
    @Column(name = "fecha_asignacion", nullable = false)
    private OffsetDateTime fechaAsignacion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Distractor getIdDistractor() {
        return idDistractor;
    }

    public void setIdDistractor(Distractor idDistractor) {
        this.idDistractor = idDistractor;
    }

    public AreaConocimiento getIdAreaConocimiento() {
        return idAreaConocimiento;
    }

    public void setIdAreaConocimiento(AreaConocimiento idAreaConocimiento) {
        this.idAreaConocimiento = idAreaConocimiento;
    }

    public OffsetDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(OffsetDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

}