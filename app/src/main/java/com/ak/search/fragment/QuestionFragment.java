package com.ak.search.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ak.search.R;
import com.ak.search.app.SaveAnswer;
import com.ak.search.realm_model.Answers;
import com.ak.search.realm_model.Questions;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static android.view.View.GONE;

/**
 * Created by dg hdghfd on 08-12-2016.
 */

public class QuestionFragment extends Fragment {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    @BindView(R.id.rg_options)
    RadioGroup rg_option;

    @BindView(R.id.rb_opt1)
    RadioButton rb_opt1;
    @BindView(R.id.rb_opt2)
    RadioButton rb_opt2;

    @BindView(R.id.txt_answer)
    EditText et_answer;

    @BindView(R.id.tv_question)
    TextView tv_question;

    SaveAnswer answer;

    public Answers ans;
    Realm realm;


    public static final QuestionFragment newInstance(Questions message) {
        QuestionFragment f = new QuestionFragment();
        Bundle bdl = new Bundle(1);
        bdl.putParcelable(EXTRA_MESSAGE, Parcels.wrap(message));
        f.setArguments(bdl);
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_question, container, false);
        ButterKnife.bind(this, v);
        realm = Realm.getDefaultInstance();


        Questions questions = Parcels.unwrap(getArguments().getParcelable(EXTRA_MESSAGE));

        //MSurvey survey=MSurvey.findById(MSurvey.class,Integer.parseInt(message.getSurveyid()));
        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(survey.getName()+"");

        tv_question.setText(questions.getQuestion());

        /*int answerId;
        try {
            answerId = realm.where(MUser.class).max("id").intValue() + 1;
        } catch(Exception ex) {
            Log.v("exception",ex.toString());
            answerId = 1;
        }*/

        ans=new Answers();


        ans.setQuestions(questions);

        if (!questions.getOpt()) {
            rg_option.setVisibility(GONE);
            ans.setSelectedopt(-1);
        } else {
            /*if (questions.getOptions().size() > 1) {
                rb_opt1.setText(questions.getOptions().get(0).getOpt());
                rb_opt2.setText(questions.getOptions().get(1).getOpt());
            }
            *//*if (opt.size() > 2) {
                for (int i = 2; i < opt.size(); i++) {

                }
            }*//*

            if (questions.getOptions().size() > 0) {
                rb_opt1.setText(questions.getOptions().get(0).getOpt());
            } else if (questions.getOptions().size() > 1) {
                rb_opt1.setText(questions.getOptions().get(0).getOpt());
                rb_opt2.setText(questions.getOptions().get(1).getOpt());
            }*/
            if (questions.getOptions().size() > 0) {
                for (int i = 0; i < questions.getOptions().size(); i++) {
                    if(i<2){
                        rb_opt1.setText(questions.getOptions().get(0).getOpt());
                        rb_opt2.setText(questions.getOptions().get(1).getOpt());

                    }
                    else {
                        RadioButton rb = new RadioButton(getContext());
                        rb.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        rb.setText(questions.getOptions().get(i).getOpt());
                        rg_option.addView(rb);
                    }
                    //allEds.add(text);
                }
            }

        }

        if (!questions.getText()) {
            et_answer.setVisibility(GONE);
            ans.setAns("-");
        }

      //  ans.setQuestionid(String.valueOf(message.getId()));

        et_answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ans.setAns(String.valueOf(editable));

                answer.onAnswerSave(ans);
            }
        });

        rg_option.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                int id=rg_option.getCheckedRadioButtonId();
                View radioButton = rg_option.findViewById(id);
                ans.setSelectedopt(rg_option.indexOfChild(radioButton));

                answer.onAnswerSave(ans);
            }
        });


        return v;

    }


    @Override
    public void onAttach(Activity activity) {
        try {
            answer = (SaveAnswer) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
        super.onAttach(activity);
    }


    /*public static MAnswers getAns()
    {

        return ans;
    }*/

}