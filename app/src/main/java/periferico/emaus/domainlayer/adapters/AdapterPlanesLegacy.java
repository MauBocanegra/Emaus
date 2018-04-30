package periferico.emaus.domainlayer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import periferico.emaus.R;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.firebase_objects.PlanLegacy_Firebase;

/**
 * Created by maubocanegra on 14/12/17.
 */

public class AdapterPlanesLegacy extends RecyclerView.Adapter<AdapterPlanesLegacy.ViewHolder>{

    private ArrayList<Object_Firebase> mDataset;
    private String[] planes;
    private String[][] ataudes;
    private String[] servicios;
    private String[] financiamientos;
    private String[] pagos;
    private String[] formaPago;

    Context c;

    public AdapterPlanesLegacy(ArrayList<Object_Firebase> myDataset, Context context){
        mDataset = myDataset;
        c = context;
    }

    // ----------------------------------------------------------- //
    // ---------------- VIEWHOLDER IMPLEMENTATION ---------------- //
    //------------------------------------------------------------ //

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_planes, parent, false);
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

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlanLegacy_Firebase planFirebase = ((PlanLegacy_Firebase)mDataset.get(position));
        holder.textviewContrato.setText(planFirebase.getStID());
        holder.textviewPlan.setText(planes[planFirebase.getIntPlan()]);
        holder.textviewAtaud.setText(ataudes[planFirebase.getIntPlan()][planFirebase.getIntAtaud()]);
        holder.textviewServicio.setText(servicios[planFirebase.getIntServicio()]);
        holder.textviewFinanciamiento.setText(financiamientos[planFirebase.getIntFinanciamiento()]);
        holder.textviewFrecuencia.setText(pagos[planFirebase.getIntFrecuenciaPagos()]);
        holder.textviewForma.setText(formaPago[planFirebase.getIntFormaPago()]);
        holder.textviewTotal.setText(c.getString(R.string.nuevoplan_formatted_monto, NumberFormat.getNumberInstance(Locale.US).format(planFirebase.getIntMonto())));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // -------------------------------------------------- //
    // ---------------- VIEWHOLDER CLASS ---------------- //
    //--------------------------------------------------- //

    class ViewHolder extends RecyclerView.ViewHolder {

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
}
