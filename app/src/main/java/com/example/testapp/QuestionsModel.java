package com.example.testapp;

import android.os.Parcel;
import android.os.Parcelable;

public class QuestionsModel implements Parcelable {
    private int id, correctAnswer;
    private String question;
    private String image;
    private String optionOne;
    private String optionTwo;
    private String optionThree;
    private String optionFour;
    private int questionScore;

    protected QuestionsModel(int id, String question, String image, String optionOne, String optionTwo, String optionThree, String optionFour, int correctAnswer, int questionScore) {
        this.id = id;
        this.image = image;
        this.question = question;
        this.optionOne = optionOne;
        this.optionTwo = optionTwo;
        this.optionThree = optionThree;
        this.optionFour = optionFour;
        this.correctAnswer = correctAnswer;
        this.questionScore = questionScore;
    }

    protected QuestionsModel(Parcel in) {
        id = in.readInt();
        correctAnswer = in.readInt();
        question = in.readString();
        image = in.readString();
        optionOne = in.readString();
        optionTwo = in.readString();
        optionThree = in.readString();
        optionFour = in.readString();
        questionScore = in.readInt();
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

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionOne() {
        return this.optionOne;
    }

    public void setName(String option) {
        this.optionOne = option;
    }

    public String getOptionTwo() {
        return this.optionTwo;
    }

    public void setOptionTwo(String option) {
        this.optionTwo = option;
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

    public int getCorrectAnswer() {
        return this.correctAnswer;
    }

    public void setCorrectAnswer(int option) {
        this.correctAnswer = option;
    }

    public int getQuestionScore() {
        return this.questionScore;
    }

    public void setQuestionScore(int score) {
        this.questionScore = score;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(correctAnswer);
        dest.writeString(question);
        dest.writeString(image);
        dest.writeString(optionOne);
        dest.writeString(optionTwo);
        dest.writeString(optionThree);
        dest.writeString(optionFour);
        dest.writeInt(questionScore);
    }
}
