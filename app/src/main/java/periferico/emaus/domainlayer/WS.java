package periferico.emaus.domainlayer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import periferico.emaus.R;
import periferico.emaus.domainlayer.firebase_objects.CatalogItem_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Cliente_Firebase;
import periferico.emaus.domainlayer.firebase_objects.PerfilEmpleado;
import periferico.emaus.domainlayer.firebase_objects.Plan_Firebase;
import periferico.emaus.domainlayer.firebase_objects.PlanLegacy_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Ticket_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.MatrizPlanes_Firebase;
import periferico.emaus.domainlayer.firebase_objects.ConfiguracionPlanes_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
//import periferico.emaus.domainlayer.firebase_objects.Plan_Firebase;
import periferico.emaus.domainlayer.firebase_objects.UserType_Firebase;
import periferico.emaus.domainlayer.firebase_objects.VersionesAPK_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.Financiamientos_Firebase;

/**
 * Created by maubocanegra on 27/10/17.
 */

public class WS{

    private static final String KEY_VENDEDORES = "vendedores";
    private static final String KEY_CLIENTES = "clientes";
    private static final String KEY_EMPLEADOS = "empleados";
    private static final String KEY_TICKETS = "tickets";
    private static final String KEY_REL_TICKETS = "_relacionesTicket";
    private static final String KEY_REL_TICKETS_COBRADOR = "_porCobrador";
    private static final String KEY_REL_TICKETS_DIA = "_porDia";

    private static String KEY_USER = "";

    private static final String TAG = "WSDebug";
    private static WS instance;

    private static FirebaseAnalytics mFirebaseAnalytics;

    private static DatabaseReference mDatabase;
    private static DatabaseReference.CompletionListener mCompletionListener;

    private static FirebaseAuth mAuth;
    private static FirebaseUser currentUser;
    private static FirebaseAuth.AuthStateListener mAuthStateListener;

    private static boolean loggedIn;
    private static boolean actualNetworkState;

    private static int HELPERrutasDescargadas;

    public static FirebaseUser getCurrentUser() {
        return currentUser;
    }

