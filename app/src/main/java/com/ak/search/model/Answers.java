package com.ak.search.model;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dg hdghfd on 14-12-2016.
 */

public class Answers extends RealmObject {


    long patientid;
    int selectedopt;
    String ans;
    Questions questions;


    public Questions getQuestions() {
        return questions;
    }

    public void setQuestions(Questions questions) {
        this.questions = questions;
    }





    public long getPatientid() {
        return patientid;
    }

    public void setPatientid(long patientid) {
        this.patientid = patientid;
    }

    public int getSelectedopt() {
        return selectedopt;
    }

    public void setSelectedopt(int selectedopt) {
        this.selectedopt = selectedopt;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }
}
