package vistas;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VistaUsuario extends JFrame{
    private JLabel labelID;
    private JTextField textFieldID;
    private JLabel labelName;
    private JTextField textFieldName;
    private JTextField textFieldEmail;
    private JPasswordField passwordField;//podria no ir
    private JTextField textFieldDate;
    private JList listHabitos;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JLabel labelEmail;
    private JLabel LabelPass;
    private JLabel LabelDate;
    private JLabel LabelHabitos;
    private JButton buttonVolver;
    private JPanel panel1;

    private VistaMenu vistaMenuPadre;
    public VistaUsuario(VistaMenu vistaMenuPadre) {
        this.vistaMenuPadre = vistaMenuPadre;

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
    }
}
