package com.example.trip2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
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

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;

    private DocumentReference chatroomRef;
    private DocumentReference messagebody;
    private String chatroomId;
    private String messagebodyid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();


        messageReceiverID = getIntent().getExtras().get("visitUserId").toString();
        messageReceiverName = getIntent().getExtras().get("visitUserName").toString();
        Log.i("TEST", "메시지 수신자 ID: "+messageReceiverID);
        Log.i("TEST", "메시지 수신자 이름: "+messageReceiverName);
//        Toast.makeText(ChatActivity.this, messageReceiverID, Toast.LENGTH_LONG).show();
//        Toast.makeText(ChatActivity.this, messageReceiverName, Toast.LENGTH_LONG).show();

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
        String messageText = messageInputText.getText().toString();
        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(this, "first write your message...", Toast.LENGTH_LONG).show();
        }
        else {
            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);
//            messageTextBody.put("time", new Timestamp(new Date()));
            // 서버 타입스탬프 적용으로 약간의 문제 발생 가능성 존재
            messageTextBody.put("time", FieldValue.serverTimestamp());

            messagebody = db.collection("ChatRooms").document(chatroomId).collection("Messages").document();
            messagebodyid = messagebody.getId();
            messagebody.set(messageTextBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    messageInputText.setText("");
                }
            });
        }

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
    protected void onStart() {
        super.onStart();
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
                            Messages messages = dc.getDocument().toObject(Messages.class, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE);
                            switch (dc.getType()){
                                case ADDED:
                                    messagesList.add(messages);
                                    messageAdapter.notifyDataSetChanged();
                                    userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                                    break;
                                case MODIFIED:
                                    // 여기때문에 채팅 증발현상 생길 가능성 존재
                                    messagesList.remove(messagesList.size()-1);
                                    messagesList.add(messages);
                                    messageAdapter.notifyDataSetChanged();
                                    userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                                    break;
                            }
                        }

                    }
                });
            }
        });
    }
}
