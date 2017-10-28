package periferico.emaus.presentationlayer.activities;

import android.content.Context;
import android.graphics.Color;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Seteamos al escucha
        WS.setLoginListener(Login.this);

        //Implementamos el listener de los toques para los botones
        findViewById(R.id.buttonLogin).setOnClickListener(this);

        //Instanciamos las variables que se usaran
        textInputUsuario = (TextInputLayout) findViewById(R.id.textInputUsuario);
        textInputContra = (TextInputLayout) findViewById(R.id.textInputContra);
        editTextUsuario = (EditText) findViewById(R.id.editTextUsuario);
        editTextContra = (EditText) findViewById(R.id.editTextContra);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
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
                    WS.tryUserLogin(stringUsuario, stringContra);
                }
            }
        }
    }

    @Override
    public void loginAnswered(boolean success) {
        progressBar.setVisibility(View.GONE);
        //Si el loggeo fue exitoso
        if(success){

        }
        //Si el loggeo NO fue exitoso
        else{
            Snackbar snackbar = Snackbar.make((CoordinatorLayout) findViewById(R.id.coordinatorLayout),getString(R.string.login_error_login), Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(Login.this, R.color.colorAccent));
            TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.BLACK);
            snackbar.show();
        }
    }
}
