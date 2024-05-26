package controladores;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;

public class transferencia {
    /*private Integer Id;*/
    private String Estado;
    private double Cantidad;
    private String Fecha;
    private double Costos_extra;
    private String Notas;
    private String Nombre;

    public transferencia(/*Integer id*/double cantidad, String fecha, double costos_extra, String notas, String nombre) {
        /*Id = id;*/
        Cantidad = cantidad;
        Fecha = fecha;
        Costos_extra = costos_extra;
        Notas = notas;
        Nombre = nombre;
    }

    public transferencia(/*Integer id*/double cantidad, String fecha, double costos_extra, String notas) {
        /*Id = id;*/
        Cantidad = cantidad;
        Fecha = fecha;
        Costos_extra = costos_extra;
        Notas = notas;
        //Nombre = nombre;
    }

    /*public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }*/

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    public double getCantidad() {
        return Cantidad;
    }

    public void setCantidad(double cantidad) {
        Cantidad = cantidad;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public double getCostos_extra() {
        return Costos_extra;
    }

    public void setCostos_extra(double costos_extra) {
        Costos_extra = costos_extra;
    }

    public String getNotas() {
        return Notas;
    }

    public void setNotas(String notas) {
        Notas = notas;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }
}