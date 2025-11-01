package vistas;
import controladores.UsuarioController;
import entidades.Habito;
import entidades.HabitoPersonalizado;
import entidades.Usuario;
import excepciones.MiExcepcion;
import persistencia.implementaciones.UsuarioDaoImpl;
import persistencia.interfaces.UsuarioDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

public class VistaUsuario extends JFrame{
    private JLabel labelID;
    private JTextField textFieldID;
    private JLabel labelName;
    private JTextField textFieldName;
    private JTextField textFieldEmail;
    private JPasswordField passwordField;
    private JList listHabitos;
    private JButton btnGuardar;
    private JLabel labelEmail;
    private JLabel LabelPass;
    private JLabel LabelHabitos;
    private JButton buttonVolver;
    private JPanel panel1;
    private JButton editarButton;
    private JButton eliminarButton;
    private JButton nuevoButton;
    private JTable tablaUsuarios;
    private JButton gestionarHabitosButton;
    private VistaMenu vistaMenuPadre;
    private UsuarioDao dao;
    private final UsuarioController controller;

    /*
    * PODRÍA ESTAR BASTANTE MEJOR
    * La lógica está toda dentro de los botones, y si bien es poca, no es lo ideal tampoco
    *
    * */

    public VistaUsuario(VistaMenu vistaMenuPadre) {
        this.vistaMenuPadre = vistaMenuPadre;
        this.dao = UsuarioDaoImpl.getInstance();

        setTitle("Gestión de Usuarios");
        setContentPane(panel1);
        //HIDE ON CLOSE para que no nos cierre toda la app
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        this.controller = new UsuarioController(this);

        /*
        * Tal vez estaría bien mover esto al controler, porque encima es bastante complejo
        * */
        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            // Estas dos líneas evitan que el evento se dispare múltiples veces y verifican que una fila esté seleccionada
            if (!e.getValueIsAdjusting() && tablaUsuarios.getSelectedRow() != -1) {

                // Obtenemos el ID de la fila seleccionada en la JTable
                int filaSeleccionada = tablaUsuarios.getSelectedRow();
                Integer id = (Integer) tablaUsuarios.getModel().getValueAt(filaSeleccionada, 0);

                try {
                    Usuario usuarioSeleccionado = dao.findById(id);
                    if (usuarioSeleccionado == null) return;

                    textFieldID.setText(usuarioSeleccionado.getId().toString());
                    textFieldName.setText(usuarioSeleccionado.getNombreDeUsuario());
                    textFieldEmail.setText(usuarioSeleccionado.getEmail());
                    passwordField.setText(""); // Limpiamos la contraseña por seguridad


                    DefaultListModel<String> modeloLista = new DefaultListModel<>();

                    List<HabitoPersonalizado> habitosDelUsuario = usuarioSeleccionado.getHabitosPersonalizados();

                    if (habitosDelUsuario != null && !habitosDelUsuario.isEmpty()) {
                        for (HabitoPersonalizado hp : habitosDelUsuario) {
                            modeloLista.addElement(hp.getHabitoBase().getNombre());
                        }
                    }

                    listHabitos.setModel(modeloLista);

                } catch (MiExcepcion ex) {
                    System.out.println("Error al seleccionar el usuario: " + ex.getMessage());
                }
            }
        });

        buttonVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VistaUsuario.this.setVisible(false);
                vistaMenuPadre.setVisible(true);
            }
        });


        nuevoButton.addActionListener(e -> controller.limpiarCampos());

        btnGuardar.addActionListener(e -> controller.guardarUsuario());

        editarButton.addActionListener(e -> controller.editarUsuario());

        eliminarButton.addActionListener(e -> controller.eliminarUsuario());
    }

    public void actualizarTabla(DefaultTableModel model) {
        tablaUsuarios.setModel(model);
    }

    public void limpiarFormulario() {
        textFieldID.setText("");
        textFieldName.setText("");
        textFieldEmail.setText("");
        passwordField.setText("");
        tablaUsuarios.clearSelection();
        listHabitos.setModel(new DefaultListModel<>());
    }

    public void mostrarMensaje(String mensaje, String titulo, int tipoMensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipoMensaje);
    }
    public void refrescarDatos() {
        // Llama al método del controlador que ya sabe cómo recargar la tabla
        controller.refrescarTabla();
    }

    public String getIdUsuario() {
        return textFieldID.getText();
    }

    public String getNombreUsuario() {
        return textFieldName.getText();
    }

    public String getEmail() {
        return textFieldEmail.getText();
    }

    public String getContrasena() {
        return new String(passwordField.getPassword());
    }
}
