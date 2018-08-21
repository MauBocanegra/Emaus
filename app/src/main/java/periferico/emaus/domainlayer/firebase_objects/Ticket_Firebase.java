package periferico.emaus.domainlayer.firebase_objects;

public class Ticket_Firebase extends Object_Firebase {
    private String clienteID;
    private String planID;
    private String empleadoID;
    private double lat;
    private double lon;
    private int numAbono;
    private float monto;
    private float nuevoSaldo;
    private long createdAt;
    private String empleado_keydia;

    private String keyDiaCreacion;


    public Ticket_Firebase(){}

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

    public String getEmpleadoID() {
        return empleadoID;
    }

    public void setEmpleadoID(String empleadoID) {
        this.empleadoID = empleadoID;
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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }


    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public int getNumAbono() {
        return numAbono;
    }

    public void setNumAbono(int numAbono) {
        this.numAbono = numAbono;
    }

    public float getNuevoSaldo() {
        return nuevoSaldo;
    }

    public void setNuevoSaldo(float nuevoSaldo) {
        this.nuevoSaldo = nuevoSaldo;
    }

    public String getKeyDiaCreacion() {
        return keyDiaCreacion;
    }

    public void setKeyDiaCreacion(String keyDiaCreacion) {
        this.keyDiaCreacion = keyDiaCreacion;
    }

    public String getEmpleado_keydia() {
        return empleado_keydia;
    }

    public void setEmpleado_keydia(String empleado_keydia) {
        this.empleado_keydia = empleado_keydia;
    }
}
