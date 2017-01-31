package com.ak.search.model;

import com.ak.search.realm_model.Survey;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dg hdghfd on 10-01-2017.
 */

public class MTransferModel implements Serializable{

    String name;
  //  List<Test1> test1List;

    List<MUser> userList;
    List<MPatients> patientsList;
    List<MSurvey> surveyList;

    List<MDataCollection> dataCollectionsList;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   /* public List<Test1> getTest1List() {
        return test1List;
    }

    public void setTest1List(List<Test1> test1List) {
        this.test1List = test1List;
    }*/


    public List<MUser> getUserList() {
        return userList;
    }

    public void setUserList(List<MUser> userList) {
        this.userList = userList;
    }

    public List<MPatients> getPatientsList() {
        return patientsList;
    }

    public void setPatientsList(List<MPatients> patientsList) {
        this.patientsList = patientsList;
    }

    public List<MSurvey> getSurveyList() {
        return surveyList;
    }

    public void setSurveyList(List<MSurvey> surveyList) {
        this.surveyList = surveyList;
    }

    public List<MDataCollection> getDataCollectionsList() {
        return dataCollectionsList;
    }

    public void setDataCollectionsList(List<MDataCollection> dataCollectionsList) {
        this.dataCollectionsList = dataCollectionsList;
    }
}
