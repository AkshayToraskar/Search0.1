package com.ak.search.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ak.search.R;
import com.ak.search.adapter.PatientAdapter;
import com.ak.search.realm_model.Patients;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class PatientsActivity extends AppCompatActivity {

    private List<Patients> patientList;
    @BindView(R.id.rv_patient)
    RecyclerView recyclerView;
    public PatientAdapter mAdapter;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        realm=Realm.getDefaultInstance();


        RealmResults<Patients> results = realm.where(Patients.class).findAll();
        //surveysList = MSurvey.listAll(MSurvey.class);
        patientList=new ArrayList<>();
        patientList.addAll(results);

        mAdapter = new PatientAdapter(this, patientList,realm);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        patientList.clear();
        //surveysList.addAll(MSurvey.listAll(MSurvey.class));
        patientList.addAll(realm.where(Patients.class).findAll());
        mAdapter.notifyDataSetChanged();
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
        if(realm != null) {
            realm.close();
        }
    }

    public void onBtnClick(View view){
        int id=view.getId();

        switch (id){
            case R.id.btn_import_csv:

                break;
        }

    }


}
