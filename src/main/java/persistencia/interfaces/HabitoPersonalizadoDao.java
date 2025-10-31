package persistencia.interfaces;

import entidades.HabitoPersonalizado;
import entidades.Usuario;
import excepciones.MiExcepcion;
import java.util.List;

public interface HabitoPersonalizadoDao extends Dao<HabitoPersonalizado> {

    List<HabitoPersonalizado> getByUsuario(Usuario usuario) throws MiExcepcion;

}