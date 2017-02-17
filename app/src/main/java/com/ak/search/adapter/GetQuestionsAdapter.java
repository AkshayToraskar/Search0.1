package com.ak.search.adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.ak.search.activity.StartSurveyActivity;
import com.ak.search.app.SaveAnswer;
import com.ak.search.app.Validate;
import com.ak.search.realm_model.Answers;
import com.ak.search.realm_model.Questions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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
    private int mYear, mMonth, mDay, mHour, mMinute;
    SaveAnswer saveAnswer;
    Realm realm;
    boolean enable, showerror = false;
    private HashMap<Integer, Boolean> validateAnswers;

    Validate validate;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_question)
        TextView tvQuestion;
        @BindView(R.id.txt_date)
        TextView tvDate;
        @BindView(R.id.txt_time)
        TextView tvTime;
        @BindView(R.id.txt_answer)
        EditText etAnswer;
        @BindView(R.id.txt_number)
        EditText etNumAns;
        @BindView(R.id.rg_options)
        RadioGroup rgOption;
        @BindView(R.id.rg_options_conditional)
        RadioGroup rgOptionConditional;
        @BindView(R.id.rb_opt1)
        RadioButton rbOpt1;
        @BindView(R.id.rb_opt2)
        RadioButton rbOpt2;
        @BindView(R.id.rb_opt1_conditional)
        RadioButton rbOpt1Conditional;
        @BindView(R.id.rb_opt2_conditional)
        RadioButton rbOpt2Conditional;
        @BindView(R.id.iv_capture)
        ImageView ivSelImg;
        @BindView(R.id.ll_check)
        LinearLayout llChck;
        @BindView(R.id.btn_date)
        Button btnSelectDate;
        @BindView(R.id.btn_time)
        Button btnSelectTime;
        @BindView(R.id.btn_capture_image)
        Button btnSelectImage;

        @BindView(R.id.btn_save)
        Button btnSave;


        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            validateAnswers = new HashMap<>();


            btnSelectDate.setOnClickListener(new View.OnClickListener() {
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

                                    tvDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                    answerList.get(getPosition()).setDate(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                    //getSelectedChkbox();
                                    saveAnswer.onAnswerSave(answerList.get(getPosition()));
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
            });

            btnSelectTime.setOnClickListener(new View.OnClickListener() {
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

                                    tvTime.setText(hourOfDay + ":" + minute);
                                    answerList.get(getPosition()).setTime(hourOfDay + ":" + minute);
                                    //getSelectedChkbox();
                                    saveAnswer.onAnswerSave(answerList.get(getPosition()));
                                }
                            }, mHour, mMinute, false);
                    timePickerDialog.show();
                }
            });

            btnSelectImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StartSurveyActivity.positionImg = getPosition();
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    ((Activity) context).startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("validate size", " : " + validateAnswers.size());
                    showerror = true;
                    notifyDataSetChanged();
                }
            });

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data, int pos) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            answerList.get(pos).setByteArrayImage(byteArray);
            notifyDataSetChanged();
        }
    }

    public GetQuestionsAdapter(Context context, List<Answers> answerList, SaveAnswer saveAnswer, Realm realm, boolean enable) {
        this.answerList = answerList;
        this.context = context;
        this.saveAnswer = saveAnswer;
        this.realm = realm;
        this.enable = enable;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.questions_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        //holder.setIsRecyclable(false);
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

                if (answerList.get(position).getQuestions() != null) {

                    holder.tvQuestion.setVisibility(View.VISIBLE);
                    holder.tvQuestion.setText(questions.getQuestion());


                    if (questions.getCompulsary() == true && showerror == true) {
                        if(validateAnswers.get(position)!=null) {
                            Boolean result = validateAnswers.get(position);
                            if (result) {
                                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.cardview_dark_background));
                            }
                        }
                    }


                    String questionType = questions.getTypeQuestion();
                    String[] quest = questionType.split(",");
                    for (int l = 0; l < quest.length; l++) {
                        int que = Integer.parseInt(quest[l]);
                        switch (que) {

                            //Text---------------------------------------
                            case 1:
                                holder.etAnswer.setVisibility(View.VISIBLE);
                                if (answerList.get(position).getAns() != null) {
                                    holder.etAnswer.setText(answerList.get(position).getAns());
                                }

                                if (!enable) {
                                    holder.etAnswer.setEnabled(false);
                                }

                                if (questions.getCompulsary()) {
                                    boolean v = validate.validateString(holder.etAnswer.getText().toString());
                                    validateAnswers.put(position, v);
                                }

                                holder.etAnswer.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    }

                                    @Override
                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {
                                        if (editable != null) {
                                            answerList.get(position).setAns(String.valueOf(editable));
                                            saveAnswer.onAnswerSave(answerList.get(position));
                                        }
                                    }
                                });

                                break;

                            //Number---------------------------------------
                            case 2:
                                holder.etNumAns.setVisibility(View.VISIBLE);
                                if (answerList.get(position).getNumAns() != null) {
                                    holder.etNumAns.setText(answerList.get(position).getNumAns());
                                }

                                if (!enable) {
                                    holder.etNumAns.setEnabled(false);
                                }

                                if (questions.getCompulsary()) {
                                    boolean v = validate.validateString(holder.etNumAns.getText().toString());
                                    validateAnswers.put(position, v);
                                }

                                holder.etNumAns.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    }

                                    @Override
                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {
                                        if (editable != null)
                                            answerList.get(position).setNumAns(String.valueOf(editable));
                                        saveAnswer.onAnswerSave(answerList.get(position));
                                    }
                                });
                                break;

                            //Date---------------------------------------
                            case 3:
                                holder.tvDate.setVisibility(View.VISIBLE);
                                if (enable) {
                                    holder.btnSelectDate.setVisibility(View.VISIBLE);
                                }

                                if (answerList.get(position).getDate() != null) {
                                    holder.tvDate.setText(answerList.get(position).getDate());
                                }

                                if (questions.getCompulsary()) {
                                    boolean v = validate.validateString(holder.tvDate.getText().toString());
                                    validateAnswers.put(position, v);
                                }
                                break;

                            //Time---------------------------------------
                            case 4:
                                holder.tvTime.setVisibility(View.VISIBLE);
                                if (enable) {
                                    holder.btnSelectTime.setVisibility(View.VISIBLE);
                                }

                                if (answerList.get(position).getTime() != null) {
                                    holder.tvTime.setText(answerList.get(position).getTime());
                                }


                                if (questions.getCompulsary()) {
                                    boolean v = validate.validateString(holder.tvTime.getText().toString());
                                    validateAnswers.put(position, v);
                                }
                                break;

                            //Image---------------------------------------
                            case 5:

                                holder.ivSelImg.setVisibility(View.VISIBLE);
                                if (enable) {
                                    holder.btnSelectImage.setVisibility(View.VISIBLE);
                                }

                                if (answerList.get(position).getByteArrayImage() != null) {
                                    Bitmap bmp = BitmapFactory.decodeByteArray(answerList.get(position).getByteArrayImage(), 0, answerList.get(position).getByteArrayImage().length);
                                    holder.ivSelImg.setImageBitmap(bmp);
                                }


                                break;

                            //Compulsary---------------------------------------
                            case 6:
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
                                        boolean v = validate.validateString(answerList.get(position).getSelectedChk());
                                        validateAnswers.put(position, v);
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
                                            }
                                        });
                                    }
                                }
                                break;

                            //Options-----------------------------------------
                            case 8:
                                List<RadioButton> allRb = new ArrayList<>();
                                holder.rgOption.setVisibility(View.VISIBLE);
                                for (int i = 0; i < questions.getOptions().size(); i++) {
                                    if (i == 0) {
                                        holder.rbOpt1.setText(questions.getOptions().get(0).getOpt());

                                        allRb.add(holder.rbOpt1);
                                    } else if (i == 1) {
                                        holder.rbOpt2.setText(questions.getOptions().get(1).getOpt());

                                        allRb.add(holder.rbOpt2);
                                    } else {
                                        RadioButton rb = new RadioButton(context);
                                        rb.setLayoutParams(new ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                        rb.setText(questions.getOptions().get(i).getOpt());

                                        holder.rgOption.addView(rb);
                                        allRb.add(rb);
                                    }
                                }
                                if (answerList.get(position).getSelectedopt() != -1) {
                                    allRb.get(answerList.get(position).getSelectedopt()).setChecked(true);
                                }

                                if (questions.getCompulsary()) {
                                    // boolean v=validate.validateRB(answerList.get(position).getSelectedopt());
                                    // validateAnswers.put(position,v);
                                }


                                holder.rgOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                        int id = holder.rgOption.getCheckedRadioButtonId();
                                        View radioButton = holder.rgOption.findViewById(id);
                                        answerList.get(position).setSelectedopt(holder.rgOption.indexOfChild(radioButton));
                                        saveAnswer.onAnswerSave(answerList.get(position));
                                        notifyDataSetChanged();
                                    }
                                });

                                if (!enable) {
                                    for (int i = 0; i < holder.rgOption.getChildCount(); i++) {
                                        ((RadioButton) holder.rgOption.getChildAt(i)).setEnabled(false);
                                    }
                                }

                                break;


                            //Conditional---------------------------------------
                            case 9:
                                List<RadioButton> allRbCon = new ArrayList<>();
                                holder.rgOptionConditional.setVisibility(View.VISIBLE);
                                for (int i = 0; i < questions.getOptionContidion().size(); i++) {
                                    if (i == 0) {
                                        holder.rbOpt1Conditional.setText(questions.getOptionContidion().get(0).getOpt());

                                        allRbCon.add(holder.rbOpt1Conditional);
                                    } else if (i == 1) {
                                        holder.rbOpt2Conditional.setText(questions.getOptionContidion().get(1).getOpt());

                                        allRbCon.add(holder.rbOpt2Conditional);
                                    } else {
                                        RadioButton rb = new RadioButton(context);
                                        rb.setLayoutParams(new ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                        rb.setText(questions.getOptionContidion().get(i).getOpt());

                                        holder.rgOptionConditional.addView(rb);

                                        allRbCon.add(rb);
                                    }
                                }
                                if (answerList.get(position).getSelectedOptConditional() != -1) {
                                    allRbCon.get(answerList.get(position).getSelectedOptConditional()).setChecked(true);
                                }

                                if (!enable) {
                                    for (int i = 0; i < holder.rgOptionConditional.getChildCount(); i++) {
                                        ((RadioButton) holder.rgOptionConditional.getChildAt(i)).setEnabled(false);
                                    }
                                }

                                if (questions.getCompulsary()) {
                                    //   boolean v=validate.validateRB(answerList.get(position).getSelectedOptConditional());
                                    //   validateAnswers.put(position,v);
                                }

                                holder.rgOptionConditional.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                        int id = holder.rgOptionConditional.getCheckedRadioButtonId();
                                        View radioButton = holder.rgOptionConditional.findViewById(id);
                                        answerList.get(position).setSelectedOptConditional(holder.rgOptionConditional.indexOfChild(radioButton));
                                        long surveyId = questions.getOptionContidion().get(holder.rgOptionConditional.indexOfChild(radioButton)).getSurveyid();
                                        saveAnswer.onAddSurvey(surveyId, position, answerList.get(position).getParentPos());
                                        notifyDataSetChanged();
                                    }
                                });
                                break;

                        }
                    }


                    /*


                    final

                    if (!questions.getOpt()) {
                        holder.rgOption.setVisibility(GONE);
                        answerList.get(position).setSelectedopt(-1);
                    } else {


                        //  holder.rgOption.check(answerList.get(position).getSelectedopt());

                    }

                    if (!questions.getOptCondition()) {
                        holder.rgOptionConditional.setVisibility(GONE);
                        answerList.get(position).setSelectedOptConditional(-1);
                    } else {


                        //holder.rgOptionConditional.check(answerList.get(position).getSelectedOptConditional());
                    }

                    if (!questions.getText()) {
                        holder.etAnswer.setVisibility(GONE);
                        answerList.get(position).setAns("-");
                    } else {

                    }

                    if (!questions.getNumber()) {
                        holder.etNumAns.setVisibility(GONE);
                        answerList.get(position).setNumAns("-");
                    } else {

                    }

                    if (!questions.getCheckbox()) {
                        holder.llChck.setVisibility(GONE);
                        answerList.get(position).setSelectedChk("-");
                    } else {


                    }

                    if (!questions.getDate()) {
                        holder.tvDate.setVisibility(GONE);
                        holder.btnSelectDate.setVisibility(GONE);
                        answerList.get(position).setDate("-");
                    } else {

                    }

                    if (!questions.getTime()) {
                        holder.tvTime.setVisibility(GONE);
                        holder.btnSelectTime.setVisibility(GONE);
                        answerList.get(position).setTime("-");
                    } else {

                    }

                    if (!questions.getImage()) {
                        byte aa[] = {-1};
                        answerList.get(position).setByteArrayImage(aa);
                        holder.ivSelImg.setVisibility(GONE);
                        holder.btnSelectImage.setVisibility(GONE);
                    } else {


                    }

                    */



                    /*if (questions.getCompulsary()) {
                        validateAnswers.put(position, validate);
                    } else {
                        validateAnswers.put(position, true);
                    }

                    if (showerror) {
                        if (!validateAnswers.get(position)) {
                            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.cardview_dark_background));
                        }
                    }*/


                } else {
                    holder.btnSave.setVisibility(View.VISIBLE);
                }
            }
        });
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