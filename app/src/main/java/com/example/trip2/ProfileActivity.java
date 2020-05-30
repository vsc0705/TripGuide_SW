package com.example.trip2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private String receiverUserId, senderUserId;
    private TextView userProfileName;
    private Button sendMessageRequestButton;
    private DatabaseReference userRef, chatRequestRef, contactsRef, notificationRef;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseFirestore.getInstance();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();

        receiverUserId = getIntent().getExtras().get("visitUserId").toString();


        userProfileName = (TextView) findViewById(R.id.visit_user_name);
        sendMessageRequestButton = (Button) findViewById(R.id.send_message_request_button);
        sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendChatRequest();
            }
        });

        RetrieveUserInfo();
    }
    private void RetrieveUserInfo() {

        if(senderUserId.equals(receiverUserId)){
            sendMessageRequestButton.setEnabled(false);
            sendMessageRequestButton.setVisibility(View.INVISIBLE);
        }
        db.collection("Users").document(receiverUserId).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        Map<String, Object> map = document.getData();
                        String userName = map.get("name").toString();
                        userProfileName.setText(userName);
                    }
                }
        );

//        userRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                String userName = dataSnapshot.child("name").getValue().toString();
//
//                userProfileName.setText(userName);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }
    private void SendChatRequest() {
        if(sendMessageRequestButton.getText().equals("Cancel Invited")){
            chatRequestRef.child(senderUserId).child(receiverUserId)
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        chatRequestRef.child(receiverUserId).child(senderUserId)
                                .removeValue();
                    }
                }
            });
            sendMessageRequestButton.setText(R.string.add_friend);
            return;
        }
//        Map<String, Object> requestInfo = new HashMap<>();
//        requestInfo.put("requestType", "sent");
//
//        db.collection("Users").document(senderUserId).collection("Matching").document(receiverUserId).set(requestInfo).addOnCompleteListener(
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
//                            db.collection("Users").document(receiverUserId).collection("Matching").document(senderUserId).set()
//                        }
//                    }
//                }
//        )


        chatRequestRef.child(senderUserId).child(receiverUserId)
                .child("requestType").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatRequestRef.child(receiverUserId).child(senderUserId)
                                    .child("requestType").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                HashMap<String, String> chatNotificationMap = new HashMap<>();
                                                chatNotificationMap.put("from", senderUserId);
                                                chatNotificationMap.put("type", "request");
                                                notificationRef.child(receiverUserId).push()
                                                        .setValue(chatNotificationMap);
                                                sendMessageRequestButton.setText(R.string.cancel_invite);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


}
