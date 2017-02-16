package com.ak.search.model;

/**
 * Created by dg hdghfd on 15-02-2017.
 */

public class MNestedAddQue {
    int pos, lengh, parentPos, childLength;
    long surveyId;

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getLengh() {
        return lengh;
    }

    public void setLengh(int lengh) {
        this.lengh = lengh;
    }

    public long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(long surveyId) {
        this.surveyId = surveyId;
    }

    public int getParentPos() {
        return parentPos;
    }

    public void setParentPos(int parentPos) {
        this.parentPos = parentPos;
    }

    public int getChildLength() {
        return childLength;
    }

    public void setChildLength(int childLength) {
        this.childLength = childLength;
    }
}
