package com.example.photomanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    EditText emailTextbox, passTextbox;
    Button btLogin, btRegister;
    String email, password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);

        firebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 인증을 위한 객체 가져오기
        emailTextbox = (EditText) findViewById(R.id.email_textbox);
        passTextbox = (EditText) findViewById(R.id.pass_textbox);
        btLogin = (Button) findViewById(R.id.bt_login);
        btRegister = (Button) findViewById(R.id.bt_register);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runLogin();
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goRegister();
            }
        });

    }

    private void runLogin() {
        email = emailTextbox.getText().toString();
        password = passTextbox.getText().toString();
        // 파이어베이스를 이용한 로그인(인증)
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    goMain();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    //   회원가입
    public void goRegister() {
        startActivity(new Intent(getApplicationContext(), UserRegister.class));
    }

    public void goMain() {
        Intent intent= new Intent(getApplicationContext(), MyAlbum.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }
}