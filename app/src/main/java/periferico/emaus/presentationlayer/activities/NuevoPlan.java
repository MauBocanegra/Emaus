package periferico.emaus.presentationlayer.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.firebase_objects.Plan_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.DescuentoPlan_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.DescuentosPlanes_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.FormasPago_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.FrecuenciasPago_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.MatrizPlanes_Firebase;
import periferico.emaus.domainlayer.firebase_objects.ConfiguracionPlanes_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.Financiamientos_Firebase;
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
        //WS.FirebaseArrayRetreivedListener,
        WS.FirebaseObjectRetrievedListener,
        WS.OnNetworkListener{

    private static final String TAG = "NuevoPlanDebug";

    private static final int FRONTAL_REQUESTED = 1;
    private static final int REVERSO_REQUESTED = 2;
    private static final int COMPROBANTE_REQUESTED = 3;
    private static final int DESCUENTO_REQUESTED = 4;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 323;

    private static final int ERROR_PLAN=1;
    private static final int ERROR_COMPROBANTES=2;
    private static final int ERROR_DESCUENTOS=3;
    private static final int ERROR_MESES=4;
    private static final int ERROR_RFC=5;

    ImagePicker imagePicker;
    CameraImagePicker cameraPicker;
    BottomSheetBehavior bottomSheetBehavior;

    //--- Spinners ---//
    Spinner spinnerTipoPlan; Spinner spinnerModeloAtaud; Spinner spinnerServicio;
    Spinner spinnerFinanciamiento; Spinner spinnerFrecuenciaPagos; Spinner spinnerDescuentos;
    Spinner spinnerFormaPago;
    //--- TextViews ---//
    TextView textviewTotalAPagar; TextView textViewDescuento; TextView textviewSaldo;
    TextView textViewMensualidades; TextView textViewErrores;
    //--- InputLayouts ---//
    TextInputLayout inputLayoutAnticipo; TextInputEditText editTextAnticipo;
    TextInputLayout inputLayoutRFC; TextInputEditText editTextRFC;
    TextInputLayout inputLayoutEmail; TextInputEditText editTextEmail;
    //--- Switch ---//
    Switch switchFactura;
    //--- CardViews ---//
    CardView cardviewFrontal; CardView cardviewReverso;
    CardView cardviewComprobante; CardView cardviewDescuento;
    //--- ViewsButtons ---//
    View viewCamara1; View viewCamara2;
    View viewGaleria1; View viewGaleria2;
    //--- ImageViews ---//
    ImageView imgFrontal; ImageView imgReverso;
    ImageView imgComprobante; ImageView imgDescuento;
    //--- Progress ---//
    ProgressBar progressFrontal; ProgressBar progressReverso;
    ProgressBar progressComprobante; ProgressBar progressDescuento;

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
    private String descuentoURL;
    ChosenImage chosenImageFrontal;
    ChosenImage chosenImageReverse;
    ChosenImage chosenImageComprobante;;
    ChosenImage chosenImageDescuento;
    private String nombreFrontal;
    private String nombreReverso;
    private String nombreComprobante;
    private String nombreDescuento;
    //kut ghyr mahdud sabar
    //kut veir majduden sobwer

    private MatrizPlanes_Firebase configPlan;
    private Financiamientos_Firebase financiamientoFirebase;
    private FrecuenciasPago_Firebase frecuenciasPagoFirebase;
    private FormasPago_Firebase formasPagoFirebase;
    private DescuentosPlanes_Firebase descuentosPlanesFirebase;
    private int plan; private int ataud; private int servicio;
    private int financiamiento; private int frecPagos; private int formaPago;
    private float montoFinalActual;

    private int numMesesFinal=-1; private int numPagosFrecFinal=-1;
    private float saldoFinal=1; private float totalAPagarFinal=-1;  private int numPagosFinal=-1;

    private float montoPrevioADescuento=0;
    private int descuento;
    private String stID;
    private boolean spinnerFunctionalityIsLocked=false;

    private int anticipo=0;
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

        WS.readConfiguracionPlanes(NuevoPlan.this);
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
        spinnerFrecuenciaPagos = findViewById(R.id.nuevoplan_spinner_frecuencia_pagos);
        textviewTotalAPagar = findViewById(R.id.nuevoplan_textview_totalapagar);
        textViewDescuento = findViewById(R.id.nuevoplan_label_descuentoaplicado);
        textviewSaldo = findViewById(R.id.nuevoplan_label_saldo);
        textViewMensualidades = findViewById(R.id.nuevoplan_mensualidad);
        textViewErrores = findViewById(R.id.nuevoplan_errores_pendientes);
        spinnerFormaPago = findViewById(R.id.nuevoplan_spinner_formapago);
        spinnerDescuentos = findViewById(R.id.nuevoplan_spinner_descuentos);
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
        cardviewDescuento = findViewById(R.id.cardview_tarjeta_descuento);
        scrollView = findViewById(R.id.scrollview);
        viewCamara1 = findViewById(R.id.bottomsheetlayout_cam1);
        viewCamara2 = findViewById(R.id.bottomsheetlayout_cam2);
        viewGaleria1 = findViewById(R.id.bottomsheetlayout_galeria1);
        viewGaleria2 = findViewById(R.id.bottomsheetlayout_galeria2);
        imgFrontal = findViewById(R.id.nuevoplan_img_frontal);
        imgReverso = findViewById(R.id.nuevoplan_img_reverso);
        imgComprobante = findViewById(R.id.nuevoplan_img_comprobante);
        imgDescuento = findViewById(R.id.nuevoplan_img_tarjeta_descuento);
        progressFrontal = findViewById(R.id.nuevocliente_progress_frontal);
        progressReverso = findViewById(R.id.nuevocliente_progress_reverso);
        progressComprobante = findViewById(R.id.nuevocliente_progress_comprobante);
        progressDescuento = findViewById(R.id.nuevocliente_progress_tarjeta_descuento);
        buttonNuevoplan = (findViewById(R.id.nuevoplan_button_nuevoplan));

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomsheetlayout_img_picker));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        imagePicker = new ImagePicker(NuevoPlan.this);
        imagePicker.setImagePickerCallback(NuevoPlan.this);
        imagePicker.allowMultiple();

        plan=-1; ataud=-1; servicio=-1; financiamiento=-1; frecPagos=-1; formaPago=-1;
    }

    private void setInitialState(){
        spinnerModeloAtaud.setEnabled(false);
        spinnerServicio.setEnabled(false);
        spinnerFinanciamiento.setEnabled(false);

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
        cardviewDescuento.setOnClickListener(this);
        viewCamara1.setOnClickListener(this);
        viewCamara2.setOnClickListener(this);
        viewGaleria1.setOnClickListener(this);
        viewGaleria2.setOnClickListener(this);
        buttonNuevoplan.setOnClickListener(this);

        spinnerTipoPlan.setOnItemSelectedListener(this);
        spinnerFormaPago.setOnItemSelectedListener(this);
        spinnerFinanciamiento.setOnItemSelectedListener(this);
        spinnerFrecuenciaPagos.setOnItemSelectedListener(this);
        spinnerServicio.setOnItemSelectedListener(this);
        spinnerModeloAtaud.setOnItemSelectedListener(this);
        spinnerDescuentos.setOnItemSelectedListener(this);
        switchFactura.setOnCheckedChangeListener(this);
        editTextAnticipo.addTextChangedListener(setTextWatcher());
    }

    private void setConfigInSpinners(){
        if(configPlan!=null){
            ArrayAdapter<String> adapterTipoPlan = new ArrayAdapter<String>(
                    NuevoPlan.this, android.R.layout.simple_spinner_item, configPlan.getTiposPlanStrings());
            adapterTipoPlan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTipoPlan.setAdapter(adapterTipoPlan);
        }
    }

    private void setFormasPagoSpinner(){
        if(formasPagoFirebase!=null){
            ArrayAdapter<String> adapterFormasPago = new ArrayAdapter<String>(
                    NuevoPlan.this, android.R.layout.simple_spinner_item, formasPagoFirebase.getFormasPagoStrings());
            adapterFormasPago.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFormaPago.setAdapter(adapterFormasPago);
        }
    }

    private void setDescuentosSpinner(){
        List<String> adapterDescuentosStrings = new ArrayList<>();
        for (DescuentoPlan_Firebase descuentoItem : descuentosPlanesFirebase.getDescuentos()) {
            adapterDescuentosStrings.add(descuentoItem.getNombre());
        }
        ArrayAdapter<String> adapterDescuentos = new ArrayAdapter<String>(
                NuevoPlan.this, android.R.layout.simple_spinner_item, adapterDescuentosStrings);
        adapterDescuentos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDescuentos.setAdapter(adapterDescuentos);
        spinnerDescuentos.setEnabled(true);
    }

    private void setFrecuenciasPagoSpinner(boolean setEmpty){

        if(!setEmpty){
            ArrayList<String> emptyArray = new ArrayList<>();
            ArrayAdapter<String> adapterFrecuenciasPago = new ArrayAdapter<String>(
                    NuevoPlan.this, android.R.layout.simple_spinner_item, emptyArray);
            adapterFrecuenciasPago.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFrecuenciaPagos.setAdapter(adapterFrecuenciasPago);
            spinnerFrecuenciaPagos.setEnabled(false);
            return;
        }

        if(frecuenciasPagoFirebase!=null){
            ArrayAdapter<String> adapterFrecuenciasPago = new ArrayAdapter<String>(
                    NuevoPlan.this, android.R.layout.simple_spinner_item, frecuenciasPagoFirebase.getFrecuenciasPagoStrings());
            adapterFrecuenciasPago.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFrecuenciaPagos.setAdapter(adapterFrecuenciasPago);
            spinnerFrecuenciaPagos.setEnabled(true);
        }
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

    private void showPendingFields(int pending){

        String error="";
        switch(pending){
            case ERROR_PLAN:{
                error = getString(R.string.nuevoplan_error_plan);
                break;
            }
            case ERROR_COMPROBANTES:{
                error = getString(R.string.nuevoplan_error_comprobantes);
                break;
            }
            case ERROR_DESCUENTOS:{
                error = getString(R.string.nuevoplan_error_descuento);
                break;
            }
            case ERROR_MESES:{
                error = getString(R.string.nuevoplan_error_meses);
                break;
            }
            case ERROR_RFC:{
                error="Ingresa correctamente el RFV y un correo";
                break;
            }
        }
        textViewErrores.setVisibility(View.VISIBLE);
        textViewErrores.setText(error);
    }

    private void noPendingErrors(){
        textViewErrores.setVisibility(View.GONE);
        textViewErrores.setText("");
    }

    private void checkForComplete(){

        boolean hasInternet = WS.hasInternet(NuevoPlan.this);

        if(plan!=-1 && ataud!=-1 && servicio!=-1 && financiamiento!=-1 && formaPago!=-1 && frecPagos!=-1){

            if(switchFactura.isChecked()){
                if(editTextRFC.getEditableText().toString().length()<5 || editTextEmail.getEditableText().toString().length()<5){
                    showPendingFields(ERROR_RFC);
                    buttonNuevoplan.setVisibility(View.GONE);
                }
            }

            if(hasInternet){
                if(chosenImageFrontal!=null && chosenImageReverse!=null && chosenImageComprobante!=null){
                    //Si escogio descuento debe tener foto tarjeta de descuento
                    if(descuento>0 && chosenImageDescuento!=null || descuento==0){
                        noPendingErrors();
                        buttonNuevoplan.setVisibility(View.VISIBLE);
                    }else{
                        showPendingFields(ERROR_DESCUENTOS);
                        buttonNuevoplan.setVisibility(View.GONE);
                    }
                }else{
                    showPendingFields(ERROR_COMPROBANTES);
                    buttonNuevoplan.setVisibility(View.GONE);
                }
                /*
                if(descuento==-1){
                    noPendingErrors();
                    buttonNuevoplan.setVisibility(View.VISIBLE);
                }
                */
            }
            //No tiene internet, puede seguir sin imagenes
            else{
                noPendingErrors();
                buttonNuevoplan.setVisibility(View.VISIBLE);
            }

        }else{
            showPendingFields(ERROR_PLAN);
            buttonNuevoplan.setVisibility(View.GONE);
        }
    }

    private void uploadAllImages(){
        storage = FirebaseStorage.getInstance(getString(R.string.FirebaseStorageBucket));
        storageRef = storage.getReference();

        uploadImageFirebase(imgFrontal, FRONTAL_REQUESTED);
        uploadImageFirebase(imgReverso, REVERSO_REQUESTED);
        uploadImageFirebase(imgComprobante, COMPROBANTE_REQUESTED);

        if(imgDescuento!=null && descuento>0){
            uploadImageFirebase(imgDescuento, DESCUENTO_REQUESTED);
        }
    }

    private void finalCreationPlanFirebase(){

        if(WS.hasInternet(NuevoPlan.this)) {
            if (frontalURL == null || reverseURL == null || comprobanteURL == null) {
                Log.d(TAG, "************* FALTA FrontRevCompr **********");
                return;
            }

            if(descuento>0 && descuentoURL==null){
                Log.d(TAG, "************* FALTA SUBIR DESCUENTO **********");
                return;
            }
        }

        progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.setCancelable(false);
        progressDialogFragment.title="Creando plan nuevo...";
        progressDialogFragment.show(getSupportFragmentManager(), TAG);

        Plan_Firebase planV2_firebase = new Plan_Firebase();

        boolean hasInternet = WS.hasInternet(NuevoPlan.this);

        planV2_firebase.setPlanID(configPlan.getTiposPlan().get(plan).getPlanID());
        planV2_firebase.setAtaudID(configPlan.getTiposPlan().get(plan).getTiposAtaud().get(ataud).getAtaudID());
        planV2_firebase.setServicioID(configPlan.getTiposPlan().get(plan).getTiposAtaud().get(ataud).getTiposServicio().get(servicio).getServicioID());
        planV2_firebase.setFinanciamientoID(financiamientoFirebase.getMensualidades().get(financiamiento).getMensualidadID());
        planV2_firebase.setFrecuenciaPagoID(frecuenciasPagoFirebase.getFrecuenciaspago().get(frecPagos).getFrecuenciaID());
        planV2_firebase.setFormaPagoID(formasPagoFirebase.getFormaspago().get(formaPago).getFormaPagoID());
        planV2_firebase.setNumeroDePagosARealizar(frecuenciasPagoFirebase.getFrecuenciaspago().get(frecPagos).getPagosAlAnio());

        planV2_firebase.setCreadoOffline(hasInternet ? 0 : 1);

        if(descuento>0){
            planV2_firebase.setDescuentoID(descuentosPlanesFirebase.getDescuentos().get(descuento).getDescuentoID());
            if(hasInternet){
                planV2_firebase.setComprobanteDescuentoURL(descuentoURL);
            }
        }

        if(switchFactura.isChecked()){
            planV2_firebase.setBoolFacturacion(true);
            planV2_firebase.setStRFC(editTextRFC.getEditableText().toString());
            planV2_firebase.setStEmailFacturacion(editTextEmail.getEditableText().toString());
        }else{
            planV2_firebase.setBoolFacturacion(false);
        }

        planV2_firebase.setTotalAPagar(Math.round(totalAPagarFinal));
        planV2_firebase.setSaldo(Math.round(saldoFinal));

        if(anticipo!=0 && (anticipo<totalAPagarFinal)){
            planV2_firebase.setAnticipo(Math.round(anticipo));
        }else{
            planV2_firebase.setAnticipo(0);
        }

        if(hasInternet){
            planV2_firebase.setComprobanteINEFrontalURL(frontalURL);
            planV2_firebase.setComprobanteINEReversoURL(reverseURL);
            planV2_firebase.setComprobanteDomicilioURL(comprobanteURL);
        }

        planV2_firebase.setStatus(0);
        //planV2_firebase.setPrioridadEnCobros(0);

        planV2_firebase.setStID(stID);
        planV2_firebase.setStCliente(getIntent().getStringExtra("stID"));

        FirebaseCrash.log("NuevoPlan_TRY_CrearPlan");

        WS.writePlanFirebase(planV2_firebase, NuevoPlan.this);
        Log.d(TAG,"PlanFirebase="+planV2_firebase.toString());

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

    private void calculateShowTexts(){

        // ++++++++++++++++++++++++++++++++++
        // ++ Primero hacemos los calculos ++
        // ++++++++++++++++++++++++++++++++++

        //Si ya escogió hasta financiamiento entonces
        if(plan!=-1 && ataud!=-1 && servicio!=-1 && financiamiento!=-1){
            totalAPagarFinal=configPlan.getTiposPlan().get(plan).getTiposAtaud().get(ataud).getTiposServicio().get(servicio).getCosto();
            totalAPagarFinal*=financiamientoFirebase.getMensualidades().get(financiamiento).getMultiplicadorFinanciamiento();
            numMesesFinal = financiamientoFirebase.getMensualidades().get(financiamiento).getMensualidades();
        }else{
            totalAPagarFinal=-1;
            numMesesFinal=-1;

            return;
        }

        //Si es de contado
        if(financiamientoFirebase.getMensualidades().get(financiamiento).getMensualidadID()==1){
            setFrecuenciasPagoSpinner(false);
            numPagosFrecFinal=1;
        }
        //Si es financiamiento y YA se escogio el financiamiento
        /*
        else if(frecPagos!=-1){
            numPagosFrecFinal = frecuenciasPagoFirebase.getFrecuenciaspago().get(frecPagos).getPagosPorMes();
        }
        */
        //Si no se ha escogido la frecPagos se sale
        //else{return;}

        //Si ya tenemos la frecuencia y los numeros de pagos la calculamos
        if(totalAPagarFinal!=-1 && numMesesFinal!=-1 && frecPagos!=-1){
            /*
            numPagosFinal=numMesesFinal*numPagosFrecFinal;
            //Si es semanal se calculan 52 semanas
            if(frecuenciasPagoFirebase.getFrecuenciaByID(frecPagos).getFrecuenciaID()==1){
                numPagosFinal=(numMesesFinal*52)/12;
            }
            //Si es quincenal se calculan 26 semanas
            if(frecuenciasPagoFirebase.getFrecuenciaByID(frecPagos).getFrecuenciaID()==2){
                numPagosFinal=(numMesesFinal*26)/12;
            }
            */

            numPagosFinal=(numMesesFinal*frecuenciasPagoFirebase.getFrecuenciaspago().get(frecPagos).getPagosAlAnio())/12;
        }

        //Si ya calculamos el total a Pagar y se eligió el descuento, el total a pagar se modifica
        if(totalAPagarFinal!=-1 && descuento!=-1){
            totalAPagarFinal-=((descuentosPlanesFirebase.getDescuentos().get(descuento).getDescuento()*totalAPagarFinal)/100);
        }

        if(anticipo!=0 && anticipo<totalAPagarFinal){
            saldoFinal=totalAPagarFinal-anticipo;
            inputLayoutAnticipo.setError(null);
        }else{
            if(anticipo>=totalAPagarFinal) {
                inputLayoutAnticipo.setError("El anticipo NO puede ser mayor al saldo total");
            }
            saldoFinal=totalAPagarFinal;
        }

        // +++++++++++++++++++++++++++++++++++++
        // ++ Mostramos los datos disponibles ++
        // +++++++++++++++++++++++++++++++++++++

        //Si el total a pagar esta disponible, lo presentamos
        if(totalAPagarFinal!=-1){
            String totalApagarFormateado = NumberFormat.getNumberInstance(Locale.US).format(Math.round(totalAPagarFinal));
            textviewTotalAPagar.setText(String.format( getString(R.string.nuevoplan_formatted_monto),totalApagarFormateado));
        }else{
            textviewTotalAPagar.setText("x");
        }

        //Si el saldo final esta disponible lo presentamos
        if(saldoFinal!=0){
            String saldoFinalFormateado = NumberFormat.getNumberInstance(Locale.US).format(Math.round(saldoFinal));
            textviewSaldo.setText(String.format( getString(R.string.nuevoplan_formatted_monto),saldoFinalFormateado));
        }else{
            textviewSaldo.setText("");
            Log.d(TAG,"");
        }

        boolean esContado = financiamientoFirebase.getMensualidades().get(financiamiento).getMensualidadID()==1;
        //Si tenemos disponible el numPagosFinal, dividimos el total a pagar y lo mostramos
        if(numPagosFinal!=-1 && totalAPagarFinal!=-1 && (esContado || (!esContado && frecPagos!=-1) )){
            String montoFrecuenciaFormateado = NumberFormat.getNumberInstance(Locale.US).format(Math.round(totalAPagarFinal/numPagosFinal));
            textViewMensualidades.setText(String.format(getString(R.string.nuevoplan_format_numeropagos),
                    numPagosFinal,
                    String.format( getString(R.string.nuevoplan_formatted_monto),montoFrecuenciaFormateado)));
        }else{
            textViewMensualidades.setText("");
        }

    }

    private void uploadImageFirebase(ImageView imageView, int imgIDToUpload){

        int planID = configPlan.getTiposPlan().get(plan).getPlanID();
        int ataudID = configPlan.getTiposPlan().get(plan).getTiposAtaud().get(ataud).getAtaudID();
        int servicioID = configPlan.getTiposPlan().get(plan).getTiposAtaud().get(ataud).getTiposServicio().get(servicio).getServicioID();


        int financiamientoID=financiamientoFirebase.getMensualidades().get(financiamiento).getMensualidadID();
        //int financiamientoID = configPlan.getTiposPlan().get(plan).getTiposAtaud().get(ataud).getTiposServicio().get(servicio).getTiposPago().get(financiamiento).getPagoID();


        Random r = new Random();
        int i1 = r.nextInt(1000 - 1) + 1;
        stID = getIntent().getStringExtra("stID")+"_P"+planID+"A"+ataudID+"S"+servicioID+"F"+financiamientoID+"_"+i1;

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
            case DESCUENTO_REQUESTED:{
                chosenImg=chosenImageDescuento; name="descuento";
                progressDescuento.setVisibility(View.VISIBLE); break;
            }
        }

        String ref = name+chosenImg.getFileExtensionFromMimeType();
        String fullRef = "comprobantes/"+stID+"/"+ref;

        switch (imgIDToUpload){
            case FRONTAL_REQUESTED:{ nombreFrontal=ref; break; }
            case REVERSO_REQUESTED:{ nombreReverso=ref; break; }
            case COMPROBANTE_REQUESTED:{ nombreComprobante=ref; break; }
            case DESCUENTO_REQUESTED:{ nombreDescuento=ref; break; }
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

    private void checkforCamPermissions(){
        if (ContextCompat.checkSelfPermission(NuevoPlan.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(NuevoPlan.this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                Log.d(TAG, "Explanation NEEDED, ASK FOR PERMISSION");
                ActivityCompat.requestPermissions(NuevoPlan.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

            } else {

                // No explanation needed, we can request the permission.

                Log.d(TAG, "NO EXP, ASK PERMISSION");

                ActivityCompat.requestPermissions(NuevoPlan.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

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

            takePicture();

        }
    }

    // ------------------------------------------------------------- //
    // ---------------- TEXT WATCHER IMPLEMENTATION ---------------- //
    //-------------------------------------------------------------- //

    private TextWatcher setTextWatcher(){
        return new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                int respaldoAnticipo = anticipo;
                if(editable.toString().length()>0) {
                    anticipo = Integer.parseInt(editable.toString());
                }else{
                    anticipo=0;
                }

                try{

                    calculateShowTexts();
                    /*
                    int numeroPagos = financiamientoFirebase.getMensualidades().get(financiamiento).getMensualidades();
                    float newMontoFinal=montoFinalActual;
                    if(descuento!=-1) {
                        montoFinalActual -= ((descuentosPlanesFirebase.getDescuentos().get(descuento).getDescuento() * montoFinalActual) / 100);
                    }
                    */

                    /*
                    if(respaldoAnticipo!=anticipo){montoFinalActual+=respaldoAnticipo;}
                    montoFinalActual-=anticipo;

                    //Calcular mensualidades y Descuento

                    String montoTotalFormateado = NumberFormat.getNumberInstance(Locale.US).format(Math.round(montoFinalActual));
                    textviewTotalAPagar.setText(String.format( getString(R.string.nuevoplan_formatted_monto),montoTotalFormateado));
                    String montoMensualidadFormateado = NumberFormat.getNumberInstance(Locale.US).format(Math.round(montoFinalActual/ numPagosFinal));
                    textViewMensualidades.setText(String.format(getString(R.string.nuevoplan_format_numeropagos),
                            numPagosFinal,
                            String.format( getString(R.string.nuevoplan_formatted_monto),montoMensualidadFormateado)));

                    */


                }catch(Exception e){e.printStackTrace();}
            }
        };
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
                Log.d(TAG, "--- frontalURL="+frontalURL);
            }
            //Viene de REVERSO
            else if(name.compareTo(nombreReverso)==0){
                reverseURL = taskSnapshot.getDownloadUrl().toString();
                progressReverso.setVisibility(View.GONE);
                Log.d(TAG, "--- reverseURL="+reverseURL);
            }
            //Viene de COMPROBANTE
            else if(name.compareTo(nombreComprobante)==0){
                comprobanteURL = taskSnapshot.getDownloadUrl().toString();
                progressComprobante.setVisibility(View.GONE);
                Log.d(TAG, "--- comprobanteURL="+comprobanteURL);
            }

            //Viene de DESCUENTO
            else if(name.compareTo(nombreDescuento)==0){
                descuentoURL = taskSnapshot.getDownloadUrl().toString();
                progressDescuento.setVisibility(View.GONE);
                Log.d(TAG, "--- descuentoURL="+descuentoURL);
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

                case DESCUENTO_REQUESTED:{
                    Picasso.with(NuevoPlan.this).load(chosenImage.getQueryUri()).into(imgDescuento);
                    chosenImageDescuento=chosenImage;
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
            case R.id.cardview_tarjeta_descuento:{
                if(!WS.hasInternet(NuevoPlan.this)){
                    Toast.makeText(NuevoPlan.this, getString(R.string.toast_noimagenes_sinconexion), Toast.LENGTH_SHORT).show();
                    return;
                }
                img_requested=DESCUENTO_REQUESTED;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            }
            case R.id.bottomsheetlayout_cam1:{}
            case R.id.bottomsheetlayout_cam2:{
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                checkforCamPermissions();
                //takePicture();
                break;
            }
            case R.id.bottomsheetlayout_galeria1:{}
            case R.id.bottomsheetlayout_galeria2:{
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                pickImageSingle();
                break;
            }

            case R.id.nuevoplan_button_nuevoplan:{

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
                if(spinnerFunctionalityIsLocked){break;}
                spinnerFunctionalityIsLocked=true;

                //seleccionamos el plan y lo guardamos
                plan=selectedInt;

                Log.d(TAG,"plan elegido="+plan);

                /** SETEAMOS ATAUD */
                List<String> stringsAtaudes = configPlan.getTiposPlan().get(plan).getTiposAtaudStrings();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        NuevoPlan.this, android.R.layout.simple_spinner_item, stringsAtaudes);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerModeloAtaud.setAdapter(adapter);
                spinnerModeloAtaud.setEnabled(true);
                ataud=-1;

                //preparamos el adapter vacio para borrar los que siguen del siguiente nivel
                List<String> stringsVacias = new ArrayList<>();
                ArrayAdapter<String> adapterVacio = new ArrayAdapter<String>(
                        NuevoPlan.this, android.R.layout.simple_spinner_item, stringsVacias);
                adapterVacio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                /** BORRAMOS SERVICIO*/
                spinnerServicio.setAdapter(adapterVacio);
                spinnerServicio.setEnabled(false);
                servicio=-1;
                /** BORRAMOS Financiamiento*/
                spinnerFinanciamiento.setAdapter(adapterVacio);
                spinnerFinanciamiento.setEnabled(false);
                financiamiento=-1;

                spinnerFunctionalityIsLocked=false;
                break;
            }

            case R.id.nuevoplan_spinner_ataud:{
                if(spinnerFunctionalityIsLocked){break;}
                spinnerFunctionalityIsLocked=true;

                ataud=selectedInt;
                Log.d(TAG,"ATAUD = selectedInt="+selectedInt);

                List<String> stringsServicios = configPlan.getTiposPlan().get(plan).getTiposAtaud().get(ataud).getTiposServicioStrings();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        NuevoPlan.this, android.R.layout.simple_spinner_item, stringsServicios);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerServicio.setAdapter(adapter);
                spinnerServicio.setEnabled(true);
                servicio=-1;

                List<String> stringsVacias = new ArrayList<>();
                ArrayAdapter<String> adapterVacio = new ArrayAdapter<String>(
                        NuevoPlan.this, android.R.layout.simple_spinner_item, stringsVacias);
                adapterVacio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerFinanciamiento.setAdapter(adapterVacio);
                spinnerFinanciamiento.setEnabled(false);
                financiamiento=-1;

                spinnerFunctionalityIsLocked=false;
                break;
            }

            case R.id.nuevoplan_spinner_servicio:{
                if(spinnerFunctionalityIsLocked){return;}
                spinnerFunctionalityIsLocked=true;

                servicio=selectedInt;

                List<String> stringsPago = financiamientoFirebase.getMensualidadesStrings();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        NuevoPlan.this, android.R.layout.simple_spinner_item, stringsPago);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFinanciamiento.setAdapter(adapter);
                spinnerFinanciamiento.setEnabled(true);
                financiamiento=-1;

                spinnerFunctionalityIsLocked=false;
                break;
            }

            case R.id.nuevoplan_spinner_financiamiento:{
                financiamiento=selectedInt;

                if(financiamientoFirebase.getMensualidades().get(financiamiento).getMensualidadID()==1){
                    setFrecuenciasPagoSpinner(false);
                }else{
                    setFrecuenciasPagoSpinner(true);
                }

                break;
            }

            case R.id.nuevoplan_spinner_descuentos:{
                descuento=selectedInt;
                if(descuento!=0) {
                    textViewDescuento.setText(String.format(getString(R.string.nuevoplan_format_labeldescuento),
                            descuentosPlanesFirebase.getDescuentos().get(descuento).getDescuento()));
                }else{
                    textViewDescuento.setText(getString(R.string.nuevoplan_sindescuento));
                }
                break;
            }

            case R.id.nuevoplan_spinner_formapago: {
                formaPago=selectedInt;
                Log.d(TAG, "formaPagoID="+formasPagoFirebase.getFormaspago().get(formaPago).getFormaPagoID());
                break;
            }

            case R.id.nuevoplan_spinner_frecuencia_pagos:{
                frecPagos = selectedInt;
                break;
            }
        }

        calculateShowTexts();
        checkForComplete();
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, "AfterPermissionGranted, TakePhoto");

                    takePicture();

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
    public void firebaseObjectRetrieved(Object_Firebase objectFirebase) {

        Log.d(TAG, ""+objectFirebase);

        ConfiguracionPlanes_Firebase configuracionPlanesFirebase = (ConfiguracionPlanes_Firebase)objectFirebase;

        configPlan = configuracionPlanesFirebase.getPlanes();
        setConfigInSpinners();
        financiamientoFirebase = configuracionPlanesFirebase.getListamensualidades();
        formasPagoFirebase = configuracionPlanesFirebase.getListaformaspago();
        setFormasPagoSpinner();
        descuentosPlanesFirebase = configuracionPlanesFirebase.getListadescuentos();
        setDescuentosSpinner();
        frecuenciasPagoFirebase = configuracionPlanesFirebase.getListafrecuenciaspago();
        //setFrecuenciasPagoSpinner(true);
    }

    @Override
    public void firebaseCompleted(boolean hasError) {
        if(hasError){
            //Ocurrio algun error
        }else{

            //Salio bien!
            Bundle analyticsBundle = new Bundle();
            analyticsBundle.putString("cliente_id", stID);
            WS.registerAnalyticsEvent("plan_creado",analyticsBundle);


            try {
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
            }catch(Exception e){e.printStackTrace();}

        }
    }
}
