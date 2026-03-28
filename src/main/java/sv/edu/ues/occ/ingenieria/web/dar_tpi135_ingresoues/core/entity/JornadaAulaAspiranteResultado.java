package sv.edu.ues.occ.ingenieria.web.dar_tpi135_ingresoues.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "jornada_aula_aspirante_resultado", schema = "public")
@NamedQueries({
        @NamedQuery(
                name = "JornadaAulaAspiranteResultado.buscarPorJornadaAulaAspirante",
                query = "SELECT r FROM JornadaAulaAspiranteResultado r WHERE r.idJornadaAulaAspirante.id = :idJornadaAulaAspirante"
        ),
        @NamedQuery(
                name = "JornadaAulaAspiranteResultado.buscarPorAprobado",
                query = "SELECT r FROM JornadaAulaAspiranteResultado r WHERE r.aprobado = :aprobado"
        ),
        @NamedQuery(
                name = "JornadaAulaAspiranteResultado.buscarPorRangoPuntaje",
                query = "SELECT r FROM JornadaAulaAspiranteResultado r WHERE r.puntajeObtenido BETWEEN :min AND :max"
        ),
        @NamedQuery(
                name = "JornadaAulaAspiranteResultado.countByAprobado",
                query = "SELECT COUNT(r) FROM JornadaAulaAspiranteResultado r WHERE r.aprobado = :aprobado"
        )
})
public class JornadaAulaAspiranteResultado {
    @Id
    @Column(name = "id_jornada_aula_aspirante_resultado", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_jornada_aula_aspirante", nullable = false)
    private JornadaAulaAspirante idJornadaAulaAspirante;

    @Column(name = "puntaje_obtenido", precision = 6, scale = 2)
    private BigDecimal puntajeObtenido;

    @Column(name = "aprobado")
    private Boolean aprobado;

    @Column(name = "fecha_calificacion")
    private OffsetDateTime fechaCalificacion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JornadaAulaAspirante getIdJornadaAulaAspirante() {
        return idJornadaAulaAspirante;
    }

    public void setIdJornadaAulaAspirante(JornadaAulaAspirante idJornadaAulaAspirante) {
        this.idJornadaAulaAspirante = idJornadaAulaAspirante;
    }

    public BigDecimal getPuntajeObtenido() {
        return puntajeObtenido;
    }

    public void setPuntajeObtenido(BigDecimal puntajeObtenido) {
        this.puntajeObtenido = puntajeObtenido;
    }

    public Boolean getAprobado() {
        return aprobado;
    }

    public void setAprobado(Boolean aprobado) {
        this.aprobado = aprobado;
    }

    public OffsetDateTime getFechaCalificacion() {
        return fechaCalificacion;
    }

    public void setFechaCalificacion(OffsetDateTime fechaCalificacion) {
        this.fechaCalificacion = fechaCalificacion;
    }

}