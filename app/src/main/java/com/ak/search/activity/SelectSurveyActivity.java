package com.ak.search.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.adapter.PatientTabViewpagerAdapter;
import com.ak.search.app.SessionManager;
import com.ak.search.app.Validate;
import com.ak.search.bluetooth.BluetoothActivity;
import com.ak.search.bluetooth.BluetoothClientActivity;
import com.ak.search.fragment.SuperviserFragment;
import com.ak.search.fragment.SurveyHistoryFragment;
import com.ak.search.fragment.UserFragment;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.User;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class SelectSurveyActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    /*@BindView(R.id.tv_patient_name)
    TextView tvPatientName;
    @BindView(R.id.tv_patient_address)
    TextView tvPatientAddress;*/
    //public static Patients patients;
    private int loginType;
    public static SessionManager sessionManager;

    public static long SuperviserLogin = 0;

    private String username;
    Realm realm;
    //Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);


        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            username = sessionManager.getUsername();
            loginType = sessionManager.getLoginType();

            getSupportActionBar().setTitle("Welcome " + username);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }

        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /*if (getIntent().getExtras() != null) {
            patients = Parcels.unwrap(getIntent().getExtras().getParcelable("PatientData"));

            tvPatientName.setText(patients.getPatientname());
            tvPatientAddress.setText(patients.getAddress());

        }*/

        PatientTabViewpagerAdapter adapter = new PatientTabViewpagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UserFragment(), "NEW SURVEY");
        adapter.addFragment(new SurveyHistoryFragment(), "SURVEY HISTORY");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_fieldworker, menu);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case R.id.action_send:
                startActivity(new Intent(this, BluetoothClientActivity.class));
                break;

            case R.id.action_receive:
                startActivity(new Intent(this, BluetoothActivity.class));
                break;

            case R.id.action_logout:

                new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Would you like to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                sessionManager.setLogin(false, "", 0, 0);
                                Intent i = new Intent(SelectSurveyActivity.this, LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // user doesn't want to logout
                            }
                        })
                        .show();
                break;

           /* case R.id.action_patient_data:
               // startActivity(new Intent(this, GetSurveyActivity.class));
                break;*/

            case android.R.id.home:
                finish();
                break;


        }
        return super.onOptionsItemSelected(item);
    }


    /*public void onBtnClick(View view) {
        int id = view.getId();

        switch (id) {

            case R.id.btn_send:

                break;

            case R.id.btn_login_logout:
                if (SuperviserLogin == 0) {
                    callLoginDialog();

                } else {
                    callLogoutDialog();
                }
                break;

        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(realm != null) {
            realm.close();
        }
    }

}
