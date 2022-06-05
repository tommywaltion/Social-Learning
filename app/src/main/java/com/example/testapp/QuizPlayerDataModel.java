package com.example.testapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class QuizPlayerDataModel implements Parcelable{
    private int scores;
    private String nickname;
    private final ArrayList answers;

    public QuizPlayerDataModel(String nickname, int scores, ArrayList<Integer> answers) {
        this.nickname = nickname;
        this.scores = scores;
        this.answers = answers;
    }

    protected QuizPlayerDataModel(Parcel in) {
        this.scores = in.readInt();
        this.nickname = in.readString();
        this.answers = in.readArrayList(null);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(scores);
        dest.writeString(nickname);
        dest.writeList(answers);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QuizPlayerDataModel> CREATOR = new Creator<QuizPlayerDataModel>() {
        @Override
        public QuizPlayerDataModel createFromParcel(Parcel in) {
            return new QuizPlayerDataModel(in);
        }

        @Override
        public QuizPlayerDataModel[] newArray(int size) {
            return new QuizPlayerDataModel[size];
        }
    };

    public int getScores() {
        return this.scores;
    }

    public void setScores(int scores) {
        this.scores = scores;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public ArrayList<Integer> getAnswer() {
        return this.answers;
    }

    public  void addAnswer(Integer answers) {
        this.answers.add(answers);
    }
}

