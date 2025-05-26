package navegadores;

import models.pantalla1;
import models.pantalla2;
import views.pantallaequipo;
import views.pantallamostrar;
import views.pantallaTorneo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame; 

public class Dispatcher {

    private iniciodesesion loginFrame;
    private pantalla1 pantalla1Frame;
    private pantalla2 pantalla2Frame;
    private pantallaequipo pantallaEquipoFrame;
    private pantallamostrar pantallaMostrarFrame;
    private pantallaTorneo pantallaTorneoFrame;

    private JFrame currentVisibleFrame;

    public void dispatch(String pantalla) {
        dispatch(pantalla, null); // Call the overloaded method with null list
    }

    public void dispatch(String pantalla, List<inicio> jugadoresList) {
        if (currentVisibleFrame != null) {
            currentVisibleFrame.setVisible(false);
        }

        switch (pantalla.toLowerCase()) {
            case "iniciosesion":
                if (loginFrame == null) {
                    loginFrame = new iniciodesesion(this);
                }
                loginFrame.setVisible(true);
                currentVisibleFrame = loginFrame;
                break;

            case "pantalla1":
                if (pantalla1Frame == null) {
                    pantalla1Frame = new pantalla1(this);
                }
                pantalla1Frame.setVisible(true);
                currentVisibleFrame = pantalla1Frame;
                break;

            case "pantalla2":
                if (pantalla2Frame == null) {
                    pantalla2Frame = new pantalla2(this);
                }
                pantalla2Frame.setVisible(true);
                currentVisibleFrame = pantalla2Frame;
                break;

            case "equipo":
                if (pantallaEquipoFrame == null) {
                    pantallaEquipoFrame = new pantallaequipo(this);
                }
                pantallaEquipoFrame.setVisible(true);
                currentVisibleFrame = pantallaEquipoFrame;
                break;

            case "mostrar":
                if (pantallaMostrarFrame == null || jugadoresList != null) {
                    pantallaMostrarFrame = new pantallamostrar(jugadoresList != null ? new ArrayList<>(jugadoresList) : new ArrayList<>());
                }
                pantallaMostrarFrame.setVisible(true);
                currentVisibleFrame = pantallaMostrarFrame;
                break;


            case "torneo":
                if (pantallaTorneoFrame == null) {
                    pantallaTorneoFrame = new pantallaTorneo(this);
                }
                pantallaTorneoFrame.setVisible(true);
                currentVisibleFrame = pantallaTorneoFrame;
                break;

            default:
                System.out.println("Pantalla no reconocida: " + pantalla);
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.dispatch("iniciosesion");
        });
    }
}