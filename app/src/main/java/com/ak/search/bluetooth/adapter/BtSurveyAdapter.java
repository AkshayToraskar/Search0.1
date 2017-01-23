package com.ak.search.bluetooth.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ak.search.R;
import com.ak.search.activity.AddSurveyActivity;
import com.ak.search.app.CollectDataInfo;
import com.ak.search.realm_model.Survey;
import com.ak.search.realm_model.TransferModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class BtSurveyAdapter extends RecyclerView.Adapter<BtSurveyAdapter.MyViewHolder> {

    private List<Survey> surveysList;
    private Context context;
    CollectDataInfo collectDataInfo;
    TransferModel transferModel;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox cbName;

        public MyViewHolder(View view) {
            super(view);
            cbName = (CheckBox) view.findViewById(R.id.cbName);


            cbName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    List<Survey> surveys=new ArrayList<Survey>();
                    surveys.add(surveysList.get(getPosition()));
                    transferModel.setName(String.valueOf(b));
                    transferModel.setSurveyList(surveys);

                    collectDataInfo.collectData(transferModel);
                }
            });


        }
    }


    public BtSurveyAdapter(CollectDataInfo collectDataInfo, Context context, List<Survey> surveysList) {
        this.surveysList = surveysList;
        this.context = context;
        this.collectDataInfo = collectDataInfo;
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
        Survey survey = surveysList.get(position);
        holder.cbName.setText(survey.getName());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return surveysList.size();
    }
}