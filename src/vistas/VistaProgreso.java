package vistas;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VistaProgreso extends JFrame {

    //No debería hacer falta tener ID en la vista pero lo dejamos porque es un atributo, habría que removerlo más adelante
    private JTextField textFieldID;
    //Todos los hábitos llenan el combo box y de ahi el usuario selecciona el que quiera registrar
    private JComboBox comboBoxHabito;
    // Revisar si hay algo mejor para las fechas que un JTextField
    private JTextField textFieldFechaReg;
    // Un enum tal vez, con opciones como COMPLETADO - NO COMPLETADO - FINALIZADO, etc
    private JComboBox comboBoxEstado;
    // Acá va un textArea porque a lo mejor hay que escribir bastante y es más cómodo
    private JTextArea textAreaObserv;
    // Acá iría en unidades lo que se hizo, ejemplo: 19 --pasos--
    private JTextField textFieldLogro;
    private JButton guardarButton;
    private JButton cancelarButton;
    private JButton buttonVolver;
    private JPanel panel1;

    private VistaMenu vistaMenuPadre;

    public VistaProgreso(VistaMenu vistaMenuPadre) {
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
                VistaProgreso.this.setVisible(false);
                vistaMenuPadre.setVisible(true);
            }
        });
    }
}
