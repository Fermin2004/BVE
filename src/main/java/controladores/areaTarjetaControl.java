package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class areaTarjetaControl {
    public Label textoAlias;
    public Label textoTipo;
    public Label textoPIN;
    public Label textoFecha;
    public Label textoFormato;
    public Label textoEstado;
    public Label textoPAN;
    public TableView tablaTarjetas;
    @FXML
    TableColumn<tarjeta, Long> columnaTarjeta = new TableColumn<>();
    @FXML
    TableColumn<tarjeta, tarjeta> columnaDatos = new TableColumn<>();
    @FXML
    TableColumn<tarjeta, tarjeta> columnaEliminar = new TableColumn<>();
    public Button botonCrear;
    public String correo;
    public Button botonMostrar;
    public int PIN;

    public void initialize() {
        botonMostrar.setDisable(true);

    }

    public void setCorreo(String c) {
        correo = c;
    }

    public void MostrarTabla(String correo) {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());

            Ini lector = new Ini(new File("src/main/resources/clases/recursos.ini"));
            lector.load();

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
            columnaDatos.setCellFactory(param -> {
                ImageView a = new ImageView(lector.fetch("Image", "ojo"));
                Button botonVer = new Button(null, a);
                return new TableCell<tarjeta, tarjeta>() {
                    {
                        botonVer.setOnAction(e -> {
                            try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
                                tarjeta t = getTableRow().getItem();
                                String verDatos = "select * from Tarjeta where PAN = ?";
                                PreparedStatement stat2 = con.prepareStatement(verDatos);
                                stat2.setLong(1, t.getPAN());
                                ResultSet rs2 = stat2.executeQuery();
                                if (Objects.equals(rs2.getString("Tipo"), "Débito")) {
                                    textoAlias.setText("Tarj. débito *" + String.valueOf(rs2.getLong("PAN")).substring(String.valueOf(rs2.getLong("PAN")).length() - 4));
                                    textoTipo.setText("Tarjeta de débito");
                                }
                                else if (Objects.equals(rs2.getString("Tipo"), "Crédito")) {
                                    textoAlias.setText("Tarj. débito *" + String.valueOf(rs2.getLong("PAN")).substring(String.valueOf(rs2.getLong("PAN")).length() - 4));
                                    textoTipo.setText("Tarjeta de crédito");
                                }
                                textoPAN.setText(String.valueOf(rs2.getLong("PAN")));
                                textoPIN.setText("****");
                                PIN = rs2.getInt("PIN");
                                String fecha = rs2.getString("Válida_hasta");
                                int a = fecha.indexOf('-');
                                int mes = Integer.parseInt(fecha.substring(a + 1, (fecha.indexOf("-", a + 1))));
                                String anio = fecha.substring(0, a);
                                textoFecha.setText(mes + "/" + anio.substring(anio.length() - 2));
                                textoFormato.setText(rs2.getString("Formato"));
                                textoEstado.setText(rs2.getString("Estado"));
                                botonMostrar.setDisable(false);
                            }
                            catch (SQLException ex) {throw new RuntimeException(ex);}
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
            columnaEliminar.setCellFactory(param -> {
                ImageView a = new ImageView(lector.fetch("Image", "basura"));
                Button botonBorrar = new Button(null, a);
                return new TableCell<tarjeta, tarjeta>() {
                    {
                        botonBorrar.setOnAction(e -> {
                            try {
                                Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db");
                                tarjeta t = getTableRow().getItem();

                                TextInputDialog mensaje = new TextInputDialog();
                                mensaje.setTitle("Descartando tarjeta");
                                mensaje.setContentText("Introduzca su contraseña:");
                                mensaje.setHeaderText(null);
                                mensaje.initOwner(botonBorrar.getScene().getWindow());
                                Optional<String> resultado = mensaje.showAndWait();

                                String buscarContra = "select cue.Contrasena from Cuenta cue inner join Cliente on Cliente.Id_cliente = cue.Id_Cliente where Correo_elec = ?";
                                PreparedStatement stat3 = con.prepareStatement(buscarContra);
                                stat3.setString(1, correo);
                                ResultSet rs = stat3.executeQuery();
                                String a = rs.getString("Contrasena");

                                resultado.ifPresent(contra -> {
                                    if (contra.isEmpty() || !contra.equals(a)) {
                                        Alert error = new Alert(Alert.AlertType.ERROR);
                                        error.setHeaderText(null);
                                        error.setTitle("Error");
                                        error.setContentText("Contraseña incorrecta.");
                                        error.initOwner(botonBorrar.getScene().getWindow());
                                        error.showAndWait();
                                    }
                                    else {
                                        HiloBorrar hilo = new HiloBorrar(t.getPAN(), t.getAlias(), con);
                                        try {hilo.start();}
                                        catch (Exception ex) {
                                            Alert mensaje2 = new Alert(Alert.AlertType.ERROR);
                                            mensaje2.setHeaderText(null);
                                            mensaje2.setTitle("Error de base de datos.");
                                            mensaje2.setContentText("Se ha producido un error en el sistema.\nPor favor, cierre sesión y vuelva a intentarlo.");
                                            mensaje2.initOwner(botonBorrar.getScene().getWindow());
                                            mensaje2.showAndWait();
                                        }
                                        try {hilo.join();}
                                        catch (InterruptedException ex) {throw new RuntimeException(ex);}
                                        Alert mensaje2 = new Alert(Alert.AlertType.INFORMATION);
                                        mensaje2.setHeaderText(null);
                                        mensaje2.setTitle("Tarjeta eliminada.");
                                        mensaje2.setContentText("Se ha eliminado su tarjeta.");
                                        mensaje2.initOwner(botonBorrar.getScene().getWindow());
                                        mensaje2.showAndWait();

                                        textoAlias.setText("Nada que mostrar");
                                        textoTipo.setText("Nada que mostrar");
                                        textoPAN.setText("Nada que mostrar");
                                        textoPIN.setText("Nada que mostrar");
                                        textoFecha.setText("Nada que mostrar");
                                        textoFormato.setText("Nada que mostrar");
                                        textoEstado.setText("Nada que mostrar");
                                        botonMostrar.setDisable(true);

                                        String contarTarjetas = "select count(*) as cant from Tarjeta inner join Cuenta cue on cue.Id_Cuenta = Tarjeta.Id_cuenta inner join Cliente cli on cli.Id_cliente = cue.Id_Cliente where cli.Correo_elec = ?";
                                        try {
                                            PreparedStatement stat2 = con.prepareStatement(contarTarjetas);
                                            stat2.setString(1, correo);
                                            ResultSet cant = stat2.executeQuery();

                                            if (cant.getInt("cant") >= 3) {botonCrear.setDisable(true);}
                                            else {botonCrear.setDisable(false);}
                                        }
                                        catch (SQLException ex) {throw new RuntimeException(ex);}

                                        tablaTarjetas.getItems().clear();
                                        try {con.close();}
                                        catch (SQLException ex) {throw new RuntimeException(ex);}
                                        MostrarTabla(correo);
                                    }
                                });
                            }
                            catch (SQLException ex) {throw new RuntimeException(ex);}
                        });
                    }

                    @Override
                    protected void updateItem(tarjeta tarjeta, boolean vacio) {
                        super.updateItem(tarjeta, vacio);
                        if (vacio) {
                            setGraphic(null);
                        } else {
                            setGraphic(botonBorrar);
                        }
                    }
                };
            });
            tablaTarjetas.getItems().addAll(tarjetas);

            String contarTarjetas = "select count(*) as cant from Tarjeta inner join Cuenta cue on cue.Id_Cuenta = Tarjeta.Id_cuenta inner join Cliente cli on cli.Id_cliente = cue.Id_Cliente where cli.Correo_elec = ?";
            PreparedStatement stat2 = con.prepareStatement(contarTarjetas);
            stat2.setString(1, correo);
            ResultSet cant = stat2.executeQuery();

            if (cant.getInt("cant") == 3) {botonCrear.setDisable(true);}
        }
        catch (Exception e) {e.printStackTrace();}
    }

    public void CrearTarjeta() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/clases/crearTarjeta.fxml")));
            Parent p = loader.load();
            crearTarjetaControl control = loader.getController();
            control.setCorreo(correo);
            Stage stage = new Stage();
            stage.setScene(new Scene(p));
            Ini lector = new Ini(new File("src/main/resources/clases/recursos.ini"));
            Image icono = new Image(lector.fetch("Image", "logoNormal"));
            stage.getIcons().add(icono);
            stage.setResizable(false);
            stage.setX(520);
            stage.setY(230);
            stage.setTitle("Creando tarjeta nueva");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(botonCrear.getScene().getWindow());
            stage.show();

            stage.setOnHidden(e -> {
                tablaTarjetas.getItems().clear();
                MostrarTabla(correo);
            });
        }
        catch (IOException e) {throw new RuntimeException(e);}
    }

    public void MostrarPIN() {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());

            String buscarContra = "select Contrasena from Cuenta inner join Cliente on Cliente.Id_cliente = Cuenta.Id_Cliente where Correo_elec = ?";
            PreparedStatement stat = con.prepareStatement(buscarContra);
            stat.setString(1, correo);
            ResultSet rs = stat.executeQuery();
            String contra = rs.getString("Contrasena");

            TextInputDialog mensaje = new TextInputDialog();
            mensaje.setTitle("Mostrar PIN");
            mensaje.setContentText("Introduzca su contraseña:");
            mensaje.setHeaderText(null);
            mensaje.initOwner(botonCrear.getScene().getWindow());
            Optional<String> resultado = mensaje.showAndWait();

            resultado.ifPresent(c -> {
                if (c.isEmpty() || !c.equals(contra)) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setHeaderText(null);
                    error.setTitle("Error");
                    error.setContentText("Contraseña incorrecta.");
                    error.initOwner(botonCrear.getScene().getWindow());
                    error.showAndWait();
                }
                else {
                    textoPIN.setText(String.valueOf(PIN));
                    botonMostrar.setDisable(true);
                }
            });
        }
        catch (Exception e) {e.printStackTrace();}
    }

    public class HiloBorrar extends Thread {
        Long PAN;
        String alias;
        Connection con;
        public HiloBorrar(Long PAN, String ali, Connection con) {
            this.PAN = PAN;
            this.alias = ali;
            this.con = con;
        }
        public void run() {
            try {
                try (Statement stat = con.createStatement()) {
                    stat.execute("PRAGMA foreign_keys = ON");
                }
                Class.forName("org.sqlite.JDBC");
                DriverManager.registerDriver(new org.sqlite.JDBC());

                String verDatos = "delete from Tarjeta where PAN = ?";
                PreparedStatement stat2 = con.prepareStatement(verDatos);
                stat2.setLong(1, this.PAN);
                stat2.executeUpdate();

                String buscarId = "select Id_cuenta from Cuenta cue inner join Cliente cli on cli.Id_cliente = cue.Id_Cliente where cli.Correo_elec = ?";
                PreparedStatement stat3 = con.prepareStatement(buscarId);
                stat3.setString(1, correo);
                ResultSet rs = stat3.executeQuery();

                String mensaje = "insert into Buzón(Id_Cuenta, Mensaje, Fecha) values(?, ?, ?)";
                PreparedStatement stat = con.prepareStatement(mensaje);
                stat.setInt(1, rs.getInt("Id_cuenta"));
                stat.setString(2, "Se ha eliminado la tarjeta " + alias);
                stat.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()));
                stat.executeUpdate();

                //con.close();
            }
            catch (Exception e) {e.printStackTrace();}
        }
    }
}
