package com.ak.search.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ak.search.activity.AddUserActivity;
import com.ak.search.R;
import com.ak.search.realm_model.User;

import java.util.List;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    private List<User> userList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvUserType;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvUserType = (TextView) view.findViewById(R.id.tv_user_type);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Log.v("SurveyID","asf"+surveysList.get(getPosition()).getId());

                    Intent i = new Intent(context, AddUserActivity.class);
                    i.putExtra("userId", userList.get(getPosition()).getId());
                    context.startActivity(i);
                }
            });


        }
    }


    public UsersAdapter(Context context, List<User> userList) {
        this.userList = userList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user = userList.get(position);
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
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}