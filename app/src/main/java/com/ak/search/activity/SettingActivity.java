package com.ak.search.activity;

/**
 * change setting of app
 * - change the language
 * - logout user
 * - send and receive the data
 * - see user profile
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.app.SessionManager;
import com.ak.search.bluetooth.BluetoothActivity;
import com.ak.search.bluetooth.BluetoothClientActivity;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.rb_marathi)
    RadioButton rbMarathi;
    @BindView(R.id.rb_english)
    RadioButton rbEnglish;
    @BindView(R.id.rg_lang)
    RadioGroup rgLang;

    @BindView(R.id.tvUsername)
    TextView tvUsername;
    @BindView(R.id.tvUserType)
    TextView tvUserType;
    @BindView(R.id.btn_logout)
    Button btnLogout;
    @BindView(R.id.cv_profile)
    CardView cvProfile;

    @BindView(R.id.ll_delete_old_records)
    LinearLayout llDeleteOldRecords;
    @BindView(R.id.ll_indays)
    LinearLayout llIndays;
    @BindView(R.id.rb_yes)
    RadioButton rbYes;
    @BindView(R.id.rb_no)
    RadioButton rbNo;
    @BindView(R.id.rg_delete_old_data)
    RadioGroup rgDeleteOldData;
    @BindView(R.id.et_indays)
    EditText etInDays;

    Locale myLocale;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);

        getSupportActionBar().setTitle(getString(R.string.setting));


        if (sessionManager.isLoggedIn()) {
            tvUsername.setText(sessionManager.getUsername());

            String usertype = "";
            switch (sessionManager.getLoginType()) {
                case 1:
                    usertype = "Admin";
                    llDeleteOldRecords.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    usertype = "Supervisor";
                    llDeleteOldRecords.setVisibility(View.GONE);
                    break;
                case 3:
                    usertype = "worker";
                    llDeleteOldRecords.setVisibility(View.GONE);
                    break;
            }

            tvUserType.setText(usertype);

        } else {
            cvProfile.setVisibility(View.GONE);
        }


        if (sessionManager.getLanguage().equals("mr")) {
            rbMarathi.setChecked(true);
        } else if (sessionManager.getLanguage().equals("en")) {
            rbEnglish.setChecked(true);
        }

        if (sessionManager.isDeleteOldData()) {
            rbYes.setChecked(true);
            llIndays.setVisibility(View.VISIBLE);
        } else {
            rbNo.setChecked(true);
            llIndays.setVisibility(View.GONE);
        }

        etInDays.setText(String.valueOf(sessionManager.getInDays()));

        rgLang.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_marathi:
                        sessionManager.setLanguage("mr");
                        setLocale("mr");
                        break;

                    case R.id.rb_english:
                        sessionManager.setLanguage("en");
                        setLocale("en");
                        break;
                }
            }
        });

        rgDeleteOldData.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_yes:
                        sessionManager.setDeleteOldData(true);
                        llIndays.setVisibility(View.VISIBLE);
                        break;

                    case R.id.rb_no:
                        sessionManager.setDeleteOldData(false);
                        llIndays.setVisibility(View.GONE);
                        break;
                }
            }
        });

        etInDays.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().equals("")) {
                    sessionManager.setInDays(Integer.parseInt(s.toString()));
                } else {
                    sessionManager.setInDays(30);
                }
            }
        });


    }


    public void onBtnClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_send:
                startActivity(new Intent(this, BluetoothClientActivity.class));
                //setLocale("en");
                break;

            case R.id.btn_receive:
                startActivity(new Intent(this, BluetoothActivity.class));
                //setLocale("mr");
                break;

            case R.id.btn_logout: //logout the user
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.logout))
                        .setMessage(getString(R.string.sure_logout))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                sessionManager.setLogin(false, "", 0, 0);
                                Intent i = new Intent(SettingActivity.this, LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // user doesn't want to logout
                            }
                        })
                        .show();
                break;
        }
    }


    //change the language
    public void setLocale(String lang) {

        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        Intent refresh = new Intent(this, SettingActivity.class);
        refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(refresh);
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
