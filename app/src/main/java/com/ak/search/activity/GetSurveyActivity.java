package com.ak.search.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.adapter.SurveyHistoryAdapter;
import com.ak.search.app.CsvOperation;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.Survey;
import com.ak.search.realm_model.User;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;

public class GetSurveyActivity extends AppCompatActivity {

    long surveyId;
    private List<DataCollection> surveyHistory;
    List<DataCollection> surveyHistoryFilter;

    @BindView(R.id.rv_questions)
    RecyclerView recyclerView;

    @BindView(R.id.spnSurveyName)
    Spinner spnSurveyName;

    @BindView(R.id.slidingDrawer)
    SlidingDrawer slidingDrawer;

    public SurveyHistoryAdapter mAdapter;
    ArrayAdapter<String> spnSurveyNameAdapter, spnFiledworkerNameAdapter;
    Realm realm;
    public static int PATIENT_REQUEST = 12;
    List<Survey> lstSurveyData;

    List<User> lstUserData;

    @BindView(R.id.ivArrow)
    ImageView ivArrow;

    @BindView(R.id.tvDate)
    TextView tvDate;

    @BindView(R.id.tvPatient)
    TextView tvPatient;

    @BindView(R.id.spnFieldworker)
    Spinner spnFieldWorker;

    @BindView(R.id.tv_survey_count)
    TextView tvSurveyCount;

    private int mYear, mMonth, mDay, mHour, mMinute;

    Long patientId = (long) 0;
    String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_survey);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        getSupportActionBar().setTitle(getResources().getString(R.string.collectedData));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        slidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {

                slidingDrawer.setBackgroundResource(R.color.cardview_light_background);
                ivArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));
                recyclerView.setVisibility(View.GONE);
            }
        });

        slidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {

                recyclerView.setVisibility(View.VISIBLE);
                slidingDrawer.setBackgroundColor(Color.TRANSPARENT);
                ivArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
            }
        });

        surveyHistory = new ArrayList<>();
        surveyHistoryFilter = new ArrayList<>();
        surveyHistory.addAll(realm.where(DataCollection.class).findAll());

        tvSurveyCount.setText("" + surveyHistory.size());
        mAdapter = new SurveyHistoryAdapter(this, surveyHistory, true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(mAdapter);


        lstSurveyData = realm.where(Survey.class).equalTo("nested", false).findAll();

        final String surveyName[] = new String[lstSurveyData.size() + 1];
        surveyName[0] = "All";
        for (int i = 0; i < lstSurveyData.size(); i++) {
            surveyName[i + 1] = lstSurveyData.get(i).getName();
        }


        spnSurveyNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, surveyName);
        spnSurveyNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spnSurveyName.setAdapter(spnSurveyNameAdapter);


        lstUserData = realm.where(User.class).equalTo("type", 3).findAll();
        final String fieldworkderName[] = new String[lstUserData.size() + 1];
        fieldworkderName[0] = "All";
        for (int i = 0; i < lstUserData.size(); i++) {
            fieldworkderName[i + 1] = lstUserData.get(i).getName();
        }

        spnFiledworkerNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fieldworkderName);
        spnFiledworkerNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spnFieldWorker.setAdapter(spnFiledworkerNameAdapter);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_export:


                if (spnSurveyName.getSelectedItemPosition() == 0) {
                    Toast.makeText(getApplicationContext(), "please select any one survey from filter to export..!", Toast.LENGTH_LONG).show();
                    break;
                }

                new AlertDialog.Builder(this)
                        .setTitle("Export Data")
                        .setMessage("Would you like to Export data to csv?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                generateCSV(lstSurveyData.get(spnSurveyName.getSelectedItemPosition() - 1).getId());
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

    public void generateCSV(Long survId) {

        try {

            File myDirectory = new File(Environment.getExternalStorageDirectory(), "SEARCH");
            if (!myDirectory.exists()) {
                myDirectory.mkdirs();
            }
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

            List<String[]> data = new ArrayList<String[]>();

            CsvOperation csvOperation = new CsvOperation(surveyHistory, survId);
            List<String[]> strData = csvOperation.generateString();
            Survey survey = realm.where(Survey.class).equalTo("id", survId).findFirst();

            String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "SEARCH" + File.separator + survey.getName() + "_" + "Ans_" + currentDateTimeString + ".csv";
            CSVWriter writer = null;
            writer = new CSVWriter(new FileWriter(csv));

            for (int k = 0; k < strData.size(); k++) {
                data.add(strData.get(k));
            }


            writer.writeAll(data);
            writer.close();
            Log.v("Export Data", "SUCCESS");

            Toast.makeText(getApplicationContext(), "Data Exported Successfully into " + survey.getName() + "_" + "Ans_" + currentDateTimeString + ".csv file", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Log.v("Export Data", "FAIL");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }

    public void onBtnClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btnSelectDate:
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                selectedDate = dayOfMonth + "." + String.format("%02d", (monthOfYear + 1)) + "." + year;
                                tvDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);


                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

                break;

            case R.id.btnSelectPatient:
                Intent i = new Intent(this, SelectPatientsActivity.class);
                startActivityForResult(i, PATIENT_REQUEST);
                break;

            case R.id.btnApply:
                applyFilter();

                slidingDrawer.close();

                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PATIENT_REQUEST && resultCode == Activity.RESULT_OK) {

            patientId = (long) data.getExtras().get("data");
            Patients p = realm.where(Patients.class).equalTo("id", patientId).findFirst();
            if (p != null) {
                tvPatient.setText(p.getPatientname());
            }

        }
    }

    public void applyFilter() {

        RealmQuery q = realm.where(DataCollection.class);

        if (spnSurveyName.getSelectedItemPosition() > 0) {
            q = q.equalTo("surveyid", lstSurveyData.get(spnSurveyName.getSelectedItemPosition() - 1).getId());
        }

        if (spnFieldWorker.getSelectedItemPosition() > 0) {
            q = q.equalTo("fieldworkerId", lstUserData.get(spnFieldWorker.getSelectedItemPosition() - 1).getId());
        }

        if (patientId != 0) {
            q = q.equalTo("patients.id", patientId);
        }

        if (selectedDate != null) {
            q = q.beginsWith("timestamp", selectedDate);
        }

        surveyHistory.clear();
        surveyHistory.addAll(q.findAll());
        mAdapter.notifyDataSetChanged();
        tvSurveyCount.setText("" + surveyHistory.size());
    }

}
