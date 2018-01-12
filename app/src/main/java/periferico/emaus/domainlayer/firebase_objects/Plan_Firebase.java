package periferico.emaus.domainlayer.firebase_objects;

/**
 * Created by maubocanegra on 12/12/17.
 */

public class Plan_Firebase extends Object_Firebase {
    private int intPlan;
    private int intAtaud;
    private int intServicio;
    private int intFinanciamiento;
    private int intFrecuenciaPagos;
    private int intFormaPago;
    private int intAnticipo;
    private int intMonto;
    private boolean boolFacturacion;
    private String stRFC;
    private String stEmailFacturacion;
    private String linkINEFrontal;
    private String linkINEReverso;
    private String linkComprobante;

    private String stCliente;

    private String stVendedor;
    private Long createdAt;

    public String toString(){
        return "stID="+stID+" intPlan="+intPlan+" intAtaud="+intAtaud+" intServicio="+intServicio+" intFinanciamiento="+intFinanciamiento
        +" intFrecuenciaPagos="+intFrecuenciaPagos+" intFormaPago="+intFormaPago+" intAnticipo="+intAnticipo
        +" intMonto="+intMonto+" boolFacturacion="+boolFacturacion+" stRFC="+stRFC+" stEmailFacturacion="+stEmailFacturacion
        +" linkINEFrontal="+linkINEFrontal+" linkINEReverso="+linkINEReverso+" linkComprobante="+linkComprobante;
    }

    public int getIntPlan() {
        return intPlan;
    }

    public void setIntPlan(int intPlan) {
        this.intPlan = intPlan;
    }

    public int getIntAtaud() {
        return intAtaud;
    }

    public void setIntAtaud(int intAtaud) {
        this.intAtaud = intAtaud;
    }

    public int getIntServicio() {
        return intServicio;
    }

    public void setIntServicio(int intServicio) {
        this.intServicio = intServicio;
    }

    public int getIntFinanciamiento() {
        return intFinanciamiento;
    }

    public void setIntFinanciamiento(int intFinanciamiento) {
        this.intFinanciamiento = intFinanciamiento;
    }

    public int getIntFrecuenciaPagos() {
        return intFrecuenciaPagos;
    }

    public void setIntFrecuenciaPagos(int intFrecuenciaPagos) {
        this.intFrecuenciaPagos = intFrecuenciaPagos;
    }

    public int getIntMonto() {
        return intMonto;
    }

    public void setIntMonto(int intMonto) {
        this.intMonto = intMonto;
    }

    public int getIntFormaPago() {
        return intFormaPago;
    }

    public void setIntFormaPago(int intFormaPago) {
        this.intFormaPago = intFormaPago;
    }

    public int getIntAnticipo() {
        return intAnticipo;
    }

    public void setIntAnticipo(int intAnticipo) {
        this.intAnticipo = intAnticipo;
    }

    public boolean isBoolFacturacion() {
        return boolFacturacion;
    }

    public void setBoolFacturacion(boolean boolFacturacion) {
        this.boolFacturacion = boolFacturacion;
    }

    public String getStRFC() {
        return stRFC;
    }

    public void setStRFC(String stRFC) {
        this.stRFC = stRFC;
    }

    public String getStEmailFacturacion() {
        return stEmailFacturacion;
    }

    public void setStEmailFacturacion(String stEmailFacturacion) {
        this.stEmailFacturacion = stEmailFacturacion;
    }

    public String getLinkINEFrontal() {
        return linkINEFrontal;
    }

    public void setLinkINEFrontal(String linkINEFrontal) {
        this.linkINEFrontal = linkINEFrontal;
    }

    public String getLinkINEReverso() {
        return linkINEReverso;
    }

    public void setLinkINEReverso(String linkINEReverso) {
        this.linkINEReverso = linkINEReverso;
    }

    public String getLinkComprobante() {
        return linkComprobante;
    }

    public void setLinkComprobante(String linkComprobante) {
        this.linkComprobante = linkComprobante;
    }

    public String getStVendedor() {
        return stVendedor;
    }

    public void setStVendedor(String stVendedor) {
        this.stVendedor = stVendedor;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getStCliente() {
        return stCliente;
    }

    public void setStCliente(String stCliente) {
        this.stCliente = stCliente;
    }
}
