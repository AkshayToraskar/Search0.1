package com.ak.search.bluetooth.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ak.search.R;
import com.ak.search.activity.ShowSurveyActivity;
import com.ak.search.app.CollectDataInfo;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.TransferModel;
import com.ak.search.realm_model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class BtSurveyHistoryAdapter extends RecyclerView.Adapter<BtSurveyHistoryAdapter.MyViewHolder> {

    private List<DataCollection> patientsList;
    private Context context;
    CollectDataInfo collectDataInfo;
    TransferModel transferModel;
boolean isSelectedAll;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox cbName;
        //public ImageView ivDelete;

        public MyViewHolder(View view) {
            super(view);
            cbName = (CheckBox) view.findViewById(R.id.cbName);

            cbName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    List<DataCollection> dataCollectionList = new ArrayList<DataCollection>();
                    dataCollectionList.add(patientsList.get(getPosition()));
                    transferModel.setDataCollectionList(dataCollectionList);
                    transferModel.setName(String.valueOf(b));
                    collectDataInfo.collectData(transferModel);
                }
            });

        }
    }


    public BtSurveyHistoryAdapter(CollectDataInfo collectDataInfo,Context context, List<DataCollection> patientsList) {
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
        DataCollection user = patientsList.get(position);
        if(user.getPatients()!=null) {
            holder.cbName.setText(user.getPatients().getPatientname() + " ");
        }else
        {holder.cbName.setText("asdf");

        }
        if (!isSelectedAll) {
            holder.cbName.setChecked(false);
        }
        else{
            holder.cbName.setChecked(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return patientsList.size();
    }

    public void selectAll(boolean val) {
        Log.e("onClickSelectAll", "yes");
        isSelectedAll = val;
        notifyDataSetChanged();
    }
}