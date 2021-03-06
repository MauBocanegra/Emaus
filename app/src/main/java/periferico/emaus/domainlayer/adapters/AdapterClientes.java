package periferico.emaus.domainlayer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.firebase_objects.Cliente_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;

/**
 * Created by maubocanegra on 08/12/17.
 */

public class AdapterClientes extends RecyclerView.Adapter<AdapterClientes.ViewHolder>{

    private final int VIEW_PROSPECTO = 0;
    private final int VIEW_VERIFICACION = 1;
    private final int VIEW_ACTIVO = 2;

    private ArrayList<String> mDataset;
    private ArrayList<Cliente_Firebase> clientes;
    Context c;

    /**
     * Escucha que gestionara los clicks fuera del adapter
     */
    public OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(String clientID, String clientNombreSt, int clientStatus);
    }

    /**
     * Constructor unico
     * @param myDataset
     * @param onItemClickListener_
     */
    public AdapterClientes(ArrayList<String> myDataset, OnItemClickListener onItemClickListener_, Context context){
        mDataset = myDataset;
        clientes = new ArrayList<>();
        onItemClickListener = onItemClickListener_;
        c = ((Fragment)onItemClickListener_).getContext();
    }

    // ----------------------------------------------------------- //
    // ---------------- VIEWHOLDER IMPLEMENTATION ---------------- //
    //------------------------------------------------------------ //

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //Log.d("AdapterClienteDebug",((Cliente_Firebase)mDataset.get(position)).toString() );

        WS.readClientFirebase(mDataset.get(position), new WS.FirebaseObjectRetrievedListener() {
            @Override
            public void firebaseObjectRetrieved(Object_Firebase objectFirebase) {
                final Cliente_Firebase clienteFirebase = (Cliente_Firebase)objectFirebase;
                //if(clientes==null){clientes = new ArrayList<>();}
                //clientes.set(position, clienteFirebase);
                final String fullName=c.getString(
                        R.string.format_fullname,
                        clienteFirebase.getStNombre(),
                        clienteFirebase.getStApellido());
                holder.textviewNombre.setText(fullName);
                holder.textviewStatus.setText(getStatusLabel(
                        clienteFirebase.getIntStatus()
                ));

                holder.fullCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {onItemClickListener.onItemClick(clienteFirebase.getStID(), fullName, clienteFirebase.getIntStatus());}
                });

                holder.textviewLetra.setText(c.getString(
                        R.string.format_letras,
                        clienteFirebase.getStNombre().substring(0,1),
                        clienteFirebase.getStApellido().substring(0,1)));
                Log.d("AdpterClientesDebug", ""+clienteFirebase.getIntStatus());
                switch (clienteFirebase.getIntStatus()){
                    case Cliente_Firebase.STATUS_PROSPECTO:{
                        holder.viewColorStatus.setBackgroundColor(Color.parseColor("#f3f3f3"));
                        break;
                    }
                    case Cliente_Firebase.STATUS_ENVERIFICACION:{
                        holder.viewColorStatus.setBackgroundColor(Color.parseColor("#9b9b9b"));
                        break;
                    }
                    case Cliente_Firebase.STATUS_ACTIVO:{
                        holder.viewColorStatus.setBackgroundColor(ContextCompat.getColor(c,R.color.colorAccent));
                        holder.textviewStatus.setTextColor(ContextCompat.getColor(c,R.color.colorPrimaryDark));
                        holder.iconVerificado.setVisibility(View.VISIBLE);
                        break;
                    }
                }

            }
        });
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

    class ViewHolder extends RecyclerView.ViewHolder {

        private View viewColorStatus;
        private TextView textviewNombre;
        private TextView textviewStatus;
        private TextView textviewLetra;
        private View iconVerificado;
        private View fullCard;

        ViewHolder(View v) {
            super(v);
            fullCard = v.findViewById(R.id.item_cliente_fullcard);
            viewColorStatus = v.findViewById(R.id.item_cliente_viewColorStatus);
            textviewNombre = v.findViewById(R.id.item_cliente_textviewNombre);
            textviewStatus = v.findViewById(R.id.item_cliente_textviewStatus);
            textviewLetra = v.findViewById(R.id.item_cliente_letra);
            iconVerificado = v.findViewById(R.id.item_cliente_iconoverif);
        }
    }

    public ArrayList<Cliente_Firebase> getClientes() {
        return clientes;
    }
}
