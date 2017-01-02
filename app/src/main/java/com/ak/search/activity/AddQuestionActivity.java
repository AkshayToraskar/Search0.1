package com.ak.search.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ak.search.R;
import com.ak.search.app.Validate;
import com.ak.search.model.Options;
import com.ak.search.model.Questions;
import com.ak.search.model.Survey;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;

public class AddQuestionActivity extends AppCompatActivity {
    Validate validate;
    @BindView(R.id.txt_question)
    EditText txt_question;

    @BindView(R.id.cb_options)
    CheckBox cb_option;

    @BindView(R.id.cb_text)
    CheckBox cb_text;

    @BindView(R.id.txt_opt1)
    EditText et_opt1;

    @BindView(R.id.txt_opt2)
    EditText et_opt2;

    @BindView(R.id.ll_option)
    LinearLayout ll_option;

    @BindView(R.id.btn_add_more_option)
    Button btn_add_more_option;

    List<EditText> allEds;
    public Questions questions;
    public List<Options> option;

    public boolean update;


    long questionsId;
    Realm realm;
    RealmList<Questions> lstQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        validate = new Validate();

        allEds = new ArrayList<EditText>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        questions = new Questions();
        allEds.add(et_opt1);
        allEds.add(et_opt2);
        lstQuestion = new RealmList<Questions>();


