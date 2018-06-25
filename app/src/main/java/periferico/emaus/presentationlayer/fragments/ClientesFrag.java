package periferico.emaus.presentationlayer.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.adapters.AdapterClientes;
import periferico.emaus.domainlayer.firebase_objects.Cliente_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.presentationlayer.activities.DetalleCliente;
import periferico.emaus.presentationlayer.activities.NuevoCliente;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClientesFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClientesFrag extends Fragment implements
        WS.FirebaseKeyListRetrievedListener,
        AdapterClientes.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView labelNoHay;

    private ArrayList<String> mDataset;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ClientesFrag.
     */
    public static ClientesFrag newInstance() {
        return new ClientesFrag();
    }
    public ClientesFrag() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clientes, container, false);

        labelNoHay = view.findViewById(R.id.clientes_label_nohay);

        // Inflate the layout for this fragment
        mRecyclerView = view.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mDataset = new ArrayList<String>();

        mAdapter = new AdapterClientes(mDataset, ClientesFrag.this, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout=view.findViewById(R.id.swipeRefreshLayout);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        view.findViewById(R.id.fabNuevoCliente).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NuevoCliente.class);
                startActivity(intent);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(this);

        //mSwipeRefreshLayout.setRefreshing(true);
        //WS.readClientListFirebase(ClientesFrag.this);
        //refreshClientList();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshClientList();
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    private void refreshClientList(){
        mSwipeRefreshLayout.setRefreshing(true);
        WS.readClientListFirebase(ClientesFrag.this);
    }

    // --------------------------------------------------------- //
    // ---------------- FIREBASE IMPLEMENTATION ---------------- //
    //---------------------------------------------------------- //


    @Override
    public void firebaseKeyListRetrieved(ArrayList<String> keys) {
        labelNoHay.setVisibility((keys.size()>0) ? View.GONE : View.VISIBLE);
        mDataset.clear();
        mDataset.addAll(keys);
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /*
    @Override
    public void firebaseCompleted(ArrayList<Object_Firebase> arrayList) {
        labelNoHay.setVisibility((arrayList.size()>0) ? View.GONE : View.VISIBLE);
        mDataset.clear();
        for(Object_Firebase arrayObject : arrayList){
            Cliente_Firebase cliente = new Cliente_Firebase();
            cliente.setStID(arrayObject.getStID());
            mDataset.add(cliente);
        }
        //mDataset.addAll(arrayList);
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }
    */

    // ------------------------------------------------------------- //
    // ---------------- ONCLICK ITEM IMPLEMENTATION ---------------- //
    //-------------------------------------------------------------- //


    @Override
    public void onItemClick(String clienteID, String fullName, int status) {
        Intent intent = new Intent(getContext(), DetalleCliente.class);
        //Log.d("ClientesFrag","stID="+cliente.toString());
        intent.putExtra("clientID",clienteID);
        intent.putExtra("stNombre",fullName);
        intent.putExtra("intStatus",status);

        startActivity(intent);
    }

    // ------------------------------------------------------------- //
    // ---------------- SWIPEREFRESH IMPLEMENTATION ---------------- //
    //-------------------------------------------------------------- //

    @Override
    public void onRefresh() {
        refreshClientList();
    }


}
