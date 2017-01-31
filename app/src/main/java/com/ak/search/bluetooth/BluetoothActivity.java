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
import android.util.Log;
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
import com.ak.search.model.MTransferModel;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.Survey;
import com.ak.search.realm_model.TransferModel;
import com.ak.search.realm_model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class BluetoothActivity extends AppCompatActivity implements ChangeUIFromThread, CollectDataInfo {

    @BindView(R.id.ll_inputpane)
    LinearLayout inputPane;

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

    @BindView(R.id.tv_survey_info_count)
    TextView surveyCount;
    @BindView(R.id.tv_login_info_count)
    TextView loginCount;
    @BindView(R.id.tv_patient_info_count)
    TextView patientCount;
    @BindView(R.id.tv_data_collection_count)
    TextView dataCollectionCount;


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
        transferModel = new TransferModel();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessionManager = new SessionManager(this);
        changeUIFromThread = this;


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(this,
                    "FEATURE_BLUETOOTH NOT support",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //generate UUID on web: http://www.famkruithof.net/uuid/uuidgen
        //have to match the UUID on the another device of the BT connection
        myUUID = UUID.fromString("03e159d0-e6b8-11e6-9598-0800200c9a66");
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

      /*  if (myThreadConnected != null) {
            myThreadConnected.cancel();
        }*/

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
        myThreadBeConnected = new ThreadBeConnected(changeUIFromThread, bluetoothAdapter, this);
        myThreadBeConnected.start();
    }




    public void onBtnClick(View view) {
        int id = view.getId();


    }

    @Override
    public void changeUi() {
        // tabs.setVisibility(View.VISIBLE);
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
        myThreadConnected = new ThreadConnected(changeUIFromThread, socket, this);
        myThreadConnected.start();
    }

    @Override
    public void dataReceived(MTransferModel transferModel) {
        Toast.makeText(getApplicationContext(), " User List: " + transferModel.getUserList().size() +
                "\n patient List: " + transferModel.getPatientsList().size() +
                "\n Survey List: " + transferModel.getSurveyList().size()+
                "\n Data Collection List: " + transferModel.getDataCollectionsList().size(), Toast.LENGTH_SHORT).show();

        patientCount.setText(transferModel.getPatientsList().size()+" ");
        loginCount.setText(transferModel.getUserList().size()+ " ");
        surveyCount.setText(transferModel.getSurveyList().size()+" ");
        dataCollectionCount.setText(transferModel.getDataCollectionsList().size()+" ");
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


      /*  if (myThreadBeConnected != null) {
            myThreadBeConnected.cancel();
        }*/

      /*  if (myThreadConnected != null) {
            myThreadConnected.cancel();
        }*/


    }

    @Override
    public void collectData(TransferModel transferModel) {
    }
}