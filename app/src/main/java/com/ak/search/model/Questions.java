package com.ak.search.model;


import com.ak.search.app.RealmListParcelConverter;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import java.io.Serializable;
import java.util.List;

import io.realm.QuestionsRealmProxy;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dg hdghfd on 01-12-2016.
 */

@Parcel(implementations = { QuestionsRealmProxy.class },
        value = Parcel.Serialization.FIELD,
        analyze = { Questions.class })
public class Questions extends RealmObject{

    @PrimaryKey
    long id;

    String question;
    @ParcelPropertyConverter(RealmListParcelConverter.class)
    RealmList<Options> options;

    Boolean text, opt;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RealmList<Options> getOptions() {
        return options;
    }

    public void setOptions(RealmList<Options> options) {
        this.options = options;
    }



    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

   public Boolean getText() {
        return text;
    }

    public void setText(Boolean text) {
        this.text = text;
    }

    public Boolean getOpt() {
        return opt;
    }

    public void setOpt(Boolean opt) {
        this.opt = opt;
    }
}
