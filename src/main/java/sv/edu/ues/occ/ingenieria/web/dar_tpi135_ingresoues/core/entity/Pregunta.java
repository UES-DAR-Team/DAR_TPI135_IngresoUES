package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

@Entity
@Table(name = "pregunta", schema = "public")
@NamedQueries({
    @NamedQuery(name = "Pregunta.findByCoincidenciaTexto",
            query = "SELECT p FROM Pregunta p WHERE UPPER(p.pregunta) LIKE :texto")

})
public class Pregunta {
    @Id
    @Column(name = "id_pregunta", nullable = false)
    private Integer id;

    //Luego cambiar este campo por pregunta directamente
    @Size(max = 250)
    @NotNull
    @Column(name = "nombre_pregunta", nullable = false, length = 250)
    private String pregunta;

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

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
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