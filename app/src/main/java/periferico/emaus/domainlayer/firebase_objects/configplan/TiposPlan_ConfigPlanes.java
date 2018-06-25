package periferico.emaus.domainlayer.firebase_objects.configplan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maubocanegra on 05/04/18.
 */

public class TiposPlan_ConfigPlanes {

    List<TiposAtaud_ConfigPlanes> tiposAtaud;
    String nombre;
    int planID;

    public TiposPlan_ConfigPlanes(){}

    public List<TiposAtaud_ConfigPlanes> getTiposAtaud() { return tiposAtaud; }
    public void setTiposAtaud(List<TiposAtaud_ConfigPlanes> tiposAtaud) { this.tiposAtaud = tiposAtaud; }

    public List<String> getTiposAtaudStrings(){
        List<String> tiposAtaudStrings  = new ArrayList<>();
        for(TiposAtaud_ConfigPlanes tiposAtaudItem : tiposAtaud){
            tiposAtaudStrings.add(tiposAtaudItem.getNombre());
        }
        return tiposAtaudStrings;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPlanID() {
        return planID;
    }

    public void setPlanID(int planD) {
        this.planID = planD;
    }

    public TiposAtaud_ConfigPlanes getAtaudByID(int ataudID){
        for(TiposAtaud_ConfigPlanes ataudItem : tiposAtaud){
            if(ataudItem.getAtaudID()==ataudID){
                return ataudItem;
            }
        }
        return null;
    }
}
