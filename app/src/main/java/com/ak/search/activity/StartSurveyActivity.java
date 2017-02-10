package com.ak.search.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.ak.search.R;
import com.ak.search.adapter.GetQuestionsAdapter;
import com.ak.search.app.SaveAnswer;
import com.ak.search.fragment.QuestionFragment;
import com.ak.search.realm_model.Answers;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.Questions;
import com.ak.search.realm_model.Survey;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class StartSurveyActivity extends AppCompatActivity implements SaveAnswer {

    Survey survey;
    Realm realm;
    @BindView(R.id.rv_questions)
    RecyclerView recyclerView;
    Patients patients;
    List<Questions> questionsList;
    public GetQuestionsAdapter mAdapter;
    private List<Answers> answersList;

    public static HashMap<Long, Answers> answers;
    public static int CAMERA_REQUEST = 11;

    public static int pos = 0, length = 0;
    SaveAnswer saveAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_survey);

        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        answersList = new ArrayList<>();
        answers = new HashMap<>();
        saveAnswer = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            survey = Parcels.unwrap(getIntent().getExtras().getParcelable("survey"));
            patients = Parcels.unwrap(getIntent().getExtras().getParcelable("patient"));


            // survey = MSurvey.findById(MSurvey.class, (int) surveyId);
            getSupportActionBar().setTitle(survey.getName() + " ");


            for (int i = 0; i < survey.getQuestions().size(); i++) {

                Answers answ = new Answers();

                answ.setPatientid(patients.getId());
                answ.setQuestions(survey.getQuestions().get(i));

                answersList.add(answ);
            }


            mAdapter = new GetQuestionsAdapter(this, answersList, saveAnswer);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            RecyclerView.ItemDecoration itemDecoration = new
                    DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(itemDecoration);
            recyclerView.setAdapter(mAdapter);


        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                discardSurvey();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void discardSurvey() {
        new AlertDialog.Builder(this)
                .setTitle("Discard")
                .setMessage("Would you like to Discard MSurvey?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //    MPatients patients = MPatients.findById(MPatients.class, patientId);
                        //    patients.delete();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // user doesn't want to logout
                    }
                })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        mAdapter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAnswerSave(Answers ans) {
        ans.setPatientid(patients.getId());
        answers.put(ans.getQuestions().getId(), ans);


    }

    @Override
    public void onAddSurvey(long id, int pos) {


        if (this.pos != 0 && length != 0) {
            for(int i=pos;i<length; i++) {
                answersList.remove(i);
            }
        }

        Log.v("Survey ID", "asdf " + id);
        Survey survey = realm.where(Survey.class).equalTo("id", id).findFirst();
        this.pos = pos;
        this.length = survey.getQuestions().size();

        for (int i = 0; i < survey.getQuestions().size(); i++) {
            Answers answ = new Answers();
            answ.setPatientid(patients.getId());
            answ.setQuestions(survey.getQuestions().get(i));
            answersList.add((pos + 1) + i, answ);
        }

        mAdapter.notifyDataSetChanged();

    }
}
