package periferico.emaus.presentationlayer.fragments;


import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.adapters.AdapterCatalog;
import periferico.emaus.domainlayer.firebase_objects.CatalogItem_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.presentationlayer.dialogs.ImageDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class CatalogFrag extends Fragment implements
        WS.FirebaseArrayRetreivedListener,
        AdapterCatalog.OnPictureClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<Object_Firebase> mDataset;
    private Parcelable layoutManagerSavedState;

    private ImageDialogFragment imageDialogFragment;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment CatalogFrag.
     */
    public static CatalogFrag newInstance(){return new CatalogFrag();}
    public CatalogFrag() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);
        // Inflate the layout for this fragment
        mRecyclerView = view.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mDataset = new ArrayList<Object_Firebase>();

        mAdapter = new AdapterCatalog(mDataset, (AppCompatActivity) getActivity(), CatalogFrag.this);
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout=view.findViewById(R.id.swipeRefreshLayout);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mSwipeRefreshLayout.setOnRefreshListener(CatalogFrag.this);

        WS.readCatalogListFirebase(CatalogFrag.this);

        return view;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("SavedState",mRecyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(bundle);
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        try {
            layoutManagerSavedState = savedInstanceState.getParcelable("SavedState");
        }catch(Exception e){e.printStackTrace();}
        super.onViewStateRestored(savedInstanceState);
    }

    // --------------------------------------------------------------- //
    // ---------------- FIREBASE ARRAY IMPLEMENTATION ---------------- //
    //---------------------------------------------------------------- //

    @Override
    public void firebaseCompleted(ArrayList<Object_Firebase> arrayList) {
        Log.d("WTF","retreived!");
        mDataset.clear();
        mDataset.addAll(arrayList);
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
        if(layoutManagerSavedState!=null){
            mRecyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
        }
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    private void refreshCatalogList(){
        Log.d("WTF","refreshingCat!");
        WS.readCatalogListFirebase(CatalogFrag.this);
    }

    // ------------------------------------------------------------- //
    // ---------------- SWIPEREFRESH IMPLEMENTATION ---------------- //
    //-------------------------------------------------------------- //

    @Override
    public void onRefresh() {
        Log.d("WTF","onRefresh");
        refreshCatalogList();
    }

    // ------------------------------------------------------------ //
    // ---------------- ONITEMCLICK IMPLEMENTATION ---------------- //
    //------------------------------------------------------------- //


    @Override
    public void onPictureClick(int position, String imgURL) {
        imageDialogFragment = new ImageDialogFragment();
        imageDialogFragment.setImage(imgURL);
        imageDialogFragment.show(getChildFragmentManager(), "CatalogFrag");
    }
}
