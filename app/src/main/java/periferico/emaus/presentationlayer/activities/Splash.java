package periferico.emaus.presentationlayer.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;

import periferico.emaus.BuildConfig;
import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.firebase_objects.UserType_Firebase;
import periferico.emaus.domainlayer.firebase_objects.VersionesAPK_Firebase;
import periferico.emaus.domainlayer.ws_helpers.APKDownloader;

public class Splash extends AppCompatActivity implements WS.OnLoginRequested, WS.FirebaseObjectRetrievedListener, OnSuccessListener<Uri> {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private static final int LogIN_ONLINE = 0;
    private static final int LogIN_OFFLINE = 1;
    private static final int LogOUT_ONLINE = 2;
    private static final int LogOUT_OFFLINE = 3;

    Uri succesUri;

    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 12345;
    private static final String TAG="SplashDebug";
    private static VersionesAPK_Firebase versionesFirebase;

    private boolean loggedInBOOL;

    SharedPreferences prefs;
    //Boolean alreadyUpdated;
    long lastOnlineUpdate;
    boolean forceUpdate;
    private static long dayInMiliseconds = 86400000;

    // -------------------------------------------- //
    // ---------------- LIFE CYCLE ---------------- //
    //--------------------------------------------- //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try{
            TextView tvVersion = findViewById(R.id.textviewVersion);
            TextView tvCodigo = findViewById(R.id.textviewCodigo);
            TextView tvAmbiente = findViewById(R.id.textviewAmbiente);
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;

