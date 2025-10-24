package vistas;

import entidades.Habito;
import entidades.Usuario;
import excepciones.MiExcepcion;
import persistencia.implementaciones.HabitoDaoImpl;
import persistencia.implementaciones.UsuarioDaoImpl;
import persistencia.interfaces.HabitoDao;
import persistencia.interfaces.UsuarioDao;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

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

    // DAOs necesarios
    private UsuarioDao usuarioDao;
    private HabitoDao habitoDao;

    public VistaHabito(VistaMenu vistaMenuPadre) {
        this.vistaMenuPadre = vistaMenuPadre;

        // Inicializamos los DAOs
        this.usuarioDao = UsuarioDaoImpl.getInstance();
        this.habitoDao = HabitoDaoImpl.getInstance();

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

        // Llamamos a un mtdo para cargar los usuarios en el ComboBox
        cargarUsuariosEnComboBox();

        // Implementamos el btn Guardar
        guardarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // 1. Recolectar datos de la vista
                    String nombre = textFieldNombre.getText();
                    String desc = textFieldDesc.getText();
                    String tipo = comboBoxTipo.getSelectedItem().toString(); // Asumiendo que cargaste items
                    String frec = textFieldFrec.getText();
                    String horario = textFieldHorario.getText();
                    Double meta = Double.parseDouble(textFieldMeta.getText()); // Cuidado, falta validación
                    String unidad = textFieldUnidad.getText();
                    // TODO: Parsear la fecha correctamente, por ahora usamos new Date()
                    Date fechaInicio = new Date();

                    // 2. Obtener el Usuario seleccionado del ComboBox
                    Usuario usuarioSeleccionado = (Usuario) comboBoxUsuario.getSelectedItem();

                    if (usuarioSeleccionado == null) {
                        throw new MiExcepcion("Debe seleccionar un usuario asociado.");
                    }

                    // 3. Crear la nueva entidad Habito
                    Habito nuevoHabito = new Habito(nombre, desc, tipo, frec, horario, meta, unidad, fechaInicio);

                    // 4. Establecer la relación
                    // Esto es crucial: le decimos al hábito quién es su dueño
                    nuevoHabito.setUsuario(usuarioSeleccionado);

                    // x el helper aca podriamos hacer link con la documentacion
                    usuarioSeleccionado.addHabito(nuevoHabito);

                    // 5. Guardar el hábito (JPA se encarga del usuario)
                    habitoDao.save(nuevoHabito);

                    JOptionPane.showMessageDialog(null, "Hábito guardado con éxito!");
                    // TODO: Limpiar campos o refrescar tablas

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Error en el formato de la Meta, debe ser un número.");
                } catch (MiExcepcion ex) {
                    JOptionPane.showMessageDialog(null, "Error al guardar: " + ex.getMessage());
                }
            }
        });
    }

    // Mtdo para llenar el JComboBox de Usuarios
    private void cargarUsuariosEnComboBox() {
        try {
            List<Usuario> usuarios = usuarioDao.findAll();
            DefaultComboBoxModel<Usuario> model = new DefaultComboBoxModel<>();

            if (usuarios != null) {
                for (Usuario u : usuarios) {
                    model.addElement(u); // Añadimos el objeto Usuario completo
                }
            }
            comboBoxUsuario.setModel(model); // Gracias al toString() modificado, se va a ver bien

        } catch (MiExcepcion e) {
            System.out.println("Error al cargar usuarios en el ComboBox: " + e.getMessage());
        }
    }

}
