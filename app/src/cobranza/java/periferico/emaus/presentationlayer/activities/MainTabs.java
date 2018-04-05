package periferico.emaus.presentationlayer.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.utils.AppCompatActivity_Job;
import periferico.emaus.presentationlayer.fragments.CatalogFrag;
import periferico.emaus.presentationlayer.fragments.ClientesFrag;
import periferico.emaus.presentationlayer.fragments.DirectorioFrag;

public class MainTabs extends AppCompatActivity_Job implements WS.OnNetworkListener{

    private final String TAG = "MainTabs";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    TabLayout tabLayout;

    private static final int CLIENTES = 0;
    private static final int CATALOGO = 1;
    private static final int DIRECTORIO = 2;
    boolean doubleBackToExitPressedOnce = false;
    private TextView bannerNetworkListener;

    ClientesFrag clientesFrag;
    CatalogFrag catalogFrag;
    DirectorioFrag directorioFrag;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "MainTabs ONSTART-----");
        WS.setNetworkListener(MainTabs.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        WS.setNetworkListener(MainTabs.this);
        bannerNetworkListener = findViewById(R.id.maintabs_banner_sinconexion);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.BLACK);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        try{
            for(int i=0; i<tabLayout.getTabCount(); i++){

                if(i==CLIENTES){setTabIcon(i,R.drawable.ic_handshake, R.color.textColorSecondary);}
                if(i==CATALOGO){setTabIcon(i,R.drawable.ic_cart, R.color.textColorSecondary);}
                if(i==DIRECTORIO){setTabIcon(i,R.drawable.ic_agenda, R.color.textColorSecondary);}
            }
        }catch(Exception e){e.printStackTrace();}

        handleIntent(getIntent());

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

    }


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_tabs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragmentMain extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragmentMain() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragmentMain newInstance() {
            PlaceholderFragmentMain fragment = new PlaceholderFragmentMain();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_tabs, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.flavor_string));
            return rootView;
        }
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
            if(position==CLIENTES){ return PlaceholderFragmentMain.newInstance();}
            if(position==CATALOGO){ return PlaceholderFragmentMain.newInstance(); }
            if(position==DIRECTORIO){ return PlaceholderFragmentMain.newInstance();}
            return PlaceholderFragmentMain.newInstance();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //onBackPressed();
                Log.d("backDeb","BACK PRESSED!");
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed()
    {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Presiona nuevamente ATRÁS para salir", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

        return;
    }

    private void setTabIcon(int tab, int icon, int color){
        tabLayout.getTabAt(tab).setIcon(icon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tabLayout.getTabAt(tab).getIcon().setTint(ResourcesCompat.getColor(getResources(), color, null));
        } else {
            Drawable originalDrawable = tabLayout.getTabAt(tab).getIcon();
            Drawable wrappedDrawable = DrawableCompat.wrap(originalDrawable);
            DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), color, null)));
            tabLayout.getTabAt(tab).setIcon(wrappedDrawable);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }

    @Override
    public void fromOffToOn() {
        //Log.d(TAG,"from "+TAG+" off -> ON");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bannerNetworkListener.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void fromOnToOff() {
        //Log.d(TAG,"from "+TAG+" on -> OFF");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bannerNetworkListener.setVisibility(View.GONE);
            }
        });
    }
}
