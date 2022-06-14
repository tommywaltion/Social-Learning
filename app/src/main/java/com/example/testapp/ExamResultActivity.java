package com.example.testapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ExamResultActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference usersDatabase = db.collection("users");
    private final CollectionReference examScoreboardDatabase = db.collection("examinationScoreBoard");
    private final Handler handler = new Handler(Looper.getMainLooper());

    private FirebaseUser currentUser;
    private ArrayList<Integer> playerData;
    private final Map<String,String> usersData = new HashMap<>();
    private final Map<String,Integer> leaderboard = new HashMap<>();
    private final ArrayList<Integer> descending = new ArrayList<>();
    private final Map<Integer,String> reversed = new HashMap<>();
    private String examId;

    private TextView exam_result_title, exam_touch_anywhere;
    private LinearLayout exam_users_list;
    private RelativeLayout exam_result_screen;
    private ProgressBar loading;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_result);

        setup();
        startup();
        exam_users_list.animate().translationY(0).setStartDelay(500).setDuration(1000);

        FirebaseAuth auth =  FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        playerData = (ArrayList<Integer>) getIntent().getSerializableExtra("playerData");
        examId = getIntent().getStringExtra("examID");

        saveData();
    }
    private void showScoreboard2() {
        loading.setVisibility(View.GONE);
        for (int i = 0; i < leaderboard.size();i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            View scoreboardPlate = getLayoutInflater().inflate(R.layout.exam_result_scoreplate, exam_users_list, false);
            TextView namePlate = scoreboardPlate.findViewById(R.id.exam_result_score_namePlate);
            TextView scorePLate = scoreboardPlate.findViewById(R.id.exam_result_score_pointsPlate);
            namePlate.setText(usersData.get(reversed.get(descending.get(i))));
            scorePLate.setText(String.valueOf(descending.get(i)));
            if (Objects.equals(reversed.get(descending.get(i)), currentUser.getUid())) {
                params.setMargins(30,10,30,10);
                scoreboardPlate.setPadding(10,30,10,30);
            } else {
                params.setMargins(50,20,50,20);
            }
            scoreboardPlate.setLayoutParams(params);
            exam_users_list.addView(scoreboardPlate);
        }
        handler.postDelayed(() -> {
            exam_touch_anywhere.setVisibility(View.VISIBLE);
            exam_result_screen.setOnClickListener(v -> finish());
        },500);
    }

    private void startup() {
        exam_result_title.animate().translationY(0).setStartDelay(500).setDuration(500);
        loading.setVisibility(View.VISIBLE);
    }

    private void setup() {
        exam_result_title = findViewById(R.id.exam_result_title);
        exam_touch_anywhere = findViewById(R.id.exam_result_touch_text);
        exam_users_list = findViewById(R.id.exam_result_users_list);
        exam_result_screen = findViewById(R.id.exam_result_screen);
        loading = findViewById(R.id.exam_result_loading);
    }

    private void saveData() {
        Map<String,Object> saveData = new HashMap<>();
        saveData.put(currentUser.getUid(), playerData);
        examScoreboardDatabase.document(examId).set(saveData, SetOptions.merge());
        gettingData();
    }

    @SuppressWarnings("unchecked")
    private void gettingData2() {
        examScoreboardDatabase.document(examId).get().addOnSuccessListener(task -> {
            Map<String, Object> scoreboardData = task.getData();
            if (scoreboardData != null) {
                for (String data : scoreboardData.keySet()) {
                    if (usersData.get(data) != null) {
                        ArrayList<Integer> scores = (ArrayList<Integer>) scoreboardData.get(data);
                        if (scores != null) {
                            descending.add(Integer.parseInt(String.valueOf(scores.get(0))));
                            leaderboard.put(data, Integer.parseInt(String.valueOf(scores.get(0))));
                        }
                    }
                }
                sortData();
            }
        }).addOnFailureListener(e -> {
            loading.setVisibility(View.GONE);
            Log.e("LoginActivity",e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void sortData() {
        for (int i = 0; i < descending.size() - 1; i++) {
            for (int j = 0; j < descending.size() - i - 1; j++) {
                if (descending.get(j) < descending.get(j + 1)) {
                    int temp = descending.get(j);
                    descending.set(j, descending.get(j + 1));
                    descending.set(j + 1, temp);
                }
            }
        }
        for (String data : leaderboard.keySet()) {
            reversed.put(leaderboard.get(data), data);
        }
//        showScoreboard();
        showScoreboard2();
    }

    private void gettingData() {
        usersDatabase.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String username = doc.getString("username");
                    String userId = doc.getReference().getId();
                    usersData.put(userId,String.valueOf(username));
                }
                gettingData2();
            } else {
                loading.setVisibility(View.GONE);
                Exception e = task.getException();
                if (e != null) {
                    Log.e("LoginActivity",e.getMessage());
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}