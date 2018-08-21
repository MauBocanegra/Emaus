package periferico.emaus.domainlayer.objetos;

import android.view.View;

import com.kbeanie.multipicker.api.entity.ChosenImage;

/**
 * Created by maubocanegra on 23/12/17.
 */

public class DireccionesHelper {

    String stCalleNum;
    String stNumInt;
    String stCP;
    String stColonia;
    String linkFachada;
    String idColonia;
    double lat; double lon;

    public ChosenImage chosenImage;
    public int imagePosition;
    public View fullViewFachada;
    public boolean isLinkSet;
    public String imgName;
    public boolean hasImgSet;

    public DireccionesHelper(){ isLinkSet=false; }

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

    public String getIdColonia() {
        return idColonia;
    }

    public void setIdColonia(String idColonia) {
        this.idColonia = idColonia;
    }
}
