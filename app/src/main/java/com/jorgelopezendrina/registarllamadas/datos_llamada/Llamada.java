package com.jorgelopezendrina.registarllamadas.datos_llamada;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Clase POJO del tipo llamada que contiene los atributos de tipo String, numero, nombre y fecha.
 *
 * @author Jorge López Endrina.
 */
public class Llamada implements Comparable<Llamada>, Serializable {

    private String numero, nombre, fecha;

    public Llamada(String numero, String nombre, String fecha) {
        this.numero = numero;
        this.nombre = nombre;
        this.fecha = fecha;
    }

    public Llamada() {
        this("0", "desconocido", "01-01-2020 00d-00-00");
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public int compareTo(Llamada llamada) {
        int sort = this.nombre.compareTo(llamada.nombre);
        if (sort == 0) {
            sort = fecha.compareTo(llamada.fecha);
        }
        return sort;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Llamada)) return false;
        Llamada llamada = (Llamada) obj;
        return nombre == llamada.nombre && Objects.equals(fecha, llamada.fecha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, nombre, fecha);
    }

    @Override
    public String toString() {
        return fecha + numero +"; "+ nombre+";";
    }
}
