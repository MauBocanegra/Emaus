package periferico.emaus.presentationlayer.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.adapters.AdapterPlanes;
import periferico.emaus.domainlayer.adapters.AdapterPlanesLegacy;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlanesFrag extends Fragment implements
        WS.FirebaseArrayRetreivedListener,
        SwipeRefreshLayout.OnRefreshListener{

    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<Object_Firebase> mDataset;
    private String stID;
    private TextView labelNoHayPlanes;

    public static PlanesFrag newInstance() {
        return new PlanesFrag();
    }
    public PlanesFrag(){}

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
        View view = inflater.inflate(R.layout.fragment_planes, container, false);

        //Instanciamos y asignamos todos los objetos que se usen
        instanciateObjects(view);

        //iniciamos el refresh de los planes
        //refreshPlanesList();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //iniciamos el refresh de los planes
        refreshPlanesList();
    }

    // ----------------------------------------------------- //
    // ---------------- GETTERS AND SETTERS ---------------- //
    //------------------------------------------------------ //

    public String getStID() {
        return stID;
    }

    public void setStID(String stID) {
        this.stID = stID;
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    private void instanciateObjects(View view){

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mDataset = new ArrayList<Object_Firebase>();

        mAdapter = new AdapterPlanes(mDataset, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout=view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(PlanesFrag.this);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        labelNoHayPlanes = view.findViewById(R.id.planes_label_nohay);
    }

    private void refreshPlanesList(){
        mSwipeRefreshLayout.setRefreshing(true);
        //WS.readPlansListFirebase(stID, PlanesFrag.this);
        WS.readPlanesListFirebase(stID, PlanesFrag.this);
    }

    // --------------------------------------------------------- //
    // ---------------- FIREBASE IMPLEMENTATION ---------------- //
    //---------------------------------------------------------- //

    @Override
    public void firebaseCompleted(ArrayList<Object_Firebase> arrayList) {

        labelNoHayPlanes.setVisibility((arrayList.size()>0) ? View.GONE : View.VISIBLE);

        mDataset.clear();
        mDataset.addAll(arrayList);
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    // ------------------------------------------------------------- //
    // ---------------- SWIPEREFRESH IMPLEMENTATION ---------------- //
    //-------------------------------------------------------------- //

    @Override
    public void onRefresh() {
        refreshPlanesList();
    }
}
