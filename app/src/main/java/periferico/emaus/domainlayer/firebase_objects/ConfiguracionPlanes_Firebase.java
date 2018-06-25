package periferico.emaus.domainlayer.firebase_objects;

import periferico.emaus.domainlayer.firebase_objects.configplan.DescuentosPlanes_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.FormasPago_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.FrecuenciasPago_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.MatrizPlanes_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.Financiamientos_Firebase;

public class ConfiguracionPlanes_Firebase extends Object_Firebase {

    MatrizPlanes_Firebase planes;
    Financiamientos_Firebase listamensualidades;
    FormasPago_Firebase listaformaspago;
    DescuentosPlanes_Firebase listadescuentos;
    FrecuenciasPago_Firebase listafrecuenciaspago;

    public ConfiguracionPlanes_Firebase() {
    }

    public MatrizPlanes_Firebase getPlanes() {
        return planes;
    }

    public void setPlanes(MatrizPlanes_Firebase planes) {
        this.planes = planes;
    }

    public Financiamientos_Firebase getListamensualidades() {
        return listamensualidades;
    }

    public void setListamensualidades(Financiamientos_Firebase listamensualidades) {
        this.listamensualidades = listamensualidades;
    }

    public FormasPago_Firebase getListaformaspago() {
        return listaformaspago;
    }

    public void setListaformaspago(FormasPago_Firebase listaformaspago) {
        this.listaformaspago = listaformaspago;
    }

    public DescuentosPlanes_Firebase getListadescuentos() {
        return listadescuentos;
    }

    public void setListadescuentos(DescuentosPlanes_Firebase listadescuentos) {
        this.listadescuentos = listadescuentos;
    }

    public FrecuenciasPago_Firebase getListafrecuenciaspago() {
        return listafrecuenciaspago;
    }

    public void setListafrecuenciaspago(FrecuenciasPago_Firebase listafrecuenciaspago) {
        this.listafrecuenciaspago = listafrecuenciaspago;
    }
}
