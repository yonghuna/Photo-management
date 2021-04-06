package com.example.photomanagementapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;
import com.yanzhenjie.album.impl.OnItemClickListener;
import com.yanzhenjie.album.widget.divider.Api21ItemDivider;
import com.yanzhenjie.album.widget.divider.Divider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserTakepicture extends AppCompatActivity {
    TextView mTextView;
    private ImageView mImageView;
    Button btUpload, btPhoto;
    Intent it;

    boolean firstCall = true;    //추가
    Spinner menuSpinner;
    String[] menuTitle = {"앨범", "사진찍기", "사진검색", "지도"};
    String Photouri, email, getTime;
    double latitude;
    double longitude;
    private GpsTracker gpsTracker;


    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_takepicture);


        mTextView = (TextView)findViewById(R.id.tv_message);
        btUpload = (Button)findViewById(R.id.btn_upload);
        btPhoto = (Button)findViewById(R.id.btn_photo);
        mImageView = (ImageView)findViewById(R.id.image_view);
        menuSpinner = (Spinner) findViewById(R.id.menu_spinner);
        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        btPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location();
                goHash();
            }
        });



        if (checkLocationServicesStatus()) {
            checkRunTimePermission();
        } else {
            showDialogForLocationServiceSetting();
        }

        ArrayAdapter<String> adapter;  //추가
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, menuTitle);  //추가
        menuSpinner.setAdapter(adapter);  //추가
        menuSpinner.setSelection(1);
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
                    } else if (i == 3) {
                        goLogout();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });


    }

    // 사진 찍기
    private void takePicture() {
        Album.camera(this)
                .image()
                .onResult(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        Photouri = result;
                        mTextView.setText(result);
                        mImageView.setImageURI(Uri.parse(result));

                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        Toast.makeText(UserTakepicture.this, "사진 찍기 실패입니다", Toast.LENGTH_LONG).show();
                    }
                })
                .start();
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

    void goHash() {
        it = new Intent(this, UserHash.class);

        //시간
        getTime();

        // 값을 전달
        Intent intent1 = new Intent(getApplicationContext(), UserHash.class);
        intent1.putExtra("photoUri", Photouri);
        intent1.putExtra("latitude", latitude);
        intent1.putExtra("longitude", longitude);
        intent1.putExtra("email", email);
        intent1.putExtra("date",getTime);
        startActivity(intent1);


    }

    void goLogout() {
        it = new Intent(this, MainActivity.class);
        startActivity(it);
    }

    void Location(){
        gpsTracker = new GpsTracker(UserTakepicture.this);

        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
    }

    // 시간을 저장하는 메소드
    void getTime(){
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        getTime = simpleDate.format(mDate);

    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                //위치 값을 가져올 수 있음
                ;
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(UserTakepicture.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                } else {

                    Toast.makeText(UserTakepicture.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission() {


        // 1. 위치 퍼미션을 가지고 있는지 체크
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(UserTakepicture.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(UserTakepicture.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(UserTakepicture.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유
                Toast.makeText(UserTakepicture.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청
                ActivityCompat.requestPermissions(UserTakepicture.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(UserTakepicture.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    //GPS 활성화
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(UserTakepicture.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}







