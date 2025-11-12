package entidades;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "habitos")
public class Habito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    private String tipo;

    @OneToMany(mappedBy = "habitoBase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HabitoPersonalizado> suscripciones = new ArrayList<>();

    public Habito() {
    }

    public Habito(String nombre, String descripcion, String tipo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
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

    public List<HabitoPersonalizado> getSuscripciones() {
        return suscripciones;
    }

    public void setSuscripciones(List<HabitoPersonalizado> suscripciones) {
        this.suscripciones = suscripciones;
    }

    @Override
    public String toString() {
        return this.nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Habito habito = (Habito) o;
        return id != null && id.equals(habito.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}