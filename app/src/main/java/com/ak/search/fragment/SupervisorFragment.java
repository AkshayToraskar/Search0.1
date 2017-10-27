package com.ak.search.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ak.search.R;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.Survey;
import com.ak.search.realm_model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 * show supervisor panel
 */
public class SupervisorFragment extends Fragment {


    @BindView(R.id.tv_patient_count)
    TextView tvPatientCount;
    @BindView(R.id.tv_collected_data_count)
    TextView tvCollectedDataCount;
    Realm realm;
    private View view;

    public SupervisorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_superviser, container, false);
        ButterKnife.bind(this, view);
        realm = Realm.getDefaultInstance();
        setCount();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        setCount();
    }

    public void setCount() {

        long patientsCount = realm.where(Patients.class).count();
        long dataCollectionCount = realm.where(DataCollection.class).count();


        tvPatientCount.setText(String.valueOf(patientsCount));
        tvCollectedDataCount.setText(String.valueOf(dataCollectionCount));
    }
}
