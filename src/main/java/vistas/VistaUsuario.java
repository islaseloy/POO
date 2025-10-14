package vistas;
import entidades.Usuario;
import excepciones.MiExcepcion;
import persistencia.implementaciones.UsuarioDaoImpl;
import persistencia.interfaces.UsuarioDao;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class VistaUsuario extends JFrame{
    private JLabel labelID;
    private JTextField textFieldID;
    private JLabel labelName;
    private JTextField textFieldName;
    private JTextField textFieldEmail;
    private JPasswordField passwordField;
    private JTextField textFieldDate;
    private JList listHabitos;
    private JButton btnGuardar;
    private JLabel labelEmail;
    private JLabel LabelPass;
    private JLabel LabelHabitos;
    private JButton buttonVolver;
    private JPanel panel1;
    private JTable table1;
    private JButton editarButton;
    private JButton eliminarButton;
    private VistaMenu vistaMenuPadre;
    private UsuarioDao dao;

    public VistaUsuario(VistaMenu vistaMenuPadre) {
        this.vistaMenuPadre = vistaMenuPadre;
        this.dao = UsuarioDaoImpl.getInstance();

        setTitle("Progreso");
        setContentPane(panel1);
        //HIDE ON CLOSE para que no nos cierre toda la app
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

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
                    Usuario nuevoUsuario = new Usuario(nombre, email, contrasena, new Date());

                    //Metodo del dao para guardar
                    dao.save(nuevoUsuario);

                    //Usamos listAll del DAO para ver que onda
                    System.out.println("USUARIOS CREADOS: ");
                    for (Usuario u : dao.findAll()){
                        System.out.println("ID: " + u.getId() + ", Nombre: " + u.getNombreDeUsuario() + ", Email: " + u.getEmail());
                    }

                } catch (MiExcepcion ex) {
                    //Va primero a buscar nuestra excepción según la definimos
                    System.out.println("ERROR : " + ex.getMessage());
                } catch (Exception ex){
                    //Busca cualquier otro horrible error que esperemos no suceda
                    System.out.println("ERROR PEOR : " + ex.getMessage());
                }
            }
        });

    }

    //Siempre importante una función para limpiar los campos
    private void limpiarCampos(){
        textFieldID.setText("");
        textFieldName.setText("");
        textFieldEmail.setText("");
        passwordField.setText("");
        textFieldDate.setText("");
    }
}
