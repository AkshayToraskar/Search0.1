package com.ak.search.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ak.search.R;
import com.ak.search.activity.AddUserActivity;
import com.ak.search.realm_model.ConditionalOptions;
import com.ak.search.realm_model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.MyViewHolder> {

    private List<ConditionalOptions> optionList;
    private Context context;
    private boolean isConditional;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btn_remove)
        Button btnRemove;
        @BindView(R.id.btn_add_survey)
        Button btnAddSurvey;
        @BindView(R.id.et_option)
        EditText etOption;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);


            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    optionList.remove(getPosition());
                    notifyItemRemoved(getPosition());
                }
            });


        }
    }


    public OptionsAdapter(Context context, List<ConditionalOptions> optionList, boolean isConditional) {
        this.optionList = optionList;
        this.context = context;
        this.isConditional = isConditional;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.options_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ConditionalOptions options = optionList.get(position);
        holder.etOption.setText(options.getOpt());

        if (!isConditional) {
            holder.btnAddSurvey.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }
}