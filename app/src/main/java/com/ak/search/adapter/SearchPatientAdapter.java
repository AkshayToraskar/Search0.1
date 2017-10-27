package com.ak.search.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ak.search.R;
import com.ak.search.activity.MainActivity;
import com.ak.search.realm_model.Patients;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by dg hdghfd on 03-01-2017.
 * <p>
 * show patient list
 */

public class SearchPatientAdapter extends RecyclerView.Adapter<SearchPatientAdapter.MyViewHolder> {

    private RealmList<Patients> patientList;
    private Context context;
    Realm realm;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvAddress;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_patient_name);
            tvAddress = (TextView) view.findViewById(R.id.tv_patient_address);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Date date = new Date();
                            dateFormat.format(date);

                            //patientList.get(getPosition()).setSaveDate(date);
                            realm.copyToRealmOrUpdate(patientList.get(getPosition()));
                        }
                    });


                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("data", patientList.get(getPosition()).getId());
                    ((Activity) context).setResult(Activity.RESULT_OK, returnIntent);
                    ((Activity) context).finish();


                }
            });
        }
    }


    public SearchPatientAdapter(Context context, RealmList<Patients> questionsList) {
        this.patientList = questionsList;
        this.context = context;
        realm = Realm.getDefaultInstance();
    }

    @Override
    public SearchPatientAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patient_list_item, parent, false);

        return new SearchPatientAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchPatientAdapter.MyViewHolder holder, int position) {
        Patients patients = patientList.get(position);
        holder.tvName.setText(patients.getPatientname());
        holder.tvAddress.setText("MMUID: " + patients.getId());
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public long getDateDifference(Date startDate){

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date currentDate = new Date();
        dateFormat.format(currentDate);
        //String startDate = date.toString();
        //String endDate = currentDate.toString();
        long duration  = currentDate.getTime() - startDate.getTime();
       // long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
       // long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        //long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
        long diffInDays=TimeUnit.MILLISECONDS.toDays(duration);
        //Log.v();

        return diffInDays;
    }
}