package com.ak.search.app;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by dg hdghfd on 10-01-2017.
 */

public class ThreadConnectBTdevice  extends Thread {
    private BluetoothSocket bluetoothSocket = null;
    private final BluetoothDevice bluetoothDevice;


    public ThreadConnectBTdevice(BluetoothDevice device) {
        bluetoothDevice = device;
/*
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
            textStatus.setText("bluetoothSocket: \n" + bluetoothSocket);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }

    @Override
    public void run() {
        boolean success = false;
        try {
            bluetoothSocket.connect();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();

            final String eMessage = e.getMessage();
           /* runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    textStatus.setText("something wrong bluetoothSocket.connect(): \n" + eMessage);
                }});*/

            try {
                bluetoothSocket.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        if(success){
            //connect successful
            final String msgconnected = "connect successful:\n"
                    + "BluetoothSocket: " + bluetoothSocket + "\n"
                    + "BluetoothDevice: " + bluetoothDevice;

          /*  runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    textStatus.setText(msgconnected);

                    listViewPairedDevice.setVisibility(View.GONE);
                    inputPane.setVisibility(View.VISIBLE);
                }});

            startThreadConnected(bluetoothSocket);*/
        }else{
            //fail
        }
    }

    public void cancel() {

        /*Toast.makeText(getApplicationContext(),
                "close bluetoothSocket",
                Toast.LENGTH_LONG).show();

        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/

    }
}