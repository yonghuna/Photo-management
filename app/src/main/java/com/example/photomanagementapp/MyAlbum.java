package com.example.photomanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;
import com.yanzhenjie.album.impl.OnItemClickListener;
import com.yanzhenjie.album.widget.divider.Api21ItemDivider;
import com.yanzhenjie.album.widget.divider.Divider;

import java.io.File;
import java.util.ArrayList;

public class MyAlbum extends AppCompatActivity {
    Intent it;
    boolean firstCall = true;
    Spinner menuSpinner;
    String email, email1, id;

    private RecyclerView recyclerView;
    private DatabaseReference myRef;
    private ArrayList<User> userList;
    private RecyclerAdapter recyclerAdapter;
    private Context mContext;

    int idx;
    String[] menuTitle = {"앨범", "사진찍기", "사진검색", "지도", "로그아웃"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_album);

        goIntent();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView); // 아디 연결
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화


        //firebase
        myRef = FirebaseDatabase.getInstance().getReference();

        //ArrayList
        userList = new ArrayList<>();
        GetDataFromFirebase();

        //clear
        ClearALL();

        //스피너
        menuSpinner = (Spinner) findViewById(R.id.menu_spinner);  //추가

        ArrayAdapter<String> adapter1;  //추가
        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, menuTitle);  //추가
        menuSpinner.setAdapter(adapter1);  //추가
        menuSpinner.setSelection(0);
        menuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {  //추가

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (firstCall) {
                    firstCall = false;
                } else {
                    if (i == 0) {
                        goAlbum();
                    } else if (i == 1) {
                        goTakepicture();
                    } else if (i == 2) {
                        goSearch();
                    } else if (i == 3) {
                        goMap();
                    } else if (i == 4) {
                        goLogout();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });


    }

    private void GetDataFromFirebase() {

        Query query = myRef.child("User/"+email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                ClearALL();
                for(DataSnapshot snapshot: datasnapshot.getChildren()){
                    User user = new User();
                    user.setPhotoUri(snapshot.child("photoUrl").getValue().toString());
                    userList.add(user);
                }

                recyclerAdapter = new RecyclerAdapter(getApplicationContext(), userList);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private  void ClearALL(){
        if(userList != null){
            userList.clear();
            if (recyclerAdapter != null){
                recyclerAdapter.notifyDataSetChanged();
            }

        }



        userList = new ArrayList<>();
    }

    void goMap() {
        it = new Intent(this, UserMap.class);
        it.putExtra("email", email);
        //
        startActivity(it);
    }

    void goSearch() {
        it = new Intent(this, UserSearch.class);
        it.putExtra("email", email);
        //it.putExtra("fullemail", fullEmail);
        startActivity(it);
    }

    void goTakepicture() {
        it = new Intent(getApplicationContext(), UserTakepicture.class);
        it.putExtra("email", email);
        //it.putExtra("fullemail", fullEmail);
        startActivity(it);
    }

    void goAlbum() {
        it = new Intent(this, MyAlbum.class);
        it.putExtra("email", email);
        //it.putExtra("fullemail", fullEmail);
        startActivity(it);
    }

    void goLogout() {
        it = new Intent(this, MainActivity.class);
        startActivity(it);
    }

    void goIntent(){
        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        if(email.contains("@")) {
            email1 = email;
            int idx = email1.indexOf("@");
            email = email1.substring(0, idx);
        }
    }


}
