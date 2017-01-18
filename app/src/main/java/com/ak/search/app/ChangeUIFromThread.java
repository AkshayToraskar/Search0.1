package com.ak.search.app;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * Created by dg hdghfd on 18-01-2017.
 */

public interface ChangeUIFromThread {
    public void changeUi();
    public void changeStatus(String status);
    public void connectToThread(BluetoothDevice device);
    public void startThread(BluetoothSocket socket);
    public void disconnectThread();
}
