package controladores;

import entidades.HabitoPersonalizado;
import entidades.Usuario;
import excepciones.MiExcepcion;
import persistencia.implementaciones.UsuarioDaoImpl;
import persistencia.interfaces.UsuarioDao;
import vistas.VistaUsuario;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.Date;
import java.util.List;

public class UsuarioController {

    private final VistaUsuario vista;
    private final UsuarioDao dao;

    public UsuarioController(VistaUsuario vista) {
        this.vista = vista;
        this.dao = UsuarioDaoImpl.getInstance();

        refrescarTabla();
    }

    public void limpiarCampos() {
        vista.limpiarFormulario();
    }

    public void refrescarTabla() {
        try {
            List<Usuario> usuarios = dao.findAll();
            DefaultTableModel model = new DefaultTableModel();

            model.addColumn("ID");
            model.addColumn("Nombre");
            model.addColumn("Email");
            model.addColumn("Hábitos");
            if (usuarios != null) {
                for (Usuario u : usuarios) {

                    //El StringBuilder es la forma más fácil de trabajar estas cosas
                    StringBuilder habitosBuilder = new StringBuilder();

                    List<HabitoPersonalizado> habitosPersonales = u.getHabitosPersonalizados();

                    if (habitosPersonales != null && !habitosPersonales.isEmpty()) {
                        for (int i = 0; i < habitosPersonales.size(); i++) {
                            String nombreHabito = habitosPersonales.get(i).getHabitoBase().getNombre();
                            habitosBuilder.append(nombreHabito);

                            if (i < habitosPersonales.size() - 1) {
                                habitosBuilder.append(", ");
                            }
                        }
                    }

                    String habitosStr = habitosBuilder.toString();

                    model.addRow(new Object[]{
                            u.getId(),
                            u.getNombreDeUsuario(),
                            u.getEmail(),
                            habitosStr
                    });
                }
            }
            vista.actualizarTabla(model);

        } catch (MiExcepcion ex) {
            vista.mostrarMensaje("Error al cargar la tabla de usuarios: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void guardarUsuario() {
        try {
            String nombre = vista.getNombreUsuario();
            String email = vista.getEmail();
            String contrasena = vista.getContrasena();

            if (nombre.trim().isEmpty() || email.trim().isEmpty() || contrasena.trim().isEmpty()) {
                throw new MiExcepcion("Todos los campos (nombre, email, contraseña) son obligatorios.");
            }

            Usuario nuevoUsuario = new Usuario(nombre, email, contrasena, new Date());

            dao.save(nuevoUsuario);

            vista.mostrarMensaje("Usuario guardado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            refrescarTabla();
            vista.limpiarFormulario();

        } catch (MiExcepcion ex) {
            vista.mostrarMensaje(ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            vista.mostrarMensaje("Ocurrió un error inesperado al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void editarUsuario() {
        try {
            String idStr = vista.getIdUsuario();
            if (idStr.trim().isEmpty()) {
                throw new MiExcepcion("Debe seleccionar un usuario de la tabla para poder editarlo.");
            }
            Integer id = Integer.parseInt(idStr);

            Usuario usuarioAModificar = dao.findById(id);
            if (usuarioAModificar == null) {
                throw new MiExcepcion("El usuario que intenta editar ya no existe.");
            }

            String nombre = vista.getNombreUsuario();
            String email = vista.getEmail();
            String contrasena = vista.getContrasena();

            if (nombre.trim().isEmpty() || email.trim().isEmpty()) {
                throw new MiExcepcion("El nombre y el email no pueden quedar vacíos.");
            }

            usuarioAModificar.setNombreDeUsuario(nombre);
            usuarioAModificar.setEmail(email);

            if (!contrasena.trim().isEmpty()) {
                usuarioAModificar.setContrasena(contrasena);
            }

            dao.update(usuarioAModificar);

            vista.mostrarMensaje("Usuario modificado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            refrescarTabla();
            vista.limpiarFormulario();

        } catch (MiExcepcion e) {
            vista.mostrarMensaje(e.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            vista.mostrarMensaje("Ocurrió un error al editar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void eliminarUsuario() {
        try {
            String idStr = vista.getIdUsuario();
            if (idStr.trim().isEmpty()) {
                throw new MiExcepcion("Debe seleccionar un usuario de la tabla para poder eliminarlo.");
            }
            Integer id = Integer.parseInt(idStr);

            int respuesta = JOptionPane.showConfirmDialog(
                    null,
                    "¿Está seguro de que desea eliminar al usuario con ID " + id + "?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (respuesta == JOptionPane.YES_OPTION) {
                dao.delete(id);

                vista.mostrarMensaje("Usuario eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                refrescarTabla();
                vista.limpiarFormulario();
            }

        } catch (NumberFormatException e) {
            vista.mostrarMensaje("El ID del usuario no es válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (MiExcepcion ex) {
            vista.mostrarMensaje(ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            vista.mostrarMensaje("Ocurrió un error inesperado al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

