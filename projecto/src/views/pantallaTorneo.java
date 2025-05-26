package views;

import models.Torneo;
import models.pantalla1;
import navegadores.Dispatcher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class pantallaTorneo extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textFieldNombreTorneo;
    private JTextField textFieldCantidadEquipos;
    private ArrayList<Torneo> torneos = new ArrayList<>();
    private Dispatcher dispatcher;
    private JTable tableTorneos;
    private DefaultTableModel tableModelTorneos;

    public pantallaTorneo(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;

        setTitle("Gestión de Torneos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 450);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menuTorneos = new JMenu("Opciones de Torneo");
        menuBar.add(menuTorneos);

        JMenuItem itemAgregar = new JMenuItem("Agregar Torneo");
        itemAgregar.addActionListener(e -> agregarTorneoDesdeCampos());
        menuTorneos.add(itemAgregar);

        JMenuItem itemModificar = new JMenuItem("Modificar Torneo");
        itemModificar.addActionListener(e -> modificarTorneo());
        menuTorneos.add(itemModificar);

        JMenuItem itemEliminar = new JMenuItem("Eliminar Torneo");
        itemEliminar.addActionListener(e -> eliminarTorneo());
        menuTorneos.add(itemEliminar);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel panelInput = new JPanel();
        panelInput.setBackground(new Color(255, 255, 128));
        panelInput.setBounds(10, 11, 564, 120);
        contentPane.add(panelInput);
        panelInput.setLayout(null);

        JLabel lblNombreTorneo = new JLabel("Nombre del Torneo:");
        lblNombreTorneo.setBounds(20, 20, 150, 20);
        panelInput.add(lblNombreTorneo);

        textFieldNombreTorneo = new JTextField();
        textFieldNombreTorneo.setBounds(180, 20, 200, 20);
        panelInput.add(textFieldNombreTorneo);
        textFieldNombreTorneo.setColumns(10);

        JLabel lblCantidadEquipos = new JLabel("Cantidad de Equipos:");
        lblCantidadEquipos.setBounds(20, 60, 150, 20);
        panelInput.add(lblCantidadEquipos);

        textFieldCantidadEquipos = new JTextField();
        textFieldCantidadEquipos.setBounds(180, 60, 100, 20);
        panelInput.add(textFieldCantidadEquipos);
        textFieldCantidadEquipos.setColumns(10);
        textFieldCantidadEquipos.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) || textFieldCantidadEquipos.getText().length() >= 3) {
                    e.consume();
                }
            }
        });

        String[] columnas = {"ID", "Nombre del Torneo", "Cantidad de Equipos"};
        tableModelTorneos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableTorneos = new JTable(tableModelTorneos);
        JScrollPane scrollPane = new JScrollPane(tableTorneos);
        scrollPane.setBounds(10, 140, 564, 200);
        contentPane.add(scrollPane);

        JButton btnVolver = new JButton("Volver a Principal");
        btnVolver.setBounds(400, 370, 150, 25);
        btnVolver.addActionListener(e -> {
            if (dispatcher != null) {
                dispatcher.dispatch("pantalla1");
                dispose();
            }
        });
        contentPane.add(btnVolver);

        cargarTorneosDesdeBD();
    }

    private void agregarTorneoDesdeCampos() {
        String nombre = textFieldNombreTorneo.getText().trim();
        String cantidadStr = textFieldCantidadEquipos.getText().trim();

        if (nombre.isEmpty() || cantidadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos.");
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            if (pantalla1.insertarTorneoBD(nombre, cantidad)) {
                JOptionPane.showMessageDialog(this, "Torneo agregado correctamente.");
                textFieldNombreTorneo.setText("");
                textFieldCantidadEquipos.setText("");
                cargarTorneosDesdeBD();
            } else {
                JOptionPane.showMessageDialog(this, "Error al agregar torneo.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cantidad debe ser un número válido.");
        }
    }

    private void cargarTorneosDesdeBD() {
        torneos = pantalla1.obtenerTodosLosTorneosBD(); // Debes tener este método en pantalla1
        tableModelTorneos.setRowCount(0);
        for (Torneo t : torneos) {
            tableModelTorneos.addRow(new Object[]{t.getId(), t.getNombre(), t.getCantidadEquipos()});
        }
    }

    private void modificarTorneo() {
        int fila = tableTorneos.getSelectedRow();
        if (fila >= 0) {
            int id = (int) tableModelTorneos.getValueAt(fila, 0);
            String nombreActual = (String) tableModelTorneos.getValueAt(fila, 1);
            int cantidadActual = (int) tableModelTorneos.getValueAt(fila, 2);

            String nuevoNombre = JOptionPane.showInputDialog(this, "Nuevo nombre del torneo:", nombreActual);
            String nuevaCantidadStr = JOptionPane.showInputDialog(this, "Nueva cantidad de equipos:", cantidadActual);

            if (nuevoNombre != null && nuevaCantidadStr != null) {
                try {
                    int nuevaCantidad = Integer.parseInt(nuevaCantidadStr.trim());
                    if (pantalla1.actualizarTorneoBD(id, nuevoNombre.trim(), nuevaCantidad)) {
                        JOptionPane.showMessageDialog(this, "Torneo actualizado correctamente.");
                        cargarTorneosDesdeBD();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al actualizar torneo.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Cantidad no válida.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un torneo para modificar.");
        }
    }

    private void eliminarTorneo() {
        int fila = tableTorneos.getSelectedRow();
        if (fila >= 0) {
            int id = (int) tableModelTorneos.getValueAt(fila, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar torneo?", "Confirmación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (pantalla1.eliminarTorneoBD(id)) {
                    JOptionPane.showMessageDialog(this, "Torneo eliminado.");
                    cargarTorneosDesdeBD();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar torneo.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un torneo para eliminar.");
        }
    }
}
