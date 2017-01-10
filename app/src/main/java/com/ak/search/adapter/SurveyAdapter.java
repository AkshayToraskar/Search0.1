package com.ak.search.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ak.search.activity.AddSurveyActivity;
import com.ak.search.R;
import com.ak.search.realm_model.Survey;

import java.util.List;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.MyViewHolder> {

private List<Survey> surveysList;
private Context context;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView tvName;

    public MyViewHolder(View view) {
        super(view);
        tvName = (TextView) view.findViewById(R.id.tvName);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             //   Log.v("SurveyID","asf"+surveysList.get(getPosition()).getId());

                Intent i=new Intent(context, AddSurveyActivity.class);
                i.putExtra("surveyId",surveysList.get(getPosition()).getId());
                i.putExtra("surveyName",surveysList.get(getPosition()).getName());
                context.startActivity(i);
            }
        });


    }
}


    public SurveyAdapter(Context context, List<Survey> surveysList) {
        this.surveysList = surveysList;
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Survey survey = surveysList.get(position);
        holder.tvName.setText(survey.getName());
    }

    @Override
    public int getItemCount() {
        return surveysList.size();
    }
}