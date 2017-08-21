package com.ak.search.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ak.search.R;
import com.ak.search.activity.AddUserActivity;
import com.ak.search.app.AddNestedInfo;
import com.ak.search.realm_model.ConditionalOptions;
import com.ak.search.realm_model.Survey;
import com.ak.search.realm_model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.MyViewHolder> {

    private List<ConditionalOptions> optionList;
    private Context context;
    private boolean isConditional;
    AddNestedInfo nestedInfo;
    Realm realm;

    long survId = 0;

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
                    //abandon current focus
                    View currentFocus = ((Activity) context).getCurrentFocus();
                    if (currentFocus != null) {
                        currentFocus.clearFocus();
                    }
                    notifyItemRemoved(getPosition());
                }
            });

            etOption.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(final Editable s) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            optionList.get(getPosition()).setOpt(String.valueOf(s));
                        }
                    });
                }
            });


            btnAddSurvey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    survId = 0;
                    if (optionList.get(getPosition()).getSurveyid() != 0) {
                        //  nesSurveyId.set(pos, conditionalOptions.get(pos).getSurveyid());
                        survId = optionList.get(getPosition()).getSurveyid();
                    } else {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

                                try {
                                    survId = realm.where(Survey.class).max("id").intValue() + 1;
                                } catch (Exception ex) {
                                    Log.v("exception", ex.toString());
                                    survId = 1;
                                }

                                // Add a survey
                                Survey survey = realm.createObject(Survey.class, survId);
                                survey.setNested(true);
                                survey.setName("Survey " + survId);
                                realm.copyToRealmOrUpdate(survey);

                                optionList.get(getPosition()).setSurveyid(survId);
                            }
                        });

                        //   nesSurveyId.set(pos, survId);

                    }


                    nestedInfo.addNestedData(survId);

                    notifyItemChanged(getPosition());

                }
            });


        }
    }


    public OptionsAdapter(Context context, List<ConditionalOptions> optionList, boolean isConditional, AddNestedInfo nesInfo) {
        this.optionList = optionList;
        this.context = context;
        this.isConditional = isConditional;
        this.nestedInfo = nesInfo;
        realm = Realm.getDefaultInstance();
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
        } else {
            if (optionList.get(position).getSurveyid() != 0) {
                holder.btnAddSurvey.setText("Update");
            } else {
                holder.btnAddSurvey.setText("Link Survey");
            }
        }


    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }
}