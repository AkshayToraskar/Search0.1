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
                        // user doesn't want to discard survey
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


                if (nsd.getQuestionId() == questionId && nsd.getPos()==pos) {
                    List<MyTreeNode> lstChildren = x.getChildren();

                    qFlag = true;

                    nesPos++;



                    //remove all child of current node

                    traverse(x);

                   /* for (int i = 0; i < lstChildren.size(); i++) {
                        NestedData ns = (NestedData) lstChildren.get(i).getData();
                        //deleteQuestion(ns.getPos(), ns.getSize()-1);
                        deleteQue(ns.getQuestionId());

                        if(lstChildren.get(i).getChildren()!=null){
                            traverse(lstChildren.get(i));
                        }

                    }*/

                   for(int i=0; i<questionIdList.size(); i++) {
                       Log.v("asdf"," "+questionIdList.get(i));
                       deleteQue(questionIdList.get(i));
                   }

                    x.removeChild(lstChildren);

                    questionIdList.clear();



                    // add new child to current node
                    if (surveyId != 0) {
                        // Survey survey = realm.where(Survey.class).equalTo("id", surveyId).findFirst();
                        // NestedData nsd1 = new NestedData(nesPos, survey.getQuestions().size(), surveyId, questionId);
                        // x.addChild(nsd1);
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
        changeItem(mHandler,recyclerView,mAdapter,pos);


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

    protected  void insertItem(final Handler handler, final RecyclerView recyclerView, final RecyclerView.Adapter adapter, final int pos) {
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

    protected  void removeItem(final Handler handler, final RecyclerView recyclerView, final RecyclerView.Adapter adapter, final int pos) {
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


    protected  void changeItem(final Handler handler, final RecyclerView recyclerView, final RecyclerView.Adapter adapter, final int pos) {
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




    List<Long> questionIdList=new ArrayList<>();

    public void traverse(MyTreeNode child){ // post order traversal
        List<MyTreeNode> as=child.getChildren();
        for(MyTreeNode ch : as){

           // for (int i = 0; i < ch.getChildren().size(); i++) {
                NestedData ns = (NestedData) ch.getData();
                //deleteQuestion(ns.getPos(), ns.getSize()-1);
                questionIdList.add(ns.getQuestionId());
                //deleteQue(ns.getQuestionId());
          //  }
            traverse(ch);
        }
        //this.printData();
    }





    public void deleteQue(long qId) {

        List<Integer> delData=new ArrayList<>();

        int start=0;
        for (int i=0; i<answersList.size(); i++) {
            if(answersList.get(i).getQuestions() !=null) {
                if (qId == answersList.get(i).getQuestions().getId()) {
                    //answersList.remove(i);
                    if (start == 0) {
                        start = i;
                    }
                    delData.add(i);
                }
            }
        }

        for(int i=start; i<(start+delData.size());i++){
            answersList.remove(i);

            android.os.Handler mHandler = this.getWindow().getDecorView().getHandler();
            //final int finalI = i;
            /*mHandler.post(new Runnable() {
                public void run(){
                    //change adapter contents
                    mAdapter.notifyItemRemoved(finalI);
                }
            });*/
            removeItem(mHandler,recyclerView,mAdapter,i);

        }
    }


    public void addQuestion(final int pos, Survey survey, MyTreeNode node) {

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

            addNestedData(node, survey.getQuestions().get(i).getId(), pos+i);


            /*final int finalI = i;
            mHandler.post(new Runnable() {
                public void run(){
                    //change adapter contents
                    mAdapter.notifyItemInserted(pos+ finalI);
                }
            });*/
            android.os.Handler mHandler = this.getWindow().getDecorView().getHandler();
            insertItem(mHandler,recyclerView,mAdapter,pos);

            // setupNestedData(node,pos,survey.getQuestions().size());
        }
       //  mAdapter.notifyDataSetChanged();
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
        for (int i = pos; i < pos + size; i++) {
            Answers ans = answersList.get(i);
            if (ans.getQuestions() != null) {
                //  if (ans.getQuestions().getOptCondition()) {
                //NestedData nestedData = new NestedData(i, answersList.size(), survey.getId(), answersList.get(i).getQuestions().getId());
                NestedData nestedData=new NestedData(answersList.get(i).getQuestions().getId(),i);
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

    public void addNestedData(MyTreeNode node, long questionId,int pos) {

        NestedData nestedData = new NestedData(questionId, pos);
        //NestedData nestedData = new NestedData(pos, size, surveyId, questionId);
        node.addChild(nestedData);
        Log.v("root size", "" + root.getChildren().size());

    }


}
