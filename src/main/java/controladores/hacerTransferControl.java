package controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.ConnectException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class hacerTransferControl {
    public TextField textoCantidad;
    public TextArea textoNotas;
    public TextField textoIBAN;
    public DatePicker textoFecha;
    public TextField textoHora;
    public Button botonAceptar;
    public Button botonVolver;
    public Stage stage;
    public Scene scene;
    public String correo;

    public void hacerTranferencia() {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());

            //saco el id de la cuenta que va a hacer la transferencia según el correo
            String buscarCuenta = "select Id_Cuenta from Cuenta where Id_cliente in (select Id_Cliente from Cliente where Correo_elec = ?)";
            PreparedStatement stat = con.prepareStatement(buscarCuenta);
            stat.setString(1, correo);
            ResultSet rs = stat.executeQuery();
            int idOrigen = rs.getInt("Id_Cuenta");

            //saco el saldo disponible para comprobar si se puede hacer la transferencia
            String comprobarDinero = "select Saldo from Cuenta where Id_Cuenta = ?";
            PreparedStatement stat2 = con.prepareStatement(comprobarDinero);
            stat2.setDouble(1, idOrigen);
            ResultSet rs2 = stat2.executeQuery();
            Double saldo = rs2.getDouble("Saldo");

            String existeObjetivo = "select Id_Cuenta from Cuenta where IBAN = ?";
            PreparedStatement stat3 = con.prepareStatement(existeObjetivo);
            stat3.setString(1, textoIBAN.getText());
            ResultSet rs3 = stat3.executeQuery();
            int idDestino = rs3.getInt("Id_Cuenta");

            if (textoCantidad.getText() == null || !textoCantidad.getText().matches("^\\d+(\\.\\d{1,2})?$")) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error");
                error.setContentText("Debe introducir un número de hasta 2 decimales.\nUse . en vez de ,");
                error.initOwner(botonAceptar.getScene().getWindow());
                error.showAndWait();
            }
            else if (Double.parseDouble(textoCantidad.getText()) > saldo) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error");
                error.setContentText("Está intentando mandar una cantidad\nmayor de la que posee.");
                error.initOwner(botonAceptar.getScene().getWindow());
                error.showAndWait();
            }
            else if (textoIBAN.getText() == null || !textoIBAN.getText().matches("^ES\\d{2}\\s\\d{4}\\s\\d{4}\\s\\d{4}\\s\\d{4}\\s\\d{4}$")) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error");
                error.setContentText("Debe introducir un IBAN válido.\nEl formato es 'ESXX XXXX XXXX XXXX XXXX XXXX'");
                error.initOwner(botonAceptar.getScene().getWindow());
                error.showAndWait();
            }
            else if (idDestino == idOrigen) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error");
                error.setContentText("Ha introducido su propio IBAN.");
                error.initOwner(botonAceptar.getScene().getWindow());
                error.showAndWait();
            }
            else if (!rs3.next()) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error");
                error.setContentText("La cuenta introducida no existe.");
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
            else {
                Hilo hilo = new Hilo(idOrigen, idDestino, con);
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

                Alert mensaje = new Alert(Alert.AlertType.INFORMATION);
                mensaje.setHeaderText(null);
                mensaje.setTitle("Transferencia realizada");
                mensaje.setContentText("La transferencia se ha realizado con éxito");
                mensaje.initOwner(botonAceptar.getScene().getWindow());
                mensaje.showAndWait();

                stage = (Stage) botonVolver.getScene().getWindow();
                stage.close();
            }
        }
        catch (Exception e) {e.printStackTrace();}
    }

    public void volver(ActionEvent event) {
        stage = (Stage) botonVolver.getScene().getWindow();
        stage.close();
    }

    public void setCorreo(String correo) {this.correo = correo;}

    public class Hilo extends Thread {
        int idOrigen;
        int idDestino;
        Connection con;
        public Hilo(int org, int des, Connection c) {
            this.idOrigen = org;
            this.idDestino = des;
            this.con = c;
        }
        public void run() {
            try {
                Class.forName("org.sqlite.JDBC");
                DriverManager.registerDriver(new org.sqlite.JDBC());

                String crearTransfer = "insert into Transferencia(Estado, Dinero, Fecha, Extras, Notas) values(?, ?, ?, ?, ?)";
                PreparedStatement stat4 = con.prepareStatement(crearTransfer);
                String fecha = textoFecha.getValue() + " " + textoHora.getText();
                stat4.setString(1, "Ejecutada");
                stat4.setDouble(2, Double.parseDouble(textoCantidad.getText()));
                stat4.setString(3, fecha);
                stat4.setDouble(4, 0.0);
                stat4.setString(5, textoNotas.getText());
                stat4.executeUpdate();

                //saco el id de la transferencia que se acaba de realizar
                String buscarTransfer = "select max(Id_transferencia) from Transferencia";
                Statement stat5 = con.createStatement();
                ResultSet rs4 = stat5.executeQuery(buscarTransfer);
                int idTransfer = rs4.getInt(1);

                //registrar la transferencia, la cuentas de origen y de destino en la tabla cuenta_transferencia
                String registrarTransfer = "insert into Cuenta_transferencia(Id_transferencia, Origen, Destino) values(?, ?, ?)";
                PreparedStatement stat6 = con.prepareStatement(registrarTransfer);
                stat6.setInt(1, idTransfer);
                stat6.setInt(2, idOrigen);
                stat6.setInt(3, idDestino);
                stat6.executeUpdate();

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
                stat9.setString(2, "Se ha hecho una transferencia de " + Double.parseDouble(textoCantidad.getText()) + "€ a " + rs2.getString("Nombre") + " " + rs2.getString("Apellidos"));
                stat9.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()));
                stat9.executeUpdate();
                //con.close();
            }
            catch (Exception e) {e.printStackTrace();}
        }
    }
}
