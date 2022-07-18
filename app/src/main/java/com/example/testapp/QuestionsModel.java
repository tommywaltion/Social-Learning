package com.example.testapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class QuestionsModel implements Parcelable {

    private int questionScore;
    private int correctAnswer;
    private String question;
    private String image;
    private String optionOne;
    private String optionTwo;
    private String optionThree;
    private String optionFour;
    private String optionFive;

    protected QuestionsModel(int questionScore, int correctAnswer, String question, String image, String optionOne, String optionTwo, String... data) {
        this.questionScore = questionScore;
        this.correctAnswer = correctAnswer;
        this.question = question;
        this.image = image;
        this.optionOne = optionOne;
        this.optionTwo = optionTwo;
        this.optionThree = data.length > 0 ? data[0] : "";
        this.optionFour = data.length > 1 ? data[1] : "";
        this.optionFive = data.length > 2 ? data[2] : "";
    }

    protected QuestionsModel(Parcel in) {
        questionScore = in.readInt();
        correctAnswer = in.readInt();
        question = in.readString();
        image = in.readString();
        optionOne = in.readString();
        optionTwo = in.readString();
        optionThree = in.readString();
        optionFour = in.readString();
        optionFive = in.readString();
    }

    public static final Creator<QuestionsModel> CREATOR = new Creator<QuestionsModel>() {
        @Override
        public QuestionsModel createFromParcel(Parcel in) {
            return new QuestionsModel(in);
        }

        @Override
        public QuestionsModel[] newArray(int size) {
            return new QuestionsModel[size];
        }
    };

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getQuestion() {
        return this.question;
    }

    public String getOptionOne() {
        return this.optionOne;
    }

    public String getOptionTwo() {
        return this.optionTwo;
    }

    public String getOptionThree() {
        return this.optionThree;
    }

    public void setOptionThree(String option) {
        this.optionThree = option;
    }

    public String getOptionFour() {
        return this.optionFour;
    }

    public void setOptionFour(String option) {
        this.optionFour = option;
    }

    public String getOptionFive() {
        return this.optionFive;
    }

    public void setOptionFive(String option) {
        this.optionFive = option;
    }

    public int getCorrectAnswer() {
        return this.correctAnswer;
    }

    public int getQuestionScore() {
        return this.questionScore;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(questionScore);
        dest.writeInt(correctAnswer);
        dest.writeString(question);
        dest.writeString(image);
        dest.writeString(optionOne);
        dest.writeString(optionTwo);
        dest.writeString(optionThree);
        dest.writeString(optionFour);
        dest.writeString(optionFive);
    }
}
