package controladores;

public class prestamo {

    public int Id;
    public Double Cantidad;
    public Double Restante;
    public Double Interés;
    public String Fecha;
    public String Fecha_límite;
    public String Tipo;
    public String Estado;

    public prestamo(int id,Double c, Double r, Double i, String f, String l, String t, String e) {
        this.Id = id;
        this.Cantidad = c;
        this.Restante = r;
        this.Interés = i;
        this.Fecha = f;
        this.Fecha_límite = l;
        this.Tipo = t;
        this.Estado = e;
    }

    public int getId() {return Id;}

    public void setId(int id) {Id = id;}

    public Double getCantidad() {
        return Cantidad;
    }

    public void setCantidad(Double cantidad) {
        Cantidad = cantidad;
    }

    public Double getRestante() {
        return Restante;
    }

    public void setRestante(Double restante) {
        Restante = restante;
    }

    public Double getInterés() {
        return Interés;
    }

    public void setInterés(Double interés) {
        Interés = interés;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getFecha_límite() {
        return Fecha_límite;
    }

    public void setFecha_límite(String limite) {
        Fecha_límite = limite;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }
}
