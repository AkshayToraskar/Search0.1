package com.ak.search.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.adapter.MyPagerAdapter;
import com.ak.search.app.SaveAnswer;
import com.ak.search.fragment.QuestionFragment;
import com.ak.search.fragment.QuestionReviewFragment;
import com.ak.search.realm_model.Answers;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.Questions;
import com.ak.search.realm_model.Survey;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;

public class QuestionsActivity extends AppCompatActivity implements SaveAnswer {


    MyPagerAdapter adapterViewPager;
    long surveyId, patientId;
    String patientName;

    List<Questions> questionsList;
    List<Fragment> fragments;

    @BindView(R.id.view_pager)
    ViewPager pager;

    @BindView(R.id.btn_next)
    Button btn_next;

    @BindView(R.id.btn_previous)
    Button btn_previous;

    @BindView(R.id.txt_page_count)
    TextView txt_page_count;
    QuestionReviewFragment questionReviewFragment;
    Survey survey;
    Realm realm;

    Patients patients;

    public static HashMap<Long, Answers> answers;

    DataCollection dataCollection;

    //public ArrayList<MAnswers> ans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        answers = new HashMap<>();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            survey = Parcels.unwrap(getIntent().getExtras().getParcelable("survey"));
            patients = Parcels.unwrap(getIntent().getExtras().getParcelable("patient"));




            // survey = MSurvey.findById(MSurvey.class, (int) surveyId);
            getSupportActionBar().setTitle(survey.getName() + " ");


            // ans=new ArrayList<>();

            for (int i = 0; i < survey.getQuestions().size(); i++) {

                Answers answ = new Answers();

                answ.setPatientid(patients.getId());
                answ.setQuestions(survey.getQuestions().get(i));

                answers.put(survey.getQuestions().get(i).getId(), answ);
            }



            /*realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {


                    int patientId;
                    try {
                        patientId = realm.where(MPatients.class).max("id").intValue() + 1;
                    } catch (Exception ex) {
                        Log.v("exception", ex.toString());
                        patientId = 1;
                    }


                    //MPatients patients = realm.createObject(MPatients.class, patientId);
                    patients.setPatientname(patients.getPatientname());
                    //patients.setSurveyid(surveyId);

                    realm.copyToRealmOrUpdate(patients);

                }
            });*/
            //     patientId = patients.save();

            questionsList = new ArrayList<>();

            questionsList.addAll(survey.getQuestions());

            //     questionsList = MQuestions.find(MQuestions.class, "surveyid = ?", String.valueOf(surveyId));


