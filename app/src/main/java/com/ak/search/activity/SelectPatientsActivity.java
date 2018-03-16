package com.ak.search.activity;

/**
 * Search and select patient while collecting the survey.
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.ak.search.R;
import com.ak.search.adapter.SearchPatientAdapter;
import com.ak.search.realm_model.Patients;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class SelectPatientsActivity extends AppCompatActivity {

    @BindView(R.id.rv_patient)
    RecyclerView rvPatientList;
    @BindView(R.id.et_patient_name)
    EditText etPatientName;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.spn_type)
    Spinner spnType;
    Realm realm;
    SearchPatientAdapter searchPatientAdapter;

    RealmList<Patients> patientsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_patients);

        ButterKnife.setDebug(true);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //loadData();

        patientsList = new RealmList<>();

        RealmResults<Patients> results
                = realm.where(Patients.class).findAll();
        patientsList.addAll(results);


        searchPatientAdapter = new SearchPatientAdapter(this, patientsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvPatientList.setLayoutManager(mLayoutManager);
        rvPatientList.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvPatientList.addItemDecoration(itemDecoration);
        rvPatientList.setAdapter(searchPatientAdapter);


        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    etPatientName.setInputType(InputType.TYPE_CLASS_TEXT);
                } else if (position == 0) {
                    etPatientName.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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
                if (editable == null || String.valueOf(editable).equals("")) {
                    RealmResults<Patients> results = realm.where(Patients.class).findAll();
                    patientsList.addAll(results);
                    searchPatientAdapter.notifyDataSetChanged();
                } else {
                    RealmList<Patients> results = new RealmList<Patients>();
                    if (spnType.getSelectedItemPosition() == 1) {
                        RealmResults realmResult = realm.where(Patients.class).beginsWith("patientname", String.valueOf(editable)).findAll();
                        results.addAll(realmResult.subList(0, realmResult.size()));

                    } else {
                        Patients patients = realm.where(Patients.class).equalTo("id", Long.parseLong(editable.toString())).findFirst();
                        if (patients != null) {
                            results.add(patients);
                        }
                    }
                    if (results != null) {
                        patientsList.addAll(results);
                        searchPatientAdapter.notifyDataSetChanged();
                    }
                }


            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }
}
