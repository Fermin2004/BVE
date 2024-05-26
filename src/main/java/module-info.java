module com.example.bve {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.xerial.sqlitejdbc;
    requires java.mail;
    requires ini4j;

    /*opens com.example.bve to javafx.fxml;
    exports com.example.bve;*/

    opens clases to javafx.fxml;
    exports clases;

    opens controladores to javafx.fxml;
    exports controladores;
}