package com.example.trip2;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class MyApplication extends Application {

    private DatabaseReference userDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentOnlineUser;
    private FirebaseFirestore db;


    @Override
    public void onCreate() {

        super.onCreate();
//        db = FirebaseFirestore.getInstance();
//        //  all strings >> load offline
//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(true).setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
//                .build();
//        db.setFirestoreSettings(settings);


        //  all images >> load offline
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso builtPicasso = builder.build();
        builtPicasso.setIndicatorsEnabled(true);
        builtPicasso.setLoggingEnabled(true);

        Picasso.setSingletonInstance(builtPicasso);


//        // ONLINE STATUS
//        mAuth = FirebaseAuth.getInstance();
//        currentOnlineUser = mAuth.getCurrentUser();
//
//        if (currentOnlineUser != null){
//            String user_u_id = mAuth.getCurrentUser().getUid();
//
//            userDatabaseReference
//                    = FirebaseDatabase.getInstance().getReference().child("users").child(user_u_id);
//
//            userDatabaseReference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    userDatabaseReference.child("active_now").onDisconnect().setValue(ServerValue.TIMESTAMP);
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//
//        }


    }
}