    /**
     * Metodo que instancia el singleton de los WebServices (Firebase)
     * @param c que es el contexto con el cual se hará la instancia
     * @return Regresa la instancia del
     */
    public synchronized static WS getInstance(final Context c, boolean forceUpdate){

        switch (c.getString(R.string.flavor_string)){
            case "Ventas":{setKeyUser(KEY_VENDEDORES); break;}
            case "Cobranza":{setKeyUser("cobranza"); break;}
        }

        if(instance==null){
            instance = new WS();



            if(mAuth==null) {
                FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
                fbdb.setPersistenceEnabled(true);
                mDatabase = fbdb.getReference();
                mDatabase.keepSynced(true);
                mAuth = FirebaseAuth.getInstance();
            }

            /**
             * Declaramos el escucha de sesiones de Firebase para gestionar si esta loggeado o no
             */
            if(mAuthStateListener==null)
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                    currentUser = firebaseAuth.getCurrentUser();
                    if (currentUser != null) {
                        // User is signed in
                        Log.d("FirebaseDebug", "onAuthStateChanged:signed_in:" + currentUser.getUid());
                        loggedIn=true;

                        mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);

                        String usuario;
                        String preVendedor = currentUser.getEmail().replace(".","");
                        usuario = preVendedor.split("@")[0];
                        usuario = usuario.replace(".","");
                        Long tsLong = System.currentTimeMillis()/1000;
                        Log.d("FirebaseDebug","usuario="+usuario);

                        //--------------------------------------------

                        Calendar cal = Calendar.getInstance();
                        TimeZone tz = cal.getTimeZone();

                        /* debug: is it local time? */
                        Log.d("Time zone: ", tz.getDisplayName());

                        /* date formatter in local timezone */
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        sdf.setTimeZone(tz);

                        /* print your timestamp and double check it's the date you expect */
                        String localTime = sdf.format(new Date(System.currentTimeMillis())); // I assume your timestamp is in seconds and you're converting to milliseconds?
                        Log.d("LocalTime: ", localTime);

                        //13 - 35 - 450
                        //14 - 35 - 490
                        //15 - 35 - 4525
                        //18.3 - 35 - 640


                        //readUserType(c);

                        Map<String,Object> updateUser = new HashMap<>();
                        updateUser.put("lastLogin",tsLong);
                        updateUser.put("lastLoginString",localTime);

                        mDatabase.child(KEY_EMPLEADOS).child(usuario).updateChildren(updateUser,new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError!=null) {
                                    loggedIn = true;
                                    onLoginRequested.loginAnswered(true, null);
                                }
                            }
                        });
                        /*
                        mDatabase.child(KEY_EMPLEADOS).child(usuario).child("lastLogin").setValue(tsLong,
                                new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError!=null) {
                                    loggedIn = true;
                                    onLoginRequested.loginAnswered(true, null);
                                }
                            }
                        });
                        */




                    } else {
                        // User is signed out
                        Log.d("FirebaseDebug", "onAuthStateChanged:signed_out");
                        loggedIn=false;
                    }
                    onLoginRequested.loginAnswered(loggedIn, null);
                }
            };
        }

        return instance;
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    static Thread thread;

    public static void goOffline(){
        mDatabase.onDisconnect();
    }

    public static void goOnline(){

    }

    public static void startJob() {

        if(thread!=null){return;}

        //previousNetworkState = isInternetAvailable();
        actualNetworkState = isInternetAvailable();


        thread = new Thread(new Runnable() {
            public void run() {
                while (true){
                    actualNetworkState=isInternetAvailable();

                    if(actualNetworkState){
                        onNetworkListener.fromOnToOff();
                    }else{
                        onNetworkListener.fromOffToOn();
                    }

                    //Log.d(TAG,"InternetAvailable ? prev="+previousNetworkState+" actual="+actualNetworkState);
                    /*
                    if(previousNetworkState!=actualNetworkState){
                        if(previousNetworkState){onNetworkListener.fromOffToOn();}else{onNetworkListener.fromOnToOff();}
                    }
                    previousNetworkState=actualNetworkState;
                    */

                    try{
                        Thread.sleep(3000);
                    }catch(Exception e){//e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Metodo que intenta hacer el login mediante Firebase Authentication
     * @param email
     * @param password
     */
    public static void tryUserLogin(String email, String password){
        Log.d("LoginDebug","Will try now");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            currentUser = mAuth.getCurrentUser();
                        }else{
                            Log.d("LoginDebug", "signInWithEmail:failure", task.getException());
                            //mandamos a llamar a la implementacion que lo contenga
                            loggedIn=false;
                            onLoginRequested.loginAnswered(false, task.getException());
                        }
                    }
                });
    }

    public static boolean hasInternet(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void showAlert(Activity activity, String title, String message, DialogInterface.OnClickListener listener, boolean hasCancelButton){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title).setMessage(message);
        if(listener!=null){
            builder.setPositiveButton("Entendido", listener);
        }

        if(hasCancelButton){
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    // ------------------------------------------------- //
    // ---------------- WRITING METHODS ---------------- //
    //-------------------------------------------------- //

    public static void writeClientFirebase(final Cliente_Firebase clienteFirebase, final FirebaseCompletionListener firebaseCompletionListener){
        final String vendedor = currentUser.getEmail().split("@")[0].replace(".","");
        clienteFirebase.setCreatedAt(System.currentTimeMillis()/1000);
        clienteFirebase.setStVendedor(vendedor);

        mDatabase.child(KEY_CLIENTES).child(clienteFirebase.getStID()).setValue(clienteFirebase, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Object_Firebase objF = new Object_Firebase();
                objF.setStID(clienteFirebase.getStID());
                mDatabase.child(KEY_VENDEDORES).child(vendedor).child(KEY_CLIENTES).child(clienteFirebase.getStID()).setValue(true, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        firebaseCompletionListener.firebaseCompleted(false);
                    }
                });
            }
        });
    }

    public static void writePlanFirebase(final Object_Firebase obj_firebase, final FirebaseCompletionListener firebaseCompletionListener_){
        String vendedor = currentUser.getEmail().split("@")[0].replace(".","");
        ((Plan_Firebase)obj_firebase).setCreatedAt(System.currentTimeMillis()/1000);
        ((Plan_Firebase)obj_firebase).setStVendedor(vendedor);

        Log.d(TAG, "stVendedor="+vendedor+" stCliente="+((Plan_Firebase)obj_firebase).getStCliente());

        //Guardamos la referencia del plan en el cliente
        mDatabase.child(KEY_CLIENTES).child(((Plan_Firebase)obj_firebase).getStCliente()).child("planes").child(obj_firebase.getStID()).setValue(true);

        //Actualizamos el status del cliente
        mDatabase.child(KEY_CLIENTES).child(((Plan_Firebase)obj_firebase).getStCliente()).child("intStatus").setValue(1);

        //((Plan_Firebase)obj_firebase).
        mDatabase.child(KEY_CLIENTES).child(((Plan_Firebase)obj_firebase).getStCliente()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Cliente_Firebase clienteFirebase = dataSnapshot.getValue(Cliente_Firebase.class);
                ((Plan_Firebase)obj_firebase).setDirecciones(clienteFirebase.getDirecciones());
                Log.d(TAG, "previo a subirse="+((Plan_Firebase)obj_firebase));

                //Escribimos el plan en la lista de planes
                mDatabase.child("planes").child(((Plan_Firebase)obj_firebase).getStID()).setValue(obj_firebase,
                        new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError!=null) {
                                    Log.e(TAG, "ERROR = " + databaseError.getDetails() + "\n" + databaseError.getMessage());
                                    firebaseCompletionListener_.firebaseCompleted(true);
                                }else {
                                    Log.d(TAG, "SUCCESFULLY WRITEN!");
                                    firebaseCompletionListener_.firebaseCompleted(false);
                                }
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public static void writeTicketFirebase(
            final Ticket_Firebase ticketFirebase,
            final FirebaseCompletionListener firebaseCompletionListener){
        final String empleado = currentUser.getEmail().split("@")[0].replace(".","");
        final Object_Firebase ticketObj = new Object_Firebase();


        //-------GUARDAMOS EL TICKET
        mDatabase.child(KEY_TICKETS).child(ticketFirebase.getStID()).setValue(ticketFirebase, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.d(TAG, "Paso1 TicketGuardado!");
            }
        });


        //-------GUARDAMOS EL ID del ticket por cobrador
        mDatabase.child(KEY_REL_TICKETS).child(KEY_REL_TICKETS_COBRADOR).child(empleado).child(ticketFirebase.getStID()).setValue(true, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.d(TAG, "Paso2 TicketHistorialCobrador Guardado!");
            }
        });

        //-------GUARDAMOS EL ID del ticket por DIA
        mDatabase.child(KEY_REL_TICKETS).child(KEY_REL_TICKETS_DIA).child(empleado).child(ticketFirebase.getKeyDiaCreacion()).child(ticketFirebase.getStID()).setValue(true, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.d(TAG, "Paso3 TicketHistorialDia Guardado!");
            }
        });

        //-------Guardamos la referencia en el Plan
        mDatabase.child("planes")
                .child(ticketFirebase.getPlanID())
                .child("tickets")
                .child(ticketFirebase.getStID())
                .setValue(true, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                Log.d(TAG, "Paso4 GuardadoListaTicketsPlan");
                            }
                        }
                );

        //-------Actualizamos el numero de pagos realizados y el saldo en el plan
        Map<String, Object> childrenToUpdate = new HashMap<>();
        childrenToUpdate.put("pagosRealizados",ticketFirebase.getNumAbono());
        childrenToUpdate.put("saldo",ticketFirebase.getNuevoSaldo());
        childrenToUpdate.put("ultimoTicketRealizado",ticketFirebase.getStID());
        childrenToUpdate.put("fechaUltimoPago",ticketFirebase.getCreatedAt());
        mDatabase.child("planes").child(ticketFirebase.getPlanID()).updateChildren(childrenToUpdate, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.d(TAG, "Paso5 PlanUpdated");
                firebaseCompletionListener.firebaseCompleted(false);
            }
        });

    }


    // ------------------------------------------------- //
    // ---------------- READING METHODS ---------------- //
    //-------------------------------------------------- //

    public static void readUserType(Context c, final FirebaseObjectRetrievedListener firebaseObjectRetrievedListener){
        String userKey = c.getString(R.string.flavor_string);


        switch (userKey){
            case "Ventas":{ userKey = KEY_VENDEDORES; break;}
            case "Cobranza":{ userKey = "cobranza"; break;}
        }


        String usuario = currentUser.getEmail().split("@")[0].replace(".","");

        mDatabase.child(KEY_EMPLEADOS).child(usuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d(TAG,"userKey="+key+"usuario="+currentUser.getEmail().split("@")[0].replace(".","")+"SEGMENTO dataSnapshot = "+dataSnapshot.getValue());
                if(dataSnapshot.getValue()!=null){
                    firebaseObjectRetrievedListener.firebaseObjectRetrieved(dataSnapshot.getValue(UserType_Firebase.class));
                }
            }

            @Override public void onCancelled(DatabaseError databaseError) {Log.e("WS -> readUserType",databaseError.toString());}
        });
    }

    public static void readClientAndDirectoryFirebase(final FirebaseArrayRetreivedListener arrayRetreivedListener){
        String vendedor = currentUser.getEmail().split("@")[0].replace(".","");

        switch(getKeyUser()){
            case KEY_VENDEDORES:{
                final ArrayList<Object_Firebase> fullDirectory = new ArrayList<>();
                mDatabase.child(KEY_VENDEDORES).child(vendedor).child("clientes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final long snapFatherCount = dataSnapshot.getChildrenCount();
                        for(DataSnapshot snap : dataSnapshot.getChildren()){
                            mDatabase.child("clientes").child(snap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    fullDirectory.add(dataSnapshot.getValue(Cliente_Firebase.class));
                                    if(fullDirectory.size()==snapFatherCount){
                                        arrayRetreivedListener.firebaseCompleted(fullDirectory);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.toString());
                    }
                });
                break;
            }

            case "cobranza":{

                final ArrayList<Object_Firebase> fullDirectory = new ArrayList<>();

                String empleado = currentUser.getEmail().split("@")[0].replace(".","");
                mDatabase.child("asignacionRutas").child(empleado).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final DataSnapshot dataSnapshotFather = dataSnapshot;
                        ArrayList<String> cps = new ArrayList<>();
                        final ArrayList<Object_Firebase> planes = new ArrayList<>();
                        for(DataSnapshot cpSnap : dataSnapshotFather.getChildren()){

                            String spToFilterBy = cpSnap.getKey();
                            spToFilterBy = spToFilterBy.replace("\"","");
                            mDatabase.child("planes").orderByChild("direcciones/0/stCP").equalTo(spToFilterBy).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final DataSnapshot dataSnapshotplanes = dataSnapshot;
                                    for(DataSnapshot snap : dataSnapshot.getChildren()){
                                        Plan_Firebase planFirebase = snap.getValue(Plan_Firebase.class);

                                        mDatabase.child(KEY_CLIENTES).child(planFirebase.getStCliente()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                fullDirectory.add(dataSnapshot.getValue(Cliente_Firebase.class));
                                                if(dataSnapshotFather.getChildrenCount()== fullDirectory.size()){
                                                    arrayRetreivedListener.firebaseCompleted(fullDirectory);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Log.e(TAG, databaseError.toString());
                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, databaseError.toString());
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {Log.e("WS -> readRutasAsig",databaseError.toString());}
                });


                break;
            }
        }

        mDatabase.child("directorio").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Object_Firebase> fullDirectory = new ArrayList<>();
                for(DataSnapshot clienteSnapshot : dataSnapshot.getChildren()){
                    fullDirectory.add(clienteSnapshot.getValue(Cliente_Firebase.class));
                }
                arrayRetreivedListener.firebaseCompleted(fullDirectory);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {Log.e("WS -> readClientAndDir",databaseError.toString());}
        });
    }

    public static void readClientListFirebase(final FirebaseKeyListRetrievedListener firebaseKeyListRetrievedListener){
        String vendedor = currentUser.getEmail().split("@")[0].replace(".","");
        mDatabase.child(KEY_VENDEDORES).child(vendedor).child(KEY_CLIENTES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> allClients = new ArrayList<>();
                for(DataSnapshot clienteSnapshot : dataSnapshot.getChildren()){
                    allClients.add(clienteSnapshot.getKey());
                }
                firebaseKeyListRetrievedListener.firebaseKeyListRetrieved(allClients);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {Log.e("WS -> readClientList",databaseError.toString());}
        });
        /*

        mDatabase.child(KEY_VENDEDORES).child(vendedor).child(KEY_CLIENTES).orderByChild("createdAt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */
    }

    public static void readCatalogListFirebase(final FirebaseArrayRetreivedListener firebaseArrayRetreivedListener){
        mDatabase.child("images").child("catalogo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,dataSnapshot.toString());
                ArrayList<Object_Firebase> imagenes = new ArrayList<>();
                for(DataSnapshot imagenSnapshot : dataSnapshot.getChildren()){
                    imagenes.add(imagenSnapshot.getValue(CatalogItem_Firebase.class));
                }

                firebaseArrayRetreivedListener.firebaseCompleted(imagenes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {Log.e("WS -> readCatalogList",databaseError.toString());}
        });

    }

    public static void readClientFirebase(String clientID, final FirebaseObjectRetrievedListener firebaseObjectRetrievedListener){
        Log.d(TAG,"breforeAskingFullClient = "+clientID);
        mDatabase.child(KEY_CLIENTES).child(clientID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Cliente_Firebase clienteFirebase = dataSnapshot.getValue(Cliente_Firebase.class);
                firebaseObjectRetrievedListener.firebaseObjectRetrieved(clienteFirebase);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {Log.e("WS -> readClientF",databaseError.toString());}
        });
    }

    public static void readPlansListFirebase(String stID, final FirebaseArrayRetreivedListener firebaseArrayRetreivedListener){
        mDatabase.child("planes").child(stID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,dataSnapshot.toString());
                ArrayList<Object_Firebase> planes = new ArrayList<>();
                for(DataSnapshot planSnapshot : dataSnapshot.getChildren()){
                    planes.add(planSnapshot.getValue(PlanLegacy_Firebase.class));
                }
                firebaseArrayRetreivedListener.firebaseCompleted(planes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {Log.e("WS -> readPlansList",databaseError.toString());}
        });
    }

    public static void readPlanFirebase(String planID, final FirebaseObjectRetrievedListener firebaseObjectRetrievedListener){
        mDatabase.child("planes").child(planID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Plan_Firebase planFirebase = dataSnapshot.getValue(Plan_Firebase.class);
                firebaseObjectRetrievedListener.firebaseObjectRetrieved(planFirebase);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { Log.e("WS -> readPlan",databaseError.toString());}
        });
    }

    /*
    public static void readPlanFirebase(String clienteID, String planID, final FirebaseObjectRetrievedListener firebaseObjectRetrievedListener){
        mDatabase.child("planes").child(clienteID).child(planID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Plan_Firebase planFirebase = dataSnapshot.getValue(Plan_Firebase.class);
                firebaseObjectRetrievedListener.firebaseObjectRetrieved(planFirebase);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    */

    public static void readPlanesListFirebase(String clientID, final FirebaseKeyListRetrievedListener firebaseKeyListRetrievedListener){
        mDatabase.child(KEY_CLIENTES).child(clientID).child("planes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,dataSnapshot.toString());
                ArrayList<String> planes = new ArrayList<>();
                for(DataSnapshot planSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "leido="+planSnapshot.getKey());
                    planes.add(planSnapshot.getKey());
                }
                firebaseKeyListRetrievedListener.firebaseKeyListRetrieved(planes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {Log.e("WS -> readPlanesList",databaseError.toString());}
        });
    }

    public static void readMensualidadesFinanciamiento (final FirebaseObjectRetrievedListener firebaseObjectRetrievedListener){
        Log.d(TAG,"beforeDownloadingMensualidadesFinanciamiento");
        mDatabase.child("configplanes").child("listamensualidades").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.toString());
                Financiamientos_Firebase mensualidadesPago = dataSnapshot.getValue(Financiamientos_Firebase.class);
                firebaseObjectRetrievedListener.firebaseObjectRetrieved(mensualidadesPago);
            }

            @Override public void onCancelled(DatabaseError databaseError) { Log.e("WS -> readMensualidades",databaseError.toString());}
        });
    }

    public static void readConfigPlanes(final FirebaseObjectRetrievedListener firebaseObjectRetrievedListener){
        Log.d(TAG,"beforeDownloadingConfigPlanes");
        mDatabase.child("configplanes").child("planes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.toString());
                MatrizPlanes_Firebase configPlanes = dataSnapshot.getValue(MatrizPlanes_Firebase.class);
                firebaseObjectRetrievedListener.firebaseObjectRetrieved(configPlanes);
            }

            @Override public void onCancelled(DatabaseError databaseError) { Log.e("WS -> rConfigPlanes",databaseError.toString());}
        });
    }

    public static void readConfiguracionPlanes(final FirebaseObjectRetrievedListener firebaseObjectRetrievedListener){
        Log.d(TAG,"beforeDownloadingConfiguracionPlanes");
        mDatabase.child("configplanes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.toString());
                ConfiguracionPlanes_Firebase configPlanes = dataSnapshot.getValue(ConfiguracionPlanes_Firebase.class);
                firebaseObjectRetrievedListener.firebaseObjectRetrieved(configPlanes);
            }

            @Override public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public static void readRutasAsignadas(final FirebaseArrayRetreivedListener firebaseArrayRetreivedListener){
        String empleado = currentUser.getEmail().split("@")[0].replace(".","");
        mDatabase.child("asignacionRutas").child(empleado).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final DataSnapshot dataSnapshotFather = dataSnapshot;
                ArrayList<String> cps = new ArrayList<>();
                final ArrayList<Object_Firebase> planes = new ArrayList<>();
                if(dataSnapshotFather.getChildrenCount()==0){firebaseArrayRetreivedListener.firebaseCompleted(planes);}
                for(DataSnapshot cpSnap : dataSnapshotFather.getChildren()){
                    Log.d(TAG, "rutasAsignadas="+cpSnap.getKey());

                    String spToFilterBy = cpSnap.getKey();
                    spToFilterBy = spToFilterBy.replace("\"","");
                    mDatabase.child("planes").orderByChild("direcciones/0/stCP").equalTo(spToFilterBy).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot snap : dataSnapshot.getChildren()){
                                Log.d(TAG, "ruta -- CPDownloaded="+snap.getKey());
                                planes.add(snap.getValue(Plan_Firebase.class));

                                if(planes.size()==dataSnapshotFather.getChildrenCount()){
                                    firebaseArrayRetreivedListener.firebaseCompleted(planes);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, databaseError.toString());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {Log.e("WS -> readRutasAsig",databaseError.toString());}
        });
    }

    public static void descargarTicketsPorPlan(final FirebaseArrayRetreivedListener firebaseArrayRetreivedListener, final ArrayList<String> keys){
        final ArrayList<Object_Firebase> tickets = new ArrayList<>();
        if(keys.size()==0){firebaseArrayRetreivedListener.firebaseCompleted(tickets);}
        for(String keyTicket : keys){
            mDatabase.child(KEY_TICKETS).child(keyTicket).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tickets.add(dataSnapshot.getValue(Ticket_Firebase.class));
                    if(tickets.size()==keys.size()){firebaseArrayRetreivedListener.firebaseCompleted(tickets);}
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {Log.e("WS -> tickPorFech_Inn",databaseError.toString());}
            });
        }
    }

    public static void descargarTicketsPorCobrador(final FirebaseKeyListRetrievedListener firebaseKeyListRetrievedListener){
        String empleado = currentUser.getEmail().split("@")[0].replace(".","");
        mDatabase.child(KEY_REL_TICKETS).child(KEY_REL_TICKETS_COBRADOR).child(empleado).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> keys = new ArrayList<>();
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    keys.add(snap.getKey());
                }
                firebaseKeyListRetrievedListener.firebaseKeyListRetrieved(keys);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {Log.e("WS -> ticketsPorCobr",databaseError.toString());}
        });
    }

    public static void descargarTicketsPorFechaYCobrador(String keyFecha, final FirebaseArrayRetreivedListener firebaseArrayRetrievedListener){
        String empleado = currentUser.getEmail().split("@")[0].replace(".","");
        Log.d(TAG, "willAsk="+KEY_REL_TICKETS+" > "+KEY_REL_TICKETS_DIA+" > "+empleado+" > "+keyFecha);
        mDatabase.child(KEY_REL_TICKETS).child(KEY_REL_TICKETS_DIA).child(empleado).child(keyFecha).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> keys = new ArrayList<>();
                final long numTickets=dataSnapshot.getChildrenCount();
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    keys.add(snap.getKey());
                }

                final ArrayList<Object_Firebase> tickets = new ArrayList<>();
                if(keys.size()==0){firebaseArrayRetrievedListener.firebaseCompleted(tickets);}
                for(String keyTicket : keys){
                    mDatabase.child(KEY_TICKETS).child(keyTicket).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            tickets.add(dataSnapshot.getValue(Ticket_Firebase.class));
                            if(tickets.size()==numTickets){firebaseArrayRetrievedListener.firebaseCompleted(tickets);}
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {Log.e("WS -> tickPorFech_Inn",databaseError.toString());}
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {Log.e("WS -> ticketsPorFech",databaseError.toString());}
        });
    }

    public static void readTicket(String ticketID, final FirebaseObjectRetrievedListener firebaseObjectRetrievedListener){
        mDatabase.child(KEY_TICKETS).child(ticketID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseObjectRetrievedListener.firebaseObjectRetrieved(dataSnapshot.getValue(Ticket_Firebase.class));
            }

            @Override public void onCancelled(DatabaseError databaseError) {Log.e("WS -> readTicket",databaseError.toString());}
        });
    }

    public static void readPerfilEmpleado(final FirebaseObjectRetrievedListener firebaseObjectRetrievedListener){
        String empleado = currentUser.getEmail().split("@")[0].replace(".","");
        mDatabase.child(KEY_EMPLEADOS).child(empleado).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseObjectRetrievedListener.firebaseObjectRetrieved(dataSnapshot.getValue(PerfilEmpleado.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("WS -> readEmpleado",databaseError.toString());
            }
        });
    }



    public static void readVersionesAPKFirebase(/*final FirebaseObjectRetrieved firebaseObjectRetrieved*/Activity activity){

        final FirebaseObjectRetrievedListener firebaseObjectRetrievedListener = (FirebaseObjectRetrievedListener)activity;

        String usuario = currentUser.getEmail().split("@")[0].replace(".","");
        String keyUser = KEY_USER.equals(KEY_VENDEDORES) ? "ventas" : "cobranza";
        mDatabase.child("versionesAPK").child(keyUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    VersionesAPK_Firebase versionesAPKFirebase = dataSnapshot.getValue(VersionesAPK_Firebase.class);
                    Log.d(TAG, "versiones="+dataSnapshot.toString());
                    Log.d(TAG, "ultimaVersion=" + versionesAPKFirebase.getUltimaVersion());
                    firebaseObjectRetrievedListener.firebaseObjectRetrieved(versionesAPKFirebase);
                }catch(Exception e){e.printStackTrace();}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {Log.e("WS -> readAPK",databaseError.toString());}
        });
    }

    public static void downloadAPKLink(final Context c, final String stID, OnSuccessListener<Uri> successListener, String apkLink){
        FirebaseStorage storage;
        StorageReference storageRef;

        storage = FirebaseStorage.getInstance("gs://"+c.getResources().getString(R.string.google_storage_bucket));
        storageRef = storage.getReference();

        Log.d(TAG, "apkLink="+apkLink);

        StorageReference httpsReference = storage.getReferenceFromUrl(apkLink);

        httpsReference.getDownloadUrl().addOnSuccessListener(successListener);
    }

    // --------------------------------------------------- //
    // ---------------- ANALYTICS METHODS ---------------- //
    //---------------------------------------------------- //

    public static void registerAnalyticsEvent(String eventName, Bundle bundle){
        String vendedor = currentUser.getEmail().split("@")[0].replace(".","");
        bundle.putString("vendedor",vendedor);
        bundle.putLong("createdAt",System.currentTimeMillis()/1000);
        mFirebaseAnalytics.logEvent(eventName, bundle);
    }

    /*
    private static void showError(String error, View view){
        Snackbar snackbar = Snackbar.make((CoordinatorLayout) view.findViewById(R.id.coordinatorLayout),context.getString(R.string.login_error_login), Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorAccent));
        TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.BLACK);
        tv.setText("Google Play Servies no está actualizado!");
        snackbar.show();
    }
    */

    // --------------------------------------------------- //
    // ---------------- SETTERS & GETTERS ---------------- //
    //---------------------------------------------------- //

    public static String getKeyUser() { return KEY_USER; }
    public static void setKeyUser(String keyUser) { KEY_USER = keyUser; }


    // ---------------------------------------------- //
    // ---------------- Listeners ?? ---------------- //
    //----------------------------------------------- //

    /**
     * Settea el listener en cuestion para la escucha de las sesiones
     * @param loginRequested
     */
    public static void setLoginListener(OnLoginRequested loginRequested){
        onLoginRequested=loginRequested;
    }

    /**
     * Metodo que se encarga de activar el escucha de las sesiones de Firebase
     */
    public static void addFirebaseAuthStateListener(){
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    /**
     * Metodo que se encarga de desactivar el escucha de las sesiones de Firebase
     */
    public static void removeFirebaseAuthStateListener(){
        if(mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    // ---------------------------------------------- //
    // ---------------- OWN LISTENER ---------------- //
    //----------------------------------------------- //

    /**
     * Declaracion de la interfaz que comunca el WS  con la clase que lo implemente
     */
    public interface FirebaseCompletionListener{
        public void firebaseCompleted(boolean hasError);
    }

    public interface  FirebaseArrayRetreivedListener{
        public void firebaseCompleted(ArrayList<Object_Firebase> arrayList);
    }

    public interface FirebaseObjectRetrievedListener {
        public void firebaseObjectRetrieved(Object_Firebase objectFirebase);
    }

    public interface FirebaseKeyListRetrievedListener{
        public void firebaseKeyListRetrieved(ArrayList<String> keys);
    }

    /**
     * Declaracion de la interfaz que se encargara de la comunicacion entre WS y la clase de LOGIN
     */
    public static OnLoginRequested onLoginRequested;
    public interface  OnLoginRequested{
        void loginAnswered(boolean success, Exception errExc);
    }

    public static OnNetworkListener onNetworkListener;
    public interface OnNetworkListener{
        void  fromOffToOn();
        void fromOnToOff();
    }public static void setNetworkListener(OnNetworkListener onNetworkListener_) {
        onNetworkListener = onNetworkListener_;
    }
}
