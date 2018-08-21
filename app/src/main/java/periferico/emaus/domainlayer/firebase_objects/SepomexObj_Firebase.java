package periferico.emaus.domainlayer.firebase_objects;

public class SepomexObj_Firebase extends Object_Firebase {

    String id;
    String colonia;
    String cp;

    public SepomexObj_Firebase(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }
}
