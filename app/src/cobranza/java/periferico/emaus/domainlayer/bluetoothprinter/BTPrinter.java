package periferico.emaus.domainlayer.bluetoothprinter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class BTPrinter{

    private final String TAG = "BTPrinterDebug";

    private ConnectThread connectThread;

    public BTPrinter(){}

    public void startProcess(BluetoothDevice device){
        if(connectThread==null) {
            connectThread = new ConnectThread(device);
            connectThread.writingBufferReady = new ConnectThread.WritingBufferReady() {
                @Override
                public void notifyBTPrinter() {
                    btprtpListener.notifyPrinterActivity();
                }
            };
            connectThread.start();
        }
    }

    public void finishProcess(){
        try {
            if (connectThread != null) {
                connectThread.closeConnectedSocket();
                connectThread.cancel();
            }
        }catch (Exception e){e.printStackTrace();}
    }

    private void createTicketCanvas(){

    }

    private void skipLines(int nLines){
        // /d(n)
    }

    public interface ReadyToPrintListener{  public void notifyPrinterActivity();
    }public void setPrintingReadyListener(ReadyToPrintListener btprtpL){
        btprtpListener=btprtpL;
    }public ReadyToPrintListener btprtpListener;

}
