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
import com.ak.search.realm_model.TreeString;
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
    boolean superviserLogin;

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

        Long collectionId;
        superviserLogin = false;

        if (getIntent().getExtras() != null) {

            update = getIntent().getExtras().getBoolean("update");
            root = new MyTreeNode<>(null);

            if (update) {
                superviserLogin = getIntent().getExtras().getBoolean("superviserLogin");
                collectionId = getIntent().getExtras().getLong("collectionid");


                dataCollection = realm.where(DataCollection.class).equalTo("id", collectionId).findFirst();
                answersList.clear();

                generateTreeNode();

                survey = realm.where(Survey.class).equalTo("id", dataCollection.getSurveyid()).findFirst();
                if (survey != null) {
                    getSupportActionBar().setTitle(survey.getName());
                }

                addQue(dataCollection.getAnswerses());

                mAdapter = new GetQuestionsAdapter(this, answersList, saveAnswer, realm, superviserLogin);

            } else {

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
                answ.setAns(null);
                answ.setNumAns(null);
                answ.setDate(null);
                answ.setTime(null);
                byte[] a = {};
                answ.setByteArrayImage(a);
                answersList.add(answ);

                mAdapter = new GetQuestionsAdapter(this, answersList, saveAnswer, realm, true);

                setupNestedData(root, 0, answersList.size());
            }

            tvCounter.setText("0 of " + (answersList.size() - 1) + " Questions Answered");


            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);


        }
    }


    public void discardSurvey() {

        if (!update || superviserLogin) {
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
                            // user doesn't want to discard survey
                        }
                    })
                    .show();
        } else {
            finish();
        }
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

    }


    @Override
    public void saveCollection() {

        saveTree();

        if (update) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {


                    RealmList<Answers> answerses = new RealmList<>();
                    for (Answers a : answersList) {

                        Answers ans = realm.createObject(Answers.class);
                        if (a.getQuestions() != null) {
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
                    }

                    Patients patients1 = null;

                    if (dataCollection.getPatients() != null) {
                        patients1 = dataCollection.getPatients();
                    }

                    RealmList<TreeString> treeStrings = new RealmList<TreeString>();
                    for (String str : aa) {
                        TreeString trData = realm.createObject(TreeString.class);
                        trData.setTreeData(str);
                        treeStrings.add(trData);
                    }


                    dataCollection.setPatients(patients1);
                    dataCollection.setAnswerses(answerses);
                    dataCollection.setTreeData(treeStrings);

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
        } else {


            Log.v("asdf", "a" + aa.size());

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

                    RealmList<TreeString> treeStrings = new RealmList<TreeString>();
                    for (String str : aa) {
                        TreeString trData = realm.createObject(TreeString.class);
                        trData.setTreeData(str);
                        treeStrings.add(trData);
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
                    dataCollection.setTreeData(treeStrings);

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
    }


    List<String> aa = new ArrayList<>();

    public void saveTree() {
        for (MyTreeNode x : root.inOrderView) {
            treeString.clear();
            if (x.getData() != null) {
                traverseTree(x);
                String a = treeString.toString();
                aa.add(a);
            }
        }
    }

    List<String> treeString = new ArrayList<>();

    public void traverseTree(MyTreeNode child) { // post order traversal

        NestedData nsD = (NestedData) child.getData();
        NestedData nsParentD = (NestedData) child.getParent().getData();
        if (nsParentD == null) {
            //parentQuestionId:postion:qustionId
            String dataD = 0 + ":" + nsD.getPos() + ":" + nsD.getQuestionId();
            treeString.add(dataD);
        }

        List<MyTreeNode> as = child.getChildren();
        for (MyTreeNode ch : as) {

            NestedData ns = (NestedData) ch.getData();
            NestedData nsParent = (NestedData) ch.getParent().getData();

            //parentQuestionId:postion:qustionId
            String data = nsParent.getQuestionId() + ":" + ns.getPos() + ":" + ns.getQuestionId();
            treeString.add(data);
            traverseTree(ch);
        }
    }


    @Override
    public void scrollToError(int pos) {
        recyclerView.getLayoutManager().scrollToPosition(pos);
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
        //super.onBackPressed();
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

        Survey surveyN = realm.where(Survey.class).equalTo("id", surveyId).findFirst();
        boolean qFlag = false;

        for (MyTreeNode x : root.inOrderView) {

            //traverse the tree row wise
            if (x.getData() != null) {

                NestedData nsd = (NestedData) x.getData();
                Log.d("questionId", "" + questionId + " eleQueId:" + nsd.getQuestionId() + ", Position:" + pos + " elePOs:" + nsd.getPos());
                Log.d("tree elements", " size:" + nsd.getSize() + ", SurveyId:" + nsd.getSurveyId());


                if (nsd.getQuestionId() == questionId) {
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


    protected void postAndNotifyAll(final Handler handler, final RecyclerView recyclerView, final RecyclerView.Adapter adapter) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!recyclerView.isComputingLayout()) {
                    adapter.notifyDataSetChanged();
                    handler.removeCallbacks(this);
                } else {
                    postAndNotifyAll(handler, recyclerView, adapter);
                }
            }
        });
    }

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

    public void traverse(MyTreeNode child) { // post order traversal
        List<MyTreeNode> as = child.getChildren();
        for (MyTreeNode ch : as) {


            NestedData ns = (NestedData) ch.getData();

            questionIdList.add(ns.getQuestionId());

            traverse(ch);
        }

    }


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
            answ.setAns(null);
            answ.setNumAns(null);
            answ.setDate(null);
            answ.setTime(null);
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

        node.addChild(nestedData);
        Log.v("root size", "" + root.getChildren().size());

    }


    //update survey
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

        if (superviserLogin) {
            Answers answ = new Answers();
            answ.setPatientid(0);
            answ.setQuestions(null);
            answ.setParentPos(0);
            answ.setSelectedopt(-1);
            answ.setSelectedOptConditional(-1);
            answ.setSelectedChk("");
            answ.setAns(null);
            answ.setNumAns(null);
            answ.setDate(null);
            answ.setTime(null);
            byte[] a = {};
            answ.setByteArrayImage(a);
            answersList.add(answ);
        }

    }

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


                        boolean present = false;

                        for (MyTreeNode x : root.inOrderView) {

                            if (x.getData() != null && present == false) {
                                NestedData ns = (NestedData) x.getData();

                                List<MyTreeNode> child = x.getChildren();
                                boolean flagItem = false;

                                if (child != null || child.size() > 0) {
                                    for (MyTreeNode nn : child) {

                                        NestedData nd = (NestedData) nn.getData();

                                        if (nd.getQuestionId() == questionId && nd.getPos() == position) {
                                            flagItem = true;
                                            present = true;
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


}
