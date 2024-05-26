package controladores;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class areaTransferControl {
    public Stage stage;
    public Scene scene;
    public static String correo;
    public TableView tablaTransfers;
    public Button botonHacer;

    //esto crea el array de transferencias, luego, le da los tipos de datos a las columnas creadas en el scene builder, se les puede poner nombre aquí pero no hacen nada
    //ObservableList<transferencia> transferencias = FXCollections.observableArrayList();
    @FXML
    TableColumn<transferencia, Double> columnaCant = new TableColumn<>();
    @FXML
    TableColumn<transferencia, String> columnaNombre = new TableColumn<>();
    @FXML
    TableColumn<transferencia, Date> columnaFecha = new TableColumn<>();
    @FXML
    TableColumn<transferencia, Double> columnaCostos = new TableColumn<>();
    @FXML
    TableColumn<transferencia, String> columnaNotas = new TableColumn<>();

    public void MostrarTabla() {
        try {
            //asigna que valores de transferencia van a mostrar las columnas, tiene que coincidir con los atributos de esta
            columnaCant.setCellValueFactory(new PropertyValueFactory<>("Cantidad"));
            columnaNombre.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
            columnaFecha.setCellValueFactory(new PropertyValueFactory<>("Fecha"));
            columnaCostos.setCellValueFactory(new PropertyValueFactory<>("Costos_extra"));
            columnaNotas.setCellValueFactory(new PropertyValueFactory<>("Notas"));

            List<transferencia> transfers = verTransfers();
            tablaTransfers.getItems().addAll(transfers);
        }
        catch (Exception e ) {e.printStackTrace();}
    }

    public List<transferencia> verTransfers() throws Exception {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());

            ArrayList nombres = new ArrayList<String>();
            ArrayList apellidos = new ArrayList<String>();

            //saco el id de la cuenta asociada al correo del cliente del que muestra las transferencias
            String buscarCuenta = "select Id_cuenta from Cuenta where Id_cliente in (select Id_cliente from Cliente where Correo_elec = ?)";
            PreparedStatement stat0 = con.prepareStatement(buscarCuenta);
            stat0.setString(1, correo);
            ResultSet rs0 = stat0.executeQuery();

            String buscarTranfers = "select Nombre, Apellidos, Dinero, Fecha, Extras, Notas from Transferencia inner join Cuenta_transferencia on Transferencia.Id_transferencia = Cuenta_transferencia.Id_transferencia inner join Cuenta on Cuenta_transferencia.Destino = Cuenta.Id_cuenta inner join Cliente on Cuenta.Id_cliente = Cliente.Id_cliente where Cuenta_transferencia.Origen = ?";
            PreparedStatement stat = con.prepareStatement(buscarTranfers);
            stat.setInt(1, rs0.getInt("Id_cuenta"));
            ResultSet rs = stat.executeQuery();

            List<transferencia> transfers = new ArrayList<>();
            while (rs.next()) {
                transferencia t = new transferencia(rs.getDouble("Dinero"), rs.getString("Fecha"), rs.getDouble("Extras"), rs.getString("Notas"), rs.getString("Nombre") + " " + rs.getString("Apellidos"));
                transfers.add(t);
            }
            return transfers;
        }
    }

    public void setCorreo(String correo) {
        areaTransferControl.correo = correo;
    }

    public void pasarATransferencia() throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/clases/hacerTransfer.fxml")));
        Parent p = loader.load();
        hacerTransferControl control = loader.getController();
        control.setCorreo(correo);
        Stage stage = new Stage();
        stage.setScene(new Scene(p));
        Image icono = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/clases/logoBVE.png")));
        stage.getIcons().add(icono);
        stage.setResizable(false);
        stage.setX(420);
        stage.setY(200);
        stage.setTitle("Realizando transferencia");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(botonHacer.getScene().getWindow());
        stage.show();

        stage.setOnHidden(e -> {
            tablaTransfers.getItems().clear();
            MostrarTabla();
        });
    }
}
