package com.ak.search.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ak.search.R;
import com.ak.search.activity.AddSurveyActivity;
import com.ak.search.activity.LinkNestedActivity;
import com.ak.search.app.AddNestedInfo;
import com.ak.search.realm_model.Survey;

import java.util.List;

import io.realm.Realm;

import static android.view.View.GONE;

/**
 * Created by dg hdghfd on 26-10-2017.
 */

public class LinkNestedSurveyAdapter extends RecyclerView.Adapter<LinkNestedSurveyAdapter.MyViewHolder> {

    private List<Survey> surveysList;
    private Context context;
    Realm realm;
    AddNestedInfo addNestedInfo;
    long surveyId;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvSubsurvey;
        ImageView ivExport, ivCheck;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvSubsurvey = (TextView) view.findViewById(R.id.tv_subsurvey);
            ivExport = (ImageView) view.findViewById(R.id.ivExport);
            ivCheck = (ImageView) view.findViewById(R.id.iv_check);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //   Log.v("SurveyID","asf"+surveysList.get(getPosition()).getId());
                    addNestedInfo.addNestedData(surveysList.get(getPosition()).getId());
                }
            });


        }
    }


    public LinkNestedSurveyAdapter(Activity context, List<Survey> surveysList, AddNestedInfo addNestedInfo, Long surveyId) {
        this.surveysList = surveysList;
        this.context = context;
        this.addNestedInfo = addNestedInfo;
        this.surveyId = surveyId;
        realm = Realm.getDefaultInstance();
    }

    @Override
    public LinkNestedSurveyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.survey_list_row, parent, false);

        return new LinkNestedSurveyAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LinkNestedSurveyAdapter.MyViewHolder holder, int position) {
        Survey survey = surveysList.get(position);
        holder.tvName.setText(survey.getName());
        holder.ivExport.setVisibility(GONE);

        if (surveysList.get(position).getId() == surveyId) {
            holder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            holder.ivCheck.setVisibility(GONE);
        }


    }

    @Override
    public int getItemCount() {
        return surveysList.size();
    }

}
