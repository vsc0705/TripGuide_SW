package com.example.trip2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteFeedActivity extends Activity {
    FirebaseFirestore db;
    private static final String TAG = "DeleteFeedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_delete_feed);
        db = FirebaseFirestore.getInstance();
    }

    public void mDelete(View v){
        Intent intent=getIntent();
        db.collection("Feeds").document(intent.getExtras().get("id").toString()).delete();
        finish();
    }

    public void mCancel(View v){
        finish();
    }
}