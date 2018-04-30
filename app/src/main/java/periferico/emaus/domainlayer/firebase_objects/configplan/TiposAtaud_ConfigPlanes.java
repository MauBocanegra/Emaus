package periferico.emaus.domainlayer.firebase_objects.configplan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maubocanegra on 05/04/18.
 */

public class TiposAtaud_ConfigPlanes {

    List<TiposServicio_ConfigPlanes> tiposServicio;
    String nombre;
    int ataudID;

    public TiposAtaud_ConfigPlanes(){}

    public List<TiposServicio_ConfigPlanes> getTiposServicio() { return tiposServicio; }
    public void setTiposServicio(List<TiposServicio_ConfigPlanes> tiposServicio) { this.tiposServicio = tiposServicio; }

    public List<String> getTiposServicioStrings(){
        List<String> tiposServicioStrings  = new ArrayList<>();
        for(TiposServicio_ConfigPlanes tiposServicioItem : tiposServicio){
            tiposServicioStrings.add(tiposServicioItem.getNombre());
        }
        return tiposServicioStrings;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getAtaudID() {
        return ataudID;
    }

    public void setAtaudID(int ataudID) {
        this.ataudID = ataudID;
    }

    public TiposServicio_ConfigPlanes getServicioByID(int servicioID){
        for(TiposServicio_ConfigPlanes servicioItem : tiposServicio){
            if(servicioItem.getServicioID() == servicioID){
                return servicioItem;
            }
        }
        return null;
    }
}
