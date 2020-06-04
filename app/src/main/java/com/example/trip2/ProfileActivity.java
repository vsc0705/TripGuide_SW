package com.example.trip2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ErrorManager;

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

        db= FirebaseFirestore.getInstance();
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
        profile_ivUser = (CircleImageView) findViewById(R.id.ivUser);
        // if 문에서 이미 매칭된 경우에도 버튼 제거 필요
        if(senderUserId.equals(receiverUserId)){
            sendMessageRequestButton.setEnabled(false);
            sendMessageRequestButton.setVisibility(View.INVISIBLE);
        }
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
        RetrieveUserInfo();
    }



    private void RetrieveUserInfo() {

        db.collection("Users").document(receiverUserId).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        String userName = document.get("name").toString();
                        userProfileName.setText(userName);
                    }
                }
        );
        db.collection("Users").document(senderUserId).collection("Matching")
                .document(receiverUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> requestinfo;
                    requestinfo = task.getResult().getData();
//                    Log.i("debug", String.valueOf(requestinfo));
                    if(requestinfo != null){
                        if(requestinfo.containsKey("sent")){
                            sendMessageRequestButton.setText(R.string.cancel_invite);
                        }
                    }
                }

            }

        });
    }
    private void SendChatRequest() {

        if(sendMessageRequestButton.getText().equals("Cancel invite")){
            Map<String,Object> removesent = new HashMap<>();
            removesent.put("sent", FieldValue.delete());
            //removesent.put("ismatched", false);
            db.collection("Users").document(senderUserId).collection("Matching")
                    .document(receiverUserId).update(removesent).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Map<String,Object> removereceived = new HashMap<>();
                        removereceived.put("received", FieldValue.delete());
                        //removereceived.put("ismatched", false);
                        db.collection("Users").document(receiverUserId)
                                .collection("Matching").document(senderUserId).update(removereceived);
                    }
                }
            });
            sendMessageRequestButton.setText("Add friend");
        } else{
            Map<String, Object> requestInfo_send = new HashMap<>();
            requestInfo_send.put("sent", true);
            requestInfo_send.put("ismatched", false);
            db.collection("Users").document(senderUserId).collection("Matching").document(receiverUserId).set(requestInfo_send, SetOptions.merge()).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Map<String, Object> requestInfo_receive = new HashMap<>();
                                requestInfo_receive.put("received", true);
                                requestInfo_receive.put("ismatched", false);
                                db.collection("Users").document(receiverUserId)
                                        .collection("Matching").document(senderUserId)
                                        .set(requestInfo_receive, SetOptions.merge());
                                sendFCM(receiverUserId);
                                sendMessageRequestButton.setText(R.string.cancel_invite);
                            }
                        }
                    }
            );
        }
    }

    private void sendFCM(final String receiverId){
        //Log.d(TAG, senderName + "님이 " + task.getResult().get("name").toString() + "님께 매칭요청 전송");
        final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
        final String SERVER_KEY = "AAAAst3LJCQ:APA91bFXxaAjnupdToP6oUYp8qXK8akknY5EKOo-8_ZXURJ64zraxbV27OnKrMhIaQm9hKx4JcPqtQRvl1_O6xbob-xv66WEvXFrV7wzLAXHsJA_tt1RTXXLP7v-9fXq6BXQsliEPFT4";
        db.collection("Users").document(senderUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                final String senderName = task.getResult().get("name").toString();
                db.collection("Users").document(receiverId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                        final String receiverFCMId = task.getResult().get("FCMToken").toString();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //JSON 메시지 생성
                                    JSONObject pushRoot = new JSONObject();
                                    JSONObject pushMsg = new JSONObject();
                                    JSONObject pushData = new JSONObject();
                                    pushMsg.put("body", senderName+"님이 매칭을 요청하셨습니다.");
                                    pushMsg.put("title", "새 매칭 요청");
                                    pushData.put("pushType", "request");
                                    pushData.put("requestUserId", senderUserId);
                                    pushRoot.put("notification", pushMsg);
                                    pushRoot.put("to", receiverFCMId);
                                    pushRoot.put("data", pushData);
                                    Log.d(TAG, "onComplete: "+pushRoot.toString());
                                    //POST 방식으로 FCM 서버에 전송
                                    URL Url = new URL(FCM_MESSAGE_URL);
                                    HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                                    conn.setRequestMethod("POST");
                                    conn.setDoOutput(true);
                                    conn.setDoInput(true);
                                    conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                                    conn.setRequestProperty("Accept", "application/json");
                                    conn.setRequestProperty("Content-type", "application/json");
                                    OutputStream os = conn.getOutputStream();
                                    os.write(pushRoot.toString().getBytes("utf-8"));
                                    os.flush();
                                    conn.getResponseCode();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
            }
        });

    }


}
