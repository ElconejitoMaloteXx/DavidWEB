package views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import models.equipo;
import navegadores.ConexionPostgresUsuario;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PantallaMostrarequipos extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel tableModel;

    private ArrayList<equipo> equipos = new ArrayList<>();

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                PantallaMostrarequipos frame = new PantallaMostrarequipos();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public PantallaMostrarequipos() {
        setTitle("Listado de Equipos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menuArchivo = new JMenu("Archivo");
        menuBar.add(menuArchivo);

        JMenuItem cargarItem = new JMenuItem("Cargar desde archivo...");
        JMenuItem guardarItem = new JMenuItem("Guardar en archivo...");
        menuArchivo.add(cargarItem);
        menuArchivo.add(guardarItem);

        JMenu menuEditar = new JMenu("Editar");
        menuBar.add(menuEditar);

        JMenuItem btnModificar = new JMenuItem("Modificar");
        JMenuItem btnBorrar = new JMenuItem("Borrar");
        JMenuItem btnGuardar = new JMenuItem("Guardar");
        JMenuItem btnVolver = new JMenuItem("Volver Atrás");

        menuEditar.add(btnModificar);
        menuEditar.add(btnBorrar);
        menuEditar.add(btnGuardar);
        menuEditar.add(btnVolver);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout(10, 10));
        setContentPane(contentPane);

        String[] columnas = { "Nombre del equipo", "Cantidad de jugadores" };
        tableModel = new DefaultTableModel(columnas, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        cargarItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("SQL Files", "sql"));

            int seleccion = fileChooser.showOpenDialog(null);
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                String extension = getFileExtension(archivo);
                if (extension.equals("txt")) {
                    cargarDesdeArchivoTxt(archivo);
                } else if (extension.equals("sql")) {
                    cargarDesdeArchivoSql(archivo);
                }
            }
        });

        guardarItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int seleccion = fileChooser.showSaveDialog(null);
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        String nombre = tableModel.getValueAt(i, 0).toString();
                        String cantidad = tableModel.getValueAt(i, 1).toString();
                        pw.println(nombre + "," + cantidad);
                    }
                    JOptionPane.showMessageDialog(this, "Archivo guardado correctamente.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error al guardar archivo: " + ex.getMessage());
                }
            }
        });

        btnModificar.addActionListener(e -> {
            int fila = table.getSelectedRow();
            if (fila >= 0) {
                String nuevoNombre = JOptionPane.showInputDialog("Nuevo nombre del equipo:", tableModel.getValueAt(fila, 0));
                String nuevaCantidad = JOptionPane.showInputDialog("Nueva cantidad de jugadores:", tableModel.getValueAt(fila, 1));
                try {
                    int cantidad = Integer.parseInt(nuevaCantidad);
                    tableModel.setValueAt(nuevoNombre, fila, 0);
                    tableModel.setValueAt(cantidad, fila, 1);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Cantidad no válida.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un equipo.");
            }
        });

        btnBorrar.addActionListener(e -> {
            int fila = table.getSelectedRow();
            if (fila >= 0) {
                int opcion = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar el equipo seleccionado?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
                if (opcion == JOptionPane.YES_OPTION) {
                    tableModel.removeRow(fila);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un equipo para borrar.");
            }
        });

        btnGuardar.addActionListener(e -> {
            equipos.clear();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String nombre = tableModel.getValueAt(i, 0).toString();
                int cantidad = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
                equipos.add(new equipo(nombre, cantidad));
            }
            JOptionPane.showMessageDialog(this, "Cambios guardados en memoria.");
        });

        btnVolver.addActionListener(e -> {
            dispose();
        });

        cargarEquiposDesdeBD();
    }

    private void cargarEquiposDesdeBD() {
        equipos.clear();
        tableModel.setRowCount(0);

        String sql = "SELECT nombre, numero_jugadores FROM equipo";

        try (Connection conn = ConexionPostgresUsuario.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                int cantidad = rs.getInt("numero_jugadores");

                equipo eq = new equipo(nombre, cantidad);
                equipos.add(eq);
                tableModel.addRow(new Object[]{nombre, cantidad});
            }

            JOptionPane.showMessageDialog(this, "Equipos cargados desde la base de datos.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar desde BD: " + e.getMessage());
        }   
    }

    private void cargarDesdeArchivoTxt(File archivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            tableModel.setRowCount(0);
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 2) {
                    String nombre = partes[0].trim();
                    int cantidad = Integer.parseInt(partes[1].trim());
                    tableModel.addRow(new Object[] { nombre, cantidad });
                }
            }
            JOptionPane.showMessageDialog(this, "Equipos cargados desde el archivo .txt.");
        } catch (IOException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar archivo: " + ex.getMessage());
        }
    }

    private void cargarDesdeArchivoSql(File archivo) {
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }
}
