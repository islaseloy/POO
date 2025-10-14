package persistencia.interfaces;

import entidades.Usuario;

public interface UsuarioDao extends Dao<Usuario> {
    /*
    * Esta interfaz exitende al Dao general que está creado con todos los métodos, y eso está bien
    * Tenemos una interface específica porque a lo mejor más adelante necesita métodos exclusivos de usuario
    * Como por ejemplo, buscar por email o algo así
    * */
}
