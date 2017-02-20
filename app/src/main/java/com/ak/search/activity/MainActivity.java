package com.ak.search.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ak.search.R;
import com.ak.search.app.ManageFragment;
import com.ak.search.app.SessionManager;
import com.ak.search.app.Validate;
import com.ak.search.bluetooth.BluetoothActivity;
import com.ak.search.bluetooth.BluetoothClientActivity;
import com.ak.search.fragment.AdminFragment;
import com.ak.search.fragment.PatientFragment;
import com.ak.search.fragment.SuperviserFragment;
import com.ak.search.realm_model.Patients;

import org.parceler.Parcels;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ManageFragment {

    private String username;
    private int loginType;
    public static String TAG = MainActivity.class.getName();
    private SessionManager sessionManager;

    public static ManageFragment manageFragment;
    /*@BindView(R.id.ll_admin)
    LinearLayout llAdmin;

    @BindView(R.id.ll_user)
    LinearLayout llUser;

    @BindView(R.id.txt_patient_name)
    EditText txt_patient_name;

    private List<MSurvey> surveysList;
    @BindView(R.id.rv_survey)
    RecyclerView recyclerView;
    public GetSurveyAdapter mAdapter;
*/

    public static long surveyId = -1;

    private Validate validate;
    public static FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);

        manageFragment = this;

        sessionManager = new SessionManager(this);
        validate = new Validate();

        if (sessionManager.isLoggedIn()) {
            username = sessionManager.getUsername();
            loginType = sessionManager.getLoginType();

            transaction = getSupportFragmentManager().beginTransaction();


            getSupportActionBar().setTitle("Welcome " + username);


            switch (loginType) {
                case 1:
                    //AdminFragment adminFragment=new AdminFragment();
                    transaction.add(R.id.main_frame, new AdminFragment());
                    transaction.commit();
                    break;

                case 2:
                    // SuperviserFragment superviserFragment=new SuperviserFragment();
                    transaction.add(R.id.main_frame, new SuperviserFragment());
                    transaction.commit();
                    break;

                case 3:
                    //UserFragment userFragment=new UserFragment();
                    //transaction.add(R.id.main_frame, new UserFragment());
                    //transaction.add(R.id.main_frame, new PatientFragment());
                    //transaction.commit();

                   // Intent i=new Intent(this,SelectSurveyActivity.class);

                    //Bundle args = new Bundle();
                    //args.putParcelable("PatientData", Parcels.wrap(MPatients.class,patients));
                    //i.putExtra("PatientData",Parcels.wrap(Patients.class,patients));
                   // startActivity(i);

                    break;
            }

               /* Log.v(TAG, "ADMIN");
               // llUser.setVisibility(View.GONE);
            } else {
                Log.v(TAG, "USER");
                *//*llAdmin.setVisibility(View.GONE);

                surveysList = MSurvey.listAll(MSurvey.class);

                mAdapter = new GetSurveyAdapter(this, surveysList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);*//*

            }*/
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        /*if (loginType == 1) {
            menu.getItem(0).setVisible(false);
        }*/


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:

                new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Would you like to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                sessionManager.setLogin(false, "", 0,0);
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
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

            /*case R.id.action_patient_data:
                startActivity(new Intent(this, GetSurveyActivity.class));
                break;*/


        }
        return super.onOptionsItemSelected(item);
    }


    public void onBtnClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_user:
                startActivity(new Intent(this, UserActivity.class));
                break;

            case R.id.btn_survey:
                startActivity(new Intent(this, SurveyActivity.class));
                break;

            case R.id.btn_data:
                startActivity(new Intent(this, GetSurveyActivity.class));
                break;

            case R.id.btn_bluetooth:
                startActivity(new Intent(this, BluetoothActivity.class));
                break;

            case R.id.btn_bluetooth_client:
                startActivity(new Intent(this, BluetoothClientActivity.class));
                break;


        }

    }


    @Override
    public void changeFragment(Patients patients) {

        //UserFragment userFragment=new UserFragment();

        Intent i=new Intent(this,SelectSurveyActivity.class);

        //Bundle args = new Bundle();
        //args.putParcelable("PatientData", Parcels.wrap(MPatients.class,patients));
        //i.putExtra("PatientData",Parcels.wrap(Patients.class,patients));
        startActivity(i);

        //userFragment.setArguments(args);

        //FragmentTransaction tras = getSupportFragmentManager().beginTransaction();
        //tras.replace(R.id.main_frame,userFragment);
        //tras.addToBackStack(null);
        //tras.commit();
    }

    @Override
    public void onBackPressed()
    {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }
}
