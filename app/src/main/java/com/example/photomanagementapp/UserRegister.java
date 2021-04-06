package com.example.photomanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class UserRegister extends AppCompatActivity {
    FirebaseAuth firebaseauth;
    EditText emailTextbox, passTextBox;
    Button btUpload, btCancel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_register);

        firebaseauth = FirebaseAuth.getInstance();

        emailTextbox = (EditText) findViewById(R.id.email_textbox2);
        passTextBox = (EditText) findViewById(R.id.password_textbox2);
        btCancel = (Button) findViewById(R.id.bt_cancel);
        btUpload = (Button) findViewById(R.id.bt_upload);

        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runUpload();
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runCancel();
            }
        });
    }

    // 취소
    public void runCancel() {
        Toast.makeText(this, "cancel", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MainActivity.class));
    }
    // 회원가입 시도
    public void runUpload() {
        String email = emailTextbox.getText().toString();
        String password = passTextBox.getText().toString();

        if (email == null || email.equals("")) {
            Toast.makeText(this, "email check", Toast.LENGTH_LONG).show();
            return;
        }
        if (password == null || password.equals("") || password.length() < 6) {
            Toast.makeText(this, "over length 6", Toast.LENGTH_LONG).show();
            return;
        }
        firebaseauth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "등록 실패", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
