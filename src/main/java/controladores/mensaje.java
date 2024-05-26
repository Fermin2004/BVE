package controladores;

public class mensaje {
    public int Id_cuenta;
    public String Mensaje;
    public String Fecha;

    public mensaje(int id, String men, String fec) {
        this.Id_cuenta = id;
        this.Mensaje = men;
        this.Fecha = fec;
    }

    public int getId_cuenta() {
        return Id_cuenta;
    }

    public void setId_cuenta(int id_cuenta) {
        Id_cuenta = id_cuenta;
    }

    public String getMensaje() {
        return Mensaje;
    }

    public void setMensaje(String mensaje) {
        Mensaje = mensaje;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }
}
