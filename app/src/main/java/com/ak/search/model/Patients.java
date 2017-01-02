package com.ak.search.model;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dg hdghfd on 15-12-2016.
 */

public class Patients extends RealmObject {
    @PrimaryKey
    long id;

    long surveyid;

    String patientname;

    Answers answers;

    public Answers getAnswers() {
        return answers;
    }

    public void setAnswers(Answers answers) {
        this.answers = answers;
    }

    public void setSurveyid(long surveyid) {
        this.surveyid = surveyid;
    }

    public long getSurveyid() {
        return surveyid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getPatientname() {
        return patientname;
    }

    public void setPatientname(String patientname) {
        this.patientname = patientname;
    }
}
