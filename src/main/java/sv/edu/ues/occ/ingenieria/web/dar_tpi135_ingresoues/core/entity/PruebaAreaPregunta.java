package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

@Entity
@NamedQueries({
        @NamedQuery(name = "PruebaAreaPregunta.findByPruebaArea", query = "SELECT pap FROM PruebaAreaPregunta pap WHERE pap.idPruebaArea.id = :idPruebaArea ORDER BY pap.orden ASC"),
        @NamedQuery(name = "PruebaAreaPregunta.findByPregunta", query = "SELECT pap FROM PruebaAreaPregunta pap WHERE pap.idPregunta.id = :idPregunta ORDER BY pap.idPruebaArea.id ASC, pap.orden ASC")
})
@Table(name = "prueba_area_pregunta", schema = "public")
public class PruebaAreaPregunta {
    @Id
    @Column(name = "id_prueba_area_pregunta", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_prueba_area", nullable = false)
    private PruebaArea idPruebaArea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pregunta", nullable = false)
    private Pregunta idPregunta;

    @Column(name = "orden")
    private Short orden;

    @NotNull
    @Column(name = "fecha_asignacion", nullable = false)
    private OffsetDateTime fechaAsignacion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PruebaArea getIdPruebaArea() {
        return idPruebaArea;
    }

    public void setIdPruebaArea(PruebaArea idPruebaArea) {
        this.idPruebaArea = idPruebaArea;
    }

    public Pregunta getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(Pregunta idPregunta) {
        this.idPregunta = idPregunta;
    }

    public Short getOrden() {
        return orden;
    }

    public void setOrden(Short orden) {
        this.orden = orden;
    }

    public OffsetDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(OffsetDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

}