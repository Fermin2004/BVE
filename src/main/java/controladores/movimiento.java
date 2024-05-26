package controladores;

public class movimiento {
    private String Destino;
    private Double Cantidad;
    private String Fecha;
    private String Nota;

    public movimiento(String d, Double c, String f, String n){
        this.Destino = d;
        this.Cantidad = c;
        this.Fecha = f;
        this.Nota = n;
    }

    public String getDestino() {
        return Destino;
    }

    public void setDestino(String destino) {
        Destino = destino;
    }

    public Double getCantidad() {
        return Cantidad;
    }

    public void setCantidad(Double cantidad) {
        Cantidad = cantidad;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getNota() {
        return Nota;
    }

    public void setNota(String notas) {
        Nota = notas;
    }

}
