package com.ak.search.activity;
/**
 * See and update collected surveys
 * */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.adapter.GetQuestionsAdapter;
import com.ak.search.app.SaveAnswer;
import com.ak.search.app.SessionManager;
import com.ak.search.model.MNestedAddQue;
import com.ak.search.model.MyTreeNode;
import com.ak.search.model.NestedData;
import com.ak.search.realm_model.Answers;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.Questions;
import com.ak.search.realm_model.Survey;
import com.ak.search.realm_model.TreeString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;

public class ShowSurveyActivity extends AppCompatActivity implements SaveAnswer {

    Long collectionId;
    private List<Answers> answersList;
    @BindView(R.id.rv_questions)
    RecyclerView recyclerView;
    public GetQuestionsAdapter mAdapter;
    DataCollection dataCollection;
    Realm realm;
    boolean superviserLogin = false;
    public static int positionImg;
    public static Patients patients;
    public static HashMap<Integer, MNestedAddQue> addQueHashMap;
    SessionManager sessionManager;

    MyTreeNode<NestedData> root;
    Survey survey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_survey);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        answersList = new ArrayList<>();
        addQueHashMap = new HashMap<>();
        sessionManager = new SessionManager(this);

        root = new MyTreeNode<>(null);

        if (getIntent().getExtras() != null) {
            superviserLogin = getIntent().getExtras().getBoolean("superviserLogin");
            collectionId = getIntent().getExtras().getLong("collectionid");
            SaveAnswer saveAnswer = this;

            dataCollection = realm.where(DataCollection.class).equalTo("id", collectionId).findFirst();
            answersList.clear();

            generateTreeNode();

            survey = realm.where(Survey.class).equalTo("id", dataCollection.getSurveyid()).findFirst();
            if (survey != null) {
                getSupportActionBar().setTitle(survey.getName());
            }




            addQue(dataCollection.getAnswerses());



            mAdapter = new GetQuestionsAdapter(this, answersList, saveAnswer, realm, superviserLogin);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());


            recyclerView.setAdapter(mAdapter);
            if (dataCollection.getPatients() != null) {
                getSupportActionBar().setTitle(dataCollection.getPatients().getPatientname() + " ");
            }


            setupNestedData(root, 0, answersList.size());
        }

    }


    //if surveys have nestead questions generate tree node for questions
    public void generateTreeNode() {

        RealmList<TreeString> treeStrings = dataCollection.getTreeData();
        for (int i = 0; i < treeStrings.size(); i++) {

            String str = treeStrings.get(i).getTreeData();
            String vvv = str.substring(1, str.length() - 1);
            String treeVal[] = vvv.split(",");
            if (treeVal != null) {
                for (int j = 0; j < treeVal.length; j++) {

                    String trData[] = treeVal[j].split(":");
                    if (trData.length == 3) {
                        long parentQId = Long.parseLong(trData[0].trim());
                        int position = Integer.parseInt(trData[1].trim());
                        long questionId = Long.parseLong(trData[2].trim());

                        //addNestedData(root, survey.getQuestions().get(i).getId(), pos + i);
                        boolean present = false;

                        for (MyTreeNode x : root.inOrderView) {

                            if (x.getData() != null && present == false) {
                                NestedData ns = (NestedData) x.getData();

                                List<MyTreeNode> child = x.getChildren();
                                boolean flagItem=false;

                                if (child != null || child.size() > 0) {
                                    for(MyTreeNode nn: child){

                                        NestedData nd=(NestedData)nn.getData();

                                        if(nd.getQuestionId()==questionId && nd.getPos()==position){
                                            flagItem=true;
                                            present=true;
                                        }

                                    }
                                }

                                if (ns.getQuestionId() == parentQId && !flagItem) {
                                    addNestedData(x, questionId, position);
                                    present = true;
                                }

                            }
                        }

                        if (!present) {
                            addNestedData(root, questionId, position);
                        }


                    }
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_survey, menu);

        if (!superviserLogin) {
            menu.getItem(0).setVisible(false);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_update_survey:
                updateData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAnswerSave(int index, Answers ans) {
        if (patients != null) {
            ans.setPatientid(patients.getId());
        }

    }

    @Override
    public void onAddSurvey(long id, int pos, int parentPos, long questionId) {
        Survey survey = realm.where(Survey.class).equalTo("id", id).findFirst();
        if (id != 0) {
            newLogic(pos, survey.getQuestions().size(), id, questionId);
        } else {
            newLogic(pos, 0, 0, questionId);
        }
    }

    @Override
    public void saveCollection() {

    }

    @Override
    public void scrollToError(int pos) {
        recyclerView.getLayoutManager().scrollToPosition(pos);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAdapter.onActivityResult(requestCode, resultCode, data, positionImg);
    }

    //add new nested question in survey
    public void addQue(List<Answers> ans) {
        for (int i = 0; i < ans.size(); i++) {
            Answers answ = new Answers();
            answ.setPatientid(ans.get(i).getPatientid());
            answ.setQuestions(ans.get(i).getQuestions());
            answ.setParentPos(0);
            answ.setSelectedopt(ans.get(i).getSelectedopt());
            answ.setSelectedOptConditional(ans.get(i).getSelectedOptConditional());
            answ.setSelectedChk(ans.get(i).getSelectedChk());
            answ.setAns(ans.get(i).getAns());
            answ.setNumAns(ans.get(i).getNumAns());
            answ.setDate(ans.get(i).getDate());
            answ.setTime(ans.get(i).getTime());
            answ.setByteArrayImage(ans.get(i).getByteArrayImage());
            answersList.add(answ);

        }
    }

    // update nested question
    public void newLogic(int pos, int size, long surveyId, long questionId) {

        int nesPos = pos;

        Survey surveyN = realm.where(Survey.class).equalTo("id", surveyId).findFirst();
        boolean qFlag = false;

        for (MyTreeNode x : root.inOrderView) {

            //traverse the tree row wise
            if (x.getData() != null) {

                NestedData nsd = (NestedData) x.getData();
                Log.d("questionId", "" + questionId + " eleQueId:" + nsd.getQuestionId() + ", Position:" + pos + " elePOs:" + nsd.getPos());
                Log.d("tree elements", " size:" + nsd.getSize() + ", SurveyId:" + nsd.getSurveyId());


                if (nsd.getQuestionId() == questionId && nsd.getPos() == pos) {
                    List<MyTreeNode> lstChildren = x.getChildren();
                    qFlag = true;
                    nesPos++;

                    //remove all child of current node
                    traverse(x);

                    for (int i = 0; i < questionIdList.size(); i++) {
                        Log.v("asdf", " " + questionIdList.get(i));
                        deleteQue(questionIdList.get(i));
                    }

                    x.removeChild(lstChildren);
                    questionIdList.clear();


                    // add new child to current node
                    if (surveyId != 0) {
                        addQuestion(nesPos, surveyN, x);
                    }
                }
            }


            Log.d("tree elements", " size:");
        }

        if (!qFlag) {
            nesPos++;
            addQuestion(nesPos, survey, root);
        }

        android.os.Handler mHandler = this.getWindow().getDecorView().getHandler();
        changeItem(mHandler, recyclerView, mAdapter, pos);


    }

    //insert new item in nested survey
    protected void insertItem(final Handler handler, final RecyclerView recyclerView, final RecyclerView.Adapter adapter, final int pos) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!recyclerView.isComputingLayout()) {
                    adapter.notifyItemInserted(pos);
                    handler.removeCallbacks(this);
                } else {
                    insertItem(handler, recyclerView, adapter, pos);
                }
            }
        });
    }

    //remove item from survey
    protected void removeItem(final Handler handler, final RecyclerView recyclerView, final RecyclerView.Adapter adapter, final int pos) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!recyclerView.isComputingLayout()) {
                    adapter.notifyItemRemoved(pos);
                    handler.removeCallbacks(this);
                } else {
                    removeItem(handler, recyclerView, adapter, pos);
                }
            }
        });
    }


    protected void changeItem(final Handler handler, final RecyclerView recyclerView, final RecyclerView.Adapter adapter, final int pos) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!recyclerView.isComputingLayout()) {
                    adapter.notifyItemChanged(pos);
                    handler.removeCallbacks(this);
                } else {
                    removeItem(handler, recyclerView, adapter, pos);
                }
            }
        });
    }


    List<Long> questionIdList = new ArrayList<>();
    // traverse the tree
    public void traverse(MyTreeNode child) { // post order traversal
        List<MyTreeNode> as = child.getChildren();
        for (MyTreeNode ch : as) {

            NestedData ns = (NestedData) ch.getData();
            questionIdList.add(ns.getQuestionId());
            traverse(ch);
        }
    }

    //remove questions from tree and survey
    public void deleteQue(long qId) {

        List<Integer> delData = new ArrayList<>();

        int start = 0;
        for (int i = 0; i < answersList.size(); i++) {
            if (answersList.get(i).getQuestions() != null) {
                if (qId == answersList.get(i).getQuestions().getId()) {
                    if (start == 0) {
                        start = i;
                    }
                    delData.add(i);
                }
            }
        }

        for (int i = start; i < (start + delData.size()); i++) {
            answersList.remove(i);

            android.os.Handler mHandler = this.getWindow().getDecorView().getHandler();

            removeItem(mHandler, recyclerView, mAdapter, i);

        }
    }


    public void addQuestion(final int pos, Survey survey, MyTreeNode node) {

        for (int i = 0; i < survey.getQuestions().size(); i++) {

            Answers answ = new Answers();
            answ.setPatientid(0);
            answ.setQuestions(survey.getQuestions().get(i));
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

            addNestedData(node, survey.getQuestions().get(i).getId(), pos + i);

            android.os.Handler mHandler = this.getWindow().getDecorView().getHandler();
            insertItem(mHandler, recyclerView, mAdapter, pos);

        }

    }




    public void setupNestedData(MyTreeNode myTree, int pos, int size) {

        List<MyTreeNode> qList = new LinkedList<>();

        for (int i = pos; i < pos + size; i++) {
            Answers ans = answersList.get(i);
            if (ans.getQuestions() != null) {

                NestedData nestedData = new NestedData(answersList.get(i).getQuestions().getId(), i);
                MyTreeNode myTreeNode = new MyTreeNode(nestedData);
                qList.add(myTreeNode);


            }

        }


        myTree.addChildren(qList);
        Log.v("root size", "" + root.getChildren().size());
    }

    public void addNestedData(MyTreeNode node, long questionId, int pos) {

        NestedData nestedData = new NestedData(questionId, pos);
        //NestedData nestedData = new NestedData(pos, size, surveyId, questionId);
        node.addChild(nestedData);
        Log.v("root size", "" + root.getChildren().size());

    }


    public void updateData() {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmList<Answers> answerses = new RealmList<>();
                for (int i = 0; i < answersList.size(); i++) {
                    Answers a = answersList.get(i);
                    Answers ans = realm.createObject(Answers.class);

                    Questions questions = realm.where(Questions.class).equalTo("id", a.getQuestions().getId()).findFirst();

                    ans.setQuestions(questions);
                    ans.setPatientid(a.getPatientid());
                    ans.setAns(a.getAns());
                    ans.setSelectedopt(a.getSelectedopt());
                    ans.setNumAns(a.getNumAns());
                    ans.setByteArrayImage(a.getByteArrayImage());
                    ans.setDate(a.getDate());
                    ans.setTime(a.getTime());
                    ans.setSelectedChk(a.getSelectedChk());
                    answerses.add(ans);

                }

                Patients patients1 = null;

                if (dataCollection.getPatients() != null) {
                    patients1 = dataCollection.getPatients();
                }


                dataCollection = realm.where(DataCollection.class).equalTo("id", dataCollection.getId()).findFirst();
                dataCollection.setSurveyid(dataCollection.getSurveyid());
                dataCollection.setPatients(patients1);
                dataCollection.setAnswerses(answerses);


                dataCollection.setFieldworkerId(dataCollection.getFieldworkerId());
                dataCollection.setSuperwiserId(dataCollection.getSuperwiserId());

                String timeStamp = new SimpleDateFormat("dd.MM.yyyy:HH.mm.ss").format(new Date());
                dataCollection.setLat(dataCollection.getLat());
                dataCollection.setLng(dataCollection.getLng());
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
}
