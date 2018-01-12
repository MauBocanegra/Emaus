package periferico.emaus.domainlayer.firebase_objects;

/**
 * Created by maubocanegra on 10/12/17.
 */

public class CatalogItem_Firebase extends Object_Firebase{

    private String stNombre;
    private String link;

    public String getStNombre() {
        return stNombre;
    }

    public void setStNombre(String stNombre) {
        this.stNombre = stNombre;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String toString(){
        return "stID="+stID+" stNombre="+stNombre+" link="+link;
    }
}
