package periferico.emaus.domainlayer.adapters;

import android.content.Context;
import android.content.Intent;
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
import periferico.emaus.domainlayer.firebase_objects.Cliente_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.presentationlayer.activities.DetalleCliente;

/**
 * Created by maubocanegra on 08/12/17.
 */

public class AdapterDirectorio extends RecyclerView.Adapter<AdapterDirectorio.ViewHolder>{

    private final int VIEW_PROSPECTO = 0;
    private final int VIEW_VERIFICACION = 1;
    private final int VIEW_ACTIVO = 2;
    String telToCall;

    private ArrayList<Object_Firebase> mDataset;

    Context c;

    /**
     * Escucha que gestionara los clicks fuera del adapter
     */
    public OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(int position, int tipoUsuario, String telToCall_);
    }

    /**
     * Constructor unico
     * @param myDataset
     * @param onItemClickListener_
     */
    public AdapterDirectorio(ArrayList<Object_Firebase> myDataset, OnItemClickListener onItemClickListener_, Context context){
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
                R.layout.item_directorio, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        try {
            telToCall = ((Cliente_Firebase) mDataset.get(position)).getStTelefono();
            if (telToCall == null) {
                telToCall = "5555555555";
            }

            holder.fullView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(position, ((Cliente_Firebase) mDataset.get(position)).getTipoUsuario(), telToCall);
                }
            });

            holder.textviewNombre.setText(c.getString(
                    R.string.format_fullname,
                    ((Cliente_Firebase) mDataset.get(position)).getStNombre(),
                    ((Cliente_Firebase) mDataset.get(position)).getStApellido()==null ? "" : ((Cliente_Firebase) mDataset.get(position)).getStApellido()
            ));
            holder.textviewNombre.setTag(((Cliente_Firebase) mDataset.get(position)).getStTelefono());
            holder.textviewLetra.setText(((Cliente_Firebase) mDataset.get(position)).getStNombre().substring(0, 1));

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(c, DetalleCliente.class);
                    //Log.d("ClientesFrag","stID="+cliente.toString());
                    intent.putExtra("clientID",((Cliente_Firebase) mDataset.get(position)).getStID());
                    intent.putExtra("stNombre",((Cliente_Firebase) mDataset.get(position)).getStNombre().split(" ")[0]+" "+((Cliente_Firebase) mDataset.get(position)).getStApellido().split(" ")[0]);
                    intent.putExtra("intStatus",((Cliente_Firebase) mDataset.get(position)).getIntStatus());

                    c.startActivity(intent);
                }
            };

            holder.textviewNombre.setOnClickListener(clickListener);
            holder.textviewLetra.setOnClickListener(clickListener);
            holder.textviewStatus.setOnClickListener(clickListener);

            switch (((Cliente_Firebase) mDataset.get(position)).getTipoUsuario()) {
                case Cliente_Firebase.TIPOUSUARIO_CLIENTE: {
                    //holder.viewColorStatus.setBackgroundColor(Color.parseColor("#f3f3f3"));
                    holder.textviewStatus.setText("Cliente");
                    holder.iconPhone.setVisibility(View.GONE);
                    break;
                }
                case Cliente_Firebase.TIPOUSUARIO_DIRECTORIO: {
                    holder.viewColorStatus.setBackgroundColor(Color.parseColor("#000000"));
                    holder.textviewStatus.setText("Emaus");
                    holder.textviewLetra.setTextColor(Color.WHITE);
                    holder.iconPhone.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }catch(Exception e){Log.e("AdapterDirectorio", "Directorio sigue fallando!");}
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

    public ArrayList<Object_Firebase> searchInList(String query){
        ArrayList<Object_Firebase> listAfterSearch = new ArrayList<>();
        try {
            for (Object_Firebase objectFirebase : mDataset) {
                if (((Cliente_Firebase) objectFirebase).getStNombre().toLowerCase().contains(query.toLowerCase())) {
                    Log.d("AdapterDirectory","["+query+"] in ["+((Cliente_Firebase) objectFirebase).getStNombre()+"]");
                    listAfterSearch.add(objectFirebase);
                }
            }
        }catch(Exception e){e.printStackTrace();}
        return  listAfterSearch;
    }


    // -------------------------------------------------- //
    // ---------------- VIEWHOLDER CLASS ---------------- //
    //--------------------------------------------------- //

    class ViewHolder extends RecyclerView.ViewHolder{

        private View viewColorStatus;
        private TextView textviewNombre;
        private TextView textviewStatus;
        private TextView textviewLetra;
        private View iconVerificado;
        private View fullView;
        private View iconPhone;

        ViewHolder(View v){
            super(v);
            fullView = v.findViewById(R.id.item_directorio_fullview);
            viewColorStatus = v.findViewById(R.id.item_cliente_viewColorStatus);
            textviewNombre = v.findViewById(R.id.item_cliente_textviewNombre);
            textviewStatus = v.findViewById(R.id.item_cliente_textviewStatus);
            textviewLetra = v.findViewById(R.id.item_cliente_letra);
            iconVerificado = v.findViewById(R.id.item_cliente_iconoverif);
            iconPhone = v.findViewById(R.id.item_directorio_icon_phone);
        }
    }
}
