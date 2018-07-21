package periferico.emaus.domainlayer.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.firebase_objects.ConfiguracionPlanes_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Plan_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.FormasPago_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.FrecuenciasPago_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.MatrizPlanes_Firebase;
import periferico.emaus.domainlayer.firebase_objects.configplan.Financiamientos_Firebase;
import periferico.emaus.presentationlayer.activities.DetallePlan;

/**
 * Created by maubocanegra on 14/12/17.
 */

public class AdapterPlanes extends RecyclerView.Adapter<AdapterPlanes.ViewHolder> implements WS.FirebaseObjectRetrievedListener {

    private ArrayList<String> mDataset;

    private MatrizPlanes_Firebase configPlan;
    private Financiamientos_Firebase mensualidadesFirebase;
    private FormasPago_Firebase formasPagoFirebase;
    private Financiamientos_Firebase financiamientoFirebase;
    private FrecuenciasPago_Firebase frecuenciasPagoFirebase;



    Context c;

    public AdapterPlanes(ArrayList<String> myDataset, Context context){
        mDataset = myDataset;
        c = context;

        WS.readConfiguracionPlanes(AdapterPlanes.this);
    }

    // ----------------------------------------------------------- //
    // ---------------- VIEWHOLDER IMPLEMENTATION ---------------- //
    //------------------------------------------------------------ //

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_planes, parent, false);

        /*
        planes = c.getResources().getStringArray(R.array.nuevoplan_array_planes);
        ataudes = new String[][]{
                c.getResources().getStringArray(R.array.nuevoplan_array_planbasico),
                c.getResources().getStringArray(R.array.nuevoplan_array_planLujo),
                c.getResources().getStringArray(R.array.nuevoplan_array_planmadera)
        };
        servicios = c.getResources().getStringArray(R.array.nuevoplan_array_servicio);
        financiamientos = c.getResources().getStringArray(R.array.nuevoplan_array_financiamiento);
        pagos = c.getResources().getStringArray(R.array.nuevoplan_array_pago);
        formaPago = c.getResources().getStringArray(R.array.nuevoplan_array_formapago);
        */

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        WS.readPlanFirebase(mDataset.get(position), new WS.FirebaseObjectRetrievedListener(){
            @Override
            public void firebaseObjectRetrieved(Object_Firebase objectFirebase) {
                final Plan_Firebase planFirebase = (Plan_Firebase)objectFirebase;

                holder.textviewContrato.setText(planFirebase.getStID());

                try {
                    holder.textviewPlan.setText(configPlan.getPlanByPlanID(planFirebase.getPlanID()).getNombre());
                    holder.textviewAtaud.setText(configPlan.getPlanByPlanID(planFirebase.getPlanID()).getAtaudByID(planFirebase.getAtaudID()).getNombre());
                    holder.textviewServicio.setText(configPlan.getPlanByPlanID(planFirebase.getPlanID()).getAtaudByID(planFirebase.getAtaudID()).getServicioByID(planFirebase.getServicioID()).getNombre());
                    holder.textviewFinanciamiento.setText(financiamientoFirebase.getFinanciamientoByID(planFirebase.getFinanciamientoID()).getNombre());
                    holder.textviewFrecuencia.setText(frecuenciasPagoFirebase.getFrecuenciaByID(planFirebase.getFrecuenciaPagoID()).getNombre());
                    holder.textviewForma.setText(formasPagoFirebase.getFormaPagoByID(planFirebase.getFormaPagoID()).getNombre());


                    String montoFormateado = NumberFormat.getNumberInstance(Locale.US).format(planFirebase.getTotalAPagar());
                    holder.textviewTotal.setText(String.format( c.getString(R.string.nuevoplan_formatted_monto),montoFormateado));

                    holder.fullView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(c, DetallePlan.class);
                            intent.putExtra("clienteID", planFirebase.getStCliente());
                            intent.putExtra("planID", planFirebase.getStID());
                            c.startActivity(intent);
                        }
                    });

                    /*
                    switch (c.getString(R.string.flavor_string)){
                        case "Cobranza":{

                            holder.fullView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(c, DetallePlan.class);
                                    intent.putExtra("clienteID", planFirebase.getStCliente());
                                    intent.putExtra("planID", planFirebase.getStID());
                                    c.startActivity(intent);
                                }
                            });

                            break;
                        }
                    }
                    */
                }catch(Exception e){}
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // -------------------------------------------------- //
    // ---------------- VIEWHOLDER CLASS ---------------- //
    //--------------------------------------------------- //

    class ViewHolder extends RecyclerView.ViewHolder {

        private View fullView;

        private TextView textviewContrato;
        private TextView textviewPlan;
        private TextView textviewAtaud;
        private TextView textviewTotal;
        private TextView textviewServicio;
        private TextView textviewFinanciamiento;
        private TextView textviewFrecuencia;
        private TextView textviewForma;

        ViewHolder(View view){
            super(view);
            fullView=view;
            textviewContrato = view.findViewById(R.id.itemplan_textview_contrato);
            textviewPlan = view.findViewById(R.id.itemplan_textview_plan);
            textviewAtaud = view.findViewById(R.id.itemplan_textview_ataud);
            textviewTotal = view.findViewById(R.id.itemplan_textview_total);
            textviewServicio = view.findViewById(R.id.itemplan_textview_servicio);
            textviewFinanciamiento = view.findViewById(R.id.itemplan_textview_financiamiento);
            textviewFrecuencia = view.findViewById(R.id.itemplan_textview_frecpago);
            textviewForma = view.findViewById(R.id.itemplan_textview_formapago);
        }
    }

    // ----------------------------------------------------------- //
    // ---------------- FIREBASE OBJECT RETRIEVED ---------------- //
    //------------------------------------------------------------ //


    @Override
    public void firebaseObjectRetrieved(Object_Firebase objectFirebase) {
        ConfiguracionPlanes_Firebase configuracionPlanesFirebase = (ConfiguracionPlanes_Firebase)objectFirebase;

        configPlan = configuracionPlanesFirebase.getPlanes();
        mensualidadesFirebase = configuracionPlanesFirebase.getListamensualidades();
        formasPagoFirebase = configuracionPlanesFirebase.getListaformaspago();

        financiamientoFirebase = configuracionPlanesFirebase.getListamensualidades();
        frecuenciasPagoFirebase = configuracionPlanesFirebase.getListafrecuenciaspago();
    }
}
