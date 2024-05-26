package com.example.bve;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class saludoControl {

    @FXML
    private Label textoNombre;
    private Stage stage;
    private Scene scene;

    @FXML
    protected void cambiarAEscena1(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        scene.setFill(Color.WHITE);
        //Image icono = new Image("C:\\Users\\usuario\\OneDrive\\Documentos\\Proyectos_Intellij\\BVE\\src\\iconoSMT3.jpg");
        //stage.getIcons().add(icono);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    protected void saludarNombe(String nom) {
        textoNombre.setText(textoNombre.getText() + " " + nom);
    }
}
