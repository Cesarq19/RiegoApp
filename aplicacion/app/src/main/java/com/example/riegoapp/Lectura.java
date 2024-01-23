package com.example.riegoapp;

public class Lectura {
    private float temperatura;
    private float humedad;

    public Lectura(){}

    public Lectura(float temperatura, float humedad) {
        this.temperatura = temperatura;
        this.humedad = humedad;
    }

    public float getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(float temperatura) {
        this.temperatura = temperatura;
    }

    public float getHumedad() {
        return humedad;
    }

    public void setHumedad(float humedad) {
        this.humedad = humedad;
    }
}
