package periferico.emaus.domainlayer.firebase_objects.configplan;

import java.util.ArrayList;
import java.util.List;

import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;

public class FrecuenciasPago_Firebase extends Object_Firebase {

    List<FrecuenciaPago_Firebase> frecuenciaspago;

    public FrecuenciasPago_Firebase() {
    }

    public List<String> getFrecuenciasPagoStrings(){
        List<String> frecuenciasPagoStrings  = new ArrayList<>();
        for(FrecuenciaPago_Firebase frecuenciaPagoItem : frecuenciaspago){
            frecuenciasPagoStrings.add(frecuenciaPagoItem.getNombre());
        }
        return frecuenciasPagoStrings;
    }

    public List<FrecuenciaPago_Firebase> getFrecuenciaspago() {
        return frecuenciaspago;
    }

    public void setFrecuenciaspago(List<FrecuenciaPago_Firebase> frecuenciaspago) {
        this.frecuenciaspago = frecuenciaspago;
    }

    public FrecuenciaPago_Firebase getFrecuenciaByID(int frecuenciaID){
        for(FrecuenciaPago_Firebase frecITem : frecuenciaspago){
            if(frecITem.getFrecuenciaID()==frecuenciaID){
                return frecITem;
            }
        }
        return null;
    }
}
