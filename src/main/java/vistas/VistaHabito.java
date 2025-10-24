package vistas;

import entidades.Habito;
import entidades.Usuario;
import excepciones.MiExcepcion;
import persistencia.implementaciones.HabitoDaoImpl;
import persistencia.implementaciones.UsuarioDaoImpl;
import persistencia.interfaces.HabitoDao;
import persistencia.interfaces.UsuarioDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class VistaHabito extends JFrame {
    //No debería hacer falta tener ID en la vista pero lo dejamos porque es un atributo, habría que removerlo más adelante
    private JTextField textFieldID;
    private JTextField textFieldNombre;
    private JTextField textFieldDesc;
    private JTextField textFieldFrec;
    //Salud, ejercicio, estudio? tal vez después se puedan filtrar por tipo, estaría bien
    private JComboBox comboBoxTipo;
    //Revisar si hay mejores opciones para el tema de las horas
    private JTextField textFieldHorario;
    //Objetivo, que se combina con la unidad
    private JTextField textFieldMeta;
    //Pasos, kilos, litros, días, etc.
    private JTextField textFieldUnidad;
    //Lo mismo, chequear si hay algo mejor uqe un text field porque si no es espantoso el manejo
    private JTextField textFieldFecha;
    //Asociar el hábito a un usuario, tal vez el software lo use toda la familia
    private JComboBox comboBoxUsuario;
    private JButton guardarButton;
    private JButton cancelarButton;
    private JButton buttonVolver;
    private JPanel panel1;
    private JTable tablaHabitos;
    private JButton editarBtn;
    private JButton eliminarButton;


    private VistaMenu vistaMenuPadre;

    // DAOs necesarios
    private UsuarioDao usuarioDao;
    private HabitoDao habitoDao;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Esta variable controla el "modo" de la vista.
     * Si es null, muestra TODOS los hábitos.
     * Si contiene un Usuario, muestra SÓLO los hábitos de ese usuario.
     */
    private Usuario usuarioFiltrado = null;

    public VistaHabito(VistaMenu vistaMenuPadre) {
        this.vistaMenuPadre = vistaMenuPadre;

        // Inicializamos los DAOs
        this.usuarioDao = UsuarioDaoImpl.getInstance();
        this.habitoDao = HabitoDaoImpl.getInstance();

        setTitle("Gestión de Hábitos");
        setContentPane(panel1);
        //HIDE ON CLOSE para que no nos cierre toda la app
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        // --- INICIALIZACIÓN DE LA VISTA ---
        // 1. Carga los ComboBox
        cargarUsuariosEnComboBox();
        cargarTiposPlaceholder();
        // 2. Carga la tabla
        refrescarTablaHabitos();

        // 3. Deshabilita el campo ID (no debe ser editable por el usuario)
        textFieldID.setEnabled(false);

        // 4. Limpia los campos para una nueva entrada
        limpiarCampos();

        //botones
        buttonVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VistaHabito.this.setVisible(false);
                // Hacemos visible al padre (VistaMenu)
                vistaMenuPadre.setVisible(true);

                // Si estábamos en modo filtrado, reseteamos esta vista
                // para la próxima vez que se abra desde el menú principal.
                if (usuarioFiltrado != null) {
                    resetearVista();
                }
            }
        });

        guardarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarNuevoHabito();
            }
        });

        editarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarHabito();
            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarHabito();
            }
        });

        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });

        // --- LISTENER DE LA TABLA ---
        tablaHabitos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaHabitos.getSelectedRow() != -1) {
                int filaSeleccionada = tablaHabitos.getSelectedRow();
                Integer id = (Integer) tablaHabitos.getModel().getValueAt(filaSeleccionada, 0);

                try {
                    Habito habitoSeleccionado = habitoDao.findById(id);
                    if (habitoSeleccionado == null) return;

                    // Rellenar campos
                    textFieldID.setText(habitoSeleccionado.getId().toString());
                    textFieldNombre.setText(habitoSeleccionado.getNombre());
                    textFieldDesc.setText(habitoSeleccionado.getDescripcion());
                    textFieldFrec.setText(habitoSeleccionado.getFrecuencia());
                    textFieldHorario.setText(habitoSeleccionado.getHorario());
                    if (habitoSeleccionado.getMeta() != null) {
                        textFieldMeta.setText(habitoSeleccionado.getMeta().toString());
                    }
                    textFieldUnidad.setText(habitoSeleccionado.getUnidad());
                    if (habitoSeleccionado.getFechaInicio() != null) {
                        textFieldFecha.setText(dateFormat.format(habitoSeleccionado.getFechaInicio()));
                    }

                    // Seleccionar ComboBoxes
                    comboBoxTipo.setSelectedItem(habitoSeleccionado.getTipo());
                    comboBoxUsuario.setSelectedItem(habitoSeleccionado.getUsuario());

                } catch (MiExcepcion ex) {
                    JOptionPane.showMessageDialog(this, "Error al seleccionar el hábito: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }


        // --- MÉTODOS DE CONTROL DE VISTA (MODO FILTRADO) ---

        /**
         * Pone la vista en "Modo Filtrado", mostrando solo los hábitos de un usuario.
         * Este método es llamado por VistaMenu.
         */
        public void mostrarHabitosDe(Usuario usuario) {
            this.usuarioFiltrado = usuario;

            // Configura la UI para este modo
            setTitle("Hábitos de: " + usuario.getNombreDeUsuario());
            comboBoxUsuario.setSelectedItem(usuario);
            comboBoxUsuario.setEnabled(false); // El usuario no se puede cambiar
            buttonVolver.setText("Volver a Usuarios"); // Cambia el texto del botón

            // Refresca la tabla solo con los hábitos de este usuario
            refrescarTablaHabitos();
            limpiarCampos(); // Limpia el formulario
        }

        // Resetea la vista al "Modo Global" (cuando se vuelve desde el modo filtrado)
        private void resetearVista() {
            this.usuarioFiltrado = null;
            setTitle("Gestión de Hábitos");
            comboBoxUsuario.setEnabled(true);
            buttonVolver.setText("Volver");
            limpiarCampos();
            refrescarTablaHabitos(); // Recarga la tabla con TODOS los hábitos
        }


    /**
     * Lógica para el botón Guardar (Crea un nuevo hábito)
     */
    private void guardarNuevoHabito() {
        try {
            // --- 1. LEER DATOS DE LA VISTA ---
            String nombre = textFieldNombre.getText();
            String desc = textFieldDesc.getText();
            String frec = textFieldFrec.getText();
            String horario = textFieldHorario.getText();
            String unidad = textFieldUnidad.getText();
            String metaStr = textFieldMeta.getText();
            String fechaStr = textFieldFecha.getText();

            Object tipoItem = comboBoxTipo.getSelectedItem();
            Object usuarioItem = comboBoxUsuario.getSelectedItem();

            // --- 2. VALIDACIONES ---
            if (nombre.trim().isEmpty()) {
                throw new MiExcepcion("El nombre es obligatorio.");
            }
            if (metaStr.trim().isEmpty()) {
                throw new MiExcepcion("La meta es obligatoria.");
            }
            if (usuarioItem == null) {
                throw new MiExcepcion("Debe seleccionar un usuario asociado.");
            }
            if (tipoItem == null) {
                throw new MiExcepcion("Debe seleccionar un tipo.");
            }

            // --- 3. PARSEO DE DATOS (después de validar que no están vacíos) ---
            Double meta;
            try {
                meta = Double.parseDouble(metaStr);
            } catch (NumberFormatException ex) {
                throw new MiExcepcion("La meta debe ser un número válido (ej: 10.5).");
            }

            Date fechaInicio = new Date(); // Por defecto, hoy
            if (!fechaStr.trim().isEmpty()) {
                try {
                    fechaInicio = dateFormat.parse(fechaStr);
                } catch (ParseException ex) {
                    throw new MiExcepcion("Formato de fecha incorrecto. Use yyyy-MM-dd");
                }
            }

            // Convertimos los objetos de los ComboBox
            Usuario usuarioSeleccionado = (Usuario) usuarioItem;
            String tipo = tipoItem.toString();

            // --- 4. CREAR Y GUARDAR ---
            Habito nuevoHabito = new Habito(nombre, desc, tipo, frec, horario, meta, unidad, fechaInicio);

            // Establecemos la relación
            usuarioSeleccionado.addHabito(nuevoHabito);

            // Guardamos el Usuario (que por cascada guarda el Hábito)
            usuarioDao.update(usuarioSeleccionado);

            JOptionPane.showMessageDialog(this, "Hábito guardado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // --- 5. ACTUALIZAR VISTA ---
            refrescarTablaHabitos();
            limpiarCampos();

        } catch (MiExcepcion ex) {
            // Un solo catch para todos nuestros errores de validación
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            // Un catch general para errores inesperados
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Bueno para depurar
        }
    }

    /**
     * Lógica para el botón Editar (Actualiza un hábito existente)
     */
    private void editarHabito() {
        try {
            // --- 1. VALIDACIÓN INICIAL ---
            if (textFieldID.getText().isEmpty()) {
                throw new MiExcepcion("Seleccione un hábito de la tabla para editar.");
            }
            Integer idHabito = Integer.parseInt(textFieldID.getText());

            // --- 2. LEER DATOS DE LA VISTA ---
            String nombre = textFieldNombre.getText();
            String desc = textFieldDesc.getText();
            String frec = textFieldFrec.getText();
            String horario = textFieldHorario.getText();
            String unidad = textFieldUnidad.getText();
            String metaStr = textFieldMeta.getText();
            String fechaStr = textFieldFecha.getText();

            Object tipoItem = comboBoxTipo.getSelectedItem();
            Object usuarioItem = comboBoxUsuario.getSelectedItem();

            // --- 3. VALIDACIONES DE CAMPOS ---
            if (nombre.trim().isEmpty()) {
                throw new MiExcepcion("El nombre es obligatorio.");
            }
            if (metaStr.trim().isEmpty()) {
                throw new MiExcepcion("La meta es obligatoria.");
            }
            if (usuarioItem == null) {
                throw new MiExcepcion("Debe seleccionar un usuario asociado.");
            }
            if (tipoItem == null) {
                throw new MiExcepcion("Debe seleccionar un tipo.");
            }

            // --- 4. PARSEO DE DATOS ---
            Double meta;
            try {
                meta = Double.parseDouble(metaStr);
            } catch (NumberFormatException ex) {
                throw new MiExcepcion("La meta debe ser un número válido (ej: 10.5).");
            }

            Date fechaInicio = null; // Asumimos que puede quedar nula si se borra
            if (!fechaStr.trim().isEmpty()) {
                try {
                    fechaInicio = dateFormat.parse(fechaStr);
                } catch (ParseException ex) {
                    throw new MiExcepcion("Formato de fecha incorrecto. Use yyyy-MM-dd");
                }
            }

            Usuario usuarioSeleccionado = (Usuario) usuarioItem;
            String tipo = tipoItem.toString();

            // --- 5. BUSCAR Y ACTUALIZAR ENTIDAD ---
            Habito habitoAEditar = habitoDao.findById(idHabito);
            if (habitoAEditar == null) {
                throw new MiExcepcion("El hábito no existe o fue eliminado.");
            }

            habitoAEditar.setNombre(nombre);
            habitoAEditar.setDescripcion(desc);
            habitoAEditar.setTipo(tipo);
            habitoAEditar.setFrecuencia(frec);
            habitoAEditar.setHorario(horario);
            habitoAEditar.setUnidad(unidad);
            habitoAEditar.setMeta(meta);
            habitoAEditar.setFechaInicio(fechaInicio); // Setea la nueva fecha (o null si se borró)

            // Gestionar cambio de Usuario (si cambió)
            if (!habitoAEditar.getUsuario().equals(usuarioSeleccionado)) {
                // ROTO: Esta lógica es compleja. Por ahora, solo actualizamos el hábito.
                // Para mover un hábito de usuario, necesitaríamos quitarlo del
                // usuario antiguo y agregarlo al nuevo, y luego actualizar AMBOS usuarios.
                // Por simplicidad, mantendremos la lógica de actualizar el hábito.
                habitoAEditar.setUsuario(usuarioSeleccionado);
            }

            // Guardamos los cambios en la BD
            habitoDao.update(habitoAEditar);

            JOptionPane.showMessageDialog(this, "Hábito modificado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // --- 6. ACTUALIZAR VISTA ---
            refrescarTablaHabitos();
            limpiarCampos();

        } catch (MiExcepcion ex) {
            // Un solo catch para todos nuestros errores de validación
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            // Un catch general para errores inesperados
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Lógica para el botón Eliminar
     */
    private void eliminarHabito() {
        try {
            // 1. Verificar que se haya seleccionado un hábito
            if (textFieldID.getText().isEmpty()) {
                throw new MiExcepcion("Seleccione un hábito de la tabla para eliminar.");
            }
            Integer idHabito = Integer.parseInt(textFieldID.getText());

            // 2. Pedir confirmación
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de que desea eliminar este hábito?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (respuesta == JOptionPane.YES_OPTION) {
                // 3. Borrar de la BD
                habitoDao.delete(idHabito);
                JOptionPane.showMessageDialog(this, "Hábito eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                // 4. Actualizar la vista
                refrescarTablaHabitos();
                limpiarCampos();
            }

        } catch (MiExcepcion ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Mtodo para llenar el JComboBox de Usuarios (READ)
     */
    private void cargarUsuariosEnComboBox() {
        try {
            List<Usuario> usuarios = usuarioDao.findAll();
            DefaultComboBoxModel<Usuario> model = new DefaultComboBoxModel<>();

            if (usuarios != null) {
                for (Usuario u : usuarios) {
                    model.addElement(u); // Añadimos el objeto Usuario completo
                }
            }
            comboBoxUsuario.setModel(model); // Gracias al toString() modificado, se va a ver bien

        } catch (MiExcepcion e) {
            System.out.println("Error al cargar usuarios en el ComboBox: " + e.getMessage());
        }
    }

    /**
     * Mtodo para llenar el JComboBox de Tipos (Placeholder)
     */
    private void cargarTiposPlaceholder() {
        // Puedes cambiar estos valores por los que necesites
        String[] tipos = {"Salud", "Estudio", "Ejercicio", "Ocio", "Trabajo", "Otro"};
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(tipos);
        comboBoxTipo.setModel(model);
    }

    /**
     * Mtodo para refrescar la JTable de Hábitos (READ)
     */
    private void refrescarTablaHabitos() {
        try {
            List<Habito> habitos;

            // ¡LÓGICA DEL FILTRO!
            if (this.usuarioFiltrado != null) {
                // Si hay filtro, busca los hábitos de ESE usuario
                // (Usamos .getHabitos() porque la relación es EAGER)
                habitos = this.usuarioFiltrado.getHabitos();
            } else {
                // Si no hay filtro, busca TODOS
                habitos = habitoDao.findAll();
            }

            DefaultTableModel modelo = new DefaultTableModel();

            // Definimos las columnas
            modelo.addColumn("ID");
            modelo.addColumn("Nombre");
            modelo.addColumn("Tipo");
            modelo.addColumn("Meta");
            modelo.addColumn("Unidad");
            modelo.addColumn("Usuario");

            if (habitos != null) {
                for (Habito h : habitos) {
                    modelo.addRow(new Object[]{
                            h.getId(),
                            h.getNombre(),
                            h.getTipo(),
                            h.getMeta(),
                            h.getUnidad(),
                            h.getUsuario().getNombreDeUsuario() // Mostramos el nombre del usuario
                    });
                }
            }
            tablaHabitos.setModel(modelo);

        } catch (MiExcepcion ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar la tabla de hábitos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Mtdo para limpiar todos los campos del formulario
     */
    private void limpiarCampos() {
        textFieldID.setText("");
        textFieldNombre.setText("");
        textFieldDesc.setText("");
        textFieldFrec.setText("");
        textFieldHorario.setText("");
        textFieldMeta.setText("");
        textFieldUnidad.setText("");
        textFieldFecha.setText("");

        if(comboBoxTipo.getItemCount() > 0) {
            comboBoxTipo.setSelectedIndex(0); // Vuelve al primer item
        }

        // ¡LÓGICA DEL FILTRO!
        if (this.usuarioFiltrado != null) {
            // Si estamos en modo filtrado, no resetees el ComboBox de usuario
            comboBoxUsuario.setSelectedItem(this.usuarioFiltrado);
        } else {
            // Si estamos en modo global, resetea al primer usuario
            if (comboBoxUsuario.getItemCount() > 0) {
                comboBoxUsuario.setSelectedIndex(0);
            }
        }

        tablaHabitos.clearSelection(); // Deselecciona la fila de la tabla
    }
}
