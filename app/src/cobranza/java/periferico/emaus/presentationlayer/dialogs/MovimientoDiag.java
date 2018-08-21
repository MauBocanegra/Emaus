package periferico.emaus.presentationlayer.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.firebase_objects.Movimiento_Firebase;

public class MovimientoDiag extends DialogFragment implements WS.FirebaseCompletionListener{

    public static final String TAG = "MovimientoDiagDebug";

    private TextInputLayout inputLayoutCantidad;
    private TextInputEditText editTextCantidad;
    private Spinner spinnerTipoMovimiento;
    private TextInputLayout inputLayoutReferencia;
    private TextInputEditText editTextReferencia;
    private View spinnerLine;
    private TextView spinnerErrorTextview;
    private Button buttonRegistrarMovimiento;
    private Button buttonCancelar;

    ProgressDialogFragment progressDialogFragment;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_agregarmovimiento, null);
        builder.setView(view);
        builder.setCancelable(false);

        setView(view);
        setListeners(view);

        return builder.create();
    }

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //

    private void setView(View view){
        inputLayoutCantidad = view.findViewById(R.id.dialog_movimiento_inputlayout_cantidad);
        editTextCantidad = view.findViewById(R.id.dialog_movimiento_edittext_cantidad);
        spinnerTipoMovimiento = view.findViewById(R.id.dialog_movimiento_spinner_tipomovimiento);
        inputLayoutReferencia = view.findViewById(R.id.dialog_movimiento_inputlayout_referencia);
        editTextReferencia = view.findViewById(R.id.dialog_movimiento_edittext_referencia);
        spinnerLine = view.findViewById(R.id.dialog_movimiento_spinner_error_line);
        spinnerErrorTextview = view.findViewById(R.id.dialog_movimiento_spinner_error_text);
        buttonRegistrarMovimiento = view.findViewById(R.id.diag_movimiento_button_registrarmovimiento);
        buttonCancelar = view.findViewById(R.id.diag_movimiento_button_cancelar);
    }

    private void setListeners(View view){
        view.findViewById(R.id.diag_movimiento_button_registrarmovimiento).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "OnClickRegistrarMovimiento");
                if(verifyFields()){
                    buttonRegistrarMovimiento.setEnabled(false);
                    buttonRegistrarMovimiento.setTextColor(ContextCompat.getColor(getActivity(), R.color.textColorTerciary));
                    buttonCancelar.setEnabled(false);
                    buttonCancelar.setTextColor(ContextCompat.getColor(getActivity(), R.color.textColorTerciary));

                    progressDialogFragment = new ProgressDialogFragment();
                    progressDialogFragment.setCancelable(false);
                    progressDialogFragment.title="Registrando movimiento...";
                    progressDialogFragment.show(getActivity().getSupportFragmentManager(), TAG);

                    writeMovimientoInFirebase();
                }
            }
        });

        view.findViewById(R.id.diag_movimiento_button_cancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getDialog().dismiss();
                    }
                }, 200);

            }
        });
    }

    private boolean verifyFields(){

        //Integer.parseInt(editable.toString()

        int errors=0;

        if(editTextCantidad.getEditableText().toString().length()==0){
            inputLayoutCantidad.setError(getString(R.string.dialog_movimientos_error_cantidad));
            errors++;
        }else{
            inputLayoutCantidad.setError(null);
        }

        if(editTextReferencia.getEditableText().toString().length()<5){
            inputLayoutReferencia.setError(getString(R.string.dialog_movimientos_error_referencia));
            errors++;
        }else{
            inputLayoutReferencia.setError(null);
        }

        if(spinnerTipoMovimiento.getSelectedItemPosition()==-1){
            setErrorSpinner(true);
        }else{
            setErrorSpinner(false);
        }

        return errors==0 ;
    }

    private void setErrorSpinner(boolean errorState){
        if(errorState){
            spinnerLine.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.redError));
            spinnerErrorTextview.setVisibility(View.VISIBLE);
        }else{
            spinnerLine.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.cardview_dark_background));
            spinnerErrorTextview.setVisibility(View.GONE);
        }
    }

    private void writeMovimientoInFirebase(){

        //Creamos el Objeto de Firebase
        Movimiento_Firebase movimientoFirebase = new Movimiento_Firebase();
        movimientoFirebase.setCreatedAt(System.currentTimeMillis()/1000);

        //Obtenemos el createdAt y el FechaKey
        Long tsLong = System.currentTimeMillis()/1000;
        Calendar cal = Calendar.getInstance();
        movimientoFirebase.setFecha(""+cal.get(Calendar.DAY_OF_MONTH)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.YEAR));

        int tipoMovimiento=-1;
        switch(spinnerTipoMovimiento.getSelectedItemPosition()){
            case 0: tipoMovimiento=Movimiento_Firebase.movimientoDeposito; break;
            case 1: tipoMovimiento=Movimiento_Firebase.movimientoRetiro; break;
        }
        movimientoFirebase.setTipoMovimientoID(tipoMovimiento);
        movimientoFirebase.setTipoMovimiento(spinnerTipoMovimiento.getItemAtPosition(spinnerTipoMovimiento.getSelectedItemPosition()).toString());
        movimientoFirebase.setMovimiento(Float.parseFloat(editTextCantidad.getEditableText().toString()));
        movimientoFirebase.setDescripcionMovimiento(editTextReferencia.getEditableText().toString());

        WS.writeMovimientoFirebase(movimientoFirebase, MovimientoDiag.this);
    }

    // --------------------------------------------------------- //
    // ---------------- FIREBASE IMPLEMENTATION ---------------- //
    //---------------------------------------------------------- //

    @Override
    public void firebaseCompleted(boolean hasError) {
        if(!hasError){
            progressDialogFragment.changeTitle("¡Movimiento Registrado!");
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialogFragment.dismiss();
                    movimientoAgregadoListener.onMovimientoAgregado();
                    dismiss();
                }
            }, 1500);
        }
    }

    public interface MovimientoAgregadoListener{
        public void onMovimientoAgregado();
    }MovimientoAgregadoListener movimientoAgregadoListener;
    public void setMovimientoAgregadoListener(MovimientoAgregadoListener movimientoAgregadoListener) {
        this.movimientoAgregadoListener = movimientoAgregadoListener;
    }
}
