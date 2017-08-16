package com.ak.search.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.search.activity.AddSurveyActivity;
import com.ak.search.R;
import com.ak.search.app.CsvOperation;
import com.ak.search.realm_model.Survey;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

import static android.view.View.GONE;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.MyViewHolder> {

    private List<Survey> surveysList;
    private Context context;
    Realm realm;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvSubsurvey;
        ImageView ivExport;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvSubsurvey = (TextView) view.findViewById(R.id.tv_subsurvey);
            ivExport = (ImageView) view.findViewById(R.id.ivExport);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //   Log.v("SurveyID","asf"+surveysList.get(getPosition()).getId());

                    Intent i = new Intent(context, AddSurveyActivity.class);
                    i.putExtra("surveyId", surveysList.get(getPosition()).getId());
                    i.putExtra("surveyName", surveysList.get(getPosition()).getName());
                    i.putExtra("isNestead",surveysList.get(getPosition()).getNested());
                    context.startActivity(i);
                }
            });

            ivExport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    generateCSV(surveysList.get(getPosition()).getId());
                }
            });


        }
    }


    public SurveyAdapter(Context context, List<Survey> surveysList) {
        this.surveysList = surveysList;
        this.context = context;
        realm = Realm.getDefaultInstance();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.survey_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Survey survey = surveysList.get(position);
        holder.tvName.setText(survey.getName());

        if (!survey.getNested()) {
            holder.tvSubsurvey.setVisibility(GONE);
            holder.ivExport.setVisibility(View.VISIBLE);
        } else {
            holder.tvSubsurvey.setVisibility(View.VISIBLE);
            holder.ivExport.setVisibility(GONE);
        }
    }

    @Override
    public int getItemCount() {
        return surveysList.size();
    }


    public void generateCSV(Long survId) {

        try {

            File myDirectory = new File(Environment.getExternalStorageDirectory(), "SEARCH");
            if (!myDirectory.exists()) {
                myDirectory.mkdirs();
            }
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

            List<String[]> data = new ArrayList<String[]>();

            CsvOperation csvOperation = new CsvOperation(survId);
            List<String[]> strData = csvOperation.generateQuestionString();
            Survey survey = realm.where(Survey.class).equalTo("id", survId).findFirst();

            String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "SEARCH" + File.separator + survey.getName() + "_" + "Que_" + currentDateTimeString + ".csv";
            CSVWriter writer = null;
            writer = new CSVWriter(new FileWriter(csv));

            for (int k = 0; k < strData.size(); k++) {
                data.add(strData.get(k));
            }


            writer.writeAll(data);
            writer.close();
            Log.v("Export Data", "SUCCESS");

            Toast.makeText(context, "Data Exported Successfully into " + survey.getName() + "_" + "Que_" + currentDateTimeString + ".csv file", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Log.v("Export Data", "FAIL");
        }
    }

}