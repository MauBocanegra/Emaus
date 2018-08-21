package periferico.emaus.domainlayer.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import periferico.emaus.R;

public class AdapterSemana extends RecyclerView.Adapter<AdapterSemana.ViewHolderSemana>{

    public static final String TAG ="Adapter31";

    private ArrayList<TextView> mDataset;
    private TextView diaSemanal;

    public AdapterSemana(OnDayClickListener odcl){
        mDataset=new ArrayList<>();
        onDayClickListener = odcl;
    }

    @NonNull
    @Override
    public AdapterSemana.ViewHolderSemana onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_semanas, parent, false);
        return new ViewHolderSemana(view);
    }

    public void clearDiaSemana(Context c){
        if(diaSemanal!=null){
            diaSemanal.setBackgroundColor(ContextCompat.getColor(c, R.color.semanasBg));
            diaSemanal=null;
        }
    }

    //En si el dia ya esta escogido desde el onclick
    public void setDiaSemanal(String textDia, Context c){
        //borramos el previo si existe
        clearDiaSemana(c);

        for(TextView diaTv : mDataset){
            if(diaTv.getText().equals(textDia)){
                diaSemanal = diaTv;
                diaSemanal.setBackgroundColor(ContextCompat.getColor(c, R.color.colorAccent));
                break;
            }
        }
    }

    public boolean diaIsSet(){
        return diaSemanal!=null;
    }

    public int getDiaSemanal() {
        return Integer.valueOf(diaSemanal.getTag().toString());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSemana holder, int position) {

        holder.arrayDays[0].setText("Lu");
        holder.arrayDays[1].setText("Ma");
        holder.arrayDays[2].setText("Mi");
        holder.arrayDays[3].setText("Ju");
        holder.arrayDays[4].setText("Vi");
        holder.arrayDays[5].setText("Sa");
        holder.arrayDays[6].setText("Do");

        holder.arrayDays[0].setTag("1");
        holder.arrayDays[1].setTag("2");
        holder.arrayDays[2].setTag("3");
        holder.arrayDays[3].setTag("4");
        holder.arrayDays[4].setTag("5");
        holder.arrayDays[5].setTag("6");
        holder.arrayDays[6].setTag("7");

        for(TextView day : holder.arrayDays){
            day.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDataset.add((TextView)view);
                    onDayClickListener.onClickDay(view);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolderSemana extends RecyclerView.ViewHolder{

        TextView[] arrayDays;
        public ViewHolderSemana(View itemView) {
            super(itemView);
            arrayDays = new TextView[7];
            arrayDays[0] = itemView.findViewById(R.id.tv1);
            arrayDays[1] = itemView.findViewById(R.id.tv2);
            arrayDays[2] = itemView.findViewById(R.id.tv3);
            arrayDays[3] = itemView.findViewById(R.id.tv4);
            arrayDays[4] = itemView.findViewById(R.id.tv5);
            arrayDays[5] = itemView.findViewById(R.id.tv6);
            arrayDays[6] = itemView.findViewById(R.id.tv7);
        }
    }
    public interface OnDayClickListener{
        public void onClickDay(View day);
    }public OnDayClickListener onDayClickListener;
    public OnDayClickListener getOnDayClickListener() {
        return onDayClickListener;
    }
}
