package excepciones;

/**
 * Clase MUY simple que hereda de Exception para superponer un mensaje
 * */
public class MiExcepcion extends Exception {
    public MiExcepcion(String msg) {
        super(msg);
    }
}
