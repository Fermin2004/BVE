package controladores;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

public class areaUsuarioControl {
    public GridPane gridUsuario;
    public Stage stage;
    public Scene scene;
    public Label textoCiudad;
    public Label textoCorreo;
    public Label textoTelf;
    public Label textoNombre;
    public Label textoDirecc;
    public Button botonCiudad;
    public Label textoIBAN;
    public Label textoTipo;
    public String correo;
    public String contra;

    public void MostrarDatos(String correo, String contra) {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());

            String buscarcliente = "select * from Cliente where Correo_elec = ?";
            PreparedStatement stat = con.prepareStatement(buscarcliente);
            stat.setString(1, correo);
            ResultSet rs = stat.executeQuery();
            int id = rs.getInt("Id_Cliente");
            String nom = rs.getString("Nombre");
            String ape = rs.getString("Apellidos");
            int telf = rs.getInt("Teléfono");
            String ciudad = rs.getString("Ciudad");
            String direc = rs.getString("Direccion");
            String cuenta = rs.getString("Correo_elec");

            String buscarcuenta = "select * from Cuenta where Id_Cliente = ?";
            PreparedStatement stat2 = con.prepareStatement(buscarcuenta);
            stat2.setInt(1, id);
            ResultSet rs2 = stat2.executeQuery();
            String IBAN = rs2.getString("IBAN");
            String tipo = rs2.getString("Tipo");

