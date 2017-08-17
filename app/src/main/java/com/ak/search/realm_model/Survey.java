package com.ak.search.realm_model;


import com.ak.search.app.RealmListParcelConverter;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import io.realm.RealmList;
import io.realm.RealmObject;

/*import io.realm.SurveyRealmProxy;*/
import io.realm.annotations.PrimaryKey;

/**
 * Created by dg hdghfd on 01-12-2016.
 */

/*@Parcel(implementations = { SurveyRealmProxy.class },
        value = Parcel.Serialization.FIELD,
        analyze = { Survey.class })*/
public class Survey extends RealmObject {

    @PrimaryKey
    long id;
    boolean nested;
    String name;

    @ParcelPropertyConverter(RealmListParcelConverter.class)
    RealmList<Questions> questions;

    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public RealmList<Questions> getQuestions() {
        return questions;
    }

    public void setQuestions(RealmList<Questions> questions) {
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean getNested() {
        return nested;
    }

    public void setNested(boolean nested) {
        this.nested = nested;
    }
}
