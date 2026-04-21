package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "pregunta", schema = "public")
@NamedQueries({
        @NamedQuery(name = "Pregunta.findByCoincidenciaTexto",
                query = "SELECT p FROM Pregunta p WHERE UPPER(p.contenidoPregunta) LIKE :text")

})
public class Pregunta {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_pregunta", nullable = false)
    private UUID id;

    @NotNull
    @Lob
    @Column(name = "contenido_pregunta", nullable = false)
    private String contenidoPregunta;

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

    public String getContenidoPregunta() {
        return contenidoPregunta;
    }

    public void setContenidoPregunta(String contenidoPregunta) {
        this.contenidoPregunta = contenidoPregunta;
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