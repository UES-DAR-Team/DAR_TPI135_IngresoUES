package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

@Entity
@Table(name = "prueba_area", schema = "public")
@NamedQueries({
        @NamedQuery(name = "PruebaArea.findByPrueba", query = "SELECT pa FROM PruebaArea pa WHERE pa.idPrueba.id = :idPrueba"),
        @NamedQuery(name = "PruebaArea.findByAreaConocimiento", query = "SELECT pa FROM PruebaArea pa WHERE pa.idAreaConocimiento.id = :idAreaConocimiento"),
        @NamedQuery(name = "PruebaArea.findByNumPreguntasMin", query = "SELECT pa FROM PruebaArea pa WHERE pa.numPreguntas >= :numPreguntas")
})
public class PruebaArea {
    @Id
    @Column(name = "id_prueba_area", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_prueba", nullable = false)
    private Prueba idPrueba;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_area_conocimiento", nullable = false)
    private AreaConocimiento idAreaConocimiento;

    @Column(name = "num_preguntas")
    private Short numPreguntas;

    @NotNull
    @Column(name = "fecha_asignacion", nullable = false)
    private OffsetDateTime fechaAsignacion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Prueba getIdPrueba() {
        return idPrueba;
    }

    public void setIdPrueba(Prueba idPrueba) {
        this.idPrueba = idPrueba;
    }

    public AreaConocimiento getIdAreaConocimiento() {
        return idAreaConocimiento;
    }

    public void setIdAreaConocimiento(AreaConocimiento idAreaConocimiento) {
        this.idAreaConocimiento = idAreaConocimiento;
    }

    public Short getNumPreguntas() {
        return numPreguntas;
    }

    public void setNumPreguntas(Short numPreguntas) {
        this.numPreguntas = numPreguntas;
    }

    public OffsetDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(OffsetDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

}