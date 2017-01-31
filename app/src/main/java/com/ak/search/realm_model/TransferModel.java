package com.ak.search.realm_model;

import com.ak.search.model.MPatients;
import com.ak.search.model.MSurvey;
import com.ak.search.model.MUser;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dg hdghfd on 10-01-2017.
 */

public class TransferModel implements Serializable {

    String name;
    //  List<Test1> test1List;

    List<User> userList;
    List<Patients> patientsList;
    List<Survey> surveyList;
    List<DataCollection> dataCollectionList;


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


    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public List<Patients> getPatientsList() {
        return patientsList;
    }

    public void setPatientsList(List<Patients> patientsList) {
        this.patientsList = patientsList;
    }

    public List<Survey> getSurveyList() {
        return surveyList;
    }

    public void setSurveyList(List<Survey> surveyList) {
        this.surveyList = surveyList;
    }

    public List<DataCollection> getDataCollectionList() {
        return dataCollectionList;
    }

    public void setDataCollectionList(List<DataCollection> dataCollectionList) {
        this.dataCollectionList = dataCollectionList;
    }
}
