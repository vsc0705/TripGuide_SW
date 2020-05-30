package com.example.trip2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SelectionActivity extends AppCompatActivity {
    ImageButton questioner;
    ImageButton respondent;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String currentUserId;

    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        questioner= (ImageButton) findViewById(R.id.selection_questioner);
        respondent=(ImageButton)findViewById(R.id.selection_respondent);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        rootRef = FirebaseDatabase.getInstance().getReference();


        questioner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent questioner_home= new Intent(SelectionActivity.this, questioner_main.class);
                startActivity(questioner_home);
            }
        });
        respondent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent respondent_home = new Intent(SelectionActivity.this, MainActivity.class);
                startActivity(respondent_home);
            }
        });
    }
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            SendUserToLoginActivity();
        }
        else{

            VerifyUserExistance();
            updateUserStatus("online");
        }
    }
    private void VerifyUserExistance() {
        String currentUserID = mAuth.getCurrentUser().getUid();

        db.collection("Users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    if(document.exists()){
                        Map<String, Object> map = document.getData();
                        if(!map.containsKey("name")){
                            SendUserToSettingsActivity();
                        }
                        if(!map.containsKey("status")){
                            SendUserToSettingsActivity();
                        }
                    }
                }
            }
        });

    }
    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(SelectionActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(SelectionActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
    private void updateUserStatus(String state) {
        String saveCurrentUserTime, saveCurrentUserDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate  = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentUserDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime  = new SimpleDateFormat("hh:mm ss");
        saveCurrentUserTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();

        onlineStateMap.put("time", saveCurrentUserTime);
        onlineStateMap.put("date", saveCurrentUserDate);
        onlineStateMap.put("state", state);

        currentUserId = currentUser.getUid();
        rootRef.child("Users").child(currentUserId).child("userState")
                .updateChildren(onlineStateMap);
    }
}
