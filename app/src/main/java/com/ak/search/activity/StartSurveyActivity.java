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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.adapter.GetQuestionsAdapter;
import com.ak.search.app.SaveAnswer;
import com.ak.search.fragment.QuestionFragment;
import com.ak.search.realm_model.Answers;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.Questions;
import com.ak.search.realm_model.Survey;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;

public class StartSurveyActivity extends AppCompatActivity implements SaveAnswer {

    Survey survey;
    Realm realm;
    @BindView(R.id.rv_questions)
    RecyclerView recyclerView;

    public boolean update;

    Patients patients;
    List<Questions> questionsList;
    public GetQuestionsAdapter mAdapter;
    private List<Answers> answersList;

    public static HashMap<Long, Answers> answers;
    public static int CAMERA_REQUEST = 11;

    public static int positionImg;

    public static int pos = 0, length = 0;
    SaveAnswer saveAnswer;
    DataCollection dataCollection;


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

        update = false;

        if (getIntent().getExtras() != null) {
            survey = Parcels.unwrap(getIntent().getExtras().getParcelable("survey"));
            patients = Parcels.unwrap(getIntent().getExtras().getParcelable("patient"));


            // survey = MSurvey.findById(MSurvey.class, (int) surveyId);
            getSupportActionBar().setTitle(survey.getName() + " ");


            for (int i = 0; i < survey.getQuestions().size(); i++) {

                Answers answ = new Answers();

                answ.setPatientid(patients.getId());
                answ.setQuestions(survey.getQuestions().get(i));

                answ.setSelectedopt(-1);
                answ.setSelectedOptConditional(-1);
                answ.setSelectedChk("");
                answ.setAns("");
                answ.setNumAns("");
                answ.setDate("");
                answ.setTime("");
                byte[] a = {-1};
                answ.setByteArrayImage(a);

                answersList.add(answ);
            }


            mAdapter = new GetQuestionsAdapter(this, answersList, saveAnswer, realm,true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            RecyclerView.ItemDecoration itemDecoration = new
                    DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(itemDecoration);
            recyclerView.setAdapter(mAdapter);


        }


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

        mAdapter.onActivityResult(requestCode, resultCode, data, positionImg);

        /*if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //ivCapture.setImageBitmap(photo);


           *//* ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            //ans.setByteArrayImage(byteArray);
            //getSelectedChkbox();
            //answer.onAnswerSave(ans);

            answersList.get(positionImg).setByteArrayImage(byteArray);
            mAdapter.notifyDataSetChanged();*//*
        }*/
    }

    @Override
    public void onAnswerSave(Answers ans) {
        ans.setPatientid(patients.getId());
        answers.put(ans.getQuestions().getId(), ans);


    }

    @Override
    public void onAddSurvey(long id, int pos) {


        if (this.pos != 0 || this.length != 0) {
            int count = 0;
            if (count < this.length) {
                for (int i = this.pos + 1; i <= (this.pos + this.length); i++) {
                    answersList.remove(this.pos + 1);
                    count++;
                }
            }
        }

        Log.v("Survey ID", "asdf " + id);
        Survey survey = realm.where(Survey.class).equalTo("id", id).findFirst();
        this.pos = pos;
        this.length = survey.getQuestions().size();

       /* for (int i = 0; i < survey.getQuestions().size(); i++) {
            Answers answ = new Answers();
            answ.setPatientid(patients.getId());
            answ.setQuestions(survey.getQuestions().get(i));
            answersList.add((pos + 1) + i, answ);
        }
*/

        for (int i = 0; i < survey.getQuestions().size(); i++) {

            Answers answ = new Answers();

            answ.setPatientid(patients.getId());
            answ.setQuestions(survey.getQuestions().get(i));

            answ.setSelectedopt(-1);
            answ.setSelectedOptConditional(-1);
            answ.setSelectedChk("");
            answ.setAns("");
            answ.setNumAns("");
            answ.setDate("");
            answ.setTime("");
            byte[] a = {-1};
            answ.setByteArrayImage(a);

            answersList.add((pos+1)+i,answ);
        }


        mAdapter.notifyDataSetChanged();


    }

    @Override
    protected void onDestroy() {
        pos = 0;
        length = 0;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_survey, menu);
        if (update) {
            menu.getItem(1).setTitle("update");

        } else {
            menu.getItem(0).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                discardSurvey();
                break;

            case R.id.action_save_survey:

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {


                        RealmList<Answers> answerses = new RealmList<Answers>();
                        for (int i = 0; i < answersList.size(); i++) {
                            Answers a = answersList.get(i);
                            Answers ans = realm.createObject(Answers.class);

                            Questions questions = realm.where(Questions.class).equalTo("id", a.getQuestions().getId()).findFirst();

                            ans.setQuestions(questions);
                            ans.setPatientid(a.getPatientid());
                            ans.setAns(a.getAns());
                            ans.setSelectedopt(a.getSelectedopt());
                            ans.setNumAns(a.getNumAns());
                            ans.setByteArrayImage(a.getByteArrayImage());
                            ans.setDate(a.getDate());
                            ans.setTime(a.getTime());
                            ans.setSelectedChk(a.getSelectedChk());
                            answerses.add(ans);

                        }


                        Patients patients1 = realm.where(Patients.class).equalTo("id", patients.getId()).findFirst();


                        int collectionId;
                        try {
                            collectionId = realm.where(DataCollection.class).max("id").intValue() + 1;
                        } catch (Exception ex) {
                            Log.v("exception", ex.toString());
                            collectionId = 1;
                        }

                        dataCollection = realm.createObject(DataCollection.class, collectionId);
                        dataCollection.setSurveyid(survey.getId());
                        dataCollection.setPatients(patients1);
                        dataCollection.setAnswerses(answerses);

                        String timeStamp = new SimpleDateFormat("dd.MM.yyyy:HH.mm.ss").format(new Date());
                        dataCollection.setLat(0);
                        dataCollection.setLng(0);
                        dataCollection.setTimestamp(timeStamp);

                        realm.copyToRealmOrUpdate(dataCollection);

                        Toast.makeText(getApplicationContext(), "DONE", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                });


                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
