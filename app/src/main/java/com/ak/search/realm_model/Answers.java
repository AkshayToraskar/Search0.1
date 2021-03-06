package com.ak.search.realm_model;


import io.realm.RealmObject;

/**
 * Created by dg hdghfd on 14-12-2016.
 */

public class Answers extends RealmObject {


    long patientid;
    int selectedopt,selectedOptConditional,parentPos;
    String selectedChk;
    String ans,numAns,date,time;
    Questions questions;
    byte[] byteArrayImage;

    boolean answered;

    public Questions getQuestions() {
        return questions;
    }

    public void setQuestions(Questions questions) {
        this.questions = questions;
    }


    public boolean getAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
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

    public String getSelectedChk() {
        return selectedChk;
    }

    public void setSelectedChk(String selectedChk) {
        this.selectedChk = selectedChk;
    }

    public String getNumAns() {
        return numAns;
    }

    public void setNumAns(String numAns) {
        this.numAns = numAns;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public byte[] getByteArrayImage() {
        return byteArrayImage;
    }

    public void setByteArrayImage(byte[] byteArrayImage) {
        this.byteArrayImage = byteArrayImage;
    }

    public int getParentPos() {
        return parentPos;
    }

    public void setParentPos(int parentPos) {
        this.parentPos = parentPos;
    }

    public int getSelectedOptConditional() {
        return selectedOptConditional;
    }

    public void setSelectedOptConditional(int selectedOptConditional) {
        this.selectedOptConditional = selectedOptConditional;
    }
}
