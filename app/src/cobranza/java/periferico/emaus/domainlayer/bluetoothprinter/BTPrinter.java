package periferico.emaus.domainlayer.bluetoothprinter;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

public class BTPrinter{

    private final String TAG = "BTPrinterDebug";

    private ConnectThread connectThread;
    private ArrayList<PrintingCommands> printingCommands;

    public BTPrinter(){}

    public BTPrinter(AppCompatActivity activity){

    }

    public void startProcess(BluetoothDevice device){
        if(connectThread==null) {
            connectThread = new ConnectThread(device);
            connectThread.writingBufferReady = new ConnectThread.WritingBufferReady() {
                @Override
                public void notifyBTPrinter() {
                    Log.d(TAG, "BTPrinter activity has been notified");
                    btprtpListener.notifyPrinterActivity();
                }
            };

            connectThread.writingBufferFailureListener = new ConnectThread.WritingBufferFailureListener() {
                @Override
                public void notifuyFailure() {
                    Log.e(TAG, "AN ERROR IN BT PRINTER -----------");
                    failureNotifiedListener.notifyPrintingActivityOfFailure();
                }
            };
            connectThread.start();

            if(!connectThread.isAlive()){
                Log.e(TAG, "AN ERROR IN BT PRINTER -----------");
                failureNotifiedListener.notifyPrintingActivityOfFailure();
            }
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

    public interface ReadyToPrintListener{  public void notifyPrinterActivity();
    }public void setPrintingReadyListener(ReadyToPrintListener btprtpL){
        btprtpListener=btprtpL;
    }public ReadyToPrintListener btprtpListener;

    public interface FailureNotifiedListener{ public void notifyPrintingActivityOfFailure();
    }public void setPrintinfFailureListener(FailureNotifiedListener fnl){
        failureNotifiedListener=fnl;
    }public FailureNotifiedListener failureNotifiedListener;

    //----------------------------------
    //----------------------------------
    //----------------------------------

    public void startPrintingCanvas(){
        printingCommands = new ArrayList<>();
        printingCommands.add(new PrintingCommands(PrintingCommands.printLinebreak));
    }

    public void printLinebreak(){
        printingCommands.add(new PrintingCommands(PrintingCommands.printLinebreak));
    }

    public void addLineBreak(){
        printingCommands.add(new PrintingCommands(PrintingCommands.addLineBreak));
    }

    public void sameLinePrinting(){
        printingCommands.add(new PrintingCommands(PrintingCommands.samelinePrinting));
    }

    public void printAlignedText(int alignment, String textToPrint){
        PrintingCommands printingCommandsObj = new PrintingCommands();
        printingCommandsObj.stringToPrintcode(alignment, textToPrint);
        printingCommands.add(printingCommandsObj);
    }


    public void printTicket(){

        printingCommands.add(new PrintingCommands(PrintingCommands.addEnding));

        //Primero calculamos el total de bytes
        int byteArrayLength = 0;
        for(PrintingCommands printingCommand : printingCommands){
            byteArrayLength+=printingCommand.byteArray.length;
        }

        //ahora creamos un solo array con todos los bytes
        byte[] byteArray = new byte[byteArrayLength];
        int byteArrayLengthIterator=0;
        for(PrintingCommands printingCommand : printingCommands){
            for(int i=0; i<printingCommand.byteArray.length; i++){
                Log.d(TAG, "b["+byteArrayLengthIterator+"] = printingCommand.byteArray["+i+"]("+Integer.toString(printingCommand.byteArray[i],16)+")");
                byteArray[byteArrayLengthIterator] = printingCommand.byteArray[i];
                byteArrayLengthIterator++;
            }
        }

        //mandamos a imprimir ese tren de bytes
        connectThread.writeBTPrinter(byteArray);
    }

}
