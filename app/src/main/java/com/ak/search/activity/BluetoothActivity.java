package com.ak.search.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.app.ParcebleUtil;
import com.ak.search.model.MPatients;
import com.ak.search.model.MSurvey;
import com.ak.search.model.MUser;
import com.ak.search.model.TransferModel;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.Survey;
import com.ak.search.realm_model.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;

public class BluetoothActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    BluetoothAdapter bluetoothAdapter;

    private UUID myUUID;
    private String myName;

    LinearLayout inputPane;
    //EditText inputField;
    Button btnSend;

    TextView textInfo, textStatus;

    ThreadBeConnected myThreadBeConnected;
    ThreadConnected myThreadConnected;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        textInfo = (TextView) findViewById(R.id.info);
        textStatus = (TextView) findViewById(R.id.status);

        realm = Realm.getDefaultInstance();

        inputPane = (LinearLayout) findViewById(R.id.inputpane);
        // inputField = (EditText)findViewById(R.id.input);
        btnSend = (Button) findViewById(R.id.send);
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (myThreadConnected != null) {

                    sendData();


                    // byte[] bytesToSend = inputField.getText().toString().getBytes();
                    // myThreadConnected.write(bytesToSend);
                }
            }
        });

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
        textStatus.setText("setup()");
        myThreadBeConnected = new ThreadBeConnected();
        myThreadBeConnected.start();
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


    private class ThreadBeConnected extends Thread {

        private BluetoothServerSocket bluetoothServerSocket = null;

        public ThreadBeConnected() {
            try {
                bluetoothServerSocket =
                        bluetoothAdapter.listenUsingRfcommWithServiceRecord(myName, myUUID);

                textStatus.setText("Waiting\n"
                        + "bluetoothServerSocket :\n"
                        + bluetoothServerSocket);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            BluetoothSocket bluetoothSocket = null;

            if (bluetoothServerSocket != null) {
                try {
                    bluetoothSocket = bluetoothServerSocket.accept();

                    BluetoothDevice remoteDevice = bluetoothSocket.getRemoteDevice();

                    final String strConnected = "Connected:\n" +
                            remoteDevice.getName() + "\n" +
                            remoteDevice.getAddress();

                    //connected
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            textStatus.setText(strConnected);
                            inputPane.setVisibility(View.VISIBLE);
                        }
                    });

                    startThreadConnected(bluetoothSocket);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    final String eMessage = e.getMessage();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            textStatus.setText("something wrong: \n" + eMessage);
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textStatus.setText("bluetoothServerSocket == null");
                    }
                });
            }
        }

        public void cancel() {

            Toast.makeText(getApplicationContext(),
                    "close bluetoothServerSocket",
                    Toast.LENGTH_LONG).show();

            try {
                bluetoothServerSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void startThreadConnected(BluetoothSocket socket) {

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
            byte[] buffer = new byte[2048];
            int bytes;

            while (true) {
                try {
                    bytes = connectedInputStream.read(buffer);

                    //String strReceived = new String(buffer, 0, bytes);

                    TransferModel user = (TransferModel) ParcebleUtil.deserialize(buffer);

                    final String msgReceived = String.valueOf(bytes) +
                            " bytes received:\n"
                            + user.getName();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            textStatus.setText(msgReceived);
                        }
                    });

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    final String msgConnectionLost = "Connection lost:\n"
                            + e.getMessage();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            textStatus.setText(msgConnectionLost);
                        }
                    });
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
                connectedOutputStream.flush();
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