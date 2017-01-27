package com.ak.search.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.app.ChangeUIFromThread;

import java.io.IOException;
import java.util.UUID;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;


/**
 * Created by dg hdghfd on 10-01-2017.
 */

public class ThreadBeConnected extends Thread {

    private BluetoothServerSocket bluetoothServerSocket = null;
    private UUID myUUID;
    private String myName;
    BluetoothAdapter bluetoothAdapter;
    Activity act;
    public static String TAG = "Thread";


    ChangeUIFromThread changeUIFromThread;

    public ThreadBeConnected(ChangeUIFromThread changeUIFromThread, BluetoothAdapter bluetoothAdapter, Activity act) {

        myUUID = UUID.fromString("ec79da00-853f-11e4-b4a9-0800200c9a66");
        myName = myUUID.toString();
        this.act = act;
        this.changeUIFromThread = changeUIFromThread;
        this.bluetoothAdapter = bluetoothAdapter;


        try {
            bluetoothServerSocket =
                    bluetoothAdapter.listenUsingRfcommWithServiceRecord(myName, myUUID);


            changeUIFromThread.changeStatus("Waiting\n"
                    + "bluetoothServerSocket :\n"
                    + bluetoothServerSocket);

            Log.v(TAG, "Waiting\n"
                    + "bluetoothServerSocket :\n"
                    + bluetoothServerSocket);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        BluetoothSocket bluetoothSocket = null;

        if (bluetoothServerSocket != null) {
            try {
                bluetoothSocket = bluetoothServerSocket.accept();

                BluetoothDevice remoteDevice = bluetoothSocket.getRemoteDevice();

                final String strConnected = "Connected:\n" +
                        remoteDevice.getName() + "\n" +
                        remoteDevice.getAddress();

                //connected
                act.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Log.v(TAG, strConnected);


                        changeUIFromThread.changeStatus(strConnected);
                        changeUIFromThread.changeUi();
                    }
                });


                changeUIFromThread.startThread(bluetoothSocket);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                final String eMessage = e.getMessage();
                act.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Log.v(TAG, eMessage);
                        //   textStatus.setText("something wrong: \n" + eMessage);
                        changeUIFromThread.changeStatus("something wrong: \n" + eMessage);
                    }
                });

            }
        } else {
            act.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Log.v(TAG, "bluetoothServerSocket == null");
                    //   textStatus.setText("bluetoothServerSocket == null");
                    changeUIFromThread.changeStatus("bluetoothServerSocket == null");
                    changeUIFromThread.disconnectThread();
                }
            });

        }
    }

    public void cancel() {



        if (bluetoothServerSocket != null) {
            try {bluetoothServerSocket.close();} catch (Exception e) {}
            bluetoothServerSocket = null;
        }
    }

}