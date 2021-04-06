package com.example.photomanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class UserMap extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private DatabaseReference myRef;
    Intent it;
    boolean firstCall = true;    //추가
    Spinner menuSpinner;
    String email, hash;
    String[] menuTitle = {"앨범", "사진찍기", "사진검색", "지도", "로그아웃"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_map);

        //Intent
        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        //firebase
        myRef = FirebaseDatabase.getInstance().getReference();
        menuSpinner = (Spinner) findViewById(R.id.menu_spinner);

        //map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //스피너
        menuSpinner = (Spinner) findViewById(R.id.menu_spinner);  //추가

        ArrayAdapter<String> adapter;  //추가
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, menuTitle);  //추가
        menuSpinner.setAdapter(adapter);  //추가
        menuSpinner.setSelection(3);
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

    public void onMapReady(GoogleMap googleMap) {
        //지도 관련 코드
        mMap = googleMap;
        Query query = myRef.child("User/" + email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    double latitude = (double) snapshot.child("latitude").getValue();
                    double longitude = (double) snapshot.child("longitude").getValue();
                    LatLng myUniveristyLocation = new LatLng(latitude, longitude);
                    MarkerOptions markerOptions1 = new MarkerOptions();
                    markerOptions1.position(myUniveristyLocation);
                    markerOptions1.title(hash);
                    mMap.addMarker(markerOptions1);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myUniveristyLocation, 100));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