            tvVersion.setText("Ver: "+version);
            tvCodigo.setText("Cod: "+verCode+" Rev: "+BuildConfig.revision);
            tvAmbiente.setText("Ambiente "+(getResources().getString(R.string.firebase_database_url).equals("https://fir-emaus.firebaseio.com") ? "Productivo" : "Desarrollo"));
        }catch(Exception e){e.printStackTrace();}


        //Seteamos el objeto listener del Firebase Login
        WS.setLoginListener(Splash.this);
        //Obtenemos la instancia de la clase de WebServices
        WS.getInstance(Splash.this, forceUpdate);

        FirebaseCrash.log("SplashCreated");
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

    private void manageUserType() {

        WS.readUserType(Splash.this, new WS.FirebaseObjectRetrievedListener() {
            @Override
            public void firebaseObjectRetrieved(Object_Firebase objectFirebase) {
                Log.d("","");
                if(objectFirebase==null){

                    WS.showAlert(
                            Splash.this,
                            getString(R.string.alerts_sinsegmento_titulo),
                            getString(R.string.alerts_sinsegmento_texto),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mAuth = FirebaseAuth.getInstance();
                                    mAuth.signOut();

                                    Intent intent = new Intent(Splash.this, Login.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, false
                    );
                }else{
                    boolean login = false;
                    //String segmento = ((User_Firebase)objectFirebase).getSegmento();

                    switch( (Splash.this).getString(R.string.flavor_string) ){
                        case "Cobranza":{
                            if( ((UserType_Firebase)objectFirebase).isCobranza() ){
                                delayAndContinue(true);
                                login=true;
                            }else{login=false;}
                            break;
                        }

                        case "Ventas":{
                            if( ((UserType_Firebase)objectFirebase).isVentas() ){
                                delayAndContinue(true);
                                login=true;
                            }else{login=false;}
                            break;
                        }
                    }

                    if(!login){
                        WS.showAlert(
                                Splash.this,
                                getString(R.string.alerta_segmentoerroneo_titulo),
                                getString(R.string.alerta_segmentoerroneo_texto),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mAuth = FirebaseAuth.getInstance();
                                        mAuth.signOut();

                                        Intent intent = new Intent(Splash.this, Login.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, false
                        );
                    }



                    /*

                    if( ((Splash.this).getString(R.string.flavor_string).equals("Cobranza")) && ((User_Firebase)objectFirebase).isCobranza() ){

                    }



                    Log.d(TAG,"segmentoObtenido="+segmento);
                    switch(segmento){
                        case "omni":{
                            delayAndContinue(true);
                            break;
                        }

                        case "ventas":{
                            //App[Ventas] y usuario[Ventas]
                            if((Splash.this).getString(R.string.flavor_string).equals("Ventas")){
                                WS.setKeyUser("vendedores");
                                delayAndContinue(true);
                                login=true;
                            }
                            else{login = false;}
                            break;
                        }

                        case "cobranza":{
                            //usuario[Cobranza] y app[Cobranza]
                            if((Splash.this).getString(R.string.flavor_string).equals("Cobranza")){
                                WS.setKeyUser("cobranza");
                                delayAndContinue(true);
                                login=true;
                            }
                            else{login = false;}
                            break;
                        }
                    }

                    if(!login){
                        WS.showAlert(
                                Splash.this,
                                getString(R.string.alerta_segmentoerroneo_titulo),
                                getString(R.string.alerta_segmentoerroneo_texto),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mAuth = FirebaseAuth.getInstance();
                                        mAuth.signOut();

                                        Intent intent = new Intent(Splash.this, Login.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                        );
                    }
                    */
                }
            }
        });

        //Significa que el usuario no tiene asignado un segmento

    }

    private void delayAndContinue(final boolean loggedIn){

        //Fragmento de codigo que cuenta 2 segundos para mostrar el logo y despues verifica el estado de la sesion
        final Handler handler = new Handler();
        if(loggedIn){
            TextView tvUsuario = findViewById(R.id.textViewUsuario);
            tvUsuario.setText("Usuario: "+WS.getCurrentUser().getEmail().split("@")[0].replace(".",""));
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Si no esta loggeado lanzar la actividad de Login
                if(!loggedIn) {
                    //Iniciar actividad de Login

                    Intent intent = new Intent(Splash.this, Login.class);
                    startActivity(intent);
                    finish();
                }else{
                    //Iniciar actividad de Menu

                    //LOGOUT

                    /*
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    */

                    Intent intent = new Intent(Splash.this, MainTabs.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1500);
    }

    private void askForVersion(){
        WS.readVersionesAPKFirebase(Splash.this);
    }


    // --------------------------------------------------- //
    // ---------------- WS IMPLEMENTATION ---------------- //
    //---------------------------------------------------- //


    @Override
    public void loginAnswered(final boolean success, Exception e) {
        FirebaseCrash.log("LoginWSAnswered");

        loggedInBOOL=success;
        boolean hasInternet = WS.hasInternet(Splash.this);

        //SI EL LOGGEO ES EXITOSO


        int estado =
                loggedInBOOL&&hasInternet ? LogIN_ONLINE :
                loggedInBOOL&&!hasInternet ? LogIN_OFFLINE :
                !loggedInBOOL&&hasInternet ? LogOUT_ONLINE :
                                            LogOUT_OFFLINE;



        switch (estado){
            case LogIN_ONLINE:{
                askForVersion();
                break;
            }
            case LogIN_OFFLINE:{
                WS.showAlert(
                        Splash.this,
                        getString(R.string.alerts_sinconexion_titulo),
                        getString(R.string.alerts_sinconexion_mensaje),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                delayAndContinue(success);
                            }
                        }, false
                );
                break;
            }
            case LogOUT_ONLINE:{
                delayAndContinue(success);
                break;
            }
            case LogOUT_OFFLINE:{
                WS.showAlert(
                        Splash.this,
                        getString(R.string.alerts_sinconexion_titulo),
                        getString(R.string.alerts_sinconexion_mensaje),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }, false
                );
                break;
            }
        }


    }

    @Override
    public void firebaseObjectRetrieved(Object_Firebase objectFirebase) {

        if(objectFirebase==null){
            Toast.makeText(Splash.this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
            return;
        }

        versionesFirebase = (VersionesAPK_Firebase)objectFirebase;
        try{
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;

            Log.d(TAG,"versionRetrieved="+versionesFirebase.getUltimaVersion().getVersionName());



            boolean isUpdated = versionesFirebase.getUltimaVersion().getVersionName().compareTo(version)==0 && versionesFirebase.getUltimaVersion().getVersionCode()==verCode;
            if(isUpdated){
                Log.d("SplashDebug","IS UPDATED! **********");
                manageUserType();
                //delayAndContinue(loggedInBOOL);
            }else{

                Log.d("SplashDebug","NEEDS UPDATE! ----------");
                WS.downloadAPKLink(Splash.this, versionesFirebase.getUltimaVersion().getNombreAPK(), Splash.this, versionesFirebase.getUltimaVersion().getLinkDescarga());
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        Log.d("VersionesDebug","versionesFirebase name="+versionesFirebase.getUltimaVersion().getVersionName()+" code="+versionesFirebase.getUltimaVersion().getVersionCode());
    }

    @Override
    public void onSuccess(Uri uri) {

        Log.d(TAG, "SuccededDownloadURI for APK");

        succesUri=uri;

        if (ContextCompat.checkSelfPermission(Splash.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Splash.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                Log.d(TAG, "Explanation NEEDED ASK FOR PERMISSION");

            } else {

                // No explanation needed, we can request the permission.

                Log.d(TAG, "NO EXP, ASK PERMISSION");

                ActivityCompat.requestPermissions(Splash.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{

            Log.d(TAG, "DownloaderTask - APK");
            /*
            if(versionesFirebase==null){Log.e(TAG,"versiones == NULL"); return;}
            WS.downloadAPKLink(Splash.this, versionesFirebase.getUltimaVersion().getNombreAPK(), Splash.this, versionesFirebase.getUltimaVersion().getLinkDescarga());
            */

            APKDownloader apkDownloader = new APKDownloader();
            apkDownloader.setSuccesUri(succesUri);
            apkDownloader.execute(Splash.this);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, "AfterPermissionGranted, DownloaderTask - APK");

                    APKDownloader apkDownloader = new APKDownloader();
                    apkDownloader.setSuccesUri(succesUri);
                    apkDownloader.execute(Splash.this);

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "PERMISSION DENIED!");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
