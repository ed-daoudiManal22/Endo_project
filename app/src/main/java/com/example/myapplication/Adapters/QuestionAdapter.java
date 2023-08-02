package com.example.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Questions;
import com.example.myapplication.R;

import java.util.List;
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionVH> {
    List<Questions> QuestionsList;

    public QuestionAdapter(List<Questions> questionsList) {
        QuestionsList = questionsList;
    }

    @NonNull
    @Override
    public QuestionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_question,parent,false);
        return new QuestionVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionVH holder, int position) {
        Questions question = QuestionsList.get(position);
        Context context = holder.itemView.getContext();
        //holder.titleTxt.setText(question.getTitle());
        //holder.answerTxt.setText(question.getAnswer());
        holder.titleTxt.setText(getResourceString(context,question.getTitle()));
        holder.answerTxt.setText(getResourceString(context,question.getAnswer()));


        boolean isExpandable = QuestionsList.get(position).isExpandable();
        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
    }
    private String getResourceString(Context context,String resourceName) {
        int resId = context.getResources().getIdentifier(resourceName, "string", context.getPackageName());
        return context.getString(resId);
    }

    @Override
    public int getItemCount() {
        return QuestionsList.size();
    }

    public class QuestionVH extends RecyclerView.ViewHolder {
        TextView titleTxt, answerTxt;
        LinearLayout linearLayout;
        RelativeLayout expandableLayout;

        public QuestionVH(@NonNull View itemView) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.questionTitle);
            answerTxt = itemView.findViewById(R.id.questionAnswer);
            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandableLayout = itemView.findViewById(R.id.expanded_layout);

            linearLayout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Questions questions = QuestionsList.get(getAdapterPosition());
                    questions.setExpandable(!questions.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}