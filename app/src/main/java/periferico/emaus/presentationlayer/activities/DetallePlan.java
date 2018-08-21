package periferico.emaus.presentationlayer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import periferico.emaus.R;
import periferico.emaus.domainlayer.DatesCalc;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.adapters.AdapterPerfilCobrador;
import periferico.emaus.domainlayer.firebase_objects.Cliente_Firebase;
import periferico.emaus.domainlayer.firebase_objects.ConfiguracionPlanes_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Plan_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Ticket_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.Financiamientos_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.FormasPago_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.FrecuenciasPago_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.MatrizPlanes_Firebase;
import periferico.emaus.domainlayer.objetos.TicketWrapper;
import periferico.emaus.domainlayer.utils.AppCompatActivity_Job;
import periferico.emaus.presentationlayer.dialogs.CambiarVisitaDialog;

public class DetallePlan extends AppCompatActivity_Job implements
        WS.OnNetworkListener,
        WS.FirebaseArrayRetreivedListener{

    TextView bannerNetwork;

    Cliente_Firebase clienteFirebase;
    Plan_Firebase planFirebase;

    private MatrizPlanes_Firebase configPlan;
    private Financiamientos_Firebase mensualidadesFirebase;
    private FormasPago_Firebase formasPagoFirebase;
    private Financiamientos_Firebase financiamientoFirebase;
    private FrecuenciasPago_Firebase frecuenciasPagoFirebase;

    private static final String TAG = "DetallePlanDebug";
    private Toolbar toolbar;

    //-------------------------

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private ArrayList<TicketWrapper> mDataset;

    //-------------------------

    TextView letrasClienteTextView;
    TextView clienteNombreTextView;
    TextView planTituloTextView;
    TextView clientePlanTextView;
    TextView planStatusTextView;
    TextView tipoPlanTextView;
    TextView modeloAtaudTextView;
    TextView servicioTextView;
    TextView financiamientoTextView;
    TextView pagosTextView;
    TextView formaPagoTextView;
    TextView cantidadPagadoTextView;
    TextView cantidadSaldoActualTextView;
    TextView cantidadSaldoVencidoTextView;
    TextView porcentajeTextView;
    ProgressBar progressPorcentaje;
    Button buttonHacerCobro;
    Button buttonCambiarDiaPago;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_plan);

        instanciateObjects();

        setListeners();

        instanciateToolbar();

        downloadClienteCobro();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "NuevoCliente ONSTART-----");
        WS.setNetworkListener(DetallePlan.this);
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //


    private void instanciateObjects(){

        bannerNetwork = findViewById(R.id.nuevocliente_banner_offline);

        mRecyclerView = findViewById(R.id.recyclerView);
        mDataset = new ArrayList<>();
        mAdapter = new AdapterPerfilCobrador(mDataset, DetallePlan.this);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(DetallePlan.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        letrasClienteTextView = findViewById(R.id.detalleplan_nombre_letras);
        clienteNombreTextView = findViewById(R.id.detalleplan_cliente);
        planStatusTextView = findViewById(R.id.detalleplan_plan_status);
        clientePlanTextView = findViewById(R.id.detalleplan_plan_cliente);
        planTituloTextView = findViewById(R.id.detalleplan_plan_titulo);
        tipoPlanTextView = findViewById(R.id.detalleplan_plan_tipoplan);
        modeloAtaudTextView = findViewById(R.id.detalleplan_plan_ataud);
        servicioTextView = findViewById(R.id.detalleplan_plan_servicio);
        financiamientoTextView = findViewById(R.id.detalleplan_plan_financiamiento);
        pagosTextView = findViewById(R.id.detalleplan_plan_pagos);
        formaPagoTextView = findViewById(R.id.detalleplan_plan_formapago);
        cantidadPagadoTextView = findViewById(R.id.detalleplan_pagado);
        cantidadSaldoActualTextView = findViewById(R.id.detalleplan_saldoactual);
        cantidadSaldoVencidoTextView = findViewById(R.id.detalleplan_saldovencido);
        porcentajeTextView = findViewById(R.id.detalleplan_porcentaje);
        progressPorcentaje = findViewById(R.id.detalleplan_progress_porcentaje);
        buttonHacerCobro = findViewById(R.id.detalleplan_button_cobro);
        buttonCambiarDiaPago = findViewById(R.id.detalleplan_button_cambiardiapago);

        switch (getString(R.string.flavor_string)){
            case "Ventas":{
                buttonHacerCobro.setVisibility(View.GONE);
                buttonCambiarDiaPago.setVisibility(View.GONE);
            }
        }

    }

    private void setListeners(){
        buttonHacerCobro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetallePlan.this, DetalleCobro.class);
                Bundle bundle = new Bundle();
                bundle.putString("clienteID", clienteFirebase.getStID());
                bundle.putString("planID", planFirebase.getStID());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void instanciateToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Plan");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void downloadClienteCobro(){
        String clienteID; String planID;
        clienteID = getIntent().getStringExtra("clienteID");
        planID = getIntent().getStringExtra("planID");

        Log.d(TAG, "Cliente="+getIntent().getStringExtra("clienteID")+" plan="+getIntent().getStringExtra("planID"));

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

        WS.readConfiguracionPlanes(new WS.FirebaseObjectRetrievedListener() {
            @Override
            public void firebaseObjectRetrieved(Object_Firebase objectFirebase) {
                ConfiguracionPlanes_Firebase configuracionPlanesFirebase = (ConfiguracionPlanes_Firebase)objectFirebase;

                configPlan = configuracionPlanesFirebase.getPlanes();
                mensualidadesFirebase = configuracionPlanesFirebase.getListamensualidades();
                formasPagoFirebase = configuracionPlanesFirebase.getListaformaspago();

                financiamientoFirebase = configuracionPlanesFirebase.getListamensualidades();
                frecuenciasPagoFirebase = configuracionPlanesFirebase.getListafrecuenciaspago();

                setConfigData();
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
    }

    private void setPlanData(){
        planTituloTextView.setText(planFirebase.getStID());
        planStatusTextView.setText("Activo");
        clientePlanTextView.setText(planFirebase.getStID());
        cantidadPagadoTextView.setText(formatMoney(planFirebase.getTotalAPagar()-planFirebase.getSaldo()));
        cantidadSaldoActualTextView.setText(formatMoney(planFirebase.getSaldo()));

        cantidadSaldoVencidoTextView.setText(formatMoney(0.0f));

        if((planFirebase.getTotalAPagar()-planFirebase.getSaldo())>1){
            int porciento = Math.round(((planFirebase.getTotalAPagar()-planFirebase.getSaldo())*100)/planFirebase.getTotalAPagar());
            porcentajeTextView.setText( getString(R.string.format_porcientos,porciento) );

            if(porciento>=5) {
                ConstraintSet set = new ConstraintSet();
                set.clone((ConstraintLayout) findViewById(R.id.constraintLayoutDetallePlan));
                set.setHorizontalBias(porcentajeTextView.getId(), (porciento - 5) / 100f);
                set.applyTo((ConstraintLayout) findViewById(R.id.constraintLayoutDetallePlan));
            }
        }
        progressPorcentaje.setMax(Math.round(planFirebase.getTotalAPagar()));
        progressPorcentaje.setProgress(Math.round(planFirebase.getTotalAPagar()-planFirebase.getSaldo()));
        Map<String,Object> mapKeys = planFirebase.getTickets();
        ArrayList<String> keys = new ArrayList<>();
        if(mapKeys!=null){
            keys = new ArrayList<>(mapKeys.keySet());
            WS.descargarTicketsPorPlan(DetallePlan.this, keys);
        }

        buttonCambiarDiaPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClickButtonCambiarDiaPago");
                //DatesCalc.calcularSigPagoPorFrecuencia(planFirebase.getCreatedAt(), planFirebase.getFrecuenciaPagoID());

                FragmentManager fragmentManager = getSupportFragmentManager();
                if(fragmentManager!=null){
                    CambiarVisitaDialog cambiarVisitaDiag = new CambiarVisitaDialog();
                    cambiarVisitaDiag.setPlanID(planFirebase.getStID());
                    cambiarVisitaDiag.show(fragmentManager, "dialog");
                }
            }
        });

        //WS.descargarTicketsPorFechaYCobrador(""+dia+"-"+(mes+1)+"-"+anio,PerfilCobrador.this);
    }

    private void setConfigData(){

        tipoPlanTextView.setText(configPlan.getPlanByPlanID(planFirebase.getPlanID()).getNombre());
        modeloAtaudTextView.setText(configPlan.getPlanByPlanID(planFirebase.getPlanID()).getAtaudByID(planFirebase.getAtaudID()).getNombre());
        servicioTextView.setText(configPlan.getPlanByPlanID(planFirebase.getPlanID()).getAtaudByID(planFirebase.getAtaudID()).getServicioByID(planFirebase.getServicioID()).getNombre());
        financiamientoTextView.setText(financiamientoFirebase.getFinanciamientoByID(planFirebase.getFinanciamientoID()).getNombre());
        pagosTextView.setText(frecuenciasPagoFirebase.getFrecuenciaByID(planFirebase.getFrecuenciaPagoID()).getNombre());
        formaPagoTextView.setText(formasPagoFirebase.getFormaPagoByID(planFirebase.getFormaPagoID()).getNombre());

    }

    private String formatMoney(float f){
        return getString(R.string.format_cantidad, NumberFormat.getNumberInstance(Locale.US).format(Math.ceil(f)));
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
    public void firebaseCompleted(ArrayList<Object_Firebase> arrayList) {
        for(Object_Firebase obj : arrayList){
            Ticket_Firebase ticketFirebase = (Ticket_Firebase)obj;
            mDataset.add(new TicketWrapper(ticketFirebase));
        }
        mAdapter.notifyDataSetChanged();
    }
}
