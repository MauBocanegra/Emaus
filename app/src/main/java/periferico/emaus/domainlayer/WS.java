package periferico.emaus.domainlayer;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import periferico.emaus.BuildConfig;
import periferico.emaus.R;
import periferico.emaus.domainlayer.firebase_objects.CatalogItem_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Cliente_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Directorio_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Plan_Firebase;
import periferico.emaus.domainlayer.firebase_objects.VersionesAPK_Firebase;
import periferico.emaus.presentationlayer.activities.Login;

/**
 * Created by maubocanegra on 27/10/17.
 */

public class WS{

    private static final String KEY_VENDEDORES = "vendedores";
    private static final String KEY_CLIENTES = "clientes";

    private static final String TAG = "WSDebug";
    private static WS instance;

    private static FirebaseAnalytics mFirebaseAnalytics;

    private static DatabaseReference mDatabase;
    private static DatabaseReference.CompletionListener mCompletionListener;

    private static FirebaseAuth mAuth;
    private static FirebaseUser currentUser;
    private static FirebaseAuth.AuthStateListener mAuthStateListener;

    private static boolean loggedIn;

    /**
     * Metodo que instancia el singleton de los WebServices (Firebase)
     * @param c que es el contexto con el cual se hará la instancia
     * @return Regresa la instancia del
     */
    public synchronized static WS getInstance(final Context c){
        if(instance==null){
            instance = new WS();

            if(mAuth==null) {
                FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
                fbdb.setPersistenceEnabled(false);
                mDatabase = fbdb.getReference();
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

                        String vendedor;
                        String preVendedor = currentUser.getEmail().replace(".","");
                        vendedor = preVendedor.split("@")[0];
                        vendedor = vendedor.replace(".","");
                        Long tsLong = System.currentTimeMillis()/1000;
                        Log.d("FirebaseDebug","vendedor="+vendedor);
                        mDatabase.child(KEY_VENDEDORES).child(vendedor).child("lastLogin").setValue(tsLong,
                                new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError!=null) {
                                    loggedIn = true;
                                    onLoginRequested.loginAnswered(true, null);
                                }
                            }
                        });

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
                            loggedIn=true;
                            onLoginRequested.loginAnswered(false, task.getException());
                        }
                    }
                });
    }

    // ------------------------------------------------- //
    // ---------------- WRITING METHODS ---------------- //
    //-------------------------------------------------- //

    public static void escribirFirebase(Object_Firebase obj_firebase, final FirebaseCompletionListener firebaseCompletionListener_){
    }

    public static void crearClienteFirebase(final Object_Firebase obj_firebase, final FirebaseCompletionListener firebaseCompletionListener_){
        String vendedor = currentUser.getEmail().split("@")[0].replace(".","");
        ((Cliente_Firebase)obj_firebase).setCreatedAt(System.currentTimeMillis()/1000);
        ((Cliente_Firebase)obj_firebase).setStVendedor(vendedor);
        mDatabase.child(KEY_CLIENTES).child(obj_firebase.getStID()).setValue(obj_firebase);
        mDatabase.child(KEY_CLIENTES).child(obj_firebase.getStID()).child(KEY_VENDEDORES).child(vendedor).setValue(true, new DatabaseReference.CompletionListener() {
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

        Directorio_Firebase directorioFirebase = new Directorio_Firebase();
        directorioFirebase.setStNombre(((Cliente_Firebase)obj_firebase).getStNombre()+" "+((Cliente_Firebase)obj_firebase).getStApellido());
        directorioFirebase.setCreatedAt(System.currentTimeMillis()/1000);
        directorioFirebase.setIntStatus(((Cliente_Firebase)obj_firebase).getIntStatus());
        directorioFirebase.setTipoUsuario(1);
        directorioFirebase.setStID(obj_firebase.getStID());
        mDatabase.child(KEY_VENDEDORES).child(vendedor).child(KEY_CLIENTES).child(directorioFirebase.getStID()).setValue(directorioFirebase);
    }

    public static void writePlanFirebase(final Object_Firebase obj_firebase, final FirebaseCompletionListener firebaseCompletionListener_){
        String vendedor = currentUser.getEmail().split("@")[0].replace(".","");
        ((Plan_Firebase)obj_firebase).setCreatedAt(System.currentTimeMillis()/1000);
        ((Plan_Firebase)obj_firebase).setStVendedor(vendedor);

        mDatabase.child(KEY_CLIENTES).child(((Plan_Firebase)obj_firebase).getStCliente()).child("intStatus").setValue(1);
        mDatabase.child(KEY_VENDEDORES).child(((Plan_Firebase)obj_firebase).getStVendedor()).child(KEY_CLIENTES).child(((Plan_Firebase)obj_firebase).getStCliente()).child("intStatus").setValue(1);
        mDatabase.child("planes").child(((Plan_Firebase)obj_firebase).getStCliente()).child(((Plan_Firebase)obj_firebase).getStID()).setValue(obj_firebase,
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

    // ------------------------------------------------- //
    // ---------------- READING METHODS ---------------- //
    //-------------------------------------------------- //

    public static void readClientAndDirectoryFirebase(final FirebaseArrayRetreivedListener arrayRetreivedListener){
        String vendedor = currentUser.getEmail().split("@")[0].replace(".","");
        mDatabase.child(KEY_VENDEDORES).child(vendedor).child(KEY_CLIENTES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Object_Firebase> fullDirectory = new ArrayList<Object_Firebase>();
                for(DataSnapshot clienteSnapshot : dataSnapshot.getChildren()){
                    fullDirectory.add(clienteSnapshot.getValue(Cliente_Firebase.class));
                }
                arrayRetreivedListener.firebaseCompleted(fullDirectory);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        mDatabase.child("directorio").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Object_Firebase> fullDirectory = new ArrayList<Object_Firebase>();
                for(DataSnapshot clienteSnapshot : dataSnapshot.getChildren()){
                    fullDirectory.add(clienteSnapshot.getValue(Cliente_Firebase.class));
                }
                arrayRetreivedListener.firebaseCompleted(fullDirectory);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public static void readClientListFirebase(final FirebaseArrayRetreivedListener firebaseArrayRetreivedListener){
        String vendedor = currentUser.getEmail().split("@")[0].replace(".","");
        mDatabase.child(KEY_VENDEDORES).child(vendedor).child(KEY_CLIENTES).orderByChild("createdAt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> itClientes = dataSnapshot.getChildren();
                ArrayList<Object_Firebase> clientes = new ArrayList<>();
                for(DataSnapshot clienteSnapshot : dataSnapshot.getChildren()){
                    Object_Firebase objectFirebase = clienteSnapshot.getValue(Cliente_Firebase.class);
                    objectFirebase.setStID(clienteSnapshot.getKey());
                    clientes.add(objectFirebase);
                }
                Collections.reverse(clientes);
                firebaseArrayRetreivedListener.firebaseCompleted(clientes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        mDatabase.child(KEY_VENDEDORES).child(vendedor).child(KEY_CLIENTES).orderByChild("createdAt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    public static void readClientFirebase(String clientID, final FirebaseObjectRetrieved firebaseObjectRetrieved){
        Log.d(TAG,"breforeAskingFullClient = "+clientID);
        mDatabase.child(KEY_CLIENTES).child(clientID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Cliente_Firebase clienteFirebase = dataSnapshot.getValue(Cliente_Firebase.class);
                firebaseObjectRetrieved.firebaseObjectRetrieved(clienteFirebase);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public static void readPlansListFirebase(String stID, final FirebaseArrayRetreivedListener firebaseArrayRetreivedListener){
        mDatabase.child("planes").child(stID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,dataSnapshot.toString());
                ArrayList<Object_Firebase> planes = new ArrayList<>();
                for(DataSnapshot planSnapshot : dataSnapshot.getChildren()){
                    planes.add(planSnapshot.getValue(Plan_Firebase.class));
                }
                firebaseArrayRetreivedListener.firebaseCompleted(planes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public static void readVersionesAPKFirebase(final FirebaseObjectRetrieved firebaseObjectRetrieved){
        mDatabase.child("versionesAPK").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VersionesAPK_Firebase versionesAPKFirebase = dataSnapshot.getValue(VersionesAPK_Firebase.class);
                Log.d(TAG, "ultimaVersion="+versionesAPKFirebase.getUltimaVersion());
                firebaseObjectRetrieved.firebaseObjectRetrieved(versionesAPKFirebase);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public static void downloadAPK(final Context c, final String stID, OnSuccessListener<Uri> successListener){
        FirebaseStorage storage;
        StorageReference storageRef;

        storage = FirebaseStorage.getInstance(c.getString(R.string.FirebaseStorageBucket));
        storageRef = storage.getReference();

        StorageReference httpsReference = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/firebase-emaus.appspot.com/o/apk_versiones%2FEne7_2300.apk?alt=media&token=a013044c-70a3-4cf0-a55d-eb2ea5e23952");

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

    public static FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(FirebaseUser currentUser) {
        WS.currentUser = currentUser;
    }


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

    public interface FirebaseObjectRetrieved{
        public void firebaseObjectRetrieved(Object_Firebase objectFirebase);
    }

    /**
     * Declaracion de la interfaz que se encargara de la comunicacion entre WS y la clase de LOGIN
     */
    public static OnLoginRequested onLoginRequested;
    public interface  OnLoginRequested{
        public void loginAnswered(boolean success, Exception errExc);
    }
}
