package com.ak.search.model;


import org.parceler.Parcel;

import java.io.Serializable;
import java.util.List;

import io.realm.PatientsRealmProxy;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dg hdghfd on 15-12-2016.
 */

@Parcel(implementations = { PatientsRealmProxy.class },
        value = Parcel.Serialization.FIELD,
        analyze = { Patients.class })
public class Patients extends RealmObject{
    @PrimaryKey
    long id;
    String patientname;
    String Address;

    //RealmList<Answers> answers;

   /* public RealmList<Answers> getAnswers() {
        return answers;
    }

    public void setAnswers(RealmList<Answers> answers) {
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
}
