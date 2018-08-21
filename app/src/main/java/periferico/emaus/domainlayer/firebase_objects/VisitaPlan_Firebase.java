package periferico.emaus.domainlayer.firebase_objects;

public class VisitaPlan_Firebase extends Object_Firebase {

    long createdAt;
    String empleadoID;
    String planID;
    int intEstatus;
    String tituloEstatus;

    public VisitaPlan_Firebase() {}

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmpleadoID() {
        return empleadoID;
    }

    public void setEmpleadoID(String empleadoID) {
        this.empleadoID = empleadoID;
    }

    public String getPlanID() {
        return planID;
    }

    public void setPlanID(String planID) {
        this.planID = planID;
    }

    public int getIntEstatus() {
        return intEstatus;
    }

    public void setIntEstatus(int intEstatus) {
        this.intEstatus = intEstatus;
    }

    public String getTituloEstatus() {
        return tituloEstatus;
    }

    public void setTituloEstatus(String tituloEstatus) {
        this.tituloEstatus = tituloEstatus;
    }
}
