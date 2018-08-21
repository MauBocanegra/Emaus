package periferico.emaus.domainlayer.bluetoothprinter;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Set;

public class PrintingActivity extends AppCompatActivity implements BTPrinter.ReadyToPrintListener {


    public static final String TAG = "PrintingActivity";
    public static final String TAG_BT = "BluetoothDebug";
    public static final int REQUEST_ENABLE_BT = 3736;

    public View viewTodosPermisos;
    public Button buttonAbrirAjustes;
    public DexterBuilder dexterBuilder;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice finalDevice;
    public BTPrinter btPrinter;
    boolean sentToPrint=false;

    @Override
    public void notifyPrinterActivity() {}

    // --------------------------------------------- //
    // ---------------- OWN METHODS ---------------- //
    //---------------------------------------------- //


    public void setPermissionsLogic(){
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

        dexterBuilder = Dexter.withActivity(PrintingActivity.this).withPermissions(
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

    private void searchForPairedDevices(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            int pairedCount=0;
            for (BluetoothDevice device : pairedDevices) {
                pairedCount++;
                // Add the name and address to an array adapter to show in a ListView

                try{
                    if(device.getUuids()!=null){
                        for(ParcelUuid parcel : device.getUuids()){
                            Log.d(TAG_BT, device.getName()+"["+parcel.getUuid()+"]");
                            if(!sentToPrint && parcel.getUuid().toString().toUpperCase().equals("00001101-0000-1000-8000-00805F9B34FB") && device.getName().toUpperCase().contains("PRINT")){
                                Log.d(TAG_BT, "DEVICE IS = "+(device==null ? "NULL" : "IS OK OK OK"));
                                finalDevice = device;
                                Log.d(TAG_BT,"Printer por UUID - "+device.getUuids()[0].getUuid());
                                sentToPrint=true;
                                pairingStatusListener.pairingFound();
                                btPrinter.startProcess(device);
                                break;
                            }
                        }

                        //Si no esta vinculada la impresora
                        if(!sentToPrint){
                            pairingStatusListener.notPairingFound();
                        }
                    }
                }catch(Exception e){e.printStackTrace();}
            }
        }else{
            Log.d(TAG_BT,"pairedDevs=0");
        }
    }


    // ----------------------------------------------------------- //
    // ---------------- BT-PRINTER IMPLEMENTATION ---------------- //
    //------------------------------------------------------------ //

    public interface PairingStatusListener{
        public void notPairingFound();
        public void pairingFound();
    }public PairingStatusListener pairingStatusListener;

    // ------------------------------------------------------------ //
    // ---------------- LIFE CYCLE IMPLEMENTATIONS ---------------- //
    //------------------------------------------------------------- //

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG_BT, "requestCode="+requestCode+" result="+resultCode+"=="+this.RESULT_OK+"?");

        if(requestCode== REQUEST_ENABLE_BT && resultCode == this.RESULT_OK){
            searchForPairedDevices();
        }
    }

}
