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
import com.ak.search.app.ChangeUIFromThread;

import java.io.IOException;

/**
 * Created by dg hdghfd on 10-01-2017.
 */

public class ThreadConnectBTdevice  extends Thread {
    private BluetoothSocket bluetoothSocket = null;
    private final BluetoothDevice bluetoothDevice;

    Activity act;
    ChangeUIFromThread changeUIFromThread;


    public ThreadConnectBTdevice(ChangeUIFromThread changeUIFromThread,BluetoothDevice device, Activity act) {
        bluetoothDevice = device;
        this.changeUIFromThread=changeUIFromThread;
        this.act=act;

        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(BluetoothClientActivity.myUUID);

            changeUIFromThread.changeStatus("bluetoothSocket: \n" + bluetoothSocket);
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

                    changeUIFromThread.changeStatus("something wrong bluetoothSocket.connect(): \n" + eMessage);
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

                    changeUIFromThread.changeStatus(msgconnected);
                    changeUIFromThread.changeUi();
                }});


            changeUIFromThread.startThread(bluetoothSocket);
        }else{
            //fail
        }
    }

    public void cancel() {


        if (bluetoothSocket != null) {
            try {bluetoothSocket.close();} catch (Exception e) {}
            bluetoothSocket = null;
        }

    }
}