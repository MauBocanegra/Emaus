package periferico.emaus.presentationlayer.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.github.danielfelgar.drawreceiptlib.ReceiptBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.lang.reflect.Array;
import java.util.ArrayList;

import periferico.emaus.R;
import periferico.emaus.domainlayer.WS;
import periferico.emaus.domainlayer.firebase_objects.Object_Firebase;
import periferico.emaus.domainlayer.firebase_objects.Ticket_Firebase;

public class PrintCorte extends AppCompatActivity {

    private static final String TAG = "PrintCorteDebug";
    private Toolbar toolbar;
    private ImageView image;

    ArrayList<Ticket_Firebase> ticketsFirebase;

    Bitmap barcode;

    int pagos=0;
    float pagado=0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_corte);

        instanciateObjects();

        instanciateToolbar();

        downloadAndPrepare();
    }

    private void instanciateObjects(){
        image = findViewById(R.id.ticket_image);
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
        ticketsFirebase = new ArrayList<>();
        String fecha = getIntent().getStringExtra("stFecha");
        Log.d(TAG, "fecha="+fecha);
        WS.descargarTicketsPorFechaYCobrador(fecha, new WS.FirebaseArrayRetreivedListener() {
            @Override
            public void firebaseCompleted(ArrayList<Object_Firebase> arrayList) {
                pagos=0; pagado=0;
                for(Object_Firebase obj : arrayList){
                    Log.d(TAG, obj.toString());
                    ticketsFirebase.add((Ticket_Firebase)obj);
                    pagos++;
                    pagado+=((Ticket_Firebase)obj).getMonto();
                }
                generateAndDraw();

            }
        });
    }

    private void generateAndDraw(){
        String empleado = WS.getCurrentUser().getEmail().split("@")[0].replace(".","");
        barcode = BitmapFactory.decodeResource(this.getResources(), R.drawable.barcode);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(empleado, BarcodeFormat.CODE_128,barcode.getWidth(),barcode.getHeight());
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            barcode = barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }

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
                addParagraph().
                addLine().
                addParagraph();

        for(Ticket_Firebase ticketFirebase : ticketsFirebase){
            receipt.setAlign(Paint.Align.LEFT).
                    addText("Cobro #"+ticketFirebase.getNumAbono()).
                    addText("Ticket").
                    setTextSize(40).
                    addText(ticketFirebase.getStID()).
                    setTextSize(50).
                    addText(ticketFirebase.getKeyDiaCreacion()).
                    setAlign(Paint.Align.RIGHT).
                    addText("$ "+ticketFirebase.getMonto()).
                    addLine(180).addParagraph();

        }

        receipt.setAlign(Paint.Align.LEFT).
                addParagraph().
                addLine().
                addParagraph().
                addText("NUM PAGOS", false).
                setAlign(Paint.Align.RIGHT).
                addText("TOTAL").
                setAlign(Paint.Align.LEFT).
                addText(""+pagos+" PAGOS",false).
                setAlign(Paint.Align.RIGHT).
                addText("$ "+pagado).
                addParagraph().
                addLine().
                addParagraph().
                setAlign(Paint.Align.CENTER).
                //setTypeface(this, "fonts/RobotoMono-Regular.ttf").
                        addText("CORTE APROVADO").
                addParagraph().
                addImage(barcode);
        image.setImageBitmap(receipt.build());
    }
}
