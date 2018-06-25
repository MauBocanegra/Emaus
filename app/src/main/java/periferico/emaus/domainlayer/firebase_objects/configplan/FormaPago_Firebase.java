package periferico.emaus.domainlayer.firebase_objects.configplan;

import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;

public class FormaPago_Firebase extends Object_Firebase {

    String nombre;
    int formaPagoID;

    public FormaPago_Firebase() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getFormaPagoID() {
        return formaPagoID;
    }

    public void setFormaPagoID(int formaPagoID) {
        this.formaPagoID = formaPagoID;
    }
}
