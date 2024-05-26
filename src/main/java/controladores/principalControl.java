package controladores;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class principalControl {
    public Label textoNombre;
    public Button botonInicio;
    public Button botonCerrar;
    public Button botonUsuario;
    public Button botonTarjetas;
    public Button botonMovs;
    public Button botonTransfers;
    public Button botonBuzon;
    public Button botonPrest;
    public Button botonConf;
    public VBox panelBotones;
    public Button botonMenu;
    public BorderPane panelCuerpo;
    public BorderPane cuerpoInicio;
    private Stage stage;
    private Scene scene;

    public String correo;
    public String contra;
    public static boolean oculto;

    public String nombre;
    public String apellido;

    public void initialize() {
        oculto = false;
    }

    public void MostrarMenu() {
        TranslateTransition aniBotones = new TranslateTransition();
        TranslateTransition aniCuerpo = new TranslateTransition();
        if (oculto) { // Esto muestra el panel de botones
            aniBotones.setDuration(Duration.seconds(0.3));
            aniBotones.setNode(panelBotones);
            aniBotones.setToX(0);

            aniCuerpo.setDuration(Duration.seconds(0.3));
            aniCuerpo.setNode(panelCuerpo);
            aniCuerpo.setToX(0);

            ParallelTransition a = new ParallelTransition(aniBotones, aniCuerpo);
            a.play();
            oculto = false;
        }
        else { // Esto oculta el panel de botones
            aniBotones.setDuration(Duration.seconds(0.3));
            aniBotones.setNode(panelBotones);
            aniBotones.setToX(-200);

            aniCuerpo.setDuration(Duration.seconds(0.3));
            aniCuerpo.setNode(panelCuerpo);
            aniCuerpo.setToX(-200);

            ParallelTransition a = new ParallelTransition(aniBotones, aniCuerpo);
            a.play();
            oculto = true;
        }
    }

    protected void NombreUsuario(String n, String a) {textoNombre.setText(n + " " + a);}

    @FXML
    protected void pasarAInicio(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/clases/inicioSesion.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(loader.load());
        stage.setTitle("Inicio de sesión");
        Ini lector = new Ini(new File("src/main/resources/clases/recursos.ini"));
        Image icono = new Image(lector.fetch("Image", "logoNormal"));
        stage.setResizable(false);
        stage.getIcons().add(icono);
        stage.setX(400);
        stage.setY(150);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @FXML
    public void cambiarInicio() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/clases/areaInicial.fxml")));
            BorderPane vista = loader.load();
            areaInicialControl control = loader.getController();
            control.MostrarDatos(correo, contra);
            panelCuerpo.setCenter(vista);
        }
        catch (IOException e) {e.printStackTrace();}
    }

    public void cambiarUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/clases/areaUsuario.fxml")));
            BorderPane vista = loader.load();
            areaUsuarioControl control = loader.getController();
            control.MostrarDatos(correo, contra);
            control.setCorreo(correo);
            control.setContra(contra);
            panelCuerpo.setCenter(vista);
        }
        catch (IOException e) {e.printStackTrace();}
    }

    public void cambiarTarjetas() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/clases/areaTarjeta.fxml")));
            BorderPane vista = loader.load();
            areaTarjetaControl control = loader.getController();
            control.MostrarTabla(correo);
            control.setCorreo(correo);
            panelCuerpo.setCenter(vista);
        }
        catch (IOException e) {throw new RuntimeException(e);}
    }

    public void cambiarMovimientos() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/clases/areaMovimiento.fxml")));
            BorderPane vista = loader.load();
            areaMoviControl control = loader.getController();
            control.MostrarTarjetas(correo);
            control.setCorreo(correo);
            panelCuerpo.setCenter(vista);
        }
        catch (IOException e) {e.printStackTrace();}
    }

    public void cambiarTransferencias() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/clases/areaTransfer.fxml")));
            BorderPane vista = loader.load();
            areaTransferControl control = loader.getController();
            control.setCorreo(correo);
            control.MostrarTabla();
            panelCuerpo.setCenter(vista);
        }
        catch (IOException e) {e.printStackTrace();}
    }

    public void cambiarBuzon() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/clases/areaBuzon.fxml")));
            BorderPane vista = loader.load();
            areaBuzonControl control = loader.getController();
            control.MostrarTabla(correo);
            control.setCorreo(correo);
            panelCuerpo.setCenter(vista);
        }
        catch (IOException e) {e.printStackTrace();}
    }

    public void cambiarPrestamo() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/clases/areaPrestamo.fxml")));
            BorderPane vista = loader.load();
            areaPrestamoControl control = loader.getController();
            control.MostrarPrestamos(correo);
            control.setCorreo(correo);
            panelCuerpo.setCenter(vista);
        }
        catch (IOException e) {throw new RuntimeException(e);}
    }

    public void cambiarConfig() {

    }
}
