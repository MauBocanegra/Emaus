package periferico.emaus.domainlayer.firebase_objects.configplan;

import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;

public class FinanciamientoOBJ_Firebase {

    private String nombre;
    private int mensualidadID;
    private int anios;
    private int mensualidades;
    private float multiplicadorFinanciamiento;

    public FinanciamientoOBJ_Firebase() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getMensualidadID() {
        return mensualidadID;
    }

    public void setMensualidadID(int mensualidadID) {
        this.mensualidadID = mensualidadID;
    }

    public int getMensualidades() {
        return mensualidades;
    }

    public void setMensualidades(int mensualidades) {
        this.mensualidades = mensualidades;
    }

    public float getMultiplicadorFinanciamiento() {
        return multiplicadorFinanciamiento;
    }

    public void setMultiplicadorFinanciamiento(float multiplicadorFinanciamiento) {
        this.multiplicadorFinanciamiento = multiplicadorFinanciamiento;
    }

    public int getAnios() {
        return anios;
    }

    public void setAnios(int anios) {
        this.anios = anios;
    }
}
