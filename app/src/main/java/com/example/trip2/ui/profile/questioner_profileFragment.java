package com.example.trip2.ui.profile;

import android.Manifest;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.trip2.PicassoTransformations;
import com.example.trip2.R;
import com.example.trip2.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class questioner_profileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    int REQUEST_IMAGE_CODE=1001;
    private String currentUserID;
    private FirebaseAuth mAuth;
    TextView name,keyword,language,location,introduce;

    ImageView ivUser;
    FirebaseFirestore db;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        View view = inflater.inflate(R.layout.fragment_questioner_profile, container, false);


        currentUserID = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        name=view.findViewById(R.id.profile_name);
        keyword=view.findViewById(R.id.profile_keyword);
        location=view.findViewById(R.id.profile_location);
        language=view.findViewById(R.id.profile_language);
        introduce=view.findViewById(R.id.profile_introduce);


        ivUser=view.findViewById(R.id.ivUser);

        db.collection("Users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    //db.disableNetwork();
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> imgMap = document.getData();
                        if (imgMap.containsKey("user_image")) {
                            String userUri = imgMap.get("user_image").toString();
                            PicassoTransformations.targetWidth = 150;
                            Picasso.get().load(userUri)
                                    .networkPolicy(NetworkPolicy.OFFLINE) // for offline
                                    .placeholder(R.drawable.default_profile_image)
                                    .error(R.drawable.default_profile_image)
                                    .transform(PicassoTransformations.resizeTransformation)
                                    .into(ivUser);
                        }
                    }
                }
            }
        });

        //RetrieveUserInfo();
        GridView grid = (GridView) view.findViewById(R.id.grid_view);//중요
        grid.setAdapter(new ImageAdapter(getActivity()));//중요
        return view;
    }
    /*private void UpdateSettings() {

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

            db.collection("Users").document(currentUserID).set(profileMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        SendUserToSelectActivity();
                        // Toast.makeText(SettingsActivity.this, "Profile Update Successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else {
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

    }*/



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void RetrieveUserInfo(){
        db.collection("Users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    if(document.exists())
                    {
                        Map<String, Object> profile_map=document.getData();
                        if(profile_map.containsKey("name")) {
                            String profile_name = profile_map.get("name").toString();
                            name.setText(profile_name);
                        }
                        if(profile_map.containsKey("location")){
                            String profile_location = profile_map.get("location").toString();
                            location.setText(profile_location);
                        }
                        if(profile_map.containsKey("status")){
                            String profile_status = profile_map.get("status").toString();
                            introduce.setText(profile_status);
                        }
                        if(profile_map.containsKey("language")){
                            ArrayList<String> langlist = (ArrayList<String>) profile_map.get("language");
                            String profile_language="";
                            for(String userlang:langlist) {

                                if (userlang.equals("English")) {
                                    profile_language = profile_language + "  English";
                                    language.setText(profile_language);
                                }
                                if (userlang.equals("korean")) {
                                    profile_language= profile_language+ "  한국어";
                                    language.setText(profile_language);
                                }

                            }
                        }

                        if(profile_map.containsKey("Interests")){
                            String profile_Interests="";
                            ArrayList<String> interestlist = (ArrayList<String>) profile_map.get("Interests");

                            for(String userinterest:interestlist){
                                if(userinterest.equals("restaurant")) {
                                    profile_Interests +="  restaurant";
                                    keyword.setText(profile_Interests);
                                }
                                if(userinterest.equals("culture")){
                                    profile_Interests +="  culture";
                                    keyword.setText(profile_Interests);

                                }
                                if(userinterest.equals("show")){
                                    profile_Interests +="  show";
                                    keyword.setText(profile_Interests);

                                }
                                if(userinterest.equals("art")){
                                    profile_Interests +="  art";
                                    keyword.setText(profile_Interests);

                                }
                                if(userinterest.equals("sights")){
                                    profile_Interests +="  sights";
                                    keyword.setText(profile_Interests);

                                }
                                if(userinterest.equals("food")){
                                    profile_Interests +="  food";
                                    keyword.setText(profile_Interests);

                                }
                                if(userinterest.equals("walk")){
                                    profile_Interests +="  walk";
                                    keyword.setText(profile_Interests);

                                }
                            }

                        }
                    }
                }
            }
        });
    }
}
