package com.ak.search.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.adapter.GetQuestionsAdapter;
import com.ak.search.app.SaveAnswer;
import com.ak.search.app.SessionManager;
import com.ak.search.fragment.QuestionFragment;
import com.ak.search.model.MNestedAddQue;
import com.ak.search.model.MyTreeNode;
import com.ak.search.model.NestedData;
import com.ak.search.model.NestedQuest;

import com.ak.search.realm_model.Answers;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.Questions;
import com.ak.search.realm_model.Survey;
import com.ak.search.realm_model.User;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.Sort;

public class StartSurveyActivity extends AppCompatActivity implements SaveAnswer {

    Survey survey;
    Realm realm;
    @BindView(R.id.rv_questions)
    RecyclerView recyclerView;

    @BindView(R.id.tv_counter)
    TextView tvCounter;

    public boolean update;

    public static Patients patients;
    List<Questions> questionsList;
    public GetQuestionsAdapter mAdapter;
    private List<Answers> answersList;

    public static HashMap<Long, Answers> answers;
    public static HashMap<Integer, MNestedAddQue> addQueHashMap;
    public static int CAMERA_REQUEST = 11;

    public static int positionImg;

    // public static int pos = 0, length = 0;
    //  public static long id=-1;
    SaveAnswer saveAnswer;
    DataCollection dataCollection;
    SessionManager sessionManager;

    public static List<NestedQuest> nestedQuestList = new ArrayList<>();

