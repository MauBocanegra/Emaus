package periferico.emaus.domainlayer.firebase_objects;

/**
 * Created by maubocanegra on 26/03/18.
 */

public class UserType_Firebase extends Object_Firebase{
    private boolean cobranza;
    private boolean ventas;
    private Long lastLogin;
    private String lastLoginString;


    public boolean isCobranza() {
        return cobranza;
    }

    public void setCobranza(boolean cobranza) {
        this.cobranza = cobranza;
    }

    public boolean isVentas() {
        return ventas;
    }

    public void setVentas(boolean ventas) {
        this.ventas = ventas;
    }

    public Long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastLoginString() {
        return lastLoginString;
    }

    public void setLastLoginString(String lastLoginString) {
        this.lastLoginString = lastLoginString;
    }
}
