package com.ak.search.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.ak.search.R;
import com.ak.search.activity.SelectPatientsActivity;
import com.ak.search.activity.StartSurveyActivity;
import com.ak.search.adapter.GetQuestionsAdapter;
import com.ak.search.realm_model.Answers;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.Questions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by dg hdghfd on 25-07-2017.
 *
 * scan the survey and import and export the data into csv format
 *
 */

public class CsvOperation {

    List<DataCollection> dataCollection;
    List<Questions> lstQuestions;

    List<String> strAns = new ArrayList<>();

    List<String[]> strData = new ArrayList<>();


    //for generating ans
    public CsvOperation(List<DataCollection> dataCollection, long surveyId) {
        this.dataCollection = dataCollection;

        TraverseNode trv = new TraverseNode(surveyId);
        lstQuestions = trv.setupAllNodes();
    }


    //for generating questions
    public CsvOperation(long surveyId) {

        TraverseNode trv = new TraverseNode(surveyId);
        lstQuestions = trv.setupAllNodes();
    }


    //for surveyQuestion



    //for answers
    public List<String[]> generateString() {


        List<String> st = scanHeader();
        String[] head = st.toArray(new String[st.size()]);

        strData.add(head);

        //iterate data collection
        for (DataCollection dataColl : dataCollection) {


            List<String> dataCollection = new ArrayList<>();
            long prevQ = -1;
            //iterate each questions
            for (Questions questions : lstQuestions) {

                boolean match = false;

                if (prevQ != questions.getId()) {
                    //iterate answers
                    for (Answers ans : dataColl.getAnswerses()) {


                        if (questions.getId() == ans.getQuestions().getId()) {
                            prevQ = questions.getId();
                            List<String> aa = scanQuestion(ans);
                            dataCollection.addAll(aa);
                            match = true;
                        }


                    }

                    if (!match) {
                        dataCollection.add("0");
                    }
                }
            }

            String arr[] = dataCollection.toArray(new String[dataCollection.size()]); //.split(",");
            strData.add(arr);
        }

        return strData;
    }


    public List<String> scanQuestion(Answers answers) {

        List<String> strLs = new ArrayList<>();

        String questionType = answers.getQuestions().getTypeQuestion();
        String[] quest = questionType.split(",");
        for (int l = 0; l < quest.length; l++) {
            int que = Integer.parseInt(quest[l]);
            switch (que) {

                //Text---------------------------------------
                case 1:
                    strLs.add("" + answers.getAns());

                    break;

                //Number---------------------------------------
                case 2:
                    strLs.add("" + answers.getNumAns());

                    break;

                //Date---------------------------------------
                case 3:
                    strLs.add("" + answers.getDate());

                    break;

                //Time---------------------------------------
                case 4:
                    strLs.add("" + answers.getTime());

                    break;

                //Image---------------------------------------
                case 5:
                    strLs.add("Image");

                    break;

                //patient name---------------------------------------
                case 6:
                    strLs.add("Patient Name");

                    break;

                //Checkbox---------------------------------------
                case 7:

                    for (int i = 0; i < answers.getQuestions().getChkb().size(); i++) {


                        String a = answers.getSelectedChk();
                        if (a != null) {
                            String val = String.valueOf(a.charAt(i));

                            if (val.equals("1")) {
                                strLs.add("1");
                            } else {
                                strLs.add("0");
                            }
                        }


                    }

                    break;

                //Options-----------------------------------------
                case 8:


                    Log.v("TAG", "RB " + answers.getQuestions().getOptions().size());
                    for (int i = 0; i < answers.getQuestions().getOptions().size(); i++) {

                        if (answers.getSelectedopt() == i) {
                            strLs.add("1");
                        } else {
                            strLs.add("0");
                        }
                    }


                    break;


                //Conditional---------------------------------------
                case 9:

                    Log.v("TAG", "CONDITIONAL " + answers.getQuestions().getOptionContidion().size());
                    for (int i = 0; i < answers.getQuestions().getOptionContidion().size(); i++) {

                        if (answers.getSelectedOptConditional() == i) {
                            strLs.add("1");
                        } else {
                            strLs.add("0");
                        }
                    }

                    break;

            }
        }

        return strLs;
    }


