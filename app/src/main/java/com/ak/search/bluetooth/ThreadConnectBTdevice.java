package com.ak.search.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.search.R;

import java.io.IOException;

/**
 * Created by dg hdghfd on 10-01-2017.
 */

public class ThreadConnectBTdevice  extends Thread {
    private BluetoothSocket bluetoothSocket = null;
    private final BluetoothDevice bluetoothDevice;
    TextView textStatus;
    LinearLayout inputPane;
    RecyclerView listViewPairedDevice;
    Activity act;


    public ThreadConnectBTdevice(BluetoothDevice device, Activity act) {
        bluetoothDevice = device;
        this.act=act;
        textStatus=(TextView)act.findViewById(R.id.tv_status);
        inputPane=(LinearLayout)act.findViewById(R.id.ll_inputpane);
        listViewPairedDevice=(RecyclerView) act.findViewById(R.id.rv_btlist);
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(BluetoothClientActivity.myUUID);
          textStatus.setText("bluetoothSocket: \n" + bluetoothSocket);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
            act.runOnUiThread(new Runnable(){

                @Override
                public void run() {
                  textStatus.setText("something wrong bluetoothSocket.connect(): \n" + eMessage);
                }});

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

            act.runOnUiThread(new Runnable(){

                @Override
                public void run() {
                   textStatus.setText(msgconnected);

                   listViewPairedDevice.setVisibility(View.GONE);
                   inputPane.setVisibility(View.VISIBLE);
                }});

            BluetoothClientActivity.startThreadConnected(bluetoothSocket);
        }else{
            //fail
        }
    }

    public void cancel() {

        Toast.makeText(act,
                "close bluetoothSocket",
                Toast.LENGTH_LONG).show();

        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}