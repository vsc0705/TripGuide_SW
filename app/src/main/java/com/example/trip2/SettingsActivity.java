package com.example.trip2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private Button updateAccountSettings;
    private EditText userName;

    private String currentUserID;
    private FirebaseAuth mAuth;
    // cloudfirestore로 변환중
    private FirebaseFirestore db;

    private DatabaseReference rootRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        updateAccountSettings = (Button) findViewById(R.id.update_settings_button);
        userName = (EditText) findViewById(R.id.set_user_name);
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        rootRef = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });
        RetrieveUserInfo();
    }

    private void RetrieveUserInfo() {
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("name")) {
                        String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                        userName.setText(retrieveUserName);
                    }
                } else {
                    Toast.makeText(SettingsActivity.this, "Please set & update profile...", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UpdateSettings() {
        String setUserName = userName.getText().toString();

        if (TextUtils.isEmpty(setUserName)) {
            Toast.makeText(this, "Please write your user name first...", Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", setUserName);

            rootRef.child("Users").child(currentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SendUserToMainActivity();
                                // Toast.makeText(SettingsActivity.this, "Profile Update Successfully...", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
