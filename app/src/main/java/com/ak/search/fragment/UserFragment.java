package com.ak.search.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ak.search.R;
import com.ak.search.adapter.GetSurveyAdapter;
import com.ak.search.model.Patients;
import com.ak.search.model.Survey;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {


    private List<Survey> surveysList;
    @BindView(R.id.rv_survey)
    RecyclerView recyclerView;
    /*@BindView(R.id.tv_patient_name)
    TextView tvPatientName;
    @BindView(R.id.tv_patient_address)
    TextView tvPatientAddress;*/


    public GetSurveyAdapter mAdapter;
    Realm realm;
    //public static Patients patients;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, view);
        realm = Realm.getDefaultInstance();

        surveysList = realm.where(Survey.class).findAll();

        /*if (getArguments() != null) {
            patients = Parcels.unwrap(getArguments().getParcelable("PatientData"));

            tvPatientName.setText(patients.getPatientname());
            tvPatientAddress.setText(patients.getAddress());

        }*/
        //  surveysList = Survey.listAll(Survey.class);

        mAdapter = new GetSurveyAdapter(getContext(), surveysList, getActivity());
        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

}
