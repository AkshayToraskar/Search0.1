package com.ak.search.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.adapter.OptionsAdapter;
import com.ak.search.app.AddNestedInfo;
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

public class AddQuestionActivity extends AppCompatActivity implements AddNestedInfo {
    Validate validate;
    @BindView(R.id.txt_question)
    EditText txt_question;
    @BindView(R.id.ll_option)
    LinearLayout ll_option;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cb_compulsary)
    CheckBox cb_compulsary;
    @BindView(R.id.rb_options)
    RadioButton rb_option;
    @BindView(R.id.rb_text)
    RadioButton rb_text;
    @BindView(R.id.rb_number)
    RadioButton rb_number;
    @BindView(R.id.rb_date)
    RadioButton rb_date;
    @BindView(R.id.rb_time)
    RadioButton rb_time;
    @BindView(R.id.rb_picture)
    RadioButton rb_picture;
    @BindView(R.id.rb_patient_name)
    RadioButton rb_patient_name;
    @BindView(R.id.rb_single_choice)
    RadioButton rb_single_choice;
    @BindView(R.id.rb_multi_choice)
    RadioButton rb_multichoice;
    @BindView(R.id.rb_conditional)
    RadioButton rb_conditional;
    @BindView(R.id.coordinator_layout_add_question)
    CoordinatorLayout clAddQuestion;
    @BindView(R.id.rv_options)
    RecyclerView rvOptions;
    @BindView(R.id.rg_opt)
    RadioGroup rgOptions;
    @BindView(R.id.btn_add_more_option)
    Button btnAddMoreOptions;

    AddNestedInfo addNestedInfo;
    String typeQuestion;
    List<Long> nesSurveyId;
    public Questions questions;
    public List<ConditionalOptions> conditionalOptions;
    public boolean update;
    Survey survey;
    long questionsId;
    long surveyId;
    Realm realm;
    RealmList<Questions> lstQuestion;
    OptionsAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        realm = Realm.getDefaultInstance();
        validate = new Validate();
        addNestedInfo = this;

        nesSurveyId = new ArrayList<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        questions = new Questions();
        lstQuestion = new RealmList<Questions>();

        conditionalOptions = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            ConditionalOptions conditionalOpt = new ConditionalOptions();
            conditionalOptions.add(conditionalOpt);
        }

        mAdapter = new OptionsAdapter(AddQuestionActivity.this, conditionalOptions, false, addNestedInfo);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvOptions.setLayoutManager(mLayoutManager);
        rvOptions.setItemAnimator(new DefaultItemAnimator());
        rvOptions.setAdapter(mAdapter);


        if (getIntent().getExtras() != null) {
            surveyId = getIntent().getExtras().getLong("surveyid");
            questionsId = getIntent().getExtras().getLong("questionId", 0);
            survey = realm.where(Survey.class).equalTo("id", surveyId).findFirst();
            Log.v("Question Id", "" + questionsId);
            setupData();
        }


        rb_option.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                final boolean b1 = b;
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        showOption(b1);
                    }
                });
            }
        });


        btnAddMoreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConditionalOptions cond = new ConditionalOptions();
                conditionalOptions.add(cond);
                mAdapter.notifyItemInserted(conditionalOptions.size());
            }
        });


        switch (rgOptions.getCheckedRadioButtonId()) {
            case R.id.rb_single_choice:
                mAdapter = new OptionsAdapter(AddQuestionActivity.this, conditionalOptions, false, addNestedInfo);
                break;

            case R.id.rb_multi_choice:
                mAdapter = new OptionsAdapter(AddQuestionActivity.this, conditionalOptions, false, addNestedInfo);
                break;

            case R.id.rb_conditional:
                mAdapter = new OptionsAdapter(AddQuestionActivity.this, conditionalOptions, true, addNestedInfo);
                break;

            default:
                mAdapter = new OptionsAdapter(AddQuestionActivity.this, conditionalOptions, false, addNestedInfo);
                break;
        }

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        rvOptions.setLayoutManager(mLayoutManager1);
        rvOptions.setItemAnimator(new DefaultItemAnimator());
        rvOptions.setAdapter(mAdapter);


        rgOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (R.id.rb_conditional == rgOptions.getCheckedRadioButtonId()) {
                    mAdapter = new OptionsAdapter(AddQuestionActivity.this, conditionalOptions, true, addNestedInfo);
                } else {
                    mAdapter = new OptionsAdapter(AddQuestionActivity.this, conditionalOptions, false, addNestedInfo);
                }
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                rvOptions.setLayoutManager(mLayoutManager);
                rvOptions.setItemAnimator(new DefaultItemAnimator());
                rvOptions.setAdapter(mAdapter);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_question, menu);
        if (update) {
            menu.getItem(1).setTitle("Done");
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
                if (!rb_option.isChecked()) {
                    rb_single_choice.setChecked(false);
                    rb_multichoice.setChecked(false);
                    rb_conditional.setChecked(false);
                }
                if (isValidate()) {
                    saveUpdateQuestions();
                    finish();
                }

                break;


            case R.id.action_delete_question:
                new AlertDialog.Builder(this)
                        .setTitle("Delete")
                        .setMessage("Would you like to delete this MQuestions?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        questions.deleteFromRealm();
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

                if (!rb_option.isChecked()) {
                    rb_single_choice.setChecked(false);
                    rb_multichoice.setChecked(false);
                    rb_conditional.setChecked(false);
                }
                if (isValidate()) {
                    saveUpdateQuestions();
                    finish();
                } else {
                    discardQuestion();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBtnClick(View view) {
        int id = view.getId();

        switch (id) {

        }
    }


    public boolean isValidate() {
        if (validate.validateString(txt_question.getText().toString())) {
            txt_question.setError("Enter Question");
            return false;
        } else {
            txt_question.setError(null);
        }

        typeQuestion = "";

        if (rb_text.isChecked()) {
            typeQuestion = typeQuestion + "1,";
        }
        if (rb_number.isChecked()) {
            typeQuestion = typeQuestion + "2,";
        }
        if (rb_date.isChecked()) {
            typeQuestion = typeQuestion + "3,";
        }
        if (rb_time.isChecked()) {
            typeQuestion = typeQuestion + "4,";
        }
        if (rb_picture.isChecked()) {
            typeQuestion = typeQuestion + "5,";
        }
        if (rb_patient_name.isChecked()) {
            typeQuestion = typeQuestion + "6,";
        }
        if (rb_multichoice.isChecked()) {
            typeQuestion = typeQuestion + "7,";
        }
        if (rb_single_choice.isChecked()) {
            typeQuestion = typeQuestion + "8,";
        }
        if (rb_conditional.isChecked()) {
            typeQuestion = typeQuestion + "9,";
        }

        if (typeQuestion.equals("")) {
            Toast.makeText(getApplicationContext(), "Select QuestionType", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }


    public void discardQuestion() {

        Snackbar snackbar = Snackbar
                .make(clAddQuestion, "Discard Question?", Snackbar.LENGTH_INDEFINITE)
                .setAction("YES", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                questions.deleteFromRealm();

                            }
                        });
                        finish();
                    }
                });

        snackbar.show();
    }

    public void showOption(boolean val) {
        if (val) {
            ll_option.setVisibility(View.VISIBLE);
            btnAddMoreOptions.setVisibility(View.VISIBLE);
        } else {
            ll_option.setVisibility(View.GONE);
            btnAddMoreOptions.setVisibility(View.GONE);
        }
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if (!rb_option.isChecked()) {
            rb_single_choice.setChecked(false);
            rb_multichoice.setChecked(false);
            rb_conditional.setChecked(false);
        }

        if (isValidate()) {
            saveUpdateQuestions();
            finish();
        } else {
            discardQuestion();
        }
    }


    public void saveUpdateQuestions() {


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                questions.setQuestion(txt_question.getText().toString());
                questions.setCompulsary(cb_compulsary.isChecked());
                questions.setText(rb_text.isChecked());//1
                questions.setNumber(rb_number.isChecked());//2
                questions.setDate(rb_date.isChecked());//3
                questions.setTime(rb_time.isChecked());//4
                questions.setImage(rb_picture.isChecked());//5
                questions.setPatientName(rb_patient_name.isChecked());
                questions.setCheckbox(rb_multichoice.isChecked());//7
                questions.setOpt(rb_single_choice.isChecked());//8
                questions.setOptCondition(rb_conditional.isChecked());//9

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

                if (rb_conditional.isChecked()) {

                    for (int i = 0; i < conditionalOptions.size(); i++) {
                        int optionsId;
                        try {
                            optionsId = realm.where(ConditionalOptions.class).max("id").intValue() + 1;
                        } catch (Exception ex) {
                            Log.v("exception", ex.toString());
                            optionsId = 1;
                        }
                        ConditionalOptions opt1 = realm.createObject(ConditionalOptions.class, optionsId);
                        opt1.setOpt(conditionalOptions.get(i).getOpt());
                        opt1.setSurveyid(conditionalOptions.get(i).getSurveyid());
                        conditionalOptionses.add(opt1);
                    }

                } else if (rb_single_choice.isChecked()) {
                    for (int i = 0; i < conditionalOptions.size(); i++) {
                        int optionsId;
                        try {
                            optionsId = realm.where(Options.class).max("id").intValue() + 1;
                        } catch (Exception ex) {
                            Log.v("exception", ex.toString());
                            optionsId = 1;
                        }

                        Options opt1 = realm.createObject(Options.class, optionsId);
                        opt1.setOpt(conditionalOptions.get(i).getOpt());
                        options.add(opt1);
                    }
                } else if (rb_multichoice.isChecked()) {
                    for (int i = 0; i < conditionalOptions.size(); i++) {
                        int optionsId;
                        try {
                            optionsId = realm.where(Options.class).max("id").intValue() + 1;
                        } catch (Exception ex) {
                            Log.v("exception", ex.toString());
                            optionsId = 1;
                        }

                        Options opt1 = realm.createObject(Options.class, optionsId);
                        opt1.setOpt(conditionalOptions.get(i).getOpt());
                        chk.add(opt1);
                    }
                }

                questions.setOptionContidion(conditionalOptionses);
                questions.setChkb(chk);
                questions.setOptions(options);

                realm.copyToRealmOrUpdate(questions);

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


            questions = realm.where(Questions.class).equalTo("id", questionsId).findFirst();

            rb_text.setChecked(questions.getText());
            rb_number.setChecked(questions.getNumber());
            rb_date.setChecked(questions.getDate());
            rb_time.setChecked(questions.getTime());
            rb_picture.setChecked(questions.getImage());
            cb_compulsary.setChecked(questions.getCompulsary());
            rb_patient_name.setChecked(questions.getPatientName());
            rb_single_choice.setChecked(questions.getOpt());
            rb_multichoice.setChecked(questions.getCheckbox());
            rb_conditional.setChecked(questions.getOptCondition());


            if (rb_single_choice.isChecked() || rb_multichoice.isChecked() || rb_conditional.isChecked()) {
                rb_option.setChecked(true);
                showOption(true);
                conditionalOptions.clear();
                if (rb_single_choice.isChecked()) {

                    List<ConditionalOptions> opt = new ArrayList<>();
                    for (int i = 0; i < questions.getOptions().size(); i++) {
                        ConditionalOptions op = new ConditionalOptions();
                        op.setId(questions.getOptions().get(i).getId());
                        op.setOpt(questions.getOptions().get(i).getOpt());
                        opt.add(op);
                    }
                    conditionalOptions.addAll(opt);
                } else if (rb_multichoice.isChecked()) {
                    List<ConditionalOptions> opt = new ArrayList<>();
                    for (int i = 0; i < questions.getChkb().size(); i++) {
                        ConditionalOptions op = new ConditionalOptions();
                        op.setId(questions.getChkb().get(i).getId());
                        op.setOpt(questions.getChkb().get(i).getOpt());
                        opt.add(op);
                    }
                    conditionalOptions.addAll(opt);
                } else if (rb_conditional.isChecked()) {
                    conditionalOptions.addAll(questions.getOptionContidion());
                } else {
                    for (int i = 0; i < 2; i++) {
                        ConditionalOptions conditionalOpt = new ConditionalOptions();
                        conditionalOptions.add(conditionalOpt);
                    }
                }
                mAdapter.notifyDataSetChanged();

            } else {
                rb_option.setChecked(false);
                showOption(false);
            }

            txt_question.setText(questions.getQuestion());
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
                    questions.setText(true);//1
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

    @Override
    public void addNestedData(long surveyId) {

        saveUpdateQuestions();
        Intent intent = new Intent(AddQuestionActivity.this, AddSurveyActivity.class);
        intent.putExtra("isNestead", true);
        intent.putExtra("surveyId", surveyId);
        startActivity(intent);
    }

}
