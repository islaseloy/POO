package persistencia.implementaciones;

import entidades.Progreso;
import excepciones.MiExcepcion;
import persistencia.interfaces.ProgresoDao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class ProgresoDaoImpl implements ProgresoDao {

    private final EntityManagerFactory emf;
    private static ProgresoDaoImpl instance;

    private ProgresoDaoImpl() {
        this.emf = Persistence.createEntityManagerFactory("gestorhabitos");
    }

    public static ProgresoDaoImpl getInstance() {
        if (instance == null) {
            instance = new ProgresoDaoImpl();
        }
        return instance;
    }

    @Override
    public void save(Progreso data) throws MiExcepcion {
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
    public void update(Progreso data) throws MiExcepcion {
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
            Progreso progreso = em.find(Progreso.class, Id);
            if (progreso != null) {
                em.remove(progreso);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Progreso findById(int id) throws MiExcepcion {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Progreso.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Progreso> findAll() throws MiExcepcion {
        EntityManager em = emf.createEntityManager();
        try{
            return em.createQuery("SELECT p FROM Progreso p", Progreso.class).getResultList();
        } finally {
            em.close();
        }
    }
}