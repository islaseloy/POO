package persistencia.implementaciones;

import entidades.Habito;
import excepciones.MiExcepcion;
import persistencia.interfaces.HabitoDao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import java.util.List;

public class HabitoDaoImpl implements HabitoDao {

    private final EntityManagerFactory emf;
    private static HabitoDaoImpl instance;

    private HabitoDaoImpl() {
        this.emf = Persistence.createEntityManagerFactory("gestorhabitos");
    }

    public static HabitoDaoImpl getInstance() {
        if (instance == null) {
            instance = new HabitoDaoImpl();
        }
        return instance;
    }

    @Override
    public Habito getByName(String name) throws MiExcepcion {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT h FROM Habito h WHERE h.nombre = :name", Habito.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public void save(Habito data) throws MiExcepcion {
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
    public void update(Habito data) throws MiExcepcion {
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
            Habito habito = em.find(Habito.class, Id);
            if (habito != null) {
                em.createQuery("DELETE FROM HabitoPersonalizado hp WHERE hp.habitoBase.id = :habitoId")
                        .setParameter("habitoId", Id)
                        .executeUpdate();
                em.remove(habito);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Habito findById(int id) throws MiExcepcion {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Habito.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Habito> findAll() throws MiExcepcion {
        EntityManager em = emf.createEntityManager();
        try{
            return em.createQuery("SELECT h FROM Habito h", Habito.class).getResultList();
        } finally {
            em.close();
        }
    }
}