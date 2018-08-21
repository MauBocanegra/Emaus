package periferico.emaus.presentationlayer.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.danielfelgar.drawreceiptlib.ReceiptBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.bluetoothprinter.BTPrinter;
import periferico.emaus.domainlayer.bluetoothprinter.PrintingActivity;
import periferico.emaus.domainlayer.bluetoothprinter.PrintingCommands;
import periferico.emaus.domainlayer.firebase_objects.Movimiento_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Ticket_Firebase;
import periferico.emaus.presentationlayer.fragments.PerfilCobrador;

public class PrintCorte extends PrintingActivity implements
        WS.FirebaseArrayRetreivedListener,
        BTPrinter.ReadyToPrintListener,
        BTPrinter.FailureNotifiedListener,
        PrintingActivity.PairingStatusListener{

    private static final String TAG = "PrintCorteDebug";
    private Toolbar toolbar;
    private ImageView iconPairing;
    private ImageView iconPrinter;
    private ImageView iconTicket;
    private TextView labelFeedback;
    private ImageView image;

    //ArrayList<Ticket_Firebase> ticketsFirebase;
    private ArrayList<Movimiento_Firebase> mDataset;

    Bitmap barcode;

    int pagos=0;
    float pagado=0.0f;

    int tickets=0; int retiros=0; int depositos=0;
    float cantTickets=0; float cantRetiros=0; float cantDepositos=0;
    final int TICKET=0; final int DEPOSITO=1; final int RETIRO=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_ticket);

        instanciateObjects();

        instanciateToolbar();

        downloadAndPrepare();

        btPrinter = new BTPrinter();
        pairingStatusListener=PrintCorte.this;
        btPrinter.setPrintingReadyListener(PrintCorte.this);
        btPrinter.setPrintinfFailureListener(PrintCorte.this);

        //Activamos los permisos del bluetooth
        setPermissionsLogic();
        dexterBuilder.check();
    }

    private void instanciateObjects(){
        mDataset = new ArrayList<>();

        viewTodosPermisos = findViewById(R.id.cardview_permissions);
        buttonAbrirAjustes = findViewById(R.id.permissions_abrir_ajustes);
        image = findViewById(R.id.ticket_image);
        iconPairing=findViewById(R.id.printticket_img_pairing);
        iconPrinter=findViewById(R.id.printticket_img_printer);
        iconTicket=findViewById(R.id.printticket_img_ticket);
        labelFeedback = findViewById(R.id.printitcket_label_feedback);

        buttonAbrirAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirAjustes();
            }
        });
    }

    private void instanciateToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Imprimir corte");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void downloadAndPrepare(){
        String fecha = getIntent().getStringExtra("stFecha");
        Log.d(TAG, "fecha="+fecha);

        Calendar calendar = Calendar.getInstance();
        int anio = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        WS.descargarMovimientosPorCobradorYFecha(""+dia+"-"+(mes+1)+"-"+anio,PrintCorte.this);
    }

    private void generateAndDraw(){
        String empleado = WS.getCurrentUser().getEmail().split("@")[0].replace(".","");
        barcode = BitmapFactory.decodeResource(this.getResources(), R.drawable.barcode);

        Calendar calendar = Calendar.getInstance();
        TimeZone tz = calendar.getTimeZone();
        SimpleDateFormat sdfOut = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        sdfOut.setTimeZone(tz);
        String localTimeOut = sdfOut.format(System.currentTimeMillis());

        ReceiptBuilder receipt = new ReceiptBuilder(1200);
        receipt.setMargin(30, 20).
                setAlign(Paint.Align.CENTER).
                setColor(Color.BLACK).
                setTextSize(50).
                //setTypeface(this, "fonts/RobotoMono-Regular.ttf").
                        addText("EMAUS CASA FUNERARIA").
                addText("S.A. DE C.V.").
                addParagraph().
                addText(localTimeOut).
                addParagraph().
                setAlign(Paint.Align.LEFT).
                addText("Empleado ID", false).
                setAlign(Paint.Align.RIGHT).
                addText(empleado).
                addParagraph().
                addLine().
                addParagraph();

        //Ticket=0, Deposito=1, Retiro=2
        for(Movimiento_Firebase movimientoFirebase : mDataset){
            switch (movimientoFirebase.getTipoMovimientoID()){
                case TICKET:{ tickets++;  cantTickets+=movimientoFirebase.getMovimiento(); break; }
                case DEPOSITO:{depositos++; cantDepositos+=movimientoFirebase.getMovimiento(); break;}
                case RETIRO:{ retiros++; cantRetiros+=movimientoFirebase.getMovimiento(); break; }
            }
            calendar.setTimeInMillis(movimientoFirebase.getCreatedAt()*1000);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
            sdf.setTimeZone(tz);
            String localTime = sdf.format(new Date(movimientoFirebase.getCreatedAt()*1000));

            receipt.setAlign(Paint.Align.LEFT).
                    addText(localTime).
                    addText(movimientoFirebase.getEmpleado_fecha()+"_"+movimientoFirebase.getCreatedAt()).
                    addText(movimientoFirebase.getTipoMovimiento().toUpperCase()).
                    setAlign(Paint.Align.RIGHT).
                    addText(formatMoney(movimientoFirebase.getMovimiento())).addParagraph();
        }

        receipt.setAlign(Paint.Align.LEFT).
                addParagraph().
                addLine().
                addParagraph().

                setAlign(Paint.Align.LEFT).
                addText(tickets+" COBROS", false).
                setAlign(Paint.Align.RIGHT).
                addText(formatMoney(cantTickets)).

                setAlign(Paint.Align.LEFT).
                addText(depositos+" DEPOSITOS", false).
                setAlign(Paint.Align.RIGHT).
                addText(formatMoney(cantDepositos)).

                setAlign(Paint.Align.LEFT).
                addText(retiros+" RETIROS", false).
                setAlign(Paint.Align.RIGHT).
                addText(formatMoney(cantRetiros)).
                addParagraph().
                addParagraph().

                setAlign(Paint.Align.LEFT).
                addText(""+mDataset.size()+" MOV",false).
                setAlign(Paint.Align.RIGHT).
                addText(formatMoney(cantTickets-cantDepositos-cantRetiros)).
                addParagraph().
                addParagraph();
                //addImage(barcode);
        image.setImageBitmap(receipt.build());
    }

    private void abrirAjustes(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void firebaseCompleted(ArrayList<Object_Firebase> arrayList) {
        for(Object_Firebase obj : arrayList){
            mDataset.add((Movimiento_Firebase)obj);
        }

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                generateAndDraw();
            }
        });
    }

    private String formatMoney(float f){
        return getString(R.string.format_cantidad, NumberFormat.getNumberInstance(Locale.US).format(Math.ceil(f)));
    }


    @Override
    public void notPairingFound() {
        PrintCorte.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,"--------------- NOTpairingFound! ---------------");
                iconPairing.setImageDrawable(ContextCompat.getDrawable(PrintCorte.this, R.mipmap.ic_bt_red));
                labelFeedback.setText("No has vinculado ninguna impresora");
            }
        });
    }

    @Override
    public void pairingFound() {
        PrintCorte.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"--------------- pairingFound! ---------------");
                iconPairing.setImageDrawable(ContextCompat.getDrawable(PrintCorte.this, R.mipmap.ic_bt_yellow));
                labelFeedback.setText("Conectando a impresora vinculada...");
            }
        });
    }

    @Override
    public void notifyPrintingActivityOfFailure() {
        PrintCorte.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, " --------------- NO PRINTER CONNECTION --------------");
                iconPrinter.setImageDrawable(ContextCompat.getDrawable(PrintCorte.this, R.drawable.ic_printer_red));
                labelFeedback.setText("Error al conectar a la impresora vinculada");
            }
        });
    }

    @Override
    public void notifyPrinterActivity() {
        PrintCorte.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, " --------------- ACTIVITY NOTIFIED --------------");
                labelFeedback.setText("Ticket impreso correctamente");
                iconTicket.setImageDrawable(ContextCompat.getDrawable(PrintCorte.this, R.drawable.ic_ticket_yellow));


                String empleado = WS.getCurrentUser().getEmail().split("@")[0].replace(".","");
                Calendar calendar = Calendar.getInstance();
                TimeZone tz = calendar.getTimeZone();
                SimpleDateFormat sdfOut = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
                sdfOut.setTimeZone(tz);
                String localTimeOut = sdfOut.format(System.currentTimeMillis());

                btPrinter.startPrintingCanvas();
                btPrinter.printAlignedText(PrintingCommands.alignCenter, "EMAUS CASA FUNERARIA");
                btPrinter.printAlignedText(PrintingCommands.alignCenter, "S.A. DE C.V.");
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();
                btPrinter.printAlignedText(PrintingCommands.alignCenter, localTimeOut);
                btPrinter.addLineBreak();
                btPrinter.printAlignedText(PrintingCommands.alignRight, "EmpleadoID : "+empleado);
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();

                for(Movimiento_Firebase movimientoFirebase : mDataset){
                    calendar.setTimeInMillis(movimientoFirebase.getCreatedAt()*1000);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
                    sdf.setTimeZone(tz);
                    String localTime = sdf.format(new Date(movimientoFirebase.getCreatedAt()*1000));


                    btPrinter.printAlignedText(PrintingCommands.alignLeft, localTime);
                    btPrinter.printAlignedText(PrintingCommands.alignLeft, movimientoFirebase.getEmpleado_fecha()+"_"+movimientoFirebase.getCreatedAt());
                    btPrinter.printAlignedText(PrintingCommands.alignLeft, movimientoFirebase.getTipoMovimiento().toUpperCase());
                    btPrinter.printAlignedText(PrintingCommands.alignRight, formatMoney(movimientoFirebase.getMovimiento()));
                    btPrinter.addLineBreak();

                    /*
                    receipt.setAlign(Paint.Align.LEFT).
                            addText(localTime).
                            addText(movimientoFirebase.getEmpleado_fecha()+"_"+movimientoFirebase.getCreatedAt()).
                            addText(movimientoFirebase.getTipoMovimiento().toUpperCase()).
                            setAlign(Paint.Align.RIGHT).
                            addText(formatMoney(movimientoFirebase.getMovimiento())).addParagraph();
                            */
                }
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();
                //btPrinter.printAlignedText(PrintingCommands.alignLeft, tickets+" COBROS");
                btPrinter.printAlignedText(PrintingCommands.alignRight, ""+tickets+" COBROS = "+formatMoney(cantTickets));
                //btPrinter.printAlignedText(PrintingCommands.alignLeft, depositos+" DEPOSITOS");
                btPrinter.printAlignedText(PrintingCommands.alignRight, ""+depositos+" DEPOSITOS = "+formatMoney(cantDepositos));
                //btPrinter.printAlignedText(PrintingCommands.alignLeft, retiros+" RETIROS");
                btPrinter.printAlignedText(PrintingCommands.alignRight, ""+retiros+" RETIROS = "+formatMoney(cantRetiros));
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();
                //btPrinter.printAlignedText(PrintingCommands.alignLeft, ""+mDataset.size()+" MOV");
                btPrinter.printAlignedText(PrintingCommands.alignRight, "TOTAL EFECTIVO = "+formatMoney(cantTickets-cantDepositos-cantRetiros));
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();
                btPrinter.printTicket();




                /*
                String empleado = WS.getCurrentUser().getEmail().split("@")[0].replace(".","");
                barcode = BitmapFactory.decodeResource(this.getResources(), R.drawable.barcode);

                Calendar calendar = Calendar.getInstance();
                TimeZone tz = calendar.getTimeZone();
                SimpleDateFormat sdfOut = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
                sdfOut.setTimeZone(tz);
                String localTimeOut = sdfOut.format(System.currentTimeMillis());

                ReceiptBuilder receipt = new ReceiptBuilder(1200);
                receipt.setMargin(30, 20).
                        setAlign(Paint.Align.CENTER).
                        setColor(Color.BLACK).
                        setTextSize(50).
                        //setTypeface(this, "fonts/RobotoMono-Regular.ttf").
                                addText("EMAUS CASA FUNERARIA").
                        addText("S.A. DE C.V.").
                        addParagraph().
                        addText(localTimeOut).
                        addParagraph().
                        setAlign(Paint.Align.LEFT).
                        addText("Empleado ID", false).
                        setAlign(Paint.Align.RIGHT).
                        addText(empleado).
                        addParagraph().
                        addLine().
                        addParagraph();

                int tickets=0; int retiros=0; int depositos=0;
                int cantidadFinal=0;
                float cantTickets=0; float cantRetiros=0; float cantDepositos=0;
                final int TICKET=0; final int DEPOSITO=1; final int RETIRO=2;
                //Ticket=0, Deposito=1, Retiro=2
                for(Movimiento_Firebase movimientoFirebase : mDataset){
                    switch (movimientoFirebase.getTipoMovimientoID()){
                        case TICKET:{ tickets++;  cantTickets+=movimientoFirebase.getMovimiento(); break; }
                        case DEPOSITO:{depositos++; cantDepositos+=movimientoFirebase.getMovimiento(); break;}
                        case RETIRO:{ retiros++; cantRetiros+=movimientoFirebase.getMovimiento(); break; }
                    }
                    calendar.setTimeInMillis(movimientoFirebase.getCreatedAt()*1000);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
                    sdf.setTimeZone(tz);
                    String localTime = sdf.format(new Date(movimientoFirebase.getCreatedAt()*1000));

                    receipt.setAlign(Paint.Align.LEFT).
                            addText(localTime).
                            addText(movimientoFirebase.getEmpleado_fecha()+"_"+movimientoFirebase.getCreatedAt()).
                            addText(movimientoFirebase.getTipoMovimiento().toUpperCase()).
                            setAlign(Paint.Align.RIGHT).
                            addText(formatMoney(movimientoFirebase.getMovimiento())).addParagraph();
                }

                receipt.setAlign(Paint.Align.LEFT).
                        addParagraph().
                        addLine().
                        addParagraph().

                        setAlign(Paint.Align.LEFT).
                        addText(tickets+" COBROS", false).
                        setAlign(Paint.Align.RIGHT).
                        addText(formatMoney(cantTickets)).

                        setAlign(Paint.Align.LEFT).
                        addText(depositos+" DEPOSITOS", false).
                        setAlign(Paint.Align.RIGHT).
                        addText(formatMoney(cantDepositos)).

                        setAlign(Paint.Align.LEFT).
                        addText(retiros+" RETIROS", false).
                        setAlign(Paint.Align.RIGHT).
                        addText(formatMoney(cantRetiros)).
                        addParagraph().
                        addParagraph().

                        setAlign(Paint.Align.LEFT).
                        addText(""+mDataset.size()+" MOV",false).
                        setAlign(Paint.Align.RIGHT).
                        addText("$ "+formatMoney(cantTickets-cantDepositos-cantRetiros)).
                        addParagraph().
                        addParagraph();
                */

                /*
                btPrinter.startPrintingCanvas();
                btPrinter.printAlignedText(PrintingCommands.alignCenter, "CORTE IMPRIMIENDO");
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();
                btPrinter.printTicket();
                */

                /*
                btPrinter.printAlignedText(PrintingCommands.alignCenter, "Emiliano Zapata");
                btPrinter.printAlignedText(PrintingCommands.alignCenter, "940 PTE, COL. Jorge Almada");
                btPrinter.printAlignedText(PrintingCommands.alignCenter,"Culiacan, Sinaloa, Mexico");
                btPrinter.printAlignedText(PrintingCommands.alignCenter,"(667) 761 21 61");
                btPrinter.addLineBreak();
                btPrinter.printAlignedText(PrintingCommands.alignLeft,"EmpleadoID: "+WS.getCurrentUser().getEmail().split("@")[0].replace(".",""));
                btPrinter.printAlignedText(PrintingCommands.alignLeft,"Fecha: "+ticketFirebase.getKeyDiaCreacion());
                btPrinter.addLineBreak();
                btPrinter.printAlignedText(PrintingCommands.alignLeft,"EFECTIVO");
                btPrinter.printAlignedText(PrintingCommands.alignLeft,"CLIENTE: "+ticketFirebase.getClienteID());
                btPrinter.printAlignedText(PrintingCommands.alignLeft,"PLAN: "+ticketFirebase.getPlanID());
                btPrinter.addLineBreak();
                btPrinter.printAlignedText(PrintingCommands.alignLeft,"Abono #"+ticketFirebase.getNumAbono());
                btPrinter.addLineBreak();
                btPrinter.printAlignedText(PrintingCommands.alignLeft, "SALDO ANTERIOR: $"+(ticketFirebase.getNuevoSaldo()+ticketFirebase.getMonto()));
                btPrinter.printAlignedText(PrintingCommands.alignLeft,"SALDO ACTUAL: $"+ticketFirebase.getNuevoSaldo());
                btPrinter.addLineBreak();
                btPrinter.printAlignedText(PrintingCommands.alignRight, "MONTO PAGADO: $"+ticketFirebase.getMonto());
                btPrinter.addLineBreak();
                btPrinter.printAlignedText(PrintingCommands.alignCenter,"APROVADO ONLINE");
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();
                btPrinter.printTicket();
                */
            }
        });


    }
}
