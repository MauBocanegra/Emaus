package periferico.emaus.presentationlayer.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResponse;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.firebase_objects.Cliente_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.firebase_objects.PerfilEmpleado;
import periferico.emaus.domainlayer.firebase_objects.Plan_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Ticket_Firebase;
import periferico.emaus.domainlayer.objetos.Direcciones;
import periferico.emaus.domainlayer.utils.AppCompatActivity_Job;
import periferico.emaus.presentationlayer.dialogs.ImageDialogFragment;
import periferico.emaus.presentationlayer.dialogs.ProgressDialogFragment;

public class DetalleCobro extends AppCompatActivity_Job implements
    WS.OnNetworkListener,
    WS.FirebaseCompletionListener{

    TextView bannerNetwork;

    Cliente_Firebase clienteFirebase;
    Plan_Firebase planFirebase;
    Ticket_Firebase ticketFirebase;
    PerfilEmpleado perfilEmpleado;

    //-------------------------

    TextView letrasClienteTextView;
    TextView clienteNombreTextView;
    Button buttonMostrarCliente;
    ImageView fachadaImageView;
    TextView planClienteTextView;
    TextView direccionTextView;
    TextView tipoDomicilioTextView;
    TextView fechaTextView;
    TextView planTextView;
    TextView costoTotalTextView;
    TextView numeroAbonoTextView;
    TextView cantidadAbonoTextView;
    ImageView buttonEditarAbono;
    TextInputLayout cantidadAbonoInputLayout;
    EditText cantidadAbonotEditText;
    TextView cantidadPagadoTextView;
    TextView cantidadSaldoActualTextView;
    TextView cantidadSaldoVencidoTextView;
    TextView porcentajeTextView;
    ProgressBar progressPorcentaje;
    TextView letrasCobradorTextView;
    TextView cobradorNombreTextView;
    //TextView rutaTextView;
    Button buttonPagar;
    Button buttonImprimir;

    float cantidadAbonoMinimo=0;

    ProgressDialogFragment progressDialogFragment;

    //---------------------------

    GoogleApiClient client;

    private static final String TAG = "DetalleCobroDebug";
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_cobro);

        instanciateObjects();

        setButtonsStates();

        downloadCliente_Plan_Empleado();

        instanciateToolbar();

        setTime();

        connectToAwarenessAPI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "NuevoCliente ONSTART-----");
        WS.setNetworkListener(DetalleCobro.this);
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    private void instanciateObjects(){
        bannerNetwork = findViewById(R.id.nuevocliente_banner_offline);

        letrasClienteTextView = findViewById(R.id.detallecobro_nombre_letras);
        buttonMostrarCliente = findViewById(R.id.detallecobro_button_cliente);
        clienteNombreTextView = findViewById(R.id.detallecobro_cliente);
        fachadaImageView = findViewById(R.id.detallecobro_fachada);
        planClienteTextView = findViewById(R.id.detallecobro_plan_cliente);
        direccionTextView = findViewById(R.id.detallecobro_direccion);
        tipoDomicilioTextView = findViewById(R.id.detallecobro_tipo_domicilio);
        fechaTextView = findViewById(R.id.detallecobro_fecha);
        planTextView = findViewById(R.id.detallecobro_plan_plan);
        costoTotalTextView = findViewById(R.id.detallecobro_plan_costototal);
        numeroAbonoTextView = findViewById(R.id.detallecobro_abono_numero);
        cantidadAbonoTextView = findViewById(R.id.detallecobro_textview_abonocantidad);
        buttonEditarAbono = findViewById(R.id.detallecobro_button_editabono);
        cantidadAbonoInputLayout = findViewById(R.id.detallecobro_inputlayout_abonocantidad);
        cantidadAbonotEditText = findViewById(R.id.detallecobro_edittext_abonocantidad);
        cantidadPagadoTextView = findViewById(R.id.detallecobro_pagado);
        cantidadSaldoActualTextView = findViewById(R.id.detallecobro_saldoactual);
        cantidadSaldoVencidoTextView = findViewById(R.id.detallecobro_saldovencido);
        porcentajeTextView = findViewById(R.id.detallecobro_porcentaje);
        progressPorcentaje = findViewById(R.id.detallecobro_progress_porcentaje);
        letrasCobradorTextView = findViewById(R.id.detallecobro_cobrador_letras);
        cobradorNombreTextView = findViewById(R.id.detallecobro_cobrador);
        //rutaTextView = findViewById(R.id.detallecobro_ruta);
        buttonPagar = findViewById(R.id.detallecobro_button_pagar);
        buttonImprimir = findViewById(R.id.detallecobro_button_imprimir);

    }

    private void setButtonsStates(){

        buttonPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WS.showAlert(DetalleCobro.this, "Generación de ficha de cobro",
                        "Estas por confirmar un cobro, al aceptar aceptas que has recibido " +
                                "la cantidad de dinero correspondiente al cobro y se generará un ticket " +
                                "en la base de datos.\n¿Deseas confirmar el cobro?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                /*
                                progressDialogFragment = new ProgressDialogFragment();
                                progressDialogFragment.setCancelable(false);
                                progressDialogFragment.title="Creando ticket de pago...";
                                progressDialogFragment.show(getSupportFragmentManager(), TAG);
                                */

                                showTicket();

                                //processPay();
                            }
                        }, true);
            }
        });

        buttonImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTicket();
            }
        });

        buttonMostrarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showTicket();

                Intent intent = new Intent(DetalleCobro.this, DetalleCliente.class);
                //Log.d("ClientesFrag","stID="+cliente.toString());
                intent.putExtra("clientID",clienteFirebase.getStID());
                intent.putExtra("stNombre",clienteFirebase.getStNombre()+" "+clienteFirebase.getStApellido());
                intent.putExtra("intStatus",clienteFirebase.getIntStatus());

                startActivity(intent);

            }
        });

        buttonImprimir.setVisibility(View.GONE);

        buttonEditarAbono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cantidadAbonoInputLayout.getVisibility()==View.GONE){
                    cantidadAbonoInputLayout.setVisibility(View.VISIBLE);
                    setEditTextListener();
                }
            }
        });
    }

    private void instanciateToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Cobro");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void downloadCliente_Plan_Empleado() {
        String clienteID; String planID;
        Bundle extras = getIntent().getExtras();
        clienteID = extras.getString("clienteID");
        planID = extras.getString("planID");

        Log.d(TAG, "Cliente="+extras.getString("clienteID")+" plan="+extras.getString("planID"));

        WS.readClientFirebase(clienteID, new WS.FirebaseObjectRetrievedListener(){
            @Override
            public void firebaseObjectRetrieved(Object_Firebase objectFirebase) {
                clienteFirebase = (Cliente_Firebase)objectFirebase;
                setClienteData();
            }
        });

        WS.readPlanFirebase(planID, new WS.FirebaseObjectRetrievedListener(){
            @Override
            public void firebaseObjectRetrieved(Object_Firebase objectFirebase) {
                planFirebase = (Plan_Firebase)objectFirebase;
                setPlanData();
            }
        });

        WS.readPerfilEmpleado(new WS.FirebaseObjectRetrievedListener(){
            @Override
            public void firebaseObjectRetrieved(Object_Firebase objectFirebase) {
                perfilEmpleado = (PerfilEmpleado)objectFirebase;
                setEmpleadoData();
            }
        });
    }

    private void setClienteData(){
        letrasClienteTextView.setText(getString(
                R.string.detallecliente_format_letras,
                clienteFirebase.getStNombre().toUpperCase().substring(0,1),
                clienteFirebase.getStApellido().toUpperCase().substring(0,1)
        ));
        clienteNombreTextView.setText(getString(
                R.string.format_fullname,
                clienteFirebase.getStNombre(),
                clienteFirebase.getStApellido()
        ));

        try {
            final List<Direcciones> dirs = clienteFirebase.getDirecciones();
            Picasso.with(DetalleCobro.this).load(dirs.get(0).getLinkFachada()).into(fachadaImageView);
            direccionTextView.setText(
                getString(
                    R.string.format_fulldireccion,
                    dirs.get(0).getStCalleNum(),
                    dirs.get(0).getStColonia(),
                    dirs.get(0).getStCP()
                ));
            fachadaImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageDialogFragment imageDialogFragment = new ImageDialogFragment();
                    imageDialogFragment.setImage(dirs.get(0).getLinkFachada());
                    imageDialogFragment.show(getSupportFragmentManager(), "ImgFachada");
                }
            });

            //holder.fullView.setOnClickListener(clickListener);


        }catch(Exception e){e.printStackTrace();}

        TextView direccionTextView;
        TextView tipoDomicilioTextView;
    }

    private void setEditTextListener(){
        cantidadAbonotEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (Integer.parseInt(editable.toString()) < cantidadAbonoMinimo) {
                        cantidadAbonoInputLayout.setError(getString(R.string.detallecobro_error_abono, formatMoney(planFirebase.getTotalAPagar()/planFirebase.getNumeroDePagosARealizar())));
                    }else{
                        cantidadAbonoInputLayout.setError(null);
                    }
                }catch (Exception e){
                    cantidadAbonoInputLayout.setError(getString(R.string.detallecobro_exception_abono));
                }
            }
        });
    }

    private void setPlanData(){
        planClienteTextView.setText(planFirebase.getStID());
        planTextView.setText(planFirebase.getStID());
        costoTotalTextView.setText(formatMoney(planFirebase.getTotalAPagar()));
        numeroAbonoTextView.setText(
                getString(R.string.format_abono,planFirebase.getPagosRealizados()+1)
        );

        cantidadAbonoTextView.setText(formatMoney(planFirebase.getTotalAPagar()/planFirebase.getNumeroDePagosARealizar()));
        cantidadAbonotEditText.setText(""+Math.ceil(planFirebase.getTotalAPagar()/planFirebase.getNumeroDePagosARealizar()), TextView.BufferType.EDITABLE);
        cantidadAbonoMinimo=planFirebase.getTotalAPagar()/planFirebase.getNumeroDePagosARealizar();
        cantidadPagadoTextView.setText(formatMoney(planFirebase.getTotalAPagar()-planFirebase.getSaldo()));
        setEditTextListener();

        cantidadSaldoActualTextView.setText(formatMoney(planFirebase.getSaldo()));
        cantidadSaldoVencidoTextView.setText(formatMoney(0.0f));

        if((planFirebase.getTotalAPagar()-planFirebase.getSaldo())>1){
            int porciento = Math.round(((planFirebase.getTotalAPagar()-planFirebase.getSaldo())*100)/planFirebase.getTotalAPagar());
            porcentajeTextView.setText( getString(R.string.format_porcientos,porciento) );

            if(porciento>=5) {
                ConstraintSet set = new ConstraintSet();
                set.clone((ConstraintLayout) findViewById(R.id.constraintLayoutDetalleCobro));
                set.setHorizontalBias(porcentajeTextView.getId(), (porciento - 5) / 100f);
                set.applyTo((ConstraintLayout) findViewById(R.id.constraintLayoutDetalleCobro));
            }
        }
        progressPorcentaje.setMax(Math.round(planFirebase.getTotalAPagar()));
        progressPorcentaje.setProgress(Math.round(planFirebase.getTotalAPagar()-planFirebase.getSaldo()));
        //progressPorcentaje.setScaleY(6f);
        //porcentajeTextView;
    }

    private void setEmpleadoData(){
        String nombre = perfilEmpleado.getNombre();
        cobradorNombreTextView.setText(nombre);
        String[] splitNombre = nombre.split(" ");
        letrasCobradorTextView.setText(getString(
                R.string.detallecliente_format_letras,
                splitNombre[0].toUpperCase().substring(0,1),
                splitNombre[1].toUpperCase().substring(0,1)
        ));
    }

    private void setTime(){
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();

        /* debug: is it local time? */
        Log.d("Time zone: ", tz.getDisplayName());

        /* date formatter in local timezone */
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        sdf.setTimeZone(tz);

        /* print your timestamp and double check it's the date you expect */
        String localTime = sdf.format(new Date(System.currentTimeMillis())); // I assume your timestamp is in seconds and you're converting to milliseconds?
        Log.d("LocalTime: ", localTime);

        String[] months = {"Enero","Febrero", "Marzo", "Abril", "Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};

        fechaTextView.setText(getString(R.string.format_fullfecha,
                cal.get(Calendar.DAY_OF_MONTH),
                months[cal.get(Calendar.MONTH)],
                cal.get(Calendar.YEAR)));
    }

    private String formatMoney(float f){
        return getString(R.string.format_cantidad,NumberFormat.getNumberInstance(Locale.US).format(Math.ceil(f)));
    }

    private void processPay(){

        if (ContextCompat.checkSelfPermission(DetalleCobro.this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Granted!");
            Awareness.getSnapshotClient(DetalleCobro.this).getLocation().addOnSuccessListener(new OnSuccessListener<LocationResponse>() {
                @Override
                public void onSuccess(LocationResponse locationResponse) {
                    ticketFirebase = new Ticket_Firebase();
                    ticketFirebase.setClienteID(clienteFirebase.getStID());
                    ticketFirebase.setPlanID(planFirebase.getStID());
                    ticketFirebase.setLat(locationResponse.getLocation().getLatitude());
                    ticketFirebase.setLon(locationResponse.getLocation().getLongitude());
                    ticketFirebase.setNumAbono(planFirebase.getPagosRealizados()+1);
                    ticketFirebase.setMonto(Math.round(planFirebase.getTotalAPagar()/planFirebase.getNumeroDePagosARealizar()));
                    ticketFirebase.setNuevoSaldo(Math.round(planFirebase.getSaldo()-ticketFirebase.getMonto()));
                    //Time stuff
                    Long tsLong = System.currentTimeMillis()/1000;
                    Calendar cal = Calendar.getInstance();
                    ticketFirebase.setKeyDiaCreacion(""+cal.get(Calendar.DAY_OF_MONTH)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.YEAR));

                    /*
                    TimeZone tz = cal.getTimeZone();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    sdf.setTimeZone(tz);
                    String localTime = sdf.format(new Date(System.currentTimeMillis()));
                    Log.d(TAG,localTime);
                    */
                    ticketFirebase.setCreatedAt(tsLong);

                    String ticketID = planFirebase.getStID().concat("_"+tsLong);
                    ticketFirebase.setStID(ticketID);

                    writeTicketToDB(ticketFirebase);

                }
            });
        }

    }

    private void showTicket(){
        Intent intent = new Intent(DetalleCobro.this, PrintTicket.class);
        //intent.putExtra("ticketID", ticketFirebase.getStID());
        intent.putExtra("ticketID", "PaMe7800_1527914926_P3A7S2F3_785_1528095985");
        startActivity(intent);
    }

    private void writeTicketToDB(Ticket_Firebase ticketFirebas){
        WS.writeTicketFirebase(ticketFirebas, DetalleCobro.this);
    }

    private void connectToAwarenessAPI(){
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

    // --------------------------------------------------------- //
    // ---------------- FIREBASE IMPLEMENTATION ---------------- //
    //---------------------------------------------------------- //


    @Override
    public void firebaseCompleted(boolean hasError) {
        Log.d(TAG, "TODO SALIO BIEN");

        progressDialogFragment.changeTitle("Ticket creado exitosamente!");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialogFragment.dismiss();
            }
        }, 2000);

        buttonPagar.setVisibility(View.GONE);
        buttonImprimir.setVisibility(View.VISIBLE);
    }
}
