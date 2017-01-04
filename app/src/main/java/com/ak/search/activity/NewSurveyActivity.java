package com.ak.search.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.adapter.PatientAdapter;
import com.ak.search.model.Patients;
import com.ak.search.model.Survey;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class NewSurveyActivity extends AppCompatActivity {

    long surveyId;
    private List<Patients> patientList;
    @BindView(R.id.rv_collected_survey)
    RecyclerView recyclerView;
    public PatientAdapter mAdapter;

    @BindView(R.id.tvtotalquestion)
    TextView tvTotalQuestions;

    @BindView(R.id.etPatientName)
    EditText etPatientName;

    @BindView(R.id.btnStartSurvey)
    Button btnStartSurvey;

    @BindView(R.id.ll_nodata)
    LinearLayout llNoData;

    public static Survey selectedSurvey;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_survey);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this);
        patientList = new ArrayList<>();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAdapter = new PatientAdapter(this, patientList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        if (getIntent().getExtras() != null) {

            surveyId = getIntent().getExtras().getLong("SurveyId");
            int totalQuestions = getIntent().getExtras().getInt("TotalQuestions");

            selectedSurvey = realm.where(Survey.class).findFirst();
            tvTotalQuestions.setText(" (" + totalQuestions + " Questions)");


            if (selectedSurvey != null) {
                // String surveyId1 = String.valueOf(selectedSurvey.getId());
                getSupportActionBar().setTitle(selectedSurvey.getName());
                patientList.clear();
                patientList.addAll(realm.where(Patients.class).findAll());
                mAdapter.notifyDataSetChanged();

            }

        }


        etPatientName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 2) {
                    btnStartSurvey.setEnabled(true);
                } else {
                    btnStartSurvey.setEnabled(false);
                }
            }
        });


        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_export:
                new AlertDialog.Builder(this)
                        .setTitle("Export to CSV")
                        .setMessage("Would you like to Export " + selectedSurvey.getName() + " ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                generateCSV();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // user doesn't want to logout
                            }
                        })
                        .show();
                break;
        }


        return super.onOptionsItemSelected(item);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_get_survey, menu);


        return true;
    }

    public void generateCSV() {

        try {


            File myDirectory = new File(Environment.getExternalStorageDirectory(), "SEARCH");

            if (!myDirectory.exists()) {
                myDirectory.mkdirs();
            }

            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());


            String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "SEARCH" + File.separator + "PatientData " + currentDateTimeString + ".csv";
            CSVWriter writer = null;
            writer = new CSVWriter(new FileWriter(csv));

            List<String[]> data = new ArrayList<String[]>();

           /* for(int i=0; i<patientList.size(); i++) {
                data.add(new String[]{patientList.get(i).getId().toString(), patientList.get(i).getPatientname(),patientList.get(i).getSurveyid()});
            }*/

            writer.writeAll(data);
            writer.close();

            Log.v("asdf", "SUCCESS");

            Toast.makeText(getApplicationContext(), "Successfully Exported the data..!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();

            Toast.makeText(getApplicationContext(), "Something went wrong..!", Toast.LENGTH_SHORT).show();
        }
    }


    public void onBtnClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btnStartSurvey:
                Intent i = new Intent(this, QuestionsActivity.class);
                i.putExtra("surveyId", surveyId);
                i.putExtra("patientName", etPatientName.getText().toString());
                startActivity(i);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        etPatientName.setText("");
        patientList.clear();
        // patientList.addAll(Patients.find(Patients.class, "surveyid=?", String.valueOf(surveyId)));
        patientList.addAll(realm.where(Patients.class).findAll());
        mAdapter.notifyDataSetChanged();


        if (patientList.size() > 0) {
            llNoData.setVisibility(View.GONE);
        } else {
            llNoData.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
