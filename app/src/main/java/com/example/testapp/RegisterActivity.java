package com.example.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    public void onStart() {

        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        EditText email,nis,password,confirm_password;
        Button submit;
        TextView alreadyUser;

        email = findViewById(R.id.email_new);
        nis = findViewById(R.id.NIS);
        password = findViewById(R.id.password_new);
        confirm_password = findViewById(R.id.password_confirm);
        submit = findViewById(R.id.sign_up_btn);

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
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

            if (TextUtils.isEmpty(email.getText().toString().trim())) {
                email.setError("Email is required.");
                return;
            }if (TextUtils.isEmpty(nis.getText().toString().trim())) {
                nis.setError("NIS is required.");
                return;
            }if (TextUtils.isEmpty(password.getText().toString().trim())) {
                password.setError("password is required.");
                return;
            }if (TextUtils.isEmpty(confirm_password.getText().toString().trim())) {
                confirm_password.setError("confirm your password.");
                return;
            }

            auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this,"ACCOUNT CREATED",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RegisterActivity.this,"ERROR" + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        });

        alreadyUser = findViewById(R.id.already_user);
        alreadyUser.setOnClickListener(v -> finish());

    }
}