package com.ak.search.adapter;

/**
 * show list of surveys
 * */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ak.search.R;
import com.ak.search.activity.SelectSurveyActivity;
import com.ak.search.activity.StartSurveyActivity;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.Survey;

import org.parceler.Parcels;

import java.util.List;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class GetSurveyAdapter extends RecyclerView.Adapter<GetSurveyAdapter.MyViewHolder> {

    private List<Survey> surveysList;
    private Context context;

    private Activity act;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSurveyName, tvSurveyQuestions;

        public MyViewHolder(final View view) {
            super(view);
            tvSurveyName = (TextView) view.findViewById(R.id.tv_survey_name);
            tvSurveyQuestions = (TextView) view.findViewById(R.id.tv_survey_questions);
        }
    }


    public GetSurveyAdapter(Context context, List<Survey> surveysList, Activity act) {
        this.surveysList = surveysList;
        this.context = context;
        this.act = act;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.survey_list_row_new, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Survey survey = surveysList.get(position);
        holder.tvSurveyName.setText(survey.getName());


        holder.tvSurveyQuestions.setText(survey.getQuestions().size() + " Questions");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(act, StartSurveyActivity.class);
                intent.putExtra("update", false);
                Bundle bundle = new Bundle();
                bundle.putParcelable("survey", Parcels.wrap(Survey.class, survey));
                intent.putExtras(bundle);
                act.startActivity(intent);
            }
        });
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