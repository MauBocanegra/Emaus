package periferico.emaus.presentationlayer.fragments;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.adapters.AdapterPerfilCobrador;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Ticket_Firebase;
import periferico.emaus.domainlayer.objetos.TicketWrapper;
import periferico.emaus.presentationlayer.activities.PrintCorte;
import periferico.emaus.presentationlayer.dialogs.DatePickerDiag;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilCobrador extends Fragment implements
        WS.FirebaseArrayRetreivedListener,
        DatePickerDiag.DateSetListener{

    private Toolbar toolbar;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private ArrayList<TicketWrapper> mDataset;

    TextView pagosTextView;
    TextView pagadoTextView;
    View buttonFecha;
    Button buttonCorte;
    TextView labelFecha;

    //----------------

    int dia; int mes; int anio;

    int pagos=0;
    float pagado=0.0f;

    //----------------

    View view;

    public static PerfilCobrador newInstance(){return new PerfilCobrador();}
    public PerfilCobrador() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);
        view = inflater.inflate(R.layout.fragment_perfil_cobrador, container, false);

        initObjects();

        setListeners();

        setDayInList();

        return view;
    }


    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    private void initObjects(){
        Calendar calendar = Calendar.getInstance();
        anio = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);

        //--------RecyclerView
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mDataset = new ArrayList<>();
        mAdapter = new AdapterPerfilCobrador(mDataset, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        buttonFecha = view.findViewById(R.id.perfilcobrador_button_fecha);
        labelFecha = view.findViewById(R.id.perfilcobrador_label_fecha);
        buttonCorte = view.findViewById(R.id.perfilcobrador_button_corte);

        pagadoTextView = view.findViewById(R.id.perfilcobrador_pagado);
        pagosTextView = view.findViewById(R.id.perfilcobrador_pagos);
    }

    private void setListeners(){
        buttonFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDiag datePickerDiag = new DatePickerDiag();
                datePickerDiag.setFecha(dia, mes, anio);
                datePickerDiag.setDateSetListener(PerfilCobrador.this);
                datePickerDiag.showNow(getChildFragmentManager(), "DatePicker");
            }
        });

        buttonCorte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PrintCorte.class);
                intent.putExtra("stFecha", ""+dia+"-"+(mes+1)+"-"+anio);
                //intent.putExtra("ticketID", "PaMe7800_1527914926_P3A7S2F3_785_1528095985");
                startActivity(intent);
            }
        });
    }

    private void setDayInList(){
        mDataset.clear();
        labelFecha.setText(""+dia+" / "+getMesInString()+" / "+anio);
        WS.descargarTicketsPorFechaYCobrador(""+dia+"-"+(mes+1)+"-"+anio,PerfilCobrador.this);
    }

    private String getMesInString(){
        String[] meses = {"Ene", "Feb","Mar","Abr","Mayo","Jun","Jul","Ago","Sep","Oct","Nov","Dic"};
        return meses[mes];
    }

    @Override
    public void onDateSet(DatePicker datePicker, int anio, int mes, int dia) {
        Log.d("PerfilCobrador", "date="+dia+"-"+mes+"-"+anio);
        this.dia=dia; this.mes=mes; this.anio=anio;
        setDayInList();
    }

    // --------------------------------------------------------- //
    // ---------------- FIREBASE IMPLEMENTATION ---------------- //
    //---------------------------------------------------------- //


    @Override
    public void firebaseCompleted(ArrayList<Object_Firebase> arrayList) {
        buttonCorte.setVisibility(arrayList.size()==0 ? View.GONE : View.VISIBLE);
        pagos=0; pagado=0;
        for(Object_Firebase obj : arrayList){
            Ticket_Firebase ticketFirebase = (Ticket_Firebase)obj;
            pagos++;
            pagado+=ticketFirebase.getMonto();
            mDataset.add(new TicketWrapper(ticketFirebase));
        }
        pagosTextView.setText(""+mDataset.size());
        pagadoTextView.setText(formatMoney(pagado));

        mAdapter.notifyDataSetChanged();
    }

    /*
    @Override
    public void firebaseKeyListRetrieved(ArrayList<String> keys) {
        pagos=0; pagado=0;
        for(String key : keys){
            mDataset.add(new TicketWrapper(key));
        }
        mAdapter.notifyDataSetChanged();
    }
    */

    /*
    private void afterSet(){
        Log.d("SomeTag","mDataset="+mDataset.size());
        for(TicketWrapper ticketWrapper : mDataset){
            pagado+=ticketWrapper.getTicketFirebase().getMonto();
        }

        pagosTextView.setText(""+mDataset.size());
        pagadoTextView.setText(formatMoney(pagado));
    }
    */

    private String formatMoney(float f){
        return getString(R.string.format_cantidad, NumberFormat.getNumberInstance(Locale.US).format(Math.ceil(f)));
    }
}
