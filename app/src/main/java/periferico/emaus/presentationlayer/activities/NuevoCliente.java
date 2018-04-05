package periferico.emaus.presentationlayer.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.adapters.AdapterDirNuevoCliente;
import periferico.emaus.domainlayer.adapters.AdapterTelefonos;
import periferico.emaus.domainlayer.firebase_objects.Cliente_Firebase;
import periferico.emaus.domainlayer.custom_classes.DatePickerOwn;
import periferico.emaus.domainlayer.objetos.Direcciones;
import periferico.emaus.domainlayer.objetos.DireccionesHelper;
import periferico.emaus.domainlayer.objetos.Telefonos;
import periferico.emaus.domainlayer.objetos.TelefonosHelper;
import periferico.emaus.domainlayer.utils.AppCompatActivity_Job;
import periferico.emaus.presentationlayer.dialogs.ProgressDialogFragment;

public class NuevoCliente extends AppCompatActivity_Job implements
        View.OnClickListener,
        DatePicker.OnDateChangedListener,
        Spinner.OnItemSelectedListener,
        AdapterTelefonos.OnMinusTelefonosClicked,
        AdapterDirNuevoCliente.OnMinusDireccionesClicked,
        ViewTreeObserver.OnScrollChangedListener,
        AdapterDirNuevoCliente.ClickImageInItem,
        WS.FirebaseCompletionListener,
        ImagePickerCallback,
        OnFailureListener, OnSuccessListener<UploadTask.TaskSnapshot> ,
        WS.OnNetworkListener{

    //Objetos de la vista
    ScrollView scrollview;
    TextInputLayout inputLayoutNombre;
    EditText editTextNombre;
    TextInputLayout inputLayoutApellido;
    EditText editTextApellido;
    TextView textviewAgregarTelefono;
    AppCompatImageButton buttonAgregarTelefono;
    TextView textviewAgregarDireccion;
    AppCompatImageButton buttonAgregarDireccion;
    TextInputLayout inputLayoutEmail;
    EditText editTextEmail;
    Spinner spinnerReligion;
    RadioGroup radioGroup;
    AppCompatRadioButton radioMasculino;
    AppCompatRadioButton radioFemenino;
    ImageButton buttonFechaNac;
    DatePickerOwn datePicker;
    TextInputLayout inputLayoutFechNac;
    EditText editTextFechaNac;
    Spinner spinnerEstadoCivil;
    TextInputLayout inputLayoutOcupacion;
    EditText editTextOcupacion;
    TextInputLayout inputLayoutNotas;
    EditText editTextNotas;
    Button buttonCrearCliente;
    BottomSheetBehavior bottomSheetBehavior;
    TextView bannerNetwork;

    CoordinatorLayout coordinatorLayout;
    ProgressDialogFragment progressDialogFragment;

    private RecyclerView mRecyclerViewTelefonos;
    private RecyclerView.Adapter mAdapterTelefonos;
    private android.support.v7.widget.LinearLayoutManager mLayoutManagerTelefonos;
    private ArrayList<TelefonosHelper> telefonos;

    private RecyclerView mRecyclerViewDirs;
    private RecyclerView.Adapter mAdapterDirs;
    private android.support.v7.widget.LinearLayoutManager mLayoutManagerDirs;
    private ArrayList<DireccionesHelper> direcciones;

    ImagePicker imagePicker;
    CameraImagePicker cameraPicker;

    FirebaseStorage storage;
    StorageReference storageRef;

    private static final int FACHADA_REQUESTED = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int FILES_REQUEST = 3;

    View viewCamara1;
    View viewCamara2;
    View viewGaleria1;
    View viewGaleria2;

    int lastPositionImgClicked;

    private String pickerPath;

    //Objetos de la clase
    String stNombre;
    String stApellido;
    String stEmail;
    int intReligion; boolean religionIsSet;
    int intGenero; boolean generIsSet;
    String stFechaNac;
    int intEstadoCivil; boolean estadoCivilIsSet;
    String stOcupacion;
    String stNotas;
    //List<HashMap<String,Object>> tels;

    //HashMap<String,HashMap<String,Object>> hashImgToUpload;

    String stID;

    static final String TAG = "NuevoClienteDebug";


    // -------------------------------------------- //
    // ---------------- LIFE CYCLE ---------------- //
    //--------------------------------------------- //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_cliente);

        FirebaseCrash.log("NuevoCliente_ActivityCreated");

        //Asignamos el toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Instanciamos todos los objetos
        instanciateObjects();

        //Seteamos todos los escuchas
        setListeners();

        //Setteamos lo necesario para el RecyclerViewTelefonos
        setRecyclerViewTelefonos();

        //Setteamos lo necesario para el RecyclerViewDirecciones
        setRecyclerViewDirecciones();

        //Impedimos que el teclado salga de primera instancia al cargar la pantalla
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "NuevoCliente ONSTART-----");
        WS.setNetworkListener(NuevoCliente.this);
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    /**
     * Metodo que instancia todos los objetos
     */
    private void instanciateObjects(){
        bannerNetwork = findViewById(R.id.nuevocliente_banner_offline);
        scrollview = findViewById(R.id.scrollviewNuevoCliente);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        buttonFechaNac = findViewById(R.id.buttonFechNac);
        datePicker = findViewById(R.id.datePicker);
        inputLayoutNombre = findViewById(R.id.textInputNombre);
        editTextNombre = findViewById(R.id.editTextName);
        inputLayoutApellido = findViewById(R.id.textInputApellidos);
        editTextApellido = findViewById(R.id.editTextApellidos);
        textviewAgregarTelefono = findViewById(R.id.textViewButtonAgregarTelefono);
        buttonAgregarTelefono = findViewById(R.id.buttonAgregarTelefono);
        textviewAgregarDireccion = findViewById(R.id.textViewButtonAgregarDireccion);
        buttonAgregarDireccion = findViewById(R.id.buttonAgregarDireccion);
        inputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        editTextEmail = findViewById(R.id.editTextEmail);
        spinnerReligion = findViewById(R.id.spinnerReligion);
        buttonCrearCliente = findViewById(R.id.buttonCrearCliente);
        radioGroup = findViewById(R.id.radioGroup);
        radioFemenino = findViewById(R.id.radioFemenino);
        radioMasculino = findViewById(R.id.radioMasculino);
        inputLayoutFechNac = findViewById(R.id.inputLayoutFechaNac);
        editTextFechaNac = findViewById(R.id.editTextFechNac);
        spinnerEstadoCivil = findViewById(R.id.spinnerEstadoCivil);
        inputLayoutOcupacion = findViewById(R.id.textInputOcupacion);
        editTextOcupacion = findViewById(R.id.editTextOcupacion);
        inputLayoutNotas = findViewById(R.id.textInputLayoutNotas);
        editTextNotas = findViewById(R.id.editTextNotas);
        intEstadoCivil=-1;

        viewCamara1 = findViewById(R.id.bottomsheetlayout_cam1);
        viewCamara2 = findViewById(R.id.bottomsheetlayout_cam2);
        viewGaleria1 = findViewById(R.id.bottomsheetlayout_galeria1);
        viewGaleria2 = findViewById(R.id.bottomsheetlayout_galeria2);

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomsheetlayout_img_picker));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        //hashImgToUpload = new HashMap<>();
    }

    /**
     * Metodo que agrega los listeners a los controles que lo necesiten
     */
    private void setListeners(){
        scrollview.getViewTreeObserver().addOnScrollChangedListener(this);
        editTextNombre.addTextChangedListener(newTextWatcher(inputLayoutNombre));
        editTextApellido.addTextChangedListener(newTextWatcher(inputLayoutApellido));
        buttonFechaNac.setOnClickListener(NuevoCliente.this);
        //radioGroup.setOnCheckedChangeListener(NuevoCliente.this);
        datePicker.init(2017,
                1,
                1,
                NuevoCliente.this);
        editTextOcupacion.addTextChangedListener(newTextWatcher(inputLayoutOcupacion));
        textviewAgregarTelefono.setOnClickListener(NuevoCliente.this);
        buttonAgregarTelefono.setOnClickListener(NuevoCliente.this);
        textviewAgregarDireccion.setOnClickListener(NuevoCliente.this);
        buttonAgregarDireccion.setOnClickListener(NuevoCliente.this);
        buttonCrearCliente.setOnClickListener(NuevoCliente.this);

        viewCamara1.setOnClickListener(this);
        viewCamara2.setOnClickListener(this);
        viewGaleria1.setOnClickListener(this);
        viewGaleria2.setOnClickListener(this);
    }

    /**
     * Metodo que inicializa y settea el recyclerViewTelefonos
     */
    private void setRecyclerViewDirecciones(){
        mRecyclerViewDirs = findViewById(R.id.recyclerViewDirecciones);
        mRecyclerViewDirs.setHasFixedSize(false);
        mLayoutManagerDirs = new LinearLayoutManager(this);
        mRecyclerViewDirs.setLayoutManager(mLayoutManagerDirs);
        direcciones = new ArrayList<>();
        mAdapterDirs = new AdapterDirNuevoCliente(direcciones, NuevoCliente.this);
        ((AdapterDirNuevoCliente)mAdapterDirs).setClickImageOnItemListener(NuevoCliente.this);
        mRecyclerViewDirs.setAdapter(mAdapterDirs);

        mRecyclerViewDirs.getRecycledViewPool().setMaxRecycledViews(0,0);
    }

    /**
     * Metodo que inicializa y settea el recyclerViewTelefonos
     */
    private void setRecyclerViewTelefonos(){
        mRecyclerViewTelefonos = findViewById(R.id.recyclerViewTelefonos);
        mRecyclerViewTelefonos.setHasFixedSize(false);
        mLayoutManagerTelefonos = new LinearLayoutManager(this);
        mRecyclerViewTelefonos.setLayoutManager(mLayoutManagerTelefonos);
        telefonos = new ArrayList<>();
        mAdapterTelefonos = new AdapterTelefonos(telefonos, NuevoCliente.this);
        mRecyclerViewTelefonos.setAdapter(mAdapterTelefonos);

        mRecyclerViewTelefonos.getRecycledViewPool().setMaxRecycledViews(0,0);
    }

    /**
     * Rutina que agrega un campo telefonico al ser llamado por el escucha
     */
    private void agregarTelefonoALista(){
        //TODO Intenta reinicializar los campos tambien
        //telefonos.add(new TelefonosHelper());
        int insertedInto = telefonos.size();
        telefonos.add(insertedInto,new TelefonosHelper());
        mAdapterTelefonos.notifyItemInserted(insertedInto);
        mAdapterTelefonos.notifyItemRangeChanged(insertedInto, telefonos.size());
        Log.d("AgregarTelefonoDebug","telefonosSize="+telefonos.size()+" adapter="+mAdapterTelefonos.getItemCount());
    }

    private void agregarDireccionALista(){
        int insertedInto = direcciones.size();
        direcciones.add(insertedInto, new DireccionesHelper());
        mAdapterDirs.notifyItemInserted(insertedInto);
        mAdapterDirs.notifyItemRangeChanged(insertedInto, direcciones.size());
    }

    /**
     * Rutina que elimina el campo telefonico seleccionado
     * @param position
     */
    private void eliminarTelefonoDeLista(int position){
        telefonos.remove(telefonos.get(position));
        mAdapterTelefonos.notifyItemRemoved(position);
    }

    /**
     * Rutina que elimina el campo de direccion seleccionado
     * @param position
     */
    private void eliminarDireccionDeLista(int position){
        direcciones.remove(direcciones.get(position));
        mAdapterDirs.notifyItemRemoved(position);
    }

    /**
     * Metodo que valida cada uno de los campos antes de llamar al servicio de creacion de cliente
     */
    @SuppressLint("RestrictedApi")
    private void validateFields(){

        hideKeyboard();

        int errors=0;

        //Verifica que haya al menos un campo de telefono
        if(telefonos.size()==0){
            textviewAgregarTelefono.setTextColor(Color.RED);
            buttonAgregarTelefono.setColorFilter(Color.RED);
            errors++;
        }
        //Si hay al menos un campo de telefono, verifica que no este vacio
        else{
            for(int i=0; i<telefonos.size(); i++){
                TelefonosHelper telObj = telefonos.get(i);
                String tel = telObj.getEditTextTelefono().getEditableText().toString();
                Log.d("","tel.length()>=8["+(tel.length()>=8)+"] && tel.length()<=10["+(tel.length()<=10)+"]");
                if(!(tel.length()>=8 && tel.length()<=10)){
                    errors++;
                    telObj.getTextInputLayoutTelefono().setError(getString(R.string.nuevocliente_error_telefonovacio));
                }
                if(telObj.getSpinnerTipoTelefono().getSelectedItemPosition()==-1){
                    errors++;
                    telObj.getTextInputLayoutTelefono().setError(getString(R.string.nuevocliente_error_telefonotipovacio));
                }

            }
        }
        if(errors>0){
            if(inputLayoutNombre.hasFocus())
                inputLayoutApellido.requestFocus();
            else
                inputLayoutNombre.requestFocus();

            Toast.makeText(NuevoCliente.this, "Agrega al menos un teléfono de contacto", Toast.LENGTH_SHORT).show();
        }

        //Verifica que el campo de apellido no este vacio
        stApellido = editTextApellido.getEditableText().toString();
        if(stApellido.isEmpty()){
            inputLayoutApellido.setError(getString(R.string.nuevocliente_error_apellido));
            inputLayoutApellido.requestFocus();
            errors++;
        }

        //Verifica que no este vacio el campo de nombre
        stNombre = editTextNombre.getEditableText().toString();
        if(stNombre.isEmpty()){
            inputLayoutNombre.setError(getString(R.string.nuevocliente_error_nombre));
            inputLayoutNombre.requestFocus();
            errors++;
        }

        /*
        if(intGenero==0){
            int[][] states = new int[][] {
                    new int[] { android.R.attr.state_enabled}, // enabled
                    new int[] {-android.R.attr.state_enabled}, // disabled
                    new int[] {-android.R.attr.state_checked}, // unchecked
                    new int[] { android.R.attr.state_pressed}  // pressed
            };
            int[] colors = new int[] { Color.RED, Color.RED, Color.RED, Color.RED };

            radioMasculino.setTextColor(Color.RED);
            radioFemenino.setTextColor(Color.RED);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                radioFemenino.setSupportButtonTintList(new ColorStateList(states,colors));
                radioMasculino.setSupportButtonTintList(new ColorStateList(states,colors));
            }
            errors++;
        }

        if(stFechaNac==null){
            inputLayoutFechNac.setError(getString(R.string.nuevocliente_error_fechanac));
            errors++;
        }

        //Log.d("SpinnerDebug","spinner="+spinnerEstadoCivil.getSelectedItemPosition());
        if(intEstadoCivil==-1){
        }

        stOcupacion = editTextOcupacion.getEditableText().toString();
        if(stOcupacion.isEmpty()){
            inputLayoutOcupacion.setError(getString(R.string.nuevocliente_error_ocupacion));
            errors++;
        }
        */

        if(errors==0){

            progressDialogFragment = new ProgressDialogFragment();
            progressDialogFragment.setCancelable(false);
            progressDialogFragment.title="Creando cliente nuevo...";
            progressDialogFragment.show(getSupportFragmentManager(), TAG);

            //Create ID
            String[] nombres = stNombre.split(" ");
            String[] apellidos = stApellido.split(" ");
            String nomApe = nombres[0].substring(0,2) + apellidos[0].substring(0,2);
            TelefonosHelper telObj = telefonos.get(0);
            String tel = telObj.getEditTextTelefono().getEditableText().toString();
            stID = nomApe+tel.substring(tel.length()-4,tel.length());

            //uploadImagesFirebase();

            int imgsToBeUpload = 0;
            for(DireccionesHelper direcs : direcciones){
                if(direcs.hasImgSet){imgsToBeUpload++;}
            }

            int pendingImages = 0;
            for(DireccionesHelper direcs : direcciones){
                if(!(direcs.getLinkFachada()==null) && direcs.isLinkSet){pendingImages++;}
            }

            if(imgsToBeUpload>0){
                uploadImagesFirebase();
            }else{
                client_toFirebase();
            }

            /*
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    client_toFirebase();
                }
            }, 2000);
            */
            //client_toFirebase();
        }
    }



    /**
     * Rutina que rellena los campos vacios e inicia el proceso de Firebase
     */
    private void client_toFirebase(){

        Log.d(TAG,"Entra ClientToFirebase");

        stEmail = editTextEmail.getEditableText().toString();
        if(stEmail.isEmpty()){
            stEmail="";
        }

        intReligion=spinnerReligion.getSelectedItemPosition();
        if(intReligion==-1){intReligion=0;}else{religionIsSet=true;}

        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(radioButtonID);
        int idx = radioGroup.indexOfChild(radioButton);
        intGenero = (idx==-1) ? 0 : idx;
        if(intGenero>=0){generIsSet=true;}

        Log.d(TAG, "genero="+intGenero);

        if(stFechaNac!=null && stFechaNac.isEmpty()){
            stFechaNac="";
        }

        intEstadoCivil=spinnerEstadoCivil.getSelectedItemPosition();
        if(intEstadoCivil==-1){intEstadoCivil=0;}else{estadoCivilIsSet=true;}

        stOcupacion=editTextOcupacion.getEditableText().toString();
        if(stOcupacion.isEmpty()){
            stOcupacion="";
        }

        stNotas=editTextNotas.getEditableText().toString();
        if(stNotas.isEmpty()){
            stNotas="";
        }

        List<Telefonos> telefs = new ArrayList<>();
        telefs = new ArrayList<>();
        for(TelefonosHelper telAdapterObj : telefonos){
            Telefonos telefono = new Telefonos();
            telefono.setIntTipo(telAdapterObj.getTipo());
            telefono.setStTelefono(telAdapterObj.getTelefono());
            telefs.add(telefono);
            //hashMapTels.put(telAdapterObj.getTelefono(),telAdapterObj.getTipo());
        }

        if(stNombre.substring(stNombre.length() - 1).compareTo(" ")==0){
            stNombre = stNombre.substring(0,stNombre.length() - 1);
        }

        if(stApellido.substring(stApellido.length() - 1).compareTo(" ")==0){
            stApellido = stApellido.substring(0,stApellido.length() - 1);
        }

        //Log.d(TAG, ""+nomApe+tel.substring(tel.length()-4,tel.length()));
        Log.d(TAG,"ID="+stID+" email="+stEmail+" religion="+intReligion+" genero="+intGenero+" fnac="+stFechaNac);

        List<Direcciones> direcs = new ArrayList<>();
        for(DireccionesHelper dirHelper : direcciones){
            Direcciones dir = new Direcciones();
            dir.setStNumInt(dirHelper.getStNumInt());
            dir.setStCP(dirHelper.getStCP());
            dir.setStColonia(dirHelper.getStColonia());
            dir.setStCalleNum(dirHelper.getStCalleNum());
            dir.setLinkFachada(dirHelper.getLinkFachada());
            direcs.add(dir);
        }

        Cliente_Firebase clienteFirebase;
        clienteFirebase = new Cliente_Firebase(stID+"_"+System.currentTimeMillis()/1000, stNombre, stApellido, stEmail, (religionIsSet ? intReligion+1 : -1),
                (generIsSet ? intGenero+1 : -1), stFechaNac, (estadoCivilIsSet ? intEstadoCivil+1 : -1), stOcupacion, stNotas, 1,
                telefs,direcs);


        FirebaseCrash.log("NuevoCliente_TRY_CrearCliente");
        WS.crearClienteFirebase(clienteFirebase, NuevoCliente.this);


        if(!WS.hasInternet(NuevoCliente.this)){

            final Handler handlerPre = new Handler();
            handlerPre.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Bundle analyticsBundle = new Bundle();
                    analyticsBundle.putString("cliente_id", stID);
                    WS.registerAnalyticsEvent("cliente_creado_offline",analyticsBundle);

                    progressDialogFragment.changeTitle("Cliente creado SIN CONEXIÓN!");
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialogFragment.dismiss();
                            onBackPressed();
                        }
                    }, 1500);
                }
            },1000);
            FirebaseCrash.log("NuevoCliente_TRY_ClienteCreadoExitoso_OFFLINE");
        }
    }


    /**
     * Rutina que esconde el teclado cuando se mande a llamar
     */
    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void checkPermissionCamera(){
        if (ContextCompat.checkSelfPermission(NuevoCliente.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(NuevoCliente.this,
                    Manifest.permission.CAMERA)) {

                ActivityCompat.requestPermissions(NuevoCliente.this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST);

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                Log.d(TAG, "Explanation NEEDED ASK FOR PERMISSION");

            } else {

                // No explanation needed, we can request the permission.

                Log.d(TAG, "NO EXP, ASK PERMISSION");

                ActivityCompat.requestPermissions(NuevoCliente.this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            Log.d(TAG, "Start TakePicture()");
            takePicture();
        }
    }

    // ----------------------------------------------- //
    // ---------------- INNER CLASSES ---------------- //
    //------------------------------------------------ //

    private TextWatcher newTextWatcher(final TextInputLayout textInput){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                textInput.setError(null);
            }
        };
    }

    // ---------------------------------------------------------------- //
    // ---------------- SCROLLVIEW IMPLEMENTED METHODS ---------------- //
    //----------------------------------------------------------------- //

    @Override
    public void onScrollChanged() {
        //hideKeyboard();
    }

    // ----------------------------------------------------- //
    // ---------------- IMPLEMENTED METHODS ---------------- //
    //------------------------------------------------------ //


    /**
     * Asigna todas las acciones de tap por vista
     * @param view view el cual tuvo el tap
     */
    @Override
    public void onClick(View view) {
        hideKeyboard();

        switch(view.getId()){

            case R.id.buttonAgregarTelefono:{
                agregarTelefonoALista();
                break;
            }

            case R.id.buttonAgregarDireccion:{
                agregarDireccionALista();
                break;
            }

            case R.id.buttonFechNac:{
                datePicker.setVisibility(
                        datePicker.getVisibility()==View.VISIBLE
                                ? View.GONE : View.VISIBLE
                );
                break;
            }

            case R.id.buttonCrearCliente:{
                validateFields();
                break;
            }

            case R.id.bottomsheetlayout_cam1:{}
            case R.id.bottomsheetlayout_cam2:{
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                checkPermissionCamera();
                break;
            }
            case R.id.bottomsheetlayout_galeria1:{}
            case R.id.bottomsheetlayout_galeria2:{
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                pickImageSingle();
                break;
            }
        }
    }

    /**
     * Escucha gestiona el tocar una fecha en el DatePicker
     *
     * @param datePicker Vista del DatePicker
     * @param year Anio en numero
     * @param monthOfYear mes en numero
     * @param dayOfMonth dia en numero
     */
    @Override
    public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        String[] mArrayMeses = getResources().getStringArray(R.array.array_meses);
        stFechaNac = getString(R.string.nuevocliente_formatted_fecha,dayOfMonth, mArrayMeses[monthOfYear], year);
        editTextFechaNac.setText(stFechaNac);
        inputLayoutFechNac.setError(null);
    }


    /*
    @SuppressLint("RestrictedApi")
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {
        switch(checkedID){
            case R.id.radioFemenino:{
                intGenero=2;
                break;
            }

            case R.id.radioMasculino:{
                intGenero=1;
                break;
            }
        }

        int colorAccent = ContextCompat.getColor(NuevoCliente.this, R.color.colorAccent);
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };
        int[] colors = new int[] { colorAccent, colorAccent, colorAccent, colorAccent };

        radioMasculino.setTextColor(Color.BLACK);
        radioFemenino.setTextColor(Color.BLACK);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            radioFemenino.setSupportButtonTintList(new ColorStateList(states,colors));
            radioMasculino.setSupportButtonTintList(new ColorStateList(states,colors));
        }
    }
    */

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        intEstadoCivil=position;
    } @Override public void onNothingSelected(AdapterView<?> adapterView) {}

    @Override
    public void onMinusTelefonosClicked(int position) {
        eliminarTelefonoDeLista(position);
    }

    @Override
    public void onMinusDireccionesClicked(int position) { eliminarDireccionDeLista(position); }

    @Override
    public void firebaseCompleted(boolean hasError) {
        if(hasError){
            //Ocurrio algun error
        }else{
            //Salio bien!
            FirebaseCrash.log("NuevoCliente_TRY_ClienteCreadoExitoso");

            Bundle analyticsBundle = new Bundle();
            analyticsBundle.putString("cliente_id", stID);
            WS.registerAnalyticsEvent("cliente_creado",analyticsBundle);

            try {
                progressDialogFragment.changeTitle("Cliente creado exitosamente!");
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

    // -------------------------------------------------------------------- //
    // ---------------- ON NETWORK LISTENER IMPLEMENTATION ---------------- //
    //--------------------------------------------------------------------- //

    @Override
    public void fromOffToOn() {
        //Log.d(TAG,"from "+TAG+" off -> ON");
        runOnUiThread(new Runnable() {
            @Override
            public void run() { bannerNetwork.setVisibility(View.VISIBLE); }
        });
    }

    @Override
    public void fromOnToOff() {
        //Log.d(TAG,"from "+TAG+" on -> OFF");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bannerNetwork.setVisibility(View.INVISIBLE);
            }
        });
    }


    // ------------------------------------------------------------------- //
    // ---------------- CLICKED IMG PICKER IMPLEMENTATION ---------------- //
    //-------------------------------------------------------------------- //

    @Override
    public void imgWasTouched(int position, View fullImgContainer_) {

        if(!WS.hasInternet(NuevoCliente.this)){
            Toast.makeText(NuevoCliente.this, "No puedes subir imágenes en modo offline", Toast.LENGTH_LONG).show();
        }else {

            direcciones.get(position).fullViewFachada = fullImgContainer_;
            direcciones.get(position).imagePosition = position;
            //hashImgToUpload.put(""+position,new HashMap<String, Object>());
            //(hashImgToUpload.get(""+position)).put("fullViewFachada",fullImgContainer);
            lastPositionImgClicked = position;
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }


    // ------------------------------------------------------------------- //
    // ---------------- POLICIES IMPLEMENTATION ---------------- //
    //-------------------------------------------------------------------- //

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

    // ---------------------------------------------------------- //
    // ---------------- CUSTOM IMG BUTON ACTIONS ---------------- //
    //----------------------------------------------------------- //

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

    // ------------------------------------------------------ //
    // ---------------- IMAGE PICKER METHODS ---------------- //
    //------------------------------------------------------- //

    @Override
    public void onImagesChosen(List<ChosenImage> list) {
        for(ChosenImage chosenImage : list){
            direcciones.get(lastPositionImgClicked).hasImgSet=true;
            ImageView img = direcciones.get(lastPositionImgClicked).fullViewFachada.findViewById(R.id.itemdir_foto_fachada);
            Picasso.with(NuevoCliente.this)
                    .load(chosenImage.getQueryUri())
                    .into(img);
            direcciones.get(lastPositionImgClicked).chosenImage=chosenImage;
        }
        //checkForComplete();
    }

    @Override
    public void onError(String s) {}

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

    // ---------------------------------------------------------- //
    // ---------------- IMAGE UPLOAD DECLARATION ---------------- //
    //----------------------------------------------------------- //

    private void uploadImagesFirebase(){

        storage = FirebaseStorage.getInstance(getString(R.string.FirebaseStorageBucket));
        storageRef = storage.getReference();



        for(int i=0; i<direcciones.size(); i++){

            if(!direcciones.get(i).hasImgSet){continue;}

            ChosenImage chosenImg=direcciones.get(i).chosenImage;
            String name="fachada"+i;

            String ref = name+chosenImg.getFileExtensionFromMimeType();
            String fullRef = "fachadas/"+stID+"/"+"direccion"+i+"/"+ref;

            Log.d(TAG,"fullRef="+fullRef);
            direcciones.get(i).imgName=ref;

            StorageReference imgFullRef = storageRef.child(fullRef);

            ImageView imageView = direcciones.get(i).fullViewFachada.findViewById(R.id.itemdir_foto_fachada);
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = imageView.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imgFullRef.putBytes(data);
            uploadTask.addOnFailureListener(NuevoCliente.this);
            uploadTask.addOnSuccessListener(NuevoCliente.this);


        }
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

            Log.d(TAG, "Enters at least?");

            Log.d(TAG, ""+taskSnapshot.getMetadata().getName()+" , "+taskSnapshot.getDownloadUrl().toString());


            /*
            Log.d(TAG, " SUCCESS = " + taskSnapshot.toString());
            Log.d(TAG, " SUCCESS = " + taskSnapshot.getMetadata().toString());
            Log.d(TAG, " SUCCESS = " + taskSnapshot.getMetadata().getName());
            */

            for(int i=0; i<direcciones.size(); i++){
                if(!direcciones.get(i).hasImgSet){continue;}
                Log.d(TAG, "compare "+(String)direcciones.get(i).imgName+" , "+taskSnapshot.getMetadata().getName()+" , "+taskSnapshot.getDownloadUrl().toString());

                if( direcciones.get(i).imgName.compareTo(taskSnapshot.getMetadata().getName())==0 ){
                    direcciones.get(i).setLinkFachada(taskSnapshot.getDownloadUrl().toString());
                }

            }
            /*
            it = hashImgToUpload.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                HashMap<String,Object> eachImgHash = (HashMap<String,Object>)pair.getValue();

                Log.d(TAG, "imgLink="+eachImgHash.get("linkImg"));

            }
            */

            int pendingImages = 0;
            for(DireccionesHelper direcs : direcciones){
                if(!direcs.hasImgSet){continue;}
                if(!direcs.getLinkFachada().isEmpty() && direcs.isLinkSet){pendingImages++;}
            }
            Log.d(TAG," we have "+pendingImages+" pending images!");
            if(pendingImages==0){client_toFirebase();}

            //Viene de FRONTAL
            //if(name.compareTo(nombreFachada)==0){
                //fachadaURL = taskSnapshot.getDownloadUrl().toString();

                //.setVisibility(View.GONE);
            //}
            //finalCreationPlanFirebase();

        }catch(Exception e){e.printStackTrace();}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    takePicture();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "PERMISSION DENIED!");

                    WS.showAlert(
                            NuevoCliente.this,
                            getString(R.string.alerts_permiso_titulo),
                            getString(R.string.alerts_permiso_mensaje),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //delayAndContinue(success);
                                }
                            }
                    );
                    /*
                    ActivityCompat.requestPermissions(NuevoCliente.this,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_REQUEST);
                    */
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}