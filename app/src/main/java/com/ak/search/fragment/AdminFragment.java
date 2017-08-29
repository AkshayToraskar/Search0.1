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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 *
 * represent admin data
 *
 */
public class AdminFragment extends Fragment {

    @BindView(R.id.tv_user_count)
    TextView tvUserCount;
    @BindView(R.id.tv_survey_count)
    TextView tvSurveyCount;
    @BindView(R.id.tv_patient_count)
    TextView tvPatientCount;
    @BindView(R.id.tv_collected_data_count)
    TextView tvCollectedDataCount;
    Realm realm;
    private View view;

    public AdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_admin, container, false);
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
        long userCount = realm.where(User.class).count();
        long surveysCount = realm.where(Survey.class).count();
        long patientsCount = realm.where(Patients.class).count();
        long dataCollectionCount = realm.where(DataCollection.class).count();


        tvUserCount.setText(String.valueOf(userCount));
        tvSurveyCount.setText(String.valueOf(surveysCount));
        tvPatientCount.setText(String.valueOf(patientsCount));
        tvCollectedDataCount.setText(String.valueOf(dataCollectionCount));
    }

}
