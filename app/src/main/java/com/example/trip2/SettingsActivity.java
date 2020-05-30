package com.example.trip2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button updateAccountSettings;
    private EditText userName,userStatus;
    private CheckBox english, korean, restaurant, culture, show, art, sights, food, walk;
    private Spinner location;


    private String currentUserID;
    private FirebaseAuth mAuth;
    // cloudfirestore로 변환중
    private FirebaseFirestore db;

    private DatabaseReference rootRef;
    private ProgressDialog loadingBar;

    //이미지 관련 부분
    private static final String TAG = "ProfileFragment";
    int REQUEST_IMAGE_CODE=1001;
    int REQUEST_EXTERNAL_STORAGE_PERMISSION=1002;
    private CircleImageView ivUser;
    File localFile;
    private StorageReference mStorageRef;
    String stEmail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();



        updateAccountSettings = (Button) findViewById(R.id.update_settings_button);
        userName = (EditText) findViewById(R.id.set_user_name);
        userStatus = (EditText) findViewById(R.id.set_profile_status);
        //userKeyword = (EditText) findViewById(R.id.set_profile_profile_keyword);
        ivUser = (CircleImageView) findViewById(R.id.ivUser);
        loadingBar = new ProgressDialog(this);
        location=(Spinner)findViewById(R.id.spinner_city);

        english=(CheckBox)findViewById(R.id.english);
        korean=(CheckBox)findViewById(R.id.korean);

        restaurant=(CheckBox)findViewById(R.id.restaurant);
        culture =(CheckBox)findViewById(R.id.culture);
        show=(CheckBox)findViewById(R.id.show);
        art=(CheckBox)findViewById(R.id.art);
        sights=(CheckBox)findViewById(R.id.sights);
        food=(CheckBox)findViewById(R.id.food);
        walk=(CheckBox)findViewById(R.id.walk);




        //여기서부터 아래까지 로그인 엑티비티 shared 값과 연동 db 연결 되면 대체
        SharedPreferences sharedPref = getSharedPreferences("shared", Context.MODE_PRIVATE);
        stEmail=sharedPref.getString("email","");
        Log.d(TAG, "stEmail: "+stEmail);
        //
        //여기서 부터 이미지 코드
        mStorageRef = FirebaseStorage.getInstance().getReference();

        if(ContextCompat.checkSelfPermission(SettingsActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(SettingsActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){

            }else{
                ActivityCompat.requestPermissions(SettingsActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE_PERMISSION);
            }
        }else{

        }
        //

        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });
        //사진 관련 코드
        ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, REQUEST_IMAGE_CODE);
            }
        });
        try {
            localFile = File.createTempFile("images", "jpg");
            StorageReference riversRef = mStorageRef.child("users").child(stEmail).child("profile.jpg");
            riversRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            // ...
                            Bitmap bitmap= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            ivUser.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        RetrieveUserInfo();
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CODE){
            Uri image=data.getData();
            try {
                Bitmap bitmap=MediaStore.Images.Media.getBitmap(SettingsActivity.this.getContentResolver(),image);
                ivUser.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            StorageReference riversRef = mStorageRef.child("users").child(stEmail).child("profile.jpg");

            riversRef.putFile(image)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.d(TAG, taskSnapshot.toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //내가 전에 셋팅한 셋팅 그대로 가져오기
    private void RetrieveUserInfo() {

        db.collection("Users").document(currentUserID).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                Map<String, Object> map = document.getData();
                                if(map.containsKey("name")){
                                    String retrieveUserName = map.get("name").toString();
                                    userName.setText(retrieveUserName);
                                }
                                if(map.containsKey("status")){
                                    String retrieveUserStatus = map.get("status").toString();
                                    userStatus.setText(retrieveUserStatus);
                                }
                                if(map.containsKey("language")){
                                    ArrayList<String> langlist = (ArrayList<String>) map.get("language");
                                    for(String userlang:langlist){
                                        if(userlang.equals("English")) {
                                            english.setChecked(true);
                                        }
                                        if(userlang.equals("한국어")){
                                            korean.setChecked(true);
                                        }
                                    }
                                }
                                if(map.containsKey("Interests")){
                                    ArrayList<String> interestlist = (ArrayList<String>) map.get("Interests");
                                    for(String userinterest:interestlist){
                                        if(userinterest.equals("restaurant")) {
                                            restaurant.setChecked(true);
                                        }
                                        if(userinterest.equals("culture")){
                                            culture.setChecked(true);
                                        }
                                        if(userinterest.equals("show")){
                                            show.setChecked(true);
                                        }
                                        if(userinterest.equals("art")){
                                            art.setChecked(true);
                                        }
                                        if(userinterest.equals("sights")){
                                            sights.setChecked(true);
                                        }
                                        if(userinterest.equals("food")){
                                            food.setChecked(true);
                                        }
                                        if(userinterest.equals("walk")){
                                            walk.setChecked(true);
                                        }
                                    }
                                }
                            }
                        }else {
                            Toast.makeText(SettingsActivity.this, "Please set & update profile...", Toast.LENGTH_LONG).show();
                        }

                    }
                }
        );


        /* RealtimeDB ver
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("name")) {
                        String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                        userName.setText(retrieveUserName);
                    }
                    if (dataSnapshot.hasChild("status")) {
                        String retrieveUserStatus = dataSnapshot.child("status").getValue().toString();
                        userStatus.setText(retrieveUserStatus);
                    }
                    if (dataSnapshot.hasChild("keyword")) {
                        String retrieveUserStatus = dataSnapshot.child("status").getValue().toString();
                        userStatus.setText(retrieveUserStatus);
                    }
                } else {
                    Toast.makeText(SettingsActivity.this, "Please set & update profile...", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */
    }
