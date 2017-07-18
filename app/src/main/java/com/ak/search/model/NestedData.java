package com.ak.search.model;

/**
 * Created by dg hdghfd on 11-07-2017.
 */

public class NestedData {

    int pos, size;
    long surveyId, questionId;

    public NestedData(int pos, int size, long surveyId, long questionId){
        this.pos=pos;
        this.size=size;
        this.surveyId=surveyId;
        this.questionId=questionId;
    }

    public NestedData(long questionId){
        this.questionId=questionId;
    }


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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(long surveyId) {
        this.surveyId = surveyId;
    }
}
