package models;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import navegadores.ConexionPostgresUsuario;
import navegadores.Dispatcher;
import navegadores.inicio;
import views.pantallamostrar;

import java.awt.event.*;
import java.util.ArrayList;
import java.awt.Color;
import java.sql.*;

public class pantalla2 extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;
    private JTextField textField_1;
    private JTextField textField_2;

    private ArrayList<inicio> jugadores = new ArrayList<>();
    private Dispatcher dispatcher;

    public pantalla2(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;

        setBackground(new Color(255, 255, 128));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menuJugador = new JMenu("Jugador");
        menuBar.add(menuJugador);

        JMenuItem menuAgregar = new JMenuItem("Agregar Jugador");
        menuJugador.add(menuAgregar);
        menuAgregar.addActionListener(e -> agregarJugador());

        JMenuItem menuBuscar = new JMenuItem("Buscar Jugador");
        menuJugador.add(menuBuscar);
        menuBuscar.addActionListener(e -> {
            String filtro = JOptionPane.showInputDialog(this, "Introduce el nombre, apellido o dorsal del jugador a buscar:");
            if (filtro != null && !filtro.trim().isEmpty()) {
                ArrayList<inicio> jugadoresBuscados = buscarJugadoresBD(filtro.trim());
                if (jugadoresBuscados.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No se encontraron jugadores con ese criterio.");
                } else {
                    pantallamostrar ventanaMostrar = new pantallamostrar(jugadoresBuscados);
                    ventanaMostrar.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, ingresa un criterio de búsqueda.");
            }
        });

        JMenuItem menuMostrar = new JMenuItem("Mostrar Jugadores");
        menuJugador.add(menuMostrar);
        menuMostrar.addActionListener(e -> {
            ArrayList<inicio> jugadoresTodos = obtenerTodosJugadoresBD();
            if (jugadoresTodos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay jugadores registrados en la base de datos.");
            } else {
                pantallamostrar ventanaMostrar = new pantallamostrar(jugadoresTodos);
                ventanaMostrar.setVisible(true);
            }
        });

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 128));
        panel.setBounds(0, 0, 434, 250);
        contentPane.add(panel);
        panel.setLayout(null);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(20, 30, 80, 20);
        panel.add(lblNombre);

        textField = new JTextField();
        textField.setBounds(110, 30, 120, 20);
        panel.add(textField);
        textField.setColumns(10);
        textField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (textField.getText().length() >= 20) {
                    e.consume();
                }
            }
        });

        JLabel lblApellido = new JLabel("Apellido:");
        lblApellido.setBounds(20, 70, 80, 20);
        panel.add(lblApellido);

        textField_1 = new JTextField();
        textField_1.setBounds(110, 70, 120, 20);
        panel.add(textField_1);
        textField_1.setColumns(10);
        textField_1.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (textField_1.getText().length() >= 20) {
                    e.consume();
                }
            }
        });

        JLabel lblDorsal = new JLabel("Dorsal:");
        lblDorsal.setBounds(20, 110, 80, 20);
        panel.add(lblDorsal);

        textField_2 = new JTextField();
        textField_2.setBounds(110, 110, 86, 20);
        panel.add(textField_2);
        textField_2.setColumns(10);
        textField_2.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) || textField_2.getText().length() >= 3) {
                    e.consume();
                }
            }
        });

        JButton btnVolverInicio = new JButton("Volver al Inicio");
        btnVolverInicio.setBounds(250, 200, 140, 30);
        panel.add(btnVolverInicio);
        btnVolverInicio.addActionListener(e -> {
            if (dispatcher != null) {
                dispatcher.dispatch("pantalla1");
            }
            dispose();
        });
    }

    private void agregarJugador() {
        String nombre = textField.getText().trim();
        String apellido = textField_1.getText().trim();
        String dorsalStr = textField_2.getText().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || dorsalStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.");
            return;
        }

        try {
            int dorsal = Integer.parseInt(dorsalStr);
            inicio nuevoJugador = new inicio(nombre, apellido, dorsal);
            jugadores.add(nuevoJugador);
            JOptionPane.showMessageDialog(this, "Jugador agregado: " + nuevoJugador.toString());

            // Guardar en la base de datos
            if (pantalla1.insertarJugadorBD(nombre, apellido, dorsal)) {
                JOptionPane.showMessageDialog(this, "Jugador guardado en la base de datos.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el jugador en la base de datos.");
            }

            textField.setText("");
            textField_1.setText("");
            textField_2.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Dorsal debe ser un número válido.");
        }
    }

    public static ArrayList<inicio> buscarJugadoresBD(String filtro) {
        ArrayList<inicio> jugadores = new ArrayList<>();
        String sql = "SELECT * FROM jugador WHERE nombre LIKE ? OR apellido LIKE ? OR dorsal = ?";

        try (Connection conn = ConexionPostgresUsuario.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + filtro + "%");
            stmt.setString(2, "%" + filtro + "%");

            try {
                int dorsal = Integer.parseInt(filtro);
                stmt.setInt(3, dorsal);
            } catch (NumberFormatException e) {
                stmt.setInt(3, -1);  // No hay dorsal -1
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                int dorsal = rs.getInt("dorsal");

                inicio jugador = new inicio(nombre, apellido, dorsal);
                jugadores.add(jugador);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar jugadores en la base de datos: " + e.getMessage());
        }

        return jugadores;
    }

    public static ArrayList<inicio> obtenerTodosJugadoresBD() {
        ArrayList<inicio> jugadores = new ArrayList<>();
        String sql = "SELECT * FROM jugador";

        try (Connection conn = ConexionPostgresUsuario.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery(); 
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                int dorsal = rs.getInt("dorsal");

                inicio jugador = new inicio(nombre, apellido, dorsal);
                jugadores.add(jugador);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener jugadores de la base de datos: " + e.getMessage());
            e.printStackTrace(); 
        }

        return jugadores;
    }
}
