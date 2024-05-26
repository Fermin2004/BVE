package controladores;

import javafx.scene.control.Button;

public class tarjeta {

    private Long PAN;
    private String Tipo;
    private int PIN;
    private String Valido_hasta;
    private String Estado;
    private String Formato;
    private Button BotonDatos;
    private Button BotonEliminar;
    private String alias;

    public tarjeta(Long PAN, String tipo, String alias) {
        this.PAN = PAN;
        this.Tipo = tipo;
        this.alias = alias;
        this.BotonDatos = new Button("Ver");
        this.BotonEliminar = new Button("Eliminar");
    }

    public Long getPAN() {
        return PAN;
    }

    public void setPAN(Long PAN) {
        this.PAN = PAN;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public int getPIN() {
        return PIN;
    }

    public void setPIN(int PIN) {
        this.PIN = PIN;
    }

    public String getValido_hasta() {
        return Valido_hasta;
    }

    public void setValido_hasta(String valido_hasta) {
        Valido_hasta = valido_hasta;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    public String getFormato() {
        return Formato;
    }

    public void setFormato(String formato) {
        Formato = formato;
    }

    public Button getBotonDatos() {
        return BotonDatos;
    }

    public void setBotonDatos(Button botonDatos) {
        BotonDatos = botonDatos;
    }

    public Button getBotonEliminar() {
        return BotonEliminar;
    }

    public void setBotonEliminar(Button botonEliminar) {
        BotonEliminar = botonEliminar;
    }

    public String getAlias() {return alias;}

    public void setAlias(String alias) {this.alias = alias;}
}
