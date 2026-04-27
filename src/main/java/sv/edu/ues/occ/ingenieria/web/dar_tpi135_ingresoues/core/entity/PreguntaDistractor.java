package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.Date;

@Entity
@Table(name = "pregunta_distractor", schema = "public")
@NamedQueries({
        @NamedQuery(name = "PreguntaDistractor.findByIdPregunta",
                query = "SELECT pd FROM PreguntaDistractor pd WHERE pd.idPregunta.id = :idPregunta"),
        @NamedQuery(name = "PreguntaDistractor.findByIdDistractor",
                query = "SELECT pd FROM PreguntaDistractor pd WHERE pd.idDistractor.id = :idDistractor")
})
public class PreguntaDistractor {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id_pregunta_distractor", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pregunta", nullable = false)
    private Pregunta idPregunta;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_distractor", nullable = false)
    private Distractor idDistractor;

    @Column(name = "es_correcto")
    private Boolean esCorrecto = false;

    //quitar not null, cambiar a Date, insertable false, updatable false
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_asignacion", insertable = false, updatable = false)
    private Date fechaAsignacion;

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

    public Distractor getIdDistractor() {
        return idDistractor;
    }

    public void setIdDistractor(Distractor idDistractor) {
        this.idDistractor = idDistractor;
    }

    public Boolean getEsCorrecto() {
        return esCorrecto;
    }

    public void setEsCorrecto(Boolean esCorrecto) {
        this.esCorrecto = esCorrecto;
    }

    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

}