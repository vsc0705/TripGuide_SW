package com.example.trip2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.ErrorManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private ErrorManager SweetToast;
    private String currentUserID;


    //이미지 부분
    private static final String TAG = "ProfileActivity";
    int profile_REQUEST_IMAGE_CODE=1001;
    int profile_REQUEST_EXTERNAL_STORAGE_PERMISSION=1002;
    private CircleImageView profile_ivUser;
    File profile_localFile;
    private StorageReference profile_mStorageRef;
    String profile_stEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseFirestore.getInstance();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        db= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();

        receiverUserId = getIntent().getExtras().get("visitUserId").toString();


        userProfileName = (TextView) findViewById(R.id.visit_user_name);
        sendMessageRequestButton = (Button) findViewById(R.id.send_message_request_button);
        RetrieveUserInfo();

        sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendChatRequest();
            }
        });


        currentUserID=mAuth.getCurrentUser().getUid();
        profile_ivUser = (CircleImageView) findViewById(R.id.ivUser);
        //이미지 코드
       /* try {
            profile_localFile = File.createTempFile("images", "jpg");
            StorageReference riversRef = profile_mStorageRef.child("users").child(profile_stEmail).child("profile.jpg");
            riversRef.getFile(profile_localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            // ...
                            Bitmap bitmap= BitmapFactory.decodeFile(profile_localFile.getAbsolutePath());
                            profile_ivUser.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        db=FirebaseFirestore.getInstance();
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

        db.collection("Users").document(receiverUserId).collection("Matching")
                .document(senderUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> requestinfo;
                    requestinfo = task.getResult().getData();
                    if(requestinfo != null){
                        if(requestinfo.containsKey("requestType")){
                            sendMessageRequestButton.setText(R.string.cancel_invite);
                        }
                    }
                }

            }

        });
    }
    private void SendChatRequest() {

        if(sendMessageRequestButton.getText().equals("Cancel invite")){
            db.collection("Users").document(senderUserId).collection("Matching")
                    .document(receiverUserId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        db.collection("Users").document(receiverUserId)
                                .collection("Matching").document(senderUserId).delete();

                    }
                }
            });
            sendMessageRequestButton.setText("Add friend");
        } else{
            Map<String, Object> requestInfo_send = new HashMap<>();
            requestInfo_send.put("requestType", "sent");
            db.collection("Users").document(senderUserId).collection("Matching").document(receiverUserId).set(requestInfo_send).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Map<String, Object> requestInfo_receive = new HashMap<>();
                                requestInfo_receive.put("requestType", "received");
                                db.collection("Users").document(receiverUserId)
                                        .collection("Matching").document(senderUserId)
                                        .set(requestInfo_receive);
                                sendMessageRequestButton.setText(R.string.cancel_invite);
                            }
                        }
                    }
            );
        }
//
//        if(sendMessageRequestButton.getText().equals("Cancel Invited")){
//            chatRequestRef.child(senderUserId).child(receiverUserId)
//                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if(task.isSuccessful()){
//                        chatRequestRef.child(receiverUserId).child(senderUserId)
//                                .removeValue();
//                    }
//                }
//            });
//            sendMessageRequestButton.setText(R.string.add_friend);
//            return;
//        }




//        chatRequestRef.child(senderUserId).child(receiverUserId)
//                .child("requestType").setValue("sent")
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            chatRequestRef.child(receiverUserId).child(senderUserId)
//                                    .child("requestType").setValue("received")
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                HashMap<String, String> chatNotificationMap = new HashMap<>();
//                                                chatNotificationMap.put("from", senderUserId);
//                                                chatNotificationMap.put("type", "request");
//                                                notificationRef.child(receiverUserId).push()
//                                                        .setValue(chatNotificationMap);
//                                                sendMessageRequestButton.setText(R.string.cancel_invite);
//                                            }
//                                        }
//                                    });
//                        }
//                    }
//                });
    }


}
