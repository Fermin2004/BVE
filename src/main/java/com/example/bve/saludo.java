package com.example.bve;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class saludo extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("salido.fxml"));
        Scene scene = new Scene(loader.load());
        Image icono = new Image("C:\\Users\\usuario\\OneDrive\\Documentos\\Proyectos_Intellij\\BVE\\src\\iconoSaber.png");
        stage.getIcons().add(icono);
        stage.setTitle("Saludo");
        stage.setScene(scene);
        stage.show();
        scene.setFill(Color.LIGHTBLUE);

        /*try {
            Class.forName("org.sqlite.JDBC");
            //DriverManager.registerDriver(new org.sqlite.JDBC());
            Connection con = DriverManager.getConnection("jdbc:sqlite:chinook.db");
            String queryexiste = "select * from playlists where Name = ?"; //selecciono el contenido de la lista con el nombre que le paso
            PreparedStatement stat = con.prepareStatement(queryexiste);
            stat.setString(1, "Music");
            ResultSet rs = stat.executeQuery();

            if (rs.next()) {
                String queryidlista = "select PlaylistId from playlists"; //con esto saco el id de la lista introducida
                Statement statidlista = con.createStatement();
                ResultSet rsidlista = statidlista.executeQuery(queryidlista);
                //statidlista.close();
                //while (rsidlista.next()) {
                int idlista = rsidlista.getInt("PlaylistId");
                //mostrador.setText(String.valueOf(idlista));
                System.out.println("Id: " + idlista);
                //}
                statidlista.close();

            }
            else {System.out.println("No se ha encontrado");}
        }
        catch (Exception e) {e.printStackTrace();}*/
    }

    public static void main(String[] args) {
        launch();
    }
}
