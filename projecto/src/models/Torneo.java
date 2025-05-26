package models;

import java.util.Objects;

public class Torneo {
    private int id;
    private String nombre;
    private int cantidadEquipos;

    // Constructor por defecto
    public Torneo() {
        // Constructor vacío, puedes no inicializar valores si son autoincrementados
    }

    // Constructor con parámetros para crear un torneo
    public Torneo(String nombre, int cantidadEquipos) {
        this.nombre = nombre;
        this.cantidadEquipos = cantidadEquipos;
    }

    // Constructor con ID, necesario para cuando cargamos el torneo desde la base de datos
    public Torneo(int id, String nombre, int cantidadEquipos) {
        this.id = id;
        this.nombre = nombre;
        this.cantidadEquipos = cantidadEquipos;
    }

    // Getter y Setter
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidadEquipos() {
        return cantidadEquipos;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCantidadEquipos(int cantidadEquipos) {
        this.cantidadEquipos = cantidadEquipos;
    }

    @Override
    public String toString() {
        return "Torneo: " + nombre + " | Equipos: " + cantidadEquipos + (id != 0 ? " (ID: " + id + ")" : "");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Torneo torneo = (Torneo) obj;
        return id == torneo.id && cantidadEquipos == torneo.cantidadEquipos && nombre.equals(torneo.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, cantidadEquipos);
    }
}
