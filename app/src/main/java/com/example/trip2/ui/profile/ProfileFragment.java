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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.trip2.PicassoTransformations;
import com.example.trip2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    int REQUEST_IMAGE_CODE=1001;
    int REQUEST_EXTERNAL_STORAGE_PERMISSION=1002;


    private String currentUserID;
    private FirebaseAuth mAuth;

    private CircleImageView ivUser;
    File localFile;


    FirebaseFirestore db;

    //TextView 부분
    TextView name;
    TextView keyword;
    TextView location;
    TextView language;
    TextView introduce;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name=view.findViewById(R.id.profile_name);
        keyword=view.findViewById(R.id.profile_keyword);
        location=view.findViewById(R.id.profile_location);
        language=view.findViewById(R.id.profile_language);
        introduce=view.findViewById(R.id.profile_introduce);
        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        currentUserID=mAuth.getCurrentUser().getUid();

        if(ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)){

            }else{
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE_PERMISSION);
            }
        }else{

        }

        ivUser=view.findViewById(R.id.profile_ivUser);

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

        RetrieveUserInfo();
        GridView grid = (GridView) view.findViewById(R.id.grid_view);//중요
        grid.setAdapter(new ImageAdapter(getActivity()));//중요
        return view;

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
                                    profile_language = profile_language + " English";
                                    language.setText(profile_language);
                                }
                                if (userlang.equals("korean")) {
                                    profile_language= profile_language+ " 한국어";
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}