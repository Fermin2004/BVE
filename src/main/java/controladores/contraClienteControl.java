package controladores;

import com.sun.mail.util.PropUtil;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.*;
import java.util.Objects;
import java.util.Properties;

public class contraClienteControl {

    public Button botonAceptar;
    public TextField textoDNI;
    public TextField textoCorreo;
    public Button botonVolver;

    public void MandarContra() {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:banco.db")) {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());

            if (textoCorreo.getText().isEmpty() || Objects.equals(textoCorreo.getText(), "") || !textoCorreo.getText().matches("^[^ @]{1,30}@[^ @]{1,10}\\.[^ @]+$")) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error");
                error.setContentText("No ha introducido un correo,\no no lo ha introducido correctamente");
                error.initOwner(botonAceptar.getScene().getWindow());
                error.showAndWait();
            }
            else if (textoDNI.getText() == null || textoDNI.getText().trim().isEmpty() || !textoDNI.getText().matches("^[0-9]{8}[a-zA-Z]$")) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText(null);
                error.setTitle("Error");
                error.setContentText("No ha introducido un DNI,\no no lo ha introducido correctamente");
                error.initOwner(botonAceptar.getScene().getWindow());
                error.showAndWait();
            }
            else {
                String buscarContra = "select Contrasena from Cuenta cue inner join Cliente cli on cli.Id_cliente = cue.Id_Cliente where cli.Correo_elec = ? and cli.DNI = ?";
                PreparedStatement stat = con.prepareStatement(buscarContra);
                stat.setString(1, textoCorreo.getText());
                stat.setString(2, textoDNI.getText());
                ResultSet rs = stat.executeQuery();

                if (!rs.next()) {
                    Alert error = new Alert(Alert.AlertType.INFORMATION);
                    error.setHeaderText(null);
                    error.setTitle("Información");
                    error.setContentText("No se han encontrado datos con esas credenciales.");
                    error.initOwner(botonAceptar.getScene().getWindow());
                    error.showAndWait();
                }
                else {
                    Task<Void> t = new Task<Void>() {
                        @Override
                        public Void call() throws Exception {
                            try {
                                Properties props = new Properties();
                                props.put("mail.smtp.host", "smtp.gmail.com");
                                props.put("mail.smtp.port", 25);
                                props.put("mail.smtp.auth", true);
                                props.put("mail.smtp.starttls.enable", "true");

                                Session ses = Session.getDefaultInstance(props, null);

                                Message correo = new MimeMessage(ses);
                                correo.setFrom(new InternetAddress("cuentasbve@gmail.com"));
                                correo.setRecipients(Message.RecipientType.TO, InternetAddress.parse(textoCorreo.getText()));
                                correo.setSubject("Contraseña BVE");
                                correo.setText("Buenas, su contraseña perdida es " + rs.getString("Contrasena"));

                                //fkzh fyzk lpac lczn
                                Transport transport = ses.getTransport("smtp");
                                transport.connect("smtp.gmail.com", "cuentasbve@gmail.com", "fkzh fyzk lpac lczn");
                                transport.sendMessage(correo, correo.getAllRecipients());
                                transport.close();
                            }
                            catch (MessagingException e) {
                                Alert mensaje2 = new Alert(Alert.AlertType.ERROR);
                                mensaje2.setHeaderText(null);
                                mensaje2.setTitle("Error de mensaje");
                                mensaje2.setContentText("Se ha producido un error al mandar el mensaje");
                                mensaje2.initOwner(botonVolver.getScene().getWindow());
                                mensaje2.showAndWait();
                            }
                            return null;
                        }
                    };
                    new Thread(t).start();

                    Alert mensaje2 = new Alert(Alert.AlertType.INFORMATION);
                    mensaje2.setHeaderText(null);
                    mensaje2.setTitle("Procesando...");
                    mensaje2.setContentText("Se está enviando su contraseña.");
                    mensaje2.initOwner(botonVolver.getScene().getWindow());
                    mensaje2.showAndWait();

                    t.setOnSucceeded(e -> {
                        mensaje2.close();
                        Alert mensaje3 = new Alert(Alert.AlertType.INFORMATION);
                        mensaje3.setHeaderText(null);
                        mensaje3.setTitle("Enviado");
                        mensaje3.setContentText("Se ha enviado su contraseña,\nrevise su correo.");
                        mensaje3.initOwner(botonVolver.getScene().getWindow());
                        mensaje3.showAndWait();
                        Stage stage = (Stage) botonVolver.getScene().getWindow();
                        stage.close();
                    });

                }
            }
        }
        catch (SQLException | ClassNotFoundException e) {e.printStackTrace();}
    }

    public void Volver() {
        Stage stage = (Stage) botonVolver.getScene().getWindow();
        stage.close();
    }

    public class Hilo extends Thread {
        String contra;
        public Hilo(String con) {this.contra = con;}
        public void run() {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", 25);
                props.put("mail.smtp.auth", true);
                props.put("mail.smtp.starttls.enable", "true");

                Session ses = Session.getDefaultInstance(props, null);

                Message correo = new MimeMessage(ses);
                correo.setFrom(new InternetAddress("cuentasbve@gmail.com"));
                correo.setRecipients(Message.RecipientType.TO, InternetAddress.parse(textoCorreo.getText()));
                correo.setSubject("Contraseña BVE");
                correo.setText("Buenas, su contraseña perdida es " + contra);

                //fkzh fyzk lpac lczn
                Transport transport = ses.getTransport("smtp");
                transport.connect("smtp.gmail.com", "cuentasbve@gmail.com", "fkzh fyzk lpac lczn");
                transport.sendMessage(correo, correo.getAllRecipients());
                transport.close();
            }
            catch (MessagingException e) {throw new RuntimeException(e);}
        }
    }
}
