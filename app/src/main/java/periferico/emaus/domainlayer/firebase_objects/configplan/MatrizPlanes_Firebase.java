package periferico.emaus.domainlayer.firebase_objects.configplan;

import java.util.ArrayList;
import java.util.List;

import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;

/**
 * Created by maubocanegra on 04/04/18.
 * ConfigPlan identifica y define
 */


public class MatrizPlanes_Firebase extends Object_Firebase {

    private List<TiposPlan_ConfigPlanes> tiposPlan;
    private String nombre;

    public List<TiposPlan_ConfigPlanes> getTiposPlan() { return tiposPlan; }
    public void setTiposPlan(List<TiposPlan_ConfigPlanes> tiposPlan) { this.tiposPlan = tiposPlan; }

    public List<String> getTiposPlanStrings(){
        List<String> tiposPlanStrings  = new ArrayList<>();
        for(TiposPlan_ConfigPlanes tiposPlanItem : tiposPlan){
            tiposPlanStrings.add(tiposPlanItem.getNombre());
        }
        return tiposPlanStrings;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public MatrizPlanes_Firebase(){}

    public TiposPlan_ConfigPlanes getPlanByPlanID(int planID){
        for(TiposPlan_ConfigPlanes planItem : tiposPlan){
            if(planItem.planID==planID){
                return planItem;
            }
        }
        return null;
    }
}
