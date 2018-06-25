package periferico.emaus.domainlayer.firebase_objects;

/**
 * Created by maubocanegra on 07/01/18.
 */

public class Version extends Object_Firebase{
    public String linkDescarga;
    public int versionCode;
    public String versionName;
    public String nombreAPK;

    public Version(){}

    public String getLinkDescarga() {
        return linkDescarga;
    }

    public void setLinkDescarga(String linkDescarga) {
        this.linkDescarga = linkDescarga;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getNombreAPK() {
        return nombreAPK;
    }

    public void setNombreAPK(String nombreAPK) {
        this.nombreAPK = nombreAPK;
    }
}