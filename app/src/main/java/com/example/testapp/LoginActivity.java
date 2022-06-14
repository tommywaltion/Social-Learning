package com.example.testapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String adminPassword = "1234";

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        MaterialButton login_btn = findViewById(R.id.sign_in_btn);
        TextView email = findViewById(R.id.email);
        login_btn.setEnabled(email.getText().toString().trim().length() != 0);
        if (currentUser != null) {
//            startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
            Log.d("LoginActivity" ,"onSuccess: user Profile is created for " + currentUser.getUid());
//            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        TextView email = findViewById(R.id.email);
        TextView password = findViewById(R.id.password);
        MaterialButton login_btn = findViewById(R.id.sign_in_btn);
        ProgressBar loading = findViewById(R.id.login_progressbar);

        loading.setVisibility(View.GONE);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                login_btn.setEnabled(s.toString().trim().length() != 0);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                login_btn.setEnabled(s.toString().trim().length() != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
                login_btn.setEnabled(s.toString().trim().length() != 0);
            }
        });

        login_btn.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isAcceptingText()) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),0);
            }
            loading.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(email.getText().toString().trim())) {
                email.setError("Email is required.");
                loading.setVisibility(View.GONE);
            } else if (TextUtils.isEmpty(password.getText().toString().trim())) {
                password.setError("Password is required.");
                loading.setVisibility(View.GONE);
            } else if (email.getText().toString().trim().equals("admin") && password.getText().toString().trim().equals("admin")) {
                loading.setVisibility(View.GONE);
                dialogBuilder = new AlertDialog.Builder(this);
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
                    if (userInput.getText().toString().equals(adminPassword)) {
                        dialog.dismiss();
                        Intent logged_in = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(logged_in);
                        finish();
                    } else {
                        userInput.setText("");
                        userInput.setBackgroundResource(R.drawable.layout_bg_round_edge_red);
                    }
                });

                cancel.setOnClickListener(v1 -> dialog.dismiss());

            } else if (isEmailValid(email.getText().toString())) {
                auth.signInWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim())
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                                FirebaseUser currentUser = auth.getCurrentUser();
                                if (currentUser != null) Log.d("LoginActivity" ,"onSuccess: user Profile is logged for " + currentUser.getUid());
                                finish();
                            }
                        }).addOnFailureListener(e -> {
                            loading.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
                            Log.d("LoginActivity" ,"onFailure: " + e.getMessage());
                        });
            } else {
                db.collection("users").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            if (doc.exists()) {
                                String username = doc.getString("username");
                                if (username != null) {
                                    if (username.equals(email.getText().toString())) {
                                        String Email = doc.getString("email");
                                        if (Email != null) {
                                            auth.signInWithEmailAndPassword(Email,password.getText().toString().trim())
                                                    .addOnCompleteListener(this, task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            FirebaseUser currentUser = auth.getCurrentUser();
                                                            if (currentUser != null) {
                                                                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                                                                Log.d("LoginActivity", "onSuccess: user Profile is logged for " + currentUser.getUid());
                                                                finish();
                                                            }
                                                        }
                                                    }).addOnFailureListener(e -> {
                                                        loading.setVisibility(View.GONE);
                                                        Toast.makeText(LoginActivity.this,"LOGIN FAILED, " + e.getMessage(),Toast.LENGTH_LONG).show();
                                                        Log.d("LoginActivity" ,"onFailure: " + e.getMessage());
                                                    });
                                        }
                                        break;
                                    } else {
                                        loading.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
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
        });

        TextView forgotUser,createUser;

        forgotUser = findViewById(R.id.forgot_pass);
        createUser = findViewById(R.id.create_new);

        forgotUser.setOnClickListener(v -> {
            dialogBuilder = new AlertDialog.Builder(this);
            final View EmailRequestView = getLayoutInflater().inflate(R.layout.popup_email, null);

            TextView cancel, title;
            EditText userInput;
            Button submit;

            title = EmailRequestView.findViewById(R.id.popup_email_title);
            userInput = EmailRequestView.findViewById(R.id.popup_email_input);
            submit = EmailRequestView.findViewById(R.id.popup_email_submit_btn);
            cancel = EmailRequestView.findViewById(R.id.popup_email_cancel_btn);

            title.setText(R.string.Email_Popup_title);

            dialogBuilder.setView(EmailRequestView);
            dialog = dialogBuilder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            submit.setOnClickListener(v1 -> {
                if (isEmailValid(userInput.getText().toString())) {
                    auth.sendPasswordResetEmail(userInput.getText().toString())
                            .addOnSuccessListener(command -> {
                                Toast.makeText(LoginActivity.this,"Reset Password Email Sent.",Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            })
                            .addOnFailureListener(e -> Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show());
                } else if (userInput.getText().toString().isEmpty()){
                    userInput.setError("Required field.");
                } else {
                    userInput.setError("Required a valid email.");
                }
            });

            cancel.setOnClickListener(v1 -> dialog.dismiss());
        });

        createUser.setOnClickListener(v -> {
            Intent register = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(register);
            finish();
        });

    }

    private boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}