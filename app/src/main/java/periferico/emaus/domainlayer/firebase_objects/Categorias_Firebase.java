package periferico.emaus.domainlayer.firebase_objects;

import java.util.List;
import java.util.Map;

public class Categorias_Firebase extends Object_Firebase {

    List<Map<String,Object>> estatusVisita;

    public Categorias_Firebase() {
    }

    public List<Map<String, Object>> getEstatusVisita() {
        return estatusVisita;
    }

    public void setEstatusVisita(List<Map<String, Object>> estatusVisita) {
        this.estatusVisita = estatusVisita;
    }
}
