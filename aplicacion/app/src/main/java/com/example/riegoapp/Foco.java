package com.example.riegoapp;

public class Foco {
    private String estado;
    private int tiempo;

    public Foco(){}

    public Foco(String estado, int tiempo) {
        this.estado = estado;
        this.tiempo = tiempo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }
}
