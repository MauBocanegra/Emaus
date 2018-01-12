package periferico.emaus.domainlayer.adapters;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;

import periferico.emaus.R;
import periferico.emaus.domainlayer.objetos.TelefonosHelper;
import periferico.emaus.presentationlayer.activities.NuevoCliente;

/**
 * Created by maubocanegra on 15/11/17.
 */

public class AdapterTelefonos extends RecyclerView.Adapter<AdapterTelefonos.ViewHolder>{
    private ArrayList<TelefonosHelper> mDataSet;
    Activity activity;

    public AdapterTelefonos(ArrayList<TelefonosHelper> myDataset, AppCompatActivity activity) {
        mDataSet = myDataset;
        this.activity=activity;
        onMinusTelefonosClicked = (NuevoCliente)activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_telefono_contacto, parent, false);
        Log.d("AdapterTelefonosDebug","viewType="+viewType);
        // set the view's size, margins, paddings and layout parameters
        //ViewHolder vh = new ViewHolder(v);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.buttonQuitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMinusTelefonosClicked.onMinusTelefonosClicked(position);
                //int pos = Integer.parseInt(view.getTag().toString());
                //mDataSet.remove(position);
                //notifyItemRemoved(position);
                //notifyItemRangeChanged(position, getItemCount());
            }
        });

        TelefonosHelper telefono = mDataSet.get(position);
        telefono.setTextInputLayoutTelefono(holder.inputLayoutNombre);
        telefono.setEditTextTelefono(holder.editTextNombre);
        telefono.setSpinnerTipoTelefono(holder.spinnerTipo);
        telefono.setImageButton(holder.buttonQuitar);
        holder.buttonQuitar.setTag(""+position);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextInputLayout inputLayoutNombre;
        public EditText editTextNombre;
        public Spinner spinnerTipo;
        public ImageButton buttonQuitar;
        public View wholeView;

        public ViewHolder(View v){
            super(v);
            wholeView = v;
            inputLayoutNombre = v.findViewById(R.id.textInputLayoutItemTelefonoNombre);
            editTextNombre = v.findViewById(R.id.editTextItemTelefonoNombre);
            spinnerTipo = v.findViewById(R.id.spinnerItemTelefonoTipo);
            buttonQuitar = v.findViewById(R.id.buttonQuitarTelefono);
        }
    }

    public static OnMinusTelefonosClicked onMinusTelefonosClicked;
    public interface  OnMinusTelefonosClicked{
        public void onMinusTelefonosClicked(int item);
    }


}
