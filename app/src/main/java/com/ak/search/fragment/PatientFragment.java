package com.ak.search.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ak.search.R;
import com.ak.search.adapter.SearchPatientAdapter;
import com.ak.search.realm_model.Patients;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientFragment extends Fragment {

    View view;
    @BindView(R.id.rv_patient)
    RecyclerView rvPatientList;
    @BindView(R.id.et_patient_name)
    EditText etPatientName;
    Realm realm;
    SearchPatientAdapter searchPatientAdapter;

    RealmList<Patients> patientsList;

    public PatientFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_patient, container, false);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this, view);
        realm = Realm.getDefaultInstance();



        loadData();

        patientsList=new RealmList<>();

        RealmResults<Patients> results
                = realm.where(Patients.class).findAll();
        patientsList.addAll(results);


        searchPatientAdapter = new SearchPatientAdapter(getContext(), patientsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvPatientList.setLayoutManager(mLayoutManager);
        rvPatientList.setItemAnimator(new DefaultItemAnimator());
        rvPatientList.setAdapter(searchPatientAdapter);

        etPatientName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                patientsList.clear();
                if(editable==null)
                {
                    RealmResults<Patients> results = realm.where(Patients.class).findAll();
                    patientsList.addAll(results);
                    searchPatientAdapter.notifyDataSetChanged();
                }
                else{
                    RealmResults<Patients> results = realm.where(Patients.class).beginsWith("patientname",String.valueOf(editable)).findAll();
                    patientsList.addAll(results);
                    searchPatientAdapter.notifyDataSetChanged();
                }


            }
        });



      //  realmSearchView.setAdapter(searchPatientAdapter);

        return view;
    }


    public void loadData() {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                for (int i = 0; i < 20; i++) {
                    int patientId;
                    try {
                        patientId = realm.where(Patients.class).max("id").intValue() + 1;
                    } catch (Exception ex) {
                        Log.v("exception", ex.toString());
                        patientId = 1;
                    }

                    Patients patient = realm.createObject(Patients.class, patientId);
                    patient.setPatientname("patient name " + patientId);
                    patient.setAddress("address122 asfasfd asdf" + i);
                    realm.copyToRealmOrUpdate(patient);
                }
            }
        });
    }

}
