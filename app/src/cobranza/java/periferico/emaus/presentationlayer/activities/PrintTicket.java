package periferico.emaus.presentationlayer.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.danielfelgar.drawreceiptlib.ReceiptBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.bluetoothprinter.BTPrinter;
import periferico.emaus.domainlayer.bluetoothprinter.PrintingActivity;
import periferico.emaus.domainlayer.bluetoothprinter.PrintingCommands;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Ticket_Firebase;

public class PrintTicket extends PrintingActivity implements
        BTPrinter.ReadyToPrintListener,
        BTPrinter.FailureNotifiedListener,
        PrintingActivity.PairingStatusListener{

    /*
    private static final String TAG = "PrintTicket";
    private static final String TAG_BT = "BluetoothDebug";
    private static final int REQUEST_ENABLE_BT = 3736;
    */

    private Toolbar toolbar;
    private ImageView image;
    private ImageView iconPairing;
    private ImageView iconPrinter;
    private ImageView iconTicket;
    private TextView labelFeedback;
    Ticket_Firebase ticketFirebase;
    Bitmap barcode;

    /*
    View viewTodosPermisos;
    Button buttonAbrirAjustes;
    DexterBuilder dexterBuilder;
    */

    /*
    BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mReceiver;
    BluetoothDevice finalDevice;
    BTPrinter btPrinter;
    boolean sentToPrint=false;
    */

    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_ticket);

        instanciateObjects();

        instanciateToolbar();

        WS.readTicket(getIntent().getStringExtra("ticketID"), new WS.FirebaseObjectRetrievedListener() {
            @Override
            public void firebaseObjectRetrieved(Object_Firebase objectFirebase) {
                ticketFirebase = (Ticket_Firebase)objectFirebase;
                setPermissionsLogic();
                dexterBuilder.check();

                generateAndDraw();
            }
        });

        btPrinter = new BTPrinter();
        pairingStatusListener=PrintTicket.this;
        btPrinter.setPrintingReadyListener(PrintTicket.this);
        btPrinter.setPrintinfFailureListener(PrintTicket.this);

    }

    private void generateAndDraw(){
        String empleado = WS.getCurrentUser().getEmail().split("@")[0].replace(".","");
        barcode = BitmapFactory.decodeResource(this.getResources(), R.drawable.barcode);

        //MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        //try {
            Log.d(TAG,getIntent().getStringExtra("ticketID") );
            String extraTicketID = getIntent().getStringExtra("ticketID");
            extraTicketID = extraTicketID.replace("é","e");
            extraTicketID = extraTicketID.replace("á","a");
            extraTicketID = extraTicketID.replace("í","i");
            extraTicketID = extraTicketID.replace("ó","o");
            extraTicketID = extraTicketID.replace("ú","u");
            /*
            BitMatrix bitMatrix = multiFormatWriter.encode(getIntent().getStringExtra("ticketID"), BarcodeFormat.CODE_128,barcode.getWidth(),barcode.getHeight());
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            barcode = barcodeEncoder.createBitmap(bitMatrix);
            */
        //} catch (WriterException e) {
            //e.printStackTrace();
        //}

        Log.d(TAG, "empleado="+ticketFirebase.getEmpleadoID()+" keyDia="+ticketFirebase.getKeyDiaCreacion()+" CLIENTE: "+ticketFirebase.getClienteID()+" CUENTA: "+ticketFirebase.getPlanID()
        +" abono= "+ticketFirebase.getNumAbono()+" monto="+ticketFirebase.getMonto());

        ReceiptBuilder receipt = new ReceiptBuilder(1200);
        receipt.setMargin(30, 20).
                setAlign(Paint.Align.CENTER).
                setColor(Color.BLACK).
                setTextSize(50).
                //setTypeface(this, "fonts/RobotoMono-Regular.ttf").
                        addText("Emiliano Zapata").
                addText("940 PTE, COL. Jorge Almada").
                addText("Culiacán, Sinaloa, México").
                addText("(667) 761 21 61").
                addBlankSpace(30).
                setAlign(Paint.Align.LEFT).
                addText("Empleado ID: "+empleado).
                addText("Fecha: "+ticketFirebase.getKeyDiaCreacion()).
                addParagraph().
                addText("EFECTIVO").
                addText("Cliente: "+ticketFirebase.getClienteID()).
                addText("PLAN: "+ticketFirebase.getPlanID()).
                addParagraph().
                //setTypeface(this, "fonts/RobotoMono-Bold.ttf").
                addText("Abono #"+ticketFirebase.getNumAbono()).
                addParagraph().
                addText("SALDO ANT: $ "+(ticketFirebase.getNuevoSaldo()+ticketFirebase.getMonto())).
                addText("SALDO ACTUAL: $ "+ticketFirebase.getNuevoSaldo()).
                addLine(180).
                setAlign(Paint.Align.RIGHT).
                addParagraph().
                addText("MONTO PAGADO:  $ "+ticketFirebase.getMonto()).
                addParagraph().
                setAlign(Paint.Align.CENTER).
                //setTypeface(this, "fonts/RobotoMono-Regular.ttf").
                        addText("APROVADO ONLINE").
                addParagraph().
                addImage(barcode);
        image.setImageBitmap(receipt.build());

        /*
        Geocoder coder = new Geocoder(PrintTicket.this);
        try {
            List<Address> enderecos = coder.getFromLocation(-22.90827, -47.06501, 1);
            enderecos.isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    private void instanciateObjects(){

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
            getSupportActionBar().setTitle("Ticket");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void abrirAjustes(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
    }

    @Override
    public void notPairingFound() {
        PrintTicket.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,"--------------- NOTpairingFound! ---------------");
                iconPairing.setImageDrawable(ContextCompat.getDrawable(PrintTicket.this, R.mipmap.ic_bt_red));
                labelFeedback.setText("No has vinculado ninguna impresora");
            }
        });
    }

    @Override
    public void pairingFound() {
        PrintTicket.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"--------------- pairingFound! ---------------");
                iconPairing.setImageDrawable(ContextCompat.getDrawable(PrintTicket.this, R.mipmap.ic_bt_yellow));
                labelFeedback.setText("Conectando a impresora vinculada...");
            }
        });
    }

    @Override
    public void notifyPrintingActivityOfFailure() {
        PrintTicket.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, " --------------- NO PRINTER CONNECTION --------------");

                iconPrinter.setImageDrawable(ContextCompat.getDrawable(PrintTicket.this, R.drawable.ic_printer_red));
                labelFeedback.setText("Error al conectar a la impresora vinculada");
            }
        });

    }

    @Override
    public void notifyPrinterActivity() {
        PrintTicket.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, " --------------- ACTIVITY NOTIFIED --------------");
                labelFeedback.setText("Ticket impreso correctamente");
                iconTicket.setImageDrawable(ContextCompat.getDrawable(PrintTicket.this, R.drawable.ic_ticket_yellow));

                //btPrinter.printAlignedText(PrintingCommands.align,"");

                btPrinter.startPrintingCanvas();

                /*
                btPrinter.printAlignedText(PrintingCommands.alignCenter, "CRISTINA BABY");
                btPrinter.printAlignedText(PrintingCommands.alignCenter, "Sinaloa 216, Roma N");
                btPrinter.printAlignedText(PrintingCommands.alignCenter,"CDMX, Mexico");
                btPrinter.printAlignedText(PrintingCommands.alignCenter,"-----------------");
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();
                btPrinter.printAlignedText(PrintingCommands.alignCenter,"VALE POR UN ABRAZO");
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();


                btPrinter.printAlignedText(PrintingCommands.alignLeft,"Fecha: 8/Ago/2018");
                btPrinter.printAlignedText(PrintingCommands.alignLeft,"Valido por: 1 año");
                btPrinter.addLineBreak();
                btPrinter.printAlignedText(PrintingCommands.alignCenter,"Aplica solo para Mau");
                btPrinter.printAlignedText(PrintingCommands.alignCenter,"Aplican terminos y condiciones");
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();

                btPrinter.printAlignedText(PrintingCommands.alignCenter,"-- APROVADO --");
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();
                btPrinter.addLineBreak();
                */

                /*
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
                */
                //btPrinter.printTicket();


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

            }
        });


    }

}
