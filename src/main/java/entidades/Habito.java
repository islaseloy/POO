package entidades;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "habitos")
public class Habito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;
    private String tipo;
    private String frecuencia;
    private String horario;
    private Double meta; // Usamos Double para metas numéricas (ej: 2.5 litros)
    private String unidad; // (ej: "litros", "pasos", "páginas")

    @Temporal(TemporalType.DATE) // Solo la fecha de inicio, sin hora
    private Date fechaInicio;

    /*
     * PASO CLAVE DE LA RELACIÓN (Lado "Muchos"):
     * Muchos hábitos pueden pertenecer a Un usuario.
     * FetchType.LAZY = No cargues el Usuario hasta que lo pida (habito.getUsuario()).
     * JoinColumn = La columna en la tabla "habitos" que será la clave foránea.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false) // Un hábito DEBE tener un usuario
    private Usuario usuario;

    //Constructores
    public Habito() {
    }

    // Constructor sin ID y sin Usuario (se setea después)
    public Habito(String nombre, String descripcion, String tipo, String frecuencia, String horario, Double meta, String unidad, Date fechaInicio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.frecuencia = frecuencia;
        this.horario = horario;
        this.meta = meta;
        this.unidad = unidad;
        this.fechaInicio = fechaInicio;
    }

    // Getters y Setters

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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



    // equals() y hashCode()
    // basados solo en el ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Habito habito = (Habito) o;
        return Objects.equals(id, habito.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}