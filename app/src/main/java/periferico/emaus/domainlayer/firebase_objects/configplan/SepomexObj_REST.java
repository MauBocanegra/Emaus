package periferico.emaus.domainlayer.firebase_objects.configplan;

import org.json.JSONObject;

public class SepomexObj_REST {

    int id;
    String name;
    String code;
    String type;
    String type_id;
    int municipaly_id;
    JSONObject pivot;

    public SepomexObj_REST() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public int getMunicipaly_id() {
        return municipaly_id;
    }

    public void setMunicipaly_id(int municipaly_id) {
        this.municipaly_id = municipaly_id;
    }

    public JSONObject getPivot() {
        return pivot;
    }

    public void setPivot(JSONObject pivot) {
        this.pivot = pivot;
    }
}
