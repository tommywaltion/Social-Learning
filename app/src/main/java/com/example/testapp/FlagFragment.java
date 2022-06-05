package com.example.testapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class FlagFragment extends Fragment {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference quizDatabase = db.collection("quiz");
    private final String quizId = "Mt9l4ZTYVW4oBKD96VU1";
    private String nickname;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_flag,container, false);
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        TextView AnimTest = view.findViewById(R.id.testButton);
        AnimTest.setOnClickListener(v -> {
            dialogBuilder = new AlertDialog.Builder(view.getContext());
            final View passwordPermissionView = getLayoutInflater().inflate(R.layout.popup_number, null);

            TextView cancel, title;
            EditText userInput;
            Button submit;

            title = passwordPermissionView.findViewById(R.id.popup_number_title);
            userInput = passwordPermissionView.findViewById(R.id.popup_number_input);
            submit = passwordPermissionView.findViewById(R.id.popup_number_submit_btn);
            cancel = passwordPermissionView.findViewById(R.id.popup_number_cancel_btn);

            title.setText(R.string.Number_code_title);
            userInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

            dialogBuilder.setView(passwordPermissionView);
            dialog = dialogBuilder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            submit.setOnClickListener(v1 -> {
                if (userInput.getText().toString().matches("")) {
                    userInput.setText("");
                    userInput.setBackgroundResource(R.drawable.layout_bg_round_edge_red);
                } else {
                    dialog.dismiss();
                    nickname = userInput.getText().toString();
                }
            });

            cancel.setOnClickListener(v1 -> dialog.dismiss());
            quizDatabase.document(quizId).get().addOnSuccessListener(documentSnapshot -> {
                Map<String, Object> quizData = documentSnapshot.getData();
                if (quizData != null) {
                    Map<String,Object> players = (Map<String, Object>) quizData.get("participant");
                    if (currentUser != null) {
                        assert players != null;
                        if (players.get(currentUser.getUid()) == null) {
                            QuizPlayerDataModel newItem = new QuizPlayerDataModel(
                                    nickname,
                                    0,
                                    new ArrayList<>()
                            );
                            players.put(currentUser.getUid(),newItem);
                            quizDatabase.document(quizId).update("participant",players);
                        }
                        Intent intent = new Intent(view.getContext(), QuizPlayLoadingActivity.class);
                        intent.putExtra("QuizId",quizId);
                        startActivity(intent);
                    }
                }
            });
        });

        return view;
    }
}