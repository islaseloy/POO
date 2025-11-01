package controladores;


import entidades.Habito;
import entidades.HabitoPersonalizado;
import entidades.Usuario;
import excepciones.MiExcepcion;
import persistencia.implementaciones.HabitoDaoImpl;
import persistencia.implementaciones.HabitoPersonalizadoDaoImpl;
import persistencia.implementaciones.UsuarioDaoImpl;
import persistencia.interfaces.HabitoDao;
import persistencia.interfaces.HabitoPersonalizadoDao;
import persistencia.interfaces.UsuarioDao;
import vistas.VistaHabito;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class HabitoController {

    private final VistaHabito vista;
    private final UsuarioDao usuarioDao;
    private final HabitoDao habitoDao;
    private final HabitoPersonalizadoDao habitoPersonalizadoDao;

    public HabitoController(VistaHabito vista) {
        this.vista = vista;
        this.usuarioDao = UsuarioDaoImpl.getInstance();
        this.habitoDao = HabitoDaoImpl.getInstance();
        this.habitoPersonalizadoDao = HabitoPersonalizadoDaoImpl.getInstance();

        cargarDatosIniciales();
    }

    /**
     * Gestiona el evento de selección de un hábito personalizado en la tabla de la vista
     *
     * Este método es invocado cuando el usuario hace clic en una fila de la JTable
     *
     * Si no se selecciona ninguna fila o si el hábito no se encuentra, el método
     * maneja la situación sin lanzar errores -> en su lugar manda un mensaje de error al usuario
     */
    public void habitoPersonalizadoSeleccionado() {
        try {
            Integer idSeleccionado = vista.getIdHabitoPersonalizadoSeleccionado();

            if (idSeleccionado == null) {
                return;
            }

            HabitoPersonalizado habitoP = habitoPersonalizadoDao.findById(idSeleccionado);

            if (habitoP == null) {
                vista.mostrarMensaje("El hábito seleccionado ya no se encuentra en la base de datos.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            vista.setId(habitoP.getId().toString());
            vista.setFrecuencia(habitoP.getFrecuencia());
            vista.setHorario(habitoP.getHorario());
            vista.setMeta(String.valueOf(habitoP.getMeta()));
            vista.setUnidad(habitoP.getUnidad());
            vista.setFechaInicio(habitoP.getFechaInicio());


            if (habitoP.getHabitoBase() != null) {
                vista.setNombreHabitoCatalogo(habitoP.getHabitoBase());
                vista.setDescripcion(habitoP.getHabitoBase().getDescripcion());
                vista.setTipo(habitoP.getHabitoBase().getTipo());
            }

            if (habitoP.getUsuario() != null) {
                vista.setUsuarioAsociado(habitoP.getUsuario());
            }

        } catch (MiExcepcion e) {
            vista.mostrarMensaje("Ocurrió un error al cargar los datos del hábito: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void eliminarHabitoPersonalizado() {
        try {
            Integer idParaBorrar = vista.getIdHabitoPersonalizadoSeleccionado();

            if (idParaBorrar == null) {
                vista.mostrarMensaje("Por favor, seleccione un hábito de la lista para eliminar.", "Acción Requerida", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int respuesta = JOptionPane.showConfirmDialog(
                    null,
                    "¿Está seguro de que desea eliminar esta asignación de hábito?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (respuesta == JOptionPane.YES_OPTION) {
                habitoPersonalizadoDao.delete(idParaBorrar);

                vista.mostrarMensaje("Hábito eliminado para el usuario.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                refrescarTablaHabitosPersonalizados();
                vista.limpiarCampos();
            }

        } catch (MiExcepcion e) {
            vista.mostrarMensaje("Ocurrió un error al eliminar el hábito: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void guardarHabitoPersonalizado() {
        try {
            Object itemCatalogoSeleccionado = vista.getItemHabitoSeleccionado();
            String descripcion = vista.getDescripcion();
            String tipo = vista.getTipo();

            Usuario usuario = vista.getUsuarioSeleccionado();
            String frecuencia = vista.getFrecuencia();
            String horario = vista.getHorario();
            String metaStr = vista.getMeta();
            String unidad = vista.getUnidad();
            String fechaStr = vista.getFechaInicio();

            Habito habitoBase;

            if (itemCatalogoSeleccionado instanceof Habito) {
                habitoBase = (Habito) itemCatalogoSeleccionado;
            } else if (itemCatalogoSeleccionado instanceof String) {
                String nombreHabitoEscrito = (String) itemCatalogoSeleccionado;
                if (nombreHabitoEscrito.trim().isEmpty()) {
                    throw new MiExcepcion("El campo 'Nombre' del hábito es obligatorio.");
                }

                habitoBase = habitoDao.getByName(nombreHabitoEscrito);

                if (habitoBase == null) {
                    habitoBase = new Habito(nombreHabitoEscrito, descripcion, tipo);
                    habitoDao.save(habitoBase);
                    vista.mostrarMensaje("Nuevo hábito '" + nombreHabitoEscrito + "' ha sido añadido al catálogo general.", "Catálogo Actualizado", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                throw new MiExcepcion("Debe seleccionar un hábito del catálogo o escribir el nombre de uno nuevo.");
            }

            if (usuario == null) {
                throw new MiExcepcion("Debe seleccionar un 'Usuario Asociado'.");
            }
            if (metaStr.trim().isEmpty()) {
                throw new MiExcepcion("El campo 'Meta' es obligatorio para personalizar un hábito.");
            }
            if (fechaStr.trim().isEmpty()) {
                throw new MiExcepcion("El campo 'Fecha de Inicio' es obligatorio.");
            }

            Double meta;
            try {
                meta = Double.parseDouble(metaStr);
            } catch (NumberFormatException e) {
                throw new MiExcepcion("El valor en 'Meta' debe ser un número válido (ej: 10.5).");
            }

            Date fechaInicio;
            try {
                fechaInicio = new SimpleDateFormat("dd-MM-yyyy").parse(fechaStr);
            } catch (ParseException e) {
                throw new MiExcepcion("El formato de 'Fecha de Inicio' es incorrecto. Por favor, use dd-MM-yyyy.");
            }

            if (habitoPersonalizadoDao.existe(usuario, habitoBase, meta)) {
                throw new MiExcepcion("Este usuario ya tiene el hábito '" + habitoBase.getNombre() +
                        "' registrado con la meta " + meta + " " + unidad + ".");
            }

            HabitoPersonalizado nuevoHabitoP = new HabitoPersonalizado();
            nuevoHabitoP.setFrecuencia(frecuencia);
            nuevoHabitoP.setHorario(horario);
            nuevoHabitoP.setMeta(meta);
            nuevoHabitoP.setUnidad(unidad);
            nuevoHabitoP.setFechaInicio(fechaInicio);

            nuevoHabitoP.setHabitoBase(habitoBase);
            nuevoHabitoP.setUsuario(usuario);

            habitoPersonalizadoDao.save(nuevoHabitoP);

            vista.mostrarMensaje("Hábito asignado con éxito al usuario " + usuario.getNombreDeUsuario() + ".", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            refrescarTablaHabitosPersonalizados();
            cargarCatalogoEnComboBox();
            vista.limpiarCampos();

        } catch (MiExcepcion e) {
            vista.mostrarMensaje(e.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            vista.mostrarMensaje("Ocurrió un error inesperado al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void editarHabitoPersonalizado() {
        try {
            Integer id = vista.getIdHabitoPersonalizadoSeleccionado();
            if (id == null) {
                throw new MiExcepcion("Por favor, seleccione un hábito de la tabla para poder editar.");
            }

            HabitoPersonalizado habitoP = habitoPersonalizadoDao.findById(id);
            if (habitoP == null) {
                throw new MiExcepcion("El hábito que intenta editar ya no existe o fue eliminado.");
            }

            String frecuencia = vista.getFrecuencia();
            String horario = vista.getHorario();
            String metaStr = vista.getMeta();
            String unidad = vista.getUnidad();
            String fechaStr = vista.getFechaInicio();

            if (metaStr.trim().isEmpty() || fechaStr.trim().isEmpty()) {
                throw new MiExcepcion("Los campos 'Meta' y 'Fecha de Inicio' no pueden quedar vacíos.");
            }

            Double meta;
            try {
                meta = Double.parseDouble(metaStr);
            } catch (NumberFormatException e) {
                throw new MiExcepcion("El valor de 'Meta' debe ser un número (ej: 10.5).");
            }

            Date fechaInicio;
            try {
                fechaInicio = new SimpleDateFormat("dd-MM-yyyy").parse(fechaStr);
            } catch (ParseException e) {
                throw new MiExcepcion("El formato de 'Fecha de Inicio' es incorrecto. Use dd-MM-yyyy.");
            }

            Usuario usuario = habitoP.getUsuario();
            Habito habitoBase = habitoP.getHabitoBase();
            if (habitoPersonalizadoDao.existeOtro(usuario, habitoBase, meta, id)) {
                throw new MiExcepcion("Ya existe otro registro para '" + habitoBase.getNombre() +
                        "' con la meta " + meta + " " + unidad + ".");
            }

            habitoP.setFrecuencia(frecuencia);
            habitoP.setHorario(horario);
            habitoP.setMeta(meta);
            habitoP.setUnidad(unidad);
            habitoP.setFechaInicio(fechaInicio);

            habitoPersonalizadoDao.update(habitoP);

            vista.mostrarMensaje("Hábito actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            refrescarTablaHabitosPersonalizados();
            vista.limpiarCampos();

        } catch (MiExcepcion e) {
            vista.mostrarMensaje(e.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            vista.mostrarMensaje("Ocurrió un error inesperado al editar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void cargarDatosIniciales() {
        cargarUsuariosEnComboBox();
        cargarCatalogoEnComboBox();
        refrescarTablaHabitosPersonalizados();
    }

    private void cargarUsuariosEnComboBox() {
        try {
            List<Usuario> usuarios = usuarioDao.findAll();
            vista.cargarUsuarios(usuarios);
        } catch (MiExcepcion e) {
            vista.mostrarMensaje("Error fatal: no se pudieron cargar los usuarios.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void catalogoHabitoSeleccionado() {
        Object item = vista.getItemHabitoSeleccionado();

        if (item instanceof Habito) {
            Habito habitoSeleccionado = (Habito) item;
            vista.setDescripcion(habitoSeleccionado.getDescripcion());
            vista.setTipo(habitoSeleccionado.getTipo());
        } else {
            vista.setDescripcion("");
        }
    }

    public void refrescarTablaHabitosPersonalizados() {
        try {
            List<HabitoPersonalizado> suscripciones = habitoPersonalizadoDao.findAll();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("Hábito");
            model.addColumn("Usuario");
            model.addColumn("Meta");
            model.addColumn("Unidad");
            model.addColumn("Frecuencia");

            if (suscripciones != null) {
                System.out.println("Cantidad de hábitos personalizados: " + suscripciones.size());
                for (HabitoPersonalizado hp : suscripciones) {
                    model.addRow(new Object[]{
                            hp.getId(),
                            hp.getHabitoBase().getNombre(),
                            hp.getUsuario().getNombreDeUsuario(),
                            hp.getMeta(),
                            hp.getUnidad(),
                            hp.getFrecuencia()
                    });
                }
            }

            vista.actualizarTabla(model);

        } catch (MiExcepcion e) {
            vista.mostrarMensaje("Error al refrescar la tabla de hábitos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarCatalogoEnComboBox() {
        try {
            List<Habito> catalogo = habitoDao.findAll();
            vista.cargarCatalogo(catalogo);
        } catch (MiExcepcion e) {
            vista.mostrarMensaje("Error fatal: no se pudo cargar el catálogo de hábitos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}