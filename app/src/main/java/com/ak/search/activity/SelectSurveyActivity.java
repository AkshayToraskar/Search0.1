package com.ak.search.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ak.search.R;
import com.ak.search.adapter.PatientTabViewpagerAdapter;
import com.ak.search.app.SessionManager;
import com.ak.search.bluetooth.BluetoothActivity;
import com.ak.search.bluetooth.BluetoothClientActivity;
import com.ak.search.fragment.SurveyHistoryFragment;
import com.ak.search.fragment.UserFragment;

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

    private int loginType;
    public static SessionManager sessionManager;

    public static long SuperviserLogin = 0;

    private String username;
    Realm realm;


    public static String LANG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);


        sessionManager = new SessionManager(this);

        LANG = sessionManager.getLanguage();

        if (sessionManager.isLoggedIn()) {
            username = sessionManager.getUsername();
            loginType = sessionManager.getLoginType();

            getSupportActionBar().setTitle(getResources().getString(R.string.welcome)+" "+username);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }



        PatientTabViewpagerAdapter adapter = new PatientTabViewpagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UserFragment(), getResources().getString(R.string.new_survey));
        adapter.addFragment(new SurveyHistoryFragment(), getResources().getString(R.string.survey_history));
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



            case R.id.action_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;


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

    @Override
    protected void onResume() {
        super.onResume();

        if (!LANG.equals(sessionManager.getLanguage())) {
            Intent refresh = new Intent(this, SelectSurveyActivity.class);
            refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(refresh);
        }

    }

}
