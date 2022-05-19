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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        MaterialButton login_btn = findViewById(R.id.sign_in_btn);
        TextView email = findViewById(R.id.email);
        login_btn.setEnabled(email.getText().toString().trim().length() != 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        TextView email = findViewById(R.id.email);
        TextView password = findViewById(R.id.password);
        MaterialButton login_btn = findViewById(R.id.sign_in_btn);

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
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),0);
            if (TextUtils.isEmpty(email.getText().toString().trim())) {
                email.setError("Email is required.");
                Toast.makeText(LoginActivity.this, "PASSWORD MISSING", Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(password.getText().toString().trim())) {
                password.setError("Password is required.");
                Toast.makeText(LoginActivity.this, "PASSWORD MISSING", Toast.LENGTH_LONG).show();
            } else if (email.getText().toString().trim().equals("admin") && password.getText().toString().trim().equals("admin")) {

                dialogBuilder = new AlertDialog.Builder(this);
                final View passwordPermissionView = getLayoutInflater().inflate(R.layout.popup_number, null);

                TextView cancel,title;
                EditText userInput;
                Button submit;

                title = (TextView) passwordPermissionView.findViewById(R.id.popup_number_title);
                userInput = (EditText) passwordPermissionView.findViewById(R.id.popup_number_input);
                submit = (Button) passwordPermissionView.findViewById(R.id.popup_number_submit_btn);
                cancel = (TextView) passwordPermissionView.findViewById(R.id.popup_number_cancel_btn);

                title.setText(R.string.Number_code_title);
                userInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });

                dialogBuilder.setView(passwordPermissionView);
                dialog = dialogBuilder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                submit.setOnClickListener(v1 -> {
                    if (userInput.getText().toString().equals("1234")) {
                        Intent logged_in = new Intent(getApplicationContext(), DashboardActivity.class);
                        logged_in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(logged_in);
                        finish();
                    } else {
                        userInput.setText("");
                        userInput.setBackgroundResource(R.drawable.layout_bg_round_edge_red);
                    }
                });

                cancel.setOnClickListener(v1 -> dialog.dismiss());

            } else {
                auth.signInWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim())
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Intent logged_in = new Intent(getApplicationContext(),DashboardActivity.class);
                                logged_in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(logged_in);
                                finish();
                            }else {
                                Toast.makeText(LoginActivity.this,"LOGIN FAILED, " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        TextView forgotUser,createUser;

        forgotUser = findViewById(R.id.forgot_pass);
        createUser = findViewById(R.id.create_new);

        forgotUser.setOnClickListener(v -> {
            //User forgot password, change into forgot user layout
            Toast.makeText(LoginActivity.this,"USER FORGOT PASSWORD",Toast.LENGTH_LONG).show();
        });

        createUser.setOnClickListener(v -> {
            //Create new user, change into create user layout
            Toast.makeText(LoginActivity.this,"CREATING NEW USER",Toast.LENGTH_LONG).show();
            Intent logged_in = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(logged_in);
        });

    }
}