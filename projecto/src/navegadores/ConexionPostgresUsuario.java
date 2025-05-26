package navegadores;

import java.sql.*;

public class ConexionPostgresUsuario {

    private static final String url = "jdbc:postgresql://localhost:5434/proyecto"; 
    private static String usuario = "";
    private static String password = "";

    public static Connection obtenerConexion() {
        try {
            return DriverManager.getConnection(url, usuario, password); 
        } catch (SQLException e) {
            System.err.println("Error al obtener conexión a la BD: " + e.getMessage());
            return null;
        }
    }

    public static void cerrarConexion(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión a la BD: " + e.getMessage());
        }
    }

    public static boolean validarCredenciales(String nombreUsuario, String contrasena) {
        boolean correcto = false;
        try {
            Connection conn = DriverManager.getConnection(url, nombreUsuario, contrasena);
            if(conn != null) {
                correcto = true;
                usuario = nombreUsuario;
                password = contrasena; 
                conn.close();
            }
        }catch(SQLException error) {
            System.err.println("Fallo de credenciales: " + error.getMessage()); 
            correcto = false;
        }
        return correcto;
    }
}