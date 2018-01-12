package periferico.emaus.domainlayer.firebase_objects;

/**
 * Created by maubocanegra on 29/12/17.
 */

public class Directorio_Firebase extends Object_Firebase{

    public String stNombre;
    public int intStatus;
    public Long createdAt;
    public int tipoUsuario;

    public Directorio_Firebase(){}

    public String getStNombre() {
        return stNombre;
    }

    public void setStNombre(String stNombre) {
        this.stNombre = stNombre;
    }

    public int getIntStatus() {
        return intStatus;
    }

    public void setIntStatus(int intStatus) {
        this.intStatus = intStatus;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public int getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(int tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
