package periferico.emaus.domainlayer.adapters;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.SepomexObj_REST;
import periferico.emaus.domainlayer.objetos.DireccionesHelper;
import periferico.emaus.presentationlayer.activities.NuevoCliente;

/**
 * Created by maubocanegra on 15/11/17.
 */

public class AdapterDirNuevoCliente extends RecyclerView.Adapter<AdapterDirNuevoCliente.ViewHolder>{

    private ArrayList<DireccionesHelper> mDataSet;
    Activity activity;
    private static final String TAG = "AdapterDirDebug";

    public AdapterDirNuevoCliente(ArrayList<DireccionesHelper> myDataset, AppCompatActivity activity) {
        mDataSet = myDataset;
        this.activity=activity;
        onMinusDireccionesClicked = (NuevoCliente)activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_direccion, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //ViewHolder vh = new ViewHolder(v);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.buttonAgregarImgFachada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickImageInItem.imgWasTouched(position,view);
            }
        });

        holder.editTextCalle.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {
                mDataSet.get(position).setStCalleNum(editable.toString());
            }
        });

        holder.editTextColonia.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {
                mDataSet.get(position).setStColonia(editable.toString());
            }
        });

        holder.editTextCP.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {

                mDataSet.get(position).setStCP(""+Integer.parseInt(editable.toString().length()>0 ? editable.toString() : "0"));
                if(editable.toString().length()!=5){
                    //holder.editTextCP.setError(activity.getString(R.string.nuevocliente_error_cp));
                    holder.inputLayoutCP.setError(activity.getString(R.string.nuevocliente_error_cp));
                    List<String> stringsVacias = new ArrayList<>();
                    ArrayAdapter<String> adapterVacio = new ArrayAdapter<String>(
                            activity, android.R.layout.simple_spinner_item, stringsVacias);
                    adapterVacio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    holder.spinnerColonia.setAdapter(adapterVacio);
                }else{
                    holder.inputLayoutCP.setError(null);

                    RequestQueue queue = Volley.newRequestQueue(activity);
                    String url = "http://copomex.sergio.host/api/postcodes/"+editable.toString()+"/neighborhoods";

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Display the first 500 characters of the response string.
                                    Log.d(TAG,"response="+response);
                                    try{
                                        JSONObject jsonResponse = new JSONObject(response);
                                        JSONArray jsonArray = jsonResponse.getJSONArray("data");
                                        List<String> coloniasSepomex = new ArrayList<>();
                                        holder.arraylistSepomexRest = new ArrayList<>();
                                        for(int i=0; i<jsonArray.length(); i++){
                                            JSONObject eachSepomex = jsonArray.getJSONObject(i);
                                            Log.d(TAG, "eachSepomex = id="+eachSepomex.getInt("id")+" name="+eachSepomex.getString("name"));
                                            SepomexObj_REST sepomexObjRest = new SepomexObj_REST();
                                            sepomexObjRest.setId(eachSepomex.getInt("id"));
                                            sepomexObjRest.setName(eachSepomex.getString("name"));
                                            holder.arraylistSepomexRest.add(sepomexObjRest);
                                            coloniasSepomex.add(eachSepomex.getString("name"));
                                        }

                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, coloniasSepomex);
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        holder.spinnerColonia.setAdapter(adapter);
                                        holder.spinnerColonia.setPrompt("Elige una colonia");

                                    }catch(Exception e){e.printStackTrace();}
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "ERROR ON VOLLEY -------");
                        }
                    });

                    queue.add(stringRequest);


                    /*
                    WS.readSEPOMEX(editable.toString(), new WS.FirebaseArrayRetreivedListener() {
                        @Override
                        public void firebaseCompleted(ArrayList<Object_Firebase> arrayList) {
                            holder.arraylistSepomex = new ArrayList<>();
                            List<String> coloniasSepomex = new ArrayList<>();
                            for(Object_Firebase obj : arrayList){
                                holder.arraylistSepomex.add((SepomexObj_Firebase) obj);
                                coloniasSepomex.add(((SepomexObj_Firebase) obj).getColonia());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, coloniasSepomex);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            holder.spinnerColonia.setAdapter(adapter);
                            holder.spinnerColonia.setPrompt("Elige una colonia");
                        }
                    });
                    */
                }
            }
        });

        holder.editTextInterior.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {
                mDataSet.get(position).setStNumInt(editable.toString());
            }
        });

        holder.spinnerColonia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int idColonia = holder.arraylistSepomexRest.get(i).getId();
                Log.d(TAG, "selectedView = id="+idColonia+" nombre="+holder.arraylistSepomexRest.get(i).getName());
                holder.idColonia = ""+idColonia;
                mDataSet.get(position).setIdColonia(""+idColonia);
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        holder.buttonUbicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasErrors(holder)){
                    String[] calleSplit = holder.editTextCalle.getEditableText().toString().split(" ");
                    String fullDirJoin = "";
                    for(String stSplit : calleSplit){fullDirJoin+=stSplit.concat("+");}
                    String[] coloniaSplit = holder.editTextColonia.getEditableText().toString().split(" ");
                    fullDirJoin+=holder.editTextCP.getEditableText().toString()+"+";
                    for(String stSplit : coloniaSplit){fullDirJoin+=stSplit.concat("+");}
                    fullDirJoin+="Mexico";
                    Log.d(TAG, "willAsk="+activity.getString(R.string.itemdir_url_geocoding, fullDirJoin));
                    //activity.getString(R.string.itemdir_url_geocoding, fullDirJoin)
                    JsonObjectRequest jsonArrayReq = new JsonObjectRequest(Request.Method.GET, activity.getString(R.string.itemdir_url_geocoding, fullDirJoin), null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            try {
                                JSONArray resultsArray = response.getJSONArray("results");
                                Log.d(TAG,"size="+resultsArray.length());
                                JSONObject address_components = resultsArray.getJSONObject(0);
                                JSONObject geometry = address_components.getJSONObject("geometry");
                                Log.d(TAG, "geometry="+geometry.toString());
                                JSONObject location = geometry.getJSONObject("location");
                                mDataSet.get(position).setLat(location.getDouble("lat"));
                                mDataSet.get(position).setLon(location.getDouble("lng"));
                                Picasso.with(activity).load(
                                        activity.getString(
                                                R.string.itemdir_url_mapsnapshots,
                                                location.getDouble("lat"),
                                                location.getDouble("lng")
                                        )
                                ).into(holder.imageMapa);
                            }catch (Exception e){e.printStackTrace();}
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, error.toString());
                        }
                    });
                    RequestQueue reqDir = Volley.newRequestQueue(activity);
                    reqDir.add(jsonArrayReq);
                }
            }
        });


    }

    private boolean hasErrors(ViewHolder holder){
        if(holder.editTextCalle.getEditableText().toString().isEmpty()){
            holder.inputLayoutCalle.setError(activity.getString(R.string.itemdir_error_calle));
            return true;
        }else{
            holder.inputLayoutCalle.setError(null);
        }
        if(holder.editTextCP.getEditableText().toString().length()!=5){
            holder.inputLayoutCP.setError(activity.getString(R.string.itemdir_error_cp));
            return true;
        }else{
            holder.inputLayoutCP.setError(null);
        }
        /*
        if(holder.editTextColonia.getEditableText().toString().isEmpty()){
            holder.inputLayoutColonia.setError(activity.getString(R.string.itemdir_error_colonia));
            return true;
        }else{
            holder.inputLayoutColonia.setError(null);
        }
        */

        if(holder.idColonia!=null)
        if(holder.idColonia.isEmpty()){
            holder.inputLayoutCP.setError(activity.getString(R.string.itemdir_error_cp));
            return true;
        }else{
            holder.inputLayoutCP.setError(null);
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextInputLayout inputLayoutCalle;
        private EditText editTextCalle;
        private TextInputLayout inputLayoutInterior;
        private EditText editTextInterior;
        private TextInputLayout inputLayoutCP;
        private EditText editTextCP;
        private TextInputLayout inputLayoutColonia;
        private EditText editTextColonia;
        private ImageView iconAgregarPhoto;
        private ImageView imageFachada;
        private View buttonAgregarImgFachada;
        private ProgressBar progressFachada;
        private FloatingActionButton buttonUbicar;
        private ImageView imageMapa;
        private Spinner spinnerColonia;
        private ArrayList<SepomexObj_REST> arraylistSepomexRest;
        private String idColonia;

        public ViewHolder(View v){
            super(v);
            buttonAgregarImgFachada = v.findViewById(R.id.itemdir_card_fotofachada);
            inputLayoutCalle = v.findViewById(R.id.itemdir_inputlayout_calle);
            editTextCalle = v.findViewById(R.id.itemdir_edittext_calle);
            inputLayoutInterior = v.findViewById(R.id.itemdir_inputlayout_int);
            editTextInterior = v.findViewById(R.id.itemdir_edittext_int);
            inputLayoutCP = v.findViewById(R.id.itemdir_inputlayout_cp);
            editTextCP = v.findViewById(R.id.itemdir_edittext_cp);
            inputLayoutColonia = v.findViewById(R.id.itemdir_inputlayout_colonia);
            editTextColonia = v.findViewById(R.id.itemdir_edittext_colonia);
            iconAgregarPhoto = v.findViewById(R.id.itemdir_icon_addphoto);
            imageFachada = v.findViewById(R.id.itemdir_foto_fachada);
            progressFachada = v.findViewById(R.id.itemdir_progress_foto);
            buttonUbicar = v.findViewById(R.id.itemdir_button_ubicarmapa);
            imageMapa = v.findViewById(R.id.itemdir_mapa_snapshot);
            spinnerColonia = v.findViewById(R.id.itemdir_spinner_colonias);
        }
    }

    public static OnMinusDireccionesClicked onMinusDireccionesClicked;
    public interface  OnMinusDireccionesClicked{
        public void onMinusDireccionesClicked(int item);
    }


    public ClickImageInItem clickImageInItem;
    public interface ClickImageInItem{
        void imgWasTouched(int position, View fullImgContainer);
    }public void setClickImageOnItemListener(ClickImageInItem listener){
        clickImageInItem = listener;
    }

    //-----------------------------------------------------------------------
    //--------------------- FIREBASE IMPLEMENTATION -------------------------
    //-----------------------------------------------------------------------
}
