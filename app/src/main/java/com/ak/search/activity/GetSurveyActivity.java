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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.adapter.DataCollectionAdapter;
import com.ak.search.adapter.PatientAdapter;
import com.ak.search.adapter.SurveyHistoryAdapter;
import com.ak.search.model.MSurvey;
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
import io.realm.RealmResults;

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

    private int mYear, mMonth, mDay, mHour, mMinute;

    Long patientId=(long)0;
    String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_survey);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        //RealmResults<DataCollection> results = realm.where(DataCollection.class).findAll();
        //patientList = new ArrayList<>();

        //patientList.addAll(results);

        getSupportActionBar().setTitle(getResources().getString(R.string.collectedData));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       /* if (getIntent().getExtras() != null) {
            surveyId = getIntent().getExtras().getLong("surveyId");

            questionsList = MQuestions.find(MQuestions.class, "surveyid = ?", String.valueOf(surveyId));

            List<MOptions> opt = new ArrayList<>();
            for (int i = 0; i < questionsList.size(); i++) {
                opt = questionsList.get(i).getOptions(String.valueOf(questionsList.get(i).getId()));
            }*/

        //patientList = MPatients.listAll(MPatients.class);

        slidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                // slideButton.setBackgroundResource(R.drawable.down_arrow_icon);
                slidingDrawer.setBackgroundResource(R.color.cardview_light_background);

                ivArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));

                recyclerView.setVisibility(View.GONE);
            }
        });

        slidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {

                recyclerView.setVisibility(View.VISIBLE);
                //  slideButton.setBackgroundResource(R.drawable.upwar_arrow_icon);
                slidingDrawer.setBackgroundColor(Color.TRANSPARENT);
                ivArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
            }
        });

        surveyHistory = new ArrayList<>();
        surveyHistoryFilter = new ArrayList<>();
        surveyHistory.addAll(realm.where(DataCollection.class).findAll());

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


        /*spnSurveyName.setOnItemSelectedListener
                (new AdapterView.OnItemSelectedListener() {
                     @Override
                     public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                         //String selectedItem = String.valueOf(spnSurveyName.getSelectedItem());
                         surveyHistory.clear();

                         if (i == 0) {
                             surveyHistory.addAll(realm.where(DataCollection.class).findAll());
                         } else {
                             surveyHistory.addAll(realm.where(DataCollection.class).equalTo("surveyid", lstSurveyData.get(i-1).getId()).findAll());
                         }

                         mAdapter.notifyDataSetChanged();
                     }

                     @Override
                     public void onNothingSelected(AdapterView<?> adapterView) {
                     }
                 }
                );*/
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
                        .setTitle("Export Data")
                        .setMessage("Would you like to Export data to csv?")
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
            /*data.add(new String[]{"Id", "SurveyId", "FieldworkerId", "SupervisorId", "Timestamp", "Latitude", "Longitude",
                    "Patient Name", "Sex", "Age", "QuestionId", "selectedOpt", "selectedOptConditional", "getSelectedCheck",
                    "Ans", "NumAns", "Date", "Time", "Image"});*/
            for (int i = 0; i < surveyHistory.size(); i++) {

                List<String> strData = new ArrayList<>();

                strData.add(String.valueOf(surveyHistory.get(i).getId()));
                strData.add(String.valueOf(surveyHistory.get(i).getSurveyid()));
                strData.add(String.valueOf(surveyHistory.get(i).getFieldworkerId()));
                strData.add(String.valueOf(surveyHistory.get(i).getSuperwiserId()));
                strData.add(String.valueOf(surveyHistory.get(i).getTimestamp()));
                strData.add(String.valueOf(surveyHistory.get(i).getLat()));
                strData.add(String.valueOf(surveyHistory.get(i).getLng()));
                // strData.add(" "+surveyHistory.get(i).getPatients().getPatientname());
                // strData.add(String.valueOf(surveyHistory.get(i).getPatients().getSex()));
                // strData.add(String.valueOf(surveyHistory.get(i).getPatients().getAge()));

                for (int j = 0; j < surveyHistory.get(i).getAnswerses().size(); j++) {
                    strData.add(String.valueOf(surveyHistory.get(i).getAnswerses().get(j).getQuestions().getId()));
                    strData.add(String.valueOf(surveyHistory.get(i).getAnswerses().get(j).getSelectedopt()));
                    strData.add(String.valueOf(surveyHistory.get(i).getAnswerses().get(j).getSelectedOptConditional()));
                    strData.add(String.valueOf(surveyHistory.get(i).getAnswerses().get(j).getSelectedChk()));
                    strData.add(String.valueOf(surveyHistory.get(i).getAnswerses().get(j).getAns()));
                    strData.add(String.valueOf(surveyHistory.get(i).getAnswerses().get(j).getNumAns()));
                    strData.add(String.valueOf(surveyHistory.get(i).getAnswerses().get(j).getDate()));
                    strData.add(String.valueOf(surveyHistory.get(i).getAnswerses().get(j).getTime()));
                    strData.add(String.valueOf(surveyHistory.get(i).getAnswerses().get(j).getByteArrayImage()));
                }

                String str[] = new String[strData.size()];

                for (int k = 0; k < strData.size(); k++) {

                    str[k] = strData.get(k);

                }

                data.add(str);

            }

            //data.add(new String[]{"India", "New Delhi"});
            //data.add(new String[]{"United States", "Washington D.C"});
            //data.add(new String[]{"Germany", "Berlin"});
            //data.add(new String[]{"asdf", "23423s"});
            //data.add(new String[]{"Germ3423any", "asdf"});
            writer.writeAll(data);
            writer.close();
            Log.v("asdf", "SUCCESS");

            Toast.makeText(getApplicationContext(), "Data Exported Successfully", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
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

        //surveyHistoryFilter.clear();
       // boolean filter = false;

        RealmQuery q=realm.where(DataCollection.class);

        if (spnSurveyName.getSelectedItemPosition() > 0) {
            q=q.equalTo("surveyid",lstSurveyData.get(spnSurveyName.getSelectedItemPosition() - 1).getId());
            //surveyHistoryFilter.addAll(realm.where(DataCollection.class).equalTo("surveyid", lstSurveyData.get(spnSurveyName.getSelectedItemPosition() - 1).getId()).findAll());
           // filter = true;
        }

        if (spnFieldWorker.getSelectedItemPosition() > 0) {
            q=q.equalTo("fieldworkerId", lstUserData.get(spnFieldWorker.getSelectedItemPosition() - 1).getId());
            //surveyHistoryFilter.addAll(realm.where(DataCollection.class).equalTo("superwiserId", lstUserData.get(spnSupervisor.getSelectedItemPosition() - 1).getId()).findAll());
           // filter = true;
        }

        if(patientId!=0){
            q=q.equalTo("patients.id", patientId);
          //  filter=true;
          //  surveyHistoryFilter.addAll(realm.where(DataCollection.class).equalTo("patients.id", patientId).findAll());
        }

        if (selectedDate != null) {
            q=q.beginsWith("timestamp", selectedDate);
            //surveyHistoryFilter.addAll(realm.where(DataCollection.class).equalTo("timestamp", selectedDate).findAll());
           // filter = true;
        }

       // if (filter == false) {
            //surveyHistoryFilter.addAll(realm.where(DataCollection.class).findAll());
       // }

        surveyHistory.clear();
        surveyHistory.addAll(q.findAll());
        mAdapter.notifyDataSetChanged();
    }

}
