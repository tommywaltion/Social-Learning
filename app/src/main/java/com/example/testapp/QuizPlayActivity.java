package com.example.testapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class QuizPlayActivity extends AppCompatActivity {

    private int mCurrentPosition = 1;
    private ArrayList<QuestionsModel> mQuestionList;
    private QuizPlayerDataModel playerData;
    private int mSelectedOptionPosition = 0;
    private int mCorrectAnswers = 0;
    private int correctAnswers;
    private boolean answered = false;

    private TextView answer1;
    private TextView answer2;
    private TextView answer3;
    private TextView answer4;
    private TextView questionText;
    private TextView questionNumber;
    private TextView submit;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.quiz_slideup,R.anim.quiz_slideup);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_play);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        questionText = findViewById(R.id.quiz_play_questions);
        questionNumber = findViewById(R.id.quiz_play_question_number);
        submit = findViewById(R.id.quiz_play_submit);
        TextView cancel = findViewById(R.id.quiz_play_cancel);

        answer1 = findViewById(R.id.quiz_play_answer_1);
        answer2 = findViewById(R.id.quiz_play_answer_2);
        answer3 = findViewById(R.id.quiz_play_answer_3);
        answer4 = findViewById(R.id.quiz_play_answer_4);

        Bundle args = getIntent().getBundleExtra("data");
        mQuestionList = (ArrayList<QuestionsModel>) args.getSerializable("questionData");
        playerData = getIntent().getParcelableExtra("playerData");
        final String quizId = getIntent().getStringExtra("QuizId");

        correctAnswers = setQuestion();

        answer1.setOnClickListener(v -> selectedOptionView(answer1, 1));
        answer2.setOnClickListener(v -> selectedOptionView(answer2, 2));
        answer3.setOnClickListener(v -> selectedOptionView(answer3, 3));
        answer4.setOnClickListener(v -> selectedOptionView(answer4, 4));

        submit.setOnClickListener(v -> {
            if (answered) {
                mCurrentPosition++;
                if (mCurrentPosition <= mQuestionList.size()) {
                    if (mCurrentPosition <= playerData.getAnswer().size()) {
                        correctAnswers = setQuestion();
                        mSelectedOptionPosition = (int) playerData.getAnswer().get(mCurrentPosition - 1);
                        if (correctAnswers != mSelectedOptionPosition) {
                            answerView(mSelectedOptionPosition, R.drawable.quiz_wrong_option_border_bg);
                        }

                        answerView(correctAnswers, R.drawable.quiz_correct_option_border_bg);

                        if (mCurrentPosition == mQuestionList.size()) {
                            submit.setText(R.string.Finish_Question);
                        } else {
                            submit.setText(R.string.Next_Question);
                        }
                    } else {
                        correctAnswers = setQuestion();
                        answered = false;
                    }
                } else {
                    //Quiz finished
                    Log.d("FinishedQUiz", "User has finished a quiz");
                    playerData.setScores(mCorrectAnswers);
                    Intent intent = new Intent(QuizPlayActivity.this,QuizResultActivity.class);
                    intent.putExtra("playerData",playerData);
                    intent.putExtra("QuizId",quizId);
                    overridePendingTransition(R.anim.quiz_slideup,R.anim.quiz_slideup);
                    finishAfterTransition();
                    startActivity(intent);
                }
            } else {
                if (correctAnswers != mSelectedOptionPosition) {
                    answerView(mSelectedOptionPosition, R.drawable.quiz_wrong_option_border_bg);
                } else {
                    mCorrectAnswers += mQuestionList.get(mCurrentPosition - 1).getQuestionScore();
                }

                answerView(correctAnswers, R.drawable.quiz_correct_option_border_bg);

                if (mCurrentPosition == mQuestionList.size()) {
                    submit.setText(R.string.Finish_Question);
                } else {
                    submit.setText(R.string.Next_Question);
                }
                playerData.addAnswer(mSelectedOptionPosition);
                mSelectedOptionPosition = 0;
                answered = true;
            }
        });

        cancel.setOnClickListener(v -> {
            if (mCurrentPosition - 1 >= 1) {
                answered = true;
                mCurrentPosition--;
                correctAnswers = setQuestion();
                mSelectedOptionPosition = (int) playerData.getAnswer().get(mCurrentPosition - 1);
                if (correctAnswers != mSelectedOptionPosition) {
                    answerView(mSelectedOptionPosition, R.drawable.quiz_wrong_option_border_bg);
                }

                answerView(correctAnswers, R.drawable.quiz_correct_option_border_bg);

                if (mCurrentPosition == mQuestionList.size()) {
                    submit.setText(R.string.Finish_Question);
                } else {
                    submit.setText(R.string.Next_Question);
                }
            } else {
                Log.e("QuizPlayActivity","Player trying to exit quiz");
            }
        });

    }

    private int setQuestion() {
        QuestionsModel question = mQuestionList.get(mCurrentPosition - 1);

        defaultOptionView();

        if (mCurrentPosition == mQuestionList.size()) {
            submit.setText(R.string.Final_Question);
        } else {
            submit.setText(R.string.Submit_Answer);
        }

        questionNumber.setText(String.valueOf(mCurrentPosition));
        questionText.setText(question.getQuestion());
        answer1.setText(question.getOptionOne());
        answer2.setText(question.getOptionTwo());
        answer3.setText(question.getOptionThree());
        answer4.setText(question.getOptionFour());

        return question.getCorrectAnswer();
    }

    private void selectedOptionView(TextView textView, int selectedOption) {
        if (mCurrentPosition > playerData.getAnswer().size()) {
            defaultOptionView();

            mSelectedOptionPosition = selectedOption;

            textView.setTextColor(Color.parseColor("#444444"));
            textView.setTypeface(null, Typeface.BOLD_ITALIC);
            textView.setBackground(ContextCompat.getDrawable(QuizPlayActivity.this,R.drawable.quiz_selected_option_border_bg));
        }
    }

    private void defaultOptionView() {
        ArrayList<TextView> options = new ArrayList<>();
        options.add(0,answer1);
        options.add(1,answer2);
        options.add(2,answer3);
        options.add(3,answer4);

        for (TextView option : options) {
            option.setTextColor(Color.parseColor("#000000"));
            option.setTypeface(Typeface.DEFAULT);
            option.setBackground(ContextCompat.getDrawable(QuizPlayActivity.this,R.drawable.quiz_default_option_border_bg));
        }
    }

    private void answerView(int answer, int drawableView) {
        if (answer == 1) {
            answer1.setBackground(ContextCompat.getDrawable(QuizPlayActivity.this, drawableView));
        } else if (answer == 2) {
            answer2.setBackground(ContextCompat.getDrawable(QuizPlayActivity.this, drawableView));
        } else if (answer == 3) {
            answer3.setBackground(ContextCompat.getDrawable(QuizPlayActivity.this, drawableView));
        } else if (answer == 4) {
            answer4.setBackground(ContextCompat.getDrawable(QuizPlayActivity.this, drawableView));
        }
    }

}