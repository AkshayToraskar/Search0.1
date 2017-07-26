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
 */

public class CsvOperation {

    List<DataCollection> dataCollection;
    List<Questions> lstQuestions;

    List<String> strAns = new ArrayList<>();

    List<String[]> strData = new ArrayList<>();


    public CsvOperation(List<DataCollection> dataCollection, long surveyId) {
        this.dataCollection = dataCollection;

        TraverseNode trv = new TraverseNode(surveyId);
        lstQuestions = trv.setupAllNodes();


    }

    public List<String[]> generateString() {


        List<String> st = scanHeader();
        String headerString = "";
        for (int i = 0; i < st.size(); i++) {
            headerString = headerString + "," + st.get(i);
        }

        String[] head = headerString.split(",");

        strData.add(head);

        for (DataCollection dataColl : dataCollection) {


            String dataCollect = "";
            for (Answers ans : dataColl.getAnswerses()) {
                String answw = "";
                List<String> aa = scanQuestion(ans);
                for (int i = 0; i < aa.size(); i++) {
                    answw = answw + "," + aa.get(i);
                }

                dataCollect = dataCollect + answw;

            }
            String arr[] = dataCollect.split(",");
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
                    strLs.add(answers.getAns());

                    break;

                //Number---------------------------------------
                case 2:
                    strLs.add(answers.getNumAns());

                    break;

                //Date---------------------------------------
                case 3:
                    strLs.add(answers.getDate());

                    break;

                //Time---------------------------------------
                case 4:
                    strLs.add(answers.getTime());

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


                            // String a = answers.getSelectedChk();
                            // if (a != null) {
                            //     String val = String.valueOf(a.charAt(i));

                            strH.add(qId + "_op" + i);
                            // }


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


}
