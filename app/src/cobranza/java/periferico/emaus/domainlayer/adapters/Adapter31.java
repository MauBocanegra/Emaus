package periferico.emaus.domainlayer.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import periferico.emaus.R;

public class Adapter31 extends RecyclerView.Adapter<Adapter31.ViewHolder31>{

    public static final String TAG ="Adapter31";

    private TextView primerDiaQuincenal;
    private TextView segundoDiaQuincenal;
    private TextView diaMensual;

    ArrayList<TextView[]> mDataset;

    public Adapter31(OnDayClickListener odcl){
        onDayClickListener = odcl;
        mDataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public Adapter31.ViewHolder31 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_semanas, parent, false);
        return new ViewHolder31(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder31 holder, int position) {
        int innerCounter = 6;
        for(TextView textView : holder.arrayDays){
            textView.setText(""+((position+1)*7-innerCounter));
            textView.setTag(""+((position+1)*7-innerCounter));
            innerCounter--;
            if(position==4 && innerCounter<3){textView.setVisibility(View.INVISIBLE);}else{
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onDayClickListener.onClickDay(view);
                    }
                });
            }

        }
        if(position==4){for(View view : holder.lines){view.setVisibility(View.INVISIBLE);}}

        mDataset.add(holder.arrayDays);
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //


    private TextView buscarPorDia(int dia){
        Log.d(TAG, "buscarPorDia"+String.valueOf(dia));

        for(TextView[] diaArr : mDataset)
            for(TextView diaObj : diaArr)
                if(diaObj.getText().equals(String.valueOf(dia)))
                    return diaObj;

        return null;
    }

    public void clearDiasElegidos(Context c){
        if(diaMensual!=null){
            diaMensual.setBackgroundColor(ContextCompat.getColor(c, R.color.semanasBg));
            diaMensual=null;
        }

        if(primerDiaQuincenal!=null) {
            primerDiaQuincenal.setBackgroundColor(ContextCompat.getColor(c, R.color.semanasBg));
            primerDiaQuincenal=null;
        }

        if(segundoDiaQuincenal!=null) {
            segundoDiaQuincenal.setBackgroundColor(ContextCompat.getColor(c, R.color.semanasBg));
            segundoDiaQuincenal=null;
        }
    }

    public boolean diaQuincenalIsSet(){
        return primerDiaQuincenal!=null;
    }

    public int getDiaQiuincenal() {
        return Integer.valueOf(((TextView)primerDiaQuincenal).getText().toString());
    }

    public boolean diaMensualIsSet(){
        return diaMensual!=null;
    }

    public int getDiaMensual() {
        return Integer.valueOf(((TextView)diaMensual).getText().toString());
    }

    public void setDiaMensual(int dia, Context c){
        clearDiasElegidos(c);

        diaMensual = buscarPorDia(dia);
        diaMensual.setBackgroundColor(ContextCompat.getColor(c, R.color.colorAccent));
    }

    public void setDiasQuincenales(int dia1, int dia2, Context c){
        clearDiasElegidos(c);

        primerDiaQuincenal=buscarPorDia(dia1);
        primerDiaQuincenal.setBackgroundColor(ContextCompat.getColor(c, R.color.colorAccent));
        segundoDiaQuincenal=buscarPorDia(dia2);
        segundoDiaQuincenal.setBackgroundColor(ContextCompat.getColor(c, R.color.colorAccent));
    }

    // ---------------------------------------------------- //
    // ---------------- VIEWHOLDER METHODS ---------------- //
    //----------------------------------------------------- //

    public class ViewHolder31 extends RecyclerView.ViewHolder{

        View[] lines;

        TextView[] arrayDays;
        public ViewHolder31(View itemView) {
            super(itemView);
            arrayDays = new TextView[7];
            arrayDays[0] = itemView.findViewById(R.id.tv1);
            arrayDays[1] = itemView.findViewById(R.id.tv2);
            arrayDays[2] = itemView.findViewById(R.id.tv3);
            arrayDays[3] = itemView.findViewById(R.id.tv4);
            arrayDays[4] = itemView.findViewById(R.id.tv5);
            arrayDays[5] = itemView.findViewById(R.id.tv6);
            arrayDays[6] = itemView.findViewById(R.id.tv7);

            lines = new View[4];
            lines[0] = itemView.findViewById(R.id.line4);
            lines[1] = itemView.findViewById(R.id.line5);
            lines[2] = itemView.findViewById(R.id.line6);
            lines[3] = itemView.findViewById(R.id.line7);
        }
    }
    public interface OnDayClickListener{
        public void onClickDay(View day);
    }public OnDayClickListener onDayClickListener;
    public OnDayClickListener getOnDayClickListener() {
        return onDayClickListener;
    }
}
