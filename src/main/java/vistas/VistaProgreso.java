package vistas;

import entidades.Habito;
import entidades.Progreso;
import excepciones.MiExcepcion;
import persistencia.implementaciones.HabitoDaoImpl;
import persistencia.implementaciones.ProgresoDaoImpl;
import persistencia.interfaces.HabitoDao;
import persistencia.interfaces.ProgresoDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class VistaProgreso extends JFrame {

    //No debería hacer falta tener ID en la vista pero lo dejamos porque es un atributo, habría que removerlo más adelante
    private JTextField textFieldID;
    //Todos los hábitos llenan el combo box y de ahi el usuario selecciona el que quiera registrar
    // le agregue <habito>
    private JComboBox<Habito> comboBoxHabito;
    // Revisar si hay algo mejor para las fechas que un JTextField
    private JTextField textFieldFechaReg;
    // Un enum tal vez, con opciones como COMPLETADO - NO COMPLETADO - FINALIZADO, etc
    // lo cambie de private JComboBox comboBoxEstado a:
    private JComboBox<String> comboBoxEstado;
    // Acá va un textArea porque a lo mejor hay que escribir bastante y es más cómodo
    private JTextArea textAreaObserv;
    // Acá iría en unidades lo que se hizo, ejemplo: 19 --pasos--
    private JTextField textFieldLogro;
    private JButton guardarButton;
    private JButton cancelarButton;
    private JButton buttonVolver;
    private JPanel panel1;
    private JTable tablaProgresos;
    private JButton editarButton;
    private JButton eliminarButton;

    private VistaMenu vistaMenuPadre;
    private HabitoDao habitoDao; // Usamos HabitoDao para guardar
    private ProgresoDao progresoDao;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public VistaProgreso(VistaMenu vistaMenuPadre) {
        this.vistaMenuPadre = vistaMenuPadre;
        // Solo necesitamos HabitoDao, ya que guardamos el Progreso
        // a través de su Hábito (padre).
        this.habitoDao = HabitoDaoImpl.getInstance();
        this.progresoDao = ProgresoDaoImpl.getInstance();


        setTitle("Registrar Progreso");
        setContentPane(panel1);
        //HIDE ON CLOSE para que no nos cierre toda la app
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        // Inicialización de la Vista
        textFieldID.setEnabled(false);
        cargarHabitosEnComboBox();
        cargarEstadosEnComboBox();
        refrescarTablaProgresos();
        limpiarCampos();

        //botones
        buttonVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VistaProgreso.this.setVisible(false);
                vistaMenuPadre.setVisible(true);
            }
        });

        guardarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarNuevoProgreso();
            }
        });

        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });

        editarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarProgreso();
            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarProgreso();
            }
        });

        //tablalistener
        tablaProgresos.getSelectionModel().addListSelectionListener(e -> {
            // Si el evento no se está ajustando y hay una fila seleccionada...
            if (!e.getValueIsAdjusting() && tablaProgresos.getSelectedRow() != -1) {
                int filaSeleccionada = tablaProgresos.getSelectedRow();
                Integer id = (Integer) tablaProgresos.getModel().getValueAt(filaSeleccionada, 0);

                try {
                    Progreso progresoSeleccionado = progresoDao.findById(id);
                    if (progresoSeleccionado == null) return;

                    // Rellenar todos los campos del formulario
                    textFieldID.setText(progresoSeleccionado.getId().toString());
                    textFieldLogro.setText(progresoSeleccionado.getLogro().toString());
                    textAreaObserv.setText(progresoSeleccionado.getObservaciones());

                    if (progresoSeleccionado.getFechaRegistro() != null) {
                        textFieldFechaReg.setText(dateFormat.format(progresoSeleccionado.getFechaRegistro()));
                    }

                    // Seleccionar los items correctos en los ComboBox
                    comboBoxEstado.setSelectedItem(progresoSeleccionado.getEstado());
                    comboBoxHabito.setSelectedItem(progresoSeleccionado.getHabito());

                } catch (MiExcepcion ex) {
                    JOptionPane.showMessageDialog(this, "Error al seleccionar el progreso: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    /**
     * Lógica para el botón Guardar
     */
    private void guardarNuevoProgreso() {
        try {
            // 1. LEER Y VALIDAR DATOS
            Habito habitoSeleccionado = (Habito) comboBoxHabito.getSelectedItem();
            String fechaStr = textFieldFechaReg.getText();
            String estado = (String) comboBoxEstado.getSelectedItem();
            String logroStr = textFieldLogro.getText();

            if (habitoSeleccionado == null) {
                throw new MiExcepcion("Debe seleccionar un hábito.");
            }
            if (fechaStr.trim().isEmpty()) {
                throw new MiExcepcion("La fecha de registro es obligatoria.");
            }
            if (estado == null) {
                throw new MiExcepcion("Debe seleccionar un estado.");
            }
            if (logroStr.trim().isEmpty()) {
                throw new MiExcepcion("El logro es obligatorio.");
            }

            // 2. PARSEO DE DATOS
            Date fechaRegistro;
            try {
                fechaRegistro = dateFormat.parse(fechaStr);
            } catch (ParseException ex) {
                throw new MiExcepcion("Formato de fecha incorrecto. Use yyyy-MM-dd");
            }

            Double logro;
            try {
                logro = Double.parseDouble(logroStr);
            } catch (NumberFormatException ex) {
                throw new MiExcepcion("El logro debe ser un número (ej: 2.5).");
            }

            String observaciones = textAreaObserv.getText();

            // 3. CREAR ENTIDAD Y RELACIONAR
            Progreso nuevoProgreso = new Progreso();
            nuevoProgreso.setFechaRegistro(fechaRegistro);
            nuevoProgreso.setEstado(estado);
            nuevoProgreso.setObservaciones(observaciones);
            nuevoProgreso.setLogro(logro);

            // Usamos el mtdo helper del padre (Hábito) para setear la relación
            habitoSeleccionado.addProgreso(nuevoProgreso);

            // 4. GUARDAR
            // Guardamos el Hábito (padre), y JPA guardará el Progreso (hijo) por cascada.
            habitoDao.update(habitoSeleccionado);

            JOptionPane.showMessageDialog(this, "Progreso registrado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            //actualizar vista
            refrescarTablaProgresos();
            limpiarCampos();

        } catch (MiExcepcion ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Lógica para el botón Editar (UPDATE)
     */
    private void editarProgreso() {
        try {
            // 1. Validar ID
            if (textFieldID.getText().isEmpty()) {
                throw new MiExcepcion("Seleccione un registro de progreso de la tabla para editar.");
            }
            Integer idProgreso = Integer.parseInt(textFieldID.getText());

            // 2. Leer y validar campos
            Habito habitoSeleccionado = (Habito) comboBoxHabito.getSelectedItem();
            String fechaStr = textFieldFechaReg.getText();
            String estado = (String) comboBoxEstado.getSelectedItem();
            String logroStr = textFieldLogro.getText();

            if (habitoSeleccionado == null) throw new MiExcepcion("Debe seleccionar un hábito.");
            if (fechaStr.trim().isEmpty()) throw new MiExcepcion("La fecha de registro es obligatoria.");
            if (estado == null) throw new MiExcepcion("Debe seleccionar un estado.");
            if (logroStr.trim().isEmpty()) throw new MiExcepcion("El logro es obligatorio.");

            // 3. Parsear
            Date fechaRegistro = dateFormat.parse(fechaStr);
            Double logro = Double.parseDouble(logroStr);
            String observaciones = textAreaObserv.getText();

            // 4. Buscar y Actualizar
            Progreso progresoAEditar = progresoDao.findById(idProgreso);
            if (progresoAEditar == null) {
                throw new MiExcepcion("El registro de progreso no existe o fue eliminado.");
            }

            // Asignar los nuevos valores
            progresoAEditar.setFechaRegistro(fechaRegistro);
            progresoAEditar.setEstado(estado);
            progresoAEditar.setObservaciones(observaciones);
            progresoAEditar.setLogro(logro);

            // Revisa si el Hábito (padre) cambió
            if (!progresoAEditar.getHabito().equals(habitoSeleccionado)) {
                // Esta lógica es compleja (actualizar 2 padres).
                // Por ahora, solo actualizamos el hijo, lo cual es más simple.
                progresoAEditar.setHabito(habitoSeleccionado);
            }

            // 5. Guardar
            progresoDao.update(progresoAEditar); // Actualizamos el progreso directamente

            JOptionPane.showMessageDialog(this, "Progreso modificado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // 6. Refrescar
            refrescarTablaProgresos();
            limpiarCampos();

        } catch (MiExcepcion ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha incorrecto. Use yyyy-MM-dd", "Error de Formato", JOptionPane.WARNING_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El logro debe ser un número (ej: 2.5).", "Error de Formato", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Lógica para el botón Eliminar (DELETE)
     */
    private void eliminarProgreso() {
        try {
            // 1. Verificar que se haya seleccionado un registro
            if (textFieldID.getText().isEmpty()) {
                throw new MiExcepcion("Seleccione un registro de la tabla para eliminar.");
            }
            Integer idProgreso = Integer.parseInt(textFieldID.getText());

            // 2. Pedir confirmación
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de que desea eliminar este registro de progreso?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (respuesta == JOptionPane.YES_OPTION) {
                // 3. Borrar de la BD
                progresoDao.delete(idProgreso);
                JOptionPane.showMessageDialog(this, "Registro eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                // 4. Actualizar la vista
                refrescarTablaProgresos();
                limpiarCampos();
            }

        } catch (MiExcepcion ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carga todos los hábitos en el JComboBox
     */
    private void cargarHabitosEnComboBox() {
        try {
            List<Habito> habitos = habitoDao.findAll();
            DefaultComboBoxModel<Habito> model = new DefaultComboBoxModel<>();

            if (habitos != null) {
                for (Habito h : habitos) {
                    model.addElement(h); // Se mostrará usando el Habito.toString()
                }
            }
            comboBoxHabito.setModel(model);
        } catch (MiExcepcion e) {
            System.out.println("Error al cargar hábitos en ComboBox: " + e.getMessage());
        }
    }

    /**
     * Carga los estados fijos en el JComboBox
     */
    private void cargarEstadosEnComboBox() {
        String[] estados = {"COMPLETADO", "NO COMPLETADO", "PARCIAL"};
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(estados);
        comboBoxEstado.setModel(model);
    }

    /**
     * Método para refrescar la JTable de Progresos
     */
    private void refrescarTablaProgresos() {
        try {
            List<Progreso> progresos = progresoDao.findAll();
            DefaultTableModel modelo = new DefaultTableModel();

            // Definimos las columnas
            modelo.addColumn("ID");
            modelo.addColumn("Fecha");
            modelo.addColumn("Estado");
            modelo.addColumn("Logro");
            modelo.addColumn("Hábito");

            if (progresos != null) {
                for (Progreso p : progresos) {
                    modelo.addRow(new Object[]{
                            p.getId(),
                            dateFormat.format(p.getFechaRegistro()),
                            p.getEstado(),
                            p.getLogro(),
                            p.getHabito().getNombre() // Mostramos el nombre del hábito
                    });
                }
            }
            tablaProgresos.setModel(modelo);

        } catch (MiExcepcion ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar la tabla de progresos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Limpia el formulario
     */
    private void limpiarCampos() {
        textFieldID.setText("");
        textFieldFechaReg.setText("");
        textAreaObserv.setText("");
        textFieldLogro.setText("");
        if (comboBoxHabito.getItemCount() > 0) {
            comboBoxHabito.setSelectedIndex(0);
        }
        if (comboBoxEstado.getItemCount() > 0) {
            comboBoxEstado.setSelectedIndex(0);
        }
        // Limpiar la selección de la tabla
        if (tablaProgresos != null) {
            tablaProgresos.clearSelection();
        }
    }
}