package vistas;

import controladores.HabitoController;
import entidades.Habito;
import entidades.Usuario;
import persistencia.implementaciones.HabitoDaoImpl;
import persistencia.implementaciones.UsuarioDaoImpl;
import persistencia.interfaces.HabitoDao;
import persistencia.interfaces.UsuarioDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.text.SimpleDateFormat;
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
    private JComboBox<Usuario> comboBoxUsuario;
    private JButton guardarButton;
    private JButton cancelarButton;
    private JButton buttonVolver;
    private JPanel panel1;
    private JTable tablaHabitos;
    private JButton editarBtn;
    private JButton eliminarButton;
    private JComboBox<Habito> comboBoxNombre;


    private VistaMenu vistaMenuPadre;

    // DAOs necesarios
    private UsuarioDao usuarioDao;
    private HabitoDao habitoDao;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private final HabitoController controller;

    private Usuario usuarioFiltrado = null;

    public VistaHabito(VistaMenu vistaMenuPadre) {
        this.vistaMenuPadre = vistaMenuPadre;
        this.controller = new HabitoController(this);


        // Inicializamos los DAOs
        this.usuarioDao = UsuarioDaoImpl.getInstance();
        this.habitoDao = HabitoDaoImpl.getInstance();

        setTitle("Gestión de Hábitos");
        setContentPane(panel1);
        //HIDE ON CLOSE para que no nos cierre toda la app
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        limpiarCampos();
        cargarTiposEnComboBox();

        tablaHabitos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                controller.habitoPersonalizadoSeleccionado();
            }
        });

        eliminarButton.addActionListener(e -> controller.eliminarHabitoPersonalizado());
        guardarButton.addActionListener(e -> controller.guardarHabitoPersonalizado());
        editarBtn.addActionListener(e -> controller.editarHabitoPersonalizado());

        buttonVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VistaHabito.this.setVisible(false);
                vistaMenuPadre.setVisible(true);
            }

        });

        //Esto envía al controller el dato del hábito seleccionado
        comboBoxNombre.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controller.catalogoHabitoSeleccionado();
            }
        });

        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });

        /*
        * Esto funciona como los subscribe de Angular básicamente
        * Se dispara cada vez que la ventana esta se vuelva la ventana activa (cuando abrimos o la apretamos, por ej)
        * Lo único que hace es cargar esos datos iniciales para evitar que si tocamos otra ventana los cambios no
        * se reflejen acá
        * */
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                controller.cargarDatosIniciales();
            }
        });
    }

    public void cargarUsuarios(List<Usuario> usuarios) {
        DefaultComboBoxModel<Usuario> model = new DefaultComboBoxModel<>();

        if (usuarios != null) {
            for (Usuario u : usuarios) {
                model.addElement(u);
            }
        }
        comboBoxUsuario.setModel(model);
    }

    public void actualizarTabla(DefaultTableModel model) {
        tablaHabitos.setModel(model);
    }

    public void mostrarMensaje(String mensaje, String titulo, int tipoMensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipoMensaje);
    }

    public Integer getIdHabitoPersonalizadoSeleccionado() {
        int filaSeleccionada = tablaHabitos.getSelectedRow();
        if (filaSeleccionada == -1) {
            return null;
        }
        return (Integer) tablaHabitos.getModel().getValueAt(filaSeleccionada, 0);
    }

    private void cargarTiposEnComboBox() {
        String[] tipos = {"Salud", "Ejercicio", "Estudio", "Trabajo", "Ocio", "Otros"};
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(tipos);
        comboBoxTipo.setModel(model);
    }

    public void cargarCatalogo(List<Habito> catalogo) {
        DefaultComboBoxModel<Habito> model = new DefaultComboBoxModel<>();
        if (catalogo != null) {
            for (Habito h : catalogo) {
                model.addElement(h);
            }
        }
        comboBoxNombre.setModel(model);
    }

    public Object getItemHabitoSeleccionado() {
        return comboBoxNombre.getSelectedItem();
    }

    public void limpiarCampos() {
        textFieldID.setText("");
        textFieldDesc.setText("");
        textFieldFrec.setText("");
        textFieldHorario.setText("");
        textFieldMeta.setText("");
        textFieldUnidad.setText("");
        textFieldFecha.setText("");
    }


    public String getDescripcion() { return textFieldDesc.getText(); }
    public String getTipo() { return (String) comboBoxTipo.getSelectedItem(); }
    public String getFrecuencia() { return textFieldFrec.getText(); }
    public String getHorario() { return textFieldHorario.getText(); }
    public String getMeta() { return textFieldMeta.getText(); }
    public String getUnidad() { return textFieldUnidad.getText(); }
    public String getFechaInicio() { return textFieldFecha.getText(); }
    public Usuario getUsuarioSeleccionado() { return (Usuario) comboBoxUsuario.getSelectedItem(); }

    public void setId(String id) {
        textFieldID.setText(id);
    }

    public void setNombreHabitoCatalogo(Habito habito) {
        comboBoxNombre.setSelectedItem(habito);
    }

    public void setFrecuencia(String texto) {
        textFieldFrec.setText(texto);
    }

    public void setHorario(String texto) {
        textFieldHorario.setText(texto);
    }

    public void setMeta(String texto) {
        textFieldMeta.setText(texto);
    }

    public void setUnidad(String texto) {
        textFieldUnidad.setText(texto);
    }

    public void setFechaInicio(Date fecha) {
        if (fecha != null) {
            // Usamos el mismo formato que usamos para guardar
            textFieldFecha.setText(new SimpleDateFormat("dd-MM-yyyy").format(fecha));
        } else {
            textFieldFecha.setText("");
        }
    }
    public void refrescarDatos() {
        controller.cargarDatosIniciales();
    }

    public void setUsuarioAsociado(Usuario usuario) {
        comboBoxUsuario.setSelectedItem(usuario);
    }

    public void setDescripcion(String texto) {
        textFieldDesc.setText(texto);
    }

    public void setTipo(String tipo) {
        comboBoxTipo.setSelectedItem(tipo);
    }
}