package com.ak.search.model;


import java.io.Serializable;

/**
 * Created by dg hdghfd on 14-12-2016.
 */

public class MAnswers implements Serializable {


    long patientid;
    int selectedopt;
    String ans;
    MQuestions MQuestions;


    public MQuestions getMQuestions() {
        return MQuestions;
    }

    public void setMQuestions(MQuestions MQuestions) {
        this.MQuestions = MQuestions;
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
