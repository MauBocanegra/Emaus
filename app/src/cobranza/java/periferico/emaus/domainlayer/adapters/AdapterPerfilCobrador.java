package periferico.emaus.domainlayer.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.firebase_objects.Cliente_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Plan_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Ticket_Firebase;
import periferico.emaus.domainlayer.objetos.Direcciones;
import periferico.emaus.domainlayer.objetos.TicketWrapper;
import periferico.emaus.presentationlayer.dialogs.ImageDialogFragment;

public class AdapterPerfilCobrador extends RecyclerView.Adapter<AdapterPerfilCobrador.ViewHolder>{

    private ArrayList<TicketWrapper> mDataset;
    private Activity activity;
    private String TAG = "AdapterPerfilCobrador";

    public AdapterPerfilCobrador(ArrayList<TicketWrapper> mDataset_, Activity a){
        mDataset = mDataset_;
        activity = a;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_perfilcobrador, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {

            //------- TICKET

            Ticket_Firebase ticketFirebase = mDataset.get(position).getTicketFirebase();
            mDataset.get(position).setTicketFirebase(ticketFirebase);


            holder.pagosPlan.setText(activity.getString(
                    R.string.format_cobro, ticketFirebase.getNumAbono()));

            holder.pagado.setText("$"+ticketFirebase.getMonto());

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(ticketFirebase.getCreatedAt()*1000);
            TimeZone tz = cal.getTimeZone();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sdf.setTimeZone(tz);
            String localTime = sdf.format(new Date(ticketFirebase.getCreatedAt()*1000));
            holder.fecha.setText(localTime);

            WS.readPlanFirebase(ticketFirebase.getPlanID(), new WS.FirebaseObjectRetrievedListener() {
                @Override
                public void firebaseObjectRetrieved(Object_Firebase objectFirebase) {
                    //------- PLAN
                    Plan_Firebase planFirebase = (Plan_Firebase) objectFirebase;
                    Log.d(TAG, planFirebase.toString());
                    mDataset.get(position).setPlanFirebase(planFirebase);
                    Log.d("SomeTag","pos="+position+" mData="+mDataset.size());
                }
            });

            WS.readClientFirebase(ticketFirebase.getClienteID(), new WS.FirebaseObjectRetrievedListener() {
                @Override
                public void firebaseObjectRetrieved(Object_Firebase objectFirebase) {
                    //------- CLIENT
                    Cliente_Firebase clienteFirebase = (Cliente_Firebase) objectFirebase;
                    //Log.d(TAG, clienteFirebase.toString());
                    mDataset.get(position).setClienteFirebase(clienteFirebase);

                    holder.nombreCliente.setText(activity.getString(
                            R.string.format_fullname,
                            clienteFirebase.getStNombre(),
                            clienteFirebase.getStApellido()
                    ));
                    holder.letrasCliente.setText(activity.getString(
                            R.string.detallecliente_format_letras,
                            clienteFirebase.getStNombre().toUpperCase().substring(0, 1),
                            clienteFirebase.getStApellido().toUpperCase().substring(0, 1)
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
                                imageDialogFragment.show(((FragmentActivity) activity).getSupportFragmentManager(), "ImgFachada");
                            }
                        });

                        //holder.fullView.setOnClickListener(clickListener);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){e.printStackTrace();}
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

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CardView fullView;
        private TextView nombreCliente;
        private TextView letrasCliente;
        private TextView estadoCliente;
        private ImageView fachadaImg;
        private TextView pagosPlan;
        private TextView direccionCliente;
        private TextView pagado;
        private TextView fecha;
        public ViewHolder(View v){
            super(v);
            fullView = v.findViewById(R.id.item_perfilcobrador_cardview);
            nombreCliente = v.findViewById(R.id.item_perfilcobrador_nombrecliente);
            letrasCliente = v.findViewById(R.id.item_perfilcobrador_letras);
            estadoCliente = v.findViewById(R.id.item_perfilcobrador_estadocliente);
            fachadaImg = v.findViewById(R.id.item_perfilcobrador_fachada);
            direccionCliente = v.findViewById(R.id.item_perfilcobrador_direccion);
            pagosPlan = v.findViewById(R.id.item_perfilcobrador_pagos);
            pagado = v.findViewById(R.id.item_perfilcobrador_pagado);
            fecha = v.findViewById(R.id.item_perfilcobrador_fecha);
        }
    }

}
