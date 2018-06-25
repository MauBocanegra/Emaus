package periferico.emaus.domainlayer.firebase_objects;

import java.util.List;

/**
 * Created by maubocanegra on 07/01/18.
 */

public class VersionesAPK_Firebase extends Object_Firebase {

    Version ultimaVersion;
    List<Version> versionesPasadas;

    public VersionesAPK_Firebase(){}

    public Version getUltimaVersion() {
        return ultimaVersion;
    }

    public void setUltimaVersion(Version ultimaVersion) {
        this.ultimaVersion = ultimaVersion;
    }

    public List<Version> getVersionesPasadas() {
        return versionesPasadas;
    }

    public void setVersionesPasadas(List<Version> versionesPasadas) {
        this.versionesPasadas = versionesPasadas;
    }
}
