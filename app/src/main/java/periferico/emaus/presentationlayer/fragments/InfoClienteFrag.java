package periferico.emaus.presentationlayer.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.IDNA;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.adapters.AdapterDirsLista;
import periferico.emaus.domainlayer.adapters.AdapterTelsLista;
import periferico.emaus.domainlayer.firebase_objects.Cliente_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.objetos.Direcciones;
import periferico.emaus.domainlayer.objetos.Telefonos;
import periferico.emaus.presentationlayer.activities.DetalleCliente;
import periferico.emaus.presentationlayer.activities.NuevoPlan;

public class InfoClienteFrag extends Fragment implements
        WS.FirebaseObjectRetrieved,
        AdapterTelsLista.OnItemClickListener,
        AdapterDirsLista.OnItemDirClickListener{

    private static final String TAG = "InfoClienteFragDebug";
    private static final int MY_PERMISSIONS_REQUEST_PHONE_CALL = 12345;

    String stID;

    private TextView textviewFechaRegistro;
    private TextView textviewVendedor;
    private TextView textviewNotas;
    private TextView textviewOcupacion;
    private TextView textviewEstadoCivil;
    private TextView textviewFechNac;
    private TextView textviewGenero;
    private TextView textviewReligion;
    private TextView textviewEmail;

    RecyclerView mRecyclerViewTelefonos;
    private RecyclerView.Adapter mAdapterTelefonos;
    android.support.v7.widget.LinearLayoutManager mLayoutManagerTelefonos;
    private ArrayList<Telefonos> mDatasetTelefonos;

    RecyclerView mRecyclerViewDirecciones;
    private RecyclerView.Adapter mAdapterDirecciones;
    android.support.v7.widget.LinearLayoutManager mLayoutManagerDirecciones;
    private ArrayList<Direcciones> mDatasetDirecciones;

    Cliente_Firebase clienteFirebase;

    public InfoClienteFrag() { }
    public static InfoClienteFrag newInstance() {
        return new InfoClienteFrag();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            stID = savedInstanceState.getString("clientID");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("clientID", stID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_cliente_info, container, false);

        //Instanciar Objetos
        instanciateObjects(view);

        //Set Listeners
        setListeners(view);

        try{
            WS.readClientFirebase(stID, InfoClienteFrag.this);
        }catch(Exception e){e.printStackTrace();}

        return view;
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    private void instanciateObjects(View v){

        mRecyclerViewTelefonos = v.findViewById(R.id.recyclerViewTels);
        mRecyclerViewTelefonos.setHasFixedSize(true);
        mDatasetTelefonos = new ArrayList<>();
        mAdapterTelefonos = new AdapterTelsLista(mDatasetTelefonos, InfoClienteFrag.this);
        mRecyclerViewTelefonos.setAdapter(mAdapterTelefonos);
        mLayoutManagerTelefonos = new LinearLayoutManager(getActivity());
        mRecyclerViewTelefonos.setLayoutManager(mLayoutManagerTelefonos);

        mRecyclerViewDirecciones = v.findViewById(R.id.recyclerViewDirs);
        mRecyclerViewTelefonos.setHasFixedSize(true);
        mDatasetDirecciones = new ArrayList<>();
        mAdapterDirecciones = new AdapterDirsLista(mDatasetDirecciones, InfoClienteFrag.this);
        mRecyclerViewDirecciones.setAdapter(mAdapterDirecciones);
        mLayoutManagerDirecciones = new LinearLayoutManager(getActivity());
        mRecyclerViewDirecciones.setLayoutManager(mLayoutManagerDirecciones);

        textviewFechaRegistro = v.findViewById(R.id.detallecliente_textview_fechaRegistro);
        textviewVendedor = v.findViewById(R.id.detallecliente_textview_vendedor);
        textviewNotas = v.findViewById(R.id.detallecliente_textview_notas);
        textviewOcupacion = v.findViewById(R.id.detallecliente_textview_ocupacion);
        textviewEstadoCivil = v.findViewById(R.id.detallecliente_textview_estadocivil);
        textviewFechNac  = v.findViewById(R.id.detallecliente_textview_fechanac);
        textviewGenero = v.findViewById(R.id.detallecliente_textview_genero);
        textviewReligion = v.findViewById(R.id.detallecliente_textview_religion);
        textviewEmail = v.findViewById(R.id.detallecliente_textview_email);
    }

    private void setListeners(View view){
        view.findViewById(R.id.detallecliente_button_nuevoplan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NuevoPlan.class);
                intent.putExtra("stID",stID);
                startActivity(intent);
            }
        });
    }


    private void displayInfo(){
        Calendar calCreated = Calendar.getInstance();
        calCreated.setTimeInMillis(clienteFirebase.getCreatedAt()*1000);
        textviewFechaRegistro.setText(getString(
            R.string.nuevocliente_formatted_fecha,
            calCreated.get(Calendar.DAY_OF_MONTH),
            getResources().getStringArray(R.array.array_meses)[calCreated.get(Calendar.MONTH)],
            calCreated.get(Calendar.YEAR)
        ));

        textviewVendedor.setText(clienteFirebase.getStVendedor());
        textviewNotas.setText(clienteFirebase.getStNotas());
        textviewOcupacion.setText(clienteFirebase.getStOcupacion());
        if(clienteFirebase.getIntEstadoCivil()>0)
            textviewEstadoCivil.setText(getResources().getStringArray(R.array.array_estadoCivil)[clienteFirebase.getIntEstadoCivil()-1]);
        textviewFechNac.setText(clienteFirebase.getStFechaNac());
        if(clienteFirebase.getIntGenero()>0)
            textviewGenero.setText(getResources().getStringArray(R.array.array_genero)[clienteFirebase.getIntGenero()-1]);
        if(clienteFirebase.getIntReligion()>0)
            textviewReligion.setText(getResources().getStringArray(R.array.array_religiones)[clienteFirebase.getIntReligion()-1]);
        textviewEmail.setText(clienteFirebase.getStEmail());

        //HashMap<String,Integer> tels = clienteFirebase.getTelefonos();
        List<Telefonos> tels = clienteFirebase.getTelefonos();
        //Log.d(TAG, "hashmap displayInfo = "+tels.toString());
        mDatasetTelefonos.addAll(tels);
        mAdapterTelefonos.notifyDataSetChanged();

        List<Direcciones> dirs = clienteFirebase.getDirecciones();
        if(dirs!=null) {
            mDatasetDirecciones.addAll(dirs);
            mAdapterDirecciones.notifyDataSetChanged();
        }
    }


    public void setID(String stID_){
        stID=stID_;
    }

    // -------------------------------------------------------- //
    // ---------------- ONCLICK IMPLEMENTATION ---------------- //
    //--------------------------------------------------------- //

    String telToCall;

    @Override
    public void onItemDirClick(int position) {

    }

    @Override
    public void onItemClick(int position) {
        Telefonos tel = mDatasetTelefonos.get(position);

        /*
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE);

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel.get("stTelefono")));
        startActivity(intent);
        */

        telToCall = tel.getStTelefono();

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CALL_PHONE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_PHONE_CALL);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            Toast.makeText(getActivity(),"Llamando...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telToCall));
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_PHONE_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telToCall));
                    startActivity(intent);

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


    // --------------------------------------------------------- //
    // ---------------- FIREBASE IMPLEMENTATION ---------------- //
    //---------------------------------------------------------- //


    @Override
    public void firebaseObjectRetrieved(Object_Firebase objectFirebase) {

        clienteFirebase = (Cliente_Firebase)objectFirebase;
        Log.d(TAG, clienteFirebase.toString());

        //Log.d(TAG, tels.toString());

        displayInfo();
    }
}
