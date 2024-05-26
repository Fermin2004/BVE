package clases;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;
import static javafx.application.Application.launch;

public class main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/clases/inicioSesion.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Inicio de sesión");
            Image icono = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/clases/logoBVE.png")));
            stage.setResizable(false);
            stage.getIcons().add(icono);
            stage.setX(400);
            stage.setY(150);
            stage.show();
            //System.out.println("Ancho: " + stage.getWidth() + " altura: " + stage.getHeight());
        }
        catch (IOException e) {e.printStackTrace();}
    }

    public static void main(String[] args) {launch();}
}
