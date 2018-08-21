package periferico.emaus.presentationlayer.dialogs;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

import periferico.emaus.R;

public class DatePickerDiag extends DialogFragment implements
        DatePickerDialog.OnDateSetListener{

    int dia=0; int mes=0; int anio=0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if(dia==0){
            Calendar calendar = Calendar.getInstance();
            anio = calendar.get(Calendar.YEAR);
            mes = calendar.get(Calendar.MONTH);
            dia = calendar.get(Calendar.DAY_OF_MONTH);
        }
        return new DatePickerDialog(getActivity(), this, anio, mes, dia);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int anio, int mes, int dia) {
        if(dateSetListener!=null)
            dateSetListener.onDateSet(datePicker,anio,mes,dia);
    }

    public void setFecha(int dia, int mes, int anio){
        this.dia = dia; this.mes=mes; this.anio=anio;
    }

    public DateSetListener getDateSetListener() { return dateSetListener; }
    public void setDateSetListener(DateSetListener dateSetListener) { this.dateSetListener = dateSetListener; }
    private DateSetListener dateSetListener;
    public interface DateSetListener{
        public void onDateSet(DatePicker datePicker, int anio, int mes, int dia);
    }
}
