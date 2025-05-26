package views;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import models.equipo;
import navegadores.ConexionPostgresUsuario;
import navegadores.Dispatcher;

public class pantallaequipo extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textFieldNombre;
	private JTextField textFieldCantidad;
	private Dispatcher dispatcher;

	public pantallaequipo(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;

		setTitle("Gestión de Equipos");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 350);
		setLocationRelativeTo(null);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menuEquipos = new JMenu("Opciones de Equipo");
		menuBar.add(menuEquipos);

		JMenuItem itemAgregar = new JMenuItem("Agregar Equipo");
		menuEquipos.add(itemAgregar);
		itemAgregar.addActionListener(e -> agregarEquipoDesdeCampos());

		JMenuItem itemMostrar = new JMenuItem("Mostrar Equipos");
		menuEquipos.add(itemMostrar);
		itemMostrar.addActionListener(e -> {
			PantallaMostrarequipos ventanaMostrar = new PantallaMostrarequipos();
			ventanaMostrar.setVisible(true);
		});

		contentPane = new JPanel();
		contentPane.setBackground(new Color(240, 248, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBackground(new Color(255, 255, 128));
		panel.setBounds(10, 11, 464, 250);
		contentPane.add(panel);
		panel.setLayout(null);

		JLabel lblNombre = new JLabel("Nombre del Equipo:");
		lblNombre.setBounds(20, 50, 120, 20);
		panel.add(lblNombre);

		textFieldNombre = new JTextField();
		textFieldNombre.setBounds(150, 50, 200, 20);
		panel.add(textFieldNombre);
		textFieldNombre.setColumns(10);

		JLabel lblCantidad = new JLabel("Cantidad de Jugadores:");
		lblCantidad.setBounds(20, 90, 150, 20);
		panel.add(lblCantidad);

		textFieldCantidad = new JTextField();
		textFieldCantidad.setBounds(180, 90, 100, 20);
		panel.add(textFieldCantidad);
		textFieldCantidad.setColumns(10);
		textFieldCantidad.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!Character.isDigit(c) || textFieldCantidad.getText().length() >= 2) {
					e.consume();
				}
			}
		});

		JButton btnVolver = new JButton("Volver a Principal");
		btnVolver.setBounds(300, 270, 150, 25);
		contentPane.add(btnVolver);

		btnVolver.addActionListener(e -> {
			if (dispatcher != null) {
				dispatcher.dispatch("pantalla1");
				dispose();
			}
		});
	}

	private void agregarEquipoDesdeCampos() {
		String nombre = textFieldNombre.getText().trim();
		String cantidadStr = textFieldCantidad.getText().trim();

		if (nombre.isEmpty() || cantidadStr.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Completa todos los campos.");
			return;
		}

		try {
			int cantidad = Integer.parseInt(cantidadStr);
			boolean insertado = insertarEquipoBD(nombre, cantidad);

			if (insertado) {
				JOptionPane.showMessageDialog(this, "Equipo guardado correctamente en la base de datos.");
				textFieldNombre.setText("");
				textFieldCantidad.setText("");
			} else {
				JOptionPane.showMessageDialog(this, "No se pudo guardar el equipo en la base de datos.");
			}
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "La cantidad de jugadores debe ser un número válido.");
		}
	}

	private boolean insertarEquipoBD(String nombre, int cantidad) {
		Connection conn = null;
		PreparedStatement stmt = null;
		Statement stmtCodigo = null;
		ResultSet rs = null;

		try {
			conn = ConexionPostgresUsuario.obtenerConexion();

			stmtCodigo = conn.createStatement();
			rs = stmtCodigo.executeQuery("SELECT nextval('equipo_codigo_equipo_seq')");
			rs.next();
			int codigoEquipo = rs.getInt(1);

			String sql = "INSERT INTO equipo (codigo_equipo, nombre, numero_jugadores) VALUES (?, ?, ?)";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, codigoEquipo); 
			stmt.setString(2, nombre);
			stmt.setInt(3, cantidad);

			int filas = stmt.executeUpdate();

			return filas > 0;
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error al guardar en la BD: " + ex.getMessage());
			return false;
		} finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
				if (stmtCodigo != null) stmtCodigo.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
			}
		}
	}
}
