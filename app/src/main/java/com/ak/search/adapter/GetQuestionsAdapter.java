package com.ak.search.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ak.search.R;
import com.ak.search.model.Questions;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class GetQuestionsAdapter extends RecyclerView.Adapter<GetQuestionsAdapter.MyViewHolder> {

    private List<Questions> questionsList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvQuestion;
        public EditText etAnswer;
        public RadioGroup rgOption;
        public RadioButton rbOpt1, rbOpt2;

        public MyViewHolder(View view) {
            super(view);
            tvQuestion = (TextView) view.findViewById(R.id.tvQuestion);
            etAnswer = (EditText) view.findViewById(R.id.txt_answer);
            rgOption = (RadioGroup) view.findViewById(R.id.rg_options);
            rbOpt1 = (RadioButton) view.findViewById(R.id.rb_opt1);
            rbOpt2 = (RadioButton) view.findViewById(R.id.rb_opt2);


        }
    }

    public GetQuestionsAdapter(Context context, List<Questions> questionsList) {
        this.questionsList = questionsList;
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
        Questions questions = questionsList.get(position);
        holder.tvQuestion.setText(questions.getQuestion());
        //Log.v("asdff a", "" + questionsList.get(position).getId());
        List<RadioButton> allRb = new ArrayList<>();

        if (!questions.getOpt()) {
            holder.rgOption.setVisibility(GONE);
        } else {
            if (questionsList.get(position).getOptions().size() >= 2) {
                holder.rbOpt1.setText(questions.getOptions().get(0).getOpt());
                holder.rbOpt2.setText(questions.getOptions().get(1).getOpt());

                allRb.add(holder.rbOpt1);
                allRb.add(holder.rbOpt2);
            }

            if (questions.getOptions().size() > 2) {
                for (int i = 2; i < questionsList.get(position).getOptions().size(); i++) {
                    RadioButton rb = new RadioButton(context);
                    rb.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    rb.setText(questionsList.get(position).getOptions().get(i).getOpt());
                    holder.rgOption.addView(rb);
                    allRb.add(rb);
                }
                //allRb.get(questionsList.get(position).getAnswers().getSelectedOpt()).setChecked(true);
            }

            /*if (questions.getAnswers() != null) {
                allRb.get(questionsList.get(position).getAnswers().getSelectedopt()).setChecked(true);
                Log.v("sadf--------", "asdf " + questionsList.get(position).getAnswers().getSelectedopt());
            }*/

        }

        if (!questions.getText()) {
            holder.etAnswer.setVisibility(GONE);
        } else {
            /*if (questions.getAnswers() != null) {
                holder.etAnswer.setText(questions.getAnswers().getAns());
            }*/
        }

        holder.etAnswer.setEnabled(false);
        for (int i = 0; i < holder.rgOption.getChildCount(); i++) {
            holder.rgOption.getChildAt(i).setEnabled(false);
        }

    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }


    public void update(List<Questions> modelList) {
        questionsList.clear();
        questionsList.addAll(modelList);
        notifyDataSetChanged();
    }
}