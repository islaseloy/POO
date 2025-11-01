package controladores;

import entidades.HabitoPersonalizado;
import entidades.Progreso;
import entidades.Usuario;
import excepciones.MiExcepcion;
import persistencia.implementaciones.HabitoPersonalizadoDaoImpl;
import persistencia.implementaciones.ProgresoDaoImpl;
import persistencia.implementaciones.UsuarioDaoImpl;
import persistencia.interfaces.HabitoPersonalizadoDao;
import persistencia.interfaces.ProgresoDao;
import persistencia.interfaces.UsuarioDao;
import vistas.VistaProgreso;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProgresoController {

    private final VistaProgreso vista;
    private final ProgresoDao progresoDao;
    private final UsuarioDao usuarioDao;
    private final HabitoPersonalizadoDao habitoPersonalizadoDao;
    //definimos la fecha para consistencia
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public ProgresoController(VistaProgreso vista) {
        this.vista = vista;
        this.progresoDao = ProgresoDaoImpl.getInstance();
        this.usuarioDao = UsuarioDaoImpl.getInstance();
        this.habitoPersonalizadoDao = HabitoPersonalizadoDaoImpl.getInstance();
    }

    public void guardarProgreso() {
        try {
            HabitoPersonalizado habitoP = vista.getHabitoPersonalizadoSeleccionado();
            String fechaStr = vista.getFechaRegistro();
            String estado = vista.getEstadoSeleccionado();
            String observaciones = vista.getObservaciones();
            String logroStr = vista.getLogro();

            if (habitoP == null) {
                throw new MiExcepcion("Debe seleccionar un Hábito para registrar el progreso.");
            }
            if (fechaStr.trim().isEmpty()) {
                throw new MiExcepcion("El campo 'Fecha registro' es obligatorio.");
            }
            if (logroStr.trim().isEmpty()) {
                throw new MiExcepcion("El campo 'Logro' es obligatorio.");
            }

            Date fechaRegistro;
            try {
                fechaRegistro = new SimpleDateFormat("dd-MM-yyyy").parse(fechaStr);
            } catch (ParseException e) {
                throw new MiExcepcion("El formato de 'Fecha registro' es incorrecto. Use dd-MM-yyyy.");
            }

            Double logro;
            try {
                logro = Double.parseDouble(logroStr);
            } catch (NumberFormatException e) {
                throw new MiExcepcion("El valor en 'Logro' debe ser un número (ej: 12.5).");
            }

            Progreso nuevoProgreso = new Progreso();
            nuevoProgreso.setFechaRegistro(fechaRegistro);
            nuevoProgreso.setEstado(estado);
            nuevoProgreso.setObservaciones(observaciones);
            nuevoProgreso.setLogro(logro);

            nuevoProgreso.setHabitoPersonalizado(habitoP);

            progresoDao.save(nuevoProgreso);

            vista.mostrarMensaje("Progreso registrado con éxito para el hábito '" + habitoP.getHabitoBase().getNombre() + "'.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            refrescarTablaProgresos();
            vista.limpiarFormulario();

        } catch (MiExcepcion e) {
            vista.mostrarMensaje(e.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            vista.mostrarMensaje("Ocurrió un error inesperado al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void editarProgreso() {
        try {
            // 1. OBTENER ID
            Integer id = vista.getIdProgreso();
            if (id == null) {
                throw new MiExcepcion("Debe seleccionar un progreso de la tabla para editar.");
            }

            HabitoPersonalizado habitoP = vista.getHabitoPersonalizadoSeleccionado();
            String fechaStr = vista.getFechaRegistro();
            String estado = vista.getEstadoSeleccionado();
            String observaciones = vista.getObservaciones();
            String logroStr = vista.getLogro();


            if (habitoP == null) throw new MiExcepcion("Debe seleccionar un Hábito.");
            if (fechaStr.trim().isEmpty()) throw new MiExcepcion("El campo 'Fecha registro' es obligatorio.");
            if (logroStr.trim().isEmpty()) throw new MiExcepcion("El campo 'Logro' es obligatorio.");


            Date fechaRegistro = dateFormat.parse(fechaStr);
            Double logro = Double.parseDouble(logroStr);

            Progreso progresoAEditar = progresoDao.findById(id);
            if (progresoAEditar == null) {
                throw new MiExcepcion("El progreso que intenta editar ya no existe.");
            }

            progresoAEditar.setFechaRegistro(fechaRegistro);
            progresoAEditar.setEstado(estado);
            progresoAEditar.setObservaciones(observaciones);
            progresoAEditar.setLogro(logro);
            progresoAEditar.setHabitoPersonalizado(habitoP);

            progresoDao.update(progresoAEditar);


            vista.mostrarMensaje("Progreso actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            refrescarTablaProgresos();
            vista.limpiarFormulario();

        } catch (MiExcepcion e) {
            vista.mostrarMensaje(e.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
        } catch (ParseException e) {
            vista.mostrarMensaje("El formato de 'Fecha registro' es incorrecto. Use dd-MM-yyyy.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            vista.mostrarMensaje("El valor en 'Logro' debe ser un número (ej: 12.5).", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            vista.mostrarMensaje("Ocurrió un error inesperado al editar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void eliminarProgreso() {
        try {
            Integer id = vista.getIdProgreso();
            if (id == null) {
                throw new MiExcepcion("Debe seleccionar un progreso de la tabla para eliminar.");
            }

            int respuesta = JOptionPane.showConfirmDialog(
                    null,
                    "¿Está seguro de que desea eliminar este registro de progreso?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (respuesta == JOptionPane.YES_OPTION) {
                progresoDao.delete(id);
                vista.mostrarMensaje("Progreso eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                refrescarTablaProgresos();
                vista.limpiarFormulario();
            }
        } catch (MiExcepcion e) {
            vista.mostrarMensaje(e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            vista.mostrarMensaje("Ocurrió un error inesperado al eliminar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    public void cargarDatosIniciales() {
        cargarUsuariosEnComboBox();
        refrescarTablaProgresos();
        // limpia los combos al inicio
        vista.cargarHabitosPersonalizados(new ArrayList<>());
        vista.habilitarComboHabitos(false);
    }

    private void cargarUsuariosEnComboBox() {
        try {
            List<Usuario> usuarios = usuarioDao.findAll();
            vista.cargarUsuarios(usuarios);
        } catch (MiExcepcion e) {
            vista.mostrarMensaje("Error al cargar la lista de usuarios.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void progresoSeleccionado() {
        try {
            Integer id = vista.getIdProgreso();
            if (id == null) {
                // esto pasa si se llama al limpiar, no es un error
                return;
            }

            Progreso p = progresoDao.findById(id);
            if (p == null) {
                throw new MiExcepcion("El progreso seleccionado ya no existe.");
            }
            //rellenar form
            vista.setFormulario(p);

        } catch (MiExcepcion e) {
            vista.mostrarMensaje(e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void usuarioSeleccionado() {
        Usuario usuarioSeleccionado = vista.getUsuarioSeleccionado();

        if (usuarioSeleccionado != null) {
            try {
                List<HabitoPersonalizado> habitos = habitoPersonalizadoDao.getByUsuario(usuarioSeleccionado);

                vista.cargarHabitosPersonalizados(habitos);
                vista.habilitarComboHabitos(true);

            } catch (MiExcepcion e) {
                vista.mostrarMensaje("Error al cargar los hábitos del usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            vista.cargarHabitosPersonalizados(new ArrayList<>());
            vista.habilitarComboHabitos(false);
        }
    }

    public void refrescarTablaProgresos() {
        try {
            List<Progreso> progresos = progresoDao.findAll();
            DefaultTableModel model = new DefaultTableModel();

            model.addColumn("ID");
            model.addColumn("Fecha");
            model.addColumn("Hábito");
            model.addColumn("Usuario");
            model.addColumn("Logro");
            model.addColumn("Estado");

            if (progresos != null) {
                for (Progreso p : progresos) {

                    model.addRow(new Object[]{
                            p.getId(),
                            dateFormat.format(p.getFechaRegistro()),
                            p.getHabitoPersonalizado().getHabitoBase().getNombre(),
                            p.getHabitoPersonalizado().getUsuario().getNombreDeUsuario(),
                            p.getLogro(),
                            p.getEstado()
                    });
                }
            }
            vista.actualizarTabla(model);

        } catch (MiExcepcion e) {
            vista.mostrarMensaje("Error al cargar el historial de progresos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}



