package entidades;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "progresos")
public class Progreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date fechaRegistro;

    @Column(nullable = false)
    private String estado;

    @Column(length = 1024)
    private String observaciones;

    @Column(nullable = false)
    private Double logro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habito_personalizado_id", nullable = false)
    private HabitoPersonalizado habitoPersonalizado;

    public Progreso() {
    }

    public Progreso(Date fechaRegistro, String estado, String observaciones, Double logro, Habito habito) {
        this.fechaRegistro = fechaRegistro;
        this.estado = estado;
        this.observaciones = observaciones;
        this.logro = logro;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Double getLogro() {
        return logro;
    }

    public void setLogro(Double logro) {
        this.logro = logro;
    }

    public HabitoPersonalizado getHabitoPersonalizado() { return habitoPersonalizado; }

    public void setHabitoPersonalizado(HabitoPersonalizado habitoPersonalizado) { this.habitoPersonalizado = habitoPersonalizado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Progreso progreso = (Progreso) o;
        return Objects.equals(id, progreso.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}