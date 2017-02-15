package com.ak.search.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ak.search.R;
import com.ak.search.adapter.QuestionsAdapter;
import com.ak.search.app.SessionManager;
import com.ak.search.app.Validate;
import com.ak.search.realm_model.Options;
import com.ak.search.realm_model.Questions;
import com.ak.search.realm_model.Survey;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;

public class AddSurveyActivity extends AppCompatActivity {

    @BindView(R.id.txt_survey_name)
    EditText txt_survey_name;
@BindView(R.id.cb_nested_survey)
    CheckBox cbNestedSurvey;

    @BindView(R.id.rv_questions)
    RecyclerView recyclerView;
    public static QuestionsAdapter mAdapter;

    Validate validate;

    public static String SURVEYID = "surveyid";
    long surveyId;
    String surveyName;
    public boolean update;
    SessionManager sessionManager;
    Realm realm;

    public static Survey survey;
    public RealmList<Questions> questionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_survey);
        ButterKnife.bind(this);
        validate = new Validate();
        realm = Realm.getDefaultInstance();

        sessionManager = new SessionManager(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        questionsList = new RealmList<>();

        if (getIntent().getExtras() != null) {
            this.surveyId = getIntent().getExtras().getLong("surveyId");
            this.surveyName = getIntent().getExtras().getString("surveyName");
            txt_survey_name.setText(surveyName + " ");

            survey = realm.where(Survey.class).equalTo("id", surveyId).findFirst();

            Log.v("SURVEY NAME", "dasf " + survey.getName() + "=====" + survey.getQuestions().size());

            cbNestedSurvey.setChecked(survey.getNested());
            questionsList.clear();
            questionsList.addAll(survey.getQuestions());

            update = true;
        } else {

            txt_survey_name.setText("MSurvey " + String.valueOf(sessionManager.getSurveyId()));

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    //long id = realm.where(MUser.class).max("id").longValue() + 1;


                    try {
                        surveyId = realm.where(Survey.class).max("id").intValue() + 1;
                    } catch (Exception ex) {
                        Log.v("exception", ex.toString());
                        surveyId = 1;
                    }

                    // Add a survey
                    survey = realm.createObject(Survey.class, surveyId);
                    //survey.setId(surveyId);
                    survey.setNested(cbNestedSurvey.isChecked());
                    survey.setName("MSurvey " + String.valueOf(sessionManager.getSurveyId()));
                    realm.copyToRealmOrUpdate(survey);
                    //Toast.makeText(getApplicationContext(), "MUser Added Successfully !", Toast.LENGTH_SHORT).show();

                }
            });


            // long surveyid = survey.save();

            sessionManager.setSurveyId(sessionManager.getSurveyId() + 1);

            // this.surveyId = String.valueOf(surveyid);
            update = false;
        }
        // questionsList = MQuestions.find(MQuestions.class, "surveyid = ?", surveyId);


        List<Options> opt = new ArrayList<>();
        for (int i = 0; i < questionsList.size(); i++) {
            //     opt = questionsList.get(i).getOptions(String.valueOf(questionsList.get(i).getId()));
        }

        mAdapter = new QuestionsAdapter(this, questionsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    public void onBtnClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_add_question:

                Intent i = new Intent(this, AddQuestionActivity.class);
                i.putExtra(SURVEYID, surveyId);
                startActivity(i);
                break;
        }

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
            case R.id.action_save_survey:

                if (validate.validateString(txt_survey_name.getText().toString())) {
                    txt_survey_name.setError("Enter Username");
                    break;
                } else {
                    txt_survey_name.setError(null);
                }

                //   MSurvey surveyName = MSurvey.findById(MSurvey.class, Long.parseLong(surveyId));
                //    surveyName.setName(txt_survey_name.getText().toString());
                //    surveyName.save();

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // save a survey
                        //survey = realm.where(MSurvey.class).equalTo("id", surveyId).findFirst();
                        survey.setName(txt_survey_name.getText().toString());

                        survey.setNested(cbNestedSurvey.isChecked());
                        /*RealmList que = new RealmList();
                        for (int i = 0; i < 3; i++) {

                            int questionid;
                            try {
                                questionid = realm.where(MQuestions.class).max("id").intValue() + 1;
                            } catch (Exception ex) {
                                Log.v("exception", ex.toString());
                                questionid = 1;
                            }

                            MQuestions questions = realm.createObject(MQuestions.class, questionid);
                            //questions.setId(i);
                            questions.setQuestion("question"+i+"?");
                            questions.setOpt(true);
                            questions.setText(true);
                            RealmList<MOptions> opt = new RealmList();
                            for (int j = 0; j < 2; j++) {
                                int optionsId;
                                try {
                                    optionsId = realm.where(MOptions.class).max("id").intValue() + 1;
                                } catch (Exception ex) {
                                    Log.v("exception", ex.toString());
                                    optionsId = 1;
                                }


                                MOptions opt1 = realm.createObject(MOptions.class, optionsId);
                                //opt1.setId(j);
                                opt1.setOpt("asdf" + j);
                                opt.add(opt1);
                            }
                            questions.setOptions(opt);

                            que.add(questions);
                        }


                        survey.setMQuestions(que);*/
                        realm.copyToRealmOrUpdate(survey);
                        //Toast.makeText(getApplicationContext(), "MUser Added Successfully !", Toast.LENGTH_SHORT).show();
                    }
                });


                finish();

                break;


            case R.id.action_delete_survey:
                new AlertDialog.Builder(this)
                        .setTitle("Delete")
                        .setMessage("Would you like to delete this survey and MQuestions?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //       MSurvey survey = MSurvey.findById(MSurvey.class, Long.parseLong(surveyId));
                                //        survey.delete();

                                //       List<MQuestions> que = MQuestions.find(MQuestions.class, "surveyid = ?", surveyId);

                                //       for (int i = 0; i < que.size(); i++) {
                                //           List<MOptions> op = MOptions.find(MOptions.class, "questionid=?", String.valueOf(que.get(i).getId()));

                                //          for (int j = 0; j < op.size(); j++) {
                                //              op.get(i).delete();
                                //         }
                                //         que.get(i).delete();
                                //     }
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        // save a survey
                                        survey.deleteFromRealm();
                                        //Toast.makeText(getApplicationContext(), "MUser Added Successfully !", Toast.LENGTH_SHORT).show();
                                    }
                                });


                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // user doesn't want to logout
                            }
                        })
                        .show();

                break;

            case android.R.id.home:
                SurveyActivity.mAdapter.notifyDataSetChanged();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        questionsList.clear();

        survey = realm.where(Survey.class).equalTo("id", surveyId).findFirst();
        //questionsList.clear();
        questionsList.addAll(survey.getQuestions());

        for(int i=0; i<survey.getQuestions().size(); i++) {
            Log.v("asfd", "???????" + survey.getQuestions().get(i).getQuestion()+" size"+survey.getQuestions().get(i).getOptions().size());
            for(int j=0; j<survey.getQuestions().get(i).getOptions().size(); j++){
                Log.v("asfd", "//" + survey.getQuestions().get(i).getOptions().get(j).getOpt());
            }

        }

        //  questionsList.addAll(MQuestions.find(MQuestions.class, "surveyid = ?", surveyId));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
