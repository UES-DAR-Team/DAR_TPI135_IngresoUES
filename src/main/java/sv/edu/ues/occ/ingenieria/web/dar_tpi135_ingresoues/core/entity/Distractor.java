package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "distractor", schema = "public")
@NamedQueries({
        @NamedQuery(name = "Distractor.findByCoincidenciaTexto",
                query ="SELECT d FROM Distractor d WHERE UPPER(d.contenidoDistractor) LIKE :text")
})
public class Distractor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_distractor", nullable = false)
    private UUID id;

    @NotNull
    @Lob
    @Column(name = "contenido_distractor", nullable = false)
    private String contenidoDistractor;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private Date fechaCreacion;

    @NotNull
    @Column(name = "activo", nullable = false)
    private Boolean activo = false;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContenidoDistractor() {
        return contenidoDistractor;
    }

    public void setContenidoDistractor(String contenidoDistractor) {
        this.contenidoDistractor = contenidoDistractor;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

}