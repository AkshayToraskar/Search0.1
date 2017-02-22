package com.ak.search.model;


import android.net.sip.SipErrorCode;

import org.parceler.Parcel;

import java.io.Serializable;

//import io.realm.PatientsRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dg hdghfd on 15-12-2016.
 */


public class MPatients implements Serializable {

    long id;
    String patientname;
    String Address;
    int sex, age;

    //RealmList<MAnswers> answers;

   /* public RealmList<MAnswers> getAnswers() {
        return answers;
    }

    public void setAnswers(RealmList<MAnswers> answers) {
        this.answers = answers;
    }*/

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
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

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
