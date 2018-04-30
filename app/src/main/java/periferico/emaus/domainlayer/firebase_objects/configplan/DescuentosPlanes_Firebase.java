package periferico.emaus.domainlayer.firebase_objects.configplan;

import java.util.ArrayList;
import java.util.List;

import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;

public class DescuentosPlanes_Firebase extends Object_Firebase {

    List<DescuentoPlan_Firebase> descuentos;

    public DescuentosPlanes_Firebase() {
    }

    public List<String> getDescuentosPlanesStrings(){
        List<String> descuentosPlanesStrings  = new ArrayList<>();
        for(DescuentoPlan_Firebase descuentoPlanItem : descuentos){
            descuentosPlanesStrings.add(descuentoPlanItem.getNombre());
        }
        return descuentosPlanesStrings;
    }

    public List<DescuentoPlan_Firebase> getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(List<DescuentoPlan_Firebase> descuentos) {
        this.descuentos = descuentos;
    }
}
