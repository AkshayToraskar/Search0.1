package com.ak.search.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ak.search.activity.AddQuestionActivity;
import com.ak.search.R;
import com.ak.search.realm_model.Questions;

import io.realm.RealmList;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.MyViewHolder> {

private RealmList<Questions> questionsList;
private Context context;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView tvName, tvQuestionType;

    public MyViewHolder(View view) {
        super(view);
        tvName = (TextView) view.findViewById(R.id.tvName);
tvQuestionType=(TextView)view.findViewById(R.id.tv_user_type);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(context, AddQuestionActivity.class);
                i.putExtra("questionId",questionsList.get(getPosition()).getId());
                context.startActivity(i);
            }
        });


    }
}


    public QuestionsAdapter(Context context, RealmList<Questions> questionsList) {
        this.questionsList = questionsList;
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Questions questions = questionsList.get(position);
        holder.tvName.setText(questions.getQuestion());


        String questionType="";

        if(questionsList.get(position).getCompulsary()){
            questionType=questionType+"* ";
        }

        String type[]=questions.getTypeQuestion().split(",");
        for(int i=0; i<type.length; i++){
            int t=Integer.parseInt(type[i]);
            switch (t){
                case 1:
                    questionType=questionType+"Text ";
                    break;
                case 2:
                    questionType=questionType+"Number ";
                    break;
                case 3:
                    questionType=questionType+"Date ";
                    break;
                case 4:
                    questionType=questionType+"Time ";
                    break;
                case 5:
                    questionType=questionType+"Image ";
                    break;
                case 6:
                    questionType=questionType+"Patient Name ";
                    break;
                case 7:
                    questionType=questionType+"Checkbox ";
                    break;
                case 8:
                    questionType=questionType+"Option ";
                    break;
                case 9:
                    questionType=questionType+"Conditional ";
                    break;

            }
        }

        holder.tvQuestionType.setText(questionType);


    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }
}