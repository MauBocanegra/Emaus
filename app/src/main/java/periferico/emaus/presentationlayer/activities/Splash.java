package periferico.emaus.presentationlayer.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;

public class Splash extends AppCompatActivity implements WS.OnLoginRequested{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    // -------------------------------------------- //
    // ---------------- LIFE CYCLE ---------------- //
    //--------------------------------------------- //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Obtenemos la instancia de la clase de WebServices
        WS.getInstance(Splash.this);
        //Seteamos el objeto listener del Firebase Login
        WS.setLoginListener(Splash.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        //Cuando empiece la actividad activar el listener
        WS.addFirebaseAuthStateListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            //Cuando termine la actividad desactivar el listener
            WS.removeFirebaseAuthStateListener();
        }
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    private void delayAndContinue(final boolean loggedIn){
        /**
         * Fragmento de codigo que cuenta 2 segundos para mostrar el logo y despues verifica el estado de la sesion
         */
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Si no esta loggeado lanzar la actividad de Login
                if(!loggedIn) {
                    //Iniciar actividad de Login
                    Intent intent = new Intent(Splash.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1500);
    }

    // --------------------------------------------------- //
    // ---------------- WS IMPLEMENTATION ---------------- //
    //---------------------------------------------------- //


    @Override
    public void loginAnswered(boolean success) {
        delayAndContinue(success);
    }
}
