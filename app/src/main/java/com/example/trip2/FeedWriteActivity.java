package com.example.trip2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import xyz.hasnat.sweettoast.SweetToast;

public class FeedWriteActivity extends AppCompatActivity {
    //여기
    private static final String TAG = "FeedWriteActivity";
    private ImageView imageview;
    Button btn_ok;
    EditText text;
    private CheckBox english, korean, restaurant, culture, show, art, sights, food, walk;

    FirebaseFirestore db;
    private String currentUserID;
    private String documentId;
    private FirebaseAuth mAuth;


    StorageReference storageRef;
    int REQUEST_EXTERNAL_STORAGE_PERMISSION=1002;
    int REQUEST_IMAGE_CODE=1001;

    String feed_uri;
    String feed_desc,uid,time;



    //여기


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedwrite);


        //여기
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        storageRef = FirebaseStorage.getInstance().getReference();
        documentId=db.collection("feeds").document().getId();

        restaurant=(CheckBox)findViewById(R.id.restaurant);
        culture =(CheckBox)findViewById(R.id.culture);
        show=(CheckBox)findViewById(R.id.show);
        art=(CheckBox)findViewById(R.id.art);
        sights=(CheckBox)findViewById(R.id.sights);
        food=(CheckBox)findViewById(R.id.food);
        walk=(CheckBox)findViewById(R.id.walk);

        imageview = (ImageView) findViewById(R.id.image);
        btn_ok= findViewById(R.id.btn_ok);

        text = (EditText) findViewById(R.id.feed_text);
        if(ContextCompat.checkSelfPermission(FeedWriteActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(FeedWriteActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){

            }else{
                ActivityCompat.requestPermissions(FeedWriteActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE_PERMISSION);
            }
        }else{

        }

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, REQUEST_IMAGE_CODE);
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writefeed();
            }
        });
    }

    //여기

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CODE){
            final Uri image=data.getData();
            Picasso.get().load(image)
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image)
                    .resize(0,400)
                    .into(imageview);

            final StorageReference riversRef = storageRef.child("Feeds").child(currentUserID).child(documentId).child("feed.jpg");
            UploadTask uploadTask=riversRef.putFile(image);
            Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        SweetToast.error(FeedWriteActivity.this, "Feed Photo Error: " + task.getException().getMessage());
                    }
                    feed_uri=riversRef.getDownloadUrl().toString();
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        feed_uri=task.getResult().toString();

                        HashMap<String, Object> update_feed_data=new HashMap<>();
                        update_feed_data.put("feed_uri",feed_uri);

                        db.collection("Feeds").document(documentId).set(update_feed_data, SetOptions.merge())
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

    private void writefeed() {
        feed_desc=text.getText().toString();

        final HashMap<String,Boolean> feed_keyword= new HashMap<>();

        if(restaurant.isChecked())
            feed_keyword.put(restaurant.getText().toString(),true);
        if(culture.isChecked())
            feed_keyword.put(culture.getText().toString(),true);
        if(show.isChecked())
            feed_keyword.put(show.getText().toString(),true);
        if(art.isChecked())
            feed_keyword.put(art.getText().toString(),true);
        if(sights.isChecked())
            feed_keyword.put(sights.getText().toString(),true);
        if(food.isChecked())
            feed_keyword.put(food.getText().toString(),true);
        if(walk.isChecked())
            feed_keyword.put(walk.getText().toString(),true);

        Map<String, Object> feed = new HashMap<>();
        feed.put("feed_desc",feed_desc);
        feed.put("feed_time", FieldValue.serverTimestamp());
        feed.put("uid", currentUserID);
        feed.put("feed_area",feed_keyword);

        db.collection("Feeds").document(documentId).set(feed,SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FeedWriteActivity.this, "피드 등록이 완료되었습니다.",Toast.LENGTH_LONG).show();
                        SendUserToMainActivity();
                    }
                });
    }

    private void SendUserToMainActivity() {
        Intent selectIntent = new Intent(this, MainActivity.class);
        selectIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(selectIntent);
        finish();
    }
}