package views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import navegadores.inicio;
import models.pantalla1;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class pantallamostrar extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel tableModel;
    private ArrayList<inicio> jugadores;

    public pantallamostrar(ArrayList<inicio> jugadoresIniciales) {
        this.jugadores = (jugadoresIniciales != null) ? jugadoresIniciales : new ArrayList<>();

        setTitle("Jugadores Registrados");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 700, 400);
        setLocationRelativeTo(null);

        // Menú
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menuOpciones = new JMenu("Opciones");
        menuBar.add(menuOpciones);

        JMenuItem cargarItem = new JMenuItem("Actualizar desde BD");
        menuOpciones.add(cargarItem);
        cargarItem.addActionListener(e -> cargarJugadoresDesdeBD());

        JMenuItem cargarArchivoItem = new JMenuItem("Cargar Archivo");
        menuOpciones.add(cargarArchivoItem);
        cargarArchivoItem.addActionListener(e -> cargarArchivo());

        // Contenido
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout(10, 10));
        setContentPane(contentPane);

        // Tabla con DNI
        String[] columnas = { "DNI", "Nombre", "Apellido", "Dorsal" };
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel();
        JButton btnModificar = new JButton("Modificar");
        JButton btnBorrar = new JButton("Borrar");
        JButton btnVolver = new JButton("Volver");

        panelBotones.add(btnModificar);
        panelBotones.add(btnBorrar);
        panelBotones.add(btnVolver);
        contentPane.add(panelBotones, BorderLayout.SOUTH);

        cargarJugadoresDesdeLista();

        btnModificar.addActionListener(e -> {
            int fila = table.getSelectedRow();
            if (fila >= 0) {
                String dni = (String) tableModel.getValueAt(fila, 0);
                String nombreAnterior = (String) tableModel.getValueAt(fila, 1);
                String apellidoAnterior = (String) tableModel.getValueAt(fila, 2);
                int dorsalAnterior = (int) tableModel.getValueAt(fila, 3);

                String nuevoNombre = JOptionPane.showInputDialog("Nuevo nombre:", nombreAnterior);
                String nuevoApellido = JOptionPane.showInputDialog("Nuevo apellido:", apellidoAnterior);
                String nuevoDorsalStr = JOptionPane.showInputDialog("Nuevo dorsal:", dorsalAnterior);

                if (nuevoNombre == null || nuevoApellido == null || nuevoDorsalStr == null ||
                        nuevoNombre.trim().isEmpty() || nuevoApellido.trim().isEmpty() || nuevoDorsalStr.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int nuevoDorsal = Integer.parseInt(nuevoDorsalStr);
                    boolean actualizado = pantalla1.actualizarJugadorBD(
                        nombreAnterior, apellidoAnterior, dorsalAnterior,
                        nuevoNombre, nuevoApellido, nuevoDorsal
                    );
                    if (actualizado) {
                        JOptionPane.showMessageDialog(this, "Jugador actualizado correctamente.");
                        cargarJugadoresDesdeBD();
                    } else {
                        JOptionPane.showMessageDialog(this, "No se pudo actualizar el jugador en la BD.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Dorsal debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una fila primero.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Borrar
        btnBorrar.addActionListener(e -> {
            int fila = table.getSelectedRow();
            if (fila >= 0) {
                String dni = (String) tableModel.getValueAt(fila, 0);
                String nombre = (String) tableModel.getValueAt(fila, 1);
                String apellido = (String) tableModel.getValueAt(fila, 2);
                int dorsal = (int) tableModel.getValueAt(fila, 3);

                int confirm = JOptionPane.showConfirmDialog(this,
                        "¿Borrar jugador con DNI: " + dni + " - " + nombre + " " + apellido + ", dorsal " + dorsal + "?",
                        "Confirmación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (pantalla1.eliminarJugadorBD(nombre, apellido, dorsal)) {
                        JOptionPane.showMessageDialog(this, "Jugador eliminado correctamente.");
                        cargarJugadoresDesdeBD();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al eliminar jugador.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una fila primero.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnVolver.addActionListener(e -> dispose());
    }

    private void cargarJugadoresDesdeLista() {
        tableModel.setRowCount(0);
        for (inicio jugador : jugadores) {
            tableModel.addRow(new Object[]{
                jugador.getDni(),
                jugador.getNombre(),
                jugador.getApellido(),
                jugador.getDorsal()
            });
        }
    }

    private void cargarJugadoresDesdeBD() {
        jugadores = pantalla1.obtenerTodosLosJugadoresBD();
        cargarJugadoresDesdeLista();
    }

    private void cargarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo");

        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Archivos de texto (.txt)", "txt");
        FileNameExtensionFilter sqlFilter = new FileNameExtensionFilter("Archivos SQL (.sql)", "sql");
        fileChooser.addChoosableFileFilter(txtFilter);
        fileChooser.addChoosableFileFilter(sqlFilter);
        fileChooser.setFileFilter(txtFilter);

        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            String nombreArchivo = archivo.getName().toLowerCase();

            if (nombreArchivo.endsWith(".txt")) {
                handleTxtFile(archivo);
            } else if (nombreArchivo.endsWith(".sql")) {
                handleSqlFile(archivo);
            } else {
                JOptionPane.showMessageDialog(this, "Tipo de archivo no soportado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleTxtFile(File archivo) {
        int importados = 0, fallidos = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 3) {
                    try {
                        String nombre = datos[0].trim();
                        String apellido = datos[1].trim();
                        int dorsal = Integer.parseInt(datos[2].trim());

                        if (pantalla1.insertarJugadorBD(nombre, apellido, dorsal)) {
                            importados++;
                        } else {
                            fallidos++;
                        }
                    } catch (NumberFormatException ex) {
                        fallidos++;
                    }
                } else {
                    fallidos++;
                }
            }

            JOptionPane.showMessageDialog(this,
                importados + " jugadores importados.\n" +
                fallidos + " fallidos.\n" +
                "Actualizando tabla...");
            cargarJugadoresDesdeBD();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error leyendo archivo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSqlFile(File archivo) {
        JOptionPane.showMessageDialog(this, "No se permite ejecutar scripts SQL directamente.\n" +
                                              "Utiliza un gestor de base de datos externo para eso.", "Advertencia", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrar() {
        setVisible(true);
    }
}
