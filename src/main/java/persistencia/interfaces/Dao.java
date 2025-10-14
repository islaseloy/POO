package persistencia.interfaces;

import excepciones.MiExcepcion;

import java.util.List;

public interface Dao<T> {
    void save(T data) throws MiExcepcion;
    void update(T data) throws MiExcepcion;
    void delete(Integer Id) throws MiExcepcion;
    T findById(int id) throws MiExcepcion;
    List<T> findAll() throws MiExcepcion;
}
