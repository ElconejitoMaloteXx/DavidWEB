package navegadores;

import javax.swing.JFrame;

public class inicio {
    private int codigoJugador; // Nuevo atributo
    private String nombre;
    private String apellido;
    private int dorsal;
    private String dni;

    // Constructor sin códigoJugador
    public inicio(String nombre, int dorsal) {
        this.nombre = nombre;
        this.dorsal = dorsal;
        this.dni = null;
    }

    // Constructor completo con todos los atributos
    public inicio(int codigoJugador, String dni, String nombre, String apellido, int dorsal) {
        this.codigoJugador = codigoJugador;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dorsal = dorsal;
    }

    // Constructor sin códigoJugador (si lo insertas y el código es autogenerado)
    public inicio(String dni, String nombre, int dorsal) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dorsal = dorsal;
    }

    public inicio() {
        // Constructor por defecto
    }

    // Método principal para iniciar la aplicación
    public static void main(String[] args) {
        Dispatcher dispatcher = new Dispatcher();
        JFrame login = new iniciodesesion(dispatcher);
        login.setVisible(true);
    }

    // Métodos toString y Getters/Setters
    @Override
    public String toString() {
        return "Jugador: " + nombre + " " + apellido + ", Dorsal: " + dorsal +
               (dni != null ? ", DNI: " + dni : "") +
               (codigoJugador != 0 ? ", Código: " + codigoJugador : "");
    }

    public int getCodigoJugador() {
        return codigoJugador;
    }

    public void setCodigoJugador(int codigoJugador) {
        this.codigoJugador = codigoJugador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getDorsal() {
        return dorsal;
    }

    public void setDorsal(int dorsal) {
        this.dorsal = dorsal;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Object getCantidadEquipos() {
        // Puedes implementar esta lógica más adelante
        return null;
    }
}
