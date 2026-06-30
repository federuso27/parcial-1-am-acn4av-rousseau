package com.example.cardfolio;

public class Carta implements java.io.Serializable {
    private String nombre;
    private String rareza;
    private int imagenResId;
    private double valor;
    private String juego;
    private String imageUrl;

    public Carta(String nombre, String rareza, int imagenResId, double valor, String juego) {
        this(nombre, rareza, imagenResId, valor, juego, null);
    }

    public Carta(String nombre, String rareza, int imagenResId, double valor, String juego, String imageUrl) {
        this.nombre = nombre;
        this.rareza = rareza;
        this.imagenResId = imagenResId;
        this.valor = valor;
        this.juego = juego;
        this.imageUrl = imageUrl;
    }

    public String getNombre() { return nombre; }
    public String getRareza() { return rareza; }
    public int getImagenResId() { return imagenResId; }
    public double getValor() { return valor; }
    public String getJuego() { return juego; }
    public String getImageUrl() { return imageUrl; }
}
