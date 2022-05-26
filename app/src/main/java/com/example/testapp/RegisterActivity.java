package com.example.testapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String UserId;

    public void onStart() {

        super.onStart();

    }

    @Override
    public void onBackPressed() {
        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(login);
        finish();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        auth = FirebaseAuth.getInstance();

        EditText username,email,nis,password,confirm_password;
        Button submit;
        TextView alreadyUser;
        ProgressBar loading;

        username = findViewById(R.id.username_new);
        email = findViewById(R.id.email_new);
        nis = findViewById(R.id.NIS);
        password = findViewById(R.id.password_new);
        confirm_password = findViewById(R.id.password_confirm);
        submit = findViewById(R.id.sign_up_btn);
        loading = findViewById(R.id.register_progressbar);

        loading.setVisibility(View.GONE);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                submit.setEnabled(s.toString().trim().length() != 0);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                submit.setEnabled(s.toString().trim().length() != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
                submit.setEnabled(s.toString().trim().length() != 0);
            }
        });

        submit.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isAcceptingText()) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),0);
            }
            loading.setVisibility(View.VISIBLE);
            boolean empty = false;
            if (username.getText().toString().isEmpty()) {
                username.setError("Username is required.");
                empty = true;
            }if (email.getText().toString().isEmpty()) {
                email.setError("Email is required.");
                empty = true;
            }if (nis.getText().toString().isEmpty()) {
                nis.setError("NIS is required.");
                empty = true;
            }if (password.getText().toString().isEmpty()) {
                password.setError("password is required.");
                empty = true;
            }if (password.getText().toString().length()<6) {
                password.setError("password is required 6 minimum character.");
                empty = true;
            }if (confirm_password.getText().toString().isEmpty()) {
                confirm_password.setError("confirm your password.");
                empty = true;
            }if (!password.getText().toString().equals(confirm_password.getText().toString())) {
                confirm_password.setError("Password do not match.");
                empty = true;
            }

            if (empty) return;

            auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            UserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                            Map<String, Object> user = new HashMap<>();
                            user.put("username",username.getText().toString());
                            user.put("NIS",nis.getText().toString());
                            user.put("email",email.getText().toString());
                            db.collection("users").add(user).addOnSuccessListener(command -> Log.d("RegisterActivity" ,"onSuccess: user Profile is created for " + UserId));
                            startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(e -> {
                        Log.d("RegisterActivity" ,"onFailure: " + e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
            });
        });

        alreadyUser = findViewById(R.id.already_user);
        alreadyUser.setOnClickListener(v -> {
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
            finish();
        });

    }
}