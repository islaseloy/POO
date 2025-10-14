import vistas.VistaMenu;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            /*
            * UIManager permite elegir un estilo para Swing que aplica a toda la aplicación
            * ahorrándonos el diseño, lo cual está muy bien
            * */
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.out.println("Error al establecer el Look and Feel: " + e);
        }

        VistaMenu vistaMenu = new VistaMenu();
        vistaMenu.setVisible(true);
    }
}