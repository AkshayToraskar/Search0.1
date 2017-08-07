package com.ak.search.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.adapter.QuestionsAdapter;
import com.ak.search.app.OnStartDragListener;
import com.ak.search.app.SessionManager;
import com.ak.search.app.SimpleItemTouchHelperCallback;
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
import io.realm.Sort;

public class AddSurveyActivity extends AppCompatActivity implements OnStartDragListener {

    @BindView(R.id.txt_survey_name)
    EditText txt_survey_name;
    @BindView(R.id.cb_nested_survey)
    CheckBox cbNestedSurvey;

    @BindView(R.id.rv_questions)
    RecyclerView recyclerView;
    public static QuestionsAdapter mAdapter;

    @BindView(R.id.btn_add_question)
    FloatingActionButton fabAddQuestion;

    @BindView(R.id.view_divider)
    View view;

    Validate validate;

    public static String SURVEYID = "surveyid";
    long surveyId;
    String surveyName;
    public boolean update;
    SessionManager sessionManager;
    Realm realm;

    public static Survey survey;
    public RealmList<Questions> questionsList;

    ItemTouchHelper touchHelper;
    OnStartDragListener onStartDragListener;

    public static boolean arrange = false;

    Menu menus = null;

    public boolean isNestead = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_survey);
        ButterKnife.bind(this);
        validate = new Validate();
        realm = Realm.getDefaultInstance();

        sessionManager = new SessionManager(this);
        onStartDragListener = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        questionsList = new RealmList<>();

        if (getIntent().getExtras() != null) {

            this.surveyId = getIntent().getExtras().getLong("surveyId");
            this.surveyName = getIntent().getExtras().getString("surveyName");
            this.isNestead = getIntent().getExtras().getBoolean("isNestead");


            txt_survey_name.setText(surveyName + " ");


            survey = realm.where(Survey.class).equalTo("id", surveyId).findFirst();
            if (survey != null) {
                Log.v("SURVEY NAME", "dasf " + survey.getName() + "=====" + survey.getQuestions().size());

                cbNestedSurvey.setChecked(survey.getNested());
                questionsList.clear();
                questionsList.addAll(survey.getQuestions().sort("question_pos", Sort.ASCENDING));

                update = true;
            } else {

                txt_survey_name.setText("Survey " + String.valueOf(sessionManager.getSurveyId()));

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
                        survey.setName("Survey " + String.valueOf(sessionManager.getSurveyId()));
                        realm.copyToRealmOrUpdate(survey);
                        //Toast.makeText(getApplicationContext(), "MUser Added Successfully !", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            // long surveyid = survey.save();

            sessionManager.setSurveyId(sessionManager.getSurveyId() + 1);

            // this.surveyId = String.valueOf(surveyid);
            update = false;
        }

        if (isNestead) {
            txt_survey_name.setVisibility(View.GONE);
            cbNestedSurvey.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }
        // questionsList = MQuestions.find(MQuestions.class, "surveyid = ?", surveyId);


        List<Options> opt = new ArrayList<>();
        for (int i = 0; i < questionsList.size(); i++) {
            //     opt = questionsList.get(i).getOptions(String.valueOf(questionsList.get(i).getId()));
        }

        mAdapter = new QuestionsAdapter(this, questionsList, onStartDragListener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

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
        menus = menu;
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
                        .setMessage("Would you like to delete this Survey and Questions?")
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

            case R.id.action_arrange:

                if (arrange) {
                    menus.getItem(0).setVisible(true);
                    menus.getItem(1).setVisible(true);
                    menus.getItem(3).setVisible(true);

                    menus.getItem(2).setShowAsAction(0);

                    item.setTitle("Arrange");
                    arrange = false;
                    fabAddQuestion.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                } else {
                    menus.getItem(0).setVisible(false);
                    menus.getItem(1).setVisible(false);
                    menus.getItem(3).setVisible(false);

                    menus.getItem(2).setShowAsAction(1);

                    item.setTitle("Done");
                    arrange = true;
                    fabAddQuestion.animate().translationY(fabAddQuestion.getHeight() + 50).setInterpolator(new AccelerateInterpolator(2)).start();
                }
                mAdapter.notifyDataSetChanged();
                break;

            case R.id.action_duplicate:
                createDublicateSurvey();
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
        questionsList.addAll(survey.getQuestions().sort("question_pos", Sort.ASCENDING));

        for (int i = 0; i < survey.getQuestions().size(); i++) {
            Log.v("asfd", "???????" + survey.getQuestions().get(i).getQuestion() + " size" + survey.getQuestions().get(i).getOptions().size());
            for (int j = 0; j < survey.getQuestions().get(i).getOptions().size(); j++) {
                Log.v("asfd", "//" + survey.getQuestions().get(i).getOptions().get(j).getOpt());
            }

        }

        //  questionsList.addAll(MQuestions.find(MQuestions.class, "surveyid = ?", surveyId));
        mAdapter.notifyDataSetChanged();
    }

    public void createDublicateSurvey() {

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
                Survey dub_survey = realm.createObject(Survey.class, surveyId);
                //survey.setId(surveyId);
                dub_survey.setNested(survey.getNested());
                dub_survey.setName("copy_" + survey.getName());

                RealmList<Questions> dub_question_list = new RealmList<Questions>();


                for (int i = 0; i < survey.getQuestions().size(); i++) {

                    int questionId;

                    try {
                        questionId = realm.where(Questions.class).max("id").intValue() + 1;
                    } catch (Exception ex) {
                        Log.v("exception", ex.toString());
                        questionId = 1;
                    }
                    Questions questions = realm.createObject(Questions.class, questionId);

                    questions.setQuestion(survey.getQuestions().get(i).getQuestion());
                    questions.setTypeQuestion(survey.getQuestions().get(i).getTypeQuestion());

                    questions.setOptions(survey.getQuestions().get(i).getOptions());
                    questions.setChkb(survey.getQuestions().get(i).getChkb());
                    questions.setOptionContidion(survey.getQuestions().get(i).getOptionContidion());

                    questions.setText(survey.getQuestions().get(i).getText());
                    questions.setNumber(survey.getQuestions().get(i).getNumber());
                    questions.setDate(survey.getQuestions().get(i).getDate());
                    questions.setTime(survey.getQuestions().get(i).getTime());
                    questions.setImage(survey.getQuestions().get(i).getImage());
                    questions.setCompulsary(survey.getQuestions().get(i).getCompulsary());
                    questions.setOpt(survey.getQuestions().get(i).getOpt());
                    questions.setCheckbox(survey.getQuestions().get(i).getCheckbox());
                    questions.setOptCondition(survey.getQuestions().get(i).getOptCondition());
                    questions.setPatientName(survey.getQuestions().get(i).getPatientName());


                    dub_question_list.add(questions);


                }

                dub_survey.setQuestions(dub_question_list);

                realm.copyToRealmOrUpdate(dub_survey);
                Toast.makeText(getApplicationContext(), "Survey Replicated Successfully !", Toast.LENGTH_SHORT).show();

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

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }
}
