package com.ak.search.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.activity.AddUserActivity;
import com.ak.search.app.ChangeUIFromThread;
import com.ak.search.bluetooth.BluetoothClientActivity;
import com.ak.search.bluetooth.ThreadConnectBTdevice;

import java.util.List;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class BluetoothPairedAdapter extends RecyclerView.Adapter<BluetoothPairedAdapter.MyViewHolder> {

private List<BluetoothDevice> bluetoothDevicesList;

    ChangeUIFromThread changeUIFromThread;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView tvName,tvAddress;

    public MyViewHolder(View view) {
        super(view);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvAddress=(TextView) view.findViewById(R.id.tv_address);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               BluetoothDevice device =
                        (BluetoothDevice) bluetoothDevicesList.get(getPosition());

                changeUIFromThread.connectToThread(device);

            }
        });


    }
}


    public BluetoothPairedAdapter(ChangeUIFromThread changeUIFromThread, List<BluetoothDevice> bluetoothDevicesList) {
        this.bluetoothDevicesList = bluetoothDevicesList;
        this.changeUIFromThread=changeUIFromThread;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bluetooth_item_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tvName.setText(bluetoothDevicesList.get(position).getName());
        holder.tvAddress.setText(bluetoothDevicesList.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return bluetoothDevicesList.size();
    }
}