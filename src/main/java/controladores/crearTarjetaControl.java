package controladores;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

public class crearTarjetaControl {
    public String correo;
    public Stage stage;
    public Scene scene;
    public Button botonVolver;
    public ChoiceBox<String> seleccTipo;
    public TextField textoPin;
    public ChoiceBox<String> seleccFormato;

    @FXML
    public void initialize() {
        seleccTipo.getItems().addAll("Débito", "Crédito");
        seleccFormato.getItems().addAll("Física", "Digital");
    }

    public void crearTarjeta() {
        if (seleccTipo.getValue() == null) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText(null);
            error.setTitle("Error");
            error.setContentText("Debe introducir el tipo de la tarjeta.");
            error.initOwner(botonVolver.getScene().getWindow());
            error.showAndWait();
        }
        else if (!textoPin.getText().isEmpty() && !textoPin.getText().matches("(\\d{4})")) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText(null);
            error.setTitle("Error");
            error.setContentText("Escriba un PIN de 4 dígitos.\nRecuérdelo bien.");
            error.initOwner(botonVolver.getScene().getWindow());
            error.showAndWait();
        }
        else if (seleccFormato.getValue() == null) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText(null);
            error.setTitle("Error");
            error.setContentText("Debe introducir el fomato de la tarjeta.");
            error.initOwner(botonVolver.getScene().getWindow());
            error.showAndWait();
        }
        else {
            Hilo hilo = new Hilo();
            try {hilo.start();}
            catch (Exception e) {
                Alert mensaje2 = new Alert(Alert.AlertType.ERROR);
                mensaje2.setHeaderText(null);
                mensaje2.setTitle("Error de base de datos.");
                mensaje2.setContentText("Se ha producido un error en el sistema.\nPor favor, cierre sesión y vuelva a intentarlo.");
                mensaje2.initOwner(botonVolver.getScene().getWindow());
                mensaje2.showAndWait();
            }
            try {hilo.join();}
            catch (InterruptedException e) {throw new RuntimeException(e);}

            Alert error = new Alert(Alert.AlertType.INFORMATION);
            error.setHeaderText(null);
            error.setTitle("Tarjeta creada");
            error.setContentText("La tarjeta se ha creado");
            error.initOwner(botonVolver.getScene().getWindow());
            error.showAndWait();

            stage = (Stage) botonVolver.getScene().getWindow();
            stage.close();
        }
    }

    public void cerrar() {
        stage = (Stage) botonVolver.getScene().getWindow();
        stage.close();
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public class Hilo extends Thread {
        public void run() {
            try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
                Class.forName("org.sqlite.JDBC");
                DriverManager.registerDriver(new org.sqlite.JDBC());

                String buscarCuenta = "select cue.Id_cuenta from Cuenta cue inner join Cliente cli on cli.Id_cliente = cue.Id_Cliente where cli.Correo_elec = ?";
                PreparedStatement stat = con.prepareStatement(buscarCuenta);
                stat.setString(1, correo);
                ResultSet rs = stat.executeQuery();

                Random random = new Random();
                long PAN = (long) (random.nextDouble() * 9_000_000_000_000_000L) + 1_000_000_000_000_000L;
                int anio = (int) (Math.random() * (2030 - 2024 + 1)) + 2024;
                int mes = (int) (Math.random() * 12 + 1);
                int dia;
                if (mes == 2) {dia = (int) (Math.random() * 28 + 1);}
                else {dia = (int) (Math.random() * 31 + 1);}
                String fecha = anio + "-" + mes + "-" + dia;

                String crearTarjeta = "insert into Tarjeta(Id_cuenta, PAN, Tipo, PIN, Válida_hasta, Estado, Formato) values(?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stat2 = con.prepareStatement(crearTarjeta);
                stat2.setInt(1, rs.getInt("Id_cuenta"));
                stat2.setLong(2, PAN);
                stat2.setString(3, seleccTipo.getValue());
                if (textoPin.getText() == null || textoPin.getText().isEmpty()) {stat2.setInt(4, (int) (Math.random() * 8999) + 1000);}
                else {stat2.setInt(4, Integer.parseInt(textoPin.getText()));}
                stat2.setString(5, fecha);
                stat2.setString(6, "Activa");
                stat2.setString(7, seleccFormato.getValue());
                stat2.executeUpdate();

                String mandarMensaje = "insert into Buzón(Id_Cuenta, Mensaje, Fecha) values(?, ?, ?)";
                PreparedStatement stat3 = con.prepareStatement(mandarMensaje);
                stat3.setInt(1, rs.getInt("Id_Cuenta"));
                String alias = "";
                if (Objects.equals(seleccTipo.getValue(), "Débito")) {alias = "Tarj. débito *" + String.valueOf(PAN).substring(String.valueOf(PAN).length() - 4);}
                else if (Objects.equals(seleccTipo.getValue(), "Crédito")) {alias = "Tarj. crédito *" + String.valueOf(PAN).substring(String.valueOf(PAN).length() - 4);}
                stat3.setString(2, "Se ha creado la tarjeta " + seleccTipo.getValue() + " '" + alias + "'");
                stat3.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()));
                stat3.executeUpdate();
            }
            catch (Exception e) {e.printStackTrace();}
        }
    }
}
