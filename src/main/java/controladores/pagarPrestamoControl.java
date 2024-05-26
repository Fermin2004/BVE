package controladores;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class pagarPrestamoControl {
    public int idprestamo;
    public String correo;
    public Stage stage;
    public Double restante;
    public Double saldo;
    public TextField textoCantidad;
    public Button botonVolver;
    public Button botonAceptar;
    public Label textoRestante;

    public void Mostrar(int idprestamo, String correo) {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());

            String verDinero = "select cuen.Saldo, cuen.Id_Cuenta from Cuenta cuen inner join Préstamo pre on pre.Id_cuenta = cuen.Id_Cuenta where pre.Id_Prestamo = ? group by pre.Id_cuenta;";
            PreparedStatement stat = con.prepareStatement(verDinero);
            stat.setInt(1, idprestamo);
            ResultSet rs = stat.executeQuery();
            saldo = rs.getDouble("Saldo");

            String verRestante = "select Restante from Préstamo where Id_Prestamo = ?";
            PreparedStatement stat2 = con.prepareStatement(verRestante);
            stat2.setInt(1, idprestamo);
            ResultSet rs2 = stat2.executeQuery();
            restante = rs2.getDouble("Restante");

            restante = (double) Math.round(restante * 100);
            restante = restante / 100;
            textoRestante.setText(String.valueOf(restante));
        }
        catch (Exception e) {e.printStackTrace();}
    }

    public void Pagar() {
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
            error.setContentText("Se está introduciendo una cantidad\nmayor de la que se tiene.");
            error.initOwner(botonAceptar.getScene().getWindow());
            error.showAndWait();
        }
        else if (Double.parseDouble(textoCantidad.getText()) > restante) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText(null);
            error.setTitle("Error");
            error.setContentText("Se está introduciendo una cantidad\nmayor de la que se debe.");
            error.initOwner(botonAceptar.getScene().getWindow());
            error.showAndWait();
        }
        else {
            if (Double.parseDouble(textoCantidad.getText()) == restante) {
                Hilo hilo = new Hilo(true);
                try {hilo.start();}
                catch (Exception e) {
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
                error.setTitle("Préstamo pagado");
                error.setContentText("Se ha pagado la totalidad del préstamo.");
                error.initOwner(botonAceptar.getScene().getWindow());
                error.showAndWait();
            }
            else {
                Hilo hilo = new Hilo(false);
                try {hilo.start();}
                catch (Exception e) {
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
                error.setTitle("Préstamo pagado");
                error.setContentText("Se ha aceptado el pago.");
                error.initOwner(botonAceptar.getScene().getWindow());
                error.showAndWait();
            }

            stage = (Stage) botonVolver.getScene().getWindow();
            stage.close();
        }
    }
    
    public void volver() {
        stage = (Stage) botonVolver.getScene().getWindow();
        stage.close();
    }

    public class Hilo extends Thread {
        boolean terminar;
        public Hilo(boolean ter) {this.terminar = ter;}
        public void run() {
            try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
                Class.forName("org.sqlite.JDBC");
                DriverManager.registerDriver(new org.sqlite.JDBC());

                if (terminar) {
                    String eliminarPrestamo = "delete from Préstamo where Id_Prestamo = ?";
                    PreparedStatement stat = con.prepareStatement(eliminarPrestamo);
                    stat.setInt(1, idprestamo);
                    stat.executeUpdate();

                    String restarUsuario = "update Cuenta set Saldo = (Saldo - '" + Double.parseDouble(textoCantidad.getText()) + "') where Id_cliente in (select Id_Cliente from Cliente where Correo_elec = ?)";
                    PreparedStatement stat2 = con.prepareStatement(restarUsuario);
                    stat2.setString(1, correo);
                    stat2.executeUpdate();
                }
                else {
                    String restarPrestamo = "update Préstamo set Restante = (Restante - '" + Double.parseDouble(textoCantidad.getText()) + "') where Id_Prestamo = ?";
                    PreparedStatement stat = con.prepareStatement(restarPrestamo);
                    stat.setInt(1, idprestamo);
                    stat.executeUpdate();

                    String restarUsuario = "update Cuenta set Saldo = (Saldo - '" + Double.parseDouble(textoCantidad.getText()) + "') where Id_cliente in (select Id_Cliente from Cliente where Correo_elec = ?)";
                    PreparedStatement stat2 = con.prepareStatement(restarUsuario);
                    stat2.setString(1, correo);
                    stat2.executeUpdate();

                    String actualizarPrestamo = "update Préstamo set Estado = 'Pagado/Al día' where Id_Prestamo = ?";
                    PreparedStatement stat3 = con.prepareStatement(actualizarPrestamo);
                    stat3.setInt(1, idprestamo);
                    stat3.executeUpdate();

                    String verNombre = "select Id_cuenta from Cuenta cue inner join Cliente cli on cli.Id_cliente = cue.Id_Cliente where cli.Correo_elec = ?";
                    PreparedStatement stat10 = con.prepareStatement(verNombre);
                    stat10.setString(1, correo);
                    ResultSet rs2 = stat10.executeQuery();

                    String verPrestamo = "select * from Préstamo where Id_Prestamo = ?";
                    PreparedStatement stat11 = con.prepareStatement(verPrestamo);
                    stat11.setInt(1, idprestamo);
                    ResultSet rs3 = stat11.executeQuery();

                    String mensaje = "insert into Buzón(Id_Cuenta, Mensaje, Fecha) values(?, ?, ?)";
                    PreparedStatement stat9 = con.prepareStatement(mensaje);
                    stat9.setInt(1, rs2.getInt("Id_cuenta"));
                    stat9.setString(2, "Se ha hecho un pago en el préstamo " + rs3.getString("Fecha"));
                    stat9.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()));
                    stat9.executeUpdate();
                }
            }
            catch (Exception e) {e.printStackTrace();}
        }
    }
    
    public void setIdprestamo(int idprestamo) {
        this.idprestamo = idprestamo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
