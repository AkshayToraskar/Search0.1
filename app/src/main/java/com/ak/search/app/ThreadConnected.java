package com.ak.search.app;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by dg hdghfd on 10-01-2017.
 */

public class ThreadConnected  extends Thread {
    private final BluetoothSocket connectedBluetoothSocket;
    private final InputStream connectedInputStream;
    private final OutputStream connectedOutputStream;

    public ThreadConnected(BluetoothSocket socket) {
        connectedBluetoothSocket = socket;
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
        byte[] buffer = new byte[4096];
        int bytes;

        while (true) {
            try {
                bytes = connectedInputStream.read(buffer);
                //String strReceived = new String(buffer, 0, bytes);

                /*TestModel user=(TestModel) ParcebleUtil.deserialize(buffer);

                final String msgReceived = String.valueOf(bytes) +
                        " bytes received:\n"
                        + user.getName();*/

                /*runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        textStatus.setText(msgReceived);
                    }});*/

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                final String msgConnectionLost = "Connection lost:\n"
                        + e.getMessage();
               /* runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        textStatus.setText(msgConnectionLost);
                    }});*/
            } /*catch (ClassNotFoundException e) {
                e.printStackTrace();
            }*/
        }
    }

    public void write(byte[] buffer) {
        try {
            connectedOutputStream.write(buffer);
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