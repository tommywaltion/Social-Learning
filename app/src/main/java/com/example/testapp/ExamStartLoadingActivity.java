package com.example.testapp;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ExamStartLoadingActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference examData = db.collection("examination");

    private String userName;
    private boolean dataFetch;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.quiz_slideup,R.anim.quiz_slideup);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_start_loading);

        final String examId = getIntent().getStringExtra("examID");
        userName = getIntent().getStringExtra("userNickname");

        ArrayList<QuestionsModel> questionData = new ArrayList<>();
        final Handler handler = new Handler(Looper.getMainLooper());

        final Animation swipeIn = AnimationUtils.loadAnimation(this,R.anim.quiz_player_name_moveup);

        final RelativeLayout screenView = findViewById(R.id.display_exam_start);
        final ProgressBar loading = findViewById(R.id.exam_start_progress);
        final TextView namePlate = findViewById(R.id.exam_start_player_name);

        final TextView readyTitlePlate = findViewById(R.id.exam_start_ready_title);
        final TextView readyConfirmPlate = findViewById(R.id.exam_start_ready_confirm);
        final TextView readyCancelPlate = findViewById(R.id.exam_start_ready_cancel);

        examData.document(examId).get().addOnSuccessListener(documentSnapshot -> {
            HashMap<String,Object> examData = (HashMap<String, Object>) documentSnapshot.getData();
            if (examData != null) {
                dataFetch = true;
                loading.setVisibility(View.GONE);

                ArrayList<Map<String,QuestionsModel>> questions = (ArrayList<Map<String, QuestionsModel>>) examData.get("questions");
                HashMap<String,Boolean> settings = (HashMap<String, Boolean>) examData.get("settings");
                userName = getIntent().getStringExtra("userNickname");

                if (questions != null) {
                    for (int i = 0; i < questions.size(); i++) {
                        Map<String, Objects> data = (Map<String, Objects>) questions.get(i).get("data");
                        if (data != null) {
                            QuestionsModel questionsModel = new QuestionsModel(
                                    Integer.parseInt(String.valueOf(data.get("questionScore"))),
                                    Integer.parseInt(String.valueOf(data.get("correctAnswer"))),
                                    String.valueOf(data.get("question")),
                                    String.valueOf(data.get("image")),
                                    String.valueOf(data.get("optionOne")),
                                    String.valueOf(data.get("optionTwo"))
                            );
                            if (data.size() >= 7) {
                                questionsModel.setOptionThree(String.valueOf(data.get("optionThree")));
                            }if (data.size() >= 8) {
                                questionsModel.setOptionFour(String.valueOf(data.get("optionFour")));
                            } if (data.size() == 9) {
                                questionsModel.setOptionFive(String.valueOf(data.get("optionFive")));
                            }
                            questionData.add(questionsModel);
                        }
                    }

                    readyConfirmPlate.setOnClickListener(v -> {
                        Intent intent = new Intent(ExamStartLoadingActivity.this, ExamStartActivity.class);
                        Bundle args = new Bundle();
                        args.putSerializable("questionData", questionData);
                        args.putSerializable("settings",settings);
                        intent.putExtra("data", args);
                        intent.putExtra("examID", examId);
                        overridePendingTransition(R.anim.quiz_slideup, R.anim.quiz_slideup);
                        finishAfterTransition();
                        startActivity(intent);
                    });

                    readyCancelPlate.setOnClickListener(v -> {
                        overridePendingTransition(R.anim.quiz_slideup, R.anim.quiz_slideup);
                        finishAfterTransition();
                    });

                }

            }
        }).addOnFailureListener(e -> {
            Log.e("ExamStartActivity", "Loading up quiz data, " + e.getMessage());
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        });

        namePlate.startAnimation(swipeIn);

        handler.postDelayed(() -> {
            final int screenWidth = screenView.getWidth();

            ValueAnimator widthAnimator = ValueAnimator.ofInt(namePlate.getWidth(), screenWidth);

            widthAnimator.setDuration(500);

            widthAnimator.addUpdateListener(animation -> {
                namePlate.getLayoutParams().width = (int) animation.getAnimatedValue();
                namePlate.requestLayout();
            });

            widthAnimator.start();
        }, swipeIn.getDuration());

        handler.postDelayed(() -> namePlate.setText(userName), swipeIn.getDuration()*2);
//
        handler.postDelayed(() -> {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
            valueAnimator.setDuration(250);
            valueAnimator.addUpdateListener(animation -> {
                float alpha = (float) animation.getAnimatedValue();
                readyTitlePlate.setAlpha(alpha);
            });
            valueAnimator.start();
            if (!dataFetch) {
                loading.setVisibility(View.VISIBLE);
            }
        }, swipeIn.getDuration()*2);

        handler.postDelayed(() -> {
            readyCancelPlate.setVisibility(View.VISIBLE);
            readyConfirmPlate.setVisibility(View.VISIBLE);
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
            valueAnimator.setDuration(250);
            valueAnimator.addUpdateListener(animation -> {
                float alpha = (float) animation.getAnimatedValue();
                readyCancelPlate.setAlpha(alpha);
                readyConfirmPlate.setAlpha(alpha);
            });
            valueAnimator.start();
        }, 1100);

    }
}