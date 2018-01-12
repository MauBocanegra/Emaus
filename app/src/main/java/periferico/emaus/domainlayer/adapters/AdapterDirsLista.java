package periferico.emaus.domainlayer.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import periferico.emaus.R;
import periferico.emaus.domainlayer.objetos.Direcciones;

/**
 * Created by maubocanegra on 08/12/17.
 */

public class AdapterDirsLista extends RecyclerView.Adapter<AdapterDirsLista.ViewHolder>{

    private List<Direcciones> mDataset;

    Context c;

    /**
     * Escucha que gestionara los clicks fuera del adapter
     */
    public OnItemDirClickListener onItemDirClickListener;
    public interface OnItemDirClickListener{
        void onItemDirClick(int position);
    }

    /**
     * Constructor unico
     * @param myDataset
     * @param onItemClickListener_
     */
    public AdapterDirsLista(ArrayList<Direcciones> myDataset, OnItemDirClickListener onItemClickListener_
    ){
        mDataset = myDataset;
        onItemDirClickListener = onItemClickListener_;
        c = ((Fragment)onItemClickListener_).getContext();
    }

    // ----------------------------------------------------------- //
    // ---------------- VIEWHOLDER IMPLEMENTATION ---------------- //
    //------------------------------------------------------------ //

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_direcciones_lista, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.fullView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onItemClickListener.onItemClick(position);
            }
        });

        holder.textviewDireccion.setText(
                mDataset.get(position).getStCalleNum()+" "+
                mDataset.get(position).getStNumInt()+" "+
                mDataset.get(position).getStColonia()+" "+
                mDataset.get(position).getStCP()
        );

        Picasso.with(c).load(mDataset.get(position).getLinkFachada()).into(holder.imageviewFachada);

        /*
        HashMap<String,Object> hashMap = mDataset.get(position);
        holder.textviewTelefono.setText(hashMap.get("stTelefono").toString());
        holder.imageviewTipo.setImageResource(getImageTipo( ((Long)hashMap.get("intTipo")).intValue() ));
        */
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //


    // -------------------------------------------------- //
    // ---------------- VIEWHOLDER CLASS ---------------- //
    //--------------------------------------------------- //

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textviewDireccion;
        private ImageView imageviewFachada;
        private View fullView;

        ViewHolder(View v){
            super(v);
            fullView = v.findViewById(R.id.item_dir_lista_fullItemDirecs);
            imageviewFachada = v.findViewById(R.id.item_dir_lista_img);
            textviewDireccion = v.findViewById(R.id.item_dir_lista_textview_direccion);
        }
    }
}
