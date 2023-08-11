package com.example.myapplication.Models;

import java.util.List;

public class Quiz_question {
    private String qst, opt1, opt2, opt3, opt4,justif;
    private String userSelectedAnswer;
    private List<String> answers;

    public Quiz_question() {
    }

    public Quiz_question(String qst, String opt1, String opt2, String opt3, String opt4, List<String> answer,String justif) {
        this.qst = qst;
        this.opt1 = opt1;
        this.opt2 = opt2;
        this.opt3 = opt3;
        this.opt4 = opt4;
        this.answers = answer;
        this.justif = justif;
    }

    public String getQst() {
        return qst;
    }

    public void setQst(String qst) {
        this.qst = qst;
    }

    public String getOpt1() {
        return opt1;
    }

    public void setOpt1(String opt1) {
        this.opt1 = opt1;
    }

    public String getOpt2() {
        return opt2;
    }

    public void setOpt2(String opt2) {
        this.opt2 = opt2;
    }

    public String getOpt3() {
        return opt3;
    }

    public void setOpt3(String opt3) {
        this.opt3 = opt3;
    }

    public String getOpt4() {
        return opt4;
    }

    public void setOpt4(String opt4) {
        this.opt4 = opt4;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public String getJustif() {
        return justif;
    }

    public void setJustif(String justif) {
        this.justif = justif;
    }

    public String getUserSelectedAnswer() {
        return userSelectedAnswer;
    }

    public void setUserSelectedAnswer(String userSelectedAnswer) {
        this.userSelectedAnswer = userSelectedAnswer;
    }
}
