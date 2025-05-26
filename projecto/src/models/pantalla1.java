package models;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import navegadores.ConexionPostgresUsuario;
import navegadores.Dispatcher;
import navegadores.inicio;

public class pantalla1 extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private Dispatcher dispatcher;

    public pantalla1(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;

        setTitle("Basket 3X3 - Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 458, 400); 
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu Jugadores = new JMenu("Jugadores");
        JMenuItem menuItemJugadores = new JMenuItem("Gestionar Jugadores");
        Jugadores.add(menuItemJugadores);
        menuItemJugadores.addActionListener(e -> {
            if (dispatcher != null) {
                dispatcher.dispatch("pantalla2");
            }
        });
        menuBar.add(Jugadores);

        JMenu Equipos = new JMenu("Equipos");
        JMenuItem menuItemEquipos = new JMenuItem("Gestionar Equipos");
        Equipos.add(menuItemEquipos);
        menuItemEquipos.addActionListener(e -> {
            if (dispatcher != null) {
                dispatcher.dispatch("equipo");
            }
        });
        menuBar.add(Equipos);

        JMenu Torneos = new JMenu("Torneos");
        JMenuItem menuItemTorneos = new JMenuItem("Gestionar Torneos");
        Torneos.add(menuItemTorneos);
        menuItemTorneos.addActionListener(e -> {
            if (dispatcher != null) {
                dispatcher.dispatch("torneo");
            }
        });
        menuBar.add(Torneos);

        JMenu CerrarSesion = new JMenu("Cerrar Sesión");
        JMenuItem menuItemCerrarSesion = new JMenuItem("Cerrar Sesión");
        CerrarSesion.add(menuItemCerrarSesion);
        menuItemCerrarSesion.addActionListener(e -> {
            dispatcher.dispatch("iniciosesion");
            dispose();
        });
        menuBar.add(CerrarSesion);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 255, 128));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 204));
        panel.setBounds(10, 11, 422, 266);
        contentPane.add(panel);
        panel.setLayout(null);

        JLabel lblTitulo = new JLabel("Bienvenido al Basket 3X3");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblTitulo.setBounds(86, 23, 260, 30);
        panel.add(lblTitulo);

        JLabel imagenLabel = new JLabel();
        imagenLabel.setBounds(110, 70, 200, 120);
        try {
            ImageIcon icon = new ImageIcon("img/basket.jpg"); 
            Image image = icon.getImage().getScaledInstance(200, 120, Image.SCALE_SMOOTH);
            imagenLabel.setIcon(new ImageIcon(image));
        } catch (Exception ex) {
            System.err.println("Error al cargar imagen: " + ex.getMessage());
        }
        panel.add(imagenLabel);

        JButton btnGuardar = new JButton("Guardar Datos");
        btnGuardar.setBounds(40, 200, 150, 30);
        panel.add(btnGuardar);

        JButton btnCargar = new JButton("Cargar Datos");
        btnCargar.setBounds(220, 200, 150, 30);
        panel.add(btnCargar);

        JTextArea textAreaDatos = new JTextArea();
        textAreaDatos.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textAreaDatos);
        scrollPane.setBounds(10, 290, 422, 70);
        contentPane.add(scrollPane);

        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Guardar Datos");

                int userSelection = fileChooser.showSaveDialog(null);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                        writer.write("Ejemplo de datos guardados:\n");
                        writer.write("Jugador: Juan Pérez - Dorsal: 7\n");
                        writer.write("Equipo: Los Tigres - Región: Norte\n");
                        writer.write("Torneo: Torneo Invierno\n");
                        JOptionPane.showMessageDialog(null, "Datos guardados exitosamente.");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error al guardar archivo: " + ex.getMessage());
                    }
                }
            }
        });

        btnCargar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Seleccionar archivo de datos");

                int userSelection = fileChooser.showOpenDialog(null);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToOpen = fileChooser.getSelectedFile();
                    try {
                        String contenido = new String(Files.readAllBytes(Paths.get(fileToOpen.getAbsolutePath())));
                        textAreaDatos.setText(contenido);
                        JOptionPane.showMessageDialog(null, "Datos cargados exitosamente.");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error al cargar archivo: " + ex.getMessage());
                    }
                }
            }
        });
    }

    public static boolean insertarJugadorBD(String nombre, String apellido, int dorsal) {
        String sql = "INSERT INTO jugador (dni, nombre, apellido, fecha_nac, dorsal) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionPostgresUsuario.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Generar DNI aleatorio (por ejemplo)
            String dni = generarDNI(); 
            Date fechaNac = Date.valueOf("2000-01-01"); // puedes permitir que el usuario la introduzca

            stmt.setString(1, dni);
            stmt.setString(2, nombre);
            stmt.setString(3, apellido); // asegúrate de que no sea null
            stmt.setDate(4, fechaNac);
            stmt.setInt(5, dorsal);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar jugador en BD: " + e.getMessage());
            return false;
        }
    }


    private static String generarDNI() {
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        int num = (int)(Math.random() * 100000000);
        char letra = letras.charAt(num % 23);
        return String.format("%08d%c", num, letra);
    }


	public static boolean insertarEquipoBD(String nombre, int numeroJugadores, String region) {
        int codigoEquipo = (int) (System.currentTimeMillis() % 1000000);

        String sql = "INSERT INTO equipo (codigo_equipo, region, numero_jugadores) VALUES (?, ?, ?)";
        try (Connection conn = ConexionPostgresUsuario.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codigoEquipo);
            stmt.setString(2, region);
            stmt.setInt(3, numeroJugadores);
            stmt.executeUpdate();
            System.out.println("Equipo insertado en BD: " + nombre);
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar equipo en BD: " + e.getMessage());
            return false;
        }
    }

    public static boolean insertarTorneoBD(String nombre, int cantidadEquipos) {
        String sql = "INSERT INTO torneo (nombre, cantidad_equipos) VALUES (?, ?)";
        try (Connection conn = ConexionPostgresUsuario.obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setInt(2, cantidadEquipos);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static ArrayList<Torneo> obtenerTodosLosTorneosBD() {
        ArrayList<Torneo> torneos = new ArrayList<>();
        String sql = "SELECT * FROM torneo";
        try (Connection conn = ConexionPostgresUsuario.obtenerConexion(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                int cantidadEquipos = rs.getInt("cantidad_equipos");
                torneos.add(new Torneo(id, nombre, cantidadEquipos));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return torneos;
    }
    public static boolean actualizarTorneoBD(int id, String nuevoNombre, int nuevaCantidadEquipos) {
        String sql = "UPDATE torneo SET nombre = ?, cantidad_equipos = ? WHERE id = ?";
        try (Connection conn = ConexionPostgresUsuario.obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoNombre);
            stmt.setInt(2, nuevaCantidadEquipos);
            stmt.setInt(3, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean eliminarTorneoBD(int id) {
        String sql = "DELETE FROM torneo WHERE id = ?";
        try (Connection conn = ConexionPostgresUsuario.obtenerConexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
  
    // Métodos de jugadores
    public static ArrayList<inicio> obtenerTodosLosJugadoresBD() {
        ArrayList<inicio> lista = new ArrayList<>();
        String sql = "SELECT nombre, dorsal FROM jugadores";

        try (Connection conn = ConexionPostgresUsuario.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                inicio jugador = new inicio();
                jugador.setNombre(rs.getString("nombre"));
                jugador.setApellido(rs.getString("apellido"));
                jugador.setDorsal(rs.getInt("dorsal"));
                lista.add(jugador);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static boolean eliminarJugadorBD(String nombre, String apellido, int dorsal) {
        String nombreCompleto = nombre + " " + apellido;
        String sql = "DELETE FROM Jugador WHERE nombre = ? AND dorsal = ?";

        try (Connection conn = ConexionPostgresUsuario.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombreCompleto);
            ps.setInt(2, dorsal);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean actualizarJugadorBD(String nombreAntiguo, String apellidoAntiguo, int dorsalAntiguo,
            String nuevoNombre, String nuevoApellido, int nuevoDorsal) {
        String nombreCompletoAntiguo = nombreAntiguo + " " + apellidoAntiguo;
        String nuevoNombreCompleto = nuevoNombre + " " + nuevoApellido;

        String sql = "UPDATE Jugador SET nombre = ?, dorsal = ? WHERE nombre = ? AND dorsal = ?";

        try (Connection conn = ConexionPostgresUsuario.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuevoNombreCompleto); 
            ps.setInt(2, nuevoDorsal);            
            ps.setString(3, nombreCompletoAntiguo); 
            ps.setInt(4, dorsalAntiguo);            

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }  
  }

