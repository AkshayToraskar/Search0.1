package com.ak.search.app;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.view.View;
import android.widget.Toast;

import com.ak.search.activity.BluetoothActivity;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by dg hdghfd on 10-01-2017.
 */

public class ThreadBeConnected extends Thread {

    private BluetoothServerSocket bluetoothServerSocket = null;
    private UUID myUUID;
    private String myName;

    Activity act;

    public ThreadBeConnected(Activity act) {

        myUUID = UUID.fromString("ec79da00-853f-11e4-b4a9-0800200c9a66");
        myName = myUUID.toString();
        this.act=act;
       /* try {


           // bluetoothServerSocket =
                //    BluetoothActivity.bluetoothAdapter.listenUsingRfcommWithServiceRecord(myName, myUUID);

            //textStatus.setText("Waiting\n"
             //       + "bluetoothServerSocket :\n"
             //       + bluetoothServerSocket);
        }*//* catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }

    @Override
    public void run() {
        BluetoothSocket bluetoothSocket = null;

        if(bluetoothServerSocket!=null){
            try {
                bluetoothSocket = bluetoothServerSocket.accept();

                BluetoothDevice remoteDevice = bluetoothSocket.getRemoteDevice();

                final String strConnected = "Connected:\n" +
                        remoteDevice.getName() + "\n" +
                        remoteDevice.getAddress();

                //connected
                act.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                      //  textStatus.setText(strConnected);
                     //   inputPane.setVisibility(View.VISIBLE);
                    }});

             //   BluetoothActivity.startThreadConnected(bluetoothSocket);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                final String eMessage = e.getMessage();
                act.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                      //  textStatus.setText("something wrong: \n" + eMessage);
                    }});
            }
        }else{
            act.runOnUiThread(new Runnable(){

                @Override
                public void run() {
                //    textStatus.setText("bluetoothServerSocket == null");
                }});
        }
    }

    public void cancel() {

        Toast.makeText(act,
                "close bluetoothServerSocket",
                Toast.LENGTH_LONG).show();

        try {
            bluetoothServerSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}