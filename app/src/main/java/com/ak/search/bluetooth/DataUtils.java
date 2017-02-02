package com.ak.search.bluetooth;

import com.ak.search.model.MAnswers;
import com.ak.search.model.MDataCollection;
import com.ak.search.model.MOptions;
import com.ak.search.model.MPatients;
import com.ak.search.model.MQuestions;
import com.ak.search.model.MSurvey;
import com.ak.search.model.MUser;
import com.ak.search.model.MTransferModel;
import com.ak.search.realm_model.Answers;
import com.ak.search.realm_model.DataCollection;
import com.ak.search.realm_model.Options;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.Questions;
import com.ak.search.realm_model.Survey;
import com.ak.search.realm_model.TransferModel;
import com.ak.search.realm_model.User;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by dg hdghfd on 12-01-2017.
 */

public class DataUtils {


    public MTransferModel sendData(Realm realm, TransferModel transferModel) {

        List<MUser> mUserList = new ArrayList<>();
        List<MPatients> mPatientsList = new ArrayList<>();
        List<MSurvey> mSurveys = new ArrayList<>();
        List<MDataCollection> mDataCollections = new ArrayList<>();

        List<User> user = transferModel.getUserList(); //realm.where(User.class).findAll();
        List<Patients> patients = transferModel.getPatientsList(); //realm.where(Patients.class).findAll();
        List<Survey> surveys = transferModel.getSurveyList(); //new MSurvey();
        List<DataCollection> dataCollections = transferModel.getDataCollectionList();

        //ConvertUser
        if (user != null) {
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
        if (patients != null) {
            for (int i = 0; i < patients.size(); i++) {
                MPatients pa = new MPatients();
                pa.setId(patients.get(i).getId());
                pa.setAddress(patients.get(i).getAddress());
                pa.setPatientname(patients.get(i).getPatientname());
                mPatientsList.add(pa);
            }
        }

        //Convert Survey
        if (surveys != null) {
            for (int i = 0; i < surveys.size(); i++) {

                MSurvey sur = new MSurvey();
                sur.setId(surveys.get(i).getId());
                sur.setName(surveys.get(i).getName());

                List<MQuestions> ques = new ArrayList<>();
                if (surveys.get(i).getQuestions() != null) {
                    for (int j = 0; j < surveys.get(i).getQuestions().size(); j++) {
                        MQuestions que = new MQuestions();
                        que.setId(surveys.get(i).getQuestions().get(j).getId());
                        que.setOpt(surveys.get(i).getQuestions().get(j).getOpt());
                        que.setQuestion(surveys.get(i).getQuestions().get(j).getQuestion());
                        que.setText(surveys.get(i).getQuestions().get(j).getText());

                        que.setCheckbox(surveys.get(i).getQuestions().get(j).getCheckbox());
                        que.setCompulsary(surveys.get(i).getQuestions().get(j).getCompulsary());
                        que.setDate(surveys.get(i).getQuestions().get(j).getDate());
                        que.setImage(surveys.get(i).getQuestions().get(j).getImage());
                        que.setNumber(surveys.get(i).getQuestions().get(j).getNumber());
                        que.setTime(surveys.get(i).getQuestions().get(j).getTime());

                        List<MOptions> opt = new ArrayList<>();
                        if (surveys.get(i).getQuestions().get(j).getOptions() != null) {
                            for (int k = 0; k < surveys.get(i).getQuestions().get(j).getOptions().size(); k++) {
                                MOptions op = new MOptions();
                                op.setId(surveys.get(i).getQuestions().get(j).getOptions().get(k).getId());
                                op.setOpt(surveys.get(i).getQuestions().get(j).getOptions().get(k).getOpt());
                                opt.add(op);
                            }
                            que.setOptions(opt);
                        }

                        List<MOptions> optChk = new ArrayList<>();
                        if (surveys.get(i).getQuestions().get(j).getChkb() != null) {
                            for (int k = 0; k < surveys.get(i).getQuestions().get(j).getChkb().size(); k++) {
                                MOptions op = new MOptions();
                                op.setId(surveys.get(i).getQuestions().get(j).getChkb().get(k).getId());
                                op.setOpt(surveys.get(i).getQuestions().get(j).getChkb().get(k).getOpt());
                                optChk.add(op);
                            }
                            que.setChkb(optChk);
                        }

                        ques.add(que);
                    }
                    sur.setQuestions(ques);
                }

                mSurveys.add(sur);

            }
        }


        //data Collection
        if (dataCollections != null) {
            for (int i = 0; i < dataCollections.size(); i++) {
                MDataCollection dataColl = new MDataCollection();

                dataColl.setId(dataCollections.get(i).getId());
                dataColl.setSurveyid(dataCollections.get(i).getSurveyid());
                dataColl.setTimestamp(dataCollections.get(i).getTimestamp());
                dataColl.setLng(dataCollections.get(i).getLng());
                dataColl.setLat(dataCollections.get(i).getLat());

                MPatients mPatients = new MPatients();
                mPatients.setId(dataCollections.get(i).getPatients().getId());
                mPatients.setPatientname(dataCollections.get(i).getPatients().getPatientname());
                mPatients.setAddress(dataCollections.get(i).getPatients().getAddress());
                dataColl.setPatients(mPatients);

                List<MAnswers> answerses = new ArrayList<>();
                if (dataCollections.get(i).getAnswerses() != null) {
                    for (int j = 0; j < dataCollections.get(i).getAnswerses().size(); j++) {
                        MAnswers answers = new MAnswers();
                        answers.setPatientid(dataCollections.get(i).getAnswerses().get(j).getPatientid());
                        answers.setSelectedopt(dataCollections.get(i).getAnswerses().get(j).getSelectedopt());
                        answers.setSelectedChk(dataCollections.get(i).getAnswerses().get(j).getSelectedChk());
                        answers.setAns(dataCollections.get(i).getAnswerses().get(j).getAns());
                        answers.setNumAns(dataCollections.get(i).getAnswerses().get(j).getNumAns());
                        answers.setDate(dataCollections.get(i).getAnswerses().get(j).getDate());
                        answers.setTime(dataCollections.get(i).getAnswerses().get(j).getTime());
                        answers.setByteArrayImage(dataCollections.get(i).getAnswerses().get(j).getByteArrayImage());

                        MQuestions mQuestions = new MQuestions();
                        mQuestions.setId(dataCollections.get(i).getAnswerses().get(j).getQuestions().getId());
                        mQuestions.setQuestion(dataCollections.get(i).getAnswerses().get(j).getQuestions().getQuestion());
                        mQuestions.setText(dataCollections.get(i).getAnswerses().get(j).getQuestions().getOpt());
                        mQuestions.setNumber(dataCollections.get(i).getAnswerses().get(j).getQuestions().getNumber());
                        mQuestions.setDate(dataCollections.get(i).getAnswerses().get(j).getQuestions().getDate());
                        mQuestions.setTime(dataCollections.get(i).getAnswerses().get(j).getQuestions().getTime());
                        mQuestions.setImage(dataCollections.get(i).getAnswerses().get(j).getQuestions().getImage());
                        mQuestions.setCompulsary(dataCollections.get(i).getAnswerses().get(j).getQuestions().getCompulsary());
                        mQuestions.setOpt(dataCollections.get(i).getAnswerses().get(j).getQuestions().getOpt());
                        mQuestions.setCheckbox(dataCollections.get(i).getAnswerses().get(j).getQuestions().getCheckbox());


                        List<MOptions> options = new ArrayList<>();
                        if (dataCollections.get(i).getAnswerses().get(j).getQuestions().getOptions() != null) {
                            for (int k = 0; k < dataCollections.get(i).getAnswerses().get(j).getQuestions().getOptions().size(); k++) {
                                MOptions op = new MOptions();
                                op.setId(dataCollections.get(i).getAnswerses().get(j).getQuestions().getOptions().get(k).getId());
                                op.setOpt(dataCollections.get(i).getAnswerses().get(j).getQuestions().getOptions().get(k).getOpt());
                                options.add(op);
                            }
                        }
                        mQuestions.setOptions(options);

                        List<MOptions> chkb = new ArrayList<>();
                        if (dataCollections.get(i).getAnswerses().get(j).getQuestions().getOptions() != null) {
                            for (int k = 0; k < dataCollections.get(i).getAnswerses().get(j).getQuestions().getChkb().size(); k++) {
                                MOptions op = new MOptions();
                                op.setId(dataCollections.get(i).getAnswerses().get(j).getQuestions().getChkb().get(k).getId());
                                op.setOpt(dataCollections.get(i).getAnswerses().get(j).getQuestions().getChkb().get(k).getOpt());
                                chkb.add(op);
                            }
                        }
                        mQuestions.setChkb(chkb);


                        answers.setMQuestions(mQuestions);

                        answerses.add(answers);
                    }
                }
                dataColl.setAnswerses(answerses);


                mDataCollections.add(dataColl);
            }
        }


        MTransferModel transModel = new MTransferModel();
        transModel.setName("aa");
        transModel.setUserList(mUserList);
        transModel.setPatientsList(mPatientsList);
        transModel.setSurveyList(mSurveys);
        transModel.setDataCollectionsList(mDataCollections);

        return transModel;
    }


    public void saveData(final MTransferModel data, Realm realm) {


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

                                                 // sur.setId(data.getSurveyList().get(i).getId());
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

                                                     qu.setCheckbox(data.getSurveyList().get(i).getQuestions().get(j).getCheckbox());
                                                     qu.setCompulsary(data.getSurveyList().get(i).getQuestions().get(j).getCompulsary());
                                                     qu.setDate(data.getSurveyList().get(i).getQuestions().get(j).getDate());
                                                     qu.setImage(data.getSurveyList().get(i).getQuestions().get(j).getImage());
                                                     qu.setNumber(data.getSurveyList().get(i).getQuestions().get(j).getNumber());
                                                     qu.setTime(data.getSurveyList().get(i).getQuestions().get(j).getTime());


                                                     if (data.getSurveyList().get(i).getQuestions().get(j).getOptions() != null) {
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
                                                     }

                                                     if (data.getSurveyList().get(i).getQuestions().get(j).getChkb() != null) {
                                                         RealmList<Options> optChk = new RealmList<Options>();
                                                         for (int k = 0; k < data.getSurveyList().get(i).getQuestions().get(j).getChkb().size(); k++) {
                                                             Options op = realm.where(Options.class).equalTo("id", data.getSurveyList().get(i).getId()).findFirst();
                                                             Options o;
                                                             if (op != null) {
                                                                 o = realm.where(Options.class).equalTo("id", data.getSurveyList().get(i).getId()).findFirst();
                                                             } else {
                                                                 o = realm.createObject(Options.class, data.getSurveyList().get(i).getQuestions().get(j).getChkb().get(k).getId());
                                                             }

                                                             //o.setId(data.getSurveyList().get(i).getQuestions().get(j).getOptions().get(k).getId());
                                                             o.setOpt(data.getSurveyList().get(i).getQuestions().get(j).getChkb().get(k).getOpt());

                                                             realm.copyToRealmOrUpdate(o);
                                                             optChk.add(o);

                                                         }
                                                         qu.setChkb(optChk);
                                                     }


                                                     realm.copyToRealmOrUpdate(qu);
                                                     ques.add(qu);

                                                 }

                                                 sur.setQuestions(ques);

                                                 realm.copyToRealmOrUpdate(sur);
                                             }

                                         }


                                         //save data collection
                                         if (data.getDataCollectionsList() != null) {
                                             for (int i = 0; i < data.getDataCollectionsList().size(); i++) {

                                                 DataCollection dataCollection = realm.where(DataCollection.class).equalTo("id", data.getDataCollectionsList().get(i).getId()).findFirst();
                                                 DataCollection dataColl;
                                                 if (dataCollection != null) {
                                                     dataColl = realm.where(DataCollection.class).equalTo("id", data.getDataCollectionsList().get(i).getId()).findFirst();
                                                 } else {
                                                     dataColl = realm.createObject(DataCollection.class, data.getDataCollectionsList().get(i).getId());
                                                 }


                                                 //MDataCollection dataColl = new MDataCollection();

                                                 //dataColl.setId(data.getDataCollectionsList().get(i).getId());
                                                 dataColl.setSurveyid(data.getDataCollectionsList().get(i).getSurveyid());
                                                 dataColl.setTimestamp(data.getDataCollectionsList().get(i).getTimestamp());
                                                 dataColl.setLng(data.getDataCollectionsList().get(i).getLng());
                                                 dataColl.setLat(data.getDataCollectionsList().get(i).getLat());


                                                 Patients patients = realm.where(Patients.class).equalTo("id", data.getDataCollectionsList().get(i).getPatients().getId()).findFirst();
                                                 Patients mPatients;
                                                 if (patients != null) {
                                                     mPatients = realm.where(Patients.class).equalTo("id", data.getDataCollectionsList().get(i).getPatients().getId()).findFirst();
                                                 } else {
                                                     mPatients = realm.createObject(Patients.class, data.getDataCollectionsList().get(i).getPatients().getId());
                                                 }
                                                 // MPatients mPatients = new MPatients();
                                                 //mPatients.setId(data.getDataCollectionsList().get(i).getPatients().getId());
                                                 mPatients.setPatientname(data.getDataCollectionsList().get(i).getPatients().getPatientname());
                                                 mPatients.setAddress(data.getDataCollectionsList().get(i).getPatients().getAddress());

                                                 realm.copyToRealmOrUpdate(mPatients);
                                                 dataColl.setPatients(mPatients);

                                                 RealmList<Answers> answerses = new RealmList<Answers>();
                                                 if (data.getDataCollectionsList().get(i).getAnswerses() != null) {
                                                     for (int j = 0; j < data.getDataCollectionsList().get(i).getAnswerses().size(); j++) {
                                                         //MAnswers answers = new MAnswers();
                                                         // Answers ans = realm.where(Answers.class).equalTo("id", data.getDataCollectionsList().get(i).getAnswerses().get(j).getPatientid()).findFirst();
                                                         Answers answers =realm.createObject(Answers.class);
                                                         // if (ans != null) {
                                                         //     answers = realm.where(Answers.class).equalTo("id", data.getDataCollectionsList().get(i).getAnswerses().get(j).getPatientid()).findFirst();
                                                         // } else {
                                                         //     answers = realm.createObject(Answers.class, data.getDataCollectionsList().get(i).getPatients().getId());
                                                         // }
                                                         answers.setPatientid(data.getDataCollectionsList().get(i).getAnswerses().get(j).getPatientid());
                                                         answers.setSelectedopt(data.getDataCollectionsList().get(i).getAnswerses().get(j).getSelectedopt());
                                                         answers.setSelectedChk(data.getDataCollectionsList().get(i).getAnswerses().get(j).getSelectedChk());
                                                         answers.setAns(data.getDataCollectionsList().get(i).getAnswerses().get(j).getAns());
                                                         answers.setNumAns(data.getDataCollectionsList().get(i).getAnswerses().get(j).getNumAns());
                                                         answers.setDate(data.getDataCollectionsList().get(i).getAnswerses().get(j).getDate());
                                                         answers.setTime(data.getDataCollectionsList().get(i).getAnswerses().get(j).getTime());
                                                         answers.setByteArrayImage(data.getDataCollectionsList().get(i).getAnswerses().get(j).getByteArrayImage());

                                                         Questions mQue = realm.where(Questions.class).equalTo("id", data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getId()).findFirst();
                                                         Questions mQuestions;
                                                         if (mQue != null) {
                                                             mQuestions = realm.where(Questions.class).equalTo("id", data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getId()).findFirst();
                                                         } else {
                                                             mQuestions = realm.createObject(Questions.class, data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getId());
                                                         }
                                                         // mQuestions.setId(data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getId());
                                                         mQuestions.setQuestion(data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getQuestion());
                                                         mQuestions.setText(data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getOpt());
                                                         mQuestions.setNumber(data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getNumber());
                                                         mQuestions.setDate(data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getDate());
                                                         mQuestions.setTime(data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getTime());
                                                         mQuestions.setImage(data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getImage());
                                                         mQuestions.setCompulsary(data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getCompulsary());
                                                         mQuestions.setOpt(data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getOpt());
                                                         mQuestions.setCheckbox(data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getCheckbox());


                                                         RealmList<Options> options = new RealmList<Options>();
                                                         if (data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getOptions() != null) {
                                                             for (int k = 0; k < data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getOptions().size(); k++) {
                                                                 Options op = realm.where(Options.class).equalTo("id", data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getOptions().get(k).getId()).findFirst();
                                                                 Options o;
                                                                 if (op != null) {
                                                                     o = realm.where(Options.class).equalTo("id", data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getOptions().get(k).getId()).findFirst();
                                                                 } else {
                                                                     o = realm.createObject(Options.class, data.getSurveyList().get(i).getQuestions().get(j).getOptions().get(k).getId());
                                                                 }
                                                                 o.setOpt(data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getOptions().get(k).getOpt());
                                                                 realm.copyToRealmOrUpdate(o);
                                                                 options.add(o);
                                                             }
                                                         }
                                                         mQuestions.setOptions(options);

                                                         RealmList<Options> chkb = new RealmList<Options>();
                                                         if (data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getOptions() != null) {
                                                             for (int k = 0; k < data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getChkb().size(); k++) {
                                                                 Options op = realm.where(Options.class).equalTo("id", data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getChkb().get(k).getId()).findFirst();
                                                                 Options o;
                                                                 if (op != null) {
                                                                     o = realm.where(Options.class).equalTo("id", data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getChkb().get(k).getId()).findFirst();
                                                                 } else {
                                                                     o = realm.createObject(Options.class, data.getSurveyList().get(i).getQuestions().get(j).getChkb().get(k).getId());
                                                                 }
                                                                 o.setOpt(data.getDataCollectionsList().get(i).getAnswerses().get(j).getMQuestions().getChkb().get(k).getOpt());
                                                                 realm.copyToRealmOrUpdate(o);
                                                                 chkb.add(o);
                                                             }
                                                         }
                                                         mQuestions.setChkb(chkb);
                                                         realm.copyToRealmOrUpdate(mQuestions);


                                                         answers.setQuestions(mQuestions);

                                                         answerses.add(answers);
                                                     }
                                                 }
                                                 dataColl.setAnswerses(answerses);

                                                 realm.copyToRealmOrUpdate(dataColl);


                                                 // mDataCollections.add(dataColl);
                                             }
                                         }


                                     }


                                 }

        );


    }


}
