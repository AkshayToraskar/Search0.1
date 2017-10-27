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
import android.widget.TextView;

import com.ak.search.R;
import com.ak.search.activity.StartSurveyActivity;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.Survey;
import com.ak.search.realm_model.User;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class FieldworkerStatusAdapter extends RecyclerView.Adapter<FieldworkerStatusAdapter.MyViewHolder> {

    private List<User> usersList;
    private Context context;
Realm realm;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvName,tvUserType,tvDataCount;


        public MyViewHolder(final View view) {
            super(view);
            tvName=(TextView)view.findViewById(R.id.tvName);
            tvUserType=(TextView)view.findViewById(R.id.tv_user_type);
            tvDataCount=(TextView)view.findViewById(R.id.tv_data_count);


        }
    }


    public FieldworkerStatusAdapter(Context context, List<User> usersList) {
        this.usersList = usersList;
        this.context = context;
        realm=Realm.getDefaultInstance();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_status_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final User user = usersList.get(position);
        holder.tvName.setText(user.getName());
        String usertype = "";
        switch (user.getType()) {
            case 1:
                usertype = "admin";
                break;

            case 2:
                usertype = "supervisor";
                break;

            case 3:
                usertype = "field worker";
                break;
        }
        holder.tvUserType.setText(usertype);

        long totalCount = realm.where(DataCollection.class).equalTo("fieldworkerId", user.getId()).count();
        holder.tvDataCount.setText(String.valueOf(totalCount));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}