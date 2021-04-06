package com.example.photomanagementapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserHash extends Activity {

    EditText txtText;
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    FirebaseStorage firebaseStorage;
    String email;
    String time;
    String PhotoUri;
    double latitude, longitude;
    String hash;
    Intent it;

    FirebaseDatabase firebasedb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_hashtag);

        //UI 객체생성
        txtText = (EditText) findViewById(R.id.editText);

        firebasedb = FirebaseDatabase.getInstance();

        //데이터 가져오기
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        time = intent.getStringExtra("date");
        PhotoUri = intent.getStringExtra("photoUri");
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);


    }

    //확인 버튼 클릭
    public void mOnClose(View v) {
        //데이터 전달하기
        hash = txtText.getText().toString();

        //photoUpload();
        dataUpload(Uri.parse("file://"+PhotoUri));

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

    // 사진 storage에 저장
    public void photoUpload() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        Uri file = Uri.fromFile(new File(PhotoUri));
        StorageReference riversRef = mStorageRef.child(email + "/" + hash);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "사진 업로드 성공.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "사진 업로드 실패.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //올린 사진 url 추출 리얼타임데이터에 데이터저장
    public void dataUpload(Uri uri) {

        String url = email + "/" + hash;
        final StorageReference fileRef = mStorageRef.child(url);
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        DatabaseReference root = firebasedb.getReference(); // 루트 노드 가져오기
                        DatabaseReference node = root.child("User/" + email);
                        DatabaseReference childnode = node.push();
                        childnode.child("email").setValue(email+"@daum.net");
                        childnode.child("time").setValue(time);
                        childnode.child("photoUrl").setValue(imageUrl);
                        childnode.child("latitude").setValue(latitude);
                        childnode.child("longitude").setValue(longitude);
                        childnode.child("hash").setValue(hash);
                        Toast.makeText(getApplicationContext(), "데이터 저장 성공", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        /*
        final StorageReference ref = mStorageRef.child(url);
        UploadTask uploadTask = ref.putFile(Uri.parse(PhotoUri));

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    String imageUrl = downloadUri.toString();
                    DatabaseReference root = firebasedb.getReference(); // 루트 노드 가져오기
                    DatabaseReference node = root.child("User/" + email);
                    DatabaseReference childnode = node.push();
                    childnode.child("email").setValue(email+"@daum.net");
                    childnode.child("time").setValue(time);
                    childnode.child("photoUrl").setValue(imageUrl);
                    childnode.child("latitude").setValue(latitude);
                    childnode.child("longitude").setValue(longitude);
                    childnode.child("hash").setValue(hash);
                    Toast.makeText(getApplicationContext(), "데이터 저장 성공", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "데이터 저장 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
        */


    }
}





