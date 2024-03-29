package com.example.scheduli.ui.forgotPassowrd;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.scheduli.R;
import com.example.scheduli.utils.UsersUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String FORGOT_TAG = "Forgot password activity";

    private UsersUtils usersUtils;
    private Button sendPassword, cancelRecovery;
    private EditText userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        usersUtils = UsersUtils.getInstance();
        initView();

        sendPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverPasswordToEmail();
            }
        });

        cancelRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void recoverPasswordToEmail() {
        if (checkIfEmpty(userEmail))
            userEmail.setError("Email cannot be empty");
        if (!isEmailValid(userEmail.getText().toString()))
            userEmail.setError("Email format is invalid");

        if (!checkIfEmpty(userEmail) && isEmailValid(userEmail.getText().toString())) {
            usersUtils.getFireBaseAuth().sendPasswordResetEmail(userEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.i(FORGOT_TAG, "Sent recovery password to client email " + userEmail.getText().toString());
                        Toast.makeText(ForgotPasswordActivity.this, "password was sent to " + userEmail.getText(), Toast.LENGTH_LONG).show();
                    } else {
                        Log.i(FORGOT_TAG, "Sent recovery password to client email " + userEmail.getText().toString());
                        Toast.makeText(ForgotPasswordActivity.this, "Cannot send recovery mail", Toast.LENGTH_LONG).show();
                        MediaPlayer player = MediaPlayer.create(ForgotPasswordActivity.this, R.raw.computer_error);
                        player.start();
                        userEmail.setError("Email Address is not in the system");
                    }
                }
            });
        }

    }

    private void initView() {
        sendPassword = findViewById(R.id.btn_forgot_pass_send);
        cancelRecovery = findViewById(R.id.btn_forgot_pass_cancel);
        userEmail = findViewById(R.id.et_forgot_pass_email);
    }


    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean checkIfEmpty(EditText editText) {
        return editText.getText().toString().isEmpty();
    }
}
