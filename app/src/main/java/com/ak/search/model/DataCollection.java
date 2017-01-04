package com.ak.search.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dg hdghfd on 04-01-2017.
 */

public class DataCollection extends RealmObject {

    @PrimaryKey
    long id;
    long surveyid;

    Patients patients;
    RealmList<Answers> answerses;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSurveyid() {
        return surveyid;
    }

    public void setSurveyid(long surveyid) {
        this.surveyid = surveyid;
    }

    public Patients getPatients() {
        return patients;
    }

    public void setPatients(Patients patients) {
        this.patients = patients;
    }

    public RealmList<Answers> getAnswerses() {
        return answerses;
    }

    public void setAnswerses(RealmList<Answers> answerses) {
        this.answerses = answerses;
    }
}
