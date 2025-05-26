package models;

public class equipo {
    private int id;
    private String nombre;
    private int cantidadJugadores;

    public equipo(int id, String nombre, int cantidadJugadores) {
        this.id = id;
        this.nombre = nombre;
        this.cantidadJugadores = cantidadJugadores;
    }

    public equipo(String nombre, int cantidadJugadores) {
        this.nombre = nombre;
        this.cantidadJugadores = cantidadJugadores;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidadJugadores() {
        return cantidadJugadores;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return nombre + " (" + cantidadJugadores + " jugadores)" + (id != 0 ? " (ID: " + id + ")" : "");
    }
}