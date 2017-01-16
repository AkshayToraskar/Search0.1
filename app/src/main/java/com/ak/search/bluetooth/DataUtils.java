package com.ak.search.bluetooth;

import com.ak.search.app.ParcebleUtil;
import com.ak.search.model.MOptions;
import com.ak.search.model.MPatients;
import com.ak.search.model.MQuestions;
import com.ak.search.model.MSurvey;
import com.ak.search.model.MUser;
import com.ak.search.model.TransferModel;
import com.ak.search.realm_model.Options;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.Questions;
import com.ak.search.realm_model.Survey;
import com.ak.search.realm_model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by dg hdghfd on 12-01-2017.
 */

public class DataUtils {


    public TransferModel sendData(Realm realm) {

        List<MUser> mUserList = new ArrayList<>();
    //    List<MPatients> mPatientsList = new ArrayList<>();
      //  List<MSurvey> mSurveys = new ArrayList<>();

        List<User> user = realm.where(User.class).findAll();
     //   List<Patients> patients = realm.where(Patients.class).findAll();
       // MSurvey survey=new MSurvey();

        //ConvertUser
        if(user!=null) {
            for (int i = 0; i < user.size(); i++) {
                MUser mu = new MUser();
                mu.setId(user.get(i).getId());
                mu.setName(user.get(i).getName());
                mu.setPassword(user.get(i).getPassword());
                mu.setType(user.get(i).getType());
                mUserList.add(mu);
            }
        }

        //ConvertPatients
      /*  if(patients!=null) {
            for (int i = 0; i < patients.size(); i++) {
                MPatients pa = new MPatients();
                pa.setId(patients.get(i).getId());
                pa.setAddress(patients.get(i).getAddress());
                pa.setPatientname(patients.get(i).getPatientname());
                mPatientsList.add(pa);
            }
        }*/

        //Convert Survey
        /*for(int i=0; i<surveys.size(); i++){
            Survey sur=new Survey();
            sur.setId(surveys.get(i).getId());
            sur.setName(surveys.get(i).getName());
            for(int j=0; j<surveys.get(i).getQuestions().size(); j++){
            }
        }*/


        TransferModel transModel = new TransferModel();
        transModel.setName("aa");
        transModel.setUserList(mUserList);
     //   transModel.setPatientsList(mPatientsList);
        //transModel.setSurveyList(mSurveys);
        //transModel.setSurvey(survey);

        /*try {
            byte[] bytesToSend = ParcebleUtil.serialize(transModel);
            myThreadConnected.write(bytesToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return transModel;

    }

    public TransferModel sendSurveyData(Realm realm) {
        // List<MUser> mUserList = new ArrayList<>();
        // List<MPatients> mPatientsList = new ArrayList<>();
        // List<MSurvey> mSurveys = new ArrayList<>();

        //List<User> user = realm.where(User.class).findAll();
        //List<Patients> patients = realm.where(Patients.class).findAll();
        //List<Survey> surveys = realm.where(Survey.class).findAll();
        //List<Test1> test1=new ArrayList<Test1>();
        //test1.add(new Test1(1));
        // test1.add(new Test1(2));


        Survey surveys = realm.where(Survey.class).findFirst();

        //ConvertUser
        /*for (int i = 0; i < user.size(); i++) {
            MUser mu = new MUser();
            mu.setId(user.get(i).getId());
            mu.setName(user.get(i).getName());
            mu.setPassword(user.get(i).getPassword());
            mu.setType(user.get(i).getType());
            mUserList.add(mu);
        }

        //ConvertPatients
        for (int i = 0; i < patients.size(); i++) {
            MPatients pa = new MPatients();
            pa.setId(patients.get(i).getId());
            pa.setAddress(patients.get(i).getAddress());
            pa.setPatientname(patients.get(i).getPatientname());
            mPatientsList.add(pa);
        }*/
        MSurvey sur = new MSurvey();
        if(surveys!=null) {
            // for (int i = 0; i < surveys.size(); i++) {


            sur.setId(surveys.getId());
            sur.setName(surveys.getName());

            List<MQuestions> ques = new ArrayList<>();

            for (int j = 0; j < surveys.getQuestions().size(); j++) {
                MQuestions que = new MQuestions();
                que.setId(surveys.getQuestions().get(j).getId());
                que.setOpt(surveys.getQuestions().get(j).getOpt());
                que.setQuestion(surveys.getQuestions().get(j).getQuestion());
                que.setText(surveys.getQuestions().get(j).getText());

                List<MOptions> opt = new ArrayList<>();
                for (int k = 0; k < surveys.getQuestions().get(j).getOptions().size(); k++) {
                    MOptions op = new MOptions();
                    op.setId(surveys.getQuestions().get(j).getOptions().get(k).getId());
                    op.setOpt(surveys.getQuestions().get(j).getOptions().get(k).getOpt());
                    opt.add(op);
                }
                que.setOptions(opt);

                ques.add(que);
            }
            sur.setQuestions(ques);


            // mSurveys.add(sur);

            // }
        }
        TransferModel transModel = new TransferModel();
        transModel.setName("single_survey");
        //transModel.setUserList(mUserList);
        //transModel.setPatientsList(mPatientsList);
        //transModel1.setSurveyList(mSurveys);
        transModel.setSurvey(sur);


        /*try {
            byte[] bytesToSend = ParcebleUtil.serialize(transModel1);
            myThreadConnected.write(bytesToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return transModel;
    }


    public void saveData(final TransferModel data, Realm realm) {


        realm.executeTransaction(new Realm.Transaction() {
                                      @Override
                                      public void execute(Realm realm) {

                                          //save User
                                          if (data.getUserList() != null) {


                                              for (int i = 0; i < data.getUserList().size(); i++) {
                                                  User mu = realm.where(User.class).equalTo("id", data.getUserList().get(i).getId()).findFirst();
                                                  if (mu != null) {
                                                      //mu.setId(data.getUserList().get(i).getId());
                                                      mu.setName(data.getUserList().get(i).getName());
                                                      mu.setPassword(data.getUserList().get(i).getPassword());
                                                      mu.setType(data.getUserList().get(i).getType());
                                                      realm.copyToRealmOrUpdate(mu);
                                                  } else {
                                                      User us = realm.createObject(User.class, data.getUserList().get(i).getId());
                                                      us.setName(data.getUserList().get(i).getName());
                                                      us.setPassword(data.getUserList().get(i).getPassword());
                                                      us.setType(data.getUserList().get(i).getType());
                                                      realm.copyToRealmOrUpdate(us);
                                                  }

                                                  //mUserList.add(mu);
                                              }
                                          }
                                          //save Patients
                                          if (data.getPatientsList() != null) {
                                              for (int i = 0; i < data.getPatientsList().size(); i++) {
                                                  Patients pa = realm.where(Patients.class).equalTo("id", data.getPatientsList().get(i).getId()).findFirst();
                                                  if (pa != null) {
                                                      //pa.setId(data.getPatientsList().get(i).getId());
                                                      pa.setAddress(data.getPatientsList().get(i).getAddress());
                                                      pa.setPatientname(data.getPatientsList().get(i).getPatientname());
                                                      //mPatientsList.add(pa);
                                                      realm.copyToRealmOrUpdate(pa);
                                                  } else {
                                                      Patients up = realm.createObject(Patients.class, data.getPatientsList().get(i).getId());
                                                      //pa.setId(data.getPatientsList().get(i).getId());
                                                      up.setAddress(data.getPatientsList().get(i).getAddress());
                                                      up.setPatientname(data.getPatientsList().get(i).getPatientname());
                                                      //mPatientsList.add(pa);
                                                      realm.copyToRealmOrUpdate(up);
                                                  }
                                              }
                                              //Toast.makeText(getApplicationContext(),"Data Saved")
                                          }


                                          //save survey
                                          if (data.getSurveyList() != null) {
                                              for (int i = 0; i < data.getSurveyList().size(); i++) {

                                                  Survey surv = realm.where(Survey.class).equalTo("id", data.getSurveyList().get(i).getId()).findFirst();
                                                  Survey sur;
                                                  if (surv != null) {
                                                      sur = realm.where(Survey.class).equalTo("id", data.getSurveyList().get(i).getId()).findFirst();
                                                  } else {
                                                      sur = realm.createObject(Survey.class, data.getSurveyList().get(i).getId());
                                                  }

                                                  sur.setId(data.getSurveyList().get(i).getId());
                                                  sur.setName(data.getSurveyList().get(i).getName());

                                                  RealmList<Questions> ques = new RealmList<Questions>();

                                                  for (int j = 0; j < data.getSurveyList().get(i).getQuestions().size(); j++) {
                                                      Questions que = realm.where(Questions.class).equalTo("id", data.getSurveyList().get(i).getQuestions().get(j).getId()).findFirst();
                                                      Questions qu;
                                                      if (que != null) {
                                                          qu = realm.where(Questions.class).equalTo("id", data.getSurveyList().get(i).getQuestions().get(j).getId()).findFirst();
                                                      } else {
                                                          qu = realm.createObject(Questions.class, data.getSurveyList().get(i).getQuestions().get(j).getId());
                                                      }
                                                      //  que.setId(data.getSurveyList().get(i).getQuestions().get(j).getId());
                                                      qu.setOpt(data.getSurveyList().get(i).getQuestions().get(j).getOpt());
                                                      qu.setQuestion(data.getSurveyList().get(i).getQuestions().get(j).getQuestion());
                                                      qu.setText(data.getSurveyList().get(i).getQuestions().get(j).getText());

                                                      RealmList<Options> opt = new RealmList<Options>();
                                                      for (int k = 0; k < data.getSurveyList().get(i).getQuestions().get(j).getOptions().size(); k++) {
                                                          Options op = realm.where(Options.class).equalTo("id", data.getSurveyList().get(i).getId()).findFirst();
                                                          Options o;
                                                          if (op != null) {
                                                              o = realm.where(Options.class).equalTo("id", data.getSurveyList().get(i).getId()).findFirst();
                                                          } else {
                                                              o = realm.createObject(Options.class, data.getSurveyList().get(i).getQuestions().get(j).getOptions().get(k).getId());
                                                          }

                                                          //o.setId(data.getSurveyList().get(i).getQuestions().get(j).getOptions().get(k).getId());
                                                          o.setOpt(data.getSurveyList().get(i).getQuestions().get(j).getOptions().get(k).getOpt());

                                                          realm.copyToRealmOrUpdate(o);
                                                          opt.add(o);

                                                      }
                                                      qu.setOptions(opt);
                                                      realm.copyToRealmOrUpdate(qu);
                                                      ques.add(qu);

                                                  }
                                                  sur.setQuestions(ques);

                                                  realm.copyToRealmOrUpdate(sur);
                                              }

                                          }


                                          if(data.getSurvey()!=null){
                                              Survey surv = realm.where(Survey.class).equalTo("id", data.getSurvey().getId()).findFirst();
                                              Survey sur;
                                              if (surv != null) {
                                                  sur = realm.where(Survey.class).equalTo("id",data.getSurvey().getId()).findFirst();
                                              } else {
                                                  sur = realm.createObject(Survey.class, data.getSurvey().getId());
                                              }

                                             // sur.setId(data.getSurvey().getId());
                                              sur.setName(data.getSurvey().getName());

                                              RealmList<Questions> ques = new RealmList<Questions>();

                                              if(data.getSurvey().getQuestions()!=null) {
                                                  for (int j = 0; j < data.getSurvey().getQuestions().size(); j++) {
                                                      Questions que = realm.where(Questions.class).equalTo("id", data.getSurvey().getQuestions().get(j).getId()).findFirst();
                                                      Questions qu;
                                                      if (que != null) {
                                                          qu = realm.where(Questions.class).equalTo("id", data.getSurvey().getQuestions().get(j).getId()).findFirst();
                                                      } else {
                                                          qu = realm.createObject(Questions.class, data.getSurvey().getQuestions().get(j).getId());
                                                      }
                                                      //  que.setId(data.getSurveyList().get(i).getQuestions().get(j).getId());
                                                      qu.setOpt(data.getSurvey().getQuestions().get(j).getOpt());
                                                      qu.setQuestion(data.getSurvey().getQuestions().get(j).getQuestion());
                                                      qu.setText(data.getSurvey().getQuestions().get(j).getText());

                                                      RealmList<Options> opt = new RealmList<Options>();
                                                      for (int k = 0; k < data.getSurvey().getQuestions().get(j).getOptions().size(); k++) {
                                                          Options op = realm.where(Options.class).equalTo("id", data.getSurvey().getId()).findFirst();
                                                          Options o;
                                                          if (op != null) {
                                                              o = realm.where(Options.class).equalTo("id", data.getSurvey().getId()).findFirst();
                                                          } else {
                                                              o = realm.createObject(Options.class, data.getSurvey().getQuestions().get(j).getOptions().get(k).getId());
                                                          }

                                                          //o.setId(data.getSurveyList().get(i).getQuestions().get(j).getOptions().get(k).getId());
                                                          o.setOpt(data.getSurvey().getQuestions().get(j).getOptions().get(k).getOpt());

                                                          realm.copyToRealmOrUpdate(o);
                                                          opt.add(o);

                                                      }
                                                      qu.setOptions(opt);
                                                      realm.copyToRealmOrUpdate(qu);
                                                      ques.add(qu);

                                                  }
                                                  sur.setQuestions(ques);
                                              }

                                              realm.copyToRealmOrUpdate(sur);
                                          }






                                      }


                                  }

        );


    }





}
