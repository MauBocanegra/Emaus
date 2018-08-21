package periferico.emaus.domainlayer;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatesCalc {

    private static final String TAG = "DatesCalcDebug";
    private static final int FREC_SEMANAL = 1;
    private static final int FREC_QUINCENAL = 2;
    private static final int FREC_MENSUAL = 3;

    private static DatesCalc instance;

    public synchronized static DatesCalc getInstance(){
        if(instance==null){
            instance = new DatesCalc();
        }

        return instance;
    }

    public static long calcularSigPagoPorFrecuencia(long actualInMillis, int frecuenciaActual){
        Calendar cal = GregorianCalendar.getInstance();
        actualInMillis*=1000;

        /*
        actualInMillis = System.currentTimeMillis();
        cal.setTimeInMillis(actualInMillis);
        Log.d(TAG, "diaDeHoy = "+cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR));
        //supongamos que frecuencia es por semana
        cal.setTimeInMillis(actualInMillis);
        cal.add(Calendar.DAY_OF_WEEK,7);
        Log.d(TAG, "porSemana = "+cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR));

        //supongamos que frecuencia es por quincena
        cal.setTimeInMillis(actualInMillis);
        cal.add(Calendar.DAY_OF_WEEK,14);
        Log.d(TAG, "porQuincena = "+cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR));

        //supongamos que frecuencia es por mes
        cal.setTimeInMillis(actualInMillis);
        cal.add(Calendar.MONTH,1);
        Log.d(TAG, "porMes = "+cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR));
        */

        cal.setTimeInMillis(actualInMillis);
        Log.d(TAG, "prev = "+cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR));
        switch (frecuenciaActual){
            case FREC_SEMANAL:{ cal.add(Calendar.DAY_OF_WEEK,7); break;  }
            case FREC_QUINCENAL:{ cal.add(Calendar.DAY_OF_WEEK,14); break;  }
            case FREC_MENSUAL:{ cal.add(Calendar.MONTH,1); break;  }
        }
        Log.d(TAG, "calculated = "+cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR));


        return cal.getTimeInMillis();
    }

    /**
     * Este metodo se ocupa para cambiar el dia de pago por dia de la semana o dia especifico del mes
     * @param actualInMillis
     * @param frecuenciaActual
     * @param parametro
     * @return
     */
    public static long cambiarDiaDePago_Cuadricula(long actualInMillis, int frecuenciaActual, int parametro){



        return 0;
    }

}
