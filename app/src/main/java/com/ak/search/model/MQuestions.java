package com.ak.search.model;


import java.io.Serializable;
import java.util.List;

/**
 * Created by dg hdghfd on 01-12-2016.
 */

public class MQuestions implements Serializable {

    long id;

    String question, typeQuestion;

    List<MOptions> options;

    List<MOptions> chkb;
    List<MConditionalOptions> optionContidion;

    int question_pos;

    Boolean text, number, date, time, image, compulsary, opt, checkbox, optCondition, patientName;


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

    public String getTypeQuestion() {
        return typeQuestion;
    }

    public void setTypeQuestion(String typeQuestion) {
        this.typeQuestion = typeQuestion;
    }

    public List<MConditionalOptions> getOptionContidion() {
        return optionContidion;
    }

    public void setOptionContidion(List<MConditionalOptions> optionContidion) {
        this.optionContidion = optionContidion;
    }

    public Boolean getOptCondition() {
        return optCondition;
    }

    public void setOptCondition(Boolean optCondition) {
        this.optCondition = optCondition;
    }

    public Boolean getPatientName() {
        return patientName;
    }

    public void setPatientName(Boolean patientName) {
        this.patientName = patientName;
    }

    public int getQuestion_pos() {
        return question_pos;
    }

    public void setQuestion_pos(int question_pos) {
        this.question_pos = question_pos;
    }
}
