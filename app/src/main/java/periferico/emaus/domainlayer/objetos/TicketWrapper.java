package periferico.emaus.domainlayer.objetos;

import periferico.emaus.domainlayer.firebase_objects.Cliente_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Plan_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Ticket_Firebase;

public class TicketWrapper {

    private Cliente_Firebase clienteFirebase;
    private Plan_Firebase planFirebase;
    private Ticket_Firebase ticketFirebase;
    private String ticketID;

    public TicketWrapper(){}

    public TicketWrapper(String ticketID_){
        ticketID=ticketID_;
    }

    public TicketWrapper(Ticket_Firebase ticket){
        ticketFirebase = ticket;
    }

    public Cliente_Firebase getClienteFirebase() {
        return clienteFirebase;
    }

    public void setClienteFirebase(Cliente_Firebase clienteFirebase) {
        this.clienteFirebase = clienteFirebase;
    }

    public Plan_Firebase getPlanFirebase() {
        return planFirebase;
    }

    public void setPlanFirebase(Plan_Firebase planFirebase) {
        this.planFirebase = planFirebase;
    }

    public Ticket_Firebase getTicketFirebase() {
        return ticketFirebase;
    }

    public void setTicketFirebase(Ticket_Firebase ticketFirebase) {
        this.ticketFirebase = ticketFirebase;
    }

    public String getTicketID() {

        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }
}
