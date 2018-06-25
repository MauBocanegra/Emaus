package periferico.emaus.domainlayer.bluetoothprinter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



public class ConnectThread extends Thread {
    private static final String TAG = "ConnectThreadDebug";
    private  ConnectedThread connectedThread;
    private final BluetoothSocket mmSocket;

    private static final int esc = 0x1b;

    private void manageConnectedSocket (BluetoothSocket mmSocket){
        connectedThread = new ConnectedThread(mmSocket);
        connectedThread.start();

        connectedThread.write(new byte[]{esc, 0x40, esc, 0x64, 0x02});

        //throwCallback();

        //btConnectionListener.isPairedAndReady();
    }

    public void closeConnectedSocket(){
        try {
            if (connectedThread != null) {
                connectedThread.cancel();
            }
        }catch(Exception e){e.printStackTrace();}

    }

    public ConnectThread(BluetoothDevice btDevice) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            Log.d(TAG, btDevice.getUuids()[0].getUuid().toString());
            tmp = btDevice.createRfcommSocketToServiceRecord(btDevice.getUuids()[0].getUuid());
        } catch (IOException e) { e.printStackTrace();}
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        //mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                closeException.printStackTrace();}
            return;
        }

        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mmSocket);
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { e.printStackTrace();}
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                Log.d(TAG,"getI/O");
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { Log.e(TAG,"I/O");}

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public interface WritingBufferReady{  public void notifyBTPrinter();
    }public void setWritingBufferReadyListener(WritingBufferReady wbr){
        writingBufferReady=wbr;
    }public WritingBufferReady writingBufferReady;
}
