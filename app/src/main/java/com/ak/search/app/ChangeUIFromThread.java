package com.ak.search.app;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.ak.search.model.MTransferModel;
import com.ak.search.realm_model.TransferModel;

/**
 * Created by dg hdghfd on 18-01-2017.
 */

public interface ChangeUIFromThread {
    public void changeUi();
    public void changeStatus(String status);
    public void connectToThread(BluetoothDevice device);
    public void startThread(BluetoothSocket socket);
    public void dataReceived(MTransferModel transferModel);

    public void disconnectThread();
}
