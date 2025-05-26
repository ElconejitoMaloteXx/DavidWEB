package navegadores;

import navegadores.Dispatcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class iniciodesesion extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField usuarioField;
    private JPasswordField contrasenaField;
    private Dispatcher dispatcher;

    public iniciodesesion(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;

        setTitle("Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 250);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 255, 128));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setBounds(50, 50, 80, 25);
        contentPane.add(lblUsuario);

        usuarioField = new JTextField();
        usuarioField.setBounds(140, 50, 180, 25);
        contentPane.add(usuarioField);
        usuarioField.setColumns(10);

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setBounds(50, 90, 80, 25);
        contentPane.add(lblContrasena);

        contrasenaField = new JPasswordField();
        contrasenaField.setBounds(140, 90, 180, 25);
        contentPane.add(contrasenaField);

        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.setBounds(140, 130, 100, 30);
        contentPane.add(btnIngresar);

        btnIngresar.addActionListener((ActionEvent e) -> {
            String usuario = usuarioField.getText().trim();
            String contrasena = new String(contrasenaField.getPassword());

            boolean conexion = ConexionPostgresUsuario.validarCredenciales(usuario, contrasena);
            if(conexion == true) {
                JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                if (dispatcher != null) {
                    dispatcher.dispatch("pantalla1");
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales inválidas. Inténtalo de nuevo.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}