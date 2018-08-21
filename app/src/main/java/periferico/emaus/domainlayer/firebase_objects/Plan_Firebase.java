package periferico.emaus.domainlayer.firebase_objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import periferico.emaus.domainlayer.objetos.Direcciones;

public class Plan_Firebase extends Object_Firebase {

    public static final int FREC_SEMANAL = 1;
    public static final int FREC_QUINCENAL = 2;
    public static final int FREC_MENSUAL= 3;

    private int planID;
    private int ataudID;
    private int servicioID;
    private int financiamientoID;
    private int formaPagoID;
    private int frecuenciaPagoID;

    private int frecuenciaVisitas;
    private int diaVisitas;

    private int status;

    private int creadoOffline;

    private int descuentoID;
    private String comprobanteDescuentoURL;

    private boolean boolFacturacion;
    private String stRFC;
    private String stEmailFacturacion;

    private float anticipo;
    private float totalAPagar;
    private float saldo;

    private int diaDeCobro;
    private int numeroDePagosARealizar;
    private int pagosRealizados;

    private String comprobanteINEFrontalURL;
    private String comprobanteINEReversoURL;
    private String comprobanteDomicilioURL;

    private long createdAt;
    private String stVendedor;
    private String stCliente;

    private Map<String,Object> tickets;
    //private List<String> tickets;
    private String ultimoTicketRealizado;
    private long fechaUltimoPago;
    private List<Direcciones> direcciones;

    public String toString(){
        return "planID="+planID+" ataudID="+ataudID+" servicioID"+servicioID+" financiamientoID="+ financiamientoID
                +" formaPagoID="+formaPagoID+" creadoOffline="+creadoOffline+" descuentoID="+descuentoID
                +" comprobanteDescuentoURL="+comprobanteDescuentoURL+" boolFacturacion="+boolFacturacion
                +" stRFC="+stRFC+" stEmailFacturacion="+stEmailFacturacion
                +" anticipo="+anticipo+" totalAPagar=";
    }


    public Plan_Firebase() {
    }

    // --------------------------------------------------- //
    // ---------------- GETTERS & SETTERS ---------------- //
    //---------------------------------------------------- //

    public int getPlanID() {
        return planID;
    }

    public void setPlanID(int planID) {
        this.planID = planID;
    }

    public int getAtaudID() {
        return ataudID;
    }

    public void setAtaudID(int ataudID) {
        this.ataudID = ataudID;
    }

    public int getServicioID() {
        return servicioID;
    }

    public void setServicioID(int servicioID) {
        this.servicioID = servicioID;
    }

    public int getFinanciamientoID() {
        return financiamientoID;
    }

    public void setFinanciamientoID(int financiamientoID) {
        this.financiamientoID = financiamientoID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFormaPagoID() {
        return formaPagoID;
    }

    public void setFormaPagoID(int formaPagoID) {
        this.formaPagoID = formaPagoID;
    }

    public int getFrecuenciaPagoID() {
        return frecuenciaPagoID;
    }

    public void setFrecuenciaPagoID(int frecuenciaPagoID) {
        this.frecuenciaPagoID = frecuenciaPagoID;
    }

    public int getCreadoOffline() {
        return creadoOffline;
    }

    public void setCreadoOffline(int creadoOffline) {
        this.creadoOffline = creadoOffline;
    }

    public int getDescuentoID() {
        return descuentoID;
    }

    public void setDescuentoID(int descuentoID) {
        this.descuentoID = descuentoID;
    }

    public String getComprobanteDescuentoURL() {
        return comprobanteDescuentoURL;
    }

    public void setComprobanteDescuentoURL(String comprobanteDescuentoURL) {
        this.comprobanteDescuentoURL = comprobanteDescuentoURL;
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

    public int getFrecuenciaVisitas() {
        return frecuenciaVisitas;
    }

    public void setFrecuenciaVisitas(int frecuenciaVisitas) {
        this.frecuenciaVisitas = frecuenciaVisitas;
    }

    public int getDiaVisitas() {
        return diaVisitas;
    }

    public void setDiaVisitas(int diaVisitas) {
        this.diaVisitas = diaVisitas;
    }

    /*
    public int getMensualidadesID() {
        return mensualidadesID;
    }

    public void setMensualidadesID(int mensualidadesID) {
        this.mensualidadesID = mensualidadesID;
    }

    public int getNumMensualidades() {
        return numMensualidades;
    }

    public void setNumMensualidades(int numMensualidades) {
        this.numMensualidades = numMensualidades;
    }
    */

    public float getAnticipo() {
        return anticipo;
    }

    public void setAnticipo(float anticipo) {
        this.anticipo = anticipo;
    }

    public String getComprobanteINEFrontalURL() {
        return comprobanteINEFrontalURL;
    }

    public void setComprobanteINEFrontalURL(String comprobanteINEFrontalURL) {
        this.comprobanteINEFrontalURL = comprobanteINEFrontalURL;
    }

    public String getComprobanteINEReversoURL() {
        return comprobanteINEReversoURL;
    }

    public void setComprobanteINEReversoURL(String comprobanteINEReversoURL) {
        this.comprobanteINEReversoURL = comprobanteINEReversoURL;
    }

    public String getComprobanteDomicilioURL() {
        return comprobanteDomicilioURL;
    }

    public void setComprobanteDomicilioURL(String comprobanteDomicilioURL) {
        this.comprobanteDomicilioURL = comprobanteDomicilioURL;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getStVendedor() {
        return stVendedor;
    }

    public void setStVendedor(String stVendedor) {
        this.stVendedor = stVendedor;
    }

    public String getStCliente() {
        return stCliente;
    }

    public void setStCliente(String stCliente) {
        this.stCliente = stCliente;
    }

    public float getTotalAPagar() {
        return totalAPagar;
    }

    public void setTotalAPagar(float totalAPagar) {
        this.totalAPagar = totalAPagar;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    /*
    public float getCantidadPorPago() {
        return cantidadPorPago;
    }

    public void setCantidadPorPago(float cantidadPorPago) {
        this.cantidadPorPago = cantidadPorPago;
    }
    */

    public int getNumeroDePagosARealizar() {
        return numeroDePagosARealizar;
    }

    public void setNumeroDePagosARealizar(int numeroDePagosARealizar) {
        this.numeroDePagosARealizar = numeroDePagosARealizar;
    }

    public int getPagosRealizados() {
        return pagosRealizados;
    }

    public void setPagosRealizados(int pagosRealizados) {
        this.pagosRealizados = pagosRealizados;
    }

    public int getDiaDeCobro() {
        return diaDeCobro;
    }

    public void setDiaDeCobro(int diaDeCobro) {
        this.diaDeCobro = diaDeCobro;
    }

    public Map<String, Object> getTickets() {
        return tickets;
    }

    public void setTickets(Map<String, Object> tickets) {
        this.tickets = tickets;
    }

    public String getUltimoTicketRealizado() {
        return ultimoTicketRealizado;
    }

    public void setUltimoTicketRealizado(String ultimoTicketRealizado) {
        this.ultimoTicketRealizado = ultimoTicketRealizado;
    }

    public List<Direcciones> getDirecciones() {
        return direcciones;
    }

    public void setDirecciones(List<Direcciones> direcciones) {
        this.direcciones = direcciones;
    }

    public long getFechaUltimoPago() {
        return fechaUltimoPago;
    }

    public void setFechaUltimoPago(long fechaUltimoPago) {
        this.fechaUltimoPago = fechaUltimoPago;
    }
}
