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
import com.ak.search.realm_model.Patients;

import java.util.List;

import io.realm.Realm;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.MyViewHolder> {

    private List<Patients> patientsList;
    private Context context;
    private Realm realm;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvVillage, tvHouse, tvFamily, tvIndividual, tvName, tvSex, tvAge, tvMMUId;
        public ImageView ivDelete;

        public MyViewHolder(View view) {
            super(view);
            tvVillage = (TextView) view.findViewById(R.id.tvVillageNo);
            tvHouse = (TextView) view.findViewById(R.id.tvHouseId);
            tvFamily = (TextView) view.findViewById(R.id.tvFamilyId);
            tvIndividual = (TextView) view.findViewById(R.id.tvIndividualId);
            tvName = (TextView) view.findViewById(R.id.tvName);
            ivDelete = (ImageView) view.findViewById(R.id.ivDelete);
            tvSex = (TextView) view.findViewById(R.id.tvSex);
            tvAge = (TextView) view.findViewById(R.id.tvAge);
            tvMMUId=(TextView)view.findViewById(R.id.tv_mmu);
           /* view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Log.v("SurveyID","asf"+surveysList.get(getPosition()).getId());

                    //Intent i = new Intent(context, ShowSurveyActivity.class);
                  //  i.putExtra("surveyId", patientsList.get(getPosition()).getSurveyid());
                  //  i.putExtra("patientId", patientsList.get(getPosition()).getId());
                  //  context.startActivity(i);
                }
            });*/


            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete")
                            .setMessage("Would you like to delete?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            Patients patients = realm.where(Patients.class).equalTo("id", patientsList.get(getPosition()).getId()).findFirst();
                                            patients.deleteFromRealm();
                                            patientsList.remove(getPosition());
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


    public PatientAdapter(Context context, List<Patients> patientsList, Realm realm) {
        this.patientsList = patientsList;
        this.context = context;
        this.realm = realm;
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
        String ids=user.getHouseId();
        if(ids.length()>=4) {
            holder.tvVillage.setText("Village No: " + ids.charAt(0) + ", ");
            holder.tvHouse.setText("House No: " + ids.charAt(1) + ", ");
            holder.tvFamily.setText("Family No: " + ids.charAt(2) + ", ");
            holder.tvIndividual.setText("Id No: " + ids.charAt(3) + " ");
        }
        holder.tvMMUId.setText("MMUID: "+String.valueOf(user.getId()));
        holder.tvName.setText(user.getPatientname() + " ");
        holder.tvAge.setText("Age: " + user.getAge() + " ");
        String sex = "";
        switch (user.getSex()) {
            case 1:
                sex = "Male";
                break;
            case 2:
                sex = "Female";
                break;
        }
        holder.tvSex.setText(sex + " ");

    }

    @Override
    public int getItemCount() {
        return patientsList.size();
    }
}