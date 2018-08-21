package periferico.emaus.domainlayer.firebase_objects;

import java.util.List;

import periferico.emaus.domainlayer.objetos.LiquidaCon;

public class ConfigLiquidaCon extends Object_Firebase {
    public ConfigLiquidaCon(){}

    private List<LiquidaCon> tablaliquidacon;

    public List<LiquidaCon> getTablaliquidacon() {
        return tablaliquidacon;
    }

    public void setTablaliquidacon(List<LiquidaCon> tablaliquidacon) {
        this.tablaliquidacon = tablaliquidacon;
    }
}
