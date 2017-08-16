package com.ak.search.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

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
    @BindView(R.id.cb_patient_name)
    CheckBox cb_patient_name;

    @BindView(R.id.txt_opt1_conditional)
    EditText et_opt1_conditional;
    @BindView(R.id.txt_opt2_conditional)
    EditText et_opt2_conditional;
    @BindView(R.id.ll_option_condition)
    LinearLayout ll_option_conditional;
    @BindView(R.id.btn_add_more_option_conditional)
    Button btn_add_more_option_conditional;


    @BindView(R.id.btn_cond1)
    Button btnCond1;
    @BindView(R.id.btn_cond2)
    Button btnCond2;


    String typeQuestion;


    List<EditText> allEds, allEdsChk, allEdsOptConditional;

    List<Button> allBtnCondition;
    List<Long> nesSurveyId;
    public Questions questions;
    public List<Options> option, checkboxOpt;
    public List<ConditionalOptions> conditionalOptions;

    public boolean update;


    Survey survey;
    long questionsId;
    long surveyId;
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

        allBtnCondition = new ArrayList<>();
        nesSurveyId = new ArrayList<>();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        questions = new Questions();
        allEds.add(et_opt1);
        allEds.add(et_opt2);

        removeTextFieldListener(et_opt1);
        removeTextFieldListener(et_opt2);
        removeTextFieldListener(et_chk1);
        removeTextFieldListener(et_chk2);
        removeTextFieldListener(et_opt1_conditional);
        removeTextFieldListener(et_opt2_conditional);

        allEdsChk.add(et_chk1);
        allEdsChk.add(et_chk2);

        allEdsOptConditional.add(et_opt1_conditional);
        allEdsOptConditional.add(et_opt2_conditional);

        allBtnCondition.add(btnCond1);
        allBtnCondition.add(btnCond2);

        nesSurveyId.add((long) 0);
        nesSurveyId.add((long) 0);

        addNesteadSurvey(btnCond1);
        addNesteadSurvey(btnCond2);


        lstQuestion = new RealmList<Questions>();

        option = new ArrayList<>();
        checkboxOpt = new ArrayList<>();
        conditionalOptions = new ArrayList<>();


        if (getIntent().getExtras() != null) {
            surveyId = getIntent().getExtras().getLong("surveyid");
            questionsId = getIntent().getExtras().getLong("questionId", 0);

            survey = realm.where(Survey.class).equalTo("id", surveyId).findFirst();

            Log.v("Question Id", "" + questionsId);

            setupData();

        }


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

                validateAndSave();
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
                validateAndSave();
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
                text.setCompoundDrawablePadding(5);
                text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_circle_outline_black_24dp, 0);
                ll_option.addView(text);
                allEds.add(text);

                removeTextFieldListener(text);
                break;

            case R.id.btn_add_more_checkbox:
                EditText text1 = new EditText(this);
                text1.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                text1.setCompoundDrawablePadding(5);
                text1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_circle_outline_black_24dp, 0);
                text1.setHint("option");
                ll_check.addView(text1);
                allEdsChk.add(text1);

                removeTextFieldListener(text1);
                break;

            case R.id.btn_add_more_option_conditional:

                LinearLayout ll = new LinearLayout(this);
                ll.setWeightSum(7);
                EditText text2 = new EditText(this);
                text2.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 4));
                text2.setCompoundDrawablePadding(5);
                text2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_circle_outline_black_24dp, 0);
                text2.setHint("option");
                //  Spinner spn = new Spinner(this);
                /// spn.setLayoutParams(new LinearLayout.LayoutParams(
                //           0,
                //         ViewGroup.LayoutParams.WRAP_CONTENT, 3));

                // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.
                //           R.layout.simple_spinner_dropdown_item, sur);
                //   spn.setAdapter(adapter);

                Button btn = new Button(this);
                btn.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 0));
                btn.setText("Add");

                ll.addView(text2);
                //     ll.addView(spn);
                ll.addView(btn);

                ll_option_conditional.addView(ll);
                allEdsOptConditional.add(text2);
                nesSurveyId.add((long) 0);
                //    allSpnCondition.add(spn);

                removeTextFieldListener(text2);
                addNesteadSurvey(btn);

                break;
        }
    }


    public void validateAndSave() {
        if (validate.validateString(txt_question.getText().toString())) {
            txt_question.setError("Enter Question");
            return;
        } else {
            txt_question.setError(null);
        }

        typeQuestion = "";

        if (cb_text.isChecked()) {
            typeQuestion = typeQuestion + "1,";
        }
        if (cb_number.isChecked()) {
            typeQuestion = typeQuestion + "2,";
        }
        if (cb_date.isChecked()) {
            typeQuestion = typeQuestion + "3,";
        }
        if (cb_time.isChecked()) {
            typeQuestion = typeQuestion + "4,";
        }
        if (cb_picture.isChecked()) {
            typeQuestion = typeQuestion + "5,";
        }
        if (cb_patient_name.isChecked()) {
            typeQuestion = typeQuestion + "6,";
        }
        if (cb_checkbox.isChecked()) {
            typeQuestion = typeQuestion + "7,";
        }
        if (cb_option.isChecked()) {
            typeQuestion = typeQuestion + "8,";
        }
        if (cb_options_condition.isChecked()) {
            typeQuestion = typeQuestion + "9,";
        }

        if (typeQuestion.equals("")) {
            Toast.makeText(getApplicationContext(), "Select QuestionType", Toast.LENGTH_SHORT).show();
            return;
        }


        saveUpdateQuestions();

    }

    public void removeTextFieldListener(final EditText et) {
        et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (et.getRight() - et.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        if (cb_option.isChecked()) {
                            for (int i = 0; i < allEds.size(); i++) {
                                EditText et1 = allEds.get(i);
                                if (et.getId() == et1.getId()) {
                                    ll_option.removeView(et1);
                                    allEds.remove(et1);
                                }
                            }
                        }
                        if (cb_checkbox.isChecked()) {
                            for (int i = 0; i < allEdsChk.size(); i++) {
                                EditText et1 = allEdsChk.get(i);
                                if (et.getId() == et1.getId()) {
                                    ll_check.removeView(et1);
                                    allEdsChk.remove(et1);
                                }
                            }
                        }
                        if (cb_options_condition.isChecked()) {
                            for (int i = 0; i < allEdsOptConditional.size(); i++) {

                                EditText et1 = allEdsOptConditional.get(i);
                                if (et.getId() == et1.getId()) {
                                    //  Spinner spn = allSpnCondition.get(i);
                                    Button btn = allBtnCondition.get(i);
                                    ll_option_conditional.removeViewAt(i);
                                    ll_option_conditional.removeView(btn);
                                    //ll_option_conditional.removeView(spn);

                                    allEdsOptConditional.remove(et1);
                                    allBtnCondition.remove(btn);
                                    nesSurveyId.remove(i);
                                    // allSpnCondition.remove(spn);
                                }
                            }
                        }


                        return true;
                    }
                }
                return false;
            }
        });
    }

    long survId = 1;

    public void addNesteadSurvey(final Button btn) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < allBtnCondition.size(); i++) {
                    if (btn.getId() == allBtnCondition.get(i).getId()) {

                       validateAndSave();

                        questions = realm.where(Questions.class).equalTo("id", questionsId).findFirst();
                        //conditionalOptions.clear();
                        conditionalOptions = questions.getOptionContidion();

                        if (conditionalOptions.get(i).getSurveyid() != 0) {
                            nesSurveyId.set(i, conditionalOptions.get(i).getSurveyid());
                            survId = conditionalOptions.get(i).getSurveyid();
                        } else {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {

                                    try {
                                        survId = realm.where(Survey.class).max("id").intValue() + 1;
                                    } catch (Exception ex) {
                                        Log.v("exception", ex.toString());
                                        survId = 1;
                                    }

                                    // Add a survey
                                    Survey survey = realm.createObject(Survey.class, survId);
                                    survey.setNested(true);
                                    survey.setName("Survey " + survId);
                                    realm.copyToRealmOrUpdate(survey);
                                }
                            });

                            nesSurveyId.set(i, survId);

                        }

                        Intent intent = new Intent(AddQuestionActivity.this, AddSurveyActivity.class);
                        intent.putExtra("isNestead", true);
                        intent.putExtra("surveyId", survId);
                        startActivity(intent);
                    }
                }
            }
        });
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

        validateAndSave();
        finish();
    }


    public void saveUpdateQuestions() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                //long id = realm.where(MUser.class).max("id").longValue() + 1;


                // questions.setSurveyid(surveyid);
                questions.setQuestion(txt_question.getText().toString());

                questions.setCompulsary(cb_compulsary.isChecked());
                questions.setText(cb_text.isChecked());//1
                questions.setNumber(cb_number.isChecked());//2
                questions.setDate(cb_date.isChecked());//3
                questions.setTime(cb_time.isChecked());//4
                questions.setImage(cb_picture.isChecked());//5
                questions.setPatientName(cb_patient_name.isChecked());
                questions.setCheckbox(cb_checkbox.isChecked());//7
                questions.setOpt(cb_option.isChecked());//8
                questions.setOptCondition(cb_options_condition.isChecked());//9

                //patient name 6


                questions.setTypeQuestion(typeQuestion);


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

                            opt1.setOpt(allEds.get(i).getText().toString());
                            options.add(opt1);


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

                            opt1.setOpt(allEdsChk.get(i).getText().toString());
                            chk.add(opt1);


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

                            //long surveyId;
                            Log.v("TAG", "Selected Position====" + nesSurveyId.get(i));


                            ConditionalOptions opt1 = realm.createObject(ConditionalOptions.class, optionsId);

                            opt1.setOpt(allEdsOptConditional.get(i).getText().toString());
                            opt1.setSurveyid(nesSurveyId.get(i));
                            conditionalOptionses.add(opt1);
                        }
                    }
                }
                questions.setOptionContidion(conditionalOptionses);


                if (update) {
                    realm.copyToRealmOrUpdate(questions);


                }
                /*if (survey != null) {
                    lstQuestion.clear();
                    lstQuestion.addAll(survey.getQuestions());
                    lstQuestion.add(questions);
                    survey.setQuestions(lstQuestion);
                    realm.copyToRealmOrUpdate(survey);
                }*/

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
    protected void onResume() {
        super.onResume();

        setupData();

    }

    public void setupData() {

        if (questionsId != 0) {

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    option.clear();
                    checkboxOpt.clear();
                    conditionalOptions.clear();
                }
            });


            questions = realm.where(Questions.class).equalTo("id", questionsId).findFirst();
            // saveUpdateQuestions();

            cb_option.setChecked(questions.getOpt());
            cb_text.setChecked(questions.getText());
            cb_number.setChecked(questions.getNumber());
            cb_date.setChecked(questions.getDate());
            cb_time.setChecked(questions.getTime());
            cb_picture.setChecked(questions.getImage());
            cb_compulsary.setChecked(questions.getCompulsary());
            cb_checkbox.setChecked(questions.getCheckbox());
            cb_options_condition.setChecked(questions.getOptCondition());
            cb_patient_name.setChecked(questions.getPatientName());
            showOption(questions.getOpt());
            showCheckbox(questions.getCheckbox());
            showOptionConditional(questions.getOptCondition());
            txt_question.setText(questions.getQuestion());

            option = questions.getOptions();

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

                    text.setCompoundDrawablePadding(5);
                    text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_circle_outline_black_24dp, 0);

                    ll_option.addView(text);
                    allEds.add(text);

                    removeTextFieldListener(text);
                }

            }

            checkboxOpt = questions.getChkb();

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

                    text.setCompoundDrawablePadding(5);
                    text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_circle_outline_black_24dp, 0);

                    ll_check.addView(text);
                    allEdsChk.add(text);

                    removeTextFieldListener(text);

                }

            }


            conditionalOptions = questions.getOptionContidion();
            for (int i = 0; i < conditionalOptions.size(); i++) {
                if (i == 0) {
                    et_opt1_conditional.setText(conditionalOptions.get(i).getOpt());

                    if (conditionalOptions.get(i).getSurveyid() > 0) {
                        btnCond1.setText("Update");
                        nesSurveyId.add(i, conditionalOptions.get(i).getSurveyid());
                    } else {
                        btnCond1.setText("Add");
                        nesSurveyId.add(i, (long) 0);
                    }

                } else if (i == 1) {
                    et_opt2_conditional.setText(conditionalOptions.get(i).getOpt());

                    if (conditionalOptions.get(i).getSurveyid() > 0) {
                        btnCond2.setText("Update");
                        nesSurveyId.add(i, conditionalOptions.get(i).getSurveyid());
                    } else {
                        btnCond2.setText("Add");
                        nesSurveyId.add(i, (long) 0);
                    }

                } else {
                    LinearLayout ll = new LinearLayout(this);
                    ll.setWeightSum(7);
                    EditText text2 = new EditText(this);
                    text2.setLayoutParams(new LinearLayout.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.WRAP_CONTENT, 4));
                    text2.setText(conditionalOptions.get(i).getOpt());


                    text2.setCompoundDrawablePadding(5);
                    text2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_circle_outline_black_24dp, 0);


                    Button btn = new Button(this);
                    btn.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT, 0));


                    if (conditionalOptions.get(i).getSurveyid() > 0) {
                        btn.setText("Update");
                        nesSurveyId.add(i, conditionalOptions.get(i).getSurveyid());
                    } else {
                        btn.setText("Add");
                        nesSurveyId.add(i, (long) 0);
                    }

                    ll.addView(text2);
                    ll.addView(btn);


                    ll_option_conditional.addView(ll);
                    allEdsOptConditional.add(text2);

                    allBtnCondition.add(btn);

                    removeTextFieldListener(text2);
                    addNesteadSurvey(btn);
                }
            }
            update = true;


        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {


                    try {
                        questionsId = realm.where(Questions.class).max("id").intValue() + 1;
                    } catch (Exception ex) {
                        Log.v("exception", ex.toString());
                        questionsId = 1;
                    }
                    questions = realm.createObject(Questions.class, questionsId);
                    questions.setQuestion(txt_question.getText().toString());

                    questions.setCompulsary(false);
                    questions.setText(false);//1
                    questions.setNumber(false);//2
                    questions.setDate(false);//3
                    questions.setTime(false);//4
                    questions.setImage(false);//5
                    questions.setPatientName(false);
                    questions.setCheckbox(false);//7
                    questions.setOpt(false);//8
                    questions.setOptCondition(false);//9
                    update = false;

                    if (survey != null) {
                        lstQuestion.clear();
                        lstQuestion.addAll(survey.getQuestions());
                        lstQuestion.add(questions);
                        survey.setQuestions(lstQuestion);
                        realm.copyToRealmOrUpdate(survey);
                    }

                }
            });

        }
    }

}
