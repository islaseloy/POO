package entidades;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombreDeUsuario;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String contrasena;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<HabitoPersonalizado> habitosPersonalizados = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(String nombreDeUsuario, String email, String contrasena, Date fechaRegistro) {
        this.nombreDeUsuario = nombreDeUsuario;
        this.email = email;
        this.contrasena = contrasena;
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombreDeUsuario() { return nombreDeUsuario; }
    public void setNombreDeUsuario(String nombreDeUsuario) { this.nombreDeUsuario = nombreDeUsuario; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public List<HabitoPersonalizado> getHabitosPersonalizados() { return habitosPersonalizados; }
    public void setHabitosPersonalizados(List<HabitoPersonalizado> habitosPersonalizados) { this.habitosPersonalizados = habitosPersonalizados; }


    public void addHabitoPersonalizado(HabitoPersonalizado habitoP) {
        if (this.habitosPersonalizados == null) {
            this.habitosPersonalizados = new ArrayList<>();
        }
        this.habitosPersonalizados.add(habitoP);
        habitoP.setUsuario(this);
    }

    public void removeHabitoPersonalizado(HabitoPersonalizado habitoP) {
        if (this.habitosPersonalizados != null) {
            this.habitosPersonalizados.remove(habitoP);
            habitoP.setUsuario(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id != null && id.equals(usuario.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return this.nombreDeUsuario;
    }
}