package vistas;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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


    private VistaMenu vistaMenuPadre;

    public VistaHabito(VistaMenu vistaMenuPadre) {
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
                VistaHabito.this.setVisible(false);
                vistaMenuPadre.setVisible(true);
            }
        });
    }
}
