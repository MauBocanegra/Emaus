package periferico.emaus.presentationlayer.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kbeanie.multipicker.api.CacheLocation;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.firebase_objects.Plan_Firebase;
import periferico.emaus.domainlayer.utils.AppCompatActivity_Job;
import periferico.emaus.presentationlayer.dialogs.ProgressDialogFragment;

public class NuevoPlan extends AppCompatActivity_Job implements
        ImagePickerCallback,
        ViewTreeObserver.OnScrollChangedListener,
        View.OnClickListener,
        Spinner.OnItemSelectedListener,
        Switch.OnCheckedChangeListener,
        OnFailureListener, OnSuccessListener<UploadTask.TaskSnapshot>,
        WS.FirebaseCompletionListener,
        WS.OnNetworkListener{

    private static final String TAG = "NuevoPlanDebug";

    private static final int FRONTAL_REQUESTED = 1;
    private static final int REVERSO_REQUESTED = 2;
    private static final int COMPROBANTE_REQUESTED = 3;

    private static final int PLAN_BASICO = 0;
    private static final int PLAN_LUJO = 1;
    private static final int PLAN_MADERA = 2;

    ImagePicker imagePicker;
    CameraImagePicker cameraPicker;
    BottomSheetBehavior bottomSheetBehavior;

    Spinner spinnerTipoPlan;
    Spinner spinnerModeloAtaud;
    Spinner spinnerServicio;
    Spinner spinnerFinanciamiento;
    Spinner spinnerPagos;
    TextView textviewMonto;
    Spinner spinnerFormaPago;
    TextInputLayout inputLayoutAnticipo;
    TextInputEditText editTextAnticipo;
    Switch switchFactura;
    TextInputLayout inputLayoutRFC;
    TextInputEditText editTextRFC;
    TextInputLayout inputLayoutEmail;
    TextInputEditText editTextEmail;
    CardView cardviewFrontal;
    CardView cardviewReverso;
    CardView cardviewComprobante;
    View viewCamara1;
    View viewCamara2;
    View viewGaleria1;
    View viewGaleria2;
    ImageView imgFrontal;
    ImageView imgReverso;
    ImageView imgComprobante;
    ProgressBar progressFrontal;
    ProgressBar progressReverso;
    ProgressBar progressComprobante;
    Button buttonNuevoplan;
    ScrollView scrollView;
    TextView bannerNetwork;

    FirebaseStorage storage;
    StorageReference storageRef;

    ProgressDialogFragment progressDialogFragment;

    private int img_requested;
    private String pickerPath;
    private String frontalURL;
    private String reverseURL;
    private String comprobanteURL;
    ChosenImage chosenImageFrontal;
    ChosenImage chosenImageReverse;
    ChosenImage chosenImageComprobante;;
    private String nombreFrontal;
    private String nombreReverso;
    private String nombreComprobante;
    //kut ghyr mahdud sabar
    //kut veir majduden sobwer

    private String[][] planes;
    private int[][][][] precios;
    private int [][][][] montos;
    private int plan; private int ataud; private int servicio;
    private int financiamiento; private int pago; private int formaPago;
    private String stID;

    private int anticipo;
    private String RFC; private String email;

    @Override
    protected void onStart() {
        super.onStart();
        WS.setNetworkListener(NuevoPlan.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        FirebaseCrash.log("NuevoPlan_ActivityCreated");

        setContentView(R.layout.activity_nuevo_plan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Instanciamos todos los objetos que necesitaremos
        instanciateObjects();

        //Seteamos todos los escuchas
        setListeners();

        //Impedimos que el teclado salga de primera instancia al cargar la pantalla
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Seteamos los policies para los threads
        setPolicies();

        //ponemos las cosas como debe empezar
        setInitialState();
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    private void instanciateObjects(){
        bannerNetwork = findViewById(R.id.nuevoplan_banner_network);
        spinnerTipoPlan = findViewById(R.id.nuevoplan_spinner_tipoplan);
        spinnerModeloAtaud = findViewById(R.id.nuevoplan_spinner_ataud);
        spinnerServicio = findViewById(R.id.nuevoplan_spinner_servicio);
        spinnerFinanciamiento = findViewById(R.id.nuevoplan_spinner_financiamiento);
        spinnerPagos = findViewById(R.id.nuevoplan_spinner_pagos);
        textviewMonto = findViewById(R.id.nuevoplan_textview_monto);
        spinnerFormaPago = findViewById(R.id.nuevoplan_spinner_formapago);
        inputLayoutAnticipo = findViewById(R.id.nuevoplan_inputlayou_anticipo);
        editTextAnticipo = findViewById(R.id.nuevoplan_edittext_anticipo);
        switchFactura = findViewById(R.id.nuevoplan_switch_facturacion);
        inputLayoutRFC = findViewById(R.id.nuevoplan_inputlayout_rfc);
        editTextRFC = findViewById(R.id.nuevoplan_edittext_rfc);
        inputLayoutEmail = findViewById(R.id.nuevoplan_inputlayout_emailrfc);
        editTextEmail = findViewById(R.id.nuevoplan_edittext_emailrfc);
        cardviewFrontal = findViewById(R.id.cardview_tarjetafrente);
        cardviewReverso = findViewById(R.id.cardview_tarjetareverso);
        cardviewComprobante = findViewById(R.id.cardview_comprobante);
        scrollView = findViewById(R.id.scrollview);
        viewCamara1 = findViewById(R.id.bottomsheetlayout_cam1);
        viewCamara2 = findViewById(R.id.bottomsheetlayout_cam2);
        viewGaleria1 = findViewById(R.id.bottomsheetlayout_galeria1);
        viewGaleria2 = findViewById(R.id.bottomsheetlayout_galeria2);
        imgFrontal = findViewById(R.id.nuevoplan_img_frontal);
        imgReverso = findViewById(R.id.nuevoplan_img_reverso);
        imgComprobante = findViewById(R.id.nuevoplan_img_comprobante);
        progressFrontal = findViewById(R.id.nuevocliente_progress_frontal);
        progressReverso = findViewById(R.id.nuevocliente_progress_reverso);
        progressComprobante = findViewById(R.id.nuevocliente_progress_comprobante);
        buttonNuevoplan = (findViewById(R.id.nuevoplan_button_nuevoplan));

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomsheetlayout_img_picker));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        imagePicker = new ImagePicker(NuevoPlan.this);
        imagePicker.setImagePickerCallback(NuevoPlan.this);
        imagePicker.allowMultiple();

        planes = new String[][]{
                getResources().getStringArray(R.array.nuevoplan_array_planbasico),
                getResources().getStringArray(R.array.nuevoplan_array_planLujo),
                getResources().getStringArray(R.array.nuevoplan_array_planmadera)
        };

        plan=-1; ataud=-1; servicio=-1; financiamiento=-1;pago=-1; formaPago=-1;

        montos = new int[][][][]{
            /*basicos*/
            new int[][][]{
                /*egipcio*/
                new int[][]{
                    /*contado*/
                    new int[]{/*domicilio*/6421,/*capilla*/9790},
                    /*financiado*/
                    new int[]{/*domicilio*/9000,/*capilla*/13752},},
                /*faraon*/
                new int[][]{
                    //contado
                    new int[]{/*domicilio*/7800,/*capilla*/11064},
                    //financiado
                    new int[]{/*domicilio*/10921,/*capilla*/15536},},
                //estandar
                new int[][]{
                    //contado
                    new int[]{/*domicilio*/12035,/*capilla*/15642},
                    //financiado
                    new int[]{/*domicilio*/16864,/*capilla*/21905},}},
            //lujo
            new int[][][]{
                //lujo estandar
                new int[][]{
                    //contado
                    new int[]{/*domicilio*/15250,/*capilla*/18900},
                    //financiado
                    new int[]{/*domicilio*/21387,/*capilla*/26499},},
                //lujo premium
                new int[][]{
                    //contado
                    new int[]{/*domicilio*/16214,/*capilla*/23035},
                    //financiado
                    new int[]{/*domicilio*/25534,/*capilla*/32279},}},
            //madera
            new int[][][]{
                //esmeralda
                new int[][]{
                    //contado
                    new int[]{/*domicilio*/21380,/*capilla*/24642},
                    //financiado
                    new int[]{/*domicilio*/30036,/*capilla*/34509},},
                //lujo
                new int[][]{
                    //contado
                    new int[]{/*domicilio*/27950,/*capilla*/33214},
                    //financiado
                    new int[]{/*domicilio*/39153,/*capilla*/46537},
        }}};
    }

    private void setInitialState(){
        spinnerModeloAtaud.setEnabled(false);
        spinnerServicio.setEnabled(false);
        spinnerFinanciamiento.setEnabled(false);
        spinnerPagos.setEnabled(false);

        inputLayoutRFC.setEnabled(false);
        editTextRFC.setEnabled(false);
        inputLayoutEmail.setEnabled(false);
        editTextEmail.setEnabled(false);
    }

    private void setListeners(){
        scrollView.getViewTreeObserver().addOnScrollChangedListener(this);
        cardviewFrontal.setOnClickListener(this);
        cardviewReverso.setOnClickListener(this);
        cardviewComprobante.setOnClickListener(this);
        viewCamara1.setOnClickListener(this);
        viewCamara2.setOnClickListener(this);
        viewGaleria1.setOnClickListener(this);
        viewGaleria2.setOnClickListener(this);
        buttonNuevoplan.setOnClickListener(this);

        spinnerTipoPlan.setOnItemSelectedListener(this);
        spinnerFormaPago.setOnItemSelectedListener(this);
        spinnerFinanciamiento.setOnItemSelectedListener(this);
        spinnerPagos.setOnItemSelectedListener(this);
        spinnerServicio.setOnItemSelectedListener(this);
        spinnerModeloAtaud.setOnItemSelectedListener(this);
        switchFactura.setOnCheckedChangeListener(this);
    }

    private void setPolicies(){
        StrictMode.setThreadPolicy (new StrictMode.ThreadPolicy.Builder ()
                .detectDiskReads ()
                .detectDiskWrites ()
                .detectNetwork ()
                .detectAll ()// or .detectAll() for all detectable problems
                .penaltyLog ()
                .build ());
        StrictMode.setVmPolicy (new StrictMode.VmPolicy.Builder ()
                .detectLeakedSqlLiteObjects ()
                .detectLeakedClosableObjects ()
                .penaltyLog ()
                .penaltyDeath ()
                .build ());
    }

    private void pickImageSingle() {
        imagePicker = new ImagePicker(this);
        imagePicker.setFolderName("Random");
        imagePicker.setRequestId(1234);
        imagePicker.ensureMaxSize(500, 500);
        imagePicker.shouldGenerateMetadata(true);
        imagePicker.shouldGenerateThumbnails(true);
        imagePicker.setImagePickerCallback(this);
        Bundle bundle = new Bundle();
        bundle.putInt("android.intent.extras.CAMERA_FACING", 1);
        imagePicker.pickImage();
    }

    private void takePicture() {
        cameraPicker = new CameraImagePicker(this);
        cameraPicker.setDebugglable(true);
        cameraPicker.setCacheLocation(CacheLocation.EXTERNAL_STORAGE_APP_DIR);
        cameraPicker.setImagePickerCallback(this);
        cameraPicker.shouldGenerateMetadata(true);
        cameraPicker.shouldGenerateThumbnails(true);
        pickerPath = cameraPicker.pickImage();
    }

    private void generateAndShowMonto(){
        Log.d(TAG,"plan="+plan+" ataud="+ataud+" servicio="+servicio+" financ="+financiamiento);
        if(plan>=0 && ataud>=0 && servicio>=0 && financiamiento>=0){
            textviewMonto.setText(getString(R.string.nuevoplan_formatted_monto,NumberFormat.getNumberInstance(Locale.US).format(
                    montos[plan][ataud][(financiamiento>0 ? 1 : 0)][servicio])));
        }
    }

    private void checkForComplete(){

        if(WS.hasInternet(NuevoPlan.this)) {

            if (plan >= 0 && ataud >= 0 && servicio >= 0 && financiamiento >= 0 && pago >= 0 && formaPago >= 0 && chosenImageFrontal != null && chosenImageReverse != null && chosenImageComprobante != null) {
                String stAnticipo = editTextAnticipo.getEditableText().toString();
                if (stAnticipo.isEmpty()) {
                    anticipo = 0;
                } else {
                    anticipo = Integer.parseInt(editTextAnticipo.getEditableText().toString());
                }
                buttonNuevoplan.setVisibility(View.VISIBLE);
            }
        }else{
            if (plan >= 0 && ataud >= 0 && servicio >= 0 && financiamiento >= 0 && pago >= 0 && formaPago >= 0) {
                String stAnticipo = editTextAnticipo.getEditableText().toString();
                if (stAnticipo.isEmpty()) {
                    anticipo = 0;
                } else {
                    anticipo = Integer.parseInt(editTextAnticipo.getEditableText().toString());
                }
                buttonNuevoplan.setVisibility(View.VISIBLE);
            }
        }
    }

    private void uploadAllImages(){
        storage = FirebaseStorage.getInstance(getString(R.string.FirebaseStorageBucket));
        storageRef = storage.getReference();

        uploadImageFirebase(imgFrontal, FRONTAL_REQUESTED);
        uploadImageFirebase(imgReverso, REVERSO_REQUESTED);
        uploadImageFirebase(imgComprobante, COMPROBANTE_REQUESTED);
    }

    private void finalCreationPlanFirebase(){

        if(WS.hasInternet(NuevoPlan.this)) {
            if (frontalURL == null || reverseURL == null || comprobanteURL == null) {
                Log.d(TAG, "************* CHECK DE URLS FAILED **********");
                return;
            }
        }

        progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.setCancelable(false);
        progressDialogFragment.title="Creando plan nuevo...";
        progressDialogFragment.show(getSupportFragmentManager(), TAG);

        Plan_Firebase planFirebase = new Plan_Firebase();
        planFirebase.setIntPlan(plan);
        planFirebase.setIntAtaud(ataud);
        planFirebase.setIntServicio(servicio);
        planFirebase.setIntFinanciamiento(financiamiento);
        planFirebase.setIntFrecuenciaPagos(pago);
        planFirebase.setIntFormaPago(formaPago);
        planFirebase.setIntMonto(montos[plan][ataud][(financiamiento>0 ? 1 : 0)][servicio]);
        planFirebase.setIntAnticipo(anticipo);

        planFirebase.setBoolFacturacion(switchFactura.isChecked());

        if(switchFactura.isChecked()){
            planFirebase.setStRFC(editTextRFC.getEditableText().toString());
            planFirebase.setStEmailFacturacion(editTextEmail.getEditableText().toString());
        }
        else{
            planFirebase.setStRFC("");
            planFirebase.setStEmailFacturacion("");
        }

        planFirebase.setLinkINEFrontal(frontalURL);
        planFirebase.setLinkINEReverso(reverseURL);
        planFirebase.setLinkComprobante(comprobanteURL);

        planFirebase.setStID(stID);
        planFirebase.setStCliente(getIntent().getStringExtra("stID"));

        FirebaseCrash.log("NuevoPlan_TRY_CrearPlan");

        WS.writePlanFirebase(planFirebase, NuevoPlan.this);
        Log.d(TAG,"PlanFirebase="+planFirebase.toString());

        if(!WS.hasInternet(NuevoPlan.this)){

            final Handler handlerPre = new Handler();
            handlerPre.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Bundle analyticsBundle = new Bundle();
                    analyticsBundle.putString("cliente_id", stID);
                    WS.registerAnalyticsEvent("plan_creado_offline",analyticsBundle);


                    FirebaseCrash.log("NuevoPlan_TRY_PlanCreadoExitoso_OFFLINE");
                    progressDialogFragment.changeTitle("Plan creado SIN CONEXIÓN!");
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialogFragment.dismiss();
                            onBackPressed();
                        }
                    }, 1000);
                }
            },1500);
        }
    }

    private void uploadImageFirebase(ImageView imageView, int imgIDToUpload){

        ChosenImage chosenImg=null;
        String name="";
        switch (imgIDToUpload){
            case FRONTAL_REQUESTED:{
                chosenImg=chosenImageFrontal; name="INEfrontal";
                progressFrontal.setVisibility(View.VISIBLE); break;
            }
            case REVERSO_REQUESTED:{
                chosenImg=chosenImageReverse; name="INEreverso";
                progressReverso.setVisibility(View.VISIBLE); break;
            }
            case COMPROBANTE_REQUESTED:{
                chosenImg=chosenImageComprobante; name="comprobante";
                progressComprobante.setVisibility(View.VISIBLE); break;
            }
        }

        String ref = name+chosenImg.getFileExtensionFromMimeType();
        String fullRef = "comprobantes/"+stID+"/"+ref;

        switch (imgIDToUpload){
            case FRONTAL_REQUESTED:{ nombreFrontal=ref; break; }
            case REVERSO_REQUESTED:{ nombreReverso=ref; break; }
            case COMPROBANTE_REQUESTED:{ nombreComprobante=ref; break; }
        }

        StorageReference imgFullRef = storageRef.child(fullRef);

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imgFullRef.putBytes(data);
        uploadTask.addOnFailureListener(NuevoPlan.this);
        uploadTask.addOnSuccessListener(NuevoPlan.this);

        //Log.d(TAG," ref="+imgFullRef.getName()+" fullRef="+imgFullRef.getPath());
    }

    // ----------------------------------------------------------------- //
    // ---------------- NETWORK LISTENER IMPLEMENTATION ---------------- //
    //------------------------------------------------------------------ //

    @Override
    public void fromOffToOn() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bannerNetwork.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void fromOnToOff() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bannerNetwork.setVisibility(View.INVISIBLE);
            }
        });
    }


    // ------------------------------------------------------------- //
    // ---------------- IMAGE UPLOAD IMPLEMENTATION ---------------- //
    //-------------------------------------------------------------- //

    @Override
    public void onFailure(@NonNull Exception e) {

    }

    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        try {
            /*
            Log.d(TAG, " SUCCESS = " + taskSnapshot.toString());
            Log.d(TAG, " SUCCESS = " + taskSnapshot.getMetadata().toString());
            Log.d(TAG, " SUCCESS = " + taskSnapshot.getMetadata().getName());
            */
            String name=taskSnapshot.getMetadata().getName();

            //Viene de FRONTAL
            if(name.compareTo(nombreFrontal)==0){
                frontalURL = taskSnapshot.getDownloadUrl().toString();
                progressFrontal.setVisibility(View.GONE);
            }
            //Viene de REVERSO
            else if(name.compareTo(nombreReverso)==0){
                reverseURL = taskSnapshot.getDownloadUrl().toString();
                progressReverso.setVisibility(View.GONE);
            }
            //Viene de COMPROBANTE
            else if(name.compareTo(nombreComprobante)==0){
                comprobanteURL = taskSnapshot.getDownloadUrl().toString();
                progressComprobante.setVisibility(View.GONE);
            }

            finalCreationPlanFirebase();

        }catch(Exception e){e.printStackTrace();}
    }

    // ------------------------------------------------------ //
    // ---------------- IMAGE PICKER METHODS ---------------- //
    //------------------------------------------------------- //

    @Override
    public void onImagesChosen(List<ChosenImage> list) {
        for(ChosenImage chosenImage : list){
            switch (img_requested){
                case FRONTAL_REQUESTED:{
                    Picasso.with(NuevoPlan.this).load(chosenImage.getQueryUri()).into(imgFrontal);
                    //frontalURI=chosenImage.getQueryUri();
                    chosenImageFrontal=chosenImage;
                    //uploadImageFirebase(null,FRONTAL_REQUESTED);
                    break;
                }
                case REVERSO_REQUESTED:{
                    Picasso.with(NuevoPlan.this).load(chosenImage.getQueryUri()).into(imgReverso);
                    //reverseURI=chosenImage.getQueryUri();
                    chosenImageReverse=chosenImage;
                    //uploadImageFirebase(null,REVERSO_REQUESTED);
                    break;
                }
                case COMPROBANTE_REQUESTED:{
                    Picasso.with(NuevoPlan.this).load(chosenImage.getQueryUri()).into(imgComprobante);
                    //comprobanteURI=chosenImage.getQueryUri();
                    chosenImageComprobante=chosenImage;
                    //uploadImageFirebase(null,COMPROBANTE_REQUESTED);
                    break;
                }
            }
        }
        checkForComplete();
    }

    @Override
    public void onError(String s) {}

    // ---------------------------------------------------------------- //
    // ---------------- SCROLLVIEW IMPLEMENTED METHODS ---------------- //
    //----------------------------------------------------------------- //

    @Override
    public void onScrollChanged() {
        //Log.d("OnScrollChanged",""+(bottomSheetBehavior!=null)+" "+(bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED));
        if(bottomSheetBehavior!=null && bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    // ------------------------------------------------- //
    // ---------------- ONCLICK METHODS ---------------- //
    //-------------------------------------------------- //

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.cardview_tarjetafrente:{
                if(!WS.hasInternet(NuevoPlan.this)){
                    Toast.makeText(NuevoPlan.this, getString(R.string.toast_noimagenes_sinconexion), Toast.LENGTH_SHORT).show();
                    return;
                }
                img_requested = FRONTAL_REQUESTED;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            }
            case R.id.cardview_tarjetareverso:{
                if(!WS.hasInternet(NuevoPlan.this)){
                    Toast.makeText(NuevoPlan.this, getString(R.string.toast_noimagenes_sinconexion), Toast.LENGTH_SHORT).show();
                    return;
                }
                img_requested = REVERSO_REQUESTED;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            }
            case R.id.cardview_comprobante:{
                if(!WS.hasInternet(NuevoPlan.this)){
                    Toast.makeText(NuevoPlan.this, getString(R.string.toast_noimagenes_sinconexion), Toast.LENGTH_SHORT).show();
                    return;
                }
                img_requested = COMPROBANTE_REQUESTED;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            }
            case R.id.bottomsheetlayout_cam1:{}
            case R.id.bottomsheetlayout_cam2:{
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                takePicture();
                break;
            }
            case R.id.bottomsheetlayout_galeria1:{}
            case R.id.bottomsheetlayout_galeria2:{
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                pickImageSingle();
                break;
            }

            case R.id.nuevoplan_button_nuevoplan:{
                stID = getIntent().getStringExtra("stID")+"_P"+plan+"A"+ataud+"F"+financiamiento+"Pa"+pago+"S"+servicio+"Fp"+formaPago;
                if(WS.hasInternet(NuevoPlan.this)) {
                    uploadAllImages();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(NuevoPlan.this);

                    builder.setMessage("Vas a crear un plan en modo \"Sin conexión\" el cual no permite subir imágenes, por lo tanto, los comprobantes tendrán que subirse manualmente después\n¿Estás de acuerdo?")
                            .setTitle("Planes nuevos SIN CONEXIÓN")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button
                                    finalCreationPlanFirebase();
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                break;
            }
        }
    }

    // ------------------------------------------------------------ //
    // ---------------- SWITCH IMPLEMENTED METHODS ---------------- //
    //------------------------------------------------------------- //

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        inputLayoutRFC.setEnabled(b); editTextRFC.setEnabled(b);
        inputLayoutEmail.setEnabled(b); editTextEmail.setEnabled(b);
        Log.d(TAG,"switchFactura:"+(b?"true":"false"));
    }


    // ------------------------------------------------------------- //
    // ---------------- SPINNER IMPLEMENTED METHODS ---------------- //
    //-------------------------------------------------------------- //

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int selectedInt, long l) {
        switch(adapterView.getId()){
            case R.id.nuevoplan_spinner_tipoplan:{
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        planes[selectedInt]
                );
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerModeloAtaud.setAdapter(spinnerArrayAdapter);
                spinnerModeloAtaud.setEnabled(true);
                spinnerServicio.setEnabled(true);
                spinnerFinanciamiento.setEnabled(true);
                spinnerPagos.setEnabled(true);
                spinnerModeloAtaud.setSelection(-1);
                ataud=-1;
                plan=selectedInt;
                textviewMonto.setText("");
                break;
            }

            case R.id.nuevoplan_spinner_ataud:{ ataud=selectedInt; break;}
            case R.id.nuevoplan_spinner_servicio:{ servicio=selectedInt; break;}
            case R.id.nuevoplan_spinner_financiamiento:{ financiamiento=selectedInt; break;}
            case R.id.nuevoplan_spinner_pagos:{ pago=selectedInt; break;}

            case R.id.nuevoplan_spinner_formapago:{ formaPago=selectedInt; break; }
        }
        checkForComplete();
        generateAndShowMonto();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    // --------------------------------------------------------- //
    // ---------------- ACTIVITY RESULT METHODS ---------------- //
    //---------------------------------------------------------- //

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                if (imagePicker == null) {
                    imagePicker = new ImagePicker(this);
                    imagePicker.setImagePickerCallback(this);
                }
                Log.d(TAG,"preSubmitGaleria");
                imagePicker.submit(data);
            } else if (requestCode == Picker.PICK_IMAGE_CAMERA) {
                if (cameraPicker == null) {
                    cameraPicker = new CameraImagePicker(this);
                    cameraPicker.setImagePickerCallback(this);
                    cameraPicker.reinitialize(pickerPath);
                }
                Log.d(TAG,"preSubmitCam");
                cameraPicker.submit(data);
            }
        }
    }

    // ------------------------------------------------------------ //
    // ---------------- LIFECYCLE ACTIVITY METHODS ---------------- //
    //------------------------------------------------------------- //

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // You have to save path in case your activity is killed.
        // In such a scenario, you will need to re-initialize the CameraImagePicker
        outState.putString("picker_path", pickerPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // After Activity recreate, you need to re-intialize these
        // two values to be able to re-intialize CameraImagePicker
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("picker_path")) {
                pickerPath = savedInstanceState.getString("picker_path");
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    // ----------------------------------------------------------------- //
    // ---------------- FIREBASE IMPLEMENTATION METHODS ---------------- //
    //------------------------------------------------------------------ //


    @Override
    public void firebaseCompleted(boolean hasError) {
        if(hasError){
            //Ocurrio algun error
        }else{
            //Salio bien!
            Bundle analyticsBundle = new Bundle();
            analyticsBundle.putString("cliente_id", stID);
            WS.registerAnalyticsEvent("plan_creado",analyticsBundle);


            FirebaseCrash.log("NuevoPlan_TRY_PlanCreadoExitoso");
            progressDialogFragment.changeTitle("Plan creado exitosamente!");
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialogFragment.dismiss();
                    onBackPressed();
                }
            }, 1500);
        }
    }
}
