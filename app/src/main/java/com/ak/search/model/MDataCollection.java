package com.ak.search.model;

import java.util.List;

/**
 * Created by dg hdghfd on 04-01-2017.
 */

public class MDataCollection {


    long id;
    long surveyid;

    MPatients MPatients;
    List<MAnswers> answerses;

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

    public MPatients getMPatients() {
        return MPatients;
    }

    public void setMPatients(MPatients MPatients) {
        this.MPatients = MPatients;
    }

    public List<MAnswers> getAnswerses() {
        return answerses;
    }

    public void setAnswerses(List<MAnswers> answerses) {
        this.answerses = answerses;
    }
}
