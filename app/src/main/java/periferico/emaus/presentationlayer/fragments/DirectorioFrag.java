package periferico.emaus.presentationlayer.fragments;


import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.adapters.AdapterClientes;
import periferico.emaus.domainlayer.adapters.AdapterDirectorio;
import periferico.emaus.domainlayer.firebase_objects.Cliente_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.presentationlayer.activities.DetalleCliente;
import periferico.emaus.presentationlayer.activities.NuevoCliente;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DirectorioFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DirectorioFrag extends Fragment implements
        WS.FirebaseArrayRetreivedListener,
        AdapterDirectorio.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "DirectorioFragDebug";

    private static final int MY_PERMISSIONS_REQUEST_PHONE_CALL = 12345;
    private String telToCall;

    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    Toolbar toolbar;

    private ArrayList<Object_Firebase> mDataset;
    private ArrayList<Object_Firebase> mDatasetBackup;
    ProgressBar progressBar;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ClientesFrag.
     */
    public static DirectorioFrag newInstance() {
        return new DirectorioFrag();
    }
    public DirectorioFrag() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_directorio, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        if(toolbar!=null) {
            toolbar.inflateMenu(R.menu.menu_directorio);

            SearchManager searchManager =
                    (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView =
                    (SearchView) toolbar.getMenu().findItem(R.id.action_search).getActionView();
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getActivity().getComponentName()));

            ((SearchView) toolbar.getMenu().findItem(R.id.action_search).getActionView()).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.d(TAG,"string="+newText+" size="+newText.length());

                    if(newText.length()>0) {
                        ArrayList<Object_Firebase> newList = ((AdapterDirectorio) mAdapter).searchInList(newText);
                        mDataset.clear();
                        mDataset.addAll(newList);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mDataset.clear();
                        mDataset.addAll(mDatasetBackup);
                        mAdapter.notifyDataSetChanged();
                    }
                    return false;
                }
            });
        }

        // Inflate the layout for this fragment
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mDataset = new ArrayList<>();
        mDatasetBackup = new ArrayList<>();

        mAdapter = new AdapterDirectorio(mDataset, DirectorioFrag.this, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout=view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(DirectorioFrag.this);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        progressBar = view.findViewById(R.id.progressDirectorio);

        //mSwipeRefreshLayout.setOnRefreshListener(this);

        //mSwipeRefreshLayout.setRefreshing(true);
        //WS.readClientListFirebase(DirectorioFrag.this);
        WS.readClientAndDirectoryFirebase(DirectorioFrag.this);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {


        /*
        Log.d("MenuDebug", "debug itemID = " + item.getItemId());
        TransitionManager.beginDelayedTransition((ViewGroup) getActivity().findViewById(R.id.toolbar));
        MenuItemCompat.expandActionView(item);
        return true;
        */


        switch(item.getItemId()) {
            case R.id.action_search:
                TransitionManager.beginDelayedTransition((ViewGroup) getActivity().findViewById(R.id.toolbar));
                item.expandActionView();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    private void refreshClientList(){

        Log.d(TAG, "startRefresh");

        mSwipeRefreshLayout.setRefreshing(true);
        mDataset.clear();
        WS.readClientAndDirectoryFirebase(DirectorioFrag.this);

    }

    // --------------------------------------------------------- //
    // ---------------- FIREBASE IMPLEMENTATION ---------------- //
    //---------------------------------------------------------- //

    @Override
    public void firebaseCompleted(ArrayList<Object_Firebase> arrayList) {

        Log.d(TAG, "firebaseCompleted");

        mDataset.addAll(arrayList);
        mDatasetBackup.addAll(arrayList);
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);

    }

    // ------------------------------------------------------------- //
    // ---------------- ONCLICK ITEM IMPLEMENTATION ---------------- //
    //-------------------------------------------------------------- //


    @Override
    public void onItemClick(int position, int tipoUsuario, String telToCall_) {

        if(tipoUsuario==Cliente_Firebase.TIPOUSUARIO_CLIENTE){
            /*
            Intent intent = new Intent(getContext(), DetalleCliente.class);
            Log.d("ClientesFrag","stID="+mDataset.get(position).toString());
            intent.putExtra("clientID",mDataset.get(position).getStID());
            intent.putExtra("stNombre",((Cliente_Firebase)mDataset.get(position)).getStNombre());
            intent.putExtra("intStatus",((Cliente_Firebase)mDataset.get(position)).getIntStatus());
            startActivity(intent);
            */
        }else{

            telToCall = telToCall_;

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
        /*
        Intent intent = new Intent(getContext(), DetalleCliente.class);
        intent.putExtra("clientID",mDataset.get(position).getStID());
        intent.putExtra("stNombre",((Cliente_Firebase)mDataset.get(position)).getStNombre());
        intent.putExtra("intStatus",((Cliente_Firebase)mDataset.get(position)).getIntStatus());
        startActivity(intent);
        */
    }

    // ------------------------------------------------------------- //
    // ---------------- SWIPEREFRESH IMPLEMENTATION ---------------- //
    //-------------------------------------------------------------- //

    @Override
    public void onRefresh() {
        refreshClientList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_PHONE_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "5555555555"));
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
}
