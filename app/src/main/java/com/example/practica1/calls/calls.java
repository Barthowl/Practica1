package com.example.practica1.calls;

import java.util.Objects;

public class calls {
    String nombre,numero;
    int year, mes, dias, horas, minutos, segundos;

    public calls(){}

    // llamadas
    public calls(String nombre, int year, int mes, int dias, int horas, int minutos, int segundos, String numero) {
        this.nombre = nombre;
        this.year = year;
        this.mes = mes;
        this.dias = dias;
        this.horas = horas;
        this.minutos = minutos;
        this.segundos = segundos;
        this.numero = numero;
    }

    // historial
    public calls(int year, int mes, int dias, int horas, int minutos, int segundos,String nombre,String numero) {
        this.nombre = nombre;
        this.year = year;
        this.mes = mes;
        this.dias = dias;
        this.horas = horas;
        this.minutos = minutos;
        this.segundos = segundos;
        this.numero = numero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public int getSegundos() {
        return segundos;
    }

    public void setSegundos(int segundos) {
        this.segundos = segundos;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof calls)) return false;
        calls calls = (calls) o;
        return year == calls.year &&
                mes == calls.mes &&
                dias == calls.dias &&
                horas == calls.horas &&
                minutos == calls.minutos &&
                segundos == calls.segundos &&
                numero == calls.numero &&
                nombre.equals(calls.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, year, mes, dias, horas, minutos, segundos, numero);
    }

    @Override
    public String toString() {
        return "calls{" +
                "nombre='" + nombre + '\'' +
                ", year=" + year +
                ", mes=" + mes +
                ", dias=" + dias +
                ", horas=" + horas +
                ", minutos=" + minutos +
                ", segundos=" + segundos +
                ", numero=" + numero +
                '}';
    }

    public String toCsvC() {
        return nombre + "; " + year + "; " + mes + "; "  + dias + "; " + horas + "; " + minutos + "; " + segundos + "; " + numero;
    }

    public String toCsvH() {
        return  year + "; " + mes + "; "  + dias + "; " + horas + "; " + minutos + "; " + segundos + "; " + numero + "; " +  nombre;
    }

    public static calls fromCsvString (String csv, String separator) {
        calls c = null;
        String[] partes = csv.split(separator);
        if(partes.length == 8) {
            c = new calls(partes[0].trim(), Integer.parseInt(partes[1].trim()), Integer.parseInt(partes[2].trim()),
                    Integer.parseInt(partes[3].trim()),Integer.parseInt(partes[4].trim()),Integer.parseInt(partes[5].trim())
            , Integer.parseInt(partes[6].trim()), partes[7].trim());
        }
        return c;
    }

    public static calls fromCsvString2 (String csv, String separator) {
        calls c = null;
        String[] partes = csv.split(separator);
        if(partes.length == 8) {
            c = new calls(Integer.parseInt(partes[0].trim()), Integer.parseInt(partes[1].trim()), Integer.parseInt(partes[2].trim()),
                    Integer.parseInt(partes[3].trim()),Integer.parseInt(partes[4].trim()),Integer.parseInt(partes[5].trim())
                    , partes[6].trim(), partes[7].trim());
        }
        return c;
    }
}
