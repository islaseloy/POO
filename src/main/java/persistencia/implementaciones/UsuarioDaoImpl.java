package persistencia.implementaciones;

import entidades.Usuario;
import excepciones.MiExcepcion;
import persistencia.interfaces.UsuarioDao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class UsuarioDaoImpl implements UsuarioDao {

    private final EntityManagerFactory emf;
    private static UsuarioDaoImpl instance;

    private UsuarioDaoImpl() {
        this.emf = Persistence.createEntityManagerFactory("gestorhabitos");
    }

    public static UsuarioDaoImpl getInstance() {
        if (instance == null) {
            instance = new UsuarioDaoImpl();
        }
        return instance;
    }

   /**
    * Guarda un nuevo objeto Usuario en la base de datos
    *
    * @param data El objeto Usuario a enviar. No debe ser nulo
    * @throws MiExcepcion si el objeto 'data' es nulo o si falla la persistencia
    * */
    @Override
    public void save(Usuario data) throws MiExcepcion {
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(data);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Usuario data) throws MiExcepcion {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(data);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Integer Id) throws MiExcepcion {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Usuario usuario = em.find(Usuario.class, Id);
            if (usuario != null) {
                em.remove(usuario);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Usuario findById(int id) throws MiExcepcion {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Usuario> findAll() throws MiExcepcion {
        EntityManager em = emf.createEntityManager();
        try{
            List<Usuario> usuarios = em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
            // Forzamos a JPA a refrescar cada usuario desde la BD.
            // Esto actualizará sus colecciones internas
            for (Usuario u : usuarios) {
                em.refresh(u);
            }
            return usuarios;

        } finally {
            em.close();
        }
    }
}
