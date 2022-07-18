package com.example.testapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class ExamCreateActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference examDatabase = db.collection("examination");
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReference();
    private final StorageReference examStorageRef = storageRef.child("examination");
    private StorageReference currentExamFolder;

    private TextView examNameView, examCancel, examSubmit;
    private LinearLayout examQuestionList;
    private RelativeLayout examAddQuestion, loading;

    private final ArrayList<View> questionList = new ArrayList<>();
    private final ArrayList<Integer> correctAnswerList = new ArrayList<>();
    private final HashMap<Integer, Uri> imageQuestionList = new HashMap<>();

    private int currentLocImages;
    private String examName;

    private ActivityResultLauncher<Intent> getImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_create);

        examName = getIntent().getStringExtra("examName");

        setup();

        examNameView.setText(examName);
        currentExamFolder = examStorageRef.child(examName + String.valueOf(new Random().nextInt((9999 - 1) + 1) + 1));

        addQuestion();

        examAddQuestion.setOnClickListener(v -> addQuestion());

        examCancel.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ExamCreateActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Are you sure ?");
            builder.setMessage("Menekan iya akan menghapus semua yang telah anda lakukan");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                questionList.clear();
                examQuestionList.removeAllViews();
                finish();
            });
            builder.setNegativeButton("No", (dialog, which) -> {});

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        examSubmit.setOnClickListener(v -> {
            loading.setVisibility(View.VISIBLE);
            AlertDialog.Builder builder = new AlertDialog.Builder(ExamCreateActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Are you sure ?");
            builder.setMessage("Menekan iya akan meyimpan semua pertanyaan dan tidak bisa diubah");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                loading.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                ArrayList< Map< String, QuestionsModel>> questionData = new ArrayList<>();
                Log.i("Saving Data","Starting  saving up");
                uploadQuestion(questionList.size() - 1, questionData);
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                loading.setVisibility(View.GONE);
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void uploadQuestion(int Size, ArrayList< Map< String, QuestionsModel>> questionData) {
        loading.setVisibility(View.VISIBLE);
        boolean cancel = false;
        View question = questionList.get(Size);
        EditText questionText = question.findViewById(R.id.exam_create_question);
        if (correctAnswerList.get(Size) == 0) {
            questionText.setError("No Correct Answers");
            return;
        }
        LinearLayout answerSheet = question.findViewById(R.id.exam_create_answer_sheet_list);
        EditText answer1 = question.findViewById(R.id.exam_create_answer_1_container);
        EditText answer2 = question.findViewById(R.id.exam_create_answer_2_container);
        EditText answer3 = question.findViewById(R.id.exam_create_answer_3_container);
        EditText answer4 = question.findViewById(R.id.exam_create_answer_4_container);
        EditText answer5 = question.findViewById(R.id.exam_create_answer_5_container);
        if (answerSheet.getTag().equals("2")) {
            if (answer1.getText().toString().matches("")) {
                answer1.setError("Answer required");
                cancel = true;
            } if (answer2.getText().toString().matches("")) {
                answer2.setError("Answer required");
                cancel = true;
            }
        } else if (answerSheet.getTag().equals("3")) {
            if (answer1.getText().toString().matches("")) {
                answer1.setError("Answer required");
                cancel = true;
            } if (answer2.getText().toString().matches("")) {
                answer2.setError("Answer required");
                cancel = true;
            } if (answer3.getText().toString().matches("")) {
                answer3.setError("Answer required");
                cancel = true;
            }
        } else if (answerSheet.getTag().equals("4")) {
            if (answer1.getText().toString().matches("")) {
                answer1.setError("Answer required");
                cancel = true;
            } if (answer2.getText().toString().matches("")) {
                answer2.setError("Answer required");
                cancel = true;
            } if (answer3.getText().toString().matches("")) {
                answer3.setError("Answer required");
                cancel = true;
            } if (answer4.getText().toString().matches("")) {
                answer4.setError("Answer required");
                cancel = true;
            }
        } else if (answerSheet.getTag().equals("5")) {
            if (answer1.getText().toString().matches("")) {
                answer1.setError("Answer required");
                cancel = true;
            } if (answer2.getText().toString().matches("")) {
                answer2.setError("Answer required");
                cancel = true;
            } if (answer3.getText().toString().matches("")) {
                answer3.setError("Answer required");
                cancel = true;
            } if (answer4.getText().toString().matches("")) {
                answer4.setError("Answer required");
                cancel = true;
            } if (answer5.getText().toString().matches("")) {
                answer5.setError("Answer required");
                cancel = true;
            }
        }
        if (cancel) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            return;
        }
        EditText questionScores = question.findViewById(R.id.exam_create_question_score);
        Map<String, QuestionsModel> data = new HashMap<>();
        int scores;
        if (questionScores.getText().toString().matches("")) {
            scores = 20;
        } else {
            scores = Integer.parseInt(questionScores.getText().toString());
        }

        data.put("data", new QuestionsModel(
                scores,
                correctAnswerList.get(Size),
                questionText.getText().toString(),
                "",
                answer1.getText().toString(),
                answer2.getText().toString(),
                answer3.getText().toString(),
                answer4.getText().toString(),
                answer5.getText().toString()
        ));

        if (questionList.size() != 0) {
            Uri uris = imageQuestionList.get(Size); assert uris != null;
            StorageReference examImageRef = currentExamFolder.child(uris.getLastPathSegment());
            UploadTask uploadTask = examImageRef.putFile(uris);
            uploadTask.addOnFailureListener(e -> Toast.makeText(this, "No Connection", Toast.LENGTH_LONG).show());
            uploadTask.addOnSuccessListener(command -> examImageRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                QuestionsModel questionsModel = data.get("data"); assert questionsModel != null;
                questionsModel.setImage(String.valueOf(uri1));
                questionData.add(data);
                if (Size > 0) {
                    int pos = Size - 1;
                    uploadQuestion(pos, questionData);
                } else {
                    Map<String, Object> questions = new HashMap<>();
                    questions.put("questions", questionData);
                    examDatabase.add(questions).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Map<String, Map<String, Boolean>> settingsMap = new HashMap<>();
                            Map<String, Boolean> settings = new HashMap<>();
                            settings.put("revealAnswer", true);
                            settingsMap.put("settings",settings);
                            examDatabase.document(task.getResult().getId()).set(settingsMap, SetOptions.merge());
                            Map<String, String> EXAMNAME = new HashMap<>();
                            EXAMNAME.put("examName",examName);
                            examDatabase.document(task.getResult().getId()).set(EXAMNAME, SetOptions.merge());
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            finish();
                        }
                    });
                }
            }));
        }
    }

    private void  addQuestion() {
        View questionTemplate = getLayoutInflater().inflate(R.layout.exam_create_question_template, examQuestionList, false);
        TextView questionPos = questionTemplate.findViewById(R.id.exam_create_question_number);
        EditText examScores = questionTemplate.findViewById(R.id.exam_create_question_score);
        TextView closeButton = questionTemplate.findViewById(R.id.exam_create_question_delete);

        RelativeLayout answer1_pos = questionTemplate.findViewById(R.id.exam_create_answer_1);
        RelativeLayout answer2_pos = questionTemplate.findViewById(R.id.exam_create_answer_2);
        RelativeLayout answer3_pos = questionTemplate.findViewById(R.id.exam_create_answer_3);
        TextView answer3_delete = questionTemplate.findViewById(R.id.exam_create_answer_3_delete);
        RelativeLayout answer4_pos = questionTemplate.findViewById(R.id.exam_create_answer_4);
        TextView answer4_delete = questionTemplate.findViewById(R.id.exam_create_answer_4_delete);
        RelativeLayout answer5_pos = questionTemplate.findViewById(R.id.exam_create_answer_5);
        TextView answer5_delete = questionTemplate.findViewById(R.id.exam_create_answer_5_delete);
        RelativeLayout answer_add = questionTemplate.findViewById(R.id.exam_create_add_answers);
        LinearLayout answerSheet = questionTemplate.findViewById(R.id.exam_create_answer_sheet_list);
        ImageView image = questionTemplate.findViewById(R.id.exam_create_image);

        answer3_pos.setVisibility(View.GONE); answer4_pos.setVisibility(View.GONE); answer5_pos.setVisibility(View.GONE);

        examScores.setHint("20");
        questionPos.setText(String.valueOf(questionList.size() + 1));
        questionList.add(questionTemplate);
        examQuestionList.addView(questionTemplate,questionList.size() - 1);
        correctAnswerList.add(0);
        questionTemplate.setTag(questionList.size() - 1);

        image.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            currentLocImages = (int) questionTemplate.getTag();
            getImages.launch(intent);
        });

        closeButton.setOnClickListener(v -> {
            ((ViewGroup) questionTemplate.getParent()).removeView(questionTemplate);
            questionList.remove((int)questionTemplate.getTag());
            for (int i = 0; i < questionList.size(); i++) {
                final TextView questionNumber = (TextView) questionList.get(i).findViewById(R.id.exam_create_question_number);
                questionNumber.setText(String.valueOf(i + 1));
                questionList.get(i).setTag(i);
                imageQuestionList.put(i, imageQuestionList.get(i + 1));
                imageQuestionList.remove(i + 1);
            }
        });

        answer_add.setOnClickListener(v -> {
            if (answerSheet.getTag().equals("2")) {
                answerSheet.setTag("3");
                answer3_pos.setVisibility(View.VISIBLE);
            } else if (answerSheet.getTag().equals("3")) {
                answerSheet.setTag("4");
                answer4_pos.setVisibility(View.VISIBLE);
            } else if (answerSheet.getTag().equals("4")) {
                answerSheet.setTag("5");
                answer5_pos.setVisibility(View.VISIBLE);
                answer_add.setVisibility(View.GONE);
            }
        });

        answer3_delete.setOnClickListener(v -> {
            EditText answer3 = questionTemplate.findViewById(R.id.exam_create_answer_3_container);
            EditText answer4 = questionTemplate.findViewById(R.id.exam_create_answer_4_container);
            EditText answer5 = questionTemplate.findViewById(R.id.exam_create_answer_5_container);
            if (answerSheet.getTag().equals("3")) {
                answer3_pos.setVisibility(View.GONE);
                answer3.setText("");
                answerSheet.setTag("2");
            } else if (answerSheet.getTag().equals("4")) {
                answer3.setText(answer4.getText());
                answer4_pos.setVisibility(View.GONE);
                answer4.setText("");
                answerSheet.setTag("3");
            } else if (answerSheet.getTag().equals("5")) {
                answer3.setText(answer4.getText());
                answer4.setText(answer5.getText());
                answer5_pos.setVisibility(View.GONE);
                answer5.setText("");
                answerSheet.setTag("4");
            }
            if (correctAnswerList.get((Integer) questionTemplate.getTag()) == 3) {
                correctAnswerList.set((Integer) questionTemplate.getTag(), 0);
                answer3_pos.setBackgroundResource(0);
            } else if (correctAnswerList.get((Integer) questionTemplate.getTag()) == 4) {
                correctAnswerList.set((Integer) questionTemplate.getTag(), 3);
                answer3_pos.setBackground(ContextCompat.getDrawable(ExamCreateActivity.this,R.drawable.layout_bg_round_edge_green));
                answer4_pos.setBackgroundResource(0);
            } else if (correctAnswerList.get((Integer) questionTemplate.getTag()) == 5) {
                correctAnswerList.set((Integer) questionTemplate.getTag(), 4);
                answer4_pos.setBackground(ContextCompat.getDrawable(ExamCreateActivity.this,R.drawable.layout_bg_round_edge_green));
                answer5_pos.setBackgroundResource(0);
            }
            answer_add.setVisibility(View.VISIBLE);
        });

        answer4_delete.setOnClickListener(v -> {
            EditText answer4 = questionTemplate.findViewById(R.id.exam_create_answer_4_container);
            EditText answer5 = questionTemplate.findViewById(R.id.exam_create_answer_5_container);
            if (answerSheet.getTag().equals("4")) {
                answer4_pos.setVisibility(View.GONE);
                answer4.setText("");
                answerSheet.setTag("3");
            } else if (answerSheet.getTag().equals("5")) {
                answer4.setText(answer5.getText());
                answer5_pos.setVisibility(View.GONE);
                answer5.setText("");
                answerSheet.setTag("4");
            }
            if (correctAnswerList.get((Integer) questionTemplate.getTag()) == 4) {
                correctAnswerList.set((Integer) questionTemplate.getTag(), 0);
                answer4_pos.setBackgroundResource(0);
            } else if (correctAnswerList.get((Integer) questionTemplate.getTag()) == 5) {
                correctAnswerList.set((Integer) questionTemplate.getTag(), 4);
                answer4_pos.setBackground(ContextCompat.getDrawable(ExamCreateActivity.this,R.drawable.layout_bg_round_edge_green));
                answer5_pos.setBackgroundResource(0);
            }
            answer_add.setVisibility(View.VISIBLE);
        });

        answer5_delete.setOnClickListener(v -> {
            EditText answer5 = questionTemplate.findViewById(R.id.exam_create_answer_5_container);
            if (answerSheet.getTag().equals("5")) {
                answer5_pos.setVisibility(View.GONE);
                answer5.setText("");
                answerSheet.setTag("4");
            }
            if (correctAnswerList.get((Integer) questionTemplate.getTag()) == 5) {
                correctAnswerList.set((Integer) questionTemplate.getTag(), 0);
                answer5_pos.setBackgroundResource(0);
            }
            answer_add.setVisibility(View.VISIBLE);
        });

        answer1_pos.setOnClickListener(v -> {
            correctAnswerList.set((Integer) questionTemplate.getTag(), 1);
            answer1_pos.setBackground(ContextCompat.getDrawable(ExamCreateActivity.this,R.drawable.layout_bg_round_edge_green));
            answer2_pos.setBackgroundResource(0);
            answer3_pos.setBackgroundResource(0);
            answer4_pos.setBackgroundResource(0);
            answer5_pos.setBackgroundResource(0);
        });

        answer2_pos.setOnClickListener(v -> {
            correctAnswerList.set((Integer) questionTemplate.getTag(), 2);
            answer1_pos.setBackgroundResource(0);
            answer2_pos.setBackground(ContextCompat.getDrawable(ExamCreateActivity.this,R.drawable.layout_bg_round_edge_green));
            answer3_pos.setBackgroundResource(0);
            answer4_pos.setBackgroundResource(0);
            answer5_pos.setBackgroundResource(0);
        });

        answer3_pos.setOnClickListener(v -> {
            correctAnswerList.set((Integer) questionTemplate.getTag(), 3);
            answer1_pos.setBackgroundResource(0);
            answer2_pos.setBackgroundResource(0);
            answer3_pos.setBackground(ContextCompat.getDrawable(ExamCreateActivity.this,R.drawable.layout_bg_round_edge_green));
            answer4_pos.setBackgroundResource(0);
            answer5_pos.setBackgroundResource(0);
        });

        answer4_pos.setOnClickListener(v -> {
            correctAnswerList.set((Integer) questionTemplate.getTag(), 4);
            answer1_pos.setBackgroundResource(0);
            answer2_pos.setBackgroundResource(0);
            answer3_pos.setBackgroundResource(0);
            answer4_pos.setBackground(ContextCompat.getDrawable(ExamCreateActivity.this,R.drawable.layout_bg_round_edge_green));
            answer5_pos.setBackgroundResource(0);
        });

        answer5_pos.setOnClickListener(v -> {
            correctAnswerList.set((Integer) questionTemplate.getTag(), 5);
            answer1_pos.setBackgroundResource(0);
            answer2_pos.setBackgroundResource(0);
            answer3_pos.setBackgroundResource(0);
            answer4_pos.setBackgroundResource(0);
            answer5_pos.setBackground(ContextCompat.getDrawable(ExamCreateActivity.this,R.drawable.layout_bg_round_edge_green));
        });
    }

    private void setup() {
        examNameView = findViewById(R.id.exam_create_examName);
        examQuestionList = findViewById(R.id.exam_create_question_list);
        examAddQuestion = findViewById(R.id.exam_create_more_questions);
        examCancel = findViewById(R.id.exam_create_cancel);
        examSubmit = findViewById(R.id.exam_create_submit);
        loading = findViewById(R.id.exam_create_uploading_progress);

        getImages = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        if (currentLocImages <= questionList.size()) {
                            Uri selectedImage = data.getData();
                            ImageView view = questionList.get(currentLocImages).findViewById(R.id.exam_create_image);
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),selectedImage);
                                view.setImageBitmap(bitmap);
                                imageQuestionList.put(currentLocImages, selectedImage);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
    }
}