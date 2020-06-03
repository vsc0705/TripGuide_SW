package com.example.trip2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FeedWriteActivity extends AppCompatActivity {
    //여기

    private final int GET_GALLERY_IMAGE = 200;
    private ImageView imageview;
    ImageButton btn_change;
    ImageButton btn_ok;
    EditText text;
    FirebaseFirestore db;


    String uid;

    long now;
    Date date;

    String photo_path;


    FirebaseStorage storage;
    StorageReference storageRef;



    //여기


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedwrite);


        //여기
        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            uid = user.getUid();
        }


        imageview = (ImageView) findViewById(R.id.image);



        text = (EditText) findViewById(R.id.feed_text);



        btn_change = (ImageButton) findViewById(R.id.btn_change);
        btn_change.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);

                System.out.println(photo_path);
            }
        });




        btn_ok = (ImageButton) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                now = System.currentTimeMillis();
                date = new Date(now);
                writefeed(text.getText().toString(), new Timestamp(new Date()), " ", uid);

                finish();


            }
        });
        //여기


    }

    //여기

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            imageview.setImageURI(selectedImageUri);
            photo_path = selectedImageUri.toString();
        }
    }

    //여기



    private void writefeed(String feed_desc, Timestamp feed_time, String feed_uri, String uid) {

        Map<String, Object> feed = new HashMap<>();
        feed.put("feed_desc", feed_desc);
        feed.put("feed_time", feed_time);
        feed.put("feed_uri", feed_uri);
        feed.put("uid", uid);

        db.collection("Feeds")
                .add(feed)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
}
