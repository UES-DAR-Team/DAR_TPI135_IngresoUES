package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "opcion", schema = "public")
public class Opcion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_opcion", nullable = false)
    private UUID id;

    @Size(max = 150)
    @Column(name = "nombre_opcion", length = 150)
    private String nombreOpcion;

    @Size(max = 150)
    @Column(name = "codigo_opcion", length = 150)
    private String codigoOpcion;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @NotNull
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;

    @OneToMany(mappedBy = "idOpcion")
    private Set<AspiranteOpcion> aspiranteOpcions = new LinkedHashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombreOpcion() {
        return nombreOpcion;
    }

    public void setNombreOpcion(String nombreOpcion) {
        this.nombreOpcion = nombreOpcion;
    }

    public String getCodigoOpcion() {
        return codigoOpcion;
    }

    public void setCodigoOpcion(String codigoOpcion) {
        this.codigoOpcion = codigoOpcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Set<AspiranteOpcion> getAspiranteOpcions() {
        return aspiranteOpcions;
    }

    public void setAspiranteOpcions(Set<AspiranteOpcion> aspiranteOpcions) {
        this.aspiranteOpcions = aspiranteOpcions;
    }

}