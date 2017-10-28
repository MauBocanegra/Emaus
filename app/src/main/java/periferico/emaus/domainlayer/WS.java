package periferico.emaus.domainlayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by maubocanegra on 27/10/17.
 */

public class WS {
    private static WS instance;
    private static Context context;

    private static FirebaseAuth mAuth;
    private static FirebaseUser currentUser;
    private static FirebaseAuth.AuthStateListener mAuthStateListener;

    private static boolean loggedIn;

    /**
     * Metodo que instancia el singleton de los WebServices (Firebase)
     * @param c que es el contexto con el cual se hará la instancia
     * @return Regresa la instancia del
     */
    public synchronized static WS getInstance(Context c){
        if(instance==null){
            instance = new WS();
            context=c;

            if(mAuth==null)
            mAuth = FirebaseAuth.getInstance();

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

                    } else {
                        // User is signed out
                        Log.d("FirebaseDebug", "onAuthStateChanged:signed_out");
                        loggedIn=false;
                    }
                    onLoginRequested.loginAnswered(loggedIn);
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
                            onLoginRequested.loginAnswered(false);
                        }
                    }
                });
    }

    // --------------------------------------------------- //
    // ---------------- Listeners ?? ---------------- //
    //---------------------------------------------------- //

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
     * Declaracion de la interfaz que se encargara de la comunicacion entre WS y la clase que lo implemente
     */
    public static OnLoginRequested onLoginRequested;
    public interface  OnLoginRequested{
        public void loginAnswered(boolean success);
    }
}
