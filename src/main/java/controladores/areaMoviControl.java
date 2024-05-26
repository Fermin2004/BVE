package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.ini4j.*;

public class areaMoviControl {
    public TableView tablaTarjetas;
    @FXML
    TableColumn<tarjeta, String> columnaTarjeta = new TableColumn<>();
    @FXML
    TableColumn<tarjeta, tarjeta> columnaVer = new TableColumn<>();
    @FXML
    TableColumn<tarjeta, tarjeta> columnaHacer = new TableColumn<>();
    public TableView tablaMovis;
    @FXML
    TableColumn<movimiento, Double> columnaCant = new TableColumn<>();
    @FXML
    TableColumn<movimiento, String> columnaDestino = new TableColumn<>();
    @FXML
    TableColumn<movimiento, String> columnaFecha = new TableColumn<>();
    @FXML
    TableColumn<movimiento, String> columnaNotas = new TableColumn<>();
    public String correo;
    public void MostrarTarjetas(String correo) {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());

            Ini lector = new Ini(new File("src/main/resources/clases/recursos.ini"));

            String buscarTarjeta = "select t.PAN, t.Tipo from Tarjeta t inner join Cuenta c on c.Id_Cuenta = t.Id_cuenta inner join Cliente cli on cli.Id_cliente = c.Id_Cliente where cli.Correo_elec = ?";
            PreparedStatement stat = con.prepareStatement(buscarTarjeta);
            stat.setString(1, correo);
            ResultSet rs = stat.executeQuery();
            String alias = "";

            List<tarjeta> tarjetas = new ArrayList<>();
            while (rs.next()) {
                if (Objects.equals(rs.getString("Tipo"), "Débito")) {alias = "Tarj. débito *" + String.valueOf(rs.getLong("PAN")).substring(String.valueOf(rs.getLong("PAN")).length() - 4);}
                else if (Objects.equals(rs.getString("Tipo"), "Crédito")) {alias = "Tarj. crédito *" + String.valueOf(rs.getLong("PAN")).substring(String.valueOf(rs.getLong("PAN")).length() - 4);}
                tarjetas.add(new tarjeta(rs.getLong("PAN"), rs.getString("Tipo"), alias));
            }

            columnaTarjeta.setCellValueFactory(new PropertyValueFactory<>("alias"));
            columnaVer.setCellFactory(param -> {
                Button botonVer = new Button();
                ImageView img = new ImageView(lector.fetch("Image", "ojo"));
                botonVer.setGraphic(img);
                return new TableCell<tarjeta, tarjeta>() {
                    {
                        botonVer.setOnAction(e -> {
                            try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
                                tablaMovis.getItems().clear();
                                tarjeta t = getTableRow().getItem();

                                String verMovis = "select Cliente.Nombre, Cliente.Apellidos, m.Cantidad, m.Fecha, m.Nota from Cliente inner join Cuenta on Cliente.Id_cliente = Cuenta.Id_Cliente inner join Movimiento m on m.Destino = Cuenta.IBAN where m.Id_tarjeta = (select Id_tarjeta from Tarjeta where PAN = ?)";
                                PreparedStatement stat = con.prepareStatement(verMovis);
                                stat.setLong(1, t.getPAN());
                                ResultSet rs = stat.executeQuery();

                                List<movimiento> movimientos = new ArrayList<>();
                                while (rs.next()) {
                                    movimientos.add(new movimiento(rs.getString("Nombre") + " " + rs.getString("Apellidos"), rs.getDouble("Cantidad"), rs.getString("Fecha"), rs.getString("Nota")));
                                }

                                columnaCant.setCellValueFactory(new PropertyValueFactory<>("Cantidad"));
                                columnaDestino.setCellValueFactory(new PropertyValueFactory<>("Destino"));
                                columnaFecha.setCellValueFactory(new PropertyValueFactory<>("Fecha"));
                                columnaNotas.setCellValueFactory(new PropertyValueFactory<>("Nota"));
                                tablaMovis.getItems().addAll(movimientos);
                            }
                            catch (Exception ex) {ex.printStackTrace();}

                        });
                    }

                    @Override
                    protected void updateItem(tarjeta tarjeta, boolean vacio) {
                        super.updateItem(tarjeta, vacio);
                        if (vacio) {
                            setGraphic(null);
                        } else {
                            setGraphic(botonVer);
                        }
                    }
                };
            });
            columnaHacer.setCellFactory(param -> {
                ImageView img = new ImageView(lector.fetch("Image", "movimiento"));
                Button botonHacer = new Button(null, img);
                return new TableCell<tarjeta, tarjeta>() {
                    {
                        botonHacer.setOnAction(e -> {
                            tarjeta t = getTableRow().getItem();
                            try {
                                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/clases/hacerMovi.fxml")));
                                Parent p = loader.load();
                                hacerMoviControl control = loader.getController();
                                control.setPAN(t.getPAN());
                                Stage stage = new Stage();
                                stage.setScene(new Scene(p));
                                Image icono = new Image(lector.fetch("Image", "logoNormal"));
                                stage.getIcons().add(icono);
                                stage.setResizable(false);
                                stage.setX(420);
                                stage.setY(200);
                                stage.setTitle("Realizando movimiento");
                                stage.initModality(Modality.WINDOW_MODAL);
                                stage.initOwner(botonHacer.getScene().getWindow());
                                stage.show();

                                stage.setOnHidden(i -> tablaMovis.getItems().clear());
                            }
                            catch (IOException ex) {throw new RuntimeException(ex);}
                        });
                    }

                    @Override
                    protected void updateItem(tarjeta tarjeta, boolean vacio) {
                        super.updateItem(tarjeta, vacio);
                        if (vacio) {
                            setGraphic(null);
                        } else {
                            setGraphic(botonHacer);
                        }
                    }
                };
            });
            tablaTarjetas.getItems().addAll(tarjetas);
        }
        catch (Exception e) {e.printStackTrace();}
    }

    public void setCorreo(String correo) {this.correo = correo;}
}
