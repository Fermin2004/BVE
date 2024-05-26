package controladores;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class hacerMoviControl {
    public Long PAN;
    public TextField textoCantidad;
    public TextField textoHora;
    public TextField textIBAN;
    public DatePicker textoFecha;
    public TextArea textoNotas;
    public Button botonAceptar;
    public Button botonVolver;
    public Stage stage;

    public void setPAN(Long PAN) {
        this.PAN = PAN;
    }

    public void HacerMovimiento() {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());

            //para ver si el iban introducido coincide con el introducido para el destino
            //esto además saca los datos del usuario que va a hacer el movimiento
            String buscarIBAN = "select IBAN, Saldo, cue.Id_Cuenta from Cuenta cue inner join Tarjeta tar on tar.Id_cuenta = cue.Id_Cuenta where tar.PAN = ?";
            PreparedStatement stat = con.prepareStatement(buscarIBAN);
            stat.setLong(1, PAN);
            ResultSet rs = stat.executeQuery();
            String ibanusu = rs.getString("IBAN");
            Double saldousu = rs.getDouble("Saldo");

            //esto comprueba si existe el objetivo
            String buscarCuenta = "select * from Cuenta where IBAN = ?";
            PreparedStatement stat2 = con.prepareStatement(buscarCuenta);
            stat2.setString(1, textIBAN.getText());
            ResultSet rs2 = stat2.executeQuery();
            String wazaa = textIBAN.getText();

            if (textoCantidad.getText() == null || !textoCantidad.getText().matches("^\\d+(\\.\\d{1,2})?$")) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error");
                error.setContentText("Debe introducir un número de hasta 2 decimales.\nUse . en vez de ,");
                error.initOwner(botonAceptar.getScene().getWindow());
                error.showAndWait();
            }
            else if (saldousu < Double.parseDouble(textoCantidad.getText())) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error");
                error.setContentText("Está intentando mandar una cantidad\nmayor de la que posee.");
                error.initOwner(botonAceptar.getScene().getWindow());
                error.showAndWait();
            }
            else if (textIBAN.getText() == null || !textIBAN.getText().matches("^ES\\d{2}\\s\\d{4}\\s\\d{4}\\s\\d{4}\\s\\d{4}\\s\\d{4}$")) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error");
                error.setContentText("Debe introducir un IBAN válido.\nEl formato es 'ESXX XXXX XXXX XXXX XXXX XXXX'");
                error.initOwner(botonAceptar.getScene().getWindow());
                error.showAndWait();
            }
            else if (ibanusu.equals(wazaa)) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error");
                error.setContentText("Ha puesto su propio IBAN.");
                error.initOwner(botonAceptar.getScene().getWindow());
                error.showAndWait();
            }
            else if (textoFecha.getValue() == null) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error");
                error.setContentText("Debe elegir una fecha con el seleccionador.");
                error.initOwner(botonAceptar.getScene().getWindow());
                error.showAndWait();
            }
            else if (textoHora.getText() == null || !textoHora.getText().matches("^(\\d\\d:\\d\\d)")) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error");
                error.setContentText("Debe introducir una hora, formato 00:00 - 23:59");
                error.initOwner(botonAceptar.getScene().getWindow());
                error.showAndWait();
            }
            else if (!rs2.next()) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error");
                error.setContentText("La cuenta introducida no existe.");
                error.initOwner(botonAceptar.getScene().getWindow());
                error.showAndWait();
            }
            else {
                Hilo hilo = new Hilo(rs.getInt("Id_cuenta"), rs2.getInt("Id_cuenta"), con);
                try {hilo.start();}
                catch (Exception e) {
                    Alert mensaje2 = new Alert(Alert.AlertType.ERROR);
                    mensaje2.setHeaderText(null);
                    mensaje2.setTitle("Error de base de datos.");
                    mensaje2.setContentText("Se ha producido un error en el sistema.\nPor favor, cierre sesión y vuelva a intentarlo.");
                    mensaje2.initOwner(botonAceptar.getScene().getWindow());
                    mensaje2.showAndWait();
                }
                hilo.join();
                Alert error = new Alert(Alert.AlertType.INFORMATION);
                error.setHeaderText(null);
                error.setTitle("Movimiento realizado");
                error.setContentText("El movimiento se ha realizado con éxito.");
                error.initOwner(botonAceptar.getScene().getWindow());
                error.showAndWait();

                stage = (Stage) botonVolver.getScene().getWindow();
                stage.close();
            }
        }
        catch (Exception e) {e.printStackTrace();}
    }

    public class Hilo extends Thread {
        int idOrigen;
        int idDestino;
        Connection con;
        public Hilo(int ori, int des, Connection c) {
            this.idOrigen = ori;
            this.idDestino = des;
            this.con = c;
        }
        public void run() {
            try {
                Class.forName("org.sqlite.JDBC");
                DriverManager.registerDriver(new org.sqlite.JDBC());

                String buscarTarjeta = "select Id_tarjeta from Tarjeta where PAN = ?";
                PreparedStatement stat = con.prepareStatement(buscarTarjeta);
                stat.setLong(1, PAN);
                ResultSet rs = stat.executeQuery();

                String hacerMovimiento = "insert into Movimiento(Id_tarjeta, Destino, Cantidad, Fecha, Nota) values(?, ?, ?, ?, ?)";
                PreparedStatement stat2 = con.prepareStatement(hacerMovimiento);
                stat2.setInt(1, rs.getInt("Id_tarjeta"));
                stat2.setString(2, textIBAN.getText());
                stat2.setDouble(3, Double.parseDouble(textoCantidad.getText()));
                stat2.setString(4, textoFecha.getValue() + " " + textoHora.getText());
                stat2.setString(5, textoNotas.getText());
                stat2.executeUpdate();

                String restarDinero = "update Cuenta set Saldo = (Saldo - '" + Double.parseDouble(textoCantidad.getText()) + "') where Id_Cuenta = ?";
                PreparedStatement stat7 = con.prepareStatement(restarDinero);
                stat7.setInt(1, idOrigen);
                stat7.executeUpdate();

                String sumarDinero = "update Cuenta set Saldo = (Saldo + '" + Double.parseDouble(textoCantidad.getText()) + "') where Id_Cuenta = ?";
                PreparedStatement stat8 = con.prepareStatement(sumarDinero);
                stat8.setInt(1, idDestino);
                stat8.executeUpdate();

                String verNombre = "select cli.Nombre, cli.Apellidos from Cliente cli inner join Cuenta cue on cue.Id_Cliente = cli.Id_cliente where cue.Id_Cuenta = ?";
                PreparedStatement stat10 = con.prepareStatement(verNombre);
                stat10.setInt(1, idDestino);
                ResultSet rs2 = stat10.executeQuery();

                String mensaje = "insert into Buzón(Id_Cuenta, Mensaje, Fecha) values(?, ?, ?)";
                PreparedStatement stat9 = con.prepareStatement(mensaje);
                stat9.setInt(1, idOrigen);
                stat9.setString(2, "Se ha hecho un movimiento de " + Double.parseDouble(textoCantidad.getText()) + "€ a " + rs2.getString("Nombre") + " " + rs2.getString("Apellidos"));
                stat9.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()));
                stat9.executeUpdate();
                //con.close();
            }
            catch (Exception e) {e.printStackTrace();}
        }
    }

    public void volver(ActionEvent event) {
        stage = (Stage) botonVolver.getScene().getWindow();
        stage.close();
    }
}
