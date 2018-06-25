package periferico.emaus.domainlayer.firebase_objects.configplan;

import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;

public class DescuentoPlan_Firebase extends Object_Firebase {

    int descuento;
    int descuentoID;
    String nombre;

    public DescuentoPlan_Firebase() {
    }


    public int getDescuento() {
        return descuento;
    }

    public void setDescuento(int descuento) {
        this.descuento = descuento;
    }

    public int getDescuentoID() {
        return descuentoID;
    }

    public void setDescuentoID(int descuentoID) {
        this.descuentoID = descuentoID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
