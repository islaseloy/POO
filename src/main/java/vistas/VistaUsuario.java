package vistas;
import entidades.Usuario;
import excepciones.MiExcepcion;
import persistencia.implementaciones.UsuarioDaoImpl;
import persistencia.interfaces.UsuarioDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
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
    private VistaMenu vistaMenuPadre;
    private UsuarioDao dao;

    /*
    * PODRÍA ESTAR BASTANTE MEJOR
    * La lógica está toda dentro de los botones, y si bien es poca, no es lo ideal tampoco
    *
    * */

    public VistaUsuario(VistaMenu vistaMenuPadre) {
        this.vistaMenuPadre = vistaMenuPadre;
        this.dao = UsuarioDaoImpl.getInstance();

        setTitle("Progreso");
        setContentPane(panel1);
        //HIDE ON CLOSE para que no nos cierre toda la app
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        refrescarTabla();
        cargarHabitosPlaceholder();

        //El listener a continuación es para que al hacer click en una línea, se populen los textField, es medio complejo
        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaUsuarios.getSelectedRow() != -1) {
                int filaSeleccionada = tablaUsuarios.getSelectedRow();
                Integer id = (Integer) tablaUsuarios.getModel().getValueAt(filaSeleccionada, 0);

                try {
                    Usuario usuarioSeleccionado = dao.findById(id);
                    if (usuarioSeleccionado == null) return;

                    textFieldID.setText(usuarioSeleccionado.getId().toString());
                    textFieldName.setText(usuarioSeleccionado.getNombreDeUsuario());
                    textFieldEmail.setText(usuarioSeleccionado.getEmail());
                    passwordField.setText("");

                    listHabitos.clearSelection();

                    String habitosGuardadosTexto = usuarioSeleccionado.getHabitos();

                    if (habitosGuardadosTexto != null && !habitosGuardadosTexto.isEmpty()) {
                        List<String> habitosGuardados = Arrays.asList(habitosGuardadosTexto.split(", "));

                        DefaultListModel<String> modeloLista = (DefaultListModel<String>) listHabitos.getModel();
                        for (int i = 0; i < modeloLista.getSize(); i++) {
                            String habitoEnLista = modeloLista.getElementAt(i);
                            if (habitosGuardados.contains(habitoEnLista)) {
                                listHabitos.addSelectionInterval(i, i);
                            }
                        }
                    }
                } catch (MiExcepcion ex) {
                    System.out.println("Error en la seleccionar el habito: " + ex.getMessage());
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

        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String nombre = textFieldName.getText();
                    String email = textFieldEmail.getText();
                    //Esta es la forma correcta para agarrar una password
                    String contrasena = new String(passwordField.getPassword());
                    String habitos = listHabitos.getSelectedValue().toString();

                    // Validación de datos no muy completa
                    if(nombre.trim().isEmpty()){
                        throw new MiExcepcion("El nombre de usuario es obligatorio.");
                    }
                    if(email.trim().isEmpty()){
                        throw new MiExcepcion("El email es obligatorio");
                    }
                    if(contrasena.trim().isEmpty()){
                        throw new MiExcepcion("La contraseña es obligatoria.");
                    }
                    Usuario nuevoUsuario = new Usuario(nombre, email, contrasena, habitos, new Date());

                    //Metodo del dao para guardar
                    dao.save(nuevoUsuario);

                    //Usamos listAll del DAO para ver que onda
                    System.out.println("USUARIOS CREADOS: ");
                    for (Usuario u : dao.findAll()){
                        System.out.println("ID: " + u.getId() + ", Nombre: " + u.getNombreDeUsuario() + ", Email: " + u.getEmail());
                    }

                    refrescarTabla();

                } catch (MiExcepcion ex) {
                    //Va primero a buscar nuestra excepción según la definimos
                    System.out.println("ERROR : " + ex.getMessage());
                } catch (Exception ex){
                    //Busca cualquier otro horrible error que esperemos no suceda
                    System.out.println("ERROR PEOR : " + ex.getMessage());
                }
            }
        });

        nuevoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });

        editarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (textFieldID.getText().isEmpty()) {
                        throw new MiExcepcion("Seleccione un usuario para editar.");
                    }
                    Integer idUsuario = Integer.parseInt(textFieldID.getText());

                    Usuario usuarioSeleccionado = dao.findById(idUsuario);
                    if (usuarioSeleccionado == null) {
                        throw new MiExcepcion("El usuario no existe");
                    }

                    usuarioSeleccionado.setNombreDeUsuario(textFieldName.getText());
                    usuarioSeleccionado.setEmail(textFieldEmail.getText());

                    String contrasena = new String(passwordField.getPassword());
                    if(!contrasena.isEmpty()){
                        usuarioSeleccionado.setContrasena(contrasena);
                    }

                    //Arrays puede parecer medio complejo pero es crucial para gestionar los hábitos porque bien
                    //pueden ser más de uno

                    List<String> habitosGuardados = listHabitos.getSelectedValuesList();
                    String habitosGuardadosTexto = String.join(", ",habitosGuardados);
                    usuarioSeleccionado.setHabitos(habitosGuardadosTexto);

                    dao.update(usuarioSeleccionado);

                    System.out.println("Usuario modificado con éxito!!");

                    refrescarTabla();

                } catch (MiExcepcion ex){
                    System.out.println("Error al editar: " + ex.getMessage());
                }
            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (textFieldID.getText().isEmpty()) {
                        throw new MiExcepcion("Debe seleccionar un usuario de la lista para eliminar");
                    }

                    //Ninguna ciencia, va y lo borra
                    Integer id = Integer.parseInt(textFieldID.getText());
                    dao.delete(id);

                    refrescarTabla();
                    limpiarCampos();


                } catch (MiExcepcion ex){
                    System.out.println("ERROR AL ELIMINAR : " + ex.getMessage());
                }
            }
        });
    }

    //El DAO está en inglés y las demás funciones en español, fantástico
    private void refrescarTabla(){
        //Vamos pasito a pasito
        try {
            //Trae todos los usuarios con el dao.findAll(); y los mete en una lista
            List<Usuario> usuarios = dao.findAll();

            //Crea la tabla modelo
            DefaultTableModel modelo = new DefaultTableModel();

            //Añadimos las columnas
            modelo.addColumn("ID");
            modelo.addColumn("Nombre");
            modelo.addColumn("Email");
            modelo.addColumn("Hábitos");

            //Si hay usuarios, hace un for para ir añadiendo filas a la tabla
            if(usuarios != null){
                for (Usuario u : usuarios){
                    modelo.addRow(new Object[]{
                       u.getId(),
                       u.getNombreDeUsuario(),
                       u.getEmail(),
                       u.getHabitos()
                    });
                }
            }

            //Le da a la tabla de la vista la tabla que armamos
            tablaUsuarios.setModel(modelo);

        } catch (MiExcepcion ex) {
            System.out.println("ERROR AL CARGAR USUARIOS!! : " + ex.getMessage());
        }
    }

    //Siempre importante una función para limpiar los campos
    private void limpiarCampos(){
        textFieldID.setText("");
        textFieldName.setText("");
        textFieldEmail.setText("");
        passwordField.setText("");
    }

    //Solo sirve para que haya algún hábito y nada más, todavía hay que armar la otra entidad
    private void cargarHabitosPlaceholder(){
        String[] habitosEjemplo = {
                "Tomar 2 litros de agua",
                "Leer",
                "Ejercitarse",
        };

        DefaultListModel modelo = new DefaultListModel();

        for (String habito: habitosEjemplo){
            modelo.addElement(habito);
        }

        listHabitos.setModel(modelo);
    }
}
