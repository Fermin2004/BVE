package controladores;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static java.lang.Math.round;

public class areaInicialControl {

    public Label textoCapital;
    public BorderPane panelCuerpo;
    public Stage stage;
    public Scene scene;

    public void MostrarDatos(String correo, String contra) {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());

            String buscarcliente = "select Id_Cliente from Cliente where Correo_elec = ?";
            PreparedStatement stat = con.prepareStatement(buscarcliente);
            stat.setString(1, correo);
            ResultSet rs = stat.executeQuery();
            int id = rs.getInt("Id_Cliente");

            String buscarcuenta = "select Saldo from Cuenta where Id_Cliente = ?";
            PreparedStatement stat2 = con.prepareStatement(buscarcuenta);
            stat2.setInt(1, id);
            ResultSet rs2 = stat2.executeQuery();
            Double saldo = rs2.getDouble("Saldo");
            saldo = (double) Math.round(saldo * 100);
            saldo = saldo / 100;
            textoCapital.setText(saldo + "€");
        }
        catch (Exception e) {e.printStackTrace();}
    }


}
