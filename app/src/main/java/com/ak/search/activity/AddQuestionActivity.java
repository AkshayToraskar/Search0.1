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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.ak.search.R;
import com.ak.search.app.Validate;
import com.ak.search.realm_model.ConditionalOptions;
import com.ak.search.realm_model.Options;
import com.ak.search.realm_model.Questions;
import com.ak.search.realm_model.Survey;

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

    @BindView(R.id.txt_opt1)
    EditText et_opt1;
    @BindView(R.id.txt_opt2)
    EditText et_opt2;
    @BindView(R.id.ll_option)
    LinearLayout ll_option;
    @BindView(R.id.btn_add_more_option)
    Button btn_add_more_option;

    @BindView(R.id.txt_chk1)
    EditText et_chk1;
    @BindView(R.id.txt_chk2)
    EditText et_chk2;
    @BindView(R.id.ll_check)
    LinearLayout ll_check;
    @BindView(R.id.btn_add_more_checkbox)
    Button btn_add_more_checkbox;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.cb_options)
    CheckBox cb_option;
    @BindView(R.id.cb_text)
    CheckBox cb_text;
    @BindView(R.id.cb_number)
    CheckBox cb_number;
    @BindView(R.id.cb_date)
    CheckBox cb_date;
    @BindView(R.id.cb_time)
    CheckBox cb_time;
    @BindView(R.id.cb_picture)
    CheckBox cb_picture;
    @BindView(R.id.cb_compulsary)
    CheckBox cb_compulsary;
    @BindView(R.id.cb_checkbox)
    CheckBox cb_checkbox;
    @BindView(R.id.cb_options_condition)
    CheckBox cb_options_condition;

    @BindView(R.id.txt_opt1_conditional)
    EditText et_opt1_conditional;
    @BindView(R.id.txt_opt2_conditional)
    EditText et_opt2_conditional;
    @BindView(R.id.ll_option_condition)
    LinearLayout ll_option_conditional;
    @BindView(R.id.btn_add_more_option_conditional)
    Button btn_add_more_option_conditional;
    @BindView(R.id.spn_op1)
    Spinner spnOp1;
    @BindView(R.id.spn_op2)
    Spinner spnOp2;


    List<EditText> allEds, allEdsChk, allEdsOptConditional;
    List<Spinner> allSpnCondition;
    public Questions questions;
    public List<Options> option, checkboxOpt;
    public List<ConditionalOptions> conditionalOptions;

    public boolean update;

    String[] sur;
    List<Survey> surveys;

    long questionsId;
    Realm realm;
    RealmList<Questions> lstQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);


        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        realm = Realm.getDefaultInstance();
        validate = new Validate();

        allEds = new ArrayList<EditText>();
        allEdsChk = new ArrayList<>();
        allEdsOptConditional = new ArrayList<>();
        allSpnCondition = new ArrayList<>();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        questions = new Questions();
        allEds.add(et_opt1);
        allEds.add(et_opt2);


        allEdsChk.add(et_chk1);
        allEdsChk.add(et_chk2);

        allEdsOptConditional.add(et_opt1_conditional);
        allEdsOptConditional.add(et_opt2_conditional);
        allSpnCondition.add(spnOp1);
        allSpnCondition.add(spnOp2);

        lstQuestion = new RealmList<Questions>();


        surveys = realm.where(Survey.class).findAll();
        sur = new String[surveys.size()];

        for (int i = 0; i < surveys.size(); i++) {
            sur[i] = surveys.get(i).getName();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.
                R.layout.simple_spinner_dropdown_item, sur);
        spnOp1.setAdapter(adapter);
        spnOp2.setAdapter(adapter);


        if (getIntent().getExtras() != null) {
            //surveyid = getIntent().getExtras().getLong(AddSurveyActivity.SURVEYID);
            questionsId = getIntent().getExtras().getLong("questionId", 0);

            Log.v("Question Id", "" + questionsId);


            if (questionsId != 0) {
                // questions = MQuestions.findById(MQuestions.class, Long.parseLong(questionsId));

                questions = realm.where(Questions.class).equalTo("id", questionsId).findFirst();


                cb_option.setChecked(questions.getOpt());
                cb_text.setChecked(questions.getText());
                cb_number.setChecked(questions.getNumber());
                cb_date.setChecked(questions.getDate());
                cb_time.setChecked(questions.getTime());
                cb_picture.setChecked(questions.getImage());
                cb_compulsary.setChecked(questions.getCompulsary());
                cb_checkbox.setChecked(questions.getCheckbox());
                cb_options_condition.setChecked(questions.getOptCondition());
                showOption(questions.getOpt());
                showCheckbox(questions.getCheckbox());
                showOptionConditional(questions.getOptCondition());
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

                checkboxOpt = questions.getChkb();
                /*if (option.size() > 0)
                    et_opt1.setText(option.get(0).getOpt());
                if (option.size() > 1)
                    et_opt2.setText(option.get(1).getOpt());
                if (option.size() > 2) {*/
                for (int i = 0; i < checkboxOpt.size(); i++) {
                    if (i == 0) {
                        et_chk1.setText(checkboxOpt.get(i).getOpt());
                    } else if (i == 1) {
                        et_chk2.setText(checkboxOpt.get(i).getOpt());
                    } else {
                        EditText text = new EditText(this);
                        text.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        text.setText(checkboxOpt.get(i).getOpt());
                        ll_check.addView(text);
                        allEdsChk.add(text);
                    }

                }


                conditionalOptions=questions.getOptionContidion();
                for(int i=0; i<conditionalOptions.size(); i++){
                    if (i == 0) {
                        et_opt1_conditional.setText(conditionalOptions.get(i).getOpt());
                        spnOp1.setSelection(getSurveyPosition(conditionalOptions.get(i).getSurveyid()));
                    } else if (i == 1) {
                        et_opt2_conditional.setText(conditionalOptions.get(i).getOpt());
                        spnOp2.setSelection(getSurveyPosition(conditionalOptions.get(i).getSurveyid()));
                    } else {
                        LinearLayout ll = new LinearLayout(this);
                        ll.setWeightSum(7);
                        EditText text2 = new EditText(this);
                        text2.setLayoutParams(new LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.WRAP_CONTENT, 4));
                        text2.setText(conditionalOptions.get(i).getOpt());
                        Spinner spn = new Spinner(this);
                        spn.setLayoutParams(new LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.WRAP_CONTENT, 3));

                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.
                                R.layout.simple_spinner_dropdown_item, sur);
                        spn.setAdapter(adapter1);
                        spn.setSelection(getSurveyPosition(conditionalOptions.get(i).getSurveyid()));
                        ll.addView(text2);
                        ll.addView(spn);
                        ll_option_conditional.addView(ll);
                        allEdsOptConditional.add(text2);
                        allSpnCondition.add(spn);
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

        cb_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showCheckbox(cb_checkbox.isChecked());
            }
        });

        cb_options_condition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showOptionConditional(b);
            }
        });


    }

    public int getSurveyPosition(long surveyId){
        int pos=0;

        for(int i=0; i<surveys.size(); i++){
            if(surveys.get(i).getId()==surveyId){
                pos=i;
                break;
            }
        }

        return pos;
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
                    txt_question.setError("Enter Question");
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
                            MOptions op = new MOptions();
                            //    op.setQuestionid(String.valueOf(questions.getId()));
                            op.setOpt(allEds.get(j).getText().toString());

                            option.add(op);
                        }
                        // }
                    }*/
                    //   MOptions.updateInTx(option);

                    //questions.setOptions(options);


                    //questions.save();
                    //AddSurveyActivity.mAdapter.notifyDataSetChanged();
                    // finish();

                } else {


                    //  final MQuestions questions = new MQuestions();


                    saveUpdateQuestions();


                    //   MOptions.saveInTx(options);

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
                        .setMessage("Would you like to delete this MQuestions?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //                    MQuestions que = MQuestions.findById(MQuestions.class, Long.parseLong(questionsId));
                                //                   que.delete();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        // save a survey
                                        questions.deleteFromRealm();
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

            case R.id.btn_add_more_checkbox:
                EditText text1 = new EditText(this);
                text1.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                text1.setHint("option");
                ll_check.addView(text1);
                allEdsChk.add(text1);
                break;

            case R.id.btn_add_more_option_conditional:

                LinearLayout ll = new LinearLayout(this);
                ll.setWeightSum(7);
                EditText text2 = new EditText(this);
                text2.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 4));
                text2.setHint("option");
                Spinner spn = new Spinner(this);
                spn.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 3));

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.
                        R.layout.simple_spinner_dropdown_item, sur);
                spn.setAdapter(adapter);

                ll.addView(text2);
                ll.addView(spn);
                ll_option_conditional.addView(ll);
                allEdsOptConditional.add(text2);
                allSpnCondition.add(spn);

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


    public void showCheckbox(boolean val) {
        if (val) {
            ll_check.setVisibility(View.VISIBLE);
            btn_add_more_checkbox.setVisibility(View.VISIBLE);
        } else {
            ll_check.setVisibility(View.GONE);
            btn_add_more_checkbox.setVisibility(View.GONE);
        }
    }

    public void showOptionConditional(boolean val) {
        if (val) {
            ll_option_conditional.setVisibility(View.VISIBLE);
            btn_add_more_option_conditional.setVisibility(View.VISIBLE);
        } else {
            ll_option_conditional.setVisibility(View.GONE);
            btn_add_more_option_conditional.setVisibility(View.GONE);
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

                //long id = realm.where(MUser.class).max("id").longValue() + 1;


                // questions.setSurveyid(surveyid);
                questions.setQuestion(txt_question.getText().toString());
                questions.setText(cb_text.isChecked());
                questions.setNumber(cb_number.isChecked());
                questions.setDate(cb_date.isChecked());
                questions.setTime(cb_time.isChecked());
                questions.setImage(cb_picture.isChecked());
                questions.setCompulsary(cb_compulsary.isChecked());
                questions.setCheckbox(cb_checkbox.isChecked());
                questions.setOpt(cb_option.isChecked());
                questions.setOptCondition(cb_options_condition.isChecked());

                RealmList<Options> options = new RealmList<>();
                if (questions.getOptions() != null) {
                    questions.getOptions().clear();
                }


                RealmList<Options> chk = new RealmList<>();
                if (questions.getChkb() != null) {
                    questions.getChkb().clear();
                }

                RealmList<ConditionalOptions> conditionalOptionses = new RealmList<ConditionalOptions>();
                if (questions.getOptionContidion() != null) {
                    questions.getOptionContidion().clear();
                }

                //MOptions opt1 = new MOptions();
                // opt2 = new MOptions();

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


                            //MOptions op = new MOptions();
                            //           op.setQuestionid(String.valueOf(questionid));
                            //            op.setOpt(allEds.get(i).getText().toString());
                            //options.add(op);
                        }
                    }
                }
                questions.setOptions(options);


                if (cb_checkbox.isChecked()) {
                    if (allEdsChk.size() >= 0) {
                        for (int i = 0; i < allEdsChk.size(); i++) {
                            int optionsId;
                            try {
                                optionsId = realm.where(Options.class).max("id").intValue() + 1;
                            } catch (Exception ex) {
                                Log.v("exception", ex.toString());
                                optionsId = 1;
                            }


                            Options opt1 = realm.createObject(Options.class, optionsId);
                            //opt1.setId(j);
                            opt1.setOpt(allEdsChk.get(i).getText().toString());
                            chk.add(opt1);


                            //MOptions op = new MOptions();
                            //           op.setQuestionid(String.valueOf(questionid));
                            //            op.setOpt(allEds.get(i).getText().toString());
                            //options.add(op);
                        }
                    }
                }
                questions.setChkb(chk);

                if (cb_options_condition.isChecked()) {
                    if (allEdsOptConditional.size() >= 0) {
                        for (int i = 0; i < allEdsOptConditional.size(); i++) {
                            int optionsId;
                            try {
                                optionsId = realm.where(ConditionalOptions.class).max("id").intValue() + 1;
                            } catch (Exception ex) {
                                Log.v("exception", ex.toString());
                                optionsId = 1;
                            }


                            long surveyId = surveys.get(allSpnCondition.get(i).getSelectedItemPosition()).getId();

                            ConditionalOptions opt1 = realm.createObject(ConditionalOptions.class, optionsId);
                            //opt1.setId(j);
                            opt1.setOpt(allEdsOptConditional.get(i).getText().toString());
                            opt1.setSurveyid(surveyId);
                            conditionalOptionses.add(opt1);
                        }
                    }
                }
                questions.setOptionContidion(conditionalOptionses);


                if (update) {
                    realm.copyToRealmOrUpdate(questions);
                } else {
                    lstQuestion.add(questions);
                    AddSurveyActivity.survey.setQuestions(lstQuestion);
                    realm.copyToRealmOrUpdate(AddSurveyActivity.survey);
                }
                //Toast.makeText(getApplicationContext(), "MUser Added Successfully !", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
