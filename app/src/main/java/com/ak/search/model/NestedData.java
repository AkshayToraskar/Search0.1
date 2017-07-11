package com.ak.search.model;

/**
 * Created by dg hdghfd on 11-07-2017.
 */

public class NestedData {

    int pos, size;
    long surveyId;

    public NestedData(int pos, int size, long surveyId){
        this.pos=pos;
        this.size=size;
        this.surveyId=surveyId;
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
