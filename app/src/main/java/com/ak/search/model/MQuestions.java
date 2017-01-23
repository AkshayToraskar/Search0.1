package com.ak.search.model;


import java.io.Serializable;
import java.util.List;

/**
 * Created by dg hdghfd on 01-12-2016.
 */

public class MQuestions implements Serializable {

    long id;

    String question;

    List<MOptions> options;

    List<MOptions> chkb;

    Boolean text, number, date, time, image, compulsary, opt, checkbox;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<MOptions> getOptions() {
        return options;
    }

    public void setOptions(List<MOptions> options) {
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


    public List<MOptions> getChkb() {
        return chkb;
    }

    public void setChkb(List<MOptions> chkb) {
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
}
