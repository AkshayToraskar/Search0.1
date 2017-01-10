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

    Boolean text, opt;


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
}
