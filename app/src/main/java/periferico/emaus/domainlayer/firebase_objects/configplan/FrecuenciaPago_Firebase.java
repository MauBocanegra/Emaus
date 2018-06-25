package periferico.emaus.domainlayer.firebase_objects.configplan;

import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;

public class FrecuenciaPago_Firebase extends Object_Firebase {

    String nombre;
    int frecuenciaID;
    int pagosAlAnio;

    public FrecuenciaPago_Firebase() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getFrecuenciaID() {
        return frecuenciaID;
    }

    public void setFrecuenciaID(int formapagoID) {
        this.frecuenciaID = formapagoID;
    }

    public int getPagosAlAnio() {
        return pagosAlAnio;
    }

    public void setPagosAlAnio(int pagosAlAnio) {
        this.pagosAlAnio = pagosAlAnio;
    }
}
