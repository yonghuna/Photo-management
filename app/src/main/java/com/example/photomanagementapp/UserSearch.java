package com.example.photomanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.appcompat.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserSearch extends AppCompatActivity {
    Intent it;
    boolean firstCall = true;    //추가
    Spinner menuSpinner;
    SearchView searchView;
    String[] menuTitle = {"앨범", "사진찍기", "사진검색", "지도", "로그아웃"};
    String searchString, email, email1, photoUrl, hash;

    private RecyclerView recyclerView;
    private DatabaseReference myRef;
    private ArrayList<User> userList;
    private SearchAdapter searchAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_serach);

        //intent
        goIntent();

        //스피너
        menuSpinner = (Spinner) findViewById(R.id.menu_spinner);  //추가
        searchView =  (SearchView) findViewById(R.id.search_view);

        ArrayAdapter<String> adapter;  //추가
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, menuTitle);  //추가
        menuSpinner.setAdapter(adapter);  //추가
        menuSpinner.setSelection(2);
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
                    } else if (i == 4){
                        goLogout();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView); // 아디 연결
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화


        //firebase
        myRef = FirebaseDatabase.getInstance().getReference();


        //ArrayList
        userList = new ArrayList<>();

        //clear
        ClearALL();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchString = s;
                Query query = myRef.child("User/" + email);

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        ClearALL();
                        for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                            User user = new User();
                            if (searchString.equals(snapshot.child("hash").getValue().toString())) {
                                user.setPhotoUri(snapshot.child("photoUrl").getValue().toString());
                                user.setHash(snapshot.child("hash").getValue().toString());
                                userList.add(user);
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "없습니다", Toast.LENGTH_LONG).show();
                            }
                        }

                        searchAdapter = new SearchAdapter(getApplicationContext(), userList);
                        recyclerView.setAdapter(searchAdapter);
                        searchAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "없습니다", Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // 입력란의 문자열이 바뀔 때 처리
                return false;
            }
        });

    }


    private void ClearALL() {
        if (userList != null) {
            userList.clear();
            if (searchAdapter != null) {
                searchAdapter.notifyDataSetChanged();
            }

        }


        userList = new ArrayList<>();
    }


    void goMap() {
        it = new Intent(this, UserMap.class);
        it.putExtra("email", email);
        startActivity(it);
    }

    void goSearch() {
        it = new Intent(this, UserSearch.class);
        it.putExtra("email", email);
        startActivity(it);
    }

    void goTakepicture() {
        it = new Intent(this, UserTakepicture.class);
        it.putExtra("email", email);
        startActivity(it);
    }

    void goAlbum() {
        it = new Intent(this, MyAlbum.class);
        it.putExtra("email", email);
        startActivity(it);
    }

    void goLogout() {
        it = new Intent(this, MainActivity.class);
        startActivity(it);
    }

    void goIntent(){
        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
    }
}
