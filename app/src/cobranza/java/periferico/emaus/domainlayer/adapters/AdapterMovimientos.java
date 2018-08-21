package periferico.emaus.domainlayer.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.firebase_objects.Cliente_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Movimiento_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Plan_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Ticket_Firebase;
import periferico.emaus.domainlayer.objetos.Direcciones;
import periferico.emaus.presentationlayer.dialogs.ImageDialogFragment;

public class AdapterMovimientos extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final String TAG = "AdapterMovimientosDebug";

    ArrayList<Object_Firebase> mDataset;
    FragmentActivity fragActivity;

    public AdapterMovimientos(ArrayList<Object_Firebase> mData, FragmentActivity fa){
        fragActivity = fa;
        mDataset = mData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;

        switch (viewType){
            case Movimiento_Firebase.movimientoTicket:{
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_perfilcobrador, parent, false);
                return new ViewHolderTicket(v);
            }

            case Movimiento_Firebase.movimientoRetiro:{}
            case Movimiento_Firebase.movimientoDeposito:{
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movimiento, parent, false);
                return new ViewHolderMovimiento(v);
            }

            default:{
                return new ViewHolderTicket(v);
            }
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holderGeneric, int position) {
        final Movimiento_Firebase movimientoFirebase = (Movimiento_Firebase) mDataset.get(position);

        switch(holderGeneric.getItemViewType()){

            case Movimiento_Firebase.movimientoTicket:{
                WS.readTicket(movimientoFirebase.getTicketID(), new WS.FirebaseObjectRetrievedListener() {
                    @Override
                    public void firebaseObjectRetrieved(Object_Firebase objectFirebase) {
                        bindTicketView(movimientoFirebase, (ViewHolderTicket)holderGeneric, (Ticket_Firebase)objectFirebase);
                    }
                });
                break;
            }

            case Movimiento_Firebase.movimientoRetiro:{}
            case Movimiento_Firebase.movimientoDeposito:{
                bindMovimientoView(movimientoFirebase, (ViewHolderMovimiento)holderGeneric);
                break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return ((Movimiento_Firebase)mDataset.get(position)).getTipoMovimientoID();
    }

    public class ViewHolderTicket extends RecyclerView.ViewHolder{

        private TextView nombreCliente;
        private TextView letrasCliente;
        private TextView estadoCliente;
        private ImageView fachadaImg;
        private TextView pagosPlan;
        private TextView direccionCliente;
        private TextView pagado;
        private TextView fecha;

        public ViewHolderTicket(View v) {
            super(v);
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

    public class ViewHolderMovimiento extends RecyclerView.ViewHolder{

        private TextView titulo;
        private TextView fecha;
        private TextView cantidad;
        private TextView referencia;

        public ViewHolderMovimiento(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.item_movimiento_label_titulo);
            fecha = itemView.findViewById(R.id.item_movimiento_label_fecha);
            cantidad = itemView.findViewById(R.id.item_movimiento_label_cantidad);
            referencia = itemView.findViewById(R.id.item_movimiento_label_referencia);
        }
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    private String formatMoney(float f){
        return fragActivity.getString(R.string.format_cantidad, NumberFormat.getNumberInstance(Locale.US).format(Math.ceil(f)));
    }

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

    private void bindMovimientoView(Movimiento_Firebase movimientoFirebase, final ViewHolderMovimiento holder){
        holder.titulo.setText(movimientoFirebase.getTipoMovimiento());
        holder.cantidad.setText(formatMoney(movimientoFirebase.getMovimiento()));

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(movimientoFirebase.getCreatedAt()*1000);
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy - HH:mm");
        sdf.setTimeZone(tz);
        String localTime = sdf.format(new Date(movimientoFirebase.getCreatedAt()*1000));
        holder.fecha.setText(localTime);

        holder.referencia.setText(movimientoFirebase.getDescripcionMovimiento());

    }

    private void bindTicketView(Movimiento_Firebase movimientoFirebase, final ViewHolderTicket holder , Ticket_Firebase ticketFirebase){
        holder.pagosPlan.setText(holder.pagosPlan.getContext().getString(
                R.string.format_cobro, ticketFirebase.getNumAbono()));

        holder.pagado.setText("$"+ticketFirebase.getMonto());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ticketFirebase.getCreatedAt()*1000);
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        sdf.setTimeZone(tz);
        String localTime = sdf.format(new Date(ticketFirebase.getCreatedAt()*1000));
        holder.fecha.setText(localTime);

        WS.readClientFirebase(ticketFirebase.getClienteID(), new WS.FirebaseObjectRetrievedListener() {
            @Override
            public void firebaseObjectRetrieved(Object_Firebase objectFirebase) {
                //------- CLIENT
                Cliente_Firebase clienteFirebase = (Cliente_Firebase) objectFirebase;
                //Log.d(TAG, clienteFirebase.toString());
                //mDataset.get(position).setClienteFirebase(clienteFirebase);

                holder.nombreCliente.setText(holder.nombreCliente.getContext().getString(
                        R.string.format_fullname,
                        clienteFirebase.getStNombre(),
                        clienteFirebase.getStApellido()
                ));
                holder.letrasCliente.setText(holder.letrasCliente.getContext().getString(
                        R.string.detallecliente_format_letras,
                        clienteFirebase.getStNombre().toUpperCase().substring(0, 1),
                        clienteFirebase.getStApellido().toUpperCase().substring(0, 1)
                ));
                holder.estadoCliente.setText(getStatusLabel(clienteFirebase.getIntStatus()));

                try {
                    final List<Direcciones> dirs = clienteFirebase.getDirecciones();
                    Picasso.with(holder.fachadaImg.getContext()).load(dirs.get(0).getLinkFachada()).into(holder.fachadaImg);
                    holder.direccionCliente.setText(
                            holder.direccionCliente.getContext().getString(
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
                            imageDialogFragment.show(fragActivity.getSupportFragmentManager(), "ImgFachada");
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
