package com.ak.search.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.adapter.GetQuestionsAdapter;
import com.ak.search.app.SaveAnswer;
import com.ak.search.app.SessionManager;
import com.ak.search.model.MNestedAddQue;
import com.ak.search.realm_model.Answers;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.Questions;
import com.ak.search.realm_model.Survey;

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

public class ShowSurveyActivity extends AppCompatActivity implements SaveAnswer {

    Long collectionId;
    private List<Answers> answersList;
    @BindView(R.id.rv_questions)
    RecyclerView recyclerView;
    public GetQuestionsAdapter mAdapter;
    DataCollection dataCollection;
    Realm realm;
    boolean superviserLogin = false;
    public static int positionImg;
    public static Patients patients;
    public static HashMap<Integer, MNestedAddQue> addQueHashMap;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_survey);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        answersList = new ArrayList<>();
        addQueHashMap = new HashMap<>();
        sessionManager = new SessionManager(this);

        if (getIntent().getExtras() != null) {
            superviserLogin = getIntent().getExtras().getBoolean("superviserLogin");
            collectionId = getIntent().getExtras().getLong("collectionid");
            SaveAnswer saveAnswer = this;

            dataCollection = realm.where(DataCollection.class).equalTo("id", collectionId).findFirst();
            answersList.clear();
            //answersList.addAll(dataCollection.getAnswerses());

            Survey survey=realm.where(Survey.class).equalTo("id",dataCollection.getSurveyid()).findFirst();
            if (survey!=null){
                getSupportActionBar().setTitle(survey.getName());
            }
            addQue(dataCollection.getAnswerses());

            mAdapter = new GetQuestionsAdapter(this, answersList, saveAnswer, realm, superviserLogin);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            /*RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(itemDecoration);*/
            recyclerView.setAdapter(mAdapter);
            if (dataCollection.getPatients() != null) {
                getSupportActionBar().setTitle(dataCollection.getPatients().getPatientname() + " ");
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_survey, menu);

        if (!superviserLogin) {
            menu.getItem(0).setVisible(false);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_update_survey:
                updateData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAnswerSave(Answers ans) {
        if (patients != null) {
            ans.setPatientid(patients.getId());
        }
        //answers.put(ans.getQuestions().getId(), ans);

    }

    @Override
    public void onAddSurvey(long id, int pos, int parentPos) {
        boolean newEntry = true;
        Survey survey = realm.where(Survey.class).equalTo("id", id).findFirst();
        int surveysize = survey.getQuestions().size();

        if (addQueHashMap.size() > 0) {
            for (Map.Entry m : addQueHashMap.entrySet()) {
                MNestedAddQue nestedAddQue = (MNestedAddQue) m.getValue();

                if (nestedAddQue.getPos() == pos) {
                    deleteQuestions(pos);


                    if (nestedAddQue.getSurveyId() != id) {
                        addToHashMap(id, pos, surveysize, parentPos, 0);

                        newEntry = false;
                    }

                }

                if (nestedAddQue.getPos() == parentPos) {
                    addToHashMap(nestedAddQue.getSurveyId(), nestedAddQue.getPos(), nestedAddQue.getLengh(), nestedAddQue.getPos(), surveysize);
                }


            }
        }


        if (newEntry) {
            addToHashMap(id, pos, survey.getQuestions().size(), parentPos, 0);
        }


        addNewQuestion(pos, survey);
    }

    @Override
    public void saveCollection() {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAdapter.onActivityResult(requestCode, resultCode, data, positionImg);
    }

    public void addToHashMap(long id, int pos, int length, int parentPos, int childLength) {

        MNestedAddQue nestedAddQue = new MNestedAddQue();
        nestedAddQue.setSurveyId(id);
        nestedAddQue.setPos(pos);
        nestedAddQue.setLengh(length);
        nestedAddQue.setParentPos(parentPos);
        nestedAddQue.setChildLength(childLength);
        addQueHashMap.put(pos, nestedAddQue);


    }

    public void addQue(List<Answers> ans) {
        for (int i = 0; i < ans.size(); i++) {
            Answers answ = new Answers();
            answ.setPatientid(ans.get(i).getPatientid());
            answ.setQuestions(ans.get(i).getQuestions());
            answ.setParentPos(0);
            answ.setSelectedopt(ans.get(i).getSelectedopt());
            answ.setSelectedOptConditional(ans.get(i).getSelectedOptConditional());
            answ.setSelectedChk(ans.get(i).getSelectedChk());
            answ.setAns(ans.get(i).getAns());
            answ.setNumAns(ans.get(i).getNumAns());
            answ.setDate(ans.get(i).getDate());
            answ.setTime(ans.get(i).getTime());
            answ.setByteArrayImage(ans.get(i).getByteArrayImage());
            answersList.add(answ);

        }

    }

    public void addNewQuestion(int pos, Survey survey) {
        for (int i = 0; i < survey.getQuestions().size(); i++) {

            Answers answ = new Answers();
            answ.setPatientid(0);
            answ.setQuestions(survey.getQuestions().get(i));
            answ.setParentPos(pos);
            answ.setSelectedopt(-1);
            answ.setSelectedOptConditional(-1);
            answ.setSelectedChk("");
            answ.setAns("");
            answ.setNumAns("");
            answ.setDate("");
            answ.setTime("");
            byte[] a = {-1};
            answ.setByteArrayImage(a);
            answersList.add((pos + 1) + i, answ);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void deleteQuestions(int pos) {
        if (addQueHashMap.size() > 0) {
            // for (Map.Entry m : addQueHashMap.entrySet()) {
            MNestedAddQue nestedAddQue = addQueHashMap.get(pos);

            if (nestedAddQue != null) {
                //if (nestedAddQue.getPos() == pos) {
                int totalSize = nestedAddQue.getLengh() + nestedAddQue.getChildLength();

                int count = 0;
                if (count < totalSize && (nestedAddQue.getPos() + 1) < (answersList.size() - 1)) {
                    for (int i = nestedAddQue.getPos() + 1; i <= (nestedAddQue.getPos() + totalSize); i++) {
                        answersList.remove(nestedAddQue.getPos() + 1);
                        count++;
                    }
                }
            }
            //}
            //}
        }
    }

    public void updateData() {

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

                Patients patients1 = null;

                if (dataCollection.getPatients() != null) {
                    patients1 = dataCollection.getPatients();
                }
                //if (patients != null) {
                //    patients1 = realm.where(Patients.class).equalTo("id", patients.getId()).findFirst();
                //}


                /*int collectionId;
                try {
                    collectionId = realm.where(DataCollection.class).max("id").intValue() + 1;
                } catch (Exception ex) {
                    Log.v("exception", ex.toString());
                    collectionId = 1;
                }*/

                dataCollection = realm.where(DataCollection.class).equalTo("id", dataCollection.getId()).findFirst();
                dataCollection.setSurveyid(dataCollection.getSurveyid());
                dataCollection.setPatients(patients1);
                dataCollection.setAnswerses(answerses);


                dataCollection.setFieldworkerId(dataCollection.getFieldworkerId());
                dataCollection.setSuperwiserId(dataCollection.getSuperwiserId());

                String timeStamp = new SimpleDateFormat("dd.MM.yyyy:HH.mm.ss").format(new Date());
                dataCollection.setLat(dataCollection.getLat());
                dataCollection.setLng(dataCollection.getLng());
                dataCollection.setTimestamp(timeStamp);

                realm.copyToRealmOrUpdate(dataCollection);

                Toast.makeText(getApplicationContext(), "DONE", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }
}
