package com.ak.search.bluetooth.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ak.search.R;
import com.ak.search.app.CollectDataInfo;
import com.ak.search.model.MTransferModel;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.TransferModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class BtPatientAdapter extends RecyclerView.Adapter<BtPatientAdapter.MyViewHolder> {

    private List<Patients> patientsList;
    private Context context;
    CollectDataInfo collectDataInfo;
    TransferModel transferModel;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox cbName;
       // public ImageView ivDelete;

        public MyViewHolder(View view) {
            super(view);
            cbName = (CheckBox) view.findViewById(R.id.cbName);
            cbName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    List<Patients> patientses=new ArrayList<Patients>();
                    patientses.add(patientsList.get(getPosition()));
                    transferModel.setName(String.valueOf(b));
                    transferModel.setPatientsList(patientses);

                    collectDataInfo.collectData(transferModel);
                }
            });

        }
    }


    public BtPatientAdapter(CollectDataInfo collectDataInfo,Context context, List<Patients> patientsList) {
        this.patientsList = patientsList;
        this.context = context;
        this.collectDataInfo=collectDataInfo;

        transferModel=new TransferModel();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bt_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Patients user = patientsList.get(position);
        holder.cbName.setText(user.getPatientname());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return patientsList.size();
    }
}