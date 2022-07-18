package com.example.testapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class FlagFragment extends Fragment {

    private FirebaseUser currentUser;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private final CollectionReference quizDatabase = db.collection("quiz");
//    private final String quizId = "Mt9l4ZTYVW4oBKD96VU1";
    private final String examId = "5nYFQvm0rPksTjCoyEN1";
//    private String nickname;

    private ImageView exam_create_button;

    @Override
    @SuppressLint({"SourceLockedOrientationActivity", "SetTextI18n"})
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_flag,container, false);
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FirebaseAuth auth =  FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        return view;
    }

    @Override
    @SuppressWarnings("unchecked")
    @SuppressLint({"SourceLockedOrientationActivity", "SetTextI18n"})
    public void onStart() {
        super.onStart();

        CollectionReference ExamList = db.collection("examination");
        View view = getView();
        assert view != null;
        LinearLayout itemList = view.findViewById(R.id.exam_list_item);

        if (isAdded()) {
            ExamList.get().addOnSuccessListener(command -> {
                ArrayList<DocumentSnapshot> data = (ArrayList<DocumentSnapshot>) command.getDocuments();
                for (DocumentSnapshot Qdata : data) {
                    Map<String, Object> datas = Qdata.getData();
                    if (datas != null) {
                        View exams = getLayoutInflater().inflate(R.layout.exam_list_item_template, itemList, false);
                        TextView examName = exams.findViewById(R.id.exam_list_template_title);
                        examName.setText((String) datas.get("examName"));
                        TextView examNumber = exams.findViewById(R.id.exam_list_template_quantity);
                        ArrayList<Object> questionData = (ArrayList<Object>) datas.get("questions");
                        assert questionData != null;
                        examNumber.setText(questionData.size() + " Soal");
                        TextView joinBTN = exams.findViewById(R.id.exam_list_template_join);
                        joinBTN.setOnClickListener(v -> {
                            Intent intent = new Intent(view.getContext(), ExamStartLoadingActivity.class);
                            intent.putExtra("examID",Qdata.getId());
                            startActivity(intent);
                        });
                        itemList.addView(exams);
                    }
                }
            });

            //exam listing


            View tests = getLayoutInflater().inflate(R.layout.exam_list_item_template, itemList, false);
            TextView joinBTN = tests.findViewById(R.id.exam_list_template_join);

            joinBTN.setOnClickListener(v -> {
                Intent intent = new Intent(view.getContext(), ExamStartLoadingActivity.class);
                intent.putExtra("examID",examId);
                intent.putExtra("userNickname","Test123");
                startActivity(intent);
            });

            itemList.addView(tests);

            //create exam list

            exam_create_button = view.findViewById(R.id.exam_create_button);

            exam_create_button.setOnClickListener(v -> {
                dialogBuilder = new AlertDialog.Builder(view.getContext());
                final View examNameView = getLayoutInflater().inflate(R.layout.popup_nickname, null);

                TextView cancel, title;
                EditText userInput;
                Button submit;

                title = examNameView.findViewById(R.id.popup_nickname_title);
                userInput = examNameView.findViewById(R.id.popup_nickname_input);
                submit = examNameView.findViewById(R.id.popup_nickname_submit_btn);
                cancel = examNameView.findViewById(R.id.popup_nickname_cancel_btn);

                title.setText(R.string.Exam_Name_Title);
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(25);
                userInput.setFilters(filterArray);
                userInput.setHint("EXAM NAME");

                dialogBuilder.setView(examNameView);
                dialog = dialogBuilder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                submit.setOnClickListener(v1 -> {
                    if (userInput.getText().toString().matches("")) {
                        userInput.setText("");
                        userInput.setError("Name Required");
                    } else {
                        dialog.dismiss();
                        Intent intent = new Intent(view.getContext(), ExamCreateActivity.class);
                        intent.putExtra("examName", userInput.getText().toString());
                        startActivity(intent);
                    }
                });

                cancel.setOnClickListener(v1 -> dialog.dismiss());
            });

            //Exam

//        TextView AnimTest = view.findViewById(R.id.exam_list_template_join);
//        AnimTest.setOnClickListener(v -> {
//            Intent intent = new Intent(view.getContext(), ExamStartLoadingActivity.class);
//            intent.putExtra("examID",examId);
//            intent.putExtra("userNickname","Test123");
//            startActivity(intent);
//        });


            //Quiz

//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = auth.getCurrentUser();
//
//        AnimTest.setOnClickListener(v -> quizDatabase.document(quizId).get().addOnSuccessListener(documentSnapshot -> {
//            Map<String, Object> quizData = documentSnapshot.getData();
//            if (quizData != null) {
//                Map<String,Object> players = (Map<String, Object>) quizData.get("participant");
//                if (currentUser != null) {
//                    assert players != null;
//                    if (players.get(currentUser.getUid()) == null) {
//                        dialogBuilder = new AlertDialog.Builder(view.getContext());
//                        final View nicknameView = getLayoutInflater().inflate(R.layout.popup_nickname, null);
//
//                        TextView cancel, title;
//                        EditText userInput;
//                        Button submit;
//
//                        title = nicknameView.findViewById(R.id.popup_nickname_title);
//                        userInput = nicknameView.findViewById(R.id.popup_nickname_input);
//                        submit = nicknameView.findViewById(R.id.popup_nickname_submit_btn);
//                        cancel = nicknameView.findViewById(R.id.popup_nickname_cancel_btn);
//
//                        title.setText(R.string.Quiz_Nickname_input);
//                        userInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
//
//                        dialogBuilder.setView(nicknameView);
//                        dialog = dialogBuilder.create();
//                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                        dialog.show();
//
//                        submit.setOnClickListener(v1 -> {
//                            if (userInput.getText().toString().matches("")) {
//                                userInput.setText("");
//                                userInput.setError("Nickname Required");
//                            } else {
//                                dialog.dismiss();
//                                nickname = userInput.getText().toString();
//                                QuizPlayerDataModel newItem = new QuizPlayerDataModel(
//                                        nickname,
//                                        0,
//                                        new ArrayList<>()
//                                );
//                                Intent intent = new Intent(view.getContext(), QuizPlayLoadingActivity.class);
//                                intent.putExtra("QuizId",quizId);
//                                intent.putExtra("playerData",newItem);
//                                startActivity(intent);
//                            }
//                        });
//
//                        cancel.setOnClickListener(v1 -> dialog.dismiss());
//                    } else {
//                        Toast.makeText(view.getContext(), "You already submit this quiz!", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            }
//        }));
        }
    }
}