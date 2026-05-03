package com.example.cardfolio;

public class Carta {
    private String nombre;
    private String rareza;
    private int imagenResId;
    private double valor;
    private String juego;

    public Carta(String nombre, String rareza, int imagenResId, double valor, String juego) {
        this.nombre = nombre;
        this.rareza = rareza;
        this.imagenResId = imagenResId;
        this.valor = valor;
        this.juego = juego;
    }

    public String getNombre() { return nombre; }
    public String getRareza() { return rareza; }
    public int getImagenResId() { return imagenResId; }
    public double getValor() { return valor; }
    public String getJuego() { return juego; }
}