            /*for (int i = 0; i < questionsList.size(); i++) {
                List<MOptions> opt = MOptions.find(MOptions.class, "questionid = ?", String.valueOf(questionsList.get(i).getId()));
                questionsList.get(i).setOptions(opt);
            }*/
        }


        fragments = getFragments();
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), fragments);
        pager.setOffscreenPageLimit(fragments.size());
        pager.setAdapter(adapterViewPager);
        pager.addOnPageChangeListener(viewPagerPageChangeListener);
        txt_page_count.setText(pager.getCurrentItem() + 1 + "/" + (fragments.size() - 1));

    }


    public void onBtnClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_next:

                if (btn_next.getText().toString().equalsIgnoreCase("next")) {
                    int current = getItem(+1);
                    if (current < fragments.size()) {
                        // move to next screen
                        pager.setCurrentItem(current);
                    } else {
                        // launchHomeScreen();

                        /*for (Map.Entry m : answers.entrySet()) {
                            MAnswers an = (MAnswers) m.getValue();
                            // an.save();

                            Log.v("ans ", " " + m.getKey() + "," + an.getPatientid() + "," + an.getPatientid() + "," + an.getAns());

                        }*/


                    }
                } else {

                   /* for(int i=0; i<fragments.size();i++){
                        Log.v("ans ",answers.get(i).getQuestionId()+", ");
                    }*/

                    //Log.v("ans "," "+answers.get(0).getQuestionId());


                    /*for (Map.Entry m : answers.entrySet()) {
                        MAnswers an = (MAnswers) m.getValue();
                       // an.save();
                        Log.v("ans ", " " + m.getKey() + "," + "," + an.getPatientid() + "--------" + an.getSelectedopt() + "," + an.getAns());

                    }*/

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            //MAnswers
                            RealmList<Answers> answerses = new RealmList<Answers>();
                            for (Map.Entry m : answers.entrySet()) {
                                /*int answerId;
                                try {
                                    answerId = realm.where(MAnswers.class).max("id").intValue() + 1;
                                } catch (Exception ex) {
                                    Log.v("exception", ex.toString());
                                    answerId = 1;
                                }*/
                                Answers a = (Answers) m.getValue();
                                Answers ans = realm.createObject(Answers.class);

                                Questions questions=realm.where(Questions.class).equalTo("id",a.getQuestions().getId()).findFirst();

                                ans.setQuestions(questions);
                                ans.setPatientid(a.getPatientid());
                                ans.setAns(a.getAns());
                                ans.setSelectedopt(a.getSelectedopt());
                                answerses.add(ans);

                            }


                            //MPatients
                            /*if (!patients.isManaged()) { // if the 'list' is managed, all items in it is also managed
                                //RealmList<Image> managedImageList = new RealmList<>();
                               // for (Image item : list) {
                                    if (patients.isManaged()) {
                                        //managedImageList.add(item);
                                    } else {
                                        realm.copyToRealm(patients);
                                        //managedImageList.add(realm.copyToRealm(item));
                                    }
                                }
                                //list = managedImageList;
                            }*/

                            Patients patients1 = realm.where(Patients.class).equalTo("id", patients.getId()).findFirst();


                            int collectionId;
                            try {
                                collectionId = realm.where(DataCollection.class).max("id").intValue() + 1;
                            } catch (Exception ex) {
                                Log.v("exception", ex.toString());
                                collectionId = 1;
                            }

                            dataCollection = realm.createObject(DataCollection.class, collectionId);
                            dataCollection.setSurveyid(surveyId);
                            dataCollection.setPatients(patients1);
                            dataCollection.setAnswerses(answerses);

                            String timeStamp = new SimpleDateFormat("dd.MM.yyyy.HH.mm.ss").format(new Date());
                            dataCollection.setLat(0);
                            dataCollection.setLng(0);
                            dataCollection.setTimestamp(timeStamp);

                            realm.copyToRealmOrUpdate(dataCollection);

                        }
                    });


                    Toast.makeText(getApplicationContext(), "DONE", Toast.LENGTH_SHORT).show();


                    finish();
                }

                break;

            case R.id.btn_previous:
                int current1 = getItem(-1);
                if (current1 < fragments.size()) {
                    // move to previous screen
                    pager.setCurrentItem(current1);
                } else {
                    // launchHomeScreen();
                }
                break;
        }
    }

    private int getItem(int i) {
        return pager.getCurrentItem() + i;
    }


    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            //addBottomDots(position);

            Log.v("aa", "page Selected " + position);


            //QuestionFragment qf=new QuestionFragment();
            //answers.put(position,qf.getAns());


            int currentPage = pager.getCurrentItem();

            txt_page_count.setText(currentPage + 1 + "/" + (fragments.size() - 1));


            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == fragments.size() - 1) {
                // last page. make button text to GOT IT
                btn_next.setText("DONE");

                txt_page_count.setVisibility(View.INVISIBLE);
                //btn_next.setVisibility(View.VISIBLE);
                btn_previous.setVisibility(View.VISIBLE);


                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(pager.getWindowToken(), 0);

                //  getSupportActionBar().setTitle("Review " + NewSurveyActivity.selectedSurvey.getName());

            } else if (position == 0) {
                // still pages are left
                // btn_next.setText(getString(R.string.next));

                btn_next.setText("NEXT");

                txt_page_count.setVisibility(View.VISIBLE);
                //btn_next.setVisibility(View.VISIBLE);
                btn_previous.setVisibility(View.INVISIBLE);

//                getSupportActionBar().setTitle(NewSurveyActivity.selectedSurvey.getName() + " ");
            } else {

                btn_next.setText("NEXT");
                txt_page_count.setVisibility(View.VISIBLE);
                //btn_next.setVisibility(View.VISIBLE);
                btn_previous.setVisibility(View.VISIBLE);


                // getSupportActionBar().setTitle(NewSurveyActivity.selectedSurvey.getName() + " ");

            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            //Log.v("aa", "page Scrolled");
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            //  Log.v("aa", "page Scroll state changed "+arg0);
        }
    };


    private List<Fragment> getFragments() {

        List<Fragment> fList = new ArrayList<Fragment>();


        for (int i = 0; i < questionsList.size(); i++) {
            fList.add(QuestionFragment.newInstance(questionsList.get(i)));
        }
        questionReviewFragment = new QuestionReviewFragment();
        Bundle args = new Bundle(1);
        args.putParcelable("questionList", Parcels.wrap(questionsList));
        questionReviewFragment.setArguments(args);
        fList.add(questionReviewFragment);

        //fList.add(QuestionFragment.newInstance("Fragment 2"));
        //fList.add(QuestionFragment.newInstance("Fragment 3"));


        return fList;

    }

    @Override
    public void onAnswerSave(Answers ans) {

        ans.setPatientid(patients.getId());


        answers.put(ans.getQuestions().getId(), ans);

        if (questionReviewFragment.updateReviewAnswer != null) {
            questionReviewFragment.updateReviewAnswer.onReviewUpdate(answers);
        }

        //Log.v("asdf",""+ans.getAns());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                discardSurvey();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        discardSurvey();
    }

    public void discardSurvey() {
        new AlertDialog.Builder(this)
                .setTitle("Discard")
                .setMessage("Would you like to Discard MSurvey?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //    MPatients patients = MPatients.findById(MPatients.class, patientId);
                        //    patients.delete();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // user doesn't want to logout
                    }
                })
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
