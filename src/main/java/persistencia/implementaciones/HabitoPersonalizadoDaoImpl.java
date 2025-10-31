package persistencia.implementaciones;

import entidades.HabitoPersonalizado;
import entidades.Usuario;
import excepciones.MiExcepcion;
import persistencia.interfaces.HabitoPersonalizadoDao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class HabitoPersonalizadoDaoImpl implements HabitoPersonalizadoDao {

    private final EntityManagerFactory emf;
    private static HabitoPersonalizadoDaoImpl instance;

    private HabitoPersonalizadoDaoImpl() {
        this.emf = Persistence.createEntityManagerFactory("gestorhabitos");
    }

    public static HabitoPersonalizadoDaoImpl getInstance() {
        if (instance == null) {
            instance = new HabitoPersonalizadoDaoImpl();
        }
        return instance;
    }

    @Override
    public List<HabitoPersonalizado> getByUsuario(Usuario usuario) throws MiExcepcion {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT hp FROM HabitoPersonalizado hp WHERE hp.usuario.id = :usuarioId",
                            HabitoPersonalizado.class
                    )
                    .setParameter("usuarioId", usuario.getId())
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void save(HabitoPersonalizado data) throws MiExcepcion {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(data);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new MiExcepcion("Error al guardar el hábito personalizado: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public void update(HabitoPersonalizado data) throws MiExcepcion {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(data);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new MiExcepcion("Error al actualizar el hábito personalizado: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Integer id) throws MiExcepcion {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            HabitoPersonalizado habitoP = em.find(HabitoPersonalizado.class, id);
            if (habitoP != null) {
                em.remove(habitoP);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new MiExcepcion("Error al eliminar el hábito personalizado: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public HabitoPersonalizado findById(int id) throws MiExcepcion {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(HabitoPersonalizado.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<HabitoPersonalizado> findAll() throws MiExcepcion {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT hp FROM HabitoPersonalizado hp", HabitoPersonalizado.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
