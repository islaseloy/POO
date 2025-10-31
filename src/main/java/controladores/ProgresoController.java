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

    public void cargarDatosIniciales() {
        cargarUsuariosEnComboBox();
        refrescarTablaProgresos();
    }

    private void cargarUsuariosEnComboBox() {
        try {
            List<Usuario> usuarios = usuarioDao.findAll();
            vista.cargarUsuarios(usuarios);
        } catch (MiExcepcion e) {
            vista.mostrarMensaje("Error al cargar la lista de usuarios.", "Error", JOptionPane.ERROR_MESSAGE);
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
                            p.getFechaRegistro(),
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



