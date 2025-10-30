package vistas;

import javax.swing.*;
import entidades.Usuario;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VistaMenu extends JFrame {
    private JButton MENUHABITOSButton;
    private JButton MENUPROGRESOButton;
    private JButton MENUUSUARIOSButton;
    private JPanel panel1;

    private VistaUsuario vistaUsuario;
    private VistaHabito vistaHabito;
    private VistaProgreso vistaProgreso;

    public VistaMenu() {
        setTitle("Menú");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        //Esto para centrarla
        setLocationRelativeTo(null);

        vistaUsuario = new VistaUsuario(this);
        vistaHabito = new VistaHabito(this);
        vistaProgreso = new VistaProgreso(this);

        MENUHABITOSButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VistaMenu.this.setVisible(false);
                vistaHabito.setVisible(true);
            }
        });
        MENUPROGRESOButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VistaMenu.this.setVisible(false);
                vistaProgreso.setVisible(true);
            }
        });
        MENUUSUARIOSButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VistaMenu.this.setVisible(false);
                vistaUsuario.setVisible(true);
            }
        });
    }

        /**
         * Este método es llamado por VistaUsuario para abrir la
         * ventana de hábitos en "modo filtrado".
         * @param usuario El usuario seleccionado en VistaUsuario.
         */
        public void abrirHabitosParaUsuario(Usuario usuario) {
            // 1. Llama al método especial de vistaHabito
            vistaHabito.mostrarHabitosDe(usuario);

            // 2. Muestra la vista de hábitos
            vistaHabito.setVisible(true);

            // 3. Oculta el menú (o la vista de usuario, que ya se ocultó)
            this.setVisible(false);
        }
    }
