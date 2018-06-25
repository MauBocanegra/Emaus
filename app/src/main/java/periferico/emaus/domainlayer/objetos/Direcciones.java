package periferico.emaus.domainlayer.objetos;

import android.view.View;

import com.kbeanie.multipicker.api.entity.ChosenImage;

import java.util.HashMap;

/**
 * Created by maubocanegra on 18/12/17.
 */

public class Direcciones {

    String stCalleNum;
    String stNumInt;
    String stCP;
    String stColonia;
    String linkFachada;
    double lat;
    double lon;

    public Direcciones(){ }

    public String getStCalleNum() {
        return stCalleNum;
    }

    public void setStCalleNum(String stCalleNum) {
        this.stCalleNum = stCalleNum;
    }

    public String getStNumInt() {
        return stNumInt;
    }

    public void setStNumInt(String stNumInt) {
        this.stNumInt = stNumInt;
    }

    public String getStCP() {
        return stCP;
    }

    public void setStCP(String stCP) {
        this.stCP = stCP;
    }

    public String getStColonia() {
        return stColonia;
    }

    public void setStColonia(String stColonia) {
        this.stColonia = stColonia;
    }

    public String getLinkFachada() {
        return linkFachada;
    }

    public void setLinkFachada(String linkFachada) {
        this.linkFachada = linkFachada;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    /*
    HashMap<String,Object> dir;

    public Direcciones(){
        dir = new HashMap<String, Object>();
    }

    public HashMap<String, Object> getDir() {
        return dir;
    }

    public void updateStCalle(String calle){
        dir.put("stCalleNum",calle);
    }

    public void updateStInt(String numInt){
        dir.put("stNumInt",numInt);
    }

    public void updateIntCP (int cp){
        dir.put("intCP",cp);
    }

    public void updateColonia(String colonia){
        dir.put("stColonia",colonia);
    }

    public void updateFachadaURL(String fachadaURL) {dir.put("linkFachada",fachadaURL);}

    public String getFachadaURL(){return (String)dir.get("linkFachada");}
    */

}
