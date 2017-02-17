package com.ak.search.realm_model;


import com.ak.search.app.RealmListParcelConverter;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import io.realm.QuestionsRealmProxy;
import io.realm.RealmList;
import io.realm.RealmObject;
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

    String question, typeQuestion;

    @ParcelPropertyConverter(RealmListParcelConverter.class)
    RealmList<Options> options;

    @ParcelPropertyConverter(RealmListParcelConverter.class)
    RealmList<Options> chkb;

    @ParcelPropertyConverter(RealmListParcelConverter.class)
    RealmList<ConditionalOptions> optionContidion;



    Boolean text, number, date, time, image, compulsary, opt, checkbox, optCondition;


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

    public String getTypeQuestion() {
        return typeQuestion;
    }

    public void setTypeQuestion(String typeQuestion) {
        this.typeQuestion = typeQuestion;
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


    public RealmList<Options> getChkb() {
        return chkb;
    }

    public void setChkb(RealmList<Options> chkb) {
        this.chkb = chkb;
    }

    public Boolean getNumber() {
        return number;
    }

    public void setNumber(Boolean number) {
        this.number = number;
    }

    public Boolean getDate() {
        return date;
    }

    public void setDate(Boolean date) {
        this.date = date;
    }

    public Boolean getTime() {
        return time;
    }

    public void setTime(Boolean time) {
        this.time = time;
    }

    public Boolean getImage() {
        return image;
    }

    public void setImage(Boolean image) {
        this.image = image;
    }

    public Boolean getCompulsary() {
        return compulsary;
    }

    public void setCompulsary(Boolean compulsary) {
        this.compulsary = compulsary;
    }

    public Boolean getCheckbox() {
        return checkbox;
    }

    public void setCheckbox(Boolean checkbox) {
        this.checkbox = checkbox;
    }

    public RealmList<ConditionalOptions> getOptionContidion() {
        return optionContidion;
    }

    public void setOptionContidion(RealmList<ConditionalOptions> optionContidion) {
        this.optionContidion = optionContidion;
    }

    public Boolean getOptCondition() {
        return optCondition;
    }

    public void setOptCondition(Boolean optCondition) {
        this.optCondition = optCondition;
    }
}
