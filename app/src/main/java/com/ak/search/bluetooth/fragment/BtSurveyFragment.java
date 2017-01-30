package com.ak.search.bluetooth.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ak.search.R;
import com.ak.search.adapter.PatientAdapter;
import com.ak.search.adapter.SurveyAdapter;
import com.ak.search.app.CollectDataInfo;
import com.ak.search.bluetooth.adapter.BtSurveyAdapter;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.Survey;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class BtSurveyFragment extends Fragment {

    private List<Survey> surveyList;
    @BindView(R.id.rv_survey)
    RecyclerView recyclerView;
    private BtSurveyAdapter mAdapter;
    Realm realm;
    View view;
    CollectDataInfo collectDataInfo;
    @BindView(R.id.cb_select_all)
    CheckBox cbSelectAll;

    public BtSurveyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_bt_survey, container, false);

        ButterKnife.bind(this,view);

        realm=Realm.getDefaultInstance();

        //  usersList = MUser.listAll(MUser.class);

        RealmResults<Survey> results = realm.where(Survey.class).findAll();

        surveyList=new ArrayList<>();
        surveyList.addAll(results);

        mAdapter = new BtSurveyAdapter(collectDataInfo,getContext(), surveyList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        cbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mAdapter.selectAll(b);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            collectDataInfo = (CollectDataInfo) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

}
