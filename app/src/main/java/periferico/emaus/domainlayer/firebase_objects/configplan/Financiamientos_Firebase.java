package periferico.emaus.domainlayer.firebase_objects.configplan;

import java.util.ArrayList;
import java.util.List;

import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;

public class Financiamientos_Firebase extends Object_Firebase {

    List<FinanciamientoOBJ_Firebase> mensualidades;

    public Financiamientos_Firebase() {
    }

    public List<String> getMensualidadesStrings(){
        List<String> mensualidadesStrings  = new ArrayList<>();
        for(FinanciamientoOBJ_Firebase mensualidadItem : mensualidades){
            mensualidadesStrings.add(mensualidadItem.getNombre());
        }
        return mensualidadesStrings;
    }

    public FinanciamientoOBJ_Firebase getFinanciamientoByID(int financID){
        for(FinanciamientoOBJ_Firebase finanObj : mensualidades){
            if (finanObj.getMensualidadID()==financID){
                return finanObj;
            }
        }
        return null;
    }

    public List<FinanciamientoOBJ_Firebase> getMensualidades() {
        return mensualidades;
    }

    public void setMensualidades(List<FinanciamientoOBJ_Firebase> mensualidades) {
        this.mensualidades = mensualidades;
    }
}
