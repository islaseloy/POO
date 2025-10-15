package entidades;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "usuarios")
public class Usuario {

    //@Id lo transforma al atributo debajo en primary key
    @Id
    //IDENTITY quiere decir que la BD lo hace autoincremental muchas gracias
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //Le ponemos Integer y no int porque en clase se enseñó que JPA recomienda usar los datos primitivos como clases.
    private Integer id;

    //Le especificamos algunos detalles de la columna, particularmente nombre de usuario no nulo y único. Puede variar.
    @Column(nullable = false, unique = true)
    private String nombreDeUsuario;

    //Acá si es bastante lógico que sea unique = true porque no hay dos emails iguales nunca jamás
    @Column(nullable = false, unique = true)
    private String email;

    //Sin ñ porque a algunas bases no les gusta
    @Column(nullable = false)
    private String contrasena;

    //@Temporal le dice a JPA como mapear un Date de Java a la base de datos
    //TIMESTAMP guarda la fecha y la hora, y lo volamos de la vista, es automático
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    //Todos los usuarios van a tener una List<Habito> más adelante, pero por ahora queda como String
    @Column(length = 1024)
    private String habitos;

    // CONSTRUCTORES
    //El buen constructor vacío, nunca falla
    public Usuario() {

    }

    //El constructor con los atributos
    public Usuario(String nombreDeUsuario, String email, String contrasena, String habitos, Date fechaRegistro) {
        this.nombreDeUsuario = nombreDeUsuario;
        this.email = email;
        this.contrasena = contrasena;
        this.habitos = habitos;
        this.fechaRegistro = fechaRegistro;
    }

    //Getters y Setters generados automáticamente gracias

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreDeUsuario() {
        return nombreDeUsuario;
    }

    public void setNombreDeUsuario(String nombreDeUsuario) {
        this.nombreDeUsuario = nombreDeUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getHabitos() {
        return habitos;
    }

    public void setHabitos(String habitos) {
        this.habitos = habitos;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id) && Objects.equals(nombreDeUsuario, usuario.nombreDeUsuario) && Objects.equals(email, usuario.email) && Objects.equals(contrasena, usuario.contrasena) && Objects.equals(fechaRegistro, usuario.fechaRegistro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombreDeUsuario, email, contrasena, fechaRegistro);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombreDeUsuario='" + nombreDeUsuario + '\'' +
                ", email='" + email + '\'' +
                ", contrasena='" + contrasena + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                '}';
    }
}
