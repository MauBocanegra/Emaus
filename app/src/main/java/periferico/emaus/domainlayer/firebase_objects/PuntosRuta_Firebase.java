package periferico.emaus.domainlayer.firebase_objects;

public class PuntosRuta_Firebase extends Object_Firebase {

    private String calle;
    private String cp;
    private int codigoColonia;
    private String direccion;
    private double lat;
    private double lon;
    private String clienteID;
    private String planID;

    public PuntosRuta_Firebase() {}

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public int getCodigoColonia() {
        return codigoColonia;
    }

    public void setCodigoColonia(int codigoColonia) {
        this.codigoColonia = codigoColonia;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

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
}
