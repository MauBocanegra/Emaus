package periferico.emaus.domainlayer.firebase_objects;

import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;

public class PerfilEmpleado extends Object_Firebase {
    String nombre;
    long lastLogin;

    boolean cobranza;
    boolean ventas;
    String lastLoginString;

    public PerfilEmpleado() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isCobranza() {
        return cobranza;
    }

    public void setCobranza(boolean cobranza) {
        this.cobranza = cobranza;
    }

    public boolean isVentas() {
        return ventas;
    }

    public void setVentas(boolean ventas) {
        this.ventas = ventas;
    }

    public String getLastLoginString() {
        return lastLoginString;
    }

    public void setLastLoginString(String lastLoginString) {
        this.lastLoginString = lastLoginString;
    }
}