            textoNombre.setText(nom + " " + ape);
            textoTelf.setText(String.valueOf(telf));
            textoCiudad.setText(ciudad);
            textoDirecc.setText(direc);
            textoCorreo.setText(cuenta);
            textoIBAN.setText(IBAN);
            textoTipo.setText(tipo);
        }
        catch (Exception e) {e.printStackTrace();}
    }

    public int BuscarCliente() throws Exception {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());

            String idcliente = "select Id_cliente from Cliente where Correo_elec = ?";
            PreparedStatement stat = con.prepareStatement(idcliente);
            stat.setString(1, textoCorreo.getText());
            ResultSet rs = stat.executeQuery();
            return rs.getInt("Id_cliente");
        }
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }

    public void CambiarNombre() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Modificación de datos - nombre y apellidos");
        ButtonType aceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        ButtonType rechazar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        TextField nom = new TextField();
        TextField ape = new TextField();

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20, 70, 10, 10));
        grid.add(new Label("Nombre: "), 0, 0);
        grid.add(nom, 1, 0);
        grid.add(new Label("Apellidos: "), 0, 1);
        grid.add(ape, 1, 1);

        dialog.getDialogPane().getButtonTypes().addAll(aceptar, rechazar);
        dialog.initOwner(botonCiudad.getScene().getWindow());
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == aceptar) {
                return new Pair<>(nom.getText(), ape.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(pair -> {
            if (pair.getKey().isEmpty() || pair.getValue().isEmpty()) {
                Alert mensaje = new Alert(Alert.AlertType.ERROR);
                mensaje.setHeaderText(null);
                mensaje.setTitle("Error");
                mensaje.setContentText("Falta de introducir algún dato.\nAsegúrese de introducir todo correctamente.");
                mensaje.initOwner(botonCiudad.getScene().getWindow());
                mensaje.showAndWait();
            }
            else if (pair.getKey().length() > 15 && pair.getValue().length() > 40) {
                Alert mensaje = new Alert(Alert.AlertType.ERROR);
                mensaje.setHeaderText(null);
                mensaje.setTitle("Error");
                mensaje.setContentText("Ha introducido un nombre y apellidos muy largos.");
                mensaje.initOwner(botonCiudad.getScene().getWindow());
                mensaje.showAndWait();
            }
            else if (!pair.getKey().matches("^[a-zA-ZÀ-ÿ ]{1,15}$")) {
                Alert mensaje = new Alert(Alert.AlertType.ERROR);
                mensaje.setHeaderText(null);
                mensaje.setTitle("Error");
                mensaje.setContentText("Ha introducido un nombre muy largo,\no ha introducido datos no correctos.");
                mensaje.initOwner(botonCiudad.getScene().getWindow());
                mensaje.showAndWait();
            }
            else if (!pair.getValue().matches("^[a-zA-ZÀ-ÿ ]{1,40}$")) {
                Alert mensaje = new Alert(Alert.AlertType.ERROR);
                mensaje.setHeaderText(null);
                mensaje.setTitle("Error");
                mensaje.setContentText("Ha introducido un apellido muy largo,\no ha introducido datos no correctos.");
                mensaje.initOwner(botonCiudad.getScene().getWindow());
                mensaje.showAndWait();
            }
            else {
                HiloNombre hilo = new HiloNombre(pair);
                try {hilo.start();}
                catch (Exception ex) {
                    Alert mensaje2 = new Alert(Alert.AlertType.ERROR);
                    mensaje2.setHeaderText(null);
                    mensaje2.setTitle("Error de base de datos.");
                    mensaje2.setContentText("Se ha producido un error en el sistema.\nPor favor, cierre sesión y vuelva a intentarlo.");
                    mensaje2.initOwner(botonCiudad.getScene().getWindow());
                    mensaje2.showAndWait();
                }
                try {hilo.join();}
                catch (InterruptedException e) {throw new RuntimeException(e);}

                Alert mensaje = new Alert(Alert.AlertType.INFORMATION);
                mensaje.setHeaderText(null);
                mensaje.setTitle("Datos cambiados");
                mensaje.setContentText("Se han cambiado su nombre y apellido.");
                mensaje.initOwner(botonCiudad.getScene().getWindow());
                mensaje.showAndWait();

                MostrarDatos(correo, contra);
            }
        });
    }

    public class HiloNombre extends Thread {
        Pair pair;
        public HiloNombre(Pair p) {this.pair = p;}
        public void run() {
            try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
                Class.forName("org.sqlite.JDBC");
                DriverManager.registerDriver(new org.sqlite.JDBC());

                int id = BuscarCliente();
                String cambiarNombre = "update Cliente set Nombre = '" + pair.getKey() + "', Apellidos = '" + pair.getValue() + "' where Id_cliente = ?";
                PreparedStatement stat2 = con.prepareStatement(cambiarNombre);
                stat2.setInt(1, id);
                stat2.executeUpdate();

                String buscarId = "select Id_cuenta from Cuenta cue inner join Cliente cli on cli.Id_cliente = cue.Id_Cliente where cli.Correo_elec = ?";
                PreparedStatement stat = con.prepareStatement(buscarId);
                stat.setString(1, correo);
                ResultSet rs = stat.executeQuery();

                String mostrarMensaje = "insert into Buzón(Id_Cuenta, Mensaje, Fecha) values(?, ?, ?)";
                PreparedStatement stat3 = con.prepareStatement(mostrarMensaje);
                stat3.setInt(1, rs.getInt("Id_Cuenta"));
                stat3.setString(2, "Se han cambiado su nombre y apellidos");
                stat3.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()));
                stat3.executeUpdate();
            }
            catch (Exception e) {e.printStackTrace();}
        }
    }

    public void CambiarTelf() {
        TextInputDialog mensaje = new TextInputDialog();
        mensaje.setTitle("Modificación de datos - número de teléfono");
        mensaje.setContentText("Introduzca un nuevo número:");
        mensaje.setHeaderText(null);
        mensaje.initOwner(botonCiudad.getScene().getWindow());
        Optional<String> resultado = mensaje.showAndWait();

        resultado.ifPresent(num -> {
            if (num.isEmpty() || !num.matches("^\\d{9}$")) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error");
                error.setContentText("No ha introducido el número,\no no se ha introducido corréctamente, debe introducir un número de 9 dígitos.");
                error.initOwner(botonCiudad.getScene().getWindow());
                error.showAndWait();
            }
            else {
                HiloTelf hiloTelf = new HiloTelf(Integer.parseInt(num));
                try {hiloTelf.start();}
                catch (Exception ex) {
                    Alert mensaje2 = new Alert(Alert.AlertType.ERROR);
                    mensaje2.setHeaderText(null);
                    mensaje2.setTitle("Error de base de datos.");
                    mensaje2.setContentText("Se ha producido un error en el sistema.\nPor favor, cierre sesión y vuelva a intentarlo.");
                    mensaje2.initOwner(botonCiudad.getScene().getWindow());
                    mensaje2.showAndWait();
                }
                try {hiloTelf.join();}
                catch (InterruptedException e) {throw new RuntimeException(e);}

                Alert mensaje2 = new Alert(Alert.AlertType.INFORMATION);
                mensaje2.setHeaderText(null);
                mensaje2.setTitle("Datos cambiados");
                mensaje2.setContentText("Se han cambiado su número de teléfono.");
                mensaje2.initOwner(botonCiudad.getScene().getWindow());
                mensaje2.showAndWait();

                MostrarDatos(correo, contra);
            }
        });
    }

    public class HiloTelf extends Thread {
        int num;
        public HiloTelf(int n) {this.num = n;}
        public void run() {
            try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
                Class.forName("org.sqlite.JDBC");
                DriverManager.registerDriver(new org.sqlite.JDBC());

                int id = BuscarCliente();
                String cambiarNumero = "update Cliente set Teléfono = '" + num + "' where Id_cliente = ?";
                PreparedStatement stat = con.prepareStatement(cambiarNumero);
                stat.setInt(1, id);
                stat.executeUpdate();

                String buscarId = "select Id_cuenta from Cuenta cue inner join Cliente cli on cli.Id_cliente = cue.Id_Cliente where cli.Correo_elec = ?";
                PreparedStatement stat2 = con.prepareStatement(buscarId);
                stat2.setString(1, correo);
                ResultSet rs = stat2.executeQuery();

                String mostrarMensaje = "insert into Buzón(Id_Cuenta, Mensaje, Fecha) values(?, ?, ?)";
                PreparedStatement stat3 = con.prepareStatement(mostrarMensaje);
                stat3.setInt(1, rs.getInt("Id_Cuenta"));
                stat3.setString(2, "Se ha cambiado su número de teléfono.");
                stat3.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()));
                stat3.executeUpdate();
            }
            catch (Exception e) {e.printStackTrace();}
        }
    }

    public void CambiarCiudad() {
        TextInputDialog mensaje = new TextInputDialog();
        mensaje.setTitle("Cambio de datos - ciudad de residencia");
        mensaje.setContentText("Introduzca una nueva ciudad:");
        mensaje.setHeaderText(null);
        mensaje.initOwner(botonCiudad.getScene().getWindow());
        Optional<String> resultado = mensaje.showAndWait();

        resultado.ifPresent(ciudad -> {
            if (ciudad.isEmpty() || !ciudad.matches("^[a-zA-ZÀ-ÿ]{1,15}$")) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error");
                error.setContentText("No ha introducido una ciudad,\no no se ha introducido corréctamente, introduzca una ciudad de máximo 15 letras.");
                error.initOwner(botonCiudad.getScene().getWindow());
                error.showAndWait();
            }
            else {
                HiloCiudad h = new HiloCiudad(ciudad);
                try {h.start();}
                catch (Exception ex) {
                    Alert mensaje2 = new Alert(Alert.AlertType.ERROR);
                    mensaje2.setHeaderText(null);
                    mensaje2.setTitle("Error de base de datos.");
                    mensaje2.setContentText("Se ha producido un error en el sistema.\nPor favor, cierre sesión y vuelva a intentarlo.");
                    mensaje2.initOwner(botonCiudad.getScene().getWindow());
                    mensaje2.showAndWait();
                }
                try {h.join();}
                catch (InterruptedException e) {throw new RuntimeException(e);}
                Alert mensaje2 = new Alert(Alert.AlertType.INFORMATION);
                mensaje2.setHeaderText(null);
                mensaje2.setTitle("Datos cambiados");
                mensaje2.setContentText("Se han cambiado su ciudad de residencia.");
                mensaje2.initOwner(botonCiudad.getScene().getWindow());
                mensaje2.showAndWait();

                MostrarDatos(correo, contra);
            }
        });
    }

    public class HiloCiudad extends Thread {
        String ciudad;
        public HiloCiudad(String ciu) {this.ciudad = ciu;}
        public void run() {
            try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
                Class.forName("org.sqlite.JDBC");
                DriverManager.registerDriver(new org.sqlite.JDBC());

                int id = BuscarCliente();
                String cambiarCiudad = "update Cliente set Ciudad = '" + ciudad + "' where Id_cliente = ?";
                PreparedStatement stat = con.prepareStatement(cambiarCiudad);
                stat.setInt(1, id);
                stat.executeUpdate();

                String buscarId = "select Id_cuenta from Cuenta cue inner join Cliente cli on cli.Id_cliente = cue.Id_Cliente where cli.Correo_elec = ?";
                PreparedStatement stat2 = con.prepareStatement(buscarId);
                stat2.setString(1, correo);
                ResultSet rs = stat2.executeQuery();

                String mostrarMensaje = "insert into Buzón(Id_Cuenta, Mensaje, Fecha) values(?, ?, ?)";
                PreparedStatement stat3 = con.prepareStatement(mostrarMensaje);
                stat3.setInt(1, rs.getInt("Id_Cuenta"));
                stat3.setString(2, "Se ha cambiado su ciudad de residencia.");
                stat3.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()));
                stat3.executeUpdate();
            }
            catch (Exception e) {e.printStackTrace();}
        }
    }

    public void CambiarDirec() {
        TextInputDialog mensaje = new TextInputDialog();
        mensaje.setTitle("Cambio de datos - dirección");
        mensaje.setContentText("Introduzca una nueva dirección:");
        mensaje.setHeaderText(null);
        mensaje.initOwner(botonCiudad.getScene().getWindow());
        Optional<String> resultado = mensaje.showAndWait();

        resultado.ifPresent(direc -> {
            if (direc.isEmpty() || direc.length() > 40) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error");
                error.setContentText("No ha introducido una dirección,\no no se ha introducido corréctamente, introduzca una dirección de máximo 40 carácteres.");
                error.initOwner(botonCiudad.getScene().getWindow());
                error.showAndWait();
            }
            else {
                HiloDirec h = new HiloDirec(direc);
                h.start();
                try {h.join();}
                catch (InterruptedException e) {throw new RuntimeException(e);}
                Alert mensaje2 = new Alert(Alert.AlertType.INFORMATION);
                mensaje2.setHeaderText(null);
                mensaje2.setTitle("Datos cambiados");
                mensaje2.setContentText("Se ha cambiado su dirección.");
                mensaje2.initOwner(botonCiudad.getScene().getWindow());
                mensaje2.showAndWait();

                MostrarDatos(correo, contra);
            }
        });
    }

    public class HiloDirec extends Thread {
        String direc;
        public HiloDirec(String d) {this.direc = d;}
        public void run() {
            try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
                Class.forName("org.sqlite.JDBC");
                DriverManager.registerDriver(new org.sqlite.JDBC());

                int id = BuscarCliente();
                String cambiarCiudad = "update Cliente set Direccion = '" + direc + "' where Id_cliente = ?";
                PreparedStatement stat = con.prepareStatement(cambiarCiudad);
                stat.setInt(1, id);
                stat.executeUpdate();

                String buscarId = "select Id_cuenta from Cuenta cue inner join Cliente cli on cli.Id_cliente = cue.Id_Cliente where cli.Correo_elec = ?";
                PreparedStatement stat2 = con.prepareStatement(buscarId);
                stat2.setString(1, correo);
                ResultSet rs = stat2.executeQuery();

                String mostrarMensaje = "insert into Buzón(Id_Cuenta, Mensaje, Fecha) values(?, ?, ?)";
                PreparedStatement stat3 = con.prepareStatement(mostrarMensaje);
                stat3.setInt(1, rs.getInt("Id_Cuenta"));
                stat3.setString(2, "Se ha cambiado su dirección.");
                stat3.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()));
                stat3.executeUpdate();
            }
            catch (Exception e) {e.printStackTrace();}
        }
    }

    public void CambiarCorreo() {
        Alert mensaje2 = new Alert(Alert.AlertType.INFORMATION);
        mensaje2.setHeaderText(null);
        mensaje2.setTitle("Aviso");
        mensaje2.setContentText("Para cambiar el correo asociado a su cuenta,\ndebe acceder a la sucursal de BVE más cercana.");
        mensaje2.initOwner(botonCiudad.getScene().getWindow());
        mensaje2.showAndWait();
    }
}
