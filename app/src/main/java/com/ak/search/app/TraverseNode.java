package com.ak.search.app;

import android.util.Log;

import com.ak.search.model.MyTreeNode;
import com.ak.search.model.NestedData;
import com.ak.search.realm_model.Answers;
import com.ak.search.realm_model.Questions;
import com.ak.search.realm_model.Survey;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by dg hdghfd on 26-07-2017.
 */

public class TraverseNode {

    long surveyId;
    public MyTreeNode<NestedData> root;
    List<Long> questionIdList = new ArrayList<>();
    List<Questions> questionList = new ArrayList<>();
    Realm realm;

    public TraverseNode(Long surveyId) {
        this.surveyId = surveyId;
        root = new MyTreeNode<>(null);
        realm = Realm.getDefaultInstance();
    }


    public void traverse(MyTreeNode child) { // post order traversal
        List<MyTreeNode> as = child.getChildren();
        for (MyTreeNode ch : as) {
            NestedData ns = (NestedData) ch.getData();
            questionIdList.add(ns.getQuestionId());
            traverse(ch);
        }
    }




    ///setup all nodes
    public List<Questions> setupAllNodes() {
        setupNestedData(root, 0, surveyId);
        return questionList;
    }

    public void setupNestedData(MyTreeNode myTree, int pos, long surveyId) {

        List<MyTreeNode> qList = new LinkedList<>();

        Survey sur = realm.where(Survey.class).equalTo("id", surveyId).findFirst();
        if (sur != null) {
            List<Questions> queList = sur.getQuestions();
            questionList.addAll(queList);
            for (int i = pos; i < pos + queList.size(); i++) {
                Questions que = queList.get(i);
                if (que != null) {
                    NestedData nestedData = new NestedData(que.getId(), i);
                    MyTreeNode myTreeNode = new MyTreeNode(nestedData);
                    qList.add(myTreeNode);

                    if (que.getOptCondition()) {
                        for (int j = 0; j < que.getOptionContidion().size(); j++) {
                            setupNestedData(myTreeNode, pos, que.getOptionContidion().get(j).getSurveyid());
                        }
                    }

                }

            }
        }

        myTree.addChildren(qList);
        Log.v("root size", "" + root.getChildren().size());
    }

}