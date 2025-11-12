package entidades;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

@Entity
@Table(name = "habitos_personalizados")
public class HabitoPersonalizado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String frecuencia;
    private String horario;
    private Double meta;
    private String unidad;

    @Temporal(TemporalType.DATE)
    private Date fechaInicio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habito_base_id", nullable = false)
    private Habito habitoBase;

    @OneToMany(mappedBy = "habitoPersonalizado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Progreso> progresos = new ArrayList<>();

    public HabitoPersonalizado() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public Double getMeta() {
        return meta;
    }

    public void setMeta(Double meta) {
        this.meta = meta;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Habito getHabitoBase() {
        return habitoBase;
    }

    public void setHabitoBase(Habito habitoBase) {
        this.habitoBase = habitoBase;
    }

    public List<Progreso> getProgresos() {
        return progresos;
    }

    public void setProgresos(List<Progreso> progresos) {
        this.progresos = progresos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HabitoPersonalizado that = (HabitoPersonalizado) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        String nombreBase = (this.habitoBase != null) ? this.habitoBase.getNombre() : "Hábito sin nombre";

        String detalles = "";
        if (this.meta != null && this.unidad != null && !this.unidad.isEmpty()) {
            detalles = " (Meta: " + this.meta + " " + this.unidad + ")";
        } else if (this.meta != null) {
            detalles = " (Meta: " + this.meta + ")";
        }

        return nombreBase + detalles;
    }

}