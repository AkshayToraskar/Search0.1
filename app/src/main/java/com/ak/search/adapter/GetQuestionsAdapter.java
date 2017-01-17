package com.ak.search.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ak.search.R;
import com.ak.search.realm_model.Answers;
import com.ak.search.realm_model.Questions;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class GetQuestionsAdapter extends RecyclerView.Adapter<GetQuestionsAdapter.MyViewHolder> {

    private List<Answers> answerList;
    private Context context;
    private List<CheckBox> lstChkbox;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvQuestion, tvDate, tvTime;
        public EditText etAnswer, etNumAns;
        public RadioGroup rgOption;
        public RadioButton rbOpt1, rbOpt2;
        public ImageView ivSelImg;
        public LinearLayout llChck;


        public MyViewHolder(View view) {
            super(view);
            tvQuestion = (TextView) view.findViewById(R.id.tvQuestion);
            etAnswer = (EditText) view.findViewById(R.id.txt_answer);
            rgOption = (RadioGroup) view.findViewById(R.id.rg_options);
            rbOpt1 = (RadioButton) view.findViewById(R.id.rb_opt1);
            rbOpt2 = (RadioButton) view.findViewById(R.id.rb_opt2);

            tvDate = (TextView) view.findViewById(R.id.txt_date);
            tvTime = (TextView) view.findViewById(R.id.txt_time);
            etNumAns = (EditText) view.findViewById(R.id.txt_number);
            ivSelImg = (ImageView) view.findViewById(R.id.iv_selImage);
            llChck = (LinearLayout) view.findViewById(R.id.ll_check);

            lstChkbox = new ArrayList<>();

        }
    }

    public GetQuestionsAdapter(Context context, List<Answers> answerList) {
        this.answerList = answerList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.questions_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        Questions questions = answerList.get(position).getQuestions();
        holder.tvQuestion.setText(questions.getQuestion());
        //Log.v("asdff a", "" + questionsList.get(position).getId());
        List<RadioButton> allRb = new ArrayList<>();

        if (!questions.getOpt()) {
            holder.rgOption.setVisibility(GONE);
        } else {


            for (int i = 0; i < answerList.get(position).getQuestions().getOptions().size(); i++) {

                if (i < 2) {
                    holder.rbOpt1.setText(answerList.get(position).getQuestions().getOptions().get(0).getOpt());
                    holder.rbOpt2.setText(answerList.get(position).getQuestions().getOptions().get(1).getOpt());
                    allRb.add(holder.rbOpt1);
                    allRb.add(holder.rbOpt2);

                } else {
                    RadioButton rb = new RadioButton(context);
                    rb.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    rb.setText(answerList.get(position).getQuestions().getOptions().get(i).getOpt());
                    holder.rgOption.addView(rb);
                    allRb.add(rb);
                }
            }
            allRb.get(answerList.get(position).getSelectedopt()).setChecked(true);


            /*if (questions.getAnswers() != null) {
                allRb.get(questionsList.get(position).getAnswers().getSelectedopt()).setChecked(true);
                Log.v("sadf--------", "asdf " + questionsList.get(position).getAnswers().getSelectedopt());
            }*/

        }

        if (!questions.getText()) {
            holder.etAnswer.setVisibility(GONE);
        } else {
            if (questions.getText() != null) {
                holder.etAnswer.setText(answerList.get(position).getAns());
            }
        }


        if (!questions.getText()) {
            holder.etNumAns.setVisibility(GONE);
        }

        if (!questions.getCheckbox()) {
            holder.llChck.setVisibility(GONE);
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
                }
                //allEds.add(text);


                if (answerList.get(position).getSelectedChk()!=null && !answerList.get(position).getSelectedChk().equals("")) {

                    String[] a = answerList.get(position).getSelectedChk().split(",");

                    for (int i = 0; i < a.length; i++) {
                        if (a[i] != null && !a[i].equals("")) {
                            lstChkbox.get(Integer.parseInt(a[i])).setChecked(true);
                        }
                    }
                }
            }

        }


        if (!questions.getDate()) {
            holder.tvDate.setVisibility(GONE);
        } else {
            holder.tvDate.setText(answerList.get(position).getDate());
        }

        if (!questions.getTime()) {
            holder.tvTime.setVisibility(GONE);
        } else {
            holder.tvTime.setText(answerList.get(position).getTime());
        }

        if (!questions.getImage()) {
            holder.ivSelImg.setVisibility(GONE);
        } else {
            if (answerList.get(position).getByteArrayImage() != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(answerList.get(position).getByteArrayImage(), 0, answerList.get(position).getByteArrayImage().length);
                holder.ivSelImg.setImageBitmap(bmp);
            }
        }


        holder.etAnswer.setEnabled(false);
        holder.etNumAns.setEnabled(false);
        for (int i = 0; i < holder.rgOption.getChildCount(); i++) {
            holder.rgOption.getChildAt(i).setEnabled(false);
        }
        if(lstChkbox!=null) {
            for (int i=0; i<lstChkbox.size(); i++){
                lstChkbox.get(i).setEnabled(false);
            }
        }
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