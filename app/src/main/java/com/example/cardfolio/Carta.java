package com.example.cardfolio;

public class Carta {
    private String nombre;
    private String rareza;
    private int imagenResId;
    private double valor;

    public Carta(String nombre, String rareza, int imagenResId, double valor) {
        this.nombre = nombre;
        this.rareza = rareza;
        this.imagenResId = imagenResId;
        this.valor = valor;
    }

    public String getNombre() { return nombre; }
    public String getRareza() { return rareza; }
    public int getImagenResId() { return imagenResId; }
    public double getValor() { return valor; }
}
