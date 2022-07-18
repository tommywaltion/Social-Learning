package com.example.testapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.Map;

public class ExamStartActivity extends AppCompatActivity {

    RequestManager glide;

    private ArrayList<QuestionsModel> mQuestionList;
    private Map<String,Boolean> settings;
    private int mCurrentPos = 1;
    private int mSelectedPos = 0;
    private boolean answered;
    private String examId;

    private ArrayList<Integer> playerData = new ArrayList<>();

    private TextView question_number, exam_timer, exam_questions, exam_prev, exam_next, exam_submit;
    private TextView exam_answer1, exam_answer2, exam_answer3, exam_answer4, exam_answer5;
    private TextView exam_selection_answer1, exam_selection_answer2, exam_selection_answer3, exam_selection_answer4, exam_selection_answer5;
    private ImageView show_question_list, hide_question_list, exam_image;
    private LinearLayout questionListLayout;
    private GridLayout exam_question_list_grid;

    @SuppressLint("SourceLockedOrientationActivity")
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.quiz_slideup,R.anim.quiz_slideup);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_start);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Bundle args = getIntent().getBundleExtra("data");
        mQuestionList = (ArrayList<QuestionsModel>) args.getSerializable("questionData");
        settings = (Map<String, Boolean>) args.getSerializable("settings");

        examId = getIntent().getStringExtra("examID");

        playerData.add(0);

        setupView();
        setQuestion();
        setupQuestionList();
        changeCurrentPos();

        exam_prev.setVisibility(View.GONE);
        exam_submit.setVisibility(View.GONE);

        if (mQuestionList.size() == 1) {
            exam_submit.setVisibility(View.VISIBLE);
        }

        exam_selection_answer1.setOnClickListener(v -> selectedOptionView(exam_selection_answer1, exam_answer1,1));
        exam_selection_answer2.setOnClickListener(v -> selectedOptionView(exam_selection_answer2, exam_answer2,2));
        exam_selection_answer3.setOnClickListener(v -> selectedOptionView(exam_selection_answer3, exam_answer3,3));
        exam_selection_answer4.setOnClickListener(v -> selectedOptionView(exam_selection_answer4, exam_answer4,4));
        exam_selection_answer5.setOnClickListener(v -> selectedOptionView(exam_selection_answer5, exam_answer5,5));
        exam_next.setOnClickListener(v -> nextQuestion(Boolean.TRUE.equals(settings.get("revealAnswer"))));
        exam_prev.setOnClickListener(v -> prevQuestion(Boolean.TRUE.equals(settings.get("revealAnswer"))));
        exam_submit.setOnClickListener(v -> submitAnswer());

        show_question_list.setOnClickListener(v -> {
            int endPos = 0;
            questionListLayout.animate().x(endPos).setDuration(500);
        });

        hide_question_list.setOnClickListener(v -> {
            int endPos = -questionListLayout.getWidth();
            questionListLayout.animate().x(endPos).setDuration(500);
        });

    }

    private void submitAnswer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ExamStartActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Are you sure ?");
        builder.setMessage("Menekan iya akan mengupload jawaban anda ke scoreboard");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(ExamStartActivity.this, ExamResultActivity.class);
            intent.putExtra("examID", examId);
            intent.putExtra("playerData",playerData);
            overridePendingTransition(R.anim.quiz_slideup,R.anim.quiz_slideup);
            finishAfterTransition();
            startActivity(intent);
        });
        builder.setNegativeButton("No", (dialog, which) -> {});

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void prevQuestion(boolean revealAnswer) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (revealAnswer) {
            if (mCurrentPos - 1 > 0) {
                answered = true;
                mCurrentPos--;
                setQuestion();

                if (playerData.get(mCurrentPos) == 0) {
                    mSelectedPos = 0;
                    changeCurrentPos();
                } else {
                    mSelectedPos = playerData.get(mCurrentPos);
                    QuestionsModel questionsModel = mQuestionList.get(mCurrentPos - 1);
                    if (mSelectedPos != questionsModel.getCorrectAnswer()) {
                        answerView(mSelectedPos, R.drawable.quiz_wrong_option_border_bg);
                    }

                    answerView(questionsModel.getCorrectAnswer(), R.drawable.quiz_correct_option_border_bg);
                }

                if (mCurrentPos == 1) {
                    exam_prev.setVisibility(View.GONE);
                }
                exam_next.setVisibility(View.VISIBLE);
            }
        } else {
            Log.d("ExamStartActivity","Settings: RevealAnswer = False");
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void nextQuestion(boolean revealAnswer){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (revealAnswer) {
            if (answered) {
                if (mCurrentPos + 1 <= mQuestionList.size()) {
                    mCurrentPos++;
                    if (playerData.get(mCurrentPos) == 0) {
                        changeCurrentPos();
                        mSelectedPos = 0;
                        answered = false;
                    } else {
                        changeCurrentPos();
                        QuestionsModel questionsModel = mQuestionList.get(mCurrentPos - 1);
                        mSelectedPos = playerData.get(mCurrentPos);
                        if (mSelectedPos != questionsModel.getCorrectAnswer()) {
                            answerView(mSelectedPos, R.drawable.exam_wrong_option_border);
                        }
                        answerView(questionsModel.getCorrectAnswer(), R.drawable.exam_correct_option_border);
                        if (mCurrentPos == mQuestionList.size()) {
                            exam_submit.setVisibility(View.VISIBLE);
                            exam_next.setVisibility(View.GONE);
                        }
                        exam_prev.setVisibility(View.VISIBLE);
                        mSelectedPos = 0;
                    }
                } else {
                    submitAnswer();
                }
            } else {
                if (mSelectedPos == 0) {
                    if (mCurrentPos + 1 <= mQuestionList.size()) {
                        mCurrentPos++;
                        changeCurrentPos();
                    }
                } else {
                    QuestionsModel questionsModel = mQuestionList.get(mCurrentPos - 1);
                    if (mSelectedPos != questionsModel.getCorrectAnswer()) {
                        answerView(mSelectedPos, R.drawable.exam_wrong_option_border);
                    } else {
                        playerData.set(0,playerData.get(0) + questionsModel.getQuestionScore());
                    }
                    answerView(questionsModel.getCorrectAnswer(), R.drawable.exam_correct_option_border);
                    exam_next.setVisibility(View.VISIBLE);
                    playerData.set(mCurrentPos,mSelectedPos);
                    mSelectedPos = 0;
                    answered = true;
                }
            }
        } else {
            Log.d("ExamStartActivity","Settings: RevealAnswer = False");
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @SuppressLint("SetTextI18n")
    private void setQuestion() {
        QuestionsModel question = mQuestionList.get(mCurrentPos - 1);

        defaultOptionView();

        String test = "No. ";
        question_number.setText(test + mCurrentPos);

        exam_questions.setText(question.getQuestion());

        if (question.getImage().isEmpty()) {
            exam_image.setVisibility(View.GONE);
        } else {
            exam_image.setVisibility(View.VISIBLE);
            glide.load(question.getImage()).into(exam_image);
        }

        exam_answer1.setText(String.format("A. %s", question.getOptionOne()));
        exam_answer2.setText(String.format("B. %s", question.getOptionTwo()));
        exam_answer3.setText(String.format("C. %s", question.getOptionThree()));
        exam_answer4.setText(String.format("D. %s", question.getOptionFour()));
        exam_answer5.setText(String.format("E. %s", question.getOptionFive()));
    }

    private void selectedOptionView(TextView view, TextView view2, int SelectedOption) {
        if (Boolean.TRUE.equals(settings.get("revealAnswer"))) {
            if (playerData.get(mCurrentPos) == 0) {
                defaultOptionView();

                mSelectedPos = SelectedOption;

                view.setTextColor(Color.parseColor("#444444"));
                view.setTypeface(null, Typeface.BOLD_ITALIC);
                view.setBackground(ContextCompat.getDrawable(ExamStartActivity.this,R.drawable.exam_selected_option_border));
                view2.setTextColor(Color.parseColor("#444444"));
                view2.setTypeface(null, Typeface.BOLD_ITALIC);
            }
        } else {
            defaultOptionView();

            mSelectedPos = SelectedOption;

            view.setTextColor(Color.parseColor("#444444"));
            view.setTypeface(null, Typeface.BOLD_ITALIC);
            view.setBackground(ContextCompat.getDrawable(ExamStartActivity.this,R.drawable.exam_selected_option_border));
            view2.setTextColor(Color.parseColor("#444444"));
            view2.setTypeface(null, Typeface.BOLD_ITALIC);
        }
    }

    private void defaultOptionView() {
        ArrayList<TextView> options = new ArrayList<>();
        ArrayList<TextView> options2 = new ArrayList<>();
        options2.add(exam_answer1);
        options2.add(exam_answer2);
        options2.add(exam_answer3);
        options2.add(exam_answer4);
        options2.add(exam_answer5);
        options.add(exam_selection_answer1);
        options.add(exam_selection_answer2);
        options.add(exam_selection_answer3);
        options.add(exam_selection_answer4);
        options.add(exam_selection_answer5);

        for (TextView option : options) {
            option.setTextColor(Color.parseColor("#000000"));
            option.setTypeface(Typeface.DEFAULT);
            option.setVisibility(View.GONE);
            option.setBackground(ContextCompat.getDrawable(ExamStartActivity.this,R.drawable.exam_default_option_border));
        }
        for (TextView option : options2) {
            option.setTextColor(Color.parseColor("#000000"));
            option.setTypeface(Typeface.DEFAULT);
            option.setVisibility(View.GONE);
            option.setBackgroundResource(0);
        }

        QuestionsModel question = mQuestionList.get(mCurrentPos - 1);

        exam_answer1.setVisibility(View.VISIBLE);
        exam_selection_answer1.setVisibility(View.VISIBLE);
        exam_answer2.setVisibility(View.VISIBLE);
        exam_selection_answer2.setVisibility(View.VISIBLE);
        if (!question.getOptionThree().isEmpty()) {
            exam_answer3.setVisibility(View.VISIBLE);
            exam_selection_answer3.setVisibility(View.VISIBLE);
        } if (!question.getOptionFour().isEmpty()) {
            exam_answer4.setVisibility(View.VISIBLE);
            exam_selection_answer4.setVisibility(View.VISIBLE);
        } if (!question.getOptionFive().isEmpty()) {
            exam_answer5.setVisibility(View.VISIBLE);
            exam_selection_answer5.setVisibility(View.VISIBLE);
        }
    }

    private void answerView(int answer, int drawableView){
        if (answer == 1) {
            exam_answer1.setBackground(ContextCompat.getDrawable(ExamStartActivity.this, drawableView));
            exam_selection_answer1.setBackground(ContextCompat.getDrawable(ExamStartActivity.this, drawableView));
        } else if (answer == 2) {
            exam_answer2.setBackground(ContextCompat.getDrawable(ExamStartActivity.this, drawableView));
            exam_selection_answer2.setBackground(ContextCompat.getDrawable(ExamStartActivity.this, drawableView));
        } else if (answer == 3) {
            exam_answer3.setBackground(ContextCompat.getDrawable(ExamStartActivity.this, drawableView));
            exam_selection_answer3.setBackground(ContextCompat.getDrawable(ExamStartActivity.this, drawableView));
        } else if (answer == 4) {
            exam_answer4.setBackground(ContextCompat.getDrawable(ExamStartActivity.this, drawableView));
            exam_selection_answer4.setBackground(ContextCompat.getDrawable(ExamStartActivity.this, drawableView));
        } else if (answer == 5) {
            exam_answer5.setBackground(ContextCompat.getDrawable(ExamStartActivity.this, drawableView));
            exam_selection_answer5.setBackground(ContextCompat.getDrawable(ExamStartActivity.this, drawableView));
        }
    }

    private void setupView() {
        question_number = findViewById(R.id.exam_start_position_number);
        exam_timer = findViewById(R.id.exam_start_timers);
        exam_prev = findViewById(R.id.exam_start_previous);
        exam_next = findViewById(R.id.exam_start_next);
        exam_submit = findViewById(R.id.exam_start_submit);
        exam_image = findViewById(R.id.exam_start_image);
        show_question_list = findViewById(R.id.exam_start_show_position);
        hide_question_list = findViewById(R.id.exam_start_hide_position);
        exam_question_list_grid = findViewById(R.id.exam_start_question_list_grid);
        questionListLayout = findViewById(R.id.exam_start_question_list);
        exam_questions = findViewById(R.id.exam_start_questions);
        exam_answer1 = findViewById(R.id.exam_start_answer_1);
        exam_answer2 = findViewById(R.id.exam_start_answer_2);
        exam_answer3 = findViewById(R.id.exam_start_answer_3);
        exam_answer4 = findViewById(R.id.exam_start_answer_4);
        exam_answer5 = findViewById(R.id.exam_start_answer_5);
        exam_selection_answer1 = findViewById(R.id.exam_start_selection_answer1);
        exam_selection_answer2 = findViewById(R.id.exam_start_selection_answer2);
        exam_selection_answer3 = findViewById(R.id.exam_start_selection_answer3);
        exam_selection_answer4 = findViewById(R.id.exam_start_selection_answer4);
        exam_selection_answer5 = findViewById(R.id.exam_start_selection_answer5);
        glide = Glide.with(getApplicationContext());
    }

    private void changeCurrentPos() {
        if (mCurrentPos > 1) {
            exam_prev.setVisibility(View.VISIBLE);
        } else {
            exam_prev.setVisibility(View.GONE);
        }
        if (mCurrentPos == mQuestionList.size()) {
            exam_submit.setVisibility(View.VISIBLE);
        } else {
            exam_submit.setVisibility(View.GONE);
        }
        if (Boolean.TRUE.equals(settings.get("revealAnswer"))) {
            if (playerData.get(mCurrentPos) == 0) {
                setQuestion();
                answered = false;
            } else {
                setQuestion();
                answered = true;
                QuestionsModel questionsModel = mQuestionList.get(mCurrentPos - 1);
                mSelectedPos = playerData.get(mCurrentPos);
                if (mSelectedPos != questionsModel.getCorrectAnswer()) {
                    answerView(mSelectedPos, R.drawable.exam_wrong_option_border);
                }
                answerView(questionsModel.getCorrectAnswer(), R.drawable.exam_correct_option_border);
            }
        } else {
            setQuestion();
            answered = true;
            mSelectedPos = playerData.get(mCurrentPos);
            answerView(mSelectedPos, R.drawable.exam_selected_option_border);
        }
    }

    private void setupQuestionList() {
        int row = 0;
        int column = 0;
        TextView numberText;
        for (int i = 0; i < mQuestionList.size(); i++) {
            playerData.add(0);
            numberText = new TextView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(row,.5f), GridLayout.spec( i - column, .5f));
            params.setMargins(10,10,10,10);
            numberText.setLayoutParams(params);
            numberText.setText(String.valueOf(i + 1));
            numberText.setTextSize(40);
            numberText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            numberText.setBackground(ContextCompat.getDrawable(ExamStartActivity.this,R.drawable.exam_number_background));
            exam_question_list_grid.addView(numberText, params);
            int finalI = i;
            numberText.setOnClickListener(v -> {
                mCurrentPos = 1 + finalI;
                changeCurrentPos();
            });
            if ((i + 1) % 5 == 0) {
                row++;
                column += 5;
            }
        }
    }
}