package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "prueba", schema = "public")
@NamedQueries({
        @NamedQuery(name = "Prueba.findActivas", query = "SELECT p FROM Prueba p WHERE p.activo = true"),
        @NamedQuery(name = "Prueba.findByNombre", query = "SELECT p FROM Prueba p WHERE LOWER(p.nombrePrueba) LIKE LOWER(CONCAT('%', :nombre, '%'))"),
})
public class Prueba {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_prueba", nullable = false)
    private UUID id;

    @Size(max = 250)
    @NotNull
    @Column(name = "nombre_prueba", nullable = false, length = 250)
    private String nombrePrueba;

    @NotNull
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;

    @NotNull
    @Column(name = "activo", nullable = false)
    private Boolean activo = false;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombrePrueba() {
        return nombrePrueba;
    }

    public void setNombrePrueba(String nombrePrueba) {
        this.nombrePrueba = nombrePrueba;
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