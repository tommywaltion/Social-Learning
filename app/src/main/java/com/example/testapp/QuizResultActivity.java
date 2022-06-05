package com.example.testapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class QuizResultActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference quizDatabase = db.collection("quiz");
    private FirebaseUser currentUser;
    private QuizPlayerDataModel playerData;
    private String playerName;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        playerData = (QuizPlayerDataModel) getIntent().getParcelableExtra("playerData");
        playerName = playerData.getNickname();
        final String quizId = getIntent().getStringExtra("QuizId");

        final Animation playerNamespaceIn = AnimationUtils.loadAnimation(this,R.anim.quiz_player_name_moveup);
        final Animation playerScorelineIn = AnimationUtils.loadAnimation(this,R.anim.quiz_player_score_moveup);
        final Handler handler = new Handler(Looper.getMainLooper());

        final TextView playersPlate = findViewById(R.id.quiz_result_player_name);
        final TextView scorePlate = findViewById(R.id.quiz_result_score);
        final RelativeLayout quizResultScreen = findViewById(R.id.Quiz_result_display);
        final TextView clickAnywhereText = findViewById(R.id.quiz_result_touch_text);

        quizDatabase.document(quizId).get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> quizData = documentSnapshot.getData();
            if (quizData != null) {
                Map<String,Object> players = (Map<String, Object>) quizData.get("participant");
                if (currentUser != null && players != null) {
                    players.put(currentUser.getUid(),playerData);
                    quizDatabase.document(quizId).update("participant",players);
                    handler.postDelayed(() -> {
                        clickAnywhereText.setVisibility(View.VISIBLE);
                        quizResultScreen.setOnClickListener(v -> finish());
                        },500);
                }
            }
        });

        playersPlate.startAnimation(playerNamespaceIn);

        handler.postDelayed(() -> {
            final int screenWidth = quizResultScreen.getWidth();

            ValueAnimator widthAnimator = ValueAnimator.ofInt(playersPlate.getWidth(), screenWidth);

            widthAnimator.setDuration(500);

            widthAnimator.addUpdateListener(animation -> {
                playersPlate.getLayoutParams().width = (int) animation.getAnimatedValue();
                playersPlate.requestLayout();
            });

            widthAnimator.start();
            handler.postDelayed(() -> playersPlate.setText(playerName), 500);
            scorePlate.startAnimation(playerScorelineIn);
        }, 500);

        handler.postDelayed(() -> {
            final int screenWidth = quizResultScreen.getWidth();

            ValueAnimator widthAnimator = ValueAnimator.ofInt(scorePlate.getWidth(), screenWidth);

            widthAnimator.setDuration(500);

            widthAnimator.addUpdateListener(animation -> {
                scorePlate.getLayoutParams().width = (int) animation.getAnimatedValue();
                scorePlate.requestLayout();
            });

            widthAnimator.start();
            handler.postDelayed(() -> {
                final int finalScore = playerData.getScores();
                ValueAnimator pointsUp = ValueAnimator.ofInt(0,finalScore);
                pointsUp.addUpdateListener(animation -> scorePlate.setText(String.format("%s pts", animation.getAnimatedValue().toString())));
                pointsUp.setDuration(500);
                pointsUp.start();
            }, 500);
        }, 750);


    }
}