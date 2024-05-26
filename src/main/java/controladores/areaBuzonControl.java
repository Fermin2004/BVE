package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class areaBuzonControl {
    public TableView tablaMensaje;
    @FXML
    TableColumn<mensaje, String> coluFecha = new TableColumn<>();
    @FXML
    TableColumn<mensaje, String> coluMensaje = new TableColumn<>();
    public String correo;

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void MostrarTabla(String correo) {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());

            String buscarBuzon = "select buz.Id_Cuenta, buz.Fecha, buz.Mensaje from Buzón buz inner join Cuenta cue on cue.Id_Cuenta = buz.Id_Cuenta inner join Cliente cli on cli.Id_cliente = cue.Id_Cliente where cli.Correo_elec = ?";
            PreparedStatement stat = con.prepareStatement(buscarBuzon);
            stat.setString(1, correo);
            ResultSet rs = stat.executeQuery();

            List<mensaje> mensajes = new ArrayList<>();
            while (rs.next()) {
                mensajes.add(new mensaje(rs.getInt("Id_Cuenta"), rs.getString("Mensaje"), rs.getString("Fecha")));
            }

            coluFecha.setCellValueFactory(new PropertyValueFactory<>("Fecha"));
            coluMensaje.setCellValueFactory(new PropertyValueFactory<>("Mensaje"));

            tablaMensaje.getItems().addAll(mensajes);
        }
        catch (Exception e) {e.printStackTrace();}
    }
}
