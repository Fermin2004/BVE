package controladores;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class CrearClienteControl {

    public Label textoSaludo;
    public TextField textoCorreo;
    public TextField textoNombre;
    public TextField textoApellidos;
    public TextField textoDNI;
    public TextField textoTelf;
    public DatePicker textoFecha;
    public TextField textoCiudad;
    public TextField TextoDireccion;
    public PasswordField textoContra;
    public ChoiceBox<String> seleccTipo;
    public String[] cuentas = {"Corriente", "Nómina", "Ahorro"};
    public Label textoAviso;
    public CheckBox YoRobot;
    public Button botonTerminos;
    public Button botonCrear;
    public Button botonVolver;
    private Stage stage;
    private Scene scene;
    public boolean terminosLeidos;
    public boolean noRobot;

    @FXML
    public void initialize() {
        int num = (int) (Math.random() * 3 + 1);
        if (num == 1) {textoSaludo.setText("¡Bienvenido a BVE!"); textoSaludo.setFont(new Font("System", 29));}
        else if (num == 2) {textoSaludo.setText("Está a un paso de formar parte de la familia BVE"); textoSaludo.setFont(new Font("System", 19));}
        else if (num == 3) {textoSaludo.setText("Tu cuenta segura, a un par de preguntas"); textoSaludo.setFont(new Font("System", 21));}
        seleccTipo.getItems().addAll(cuentas);
        textoAviso.setVisible(false);
        botonCrear.setDisable(true);
        terminosLeidos = false;
        textoFecha.setEditable(false);
        /*textoDNI.setText("16644147J");
        textoNombre.setText("maau");
        textoApellidos.setText("swsed");
        textoTelf.setText("651821834");
        textoCiudad.setText("EDED");
        TextoDireccion.setText("aaa");
        textoCorreo.setText("maau@gmail.com");
        textoContra.setText("C0ntr@3n@");*/

    }

    public void Volver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(inicioControl.class.getResource("/clases/inicioSesion.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(loader.load());
            stage.setScene(scene);
            Ini lector = new Ini(new File("src/main/resources/clases/recursos.ini"));
            Image icono = new Image(lector.fetch("Image", "logoNormal"));
            stage.getIcons().add(icono);
            stage.setTitle("Inicio de sesión");
            stage.setResizable(false);
            stage.setX(400);
            stage.setY(150);
            stage.show();
        }
        catch (IOException e) {e.printStackTrace();}
    }

    public void TerminosCondiciones(ActionEvent event) {
        TextArea area = new TextArea("Términos y Condiciones de Uso.\n\nBienvenido/a a BVE. Antes de proceder con la creación de su cuenta, le pedimos que leas detenidamente nuestros términos y condiciones de uso:\n\n1. Responsabilidad del Usuario:\n   - Al crear una cuenta, el usuario acepta la responsabilidad de mantener la confidencialidad de sus credenciales de inicio de sesión y de todas las actividades realizadas en su cuenta.\n   - El usuario es responsable de proporcionar información precisa y actualizada durante el proceso de registro.\n\n2. Uso Adecuado del Servicio:\n   - El usuario se compromete a utilizar el servicio de forma legal y ética, respetando los derechos de otros usuarios y cumpliendo con todas las leyes y regulaciones aplicables.\n   - Se prohíbe el uso del servicio para actividades ilegales, fraudulentas o que puedan causar daño a terceros.\n\n3. Privacidad y Protección de Datos:\n   - Nos comprometemos a proteger la privacidad y seguridad de los datos personales proporcionados por los usuarios, conforme a nuestra política de privacidad.\n   - El usuario acepta que cierta información, como el nombre de usuario y la foto de perfil, pueda ser visible para otros usuarios de la plataforma.\n\n4. Modificación de los Términos y Condiciones:\n   - Nos reservamos el derecho de modificar estos términos y condiciones en cualquier momento, sin previo aviso.\n   - Se recomienda al usuario revisar periódicamente los términos y condiciones actualizados.\n\nAl crear una cuenta, el usuario acepta cumplir con estos términos y condiciones de uso. Si tiene alguna pregunta o inquietud, no dude en ponerse en contacto con nosotros.\n\nGracias por confiar en nosotros.");
        area.setWrapText(true);
        area.setEditable(false);
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.getDialogPane().setContent(area);
        alerta.setResizable(false);
        alerta.setTitle("Términos y condiciones");
        alerta.initOwner(botonCrear.getScene().getWindow());
        alerta.showAndWait();
        terminosLeidos = true;
        if (noRobot) {botonCrear.setDisable(false);}
    }

    public void NoSoyRobot(ActionEvent event) {
        noRobot = true;
        if (terminosLeidos) {botonCrear.setDisable(false);}
    }

    public void MostrarMensaje() {
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(3)
        );
        visiblePause.setOnFinished(
                event -> textoAviso.setVisible(false)
        );
        visiblePause.play();
        textoAviso.setVisible(true);
    }

    public int BuscarCliente(String DNI, String cuenta) throws Exception {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());

            String buscar = "select Id_cliente from Cliente where DNI = ? and Correo_elec = ?";
            PreparedStatement stat = con.prepareStatement(buscar);
            stat.setString(1, DNI);
            stat.setString(2, cuenta);
            ResultSet rs = stat.executeQuery();
            return rs.getInt("Id_cliente");
        }
        catch (Exception e) {throw new Exception("Se ha producido un error");}
    }

    public void CrearCuenta(ActionEvent event) {
        if (textoDNI.getText() == null || textoDNI.getText().trim().isEmpty() || !textoDNI.getText().matches("^[0-9]{8}[a-zA-Z]$")) {textoAviso.setText("Introduzca un DNI válido"); MostrarMensaje();}
        else if (textoNombre.getText() == null || textoNombre.getText().trim().isEmpty() || !textoNombre.getText().matches("^[a-zA-ZÀ-ÿ ]{1,15}$")) {textoAviso.setText("Escriba un nombre de máximo 15 carác."); MostrarMensaje();}
        else if (textoApellidos.getText() == null || textoApellidos.getText().trim().isEmpty() || !textoApellidos.getText().matches("^[a-zA-ZÀ-ÿ ]{1,40}$")) {textoAviso.setText("Escriba apellidos de máximo 40 carác."); MostrarMensaje();}
        else if (textoTelf.getText() == null || textoTelf.getText().trim().isEmpty() || !textoTelf.getText().matches("^\\d{9}$")) {textoAviso.setText("Escriba un número de telf de 9 núms."); MostrarMensaje();}
        else if (textoCorreo.getText() == null || textoCorreo.getText().trim().isEmpty() || !textoCorreo.getText().matches("^[^ @]{1,30}@[^ @]{1,10}\\.[^ @]+$")) {textoAviso.setText("Introduzca un correo válido de 40 carác."); MostrarMensaje();}
        else if (textoFecha.getValue() == null) {textoAviso.setText("Introduzca su fecha de nacimiento"); MostrarMensaje();}
        else if (textoCiudad.getText() == null || textoCiudad.getText().trim().isEmpty() || !textoCiudad.getText().matches("^[a-zA-ZÀ-ÿ]{1,15}$")) {textoAviso.setText("Escriba una ciudad con 15 carác."); MostrarMensaje();}
        else if (TextoDireccion.getText() == null || TextoDireccion.getText().trim().isEmpty()) {textoAviso.setText("Escriba una dirección de 40 carác."); MostrarMensaje();}
        else if (textoContra.getText() == null || textoContra.getText().trim().isEmpty() || !textoContra.getText().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^¿!¡?/()&+=]).{8,20}$")) {textoAviso.setText("Contra debe tener mayúsculas, minúsculas,\n núms, símbolos, 8 carácteres mín/20 máx"); MostrarMensaje();}
        else if (seleccTipo.getValue() == null || seleccTipo.getValue().isEmpty()) {textoAviso.setText("Debe escoger un tipo de cuenta"); MostrarMensaje();}
        else {
            try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
                Class.forName("org.sqlite.JDBC");
                DriverManager.registerDriver(new org.sqlite.JDBC());

                String queryDNI = "select * from Cliente where DNI = ? and Correo_elec = ?";
                PreparedStatement stat = con.prepareStatement(queryDNI);
                stat.setString(1, textoDNI.getText());
                stat.setString(2, textoCorreo.getText());
                ResultSet rs = stat.executeQuery();
                if (rs.next()) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setHeaderText(null);
                    error.setTitle("Aviso");
                    error.setContentText("Ya tiene una cuenta con su DNI,\nsi quiere crear otra, introduzca una cuenta de correo diferente.");
                    error.initOwner(botonCrear.getScene().getWindow());
                    error.showAndWait();
                }
                else {
                    String queryCuenta = "select * from Cliente where Correo_elec = ?";
                    PreparedStatement stat3 = con.prepareStatement(queryCuenta);
                    stat3.setString(1, textoCorreo.getText());
                    ResultSet rs2 = stat3.executeQuery();
                    if (rs2.next()) {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setHeaderText(null);
                        error.setTitle("Aviso");
                        error.setContentText("Nombre de cuenta ya usado,\nintroduzca una cuenta de correo diferente.");
                        error.initOwner(botonCrear.getScene().getWindow());
                        error.showAndWait();
                    }
                    else {
                        Thread hilo = new Hilo(con);
                        try {hilo.start();}
                        catch (Exception e) {
                            Alert mensaje2 = new Alert(Alert.AlertType.ERROR);
                            mensaje2.setHeaderText(null);
                            mensaje2.setTitle("Error de base de datos.");
                            mensaje2.setContentText("Se ha producido un error en el sistema.\nPor favor, cierre sesión y vuelva a intentarlo.");
                            mensaje2.initOwner(botonVolver.getScene().getWindow());
                            mensaje2.showAndWait();
                        }
                        hilo.join();

                        ButtonType aceptar = new ButtonType("Sí", ButtonBar.ButtonData.OK_DONE);
                        ButtonType rechazar = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
                        Alert mensaje = new Alert(Alert.AlertType.CONFIRMATION, "Se ha creado su cuenta.\n¿Quiere entrar en ella ahora?", aceptar, rechazar);
                        mensaje.setHeaderText(null);
                        mensaje.setTitle("Cuenta creada con éxito");
                        mensaje.initOwner(botonCrear.getScene().getWindow());
                        mensaje.showAndWait();

                        if (Objects.equals(mensaje.getResult(), aceptar)) {
                            String nombre = textoNombre.getText();
                            String apellidos = textoApellidos.getText();
                            FXMLLoader loader = new FXMLLoader(inicioControl.class.getResource("/clases/principal.fxml"));
                            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                            scene = new Scene(loader.load());
                            principalControl control = loader.getController();
                            control.NombreUsuario(nombre, apellidos);
                            control.setNombre(nombre);
                            control.setApellido(apellidos);
                            control.setCorreo(textoCorreo.getText());
                            control.cambiarInicio();
                            stage.setScene(scene);
                            stage.setTitle("Principal");
                            Ini lector = new Ini(new File("src/main/resources/clases/recursos.ini"));
                            Image icono = new Image(lector.fetch("Image", "logoNormal"));
                            stage.getIcons().add(icono);
                            stage.setResizable(true);
                            stage.show();
                        }
                        else if (Objects.equals(mensaje.getResult(), rechazar)) {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/clases/inicioSesion.fxml"));
                            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                            scene = new Scene(loader.load());
                            stage.setTitle("Inicio de sesión");
                            Ini lector = new Ini(new File("src/main/resources/clases/recursos.ini"));
                            Image icono = new Image(lector.fetch("Image", "logoNormal"));
                            stage.setResizable(false);
                            stage.getIcons().add(icono);
                            stage.setScene(scene);
                            stage.show();
                        }
                    }
                }
            }
            catch (ClassNotFoundException e) {throw new RuntimeException(e);} catch (Exception e) {e.printStackTrace();}
        }
    }

    public class Hilo extends Thread {
        Connection con;
        public Hilo(Connection con) {this.con = con;}
        public synchronized void run() {
            try {
                Class.forName("org.sqlite.JDBC");
                DriverManager.registerDriver(new org.sqlite.JDBC());

                String crearCliente = "insert into Cliente(Nombre, Apellidos, DNI, Teléfono, Ciudad, Direccion, Correo_elec, Fec_nacimiento) values(?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stat2 = con.prepareStatement(crearCliente);
                stat2.setString(1, textoNombre.getText());
                stat2.setString(2, textoApellidos.getText());
                stat2.setString(3, textoDNI.getText());
                stat2.setInt(4, Integer.parseInt(textoTelf.getText()));
                stat2.setString(5, textoCiudad.getText());
                stat2.setString(6, TextoDireccion.getText());
                stat2.setString(7, textoCorreo.getText());
                stat2.setString(8, String.valueOf(textoFecha.getValue()));
                stat2.executeUpdate();

                String crearCuenta = "insert into Cuenta(Id_cliente, IBAN, Saldo, Creación, Últ_acceso, Tipo, Estado, Contrasena, Minutos) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stat4 = con.prepareStatement(crearCuenta);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                stat4.setInt(1, BuscarCliente(textoDNI.getText(), textoCorreo.getText()));
                stat4.setString(2, "ES" + (int)(Math.random()*90+10) + " " + (int)(Math.random()*9000+1000) + " " + (int)(Math.random()*9000+1000) + " " + (int)(Math.random()*9000+1000) + " " + (int)(Math.random()*9000+1000) + " " + (int)(Math.random()*9000+1000));
                stat4.setDouble(3, 0.00);
                stat4.setString(4, dtf.format(LocalDateTime.now()));
                stat4.setString(5, "NADA");
                stat4.setString(6, seleccTipo.getValue());
                stat4.setString(7, "Abierta");
                stat4.setString(8, textoContra.getText());
                stat4.setInt(9, 0);
                stat4.executeUpdate();

                String crearMensaje = "insert into Buzón(Id_Cuenta, Mensaje, Fecha) values((select max(Id_Cuenta) from Cuenta), '¡Bienvenido a BVE! Esperamos que no tenga problemas usando nuestra plataforma.', ?)";
                PreparedStatement stat5 = con.prepareStatement(crearMensaje);
                stat5.setString(1, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()));
                stat5.executeUpdate();

                //con.close();
            }
            catch (ClassNotFoundException e) {throw new RuntimeException(e);} catch (Exception e) {e.printStackTrace();}
        }
    }
}