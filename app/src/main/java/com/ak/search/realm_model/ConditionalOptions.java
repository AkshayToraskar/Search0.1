package com.ak.search.realm_model;


import org.parceler.Parcel;

import io.realm.ConditionalOptionsRealmProxy;
import io.realm.OptionsRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dg hdghfd on 01-12-2016.
 */

@Parcel(implementations = {ConditionalOptionsRealmProxy.class },
        value = Parcel.Serialization.FIELD,
        analyze = { ConditionalOptions.class })
public class ConditionalOptions extends RealmObject {

    @PrimaryKey
    long id;
    String opt;
    long surveyid;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public long getSurveyid() {
        return surveyid;
    }

    public void setSurveyid(long surveyid) {
        this.surveyid = surveyid;
    }
}
