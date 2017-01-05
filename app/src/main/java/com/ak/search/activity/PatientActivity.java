package com.ak.search.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.ak.search.R;
import com.ak.search.adapter.PatientTabViewpagerAdapter;
import com.ak.search.fragment.AdminFragment;
import com.ak.search.fragment.SurveyHistoryFragment;
import com.ak.search.fragment.UserFragment;
import com.ak.search.model.Patients;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PatientActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tv_patient_name)
    TextView tvPatientName;
    @BindView(R.id.tv_patient_address)
    TextView tvPatientAddress;
    public static Patients patients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (getIntent().getExtras() != null) {
            patients = Parcels.unwrap(getIntent().getExtras().getParcelable("PatientData"));

            tvPatientName.setText(patients.getPatientname());
            tvPatientAddress.setText(patients.getAddress());

        }

        PatientTabViewpagerAdapter adapter = new PatientTabViewpagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UserFragment(), "NEW SURVEY");
        adapter.addFragment(new SurveyHistoryFragment(), "SURVEY HISTORY");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
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
}
