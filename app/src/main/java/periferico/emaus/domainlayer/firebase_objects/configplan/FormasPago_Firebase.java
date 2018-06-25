package periferico.emaus.domainlayer.firebase_objects.configplan;

import java.util.ArrayList;
import java.util.List;

import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;

public class FormasPago_Firebase extends Object_Firebase {

    List<FormaPago_Firebase> formaspago;

    public FormasPago_Firebase() {
    }

    public List<String> getFormasPagoStrings(){
        List<String> formasPagoStrings  = new ArrayList<>();
        for(FormaPago_Firebase formaPagoItem : formaspago){
            formasPagoStrings.add(formaPagoItem.getNombre());
        }
        return formasPagoStrings;
    }

    public List<FormaPago_Firebase> getFormaspago() {
        return formaspago;
    }

    public void setFormaspago(List<FormaPago_Firebase> formaspago) {
        this.formaspago = formaspago;
    }

    public FormaPago_Firebase getFormaPagoByID(int formaID){
        for(FormaPago_Firebase formaITem : formaspago){
            if(formaITem.getFormaPagoID()==formaID){
                return formaITem;
            }
        }
        return null;
    }
}
