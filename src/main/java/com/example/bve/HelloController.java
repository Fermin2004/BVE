package com.example.bve;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;
    @FXML
    private Label saludo;
    private Stage stage;
    private Scene scene;
    @FXML
    private TextField textoNombre;

    @FXML
    protected void onHelloButtonClick() throws IOException {
        welcomeText.setText("Welcome to JavaFX Application!");
        saludo.setText("WAZAAA");
        saludo.setFont(Font.font("Arial", 22));
        Stage s = new Stage();
        new saludo().start(s);
    }

    @FXML
    protected void cambiarAEscena2(ActionEvent event) throws IOException {
        String nombre = textoNombre.getText();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("salido.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        saludoControl cosa = fxmlLoader.getController();
        cosa.saludarNombe(nombre);
        //Image icono = new Image("C:\\Users\\usuario\\OneDrive\\Documentos\\Proyectos_Intellij\\BVE\\src\\iconoSaber.png");
        //stage.getIcons().add(icono);
        stage.setTitle("Saludo");
        scene.setFill(Color.LIGHTBLUE);
        stage.setScene(scene);
        stage.show();
    }
}