        if (getIntent().getExtras() != null) {
            //surveyid = getIntent().getExtras().getLong(AddSurveyActivity.SURVEYID);
            questionsId = getIntent().getExtras().getLong("questionId", 0);

            Log.v("Question Id", "" + questionsId);


            if (questionsId != 0) {
                // questions = Questions.findById(Questions.class, Long.parseLong(questionsId));

                questions = realm.where(Questions.class).equalTo("id", questionsId).findFirst();


                cb_option.setChecked(questions.getOpt());
                cb_text.setChecked(questions.getText());
                showOption(questions.getOpt());
                txt_question.setText(questions.getQuestion());

                option = questions.getOptions();


                /*if (option.size() > 0)
                    et_opt1.setText(option.get(0).getOpt());
                if (option.size() > 1)
                    et_opt2.setText(option.get(1).getOpt());
                if (option.size() > 2) {*/
                for (int i = 0; i < option.size(); i++) {
                    if (i == 0) {
                        et_opt1.setText(option.get(i).getOpt());
                    } else if (i == 1) {
                        et_opt2.setText(option.get(i).getOpt());
                    } else {
                        EditText text = new EditText(this);
                        text.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        text.setText(option.get(i).getOpt());
                        ll_option.addView(text);
                        allEds.add(text);
                    }

                }
                //}

                update = true;


            } else {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        lstQuestion.addAll(AddSurveyActivity.survey.getQuestions());
                        int questionid;
                        try {
                            questionid = realm.where(Questions.class).max("id").intValue() + 1;
                        } catch (Exception ex) {
                            Log.v("exception", ex.toString());
                            questionid = 1;
                        }
                        questions = realm.createObject(Questions.class, questionid);

                        update = false;

                    }
                });

            }

        }


       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        cb_option.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                showOption(cb_option.isChecked());
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_question, menu);
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
            case R.id.action_save_question:
                if (validate.validateString(txt_question.getText().toString())) {
                    txt_question.setError("Enter Username");
                    break;
                } else {
                    txt_question.setError(null);
                }

                if (update) {


                    saveUpdateQuestions();


                    // questions.setSurveyid(surveyid);
                    /*questions.setQuestion(txt_question.getText().toString());
                    questions.setText(cb_text.isChecked());
                    questions.setOpt(cb_option.isChecked());


                    for (int i = 0; i < option.size(); i++) {


                        // option.get(i).setQuestionid(String.valueOf(questions.getId()));


                        *//*if (i == 0) {
                            option.get(i).setOpt(et_opt1.getText().toString());
                        } else if (i == 1) {
                            option.get(i).setOpt(et_opt2.getText().toString());
                        } else if (allEds.size() > i) {

                            for (int j = 0; j < allEds.size(); j++) {
                                option.get(i).setOpt(allEds.get(j).getText().toString());
                            }
                        } else {*//*
                        for (int j = option.size(); j < allEds.size(); j++) {
                            Options op = new Options();
                            //    op.setQuestionid(String.valueOf(questions.getId()));
                            op.setOpt(allEds.get(j).getText().toString());

                            option.add(op);
                        }
                        // }
                    }*/
                    //   Options.updateInTx(option);

                    //questions.setOptions(options);


                    //questions.save();
                    //AddSurveyActivity.mAdapter.notifyDataSetChanged();
                    // finish();

                } else {


                    //  final Questions questions = new Questions();


                    saveUpdateQuestions();


                    //   Options.saveInTx(options);

                    //questions.setOptions(options);


                    //questions.save();
                    //AddSurveyActivity.mAdapter.notifyDataSetChanged();
                    //finish();

                }

                finish();

                break;


            case R.id.action_delete_question:
                new AlertDialog.Builder(this)
                        .setTitle("Delete")
                        .setMessage("Would you like to delete this Questions?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //                    Questions que = Questions.findById(Questions.class, Long.parseLong(questionsId));
                                //                   que.delete();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        // save a survey
                                        questions.deleteFromRealm();
                                        //Toast.makeText(getApplicationContext(), "User Added Successfully !", Toast.LENGTH_SHORT).show();
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
                AddSurveyActivity.mAdapter.notifyDataSetChanged();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBtnClick(View view) {
        int id = view.getId();

        switch (id) {

            case R.id.btn_add_more_option:
                EditText text = new EditText(this);
                text.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                text.setHint("option");
                ll_option.addView(text);
                allEds.add(text);
                break;
        }
    }

    public void showOption(boolean val) {
        if (val) {
            ll_option.setVisibility(View.VISIBLE);
            btn_add_more_option.setVisibility(View.VISIBLE);
        } else {
            ll_option.setVisibility(View.GONE);
            btn_add_more_option.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AddSurveyActivity.mAdapter.notifyDataSetChanged();
        finish();
    }


    public void saveUpdateQuestions() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                //long id = realm.where(User.class).max("id").longValue() + 1;


                // questions.setSurveyid(surveyid);
                questions.setQuestion(txt_question.getText().toString());
                questions.setText(cb_text.isChecked());
                questions.setOpt(cb_option.isChecked());

                RealmList<Options> options = new RealmList<>();
                if (questions.getOptions() != null) {
                    questions.getOptions().clear();
                }


                //Options opt1 = new Options();
                // opt2 = new Options();

                //      long questionid = questions.save();
                //   opt1.setQuestionid(String.valueOf(questionid));
                ///    opt1.setOpt(et_opt1.getText().toString());
                //    opt2.setQuestionid(String.valueOf(questionid));
                //    opt2.setOpt(et_opt2.getText().toString());

                //options.add(opt1);
                //options.add(opt2);
                if (cb_option.isChecked()) {
                    if (allEds.size() >= 0) {
                        for (int i = 0; i < allEds.size(); i++) {
                            int optionsId;
                            try {
                                optionsId = realm.where(Options.class).max("id").intValue() + 1;
                            } catch (Exception ex) {
                                Log.v("exception", ex.toString());
                                optionsId = 1;
                            }


                            Options opt1 = realm.createObject(Options.class, optionsId);
                            //opt1.setId(j);
                            opt1.setOpt(allEds.get(i).getText().toString());
                            options.add(opt1);


                            //Options op = new Options();
                            //           op.setQuestionid(String.valueOf(questionid));
                            //            op.setOpt(allEds.get(i).getText().toString());
                            //options.add(op);
                        }
                    }
                }
                questions.setOptions(options);

                if (update) {
                    realm.copyToRealmOrUpdate(questions);
                } else {
                    lstQuestion.add(questions);
                    AddSurveyActivity.survey.setQuestions(lstQuestion);
                    realm.copyToRealmOrUpdate(AddSurveyActivity.survey);
                }
                //Toast.makeText(getApplicationContext(), "User Added Successfully !", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