    public List<String> scanHeader() {

        List<String> strH = new ArrayList<>();
        for (Questions questionData : lstQuestions) {
            String qId = String.valueOf(questionData.getId());

            String questionType = questionData.getTypeQuestion();
            String[] quest = questionType.split(",");
            for (int l = 0; l < quest.length; l++) {
                int que = Integer.parseInt(quest[l]);
                switch (que) {

                    //Text---------------------------------------
                    case 1:
                        strH.add(qId + "_ans");

                        break;

                    //Number---------------------------------------
                    case 2:
                        strH.add(qId + "_num_ans");

                        break;

                    //Date---------------------------------------
                    case 3:
                        strH.add(qId + "_date");

                        break;

                    //Time---------------------------------------
                    case 4:
                        strH.add(qId + "_time");

                        break;

                    //Image---------------------------------------
                    case 5:
                        strH.add(qId + "_Image");

                        break;

                    //patient name---------------------------------------
                    case 6:
                        strH.add(qId + "_Patient Name");

                        break;

                    //Checkbox---------------------------------------
                    case 7:

                        for (int i = 0; i < questionData.getChkb().size(); i++) {



                            strH.add(qId + "_op" + i);


                        }

                        break;

                    //Options-----------------------------------------
                    case 8:


                        Log.v("TAG", "RB " + questionData.getOptions().size());
                        for (int i = 0; i < questionData.getOptions().size(); i++) {

                            strH.add(qId + "_op" + i);
                        }


                        break;


                    //Conditional---------------------------------------
                    case 9:

                        Log.v("TAG", "CONDITIONAL " + questionData.getOptionContidion().size());
                        for (int i = 0; i < questionData.getOptionContidion().size(); i++) {

                            strH.add(qId + "_op" + i);
                        }

                        break;

                }
            }
        }
        return strH;
    }


    public List<String[]> generateQuestionString() {
        List<String[]> strQuestData = new ArrayList<>();
        List<String> st = scanQuestionsHeader();
        String[] head = st.toArray(new String[st.size()]);
        strQuestData.add(head);

        String[] queData = strQue.toArray(new String[strQue.size()]);
        strQuestData.add(queData);

        return strQuestData;
    }

    List<String> strQue = new ArrayList<>();

    public List<String> scanQuestionsHeader() {

        List<String> strH = new ArrayList<>();
        strQue.clear();

        for (Questions questionData : lstQuestions) {
            String qId = String.valueOf(questionData.getId());

            strH.add(qId + "_Q");
            strQue.add(questionData.getQuestion());

            String questionType = questionData.getTypeQuestion();
            String[] quest = questionType.split(",");
            for (int l = 0; l < quest.length; l++) {
                int que = Integer.parseInt(quest[l]);
                switch (que) {

                    //Text---------------------------------------
                    case 1:
                        strH.add(qId + "_ans");
                        strQue.add("Text");
                        break;

                    //Number---------------------------------------
                    case 2:
                        strH.add(qId + "_num_ans");
                        strQue.add("Number");
                        break;

                    //Date---------------------------------------
                    case 3:
                        strH.add(qId + "_date");
                        strQue.add("Date");
                        break;

                    //Time---------------------------------------
                    case 4:
                        strH.add(qId + "_time");
                        strQue.add("Time");
                        break;

                    //Image---------------------------------------
                    case 5:
                        strH.add(qId + "_Image");
                        strQue.add("Image");
                        break;

                    //patient name---------------------------------------
                    case 6:
                        strH.add(qId + "_Patient_Name");
                        strQue.add("Patient Id");
                        break;

                    //Checkbox---------------------------------------
                    case 7:

                        for (int i = 0; i < questionData.getChkb().size(); i++) {


                            // String a = answers.getSelectedChk();
                            // if (a != null) {
                            //     String val = String.valueOf(a.charAt(i));

                            strH.add(qId + "_op" + i);
                            strQue.add("" + questionData.getChkb().get(i).getOpt());
                            // }


                        }

                        break;

                    //Options-----------------------------------------
                    case 8:


                        Log.v("TAG", "RB " + questionData.getOptions().size());
                        for (int i = 0; i < questionData.getOptions().size(); i++) {

                            strH.add(qId + "_op" + i);
                            strQue.add("" + questionData.getOptions().get(i).getOpt());
                        }


                        break;


                    //Conditional---------------------------------------
                    case 9:

                        Log.v("TAG", "CONDITIONAL " + questionData.getOptionContidion().size());
                        for (int i = 0; i < questionData.getOptionContidion().size(); i++) {

                            strH.add(qId + "_cond_op" + i);
                            strQue.add("" + questionData.getOptionContidion().get(i).getOpt());
                        }

                        break;

                }
            }
        }
        return strH;
    }

}
