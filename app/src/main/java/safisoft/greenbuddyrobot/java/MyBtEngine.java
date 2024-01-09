package safisoft.greenbuddyrobot.java;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MyBtEngine {
    InputStream tmpIn = null;
    OutputStream tmpOut = null;
    static final int BT_STATE_NONE = 0;
    static final int BT_STATE_CONNECTING = 1;
    static final int BT_STATE_CONNECTED = 2;
    private static final UUID UUID_BT_DEVICE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static  String BT_DEVICE_MAC = "";//put your device MAC !!!!
    private BtWaitConnThread   mWaitConnThread    = null;
    private BtWorkThread       mWorkThread        = null;
    private final BluetoothAdapter mAdapter;
    private int mState;
    public static final int RESPONSE_MESSAGE = 10;
    Handler handler ;


    MyBtEngine() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState   =  BT_STATE_NONE;
    }
    synchronized void start(){
        if(mAdapter == null){  return; }
        BluetoothDevice device;
        try{
            device = mAdapter.getRemoteDevice(BT_DEVICE_MAC);
        }
        catch (Exception e){  return; }

        if (mWaitConnThread != null) {
            mWaitConnThread.cancel();
            mWaitConnThread = null;
        }
        if (mWorkThread != null) {
            mWorkThread.cancel();
            mWorkThread = null;
        }

        mWaitConnThread = new BtWaitConnThread(device);
        mWaitConnThread.start();
        setState(BT_STATE_CONNECTING);
    }
    public synchronized void stop() {
        if (mWaitConnThread != null) {
            mWaitConnThread.cancel();
            mWaitConnThread = null;
        }

        if (mWorkThread != null) {
            mWorkThread.cancel();
            mWorkThread = null; }

        setState(BT_STATE_NONE);
    }
    public synchronized void setState(int state) {
        Log.e("MyBtEngine", "setState() " + mState + " -> " + state);
        mState = state;
    }
    public synchronized int getState() {  return mState; }
    public boolean writeBt(byte[] out) {
        BtWorkThread r; // temp obj , just to keep write function not to destroyed if mWorkThread finish
        synchronized (this) {
            if((mWorkThread == null)||(mState != BT_STATE_CONNECTED)){
                return false;
            }
            r = mWorkThread;
        }
        r.write(out);
        return true;
    }
    private synchronized void startWorker(BluetoothSocket socket, BluetoothDevice device) {
        if (mWaitConnThread != null) {mWaitConnThread.cancel(); mWaitConnThread = null;}
        if (mWorkThread != null) {    mWorkThread.cancel(); mWorkThread = null;}

        mWorkThread = new BtWorkThread(socket);
        mWorkThread.start();
        setState(BT_STATE_CONNECTED);

    }
    public void SET_MAC(String MAC){
        BT_DEVICE_MAC = MAC ;
    }

    public void SET_HANDLER (Handler hi){
        handler = hi ;
    }



    private class BtWaitConnThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private BtWaitConnThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(UUID_BT_DEVICE);
            } catch (IOException e) { }
            mmSocket = tmp;
        }
        public void run() {
            mAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException e) {
                try {
                    mmSocket.close();
                } catch (IOException e2) { }
                setState(BT_STATE_NONE);
                return;
            }
            synchronized (MyBtEngine.this) {
                mWaitConnThread = null;//set itself to null because thread exit soon
            }
            startWorker(mmSocket, mmDevice);
        }
        private void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }



    private class BtWorkThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private BtWorkThread(BluetoothSocket socket) {
            mmSocket = socket;
            try {
                tmpIn  = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream  = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            while (true) {
                try {
                 DataInputStream mmInStream = new DataInputStream(tmpIn);
                 bytes = mmInStream.read(buffer);
                 String readMessage = new String(buffer, 0, bytes);
                    Message msg = new Message();
                    msg.what = RESPONSE_MESSAGE;
                    msg.obj = readMessage;
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    if(mState != 0){
                        MyBtEngine.this.start();//restart from beginning
                    }
                    return; // exit worker thread
                }
            }
        }
        public boolean write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) { return false; }
            return true;
        }
        private void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }

    }

}