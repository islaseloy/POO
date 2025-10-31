package persistencia.interfaces;

import entidades.Habito;
import excepciones.MiExcepcion;

public interface HabitoDao extends Dao<Habito> {
    Habito getByName(String name) throws MiExcepcion;
}
