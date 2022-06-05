package com.example.testapp;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class QuizPlayLoadingActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference quizDatabase = db.collection("quiz");
    private boolean dataFetch;
    private String playerName;
    private FirebaseUser currentUser;
    private QuizPlayerDataModel playerData;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.quiz_slideup,R.anim.quiz_slideup);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_play_loading);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        final String quizId = getIntent().getStringExtra("QuizId");

        final TextView readyConfirmPlate = findViewById(R.id.quiz_play_ready_confirm);
        final TextView readyCancelPlate = findViewById(R.id.quiz_play_ready_cancel);

        final TextView playerNamePlate = findViewById(R.id.quiz_play_player_name);
        final TextView readyTitlePlate = findViewById(R.id.quiz_play_ready_title);
        final RelativeLayout readyDisplay = findViewById(R.id.display_quiz_play);
        final ProgressBar loading = findViewById(R.id.quiz_play_progress);

        quizDatabase.document(quizId).get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> quizData = documentSnapshot.getData();
            if (quizData != null) {
                dataFetch = true;
                loading.setVisibility(View.GONE);

                ArrayList<QuestionsModel> questionData = new ArrayList<>();

                Map<String,Map<String,Objects>> players = (Map<String, Map<String, Objects>>) quizData.get("participant");
                ArrayList<Map<String,ArrayList<Objects>>> questions = (ArrayList<Map<String, ArrayList<Objects>>>) quizData.get("questions");
                playerData = (QuizPlayerDataModel) getIntent().getParcelableExtra("playerData");

                playerName = playerData.getNickname();

                if (questions != null) {
                    for (int i = 0; i < questions.size(); i++) {
                        ArrayList<Objects> data = questions.get(i).get("data");
                        assert data != null;
                        QuestionsModel questionsModel = new QuestionsModel(
                                Integer.parseInt(String.valueOf(data.get(0))),
                                String.valueOf(data.get(1)),
                                String.valueOf(data.get(2)),
                                String.valueOf(data.get(3)),
                                String.valueOf(data.get(4)),
                                String.valueOf(data.get(5)),
                                String.valueOf(data.get(6)),
                                Integer.parseInt(String.valueOf(data.get(7))),
                                Integer.parseInt(String.valueOf(data.get(8)))
                        );
                        questionData.add(questionsModel);
                    }
                }

                readyConfirmPlate.setOnClickListener(v -> {
                    Intent intent = new Intent(QuizPlayLoadingActivity.this, QuizPlayActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("questionData",(Serializable) questionData);
                    intent.putExtra("data",args);
                    intent.putExtra("playerData",playerData);
                    intent.putExtra("QuizId",quizId);
                    startActivity(intent);
                    overridePendingTransition(R.anim.quiz_slideup,R.anim.quiz_slideup);
                    finishAfterTransition();
                });

                readyCancelPlate.setOnClickListener(v -> {
                    overridePendingTransition(R.anim.quiz_slideup,R.anim.quiz_slideup);
                    finishAfterTransition();
                });

            }
        }).addOnFailureListener(e -> {
            Log.e("QuizPlayActivity", "Loading up quiz data, " + e.getMessage());
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        });

        final Animation swipeIn = AnimationUtils.loadAnimation(this,R.anim.quiz_player_name_moveup);

        playerNamePlate.startAnimation(swipeIn);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            final int screenWidth = readyDisplay.getWidth();

            ValueAnimator widthAnimator = ValueAnimator.ofInt(playerNamePlate.getWidth(), screenWidth);

            widthAnimator.setDuration(500);

            widthAnimator.addUpdateListener(animation -> {
                playerNamePlate.getLayoutParams().width = (int) animation.getAnimatedValue();
                playerNamePlate.requestLayout();
            });

            widthAnimator.start();
        }, 500);

        handler.postDelayed(() -> playerNamePlate.setText(playerName), 1000);
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
        }, 1100);

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