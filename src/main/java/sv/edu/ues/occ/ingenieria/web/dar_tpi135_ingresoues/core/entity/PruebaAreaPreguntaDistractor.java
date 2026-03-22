package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

@Entity
@NamedQueries({
        @NamedQuery(name = "PruebaAreaPreguntaDistractor.findByPruebaAreaPregunta", query = "SELECT d FROM PruebaAreaPreguntaDistractor d WHERE d.idPruebaAreaPregunta.id = :idPruebaAreaPregunta ORDER BY d.id ASC"),
        @NamedQuery(name = "PruebaAreaPreguntaDistractor.findRespuestaCorrecta", query = "SELECT d FROM PruebaAreaPreguntaDistractor d WHERE d.idPruebaAreaPregunta.id = :idPruebaAreaPregunta AND d.esRespuestaCorrecta = true"),
        @NamedQuery(name = "PruebaAreaPreguntaDistractor.findByDistractor", query = "SELECT d FROM PruebaAreaPreguntaDistractor d WHERE d.idDistractor.id = :idDistractor")
})
@Table(name = "prueba_area_pregunta_distractor", schema = "public")
public class PruebaAreaPreguntaDistractor {
    @Id
    @Column(name = "id_prueba_area_pregunta_distractor", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_prueba_area_pregunta", nullable = false)
    private PruebaAreaPregunta idPruebaAreaPregunta;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_distractor", nullable = false)
    private Distractor idDistractor;

    @Column(name = "es_respuesta_correcta")
    private Boolean esRespuestaCorrecta;

    @NotNull
    @Column(name = "fecha_registro", nullable = false)
    private OffsetDateTime fechaRegistro;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PruebaAreaPregunta getIdPruebaAreaPregunta() {
        return idPruebaAreaPregunta;
    }

    public void setIdPruebaAreaPregunta(PruebaAreaPregunta idPruebaAreaPregunta) {
        this.idPruebaAreaPregunta = idPruebaAreaPregunta;
    }

    public Distractor getIdDistractor() {
        return idDistractor;
    }

    public void setIdDistractor(Distractor idDistractor) {
        this.idDistractor = idDistractor;
    }

    public Boolean getEsRespuestaCorrecta() {
        return esRespuestaCorrecta;
    }

    public void setEsRespuestaCorrecta(Boolean esRespuestaCorrecta) {
        this.esRespuestaCorrecta = esRespuestaCorrecta;
    }

    public OffsetDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(OffsetDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

}