package periferico.emaus.presentationlayer.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.crash.FirebaseCrash;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;

public class Login extends AppCompatActivity implements
        View.OnClickListener,
        WS.OnLoginRequested{

    private TextInputLayout textInputUsuario;
    private TextInputLayout textInputContra;
    private EditText editTextUsuario;
    private EditText editTextContra;
    private ProgressBar progressBar;

    // -------------------------------------------- //
    // ---------------- LIFE CYCLE ---------------- //
    // -------------------------------------------- //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseCrash.log("LoginCreated");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Seteamos al escucha
        WS.setLoginListener(Login.this);

        //Implementamos el listener de los toques para los botones
        findViewById(R.id.buttonLogin).setOnClickListener(this);

        //Instanciamos las variables que se usaran
        textInputUsuario = findViewById(R.id.textInputUsuario);
        textInputContra = findViewById(R.id.textInputContra);
        editTextUsuario = findViewById(R.id.editTextUsuario);
        editTextContra = findViewById(R.id.editTextContra);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    /**
     * Metodo que esconde el teclado de manera programatica
     */
    private void hideKeyboard(){
        try {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }catch(Exception e){e.printStackTrace();}
    }

    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    // ----------------------------------------------------------- //
    // ---------------- INTERFACES IMPLEMENTATION ---------------- //
    //------------------------------------------------------------ //

    @Override
    public void onClick(View view) {
        hideKeyboard();

        int errorCount=0;

        switch (view.getId()){
            /**
             * Para el flujo del boton de login, primero checamos que los campos no sean vacios, de lo contrario muestra un mensaje en los respectivos inputLayouts
             */
            case R.id.buttonLogin:{

                if( !isGooglePlayServicesAvailable(Login.this)){
                    break;
                }

                //instanciamos las cadenas que se obtienen de los campos
                String stringUsuario = editTextUsuario.getText().toString();
                String stringContra = editTextContra.getText().toString();

                //Si el campo de usuario esta vacio muestra el error en el inputLayout
                if(stringUsuario.isEmpty()){
                    textInputUsuario.setError(getString(R.string.login_error_usuario_vacio));
                    errorCount++;
                }else{
                    textInputUsuario.setError(null);
                }

                //Si el campo de contra esta vacio muestra el error en el inputLayout
                if(stringContra.isEmpty()){
                    textInputContra.setError(getString(R.string.login_error_contra_vacia));
                    errorCount++;
                }else{
                    textInputContra.setError(null);
                }

                //Si los campos no estan vacios intenta hacer el login mediante Firebase Authentication
                if(errorCount==0){
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseCrash.log("LoginTry");
                    WS.tryUserLogin(stringUsuario, stringContra);
                }
            }
        }
    }

    @Override
    public void loginAnswered(boolean success, Exception e) {
        progressBar.setVisibility(View.GONE);
        //Si el loggeo fue exitoso
        if(success){

            FirebaseCrash.log("LoginAnswerSucces");

            //muestra el snackbar de exito
            Snackbar snackbar = Snackbar.make((CoordinatorLayout) findViewById(R.id.coordinatorLayout),getString(R.string.login_success), Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(Login.this, R.color.colorAccent));
            TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.BLACK);
            snackbar.show();

            /**
             * Fragmento de codigo que cuenta 1 segundos para mantener el Snackbar de exito y despues muestra la pantalla de menu
             */
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Iniciar actividad de Menu

                    Intent intent = new Intent(Login.this, Splash.class);
                    startActivity(intent);
                    finish();
                    /*
                    Intent intent = new Intent(Login.this, MainTabs.class);
                    startActivity(intent);
                    finish();
                    */
                }
            }, 1000);
        }
        //Si el loggeo NO fue exitoso
        else{
            FirebaseCrash.log("LoginAnswerFail");
            Snackbar snackbar = Snackbar.make((CoordinatorLayout) findViewById(R.id.coordinatorLayout),e.getMessage(), Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(Login.this, R.color.colorAccent));
            TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.BLACK);
            snackbar.show();
        }
    }

    private void showError(String error){
        Snackbar snackbar = Snackbar.make((CoordinatorLayout) findViewById(R.id.coordinatorLayout),error, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(Login.this, R.color.colorAccent));
        TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.BLACK);
        snackbar.show();
    }
}
