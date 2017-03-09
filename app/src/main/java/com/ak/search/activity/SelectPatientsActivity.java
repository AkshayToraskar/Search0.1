package com.ak.search.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

public class SelectPatientsActivity extends AppCompatActivity {

    @BindView(R.id.rv_patient)
    RecyclerView rvPatientList;
    @BindView(R.id.et_patient_name)
    EditText etPatientName;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
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

        patientsList=new RealmList<>();

        RealmResults<Patients> results
                = realm.where(Patients.class).findAll();
        patientsList.addAll(results);


        searchPatientAdapter = new SearchPatientAdapter(this, patientsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
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




    /*public void loadData() {

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
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(realm != null) {
            realm.close();
        }
    }
}