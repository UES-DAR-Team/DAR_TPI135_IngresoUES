package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

@Entity
@Table(name = "pregunta_area_conocimiento", schema = "public")
@NamedQueries({
        @NamedQuery(name = "PreguntaAreaConocimiento.findByIdAreaConocimiento",
                query = "SELECT pac FROM PreguntaAreaConocimiento pac WHERE pac.idAreaConocimiento.id = :idAreaConocimiento"),
        @NamedQuery(name = "PreguntaAreaConocimiento.findByIdPregunta",
                query = "SELECT pac FROM PreguntaAreaConocimiento pac WHERE pac.idPregunta.id = :idPregunta")

})
public class PreguntaAreaConocimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pregunta_area_conocimiento", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pregunta", nullable = false)
    private Pregunta idPregunta;

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

    public Pregunta getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(Pregunta idPregunta) {
        this.idPregunta = idPregunta;
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