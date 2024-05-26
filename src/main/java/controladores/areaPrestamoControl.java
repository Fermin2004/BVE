package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class areaPrestamoControl {
    public String correo;

    public int idprestamo;
    public TableView tablaPrestamos;
    public Button botonPedir;
    public Button botonPagar;
    @FXML
    TableColumn<prestamo, Double> columnaCantidad = new TableColumn<>();
    @FXML
    TableColumn<prestamo, String> columnaLimite = new TableColumn<>();
    @FXML
    TableColumn<prestamo, prestamo> columnaVer = new TableColumn<>();
    public Label textoRestante;
    public Label textoInteres;
    public Label textoLimite;
    public Label textoTipo;
    public Label textoEstado;

    public void initialize() {botonPagar.setDisable(true);}

    public void MostrarPrestamos(String correo) {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());

            Ini lector = new Ini(new File("src/main/resources/clases/recursos.ini"));

            String buscarPrestamos = "select pres.Id_Prestamo, pres.Cantidad, pres.Fecha, pres.Restante, pres.Interés, pres.Fecha_límite, pres.Tipo, pres.Estado from Préstamo pres inner join Cuenta cue on cue.Id_Cuenta = pres.Id_cuenta inner join Cliente cli on cli.Id_cliente = cue.Id_Cliente where cli.Correo_elec = ?";
            PreparedStatement stat = con.prepareStatement(buscarPrestamos);
            stat.setString(1, correo);
            ResultSet rs = stat.executeQuery();

            List<prestamo> prestamos = new ArrayList<>();
            while (rs.next()) {
                prestamos.add(new prestamo(rs.getInt("Id_Prestamo"), rs.getDouble("Cantidad"), rs.getDouble("Restante"), rs.getDouble("Interés"), rs.getString("Fecha"), rs.getString("Fecha_límite"), rs.getString("Tipo"), rs.getString("Estado")));
            }

            columnaCantidad.setCellValueFactory(new PropertyValueFactory<>("Cantidad"));
            columnaLimite.setCellValueFactory(new PropertyValueFactory<>("Fecha"));
            columnaVer.setCellFactory(param -> {
                ImageView img = new ImageView(lector.fetch("Image", "ojo"));
                Button botonVer = new Button(null, img);
                return new TableCell<prestamo, prestamo>() {
                    {
                        botonVer.setOnAction(e -> {
                            try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
                                prestamo t = getTableRow().getItem();
                                idprestamo = t.getId();

                                String verPrestamo = "select * from Préstamo where Id_Prestamo = ?";
                                PreparedStatement stat = con.prepareStatement(verPrestamo);
                                stat.setInt(1, t.getId());
                                ResultSet rs = stat.executeQuery();

                                textoRestante.setText(String.valueOf(rs.getDouble("Restante")));
                                Double interes = rs.getDouble("Interés");
                                interes = (double) Math.round(interes * 100);
                                interes = interes / 100;
                                textoInteres.setText(interes + "%");
                                textoLimite.setText(rs.getString("Fecha_límite"));
                                textoTipo.setText(rs.getString("Tipo"));
                                textoEstado.setText(rs.getString("Estado"));
                                botonPagar.setDisable(false);
                            }
                            catch (Exception ex) {ex.printStackTrace();}
                        });
                    }

                    @Override
                    protected void updateItem(prestamo prestamo, boolean vacio) {
                        super.updateItem(prestamo, vacio);
                        if (vacio) {setGraphic(null);}
                        else {setGraphic(botonVer);}
                    }
                };
            });
            tablaPrestamos.getItems().addAll(prestamos);

            String contarPrestamos = "select count(*) as cant from Préstamo prest inner join Cuenta cue on prest.Id_cuenta = cue.Id_Cuenta inner join Cliente cli on cli.Id_cliente = cue.Id_Cliente where cli.Correo_elec = ?";
            PreparedStatement stat2 = con.prepareStatement(contarPrestamos);
            stat2.setString(1, correo);
            ResultSet rs2 = stat2.executeQuery();

            if (rs2.getInt("cant") == 3) {botonPedir.setDisable(true);}
            else {botonPedir.setDisable(false);}
        }
        catch (Exception e) {e.printStackTrace();}
    }
    
    public void PedirPrestamo() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/clases/pedirPrestamo.fxml")));
            Parent p = loader.load();
            pedirPrestamoControl control = loader.getController();
            control.setCorreo(correo);
            Stage stage = new Stage();
            stage.setScene(new Scene(p));
            Ini lector = new Ini(new File("src/main/resources/clases/recursos.ini"));
            Image icono = new Image(lector.fetch("Image", "logoNormal"));
            stage.getIcons().add(icono);
            stage.setResizable(false);
            stage.setX(520);
            stage.setY(230);
            stage.setTitle("Pidiendo un préstamo");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(botonPagar.getScene().getWindow());
            stage.show();

            stage.setOnHidden(e -> {
                tablaPrestamos.getItems().clear();
                MostrarPrestamos(correo);
            });
        }
        catch (IOException e) {throw new RuntimeException(e);}
    }

    public void HacerPago() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/clases/pagarPrestamo.fxml")));
            Parent p = loader.load();
            pagarPrestamoControl control = loader.getController();
            control.setIdprestamo(idprestamo);
            control.setCorreo(correo);
            control.Mostrar(idprestamo, correo);
            Stage stage = new Stage();
            stage.setScene(new Scene(p));
            Ini lector = new Ini(new File("src/main/resources/clases/recursos.ini"));
            Image icono = new Image(lector.fetch("Image", "logoNormal"));
            stage.getIcons().add(icono);
            stage.setResizable(false);
            stage.setX(520);
            stage.setY(230);
            stage.setTitle("Pagando un préstamo");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(botonPagar.getScene().getWindow());
            stage.show();

            stage.setOnHidden(e -> {
                tablaPrestamos.getItems().clear();
                MostrarPrestamos(correo);

                textoRestante.setText("Nada que mostrar");
                textoInteres.setText("Nada que mostrar");
                textoLimite.setText("Nada que mostrar");
                textoTipo.setText("Nada que mostrar");
                textoEstado.setText("Nada que mostrar");
                botonPagar.setDisable(true);
            });
        }
        catch (IOException e) {throw new RuntimeException(e);}
    }

    public void setCorreo(String c) {this.correo = c;}
}
