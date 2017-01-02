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
import com.ak.search.model.Patients;

import java.util.List;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.MyViewHolder> {

    private List<Patients> patientsList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public ImageView ivDelete;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            ivDelete = (ImageView) view.findViewById(R.id.ivDelete);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Log.v("SurveyID","asf"+surveysList.get(getPosition()).getId());

                    Intent i = new Intent(context, ShowSurveyActivity.class);
                    i.putExtra("surveyId", patientsList.get(getPosition()).getSurveyid());
                  //  i.putExtra("patientId", patientsList.get(getPosition()).getId());
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
                          //          Patients patients = Patients.findById(Patients.class, patientsList.get(getPosition()).getId());
                        //            patients.delete();

                                    patientsList.remove(getPosition());

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


    public PatientAdapter(Context context, List<Patients> patientsList) {
        this.patientsList = patientsList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patient_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Patients user = patientsList.get(position);
        holder.tvName.setText(user.getPatientname());
    }

    @Override
    public int getItemCount() {
        return patientsList.size();
    }
}