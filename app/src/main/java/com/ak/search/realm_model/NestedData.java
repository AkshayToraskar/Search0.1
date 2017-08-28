package com.ak.search.realm_model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dg hdghfd on 28-07-2017.
 */

public class NestedData extends RealmObject {

    @PrimaryKey
    long id;

    int pos;
    long  questionId;


    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
