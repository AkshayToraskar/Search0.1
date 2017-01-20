package com.ak.search.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.activity.LoginActivity;
import com.ak.search.adapter.PatientTabViewpagerAdapter;
import com.ak.search.app.ChangeUIFromThread;
import com.ak.search.app.CollectDataInfo;
import com.ak.search.app.ParcebleUtil;
import com.ak.search.app.SessionManager;
import com.ak.search.bluetooth.fragment.BtCollectionFragment;
import com.ak.search.bluetooth.fragment.BtLoginFragment;
import com.ak.search.bluetooth.fragment.BtPatientFragment;
import com.ak.search.bluetooth.fragment.BtSurveyFragment;
import com.ak.search.fragment.AdminFragment;
import com.ak.search.fragment.ImpExpFragment;
import com.ak.search.fragment.PatientFragment;
import com.ak.search.fragment.SuperviserFragment;
import com.ak.search.fragment.UserFragment;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.TransferModel;

import java.io.IOException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class BluetoothActivity extends AppCompatActivity implements ChangeUIFromThread,CollectDataInfo {

    @BindView(R.id.ll_inputpane)
    LinearLayout inputPane;
    //@BindView(R.id.btn_send)
    //Button btnSend;
    //@BindView(R.id.btn_send_survey)
    //Button btnSendSurvey;
    @BindView(R.id.tv_info)
    TextView textInfo;
    @BindView(R.id.tv_status)
    TextView textStatus;
    @BindView(R.id.pulsator)
    PulsatorLayout pulsator;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewpager)
    ViewPager viewPager;


    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
    private UUID myUUID;
    private String myName;

    ChangeUIFromThread changeUIFromThread;

    ThreadBeConnected myThreadBeConnected;
    ThreadConnected myThreadConnected;
    Realm realm;
    SessionManager sessionManager;
    TransferModel transferModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        realm = Realm.getDefaultInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessionManager = new SessionManager(this);
        changeUIFromThread = this;

        setupViewPager(viewPager);

        tabs.setupWithViewPager(viewPager);

        if (sessionManager.isLoggedIn()) {
            // username = sessionManager.getUsername();
            int loginType = sessionManager.getLoginType();


            switch (loginType) {
                case 1://admin

                    break;

                case 2://superviser

                    break;

                case 3://field worker

                    break;
            }
        } else {

        }


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(this,
                    "FEATURE_BLUETOOTH NOT support",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //generate UUID on web: http://www.famkruithof.net/uuid/uuidgen
        //have to match the UUID on the another device of the BT connection
        myUUID = UUID.fromString("ec79da00-853f-11e4-b4a9-0800200c9a66");
        myName = myUUID.toString();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this,
                    "Bluetooth is not supported on this hardware platform",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String stInfo = bluetoothAdapter.getName() + "\n" +
                bluetoothAdapter.getAddress();
        textInfo.setText(stInfo);
        pulsator.start();


    }

    private void setupViewPager(ViewPager viewPager) {
        PatientTabViewpagerAdapter adapter = new PatientTabViewpagerAdapter(getSupportFragmentManager());
        //adapter.addFragment(new ImpExpFragment(), "Imp / Exp");
        adapter.addFragment(new BtLoginFragment(), "Login Info");
        adapter.addFragment(new BtPatientFragment(), "Patients");
        adapter.addFragment(new BtSurveyFragment(), "Survey");
        adapter.addFragment(new BtCollectionFragment(), "Collection");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
    }

    @Override

    protected void onStart() {
        super.onStart();
        //Turn ON BlueTooth if it is OFF
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        setup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myThreadBeConnected != null) {
            myThreadBeConnected.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                setup();
            } else {
                Toast.makeText(this,
                        "BlueTooth NOT enabled",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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

    private void setup() {
        textStatus.setText("setup()");
        myThreadBeConnected = new ThreadBeConnected(changeUIFromThread, bluetoothAdapter,this);
        myThreadBeConnected.start();
    }

    public void startThreadConnected(BluetoothSocket socket) {
        myThreadConnected = new ThreadConnected(changeUIFromThread,socket,this);
        myThreadConnected.start();
    }



    public void onBtnClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_send:
                if (myThreadConnected != null) {
                    try {
                        DataUtils dataUtils = new DataUtils();
                        byte[] bytesToSend = ParcebleUtil.serialize(dataUtils.sendData(realm,transferModel));
                        myThreadConnected.write(bytesToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.btn_send_survey:
                if (myThreadConnected != null) {
                    try {
                        DataUtils dataUtils = new DataUtils();
                        byte[] bytesToSend = ParcebleUtil.serialize(dataUtils.sendSurveyData(realm));
                        myThreadConnected.write(bytesToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void changeUi() {
        tabs.setVisibility(View.VISIBLE);
        inputPane.setVisibility(View.VISIBLE);
        pulsator.setVisibility(View.GONE);
    }

    @Override
    public void changeStatus(String status) {
        textStatus.setText(status);
    }

    @Override
    public void connectToThread(BluetoothDevice device) {

    }

    @Override
    public void startThread(BluetoothSocket socket) {
        startThreadConnected(socket);
    }

    @Override
    public void disconnectThread() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Disconnected..!", Snackbar.LENGTH_LONG)
                .setAction("Close", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

        snackbar.show();
    }


    @Override
    public void collectData(TransferModel transferModel) {
        Toast.makeText(getApplicationContext(),"Called..!",Toast.LENGTH_SHORT).show();
        this.transferModel=transferModel;
        //this.transferModel.setName(transferModel.getName()+"");
        //this.transferModel.getSurveyList().addAll(transferModel.getSurveyList());
        //this.transferModel.getPatientsList().addAll(transferModel.getPatientsList());
        //this.transferModel.getUserList().addAll(transferModel.getUserList());
    }
}