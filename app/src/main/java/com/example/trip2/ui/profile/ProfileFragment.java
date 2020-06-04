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
import com.example.trip2.SettingsActivity;
import com.google.android.gms.tasks.Continuation;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.hasnat.sweettoast.SweetToast;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    int REQUEST_IMAGE_CODE=1001;
    int REQUEST_EXTERNAL_STORAGE_PERMISSION=1002;
    String profileback_download_url;
    private StorageReference mStorageRef;


    private String currentUserID;
    private FirebaseAuth mAuth;

    private CircleImageView ivUser;
    private ImageView ivBack;
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
        mStorageRef = FirebaseStorage.getInstance().getReference();

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
        ivBack=view.findViewById(R.id.profile_ivUserBackground);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, REQUEST_IMAGE_CODE);
            }
        });

        db.collection("Users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> imgMap = document.getData();
                        if (imgMap.containsKey("user_image")) {
                            final String userUri = imgMap.get("user_image").toString();
                            PicassoTransformations.targetWidth = 90;
                            Picasso.get().load(userUri)
                                    .networkPolicy(NetworkPolicy.OFFLINE) // for offline
                                    .placeholder(R.drawable.default_profile_image)
                                    .error(R.drawable.default_profile_image)
                                    .transform(PicassoTransformations.resizeTransformation)
                                    .into(ivUser, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            PicassoTransformations.targetWidth = 90;
                                            Picasso.get().load(userUri)
                                                    .placeholder(R.drawable.default_profile_image)
                                                    .error(R.drawable.default_profile_image)
                                                    .transform(PicassoTransformations.resizeTransformation)
                                                    .into(ivUser);
                                        }
                                    });
                        }
                    }
                }
            }
        });
        db.collection("Users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> imgMap = document.getData();
                        if (imgMap.containsKey("user_back_image")) {
                            final String userbackUri = imgMap.get("user_back_image").toString();
                            PicassoTransformations.targetWidth = 200;
                            Picasso.get().load(userbackUri)
                                    .networkPolicy(NetworkPolicy.OFFLINE) // for offline
                                    .placeholder(R.drawable.profile_ivuserbackgroundimage)
                                    .error(R.drawable.profile_ivuserbackgroundimage)
                                    .transform(PicassoTransformations.resizeTransformation)
                                    .into(ivBack, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            PicassoTransformations.targetWidth = 200;
                                            Picasso.get().load(userbackUri)
                                                    .placeholder(R.drawable.profile_ivuserbackgroundimage)
                                                    .error(R.drawable.profile_ivuserbackgroundimage)
                                                    .transform(PicassoTransformations.resizeTransformation)
                                                    .into(ivBack);
                                        }
                                    });
                        }
                    }
                }
            }
        });

        RetrieveUserInfo();

        GridView grid = (GridView) view.findViewById(R.id.grid_view);//중요
        grid.setAdapter(new ImageAdapter(getActivity()));//중요
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        grid.measure(0, expandSpec);
        grid.getLayoutParams().height = grid.getMeasuredHeight();//스크롤 뷰에서 그리드 잘리는 문제를 해결하기 위해 그리드의 길이를 가져와 확장
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
                        Map<String, Object> profile_map=document.getData();// 문서 전체를 profile_map으로 받아온것
                        if(profile_map.containsKey("name")) {
                            String profile_name = profile_map.get("name").toString();
                            name.setText(profile_name);
                        }
                        if(profile_map.containsKey("location")){
                            HashMap<String,Boolean> locationpart=(HashMap)profile_map.get("location");
                            String profile_location="";
                            for(String userlocation : locationpart.keySet())
                            {
                                profile_location=profile_location+userlocation;
                                location.setText(profile_location);
                            }
                        }
                        if(profile_map.containsKey("status")){
                            String profile_status = profile_map.get("status").toString();
                            introduce.setText(profile_status);
                        }
                        if(profile_map.containsKey("language")){
                            HashMap<String,Boolean> langlist=(HashMap)profile_map.get("language");
                            String profile_language="";
                            for(String userlang:langlist.keySet()) {

                                profile_language=profile_language+userlang+",  ";

                                language.setText(profile_language);

                            }
                        }

                        if(profile_map.containsKey("user_keyword")){

                            HashMap<String,Boolean> user_keywords=(HashMap)profile_map.get("user_keyword");
                            String profile_userkeyword="";

                            for(String userinterest:user_keywords.keySet()){

                                profile_userkeyword=profile_userkeyword+userinterest+",  ";

                                keyword.setText(profile_userkeyword);
                               /* if(userinterest.equals("restaurant")) {
                                    profile_Interests +="  #restaurant";
                                    keyword.setText(profile_Interests);
                                }
                                if(userinterest.equals("culture")){
                                    profile_Interests +="  #culture";
                                    keyword.setText(profile_Interests);

                                }
                                if(userinterest.equals("show")){
                                    profile_Interests +="  #show";
                                    keyword.setText(profile_Interests);

                                }
                                if(userinterest.equals("art")){
                                    profile_Interests +="  #art";
                                    keyword.setText(profile_Interests);

                                }
                                if(userinterest.equals("sights")){
                                    profile_Interests +="  #sights";
                                    keyword.setText(profile_Interests);

                                }
                                if(userinterest.equals("food")){
                                    profile_Interests +="  #food";
                                    keyword.setText(profile_Interests);

                                }
                                if(userinterest.equals("walk")){
                                    profile_Interests +="  #walk";
                                    keyword.setText(profile_Interests);

                                }*/
                            }

                        }
                    }
                }
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CODE){
            final Uri image=data.getData();
            PicassoTransformations.targetWidth=200;
            Picasso.get().load(image)
                    .placeholder(R.drawable.profile_ivuserbackgroundimage)
                    .error(R.drawable.profile_ivuserbackgroundimage)
                    .transform(PicassoTransformations.resizeTransformation)
                    .into(ivBack);

            final StorageReference storeRef = mStorageRef.child("Users").child(currentUserID).child("profile_back.jpg");
            UploadTask uploadTask=storeRef.putFile(image);
            Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        SweetToast.error(getActivity(), "Profile Photo Error: " + task.getException().getMessage());
                    }
                    profileback_download_url=storeRef.getDownloadUrl().toString();
                    return storeRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        profileback_download_url=task.getResult().toString();

                        HashMap<String, Object> update_user_data=new HashMap<>();
                        update_user_data.put("user_back_image",profileback_download_url);

                        db.collection("Users").document(currentUserID).set(update_user_data, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });


                    }
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}