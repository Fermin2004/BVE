package controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.ini4j.Ini;

import javax.net.ssl.HostnameVerifier;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class inicioControl {

    public Label textoSaludo;
    public Button botonIniciar;
    public Button botonContra;
    @FXML
    private TextField textoCorreo;
    @FXML
    private TextField textoContra;
    private Stage stage;
    private Scene scene;

    //función que genera la hora actual, y dependiendo del número de las horas, saluda de una forma u otra
    @FXML
    public void initialize() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        int hora = Integer.parseInt((dtf.format(LocalDateTime.now())).substring(0, 2));
        if (hora >= 6 && hora <= 12) {textoSaludo.setText("Buenos días");}
        else if (hora >= 12 && hora <= 18) {textoSaludo.setText("Buenas tardes");}
        else if (hora >= 18 && hora <= 24 || hora <= 0) {textoSaludo.setText("Buenas noches");}

    }

    //función que lee el correo y contraseña del formulario y busca si hay un usuario y cuenta con esos datos
    //si no va, ponerla a protected en vez de public
    @FXML
    public void IniciarCliente(ActionEvent event) {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
            if (textoCorreo.getText().isEmpty() || Objects.equals(textoCorreo.getText(), "")) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error de autenticación");
                error.setContentText("Faltan datos.\nDebe introducir su correo");
                error.initOwner(botonIniciar.getScene().getWindow());
                error.showAndWait();
            }
            else if (!textoCorreo.getText().matches("^[^ @]{1,30}@[^ @]{1,10}\\.[^ @]+$")) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error de autenticación");
                error.setContentText("Introduzca su correo correctamente.");
                error.initOwner(botonIniciar.getScene().getWindow());
                error.showAndWait();
            }
            else if (textoContra.getText().isEmpty() || Objects.equals(textoContra.getText(), "")) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error de autenticación");
                error.setContentText("Faltan datos.\nDebe introducir su contraseña");
                error.initOwner(botonIniciar.getScene().getWindow());
                error.showAndWait();
            }
            else {
                Class.forName("org.sqlite.JDBC");
                DriverManager.registerDriver(new org.sqlite.JDBC());
                String queryCorreo = "select * from Cliente where Correo_elec = ?"; //selecciono el contenido de la lista con el nombre que le paso
                PreparedStatement stat = con.prepareStatement(queryCorreo);
                stat.setString(1, textoCorreo.getText());
                ResultSet rs = stat.executeQuery();

                if (rs.next()) {
                    int idCliente = rs.getInt("Id_cliente");
                    String querycuenta = "select Contrasena from Cuenta where Id_Cliente = ?";
                    PreparedStatement stat2 = con.prepareStatement(querycuenta);
                    stat2.setInt(1, idCliente);
                    ResultSet rs2 = stat2.executeQuery();

                    if (rs2.next()) {
                        String contra = textoContra.getText();
                        String contra2 = rs2.getString("Contrasena");
                        if (Objects.equals(contra, contra2)) {
                            String nombre = rs.getString("Nombre");
                            String apellidos = rs.getString("Apellidos");

                            String fechaacceso = "update Cuenta set Últ_acceso = date('now') where Id_Cliente = ?";
                            PreparedStatement stat3 = con.prepareStatement(fechaacceso);
                            stat3.setInt(1, idCliente);
                            stat3.executeUpdate();

                            FXMLLoader loader = new FXMLLoader(inicioControl.class.getResource("/clases/principal.fxml"));
                            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                            scene = new Scene(loader.load());
                            principalControl control = loader.getController();
                            control.NombreUsuario(nombre, apellidos);
                            control.correo = textoCorreo.getText();
                            control.contra = textoContra.getText();
                            control.cambiarInicio();
                            stage.setScene(scene);
                            Ini lector = new Ini(new File("src/main/resources/clases/recursos.ini"));
                            Image icono = new Image(lector.fetch("Image", "logoNormal"));
                            stage.getIcons().add(icono);
                            stage.setTitle("Banco Virtual Español - Área de cliente");
                            stage.setMinHeight(stage.getHeight());
                            stage.setMinWidth(stage.getWidth());
                            stage.setX(320);
                            stage.setY(140);
                            stage.setResizable(true);
                            stage.show();
                        }
                        else {
                            Alert error = new Alert(Alert.AlertType.ERROR);
                            error.setHeaderText(null);
                            error.setTitle("Error de autenticación");
                            error.setContentText("Credenciales incorrectas.\nRevise sus datos.");
                            error.initOwner(botonIniciar.getScene().getWindow());
                            error.showAndWait();
                        }
                    }
                }
                else {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setHeaderText(null);
                    error.setTitle("Error de autenticación");
                    error.setContentText("Credenciales incorrectas.\nRevise sus datos.");
                    error.initOwner(botonIniciar.getScene().getWindow());
                    error.showAndWait();
                }
            }
        }
        catch (Exception e) {e.printStackTrace();}
    }

    public void CrearCliente(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(inicioControl.class.getResource("/clases/CrearCliente.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(loader.load());
            stage.setScene(scene);
            Ini lector = new Ini(new File("src/main/resources/clases/recursos.ini"));
            Image icono = new Image(lector.fetch("Image", "logoNormal"));
            stage.getIcons().add(icono);
            stage.setTitle("Creando una cuenta de cliente");
            stage.setResizable(false);
            stage.setX(500);
            stage.setY(100);
            stage.show();
        }
        catch (IOException e) {e.printStackTrace();}
    }

    public void pasarContra() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/clases/pedirContraCliente.fxml")));
            Parent p = loader.load();
            Stage stage2 = new Stage();
            stage2.setScene(new Scene(p));
            Ini lector = new Ini(new File("src/main/resources/clases/recursos.ini"));
            Image icono = new Image(lector.fetch("Image", "logoNormal"));
            stage2.getIcons().add(icono);
            stage2.setResizable(false);
            stage2.setTitle("Solicitando contraseña de cliente");
            stage2.setX(455);
            stage2.setY(210);
            stage2.initModality(Modality.WINDOW_MODAL);
            stage2.initOwner(botonIniciar.getScene().getWindow());
            stage2.show();
        }
        catch (IOException e) {e.printStackTrace();}
    }
}
