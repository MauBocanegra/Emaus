package periferico.emaus.domainlayer.firebase_objects;

import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;

public class Ruta_Firebase extends Object_Firebase {
    private double lat;
    private double lon;
    private String clienteID;
    private String planID;
    private int state;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getClienteID() {
        return clienteID;
    }

    public void setClienteID(String clienteID) {
        this.clienteID = clienteID;
    }

    public String getPlanID() {
        return planID;
    }

    public void setPlanID(String planID) {
        this.planID = planID;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
