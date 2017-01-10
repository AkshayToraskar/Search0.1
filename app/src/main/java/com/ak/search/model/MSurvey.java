package com.ak.search.model;


import java.io.Serializable;
import java.util.List;

/**
 * Created by dg hdghfd on 01-12-2016.
 */


public class MSurvey implements Serializable {

    long id;
    String name;
    List<MQuestions> questions;

    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }

    public List<MQuestions> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MQuestions> questions) {
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
