package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "opcion", schema = "public")
@NamedQueries({
        @NamedQuery(
                name = "Opcion.findActivos",
                query = "SELECT o FROM Opcion o WHERE o.activo = true"
        ),
        @NamedQuery(
                name = "Opcion.findByCodigo",
                query = "SELECT o FROM Opcion o WHERE o.codigoOpcion = :codigo"
        )
})
public class Opcion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_opcion", nullable = false)
    private UUID id;

    @Size(max = 20)
    @NotNull
    @Column(name = "codigo_opcion", nullable = false, unique = true, length = 20)
    private String codigoOpcion;

    @Size(max = 250)
    @NotNull
    @Column(name = "nombre_opcion", nullable = false, length = 250)
    private String nombreOpcion;

    @NotNull
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @NotNull
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion = OffsetDateTime.now();

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) fechaCreacion = OffsetDateTime.now();
        if (activo == null) activo = true;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCodigoOpcion() { return codigoOpcion; }
    public void setCodigoOpcion(String codigoOpcion) { this.codigoOpcion = codigoOpcion; }
    public String getNombreOpcion() { return nombreOpcion; }
    public void setNombreOpcion(String nombreOpcion) { this.nombreOpcion = nombreOpcion; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}