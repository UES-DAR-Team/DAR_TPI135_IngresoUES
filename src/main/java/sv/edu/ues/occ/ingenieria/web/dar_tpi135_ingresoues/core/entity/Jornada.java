package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "jornada", schema = "public")
@NamedQueries({
        @NamedQuery(
                name = "Jornada.buscarPorNombre",
                query = "SELECT j FROM Jornada j WHERE UPPER(j.nombreJornada) LIKE :nombre"
        ),
        @NamedQuery(
                name = "Jornada.buscarPorActivo",
                query = "SELECT j FROM Jornada j WHERE j.activo = :activo"
        ),
        @NamedQuery(
                name = "Jornada.countByNombre",
                query = "SELECT COUNT(j) FROM Jornada j WHERE UPPER(j.nombreJornada) LIKE :nombre"
        ),
        @NamedQuery(
                name = "Jornada.countByActivo",
                query = "SELECT COUNT(j) FROM Jornada j WHERE j.activo = :activo"
        )
})
public class Jornada {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_jornada", nullable = false)
    private UUID id;

    @Size(max = 150)
    @NotNull
    @Column(name = "nombre_jornada", nullable = false, length = 150)
    private String nombreJornada;

    @Column(name = "fecha")
    private LocalDate fecha;

    @NotNull
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;

    @NotNull
    @Column(name = "activo", nullable = false)
    private Boolean activo = false;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = OffsetDateTime.now();
        }
        if (activo == null) {
            activo = false;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombreJornada() {
        return nombreJornada;
    }

    public void setNombreJornada(String nombreJornada) {
        this.nombreJornada = nombreJornada;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
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