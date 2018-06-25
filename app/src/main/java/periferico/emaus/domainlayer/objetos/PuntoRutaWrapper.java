package periferico.emaus.domainlayer.objetos;

import com.google.android.gms.maps.model.Marker;

import periferico.emaus.domainlayer.firebase_objects.Cliente_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Plan_Firebase;
import periferico.emaus.domainlayer.firebase_objects.PuntosRuta_Firebase;

public class PuntoRutaWrapper {

    private Marker marker;
    private Plan_Firebase plan;
    private Cliente_Firebase cliente;

    public PuntoRutaWrapper(){}

    public PuntoRutaWrapper(Marker m_, Plan_Firebase plan_){marker=m_; plan=plan_;}

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Plan_Firebase getPlan() {
        return plan;
    }

    public void setPlan(Plan_Firebase plan) {
        this.plan = plan;
    }

    public Cliente_Firebase getCliente() {
        return cliente;
    }

    public void setCliente(Cliente_Firebase cliente) {
        this.cliente = cliente;
    }
}
