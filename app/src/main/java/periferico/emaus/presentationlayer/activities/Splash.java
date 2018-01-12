package periferico.emaus.presentationlayer.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import periferico.emaus.BuildConfig;
import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.firebase_objects.VersionesAPK_Firebase;
import periferico.emaus.domainlayer.ws_helpers.APKDownloader;

public class Splash extends AppCompatActivity implements WS.OnLoginRequested, WS.FirebaseObjectRetrieved, OnSuccessListener<Uri> {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    Uri succesUri;

    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 12345;

    private boolean loggedInBOOL;

    // -------------------------------------------- //
    // ---------------- LIFE CYCLE ---------------- //
    //--------------------------------------------- //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        //Seteamos el objeto listener del Firebase Login
        WS.setLoginListener(Splash.this);
        //Obtenemos la instancia de la clase de WebServices
        WS.getInstance(Splash.this);

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

    private void downloadAPK(){
                try{
                    File file = new File(Environment.getExternalStorageDirectory(), "EMAUS.apk");
                    final Uri uri = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) ?
                            FileProvider.getUriForFile(Splash.this, BuildConfig.APPLICATION_ID + ".periferico.emaus.provider", file) :
                            Uri.fromFile(file);

                    //Delete update file if exists
                    //File file = new File(destination);
                    if (file.exists())
                        //file.delete() - test this, I think sometimes it doesnt work
                        file.delete();

                    //get url of app on server
                    String url = succesUri.toString();

                    InputStream input = null;
                    OutputStream output = null;
                    HttpURLConnection connection = null;
                    try {
                        URL sUrl = new URL(url);
                        connection = (HttpURLConnection) sUrl.openConnection();
                        connection.connect();

                        // download the file
                        input = connection.getInputStream();
                        output = new FileOutputStream(file);

                        byte data[] = new byte[4096];
                        int count;
                        while ((count = input.read(data)) != -1) {
                            // allow canceling with back button

                            output.write(data, 0, count);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (output != null)
                                output.close();
                            if (input != null)
                                input.close();
                        } catch (IOException ignored) {
                        }

                        if (connection != null)
                            connection.disconnect();
                    }

                    Intent install = new Intent(Intent.ACTION_INSTALL_PACKAGE)
                            .setData(uri)
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(install);
                }catch(Exception e){e.printStackTrace();}
    }

    // --------------------------------------------------- //
    // ---------------- WS IMPLEMENTATION ---------------- //
    //---------------------------------------------------- //


    @Override
    public void loginAnswered(boolean success, Exception e) {
        FirebaseCrash.log("LoginWSAnswered");
        loggedInBOOL=success;
        if(success){
            askForVersion();
        }
        //delayAndContinue(success);
    }

    @Override
    public void firebaseObjectRetrieved(Object_Firebase objectFirebase) {
        VersionesAPK_Firebase versionesFirebase = (VersionesAPK_Firebase)objectFirebase;
        try{
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;

            boolean isUpdated = versionesFirebase.getUltimaVersion().getVersionName().compareTo(version)==0 && versionesFirebase.getUltimaVersion().getVersionCode()==verCode;
            if(isUpdated){
                Log.d("SplashDebug","IS UPDATED! **********");
                delayAndContinue(loggedInBOOL);
            }else{
                Log.d("SplashDebug","NEEDS UPDATE! ----------");
                WS.downloadAPK(Splash.this, versionesFirebase.getUltimaVersion().getNombreAPK(), Splash.this);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        Log.d("VersionesDEBUG","versionesFirebase name="+versionesFirebase.getUltimaVersion().getVersionName()+" code="+versionesFirebase.getUltimaVersion().getVersionCode());
    }

    @Override
    public void onSuccess(Uri uri) {

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

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(Splash.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
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

                    APKDownloader apkDownloader = new APKDownloader();
                    apkDownloader.setSuccesUri(succesUri);
                    apkDownloader.execute(Splash.this);

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
