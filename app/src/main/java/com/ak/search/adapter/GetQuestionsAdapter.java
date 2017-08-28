package com.ak.search.adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ak.search.R;
import com.ak.search.activity.SelectPatientsActivity;
import com.ak.search.activity.StartSurveyActivity;
import com.ak.search.app.SaveAnswer;
import com.ak.search.app.Validate;
import com.ak.search.realm_model.Answers;
import com.ak.search.realm_model.Patients;
import com.ak.search.realm_model.Questions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static android.view.View.GONE;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class GetQuestionsAdapter extends RecyclerView.Adapter<GetQuestionsAdapter.MyViewHolder> {

    private List<Answers> answerList;
    private Context context;

    public static int CAMERA_REQUEST = 11;
    public static int PATIENT_REQUEST = 12;
    private int mYear, mMonth, mDay, mHour, mMinute;
    SaveAnswer saveAnswer;
    Realm realm;
    boolean enable, showerror = false;
    public static HashMap<Integer, Boolean> validateAnswers;


    //HashMap<Integer, List<RadioButton>> hashAllRb = new HashMap<>();
    // HashMap<Integer, List<RadioButton>> hashAllCondRb = new HashMap<>();
    Validate validate;
    private RecyclerView mRecyclerView;
    private boolean onBind = false;

    public static int CH_INDEX, CH_INDEX_C;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_question)
        TextView tvQuestion;
        @BindView(R.id.txt_date)
        TextView tvDate;
        @BindView(R.id.txt_time)
        TextView tvTime;
        @BindView(R.id.txt_patient)
        TextView tvPatient;
        @BindView(R.id.txt_answer)
        EditText etAnswer;
        @BindView(R.id.txt_number)
        EditText etNumAns;
        @BindView(R.id.rg_options)
        RadioGroup rgOption;
        @BindView(R.id.rg_options_conditional)
        RadioGroup rgOptionConditional;
        @BindView(R.id.iv_capture)
        ImageView ivSelImg;
        @BindView(R.id.ll_check)
        LinearLayout llChck;
        @BindView(R.id.ll_patient_name)
        LinearLayout llPatient;
        @BindView(R.id.ll_time)
        LinearLayout llTime;
        @BindView(R.id.ll_date)
        LinearLayout llDate;
        @BindView(R.id.ll_image)
        LinearLayout llImage;


        @BindView(R.id.btn_date)
        Button btnSelectDate;
        @BindView(R.id.btn_time)
        Button btnSelectTime;
        @BindView(R.id.btn_capture_image)
        Button btnSelectImage;
        @BindView(R.id.btn_patientname)
        Button btnPatientName;
        @BindView(R.id.tv_compulsary)
        TextView tvCompulsary;
        @BindView(R.id.view_divider)
        View viewDivider;
        @BindView(R.id.llQuestion)
        LinearLayout llQuestion;
        @BindView(R.id.btn_save)
        Button btnSave;
        @BindView(R.id.tv_question_counter)
        TextView tvCounter;

        @BindView(R.id.ivNes)
        ImageView ivNes;

        @BindView(R.id.ivRedo)
        ImageView ivRedo;


        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);


            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    boolean validate = true;

                    for (Map.Entry m : validateAnswers.entrySet()) {
                        if ((boolean) m.getValue() == false) {
                            validate = false;
                            saveAnswer.scrollToError((int) m.getKey());
                        }
                    }

                    if (validate) {
                        saveAnswer.saveCollection();
                    } else {
                        showerror = true;
                        notifyDataSetChanged();


                    }
                }
            });

            ivRedo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    byte[] a = {};

                    answerList.get(getPosition()).setAnswered(false);

                    answerList.get(getPosition()).setAns("");
                    answerList.get(getPosition()).setNumAns("");
                    answerList.get(getPosition()).setByteArrayImage(a);
                    answerList.get(getPosition()).setDate("");
                    answerList.get(getPosition()).setTime("");
                    answerList.get(getPosition()).setSelectedChk("");
                    answerList.get(getPosition()).setSelectedopt(-1);
                    answerList.get(getPosition()).setSelectedOptConditional(-1);


                    String type[] = answerList.get(getPosition()).getQuestions().getTypeQuestion().split("");

                    if (type[1].equals("9")) {

                        saveAnswer.onAddSurvey(0, getPosition(), answerList.get(getPosition()).getParentPos(), answerList.get(getPosition()).getQuestions().getId());

                        notifyDataSetChanged();
                    } else {
                        notifyItemChanged(getPosition());
                    }

                }
            });


        }
    }


    private class CustomWatcher implements TextWatcher {

        private int pos, type;// 1=ans 2=num ans
        View view;
        private boolean mActive;


        private CustomWatcher(int pos, View view, int type) {
            this.pos = pos;
            this.view = view;
            this.type = type;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            switch (type) {
                case 0:
                    answerList.get(pos).setAns(String.valueOf(editable));
                    break;
                case 1:
                    answerList.get(pos).setNumAns(String.valueOf(editable));

                    break;
            }


            if (editable.length() > 0) {
                view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background_answered));

                answerList.get(pos).setAnswered(true);
            } else {

                view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background));

                answerList.get(pos).setAnswered(false);
            }

            saveAnswer.onAnswerSave(pos, answerList.get(pos));

        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data, int pos) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            answerList.get(pos).setByteArrayImage(byteArray);
            answerList.get(pos).setAnswered(true);
            notifyItemChanged(pos);
        }

        if (requestCode == PATIENT_REQUEST && resultCode == Activity.RESULT_OK) {

            Long id = (long) data.getExtras().get("data");
            for (int i = 0; i < answerList.size(); i++) {
                answerList.get(i).setPatientid(id);
                answerList.get(pos).setAnswered(true);
            }

            StartSurveyActivity.patients = realm.where(Patients.class).equalTo("id", id).findFirst();

            notifyDataSetChanged();
        }
    }

    public GetQuestionsAdapter(Context context, List<Answers> answerList, SaveAnswer saveAnswer, Realm realm, boolean enable) {
        this.answerList = answerList;
        this.context = context;
        this.saveAnswer = saveAnswer;
        this.realm = realm;
        this.enable = enable;

        validateAnswers = new HashMap<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.questions_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Questions questions = answerList.get(position).getQuestions();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {


                holder.tvQuestion.setVisibility(GONE);
                holder.rgOption.setVisibility(GONE);
                holder.rgOptionConditional.setVisibility(GONE);
                holder.etAnswer.setVisibility(GONE);
                holder.etNumAns.setVisibility(GONE);
                holder.llChck.setVisibility(GONE);
                holder.tvDate.setVisibility(GONE);
                holder.btnSelectDate.setVisibility(GONE);
                holder.tvTime.setVisibility(GONE);

                holder.btnSelectTime.setVisibility(GONE);
                holder.ivSelImg.setVisibility(GONE);
                holder.btnSelectImage.setVisibility(GONE);
                holder.btnSave.setVisibility(GONE);
                holder.btnPatientName.setVisibility(GONE);
                holder.llPatient.setVisibility(GONE);
                holder.tvPatient.setVisibility(GONE);
                holder.tvCompulsary.setVisibility(GONE);
                holder.ivNes.setVisibility(GONE);
                holder.ivRedo.setVisibility(GONE);

                if (answerList.get(position).getQuestions() != null) {

                    holder.tvQuestion.setVisibility(View.VISIBLE);
                    holder.tvQuestion.setText(questions.getQuestion());

                    holder.viewDivider.setVisibility(View.VISIBLE);
                    holder.llQuestion.setVisibility(View.VISIBLE);

                    holder.tvCounter.setVisibility(View.VISIBLE);

                    holder.tvCounter.setText(String.valueOf(position + 1));


                    String questionType = questions.getTypeQuestion();

                    if (questionType != null) {
                        String[] quest = questionType.split(",");
                        for (int l = 0; l < quest.length; l++) {
                            int que = Integer.parseInt(quest[l]);
                            switch (que) {

                                //Text---------------------------------------
                                case 1:

                                    holder.etAnswer.setVisibility(View.VISIBLE);


                                    CustomWatcher oldWatcher = (CustomWatcher) holder.etAnswer.getTag();
                                    if (oldWatcher != null)
                                        holder.etAnswer.removeTextChangedListener(oldWatcher);

                                    //populate your editText with the model data here (before adding the new text watcher)

                                    CustomWatcher newWatcher = new CustomWatcher(position, holder.tvCounter, 0);
                                    holder.etAnswer.setTag(newWatcher);
                                    holder.etAnswer.addTextChangedListener(newWatcher);


                                    if (answerList.get(position).getAns() != null && !answerList.get(position).getAns().equals("")) {

                                        holder.etAnswer.setText(answerList.get(position).getAns());
                                        holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background_answered));
                                        holder.ivRedo.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.etAnswer.setText("");
                                        holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background));
                                        holder.ivRedo.setVisibility(GONE);
                                    }

                                    if (!enable) {
                                        holder.etAnswer.setEnabled(false);
                                    }

                                    if (questions.getCompulsary()) {
                                        if (answerList.get(position).getAns() != null && !answerList.get(position).getAns().equals("")) {
                                            validateAnswers.put(position, true);
                                        } else {
                                            validateAnswers.put(position, false);
                                        }
                                    } else {
                                        validateAnswers.put(position, true);
                                    }


                                    break;

                                //Number---------------------------------------
                                case 2:

                                    CustomWatcher oldWatcher1 = (CustomWatcher) holder.etNumAns.getTag();
                                    if (oldWatcher1 != null)
                                        holder.etNumAns.removeTextChangedListener(oldWatcher1);

                                    //populate your editText with the model data here (before adding the new text watcher)

                                    CustomWatcher newWatcher1 = new CustomWatcher(position, holder.tvCounter, 1);
                                    holder.etNumAns.setTag(newWatcher1);
                                    holder.etNumAns.addTextChangedListener(newWatcher1);

                                    holder.etNumAns.setVisibility(View.VISIBLE);
                                    if (answerList.get(position).getNumAns() != null && !answerList.get(position).getNumAns().equals("")) {


                                        holder.etNumAns.setText(answerList.get(position).getNumAns());

                                        holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background_answered));
                                        holder.ivRedo.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.etNumAns.setText("");
                                        holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background));
                                        holder.ivRedo.setVisibility(GONE);
                                    }

                                    if (!enable) {
                                        holder.etNumAns.setEnabled(false);
                                    }

                                    if (questions.getCompulsary()) {
                                        if (answerList.get(position).getNumAns() != null && !answerList.get(position).getNumAns().equals("")) {

                                            validateAnswers.put(position, true);
                                        } else {
                                            validateAnswers.put(position, false);
                                        }
                                    } else {
                                        validateAnswers.put(position, true);
                                    }


                                    break;

                                //Date---------------------------------------
                                case 3:

                                    holder.btnSelectDate.setOnClickListener(null);

                                    holder.tvDate.setVisibility(View.VISIBLE);
                                    if (enable) {
                                        holder.btnSelectDate.setVisibility(View.VISIBLE);
                                    }

                                    if (answerList.get(position).getDate() != null && !answerList.get(position).getDate().equals("")) {
                                        holder.tvDate.setText(answerList.get(position).getDate());
                                        holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background_answered));

                                        holder.ivRedo.setVisibility(View.VISIBLE);
                                        answerList.get(position).setAnswered(true);
                                    } else {

                                        holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background));
                                        holder.tvDate.setText("");
                                        answerList.get(position).setAnswered(false);
                                        holder.ivRedo.setVisibility(GONE);
                                    }

                                    if (questions.getCompulsary()) {
                                        if (answerList.get(position).getDate() != null && !answerList.get(position).getDate().equals("")) {
                                            //boolean v = validate.validateString(holder.etAnswer.getText().toString());
                                            validateAnswers.put(position, true);
                                        } else {
                                            validateAnswers.put(position, false);
                                        }
                                    } else {
                                        validateAnswers.put(position, true);
                                    }


                                    holder.llDate.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            final Calendar c = Calendar.getInstance();
                                            mYear = c.get(Calendar.YEAR);
                                            mMonth = c.get(Calendar.MONTH);
                                            mDay = c.get(Calendar.DAY_OF_MONTH);


                                            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                                                    new DatePickerDialog.OnDateSetListener() {

                                                        @Override
                                                        public void onDateSet(DatePicker view, int year,
                                                                              int monthOfYear, int dayOfMonth) {

                                                            holder.tvDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                                            answerList.get(position).setDate(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                                            //getSelectedChkbox();
                                                            answerList.get(position).setAnswered(true);
                                                            saveAnswer.onAnswerSave(position, answerList.get(position));

                                                            notifyItemChanged(position);

                                                        }
                                                    }, mYear, mMonth, mDay);
                                            datePickerDialog.show();
                                        }
                                    });


                                    break;

                                //Time---------------------------------------
                                case 4:

                                    holder.btnSelectTime.setOnClickListener(null);

                                    holder.tvTime.setVisibility(View.VISIBLE);
                                    if (enable) {
                                        holder.btnSelectTime.setVisibility(View.VISIBLE);
                                    }

                                    if (answerList.get(position).getTime() != null && !answerList.get(position).getTime().equals("")) {
                                        holder.tvTime.setText(answerList.get(position).getTime());
                                        holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background_answered));
                                        holder.ivRedo.setVisibility(View.VISIBLE);
                                        answerList.get(position).setAnswered(true);
                                    } else {

                                        holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background));
                                        holder.tvTime.setText("");
                                        answerList.get(position).setAnswered(false);
                                        holder.ivRedo.setVisibility(GONE);
                                    }


                                    if (questions.getCompulsary()) {
                                        if (answerList.get(position).getTime() != null && !answerList.get(position).getTime().equals("")) {
                                            //boolean v = validate.validateString(holder.etAnswer.getText().toString());
                                            validateAnswers.put(position, true);
                                        } else {
                                            validateAnswers.put(position, false);
                                        }
                                    } else {
                                        validateAnswers.put(position, true);
                                    }

                                    holder.llTime.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            final Calendar c = Calendar.getInstance();
                                            mHour = c.get(Calendar.HOUR_OF_DAY);
                                            mMinute = c.get(Calendar.MINUTE);

                                            // Launch Time Picker Dialog
                                            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                                                    new TimePickerDialog.OnTimeSetListener() {

                                                        @Override
                                                        public void onTimeSet(TimePicker view, int hourOfDay,
                                                                              int minute) {


                                                            String AM_PM = " AM";
                                                            if (hourOfDay >= 12) {
                                                                AM_PM = " PM";
                                                                if (hourOfDay >= 13 && hourOfDay < 24) {
                                                                    hourOfDay -= 12;
                                                                } else {
                                                                    hourOfDay = 12;
                                                                }
                                                            } else if (hourOfDay == 0) {
                                                                hourOfDay = 12;
                                                            }

                                                            holder.tvTime.setText(hourOfDay + ":" + minute + " " + AM_PM);
                                                            answerList.get(position).setTime(hourOfDay + ":" + minute + " " + AM_PM);
                                                            answerList.get(position).setAnswered(true);
                                                            //getSelectedChkbox();
                                                            saveAnswer.onAnswerSave(position, answerList.get(position));
                                                            notifyItemChanged(position);
                                                        }
                                                    }, mHour, mMinute, false);
                                            timePickerDialog.show();


                                        }
                                    });

                                    break;

                                //Image---------------------------------------
                                case 5:


                                    holder.ivSelImg.setVisibility(View.VISIBLE);
                                    if (enable) {
                                        holder.btnSelectImage.setVisibility(View.VISIBLE);
                                    }


                                    if (answerList.get(position).getByteArrayImage().length > 0) {
                                        Bitmap bmp = BitmapFactory.decodeByteArray(answerList.get(position).getByteArrayImage(), 0, answerList.get(position).getByteArrayImage().length);
                                        holder.ivSelImg.setImageBitmap(bmp);
                                        holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background_answered));
                                        holder.ivRedo.setVisibility(View.VISIBLE);
                                        answerList.get(position).setAnswered(true);
                                    } else {

                                        holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background));
                                        holder.ivSelImg.setImageDrawable(null);
                                        answerList.get(position).setAnswered(false);
                                        holder.ivRedo.setVisibility(GONE);
                                    }

                                    if (questions.getCompulsary()) {
                                        if (answerList.get(position).getByteArrayImage().length > 1) {
                                            //boolean v = validate.validateString(holder.etAnswer.getText().toString());
                                            validateAnswers.put(position, true);
                                        } else {
                                            validateAnswers.put(position, false);
                                        }
                                    } else {
                                        validateAnswers.put(position, true);
                                    }

                                    holder.llImage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            StartSurveyActivity.positionImg = position;
                                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                            ((Activity) context).startActivityForResult(cameraIntent, CAMERA_REQUEST);
                                            notifyItemChanged(position);
                                        }
                                    });


                                    break;

                                //patient name---------------------------------------
                                case 6:


                                    holder.llPatient.setVisibility(View.VISIBLE);
                                    holder.tvPatient.setVisibility(View.VISIBLE);
                                    if (enable) {
                                        holder.btnPatientName.setVisibility(View.VISIBLE);
                                    }

                                    if (answerList.get(position).getPatientid() != 0) {

                                        Patients p = realm.where(Patients.class).equalTo("id", answerList.get(position).getPatientid()).findFirst();
                                        if (p != null) {
                                            holder.tvPatient.setText(p.getPatientname());
                                        }
                                        holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background_answered));
                                        holder.ivRedo.setVisibility(View.VISIBLE);
                                        answerList.get(position).setAnswered(true);
                                    } else {

                                        holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background));
                                        holder.ivRedo.setVisibility(GONE);
                                        answerList.get(position).setAnswered(false);
                                    }

                                    if (questions.getCompulsary()) {
                                        if (answerList.get(position).getPatientid() != 0) {
                                            //boolean v = validate.validateString(holder.etAnswer.getText().toString());
                                            validateAnswers.put(position, true);
                                        } else {
                                            validateAnswers.put(position, false);
                                        }
                                    } else {
                                        validateAnswers.put(position, true);
                                    }

                                    holder.llPatient.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent i = new Intent(context, SelectPatientsActivity.class);
                                            ((Activity) context).startActivityForResult(i, PATIENT_REQUEST);
                                        }
                                    });


                                    break;

                                //Checkbox---------------------------------------
                                case 7:
                                    final List<CheckBox> lstChkbox = new ArrayList<>();
                                    holder.llChck.setVisibility(View.VISIBLE);
                                    if (questions.getChkb().size() > 0) {

                                        holder.llChck.removeAllViews();

                                        for (int i = 0; i < questions.getChkb().size(); i++) {

                                            CheckBox cb = new CheckBox(context);
                                            cb.setLayoutParams(new ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                                            cb.setText(questions.getChkb().get(i).getOpt());
                                            cb.setTextSize(22);
                                            holder.llChck.addView(cb);
                                            lstChkbox.add(cb);

                                            if (answerList.get(position).getSelectedChk() != null && !answerList.get(position).getSelectedChk().equals("")) {

                                                String a = answerList.get(position).getSelectedChk();
                                                if (a != null) {
                                                    String val = String.valueOf(a.charAt(i));

                                                    if (val.equals("1")) {
                                                        cb.setChecked(true);
                                                    } else {
                                                        cb.setChecked(false);
                                                    }
                                                }
                                            }
                                        }


                                        if (questions.getCompulsary()) {
                                            if (answerList.get(position).getSelectedChk() != null && !answerList.get(position).getSelectedChk().equals("")) {//boolean v = validate.validateString();
                                                validateAnswers.put(position, true);
                                            } else {
                                                validateAnswers.put(position, false);
                                            }

                                        } else {
                                            validateAnswers.put(position, true);
                                        }


                                        if (!enable) {

                                            for (int i = 0; i < lstChkbox.size(); i++) {
                                                lstChkbox.get(i).setEnabled(false);
                                            }
                                        }


                                        for (int i = 0; i < lstChkbox.size(); i++) {
                                            lstChkbox.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                    getSelectedChkbox(position, lstChkbox);
                                                    answerList.get(position).setAnswered(true);
                                                    notifyItemChanged(position);
                                                }
                                            });
                                        }


                                        if (answerList.get(position).getSelectedChk() != null) {
                                            if (answerList.get(position).getSelectedChk().contains("1")) {
                                                holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background_answered));
                                                answerList.get(position).setAnswered(true);
                                                holder.ivRedo.setVisibility(View.VISIBLE);
                                            } else {

                                                holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background));
                                                answerList.get(position).setAnswered(false);
                                                holder.ivRedo.setVisibility(GONE);
                                            }
                                        }


                                    }
                                    break;

                                //Options-----------------------------------------
                                case 8:


                                    List<RadioButton> allRb = new ArrayList<>();
                                    holder.rgOption.setVisibility(View.VISIBLE);


                                    int radioButtonID = holder.rgOption.getCheckedRadioButtonId();
                                    View radioButton = holder.rgOption.findViewById(radioButtonID);
                                    final int checkedIndex = holder.rgOption.indexOfChild(radioButton);
                                    CH_INDEX = checkedIndex;

                                    holder.rgOption.removeAllViews();


                                    Log.v("TAG", "RB " + questions.getOptions().size());

                                    for (int i = 0; i < questions.getOptions().size(); i++) {


                                        RadioButton rb = new RadioButton(context);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
                                        params.setMargins(0, 2, 0, 2);
                                        rb.setLayoutParams(params);
                                        rb.setText(questions.getOptions().get(i).getOpt());
                                        rb.setButtonDrawable(R.drawable.radio_button_switch);
                                        rb.setBackgroundResource(R.drawable.ra_background);
                                        rb.setTextSize(22);
                                        holder.rgOption.addView(rb);

                                        allRb.add(rb);

                                    }


                                    if (answerList.get(position).getSelectedopt() != -1 /*&& hashAllRb.get(position) != null*/) {


                                        holder.rgOption.check(allRb.get(answerList.get(position).getSelectedopt()).getId());
                                        holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background_answered));
                                        answerList.get(position).setAnswered(true);
                                        holder.ivRedo.setVisibility(View.VISIBLE);


                                    } else {

                                        holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background));
                                        answerList.get(position).setAnswered(false);
                                        holder.ivRedo.setVisibility(GONE);
                                    }

                                    if (questions.getCompulsary()) {
                                        if (questions.getCompulsary()) {
                                            if (answerList.get(position).getSelectedopt() != -1) {
                                                validateAnswers.put(position, true);
                                            } else {
                                                validateAnswers.put(position, false);
                                            }
                                        }
                                    } else {
                                        validateAnswers.put(position, true);
                                    }


                                    if (!enable) {
                                        for (int i = 0; i < holder.rgOption.getChildCount(); i++) {
                                            ((RadioButton) holder.rgOption.getChildAt(i)).setEnabled(false);
                                        }
                                    }


                                    holder.rgOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup radioGroup, int i) {


                                            int id = holder.rgOption.getCheckedRadioButtonId();
                                            View radioButton = holder.rgOption.findViewById(id);
                                            answerList.get(position).setSelectedopt(holder.rgOption.indexOfChild(radioButton));
                                            saveAnswer.onAnswerSave(position, answerList.get(position));


                                            if (CH_INDEX != answerList.get(position).getSelectedopt()) {


                                                android.os.Handler mHandler = new Handler();

                                                mHandler.post(new Runnable() {
                                                    public void run() {
                                                        //change adapter contents
                                                        notifyItemChanged(position);

                                                    }
                                                });
                                            }


                                        }
                                    });

                                    break;


                                //Conditional---------------------------------------
                                case 9:


                                    List<RadioButton> allRbCon = new ArrayList<>();
                                    holder.rgOptionConditional.setVisibility(View.VISIBLE);
                                    holder.ivNes.setVisibility(View.VISIBLE);


                                    int radioButtonID1 = holder.rgOptionConditional.getCheckedRadioButtonId();
                                    View radioButton1 = holder.rgOptionConditional.findViewById(radioButtonID1);
                                    int checkedIndexC = holder.rgOptionConditional.indexOfChild(radioButton1);
                                    CH_INDEX_C = checkedIndexC;

                                    holder.rgOptionConditional.removeAllViews();

                                    Log.v("TAG", "CONDITIONAL " + questions.getOptionContidion().size());
                                    for (int i = 0; i < questions.getOptionContidion().size(); i++) {


                                        RadioButton rb = new RadioButton(context);

                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
                                        params.setMargins(0, 2, 0, 2);
                                        rb.setLayoutParams(params);
                                        rb.setTextSize(22);
                                        rb.setText(questions.getOptionContidion().get(i).getOpt());
                                        rb.setBackgroundResource(R.drawable.ra_background);
                                        rb.setButtonDrawable(R.drawable.radio_button_switch);
                                        holder.rgOptionConditional.addView(rb);

                                        allRbCon.add(rb);

                                    }


                                    if (answerList.get(position).getSelectedOptConditional() != -1/* && hashAllCondRb.get(position) != null*/) {

                                        holder.rgOptionConditional.check(allRbCon.get(answerList.get(position).getSelectedOptConditional()).getId());
                                        holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background_answered));
                                        answerList.get(position).setAnswered(true);
                                        holder.ivRedo.setVisibility(View.VISIBLE);


                                    } else {

                                        holder.rgOptionConditional.clearCheck();
                                        holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background));
                                        answerList.get(position).setAnswered(false);
                                        holder.ivRedo.setVisibility(View.GONE);
                                    }


                                    if (!enable) {
                                        for (int i = 0; i < holder.rgOptionConditional.getChildCount(); i++) {
                                            ((RadioButton) holder.rgOptionConditional.getChildAt(i)).setEnabled(false);
                                        }
                                    }

                                    if (questions.getCompulsary()) {
                                        if (questions.getCompulsary()) {
                                            if (answerList.get(position).getSelectedOptConditional() != -1) {
                                                validateAnswers.put(position, true);
                                            } else {
                                                validateAnswers.put(position, false);
                                            }
                                        }
                                    } else {
                                        validateAnswers.put(position, true);
                                    }


                                    holder.rgOptionConditional.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup radioGroup, int i) {


                                            int id = holder.rgOptionConditional.getCheckedRadioButtonId();
                                            View radioButton = holder.rgOptionConditional.findViewById(id);
                                            if (radioButton != null) {
                                                if (radioButton.isPressed()) {
                                                    answerList.get(position).setAnswered(true);
                                                    long surveyId = 0;

                                                    answerList.get(position).setSelectedOptConditional(holder.rgOptionConditional.indexOfChild(radioButton));
                                                    surveyId = questions.getOptionContidion().get(holder.rgOptionConditional.indexOfChild(radioButton)).getSurveyid();

                                                    saveAnswer.onAnswerSave(position, answerList.get(position));
                                                    saveAnswer.onAddSurvey(surveyId, position, answerList.get(position).getParentPos(), answerList.get(position).getQuestions().getId());

                                                }
                                            }

                                        }
                                    });


                                    break;

                            }
                        }
                    }

                    if (questions.getCompulsary() == true) {
                        holder.tvCompulsary.setVisibility(View.VISIBLE);
                        if (showerror == true && validateAnswers.get(position) != null) {

                            Boolean result = validateAnswers.get(position);
                            if (!result) {
                                holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_error_background));

                            }/* else {
                                holder.tvCounter.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.counter_background));
                            }*/

                        }
                    }


                } else {
                    holder.btnSave.setVisibility(View.VISIBLE);
                    holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                    holder.viewDivider.setVisibility(GONE);
                    holder.llQuestion.setVisibility(GONE);

                    holder.tvCounter.setVisibility(GONE);
                }
            }
        });

        // onBind = false;
    }

    public void removeAt(int position) {
        answerList.remove(position);
        notifyItemRemoved(position);

    }

    public void addAt(int position, Answers ans) {
        answerList.add(position, ans);
        notifyItemInserted(position);
    }


    public void getSelectedChkbox(int position, List<CheckBox> lstCheckbox) {

        String checkedData = "";
        for (int i = 0; i < lstCheckbox.size(); i++) {
            if (lstCheckbox.get(i).isChecked()) {
                checkedData = checkedData + "1";
            } else {
                checkedData = checkedData + "0";
            }
        }


        answerList.get(position).setSelectedChk(checkedData);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }


}