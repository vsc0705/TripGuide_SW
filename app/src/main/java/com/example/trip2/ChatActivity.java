package com.example.trip2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.hasnat.sweettoast.SweetToast;

public class ChatActivity extends AppCompatActivity {
    public static Context context_chat;
    private static final String TAG = "CHATDEBUG";
    int REQUEST_IMAGE_CODE=1001;
    int REQUEST_EXTERNAL_STORAGE_PERMISSION=1002;
    private Toolbar chatToolBar;
    private String messageReceiverID, messageReceiverName,messageSenderID;
    private TextView userName;
    private CircleImageView chatUserImageView;

    private ImageButton sendMessageBtn;
    private ImageView sendImage;
    private EditText messageInputText;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;


    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;

    private DocumentReference chatroomRef;
    private DocumentReference messagebody;
    private DocumentReference imagebody;
    private String imagebodyid, chatimg_download_url;
    public String chatroomId;
    private String messagebodyid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context_chat = this;
        setContentView(R.layout.activity_chat);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        messageReceiverID = getIntent().getExtras().get("visitUserId").toString();
        messageReceiverName = getIntent().getExtras().get("visitUserName").toString();
        Log.i("TEST", "메시지 수신자 ID: "+messageReceiverID);
        Log.i("TEST", "메시지 수신자 이름: "+messageReceiverName);

        InitializeControllers();

        userName.setText(messageReceiverName);
        db.collection("Users").document(messageReceiverID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> imgMap = document.getData();
                        if (imgMap.containsKey("user_image")) {
                            final String userUri = imgMap.get("user_image").toString();
                            PicassoTransformations.targetWidth = 40;
                            Picasso.get().load(userUri)
                                    .networkPolicy(NetworkPolicy.OFFLINE) // for Offline
                                    .placeholder(R.drawable.default_profile_image)
                                    .error(R.drawable.default_profile_image)
                                    .transform(PicassoTransformations.resizeTransformation)
                                    .into(chatUserImageView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            PicassoTransformations.targetWidth = 40;
                                            Picasso.get().load(userUri)
                                                    .placeholder(R.drawable.default_profile_image)
                                                    .error(R.drawable.default_profile_image)
                                                    .transform(PicassoTransformations.resizeTransformation)
                                                    .into(chatUserImageView);

                                        }
                                    });
                        }
                    }
                }
            }
        });

        db.collection("ChatRooms").whereEqualTo("Users."+messageReceiverID, true).whereEqualTo("Users."+messageSenderID, true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().size() == 0){
                    Map chatUsers = new HashMap();
                    Map userIds = new HashMap();
                    userIds.put(messageSenderID, true);
                    userIds.put(messageReceiverID, true);
                    chatUsers.put("Users", userIds);
                    chatroomRef = db.collection("ChatRooms").document();
                    chatroomId = chatroomRef.getId();
                    chatroomRef.set(chatUsers);
                } else{
                    chatroomId = task.getResult().getDocuments().get(0).getId();
                }

                db.collection("ChatRooms").document(chatroomId).collection("Messages").orderBy("time").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for(DocumentChange dc:queryDocumentSnapshots.getDocumentChanges()){
//                            Log.e(TAG, dc.getDocument().toString());
                            Messages messages = dc.getDocument().toObject(Messages.class, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE);
                            if(!dc.getDocument().getMetadata().hasPendingWrites()) {
                                switch (dc.getType()){
                                    case ADDED:
                                        //기존에 있던 내역만 불러오게 됨
                                        messagesList.add(messages);
                                        messageAdapter.notifyDataSetChanged();
                                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                                        break;
                                    case MODIFIED:
                                        //모든 새로운 메시지는 Modified에서 불러옴
                                        messagesList.add(messages);
                                        messageAdapter.notifyDataSetChanged();
                                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                                        break;
                                }
                            }

                        }

                    }
                });
            }
        });

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetToast.info(ChatActivity.this, "사진 등록");
                Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(imageIntent, REQUEST_IMAGE_CODE);
            }
        });

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void SendMessage() {
        final String messageText = messageInputText.getText().toString();
        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(this, "first write your message...", Toast.LENGTH_LONG).show();
        }
        else {
            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);
            messageTextBody.put("time", FieldValue.serverTimestamp());
            messageInputText.setText("");
            messagebody = db.collection("ChatRooms").document(chatroomId).collection("Messages").document();
            messagebodyid = messagebody.getId();
            messagebody.set(messageTextBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        sendFCM(messageReceiverID, chatroomId, messageText, "text");
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }

    private void sendFCM(final String receiverId, final String chatRoomId, final String message, final String type){
        //Log.d(TAG, senderName + "님이 " + task.getResult().get("name").toString() + "님께 " + message + " 전송");
        final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
        final String SERVER_KEY = "AAAAst3LJCQ:APA91bFXxaAjnupdToP6oUYp8qXK8akknY5EKOo-8_ZXURJ64zraxbV27OnKrMhIaQm9hKx4JcPqtQRvl1_O6xbob-xv66WEvXFrV7wzLAXHsJA_tt1RTXXLP7v-9fXq6BXQsliEPFT4";
        db.collection("Users").document(messageSenderID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                    pushMsg.put("body", message);
                                    pushMsg.put("title", senderName);
                                    pushData.put("pushType", "message");
                                    pushData.put("visitUserId", messageSenderID);
                                    pushData.put("chatRoomId", chatRoomId);
                                    pushData.put("visitUserName", senderName);
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

    private void InitializeControllers() {
        chatToolBar = (Toolbar)findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(0);
        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);

        actionBar.setCustomView(actionBarView);

        userName = (TextView)findViewById(R.id.custom_profile_name);
        chatUserImageView = findViewById(R.id.custom_profile_image);

        sendMessageBtn = (ImageButton)findViewById(R.id.send_message_btn);
        sendImage=(ImageView)findViewById(R.id.btn_send_image);
        messageInputText = (EditText)findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = (RecyclerView)findViewById(R.id.private_message_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CODE && resultCode==RESULT_OK){
            Uri imageUri = data.getData();
            imagebody = db.collection("ChatRooms").document(chatroomId).collection("Messages").document();
            imagebodyid = imagebody.getId();

            final StorageReference file_path = mStorageRef.child("Users").child(messageSenderID).child("images_Chat").child(imagebodyid+".jpg");
            UploadTask uploadTask=file_path.putFile(imageUri);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        SweetToast.error(ChatActivity.this, "Upload Picture Error: " + task.getException().getMessage());
                    }
                    chatimg_download_url = file_path.getDownloadUrl().toString();
                    return file_path.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        chatimg_download_url=task.getResult().toString();

                        Map messageImageBody = new HashMap();
                        messageImageBody.put("message", chatimg_download_url);
                        messageImageBody.put("type", "image");
                        messageImageBody.put("from", messageSenderID);
                        messageImageBody.put("time", FieldValue.serverTimestamp());

                        imagebody.set(messageImageBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    sendFCM(messageReceiverID, chatroomId, "사진이 도착했습니다.", "image");
                                    SweetToast.info(ChatActivity.this, "Image Sent Successfully");
                                }
                                else {
                                    SweetToast.error(ChatActivity.this, "Image Sent Error");
                                }
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
