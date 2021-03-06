package com.ak.search.bluetooth.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ak.search.R;
import com.ak.search.app.CollectDataInfo;
import com.ak.search.realm_model.TransferModel;
import com.ak.search.realm_model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dg hdghfd on 29-11-2016.
 *
 * display all users
 *
 */

public class BtUsersAdapter extends RecyclerView.Adapter<BtUsersAdapter.MyViewHolder> {

    private List<User> userList;
    private Context context;
    CollectDataInfo collectDataInfo;
    TransferModel transferModel;
    boolean isSelectedAll;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox cbName;
        public TextView tvInfo;

        public MyViewHolder(View view) {
            super(view);
            cbName = (CheckBox) view.findViewById(R.id.cbName);
            tvInfo = (TextView) view.findViewById(R.id.tvInfo);

            cbName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    List<User> user = new ArrayList<User>();
                    user.add(userList.get(getPosition()));
                    transferModel.setName(String.valueOf(b));
                    transferModel.setUserList(user);
                    collectDataInfo.collectData(transferModel);
                }
            });


        }
    }


    public BtUsersAdapter(CollectDataInfo collectDataInfo, Context context, List<User> userList) {
        this.userList = userList;
        this.context = context;
        this.collectDataInfo = collectDataInfo;
        transferModel = new TransferModel();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bt_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user = userList.get(position);
        holder.cbName.setText(user.getName());
        switch (user.getType()) {
            case 1:
                holder.tvInfo.setText("Admin");
                break;
            case 2:
                holder.tvInfo.setText("Supervisor");
                break;
            case 3:
                holder.tvInfo.setText("Fieldworker");
                break;
        }

        if (!isSelectedAll) {
            holder.cbName.setChecked(false);
        } else {
            holder.cbName.setChecked(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void selectAll(boolean val) {
        Log.e("onClickSelectAll", "yes");
        isSelectedAll = val;
        notifyDataSetChanged();
    }
}