package controladores;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.util.Calendar;
import java.util.Objects;

public class pedirPrestamoControl {
    public Stage stage;
    public Scene scene;
    public String correo;
    public TextField textoCantidad;
    public DatePicker textoFecha;
    public Button botonAceptar;
    public Button botonVolver;
    public ChoiceBox<String> seleccTipo;

    public void initialize() {
        seleccTipo.getItems().addAll("Personal", "Hipotecario");
    }

    public void PedirPrestamo() {
        if (textoCantidad.getText() == null || !textoCantidad.getText().matches("^\\d+(\\.\\d{1,2})?$")) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText(null);
            error.setTitle("Error");
            error.setContentText("Debe introducir un número de hasta 2 decimales.\nUse . en vez de ,");
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
        else if (textoFecha.getValue().isBefore(LocalDate.now())) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText(null);
            error.setTitle("Error");
            error.setContentText("Debe elegir una fecha después de la actual.");
            error.initOwner(botonAceptar.getScene().getWindow());
            error.showAndWait();
        }
        else if (seleccTipo.getValue() == null) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText(null);
            error.setTitle("Error");
            error.setContentText("Debe introducir el tipo del préstamo.");
            error.initOwner(botonVolver.getScene().getWindow());
            error.showAndWait();
        }
        else {
            Hilo hilo = new Hilo();
            try {hilo.start();}
		    catch (Exception ex) {
            	Alert mensaje2 = new Alert(Alert.AlertType.ERROR);
            	mensaje2.setHeaderText(null);
            	mensaje2.setTitle("Error de base de datos.");
            	mensaje2.setContentText("Se ha producido un error en el sistema.\nPor favor, cierre sesión y vuelva a intentarlo.");
            	mensaje2.initOwner(botonAceptar.getScene().getWindow());
            	mensaje2.showAndWait();
            }
            try {hilo.join();}
            catch (InterruptedException e) {throw new RuntimeException(e);}
            Alert error = new Alert(Alert.AlertType.INFORMATION);
            error.setHeaderText(null);
            error.setTitle("Préstamo solicitado");
            error.setContentText("Se le ha conferido el préstamo.\nAsegúrese de introducir la cantidad necesaria cuando se le requiera.");
            error.initOwner(botonAceptar.getScene().getWindow());
            error.showAndWait();

            stage = (Stage) botonVolver.getScene().getWindow();
            stage.close();
        }
    }

    public class Hilo extends Thread {
        public synchronized void run() {
            try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
                Class.forName("org.sqlite.JDBC");
                DriverManager.registerDriver(new org.sqlite.JDBC());

                String buscarId = "select Id_cuenta from Cuenta cue inner join Cliente cli on cli.Id_cliente = cue.Id_Cliente where cli.Correo_elec = ?";
                PreparedStatement stat = con.prepareStatement(buscarId);
                stat.setString(1, correo);
                ResultSet rs = stat.executeQuery();

                int interes = 0;
                int anio = Year.now().getValue();
                String anio2 = String.valueOf(textoFecha.getValue()).substring(0, 4);
                Double devolver = Double.valueOf(textoCantidad.getText());

                if (Objects.equals(seleccTipo.getValue(), "Personal")) {
                    interes = 4;
                    interes = interes + (2 * (Integer.parseInt(anio2) - anio));
                    devolver = devolver + (devolver * ((double) interes / 100));
                }
                else if (Objects.equals(seleccTipo.getValue(), "Hipotecario")) {
                    interes = 3;
                    interes = interes + (2 * (Integer.parseInt(anio2) - anio));
                    devolver = devolver + (devolver * ((double) interes / 100));
                }

                String anadirPrestamo = "insert into Préstamo(Id_cuenta, Cantidad, Restante, Interés, Fecha, Fecha_límite, Tipo, Estado) values(?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stat2 = con.prepareStatement(anadirPrestamo);
                stat2.setInt(1, rs.getInt("Id_cuenta"));
                stat2.setDouble(2, Double.parseDouble(textoCantidad.getText()));
                stat2.setDouble(3, devolver);
                stat2.setString(4, interes + "%");
                stat2.setString(5, String.valueOf(LocalDate.now()));
                stat2.setString(6, String.valueOf(textoFecha.getValue()));
                stat2.setString(7, seleccTipo.getValue());
                stat2.setString(8, "Aprobado");
                stat2.executeUpdate();

                //esto está justificado porque no se pueden hacer updates con joins en sqlite
                String anadirDinero = "update Cuenta set Saldo = (Saldo + '" + Double.parseDouble(textoCantidad.getText()) + "') where Id_cliente = (select Id_cliente from Cliente where Correo_elec = ?)";
                PreparedStatement stat3 = con.prepareStatement(anadirDinero);
                stat3.setString(1, correo);
                stat3.executeUpdate();

                String mensaje = "insert into Buzón(Id_Cuenta, Mensaje, fecha) values(?, ?, ?)";
                PreparedStatement stat4 = con.prepareStatement(mensaje);
                stat4.setInt(1, rs.getInt("Id_cuenta"));
                stat4.setString(2, "Se le ha conferido un préstamo de " + textoCantidad.getText() + "€, a devolver en " + textoFecha.getValue());
                stat4.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()));
                stat4.executeUpdate();
            }
            catch (Exception e) {e.printStackTrace();}
        }
    }

    public void Cerrar() {
        stage = (Stage) botonVolver.getScene().getWindow();
        stage.close();
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
