package com.ak.search.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ak.search.R;
import com.ak.search.app.CollectDataInfo;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.TransferModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class DataCollectionAdapter extends RecyclerView.Adapter<DataCollectionAdapter.MyViewHolder> {

    private List<DataCollection> patientsList;
    private Context context;
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
                }
            });

        }
    }


    public DataCollectionAdapter(Context context, List<DataCollection> patientsList) {
        this.patientsList = patientsList;
        this.context = context;
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
        holder.cbName.setText(user.getPatients().getAddress()+" ");

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