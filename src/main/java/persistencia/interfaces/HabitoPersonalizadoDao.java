package persistencia.interfaces;

import entidades.Habito;
import entidades.HabitoPersonalizado;
import entidades.Usuario;
import excepciones.MiExcepcion;
import java.util.List;

public interface HabitoPersonalizadoDao extends Dao<HabitoPersonalizado> {

    List<HabitoPersonalizado> getByUsuario(Usuario usuario) throws MiExcepcion;

    /**
     * Verifica si ya existe un hábito personalizado para un usuario, hábito y meta específicos
     */
    boolean existe(Usuario u, Habito h, Double meta) throws MiExcepcion;

    /**
     * Verifica si ya existe OTRO hábito personalizado con la misma configuración
     * excluyendo el ID que se está editando actualmente.
     */
    boolean existeOtro(Usuario u, Habito h, Double meta, Integer idActual) throws MiExcepcion;

}