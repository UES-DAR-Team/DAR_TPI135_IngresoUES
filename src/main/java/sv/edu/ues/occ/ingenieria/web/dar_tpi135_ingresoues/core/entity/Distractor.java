package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

@Entity
@Table(name = "distractor", schema = "public")
public class Distractor {
    @Id
    @Column(name = "id_distractor", nullable = false)
    private Integer id;

    @Size(max = 250)
    @NotNull
    @Column(name = "nombre_distractor", nullable = false, length = 250)
    private String nombreDistractor;

    @NotNull
    @Column(name = "es_correcto", nullable = false)
    private Boolean esCorrecto = false;

    @NotNull
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;

    @NotNull
    @Column(name = "activo", nullable = false)
    private Boolean activo = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreDistractor() {
        return nombreDistractor;
    }

    public void setNombreDistractor(String nombreDistractor) {
        this.nombreDistractor = nombreDistractor;
    }

    public Boolean getEsCorrecto() {
        return esCorrecto;
    }

    public void setEsCorrecto(Boolean esCorrecto) {
        this.esCorrecto = esCorrecto;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

}