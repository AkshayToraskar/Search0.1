package com.ak.search.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.adapter.PatientAdapter;
import com.ak.search.realm_model.Patients;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class PatientsActivity extends AppCompatActivity {

    private List<Patients> patientList;
    @BindView(R.id.rv_patient)
    RecyclerView recyclerView;
    public PatientAdapter mAdapter;
    Realm realm;
    public static int REQUEST_CODE = 14;
    public static String TAG = PatientsActivity.class.getName();
    private File selectedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        realm = Realm.getDefaultInstance();

        getSupportActionBar().setTitle(getResources().getString(R.string.patients));


        RealmResults<Patients> results = realm.where(Patients.class).findAll();

        patientList = new ArrayList<>();
        patientList.addAll(results);

        mAdapter = new PatientAdapter(this, patientList, realm);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        patientList.clear();

        patientList.addAll(realm.where(Patients.class).findAll());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_patient, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                finish();
                break;

            case R.id.action_import_survey:
                Intent intent = new Intent(this, FilePickerActivityLatest.class);
                startActivityForResult(intent, REQUEST_CODE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {


            if (data.hasExtra(FilePickerActivity.EXTRA_FILE_PATH)) {

                selectedFile = new File
                        (data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH));


                Log.v("file path", " " + selectedFile.getPath());
                parseCSVData();
            }


        }

    }


    private void parseCSVData() {


        CSVReader reader;
        try {

            if (getFileExt(selectedFile.getName()).equals("csv")) {


                reader = new CSVReader(new FileReader(selectedFile));
                String[] row;
                List<?> content = reader.readAll();

                int rowCount = 0;

                if (content != null) {

                    for (Object object : content) {
                        if (rowCount > 0) {
                            row = (String[]) object;
                            for (int i = 0; i < row.length; i++) {
                                // display CSV values
                                System.out.println("Cell column index: " + i);
                                System.out.println("Cell Value: " + row[i]);
                                System.out.println("-------------");
                            }

                            final String strId = row[0] + row[2] + row[3] + row[5];

                            final String[] finalRow = row;
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    long id = Long.parseLong(finalRow[13]);
                                    Patients patients = realm.where(Patients.class).equalTo("id", id).findFirst();

                                    if (patients == null) {
                                        patients = realm.createObject(Patients.class, id);
                                    }

                                    patients.setHouseId(strId);
                                    patients.setPatientname(finalRow[7]);
                                    patients.setAge(Integer.parseInt(finalRow[8]));
                                    patients.setSex(Integer.parseInt(finalRow[9]));
                                    realm.copyToRealmOrUpdate(patients);
                                }
                            });

                        } else {
                            rowCount = rowCount + 1;
                        }


                    }
                }

                patientList.addAll(realm.where(Patients.class).findAll());
                mAdapter.notifyDataSetChanged();

                Toast.makeText(getApplicationContext(), "Data Successfully Imported..!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Select .csv file", Toast.LENGTH_SHORT).show();
            }

        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());

            Toast.makeText(getApplicationContext(), "File is not proper format", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            Toast.makeText(getApplicationContext(), "File is not proper format", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            Toast.makeText(getApplicationContext(), "File is not proper format", Toast.LENGTH_SHORT).show();
        }


    }

    public static String getFileExt(String fileName) {

        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).trim();
    }

}
