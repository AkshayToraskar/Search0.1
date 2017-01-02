package com.ak.search.model;


import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dg hdghfd on 01-12-2016.
 */

public class Survey extends RealmObject {

    @PrimaryKey
    long id;

    String name;
    RealmList<Questions> questions;

    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public RealmList<Questions> getQuestions() {
        return questions;
    }

    public void setQuestions(RealmList<Questions> questions) {
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