/*private void updateLanguage(){
        if(english.isChecked())
            Language.add(english.getText().toString());
        if(korean.isChecked())
            Language.add(korean.getText().toString());

    }*/
    /*private void updateInterests(){
        if(restaurant.isChecked())
            Interests.add(restaurant.getText().toString());
        if(culture.isChecked())
            Interests.add(culture.getText().toString());
        if(show.isChecked())
            Interests.add(show.getText().toString());
        if(art.isChecked())
            Interests.add(art.getText().toString());
        if(sights.isChecked())
            Interests.add(sights.getText().toString());
        if(food.isChecked())
            Interests.add(food.getText().toString());
        if(walk.isChecked())
            Interests.add(walk.getText().toString());
    }*/
    private void UpdateSettings() {
        String setUserName = userName.getText().toString();
        String setStatus = userStatus.getText().toString();
        String Location = location.getSelectedItem().toString();
         List<String> Language=new ArrayList<>();

         List<String> Interests= new ArrayList<>();


        if(english.isChecked())
            Language.add(english.getText().toString());
        if(korean.isChecked())
            Language.add(korean.getText().toString());

        if(restaurant.isChecked())
            Interests.add(restaurant.getText().toString());
        if(culture.isChecked())
            Interests.add(culture.getText().toString());
        if(show.isChecked())
            Interests.add(show.getText().toString());
        if(art.isChecked())
            Interests.add(art.getText().toString());
        if(sights.isChecked())
            Interests.add(sights.getText().toString());
        if(food.isChecked())
            Interests.add(food.getText().toString());
        if(walk.isChecked())
            Interests.add(walk.getText().toString());

       

        if (TextUtils.isEmpty(setUserName)) {
            Toast.makeText(this, "Please write your user name first...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(setStatus)) {
            Toast.makeText(this, "Please write your status...", Toast.LENGTH_SHORT).show();
        } 
         else {

            HashMap<String, Object> profileMap = new HashMap<>();

            profileMap.put("name", setUserName);
            profileMap.put("uid", currentUserID);
            profileMap.put("status", setStatus);
            profileMap.put("location",Location);
            profileMap.put("language",Language);
            profileMap.put("Interests",Interests);
            //profileMap.put("user_keyword", setKeyword);

            db.collection("Users").document(currentUserID).set(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        SendUserToSelectActivity();
                        // Toast.makeText(SettingsActivity.this, "Profile Update Successfully...", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(SettingsActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

//            rootRef.child("Users").child(currentUserID).updateChildren(profileMap)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                SendUserToMainActivity();
//                                // Toast.makeText(SettingsActivity.this, "Profile Update Successfully...", Toast.LENGTH_SHORT).show();
//                            } else {
//                                String message = task.getException().toString();
//                                Toast.makeText(SettingsActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
        }

    }

    private void SendUserToSelectActivity() {
        Intent selectIntent = new Intent(SettingsActivity.this,SelectionActivity.class);
        selectIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(selectIntent);
        finish();
    }

}
