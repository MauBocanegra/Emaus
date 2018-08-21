package periferico.emaus.domainlayer.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.firebase_objects.Categorias_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Cliente_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Plan_Firebase;
import periferico.emaus.domainlayer.objetos.Direcciones;
import periferico.emaus.domainlayer.objetos.PuntoRutaWrapper;
import periferico.emaus.presentationlayer.activities.DetalleCliente;
import periferico.emaus.presentationlayer.activities.DetalleCobro;
import periferico.emaus.presentationlayer.dialogs.ImageDialogFragment;

public class AdapterRuta extends RecyclerView.Adapter<AdapterRuta.ViewHolder>{

    private ArrayList<PuntoRutaWrapper> mDataset;
    private Activity activity;

    public AdapterRuta(
            ArrayList<PuntoRutaWrapper> myDataset,
            Activity act,
            ComponentInItemRutaClickListener componentInItemRutaClickListener,
            ItemRemoverListener itemRemoverListener){
        mDataset = myDataset;
        activity=act;
        this.componentInItemRutaClickListener = componentInItemRutaClickListener;
        this.itemRemoverListener = itemRemoverListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //TODO change item view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ruta, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(final AdapterRuta.ViewHolder holder, final int position) {
        WS.readClientFirebase(mDataset.get(position).getPlan().getStCliente(), new WS.FirebaseObjectRetrievedListener() {
            @Override
            public void firebaseObjectRetrieved(Object_Firebase objectFirebase) {
                final Cliente_Firebase clienteFirebase = (Cliente_Firebase)objectFirebase;
                holder.nombreCliente.setText(activity.getString(
                        R.string.format_fullname,
                        clienteFirebase.getStNombre(),
                        clienteFirebase.getStApellido()
                ));
                holder.letrasCliente.setText(activity.getString(
                        R.string.detallecliente_format_letras,
                        clienteFirebase.getStNombre().toUpperCase().substring(0,1),
                        clienteFirebase.getStApellido().toUpperCase().substring(0,1)
                ));
                holder.estadoCliente.setText(getStatusLabel(clienteFirebase.getIntStatus()));

                try {
                    final List<Direcciones> dirs = clienteFirebase.getDirecciones();
                    Picasso.with(activity).load(dirs.get(0).getLinkFachada()).into(holder.fachadaImg);
                    holder.direccionCliente.setText(
                        activity.getString(
                            R.string.format_fulldireccion,
                            dirs.get(0).getStCalleNum(),
                            dirs.get(0).getStColonia(),
                            dirs.get(0).getStCP()
                        ));
                    holder.fachadaImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ImageDialogFragment imageDialogFragment = new ImageDialogFragment();
                            imageDialogFragment.setImage(dirs.get(0).getLinkFachada());
                            imageDialogFragment.show(((FragmentActivity)activity).getSupportFragmentManager(), "ImgFachada");
                        }
                    });

                    //holder.fullView.setOnClickListener(clickListener);

                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(activity, DetalleCliente.class);
                            //Log.d("ClientesFrag","stID="+cliente.toString());
                            intent.putExtra("clientID",clienteFirebase.getStID());
                            intent.putExtra("stNombre",clienteFirebase.getStNombre()+" "+clienteFirebase.getStApellido());
                            intent.putExtra("intStatus",clienteFirebase.getIntStatus());

                            activity.startActivity(intent);
                        }
                    };

                    holder.nombreCliente.setOnClickListener(clickListener);
                    holder.letrasCliente.setOnClickListener(clickListener);
                    holder.estadoCliente.setOnClickListener(clickListener);


                }catch(Exception e){e.printStackTrace();}
                //recomputeClientAndPlan(position);
            }

        });

        //

        holder.pagosPlan.setText(activity.getString(
                R.string.format_cobro,
                mDataset.get(position).getPlan().getPagosRealizados() + 1));

        holder.buttonPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                componentInItemRutaClickListener.onComponentClikListener(view.getId(), position, holder.buttonPagar, null);
            }
        });

        holder.buttonMover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                WS.readEstatusVisita(new WS.FirebaseObjectRetrievedListener() {
                    @Override
                    public void firebaseObjectRetrieved(final Object_Firebase objectFirebase) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<Map<String,Object>> mapaEstatusVisita = ((Categorias_Firebase)objectFirebase).getEstatusVisita();
                                /*
                                PopupMenu popupMenu = new PopupMenu(getContext(), viewClicked);
                                popupMenu.getMenu().add(0,0,0,"Item 1");
                                popupMenu.getMenu().add(0,1,1,"Item 2");
                                popupMenu.getMenu().add(0,2,2,"Item 3");
                                */

                                componentInItemRutaClickListener.onComponentClikListener(view.getId(), position, holder.buttonMover, null);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void recomputeClientAndPlan(int position){
        Plan_Firebase planFirebase = mDataset.get(position).getPlan();

        /*
        if(planFirebase!=null && mDataset.get(position).getCliente()!=null){
            switch (planFirebase.getPrioridadEnCobros()){
                //remove from list
                case 0:{
                    itemRemoverListener.willRemoveItem(position);
                    if(mDataset.get(position).getPlan().getPrioridadEnCobros()==0){mDataset.remove(position);}
                    break;
                }
            }
            //Log.d("AdapterRuta","BOTH SET IN POS["+position+"]");
        }
        */
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CardView fullView;
        private TextView nombreCliente;
        private TextView letrasCliente;
        private TextView estadoCliente;
        private ImageView fachadaImg;
        private TextView pagosPlan;
        private TextView direccionCliente;
        private Button buttonPagar;
        private Button buttonMover;

        public ViewHolder(View v){
            super(v);
            fullView = v.findViewById(R.id.item_ruta_cardview);
            nombreCliente = v.findViewById(R.id.item_ruta_nombrecliente);
            letrasCliente = v.findViewById(R.id.item_perfilcobrador_letras);
            estadoCliente = v.findViewById(R.id.item_ruta_estadocliente);
            fachadaImg = v.findViewById(R.id.item_perfilcobrador_fachada);
            direccionCliente = v.findViewById(R.id.item_ruta_direccion);
            pagosPlan = v.findViewById(R.id.item_perfilcobrador_pagos);
            buttonPagar = v.findViewById(R.id.item_perfilcobrador_pagado);
            buttonMover = v.findViewById(R.id.item_perfilcobrador_fecha);
        }
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

    public ComponentInItemRutaClickListener componentInItemRutaClickListener;
    public interface ComponentInItemRutaClickListener{
        void onComponentClikListener(int ID, int position, View viewClicked, PopupMenu menu);
    }

    public ItemRemoverListener itemRemoverListener;
    public interface ItemRemoverListener{
        void willRemoveItem(int position );
    }
}
