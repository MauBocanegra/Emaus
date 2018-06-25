package periferico.emaus.domainlayer.firebase_objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import periferico.emaus.domainlayer.objetos.Direcciones;
import periferico.emaus.domainlayer.objetos.Telefonos;

/**
 * Created by maubocanegra on 26/11/17.
 */

public class Cliente_Firebase extends Object_Firebase{

    public static final int STATUS_PROSPECTO = 0;
    public static final int STATUS_ENVERIFICACION = 1;
    public static final int STATUS_ACTIVO = 2;

    public static final int TIPOUSUARIO_CLIENTE = 1;
    public static final int TIPOUSUARIO_DIRECTORIO = 2;

    public static final String keyID = "cliente_ID";
    public static final String keyNombre = "cliente_stNombre";
    public static final String keyApellido = "cliente_stApellido";
    public static final String keyEmail = "cliente_stEmail";
    public static final String keyReligion = "cliente_intReligion";
    public static final String keyGenero = "cliente_intGenero";
    public static final String keyFechaNac = "cliente_stFechaNac";
    public static final String keyEstadoCivil = "cliente_intEstadoCivil";
    public static final String keyOcupacion = "cliente_stOcupacion";
    public static final String keyNotas = "cliente_stNotas";

    private String stNombre;
    private String stApellido;
    private String stEmail;
    private int intReligion;
    private int intGenero;
    private String stFechaNac;
    private int intEstadoCivil;
    private String stOcupacion;
    private String stNotas;
    private int intStatus;
    private String stVendedor;
    private Long createdAt;
    private int tipoUsuario;
    private String stTelefono;
    private List<Direcciones> direcciones;
    private List<Telefonos> telefonos;
    private Map<String,Object> planes;

    public Cliente_Firebase(){}

    public Cliente_Firebase(String stid, String stNom, String stAp, String stEm, int rel
    , int gen, String feNa, int esCi, String oc, String no, int tipoUsr, List<Telefonos> tels, List<Direcciones> dirs){
        stID=stid; stNombre=stNom; stApellido=stAp; stEmail=stEm; intReligion=rel;
        intGenero=gen; stFechaNac=feNa; intEstadoCivil=esCi; stOcupacion=oc; stNotas=no;
        intStatus=STATUS_PROSPECTO; tipoUsuario=tipoUsr; direcciones = new ArrayList<>(dirs);telefonos=tels;

        /*
        telefonos = new ArrayList<>(tels); direcciones = new HashMap<>(dirs);

        direcs_ = new ArrayList<>();
        Direcciones d1 = new Direcciones();
        d1.updateColonia("ColPrueba");
        d1.updateFachadaURL("link");
        d1.updateIntCP(12345);
        d1.updateStCalle("Calle");
        d1.updateStInt("25A");
        direcs_.add(d1);
        */
    }

    public String toString(){
        return "stID="+stID+" stNombre="+stNombre+" stApellido="+stApellido+" stEmail="+stEmail
                +" intReligion="+intReligion+" intGenero="+intGenero+" stFechaNac="+stFechaNac+" intEstadoCivil="+intEstadoCivil
                +"stOcupacion="+stOcupacion+" stNotas="+stNotas+" intStatus="+intStatus+" stVendedor"+stVendedor+" tipoUsuario="+tipoUsuario+" stTelefono="+stTelefono;
    }

    public String getStID() {
        return stID;
    }

    public void setStID(String stID) {
        this.stID = stID;
    }

    public String getStNombre() {
        return stNombre;
    }

    public void setStNombre(String stNombre) {
        this.stNombre = stNombre;
    }

    public String getStApellido() {
        return stApellido;
    }

    public void setStApellido(String stApellido) {
        this.stApellido = stApellido;
    }

    public String getStEmail() {
        return stEmail;
    }

    public void setStEmail(String stEmail) {
        this.stEmail = stEmail;
    }

    public int getIntReligion() {
        return intReligion;
    }

    public void setIntReligion(int intReligion) {
        this.intReligion = intReligion;
    }

    public int getIntGenero() {
        return intGenero;
    }

    public void setIntGenero(int intGenero) {
        this.intGenero = intGenero;
    }

    public String getStFechaNac() {
        return stFechaNac;
    }

    public void setStFechaNac(String stFechaNac) {
        this.stFechaNac = stFechaNac;
    }

    public int getIntEstadoCivil() {
        return intEstadoCivil;
    }

    public void setIntEstadoCivil(int intEstadoCivil) {
        this.intEstadoCivil = intEstadoCivil;
    }

    public String getStOcupacion() {
        return stOcupacion;
    }

    public void setStOcupacion(String stOcupacion) {
        this.stOcupacion = stOcupacion;
    }

    public String getStNotas() {
        return stNotas;
    }

    public void setStNotas(String stNotas) {
        this.stNotas = stNotas;
    }

    public int getIntStatus() {
        return intStatus;
    }

    public void setIntStatus(int intStatus) {
        this.intStatus = intStatus;
    }

    public String getStVendedor() {
        return stVendedor;
    }

    public void setStVendedor(String stVendedor) {
        this.stVendedor = stVendedor;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public List<Telefonos> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<Telefonos> telefonos) {
        this.telefonos = telefonos;
    }

    public int getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(int tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }



    public String getStTelefono() {
        return stTelefono;
    }

    public void setStTelefono(String stTelefono) {
        this.stTelefono = stTelefono;
    }

    public List<Direcciones> getDirecciones() {
        return direcciones;
    }

    public void setDirecciones(List<Direcciones> direcciones) {
        this.direcciones = direcciones;
    }

    public Map<String, Object> getPlanes() {
        return planes;
    }

    public void setPlanes(Map<String, Object> planes) {
        this.planes = planes;
    }
}
