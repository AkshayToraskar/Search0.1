package com.ak.search.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.ak.search.R;
import com.ak.search.adapter.GetQuestionsAdapter;
import com.ak.search.model.Answers;
import com.ak.search.model.DataCollection;
import com.ak.search.model.Options;
import com.ak.search.model.Patients;
import com.ak.search.model.Questions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class ShowSurveyActivity extends AppCompatActivity {

   // String surveyId;
    Long collectionId;
    private List<Answers> answerList;
    @BindView(R.id.rv_questions)
    RecyclerView recyclerView;
    public GetQuestionsAdapter mAdapter;
    DataCollection dataCollection;
    Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_survey);
        ButterKnife.bind(this);
        realm=Realm.getDefaultInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        answerList=new ArrayList<>();


        if (getIntent().getExtras() != null) {
            //surveyId = getIntent().getExtras().getString("surveyId");
            collectionId = getIntent().getExtras().getLong("collectionid");


         //   Patients patients= Patients.findById(Patients.class,patientId);


            mAdapter = new GetQuestionsAdapter(this, getCollectionData());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);

           // Log.v("GET SURVEY", "" + surveyId);

            getSupportActionBar().setTitle(dataCollection.getPatients().getPatientname()+" ");
        }

    }


    public List<Answers> getCollectionData() {


        dataCollection=realm.where(DataCollection.class).equalTo("id",collectionId).findFirst();
        answerList.clear();
        answerList.addAll(dataCollection.getAnswerses());


      //  questionsList = Questions.find(Questions.class, "surveyid = ?", String.valueOf(surveyId));

        ///List<Options> opt = new ArrayList<>();
        /*for (int i = 0; i < answerList.size(); i++) {
            //opt = questionsList.get(i).getOptions(String.valueOf(questionsList.get(i).getId()));
         //   List<Options> opt = Options.find(Options.class, "questionid = ?", String.valueOf(questionsList.get(i).getId()));
         //   questionsList.get(i).setOptions(opt);


          //  List<Answers> answers = Answers.find(Answers.class, "patientid = ? and questionid = ?", String.valueOf(patientId),String.valueOf(questionsList.get(i).getId()));
          //  questionsList.get(i).setAnswers(answers.get(0));

        }*/


        return answerList;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();

        switch (id)
        {
            case android.R.id.home:
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);


    }


}
