package periferico.emaus.presentationlayer.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
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
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.bluetoothprinter.BTPrinter;
import periferico.emaus.domainlayer.bluetoothprinter.ConnectThread;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Ticket_Firebase;

public class PrintTicket extends AppCompatActivity implements
        BTPrinter.ReadyToPrintListener{

    private static final String TAG = "PrintTicketDebug";
    private static final String TAG_BT = "BluetoothDebug";
    private static final int REQUEST_ENABLE_BT = 3736;
    private Toolbar toolbar;
    private ImageView image;
    Ticket_Firebase ticketFirebase;
    Bitmap barcode;

    View viewTodosPermisos;
    Button buttonAbrirAjustes;
    DexterBuilder dexterBuilder;

    BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mReceiver;
    BluetoothDevice finalDevice;
    BTPrinter btPrinter;

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
        btPrinter.setPrintingReadyListener(PrintTicket.this);

    }



    private void setPermissionsLogic(){

        MultiplePermissionsListener permissionsChecked = new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                if(report.isAnyPermissionPermanentlyDenied()){
                    viewTodosPermisos.setVisibility(View.VISIBLE);
                    Log.d("TAG","PERMANENTLY DENIED");
                    return;
                }

                if(report.areAllPermissionsGranted()){
                    try {
                        viewTodosPermisos.setVisibility(View.GONE);
                        askForBluetoothPermissions();

                    }catch(SecurityException se){se.printStackTrace();}
                }
                else{
                    viewTodosPermisos.setVisibility(View.VISIBLE);
                }
            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        };

        MultiplePermissionsListener compositePermissionsListener =
                new CompositeMultiplePermissionsListener(
                        //snackbarMultiplePermissionsListener,
                        //dialogMultiplePermissionsListener,
                        permissionsChecked);

        dexterBuilder = Dexter.withActivity(PrintTicket.this).withPermissions(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
        ).withListener(compositePermissionsListener).onSameThread();
    }

    private void askForBluetoothPermissions(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG_BT,"wontStartDiscovery");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else{
            searchForPairedDevices();
        }
    }

    private void generateAndDraw(){
        String empleado = WS.getCurrentUser().getEmail().split("@")[0].replace(".","");
        barcode = BitmapFactory.decodeResource(this.getResources(), R.drawable.barcode);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            Log.d(TAG,getIntent().getStringExtra("ticketID") );
            BitMatrix bitMatrix = multiFormatWriter.encode(getIntent().getStringExtra("ticketID"), BarcodeFormat.CODE_128,barcode.getWidth(),barcode.getHeight());
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            barcode = barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }

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
                addText("Empleado ID", false).
                setAlign(Paint.Align.RIGHT).
                addText(empleado).
                setAlign(Paint.Align.LEFT).
                addText(ticketFirebase.getKeyDiaCreacion(), false).
                setAlign(Paint.Align.RIGHT).
                addText("SERVER #4").
                setAlign(Paint.Align.LEFT).
                addParagraph().
                addText("EFECTIVO").
                addText("Cliente: "+ticketFirebase.getClienteID()).
                addText("PLAN: "+ticketFirebase.getPlanID()).
                addParagraph().
                //setTypeface(this, "fonts/RobotoMono-Bold.ttf").
                        addText("Abono").
                addText("Num: "+ticketFirebase.getNumAbono(), false).
                setAlign(Paint.Align.RIGHT).
                addText("REF #: 1234").
                //setTypeface(this, "fonts/RobotoMono-Regular.ttf").
                        setAlign(Paint.Align.LEFT).
                addText("BATCH #: 091", false).
                setAlign(Paint.Align.RIGHT).
                addText("AUTH #: 0701C").
                setAlign(Paint.Align.LEFT).
                addParagraph().
                //setTypeface(this, "fonts/RobotoMono-Bold.ttf").
                        addText("SALDO ANT", false).
                setAlign(Paint.Align.RIGHT).
                addText("$ "+(ticketFirebase.getNuevoSaldo()+ticketFirebase.getMonto())).
                setAlign(Paint.Align.LEFT).
                addParagraph().
                addText("SALDO ACTUAL", false).
                setAlign(Paint.Align.RIGHT).
                addText("$ "+ticketFirebase.getNuevoSaldo()).
                addLine(180).
                setAlign(Paint.Align.LEFT).
                addParagraph().
                addLine().
                addText("--- MONTO PAGADO ---", false).
                setAlign(Paint.Align.RIGHT).
                addText("$ "+ticketFirebase.getMonto()).
                addLine().
                addParagraph().
                setAlign(Paint.Align.CENTER).
                //setTypeface(this, "fonts/RobotoMono-Regular.ttf").
                        addText("APROVADO").
                addParagraph().
                addImage(barcode);
        image.setImageBitmap(receipt.build());

        Geocoder coder = new Geocoder(PrintTicket.this);
        try {
            List<Address> enderecos = coder.getFromLocation(-22.90827, -47.06501, 1);
            enderecos.isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void instanciateObjects(){

        viewTodosPermisos = findViewById(R.id.cardview_permissions);
        buttonAbrirAjustes = findViewById(R.id.permissions_abrir_ajustes);
        image = findViewById(R.id.ticket_image);

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

    private void createBroadcastReceiver(){
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG_BT,"onReceiveBroadcastBT");
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    Log.d(TAG_BT,device.getName() + " - " + device.getAddress());
                    //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            }
        };

        Log.d(TAG_BT,"registerReceiver");
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    }

    private void searchForPairedDevices(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Log.d(TAG_BT,device.getName() + " - " + device.getAddress());
                if(device.getAddress().equals("0F:02:18:30:12:2D")){
                    finalDevice = device;
                    Log.d(TAG_BT,"Encontro ESTE printer - "+device.getUuids()[0].getUuid());
                    btPrinter.startProcess(device);
                    break;
                }
            }
        }else{
            Log.d(TAG_BT,"pairedDevs=0");
        }
    }

    private void startBTDiscovery(){
        Log.d(TAG_BT,"willStartDiscovery");
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 12);
        startActivity(discoverableIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mReceiver != null) {
                Log.d(TAG_BT, "registerReceiver");
                unregisterReceiver(mReceiver);
            }
            if(btPrinter!=null) btPrinter.finishProcess();
        }catch(Exception e){e.printStackTrace();}

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG_BT, "requestCode="+requestCode+" result="+resultCode+"=="+this.RESULT_OK+"?");

        if(requestCode== REQUEST_ENABLE_BT && resultCode == this.RESULT_OK){
            searchForPairedDevices();
        }
    }

    @Override
    public void notifyPrinterActivity() {
        Log.d(TAG, " --------------- ACTIVITY NOTIFIED --------------");
    }
}
