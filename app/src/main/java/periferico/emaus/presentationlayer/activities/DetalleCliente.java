package periferico.emaus.presentationlayer.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.firebase_objects.Cliente_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.utils.AppCompatActivity_Job;
import periferico.emaus.presentationlayer.fragments.InfoClienteFrag;
import periferico.emaus.presentationlayer.fragments.PlanesFrag;

public class DetalleCliente extends AppCompatActivity_Job {

    private static final String TAG = "DetalleClienteDebug";

    private Toolbar toolbar;

    private TextView textviewLetras;
    private TextView textviewStatus;

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detalle_cliente);

        instanciateObjects();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            try {
                String[] splitNombre = getIntent().getStringExtra("stNombre").split(" ");
                Log.d(TAG, "nombre completo = " + getIntent().getStringExtra("stNombre") + " size=" + getIntent().getStringExtra("stNombre").split(" ").length);
                getSupportActionBar().setTitle(splitNombre[0] + " " + splitNombre[1]);

                String letras = getString(
                        R.string.detallecliente_format_letras,
                        splitNombre[0].substring(0, 1),
                        splitNombre[1].substring(0, 1)
                );
                textviewLetras.setText(letras);

                String[] statusArray = getResources().getStringArray(R.array.array_status);
                textviewStatus.setText(statusArray[getIntent().getIntExtra("intStatus", 0)]);
            }catch(Exception e){e.printStackTrace();}
        }

        CollapsingToolbarLayout ctl =  findViewById(R.id.collapsingToolbarLayout);
        if(ctl!=null) {
            ctl.setContentScrim(getColorDrawable(getIntent().getIntExtra("intStatus", 0)));
            //ctl.setContentScrimColor(getColor_(getIntent().getIntExtra("intStatus",0)));
            ctl.setBackground(getColorDrawable(getIntent().getIntExtra("intStatus", 0)));
            Log.d(TAG, "Collapsing IS OK");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(getColor_(getIntent().getIntExtra("intStatus",0)));
        tabLayout.setSelectedTabIndicatorColor(Color.BLACK);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.containerPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    private void instanciateObjects(){
        textviewLetras = findViewById(R.id.detallecliente_textview_letras);
        textviewStatus = findViewById(R.id.detallecliente_textview_status);
    }


    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    private ColorDrawable getColorDrawable(int status){
        switch(status){
            case Cliente_Firebase.STATUS_ACTIVO:{ return new ColorDrawable(getColor_(status)); }
            case Cliente_Firebase.STATUS_ENVERIFICACION:{ return new ColorDrawable(getColor_(status)); }
            case Cliente_Firebase.STATUS_PROSPECTO:{ return new ColorDrawable(getColor_(status)); }
        }
        return null;
    }

    private int getColor_(int status){
        switch(status){
            case Cliente_Firebase.STATUS_ACTIVO:{
                return ContextCompat.getColor(DetalleCliente.this, R.color.colorAccent);
            }
            case Cliente_Firebase.STATUS_ENVERIFICACION:{
                return Color.parseColor("#9B9B9B");
            }
            case Cliente_Firebase.STATUS_PROSPECTO:{
                return Color.parseColor("#F3F3F3");
            }
        }
        return 0;
    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==0){
                InfoClienteFrag infoClienteFrag =  InfoClienteFrag.newInstance();
                infoClienteFrag.setID(getIntent().getStringExtra("clientID"));
                return infoClienteFrag;
            }
            if(position==1){
                PlanesFrag planesFrag = PlanesFrag.newInstance();
                planesFrag.setStID(getIntent().getStringExtra("clientID"));
                return planesFrag;
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_tabs, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
}