    MyTreeNode<NestedData> root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_survey);

        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        answersList = new ArrayList<>();
        answers = new HashMap<>();
        addQueHashMap = new HashMap<>();
        saveAnswer = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessionManager = new SessionManager(this);

        update = false;

        if (getIntent().getExtras() != null) {

            // patients = Parcels.unwrap(getIntent().getExtras().getParcelable("patient"));
            //RealmList<Questions> questionsList=  survey.getQuestions().sort("question_pos", Sort.ASCENDING);
            // survey = MSurvey.findById(MSurvey.class, (int) surveyId);

            survey = Parcels.unwrap(getIntent().getExtras().getParcelable("survey"));
            Survey surveyData = realm.where(Survey.class).equalTo("id", survey.getId()).findFirst();
            List<Questions> questionsList = surveyData.getQuestions().sort("question_pos", Sort.ASCENDING);


            getSupportActionBar().setTitle(survey.getName() + " ");
            int trp = 0;

            for (int i = 0; i < questionsList.size(); i++) {

                Answers answ = new Answers();
                answ.setPatientid(0);
                answ.setQuestions(questionsList.get(i));
                answ.setParentPos(trp);
                answ.setSelectedopt(-1);
                answ.setSelectedOptConditional(-1);
                answ.setSelectedChk(null);
                answ.setAns(null);
                answ.setNumAns(null);
                answ.setDate(null);
                answ.setTime(null);
                byte[] a = new byte[0];
                answ.setByteArrayImage(a);
                answersList.add(answ);
            }


            Answers answ = new Answers();
            answ.setPatientid(0);
            answ.setQuestions(null);
            answ.setParentPos(trp);
            answ.setSelectedopt(-1);
            answ.setSelectedOptConditional(-1);
            answ.setSelectedChk("");
            answ.setAns("");
            answ.setNumAns("");
            answ.setDate("");
            answ.setTime("");
            byte[] a = {};
            answ.setByteArrayImage(a);
            answersList.add(answ);

            tvCounter.setText("0 of " + (answersList.size() - 1) + " Questions Answered");

            mAdapter = new GetQuestionsAdapter(this, answersList, saveAnswer, realm, true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
           /* RecyclerView.ItemDecoration itemDecoration = new
                    DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(itemDecoration);*/
            recyclerView.setAdapter(mAdapter);
            root = new MyTreeNode<>(null);
            setupNestedData(root, 0, answersList.size());

        }
    }


    public void discardSurvey() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.discard))
                .setMessage(getString(R.string.sure_cancel))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //    MPatients patients = MPatients.findById(MPatients.class, patientId);
                        //    patients.delete();
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // user doesn't want to logout
                    }
                })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        mAdapter.onActivityResult(requestCode, resultCode, data, positionImg);
    }

    @Override
    public void onAnswerSave(int index, Answers ans) {
        if (patients != null) {
            ans.setPatientid(patients.getId());
        }
        answers.put(ans.getQuestions().getId(), ans);
        answersList.set(index, ans);
        int totalQuestions = answersList.size() - 1;
        int answeredQuetions = getAnsweredCount();
        tvCounter.setText(answeredQuetions + " of " + totalQuestions + " Questions Answered");
    }

    public int getAnsweredCount() {

        int count = 0;
        for (int i = 0; i < answersList.size(); i++) {
            if (answersList.get(i).getAnswered()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void onAddSurvey(long id, int pos, int treePos, long questionId) {

        Survey survey = realm.where(Survey.class).equalTo("id", id).findFirst();
        if (id != 0) {
            newLogic(pos, survey.getQuestions().size(), id, questionId);
        } else {
            newLogic(pos, 0, 0, questionId);
        }
        //oldLogic(pos,id,treePos);
    }


    @Override
    public void saveCollection() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmList<Answers> answerses = new RealmList<>();
                for (int i = 0; i < answersList.size() - 1; i++) {
                    Answers a = answersList.get(i);
                    Answers ans = realm.createObject(Answers.class);

                    Questions questions = realm.where(Questions.class).equalTo("id", a.getQuestions().getId()).findFirst();

                    ans.setQuestions(questions);
                    ans.setPatientid(a.getPatientid());
                    ans.setAns(a.getAns());
                    ans.setSelectedopt(a.getSelectedopt());
                    ans.setSelectedOptConditional(a.getSelectedOptConditional());
                    ans.setNumAns(a.getNumAns());
                    ans.setByteArrayImage(a.getByteArrayImage());
                    ans.setDate(a.getDate());
                    ans.setTime(a.getTime());
                    ans.setSelectedChk(a.getSelectedChk());
                    answerses.add(ans);

                }

                Patients patients1 = null;
                if (patients != null) {
                    patients1 = realm.where(Patients.class).equalTo("id", patients.getId()).findFirst();
                }

                int collectionId;
                try {
                    collectionId = realm.where(DataCollection.class).max("id").intValue() + 1;
                } catch (Exception ex) {
                    Log.v("exception", ex.toString());
                    collectionId = 1;
                }

                dataCollection = realm.createObject(DataCollection.class, collectionId);
                dataCollection.setSurveyid(survey.getId());
                dataCollection.setPatients(patients1);
                dataCollection.setAnswerses(answerses);

                if (sessionManager.getLoginType() == 2) {
                    dataCollection.setSuperwiserId(sessionManager.getUserId());
                    dataCollection.setFieldworkerId(0);
                } else if (sessionManager.getLoginType() == 3) {
                    dataCollection.setSuperwiserId(0);
                    dataCollection.setFieldworkerId(sessionManager.getUserId());
                }

                String timeStamp = new SimpleDateFormat("dd.MM.yyyy:HH.mm.ss").format(new Date());
                dataCollection.setLat(0);
                dataCollection.setLng(0);
                dataCollection.setTimestamp(timeStamp);

                realm.copyToRealmOrUpdate(dataCollection);

                Toast.makeText(getApplicationContext(), "DONE", Toast.LENGTH_SHORT).show();
                finish();

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
    public void onBackPressed() {
        super.onBackPressed();
        discardSurvey();
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



    public void newLogic(int pos, int size, long surveyId, long questionId) {

        int nesPos = pos;
        for (MyTreeNode x : root.inOrderView) {

            //traverse the tree row wise
            if (x.getData() != null) {

                NestedData nsd = (NestedData) x.getData();
                Log.d("questionId", "" + questionId + " eleQueId:" + nsd.getQuestionId() + ", Position:" + pos + " elePOs:" + nsd.getPos());
                Log.d("tree elements", " size:" + nsd.getSize() + ", SurveyId:" + nsd.getSurveyId());

                if (nsd.getQuestionId() == questionId) {
                    List<MyTreeNode> lstChildren = x.getChildren();


                    nesPos++;
                    //remove all child of current node
                    for (int i = 0; i < lstChildren.size(); i++) {
                        NestedData ns = (NestedData) lstChildren.get(i).getData();
                        deleteQuestion(ns.getPos(), ns.getSize()-1);

                    }

                    x.removeChild(lstChildren);
                    // add new child to current node
                    if (surveyId != 0) {
                        // Survey survey = realm.where(Survey.class).equalTo("id", surveyId).findFirst();
                        // NestedData nsd1 = new NestedData(nesPos, survey.getQuestions().size(), surveyId, questionId);
                        // x.addChild(nsd1);
                        addQuestion(nesPos, survey, x);
                    }
                }
            }

            Log.d("tree elements", " size:");
        }

        mAdapter.notifyDataSetChanged();
        Log.v("aa size", "asdf ");

    }


    public void addQuestion(int pos, Survey survey, MyTreeNode node) {

        // Survey survey = realm.where(Survey.class).equalTo("id",surveyId).findFirst();

        for (int i = 0; i < survey.getQuestions().size(); i++) {

            Answers answ = new Answers();
            answ.setPatientid(0);
            answ.setQuestions(survey.getQuestions().get(i));
            //  answ.setParentPos(pos);
            answ.setSelectedopt(-1);
            answ.setSelectedOptConditional(-1);
            answ.setSelectedChk("");
            answ.setAns("");
            answ.setNumAns("");
            answ.setDate("");
            answ.setTime("");
            byte[] a = {-1};
            answ.setByteArrayImage(a);
            answersList.add(pos + i, answ);

           // addNestedData(node, pos + i, survey.getQuestions().get(i).getId(), survey.getId(), survey.getQuestions().size());

            setupNestedData(node,pos,survey.getQuestions().size());
        }
        // mAdapter.notifyDataSetChanged();
    }


    public void deleteQuestion(int pos, int size) {
        for (int i = pos; i < (pos + size); i++) {
            answersList.remove(pos);
        }
    }


    public void setupNestedData(MyTreeNode myTree, int pos, int size) {

        List<MyTreeNode> qList = new LinkedList<>();
        //int count = 0;
        //for (Answers ans : answersList) {
        for (int i = pos; i < pos+size; i++) {
            Answers ans = answersList.get(i);
            if (ans.getQuestions() != null) {
                //  if (ans.getQuestions().getOptCondition()) {
                NestedData nestedData = new NestedData(i, answersList.size(), survey.getId(), answersList.get(i).getQuestions().getId());
                MyTreeNode myTreeNode = new MyTreeNode(nestedData);
                qList.add(myTreeNode);

                //   }
            }
            // count++;
        }
        // NestedData nestedData = new NestedData(count, 0, 0);
        //NestedQuest[] quests = qList.toArray(new NestedQuest[qList.size()]);

        myTree.addChildren(qList);
        Log.v("root size", "" + root.getChildren().size());
    }

    public void addNestedData(MyTreeNode node, int pos, long questionId, long surveyId, int size) {


        //int count = 0;
        //for (Answers ans : answersList) {
        //  for(int i=pos; i<size; i++){
        // Answers ans=answersList.get(i);
        //      if (ans.getQuestions() != null) {
        //  if (ans.getQuestions().getOptCondition()) {
        NestedData nestedData = new NestedData(pos, size, surveyId, questionId);
        //  MyTreeNode myTreeNode = new MyTreeNode(nestedData);
        //  qList.add(myTreeNode);
        //   }
        //  }
        // count++;
        //  }
        // NestedData nestedData = new NestedData(count, 0, 0);

        //NestedQuest[] quests = qList.toArray(new NestedQuest[qList.size()]);

        node.addChild(nestedData);

        Log.v("root size", "" + root.getChildren().size());
    }










}


/*

  public NestedQuest addNestQuestion(int pos, long id, int treePos) {

        NestedQuest nstQue = new NestedQuest();
        nstQue.setPos(pos);
        nstQue.setSurveyId(id);
        nstQue.setTreePos(treePos);
        return nstQue;

    }




    // MyTreeNode<NestedData> child1 = new MyTreeNode<>(nsd);
    // child1.addChild(nsd);
    //  child1.addChild(nsd);
    //  MyTreeNode<NestedData> child2 = new MyTreeNode<>(nsd);
    //  child2.addChild(nsd);
    //   root.addChild(child1);
    //   root.addChild(child2);
    //   root.addChild(nsd);
     *//*   root.addChildren(Arrays.<MyTreeNode>asList(
                new MyTreeNode<>(nsd),
                new MyTreeNode<>(nsd),
                new MyTreeNode<>(nsd)
        ));*//*
    //root.getChildren().get(0).removeChild(0);
        *//*for (MyTreeNode node : root.getChildren()) {
            // System.out.println(node.getData());
            NestedData ns = (NestedData) node.getData();
            if (pos == ns.getPos()) {
                NestedData nsd = new NestedData(pos, size, surveyId, 0);
                node.addChild(new MyTreeNode<>(nsd));
            }
            System.out.println(ns.getPos());
        }*//*
    //NestedData data = new NestedData(pos, size, surveyId);
        *//*tree = new NestedQuest(data,
                new NestedQuest(data,
                        new NestedQuest(data),
                        new NestedQuest(data),
                        new NestedQuest(data),
                        new NestedQuest(data,
                                new NestedQuest(data),
                                new NestedQuest(data),
                                new NestedQuest(data,
                                        new NestedQuest(data)
                                )
                        )
                ),
                new NestedQuest(data),
                new NestedQuest(data),
                new NestedQuest(data),
                new NestedQuest(data)
        );*//*

        *//*Log.v("text", "test");
        for (NestedData x : tree.inOrderView) {
            // System.out.println(x);
            //traverse the tree row wise
            if(x.getPos()==pos){
            }
            Log.d("tree elements", " " + x.getPos() + "-" + x.getSize() + "-" + x.getSurveyId());
        }*//*
    // }


    public void oldLogic(int pos, long id, int parentPos) {

        boolean newEntry = true;
        Survey survey = realm.where(Survey.class).equalTo("id", id).findFirst();

        // survey is assigned for nested entry
        if (survey != null) {
            int surveysize = survey.getQuestions().size();

            if (addQueHashMap.size() > 0) {

                //check hashmap entry
                for (Map.Entry m : addQueHashMap.entrySet()) {
                    MNestedAddQue nestedAddQue = (MNestedAddQue) m.getValue();
                    if (nestedAddQue.getPos() == pos) {

                        //delete question from that position
                        deleteQuestions(pos);

                        //update hashmap values
                        if (nestedAddQue.getSurveyId() != id) {
                            addToHashMap(id, pos, surveysize, parentPos, 0);
                            newEntry = false;
                        }
                    }

                    //update parent hashmap values
                    if (nestedAddQue.getPos() == parentPos) {
                        addToHashMap(nestedAddQue.getSurveyId(), nestedAddQue.getPos(), nestedAddQue.getSurveyLengh(), nestedAddQue.getParentPos(), surveysize);
                    }
                }
            }

            // add new entry in hashmap
            if (newEntry) {
                addToHashMap(id, pos, survey.getQuestions().size(), parentPos, 0);
            }

            addNewQuestion(pos, survey);

            mAdapter.notifyDataSetChanged();
            showHashmap();
        } else {//no survey is assigned to nested survey
            if (addQueHashMap.size() > 0) {
                for (Map.Entry m : addQueHashMap.entrySet()) {
                    MNestedAddQue nestedAddQue = (MNestedAddQue) m.getValue();

                    if (nestedAddQue.getPos() == pos) {
                        deleteQuestions(pos);
                        if (nestedAddQue.getSurveyId() != id) {
                            addToHashMap(id, pos, 0, parentPos, 0);
                            newEntry = false;
                        }
                    }
                    if (nestedAddQue.getPos() == parentPos) {
                        addToHashMap(nestedAddQue.getSurveyId(), nestedAddQue.getPos(), 0, nestedAddQue.getParentPos(), nestedAddQue.getChildLength());
                    }
                }
            }

            if (newEntry) {
                addToHashMap(id, pos, 0, parentPos, 0);
            }

            mAdapter.notifyDataSetChanged();
            showHashmap();
        }

    }


    public void showHashmap() {

        Log.v("asf", "-------===============-------");

        for (Map.Entry m : addQueHashMap.entrySet()) {
            MNestedAddQue nestedAddQue = (MNestedAddQue) m.getValue();

            Log.v("asdf", "\n---\n Id:" + nestedAddQue.getSurveyId() +
                    "\n Pos:" + nestedAddQue.getPos() +
                    "\n Length:" + nestedAddQue.getSurveyLengh() +
                    "\n Parent Pos:" + nestedAddQue.getParentPos() +
                    "\n Child Length:" + nestedAddQue.getChildLength() + "\n ---");

        }

        Log.v("asf", "-------===============-------");
    }


    public void addToHashMap(long id, int pos, int length, int parentPos, int childLength) {

        MNestedAddQue nestedAddQue = new MNestedAddQue();
        nestedAddQue.setSurveyId(id);
        nestedAddQue.setPos(pos);
        nestedAddQue.setSurveyLengh(length);
        nestedAddQue.setParentPos(parentPos);
        nestedAddQue.setChildLength(childLength);
        addQueHashMap.put(pos, nestedAddQue);

    }

    public void addNewQuestion(int pos, Survey survey) {
        for (int i = 0; i < survey.getQuestions().size(); i++) {

            Answers answ = new Answers();
            answ.setPatientid(0);
            answ.setQuestions(survey.getQuestions().get(i));
            //  answ.setParentPos(pos);
            answ.setSelectedopt(-1);
            answ.setSelectedOptConditional(-1);
            answ.setSelectedChk("");
            answ.setAns("");
            answ.setNumAns("");
            answ.setDate("");
            answ.setTime("");
            byte[] a = {-1};
            answ.setByteArrayImage(a);
            answersList.add((pos + 1) + i, answ);
        }
        // mAdapter.notifyDataSetChanged();
    }

    public void deleteQuestions(int pos) {
        if (addQueHashMap.size() > 0) {

            MNestedAddQue nestedAddQue = addQueHashMap.get(pos);

            if (nestedAddQue != null) {
                //if (nestedAddQue.getPos() == pos) {
                int totalSize = nestedAddQue.getSurveyLengh() + nestedAddQue.getChildLength();

                int count = 0;
                if (count < totalSize && (nestedAddQue.getPos() + 1) < (answersList.size() - 1)) {
                    for (int i = nestedAddQue.getPos() + 1; i <= (nestedAddQue.getPos() + totalSize); i++) {
                        answersList.remove(nestedAddQue.getPos() + 1);
                        count++;
                    }
                }
            }
        }
    }*/

















