package controladores;
import org.ini4j.Ini;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class prueba {
    public static void main(String[] args) throws IOException {
        /*BufferedReader a = new BufferedReader(new FileReader("src/main/resources/clases/pedirPrestamo.fxml"));
        String b = "";
        while ((b = a.readLine()) != null) {System.out.println(b);}*/
        Ini lector = new Ini(new File("src/main/resources/clases/recursos.ini"));
        lector.load();
        /*Image icono = new Image(lector.fetch("Image", "logoNormal"));
        stage.getIcons().add(icono);*/
    }
}
