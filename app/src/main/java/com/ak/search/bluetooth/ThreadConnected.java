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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;

/**
 * Created by dg hdghfd on 10-01-2017.
 */

public class ThreadConnected extends Thread {
    private  BluetoothSocket connectedBluetoothSocket;
    private  InputStream connectedInputStream;
    private  OutputStream connectedOutputStream;
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

        int bytes;
        List<byte[]> byteList = new ArrayList<>();
        realm = Realm.getDefaultInstance();

        while (true) {
            try {

                byte[] buffer = new byte[200];
                bytes = connectedInputStream.read(buffer);



                byteList.add(buffer);


                final String msgReceived = String.valueOf(bytes+ " bytes received");
                Log.v(TAG, msgReceived);


                if (connectedInputStream.available() == 0) {

                    Log.v("End of data", "asdf");

                    ByteBuffer bb = ByteBuffer.allocate(((byteList.size()) * 200) + bytes);

                    final String msgReceived1 = String.valueOf((((byteList.size()) * 200)-1) + bytes) + " bytes received: ";

                    for (int i = 0; i < byteList.size(); i++) {
                        bb.put(byteList.get(i));
                    }

                    byteList.clear();
                    byte[] result = bb.array();

                    final MTransferModel data = (MTransferModel) ParcebleUtil.deserialize(result);
                    DataUtils dataUtils = new DataUtils();
                    dataUtils.saveData(data, realm);

                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                          //  textStatus.setText(msgReceived);
                            changeUIFromThread.changeStatus(msgReceived1);
                            changeUIFromThread.dataReceived(data);
                        }
                    });
                }

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

            Log.v("Buffer Length sending", " " + buffer.length);
            int CHUNK_SIZE = 200;
            int currentIndex = 0;
            int size = buffer.length;
            while (currentIndex < size) {
                int currentLength = Math.min(size - currentIndex, CHUNK_SIZE);
                connectedOutputStream.write(buffer, currentIndex, currentLength);
                currentIndex += currentLength;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void cancel() {
        if (connectedInputStream != null) {
            try {connectedInputStream.close();} catch (Exception e) {}
            connectedInputStream = null;
        }

        if (connectedOutputStream != null) {
            try {connectedOutputStream.close();} catch (Exception e) {}
            connectedOutputStream = null;
        }

        if (connectedBluetoothSocket != null) {
            try {connectedBluetoothSocket.close();} catch (Exception e) {}
            connectedBluetoothSocket = null;
        }
    }

}