package com.ak.search.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ak.search.R;
import com.ak.search.activity.ShowSurveyActivity;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.Survey;

import java.util.List;

import io.realm.Realm;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class SurveyHistoryAdapter extends RecyclerView.Adapter<SurveyHistoryAdapter.MyViewHolder> {

    private List<DataCollection> patientsList;
    private Context context;
    Realm realm;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvDateTime;
        public ImageView ivDelete;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            ivDelete = (ImageView) view.findViewById(R.id.ivDelete);
            tvDateTime = (TextView) view.findViewById(R.id.tvDateTime);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Log.v("SurveyID","asf"+surveysList.get(getPosition()).getId());

                    Intent i = new Intent(context, ShowSurveyActivity.class);
                    //i.putExtra("surveyId", patientsList.get(getPosition()).getSurveyid());
                    i.putExtra("collectionid", patientsList.get(getPosition()).getId());
                    context.startActivity(i);
                }
            });


            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete")
                            .setMessage("Would you like to delete?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    final DataCollection dataCollection = realm.where(DataCollection.class).equalTo("id", patientsList.get(getPosition()).getId()).findFirst();
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            dataCollection.deleteFromRealm();
                                        }
                                    });
                                    notifyDataSetChanged();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // user doesn't want to logout
                                }
                            })
                            .show();
                }
            });

        }
    }


    public SurveyHistoryAdapter(Context context, List<DataCollection> patientsList) {
        this.patientsList = patientsList;
        this.context = context;
        realm = Realm.getDefaultInstance();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patient_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DataCollection user = patientsList.get(position);

        Survey survey = realm.where(Survey.class).equalTo("id", user.getSurveyid()).findFirst();

        holder.tvName.setText(survey.getName() + " ");
        holder.tvDateTime.setText(user.getTimestamp() + " ");
    }

    @Override
    public int getItemCount() {
        return patientsList.size();
    }
}