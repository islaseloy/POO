package vistas;

import controladores.ProgresoController;
import entidades.HabitoPersonalizado;
import entidades.Usuario;
import entidades.Progreso;
//entiendo que los dao ya no se necesitan en esta vista
//import persistencia.implementaciones.HabitoDaoImpl;
//import persistencia.implementaciones.ProgresoDaoImpl;
//import persistencia.interfaces.HabitoDao;
//import persistencia.interfaces.ProgresoDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class VistaProgreso extends JFrame {

    //No debería hacer falta tener ID en la vista pero lo dejamos porque es un atributo, habría que removerlo más adelante
    private JTextField textFieldID;
    //Todos los hábitos llenan el combo box y de ahi el usuario selecciona el que quiera registrar
    // le agregue <habito>
    private JComboBox<HabitoPersonalizado> comboBoxHabito;
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
    private JComboBox<Usuario> comboBoxUsuario;

    private VistaMenu vistaMenuPadre;
    //private HabitoDao habitoDao;
    //private ProgresoDao progresoDao;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private final ProgresoController controller;

    public VistaProgreso(VistaMenu vistaMenuPadre) {
        this.vistaMenuPadre = vistaMenuPadre;
        //this.habitoDao = HabitoDaoImpl.getInstance();
        //this.progresoDao = ProgresoDaoImpl.getInstance();
        //la vista ahora se maneja por el controller
        this.controller = new ProgresoController(this);


        setTitle("Registrar Progreso");
        setContentPane(panel1);
        //HIDE ON CLOSE para que no nos cierre toda la app
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        textFieldID.setEnabled(false);
        
        cargarEstadosEnComboBox();
        controller.cargarDatosIniciales();

        /*
        Igual que en el otro caso, esto es un disparador que le manda al controler el SELECTED
        * */
        comboBoxUsuario.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.usuarioSeleccionado();
            }
        });

        guardarButton.addActionListener(e -> controller.guardarProgreso());
        editarButton.addActionListener(e -> controller.editarProgreso());
        eliminarButton.addActionListener(e -> controller.eliminarProgreso());
        cancelarButton.addActionListener(e -> limpiarFormulario());

        buttonVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VistaProgreso.this.setVisible(false);
                vistaMenuPadre.setVisible(true);
            }
        });

        tablaProgresos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaProgresos.getSelectedRow() != -1) {
                // Cuando se selecciona una fila, le avisamos al controlador
                controller.progresoSeleccionado();
            }
        });
    }

    private void cargarEstadosEnComboBox() {
        String[] estados = {"COMPLETADO", "NO COMPLETADO", "PARCIAL"};
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(estados);
        comboBoxEstado.setModel(model);
    }

    public void cargarHabitosPersonalizados(List<HabitoPersonalizado> habitos) {
        DefaultComboBoxModel<HabitoPersonalizado> model = new DefaultComboBoxModel<>();
        model.addElement(null);
        if (habitos != null) {
            for (HabitoPersonalizado hp : habitos) {
                model.addElement(hp);
            }
        }
        comboBoxHabito.setModel(model);
    }

    public void cargarUsuarios(List<Usuario> usuarios) {
        DefaultComboBoxModel<Usuario> model = new DefaultComboBoxModel<>();
        model.addElement(null);
        if (usuarios != null) {
            for (Usuario u : usuarios) {
                model.addElement(u);
            }
        }
        comboBoxUsuario.setModel(model);
    }

    public void actualizarTabla(DefaultTableModel model) {
        tablaProgresos.setModel(model);
    }

    public void mostrarMensaje(String mensaje, String titulo, int tipoMensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipoMensaje);
    }

    public void habilitarComboHabitos(boolean habilitar) {
        comboBoxHabito.setEnabled(habilitar);
    }

    public HabitoPersonalizado getHabitoPersonalizadoSeleccionado() {
        return (HabitoPersonalizado) comboBoxHabito.getSelectedItem();
    }

    public Usuario getUsuarioSeleccionado() {
        return (Usuario) comboBoxUsuario.getSelectedItem();
    }

    public String getFechaRegistro() {
        return textFieldFechaReg.getText();
    }

    public String getEstadoSeleccionado() {
        return (String) comboBoxEstado.getSelectedItem();
    }

    public String getObservaciones() {
        return textAreaObserv.getText();
    }

    public String getLogro() {
        return textFieldLogro.getText();
    }

    public Integer getIdProgreso() {
        try {
            return Integer.parseInt(textFieldID.getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void setFormulario(Progreso p) {
        textFieldID.setText(p.getId().toString());
        textFieldFechaReg.setText(dateFormat.format(p.getFechaRegistro()));
        textFieldLogro.setText(p.getLogro().toString());
        textAreaObserv.setText(p.getObservaciones());
        comboBoxEstado.setSelectedItem(p.getEstado());

        // Selecciona el usuario y luego el hábito (para asegurar que el combo de hábito tenga info)
        comboBoxUsuario.setSelectedItem(p.getHabitoPersonalizado().getUsuario());
        comboBoxHabito.setSelectedItem(p.getHabitoPersonalizado());
    }
    public void refrescarDatos() {
        controller.cargarDatosIniciales();
    }

    public void limpiarFormulario() {
        textFieldID.setText("");
        textFieldFechaReg.setText("");
        textAreaObserv.setText("");
        textFieldLogro.setText("");
        comboBoxEstado.setSelectedIndex(0);
        comboBoxUsuario.setSelectedIndex(0); //dispara el listener y limpiará/deshabilitará el combo de hábitos
        tablaProgresos.clearSelection();
    }
}