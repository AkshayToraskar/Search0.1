package com.ak.search.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dg hdghfd on 04-01-2017.
 */

public class MDataCollection implements Serializable {


    long id;
    long surveyid, fieldworkerId, superwiserId;

    String timestamp;
    long lat, lng;

    MPatients patients;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public long getLat() {
        return lat;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public long getLng() {
        return lng;
    }

    public void setLng(long lng) {
        this.lng = lng;
    }

    public MPatients getPatients() {
        return patients;
    }

    public void setPatients(MPatients patients) {
        this.patients = patients;
    }

    public List<MAnswers> getAnswerses() {
        return answerses;
    }

    public void setAnswerses(List<MAnswers> answerses) {
        this.answerses = answerses;
    }

    public long getFieldworkerId() {
        return fieldworkerId;
    }

    public void setFieldworkerId(long fieldworkerId) {
        this.fieldworkerId = fieldworkerId;
    }

    public long getSuperwiserId() {
        return superwiserId;
    }

    public void setSuperwiserId(long superwiserId) {
        this.superwiserId = superwiserId;
    }
}
