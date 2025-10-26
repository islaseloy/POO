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
    private String estado; // Ej: "Completado", "No Completado"

    @Column(length = 1024) // Columna más grande para observaciones
    private String observaciones;

    @Column(nullable = false)
    private Double logro; // El valor numérico. Ej: 2 (de 2 litros de agua)

    /*
     * RELACIÓN CLAVE (Lado "Muchos"):
     * Muchos registros de Progreso pertenecen a Un Hábito.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habito_id", nullable = false)
    private Habito habito;

    // Constructores
    public Progreso() {
    }

    public Progreso(Date fechaRegistro, String estado, String observaciones, Double logro, Habito habito) {
        this.fechaRegistro = fechaRegistro;
        this.estado = estado;
        this.observaciones = observaciones;
        this.logro = logro;
        this.habito = habito;
    }

    // Getters y Setters

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

    public Habito getHabito() {
        return habito;
    }

    public void setHabito(Habito habito) {
        this.habito = habito;
    }

    // Equals y HashCode (basados en ID)

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