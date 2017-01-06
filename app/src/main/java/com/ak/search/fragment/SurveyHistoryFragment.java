package com.ak.search.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ak.search.R;
import com.ak.search.activity.PatientActivity;
import com.ak.search.adapter.PatientAdapter;
import com.ak.search.adapter.SurveyHistoryAdapter;
import com.ak.search.model.DataCollection;
import com.ak.search.model.Patients;
import com.ak.search.model.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class SurveyHistoryFragment extends Fragment {


    private List<DataCollection> surveyHistory;
    @BindView(R.id.rv_questions)
    RecyclerView recyclerView;

    @BindView(R.id.spnSurveyName)
    Spinner spnSurveyName;

    public SurveyHistoryAdapter mAdapter;
    ArrayAdapter<String> spnSurveyNameAdapter;

    View view;
    Realm realm;

    public SurveyHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        view = inflater.inflate(R.layout.fragment_survey_history, container, false);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this, view);

        /*spnSurveyNameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, surveyName);
        spnSurveyNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spnSurveyName.setAdapter(spnSurveyNameAdapter);*/

        surveyHistory=new ArrayList<>();

        surveyHistory.addAll(realm.where(DataCollection.class).findAll());


        mAdapter = new SurveyHistoryAdapter(getContext(), surveyHistory);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        surveyHistory.clear();
        // List<User> results = ;
        surveyHistory.addAll(realm.where(DataCollection.class).findAll());
        mAdapter.notifyDataSetChanged();

    }
}
