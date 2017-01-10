package com.ak.search.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ak.search.R;
import com.ak.search.activity.MainActivity;
import com.ak.search.realm_model.Patients;

import io.realm.RealmList;

/**
 * Created by dg hdghfd on 03-01-2017.
 */

public class SearchPatientAdapter extends RecyclerView.Adapter<SearchPatientAdapter.MyViewHolder> {

    private RealmList<Patients> questionsList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName,tvAddress;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_patient_name);
            tvAddress=(TextView)view.findViewById(R.id.tv_patient_address);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Intent i = new Intent(context, AddQuestionActivity.class);
                    //i.putExtra("questionId", questionsList.get(getPosition()).getId());
                    //context.startActivity(i);
                    //MainActivity.transaction.replace(R.id.main_frame, new UserFragment());
                    //MainActivity.transaction.commit();

                    MainActivity.manageFragment.changeFragment(questionsList.get(getPosition()));

                    /*FragmentTransaction fragmentTransaction = getA.beginTransaction();
                    fragmentTransaction.replace(mContainerId, fragment, tag);
                    fragmentTransaction.addToBackStack(tag);
                    fragmentTransaction.commitAllowingStateLoss();*/



                }
            });
        }
    }


    public SearchPatientAdapter(Context context, RealmList<Patients> questionsList) {
        this.questionsList = questionsList;
        this.context = context;
    }

    @Override
    public SearchPatientAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patient_list_item, parent, false);

        return new SearchPatientAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchPatientAdapter.MyViewHolder holder, int position) {
        Patients patients = questionsList.get(position);
        holder.tvName.setText(patients.getPatientname());
        holder.tvAddress.setText(patients.getAddress());
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }
}