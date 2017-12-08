package periferico.emaus.domainlayer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import periferico.emaus.R;
import periferico.emaus.domainlayer.firebase_objects.Cliente_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;

/**
 * Created by maubocanegra on 08/12/17.
 */

public class AdapterClientes extends RecyclerView.Adapter<AdapterClientes.ViewHolder>{

    private final int VIEW_PROSPECTO = 0;
    private final int VIEW_VERIFICACION = 1;
    private final int VIEW_ACTIVO = 2;

    private ArrayList<Object_Firebase> mDataset;

    /**
     * Escucha que gestionara los clicks fuera del adapter
     */
    public OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    /**
     * Constructor unico
     * @param myDataset
     * @param onItemClickListener_
     */
    public AdapterClientes(ArrayList<Object_Firebase> myDataset, OnItemClickListener onItemClickListener_){
        mDataset = myDataset;
        onItemClickListener = onItemClickListener_;
    }

    // ----------------------------------------------------------- //
    // ---------------- VIEWHOLDER IMPLEMENTATION ---------------- //
    //------------------------------------------------------------ //

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_cliente, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textviewNombre.setText(((Cliente_Firebase)mDataset.get(position)).getStNombre());
        holder.textviewStatus.setText(getStatusLabel(
                ((Cliente_Firebase)mDataset.get(position)).getIntStatus()
        ));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    private String getStatusLabel(int status){
        switch (status){
            case Cliente_Firebase.STATUS_PROSPECTO:
                return "Prospecto";
            case Cliente_Firebase.STATUS_ENVERIFICACION:
                return "En Verificación";
            case Cliente_Firebase.STATUS_ACTIVO:
                return "Activo";
        }

        return "";
    }


    // -------------------------------------------------- //
    // ---------------- VIEWHOLDER CLASS ---------------- //
    //--------------------------------------------------- //

    public class ViewHolder extends RecyclerView.ViewHolder{

        public View viewColorStatus;
        public TextView textviewNombre;
        public TextView textviewStatus;

        public ViewHolder(View v){
            super(v);
            viewColorStatus = v.findViewById(R.id.item_cliente_viewColorStatus);
            textviewNombre = v.findViewById(R.id.item_cliente_textviewNombre);
            textviewStatus = v.findViewById(R.id.item_cliente_textviewStatus);
        }
    }
}
