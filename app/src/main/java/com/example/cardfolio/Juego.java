package com.example.cardfolio;

public class Juego {
    private String nombre;
    private int logoResId;

    public Juego(String nombre, int logoResId) {
        this.nombre = nombre;
        this.logoResId = logoResId;
    }

    public String getNombre() { return nombre; }
    public int getLogoResId() { return logoResId; }
}
