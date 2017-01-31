package com.ak.search.model;


import java.io.Serializable;

/**
 * Created by dg hdghfd on 14-12-2016.
 */

public class MAnswers implements Serializable {


    long patientid;
    int selectedopt;
    String selectedChk;
    String ans,numAns,date,time;
    MQuestions MQuestions;
    byte[] byteArrayImage;


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
}
