package com.ak.search.fragment;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.ak.search.R;
import com.ak.search.app.SaveAnswer;
import com.ak.search.realm_model.Answers;
import com.ak.search.realm_model.Questions;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static android.view.View.GONE;

/**
 * Created by dg hdghfd on 08-12-2016.
 * show the questions to be display
 */

public class QuestionFragment extends Fragment {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    @BindView(R.id.rg_options)
    RadioGroup rg_option;

    @BindView(R.id.rg_options_conditional)
    RadioGroup rg_option_conditional;

    @BindView(R.id.rb_opt1)
    RadioButton rb_opt1;
    @BindView(R.id.rb_opt2)
    RadioButton rb_opt2;

    @BindView(R.id.rb_opt1_conditional)
    RadioButton rb_opt1_conditional;
    @BindView(R.id.rb_opt2_conditional)
    RadioButton rb_opt2_conditional;

    @BindView(R.id.txt_answer)
    EditText et_answer;
    @BindView(R.id.txt_number)
    EditText et_number;

    @BindView(R.id.tv_question)
    TextView tv_question;


    @BindView(R.id.btn_date)
    Button btnDate;
    @BindView(R.id.btn_time)
    Button btnTime;
    @BindView(R.id.btn_capture_image)
    Button btnCaptureImage;
    @BindView(R.id.iv_capture)
    ImageView ivCapture;
    @BindView(R.id.ll_check)
    LinearLayout llCheck;
    @BindView(R.id.ll_image)
    LinearLayout llSelectImage;
    @BindView(R.id.ll_time)
    LinearLayout llTime;
    @BindView(R.id.ll_date)
    LinearLayout llDate;

    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvDate)
    TextView tvDate;

    private int mYear, mMonth, mDay, mHour, mMinute;
    public static int CAMERA_REQUEST = 11;

    List<CheckBox> lstChkbox;
    SaveAnswer answer;

    public Answers ans;
    Realm realm;


    public static final QuestionFragment newInstance(Questions message) {
        QuestionFragment f = new QuestionFragment();
        Bundle bdl = new Bundle(1);
        bdl.putParcelable(EXTRA_MESSAGE, Parcels.wrap(message));
        f.setArguments(bdl);
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_question, container, false);
        ButterKnife.bind(this, v);
        realm = Realm.getDefaultInstance();

        lstChkbox = new ArrayList<>();

        final Questions questions = Parcels.unwrap(getArguments().getParcelable(EXTRA_MESSAGE));


        tv_question.setText(questions.getQuestion());



        ans = new Answers();


        ans.setQuestions(questions);

        if (!questions.getOpt()) {
            rg_option.setVisibility(GONE);
            ans.setSelectedopt(-1);
        } else {

            if (questions.getOptions().size() > 0) {
                for (int i = 0; i < questions.getOptions().size(); i++) {
                    if (i < 2) {
                        rb_opt1.setText(questions.getOptions().get(0).getOpt());
                        rb_opt2.setText(questions.getOptions().get(1).getOpt());

                    } else {
                        RadioButton rb = new RadioButton(getContext());
                        rb.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        rb.setText(questions.getOptions().get(i).getOpt());
                        rg_option.addView(rb);
                    }

                }
            }

        }

        if (!questions.getText()) {
            et_answer.setVisibility(GONE);
            ans.setAns("-");
        }


        if (!questions.getCheckbox()) {

            llCheck.setVisibility(GONE);
            ans.setSelectedChk("-");
        } else {
            if (questions.getChkb().size() > 0) {
                for (int i = 0; i < questions.getChkb().size(); i++) {

                    CheckBox cb = new CheckBox(getContext());
                    cb.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    cb.setText(questions.getChkb().get(i).getOpt());

                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            getSelectedChkbox();
                        }
                    });

                    llCheck.addView(cb);
                    lstChkbox.add(cb);
                }

            }

        }


        if (!questions.getOptCondition()) {
            rg_option_conditional.setVisibility(GONE);
            //ans.setse(-1);
        } else {

            if (questions.getOptionContidion().size() > 0) {
                for (int i = 0; i < questions.getOptionContidion().size(); i++) {
                    if (i == 0) {
                        rb_opt1_conditional.setText(questions.getOptionContidion().get(0).getOpt());
                    } else if (i == 1) {
                        rb_opt2_conditional.setText(questions.getOptionContidion().get(1).getOpt());
                    } else {
                        RadioButton rb = new RadioButton(getContext());
                        rb.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        rb.setText(questions.getOptionContidion().get(i).getOpt());
                        rg_option_conditional.addView(rb);
                    }

                }
            }

        }


        if (!questions.getNumber()) {
            et_number.setVisibility(GONE);
            ans.setNumAns("-");
        }

        if (!questions.getDate()) {
            llDate.setVisibility(GONE);
            ans.setDate("-");
        }

        if (!questions.getTime()) {
            llTime.setVisibility(GONE);
            ans.setTime("-");
        }

        if (!questions.getImage()) {
            byte[] aa = {-1};
            llSelectImage.setVisibility(GONE);
            ans.setByteArrayImage(aa);
        }



        et_answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ans.setAns(String.valueOf(editable));

                answer.onAnswerSave(0,ans);
            }
        });

        et_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ans.setNumAns(String.valueOf(editable));

                answer.onAnswerSave(0,ans);
            }
        });

        rg_option.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                int id = rg_option.getCheckedRadioButtonId();
                View radioButton = rg_option.findViewById(id);
                ans.setSelectedopt(rg_option.indexOfChild(radioButton));

                answer.onAnswerSave(0,ans);
            }
        });


        rg_option_conditional.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                int id = rg_option_conditional.getCheckedRadioButtonId();
                View radioButton = rg_option_conditional.findViewById(id);

                long surveyId = questions.getOptionContidion().get(rg_option_conditional.indexOfChild(radioButton)).getSurveyid();

            }
        });

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker();
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker();
            }
        });

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });


        return v;

    }


    @Override
    public void onAttach(Activity activity) {
        try {
            answer = (SaveAnswer) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
        super.onAttach(activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ivCapture.setImageBitmap(photo);


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            ans.setByteArrayImage(byteArray);

            answer.onAnswerSave(0,ans);
        }
    }


    public void datePicker() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        tvDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        ans.setDate(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        answer.onAnswerSave(0,ans);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    public void timePicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        tvTime.setText(hourOfDay + ":" + minute);
                        ans.setTime(hourOfDay + ":" + minute);
                        //getSelectedChkbox();
                        answer.onAnswerSave(0,ans);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void getSelectedChkbox() {
        if (!lstChkbox.isEmpty()) {

            String checkedData = "";

            for (int i = 0; i < lstChkbox.size(); i++) {
                if (lstChkbox.get(i).isChecked()) {

                    checkedData = checkedData + "1";
                } else {
                    checkedData = checkedData + "0";
                }
            }




            ans.setSelectedChk(checkedData);
            answer.onAnswerSave(0,ans);
        }
    }


}
