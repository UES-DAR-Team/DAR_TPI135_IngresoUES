package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "area_conocimiento", schema = "public")
@NamedQueries({
        @NamedQuery(name = "AreaConocimiento.findByNameLike", query = "SELECT a FROM AreaConocimiento a WHERE upper(a.nombre) like :name"),
        @NamedQuery(name = "AreaConocimiento.findByAreaPadre", query = "SELECT a FROM AreaConocimiento a WHERE a.idAutoReferenciaArea IS NULL ORDER BY a.nombre"),
        @NamedQuery(name = "AreaConocimiento.findHijosByPadre", query = "SELECT a FROM AreaConocimiento a WHERE a.idAutoReferenciaArea.id = :idPadre ORDER BY a.nombre")
})
public class AreaConocimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_area_conocimiento", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_auto_referencia_area")
    private AreaConocimiento idAutoReferenciaArea;

    @Size(max = 250)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 250)
    private String nombre;

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

    public AreaConocimiento getIdAutoReferenciaArea() {
        return idAutoReferenciaArea;
    }

    public void setIdAutoReferenciaArea(AreaConocimiento idAutoReferenciaArea) {
        this.idAutoReferenciaArea = idAutoReferenciaArea;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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