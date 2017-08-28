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
import com.ak.search.activity.QuestionsActivity;
import com.ak.search.adapter.GetQuestionsAdapter;
import com.ak.search.app.SaveAnswer;
import com.ak.search.app.UpdateReviewAnswer;
import com.ak.search.realm_model.Answers;
import com.ak.search.realm_model.Questions;
import com.ak.search.realm_model.Survey;

import org.parceler.Parcels;

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


    private List<Questions> questionsList;

    private List<Answers> answersList;

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
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this, view);

        answersList = new ArrayList<>();
        updateReviewAnswer = this;


        if (getArguments() != null) {

            questionsList = Parcels.unwrap(getArguments().getParcelable("questionList"));


            for (Map.Entry m : QuestionsActivity.answers.entrySet()) {
                answersList.add((Answers) m.getValue());

            }





            SaveAnswer saveAnswer = null;

            mAdapter = new GetQuestionsAdapter(getContext(), answersList,saveAnswer,realm,false);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);

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



        for (Map.Entry m : answers.entrySet()) {
            for (int i = 0; i < answersList.size(); i++) {
                if (Long.parseLong(String.valueOf(m.getKey())) == answersList.get(i).getQuestions().getId()) {
                    Answers an = (Answers) m.getValue();
                    answersList.set(i,an);
                }
            }


        }




        mAdapter.notifyDataSetChanged();

    }



}
