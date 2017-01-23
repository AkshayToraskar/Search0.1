package com.ak.search.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.ak.search.app.ChangeUIFromThread;
import com.ak.search.app.ParcebleUtil;
import com.ak.search.model.MTransferModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.realm.Realm;

/**
 * Created by dg hdghfd on 10-01-2017.
 */

public class ThreadConnected extends Thread {
    private final BluetoothSocket connectedBluetoothSocket;
    private final InputStream connectedInputStream;
    private final OutputStream connectedOutputStream;
    private static String TAG = "Thread Connected";
    Activity act;
    Realm realm;
    ChangeUIFromThread changeUIFromThread;

    public ThreadConnected(ChangeUIFromThread changeUIFromThread, BluetoothSocket socket, Activity act) {
        connectedBluetoothSocket = socket;
        this.act = act;
        this.changeUIFromThread = changeUIFromThread;
        InputStream in = null;
        OutputStream out = null;

        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        connectedInputStream = in;
        connectedOutputStream = out;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[63000];
        int bytes;

        realm = Realm.getDefaultInstance();

        while (true) {
            try {
                bytes = connectedInputStream.read(buffer);



                //String strReceived = new String(buffer, 0, bytes);
                int bytes1=0;
                int messageSize= connectedInputStream.read(buffer);
                while(bytes1 < messageSize)
                {
                    bytes1 += connectedInputStream.read(buffer,bytes1,messageSize - bytes1);

                }
                int len = bytes1;


                final String msgReceived = String.valueOf(bytes) +
                        " bytes received:\n";
                        //+ data.getName();

                Log.v(TAG, msgReceived);


                act.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //textStatus.setText(msgReceived);
                        changeUIFromThread.changeStatus(msgReceived);
                    }
                });

                MTransferModel data = (MTransferModel) ParcebleUtil.deserialize(buffer);


                DataUtils dataUtils = new DataUtils();
                dataUtils.saveData(data, realm);



            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                final String msgConnectionLost = "Connection lost:\n"
                        + e.getMessage();
                Log.v(TAG, msgConnectionLost);
                act.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //textStatus.setText(msgConnectionLost);
                        changeUIFromThread.changeStatus(msgConnectionLost);
                        changeUIFromThread.disconnectThread();
                    }
                });
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                act.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        changeUIFromThread.disconnectThread();
                    }
                });
            }
        }
    }

    public void write(byte[] buffer) {
        try {
            connectedOutputStream.write(buffer);

           /* int BIG_NUM=1000;
            for(int i=0; i<buffer.length;i+=BIG_NUM)
            {
                int b = ((i+BIG_NUM) < buffer.length) ? BIG_NUM: buffer.length - i;
                connectedOutputStream.write(buffer,i,b);
                connectedOutputStream.flush();
            }*/

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void cancel() {
        try {
            connectedBluetoothSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}