package periferico.emaus.domainlayer.adapters;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import periferico.emaus.R;
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
                mDataSet.get(position).setStCP(""+Integer.parseInt(editable.toString()));
            }
        });

        holder.editTextInterior.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {
                mDataSet.get(position).setStNumInt(editable.toString());
            }
        });


    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextInputLayout inputLayoutCalle;
        public EditText editTextCalle;
        public TextInputLayout inputLayoutInterior;
        public EditText editTextInterior;
        public TextInputLayout inputLayoutCP;
        public EditText editTextCP;
        public TextInputLayout inputLayoutColonia;
        public EditText editTextColonia;
        public ImageView iconAgregarPhoto;
        public ImageView imageFachada;
        public View buttonAgregarImgFachada;
        public ProgressBar progressFachada;

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
}
