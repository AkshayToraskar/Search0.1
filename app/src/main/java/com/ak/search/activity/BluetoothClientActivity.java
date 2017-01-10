package com.ak.search.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.app.ParcebleUtil;
import com.ak.search.model.MPatients;
import com.ak.search.model.MSurvey;
import com.ak.search.model.MUser;
import com.ak.search.model.TransferModel;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.realm.Realm;

public class BluetoothClientActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    BluetoothAdapter bluetoothAdapter;

    ArrayList<BluetoothDevice> pairedDeviceArrayList;

    TextView textInfo, textStatus;
    ListView listViewPairedDevice;
    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    private UUID myUUID;

    LinearLayout inputPane;
   // EditText inputField;
    Button btnSend;

    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_client);
        textInfo = (TextView)findViewById(R.id.info);
        textStatus = (TextView)findViewById(R.id.status);
        listViewPairedDevice = (ListView)findViewById(R.id.pairedlist);

        realm=Realm.getDefaultInstance();

        inputPane = (LinearLayout)findViewById(R.id.inputpane);
       // inputField = (EditText)findViewById(R.id.input);
        btnSend = (Button)findViewById(R.id.send);
        btnSend.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(myThreadConnected!=null){

                  /*  User user=realm.where(User.class).findFirst();


                        // List<Test1> test1=new ArrayList<Test1>();
                        //  test1.add(new Test1(1));
                        // test1.add(new Test1(2));

                        TransferModel testModel=new TransferModel();
                        testModel.setName("aa");
                        //    testModel.setTest1List(test1);

                        try {
                            byte[] bytesToSend = ParcebleUtil.serialize(testModel);
                            myThreadConnected.write(bytesToSend);
                        } catch (IOException e) {
                            e.printStackTrace();
                    }*/

                    sendData();


                   // byte[] bytesToSend = inputField.getText().toString().getBytes();
                   // myThreadConnected.write(bytesToSend);
                }
            }});

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
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

    private void setup() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            pairedDeviceArrayList = new ArrayList<BluetoothDevice>();

            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceArrayList.add(device);
            }

            pairedDeviceAdapter = new ArrayAdapter<BluetoothDevice>(this,
                    android.R.layout.simple_list_item_1, pairedDeviceArrayList);
            listViewPairedDevice.setAdapter(pairedDeviceAdapter);

            listViewPairedDevice.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    BluetoothDevice device =
                            (BluetoothDevice)parent.getItemAtPosition(position);
                    Toast.makeText(BluetoothClientActivity.this,
                            "Name: " + device.getName() + "\n"
                                    + "Address: " + device.getAddress() + "\n"
                                    + "BondState: " + device.getBondState() + "\n"
                                    + "BluetoothClass: " + device.getBluetoothClass() + "\n"
                                    + "Class: " + device.getClass(),
                            Toast.LENGTH_LONG).show();

                    textStatus.setText("start ThreadConnectBTdevice");
                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
                    myThreadConnectBTdevice.start();
                }});
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(myThreadConnectBTdevice!=null){
            myThreadConnectBTdevice.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==REQUEST_ENABLE_BT){
            if(resultCode == Activity.RESULT_OK){
                setup();
            }else{
                Toast.makeText(this,
                        "BlueTooth NOT enabled",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private class ThreadConnectBTdevice extends Thread {
        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;


        public ThreadConnectBTdevice(BluetoothDevice device) {
            bluetoothDevice = device;

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
                textStatus.setText("bluetoothSocket: \n" + bluetoothSocket);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();

                final String eMessage = e.getMessage();
                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        textStatus.setText("something wrong bluetoothSocket.connect(): \n" + eMessage);
                    }});

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            if(success){
                //connect successful
                final String msgconnected = "connect successful:\n"
                        + "BluetoothSocket: " + bluetoothSocket + "\n"
                        + "BluetoothDevice: " + bluetoothDevice;

                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        textStatus.setText(msgconnected);

                        listViewPairedDevice.setVisibility(View.GONE);
                        inputPane.setVisibility(View.VISIBLE);
                    }});

                startThreadConnected(bluetoothSocket);
            }else{
                //fail
            }
        }

        public void cancel() {

            Toast.makeText(getApplicationContext(),
                    "close bluetoothSocket",
                    Toast.LENGTH_LONG).show();

            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    private void startThreadConnected(BluetoothSocket socket){

        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
    }

    private class ThreadConnected extends Thread {
        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;

        public ThreadConnected(BluetoothSocket socket) {
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[4096];
            int bytes;

            while (true) {
                try {
                    bytes = connectedInputStream.read(buffer);
                    //String strReceived = new String(buffer, 0, bytes);

                    TransferModel user=(TransferModel) ParcebleUtil.deserialize(buffer);

                    final String msgReceived = String.valueOf(bytes) +
                            " bytes received:\n"
                            + user.getName();

                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            textStatus.setText(msgReceived);
                        }});

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    final String msgConnectionLost = "Connection lost:\n"
                            + e.getMessage();
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            textStatus.setText(msgConnectionLost);
                        }});
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                connectedBluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    public void sendData() {

        List<MUser> mUserList = new ArrayList<>();
        List<MPatients> mPatientsList = new ArrayList<>();
        List<MSurvey> mSurveys = new ArrayList<>();

        List<User> user = realm.where(User.class).findAll();
        List<Patients> patients = realm.where(Patients.class).findAll();
        // List<Survey> surveys = realm.where(Survey.class).findAll();
        //List<Test1> test1=new ArrayList<Test1>();
        //test1.add(new Test1(1));
        // test1.add(new Test1(2));


        //ConvertUser
        for (int i = 0; i < user.size(); i++) {
            MUser mu = new MUser();
            mu.setId(user.get(i).getId());
            mu.setName(user.get(i).getName());
            mu.setPassword(user.get(i).getPassword());
            mu.setType(user.get(i).getType());
            mUserList.add(mu);
        }

        //ConvertPatients
        for (int i = 0; i < patients.size(); i++) {
            MPatients pa = new MPatients();
            pa.setId(patients.get(i).getId());
            pa.setAddress(patients.get(i).getAddress());
            pa.setPatientname(patients.get(i).getPatientname());
            mPatientsList.add(pa);
        }


        //Convert Survey
        /*for(int i=0; i<surveys.size(); i++){

            Survey sur=new Survey();
            sur.setId(surveys.get(i).getId());
            sur.setName(surveys.get(i).getName());

            for(int j=0; j<surveys.get(i).getQuestions().size(); j++){

            }


        }*/




        TransferModel transModel = new TransferModel();
        transModel.setName("aa");
        transModel.setUserList(mUserList);
        transModel.setPatientsList(mPatientsList);
        // transModel.setSurveyList(mSurveys);

        try {
            byte[] bytesToSend = ParcebleUtil.serialize(transModel);
            myThreadConnected.write(bytesToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
