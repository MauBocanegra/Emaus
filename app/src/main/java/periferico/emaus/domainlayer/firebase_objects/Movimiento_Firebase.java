package periferico.emaus.domainlayer.firebase_objects;

public class Movimiento_Firebase extends Object_Firebase {

    public Movimiento_Firebase(){}

    public final static int movimientoTicket=0;
    public final static int movimientoDeposito=1;
    public final static int movimientoRetiro=2;

    private String tipoMovimiento;
    private int tipoMovimientoID;
    private long createdAt;
    private String empleadoID;
    private String fecha;
    private String empleado_fecha;
    private String ticketID;
    private float movimiento;
    private String descripcionMovimiento;

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public int getTipoMovimientoID() {
        return tipoMovimientoID;
    }

    public void setTipoMovimientoID(int tipoMovimientoID) {
        this.tipoMovimientoID = tipoMovimientoID;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmpleadoID() {
        return empleadoID;
    }

    public void setEmpleadoID(String empleadoID) {
        this.empleadoID = empleadoID;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEmpleado_fecha() {
        return empleado_fecha;
    }

    public void setEmpleado_fecha(String empleado_fecha) {
        this.empleado_fecha = empleado_fecha;
    }

    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public float getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(float movimiento) {
        this.movimiento = movimiento;
    }

    public String getDescripcionMovimiento() {
        return descripcionMovimiento;
    }

    public void setDescripcionMovimiento(String descripcionMovimiento) {
        this.descripcionMovimiento = descripcionMovimiento;
    }
}
