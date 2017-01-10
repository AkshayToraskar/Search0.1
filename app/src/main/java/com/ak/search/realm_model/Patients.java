package com.ak.search.realm_model;


import org.parceler.Parcel;

import io.realm.PatientsRealmProxy;
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
}
