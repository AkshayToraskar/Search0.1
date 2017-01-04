package com.ak.search.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ak.search.R;
import com.ak.search.adapter.GetQuestionsAdapter;
import com.ak.search.app.UpdateReviewAnswer;
import com.ak.search.model.Answers;
import com.ak.search.model.Options;
import com.ak.search.model.Questions;
import com.ak.search.model.Survey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionReviewFragment extends Fragment implements UpdateReviewAnswer {

    long surveyId;
    private List<Questions> questionsList;
    @BindView(R.id.rv_questions)
    RecyclerView recyclerView;
    public GetQuestionsAdapter mAdapter;

    public UpdateReviewAnswer updateReviewAnswer;
Realm realm;
    Survey survey;

    public QuestionReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question_review, container, false);
realm=Realm.getDefaultInstance();
        ButterKnife.bind(this, view);
        questionsList = new ArrayList<>();
        updateReviewAnswer = this;




        if (getArguments() != null) {
           /* surveyId = getArguments().getLong("surveyId");
            survey=realm.where(Survey.class).equalTo("id",surveyId).findFirst();
questionsList=survey.getQuestions();
            mAdapter = new GetQuestionsAdapter(getContext(), getQuestionList());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);*/

           // Log.v("GET SURVEY", "" + surveyId);

        }

        return view;

    }


    @Override
    public void onResume() {
        super.onResume();


        Log.v("ans ", " on resumed called");


    }

    @Override
    public void onReviewUpdate(HashMap<Long, Answers> answers) {

        //getQuestionList();
        questionsList.clear();
        questionsList.addAll(getQuestionList());


        for (Map.Entry m : answers.entrySet()) {


            for (int i = 0; i < questionsList.size(); i++) {
                /*if (Long.parseLong(String.valueOf(m.getKey())) == questionsList.get(i).getId()) {
                    Answers an = (Answers) m.getValue();
                    questionsList.get(i).setAnswers(an);
                }*/
            }


        }

        //Log.v("ans "," "+questionsList.get(0).getId());

        //mAdapter.notifyDataSetChanged();

        mAdapter.update(questionsList);


        //Log.v("","");
    }


    public List<Questions> getQuestionList() {


        surveyId = getArguments().getLong("surveyId");
        survey=realm.where(Survey.class).equalTo("id",surveyId).findFirst();
        questionsList=survey.getQuestions();

        ///List<Options> opt = new ArrayList<>();
       /* for (int i = 0; i < questionsList.size(); i++) {
            //opt = questionsList.get(i).getOptions(String.valueOf(questionsList.get(i).getId()));
            //List<Options> opt = Options.find(Options.class, "questionid = ?", String.valueOf(questionsList.get(i).getId()));
            //questionsList.get(i).setOptions(opt);

        }*/
        return questionsList;
    }
}
