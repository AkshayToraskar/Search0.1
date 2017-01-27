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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.activity.QuestionsActivity;
import com.ak.search.adapter.BluetoothPairedAdapter;
import com.ak.search.adapter.PatientTabViewpagerAdapter;
import com.ak.search.adapter.UsersAdapter;
import com.ak.search.app.ChangeUIFromThread;
import com.ak.search.app.CollectDataInfo;
import com.ak.search.app.ParcebleUtil;
import com.ak.search.app.SessionManager;
import com.ak.search.bluetooth.fragment.BtCollectionFragment;
import com.ak.search.bluetooth.fragment.BtLoginFragment;
import com.ak.search.bluetooth.fragment.BtPatientFragment;
import com.ak.search.bluetooth.fragment.BtSurveyFragment;
import com.ak.search.fragment.ImpExpFragment;
import com.ak.search.fragment.UserFragment;
import com.ak.search.model.MTransferModel;
import com.ak.search.realm_model.Answers;
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
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class BluetoothClientActivity extends AppCompatActivity implements ChangeUIFromThread, CollectDataInfo {

    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<BluetoothDevice> pairedDeviceArrayList;

    @BindView(R.id.tv_info)
    TextView textInfo;
    @BindView(R.id.tv_status)
    TextView textStatus;

    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    public static UUID myUUID;
    @BindView(R.id.ll_inputpane)
    LinearLayout inputPane;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_btlist)
    RecyclerView recyclerView;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    public ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;



    public static ChangeUIFromThread changeUIFromThread;
    SessionManager sessionManager;
    Realm realm;

    TransferModel transferModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_client);
        ButterKnife.bind(this);
        changeUIFromThread = this;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        realm = Realm.getDefaultInstance();

        sessionManager = new SessionManager(this);
        transferModel = new TransferModel();

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
    }

    private void setupViewPager(ViewPager viewPager) {
        PatientTabViewpagerAdapter adapter = new PatientTabViewpagerAdapter(getSupportFragmentManager());
        // adapter.addFragment(new ImpExpFragment(), "Imp / Exp");
        adapter.addFragment(new BtLoginFragment(), "Login Info");
        adapter.addFragment(new BtPatientFragment(), "Patients");
        adapter.addFragment(new BtSurveyFragment(), "Survey");
        adapter.addFragment(new BtCollectionFragment(), "Collection");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
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



        if (myThreadConnectBTdevice != null) {
            myThreadConnectBTdevice.cancel();
        }

     /*   if( myThreadConnected!=null){
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


    public void startThreadConnected(BluetoothSocket socket) {
        myThreadConnected = new ThreadConnected(changeUIFromThread, socket, this);
        myThreadConnected.start();
    }

    private void setup() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            pairedDeviceArrayList = new ArrayList<BluetoothDevice>();

            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceArrayList.add(device);
            }


            BluetoothPairedAdapter mAdapter = new BluetoothPairedAdapter(this, pairedDeviceArrayList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);


        }
    }

    public void onBtnClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_send:
                if (myThreadConnected != null) {
                    try {
                        DataUtils dataUtils = new DataUtils();
                        byte[] bytesToSend = ParcebleUtil.serialize(dataUtils.sendData(realm, transferModel));
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
        recyclerView.setVisibility(View.GONE);
        inputPane.setVisibility(View.VISIBLE);
    }

    @Override
    public void changeStatus(String status) {
        textStatus.setText(status);
    }

    @Override
    public void connectToThread(BluetoothDevice device) {
        myThreadConnectBTdevice = new ThreadConnectBTdevice(changeUIFromThread, device, this);
        myThreadConnectBTdevice.start();
    }

    @Override
    public void startThread(BluetoothSocket socket) {
        startThreadConnected(socket);
    }

    @Override
    public void dataReceived(MTransferModel transferModel) {

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



        if (myThreadConnectBTdevice != null) {
            myThreadConnectBTdevice.cancel();
        }

       /* if( myThreadConnected!=null){
            myThreadConnected.cancel();
        }*/

    }

    HashMap<Long, User> userData = new HashMap<>();
    HashMap<Long, Patients> patientData = new HashMap<>();
    HashMap<Long, Survey> surveyData = new HashMap<>();


    @Override
    public void collectData(TransferModel transferModel) {

        if (transferModel.getUserList() != null) {
            if (transferModel.getName().equals("true")) {
                userData.put(transferModel.getUserList().get(0).getId(), transferModel.getUserList().get(0));
            } else {
                userData.remove(transferModel.getUserList().get(0).getId());
            }
        } else if (transferModel.getPatientsList() != null) {
            if (transferModel.getName().equals("true")) {
                patientData.put(transferModel.getPatientsList().get(0).getId(), transferModel.getPatientsList().get(0));
            } else {
                patientData.remove(transferModel.getPatientsList().get(0).getId());
            }
        } else if (transferModel.getSurveyList() != null) {
            if (transferModel.getName().equals("true")) {
                surveyData.put(transferModel.getSurveyList().get(0).getId(), transferModel.getSurveyList().get(0));
            } else {
                surveyData.remove(transferModel.getSurveyList().get(0).getId());
            }
        }

        List<User> userList = new ArrayList<>();
        List<Patients> patientsList = new ArrayList<>();
        List<Survey> surveyList = new ArrayList<>();

        for (Map.Entry m : userData.entrySet()) {
            userList.add((User) m.getValue());
        }

        for (Map.Entry m : patientData.entrySet()) {
            patientsList.add((Patients) m.getValue());
        }

        for (Map.Entry m : surveyData.entrySet()) {
            surveyList.add((Survey) m.getValue());
        }

        this.transferModel.setName("sending");
        this.transferModel.setSurveyList(surveyList);
        this.transferModel.setUserList(userList);
        this.transferModel.setPatientsList(patientsList);
    }
}
