package periferico.emaus.presentationlayer.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.adapters.Adapter31;
import periferico.emaus.domainlayer.adapters.AdapterSemana;

public class CambiarVisitaDialog extends DialogFragment {

    static final int SEMANAL = 0;
    static final int QUINCENAL = 1;
    static final int MENSUAL = 2;

    public static final String TAG ="CambiarVisitaDialog";
    String planID;

    RecyclerView mRecyclerView31;
    RecyclerView.Adapter mAdapter31;
    android.support.v7.widget.LinearLayoutManager mLayoutManager31;
    ArrayList<TextView> mDataset31;

    RecyclerView mRecyclerViewSemana;
    RecyclerView.Adapter mAdapterSemana;
    android.support.v7.widget.LinearLayoutManager mLayoutManagerSemana;
    ArrayList<TextView> mDatasetSemana;

    ProgressDialogFragment progressDialogFragment;

    View labelMes;
    View labelSemana;
    Button buttonCambiar;
    Spinner spinnerFrecuencia;

    int frecuenciaElegida=-1;
    int diaElegido=0;
    String diaSemanalElegido;
    int segundoDiaQuincenal=0;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_cambiarvisita, null);
        builder.setView(view);
        builder.setCancelable(false);

        setView(view);
        setListeners();

        return builder.create();
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    private void setView(View view){
        mRecyclerView31 = view.findViewById(R.id.dialog_cambiarvisita_recyclerview);
        mDataset31 = new ArrayList<>();
        mAdapter31 = new Adapter31(getAdapter31Listener());
        mRecyclerView31.setAdapter(mAdapter31);
        mLayoutManager31 = new LinearLayoutManager(getActivity());
        mRecyclerView31.setLayoutManager(mLayoutManager31);

        mRecyclerViewSemana = view.findViewById(R.id.dialog_cambiarvisita_recyclerview_semana);
        mDatasetSemana = new ArrayList<>();
        mAdapterSemana = new AdapterSemana(getAdapterSemanaListener());
        mRecyclerViewSemana.setAdapter(mAdapterSemana);
        mLayoutManagerSemana = new LinearLayoutManager(getActivity());
        mRecyclerViewSemana.setLayoutManager(mLayoutManagerSemana);

        labelMes = view.findViewById(R.id.dialog_cambiarvisita_label_diadelmes);
        labelSemana = view.findViewById(R.id.dialog_cambiarvisita_label_diadelasemana);
        buttonCambiar = view.findViewById(R.id.dialog_cambiarvisita_button_cambiar);
        spinnerFrecuencia = view.findViewById(R.id.dialog_cambiarvisita_spinner);
    }

    private void semanalSelected(){
        labelMes.setVisibility(View.GONE);
        mRecyclerView31.setVisibility(View.GONE);
        labelSemana.setVisibility(View.VISIBLE);
        mRecyclerViewSemana.setVisibility(View.VISIBLE);
        ((AdapterSemana)mAdapterSemana).clearDiaSemana(getActivity());
        ((Adapter31)mAdapter31).clearDiasElegidos(getActivity());
        frecuenciaElegida=SEMANAL;
    }

    private void quincenalSelected(){
        labelMes.setVisibility(View.VISIBLE);
        mRecyclerView31.setVisibility(View.VISIBLE);
        labelSemana.setVisibility(View.GONE);
        mRecyclerViewSemana.setVisibility(View.GONE);
        ((AdapterSemana)mAdapterSemana).clearDiaSemana(getActivity());
        ((Adapter31)mAdapter31).clearDiasElegidos(getActivity());
        frecuenciaElegida=QUINCENAL;
    }

    private void mensualSelected(){
        labelMes.setVisibility(View.VISIBLE);
        mRecyclerView31.setVisibility(View.VISIBLE);
        labelSemana.setVisibility(View.GONE);
        mRecyclerViewSemana.setVisibility(View.GONE);
        ((AdapterSemana)mAdapterSemana).clearDiaSemana(getActivity());
        ((Adapter31)mAdapter31).clearDiasElegidos(getActivity());
        frecuenciaElegida=MENSUAL;
    }

    private void onDiaQuincenalClicked(){
        if(diaElegido-14>0)
            segundoDiaQuincenal=diaElegido-14;
        else
            segundoDiaQuincenal=diaElegido+14;

        ((Adapter31)mAdapter31).setDiasQuincenales(diaElegido,segundoDiaQuincenal,getContext());
    }

    private void onDiaMensualClicked(){
        ((Adapter31)mAdapter31).setDiaMensual(diaElegido,getContext());
    }

    private void onDiaSemanalClicked(){
        ((AdapterSemana)mAdapterSemana).setDiaSemanal(diaSemanalElegido,getContext());
    }

    // --------------------------------------------------------- //
    // ---------------- LISTENER IMPLEMENTATION ---------------- //
    //---------------------------------------------------------- //

    private void showLoader(boolean show){
        if(show){
            progressDialogFragment = new ProgressDialogFragment();
            progressDialogFragment.setCancelable(false);
            progressDialogFragment.title="Cambiando dias de visita...";
            progressDialogFragment.show(getActivity().getSupportFragmentManager(), TAG);
        }else{
            progressDialogFragment.changeTitle("¡Fecha de visitas cambiada!");
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialogFragment.dismiss();
                    dismiss();
                }
            }, 1500);
        }
    }

    private void executeWS(){

        int diaSel=-1;

        switch(frecuenciaElegida){
            case SEMANAL:{
                diaSel=((AdapterSemana)mAdapterSemana).getDiaSemanal();
                break;
            }
            case QUINCENAL:{
                diaSel = ((Adapter31)mAdapter31).getDiaQiuincenal();
                break;
            }
            case MENSUAL:{
                diaSel=((Adapter31)mAdapter31).getDiaMensual();
                break;
            }
        }
        final int finalDiaSel=diaSel;

        Log.d(TAG,"frecuencia="+frecuenciaElegida+" dia="+diaSel+" enplan="+planID);

        WS.showAlert(getActivity(), "Modificación de visitas", "Estas por cambiar los días que el cliente es visitado para los cobros\n¿Desesas continuar?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showLoader(true);
                WS.updateVisitasEnPlanFirebase(planID, frecuenciaElegida+1, finalDiaSel, completionListener());
            }
        }, true);
    }

    private DatabaseReference.CompletionListener completionListener(){
        return new DatabaseReference.CompletionListener(){
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                showLoader(false);
            }
        };
    }

    private void setListeners(){
        buttonCambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (frecuenciaElegida == -1) {
                    Toast.makeText(getActivity(), "Elige la periodicidad de las visitas", Toast.LENGTH_LONG).show();
                    return;
                }

                switch(frecuenciaElegida){

                    case SEMANAL:{
                        if(!((AdapterSemana)mAdapterSemana).diaIsSet()){
                            Toast.makeText(getActivity(), "Elige los días de visita semanal", Toast.LENGTH_LONG).show();
                            break;
                        }else{
                            executeWS();
                            break;
                        }
                    }

                    case QUINCENAL:{
                        if(!((Adapter31)mAdapter31).diaQuincenalIsSet()){
                            Toast.makeText(getActivity(), "Elige los días de visita quincenales", Toast.LENGTH_LONG).show();
                            break;
                        }else{
                            executeWS();
                            break;
                        }
                    }

                    case MENSUAL:{
                        if(!((Adapter31)mAdapter31).diaMensualIsSet()){
                            Toast.makeText(getActivity(), "Elige el día de visita mensual", Toast.LENGTH_LONG).show();
                            break;
                        }else{
                            executeWS();
                            break;
                        }
                    }
                }

            }
        });

        spinnerFrecuencia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "frecuencia="+i);
                ((Adapter31)mAdapter31).clearDiasElegidos(getContext());
                ((AdapterSemana)mAdapterSemana).clearDiaSemana(getContext());
                switch (i){
                    case SEMANAL: { semanalSelected(); break;}
                    case QUINCENAL: { quincenalSelected(); break;}
                    case MENSUAL: { mensualSelected(); break;}
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private Adapter31.OnDayClickListener getAdapter31Listener(){
        return new Adapter31.OnDayClickListener() {
            @Override
            public void onClickDay(View day) {
                diaElegido = Integer.valueOf(((TextView)day).getText().toString());
                if(frecuenciaElegida==QUINCENAL) onDiaQuincenalClicked();
                if(frecuenciaElegida==MENSUAL) onDiaMensualClicked();
            }
        };
    }

    private AdapterSemana.OnDayClickListener getAdapterSemanaListener(){
        return new AdapterSemana.OnDayClickListener() {
            @Override
            public void onClickDay(View day) {
                diaSemanalElegido = ((TextView)day).getText().toString();
                onDiaSemanalClicked();
            }
        };
    }

    // --------------------------------------------------- //
    // ---------------- GETTERS & SETTERS ---------------- //
    //---------------------------------------------------- //

    public void setPlanID(String planID) {
        this.planID = planID;
    }
}
