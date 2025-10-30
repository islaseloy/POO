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
            return em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
        } finally {
            em.close();
        }
    }
}
