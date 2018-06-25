package periferico.emaus.domainlayer.firebase_objects.configplan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maubocanegra on 05/04/18.
 */

public class TiposServicio_ConfigPlanes {

    //List<TiposPago_ConfigPlanes> tiposPago;
    private String nombre;
    float costo;
    private int servicioID;

    public TiposServicio_ConfigPlanes(){}

    /*
    public List<TiposPago_ConfigPlanes> getTiposPago() { return tiposPago; }
    public void setTiposPago(List<TiposPago_ConfigPlanes> tiposPago) { this.tiposPago = tiposPago; }
    */

    /*
    public List<String> getTiposPagoStrings(){
        List<String> tiposPagoStrings  = new ArrayList<>();
        for(TiposPago_ConfigPlanes tiposPagoItem : tiposPago){
            tiposPagoStrings.add(tiposPagoItem.getNombre());
        }
        return tiposPagoStrings;
    }

    public float[] getTiposPagoCostos(){
        float[] costos = new float[tiposPago.size()];
        for(int i=0 ; i<tiposPago.size(); i++){
            costos[i]=tiposPago.get(i).costo;
        }
        return costos;
    }
    */

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getServicioID() {
        return servicioID;
    }

    public void setServicioID(int servicioID) {
        this.servicioID = servicioID;
    }

    public float getCosto() {
        return costo;
    }

    public void setCosto(float costo) {
        this.costo = costo;
    }

    /*
    public TiposPago_ConfigPlanes getTipoPagoByID(int financiamientoID){
        for(TiposPago_ConfigPlanes itemPago : tiposPago){
            if(itemPago.getPagoID() == financiamientoID){
                return itemPago;
            }
        }
        return null;
    }
    */
}
