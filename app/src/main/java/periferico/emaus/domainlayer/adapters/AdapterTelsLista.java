package periferico.emaus.domainlayer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import periferico.emaus.R;
import periferico.emaus.domainlayer.firebase_objects.Cliente_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.objetos.Telefonos;

/**
 * Created by maubocanegra on 08/12/17.
 */

public class AdapterTelsLista extends RecyclerView.Adapter<AdapterTelsLista.ViewHolder>{

    private List<Telefonos> mDataset;

    Context c;

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
    public AdapterTelsLista(ArrayList<Telefonos> myDataset, OnItemClickListener onItemClickListener_
    ){
        mDataset = myDataset;
        onItemClickListener = onItemClickListener_;
        c = ((Fragment)onItemClickListener_).getContext();
    }

    // ----------------------------------------------------------- //
    // ---------------- VIEWHOLDER IMPLEMENTATION ---------------- //
    //------------------------------------------------------------ //

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_telefono, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.fullView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {onItemClickListener.onItemClick(position);}
        });

        Telefonos tel = mDataset.get(position);
        holder.textviewTelefono.setText(tel.getStTelefono());
        holder.imageviewTipo.setImageResource(getImageTipo(tel.getIntTipo()));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    private int getImageTipo(int status){
        switch(status){
            case 0 : return R.drawable.ic_cell;
            case 1 : return R.drawable.ic_home;
            case 2 : return R.drawable.ic_office;
            case 3 : return R.drawable.ic_phone;
        }
        return 3;
    }


    // -------------------------------------------------- //
    // ---------------- VIEWHOLDER CLASS ---------------- //
    //--------------------------------------------------- //

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textviewTelefono;
        private ImageView imageviewTipo;
        private View fullView;

        ViewHolder(View v){
            super(v);
            fullView = v.findViewById(R.id.item_telefono_fullview);
            textviewTelefono = v.findViewById(R.id.item_telefono_textview_telefono);
            imageviewTipo = v.findViewById(R.id.item_telefono_image_tipo);
        }
    }
}
