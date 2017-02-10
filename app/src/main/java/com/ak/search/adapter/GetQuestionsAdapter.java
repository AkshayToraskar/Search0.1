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
import com.ak.search.app.SaveAnswer;
import com.ak.search.realm_model.Answers;
import com.ak.search.realm_model.Questions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class GetQuestionsAdapter extends RecyclerView.Adapter<GetQuestionsAdapter.MyViewHolder> {

    private List<Answers> answerList;
    private Context context;
    private List<CheckBox> lstChkbox;
    public static int CAMERA_REQUEST = 11;
    private int mYear, mMonth, mDay, mHour, mMinute;
    SaveAnswer saveAnswer;

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


        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            /*tvQuestion = (TextView) view.findViewById(R.id.tv_question);
            etAnswer = (EditText) view.findViewById(R.id.txt_answer);
            rgOption = (RadioGroup) view.findViewById(R.id.rg_options);
            rbOpt1 = (RadioButton) view.findViewById(R.id.rb_opt1);
            rbOpt2 = (RadioButton) view.findViewById(R.id.rb_opt2);

            rgOptionConditional = (RadioGroup) view.findViewById(R.id.rg_options_conditional);
            rbOpt1Conditional = (RadioButton) view.findViewById(R.id.rb_opt1_conditional);
            rbOpt2Conditional = (RadioButton) view.findViewById(R.id.rb_opt2_conditional);

            tvDate = (TextView) view.findViewById(R.id.txt_date);
            tvTime = (TextView) view.findViewById(R.id.txt_time);
            etNumAns = (EditText) view.findViewById(R.id.txt_number);
            ivSelImg = (ImageView) view.findViewById(R.id.iv_capture);
            llChck = (LinearLayout) view.findViewById(R.id.ll_check);*/

            lstChkbox = new ArrayList<>();

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

                                    // ans.setDate(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                    //getSelectedChkbox();
                                    // answer.onAnswerSave(ans);
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
                                    // ans.setTime(hourOfDay + ":" + minute);
                                    //getSelectedChkbox();
                                    // answer.onAnswerSave(ans);
                                }
                            }, mHour, mMinute, false);
                    timePickerDialog.show();
                }
            });

            btnSelectImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    ((Activity) context).startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            });


        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //ivCapture.setImageBitmap(photo);


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            //ans.setByteArrayImage(byteArray);
            //getSelectedChkbox();
            // answer.onAnswerSave(ans);
        }
    }

    public GetQuestionsAdapter(Context context, List<Answers> answerList, SaveAnswer saveAnswer) {
        this.answerList = answerList;
        this.context = context;
        this.saveAnswer = saveAnswer;
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
        holder.tvQuestion.setText(questions.getQuestion());
        //Log.v("asdff a", "" + questionsList.get(position).getId());
        List<RadioButton> allRb = new ArrayList<>();


        if (!questions.getOpt()) {
            holder.rgOption.setVisibility(GONE);
            answerList.get(position).setSelectedopt(-1);
        } else {


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


            /*if (questions.getAnswers() != null) {
                allRb.get(questionsList.get(position).getAnswers().getSelectedopt()).setChecked(true);
                Log.v("sadf--------", "asdf " + questionsList.get(position).getAnswers().getSelectedopt());
            }*/

        }


        if (!questions.getOptCondition()) {
            holder.rgOptionConditional.setVisibility(GONE);

        } else {

            for (int i = 0; i < questions.getOptionContidion().size(); i++) {

                if (i == 0) {
                    holder.rbOpt1Conditional.setText(questions.getOptionContidion().get(0).getOpt());
                    allRb.add(holder.rbOpt1Conditional);
                } else if (i == 1) {
                    holder.rbOpt2Conditional.setText(questions.getOptionContidion().get(1).getOpt());
                    allRb.add(holder.rbOpt2Conditional);
                } else {
                    RadioButton rb = new RadioButton(context);
                    rb.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    rb.setText(questions.getOptionContidion().get(i).getOpt());
                    holder.rgOptionConditional.addView(rb);
                    allRb.add(rb);
                }
            }


            /*holder.rgOptionConditional.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    int id = radioGroup.getCheckedRadioButtonId();
                    View radioButton = holder.rgOptionConditional.findViewById(id);
                    //ans.setSelectedopt(rg_option.indexOfChild(radioButton));
                    long surveyId = questions.getOptionContidion().get(holder.rgOptionConditional.indexOfChild(radioButton)).getSurveyid();

                    saveAnswer.onAddSurvey(surveyId, position);
                }
            });*/

            //if (answerList.get(position).getSelectedopt() != -1) {
            //   allRb.get(answerList.get(position).getSelectedopt()).setChecked(true);
            //}

        }


        if (!questions.getText()) {
            holder.etAnswer.setVisibility(GONE);
            answerList.get(position).setAns("-");
        } else {
            if (questions.getText() != null) {
                holder.etAnswer.setText(answerList.get(position).getAns());
            }
        }


        if (!questions.getNumber()) {
            holder.etNumAns.setVisibility(GONE);
            answerList.get(position).setNumAns("-");
        } else {
            if (questions.getNumber() != null) {
                holder.etNumAns.setText(answerList.get(position).getNumAns());
            }
        }

        if (!questions.getCheckbox()) {
            holder.llChck.setVisibility(GONE);
            answerList.get(position).setSelectedChk("-");
        } else {


            if (questions.getChkb().size() > 0) {
                for (int i = 0; i < questions.getChkb().size(); i++) {

                    CheckBox cb = new CheckBox(context);
                    cb.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    cb.setText(questions.getChkb().get(i).getOpt());
                    holder.llChck.addView(cb);
                    lstChkbox.add(cb);
                    //}
                    //allEds.add(text);


                    if (answerList.get(position).getSelectedChk() != null && !answerList.get(position).getSelectedChk().equals("-")) {

                        String a = answerList.get(position).getSelectedChk();
                        if (a != null) {
                            String val = String.valueOf(a.charAt(i));

                            if (val.equals("1")) {
                                cb.setChecked(true);
                            } else {
                                cb.setChecked(false);
                            }
                        }



                    /*for (int i = 0; i < a.length; i++) {
                        if (a[i] != null && !a[i].equals("")) {
                            lstChkbox.get(Integer.parseInt(a[i])).setChecked(true);
                        }
                    }*/
                    }
                }
            }

        }

        if (!questions.getDate()) {
            holder.tvDate.setVisibility(GONE);
            holder.btnSelectDate.setVisibility(GONE);
            answerList.get(position).setDate("-");
        } else {
            holder.tvDate.setText(answerList.get(position).getDate());
        }

        if (!questions.getTime()) {
            holder.tvTime.setVisibility(GONE);
            holder.btnSelectTime.setVisibility(GONE);
            answerList.get(position).setTime("-");
        } else {
            holder.tvTime.setText(answerList.get(position).getTime());
        }

        if (!questions.getImage())

        {
            byte aa[] = {-1};
            answerList.get(position).setByteArrayImage(aa);
            holder.ivSelImg.setVisibility(GONE);
            holder.btnSelectImage.setVisibility(GONE);
        } else {
            if (answerList.get(position).getByteArrayImage() != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(answerList.get(position).getByteArrayImage(), 0, answerList.get(position).getByteArrayImage().length);
                holder.ivSelImg.setImageBitmap(bmp);
            }
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
                answerList.get(position).setAns(String.valueOf(editable));
                //getSelectedChkbox();
                saveAnswer.onAnswerSave(answerList.get(position));
            }
        });

        holder.etNumAns.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                answerList.get(position).setNumAns(String.valueOf(editable));
                //getSelectedChkbox();
                saveAnswer.onAnswerSave(answerList.get(position));
            }
        });

        holder.rgOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                int id = holder.rgOption.getCheckedRadioButtonId();
                View radioButton = holder.rgOption.findViewById(id);
                answerList.get(position).setSelectedopt(holder.rgOption.indexOfChild(radioButton));

                saveAnswer.onAnswerSave(answerList.get(position));
            }
        });


        holder.rgOptionConditional.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                int id = holder.rgOptionConditional.getCheckedRadioButtonId();
                View radioButton = holder.rgOptionConditional.findViewById(id);
                answerList.get(position).setSelectedopt(holder.rgOptionConditional.indexOfChild(radioButton));
                long surveyId = questions.getOptionContidion().get(holder.rgOptionConditional.indexOfChild(radioButton)).getSurveyid();
                saveAnswer.onAddSurvey(surveyId, position);
            }
        });


        /*holder.etAnswer.setEnabled(false);
        holder.etNumAns.setEnabled(false);
        for (
                int i = 0;
                i < holder.rgOption.getChildCount(); i++)

        {
            holder.rgOption.getChildAt(i).setEnabled(false);
        }

        if (lstChkbox != null)

        {
            for (int i = 0; i < lstChkbox.size(); i++) {
                lstChkbox.get(i).setEnabled(false);
            }
        }*/

    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }


    /*public void update(List<MAnswers> modelList) {
        answerList.clear();
        answerList.addAll(modelList);
        notifyDataSetChanged();
    }*/
}