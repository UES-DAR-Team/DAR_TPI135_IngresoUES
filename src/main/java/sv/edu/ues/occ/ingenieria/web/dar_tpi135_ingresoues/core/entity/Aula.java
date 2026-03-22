package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

@Entity
@Table(name = "aula", schema = "public")
@NamedQueries({
        @NamedQuery(name = "Aula.findByCapacidadMin", query = "SELECT a FROM Aula a WHERE a.capacidad >= :capacidad"),
        @NamedQuery(name = "Aula.findByNombre", query = "SELECT a FROM Aula a WHERE LOWER(a.nombreAula) LIKE LOWER(CONCAT('%', :nombre, '%'))"),
        @NamedQuery(name = "Aula.findActivos", query = "SELECT a FROM Aula a WHERE a.activo = true")
})
public class Aula {
    @Id
    @Column(name = "id_aula", nullable = false)
    private Integer id;

    @Size(max = 150)
    @NotNull
    @Column(name = "nombre_aula", nullable = false, length = 150)
    private String nombreAula;

    @Column(name = "capacidad")
    private Integer capacidad;

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

    public String getNombreAula() {
        return nombreAula;
    }

    public void setNombreAula(String nombreAula) {
        this.nombreAula = nombreAula;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
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